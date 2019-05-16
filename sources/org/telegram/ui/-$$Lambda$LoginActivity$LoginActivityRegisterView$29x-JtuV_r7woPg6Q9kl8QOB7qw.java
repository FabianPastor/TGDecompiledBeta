package org.telegram.ui;

import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivityRegisterView$29x-JtuV_r7woPg6Q9kl8QOB7qw implements Runnable {
    private final /* synthetic */ LoginActivityRegisterView f$0;
    private final /* synthetic */ PhotoSize f$1;
    private final /* synthetic */ PhotoSize f$2;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivityRegisterView$29x-JtuV_r7woPg6Q9kl8QOB7qw(LoginActivityRegisterView loginActivityRegisterView, PhotoSize photoSize, PhotoSize photoSize2) {
        this.f$0 = loginActivityRegisterView;
        this.f$1 = photoSize;
        this.f$2 = photoSize2;
    }

    public final void run() {
        this.f$0.lambda$didUploadPhoto$9$LoginActivity$LoginActivityRegisterView(this.f$1, this.f$2);
    }
}
