package com.kid.brain.activies.introduce;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kid.brain.R;
import com.kid.brain.activies.authorization.LoginActivity_;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.help.KidPreference;

import java.util.ArrayList;
import java.util.List;


public class IntroduceActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private LinearLayout llIntroRoot;
    private LinearLayout circlesGroup;
    private ViewPager introducePages;
    private Button btnContinue;

    private ImageView[] circles;
    private List<Fragment> fragments;
    private IntroduceAdapter introduceAdapter;
    private IntroducePageChangeListener pageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        initialize();
        callFunction();
    }

    private void initialize() {
        llIntroRoot = (LinearLayout) findViewById(R.id.llIntroRoot);
        circlesGroup = (LinearLayout) findViewById(R.id.introduceCriclesGroup);
        introducePages = (ViewPager) findViewById(R.id.introducePages);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        introduceAdapter = new IntroduceAdapter(getSupportFragmentManager());
        fragments = new ArrayList<>();

    }

    private void callFunction() {
        setRootBackgroundColor(R.color.colorIntroduceOne);
        setIntroducePage();
        setCircles();
        showIntroducePage();
    }

    private void showIntroducePage() {
        introduceAdapter.setIntroduceDataPage(fragments);
        introducePages.setAdapter(introduceAdapter);
        pageChangeListener = new IntroducePageChangeListener(circles);
        pageChangeListener.onEndOfIntroduceListener(endOfIntroduceListener);
        introducePages.setOnPageChangeListener(pageChangeListener);
    }

    private void setIntroducePage() {
        fragments.add(new IntroduceOneFragment());
        fragments.add(new IntroduceTwoFragment());
        fragments.add(new IntroduceThreeFragment());
    }

    private void setCircles() {
        circles = new ImageView[fragments.size()];
        for (int i = 0; i < fragments.size(); i++) {
            circles[i] = createCircle();
            if (i == 0) {
                circles[i].setImageResource(R.drawable.circle_blue);
            } else {
                circles[i].setImageResource(R.drawable.circle_gray);
            }
            circlesGroup.addView(circles[i]);
        }
    }

    private ImageView createCircle() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
        params.setMargins(20, 0, 20, 0);
        ImageView img = new ImageView(getBaseContext());
        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return img;
    }


    private IntroducePageChangeListener.IEndOfIntroduceListener endOfIntroduceListener = new IntroducePageChangeListener.IEndOfIntroduceListener() {

        @Override
        public void introducePage(int position) {
            switch (position) {
                case 0:
                    setRootBackgroundColor(R.color.colorIntroduceOne);
                    break;
                case 1:
                    setRootBackgroundColor(R.color.colorIntroduceTwo);
                    break;
                case 2:
                    setRootBackgroundColor(R.color.colorIntroduceThree);
                    break;
            }
        }

        @Override
        public void endOfIntroduce(boolean isEndOfIntroduce) {
            if (isEndOfIntroduce) {
                onShowButtonContinue(isEndOfIntroduce);
            } else {
                onShowButtonContinue(isEndOfIntroduce);
            }
        }
    };

    private void onShowButtonContinue(boolean isShow) {
        btnContinue.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == btnContinue) {
            KidPreference.saveValue(KidPreference.KEY_INTRODUCED, true);
            Intent intentLogin = new Intent(IntroduceActivity.this, LoginActivity_.class);
            startActivity(intentLogin);
            IntroduceActivity.this.finish();
        }
    }

    public void setRootBackgroundColor(int color) {
        if (llIntroRoot != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                IntroduceActivity.this.getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
            }
            llIntroRoot.setBackgroundColor(ContextCompat.getColor(this, color));
        }
    }
}
