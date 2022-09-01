package org.telegram.ui;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda4 implements AvatarPreviewer.Callback {
    public final /* synthetic */ ChatActivity.ChatActivityAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ ChatMessageCell f$1;
    public final /* synthetic */ TLRPC$User f$2;

    public /* synthetic */ ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda4(ChatActivity.ChatActivityAdapter.AnonymousClass1 r1, ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User) {
        this.f$0 = r1;
        this.f$1 = chatMessageCell;
        this.f$2 = tLRPC$User;
    }

    public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
        this.f$0.lambda$didLongPressUserAvatar$0(this.f$1, this.f$2, menuItem);
    }
}
