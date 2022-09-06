package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputChatPhoto;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda328 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_inputChatPhoto f$1;
    public final /* synthetic */ TLRPC$FileLocation f$2;
    public final /* synthetic */ TLRPC$FileLocation f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda328(MessagesController messagesController, TLRPC$TL_inputChatPhoto tLRPC$TL_inputChatPhoto, TLRPC$FileLocation tLRPC$FileLocation, TLRPC$FileLocation tLRPC$FileLocation2, String str, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_inputChatPhoto;
        this.f$2 = tLRPC$FileLocation;
        this.f$3 = tLRPC$FileLocation2;
        this.f$4 = str;
        this.f$5 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$changeChatAvatar$252(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
