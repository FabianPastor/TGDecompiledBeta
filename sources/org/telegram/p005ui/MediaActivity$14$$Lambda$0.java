package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.MediaActivity.CLASSNAME;

/* renamed from: org.telegram.ui.MediaActivity$14$$Lambda$0 */
final /* synthetic */ class MediaActivity$14$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final String arg$2;

    MediaActivity$14$$Lambda$0(CLASSNAME CLASSNAME, String str) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onLinkLongPress$0$MediaActivity$14(this.arg$2, dialogInterface, i);
    }
}
