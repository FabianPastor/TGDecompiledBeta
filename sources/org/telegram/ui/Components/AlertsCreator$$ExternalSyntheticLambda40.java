package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda40 implements DialogInterface.OnShowListener {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda40(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new AlertsCreator$$ExternalSyntheticLambda75(this.f$0));
    }
}
