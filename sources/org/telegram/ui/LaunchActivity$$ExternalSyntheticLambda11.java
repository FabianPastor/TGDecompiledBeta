package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda11 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda11(LaunchActivity launchActivity, long j, int i, TLRPC$User tLRPC$User, String str) {
        this.f$0 = launchActivity;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = tLRPC$User;
        this.f$4 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$runLinkRequest$41(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
