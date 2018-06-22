package com.gerbugy.note.util;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.gerbugy.note.BuildConfig;
import com.gerbugy.note.Constants;

public final class AdUtils {

    private AdUtils() {

    }

    public static void loadAd(AdView adView) {
        if (BuildConfig.DEBUG) {
            adView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(Constants.ADMIN_DEVICE_ID).build());
        } else {
            adView.loadAd(new AdRequest.Builder().build());
        }
    }
}