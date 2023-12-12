package com.softwarefactory.memorypaging.LRU;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LRURepository extends JpaRepository<LRUAlgorithm, Integer>  {
    
}
