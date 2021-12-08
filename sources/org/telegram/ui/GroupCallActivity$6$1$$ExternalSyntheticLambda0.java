package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

public final /* synthetic */ class GroupCallActivity$6$1$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ GroupCallActivity$6$1$$ExternalSyntheticLambda0(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AndroidUtilities.hideKeyboard(this.f$0);
    }
}
