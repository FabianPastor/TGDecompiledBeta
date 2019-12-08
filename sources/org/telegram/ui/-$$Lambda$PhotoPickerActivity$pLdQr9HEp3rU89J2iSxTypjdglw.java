package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoPickerActivity$pLdQr9HEp3rU89J2iSxTypjdglw implements Runnable {
    private final /* synthetic */ PhotoPickerActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ User f$4;

    public /* synthetic */ -$$Lambda$PhotoPickerActivity$pLdQr9HEp3rU89J2iSxTypjdglw(PhotoPickerActivity photoPickerActivity, int i, TLObject tLObject, boolean z, User user) {
        this.f$0 = photoPickerActivity;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = z;
        this.f$4 = user;
    }

    public final void run() {
        this.f$0.lambda$null$9$PhotoPickerActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
