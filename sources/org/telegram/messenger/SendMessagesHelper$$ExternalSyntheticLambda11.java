package org.telegram.messenger;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.TwoStepVerificationSetupActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda11 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda11(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.presentFragment(new TwoStepVerificationSetupActivity(6, (TLRPC.TL_account_password) null));
    }
}
