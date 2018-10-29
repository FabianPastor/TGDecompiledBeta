package org.telegram.ui;

final /* synthetic */ class ChannelUsersActivity$SearchAdapter$$Lambda$0 implements Runnable {
    private final SearchAdapter arg$1;
    private final String arg$2;

    ChannelUsersActivity$SearchAdapter$$Lambda$0(SearchAdapter searchAdapter, String str) {
        this.arg$1 = searchAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$processSearch$1$ChannelUsersActivity$SearchAdapter(this.arg$2);
    }
}
