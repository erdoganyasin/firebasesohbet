package com.saginsoft.firebasesohbet;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class Helper {

    public static boolean isOnline() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void imageLoad(Context context, String url, ImageView imagheView) {
        Glide.with(context).load(url).into(imagheView);
    }

}
