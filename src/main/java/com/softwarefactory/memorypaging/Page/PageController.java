package com.softwarefactory.memorypaging.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class PageController<T> {
    
    @Autowired
    private PageRepository pageRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createPage(@RequestBody Page page) {
        Page newPage = page;
        pageRepository.save(newPage);
        
        return ResponseEntity.status(201).body(newPage);
    }

    @PostMapping("/createAll")
    public ResponseEntity<?> createAllPages(int numberOfPages) {
        Page [] pages = new Page[numberOfPages];

        for (Page newPage : pages) {
            
            //varialvel priority recebe um numero aleatorio de 1 a 10
            int priority = (int) (Math.random() * 10) + 1;
            //variavel lastUsed recebe um numero aleatorio de 1 a 10
            int lastUsed = (int) (Math.random() * 10) + 1;

            newPage = new Page("Content", priority, lastUsed);
            pageRepository.save(newPage);
        }
        
        return ResponseEntity.status(201).body(pages);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletePage(int id) {
        pageRepository.deleteById(id);
        
        return ResponseEntity.status(200).body("Page deleted successfully");
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAllPages() {
        pageRepository.deleteAll();
        
        return ResponseEntity.status(200).body("All pages deleted successfully");
    }

}
