package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.Cells.TextCheckCell;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ TextCheckCell f$1;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda4(PrivacySettingsActivity privacySettingsActivity, TextCheckCell textCheckCell) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = textCheckCell;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createView$11(this.f$1, dialogInterface, i);
    }
}
