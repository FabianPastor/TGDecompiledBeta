package org.telegram.p005ui.ActionBar;

/* renamed from: org.telegram.ui.ActionBar.Theme$$Lambda$4 */
final /* synthetic */ class Theme$$Lambda$4 implements Runnable {
    static final Runnable $instance = new Theme$$Lambda$4();

    private Theme$$Lambda$4() {
    }

    public void run() {
        Theme.checkAutoNightThemeConditions();
    }
}
