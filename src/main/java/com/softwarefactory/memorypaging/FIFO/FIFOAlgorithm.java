package com.softwarefactory.memorypaging.FIFO;

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
@RequestMapping("/fifo")
public class FIFOAlgorithm {
        int pageFaults;

        FrameController frameController = new FrameController();
        PageController pageController = new PageController();

        ArrayList<Frame> frames = new ArrayList<>();
        ArrayList<Page> pages = new ArrayList<>();

        @PostMapping("/init")
        public ResponseEntity<?> FIFO_init() {
            frames = frameController.getFrames();
            if (frames.size() == 0) {
                return ResponseEntity.status(404).body("RAM not found");
            }
            pages = pageController.getPages();
            if (pages.size() == 0) {
                return ResponseEntity.status(404).body("Pages not found");
            }

            return ResponseEntity.status(200).body("FIFO algorithm init successfully");
        }

        @PostMapping("/acessPage")
        public ResponseEntity<?> FIFO_acessPage(int pageId) {
            
            //incrementa o tempo de vida de todas as páginas
            for (Frame frame : frames) {
                frame.getPage().setAge( frame.getPage().getAge() + 1 );
            }

            Page page = pageController.findPage(pageId);

            if(page == null) {
                return ResponseEntity.status(404).body("Page " + pageId + " not found");
            }

            for (Frame frame : frames) {
                if (frame.getPage() == null) {
                    frame.setPage(page);
                    return ResponseEntity.status(200).body("Page " + pageId + " loaded successfully in frame " + frame.getId() + "");
                }
            }

            frames.sort((frame1, frame2) -> frame1.getPage().getAge() - frame2.getPage().getAge());
            this.pageFaults++;
            frames.get(0).setPage(page);

            return ResponseEntity.status(200).body("Page " + pageId + " accessed successfully in frame " + frames.get(0).getId() + "");
        }

        @GetMapping ("/getArrayFrames")
        public ResponseEntity<?> FIFO_getArrayFrames() {
            
            return ResponseEntity.status(200).body(frames);

        }
    }
