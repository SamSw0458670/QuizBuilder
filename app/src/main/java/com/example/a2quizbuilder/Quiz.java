package com.example.a2quizbuilder;

public class Quiz {

    private String name;

    private int secondsPerQuestion;

    public Quiz(String pName, String pSeconds){
        this.name = pName;
        this.secondsPerQuestion = Integer.parseInt(pSeconds);
    }

}
