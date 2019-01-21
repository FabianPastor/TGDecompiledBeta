package org.telegram.ui;

import java.util.Comparator;
import org.telegram.ui.PassportActivity.AnonymousClass3;

final /* synthetic */ class PassportActivity$3$$Lambda$2 implements Comparator {
    private final AnonymousClass3 arg$1;

    PassportActivity$3$$Lambda$2(AnonymousClass3 anonymousClass3) {
        this.arg$1 = anonymousClass3;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$onIdentityDone$2$PassportActivity$3((String) obj, (String) obj2);
    }
}
