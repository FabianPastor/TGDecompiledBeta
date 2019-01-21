package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$2 implements OnKeyListener {
    private final PhoneConfirmationView arg$1;
    private final int arg$2;

    PassportActivity$PhoneConfirmationView$$Lambda$2(PhoneConfirmationView phoneConfirmationView, int i) {
        this.arg$1 = phoneConfirmationView;
        this.arg$2 = i;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$setParams$4$PassportActivity$PhoneConfirmationView(this.arg$2, view, i, keyEvent);
    }
}
