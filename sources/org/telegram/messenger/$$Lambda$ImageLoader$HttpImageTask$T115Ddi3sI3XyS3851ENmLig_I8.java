package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8 INSTANCE = new $$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8();

    private /* synthetic */ $$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ImageLoader.HttpImageTask.lambda$doInBackground$2(tLObject, tL_error);
    }
}
