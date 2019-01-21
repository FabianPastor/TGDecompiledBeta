package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class PhotoPickerActivity$$Lambda$11 implements Runnable {
    private final PhotoPickerActivity arg$1;
    private final int arg$2;
    private final TLObject arg$3;
    private final String arg$4;

    PhotoPickerActivity$$Lambda$11(PhotoPickerActivity photoPickerActivity, int i, TLObject tLObject, String str) {
        this.arg$1 = photoPickerActivity;
        this.arg$2 = i;
        this.arg$3 = tLObject;
        this.arg$4 = str;
    }

    public void run() {
        this.arg$1.lambda$null$6$PhotoPickerActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
