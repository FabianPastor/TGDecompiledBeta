package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.ui.ChannelCreateActivity.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChannelCreateActivity$1$q1TLoAihH5rBozhaNhmW5QGac-s implements OnCancelListener {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ChannelCreateActivity$1$q1TLoAihH5rBozhaNhmW5QGac-s(AnonymousClass1 anonymousClass1, int i) {
        this.f$0 = anonymousClass1;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$onItemClick$1$ChannelCreateActivity$1(this.f$1, dialogInterface);
    }
}
