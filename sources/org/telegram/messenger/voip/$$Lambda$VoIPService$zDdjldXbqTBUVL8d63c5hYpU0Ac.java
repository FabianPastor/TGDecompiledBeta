package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$zDdjldXbqTBUVL8d63c5hYpU0Ac  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$zDdjldXbqTBUVL8d63c5hYpU0Ac implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$zDdjldXbqTBUVL8d63c5hYpU0Ac INSTANCE = new $$Lambda$VoIPService$zDdjldXbqTBUVL8d63c5hYpU0Ac();

    private /* synthetic */ $$Lambda$VoIPService$zDdjldXbqTBUVL8d63c5hYpU0Ac() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
