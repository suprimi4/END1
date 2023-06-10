package com.example.kur;

import java.io.File;

public class Task {
    private int id;
    private String name;
    private String comment;
    private File file;
    private String status;
    private String textAnswer;
    private File answerFile;
    private int studentAnswerId;

    public Task(int id, String name, String comment, File file, String status) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.file = file;
        this.status = status;
    }

    public Task(int id, String name, String comment, File file, String status, String textAnswer, File answerFile) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.file = file;
        this.status = status;
        this.textAnswer = textAnswer;
        this.answerFile = answerFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getStudentAnswerId() {
        return studentAnswerId;
    }

    public void setStudentAnswerId(int studentAnswerId) {
        this.studentAnswerId = studentAnswerId;
    }
}
