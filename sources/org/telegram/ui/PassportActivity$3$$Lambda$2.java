package org.telegram.ui;

import java.util.Comparator;
import org.telegram.ui.PassportActivity.C16473;

final /* synthetic */ class PassportActivity$3$$Lambda$2 implements Comparator {
    private final C16473 arg$1;

    PassportActivity$3$$Lambda$2(C16473 c16473) {
        this.arg$1 = c16473;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$onIdentityDone$2$PassportActivity$3((String) obj, (String) obj2);
    }
}
