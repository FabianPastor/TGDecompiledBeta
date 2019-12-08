package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$b56InizIt0I4FbnoQmxOOGkS67M implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_messages_editChatAdmin f$3;

    public /* synthetic */ -$$Lambda$MessagesController$b56InizIt0I4FbnoQmxOOGkS67M(MessagesController messagesController, int i, BaseFragment baseFragment, TL_messages_editChatAdmin tL_messages_editChatAdmin) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = baseFragment;
        this.f$3 = tL_messages_editChatAdmin;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$setUserAdminRole$61$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
