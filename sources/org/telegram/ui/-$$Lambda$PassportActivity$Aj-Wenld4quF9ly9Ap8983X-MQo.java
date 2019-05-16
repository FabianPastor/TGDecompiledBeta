package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.SecureDocument;
import org.telegram.ui.PassportActivity.SecureDocumentCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$Aj-Wenld4quF9ly9Ap8983X-MQo implements OnClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ SecureDocument f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ SecureDocumentCell f$3;
    private final /* synthetic */ String f$4;

    public /* synthetic */ -$$Lambda$PassportActivity$Aj-Wenld4quF9ly9Ap8983X-MQo(PassportActivity passportActivity, SecureDocument secureDocument, int i, SecureDocumentCell secureDocumentCell, String str) {
        this.f$0 = passportActivity;
        this.f$1 = secureDocument;
        this.f$2 = i;
        this.f$3 = secureDocumentCell;
        this.f$4 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$57$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
