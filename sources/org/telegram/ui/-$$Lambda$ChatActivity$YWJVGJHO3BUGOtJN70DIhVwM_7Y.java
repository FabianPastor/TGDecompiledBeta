package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$YWJVGJHO3BUGOtJN70DIhVwM_7Y implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$YWJVGJHO3BUGOtJN70DIhVwM_7Y(ChatActivity chatActivity, int i, ArrayList arrayList) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createMenu$74$ChatActivity(this.f$1, this.f$2, view);
    }
}