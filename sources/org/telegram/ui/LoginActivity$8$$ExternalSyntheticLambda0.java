package org.telegram.ui;

import android.widget.EditText;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$8$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ LoginActivity.AnonymousClass8 f$0;
    public final /* synthetic */ EditText f$1;
    public final /* synthetic */ AtomicReference f$2;

    public /* synthetic */ LoginActivity$8$$ExternalSyntheticLambda0(LoginActivity.AnonymousClass8 r1, EditText editText, AtomicReference atomicReference) {
        this.f$0 = r1;
        this.f$1 = editText;
        this.f$2 = atomicReference;
    }

    public final void run() {
        this.f$0.m2461lambda$beforeTextChanged$0$orgtelegramuiLoginActivity$8(this.f$1, this.f$2);
    }
}
