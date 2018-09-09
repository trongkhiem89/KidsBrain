package com.kid.brain.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kid.brain.R;
import com.kid.brain.managers.help.KidPreference;


/**
 * Created by khiemnt on 10/13/16.
 */
public class IntroduceHomeDialog extends Dialog implements View.OnClickListener {

    private RelativeLayout relTutorialOne;
    private RelativeLayout relTutorialTwo;
    private Button btnGotIt;
    private Button btnStart;

    public IntroduceHomeDialog(Context context) {
        super(context, android.R.style.Theme_Light);
        initial();
    }

    public IntroduceHomeDialog(Context context, int themeResId) {
        super(context, android.R.style.Theme_Light);
        initial();
    }

    public void setGotItDescription(String description) {
        btnGotIt.setText(description);
//        tvGotIt.setText(Html.fromHtml(getContext().getString(R.string.intro_text_got_it, description)));
    }

    private void initial() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        getWindow().setBackgroundDrawable(d);

//        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_tutorial_one);
        relTutorialOne = findViewById(R.id.relTutorialOne);
        relTutorialTwo = findViewById(R.id.relTutorialTwo);
        btnGotIt = findViewById(R.id.btnGotIt);
        btnStart = findViewById(R.id.btnStart);
        btnGotIt.setOnClickListener(this);
        btnStart.setOnClickListener(this);

        KidPreference.saveValue(KidPreference.KEY_SHOW_TUTORIAL, true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        WalletPreferenceUtil.setFirstTimeShowedIntroduceWalletDialog(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        dismiss();
        return super.onTouchEvent(event);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v == btnGotIt) {
            relTutorialOne.setVisibility(View.GONE);
            relTutorialTwo.setVisibility(View.VISIBLE);
        } else if (v == btnStart) {
            dismiss();
        }
    }
}
