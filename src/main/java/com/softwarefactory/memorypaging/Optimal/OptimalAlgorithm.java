package com.softwarefactory.memorypaging.Optimal;

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
@Table(name = "optimal")
public class OptimalAlgorithm {
    
    @Id
    @Generated("uuid")
    protected int id;

}
