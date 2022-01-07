package com.dev_marinov.mynotepad;

public class ClassListNotesPageObject {
// класс для хранения переменных (данных) о заметке


    String id_row, subject, note, date;

    public String getId_row() {
        return id_row;
    }

    public void setId_row(String id_row) {
        this.id_row = id_row;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ClassListNotesPageObject(String id_row, String subject, String note, String date) {
        this.id_row = id_row;
        this.subject = subject;
        this.note = note;
        this.date = date;
    }
}
