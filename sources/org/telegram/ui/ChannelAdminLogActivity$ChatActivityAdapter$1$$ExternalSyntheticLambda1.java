package org.telegram.ui;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChannelAdminLogActivity;

public final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1 implements AvatarPreviewer.Callback {
    public final /* synthetic */ ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ ChatMessageCell f$1;
    public final /* synthetic */ TLRPC$User f$2;

    public /* synthetic */ ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1(ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1 r1, ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User) {
        this.f$0 = r1;
        this.f$1 = chatMessageCell;
        this.f$2 = tLRPC$User;
    }

    public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
        this.f$0.lambda$didLongPressUserAvatar$0(this.f$1, this.f$2, menuItem);
    }
}
