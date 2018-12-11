package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.Components.NumberPicker;

/* renamed from: org.telegram.ui.ChatSettingsActivity$$Lambda$1 */
final /* synthetic */ class ChatSettingsActivity$$Lambda$1 implements OnClickListener {
    private final ChatSettingsActivity arg$1;
    private final NumberPicker arg$2;
    private final int arg$3;

    ChatSettingsActivity$$Lambda$1(ChatSettingsActivity chatSettingsActivity, NumberPicker numberPicker, int i) {
        this.arg$1 = chatSettingsActivity;
        this.arg$2 = numberPicker;
        this.arg$3 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$ChatSettingsActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
