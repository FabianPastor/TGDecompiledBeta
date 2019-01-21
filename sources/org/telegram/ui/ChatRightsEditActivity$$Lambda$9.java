package org.telegram.ui;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.TimePicker;

final /* synthetic */ class ChatRightsEditActivity$$Lambda$9 implements OnTimeSetListener {
    private final ChatRightsEditActivity arg$1;
    private final int arg$2;

    ChatRightsEditActivity$$Lambda$9(ChatRightsEditActivity chatRightsEditActivity, int i) {
        this.arg$1 = chatRightsEditActivity;
        this.arg$2 = i;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.arg$1.lambda$null$0$ChatRightsEditActivity(this.arg$2, timePicker, i, i2);
    }
}
