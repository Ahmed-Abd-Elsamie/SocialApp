package com.example.root.socialapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.socialapp.R;
import com.example.root.socialapp.ui.Chat;

public class MessageBox {

    public static void addNewMessage(Activity context, String message, int type, LinearLayout layout) {
        TextView textView = new TextView(context);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setTextSize(18);
        textView.setLayoutParams(lp);
        textView.setPadding(8, 8, 8, 8);

        if (type == 1) {
            textView.setBackgroundResource(R.drawable.message_reciever);
            lp.gravity = Gravity.RIGHT;
            textView.setTextColor(Color.parseColor("#000000"));
        } else {
            textView.setBackgroundResource(R.drawable.message_sender);
            lp.gravity = Gravity.LEFT;
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }

        layout.addView(textView);

    }
}
