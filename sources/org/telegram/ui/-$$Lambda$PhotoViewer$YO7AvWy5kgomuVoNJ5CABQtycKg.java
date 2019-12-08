package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$YO7AvWy5kgomuVoNJ5CABQtycKg implements OnClickListener {
    private final /* synthetic */ PhotoViewer f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ User f$2;

    public /* synthetic */ -$$Lambda$PhotoViewer$YO7AvWy5kgomuVoNJ5CABQtycKg(PhotoViewer photoViewer, int i, User user) {
        this.f$0 = photoViewer;
        this.f$1 = i;
        this.f$2 = user;
    }

    public final void onClick(View view) {
        this.f$0.lambda$null$7$PhotoViewer(this.f$1, this.f$2, view);
    }
}
