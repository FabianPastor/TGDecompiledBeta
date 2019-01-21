package org.telegram.ui.Cells;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class DrawerProfileCell$$Lambda$0 implements OnClickListener {
    private final DrawerProfileCell arg$1;
    private final OnClickListener arg$2;

    DrawerProfileCell$$Lambda$0(DrawerProfileCell drawerProfileCell, OnClickListener onClickListener) {
        this.arg$1 = drawerProfileCell;
        this.arg$2 = onClickListener;
    }

    public void onClick(View view) {
        this.arg$1.lambda$setOnArrowClickListener$0$DrawerProfileCell(this.arg$2, view);
    }
}
