package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$messages_Messages;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda194 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$messages_Messages f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ ArrayList f$7;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda194(ChatActivity chatActivity, TLRPC$messages_Messages tLRPC$messages_Messages, long j, int i, int i2, int i3, int i4, ArrayList arrayList) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$messages_Messages;
        this.f$2 = j;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = i3;
        this.f$6 = i4;
        this.f$7 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDiscussionMessage$220(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
