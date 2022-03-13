package org.telegram.ui.Components;

import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.SenderSelectPopup;

public final /* synthetic */ class ChatActivityEnterView$20$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass20 f$0;
    public final /* synthetic */ SimpleAvatarView f$1;
    public final /* synthetic */ SenderSelectPopup.SenderView f$2;

    public /* synthetic */ ChatActivityEnterView$20$$ExternalSyntheticLambda0(ChatActivityEnterView.AnonymousClass20 r1, SimpleAvatarView simpleAvatarView, SenderSelectPopup.SenderView senderView) {
        this.f$0 = r1;
        this.f$1 = simpleAvatarView;
        this.f$2 = senderView;
    }

    public final void run() {
        this.f$0.lambda$onDraw$0(this.f$1, this.f$2);
    }
}
