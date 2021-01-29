package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$LocationController$sBQ0ISjA9CLASSNAMEnOsUV79F6MakmT0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$sBQ0ISjA9CLASSNAMEnOsUV79F6MakmT0 implements Runnable {
    public static final /* synthetic */ $$Lambda$LocationController$sBQ0ISjA9CLASSNAMEnOsUV79F6MakmT0 INSTANCE = new $$Lambda$LocationController$sBQ0ISjA9CLASSNAMEnOsUV79F6MakmT0();

    private /* synthetic */ $$Lambda$LocationController$sBQ0ISjA9CLASSNAMEnOsUV79F6MakmT0() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
