package com.example.community.board;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("FREE")
@Getter
@Setter
public class Free extends Board{
    private String category;
}