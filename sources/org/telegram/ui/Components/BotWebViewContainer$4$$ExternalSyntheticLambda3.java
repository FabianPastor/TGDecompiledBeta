package org.telegram.ui.Components;

import android.webkit.PermissionRequest;
import androidx.core.util.Consumer;
import org.telegram.ui.Components.BotWebViewContainer;

public final /* synthetic */ class BotWebViewContainer$4$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ BotWebViewContainer.AnonymousClass4 f$0;
    public final /* synthetic */ PermissionRequest f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ BotWebViewContainer$4$$ExternalSyntheticLambda3(BotWebViewContainer.AnonymousClass4 r1, PermissionRequest permissionRequest, String str) {
        this.f$0 = r1;
        this.f$1 = permissionRequest;
        this.f$2 = str;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onPermissionRequest$4(this.f$1, this.f$2, (Boolean) obj);
    }
}