package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import org.telegram.ui.PrivacyControlActivity.C16811;

final /* synthetic */ class PrivacyControlActivity$1$$Lambda$0 implements OnClickListener {
    private final C16811 arg$1;
    private final SharedPreferences arg$2;

    PrivacyControlActivity$1$$Lambda$0(C16811 c16811, SharedPreferences sharedPreferences) {
        this.arg$1 = c16811;
        this.arg$2 = sharedPreferences;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$PrivacyControlActivity$1(this.arg$2, dialogInterface, i);
    }
}
