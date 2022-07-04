package org.telegram.ui;

import android.app.TimePickerDialog;
import android.widget.TimePicker;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda20 implements TimePickerDialog.OnTimeSetListener {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda20(ChatRightsEditActivity chatRightsEditActivity, int i) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = i;
    }

    public final void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.f$0.m3274lambda$createView$0$orgtelegramuiChatRightsEditActivity(this.f$1, timePicker, i, i2);
    }
}
