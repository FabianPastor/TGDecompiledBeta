package org.telegram.messenger;

import android.graphics.ImageDecoder;
import android.graphics.ImageDecoder.ImageInfo;
import android.graphics.ImageDecoder.OnHeaderDecodedListener;
import android.graphics.ImageDecoder.Source;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8 implements OnHeaderDecodedListener {
    public static final /* synthetic */ -$$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8 INSTANCE = new -$$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8();

    private /* synthetic */ -$$Lambda$NotificationsController$N5IA2yCFiGMc2IXHr3hVgVbBFF8() {
    }

    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageInfo imageInfo, Source source) {
        imageDecoder.setPostProcessor(-$$Lambda$NotificationsController$wGfyzcvvHxFIxbrke7bSnOwTfcM.INSTANCE);
    }
}
