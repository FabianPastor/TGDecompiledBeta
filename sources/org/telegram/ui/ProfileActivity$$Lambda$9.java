package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ProfileActivity$$Lambda$9 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final ArrayList arg$2;
    private final User arg$3;

    ProfileActivity$$Lambda$9(ProfileActivity profileActivity, ArrayList arrayList, User user) {
        this.arg$1 = profileActivity;
        this.arg$2 = arrayList;
        this.arg$3 = user;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processOnClickOrPress$17$ProfileActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
