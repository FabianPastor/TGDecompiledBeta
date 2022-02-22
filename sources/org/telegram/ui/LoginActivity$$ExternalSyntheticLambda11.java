package org.telegram.ui;

import android.text.TextWatcher;
import android.widget.EditText;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ EditText f$0;
    public final /* synthetic */ TextWatcher f$1;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda11(EditText editText, TextWatcher textWatcher) {
        this.f$0 = editText;
        this.f$1 = textWatcher;
    }

    public final void run() {
        this.f$0.removeTextChangedListener(this.f$1);
    }
}
