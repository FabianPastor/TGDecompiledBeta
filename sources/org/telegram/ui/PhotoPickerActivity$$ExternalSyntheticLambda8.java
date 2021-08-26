package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ PhotoPickerActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda8(PhotoPickerActivity photoPickerActivity, boolean z) {
        this.f$0 = photoPickerActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchBotUser$9(this.f$1, tLObject, tLRPC$TL_error);
    }
}
