package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangeUsernameActivity$gFWNPcfawPg-VyQnhopqwvS2g8Q implements OnCancelListener {
    private final /* synthetic */ ChangeUsernameActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ChangeUsernameActivity$gFWNPcfawPg-VyQnhopqwvS2g8Q(ChangeUsernameActivity changeUsernameActivity, int i) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$saveName$8$ChangeUsernameActivity(this.f$1, dialogInterface);
    }
}
