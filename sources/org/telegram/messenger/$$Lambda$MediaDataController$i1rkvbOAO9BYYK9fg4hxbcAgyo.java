package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$i1rk-vbOAO9BYYK9fg4hxbcAgyo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$i1rkvbOAO9BYYK9fg4hxbcAgyo implements Comparator {
    public static final /* synthetic */ $$Lambda$MediaDataController$i1rkvbOAO9BYYK9fg4hxbcAgyo INSTANCE = new $$Lambda$MediaDataController$i1rkvbOAO9BYYK9fg4hxbcAgyo();

    private /* synthetic */ $$Lambda$MediaDataController$i1rkvbOAO9BYYK9fg4hxbcAgyo() {
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getTextStyleRuns$99((TLRPC.MessageEntity) obj, (TLRPC.MessageEntity) obj2);
    }
}
