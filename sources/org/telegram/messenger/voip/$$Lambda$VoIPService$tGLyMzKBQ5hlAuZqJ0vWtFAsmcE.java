package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$tGLyMzKBQ5hlAuZqJ0vWtFAsmcE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$tGLyMzKBQ5hlAuZqJ0vWtFAsmcE implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$tGLyMzKBQ5hlAuZqJ0vWtFAsmcE INSTANCE = new $$Lambda$VoIPService$tGLyMzKBQ5hlAuZqJ0vWtFAsmcE();

    private /* synthetic */ $$Lambda$VoIPService$tGLyMzKBQ5hlAuZqJ0vWtFAsmcE() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
