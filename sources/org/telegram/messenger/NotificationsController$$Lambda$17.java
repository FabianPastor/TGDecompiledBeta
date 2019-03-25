package org.telegram.messenger;

import android.graphics.ImageDecoder;
import android.graphics.ImageDecoder.ImageInfo;
import android.graphics.ImageDecoder.OnHeaderDecodedListener;
import android.graphics.ImageDecoder.Source;

final /* synthetic */ class NotificationsController$$Lambda$17 implements OnHeaderDecodedListener {
    static final OnHeaderDecodedListener $instance = new NotificationsController$$Lambda$17();

    private NotificationsController$$Lambda$17() {
    }

    public void onHeaderDecoded(ImageDecoder imageDecoder, ImageInfo imageInfo, Source source) {
        imageDecoder.setPostProcessor(NotificationsController$$Lambda$23.$instance);
    }
}
