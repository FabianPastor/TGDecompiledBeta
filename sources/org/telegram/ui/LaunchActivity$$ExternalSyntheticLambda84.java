package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda84 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda84(LaunchActivity launchActivity, int i, TLRPC$User tLRPC$User, String str) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = tLRPC$User;
        this.f$3 = str;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$runLinkRequest$28(this.f$1, this.f$2, this.f$3, dialogsActivity, arrayList, charSequence, z);
    }
}
