package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda22 implements DialogInterface.OnShowListener {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda22(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new StickersAlert$$ExternalSyntheticLambda6(this.f$0));
    }
}
