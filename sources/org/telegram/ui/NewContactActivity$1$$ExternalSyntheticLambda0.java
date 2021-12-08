package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.NewContactActivity;

public final /* synthetic */ class NewContactActivity$1$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ NewContactActivity.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_inputPhoneContact f$1;

    public /* synthetic */ NewContactActivity$1$$ExternalSyntheticLambda0(NewContactActivity.AnonymousClass1 r1, TLRPC.TL_inputPhoneContact tL_inputPhoneContact) {
        this.f$0 = r1;
        this.f$1 = tL_inputPhoneContact;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3320lambda$onItemClick$0$orgtelegramuiNewContactActivity$1(this.f$1, dialogInterface, i);
    }
}
