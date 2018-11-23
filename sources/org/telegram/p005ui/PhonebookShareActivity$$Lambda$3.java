package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.AndroidUtilities.VcardItem;

/* renamed from: org.telegram.ui.PhonebookShareActivity$$Lambda$3 */
final /* synthetic */ class PhonebookShareActivity$$Lambda$3 implements OnClickListener {
    private final PhonebookShareActivity arg$1;
    private final VcardItem arg$2;

    PhonebookShareActivity$$Lambda$3(PhonebookShareActivity phonebookShareActivity, VcardItem vcardItem) {
        this.arg$1 = phonebookShareActivity;
        this.arg$2 = vcardItem;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$2$PhonebookShareActivity(this.arg$2, dialogInterface, i);
    }
}
