package com.softwarefactory.memorypaging.FIFO;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwarefactory.memorypaging.Page.Page;
import com.softwarefactory.memorypaging.RAM.Frame;

import com.softwarefactory.memorypaging.Page.PageController;
import com.softwarefactory.memorypaging.RAM.FrameController;

@RestController
@RequestMapping("/fifo")
public class FIFOAlgorithm {
    int contPageFaults;
    boolean pageFaultHistoric;

    FrameController frameController = new FrameController();
    PageController pageController = new PageController();

    ArrayList<Frame> frames = new ArrayList<>();
    ArrayList<Page> pages = new ArrayList<>();

    @PostMapping("/init")
    public ResponseEntity<?> FIFO_init() {

        frames.addAll(FrameController.frames);
        if (frames.size() == 0) {
            return ResponseEntity.status(404).body("RAM not found");
        }
        pages.addAll(PageController.pages);
        if (pages.size() == 0) {
            return ResponseEntity.status(404).body("Pages not found");
        }

        contPageFaults = 0;

        return ResponseEntity.status(200).body("FIFO algorithm init successfully");
    }

    @PostMapping("/acessPage")
    public ResponseEntity<?> FIFO_acessPage(@RequestBody int pageId) {
        Object[] response = new Object[4];

        Page page = pageController.isExists(pageId);

        if (page == null) {
            return ResponseEntity.status(404).body("Page " + pageId + " not found");
        }

        if (frameController.findPage(pageId) != -1) {
            this.pageFaultHistoric = false;
            return ResponseEntity.status(200)
                    .body(responseConstructor(pageId, frameController.findPage(pageId), pageFaultHistoric,
                            "Page " + pageId + " already loaded in frame " + frameController.findPage(pageId)));
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                frame.setPage(page);
                this.contPageFaults++;
                this.pageFaultHistoric = true;
                return ResponseEntity.status(200)
                        .body(responseConstructor(pageId, frame.getId(), pageFaultHistoric, "Page " + pageId + " loaded successfully in frame " + frame.getId()));
            }
        }

        Page pageToRemove = frames.get(0).getPage();

        for (int i = 0; i < frames.size(); i++) {
            if (frames.get(i).getPage().getAge() > pageToRemove.getAge()) {
                pageToRemove = frames.get(i).getPage();
            }
        }

        this.contPageFaults++;
        this.pageFaultHistoric = true;

        this.frames.get((pageToRemove.getId())).setPage(page);

        return ResponseEntity.status(200)
                .body(responseConstructor(pageId, pageToRemove.getId(), pageFaultHistoric, "Page " + pageId + " accessed successfully in frame " + (pageToRemove.getId())));
    }

    @GetMapping("/getArrayFrames")
    public ResponseEntity<?> FIFO_getArrayFrames() {

        return ResponseEntity.status(200).body(frames);

    }

    @GetMapping("/getFaultHistoric")
    public ResponseEntity<?> FIFO_getFaultHistoric() {

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

    /*
     * public static void main(String[] args) {
     * System.out.println("FIFO");
     * 
     * FrameController frame = new FrameController();
     * PageController page = new PageController();
     * 
     * frame.createFrames(3);
     * page.createPagesRandom(5);
     * 
     * FIFOAlgorithm fifo = new FIFOAlgorithm();
     * 
     * fifo.FIFO_init();
     * 
     * System.out.println(fifo.FIFO_acessPage(1));
     * System.out.println(fifo.FIFO_acessPage(2));
     * System.out.println(fifo.FIFO_acessPage(3));
     * System.out.println(fifo.FIFO_acessPage(4));
     * System.out.println(fifo.FIFO_acessPage(1));
     * System.out.println(fifo.FIFO_acessPage(2));
     * }
     */
}
