package org.telegram.ui.Adapters;

final /* synthetic */ class PhonebookSearchAdapter$$Lambda$0 implements Runnable {
    private final PhonebookSearchAdapter arg$1;
    private final String arg$2;

    PhonebookSearchAdapter$$Lambda$0(PhonebookSearchAdapter phonebookSearchAdapter, String str) {
        this.arg$1 = phonebookSearchAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$processSearch$1$PhonebookSearchAdapter(this.arg$2);
    }
}
