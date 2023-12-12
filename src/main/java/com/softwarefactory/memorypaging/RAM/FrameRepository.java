package com.softwarefactory.memorypaging.RAM;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameRepository extends JpaRepository<Frame, Integer>  {

    void deleteById(int id);
    
}


