package com.dev_marinov.mynotepad;

public class FragmentNotesPageBindingView{

    String note_str;

    public String getNote_str() {
        return note_str;
    }

    public void setNote_str(String note_str) {
        this.note_str = note_str;
    }

    public FragmentNotesPageBindingView(String note_str) {
        this.note_str = note_str;
    }
}
