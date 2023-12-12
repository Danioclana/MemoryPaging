package com.softwarefactory.memorypaging.Page;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "page")
public class Page {
   
    @Id
    @Generated("uuid")
    protected int id;

    protected String content;
    protected int priority;
    protected int lastUsed;
    
    
    public Page(String content, int priority, int lastUsed) {
        this.content = content;
        this.priority = priority;
        this.lastUsed = lastUsed;
    }
}   
