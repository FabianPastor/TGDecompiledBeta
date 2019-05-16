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

    /* Access modifiers changed, original: protected */
    public int getThemeColor(java.lang.String r6) {
        /*
        r5 = this;
        r0 = r6.hashCode();
        r1 = 3;
        r2 = 2;
        r3 = 1;
        r4 = -1;
        switch(r0) {
            case -1849805674: goto L_0x002a;
            case -451706526: goto L_0x0020;
            case -93324646: goto L_0x0016;
            case 1828201066: goto L_0x000c;
            default: goto L_0x000b;
        };
    L_0x000b:
        goto L_0x0034;
    L_0x000c:
        r0 = "dialogTextBlack";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0034;
    L_0x0014:
        r0 = 1;
        goto L_0x0035;
    L_0x0016:
        r0 = "dialogButton";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0034;
    L_0x001e:
        r0 = 2;
        goto L_0x0035;
    L_0x0020:
        r0 = "dialogScrollGlow";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0034;
    L_0x0028:
        r0 = 3;
        goto L_0x0035;
    L_0x002a:
        r0 = "dialogBackground";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0034;
    L_0x0032:
        r0 = 0;
        goto L_0x0035;
    L_0x0034:
        r0 = -1;
    L_0x0035:
        if (r0 == 0) goto L_0x0043;
    L_0x0037:
        if (r0 == r3) goto L_0x0042;
    L_0x0039:
        if (r0 == r2) goto L_0x0042;
    L_0x003b:
        if (r0 == r1) goto L_0x0042;
    L_0x003d:
        r6 = super.getThemeColor(r6);
        return r6;
    L_0x0042:
        return r4;
    L_0x0043:
        r6 = -14277082; // 0xfffffffffvar_ float:-2.2084993E38 double:NaN;
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.DarkAlertDialog.getThemeColor(java.lang.String):int");
    }
}
