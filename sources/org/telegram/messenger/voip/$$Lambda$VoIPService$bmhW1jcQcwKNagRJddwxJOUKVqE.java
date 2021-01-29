package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$bmhW1jcQcwKNagRJddwxJOUKVqE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$bmhW1jcQcwKNagRJddwxJOUKVqE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$bmhW1jcQcwKNagRJddwxJOUKVqE INSTANCE = new $$Lambda$VoIPService$bmhW1jcQcwKNagRJddwxJOUKVqE();

    private /* synthetic */ $$Lambda$VoIPService$bmhW1jcQcwKNagRJddwxJOUKVqE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$45(tLObject, tLRPC$TL_error);
    }
}
