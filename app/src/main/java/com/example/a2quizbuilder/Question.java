package com.example.a2quizbuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {
    private String m_id;
    private String m_question;
    private String m_answer;

    public Question(String pId, String pQuestion, String pAnswer) {
        this.m_id = pId;
        this.m_question = pQuestion;
        this.m_answer = pAnswer;
    }

    //getters
    public String getID() {
        return this.m_id;
    }

    public String getQuestion() {
        return this.m_question;
    }

    public String getAnswer() {
        return this.m_answer;
    }

    //functions to make the question class parcelable so that it can be bundled in intents
    public Question(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);

        this.m_question = data[0];
        this.m_answer = data[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.m_question, this.m_answer});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
