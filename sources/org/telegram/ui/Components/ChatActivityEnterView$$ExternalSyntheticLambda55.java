package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda55 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ TLRPC$Document f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Object f$3;
    public final /* synthetic */ MessageObject.SendAnimationData f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda55(ChatActivityEnterView chatActivityEnterView, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = tLRPC$Document;
        this.f$2 = str;
        this.f$3 = obj;
        this.f$4 = sendAnimationData;
        this.f$5 = z;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onStickerSelected$51(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, z, i);
    }
}
