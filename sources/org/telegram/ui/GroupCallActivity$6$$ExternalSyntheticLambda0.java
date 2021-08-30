package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda0(VoIPService voIPService, ArrayList arrayList) {
        this.f$0 = voIPService;
        this.f$1 = arrayList;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        GroupCallActivity.AnonymousClass6.lambda$onItemClick$15(this.f$0, this.f$1, dialogInterface, i);
    }
}
