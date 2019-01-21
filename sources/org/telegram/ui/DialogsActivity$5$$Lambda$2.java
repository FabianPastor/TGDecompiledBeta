package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.DialogsActivity.AnonymousClass5;

final /* synthetic */ class DialogsActivity$5$$Lambda$2 implements OnClickListener {
    private final AnonymousClass5 arg$1;
    private final boolean arg$10;
    private final TL_dialog arg$2;
    private final int arg$3;
    private final long arg$4;
    private final boolean arg$5;
    private final boolean arg$6;
    private final Chat arg$7;
    private final User arg$8;
    private final boolean arg$9;

    DialogsActivity$5$$Lambda$2(AnonymousClass5 anonymousClass5, TL_dialog tL_dialog, int i, long j, boolean z, boolean z2, Chat chat, User user, boolean z3, boolean z4) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = tL_dialog;
        this.arg$3 = i;
        this.arg$4 = j;
        this.arg$5 = z;
        this.arg$6 = z2;
        this.arg$7 = chat;
        this.arg$8 = user;
        this.arg$9 = z3;
        this.arg$10 = z4;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$6$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, dialogInterface, i);
    }
}
