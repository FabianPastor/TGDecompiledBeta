package org.telegram.p005ui;

import java.util.ArrayList;

/* renamed from: org.telegram.ui.LanguageSelectActivity$$Lambda$4 */
final /* synthetic */ class LanguageSelectActivity$$Lambda$4 implements Runnable {
    private final LanguageSelectActivity arg$1;
    private final ArrayList arg$2;

    LanguageSelectActivity$$Lambda$4(LanguageSelectActivity languageSelectActivity, ArrayList arrayList) {
        this.arg$1 = languageSelectActivity;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$5$LanguageSelectActivity(this.arg$2);
    }
}
