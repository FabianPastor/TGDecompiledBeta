package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ PhotoPickerActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda11(PhotoPickerActivity photoPickerActivity, boolean z) {
        this.f$0 = photoPickerActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4225lambda$searchBotUser$9$orgtelegramuiPhotoPickerActivity(this.f$1, tLObject, tL_error);
    }
}
