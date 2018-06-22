package com.gerbugy.note.view;

import android.view.View;

import com.gerbugy.note.view.BaseFragment;

public abstract class MainFragment extends BaseFragment {

    private boolean mHasFloatingActionButton;

    public void onFloatingActionButtonClick(View v) {

    }

    protected void setHasFloatingActionButton(boolean hasFloatingActionButton) {
        mHasFloatingActionButton = hasFloatingActionButton;
    }

    public boolean hasFloatingActionButton() {
        return mHasFloatingActionButton;
    }
}
