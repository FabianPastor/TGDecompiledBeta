package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;

final /* synthetic */ class PrivacyControlActivity$$Lambda$2 implements OnClickListener {
    private final PrivacyControlActivity arg$1;
    private final SharedPreferences arg$2;

    PrivacyControlActivity$$Lambda$2(PrivacyControlActivity privacyControlActivity, SharedPreferences sharedPreferences) {
        this.arg$1 = privacyControlActivity;
        this.arg$2 = sharedPreferences;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processDone$5$PrivacyControlActivity(this.arg$2, dialogInterface, i);
    }
}
