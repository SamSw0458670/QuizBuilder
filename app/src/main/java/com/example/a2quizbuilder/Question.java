package com.example.a2quizbuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {
    private String id;
    private String question;
    private String answer;

    public Question(String pId, String pQuestion, String pAnswer){
        this.id = pId;
        this.question = pQuestion;
        this.answer = pAnswer;
    }

    public String getID(){
        return this.id;
    }

    public String getQuestion(){
        return this.question;
    }

    public String getAnswer(){
        return this.answer;
    }

    //Parcelling portion
    public Question(Parcel in){
        String[] data = new String[2];

        in.readStringArray(data);

        this.question = data[0];
        this.answer = data[1];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.question, this.answer});
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
