package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.WallpapersListActivity.AnonymousClass2;

final /* synthetic */ class WallpapersListActivity$2$$Lambda$0 implements OnClickListener {
    private final AnonymousClass2 arg$1;

    WallpapersListActivity$2$$Lambda$0(AnonymousClass2 anonymousClass2) {
        this.arg$1 = anonymousClass2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$2$WallpapersListActivity$2(dialogInterface, i);
    }
}
