package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda1 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda1(LaunchActivity launchActivity, TLRPC.User user, String str, int i) {
        this.f$0 = launchActivity;
        this.f$1 = user;
        this.f$2 = str;
        this.f$3 = i;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.m3663lambda$runLinkRequest$31$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, dialogsActivity, arrayList, charSequence, z);
    }
}
