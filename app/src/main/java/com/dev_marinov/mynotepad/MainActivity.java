package com.dev_marinov.mynotepad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String google_id = "";
    String edit_id_row = "", edit_subject = "", edit_note = "";

    String open_frag_bilo="list";
    String open_frag_active="";

    boolean status_auth = false; // false не авториз, true авториз
    GoogleSignInClient mGoogleSignInClient;
    LinearLayout ll_frag_notes_page;
    GoogleSignInOptions gso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll_frag_notes_page = findViewById(R.id.ll_frag_notes_page);

        // получение gso для Frag_auth именно в main_act чтобы потом при разлогинивании получить
        // доступ к gso из frag_notes
        // Настройте вход, чтобы запросить идентификатор пользователя, адрес электронной почты,
        // а также идентификатор основного профиля, а основной профиль включен в DEFAULT_SIGN_IN.
        // инициализация компоненнта авторизации в гугл
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // https://developers.google.com/identity/sign-in/android/start
        // Создайте GoogleSignInClient с параметрами, указанными в gso.
        // НАПОМИНАНИЕ: если нет файл бат, то нужно его создать пройдя по ссылке выше, вставить 4 параметра
        // в файл бат, создав его в любом месте
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso); // возможно requireContext()

        // установка портретной ориентации
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // во весь экран
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myBackPressed(); // метод для определения какой фрагмент я сейчас открыл и на каком был

        // не будет диалога авторизации
        // проверяет авторизован я на телефоне или нет, Если да, то в account передается не null
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.e("222MAIN_ACT", "-account-" + account);
        if (account != null)
        {
            status_auth = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    run_app(account);
                }
            },3500);
        }
    }

    // получил от адаптера 3 переменные во MainActivity для хранения на будущее
    public void editNoteMainAct(String id_row, String subject, String note)
    {
        edit_id_row = id_row;
        edit_subject = subject;
        edit_note = note;
        Log.e("333MAIN_ACT","СОСТОЯЛАСЬ ЗАПИСЬ В editNote! edit_id_row-" + id_row + " -edit_subject-" + subject + " -edit_note-" + note);
    }

    @Override
    // вызовется тогда, когда активити закроется (закрытие это тогда, когда юзер зайдет по учетке или передумает)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("222main_act", "requestCode=" + requestCode);
        // requestCode - код выбора, data - если не null, значит есть данные, сработало
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                run_app(account);

            } catch (ApiException e) {
                Log.e("222err", "err=" + e);
                Log.e("222account ", "null");
                Toast.makeText(getApplicationContext(), "На устройстве нет приложения Play Market = " + e, Toast.LENGTH_LONG).show();
                run_app(null);
            }
        }
    }
    // метод срабатывает после нажатия кнопки авторизации аккаунта в приложении
    // он не сработает если ты еще не аавторизовался
    public void run_app(GoogleSignInAccount account) {
        Log.e("222MAIN_ACT","-run_app- вып");

        if (account.equals(null))
        {

        }
        else
            {
//            if (account.getPhotoUrl() != null) {
//                google_photo = account.getPhotoUrl().toString();
//            }
            google_id = account.getId();
            //google_name = account.getDisplayName();
            //String google_email = account.getEmail();

            runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    Log.e("222MAIN_ACT","-run_app runOnUiThread вып-");

                    // открытие фрагмента после авторизации
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentNotesPage fragmentNotesPage = new FragmentNotesPage();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.ll_frag_notes_page, fragmentNotesPage);
                    fragmentTransaction.commit();
                }
            });
        }
    }

    // метод для определения какой фрагмент я сейчас открыл и на каком был
    public void myBackPressed()
    {
        // добавляем слушателя в обратном стеке вызова
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    open_frag_active = "";
                    // каждый переход вперед по фрагментам будет + 1
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    {
                        Log.e("check_frag мое 0", "getSupportFragmentManager().getBackStackEntryCount() ="
                                + getSupportFragmentManager().getBackStackEntryCount());

                        open_frag_active = getSupportFragmentManager().getBackStackEntryAt(
                                getSupportFragmentManager().getBackStackEntryCount() - 1).getName().toString();
                        Log.e("check_frag мое 1", "open_frag_active =" + open_frag_active);

                    }
                    else
                    {
                        open_frag_active = "list";
                        Log.e("check_frag мое 2", "open_frag_active =" + open_frag_active);
                        Log.e("check_frag сколько ща2", "getSupportFragmentManager().getBackStackEntryCount() ="
                                + getSupportFragmentManager().getBackStackEntryCount());
                    }
                    Log.e("check_frag сколько ща2", "getSupportFragmentManager().getBackStackEntryCount() ="
                            + getSupportFragmentManager().getBackStackEntryCount());
                    //-----------------------
                    // Открылая фрагмент - frag_one_note при создании новой заметки или редактировании старой
                    if (open_frag_active.equals("frag_one_note") && (open_frag_bilo.equals("list")) )
                    {

                    }
                    //Закрылся фрагмент - frag_one_note при нажатии кнопки назад, чтобы обновить список заметок notes
                    if (open_frag_active.equals("list") && (open_frag_bilo.equals("frag_one_note")))
                    {
                        // получить доступ к fragmentNotesPage в фоновом потоке пользовательского UI
                        // для обновления списка заметок notes, запустив retrofitRequest();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentNotesPage fragmentNotesPage= (FragmentNotesPage) getSupportFragmentManager()
                                        .findFragmentById(R.id.ll_frag_notes_page);
                                if (fragmentNotesPage != null)
                                {
                                    fragmentNotesPage.retrofitRequest();
                                }
                            }
                        });
                    }

                    Log.e("check_frag", "open_frag_bilo=" + open_frag_bilo);
                    Log.e("check_frag", "open_frag_active=" + open_frag_active);
                    open_frag_bilo = open_frag_active;
                    Log.e("check_frag", "-------------------------------------");
                }
                catch (Exception e)
                {
                    Log.e("check_frag", "try catch =" + e);
                }
            }});

    }

    // метод только для myAlertDialog();
    @Override
    public void onBackPressed()
    {
        // как только будет ноль (последний экран) выполниться else
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.e("MAIN_ACT","getFragmentManager().getBackStackEntryCount()== " + getSupportFragmentManager().getBackStackEntryCount() );
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager().popBackStack(); // удаление фрагментов из транзакции
            myAlertDialog(); // метод реализации диалога с пользователем закрыть приложение или нет
        }
    }


    // метод реализации диалога с пользователем закрыть приложение или нет
    public void myAlertDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Do you wish to exit ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // finish used for destroyed activity
                finish();
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