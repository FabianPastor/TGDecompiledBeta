package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.MediaActivity.MediaSearchAdapter;

final /* synthetic */ class MediaActivity$MediaSearchAdapter$$Lambda$0 implements RequestDelegate {
    private final MediaSearchAdapter arg$1;
    private final int arg$2;
    private final int arg$3;

    MediaActivity$MediaSearchAdapter$$Lambda$0(MediaSearchAdapter mediaSearchAdapter, int i, int i2) {
        this.arg$1 = mediaSearchAdapter;
        this.arg$2 = i;
        this.arg$3 = i2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$queryServerSearch$1$MediaActivity$MediaSearchAdapter(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
