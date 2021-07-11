package org.telegram.messenger;

/* renamed from: org.telegram.messenger.-$$Lambda$Emoji$jetPe9LKmqTb2VDJvKz-A7BWYlM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Emoji$jetPe9LKmqTb2VDJvKzA7BWYlM implements Runnable {
    public static final /* synthetic */ $$Lambda$Emoji$jetPe9LKmqTb2VDJvKzA7BWYlM INSTANCE = new $$Lambda$Emoji$jetPe9LKmqTb2VDJvKzA7BWYlM();

    private /* synthetic */ $$Lambda$Emoji$jetPe9LKmqTb2VDJvKzA7BWYlM() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiLoaded, new Object[0]);
    }
}
