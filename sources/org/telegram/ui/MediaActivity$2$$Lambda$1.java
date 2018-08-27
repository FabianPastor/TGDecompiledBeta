package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.MediaActivity.C16252;

final /* synthetic */ class MediaActivity$2$$Lambda$1 implements OnClickListener {
    private final C16252 arg$1;
    private final boolean[] arg$2;

    MediaActivity$2$$Lambda$1(C16252 c16252, boolean[] zArr) {
        this.arg$1 = c16252;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$MediaActivity$2(this.arg$2, dialogInterface, i);
    }
}
