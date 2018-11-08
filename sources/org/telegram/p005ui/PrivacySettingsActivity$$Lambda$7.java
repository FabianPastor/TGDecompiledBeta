package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.Cells.TextCheckCell;

/* renamed from: org.telegram.ui.PrivacySettingsActivity$$Lambda$7 */
final /* synthetic */ class PrivacySettingsActivity$$Lambda$7 implements OnClickListener {
    private final PrivacySettingsActivity arg$1;
    private final TextCheckCell arg$2;

    PrivacySettingsActivity$$Lambda$7(PrivacySettingsActivity privacySettingsActivity, TextCheckCell textCheckCell) {
        this.arg$1 = privacySettingsActivity;
        this.arg$2 = textCheckCell;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$11$PrivacySettingsActivity(this.arg$2, dialogInterface, i);
    }
}
