package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda7 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda7(GroupCallActivity groupCallActivity, TLObject tLObject) {
        this.f$0 = groupCallActivity;
        this.f$1 = tLObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processSelectedOption$54(this.f$1, dialogInterface, i);
    }
}
