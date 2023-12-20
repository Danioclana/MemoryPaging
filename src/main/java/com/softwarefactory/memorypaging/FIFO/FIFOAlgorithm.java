package com.softwarefactory.memorypaging.FIFO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.softwarefactory.memorypaging.Page.Page;
import com.softwarefactory.memorypaging.RAM.Frame;
import com.softwarefactory.memorypaging.Page.PageController;
import com.softwarefactory.memorypaging.RAM.FrameController;

@RestController
@RequestMapping("/fifo")
public class FIFOAlgorithm {
    private int contPageFaults;
    private boolean pageFaultHistoric;
    private FrameController frameController = new FrameController();
    private PageController pageController = new PageController();
    private List<Frame> frames = new ArrayList<>();
    private List<Page> pages = new ArrayList<>();

    private int[] pagesIdList;

    @PostMapping("/acessPages")
    public ResponseEntity<List<Object>> FIFO_acessPages(@RequestBody int[] pagesId) {
        
        List<Object> response = new ArrayList<>();

        FIFO_init();

        for (int pageId : pagesId) {
            response.add(FIFO_acessPage(pageId));
        }

        return ResponseEntity.status(200).body(response);
    }


    private void FIFO_init() {
        frames.clear();
        pages.clear();

        frames.addAll(FrameController.frames);
        pages.addAll(PageController.pages);

        pagesIdList = new int[frames.size()];

        contPageFaults = 0;
    }

    private Object FIFO_acessPage(int pageId) {
        Page page = pageController.isExists(pageId);
        

        if (page == null) {
            System.out.println("PÁGINA " + pageId + " NÃO EXISTE NA MEMÓRIA");

            return null;
        }

        int frameIndex = frameController.findPage(pageId);

        if (frameIndex != -1) {
            this.pageFaultHistoric = false;
            ageIncrement();

            System.out.println("PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameIndex);

            
            List<Integer> quadros = new ArrayList<>();
            for (int j = 0; j < pagesIdList.length; j++) {
                quadros.add(pagesIdList[j]);
            }

            return responseConstructor(pageId, quadros, pageFaultHistoric,
                    "PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId), contPageFaults);
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                ageIncrement();

                FIFO_pagesIdListUpdate(frame.getId(), page.getId());

                List<Integer> quadros = new ArrayList<>();
                for (int j = 0; j < pagesIdList.length; j++) {
                    quadros.add(pagesIdList[j]);
                }

                System.out.println("PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId());
                return responseConstructor(pageId, quadros, pageFaultHistoric,
                        "PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId(), contPageFaults);
            }
        }

        Page pageToRemove = frames.get(0).getPage();

        for (Frame frame : frames) {

            if (frame.getPage().getAge() > pageToRemove.getAge()) {
                pageToRemove = frame.getPage();
            }
        }

        pageToRemove.setAge(-1);

        this.contPageFaults++;
        this.pageFaultHistoric = true;

        Frame frame = frameController.findFrameById(frameController.findPage(pageToRemove.getId()));
        frame.setPage(page);

        ageIncrement();

        FIFO_pagesIdListUpdate(frame.getId(), page.getId());

        List<Integer> quadros = new ArrayList<>();
        for (int j = 0; j < pagesIdList.length; j++) {

            quadros.add(pagesIdList[j]);
        }

        System.out.println("PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());

        return responseConstructor(pageId, quadros, pageFaultHistoric,
                "PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId(),
                contPageFaults);
    }

    private Object responseConstructor(int pageId, Object frameId, boolean pageFaultHistoric, String action,
            int contPageFaults) {
        Object[] response = new Object[5];

        response[0] = pageId;
        response[1] = frameId;
        response[2] = pageFaultHistoric;
        response[3] = action;
        response[4] = contPageFaults;

        return response;
    }

    private void FIFO_pagesIdListUpdate(int frameId, int pageId) {
        for (int i = 0; i < frames.size(); i++) {
            if ((i + 1) == frameId) {
                pagesIdList[i] = pageId;
                System.out.println("pagesIdList[" + i + "] = " + pageId);
                return;
            }
        }
    }

    private void ageIncrement() {
        frames.stream().filter(frame -> frame.getPage() != null)
                .forEach(frame -> frame.getPage().setAge(frame.getPage().getAge() + 1));
    }


    @GetMapping("/getArrayFrames")
    public ResponseEntity<List<Frame>> FIFO_getArrayFrames() {
        return ResponseEntity.status(200).body(frames);
    }

    @GetMapping("/getFaultHistoric")
    public ResponseEntity<Boolean> FIFO_getFaultHistoric() {
        return ResponseEntity.status(200).body(this.pageFaultHistoric);
    }

    @GetMapping("/getContPageFaults")
    public ResponseEntity<Integer> FIFO_getContPageFaults() {
        return ResponseEntity.status(200).body(this.contPageFaults);
    }

    //----------------------main for test ----------------------
    public static void main(String[] args) {
        FIFOAlgorithm fifo = new FIFOAlgorithm();

        PageController pageController = new PageController();
        pageController.createPages(8);

        FrameController frameController = new FrameController();
        frameController.createFrames(3);

        fifo.FIFO_acessPages(new int[] { 8, 1, 2, 3, 1, 4, 1, 5, 3, 4, 1, 4, 3, 2, 3,
                1, 2, 8, 1, 2 });

        System.out.println("ContPageFaults: " + fifo.contPageFaults);
    }

}
