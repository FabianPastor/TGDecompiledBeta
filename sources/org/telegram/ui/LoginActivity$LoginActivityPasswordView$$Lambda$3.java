package org.telegram.ui;

import org.telegram.ui.LoginActivity.LoginActivityPasswordView;

final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$Lambda$3 implements Runnable {
    private final LoginActivityPasswordView arg$1;
    private final String arg$2;

    LoginActivity$LoginActivityPasswordView$$Lambda$3(LoginActivityPasswordView loginActivityPasswordView, String str) {
        this.arg$1 = loginActivityPasswordView;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$onNextPressed$13$LoginActivity$LoginActivityPasswordView(this.arg$2);
    }
}
