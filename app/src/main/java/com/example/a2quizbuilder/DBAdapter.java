package com.example.a2quizbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "quizDB";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_QUIZ_NAME = "quizzes";
    private static final String TABLE_QUESTION_NAME = "questions";

    // id column used for all tables
    private static final String ID_COL = "id";

    // column names used for the quizzes table
    private static final String QUIZ_NAME_COL = "name";
    private static final String QUIZ_SECONDS_PER_QUESTION_COL = "seconds_per_question";

    //column names used for the questions table
    private static final String QUIZ_ID_COL = "quiz_id";
    private static final String QUESTION_COL = "question";
    private static final String ANSWER_COL = "answer";

    // creating a constructor for the database handler.
    public DBAdapter(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {

        // make the query to create the quizzes table and then execute it
        String quizQuery = "CREATE TABLE " + TABLE_QUIZ_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QUIZ_NAME_COL + " TEXT,"
                + QUIZ_SECONDS_PER_QUESTION_COL + " INTEGER)";

        db.execSQL(quizQuery);

        // make the query to create the questions table with the FK to the
        // quiz table using the id column and then execute it
        String questionQuery = "CREATE TABLE " + TABLE_QUESTION_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QUIZ_ID_COL + " INTEGER,"
                + QUESTION_COL + " TEXT,"
                + ANSWER_COL + " TEXT, "
                + "FOREIGN KEY ("+QUIZ_ID_COL+") REFERENCES "+TABLE_QUIZ_NAME+"("+ID_COL+"));";

        db.execSQL(questionQuery);
    }

    // this function is used to add a new quiz to the quizzes table
    public void addNewQuiz(String quizName, String secondsPerQuestion) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //put each key and value into the ContentValues
        values.put(QUIZ_NAME_COL, quizName);
        values.put(QUIZ_SECONDS_PER_QUESTION_COL, secondsPerQuestion);

        //pass the new content values to the table
        db.insert(TABLE_QUIZ_NAME, null, values);

        db.close();
    }

    // function that is used to add a new question to the question table
    public void addNewQuestion(String quizID, String question, String answer) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //put each key and value into the ContentValues
        values.put(QUIZ_ID_COL, quizID);
        values.put(QUESTION_COL, question);
        values.put(ANSWER_COL, answer);

        //pass the new content values to the table
        db.insert(TABLE_QUESTION_NAME, null, values);

        db.close();
    }

    //function that returns a Cursor that has all the quizzes
    public Cursor getAllQuizzes(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor allQuizzes = db.query(TABLE_QUIZ_NAME, new String[]
                {ID_COL, QUIZ_NAME_COL, QUIZ_SECONDS_PER_QUESTION_COL},
                null, null, null, null, QUIZ_NAME_COL);
        db.close();
        return allQuizzes;
    }

    //function that returns a cursor with a specific quiz based on the quizId
    public Cursor getSingleQuiz(long quizId) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor quiz = db.query(true, TABLE_QUIZ_NAME, new String[]
                {QUIZ_NAME_COL, QUIZ_SECONDS_PER_QUESTION_COL}, ID_COL + "=" + quizId,
                null, null, null, null, null);
        if(quiz != null){
            quiz.moveToFirst();
        }
        db.close();
        return quiz;
    }

    //function that deletes a quiz and all its associated questions
    public boolean deleteQuiz(long quizId) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean successQuiz;
        boolean successQuestions = db.delete(TABLE_QUESTION_NAME,
                QUIZ_ID_COL + "=" + quizId, null) >0;

        if(successQuestions){
            successQuiz = db.delete(TABLE_QUIZ_NAME,
                    ID_COL + "=" + quizId, null) >0;
            db.close();
        } else {
            db.close();
            return false;
        }

        return successQuiz;
    }

    public boolean updateQuiz(long quizId, String quizName, String seconds){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cval = new ContentValues();
        cval.put(QUIZ_NAME_COL, quizName);
        cval.put(QUIZ_SECONDS_PER_QUESTION_COL, seconds);
        boolean result = db.update(TABLE_QUIZ_NAME, cval, ID_COL + "=" + quizId, null) > 0;
        db.close();
        return result;
    }

    //function that returns a Cursor with all the questions from a specific quiz using the quizId
    public Cursor getAllQuestions(long quizId){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor allQuestions = db.query(TABLE_QUESTION_NAME, new String[]
                        {ID_COL, QUESTION_COL, ANSWER_COL},
                QUIZ_ID_COL, new String[] {String.valueOf(quizId)},
                null, null, null);
        db.close();
        return allQuestions;
    }

    //function that returns a Cursor with a specific question based on the question id
    public Cursor getSingleQuestion(long questionId) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor question = db.query(true, TABLE_QUIZ_NAME, new String[]
                        {ID_COL, QUESTION_COL, ANSWER_COL}, ID_COL + "=" + questionId,
                null, null, null, null, null);
        if(question != null){
            question.moveToFirst();
        }
        db.close();
        return question;
    }

    //function to delete a specific question
    public boolean deleteQuestion(long questionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        boolean success = db.delete(TABLE_QUESTION_NAME,
                ID_COL + "=" + questionId, null) >0;
        db.close();

        return success;
    }

    public boolean updateQuestion(long questionId, String question, String answer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cval = new ContentValues();
        cval.put(QUESTION_COL, question);
        cval.put(ANSWER_COL, answer);
        boolean result = db.update(TABLE_QUESTION_NAME, cval,
                ID_COL + "=" + questionId, null) > 0;
        db.close();
        return result;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_NAME);
        onCreate(db);
    }
}