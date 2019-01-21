package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.DialogsActivity.AnonymousClass8;

final /* synthetic */ class DialogsActivity$8$$Lambda$0 implements OnClickListener {
    private final AnonymousClass8 arg$1;
    private final int arg$2;

    DialogsActivity$8$$Lambda$0(AnonymousClass8 anonymousClass8, int i) {
        this.arg$1 = anonymousClass8;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$needRemoveHint$0$DialogsActivity$8(this.arg$2, dialogInterface, i);
    }
}
