package com.gerbugy.note.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gerbugy.note.Constants;
import com.gerbugy.note.R;
import com.gerbugy.note.databinding.MemoEditBinding;
import com.gerbugy.note.db.MemoDao;
import com.gerbugy.note.db.SQLiteItem;

public class MemoEditActivity extends BaseActivity implements View.OnClickListener {

    private SQLiteItem mItem;
    private MemoEditBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.memo_edit);
        mBinding.okButton.setOnClickListener(this);
        long _id = getIntent().getLongExtra(MemoDao.Columns._ID, Constants.NO_ID);
        if (_id > Constants.NO_ID) {
            select(_id);
            setTitle(R.string.memo_edit);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                if (mItem == null) {
                    insert();
                } else {
                    update();
                }
                break;
        }
    }

    private void select(long _id) {
        mItem = MemoDao.getInstance().select(_id);
        mBinding.content.setText(mItem.getString(MemoDao.Columns.CONTENT));
    }

    private void insert() {
        mItem = new SQLiteItem();
        mItem.put(MemoDao.Columns.CONTENT, mBinding.content.getText().toString().trim());
        setResult(Constants.RESULT_INSERTED, new Intent().putExtra(MemoDao.Columns._ID, MemoDao.getInstance().insert(mItem)));
        finish();
    }

    private void update() {
        mItem.put(MemoDao.Columns.CONTENT, mBinding.content.getText().toString().trim());
        MemoDao.getInstance().update(mItem);
        setResult(Constants.RESULT_CHANGED, new Intent().putExtra(MemoDao.Columns._ID, mItem.getLong(MemoDao.Columns._ID)));
        finish();
    }
}
