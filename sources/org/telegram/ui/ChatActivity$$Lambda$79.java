package org.telegram.ui;

import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;

final /* synthetic */ class ChatActivity$$Lambda$79 implements OnDateSetListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$79(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        this.arg$1.lambda$null$28$ChatActivity(datePicker, i, i2, i3);
    }
}
