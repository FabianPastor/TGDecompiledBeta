package org.telegram.messenger;

import android.graphics.ImageDecoder;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$H-OFBTiN83VP4fbqp0h5IBRTz-A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$HOFBTiN83VP4fbqp0h5IBRTzA implements ImageDecoder.OnHeaderDecodedListener {
    public static final /* synthetic */ $$Lambda$NotificationsController$HOFBTiN83VP4fbqp0h5IBRTzA INSTANCE = new $$Lambda$NotificationsController$HOFBTiN83VP4fbqp0h5IBRTzA();

    private /* synthetic */ $$Lambda$NotificationsController$HOFBTiN83VP4fbqp0h5IBRTzA() {
    }

    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor($$Lambda$NotificationsController$EtYrAcpdphRORF8igfUpieFQt2I.INSTANCE);
    }
}
