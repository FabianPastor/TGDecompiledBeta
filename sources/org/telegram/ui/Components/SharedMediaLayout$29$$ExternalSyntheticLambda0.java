package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$29$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ SharedMediaLayout.AnonymousClass29 f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ SharedMediaLayout$29$$ExternalSyntheticLambda0(SharedMediaLayout.AnonymousClass29 r1, String str) {
        this.f$0 = r1;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onLinkPress$0(this.f$1, dialogInterface, i);
    }
}
