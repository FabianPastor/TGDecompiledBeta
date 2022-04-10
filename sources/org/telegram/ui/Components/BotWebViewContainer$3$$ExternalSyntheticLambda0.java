package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.net.Uri;
import org.telegram.ui.Components.BotWebViewContainer;

public final /* synthetic */ class BotWebViewContainer$3$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ BotWebViewContainer.AnonymousClass3 f$0;
    public final /* synthetic */ Uri f$1;

    public /* synthetic */ BotWebViewContainer$3$$ExternalSyntheticLambda0(BotWebViewContainer.AnonymousClass3 r1, Uri uri) {
        this.f$0 = r1;
        this.f$1 = uri;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$shouldOverrideUrlLoading$0(this.f$1, dialogInterface, i);
    }
}
