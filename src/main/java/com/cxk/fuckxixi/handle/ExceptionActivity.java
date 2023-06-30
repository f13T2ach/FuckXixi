package com.cxk.fuckxixi.handle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

/* compiled from: ExceptionActivity.kt */
public final class ExceptionActivity extends AppCompatActivity {
    @SuppressLint({"ResourceType"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.gravity = 17;
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(layoutParams);
        ScrollView scrollView = new ScrollView(this);
        linearLayout.addView(scrollView);
        AppCompatTextView appCompatTextView = new AppCompatTextView(this, null);
        scrollView.addView(appCompatTextView);
        setContentView(linearLayout);
        appCompatTextView.setText(getIntent().getStringExtra("error"));
    }
}