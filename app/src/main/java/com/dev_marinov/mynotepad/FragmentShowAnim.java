package com.dev_marinov.mynotepad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dev_marinov.mynotepad.databinding.DataClassFragmentShowAnim;

import java.util.Timer;
import java.util.TimerTask;

public class FragmentShowAnim extends Fragment {

    public DataClassFragmentShowAnim dataClassFragmentShowAnim;
    View frag;
    Animation topAnim, bottomAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // во весь экран
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dataClassFragmentShowAnim = DataClassFragmentShowAnim.inflate(getLayoutInflater());
        frag = dataClassFragmentShowAnim.getRoot();

        // устновка картинки
        dataClassFragmentShowAnim.imgNotepad.setBackground(getResources().getDrawable(R.drawable.notepad));
        // установка названия для textView
        dataClassFragmentShowAnim.setFragShowAnimDataBindView(
                new FragmentShowAnimDataBindingView("my notepad"));

        // анимация
        topAnim = AnimationUtils.loadAnimation(getContext(),R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_animation);
        dataClassFragmentShowAnim.imgNotepad.setAnimation(topAnim);
        dataClassFragmentShowAnim.tvNameStart.setAnimation(bottomAnim);


        startFirstAnimation(); // первая анимация на первом фрагменте FragmentShowAnim


        return frag;
    }

    // первая анимация на первом фрагменте FragmentShowAnim
    public void startFirstAnimation()
    {
        // исполнение в теч 3 сек
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(((MainActivity)getActivity()).status_auth == false)
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentAuthGoogle fragmentAuthGoogle = new FragmentAuthGoogle();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.ll_frag_auth_google, fragmentAuthGoogle);
                    //fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else
                {

                }

            }
        },3000);

        // задержка на 0.5 сек
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataClassFragmentShowAnim.progressBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        },500);
        dataClassFragmentShowAnim.progressBar.setVisibility(View.GONE);
    }

}