package org.telegram.ui.ActionBar;

import org.telegram.messenger.NotificationCenter;

/* renamed from: org.telegram.ui.ActionBar.-$$Lambda$Theme$lrkQF6b-gOeO7YNCFIsow0lQQno  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Theme$lrkQF6bgOeO7YNCFIsow0lQQno implements Runnable {
    public static final /* synthetic */ $$Lambda$Theme$lrkQF6bgOeO7YNCFIsow0lQQno INSTANCE = new $$Lambda$Theme$lrkQF6bgOeO7YNCFIsow0lQQno();

    private /* synthetic */ $$Lambda$Theme$lrkQF6bgOeO7YNCFIsow0lQQno() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, Boolean.FALSE);
    }
}
