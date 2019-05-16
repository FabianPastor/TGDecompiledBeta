package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$aMVVQ8fOnltbn3fHM_guwcH1dhE implements OnItemClickListener {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$aMVVQ8fOnltbn3fHM_guwcH1dhE(ChatRightsEditActivity chatRightsEditActivity, Context context) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = context;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$6$ChatRightsEditActivity(this.f$1, view, i);
    }
}
