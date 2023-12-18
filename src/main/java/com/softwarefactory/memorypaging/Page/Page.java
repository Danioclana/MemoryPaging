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
    public void setPageRequests(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}   
