package org.telegram.ui.Components;

import android.webkit.GeolocationPermissions;
import androidx.core.util.Consumer;
import org.telegram.ui.Components.BotWebViewContainer;

public final /* synthetic */ class BotWebViewContainer$4$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ BotWebViewContainer.AnonymousClass4 f$0;
    public final /* synthetic */ GeolocationPermissions.Callback f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ BotWebViewContainer$4$$ExternalSyntheticLambda1(BotWebViewContainer.AnonymousClass4 r1, GeolocationPermissions.Callback callback, String str) {
        this.f$0 = r1;
        this.f$1 = callback;
        this.f$2 = str;
    }

    public final void accept(Object obj) {
        this.f$0.m587x53986a9a(this.f$1, this.f$2, (Boolean) obj);
    }
}
