package org.telegram.ui.ActionBar;

import android.content.Context;

public class DarkAlertDialog extends AlertDialog {

    public static class Builder extends org.telegram.ui.ActionBar.AlertDialog.Builder {
        public Builder(Context context) {
            super(new DarkAlertDialog(context, 0));
        }

        public Builder(Context context, int progressViewStyle) {
            super(new DarkAlertDialog(context, progressViewStyle));
        }
    }

    public DarkAlertDialog(Context context, int progressStyle) {
        super(context, progressStyle);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int getThemeColor(String key) {
        int i;
        switch (key.hashCode()) {
            case -1849805674:
                if (key.equals(Theme.key_dialogBackground)) {
                    i = 0;
                    break;
                }
            case -451706526:
                if (key.equals(Theme.key_dialogScrollGlow)) {
                    i = 3;
                    break;
                }
            case -93324646:
                if (key.equals(Theme.key_dialogButton)) {
                    i = 2;
                    break;
                }
            case 1828201066:
                if (key.equals(Theme.key_dialogTextBlack)) {
                    i = 1;
                    break;
                }
            default:
                i = -1;
                break;
        }
        switch (i) {
            case 0:
                return -14277082;
            case 1:
            case 2:
            case 3:
                return -1;
            default:
                return super.getThemeColor(key);
        }
    }
}
