package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class ChatEditActivity$$Lambda$5 implements OnClickListener {
    private final ChatEditActivity arg$1;
    private final Context arg$2;

    ChatEditActivity$$Lambda$5(ChatEditActivity chatEditActivity, Context context) {
        this.arg$1 = chatEditActivity;
        this.arg$2 = context;
    }

    public void onClick(View view) {
        this.arg$1.lambda$createView$7$ChatEditActivity(this.arg$2, view);
    }
}
