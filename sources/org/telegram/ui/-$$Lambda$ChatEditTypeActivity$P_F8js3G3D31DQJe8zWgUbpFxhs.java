package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditTypeActivity$P_F8js3G3D31DQJe8zWgUbpFxhs implements OnClickListener {
    private final /* synthetic */ ChatEditTypeActivity f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$ChatEditTypeActivity$P_F8js3G3D31DQJe8zWgUbpFxhs(ChatEditTypeActivity chatEditTypeActivity, Chat chat) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = chat;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$12$ChatEditTypeActivity(this.f$1, dialogInterface, i);
    }
}
