package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.tgnet.-$$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU implements Runnable {
    public static final /* synthetic */ $$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU INSTANCE = new $$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU();

    private /* synthetic */ $$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }
}
