package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$_jaa_bD76RyBYqeandF7XoujxXI implements OnClickListener {
    private final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ -$$Lambda$PhotoViewer$_jaa_bD76RyBYqeandF7XoujxXI(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$checkInlinePermissions$32$PhotoViewer(dialogInterface, i);
    }
}
