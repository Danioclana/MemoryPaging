package com.softwarefactory.memorypaging.Optimal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OptimalRepository extends JpaRepository<OptimalAlgorithm, Integer>  {

    void deleteById(int id);
    
}


