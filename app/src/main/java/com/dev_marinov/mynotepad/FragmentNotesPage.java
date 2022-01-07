package com.dev_marinov.mynotepad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev_marinov.mynotepad.databinding.ClassFragmentNotesPageDataBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentNotesPage extends Fragment {
// фрагмент для отображения списка заметок notes

    public ClassFragmentNotesPageDataBinding classFragmentNotesPageDataBinding;
    AdapterNotesPage adapterNotesPage;
    HashMap<Integer, ClassListNotesPageObject> hashMap = new HashMap<>(); // массив для хранения списка notes
    int count = -1; // счетчик для записи в массив со списком notes
    int countnew = -1; // счетчик для записи в массив со списком notes

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        classFragmentNotesPageDataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_notes_page, container, false);
        View view = classFragmentNotesPageDataBinding.getRoot();

        //here data must be an instance of the class MarsDataProvider
        classFragmentNotesPageDataBinding.setNotesPageDatabinding(new FragmentNotesPageBindingView("ALL NOTES"));

        adapterNotesPage = new AdapterNotesPage(getContext(), hashMap);
        classFragmentNotesPageDataBinding.rvListNotesPage.setLayoutManager(new LinearLayoutManager(
                view.getContext(), LinearLayoutManager.VERTICAL, false));
        classFragmentNotesPageDataBinding.rvListNotesPage.setAdapter(adapterNotesPage);

        // разлогинивание на кнопку выхода
        classFragmentNotesPageDataBinding.imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("333FRAG_NOTES", " -imgLogout запустился-");
                myAlertDialogLogOut(); // метод реализации диалога с пользователем разлогиниться или нет
            }
        });

        classFragmentNotesPageDataBinding.imgCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentOneNote fragmentOneNote = new FragmentOneNote();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // передача числа во FragmentOneNote 0 - ключ id_row, который в дб не найдется,
                // а значит заметка создаться в любом случае и google_id
                fragmentOneNote.createNote("0", ((MainActivity)getActivity()).google_id);

                fragmentTransaction.replace(R.id.ll_frag_one_note, fragmentOneNote,"frag_one_note");
                fragmentTransaction.addToBackStack("frag_one_note");
                fragmentTransaction.commit();
            }
        });

        retrofitRequest(); // метод для запроса из бд списка заметок notes
        swipeDeleteNote(); // метод удаления заметки note посредством смахивания
        return view;
    }

    // ResponseBody применили чтобы получить данные в сыром виде
    // https://sprosi.pro/questions/924739/poluchit-syiroy-http-otvet-s-retrofit
    // метод для запроса из бд списка заметок notes
    public void retrofitRequest() {
        Log.e("333FRAG_NOTES", " -retrofitRequest запустился-");

        ApiInterfaceNotesPage apiInterfaceNotesPage = ApiClient.getClient().create(ApiInterfaceNotesPage.class);
        Call sosSensCall = apiInterfaceNotesPage.myParamsApiInterfaceNotesPage(((MainActivity)getActivity()).google_id);
        Log.e("333FRAG_NOTES", "-google_id-" + ((MainActivity)getActivity()).google_id);
        sosSensCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hashMap.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    Iterator<String> k = jsonObject.keys();
                    while (k.hasNext()) {
                        count++;
                        String id_row = k.next();
                        String subject = jsonObject.getJSONObject(id_row).getString("subject");
                        String note = jsonObject.getJSONObject(id_row).getString("note");
                        String date = jsonObject.getJSONObject(id_row).getString("date");
//                        Log.e("333FRAG_NOTES", "-id_row!- " + id_row);
//                        Log.e("333FRAG_NOTES", "-subject!- " + subject);
//                        Log.e("333FRAG_NOTES", "-note!- " + note);
//                        Log.e("333FRAG_NOTES", "-date!- " + date);
//                        Log.e("333FRAG_NOTES", "-count!- " + count);
                        hashMap.put(count, new ClassListNotesPageObject(id_row, subject, note, date));
                    }
                    // работа с view
                        getActivity().runOnUiThread(new Runnable() {
                        @Override
                         public void run() {
                            adapterNotesPage.notifyDataSetChanged();
                            Log.e("333FRAG_NOTES", "-notifyDataSetChanged hashmap.size!- " + hashMap.size());
                            count = -1; // после запроса снова устанавливаем счетчик в первоначальное положение

                        }
                    });

                } catch (Exception e) {
                    Log.e("333FRAG_NOTES", "-try catch 1!- " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("333FRAG_NOTES", "-onFailure t=!- " + t.getMessage());
                Toast.makeText(getContext(), "TRY AGAIN (error_server_onFailure_notes) -> " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void swipeDeleteNote()  // метод удаления заметки note посредством смахивания
    {
        ////////////////////////////////////
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int itemPosition = viewHolder.getAdapterPosition();
                new AlertDialog.Builder(getContext())
                        .setMessage("хочешь удалить " + adapterNotesPage.getItemId(itemPosition))
                        ///////////////
                        .setMessage("do you want to delete : \"" + hashMap.get(itemPosition).subject + "\"?")
                        .setPositiveButton("DELETE",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        adapterNotesPage.notifyItemRemoved(itemPosition);
                                        remove_message(hashMap.get(itemPosition).id_row);
                                        hashMap.remove(itemPosition);
                                    }
                                })
                        //.setMessage("Do you want to delete: \"" + mRecyclerViewAdapter.getItemAtPosition(itemPosition).getName() + "\"?")
                        // .setPositiveButton("Delete", (dialog, which) -> mYourActivityViewModel.removeItem(itemPosition))
                        .setNegativeButton("CANCEL", (dialog, which) -> adapterNotesPage.notifyItemChanged(itemPosition))
                        .setOnCancelListener(dialogInterface -> adapterNotesPage.notifyItemChanged(itemPosition))
                        .create().show();
                ////////////////////////
                adapterNotesPage.notifyDataSetChanged();
            }
        }).attachToRecyclerView(classFragmentNotesPageDataBinding.rvListNotesPage);
    }


    public void remove_message(String id_row)
    {
        ApiInterfaceDeleteNote apiInterfaceDeleteNote = ApiClient.getClient().create(ApiInterfaceDeleteNote.class);
        Call sosSensCall = apiInterfaceDeleteNote.myParamsApiInterfaceDeleteNote(id_row, ((MainActivity)getActivity()).google_id);
        Log.e("333FRAG_NOTES", "RM-id_row-" + id_row + " google_id =" + ((MainActivity)getActivity()).google_id);
        sosSensCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hashMap.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.e("333FRAG_NOTES", "RM-jsonObject!- " + jsonObject);
                    Iterator<String> k = jsonObject.keys();
                    while (k.hasNext()) {
                        countnew++;
                        String id_row = k.next();
                        String subject = jsonObject.getJSONObject(id_row).getString("subject");
                        String note = jsonObject.getJSONObject(id_row).getString("note");
                        String date = jsonObject.getJSONObject(id_row).getString("date");
                        Log.e("333FRAG_NOTES", "RM-id_row!- " + id_row);
                        Log.e("333FRAG_NOTES", "RM-subject!- " + subject);
                        Log.e("333FRAG_NOTES", "RM-note!- " + note);
                        Log.e("333FRAG_NOTES", "RM-date!- " + date);
                        Log.e("333FRAG_NOTES", "RM-count!- " + count);
                        hashMap.put(countnew, new ClassListNotesPageObject(id_row, subject, note, date));
                    }
                    // работа с view
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterNotesPage.notifyDataSetChanged();
                            Log.e("333FRAG_NOTES", "RM-notifyDataSetChanged hashmap.size!- " + hashMap.size());
                            countnew = -1; // после запроса снова устанавливаем счетчик в первоначальное положение

                        }
                    });

                } catch (Exception e) {
                    Log.e("333FRAG_NOTES", "RM-try catch 2!- " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("333FRAG_NOTES", "RM-onFailure t=!- " + t);
            }
        });
    }

    // метод реализации диалога с пользователем разлогиниться или нет
    public void myAlertDialogLogOut()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Do you wish to logout ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // finish used for destroyed activity

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                            ((MainActivity)getActivity()).mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FragmentAuthGoogle fragmentAuthGoogle = new FragmentAuthGoogle();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.ll_frag_auth_google, fragmentAuthGoogle);
                                    ((MainActivity)getActivity()).ll_frag_notes_page.removeAllViews();
                                    fragmentTransaction.commit();
                                }
                            });
                    }
                });
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Nothing will be happened when clicked on no button
                // of Dialog
            }
        });
        alertDialog.show();
    }
}