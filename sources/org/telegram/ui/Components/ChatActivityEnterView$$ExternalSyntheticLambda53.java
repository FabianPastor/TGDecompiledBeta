package org.telegram.ui.Components;

import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.SenderSelectPopup;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda53 implements SenderSelectPopup.OnSelectCallback {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ TLRPC.ChatFull f$1;
    public final /* synthetic */ MessagesController f$2;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda53(ChatActivityEnterView chatActivityEnterView, TLRPC.ChatFull chatFull, MessagesController messagesController) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = chatFull;
        this.f$2 = messagesController;
    }

    public final void onPeerSelected(RecyclerView recyclerView, SenderSelectPopup.SenderView senderView, TLRPC.Peer peer) {
        this.f$0.m3705lambda$new$12$orgtelegramuiComponentsChatActivityEnterView(this.f$1, this.f$2, recyclerView, senderView, peer);
    }
}
