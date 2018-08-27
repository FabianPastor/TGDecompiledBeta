package org.telegram.ui.Adapters;

final /* synthetic */ class DialogsSearchAdapter$$Lambda$4 implements Runnable {
    private final DialogsSearchAdapter arg$1;
    private final String arg$2;
    private final int arg$3;

    DialogsSearchAdapter$$Lambda$4(DialogsSearchAdapter dialogsSearchAdapter, String str, int i) {
        this.arg$1 = dialogsSearchAdapter;
        this.arg$2 = str;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$searchDialogsInternal$8$DialogsSearchAdapter(this.arg$2, this.arg$3);
    }
}
