package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.PhotoPickerActivity$$Lambda$10 */
final /* synthetic */ class PhotoPickerActivity$$Lambda$10 implements Runnable {
    private final PhotoPickerActivity arg$1;
    private final TLObject arg$2;

    PhotoPickerActivity$$Lambda$10(PhotoPickerActivity photoPickerActivity, TLObject tLObject) {
        this.arg$1 = photoPickerActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$8$PhotoPickerActivity(this.arg$2);
    }
}
