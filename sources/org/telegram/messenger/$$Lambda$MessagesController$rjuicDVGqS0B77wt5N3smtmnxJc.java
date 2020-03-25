package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$rjuicDVGqS0B77wt5N3smtmnxJc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$rjuicDVGqS0B77wt5N3smtmnxJc implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$rjuicDVGqS0B77wt5N3smtmnxJc INSTANCE = new $$Lambda$MessagesController$rjuicDVGqS0B77wt5N3smtmnxJc();

    private /* synthetic */ $$Lambda$MessagesController$rjuicDVGqS0B77wt5N3smtmnxJc() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}
