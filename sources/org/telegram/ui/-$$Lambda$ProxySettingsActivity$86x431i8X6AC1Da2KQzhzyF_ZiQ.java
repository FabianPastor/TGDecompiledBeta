package org.telegram.ui;

import android.content.ClipboardManager.OnPrimaryClipChangedListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProxySettingsActivity$86x431i8X6AC1Da2KQzhzyF_ZiQ implements OnPrimaryClipChangedListener {
    private final /* synthetic */ ProxySettingsActivity f$0;

    public /* synthetic */ -$$Lambda$ProxySettingsActivity$86x431i8X6AC1Da2KQzhzyF_ZiQ(ProxySettingsActivity proxySettingsActivity) {
        this.f$0 = proxySettingsActivity;
    }

    public final void onPrimaryClipChanged() {
        this.f$0.updatePasteCell();
    }
}
