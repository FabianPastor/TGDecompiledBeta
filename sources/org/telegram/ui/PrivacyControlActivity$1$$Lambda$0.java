package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import org.telegram.ui.PrivacyControlActivity.AnonymousClass1;

final /* synthetic */ class PrivacyControlActivity$1$$Lambda$0 implements OnClickListener {
    private final AnonymousClass1 arg$1;
    private final SharedPreferences arg$2;

    PrivacyControlActivity$1$$Lambda$0(AnonymousClass1 anonymousClass1, SharedPreferences sharedPreferences) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = sharedPreferences;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$PrivacyControlActivity$1(this.arg$2, dialogInterface, i);
    }
}
