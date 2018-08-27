package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.MediaActivity.C16309;

final /* synthetic */ class MediaActivity$9$$Lambda$0 implements OnClickListener {
    private final C16309 arg$1;
    private final String arg$2;

    MediaActivity$9$$Lambda$0(C16309 c16309, String str) {
        this.arg$1 = c16309;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onLinkLongPress$0$MediaActivity$9(this.arg$2, dialogInterface, i);
    }
}
