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

            return responseConstructor(pageId, frameIndex, pageFaultHistoric,
                    "Page " + pageId + " already loaded in frame " + frameIndex);
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                frame.setPage(page);

                this.contPageFaults++;
                this.pageFaultHistoric = true;

                ageIncrement();

                return responseConstructor(pageId, frame.getId(), pageFaultHistoric,
                        "Page " + pageId + " loaded successfully in frame " + frame.getId());
            }
        }

        Frame frameToRemove = frames.stream()
                .min((f1, f2) -> Integer.compare(f1.getPage().getAge(), f2.getPage().getAge()))
                .orElseThrow();

        frameToRemove.getPage().setAge(-1);

        this.contPageFaults++;
        this.pageFaultHistoric = true;

        Frame frame = frameController.findFrameById(frameController.findPage(frameToRemove.getId()));
        frame.setPage(page);

        ageIncrement();

        return responseConstructor(pageId, frameToRemove.getId(), pageFaultHistoric,
                "Page " + pageId + " accessed successfully in frame " + frame.getId());
    }

    private List<Object> responseConstructor(int pageId, int frameId, boolean pageFaultHistoric, String action) {
        List<Object> response = new ArrayList<>();
        response.add(pageId);
        response.add(frameId);
        response.add(pageFaultHistoric);
        response.add(action);
        return response;
    }

    private void ageIncrement() {
        frames.stream().filter(frame -> frame.getPage() != null)
                .forEach(frame -> frame.getPage().setAge(frame.getPage().getAge() + 1));
    }

}
