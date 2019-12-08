package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatAttachAlert$YOukuNzr6sOu9UxKKjWHswPV7Us implements OnClickListener {
    private final /* synthetic */ ChatAttachAlert f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ User f$2;

    public /* synthetic */ -$$Lambda$ChatAttachAlert$YOukuNzr6sOu9UxKKjWHswPV7Us(ChatAttachAlert chatAttachAlert, int i, User user) {
        this.f$0 = chatAttachAlert;
        this.f$1 = i;
        this.f$2 = user;
    }

    public final void onClick(View view) {
        this.f$0.lambda$null$11$ChatAttachAlert(this.f$1, this.f$2, view);
    }
}
