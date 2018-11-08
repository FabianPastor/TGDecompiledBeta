package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.MediaActivity.C18449;

/* renamed from: org.telegram.ui.MediaActivity$9$$Lambda$0 */
final /* synthetic */ class MediaActivity$9$$Lambda$0 implements OnClickListener {
    private final C18449 arg$1;
    private final String arg$2;

    MediaActivity$9$$Lambda$0(C18449 c18449, String str) {
        this.arg$1 = c18449;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onLinkLongPress$0$MediaActivity$9(this.arg$2, dialogInterface, i);
    }
}
