package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.LoginActivity.PhoneView;

/* renamed from: org.telegram.ui.LoginActivity$PhoneView$$Lambda$4 */
final /* synthetic */ class LoginActivity$PhoneView$$Lambda$4 implements OnClickListener {
    private final PhoneView arg$1;
    private final int arg$2;

    LoginActivity$PhoneView$$Lambda$4(PhoneView phoneView, int i) {
        this.arg$1 = phoneView;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onNextPressed$5$LoginActivity$PhoneView(this.arg$2, dialogInterface, i);
    }
}
