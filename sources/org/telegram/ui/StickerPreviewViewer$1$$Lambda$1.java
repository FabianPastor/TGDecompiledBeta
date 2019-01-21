package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import org.telegram.ui.StickerPreviewViewer.AnonymousClass1;

final /* synthetic */ class StickerPreviewViewer$1$$Lambda$1 implements OnDismissListener {
    private final AnonymousClass1 arg$1;

    StickerPreviewViewer$1$$Lambda$1(AnonymousClass1 anonymousClass1) {
        this.arg$1 = anonymousClass1;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$run$1$StickerPreviewViewer$1(dialogInterface);
    }
}
