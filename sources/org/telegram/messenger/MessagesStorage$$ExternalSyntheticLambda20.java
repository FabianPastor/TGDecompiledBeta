package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda20(MessagesStorage messagesStorage, String str, TLObject tLObject, int i, String str2) {
        this.f$0 = messagesStorage;
        this.f$1 = str;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = str2;
    }

    public final void run() {
        this.f$0.m1030lambda$putSentFile$126$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
