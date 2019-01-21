package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PhotoPickerActivity$$Lambda$8 implements RequestDelegate {
    private final PhotoPickerActivity arg$1;
    private final int arg$2;

    PhotoPickerActivity$$Lambda$8(PhotoPickerActivity photoPickerActivity, int i) {
        this.arg$1 = photoPickerActivity;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchImages$11$PhotoPickerActivity(this.arg$2, tLObject, tL_error);
    }
}
