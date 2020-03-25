package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

/* renamed from: org.telegram.messenger.-$$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc implements Comparator {
    public static final /* synthetic */ $$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc INSTANCE = new $$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc();

    private /* synthetic */ $$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Message) obj).seq_out, ((TLRPC$Message) obj2).seq_out);
    }
}
