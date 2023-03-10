package com.example.a2quizbuilder;

public class Quiz {

    private String m_name;

    private int m_secondsPerQuestion;

    private String m_id;

    public Quiz(String pId, String pName, String pSeconds) {
        this.m_id = pId;
        this.m_name = pName;
        this.m_secondsPerQuestion = Integer.parseInt(pSeconds) * 1000;
    }

    //getters
    public String getID() {
        return this.m_id;
    }

    public String getM_name() {
        return this.m_name;
    }

    public long getSeconds() {
        return this.m_secondsPerQuestion;
    }
}
