package com.ia.model;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private String question;
    private boolean isMultipleChoice;
    private List<Answer> answers;
}
