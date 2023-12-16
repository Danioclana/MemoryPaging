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

        // incrementa o tempo de vida de todas as páginas
        for (Frame frame : frames) {
            frame.getPage().setAge(frame.getPage().getAge() + 1);
        }

        Page page = pageController.isExists(pageId);

        if (page == null) {
            return ResponseEntity.status(404).body("Page " + pageId + " not found");
        }

        if (frameController.findPage(pageId) != -1) {
            this.pageFaultHistoric = false;
            return ResponseEntity.status(200)
                    .body("Page " + pageId + " already loaded in frame " + frameController.findPage(pageId));
        }

        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                frame.setPage(page);
                this.contPageFaults++;
                this.pageFaultHistoric = true;
                return ResponseEntity.status(200)
                        .body("Page " + pageId + " loaded successfully in frame " + frame.getId());
            }
        }

        this.frames.sort((frame1, frame2) -> frame1.getPage().getAge() - frame2.getPage().getAge());
        this.contPageFaults++;
        this.pageFaultHistoric = true;

        this.frames.get(0).setPage(page);

        return ResponseEntity.status(200)
                .body("Page " + pageId + " accessed successfully in frame " + frames.get(0).getId());
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
}
