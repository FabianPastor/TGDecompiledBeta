package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$B8e86bveS3knOZa0Cg1HvRKtz44 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$FileRefController$B8e86bveS3knOZa0Cg1HvRKtz44 INSTANCE = new -$$Lambda$FileRefController$B8e86bveS3knOZa0Cg1HvRKtz44();

    private /* synthetic */ -$$Lambda$FileRefController$B8e86bveS3knOZa0Cg1HvRKtz44() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$23(tLObject, tL_error);
    }
}
