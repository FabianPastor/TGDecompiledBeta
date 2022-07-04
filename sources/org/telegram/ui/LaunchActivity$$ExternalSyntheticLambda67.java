package org.telegram.ui;

import android.net.Uri;
import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda67 implements MessagesStorage.LongCallback {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ DialogsActivity f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ Uri f$5;
    public final /* synthetic */ AlertDialog f$6;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda67(LaunchActivity launchActivity, int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = dialogsActivity;
        this.f$3 = z;
        this.f$4 = arrayList;
        this.f$5 = uri;
        this.f$6 = alertDialog;
    }

    public final void run(long j) {
        this.f$0.lambda$didSelectDialogs$76(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, j);
    }
}
