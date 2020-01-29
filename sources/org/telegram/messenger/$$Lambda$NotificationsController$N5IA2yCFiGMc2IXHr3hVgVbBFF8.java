package org.telegram.messenger;

import android.graphics.ImageDecoder;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8 implements ImageDecoder.OnHeaderDecodedListener {
    public static final /* synthetic */ $$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8 INSTANCE = new $$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8();

    private /* synthetic */ $$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8() {
    }

    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor($$Lambda$NotificationsController$wGfyzcvvHxFIxbrke7bSnOwTfcM.INSTANCE);
    }
}
