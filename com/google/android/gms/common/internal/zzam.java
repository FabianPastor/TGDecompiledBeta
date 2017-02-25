package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.R;

public class zzam {
    private final Resources zzaGK;
    private final String zzaGL = this.zzaGK.getResourcePackageName(R.string.common_google_play_services_unknown_issue);

    public zzam(Context context) {
        zzac.zzw(context);
        this.zzaGK = context.getResources();
    }

    public String getString(String str) {
        int identifier = this.zzaGK.getIdentifier(str, "string", this.zzaGL);
        return identifier == 0 ? null : this.zzaGK.getString(identifier);
    }
}
