package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda2 implements DialogInterface.OnShowListener {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda2(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda16(this.f$0));
    }
}
