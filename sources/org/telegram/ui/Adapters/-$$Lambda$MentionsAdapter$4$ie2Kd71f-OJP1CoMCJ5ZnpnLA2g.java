package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.Adapters.MentionsAdapter.AnonymousClass4;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MentionsAdapter$4$ie2Kd71f-OJP1CoMCJ5ZnpnLA2g implements Runnable {
    private final /* synthetic */ AnonymousClass4 f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ MessagesController f$4;
    private final /* synthetic */ MessagesStorage f$5;

    public /* synthetic */ -$$Lambda$MentionsAdapter$4$ie2Kd71f-OJP1CoMCJ5ZnpnLA2g(AnonymousClass4 anonymousClass4, String str, TL_error tL_error, TLObject tLObject, MessagesController messagesController, MessagesStorage messagesStorage) {
        this.f$0 = anonymousClass4;
        this.f$1 = str;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = messagesController;
        this.f$5 = messagesStorage;
    }

    public final void run() {
        this.f$0.lambda$null$0$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
