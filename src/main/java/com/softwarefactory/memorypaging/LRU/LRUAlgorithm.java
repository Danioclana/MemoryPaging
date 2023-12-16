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
        boolean pageFaultHitoric;


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

            if(page == null) {
                return ResponseEntity.status(404).body("Page " + pageId + " not found");
            }

            if(frameController.findPage(pageId) != -1) {
                this.pageFaultHitoric = false;
                return ResponseEntity.status(200).body("Page " + pageId + " already loaded in frame " + frameController.findPage(pageId));
            }

            for (Frame frame : frames) {
                if (frame.getPage() == null) {
                    page.setTimeLastUsed(time);
                    frame.setPage(page);

                    this.contPageFaults++;
                    this.pageFaultHitoric = true;
                    
                    return ResponseEntity.status(200).body("Page " + pageId + " loaded successfully in frame " + frame.getId());
                }
            }

            frames.sort((frame1, frame2) -> frame2.getPage().getTimeLastUsed() - frame1.getPage().getTimeLastUsed());
            page.setTimeLastUsed(time);
            this.contPageFaults++;
            this.pageFaultHitoric = true;

            frames.get(0).setPage(page);

            return ResponseEntity.status(200).body("Page " + pageId + " accessed successfully in frame " + frames.get(0).getId());
        }

        @GetMapping ("/getArrayFrames")
        public ResponseEntity<?> LRU_getArrayFrames() {
            
            return ResponseEntity.status(200).body(frames);

        }

        @GetMapping ("/getFaultHistoric")
        public ResponseEntity<?> LRU_getFaultHistoric() {
            
            return ResponseEntity.status(200).body(this.pageFaultHitoric);

        }

        @GetMapping ("/getContPageFaults")
        public ResponseEntity<?> LRU_getContPageFaults() {
            
            return ResponseEntity.status(200).body(this.contPageFaults);

        }
}
