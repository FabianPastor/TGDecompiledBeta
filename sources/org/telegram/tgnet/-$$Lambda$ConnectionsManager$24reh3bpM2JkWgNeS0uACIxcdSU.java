package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU implements Runnable {
    public static final /* synthetic */ -$$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU INSTANCE = new -$$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU();

    private /* synthetic */ -$$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(3));
    }
}
