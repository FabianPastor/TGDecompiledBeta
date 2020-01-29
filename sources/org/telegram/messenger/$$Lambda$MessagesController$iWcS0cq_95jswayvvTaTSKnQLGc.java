package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$iWcS0cq_95jswayvvTaTSKnQLGc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$iWcS0cq_95jswayvvTaTSKnQLGc implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$iWcS0cq_95jswayvvTaTSKnQLGc INSTANCE = new $$Lambda$MessagesController$iWcS0cq_95jswayvvTaTSKnQLGc();

    private /* synthetic */ $$Lambda$MessagesController$iWcS0cq_95jswayvvTaTSKnQLGc() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
