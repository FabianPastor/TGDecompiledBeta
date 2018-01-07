package net.hockeyapp.android;

import android.content.Context;
import java.util.Date;
import net.hockeyapp.android.utils.Util;
import org.json.JSONArray;

public abstract class UpdateManagerListener {
    public Class<? extends UpdateFragment> getUpdateFragmentClass() {
        return UpdateFragment.class;
    }

    public boolean useUpdateDialog(Context context) {
        return Util.runsOnTablet(context).booleanValue();
    }

    public void onNoUpdateAvailable() {
    }

    public void onUpdateAvailable() {
    }

    public void onCancel() {
    }

    public void onUpdateAvailable(JSONArray data, String url) {
        onUpdateAvailable();
    }

    public Date getExpiryDate() {
        return null;
    }

    public boolean onBuildExpired() {
        return true;
    }

    public boolean canUpdateInMarket() {
        return false;
    }
}
