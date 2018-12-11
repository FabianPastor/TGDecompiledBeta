package org.telegram.p005ui;

import java.util.Comparator;
import org.telegram.p005ui.PassportActivity.CLASSNAME;

/* renamed from: org.telegram.ui.PassportActivity$3$$Lambda$2 */
final /* synthetic */ class PassportActivity$3$$Lambda$2 implements Comparator {
    private final CLASSNAME arg$1;

    PassportActivity$3$$Lambda$2(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$onIdentityDone$2$PassportActivity$3((String) obj, (String) obj2);
    }
}
