package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PhotoPickerActivity$$Lambda$6 */
final /* synthetic */ class PhotoPickerActivity$$Lambda$6 implements RequestDelegate {
    private final PhotoPickerActivity arg$1;
    private final int arg$2;
    private final String arg$3;

    PhotoPickerActivity$$Lambda$6(PhotoPickerActivity photoPickerActivity, int i, String str) {
        this.arg$1 = photoPickerActivity;
        this.arg$2 = i;
        this.arg$3 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchGiphyImages$7$PhotoPickerActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
