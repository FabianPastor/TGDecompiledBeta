package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda86 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ TLRPC.TL_messages_editMessage f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda86(SendMessagesHelper sendMessagesHelper, BaseFragment baseFragment, TLRPC.TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = baseFragment;
        this.f$2 = tL_messages_editMessage;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m436lambda$editMessage$16$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, tLObject, tL_error);
    }
}
