package org.telegram.ui.Adapters;

import java.util.ArrayList;
import org.telegram.messenger.DataQuery.KeywordResultCallback;

final /* synthetic */ class StickersAdapter$$Lambda$3 implements KeywordResultCallback {
    private final StickersAdapter arg$1;
    private final String arg$2;

    StickersAdapter$$Lambda$3(StickersAdapter stickersAdapter, String str) {
        this.arg$1 = stickersAdapter;
        this.arg$2 = str;
    }

    public void run(ArrayList arrayList, String str) {
        this.arg$1.lambda$null$0$StickersAdapter(this.arg$2, arrayList, str);
    }
}
