package org.telegram.ui;

import java.util.ArrayList;

final /* synthetic */ class ChannelUsersActivity$SearchAdapter$$Lambda$3 implements Runnable {
    private final SearchAdapter arg$1;
    private final String arg$2;
    private final ArrayList arg$3;

    ChannelUsersActivity$SearchAdapter$$Lambda$3(SearchAdapter searchAdapter, String str, ArrayList arrayList) {
        this.arg$1 = searchAdapter;
        this.arg$2 = str;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$0$ChannelUsersActivity$SearchAdapter(this.arg$2, this.arg$3);
    }
}
