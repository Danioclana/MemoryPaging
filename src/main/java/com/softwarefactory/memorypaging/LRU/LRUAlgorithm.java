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
        frames.clear();
        pages.clear();

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
    public Object LRU_acessPage(int pageId) {

        time++;

        Page page = pageController.isExists(pageId);

        if (page == null) {
            return ResponseEntity.status(404).body("Page " + pageId + " not found");
        }

        if (frameController.findPage(pageId) != -1) {
            this.pageFaultHistoric = false;
            page.setTimeLastUsed(time);

            System.out.println("PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId));

            return responseConstructor(pageId, frameController.findPage(pageId), pageFaultHistoric,
            "PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId));
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                page.setTimeLastUsed(time);
                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                System.out.println("PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId());

                return responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                    "PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId());
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

        System.out.println("PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());
        return responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                "PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());
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

    private Object responseConstructor(int pageId, int frameId, boolean pageFaultHistoric, String action) {
        Object[] response = new Object[4];

        response[0] = pageId;
        response[1] = frameId;
        response[2] = pageFaultHistoric;
        response[3] = action;

        return response;
    } 

}
