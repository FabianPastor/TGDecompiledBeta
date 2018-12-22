package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.PhotoPickerActivity$$Lambda$9 */
final /* synthetic */ class PhotoPickerActivity$$Lambda$9 implements Runnable {
    private final PhotoPickerActivity arg$1;
    private final int arg$2;
    private final TLObject arg$3;

    PhotoPickerActivity$$Lambda$9(PhotoPickerActivity photoPickerActivity, int i, TLObject tLObject) {
        this.arg$1 = photoPickerActivity;
        this.arg$2 = i;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$10$PhotoPickerActivity(this.arg$2, this.arg$3);
    }
}
