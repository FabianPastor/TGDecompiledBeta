package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc implements Comparator {
    public static final /* synthetic */ -$$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc INSTANCE = new -$$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc();

    private /* synthetic */ -$$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Message) obj).seq_out, ((Message) obj2).seq_out);
    }
}
