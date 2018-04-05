package com.test.edv.buseyetraveler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WellcomeActivity extends AppCompatActivity {

    private ImageView appNameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);

        appNameView = (ImageView)findViewById(R.id.appnameView);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.appnameanimation);
        appNameView.setAnimation(animation);

       final SharedPreferences UsersharedPreferences = getSharedPreferences(LoginActivity.UserPREFERENCES, Context.MODE_PRIVATE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try{
                    if(UsersharedPreferences.getString("LoginStatus",null).equals("true"))
                    {

                        Intent intent = new Intent(WellcomeActivity.this,UserMenuActivity.class);
                        startActivity(intent);
                    }
                }
                catch (NullPointerException e)
                {
                    Intent intent = new Intent(WellcomeActivity.this,LoginActivity.class);
                    startActivity(intent);

                }
            }
        },5000);

    }
}
