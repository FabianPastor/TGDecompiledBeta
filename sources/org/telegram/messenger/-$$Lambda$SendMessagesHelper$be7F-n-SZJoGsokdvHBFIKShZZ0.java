package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$be7F-n-SZJoGsokdvHBFIKShZZ0 implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ BaseFragment f$1;
    private final /* synthetic */ TL_messages_editMessage f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$be7F-n-SZJoGsokdvHBFIKShZZ0(SendMessagesHelper sendMessagesHelper, BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage, Runnable runnable) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = baseFragment;
        this.f$2 = tL_messages_editMessage;
        this.f$3 = runnable;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$editMessage$13$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
