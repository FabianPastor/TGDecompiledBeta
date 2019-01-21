package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;

final /* synthetic */ class TextPaintImageReceiverSpan$$Lambda$0 implements ImageReceiverDelegate {
    static final ImageReceiverDelegate $instance = new TextPaintImageReceiverSpan$$Lambda$0();

    private TextPaintImageReceiverSpan$$Lambda$0() {
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        TextPaintImageReceiverSpan.lambda$new$0$TextPaintImageReceiverSpan(imageReceiver, z, z2);
    }
}
