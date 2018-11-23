package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.MediaActivity.C14769;

/* renamed from: org.telegram.ui.MediaActivity$9$$Lambda$0 */
final /* synthetic */ class MediaActivity$9$$Lambda$0 implements OnClickListener {
    private final C14769 arg$1;
    private final String arg$2;

    MediaActivity$9$$Lambda$0(C14769 c14769, String str) {
        this.arg$1 = c14769;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onLinkLongPress$0$MediaActivity$9(this.arg$2, dialogInterface, i);
    }
}
