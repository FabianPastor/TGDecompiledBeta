package org.telegram.ui.ActionBar;

import android.content.Context;

public class DarkAlertDialog extends AlertDialog {

    public static class Builder extends org.telegram.ui.ActionBar.AlertDialog.Builder {
        public Builder(Context context) {
            super(new DarkAlertDialog(context, 0));
        }

        public Builder(Context context, int i) {
            super(new DarkAlertDialog(context, i));
        }
    }

    public DarkAlertDialog(Context context, int i) {
        super(context, i);
    }

    protected int getThemeColor(String str) {
        int hashCode = str.hashCode();
        if (hashCode != -NUM) {
            if (hashCode != -451706526) {
                if (hashCode != -93324646) {
                    if (hashCode == NUM) {
                        if (str.equals(Theme.key_dialogTextBlack)) {
                            hashCode = 1;
                            switch (hashCode) {
                                case 0:
                                    return -14277082;
                                case 1:
                                case 2:
                                case 3:
                                    return -1;
                                default:
                                    return super.getThemeColor(str);
                            }
                        }
                    }
                } else if (str.equals(Theme.key_dialogButton)) {
                    hashCode = 2;
                    switch (hashCode) {
                        case 0:
                            return -14277082;
                        case 1:
                        case 2:
                        case 3:
                            return -1;
                        default:
                            return super.getThemeColor(str);
                    }
                }
            } else if (str.equals(Theme.key_dialogScrollGlow)) {
                hashCode = 3;
                switch (hashCode) {
                    case 0:
                        return -14277082;
                    case 1:
                    case 2:
                    case 3:
                        return -1;
                    default:
                        return super.getThemeColor(str);
                }
            }
        } else if (str.equals(Theme.key_dialogBackground)) {
            hashCode = 0;
            switch (hashCode) {
                case 0:
                    return -14277082;
                case 1:
                case 2:
                case 3:
                    return -1;
                default:
                    return super.getThemeColor(str);
            }
        }
        hashCode = -1;
        switch (hashCode) {
            case 0:
                return -14277082;
            case 1:
            case 2:
            case 3:
                return -1;
            default:
                return super.getThemeColor(str);
        }
    }
}
