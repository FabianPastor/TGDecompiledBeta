package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.MediaActivity.C14672;

final /* synthetic */ class MediaActivity$2$$Lambda$1 implements OnClickListener {
    private final C14672 arg$1;
    private final boolean[] arg$2;

    MediaActivity$2$$Lambda$1(C14672 c14672, boolean[] zArr) {
        this.arg$1 = c14672;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$MediaActivity$2(this.arg$2, dialogInterface, i);
    }
}
