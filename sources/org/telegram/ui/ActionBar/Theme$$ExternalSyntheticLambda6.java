package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda6 implements Runnable {
    public static final /* synthetic */ Theme$$ExternalSyntheticLambda6 INSTANCE = new Theme$$ExternalSyntheticLambda6();

    private /* synthetic */ Theme$$ExternalSyntheticLambda6() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, Boolean.FALSE);
    }
}
