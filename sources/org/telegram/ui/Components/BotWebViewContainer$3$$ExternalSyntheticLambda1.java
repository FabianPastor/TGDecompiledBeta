package org.telegram.ui.Components;

import android.webkit.PermissionRequest;
import androidx.core.util.Consumer;
import org.telegram.ui.Components.BotWebViewContainer;

public final /* synthetic */ class BotWebViewContainer$3$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ PermissionRequest f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ BotWebViewContainer$3$$ExternalSyntheticLambda1(PermissionRequest permissionRequest, String str) {
        this.f$0 = permissionRequest;
        this.f$1 = str;
    }

    public final void accept(Object obj) {
        BotWebViewContainer.AnonymousClass3.lambda$onPermissionRequest$4(this.f$0, this.f$1, (Boolean) obj);
    }
}
