package com.softwarefactory.memorypaging.RAM;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frame")
public class FrameController<T> {
    
    @Autowired
    private FrameRepository frameRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createFrame() {
        Frame newFrame = new Frame();
        frameRepository.save(newFrame);
        
        return ResponseEntity.status(201).body(newFrame);
    }

    @PostMapping("/createAll")
    public ResponseEntity<?> createAllFrames(@PathVariable int numberOfFrames) {
        Frame [] frames = new Frame[numberOfFrames];

        for (Frame newFrame : frames) {
            newFrame = new Frame();
            frameRepository.save(newFrame);
        }
        
        return ResponseEntity.status(201).body(frames);
    }    

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFrame(@PathVariable int id) {
        frameRepository.deleteById(id);
        
        return ResponseEntity.status(200).body("Frame deleted successfully");
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAllFrames() {
        frameRepository.deleteAll();
        
        return ResponseEntity.status(200).body("All frames deleted successfully");
    }


}
