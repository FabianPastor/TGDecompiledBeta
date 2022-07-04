package org.telegram.messenger;

import org.telegram.messenger.LocationController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda23 implements RequestDelegate {
    public final /* synthetic */ LocationController f$0;
    public final /* synthetic */ LocationController.SharingLocationInfo f$1;
    public final /* synthetic */ int[] f$2;
    public final /* synthetic */ TLRPC.TL_messages_editMessage f$3;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda23(LocationController locationController, LocationController.SharingLocationInfo sharingLocationInfo, int[] iArr, TLRPC.TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
        this.f$2 = iArr;
        this.f$3 = tL_messages_editMessage;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1919xd8307cfb(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
