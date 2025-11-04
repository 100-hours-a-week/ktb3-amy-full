package com.example.community.board;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Qna extends Board{

    private boolean solved;
}