package org.telegram.ui;

import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;

final /* synthetic */ class ChatRightsEditActivity$$Lambda$6 implements OnDateSetListener {
    private final ChatRightsEditActivity arg$1;

    ChatRightsEditActivity$$Lambda$6(ChatRightsEditActivity chatRightsEditActivity) {
        this.arg$1 = chatRightsEditActivity;
    }

    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        this.arg$1.lambda$null$2$ChatRightsEditActivity(datePicker, i, i2, i3);
    }
}
