package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.tgnet.-$$Lambda$ConnectionsManager$mCKnTBWbUnfNosYqFdRo4Jn0c-Q  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ConnectionsManager$mCKnTBWbUnfNosYqFdRo4Jn0cQ implements Runnable {
    public static final /* synthetic */ $$Lambda$ConnectionsManager$mCKnTBWbUnfNosYqFdRo4Jn0cQ INSTANCE = new $$Lambda$ConnectionsManager$mCKnTBWbUnfNosYqFdRo4Jn0cQ();

    private /* synthetic */ $$Lambda$ConnectionsManager$mCKnTBWbUnfNosYqFdRo4Jn0cQ() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }
}
