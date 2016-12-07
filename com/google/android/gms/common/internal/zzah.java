package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.R;

public class zzah {
    private final Resources EP;
    private final String EQ = this.EP.getResourcePackageName(R.string.common_google_play_services_unknown_issue);

    public zzah(Context context) {
        zzaa.zzy(context);
        this.EP = context.getResources();
    }

    public String getString(String str) {
        int identifier = this.EP.getIdentifier(str, "string", this.EQ);
        return identifier == 0 ? null : this.EP.getString(identifier);
    }
}
