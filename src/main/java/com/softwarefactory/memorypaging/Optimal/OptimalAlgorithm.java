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

    @PostMapping("/init")
    public ResponseEntity<?> Optimal_init() {
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

        return ResponseEntity.status(200).body("Optimal algorithm init successfully");
    }

    @PostMapping("/acessPage")
    public Object Optimal_acessPage(int pageId) {

        time++;

        Page page = pageController.isExists(pageId);

        if (page == null) {
            return ResponseEntity.status(404).body("Page " + pageId + " not found");
        }

        if (frameController.findPage(pageId) != -1) {
            this.pageFaultHistoric = false;
            if (!page.getFutureAcess().isEmpty()) {
                page.getFutureAcess().remove(0);
            }

            System.out.println("PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId));

            return responseConstructor(pageId, frameController.findPage(pageId), pageFaultHistoric,
                            "PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId));
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {

                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                if (!page.getFutureAcess().isEmpty()) {
                    page.getFutureAcess().remove(0);
                }

                System.out.println("PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId());

                return responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                                "PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId());
            }
        }

        Page pageToRemove = frames.get(0).getPage();

        for (Frame frame : frames) {
            if (frame.getPage().getFutureAcess().isEmpty()) {
                pageToRemove = frame.getPage();
            } else if (!frame.getPage().getFutureAcess().isEmpty() 
                    && !pageToRemove.getFutureAcess().isEmpty() 
                        && frame.getPage().getFutureAcess().get(0) > pageToRemove.getFutureAcess().get(0) ) {
                pageToRemove = frame.getPage();
            }
        }

        this.contPageFaults++;
        this.pageFaultHistoric = true;

        Frame frame = frameController.findFrameById(frameController.findPage(pageToRemove.getId()));

        if (!page.getFutureAcess().isEmpty()) {
            page.getFutureAcess().remove(0);
        }

        frame.setPage(page);

        System.out.println("PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());

        return responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                        "PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());

    }

    @PostMapping("/acessPages")
    public ResponseEntity<?> Optimal_acessPages(@RequestBody int[] pagesId) {

        ArrayList<Object> response = new ArrayList<>();

        for (int i : pagesId) {
            updateFutureAcessPage(i, pagesId);
        }

        for (int i = 0; i < pagesId.length; i++) {
            Page page = pageController.isExists(pagesId[i]);

            if (page == null) {
                System.out.println("Page " + pagesId[i] + " not found");
                return ResponseEntity.status(404).body("Page " + pagesId[i] + " not found");
            }

            response.add(Optimal_acessPage(pagesId[i]));
        }

        Object[] responseArray = new Object[response.size()];

        for (int i = 0; i < response.size(); i++) {
            responseArray[i] = response.get(i);
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

    public void updateFutureAcessPage(int pageId, int[] acess) {

        Page page = pageController.isExists(pageId);

        if (!page.getFutureAcess().isEmpty()) {
            return;
        }

        for (int i = 0; i < acess.length; i++) {
            if (acess[i] == page.getId()) {
                page.getFutureAcess().add(i + 1);
            }
        }

        System.out.println("Future acess of page " + pageId + " = " + page.getFutureAcess());
    }

    @GetMapping("/getContPageFaults")
    public ResponseEntity<?> Optimal_getContPageFaults() {

        return ResponseEntity.status(200).body(this.contPageFaults);

    }

}
