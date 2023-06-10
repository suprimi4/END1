package com.example.kur;

import java.io.File;

public class StudentTask extends Task {
    private String textAnswer;
    private File answerFile;

    public StudentTask(int id, String name, String comment, File file, String status, String textAnswer, File answerFile) {
        super(id, name, comment, file, status);
        this.textAnswer = textAnswer;
        this.answerFile = answerFile;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public File getAnswerFile() {
        return answerFile;
    }

    public void setAnswerFile(File answerFile) {
        this.answerFile = answerFile;
    }
}
