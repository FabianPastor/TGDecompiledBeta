package org.webrtc;

import java.nio.ByteBuffer;

public final /* synthetic */ class TextureBufferImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ByteBuffer f$0;

    public /* synthetic */ TextureBufferImpl$$ExternalSyntheticLambda0(ByteBuffer byteBuffer) {
        this.f$0 = byteBuffer;
    }

    public final void run() {
        JniCommon.nativeFreeByteBuffer(this.f$0);
    }
}