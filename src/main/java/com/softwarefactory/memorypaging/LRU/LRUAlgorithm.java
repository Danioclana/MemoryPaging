package com.softwarefactory.memorypaging.LRU;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwarefactory.memorypaging.Page.Page;
import com.softwarefactory.memorypaging.RAM.Frame;

import com.softwarefactory.memorypaging.Page.PageController;
import com.softwarefactory.memorypaging.RAM.FrameController;

@RestController
@RequestMapping("/lru")
public class LRUAlgorithm {
    int time;
    int contPageFaults;
    boolean pageFaultHistoric;

    FrameController frameController = new FrameController();
    PageController pageController = new PageController();

    ArrayList<Frame> frames = new ArrayList<>();
    ArrayList<Page> pages = new ArrayList<>();

    @PostMapping("/init")
    public ResponseEntity<?> LRU_init() {
        frames.addAll(FrameController.frames);
        if (frames.size() == 0) {
            return ResponseEntity.status(404).body("RAM not found");
        }
        pages.addAll(PageController.pages);
        if (pages.size() == 0) {
            return ResponseEntity.status(404).body("Pages not found");
        }

        time = 0;
        contPageFaults = 0;

        return ResponseEntity.status(200).body("LRU algorithm init successfully");
    }

    @PostMapping("/acessPage")
    public ResponseEntity<?> LRU_acessPage(int pageId) {

        time++;

        Page page = pageController.isExists(pageId);

        if (page == null) {
            //System.out.println("Page " + pageId + " not found");
            return ResponseEntity.status(404).body("Page " + pageId + " not found");
        }

        if (frameController.findPage(pageId) != -1) {
            this.pageFaultHistoric = false;
            page.setTimeLastUsed(time);

            //System.out.println("Page " + pageId + " already loaded in frame " + frameController.findPage(pageId));
            return ResponseEntity.status(200)
            .body(responseConstructor(pageId, frameController.findPage(pageId), pageFaultHistoric,
                    "Page " + pageId + " already loaded in frame " + frameController.findPage(pageId)));
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                page.setTimeLastUsed(time);
                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                //System.out.println("Page " + pageId + " loaded successfully in frame " + frame.getId());
                return ResponseEntity.status(200)
                        .body(responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                                "Page " + pageId + " loaded successfully in frame " + frame.getId()));
            }
        }

        Page pageToRemove = frames.get(0).getPage();

        for (Frame frame : frames) {
            
            if (frame.getPage().getTimeLastUsed() < pageToRemove.getTimeLastUsed()) {
                pageToRemove = frame.getPage();
            }
        }

        page.setTimeLastUsed(time);

        this.contPageFaults++;
        this.pageFaultHistoric = true;

        Frame frame = frameController.findFrameById(frameController.findPage(pageToRemove.getId()));
        frame.setPage(page);

        //System.out.println("Page " + pageId + " accessed successfully in frame " + frames.get(0).getId());
        return ResponseEntity.status(200)
        .body(responseConstructor(pageId, pageToRemove.getId(), pageFaultHistoric,
                "Page " + pageId + " accessed successfully in frame " + frame.getId()));
    }

    @GetMapping("/getArrayFrames")
    public ResponseEntity<?> LRU_getArrayFrames() {

        return ResponseEntity.status(200).body(frames);

    }

    @GetMapping("/getFaultHistoric")
    public ResponseEntity<?> LRU_getFaultHistoric() {

        return ResponseEntity.status(200).body(this.pageFaultHistoric);

    }

    @GetMapping("/getContPageFaults")
    public ResponseEntity<?> LRU_getContPageFaults() {

        return ResponseEntity.status(200).body(this.contPageFaults);

    }

    public Object responseConstructor(int pageId, int frameId, boolean pageFaultHistoric, String action) {
        Object[] response = new Object[4];

        response[0] = pageId;
        response[1] = frameId;
        response[2] = pageFaultHistoric;
        response[3] = action;

        return response;
    } 

    public static void main(String[] args) {
        System.out.println("LRU");

        FrameController frame = new FrameController();
        PageController page = new PageController();

        frame.createFrames(3);
        page.createPagesRandom(7);

        LRUAlgorithm lru = new LRUAlgorithm();

        lru.LRU_init();

        System.out.println(lru.LRU_acessPage(7));
        System.out.println(lru.LRU_acessPage(1));
        System.out.println(lru.LRU_acessPage(2));

        System.out.println(lru.LRU_acessPage(3));

        System.out.println(lru.LRU_acessPage(1));

        System.out.println(lru.LRU_acessPage(4));

        System.out.println(lru.LRU_acessPage(5));

    }
}
