package com.softwarefactory.memorypaging.RAM;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping("/frame")
public class FrameController {

    public static ArrayList<Frame> frames = new ArrayList<Frame>();

    @PostMapping("/create")
    public ResponseEntity<?> createFrame() {

        Frame frame = new Frame();
        frame.setId(frames.size() + 1);
        frame.setPage(null);
        frames.add(frame);

        return ResponseEntity.status(201).body("Frame " + frames.size() + " created successfully");
    }

    @PostMapping("/createAll")
    public ResponseEntity<?> createFrames(@RequestBody int numberOfFrames) {

        frames.clear();

        for (int i = 0; i < numberOfFrames; i++) {
            Frame frame = new Frame();
            frame.setId(frames.size() + 1);
            frame.setPage(null);
            frames.add(frame);
        }

        return ResponseEntity.status(201).body(frames.size() + " frames created successfully");
    }

    @GetMapping("/getPageId")
    public ResponseEntity<?> getPageId(@PathVariable int id) {

        for (Frame frame : frames) {
            if (frame.getId() == id) {
                return ResponseEntity.status(200).body(frame.getPage().getId());
            }
        }

        return ResponseEntity.status(404).body("Frame not found");
    }

    @GetMapping("/delete")
    public ResponseEntity<?> deleteFrame() {

        frames.remove(frames.size() - 1);

        return ResponseEntity.status(200).body("Frame" + frames.size() + "deleted successfully");
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<?> deleteAllFrames() {

        frames.clear();

        return ResponseEntity.status(200).body("All frames deleted successfully");
    }

    public int findPage(int id) {
        for (Frame frame : frames) {
            if (frame.getPage() != null) {
                if (frame.getPage().getId() == id) {
                    return frame.getId();
                }
            }
        }
        return -1;
    }

    public int findEmptyFrame() {
        for (Frame frame : frames) {
            if (frame.getPage() == null) {
                return frame.getId();
            }
        }
        return -1;
    }

    public Frame findFrameById (int id) {
        for (Frame frame : frames) {
            if (frame.getId() == id) {
                return frame;
            }
        }
        return null;
    }

}
