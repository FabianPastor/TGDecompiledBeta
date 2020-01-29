package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$Emoji$evx_fdAb4NaXQ9KHlOHuPrE3OTE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Emoji$evx_fdAb4NaXQ9KHlOHuPrE3OTE implements Runnable {
    public static final /* synthetic */ $$Lambda$Emoji$evx_fdAb4NaXQ9KHlOHuPrE3OTE INSTANCE = new $$Lambda$Emoji$evx_fdAb4NaXQ9KHlOHuPrE3OTE();

    private /* synthetic */ $$Lambda$Emoji$evx_fdAb4NaXQ9KHlOHuPrE3OTE() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiDidLoad, new Object[0]);
    }
}
