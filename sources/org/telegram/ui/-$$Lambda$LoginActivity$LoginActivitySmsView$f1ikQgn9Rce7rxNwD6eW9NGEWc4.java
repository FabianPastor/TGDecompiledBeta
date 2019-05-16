package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LoginActivity$LoginActivitySmsView$f1ikQgn9Rce7rxNwD6eW9NGEWc4 implements OnEditorActionListener {
    private final /* synthetic */ LoginActivitySmsView f$0;

    public /* synthetic */ -$$Lambda$LoginActivity$LoginActivitySmsView$f1ikQgn9Rce7rxNwD6eW9NGEWc4(LoginActivitySmsView loginActivitySmsView) {
        this.f$0 = loginActivitySmsView;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParams$4$LoginActivity$LoginActivitySmsView(textView, i, keyEvent);
    }
}
