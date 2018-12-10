package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.NewContactActivity.CLASSNAME;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;

/* renamed from: org.telegram.ui.NewContactActivity$1$$Lambda$2 */
final /* synthetic */ class NewContactActivity$1$$Lambda$2 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final TL_inputPhoneContact arg$2;

    NewContactActivity$1$$Lambda$2(CLASSNAME CLASSNAME, TL_inputPhoneContact tL_inputPhoneContact) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = tL_inputPhoneContact;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$NewContactActivity$1(this.arg$2, dialogInterface, i);
    }
}
