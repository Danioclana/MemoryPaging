package com.softwarefactory.memorypaging.Optimal;

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
@RequestMapping("/optimal")
public class OptimalAlgorithm {
        int time;

        FrameController frameController = new FrameController();
        PageController pageController = new PageController();

        ArrayList<Frame> frames = new ArrayList<>();
        ArrayList<Page> pages = new ArrayList<>();

        @PostMapping("/init")
        public ResponseEntity<?> Optimal_init() {
            frames = frameController.getFrames();
            pages = pageController.getPages();
            this.time = -1;

            return ResponseEntity.status(200).body("Optimal algorithm init successfully");
        }

        @PostMapping("/acessPage")
        public ResponseEntity<?> Optimal_acessPage(int pageId) {

            return ResponseEntity.status(400).body("not implemented yet");
        }
}
