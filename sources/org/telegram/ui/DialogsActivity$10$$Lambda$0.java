package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.DialogsActivity.AnonymousClass10;

final /* synthetic */ class DialogsActivity$10$$Lambda$0 implements OnClickListener {
    private final AnonymousClass10 arg$1;
    private final int arg$2;

    DialogsActivity$10$$Lambda$0(AnonymousClass10 anonymousClass10, int i) {
        this.arg$1 = anonymousClass10;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$needRemoveHint$0$DialogsActivity$10(this.arg$2, dialogInterface, i);
    }
}
