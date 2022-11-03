package org.webrtc;

import android.media.MediaCodecInfo;
import java.util.Comparator;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaCodecUtils$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ MediaCodecUtils$$ExternalSyntheticLambda0 INSTANCE = new MediaCodecUtils$$ExternalSyntheticLambda0();

    private /* synthetic */ MediaCodecUtils$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getSortedCodecsList$0;
        lambda$getSortedCodecsList$0 = MediaCodecUtils.lambda$getSortedCodecsList$0((MediaCodecInfo) obj, (MediaCodecInfo) obj2);
        return lambda$getSortedCodecsList$0;
    }
}
