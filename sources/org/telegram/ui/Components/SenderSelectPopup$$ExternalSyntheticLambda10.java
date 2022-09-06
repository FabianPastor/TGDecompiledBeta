package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import java.util.List;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SenderSelectPopup;

public final /* synthetic */ class SenderSelectPopup$$ExternalSyntheticLambda10 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ SenderSelectPopup f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ ChatActivity f$3;
    public final /* synthetic */ SenderSelectPopup.OnSelectCallback f$4;

    public /* synthetic */ SenderSelectPopup$$ExternalSyntheticLambda10(SenderSelectPopup senderSelectPopup, List list, Context context, ChatActivity chatActivity, SenderSelectPopup.OnSelectCallback onSelectCallback) {
        this.f$0 = senderSelectPopup;
        this.f$1 = list;
        this.f$2 = context;
        this.f$3 = chatActivity;
        this.f$4 = onSelectCallback;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$2(this.f$1, this.f$2, this.f$3, this.f$4, view, i);
    }
}
