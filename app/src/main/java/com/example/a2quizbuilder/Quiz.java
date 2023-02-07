package com.example.a2quizbuilder;

public class Quiz {

    private String name;

    private int secondsPerQuestion;

    private String id;

    public Quiz(String pId, String pName, String pSeconds){
        this.id = pId;
        this.name = pName;
        this.secondsPerQuestion = Integer.parseInt(pSeconds);
    }

    public String getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getSeconds(){
        return String.valueOf(this.secondsPerQuestion);
    }

}
