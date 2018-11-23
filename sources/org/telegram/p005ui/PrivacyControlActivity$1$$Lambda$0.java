package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import org.telegram.p005ui.PrivacyControlActivity.C15501;

/* renamed from: org.telegram.ui.PrivacyControlActivity$1$$Lambda$0 */
final /* synthetic */ class PrivacyControlActivity$1$$Lambda$0 implements OnClickListener {
    private final C15501 arg$1;
    private final SharedPreferences arg$2;

    PrivacyControlActivity$1$$Lambda$0(C15501 c15501, SharedPreferences sharedPreferences) {
        this.arg$1 = c15501;
        this.arg$2 = sharedPreferences;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$PrivacyControlActivity$1(this.arg$2, dialogInterface, i);
    }
}
