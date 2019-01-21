package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.AndroidUtilities.VcardItem;

final /* synthetic */ class PhonebookShareActivity$$Lambda$4 implements OnClickListener {
    private final PhonebookShareActivity arg$1;
    private final VcardItem arg$2;

    PhonebookShareActivity$$Lambda$4(PhonebookShareActivity phonebookShareActivity, VcardItem vcardItem) {
        this.arg$1 = phonebookShareActivity;
        this.arg$2 = vcardItem;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$PhonebookShareActivity(this.arg$2, dialogInterface, i);
    }
}
