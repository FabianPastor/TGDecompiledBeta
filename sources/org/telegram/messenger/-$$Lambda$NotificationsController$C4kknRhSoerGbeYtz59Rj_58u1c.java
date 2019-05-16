package org.telegram.messenger;

import android.graphics.ImageDecoder;
import android.graphics.ImageDecoder.ImageInfo;
import android.graphics.ImageDecoder.OnHeaderDecodedListener;
import android.graphics.ImageDecoder.Source;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsController$C4kknRhSoerGbeYtz59Rj_58u1c implements OnHeaderDecodedListener {
    public static final /* synthetic */ -$$Lambda$NotificationsController$C4kknRhSoerGbeYtz59Rj_58u1c INSTANCE = new -$$Lambda$NotificationsController$C4kknRhSoerGbeYtz59Rj_58u1c();

    private /* synthetic */ -$$Lambda$NotificationsController$C4kknRhSoerGbeYtz59Rj_58u1c() {
    }

    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageInfo imageInfo, Source source) {
        imageDecoder.setPostProcessor(-$$Lambda$NotificationsController$cgrojPFOVxdZFVks3HRO6Y4mkwg.INSTANCE);
    }
}
