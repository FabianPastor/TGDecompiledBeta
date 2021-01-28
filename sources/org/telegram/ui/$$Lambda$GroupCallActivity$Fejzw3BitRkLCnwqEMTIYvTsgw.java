package org.telegram.ui;

import org.telegram.messenger.voip.VoIPService;

/* renamed from: org.telegram.ui.-$$Lambda$GroupCallActivity$Fejzw3-BitRkLCnwqEMTIYvTsgw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw implements Runnable {
    public static final /* synthetic */ $$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw INSTANCE = new $$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw();

    private /* synthetic */ $$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw() {
    }

    public final void run() {
        VoIPService.getSharedInstance().setMicMute(false, true, false);
    }
}
