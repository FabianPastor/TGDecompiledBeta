package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$DLRz0ef3Tin9oFNJgoDm_d_egpU implements OnClickListener {
    private final /* synthetic */ ChatActivityEnterView f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ User f$2;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$DLRz0ef3Tin9oFNJgoDm_d_egpU(ChatActivityEnterView chatActivityEnterView, int i, User user) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = i;
        this.f$2 = user;
    }

    public final void onClick(View view) {
        this.f$0.lambda$onSendLongClick$21$ChatActivityEnterView(this.f$1, this.f$2, view);
    }
}
