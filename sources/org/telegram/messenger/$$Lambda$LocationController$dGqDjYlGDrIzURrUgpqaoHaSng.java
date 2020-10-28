package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$LocationController$d-GqDjYlGDrIzURrUgpqaoHaSng  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$dGqDjYlGDrIzURrUgpqaoHaSng implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$LocationController$dGqDjYlGDrIzURrUgpqaoHaSng INSTANCE = new $$Lambda$LocationController$dGqDjYlGDrIzURrUgpqaoHaSng();

    private /* synthetic */ $$Lambda$LocationController$dGqDjYlGDrIzURrUgpqaoHaSng() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LocationController.lambda$broadcastLastKnownLocation$8(tLObject, tLRPC$TL_error);
    }
}
