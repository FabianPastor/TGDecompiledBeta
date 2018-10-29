package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.DatePicker;

final /* synthetic */ class ChatActivity$$Lambda$81 implements OnShowListener {
    private final DatePicker arg$1;

    ChatActivity$$Lambda$81(DatePicker datePicker) {
        this.arg$1 = datePicker;
    }

    public void onShow(DialogInterface dialogInterface) {
        ChatActivity.lambda$null$30$ChatActivity(this.arg$1, dialogInterface);
    }
}
