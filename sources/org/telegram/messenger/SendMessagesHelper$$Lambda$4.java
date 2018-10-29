package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class SendMessagesHelper$$Lambda$4 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final BaseFragment arg$2;
    private final TL_messages_editMessage arg$3;
    private final Runnable arg$4;

    SendMessagesHelper$$Lambda$4(SendMessagesHelper sendMessagesHelper, BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage, Runnable runnable) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = baseFragment;
        this.arg$3 = tL_messages_editMessage;
        this.arg$4 = runnable;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$editMessage$11$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
