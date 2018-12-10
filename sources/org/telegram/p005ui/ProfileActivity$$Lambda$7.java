package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$7 */
final /* synthetic */ class ProfileActivity$$Lambda$7 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final String arg$2;

    ProfileActivity$$Lambda$7(ProfileActivity profileActivity, String str) {
        this.arg$1 = profileActivity;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processOnClickOrPress$14$ProfileActivity(this.arg$2, dialogInterface, i);
    }
}
