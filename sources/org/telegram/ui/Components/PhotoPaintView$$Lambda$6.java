package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class PhotoPaintView$$Lambda$6 implements OnClickListener {
    private final Runnable arg$1;

    PhotoPaintView$$Lambda$6(Runnable runnable) {
        this.arg$1 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.run();
    }
}
