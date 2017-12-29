package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.R;

public final class zzca {
    private final Resources zzgbt;
    private final String zzgbu = this.zzgbt.getResourcePackageName(R.string.common_google_play_services_unknown_issue);

    public zzca(Context context) {
        zzbq.checkNotNull(context);
        this.zzgbt = context.getResources();
    }

    public final String getString(String str) {
        int identifier = this.zzgbt.getIdentifier(str, "string", this.zzgbu);
        return identifier == 0 ? null : this.zzgbt.getString(identifier);
    }
}
