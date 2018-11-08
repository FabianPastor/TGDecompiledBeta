package org.telegram.p005ui.ActionBar;

/* renamed from: org.telegram.ui.ActionBar.BottomSheet$$Lambda$0 */
final /* synthetic */ class BottomSheet$$Lambda$0 implements Runnable {
    private final BottomSheet arg$1;

    BottomSheet$$Lambda$0(BottomSheet bottomSheet) {
        this.arg$1 = bottomSheet;
    }

    public void run() {
        this.arg$1.dismiss();
    }
}
