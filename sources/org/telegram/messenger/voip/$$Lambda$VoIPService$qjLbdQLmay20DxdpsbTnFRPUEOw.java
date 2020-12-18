package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$qjLbdQLmay20DxdpsbTnFRPUEOw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$qjLbdQLmay20DxdpsbTnFRPUEOw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$qjLbdQLmay20DxdpsbTnFRPUEOw INSTANCE = new $$Lambda$VoIPService$qjLbdQLmay20DxdpsbTnFRPUEOw();

    private /* synthetic */ $$Lambda$VoIPService$qjLbdQLmay20DxdpsbTnFRPUEOw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$42(tLObject, tLRPC$TL_error);
    }
}
