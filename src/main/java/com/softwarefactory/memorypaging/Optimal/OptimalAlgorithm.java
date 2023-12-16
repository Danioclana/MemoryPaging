package com.softwarefactory.memorypaging.Optimal;

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
@RequestMapping("/optimal")
public class OptimalAlgorithm {
        int time;
        int pageFaults;

        FrameController frameController = new FrameController();
        PageController pageController = new PageController();

        ArrayList<Frame> frames = new ArrayList<>();
        ArrayList<Page> pages = new ArrayList<>();

        @PostMapping("/init")
        public ResponseEntity<?> Optimal_init() {
            frames = frameController.getFrames();
            if (frames.size() == 0) {
                return ResponseEntity.status(404).body("RAM not found");
            }
            pages = pageController.getPages();
            if (pages.size() == 0) {
                return ResponseEntity.status(404).body("Pages not found");
            }
            this.time = -1;

            return ResponseEntity.status(200).body("Optimal algorithm init successfully");
        }

        @PostMapping("/acessPage")
        public ResponseEntity<?> Optimal_acessPage(int pageId) {

            time++;

            Page page = pageController.findPage(pageId);

            if(page == null) {
                return ResponseEntity.status(404).body("Page " + pageId + " not found");
            }

            for (Frame frame : frames) {
                if (frame.getPage() == null) {
                    page.setTimeLastUsed(time);
                    page.setPageRequests(page.getPageRequests() + 1);
                    frame.setPage(page);

                    return ResponseEntity.status(200).body("Page " + pageId + " loaded successfully in frame " + frame.getId());
                }
            }

            frames.sort((frame1, frame2) -> frame1.getPage().getPageRequests() - frame2.getPage().getPageRequests());
            page.setTimeLastUsed(time);
            page.setPageRequests(page.getPageRequests() + 1);
            this.pageFaults++;

            frames.get(0).setPage(page);

            return ResponseEntity.status(200).body("Page " + pageId + " accessed successfully in frame " + frames.get(0).getId());
        }

        @GetMapping ("/getArrayFrames")
        public ResponseEntity<?> FIFO_getArrayFrames() {
            
            return ResponseEntity.status(200).body(frames);

        }
}
