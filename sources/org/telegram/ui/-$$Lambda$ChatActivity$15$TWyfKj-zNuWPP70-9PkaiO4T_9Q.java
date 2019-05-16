package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity.AnonymousClass15;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$15$TWyfKj-zNuWPP70-9PkaiO4T_9Q implements RequestDelegate {
    private final /* synthetic */ AnonymousClass15 f$0;
    private final /* synthetic */ MessagesStorage f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$15$TWyfKj-zNuWPP70-9PkaiO4T_9Q(AnonymousClass15 anonymousClass15, MessagesStorage messagesStorage) {
        this.f$0 = anonymousClass15;
        this.f$1 = messagesStorage;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadLastUnreadMention$2$ChatActivity$15(this.f$1, tLObject, tL_error);
    }
}
