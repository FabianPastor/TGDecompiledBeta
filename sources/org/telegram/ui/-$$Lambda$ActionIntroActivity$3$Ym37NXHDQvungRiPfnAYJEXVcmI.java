package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionIntroActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ActionIntroActivity$3$Ym37NXHDQvungRiPfnAYJEXVcmI implements OnClickListener {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ byte[] f$1;
    private final /* synthetic */ TL_error f$2;

    public /* synthetic */ -$$Lambda$ActionIntroActivity$3$Ym37NXHDQvungRiPfnAYJEXVcmI(AnonymousClass3 anonymousClass3, byte[] bArr, TL_error tL_error) {
        this.f$0 = anonymousClass3;
        this.f$1 = bArr;
        this.f$2 = tL_error;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$6$ActionIntroActivity$3(this.f$1, this.f$2, dialogInterface, i);
    }
}
