package org.telegram.ui;

import java.io.File;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ boolean[] f$2;
    public final /* synthetic */ File f$3;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda22(ProfileActivity profileActivity, AlertDialog alertDialog, boolean[] zArr, File file) {
        this.f$0 = profileActivity;
        this.f$1 = alertDialog;
        this.f$2 = zArr;
        this.f$3 = file;
    }

    public final void run() {
        this.f$0.lambda$sendLogs$32(this.f$1, this.f$2, this.f$3);
    }
}
