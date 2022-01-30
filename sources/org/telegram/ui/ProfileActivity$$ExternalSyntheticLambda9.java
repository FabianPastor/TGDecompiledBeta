package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda9 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$User f$2;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda9(ProfileActivity profileActivity, ArrayList arrayList, TLRPC$User tLRPC$User) {
        this.f$0 = profileActivity;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$User;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processOnClickOrPress$21(this.f$1, this.f$2, dialogInterface, i);
    }
}
