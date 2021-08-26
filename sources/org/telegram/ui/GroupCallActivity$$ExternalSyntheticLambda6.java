package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda6 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda6(GroupCallActivity groupCallActivity, TLRPC$User tLRPC$User, int i) {
        this.f$0 = groupCallActivity;
        this.f$1 = tLRPC$User;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processSelectedOption$54(this.f$1, this.f$2, dialogInterface, i);
    }
}
