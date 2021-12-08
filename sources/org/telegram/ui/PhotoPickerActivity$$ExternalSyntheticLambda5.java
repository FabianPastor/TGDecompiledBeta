package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ PhotoPickerActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC$User f$4;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda5(PhotoPickerActivity photoPickerActivity, int i, TLObject tLObject, boolean z, TLRPC$User tLRPC$User) {
        this.f$0 = photoPickerActivity;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = z;
        this.f$4 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$searchImages$10(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
