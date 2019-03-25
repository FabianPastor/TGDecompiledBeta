package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import org.telegram.ui.ContentPreviewViewer.AnonymousClass1;

final /* synthetic */ class ContentPreviewViewer$1$$Lambda$3 implements OnDismissListener {
    private final AnonymousClass1 arg$1;

    ContentPreviewViewer$1$$Lambda$3(AnonymousClass1 anonymousClass1) {
        this.arg$1 = anonymousClass1;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$run$3$ContentPreviewViewer$1(dialogInterface);
    }
}
