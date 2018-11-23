package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.ProxySettingsActivity$$Lambda$1 */
final /* synthetic */ class ProxySettingsActivity$$Lambda$1 implements OnEditorActionListener {
    private final ProxySettingsActivity arg$1;

    ProxySettingsActivity$$Lambda$1(ProxySettingsActivity proxySettingsActivity) {
        this.arg$1 = proxySettingsActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$1$ProxySettingsActivity(textView, i, keyEvent);
    }
}
