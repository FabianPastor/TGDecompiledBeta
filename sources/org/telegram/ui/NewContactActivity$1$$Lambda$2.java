package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.ui.NewContactActivity.AnonymousClass1;

final /* synthetic */ class NewContactActivity$1$$Lambda$2 implements OnClickListener {
    private final AnonymousClass1 arg$1;
    private final TL_inputPhoneContact arg$2;

    NewContactActivity$1$$Lambda$2(AnonymousClass1 anonymousClass1, TL_inputPhoneContact tL_inputPhoneContact) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_inputPhoneContact;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$NewContactActivity$1(this.arg$2, dialogInterface, i);
    }
}
