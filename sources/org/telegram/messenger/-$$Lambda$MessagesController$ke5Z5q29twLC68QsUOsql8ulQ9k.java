package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ke5Z5q29twLCLASSNAMEQsUOsql8ulQ9k implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ BaseFragment f$1;
    private final /* synthetic */ TL_channels_inviteToChannel f$2;

    public /* synthetic */ -$$Lambda$MessagesController$ke5Z5q29twLCLASSNAMEQsUOsql8ulQ9k(MessagesController messagesController, BaseFragment baseFragment, TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        this.f$0 = messagesController;
        this.f$1 = baseFragment;
        this.f$2 = tL_channels_inviteToChannel;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$addUsersToChannel$183$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
