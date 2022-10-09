package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ChooseSpeedLayout;
import org.telegram.ui.Components.PopupSwipeBackLayout;
/* loaded from: classes3.dex */
public class ChooseSpeedLayout {
    ActionBarMenuSubItem[] speedItems = new ActionBarMenuSubItem[5];
    ActionBarPopupWindow.ActionBarPopupWindowLayout speedSwipeBackLayout;

    /* loaded from: classes3.dex */
    public interface Callback {
        void onSpeedSelected(float f);
    }

    public ChooseSpeedLayout(Context context, final PopupSwipeBackLayout popupSwipeBackLayout, final Callback callback) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, null);
        this.speedSwipeBackLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_arrow_back, LocaleController.getString("Back", R.string.Back), false, null);
        addItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PopupSwipeBackLayout.this.closeForeground();
            }
        });
        addItem.setColors(-328966, -328966);
        addItem.setSelectorColor(NUM);
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_0_2, LocaleController.getString("SpeedVerySlow", R.string.SpeedVerySlow), false, null);
        addItem2.setColors(-328966, -328966);
        addItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(0.25f);
            }
        });
        addItem2.setSelectorColor(NUM);
        this.speedItems[0] = addItem2;
        ActionBarMenuSubItem addItem3 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_0_5, LocaleController.getString("SpeedSlow", R.string.SpeedSlow), false, null);
        addItem3.setColors(-328966, -328966);
        addItem3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(0.5f);
            }
        });
        addItem3.setSelectorColor(NUM);
        this.speedItems[1] = addItem3;
        ActionBarMenuSubItem addItem4 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_1, LocaleController.getString("SpeedNormal", R.string.SpeedNormal), false, null);
        addItem4.setColors(-328966, -328966);
        addItem4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(1.0f);
            }
        });
        addItem4.setSelectorColor(NUM);
        this.speedItems[2] = addItem4;
        ActionBarMenuSubItem addItem5 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_1_5, LocaleController.getString("SpeedFast", R.string.SpeedFast), false, null);
        addItem5.setColors(-328966, -328966);
        addItem5.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(1.5f);
            }
        });
        addItem5.setSelectorColor(NUM);
        this.speedItems[3] = addItem5;
        ActionBarMenuSubItem addItem6 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_2, LocaleController.getString("SpeedVeryFast", R.string.SpeedVeryFast), false, null);
        addItem6.setColors(-328966, -328966);
        addItem6.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(2.0f);
            }
        });
        addItem6.setSelectorColor(NUM);
        this.speedItems[4] = addItem6;
    }

    public void update(float f) {
        for (int i = 0; i < this.speedItems.length; i++) {
            if ((i == 0 && Math.abs(f - 0.25f) < 0.001f) || ((i == 1 && Math.abs(f - 0.5f) < 0.001f) || ((i == 2 && Math.abs(f - 1.0f) < 0.001f) || ((i == 3 && Math.abs(f - 1.5f) < 0.001f) || (i == 4 && Math.abs(f - 2.0f) < 0.001f))))) {
                this.speedItems[i].setColors(-9718023, -9718023);
            } else {
                this.speedItems[i].setColors(-328966, -328966);
            }
        }
    }
}
