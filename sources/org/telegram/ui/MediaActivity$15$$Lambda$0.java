package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.MediaActivity.AnonymousClass15;

final /* synthetic */ class MediaActivity$15$$Lambda$0 implements OnClickListener {
    private final AnonymousClass15 arg$1;
    private final String arg$2;

    MediaActivity$15$$Lambda$0(AnonymousClass15 anonymousClass15, String str) {
        this.arg$1 = anonymousClass15;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onLinkLongPress$0$MediaActivity$15(this.arg$2, dialogInterface, i);
    }
}
