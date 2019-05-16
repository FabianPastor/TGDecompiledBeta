package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsSettingsActivity$9FhV71oy8_vyXyR3LWFGjX-RReE implements OnClickListener {
    private final /* synthetic */ NotificationsSettingsActivity f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$NotificationsSettingsActivity$9FhV71oy8_vyXyR3LWFGjX-RReE(NotificationsSettingsActivity notificationsSettingsActivity, ArrayList arrayList) {
        this.f$0 = notificationsSettingsActivity;
        this.f$1 = arrayList;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showExceptionsAlert$9$NotificationsSettingsActivity(this.f$1, dialogInterface, i);
    }
}
