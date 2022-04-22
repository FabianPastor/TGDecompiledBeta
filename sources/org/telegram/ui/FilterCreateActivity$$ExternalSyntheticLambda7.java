package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class FilterCreateActivity$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ boolean f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ BaseFragment f$11;
    public final /* synthetic */ Runnable f$12;
    public final /* synthetic */ MessagesController.DialogFilter f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ FilterCreateActivity$$ExternalSyntheticLambda7(boolean z, AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList arrayList, ArrayList arrayList2, boolean z2, boolean z3, boolean z4, boolean z5, BaseFragment baseFragment, Runnable runnable) {
        this.f$0 = z;
        this.f$1 = alertDialog;
        this.f$2 = dialogFilter;
        this.f$3 = i;
        this.f$4 = str;
        this.f$5 = arrayList;
        this.f$6 = arrayList2;
        this.f$7 = z2;
        this.f$8 = z3;
        this.f$9 = z4;
        this.f$10 = z5;
        this.f$11 = baseFragment;
        this.f$12 = runnable;
    }

    public final void run() {
        FilterCreateActivity.lambda$saveFilterToServer$12(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
