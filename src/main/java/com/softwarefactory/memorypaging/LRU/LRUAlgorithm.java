package com.softwarefactory.memorypaging.LRU;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
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

        FrameController frameController = new FrameController();
        PageController pageController = new PageController();

        ArrayList<Frame> frames = new ArrayList<>();
        ArrayList<Page> pages = new ArrayList<>();

        @PostMapping("/init")
        public ResponseEntity<?> LRU_init() {
            frames = frameController.getFrames();
            if (frames.size() == 0) {
                return ResponseEntity.status(404).body("RAM not found");
            }
            pages = pageController.getPages();
            if (pages.size() == 0) {
                return ResponseEntity.status(404).body("Pages not found");
            }
            this.time = -1;

            return ResponseEntity.status(200).body("LRU algorithm init successfully");
        }

        @PostMapping("/acessPage")
        public ResponseEntity<?> LRU_acessPage(int pageId) {

            time++;

            Page page = pageController.findPage(pageId);

            if(page == null) {
                return ResponseEntity.status(404).body("Page " + pageId + " not found");
            }

            for (Frame frame : frames) {
                if (frame.getPage() == null) {
                    page.setTimeLastUsed(time);
                    frame.setPage(page);
                    
                    return ResponseEntity.status(200).body("Page " + pageId + " accessed successfully");
                }
            }

            frames.sort((frame1, frame2) -> frame1.getPage().getTimeLastUsed() - frame2.getPage().getTimeLastUsed());
            page.setTimeLastUsed(time);
            frames.get(0).setPage(page);

            return ResponseEntity.status(200).body("Page " + pageId + " accessed successfully");
        }
}
