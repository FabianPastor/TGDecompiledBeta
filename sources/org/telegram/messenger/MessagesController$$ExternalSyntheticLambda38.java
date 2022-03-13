package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda38 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$messages_Dialogs f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda38(MessagesController messagesController, int i, int i2, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, boolean z, int i3, ArrayList arrayList, int i4, boolean z2, boolean z3) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tLRPC$messages_Dialogs;
        this.f$4 = z;
        this.f$5 = i3;
        this.f$6 = arrayList;
        this.f$7 = i4;
        this.f$8 = z2;
        this.f$9 = z3;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDialogs$175(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
