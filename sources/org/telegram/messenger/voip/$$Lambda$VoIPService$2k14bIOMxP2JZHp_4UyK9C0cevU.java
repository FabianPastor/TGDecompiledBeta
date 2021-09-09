package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$2k14bIOMxP2JZHp_4UyK9C0cevU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$2k14bIOMxP2JZHp_4UyK9C0cevU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$2k14bIOMxP2JZHp_4UyK9C0cevU INSTANCE = new $$Lambda$VoIPService$2k14bIOMxP2JZHp_4UyK9C0cevU();

    private /* synthetic */ $$Lambda$VoIPService$2k14bIOMxP2JZHp_4UyK9C0cevU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$73(tLObject, tLRPC$TL_error);
    }
}
