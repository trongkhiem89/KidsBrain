package com.kid.brain.activies.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kid.brain.R;
import com.kid.brain.activies.profile.ProfileFragment_;
import com.kid.brain.activies.profile.kid.KidDetailActivity_;
import com.kid.brain.activies.settings.SettingsFragment_;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.IActivityCommunicatorListener;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.managers.listeners.OnCheckDbListener;
import com.kid.brain.models.Account;
import com.kid.brain.models.Kid;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.util.Constants;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.adapters.SlideMenuAdapter;
import com.kid.brain.view.dialog.DialogUtil;
import com.kid.brain.view.dialog.IntroduceHomeDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class HomeActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, IActivityCommunicatorListener {

    private LinearLayout llProfileRoot;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView tvHome, tvSetting, tvLogout;
    private RelativeLayout relHome;
    private RelativeLayout relSetting;
    private RelativeLayout relLogout;

    private ImageView imageView;
    private TextView tvUserName;

    private RecyclerView rvLevel;
    private SlideMenuAdapter mAdapter;

    private Account mAccount;
    private List<Kid> mKids;
    private Kid mKidSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpToolbar(toolbar, KidPreference.getStringValue(KidPreference.KEY_FULL_NAME));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        llProfileRoot = (LinearLayout) findViewById(R.id.llProfileRoot);
        relHome= (RelativeLayout) findViewById(R.id.relHome);
        relSetting = (RelativeLayout) findViewById(R.id.relSetting);
        relLogout = (RelativeLayout) findViewById(R.id.relLogout);
        tvHome = (TextView) findViewById(R.id.tvHome);
        tvSetting = (TextView) findViewById(R.id.tvSetting);
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        relHome.setOnClickListener(this);
        relSetting.setOnClickListener(this);
        relLogout.setOnClickListener(this);
        llProfileRoot.setOnClickListener(this);

        initialView();
        setHomeFragment();

//        if (!KidPreference.getBooleanValue(KidPreference.KEY_SHOW_TUTORIAL)) {
//            showIntroDialog();
//        }

        if (KidRepository.getInstance(HomeActivity.this).getDb() == null) {
            checkDbOpen();
        } else {
            try {
                mAccount = KidRepository.getInstance(HomeActivity.this).getAccount();
                displayAccount();
                doFetchKids();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDbOpen() {
        try {
            String keyCode = KidPreference.getStringValue(KidPreference.KEY_PIN);
            KidApplication.getInstance().checkDbOpen(keyCode, new OnCheckDbListener() {
                @Override
                public void onOpenDbSuccess() {
                    ALog.e("checkDbOpen:", "Done");
                    try {
                        mAccount = KidRepository.newInstance(HomeActivity.this).getAccount();
                        displayAccount();
                        doFetchKids();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onOpenDbFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.error_create_or_open_db_failed));
                            dismissProgressBar();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            doFetchAccount();
            doFetchKids();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialView() {
        mAdapter = new SlideMenuAdapter(this);
        mAdapter.setOnItemClickListener(onItemClickListener);

        rvLevel = findViewById(R.id.rvLevel);
        rvLevel.setLayoutManager(new LinearLayoutManager(this));
        rvLevel.setAdapter(mAdapter);
    }

    private void showIntroDialog() {
        IntroduceHomeDialog introduceESignDialog = new IntroduceHomeDialog(HomeActivity.this);
        introduceESignDialog.show();
    }

    private void doFetchAccount(){
        try {
            mAccount = KidRepository.getInstance(HomeActivity.this).getAccount();
            displayAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayAccount() {
        if (mAccount != null) {
            tvUserName.setText(mAccount.getFullNameOrEmail());
            ImageLoader.getInstance().displayImage(mAccount.getPhoto(), imageView, KidApplication.getInstance().getOptions());
        }
    }

    private void displayLevels() {
        if (mKids != null && mKids.size() > 0) {
            mAdapter.setData(mKids);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getVisibleFragment();
            if ((fragment != null && fragment instanceof HomeFragment_)) {
                finish();
            } else {
                clearAllFragmentsInBackStack();
                setHomeFragment();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logo) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            setHomeFragment();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == llProfileRoot) {
            drawer.closeDrawer(GravityCompat.START);
            showFragment(new ProfileFragment_());
        } else if (v == relHome) {
            drawer.closeDrawer(GravityCompat.START);
            setHomeFragment();
        } else if (v == relSetting) {
            drawer.closeDrawer(GravityCompat.START);
            showFragment(new SettingsFragment_());
        } else if (v == relLogout) {
            drawer.closeDrawer(GravityCompat.START);
            DialogUtil.createCustomConfirmDialog(HomeActivity.this,
                    getString(R.string.app_name),
                    getString(R.string.str_logout_confirm_message),
                    getString(R.string.dialog_confirm_ok),
                    getString(R.string.dialog_confirm_cancel),
                    new DialogUtil.ConfirmDialogOnClickListener() {
                        @Override
                        public void onOKButtonOnClick() {

                            KidRepository.getInstance(HomeActivity.this).resetDatabase();
                            KidPreference.saveValue(KidPreference.KEY_LOGGED, false);

                            goToLogin(HomeActivity.this);
                        }

                        @Override
                        public void onCancelButtonOnClick() {

                        }
                    }).show();
        }
    }

    @Override
    public <T> void passDataToActivity(T object) {
        // todo: Update UI when change language
        tvHome.setText(getString(R.string.btn_home));
        tvSetting.setText(getString(R.string.btn_Setting));
        tvLogout.setText(getString(R.string.btn_logout));
    }

    private void doFetchKids() throws Exception {
        mKids = KidRepository.getInstance(this).getKids(mAccount.getUserId());
        if (mKids != null && mKids.size() > 0) {
            displayLevels();
        }
    }


    /**
     * Todo: slide menu item selected
     */
    private IOnItemClickListener onItemClickListener = new IOnItemClickListener() {
        @Override
        public <T> void onItemClickListener(T object) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            mKidSelected = (Kid) object;
            if (mKidSelected == null) return;

            Intent intentEditProfile = new Intent(HomeActivity.this, KidDetailActivity_.class);
            intentEditProfile.putExtra(Constants.KEY_KID, mKidSelected);
            startActivity(intentEditProfile);
        }

        @Override
        public <T> void onItemLongClickListener(T object) {

        }
    };

    private void setHomeFragment() {
        showFragment(HomeFragment_.newInstance());
    }

    private void clearAllFragmentsInBackStack() {
        FragmentManager frmManager = getSupportFragmentManager();
        int count = frmManager.getBackStackEntryCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                frmManager.popBackStack();
            }
        }
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null)
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        return null;
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentView, fragment, fragment.getClass().getName());
        transaction.commit();
    }

}
