package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.ChangePhoneActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$UkKWW40jg7mpE6ErNm6_eukinew implements OnKeyListener {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ChangePhoneActivity$LoginActivitySmsView$UkKWW40jg7mpE6ErNm6_eukinew(LoginActivitySmsView loginActivitySmsView, int i) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = i;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParams$4$ChangePhoneActivity$LoginActivitySmsView(this.f$1, view, i, keyEvent);
    }
}
