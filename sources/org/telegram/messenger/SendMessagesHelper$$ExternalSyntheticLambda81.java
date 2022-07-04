package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda81 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda81(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, SendMessagesHelper.DelayedMessage delayedMessage, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = tL_messages_sendMultiMedia;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
        this.f$5 = delayedMessage;
        this.f$6 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m471xCLASSNAMEdc(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
