package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda56 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC.TL_messages_editMessage f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda56(SendMessagesHelper sendMessagesHelper, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_messages_editMessage;
    }

    public final void run() {
        this.f$0.m443lambda$editMessage$15$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3);
    }
}
