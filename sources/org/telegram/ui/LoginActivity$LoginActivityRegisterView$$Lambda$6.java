package org.telegram.ui;

import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$Lambda$6 implements Runnable {
    private final LoginActivityRegisterView arg$1;
    private final PhotoSize arg$2;
    private final PhotoSize arg$3;

    LoginActivity$LoginActivityRegisterView$$Lambda$6(LoginActivityRegisterView loginActivityRegisterView, PhotoSize photoSize, PhotoSize photoSize2) {
        this.arg$1 = loginActivityRegisterView;
        this.arg$2 = photoSize;
        this.arg$3 = photoSize2;
    }

    public void run() {
        this.arg$1.lambda$didUploadPhoto$9$LoginActivity$LoginActivityRegisterView(this.arg$2, this.arg$3);
    }
}
