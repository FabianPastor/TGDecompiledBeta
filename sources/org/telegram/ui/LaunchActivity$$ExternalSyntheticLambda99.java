package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda99 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda99(LaunchActivity launchActivity, TLRPC$User tLRPC$User, String str, int i) {
        this.f$0 = launchActivity;
        this.f$1 = tLRPC$User;
        this.f$2 = str;
        this.f$3 = i;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$runLinkRequest$31(this.f$1, this.f$2, this.f$3, dialogsActivity, arrayList, charSequence, z);
    }
}
