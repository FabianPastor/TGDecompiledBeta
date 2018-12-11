package org.telegram.p005ui;

import org.telegram.p005ui.IntroActivity.EGLThread;

/* renamed from: org.telegram.ui.IntroActivity$EGLThread$$Lambda$0 */
final /* synthetic */ class IntroActivity$EGLThread$$Lambda$0 implements Runnable {
    private final EGLThread arg$1;

    IntroActivity$EGLThread$$Lambda$0(EGLThread eGLThread) {
        this.arg$1 = eGLThread;
    }

    public void run() {
        this.arg$1.lambda$shutdown$0$IntroActivity$EGLThread();
    }
}
