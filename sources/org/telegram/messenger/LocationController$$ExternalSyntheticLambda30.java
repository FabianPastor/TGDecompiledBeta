package org.telegram.messenger;

import org.telegram.messenger.LocationController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda30 implements RequestDelegate {
    public final /* synthetic */ LocationController f$0;
    public final /* synthetic */ LocationController.SharingLocationInfo f$1;
    public final /* synthetic */ int[] f$2;
    public final /* synthetic */ TLRPC$TL_messages_editMessage f$3;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda30(LocationController locationController, LocationController.SharingLocationInfo sharingLocationInfo, int[] iArr, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage) {
        this.f$0 = locationController;
        this.f$1 = sharingLocationInfo;
        this.f$2 = iArr;
        this.f$3 = tLRPC$TL_messages_editMessage;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$broadcastLastKnownLocation$7(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
