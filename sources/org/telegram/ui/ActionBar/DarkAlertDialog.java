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

    /* Access modifiers changed, original: protected */
    public int getThemeColor(java.lang.String r3) {
        /*
        r2 = this;
        r0 = -1;
        r1 = r3.hashCode();
        switch(r1) {
            case -1849805674: goto L_0x0011;
            case -451706526: goto L_0x0032;
            case -93324646: goto L_0x0027;
            case 1828201066: goto L_0x001c;
            default: goto L_0x0008;
        };
    L_0x0008:
        r1 = r0;
    L_0x0009:
        switch(r1) {
            case 0: goto L_0x003d;
            case 1: goto L_0x0010;
            case 2: goto L_0x0010;
            case 3: goto L_0x0010;
            default: goto L_0x000c;
        };
    L_0x000c:
        r0 = super.getThemeColor(r3);
    L_0x0010:
        return r0;
    L_0x0011:
        r1 = "dialogBackground";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x0008;
    L_0x001a:
        r1 = 0;
        goto L_0x0009;
    L_0x001c:
        r1 = "dialogTextBlack";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x0008;
    L_0x0025:
        r1 = 1;
        goto L_0x0009;
    L_0x0027:
        r1 = "dialogButton";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x0008;
    L_0x0030:
        r1 = 2;
        goto L_0x0009;
    L_0x0032:
        r1 = "dialogScrollGlow";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x0008;
    L_0x003b:
        r1 = 3;
        goto L_0x0009;
    L_0x003d:
        r0 = -14277082; // 0xfffffffffvar_ float:-2.2084993E38 double:NaN;
        goto L_0x0010;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DarkAlertDialog.getThemeColor(java.lang.String):int");
    }
}
