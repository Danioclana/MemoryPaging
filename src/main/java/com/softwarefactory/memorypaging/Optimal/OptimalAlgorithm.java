package com.softwarefactory.memorypaging.Optimal;

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
@RequestMapping("/optimal")
public class OptimalAlgorithm {
    int time;
    int contPageFaults;
    boolean pageFaultHistoric;

    FrameController frameController = new FrameController();
    PageController pageController = new PageController();

    ArrayList<Frame> frames = new ArrayList<>();
    ArrayList<Page> pages = new ArrayList<>();

    int[] acess;

    @PostMapping("/init")
    public ResponseEntity<?> Optimal_init() {
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

        return ResponseEntity.status(200).body("Optimal algorithm init successfully");
    }

    @PostMapping("/acessPage")
        public ResponseEntity<?> Optimal_acessPage(int pageId) {

        time++;

        Page page = pageController.isExists(pageId);

        if (page == null) {
            return ResponseEntity.status(404).body("Page " + pageId + " not found");
        }

        if (frameController.findPage(pageId) != -1) {
            this.pageFaultHistoric = false;
            updateFutureAcessPage(pageId, time);

            return ResponseEntity.status(200)
                    .body(responseConstructor(pageId, frameController.findPage(pageId), pageFaultHistoric,
                            "Page " + pageId + " already loaded in frame " + frameController.findPage(pageId)));
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {

                updateFutureAcessPage(pageId, time);
                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                return ResponseEntity.status(200)
                        .body(responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                                "Page " + pageId + " loaded successfully in frame " + frame.getId()));
            }
        }

        Page pageToRemove = frames.get(0).getPage();

        for (Frame frame : frames) {

            if (frame.getPage().getFutureAcess() < pageToRemove.getFutureAcess()) {
                pageToRemove = frame.getPage();
            }
        }

        this.contPageFaults++;
        this.pageFaultHistoric = true;

        updateFutureAcessPage(pageId, time);

        Frame frame = frameController.findFrameById(frameController.findPage(pageToRemove.getId()));
        frame.setPage(page);

        return ResponseEntity.status(200)
                .body(responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                        "Page " + pageId + " accessed successfully in frame " + frame.getId()));
                        
    }

    @PostMapping("/acessPages")
    public ResponseEntity<?> Optimal_acessPages(@RequestBody int[] pagesId) {

        acess = new int [pagesId.length];
        for (int i = 0; i < pagesId.length; i++) {
            acess[i] = pagesId[i];
            updateFutureAcessPage(pagesId[i], 0);
        }

        ArrayList<Object> response = new ArrayList<>();

        for (int i = 0; i < pagesId.length; i++) {
            Page page = pageController.isExists(pagesId[i]);

            if (page == null) {
                System.out.println("Page " + pagesId[i] + " not found");
                return ResponseEntity.status(404).body("Page " + pagesId[i] + " not found");
            }
            
            response.add(Optimal_acessPage(pagesId[i]));
        }

        Object[] responseArray = new Object[response.size()];
        
        for(int i = 0; i < response.size(); i++){
            responseArray[i]=response.get(i);
        }

        return ResponseEntity.status(200).body(responseArray);
    }


    public Object responseConstructor(int pageId, int frameId, boolean pageFaultHistoric, String action) {
        Object[] response = new Object[4];

        response[0] = pageId;
        response[1] = frameId;
        response[2] = pageFaultHistoric;
        response[3] = action;

        return response;
    }

    @GetMapping("/getArrayFrames")
    public ResponseEntity<?> Optimal_getArrayFrames() {

        return ResponseEntity.status(200).body(frames);

    }

    @GetMapping("/getFaultHistoric")
    public ResponseEntity<?> Optimal_getFaultHistoric() {

        return ResponseEntity.status(200).body(this.pageFaultHistoric);

    }

    public void updateFutureAcessPage(int pageId, int time) {
        for (int i = time; i < acess.length; i++) {
                if (acess[i] == pageId) {
                    Page page = pageController.isExists(pageId);
                    page.setFutureAcess(page.getFutureAcess() + 1);
                }
        }
    }

    @GetMapping("/getContPageFaults")
    public ResponseEntity<?> Optimal_getContPageFaults() {

        return ResponseEntity.status(200).body(this.contPageFaults);

    }


}
