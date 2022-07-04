package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class PhonebookShareAlert$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PhonebookShareAlert f$0;
    public final /* synthetic */ AndroidUtilities.VcardItem f$1;

    public /* synthetic */ PhonebookShareAlert$$ExternalSyntheticLambda0(PhonebookShareAlert phonebookShareAlert, AndroidUtilities.VcardItem vcardItem) {
        this.f$0 = phonebookShareAlert;
        this.f$1 = vcardItem;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1159lambda$new$1$orgtelegramuiComponentsPhonebookShareAlert(this.f$1, dialogInterface, i);
    }
}
