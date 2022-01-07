package com.dev_marinov.mynotepad;

import android.content.Intent;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.dev_marinov.mynotepad.databinding.ClassFragmentAuthGoogleDataBinding;


public class FragmentAuthGoogle extends Fragment{
// фрагмент для авторизации на сайте google

    public ClassFragmentAuthGoogleDataBinding classFragmentAuthGoogleDataBinding;
    ImageView img_google_logo;
    Animation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    classFragmentAuthGoogleDataBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_auth_google, container,false);
    View view = classFragmentAuthGoogleDataBinding.getRoot();
        img_google_logo = view.findViewById(R.id.img_google_logo);

    classFragmentAuthGoogleDataBinding.setAuthGoogleDatabinding(
            new FragmentAuthGoogleDataBindingView("Sign in with Google"));



        classFragmentAuthGoogleDataBinding.googleSingInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = ((MainActivity)getActivity()).mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });
//// не будет диалога авторизации
//// проверяет авторизован я на телефоне или нет, Если да, то в account передается не null
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
//        Log.e("222FRAG_AUTH", "-account-" + account);
//        if (account != null)
//        {
//            ((MainActivity)getActivity()).run_app(account);
//        }
            // анимация
            animation = AnimationUtils.loadAnimation(getContext(),R.anim.bounce_animation);
            img_google_logo.setAnimation(animation);

    return view;
    }
}