package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_payments_bankCardData;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda30 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_payments_bankCardData f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda30(ChatActivity chatActivity, TLRPC$TL_payments_bankCardData tLRPC$TL_payments_bankCardData, String str) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_payments_bankCardData;
        this.f$2 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressMessageUrl$180(this.f$1, this.f$2, dialogInterface, i);
    }
}
