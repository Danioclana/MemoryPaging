package com.softwarefactory.memorypaging.RAM;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping("/frame")
public class FrameController {

    ArrayList<Frame> frames = new ArrayList<Frame>();

    @PostMapping("/create")
    public ResponseEntity<?> createFrames(@PathVariable int numberOfFrames) {

        for (Frame newFrame : frames) {
            newFrame = new Frame();
            newFrame.setId(frames.size() + 1);
            newFrame.setPage(null);

            frames.add(newFrame);
        }
        
        return ResponseEntity.status(201).body(frames.size() + " frames created successfully");
    }    

    @PostMapping("/delete")
    public ResponseEntity<?> deleteFrame() {
        
        frames.remove(frames.size());
        
        return ResponseEntity.status(200).body("Frame"+ frames.size() + "deleted successfully");
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAllFrames() {
        
        frames.clear();
        
        return ResponseEntity.status(200).body("All frames deleted successfully");
    }

}
