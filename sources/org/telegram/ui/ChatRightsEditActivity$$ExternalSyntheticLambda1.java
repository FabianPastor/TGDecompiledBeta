package org.telegram.ui;

import android.app.TimePickerDialog;
import android.widget.TimePicker;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda1 implements TimePickerDialog.OnTimeSetListener {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda1(ChatRightsEditActivity chatRightsEditActivity, int i) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = i;
    }

    public final void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.f$0.lambda$createView$0(this.f$1, timePicker, i, i2);
    }
}
