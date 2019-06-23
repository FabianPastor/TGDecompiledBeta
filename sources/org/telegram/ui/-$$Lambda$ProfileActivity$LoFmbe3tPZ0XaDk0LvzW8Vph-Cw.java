package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$LoFmbe3tPZ0XaDk0LvzW8Vph-Cw implements OnClickListener {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ User f$2;

    public /* synthetic */ -$$Lambda$ProfileActivity$LoFmbe3tPZ0XaDk0LvzW8Vph-Cw(ProfileActivity profileActivity, ArrayList arrayList, User user) {
        this.f$0 = profileActivity;
        this.f$1 = arrayList;
        this.f$2 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processOnClickOrPress$13$ProfileActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
