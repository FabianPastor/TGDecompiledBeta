package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public final /* synthetic */ class ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda0(Context context) {
        this.f$0 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.startActivity(new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION", Uri.parse("package:org.telegram.messenger.beta")));
    }
}
