package com.kid.brain.activies.profile;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.kid.brain.R;
import com.kid.brain.activies.profile.kid.KidsActivity_;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidBusiness;
import com.kid.brain.models.Account;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.util.Constants;
import com.kid.brain.view.dialog.DateTimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends BaseFragment {

    private Account mAccount;

    @ViewById
    ImageView imgUserAvatar;

    @ViewById
    TextView tvUserName;

    @ViewById
    TextView tvPhoneNumber;

    @ViewById
    TextView tvEmail;

    @ViewById
    TextView tvDateOfBirth;

    @ViewById
    TextView tvGender;

    @ViewById
    TextView tvAddress;

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        doFetchAccount();
    }

    @Background
    void doFetchAccount() {
        try {
            mAccount = KidRepository.getInstance(getActivity()).getAccount();
            displayAccountInfo();
        } catch (Exception e){
            dismissProgressBar();
            e.printStackTrace();
        }
    }

    @UiThread
    void displayAccountInfo() {
        if (mAccount != null) {
            tvUserName.setText(mAccount.getFullNameOrEmail());
            tvEmail.setText(mAccount.getEmail());
            tvPhoneNumber.setText(mAccount.getMobile());
            tvAddress.setText(mAccount.getAddress());
            tvDateOfBirth.setText(DateTimeUtils.convertUTCToLocal(mAccount.getBirthDay()));
            tvGender.setText(KidBusiness.getInstance().getGender(mAccount.getGender()));
            ImageLoader.getInstance().displayImage(mAccount.getPhoto(), imgUserAvatar, KidApplication.getInstance().getOptions());
        }
    }

    @Click (R.id.relAddChildren)
    void onChildrenView(){
        startActivity(new Intent(getActivity(), KidsActivity_.class));
    }

    @Click(R.id.imbEditProfile)
    void onEditProfile(){
        if (mAccount != null) {
            Intent intentEditProfile = new Intent(getActivity(), EditProfileActivity_.class);
            intentEditProfile.putExtra(Constants.KEY_ACCOUNT, mAccount);
            startActivity(intentEditProfile);
        } else {

        }
    }
}
