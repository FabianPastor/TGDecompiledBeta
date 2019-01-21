package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.MediaActivity.AnonymousClass4;

final /* synthetic */ class MediaActivity$4$$Lambda$1 implements OnClickListener {
    private final AnonymousClass4 arg$1;
    private final boolean[] arg$2;

    MediaActivity$4$$Lambda$1(AnonymousClass4 anonymousClass4, boolean[] zArr) {
        this.arg$1 = anonymousClass4;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$MediaActivity$4(this.arg$2, dialogInterface, i);
    }
}
