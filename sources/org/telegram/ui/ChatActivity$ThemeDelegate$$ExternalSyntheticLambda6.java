package org.telegram.ui;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6 implements Runnable {
    public static final /* synthetic */ ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6 INSTANCE = new ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6();

    private /* synthetic */ ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false, true);
    }
}
