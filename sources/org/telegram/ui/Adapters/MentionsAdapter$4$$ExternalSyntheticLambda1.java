package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Adapters.MentionsAdapter;

public final /* synthetic */ class MentionsAdapter$4$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ MentionsAdapter.AnonymousClass4 f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ MessagesController f$2;
    public final /* synthetic */ MessagesStorage f$3;

    public /* synthetic */ MentionsAdapter$4$$ExternalSyntheticLambda1(MentionsAdapter.AnonymousClass4 r1, String str, MessagesController messagesController, MessagesStorage messagesStorage) {
        this.f$0 = r1;
        this.f$1 = str;
        this.f$2 = messagesController;
        this.f$3 = messagesStorage;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$run$1(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
