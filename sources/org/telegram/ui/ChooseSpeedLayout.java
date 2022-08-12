package org.telegram.ui;

import android.content.Context;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.PopupSwipeBackLayout;

public class ChooseSpeedLayout {
    ActionBarMenuSubItem[] speedItems = new ActionBarMenuSubItem[5];
    ActionBarPopupWindow.ActionBarPopupWindowLayout speedSwipeBackLayout;

    public interface Callback {
        void onSpeedSelected(float f);
    }

    public ChooseSpeedLayout(Context context, PopupSwipeBackLayout popupSwipeBackLayout, Callback callback) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, (Theme.ResourcesProvider) null);
        this.speedSwipeBackLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_arrow_back, LocaleController.getString("Back", R.string.Back), false, (Theme.ResourcesProvider) null);
        addItem.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda5(popupSwipeBackLayout));
        addItem.setColors(-328966, -328966);
        addItem.setSelectorColor(NUM);
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_0_2, LocaleController.getString("SpeedVerySlow", R.string.SpeedVerySlow), false, (Theme.ResourcesProvider) null);
        addItem2.setColors(-328966, -328966);
        addItem2.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda2(callback));
        addItem2.setSelectorColor(NUM);
        this.speedItems[0] = addItem2;
        ActionBarMenuSubItem addItem3 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_0_5, LocaleController.getString("SpeedSlow", R.string.SpeedSlow), false, (Theme.ResourcesProvider) null);
        addItem3.setColors(-328966, -328966);
        addItem3.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda0(callback));
        addItem3.setSelectorColor(NUM);
        this.speedItems[1] = addItem3;
        ActionBarMenuSubItem addItem4 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_1, LocaleController.getString("SpeedNormal", R.string.SpeedNormal), false, (Theme.ResourcesProvider) null);
        addItem4.setColors(-328966, -328966);
        addItem4.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda3(callback));
        addItem4.setSelectorColor(NUM);
        this.speedItems[2] = addItem4;
        ActionBarMenuSubItem addItem5 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_1_5, LocaleController.getString("SpeedFast", R.string.SpeedFast), false, (Theme.ResourcesProvider) null);
        addItem5.setColors(-328966, -328966);
        addItem5.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda4(callback));
        addItem5.setSelectorColor(NUM);
        this.speedItems[3] = addItem5;
        ActionBarMenuSubItem addItem6 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_2, LocaleController.getString("SpeedVeryFast", R.string.SpeedVeryFast), false, (Theme.ResourcesProvider) null);
        addItem6.setColors(-328966, -328966);
        addItem6.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda1(callback));
        addItem6.setSelectorColor(NUM);
        this.speedItems[4] = addItem6;
    }

    public void update(float f) {
        for (int i = 0; i < this.speedItems.length; i++) {
            if ((i != 0 || Math.abs(f - 0.25f) >= 0.001f) && ((i != 1 || Math.abs(f - 0.5f) >= 0.001f) && ((i != 2 || Math.abs(f - 1.0f) >= 0.001f) && ((i != 3 || Math.abs(f - 1.5f) >= 0.001f) && (i != 4 || Math.abs(f - 2.0f) >= 0.001f))))) {
                this.speedItems[i].setColors(-328966, -328966);
            } else {
                this.speedItems[i].setColors(-9718023, -9718023);
            }
        }
    }
}
