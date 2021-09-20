package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda0(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AndroidUtilities.hideKeyboard(this.f$0);
    }
}
