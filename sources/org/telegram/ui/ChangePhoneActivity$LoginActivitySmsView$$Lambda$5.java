package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView;

final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$Lambda$5 implements OnClickListener {
    private final LoginActivitySmsView arg$1;

    ChangePhoneActivity$LoginActivitySmsView$$Lambda$5(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onBackPressed$8$ChangePhoneActivity$LoginActivitySmsView(dialogInterface, i);
    }
}