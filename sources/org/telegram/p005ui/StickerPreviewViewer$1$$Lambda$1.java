package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import org.telegram.p005ui.StickerPreviewViewer.CLASSNAME;

/* renamed from: org.telegram.ui.StickerPreviewViewer$1$$Lambda$1 */
final /* synthetic */ class StickerPreviewViewer$1$$Lambda$1 implements OnDismissListener {
    private final CLASSNAME arg$1;

    StickerPreviewViewer$1$$Lambda$1(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$run$1$StickerPreviewViewer$1(dialogInterface);
    }
}
