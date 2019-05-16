package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivitySmsView$W4f4bbr6ANrn17O_fi8CZ_C8uvk implements OnKeyListener {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivitySmsView$W4f4bbr6ANrn17O_fi8CZ_C8uvk(LoginActivitySmsView loginActivitySmsView, int i) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = i;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParams$3$LoginActivity$LoginActivitySmsView(this.f$1, view, i, keyEvent);
    }
}
