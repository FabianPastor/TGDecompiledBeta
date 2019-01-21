package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.ui.DialogsActivity.AnonymousClass5;

final /* synthetic */ class DialogsActivity$5$$Lambda$1 implements OnClickListener {
    private final AnonymousClass5 arg$1;
    private final TL_dialog arg$2;
    private final boolean arg$3;
    private final boolean arg$4;
    private final Chat arg$5;
    private final int arg$6;

    DialogsActivity$5$$Lambda$1(AnonymousClass5 anonymousClass5, TL_dialog tL_dialog, boolean z, boolean z2, Chat chat, int i) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = tL_dialog;
        this.arg$3 = z;
        this.arg$4 = z2;
        this.arg$5 = chat;
        this.arg$6 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$3$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, dialogInterface, i);
    }
}
