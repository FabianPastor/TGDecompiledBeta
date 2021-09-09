package org.telegram.ui;

import org.telegram.ui.ActionBar.ChatTheme;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$ThemeDelegate$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ChatActivity.ThemeDelegate f$0;
    public final /* synthetic */ ChatTheme f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ChatActivity$ThemeDelegate$$ExternalSyntheticLambda5(ChatActivity.ThemeDelegate themeDelegate, ChatTheme chatTheme, boolean z) {
        this.f$0 = themeDelegate;
        this.f$1 = chatTheme;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$setCurrentTheme$0(this.f$1, this.f$2);
    }
}
