package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PhotoPickerActivity$$Lambda$7 implements RequestDelegate {
    private final PhotoPickerActivity arg$1;

    PhotoPickerActivity$$Lambda$7(PhotoPickerActivity photoPickerActivity) {
        this.arg$1 = photoPickerActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchBotUser$9$PhotoPickerActivity(tLObject, tL_error);
    }
}
