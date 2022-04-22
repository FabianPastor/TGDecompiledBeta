package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ PhotoPickerActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC$User f$3;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda7(PhotoPickerActivity photoPickerActivity, int i, boolean z, TLRPC$User tLRPC$User) {
        this.f$0 = photoPickerActivity;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = tLRPC$User;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchImages$11(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
