package com.example.community.board;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("NOTICE")
@Getter
@Setter
public class Notice extends Board {
    private String noticeLevel;
}