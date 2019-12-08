package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.AndroidUtilities.VcardItem;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhonebookShareAlert$hhDW6LcXTbjZeBc7t6WBw5vGqm0 implements OnClickListener {
    private final /* synthetic */ PhonebookShareAlert f$0;
    private final /* synthetic */ VcardItem f$1;

    public /* synthetic */ -$$Lambda$PhonebookShareAlert$hhDW6LcXTbjZeBc7t6WBw5vGqm0(PhonebookShareAlert phonebookShareAlert, VcardItem vcardItem) {
        this.f$0 = phonebookShareAlert;
        this.f$1 = vcardItem;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$1$PhonebookShareAlert(this.f$1, dialogInterface, i);
    }
}
