package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$ImageLoader$HttpImageTask$XSVT2vTgbXXcvgcr720eEC0zjMc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ImageLoader$HttpImageTask$XSVT2vTgbXXcvgcr720eEC0zjMc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ImageLoader$HttpImageTask$XSVT2vTgbXXcvgcr720eEC0zjMc INSTANCE = new $$Lambda$ImageLoader$HttpImageTask$XSVT2vTgbXXcvgcr720eEC0zjMc();

    private /* synthetic */ $$Lambda$ImageLoader$HttpImageTask$XSVT2vTgbXXcvgcr720eEC0zjMc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ImageLoader.HttpImageTask.lambda$doInBackground$2(tLObject, tLRPC$TL_error);
    }
}
