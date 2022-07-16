package org.telegram.ui;

import android.content.DialogInterface;
import android.text.style.CharacterStyle;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda37 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ CharacterStyle f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ ChatMessageCell f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda37(ChatActivity chatActivity, String str, CharacterStyle characterStyle, MessageObject messageObject, ChatMessageCell chatMessageCell, int i) {
        this.f$0 = chatActivity;
        this.f$1 = str;
        this.f$2 = characterStyle;
        this.f$3 = messageObject;
        this.f$4 = chatMessageCell;
        this.f$5 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$openClickableLink$236(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
