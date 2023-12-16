package com.softwarefactory.memorypaging.Page;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RestController
@RequestMapping("/page")
public class PageController {

    ArrayList<Page> pages = new ArrayList<Page>();


    @PostMapping("/createAll")
    public ResponseEntity<?> createPagesRandom (@PathVariable int numberOfPages) {

        for (int i = 0; i < numberOfPages; i++) {

            Page page = new Page();
            page.setId(pages.size() + 1);
            page.setAge(-1);
            page.setTimeLastUsed(-1);
            page.setPageRequests(0);
            pages.add(page);
        }

        return ResponseEntity.status(200).body(pages.size() + " pages created successfully");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPage () {

        Page page = new Page();
        page.setId(pages.size() + 1);
        page.setAge(-1);
        page.setTimeLastUsed(-1);
        page.setPageRequests(0);
        pages.add(page);

        return ResponseEntity.status(200).body("Page " + pages.size() + " created successfully");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletePage(@PathVariable int id) {
        
        for (Page page : pages) {
            if (page.getId() == id) {
                pages.remove(page);
                break;
            }
        }

        return ResponseEntity.status(200).body("Page" + id + " deleted successfully");
    }

     @PostMapping("/deleteLast")
    public ResponseEntity<?> deleteLastPage () {
        
        pages.remove(pages.size());

        return ResponseEntity.status(200).body("Page" + pages.size() + " deleted successfully");
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<?> deleteAll() {
        
        pages.clear();

        return ResponseEntity.status(200).body("Page deleted successfully");
    }

    public Page findPage(int id) {
        for (Page page : pages) {
            if (page.getId() == id) {
                return page;
            }
        }
        return null;
    }
}
