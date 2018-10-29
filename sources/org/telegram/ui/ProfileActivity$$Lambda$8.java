package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ProfileActivity$$Lambda$8 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final String arg$2;

    ProfileActivity$$Lambda$8(ProfileActivity profileActivity, String str) {
        this.arg$1 = profileActivity;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processOnClickOrPress$16$ProfileActivity(this.arg$2, dialogInterface, i);
    }
}
