package org.telegram.ui;

import java.util.HashMap;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;

final /* synthetic */ class LaunchActivity$$Lambda$7 implements AccountSelectDelegate {
    private final LaunchActivity arg$1;
    private final Integer arg$10;
    private final String arg$11;
    private final String[] arg$12;
    private final HashMap arg$13;
    private final String arg$14;
    private final int arg$2;
    private final String arg$3;
    private final String arg$4;
    private final String arg$5;
    private final String arg$6;
    private final String arg$7;
    private final String arg$8;
    private final boolean arg$9;

    LaunchActivity$$Lambda$7(LaunchActivity launchActivity, int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, String str7, String[] strArr, HashMap hashMap, String str8) {
        this.arg$1 = launchActivity;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = str2;
        this.arg$5 = str3;
        this.arg$6 = str4;
        this.arg$7 = str5;
        this.arg$8 = str6;
        this.arg$9 = z;
        this.arg$10 = num;
        this.arg$11 = str7;
        this.arg$12 = strArr;
        this.arg$13 = hashMap;
        this.arg$14 = str8;
    }

    public void didSelectAccount(int i) {
        this.arg$1.lambda$runLinkRequest$8$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, this.arg$14, i);
    }
}
