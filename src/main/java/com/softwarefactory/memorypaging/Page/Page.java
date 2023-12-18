package com.softwarefactory.memorypaging.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Page {
   
    protected int id;

    protected int age;
    protected int timeLastUsed;
    protected int futureAcess;
    
}   
