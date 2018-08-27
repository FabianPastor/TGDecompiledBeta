package org.telegram.ui.ActionBar;

final /* synthetic */ class Theme$$Lambda$4 implements Runnable {
    static final Runnable $instance = new Theme$$Lambda$4();

    private Theme$$Lambda$4() {
    }

    public void run() {
        Theme.checkAutoNightThemeConditions();
    }
}
