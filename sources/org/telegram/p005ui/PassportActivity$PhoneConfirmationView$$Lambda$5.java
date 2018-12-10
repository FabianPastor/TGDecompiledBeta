package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.PassportActivity.PhoneConfirmationView;

/* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$$Lambda$5 */
final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$5 implements OnClickListener {
    private final PhoneConfirmationView arg$1;

    PassportActivity$PhoneConfirmationView$$Lambda$5(PhoneConfirmationView phoneConfirmationView) {
        this.arg$1 = phoneConfirmationView;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onBackPressed$8$PassportActivity$PhoneConfirmationView(dialogInterface, i);
    }
}
