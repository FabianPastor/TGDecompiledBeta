package org.telegram.ui;

import org.telegram.messenger.LanguageDetector;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda155 implements LanguageDetector.StringCallback {
    public final /* synthetic */ String[] f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ActionBarMenuSubItem f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda155(String[] strArr, String str, ActionBarMenuSubItem actionBarMenuSubItem) {
        this.f$0 = strArr;
        this.f$1 = str;
        this.f$2 = actionBarMenuSubItem;
    }

    public final void run(String str) {
        ChatActivity.lambda$createMenu$130(this.f$0, this.f$1, this.f$2, str);
    }
}
