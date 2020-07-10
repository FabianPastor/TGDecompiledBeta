package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Q_w8O5h9eycGQ7P2BP_grEWizso  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Q_w8O5h9eycGQ7P2BP_grEWizso implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$Q_w8O5h9eycGQ7P2BP_grEWizso INSTANCE = new $$Lambda$MessagesController$Q_w8O5h9eycGQ7P2BP_grEWizso();

    private /* synthetic */ $$Lambda$MessagesController$Q_w8O5h9eycGQ7P2BP_grEWizso() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}
