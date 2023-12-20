package com.softwarefactory.memorypaging.LRU;

import java.util.ArrayList;
import java.util.List;

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
@RequestMapping("/lru")
public class LRUAlgorithm {
    int time;
    int contPageFaults;
    boolean pageFaultHistoric;

    FrameController frameController = new FrameController();
    PageController pageController = new PageController();
    ArrayList<Frame> frames = new ArrayList<>();
    ArrayList<Page> pages = new ArrayList<>();    
    
    private int[] pagesIdList;
    
    @PostMapping("/acessPages")
    public ResponseEntity<?> LRU_acessPages(@RequestBody int[] pagesId) {

        List<Object> response = new ArrayList<>();

        LRU_init();

        for (int pageId : pagesId) {
            response.add(LRU_acessPage(pageId));
        }

        return ResponseEntity.status(200).body(response);
    }
    

    public void LRU_init() {
        frames.clear();
        pages.clear();

        frames.addAll(FrameController.frames);
        pages.addAll(PageController.pages);

        pagesIdList = new int[frames.size()];

        time = 0;
        contPageFaults = 0;
    }

    public Object LRU_acessPage(int pageId) {

        time++;

        Page page = pageController.isExists(pageId);

        if (page == null) {
            return null;
        }

        if (frameController.findPage(pageId) != -1) {
            this.pageFaultHistoric = false;
            page.setTimeLastUsed(time);

            System.out.println("PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId));
             
            List<Integer> quadros = new ArrayList<>();
            for (int j = 0; j < pagesIdList.length; j++) {
                quadros.add(pagesIdList[j]);
            }

            return responseConstructor(pageId, quadros, pageFaultHistoric,
                    "PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId), contPageFaults);
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                page.setTimeLastUsed(time);
                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                System.out.println("PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId());

                LRU_pagesIdListUpdate(frame.getId(), page.getId());

                List<Integer> quadros = new ArrayList<>();
                for (int j = 0; j < pagesIdList.length; j++) {
                    quadros.add(pagesIdList[j]);
                }

                return responseConstructor(pageId, quadros, pageFaultHistoric,
                        "PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId(), contPageFaults);
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

        LRU_pagesIdListUpdate(frame.getId(), page.getId());

        List<Integer> quadros = new ArrayList<>();
        for (int j = 0; j < pagesIdList.length; j++) {
            quadros.add(pagesIdList[j]);
        }

        System.out.println("PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());
        
        return responseConstructor(pageId, quadros, pageFaultHistoric,
                "PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId(), contPageFaults);
    }

    private Object responseConstructor(int pageId, Object quadros, boolean pageFaultHistoric, String action, int contPageFaults) {
        Object[] response = new Object[5];

        response[0] = pageId;
        response[1] = quadros;
        response[2] = pageFaultHistoric;
        response[3] = action;
        response[4] = contPageFaults;

        return response;
    }

    private void LRU_pagesIdListUpdate(int frameId, int pageId) {
        for (int i = 0; i < frames.size(); i++) {
            if ((i + 1) == frameId) {
                pagesIdList[i] = pageId;
                return;
            }
        }
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

    //----------------------main for test ----------------------
    public static void main(String[] args) {
    LRUAlgorithm lru = new LRUAlgorithm();
     
    PageController pageController = new PageController();
    pageController.createPages(8);
    
    FrameController frameController = new FrameController();
    frameController.createFrames(3);
     
    lru.LRU_acessPages(new int[] { 8, 1, 2, 3, 1, 4, 1, 5, 3, 4, 1, 4, 3, 2, 3, 1, 2, 8, 1, 2 });
    
    System.out.println("ContPageFaults: " + lru.contPageFaults);
    }
    

}
