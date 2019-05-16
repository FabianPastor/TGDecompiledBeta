package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$u-E5fg1gn1kuY6fPnql7A5bYvUE implements DialogsActivityDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TL_contacts_resolvedPeer f$3;

    public /* synthetic */ -$$Lambda$LaunchActivity$u-E5fg1gn1kuY6fPnql7A5bYvUE(LaunchActivity launchActivity, String str, int i, TL_contacts_resolvedPeer tL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = tL_contacts_resolvedPeer;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$null$9$LaunchActivity(this.f$1, this.f$2, this.f$3, dialogsActivity, arrayList, charSequence, z);
    }
}
