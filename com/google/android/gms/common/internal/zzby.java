package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.R;

public final class zzby {
    private final Resources zzaIw;
    private final String zzaIx = this.zzaIw.getResourcePackageName(R.string.common_google_play_services_unknown_issue);

    public zzby(Context context) {
        zzbo.zzu(context);
        this.zzaIw = context.getResources();
    }

    public final String getString(String str) {
        int identifier = this.zzaIw.getIdentifier(str, "string", this.zzaIx);
        return identifier == 0 ? null : this.zzaIw.getString(identifier);
    }
}
