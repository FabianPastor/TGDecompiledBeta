package org.telegram.ui.Components;

import android.view.View;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class JoinCallAlert$$ExternalSyntheticLambda8 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ JoinCallAlert f$0;
    public final /* synthetic */ TLRPC$Chat f$1;

    public /* synthetic */ JoinCallAlert$$ExternalSyntheticLambda8(JoinCallAlert joinCallAlert, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = joinCallAlert;
        this.f$1 = tLRPC$Chat;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$6(this.f$1, view, i);
    }
}
