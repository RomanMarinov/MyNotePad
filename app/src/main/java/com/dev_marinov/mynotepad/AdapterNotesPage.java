package com.dev_marinov.mynotepad;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.dev_marinov.mynotepad.databinding.RowNoteBinding;

import java.util.HashMap;


// ИСТОЧНИК https://c1ctech.com/android-data-binding-in-recyclerview/

class AdapterNotesPage extends RecyclerView.Adapter<AdapterNotesPage.HolderListNotesPage> {

    Context context;
    HashMap<Integer, ClassListNotesPageObject> hashMap; // массив для хранения списка заметок note

    public AdapterNotesPage(Context context, HashMap<Integer, ClassListNotesPageObject> hashMap) {
        this.context = context;
        this.hashMap = hashMap;
        Log.e("Adapter_list-->","list.size() === "+hashMap.size());
    }

    @NonNull
    @Override
    public HolderListNotesPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowNoteBinding rowNoteBinding = DataBindingUtil.inflate(LayoutInflater
                .from(parent.getContext()), R.layout.row_note, parent, false);
        return new HolderListNotesPage(rowNoteBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderListNotesPage holderListNotesPage, int position) {

        final int myPosition = position;

        ClassListNotesPageObject classListNotesPageObject = hashMap.get(position);
        holderListNotesPage.rowNoteBinding.setRowNoteDataBindView(classListNotesPageObject);

        holderListNotesPage.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id_rowAdapterClick = hashMap.get(myPosition).id_row;
                String subjectAdapterClick = hashMap.get(myPosition).subject;
                String noteAdapterClick = hashMap.get(myPosition).note;
                Log.e("333ADAPTER", "-my_id_row--click id_rowAdapterClick-" + id_rowAdapterClick);

                // открытие fragmentOneNote для редактирования заметки note
                FragmentOneNote fragmentOneNote = new FragmentOneNote();
                FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // передача 3х переменных во FragmentOneNote для отображения текущих значений
                fragmentOneNote.editNote(id_rowAdapterClick,subjectAdapterClick,noteAdapterClick);
                // передача 3х переменных во MainActivity для хранения на будущее
                ((MainActivity)context).editNoteMainAct(id_rowAdapterClick,subjectAdapterClick,noteAdapterClick);

                fragmentTransaction.replace(R.id.ll_frag_one_note, fragmentOneNote,"frag_one_note");
                fragmentTransaction.addToBackStack("frag_one_note");
                fragmentTransaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        //Log.e("333ADAPTER", "-hashMap.size-" + hashMap.size());
        return hashMap.size();
    }

    public class HolderListNotesPage extends RecyclerView.ViewHolder {
        private RowNoteBinding rowNoteBinding;

        public HolderListNotesPage(RowNoteBinding rowNoteBinding) {
            super(rowNoteBinding.getRoot());
            this.rowNoteBinding = rowNoteBinding;
        }
    }

}


