package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.NewContactActivity.C14831;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;

/* renamed from: org.telegram.ui.NewContactActivity$1$$Lambda$2 */
final /* synthetic */ class NewContactActivity$1$$Lambda$2 implements OnClickListener {
    private final C14831 arg$1;
    private final TL_inputPhoneContact arg$2;

    NewContactActivity$1$$Lambda$2(C14831 c14831, TL_inputPhoneContact tL_inputPhoneContact) {
        this.arg$1 = c14831;
        this.arg$2 = tL_inputPhoneContact;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$NewContactActivity$1(this.arg$2, dialogInterface, i);
    }
}
