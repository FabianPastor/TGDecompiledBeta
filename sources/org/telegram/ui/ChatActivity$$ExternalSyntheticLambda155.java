package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda155 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ChatMessageCell f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda155(ChatActivity chatActivity, int i, ChatMessageCell chatMessageCell) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = chatMessageCell;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.m3002lambda$didPressMessageUrl$241$orgtelegramuiChatActivity(this.f$1, this.f$2, dialogInterface);
    }
}
