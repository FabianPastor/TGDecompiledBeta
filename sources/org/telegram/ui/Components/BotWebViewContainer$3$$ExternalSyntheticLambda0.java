package org.telegram.ui.Components;

import android.webkit.GeolocationPermissions;
import androidx.core.util.Consumer;
import org.telegram.ui.Components.BotWebViewContainer;

public final /* synthetic */ class BotWebViewContainer$3$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ GeolocationPermissions.Callback f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ BotWebViewContainer$3$$ExternalSyntheticLambda0(GeolocationPermissions.Callback callback, String str) {
        this.f$0 = callback;
        this.f$1 = str;
    }

    public final void accept(Object obj) {
        BotWebViewContainer.AnonymousClass3.lambda$onGeolocationPermissionsShowPrompt$0(this.f$0, this.f$1, (Boolean) obj);
    }
}
