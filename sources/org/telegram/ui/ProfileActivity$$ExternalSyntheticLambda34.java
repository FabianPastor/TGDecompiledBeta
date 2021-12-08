package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda34 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda34(ProfileActivity profileActivity, ArrayList arrayList, TLRPC.User user) {
        this.f$0 = profileActivity;
        this.f$1 = arrayList;
        this.f$2 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3714lambda$processOnClickOrPress$19$orgtelegramuiProfileActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
