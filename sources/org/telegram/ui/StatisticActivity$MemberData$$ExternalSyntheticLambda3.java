package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$MemberData$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ StatisticActivity.MemberData f$0;
    public final /* synthetic */ StatisticActivity f$1;
    public final /* synthetic */ AlertDialog[] f$2;
    public final /* synthetic */ TLRPC.ChatFull f$3;

    public /* synthetic */ StatisticActivity$MemberData$$ExternalSyntheticLambda3(StatisticActivity.MemberData memberData, StatisticActivity statisticActivity, AlertDialog[] alertDialogArr, TLRPC.ChatFull chatFull) {
        this.f$0 = memberData;
        this.f$1 = statisticActivity;
        this.f$2 = alertDialogArr;
        this.f$3 = chatFull;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3272xvar_eb046(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
