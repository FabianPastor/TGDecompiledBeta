package org.telegram.p005ui;

import java.util.Comparator;
import org.telegram.p005ui.PassportActivity.C19143;

/* renamed from: org.telegram.ui.PassportActivity$3$$Lambda$2 */
final /* synthetic */ class PassportActivity$3$$Lambda$2 implements Comparator {
    private final C19143 arg$1;

    PassportActivity$3$$Lambda$2(C19143 c19143) {
        this.arg$1 = c19143;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$onIdentityDone$2$PassportActivity$3((String) obj, (String) obj2);
    }
}
