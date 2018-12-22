package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.DatePicker;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$91 */
final /* synthetic */ class ChatActivity$$Lambda$91 implements OnShowListener {
    private final DatePicker arg$1;

    ChatActivity$$Lambda$91(DatePicker datePicker) {
        this.arg$1 = datePicker;
    }

    public void onShow(DialogInterface dialogInterface) {
        ChatActivity.lambda$null$30$ChatActivity(this.arg$1, dialogInterface);
    }
}
