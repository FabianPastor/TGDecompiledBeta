package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import org.telegram.ui.PrivacyControlActivity.C16391;

final /* synthetic */ class PrivacyControlActivity$1$$Lambda$0 implements OnClickListener {
    private final C16391 arg$1;
    private final SharedPreferences arg$2;

    PrivacyControlActivity$1$$Lambda$0(C16391 c16391, SharedPreferences sharedPreferences) {
        this.arg$1 = c16391;
        this.arg$2 = sharedPreferences;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$PrivacyControlActivity$1(this.arg$2, dialogInterface, i);
    }
}
