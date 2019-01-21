package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.DatePicker;

final /* synthetic */ class ChatRightsEditActivity$$Lambda$8 implements OnShowListener {
    private final DatePicker arg$1;

    ChatRightsEditActivity$$Lambda$8(DatePicker datePicker) {
        this.arg$1 = datePicker;
    }

    public void onShow(DialogInterface dialogInterface) {
        ChatRightsEditActivity.lambda$null$4$ChatRightsEditActivity(this.arg$1, dialogInterface);
    }
}
