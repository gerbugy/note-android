package com.gerbugy.note.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gerbugy.note.R;
import com.gerbugy.note.databinding.MainBinding;
import com.gerbugy.note.util.AppUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private MainBinding mBinding;
    private MainPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.main);
        mBinding.drawerLayout.addDrawerListener(new MainDrawerListener(this));
        mBinding.navigationView.setNavigationItemSelectedListener(new MainNavigationItemSelectedListener(this));
        mBinding.navigationView.getMenu().findItem(R.id.habit).setVisible(AppUtils.isPackageInstalled(getPackageManager(), getString(R.string.habit_package_name)));
        ((TextView) mBinding.navigationView.getHeaderView(0).findViewById(R.id.version_name)).setText(AppUtils.getPackageInfo(this).versionName);
        mBinding.contentLayout.viewPager.setAdapter(mPagerAdapter = new MainPagerAdapter(this));
        mBinding.contentLayout.viewPager.addOnPageChangeListener(new MainPageChangeListener(this));
        mBinding.contentLayout.tabLayout.setupWithViewPager(mBinding.contentLayout.viewPager);
        mBinding.contentLayout.tabLayout.setVisibility(View.GONE);
        mBinding.contentLayout.floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button:
                mPagerAdapter.getItem(mBinding.contentLayout.viewPager.getCurrentItem()).onFloatingActionButtonClick(v);
                break;
        }
    }

    MainPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    MainBinding getBinding() {
        return mBinding;
    }
}