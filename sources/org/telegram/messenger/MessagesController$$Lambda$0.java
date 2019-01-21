package org.telegram.messenger;

import org.telegram.ui.ActionBar.Theme;

final /* synthetic */ class MessagesController$$Lambda$0 implements Runnable {
    static final Runnable $instance = new MessagesController$$Lambda$0();

    private MessagesController$$Lambda$0() {
    }

    public void run() {
        Theme.checkAutoNightThemeConditions();
    }
}
