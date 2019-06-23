package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogOrContactPickerActivity$XGjY0fzix6uDjlZVM_E2r47WlnA implements OnClickListener {
    private final /* synthetic */ DialogOrContactPickerActivity f$0;
    private final /* synthetic */ User f$1;

    public /* synthetic */ -$$Lambda$DialogOrContactPickerActivity$XGjY0fzix6uDjlZVM_E2r47WlnA(DialogOrContactPickerActivity dialogOrContactPickerActivity, User user) {
        this.f$0 = dialogOrContactPickerActivity;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showBlockAlert$3$DialogOrContactPickerActivity(this.f$1, dialogInterface, i);
    }
}
