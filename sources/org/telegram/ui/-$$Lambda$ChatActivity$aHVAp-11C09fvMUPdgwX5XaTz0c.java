package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$aHVAp-11CLASSNAMEfvMUPdgwX5XaTz0c implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TL_messages_editMessage f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$aHVAp-11CLASSNAMEfvMUPdgwX5XaTz0c(ChatActivity chatActivity, TL_error tL_error, TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = chatActivity;
        this.f$1 = tL_error;
        this.f$2 = tL_messages_editMessage;
    }

    public final void run() {
        this.f$0.lambda$null$76$ChatActivity(this.f$1, this.f$2);
    }
}