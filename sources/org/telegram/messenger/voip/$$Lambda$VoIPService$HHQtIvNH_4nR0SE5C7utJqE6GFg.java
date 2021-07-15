package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$HHQtIvNH_4nR0SE5C7utJqE6GFg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$HHQtIvNH_4nR0SE5C7utJqE6GFg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$HHQtIvNH_4nR0SE5C7utJqE6GFg INSTANCE = new $$Lambda$VoIPService$HHQtIvNH_4nR0SE5C7utJqE6GFg();

    private /* synthetic */ $$Lambda$VoIPService$HHQtIvNH_4nR0SE5C7utJqE6GFg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$70(tLObject, tLRPC$TL_error);
    }
}
