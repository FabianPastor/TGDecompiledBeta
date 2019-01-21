package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$5 implements OnClickListener {
    private final LoginActivitySmsView arg$1;

    LoginActivity$LoginActivitySmsView$$Lambda$5(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onBackPressed$9$LoginActivity$LoginActivitySmsView(dialogInterface, i);
    }
}
