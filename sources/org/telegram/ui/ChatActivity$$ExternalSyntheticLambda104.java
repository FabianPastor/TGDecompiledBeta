package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda104 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_payments_bankCardData f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda104(ChatActivity chatActivity, TLRPC.TL_payments_bankCardData tL_payments_bankCardData, String str) {
        this.f$0 = chatActivity;
        this.f$1 = tL_payments_bankCardData;
        this.f$2 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1721lambda$didPressMessageUrl$163$orgtelegramuiChatActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
