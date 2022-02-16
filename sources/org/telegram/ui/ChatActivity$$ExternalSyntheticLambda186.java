package org.telegram.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.LanguageDetector;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda186 implements LanguageDetector.StringCallback {
    public final /* synthetic */ String[] f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ActionBarMenuSubItem f$2;
    public final /* synthetic */ AtomicBoolean f$3;
    public final /* synthetic */ AtomicReference f$4;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda186(String[] strArr, String str, ActionBarMenuSubItem actionBarMenuSubItem, AtomicBoolean atomicBoolean, AtomicReference atomicReference) {
        this.f$0 = strArr;
        this.f$1 = str;
        this.f$2 = actionBarMenuSubItem;
        this.f$3 = atomicBoolean;
        this.f$4 = atomicReference;
    }

    public final void run(String str) {
        ChatActivity.lambda$createMenu$150(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, str);
    }
}
