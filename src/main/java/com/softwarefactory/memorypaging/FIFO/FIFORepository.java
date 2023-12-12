package com.softwarefactory.memorypaging.FIFO;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FIFORepository extends JpaRepository<FIFOAlgorithm, Integer>  {
    
}
