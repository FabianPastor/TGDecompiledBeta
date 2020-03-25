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
    public int getThemeColor(java.lang.String r6) {
        /*
            r5 = this;
            int r0 = r6.hashCode()
            r1 = 3
            r2 = 2
            r3 = 1
            r4 = -1
            switch(r0) {
                case -1849805674: goto L_0x002a;
                case -451706526: goto L_0x0020;
                case -93324646: goto L_0x0016;
                case 1828201066: goto L_0x000c;
                default: goto L_0x000b;
            }
        L_0x000b:
            goto L_0x0034
        L_0x000c:
            java.lang.String r0 = "dialogTextBlack"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0034
            r0 = 1
            goto L_0x0035
        L_0x0016:
            java.lang.String r0 = "dialogButton"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0034
            r0 = 2
            goto L_0x0035
        L_0x0020:
            java.lang.String r0 = "dialogScrollGlow"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0034
            r0 = 3
            goto L_0x0035
        L_0x002a:
            java.lang.String r0 = "dialogBackground"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0034
            r0 = 0
            goto L_0x0035
        L_0x0034:
            r0 = -1
        L_0x0035:
            if (r0 == 0) goto L_0x0043
            if (r0 == r3) goto L_0x0042
            if (r0 == r2) goto L_0x0042
            if (r0 == r1) goto L_0x0042
            int r6 = super.getThemeColor(r6)
            return r6
        L_0x0042:
            return r4
        L_0x0043:
            r6 = -14277082(0xfffffffffvar_, float:-2.2084993E38)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DarkAlertDialog.getThemeColor(java.lang.String):int");
    }

    public static class Builder extends AlertDialog.Builder {
        public Builder(Context context) {
            super((AlertDialog) new DarkAlertDialog(context, 0));
        }
    }
}
