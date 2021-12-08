package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda225 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_inputChatPhoto f$1;
    public final /* synthetic */ TLRPC.FileLocation f$2;
    public final /* synthetic */ TLRPC.FileLocation f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda225(MessagesController messagesController, TLRPC.TL_inputChatPhoto tL_inputChatPhoto, TLRPC.FileLocation fileLocation, TLRPC.FileLocation fileLocation2, String str, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = tL_inputChatPhoto;
        this.f$2 = fileLocation;
        this.f$3 = fileLocation2;
        this.f$4 = str;
        this.f$5 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m125x60aavar_f(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
