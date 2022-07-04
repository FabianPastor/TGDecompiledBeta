package org.telegram.ui.Components.Premium;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class VideoScreenPreview$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ VideoScreenPreview f$0;
    public final /* synthetic */ TLRPC$Document f$1;

    public /* synthetic */ VideoScreenPreview$$ExternalSyntheticLambda1(VideoScreenPreview videoScreenPreview, TLRPC$Document tLRPC$Document) {
        this.f$0 = videoScreenPreview;
        this.f$1 = tLRPC$Document;
    }

    public final void run() {
        this.f$0.lambda$setVideo$1(this.f$1);
    }
}
