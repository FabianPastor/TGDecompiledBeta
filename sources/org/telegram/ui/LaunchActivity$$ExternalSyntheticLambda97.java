package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda97 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ DialogsActivity f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda97(LaunchActivity launchActivity, int i, TLRPC$User tLRPC$User, String str, String str2, DialogsActivity dialogsActivity) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = tLRPC$User;
        this.f$3 = str;
        this.f$4 = str2;
        this.f$5 = dialogsActivity;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$runLinkRequest$42(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogsActivity, arrayList, charSequence, z);
    }
}
