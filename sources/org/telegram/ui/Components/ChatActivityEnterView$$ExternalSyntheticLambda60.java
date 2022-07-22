package org.telegram.ui.Components;

import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.ui.Components.SenderSelectPopup;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda60 implements SenderSelectPopup.OnSelectCallback {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ TLRPC$ChatFull f$1;
    public final /* synthetic */ MessagesController f$2;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda60(ChatActivityEnterView chatActivityEnterView, TLRPC$ChatFull tLRPC$ChatFull, MessagesController messagesController) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = tLRPC$ChatFull;
        this.f$2 = messagesController;
    }

    public final void onPeerSelected(RecyclerView recyclerView, SenderSelectPopup.SenderView senderView, TLRPC$Peer tLRPC$Peer) {
        this.f$0.lambda$new$13(this.f$1, this.f$2, recyclerView, senderView, tLRPC$Peer);
    }
}
