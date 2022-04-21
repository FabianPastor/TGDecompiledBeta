package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda13 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ TLRPC.ChatFull f$1;
    public final /* synthetic */ TLRPC.Chat f$2;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda13(ChatLinkActivity chatLinkActivity, TLRPC.ChatFull chatFull, TLRPC.Chat chat) {
        this.f$0 = chatLinkActivity;
        this.f$1 = chatFull;
        this.f$2 = chat;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1958lambda$showLinkAlert$9$orgtelegramuiChatLinkActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
