package org.telegram.p005ui.Components;

import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;

/* renamed from: org.telegram.ui.Components.TextPaintImageReceiverSpan$$Lambda$0 */
final /* synthetic */ class TextPaintImageReceiverSpan$$Lambda$0 implements ImageReceiverDelegate {
    static final ImageReceiverDelegate $instance = new TextPaintImageReceiverSpan$$Lambda$0();

    private TextPaintImageReceiverSpan$$Lambda$0() {
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        TextPaintImageReceiverSpan.lambda$new$0$TextPaintImageReceiverSpan(imageReceiver, z, z2);
    }
}
