package org.telegram.p005ui;

import java.util.Comparator;
import org.telegram.p005ui.PassportActivity.C21963;

/* renamed from: org.telegram.ui.PassportActivity$3$$Lambda$3 */
final /* synthetic */ class PassportActivity$3$$Lambda$3 implements Comparator {
    private final C21963 arg$1;

    PassportActivity$3$$Lambda$3(C21963 c21963) {
        this.arg$1 = c21963;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$onIdentityDone$3$PassportActivity$3((String) obj, (String) obj2);
    }
}
