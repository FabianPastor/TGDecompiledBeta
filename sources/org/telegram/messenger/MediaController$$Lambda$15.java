package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

final /* synthetic */ class MediaController$$Lambda$15 implements OnCancelListener {
    private final boolean[] arg$1;

    MediaController$$Lambda$15(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1[0] = true;
    }
}
