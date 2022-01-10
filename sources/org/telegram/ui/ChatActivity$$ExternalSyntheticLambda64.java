package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;
import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda64 implements View.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ CharSequence f$4;
    public final /* synthetic */ TranslateAlert.OnLinkPress f$5;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda64(ChatActivity chatActivity, int i, ArrayList arrayList, String str, CharSequence charSequence, TranslateAlert.OnLinkPress onLinkPress) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = str;
        this.f$4 = charSequence;
        this.f$5 = onLinkPress;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createMenu$135(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
    }
}
