package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$LG64jTT1HbQiGe4Zsym1E_9A8Os implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ BaseFragment f$1;
    private final /* synthetic */ TL_messages_createChat f$2;

    public /* synthetic */ -$$Lambda$MessagesController$LG64jTT1HbQiGe4Zsym1E_9A8Os(MessagesController messagesController, BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat) {
        this.f$0 = messagesController;
        this.f$1 = baseFragment;
        this.f$2 = tL_messages_createChat;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$createChat$161$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
