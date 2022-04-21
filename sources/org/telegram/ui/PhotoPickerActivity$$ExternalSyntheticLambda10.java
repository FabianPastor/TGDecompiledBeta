package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ PhotoPickerActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC.User f$3;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda10(PhotoPickerActivity photoPickerActivity, int i, boolean z, TLRPC.User user) {
        this.f$0 = photoPickerActivity;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = user;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2894lambda$searchImages$11$orgtelegramuiPhotoPickerActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
