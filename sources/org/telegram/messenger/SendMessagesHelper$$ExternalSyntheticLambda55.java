package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$6;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda55(SendMessagesHelper sendMessagesHelper, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = arrayList;
        this.f$4 = arrayList2;
        this.f$5 = z;
        this.f$6 = tLRPC$TL_messages_sendMultiMedia;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequestMulti$45(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}