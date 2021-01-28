package org.telegram.messenger;

import android.graphics.ImageDecoder;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$VHgcplzGmBaKtRgRg0kOeCk4Zpw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$VHgcplzGmBaKtRgRg0kOeCk4Zpw implements ImageDecoder.OnHeaderDecodedListener {
    public static final /* synthetic */ $$Lambda$NotificationsController$VHgcplzGmBaKtRgRg0kOeCk4Zpw INSTANCE = new $$Lambda$NotificationsController$VHgcplzGmBaKtRgRg0kOeCk4Zpw();

    private /* synthetic */ $$Lambda$NotificationsController$VHgcplzGmBaKtRgRg0kOeCk4Zpw() {
    }

    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor($$Lambda$NotificationsController$ZEerbecqXOslj75wKfZ4bWRpnF0.INSTANCE);
    }
}
