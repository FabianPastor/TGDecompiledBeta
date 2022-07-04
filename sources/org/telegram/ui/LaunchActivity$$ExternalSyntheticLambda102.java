package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda102 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_contacts_resolvedPeer f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda102(LaunchActivity launchActivity, String str, int i, TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = tL_contacts_resolvedPeer;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.m3669lambda$runLinkRequest$37$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, dialogsActivity, arrayList, charSequence, z);
    }
}
