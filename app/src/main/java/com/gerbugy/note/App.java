package com.gerbugy.note;

import android.app.Application;

import com.gerbugy.note.db.SQLiteHelper;
import com.gerbugy.note.util.AppUtils;

public final class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteHelper.initialize(this, Constants.DATABASE_NAME, null, AppUtils.getPackageInfo(this).versionCode);
        SQLiteHelper.getInstance().getReadableDatabase(); // 데이터베이스 생성 또는 업그레이드를 수행합니다.
    }
}