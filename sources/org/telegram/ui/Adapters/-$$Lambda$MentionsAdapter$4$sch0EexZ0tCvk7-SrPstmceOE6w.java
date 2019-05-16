package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.Adapters.MentionsAdapter.AnonymousClass4;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MentionsAdapter$4$sch0EexZ0tCvk7-SrPstmceOE6w implements RequestDelegate {
    private final /* synthetic */ AnonymousClass4 f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ MessagesController f$2;
    private final /* synthetic */ MessagesStorage f$3;

    public /* synthetic */ -$$Lambda$MentionsAdapter$4$sch0EexZ0tCvk7-SrPstmceOE6w(AnonymousClass4 anonymousClass4, String str, MessagesController messagesController, MessagesStorage messagesStorage) {
        this.f$0 = anonymousClass4;
        this.f$1 = str;
        this.f$2 = messagesController;
        this.f$3 = messagesStorage;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$run$1$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
