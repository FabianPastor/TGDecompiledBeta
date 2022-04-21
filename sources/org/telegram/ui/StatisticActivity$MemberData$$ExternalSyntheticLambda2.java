package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$MemberData$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ StatisticActivity.MemberData f$0;
    public final /* synthetic */ StatisticActivity f$1;
    public final /* synthetic */ AlertDialog[] f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;
    public final /* synthetic */ TLObject f$4;
    public final /* synthetic */ TLRPC.ChatFull f$5;

    public /* synthetic */ StatisticActivity$MemberData$$ExternalSyntheticLambda2(StatisticActivity.MemberData memberData, StatisticActivity statisticActivity, AlertDialog[] alertDialogArr, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.ChatFull chatFull) {
        this.f$0 = memberData;
        this.f$1 = statisticActivity;
        this.f$2 = alertDialogArr;
        this.f$3 = tL_error;
        this.f$4 = tLObject;
        this.f$5 = chatFull;
    }

    public final void run() {
        this.f$0.m3273xb2a63de5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
