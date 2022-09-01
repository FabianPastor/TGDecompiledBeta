package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda3 implements AvatarPreviewer.Callback {
    public final /* synthetic */ ChatActivity.ChatActivityAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ ChatMessageCell f$2;

    public /* synthetic */ ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda3(ChatActivity.ChatActivityAdapter.AnonymousClass1 r1, TLRPC$Chat tLRPC$Chat, ChatMessageCell chatMessageCell) {
        this.f$0 = r1;
        this.f$1 = tLRPC$Chat;
        this.f$2 = chatMessageCell;
    }

    public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
        this.f$0.lambda$didLongPressChannelAvatar$2(this.f$1, this.f$2, menuItem);
    }
}
