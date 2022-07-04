package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class BasePermissionsActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ BasePermissionsActivity f$0;

    public /* synthetic */ BasePermissionsActivity$$ExternalSyntheticLambda0(BasePermissionsActivity basePermissionsActivity) {
        this.f$0 = basePermissionsActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createPermissionErrorAlert$0(dialogInterface, i);
    }
}
