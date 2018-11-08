package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.MediaActivity.C18362;

/* renamed from: org.telegram.ui.MediaActivity$2$$Lambda$1 */
final /* synthetic */ class MediaActivity$2$$Lambda$1 implements OnClickListener {
    private final C18362 arg$1;
    private final boolean[] arg$2;

    MediaActivity$2$$Lambda$1(C18362 c18362, boolean[] zArr) {
        this.arg$1 = c18362;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$MediaActivity$2(this.arg$2, dialogInterface, i);
    }
}
