package com.kid.brain.activies.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.kid.brain.R;
import com.kid.brain.activies.profile.ProfileFragment_;
import com.kid.brain.activies.profile.kid.KidsActivity_;
import com.kid.brain.activies.tutorial.SearchActivity_;
import com.kid.brain.activies.tutorial.TutorialActivity_;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.enums.EItem;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Item;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.view.adapters.ItemAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_items)
public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getName();
    private RecyclerViewSkeletonScreen skeletonScreen;

    @ViewById
    TextView tvWelcome;

    @ViewById
    RecyclerView rvItems;

    private List<Item> mItems;
    private ItemAdapter adapter;

    public HomeFragment() {
    }

    public static HomeFragment_ newInstance() {
        HomeFragment_ fragment = new HomeFragment_();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        try {
            if (getActivity() != null)
                getActivity().setTitle(getString(R.string.app_name));

            initialView();
            tvWelcome.setText(getString(R.string.str_welcome, KidPreference.getStringValue(KidPreference.KEY_FULL_NAME)));
            mItems = KidRepository.getInstance(getActivity()).getItems();
            displayItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialView() {
        adapter = new ItemAdapter(getActivity());
        adapter.setOnItemClickListener(onItemClickListener);

        rvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvItems.setAdapter(adapter);

        skeletonScreen = Skeleton.bind(rvItems)
                .adapter(adapter)
                .load(R.layout.item_skeleton_person)
                .show();
    }

    private void hideSkeletonScreenLoading() {
        skeletonScreen.hide();
    }

    private void displayItems() {
        hideSkeletonScreenLoading();
        if (mItems != null && mItems.size() > 0) {
            adapter.setData(mItems);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                rvItems.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            else
                rvItems.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
    }

    private IOnItemClickListener onItemClickListener = new IOnItemClickListener() {
        @Override
        public <T> void onItemClickListener(T object) {
            Item mItem = (Item) object;
            if (mItem.getId().equalsIgnoreCase(EItem.TUTORIAL.getItemId())) {
                startActivity(new Intent(getActivity(), TutorialActivity_.class));
            } else if (mItem.getId().equalsIgnoreCase(EItem.SEARCH.getItemId())) {
                startActivity(new Intent(getActivity(), SearchActivity_.class));
            } else if (mItem.getId().equalsIgnoreCase(EItem.PROFILE.getItemId())) {
                onTransactionFragment((AppCompatActivity) getActivity(), R.id.contentView, new ProfileFragment_());
            } else if (mItem.getId().equalsIgnoreCase(EItem.KID.getItemId())) {
                startActivity(new Intent(getActivity(), KidsActivity_.class));
            }
        }

        @Override
        public <T> void onItemLongClickListener(T object) {

        }
    };


}
