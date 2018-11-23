package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.ImageUpdater$$Lambda$0 */
final /* synthetic */ class ImageUpdater$$Lambda$0 implements OnClickListener {
    private final ImageUpdater arg$1;
    private final Runnable arg$2;

    ImageUpdater$$Lambda$0(ImageUpdater imageUpdater, Runnable runnable) {
        this.arg$1 = imageUpdater;
        this.arg$2 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$openMenu$0$ImageUpdater(this.arg$2, dialogInterface, i);
    }
}
