package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda99 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$TL_contacts_resolvedPeer f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda99(LaunchActivity launchActivity, String str, int i, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = tLRPC$TL_contacts_resolvedPeer;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$runLinkRequest$37(this.f$1, this.f$2, this.f$3, dialogsActivity, arrayList, charSequence, z);
    }
}
