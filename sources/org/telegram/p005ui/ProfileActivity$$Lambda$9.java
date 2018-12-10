package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$9 */
final /* synthetic */ class ProfileActivity$$Lambda$9 implements OnClickListener {
    private final ProfileActivity arg$1;
    private final int arg$2;

    ProfileActivity$$Lambda$9(ProfileActivity profileActivity, int i) {
        this.arg$1 = profileActivity;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processOnClickOrPress$16$ProfileActivity(this.arg$2, dialogInterface, i);
    }
}
