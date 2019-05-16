package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.Cells.TextCheckCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacySettingsActivity$5uWMtQO-U72VaNWgF2qczAQ3ehw implements OnClickListener {
    private final /* synthetic */ PrivacySettingsActivity f$0;
    private final /* synthetic */ TextCheckCell f$1;

    public /* synthetic */ -$$Lambda$PrivacySettingsActivity$5uWMtQO-U72VaNWgF2qczAQ3ehw(PrivacySettingsActivity privacySettingsActivity, TextCheckCell textCheckCell) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = textCheckCell;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$11$PrivacySettingsActivity(this.f$1, dialogInterface, i);
    }
}
