package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$Zq3W07Usva2ue5xRNvv7Md_sKfM implements OnClickListener {
    private final /* synthetic */ ChatUsersActivity f$0;
    private final /* synthetic */ CharSequence[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TL_chatAdminRights f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ TLObject f$5;
    private final /* synthetic */ TL_chatBannedRights f$6;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$Zq3W07Usva2ue5xRNvv7Md_sKfM(ChatUsersActivity chatUsersActivity, CharSequence[] charSequenceArr, int i, TL_chatAdminRights tL_chatAdminRights, String str, TLObject tLObject, TL_chatBannedRights tL_chatBannedRights) {
        this.f$0 = chatUsersActivity;
        this.f$1 = charSequenceArr;
        this.f$2 = i;
        this.f$3 = tL_chatAdminRights;
        this.f$4 = str;
        this.f$5 = tLObject;
        this.f$6 = tL_chatBannedRights;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createMenuForParticipant$9$ChatUsersActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, dialogInterface, i);
    }
}
