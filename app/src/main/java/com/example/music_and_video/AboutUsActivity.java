package com.example.music_and_video;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Element developersElement = new Element();
        developersElement.setTitle("Developers");
        developersElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(AboutUsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_about_us);

                Window window = dialog.getWindow();

                if (window == null) {
                    return;
                }

                window.setLayout(550, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;

                window.setAttributes(windowAttributes);

                TextView headerAB = (TextView)dialog.findViewById(R.id.headerAB);
                headerAB.setText("Developers");

                TextView msgAB = (TextView) dialog.findViewById(R.id.msgAB);
                msgAB.setText("TEAM: NoName " +
                        "\nPhan Nguyễn Anh Vinh" + "\nHồ Viết Bảo Trung"
                        + "\nNguyễn Công Văn" + "\nTrần Ngọc Vỹ");

                dialog.show();
            }
        });

        Element policyElement = new Element();
        policyElement.setTitle("Policy");
        policyElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(AboutUsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_about_us);

                Window window = dialog.getWindow();

                if (window == null) {
                    return;
                }

                window.setLayout(550, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;

                window.setAttributes(windowAttributes);

                TextView headerAB = (TextView)dialog.findViewById(R.id.headerAB);
                headerAB.setText("Policy");

                TextView msgAB = (TextView) dialog.findViewById(R.id.msgAB);
                msgAB.setText("Hiện chưa có quyền gì.");

                dialog.show();
            }
        });


        View aboutPage = new AboutPage(AboutUsActivity.this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription("Just a simple media player app")
                .addItem(new Element("Version " + BuildConfig.VERSION_NAME, 17301569))
                .addGroup("Connect with us!")
                .addEmail("anhvinhphan65@gmail.com", "Contract our leader")
                .addFacebook("vinhphan659","Meet our leader on Facebook")
                .addGitHub("anhvinhphan659/Music_and_Video")
                .addItem(developersElement)
                .addItem(policyElement)
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright by NoName %d", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this,copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}