package org.telegram.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.LanguageDetector;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda211 implements LanguageDetector.StringCallback {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ String[] f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ ActionBarMenuSubItem f$4;
    public final /* synthetic */ AtomicBoolean f$5;
    public final /* synthetic */ AtomicReference f$6;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda211(ChatActivity chatActivity, String[] strArr, String str, boolean z, ActionBarMenuSubItem actionBarMenuSubItem, AtomicBoolean atomicBoolean, AtomicReference atomicReference) {
        this.f$0 = chatActivity;
        this.f$1 = strArr;
        this.f$2 = str;
        this.f$3 = z;
        this.f$4 = actionBarMenuSubItem;
        this.f$5 = atomicBoolean;
        this.f$6 = atomicReference;
    }

    public final void run(String str) {
        this.f$0.lambda$createMenu$174(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, str);
    }
}
