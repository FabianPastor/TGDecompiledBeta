package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.MediaActivity.C21652;

/* renamed from: org.telegram.ui.MediaActivity$2$$Lambda$1 */
final /* synthetic */ class MediaActivity$2$$Lambda$1 implements OnClickListener {
    private final C21652 arg$1;
    private final boolean[] arg$2;

    MediaActivity$2$$Lambda$1(C21652 c21652, boolean[] zArr) {
        this.arg$1 = c21652;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$MediaActivity$2(this.arg$2, dialogInterface, i);
    }
}
