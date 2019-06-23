package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity.AnonymousClass16;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$16$Liy-V7O5Gt8N8geJIFScQnccbnE implements RequestDelegate {
    private final /* synthetic */ AnonymousClass16 f$0;
    private final /* synthetic */ MessagesStorage f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$16$Liy-V7O5Gt8N8geJIFScQnccbnE(AnonymousClass16 anonymousClass16, MessagesStorage messagesStorage) {
        this.f$0 = anonymousClass16;
        this.f$1 = messagesStorage;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadLastUnreadMention$2$ChatActivity$16(this.f$1, tLObject, tL_error);
    }
}
