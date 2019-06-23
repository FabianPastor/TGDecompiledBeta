package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$J6S3hQy_nW1WeGvcfet5MPw3HFQ implements OnClickListener {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$ProfileActivity$J6S3hQy_nW1WeGvcfet5MPw3HFQ(ProfileActivity profileActivity, Chat chat) {
        this.f$0 = profileActivity;
        this.f$1 = chat;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createView$9$ProfileActivity(this.f$1, view);
    }
}
