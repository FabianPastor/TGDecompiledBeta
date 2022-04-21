package org.telegram.ui;

import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import org.telegram.ui.Components.OutlineTextContainerView;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ OutlineTextContainerView f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ EditText f$2;
    public final /* synthetic */ TextWatcher f$3;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda4(OutlineTextContainerView outlineTextContainerView, View view, EditText editText, TextWatcher textWatcher) {
        this.f$0 = outlineTextContainerView;
        this.f$1 = view;
        this.f$2 = editText;
        this.f$3 = textWatcher;
    }

    public final void run() {
        LoginActivity.lambda$onFieldError$9(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
