package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogOrContactPickerActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ DialogOrContactPickerActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ DialogOrContactPickerActivity$$ExternalSyntheticLambda0(DialogOrContactPickerActivity dialogOrContactPickerActivity, TLRPC.User user) {
        this.f$0 = dialogOrContactPickerActivity;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3369x7CLASSNAMECLASSNAME(this.f$1, dialogInterface, i);
    }
}
