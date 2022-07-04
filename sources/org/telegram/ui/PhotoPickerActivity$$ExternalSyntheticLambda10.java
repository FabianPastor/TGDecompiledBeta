package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ PhotoPickerActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC.User f$4;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda10(PhotoPickerActivity photoPickerActivity, String str, int i, boolean z, TLRPC.User user) {
        this.f$0 = photoPickerActivity;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = user;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4227lambda$searchImages$11$orgtelegramuiPhotoPickerActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
