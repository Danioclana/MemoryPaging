package com.softwarefactory.memorypaging.RAM;

import com.softwarefactory.memorypaging.Page.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Frame {
    
    protected int id;
    protected Page page;

}


