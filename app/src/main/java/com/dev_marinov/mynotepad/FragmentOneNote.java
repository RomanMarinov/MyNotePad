package com.dev_marinov.mynotepad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dev_marinov.mynotepad.databinding.ClassFragmentOneNoteDataBinding;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentOneNote extends Fragment {
// фрагмент для создания или редактирования заметки note

    public ClassFragmentOneNoteDataBinding classFragmentOneNoteDataBinding;
    String subject, note; // для записи данных о заметке (вспомогательные)
    String edit_id_row = "", edit_subject = "", edit_note = ""; // данные заметки на которые был клик в адаптере
    String id_row = ""; // переменная для записи ноль всегда
    String myGoogleId = ""; // gooogle_id 104400340901858671115
    int flag = 0; // если 1 то это от процесса создания и сохранения заметки и переход в статус просто сохранения (неходя из frag_one_note)
    String getLastId_row = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        classFragmentOneNoteDataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_one_note, container, false);
        View view = classFragmentOneNoteDataBinding.getRoot();

        //here data must be an instance of the class MarsDataProvider
        classFragmentOneNoteDataBinding.setOnenotedatabinding(new FragmentOneNoteDataBindingView("create and save note"));
        classFragmentOneNoteDataBinding.edtSubject.setHint("subject");
        classFragmentOneNoteDataBinding.edtSubject.getBackground().clearColorFilter();
        classFragmentOneNoteDataBinding.edtNote.setSelection(0);
        classFragmentOneNoteDataBinding.edtNote.getBackground().clearColorFilter();
        classFragmentOneNoteDataBinding.edtNote.addTextChangedListener(textWatcher);
        classFragmentOneNoteDataBinding.btSave.setEnabled(false);


        // условие только для редактирования заметки note
        if(!edit_id_row.equals("") && !edit_subject.equals("") && !edit_note.equals(""))
        {
        Log.e("333FRAG_ONE","В NOTE СЕЙЧАС ОТОБРАЗИЛОСЬ -edit_id_row-" + edit_id_row + " -edit_subject-" + edit_subject + " -edit_note-" + edit_note);
        classFragmentOneNoteDataBinding.setOnenotedatabinding(new FragmentOneNoteDataBindingView("save changes"));
        classFragmentOneNoteDataBinding.edtSubject.setText(edit_subject);
        classFragmentOneNoteDataBinding.edtNote.setText(edit_note);
        }

        classFragmentOneNoteDataBinding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // если edtSubject и edtNote не пустые
                if(!classFragmentOneNoteDataBinding.edtSubject.getText().toString().equals("")
                        && !classFragmentOneNoteDataBinding.edtNote.getText().toString().equals(""))
                {
                    subject = classFragmentOneNoteDataBinding.edtSubject.getText().toString();
                    note = classFragmentOneNoteDataBinding.edtNote.getText().toString();
                    myRetrofit(); // метод для создания или редактирования заметки note
                }

                // если edtSubject, а edtNote не пустой и тогда я беру первое слово из строки note
                // и копирую их как тему заметки note (реализовано как на iphone)
                if(classFragmentOneNoteDataBinding.edtSubject.getText().toString().equals("")
                && !classFragmentOneNoteDataBinding.edtNote.getText().toString().equals(""))
                {
                    // считаю сколько пробелов в заметке Note
                    int spaces = classFragmentOneNoteDataBinding.edtNote.getText().toString().length()
                            - classFragmentOneNoteDataBinding.edtNote.getText().toString().replace(" ", "").length();
                    String firstWord = "", secondWord = "";

                    String textNoteTemp = classFragmentOneNoteDataBinding.edtNote.getText().toString();
                    String arTextNoteTemp[] = textNoteTemp.split(" ",0);
                    firstWord = arTextNoteTemp[0];
                    if(spaces >= 2)
                    {
                        secondWord = arTextNoteTemp[1];
                    Log.e("проверочка","firstWord---" + firstWord + "   secondWord----" + secondWord);
                    classFragmentOneNoteDataBinding.edtSubject.setText(firstWord + " " + secondWord);
                    }
                    else
                    {
                        classFragmentOneNoteDataBinding.edtSubject.setText(firstWord);
                    }

                    subject = classFragmentOneNoteDataBinding.edtSubject.getText().toString();
                    note = classFragmentOneNoteDataBinding.edtNote.getText().toString();
                    myRetrofit(); // метод для создания или редактирования заметки note
                }

            }
        });

        return view;
    }

    // метод для создания или редактирования заметки note
    public void myRetrofit()
    {
        Log.e("333FRAG_ONE"," -ЗАПУСИЛСЯ myRetrofit- и id_row = " + id_row);

        if(flag == 1) // от создания и сохранения в просто сохранение (неходя из frag_one_note)
        {
            id_row = getLastId_row;
        }
        if(flag == 0) // сохранение через выбор заметки в адаптере
        {
            // если не равен ноль (а id_row равен ноль всегда только для создания заметки note)
            if(!id_row.equals("0"))
            {
                Log.e("333FRAG_ONE"," -id_row НЕ РАВЕН НОЛЬ-");
                id_row = ((MainActivity)getActivity()).edit_id_row; // id_row который всегда есть в MainActivity
            }
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call sosSensCall = apiService.myParams(id_row, subject, note, ((MainActivity)getActivity()).google_id);
        Log.e("333FRAG_ONE","apiService.myParams -my_id_row-" + id_row + " -subject-" + subject + " -note-" + note  + " -google_id-" + ((MainActivity)getActivity()).google_id);

        sosSensCall.enqueue(new Callback() {
            // enqueue - для асинхронной работы (в фоне), execute - синхронная работа
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call call, Response response) {
                KeysClass example = (KeysClass) response.body();
                Log.e("333FRAG_ONE","rr="+example.result);

                if(example.result.equals("good_create_save")) // получение от сервера ответа если создание и сохранение состоялось
                {
                    try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                         try {
                            Toast.makeText(getContext(), "created and saved\nsuccessful", Toast.LENGTH_SHORT).show();
                            // получаю доступ к fragmentNotesPage
                            FragmentNotesPage fragmentNotesPage = (FragmentNotesPage) getActivity().getSupportFragmentManager().findFragmentById(R.id.ll_frag_notes_page);
                            if(fragmentNotesPage != null)
                            {
                            // запрос на сервер и дб получить обновленный список заметок note
                            fragmentNotesPage.retrofitRequest();
                            // метод получения id_row последней созданной и сохраненной заметки note
                            // и послед. использ. для того чтобы нажимая на кноку сохранить эта же заметка не дублировалась
                            getId_rowFromNewNote();
                            classFragmentOneNoteDataBinding.btSave.setText("save changes");
                            }

                         }catch (Exception e)
                         {
                             Log.e("333FRAG_ONE","try catch 1="+e);
                         }

                        }
                    });

                    }catch (Exception e)
                    {
                        Log.e("333FRAG_ONE","try catch 2="+e);
                    }
                }
                if(example.result.equals("good_save")) // получение от сервера ответа если сохранение состоялось
                {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Toast.makeText(getContext(), "saved successful", Toast.LENGTH_SHORT).show();
                                    // получаю доступ к fragmentNotesPage
                                    FragmentNotesPage fragmentNotesPage = (FragmentNotesPage) getActivity().getSupportFragmentManager().findFragmentById(R.id.ll_frag_notes_page);
                                    if(fragmentNotesPage != null)
                                    {
                                        // запрос на сервер и дб получить обновленный список заметок note
                                        fragmentNotesPage.retrofitRequest();

                                    }

                                }catch (Exception e)
                                {
                                    Log.e("333FRAG_ONE","try catch 1="+e);
                                }

                            }
                        });

                    }catch (Exception e)
                    {
                        Log.e("333FRAG_ONE","try catch 2="+e);
                    }
                }

            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("333FRAG_ONE", "Failed! Error = " + t.getMessage());
                Toast.makeText(getContext(), "TRY AGAIN (error_server_onFailure_one_note) -> " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // метод для создания заметки note id_row всегда ноль и текущий google_d
    public void createNote(String my_id_row, String google_id)
    {
        id_row = my_id_row; // здесь всегда только ноль
        myGoogleId = google_id; // здесь всегда текущий google_id
    }

    // получение от адаптера 3х переменных для отображения текущих значений заметки note
    public void editNote(String id_row, String subject, String note)
    {
        edit_id_row = id_row;
        edit_subject = subject;
        edit_note = note;
        Log.e("333FRAG_ONE", "СОСТОЯЛАСЬ ЗАПИСЬ В editNote! edit_id_row ="
                + edit_id_row + "edit_subject =" + edit_subject + "edit_note =" + edit_note);
    }

    public void getId_rowFromNewNote()
    {
        try {

            FragmentNotesPage fragmentNotesPage = (FragmentNotesPage) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.ll_frag_notes_page);
            if(fragmentNotesPage != null)
            {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                        Log.e("333FRAG_NOTES", "retrofitGetId_rowFromNewNote-hashMap!- " + fragmentNotesPage.hashMap.size());

                        // получить данные последней заметки чтобы передать во frag_one_note
                        //и реализовать сохрание-перезапись текущей открытой заметки, а не создание новой с такими же данными
                        // Гланвное отпределить ее по id_row
                        for (int i = 0; i < fragmentNotesPage.hashMap.size(); i++) {

                            getLastId_row = fragmentNotesPage.hashMap.get(0).id_row; // get(0) это последняя запись
                            Log.e("333FRAG_NOTES", "-getLastId_row!- " + getLastId_row);
                        }
                            flag = 1;
                    }
                },1000);


            }

        }
        catch (Exception e)
        {
            Log.e("333FRAG_NOTES", "-try catch!- " + e);
        }

    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if(classFragmentOneNoteDataBinding.edtNote.getText().toString().length() >=1)
            {
                classFragmentOneNoteDataBinding.btSave.setEnabled(true);
            }
            else
            {
                classFragmentOneNoteDataBinding.btSave.setEnabled(false);
            }
        }
    };


}