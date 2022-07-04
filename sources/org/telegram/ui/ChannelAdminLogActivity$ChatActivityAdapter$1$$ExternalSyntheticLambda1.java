package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChannelAdminLogActivity;

public final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1 implements AvatarPreviewer.Callback {
    public final /* synthetic */ ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ ChatMessageCell f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1(ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1 r1, ChatMessageCell chatMessageCell, TLRPC.User user) {
        this.f$0 = r1;
        this.f$1 = chatMessageCell;
        this.f$2 = user;
    }

    public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
        this.f$0.m2868xd0592d13(this.f$1, this.f$2, menuItem);
    }
}
