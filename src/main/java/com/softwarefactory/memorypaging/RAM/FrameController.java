package com.softwarefactory.memorypaging.RAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.softwarefactory.memorypaging.FIFO.FIFOAlgorithm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping("/frame")
public class FrameController {

    public static final ArrayList<Frame> frames = new ArrayList<>();

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createFrame() {
        Frame frame = new Frame();
        frame.setId(frames.size() + 1);
        frame.setPage(null);
        frames.add(frame);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Frame " + frames.size() + " created successfully");

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/createAll")
    public ResponseEntity<Map<String, String>> createFrames(@RequestBody int numberOfFrames) {
        frames.clear();

        for (int i = 0; i < numberOfFrames; i++) {
            Frame frame = new Frame();
            frame.setId(frames.size() + 1);
            frame.setPage(null);
            frames.add(frame);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", frames.size() + " frames created successfully");

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/getPageId/{id}")
    public ResponseEntity<?> getPageId(@PathVariable int id) {
        for (Frame frame : frames) {
            if (frame.getId() == id) {
                Map<String, Integer> response = new HashMap<>();
                response.put("pageId", frame.getPage() != null ? frame.getPage().getId() : null);
                return ResponseEntity.status(200).body(response);
            }
        }

        return ResponseEntity.status(404).body("Frame not found");
    }

    @GetMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteFrame() {
        frames.remove(frames.size() - 1);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Frame " + frames.size() + " deleted successfully");

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<Map<String, String>> deleteAllFrames() {
        frames.clear();

        Map<String, String> response = new HashMap<>();
        response.put("message", "All frames deleted successfully");

        return ResponseEntity.status(200).body(response);
    }

    public int findPage(int id) {
        for (Frame frame : frames) {
            if (frame.getPage() != null && frame.getPage().getId() == id) {
                return frame.getId();
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

    public Frame findFrameById(int id) {
        for (Frame frame : frames) {
            if (frame.getId() == id) {
                return frame;
            }
        }
        return null;
    }
}
