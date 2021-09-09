package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$4j4ZCjxIQ6w-WuyM2NunmaSvar_I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$4j4ZCjxIQ6wWuyM2NunmaSvar_I implements Runnable {
    public static final /* synthetic */ $$Lambda$VoIPService$4j4ZCjxIQ6wWuyM2NunmaSvar_I INSTANCE = new $$Lambda$VoIPService$4j4ZCjxIQ6wWuyM2NunmaSvar_I();

    private /* synthetic */ $$Lambda$VoIPService$4j4ZCjxIQ6wWuyM2NunmaSvar_I() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndCall, new Object[0]);
    }
}
