package org.telegram.ui.ActionBar;

import android.content.Context;
import org.telegram.ui.ActionBar.AlertDialog;

public class DarkAlertDialog extends AlertDialog {
    public DarkAlertDialog(Context context, int i) {
        super(context, i);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getThemedColor(java.lang.String r3) {
        /*
            r2 = this;
            r3.hashCode()
            int r0 = r3.hashCode()
            r1 = -1
            switch(r0) {
                case -1849805674: goto L_0x002e;
                case -451706526: goto L_0x0023;
                case -93324646: goto L_0x0018;
                case 1828201066: goto L_0x000d;
                default: goto L_0x000b;
            }
        L_0x000b:
            r0 = -1
            goto L_0x0038
        L_0x000d:
            java.lang.String r0 = "dialogTextBlack"
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x0016
            goto L_0x000b
        L_0x0016:
            r0 = 3
            goto L_0x0038
        L_0x0018:
            java.lang.String r0 = "dialogButton"
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x0021
            goto L_0x000b
        L_0x0021:
            r0 = 2
            goto L_0x0038
        L_0x0023:
            java.lang.String r0 = "dialogScrollGlow"
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x002c
            goto L_0x000b
        L_0x002c:
            r0 = 1
            goto L_0x0038
        L_0x002e:
            java.lang.String r0 = "dialogBackground"
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x0037
            goto L_0x000b
        L_0x0037:
            r0 = 0
        L_0x0038:
            switch(r0) {
                case 0: goto L_0x0041;
                case 1: goto L_0x0040;
                case 2: goto L_0x0040;
                case 3: goto L_0x0040;
                default: goto L_0x003b;
            }
        L_0x003b:
            int r3 = super.getThemedColor(r3)
            return r3
        L_0x0040:
            return r1
        L_0x0041:
            r3 = -14277082(0xfffffffffvar_, float:-2.2084993E38)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DarkAlertDialog.getThemedColor(java.lang.String):int");
    }

    public static class Builder extends AlertDialog.Builder {
        public Builder(Context context) {
            super((AlertDialog) new DarkAlertDialog(context, 0));
        }
    }
}
