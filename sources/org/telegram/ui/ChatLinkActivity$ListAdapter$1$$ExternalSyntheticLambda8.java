package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatLinkActivity;

public final /* synthetic */ class ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ ChatLinkActivity.ListAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.Chat f$2;

    public /* synthetic */ ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda8(ChatLinkActivity.ListAdapter.AnonymousClass1 r1, boolean z, TLRPC.Chat chat) {
        this.f$0 = r1;
        this.f$1 = z;
        this.f$2 = chat;
    }

    public final void run() {
        this.f$0.m3255x7588dvar_(this.f$1, this.f$2);
    }
}
