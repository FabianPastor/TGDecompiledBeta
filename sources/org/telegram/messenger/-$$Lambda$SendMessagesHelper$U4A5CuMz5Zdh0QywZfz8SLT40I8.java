package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$U4A5CuMz5Zdh0QywZfz8SLT40I8 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_messages_editMessage f$3;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$U4A5CuMz5Zdh0QywZfz8SLT40I8(SendMessagesHelper sendMessagesHelper, TL_error tL_error, BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_messages_editMessage;
    }

    public final void run() {
        this.f$0.lambda$null$14$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
    }
}
