package org.telegram.messenger;

import org.telegram.messenger.ImageLoader;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class ImageLoader$HttpImageTask$$ExternalSyntheticLambda8 implements RequestDelegate {
    public static final /* synthetic */ ImageLoader$HttpImageTask$$ExternalSyntheticLambda8 INSTANCE = new ImageLoader$HttpImageTask$$ExternalSyntheticLambda8();

    private /* synthetic */ ImageLoader$HttpImageTask$$ExternalSyntheticLambda8() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ImageLoader.HttpImageTask.lambda$doInBackground$2(tLObject, tLRPC$TL_error);
    }
}
