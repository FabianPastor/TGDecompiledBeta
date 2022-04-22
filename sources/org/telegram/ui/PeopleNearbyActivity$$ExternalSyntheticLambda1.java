package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.UserConfig;

public final /* synthetic */ class PeopleNearbyActivity$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PeopleNearbyActivity f$0;
    public final /* synthetic */ UserConfig f$1;

    public /* synthetic */ PeopleNearbyActivity$$ExternalSyntheticLambda1(PeopleNearbyActivity peopleNearbyActivity, UserConfig userConfig) {
        this.f$0 = peopleNearbyActivity;
        this.f$1 = userConfig;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createView$1(this.f$1, dialogInterface, i);
    }
}
