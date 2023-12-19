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


    @PostMapping("/acessPages")
    public ResponseEntity<List<Object>> FIFO_acessPages(@RequestBody int[] pagesId) {
        List<Object> response = new ArrayList<>();

        FIFO_init();
        
        for (int pageId : pagesId) {
            response.add(FIFO_acessPage(pageId));
        }

        return ResponseEntity.status(200).body(response);
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

    private void FIFO_init() {
        frames.clear();
        pages.clear();

        frames.addAll(FrameController.frames);
        pages.addAll(PageController.pages);

        contPageFaults = 0;
    }

    private Object FIFO_acessPage(int pageId) {
        Page page = pageController.isExists(pageId);

        if (page == null) {
            return ResponseEntity.status(404).body("Page " + pageId + " not found");
        }

        int frameIndex = frameController.findPage(pageId);

        if (frameIndex != -1) {
            this.pageFaultHistoric = false;
            ageIncrement();

            System.out.println("PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameIndex);

            return responseConstructor(pageId, frameController.findPage(pageId), pageFaultHistoric,
            "PÁGINA " + pageId + " JÁ ESTÁ NO QUADRO " + frameController.findPage(pageId));
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                ageIncrement();

                System.out.println("PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId()
                );
                return responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                                "PÁGINA " + pageId + " CARREGADA NO QUADRO " + frame.getId());
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

        System.out.println("PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());

        return responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                "PÁGINA " + pageId + " SUBSTITUI " + pageToRemove.getId() + " NO QUADRO " + frame.getId());
    }

    private Object responseConstructor(int pageId, int frameId, boolean pageFaultHistoric, String action) {
        Object[] response = new Object[4];

        response[0] = pageId;
        response[1] = frameId;
        response[2] = pageFaultHistoric;
        response[3] = action;

        return response;
    } 

    private void ageIncrement() {
        frames.stream().filter(frame -> frame.getPage() != null)
                .forEach(frame -> frame.getPage().setAge(frame.getPage().getAge() + 1));
    }

    /*public static void main(String[] args) {
        FIFOAlgorithm fifo = new FIFOAlgorithm();

        PageController pageController = new PageController();
        pageController.createPages(8);

        FrameController frameController = new FrameController();
        frameController.createFrames(3);
        
        fifo.FIFO_acessPages(new int[] { 8, 1, 2, 3, 1, 4, 1, 5, 3, 4, 1, 4, 3, 2, 3, 1, 2, 8, 1, 2 });

        System.out.println("ContPageFaults: " + fifo.contPageFaults);
    }*/
    
}
