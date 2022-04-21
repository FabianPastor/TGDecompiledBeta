package org.telegram.ui.ActionBar;

import android.content.Context;
import org.telegram.ui.ActionBar.AlertDialog;

public class DarkAlertDialog extends AlertDialog {
    public DarkAlertDialog(Context context, int progressStyle) {
        super(context, progressStyle);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getThemedColor(java.lang.String r3) {
        /*
            r2 = this;
            int r0 = r3.hashCode()
            r1 = -1
            switch(r0) {
                case -1849805674: goto L_0x0027;
                case -451706526: goto L_0x001d;
                case -93324646: goto L_0x0013;
                case 1828201066: goto L_0x0009;
                default: goto L_0x0008;
            }
        L_0x0008:
            goto L_0x0031
        L_0x0009:
            java.lang.String r0 = "dialogTextBlack"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0008
            r0 = 1
            goto L_0x0032
        L_0x0013:
            java.lang.String r0 = "dialogButton"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0008
            r0 = 2
            goto L_0x0032
        L_0x001d:
            java.lang.String r0 = "dialogScrollGlow"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0008
            r0 = 3
            goto L_0x0032
        L_0x0027:
            java.lang.String r0 = "dialogBackground"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0008
            r0 = 0
            goto L_0x0032
        L_0x0031:
            r0 = -1
        L_0x0032:
            switch(r0) {
                case 0: goto L_0x003b;
                case 1: goto L_0x003a;
                case 2: goto L_0x003a;
                case 3: goto L_0x003a;
                default: goto L_0x0035;
            }
        L_0x0035:
            int r0 = super.getThemedColor(r3)
            return r0
        L_0x003a:
            return r1
        L_0x003b:
            r0 = -14277082(0xfffffffffvar_, float:-2.2084993E38)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DarkAlertDialog.getThemedColor(java.lang.String):int");
    }

    public static class Builder extends AlertDialog.Builder {
        public Builder(Context context) {
            super((AlertDialog) new DarkAlertDialog(context, 0));
        }

        public Builder(Context context, int progressViewStyle) {
            super((AlertDialog) new DarkAlertDialog(context, progressViewStyle));
        }
    }
}
