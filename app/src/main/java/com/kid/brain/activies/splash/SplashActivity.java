package com.kid.brain.activies.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.kid.brain.R;
import com.kid.brain.activies.authorization.LoginActivity_;
import com.kid.brain.activies.home.HomeActivity;
import com.kid.brain.activies.introduce.IntroduceActivity;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.help.KidPreference;

public class SplashActivity extends BaseAppCompatActivity {

    private CountDownTimer countDownTimer;
    private final int TIME_SHOW = 2000;
    private final int TIME_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        goHome();
    }

    private void goHome() {
        countDownTimer = new CountDownTimer(TIME_SHOW, TIME_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                goToNextActivity();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void goToNextActivity() {
        boolean isIntroduced = KidPreference.getBooleanValue(KidPreference.KEY_INTRODUCED);
        boolean isLogged = KidPreference.getBooleanValue(KidPreference.KEY_LOGGED);
        if (isLogged) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else if (isIntroduced) {
            startActivity(new Intent(SplashActivity.this, LoginActivity_.class));
        } else {
            startActivity(new Intent(SplashActivity.this, IntroduceActivity.class));
        }
        finish();
    }
}
