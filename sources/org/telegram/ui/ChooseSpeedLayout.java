package org.telegram.ui;

import android.content.Context;
import org.telegram.messenger.LocaleController;
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

    public ChooseSpeedLayout(Context context, PopupSwipeBackLayout swipeBackLayout, Callback callback) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, (Theme.ResourcesProvider) null);
        this.speedSwipeBackLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        ActionBarMenuSubItem backItem = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, NUM, LocaleController.getString("Back", NUM), false, (Theme.ResourcesProvider) null);
        backItem.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda5(swipeBackLayout));
        backItem.setColors(-328966, -328966);
        ActionBarMenuSubItem item = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, NUM, LocaleController.getString("SpeedVerySlow", NUM), false, (Theme.ResourcesProvider) null);
        item.setColors(-328966, -328966);
        item.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda0(callback));
        this.speedItems[0] = item;
        ActionBarMenuSubItem item2 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, NUM, LocaleController.getString("SpeedSlow", NUM), false, (Theme.ResourcesProvider) null);
        item2.setColors(-328966, -328966);
        item2.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda1(callback));
        this.speedItems[1] = item2;
        ActionBarMenuSubItem item3 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, NUM, LocaleController.getString("SpeedNormal", NUM), false, (Theme.ResourcesProvider) null);
        item3.setColors(-328966, -328966);
        item3.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda2(callback));
        this.speedItems[2] = item3;
        ActionBarMenuSubItem item4 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, NUM, LocaleController.getString("SpeedFast", NUM), false, (Theme.ResourcesProvider) null);
        item4.setColors(-328966, -328966);
        item4.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda3(callback));
        this.speedItems[3] = item4;
        ActionBarMenuSubItem item5 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, NUM, LocaleController.getString("SpeedVeryFast", NUM), false, (Theme.ResourcesProvider) null);
        item5.setColors(-328966, -328966);
        item5.setOnClickListener(new ChooseSpeedLayout$$ExternalSyntheticLambda4(callback));
        this.speedItems[4] = item5;
    }

    public void update(float currentVideoSpeed) {
        for (int a = 0; a < this.speedItems.length; a++) {
            if ((a != 0 || Math.abs(currentVideoSpeed - 0.25f) >= 0.001f) && ((a != 1 || Math.abs(currentVideoSpeed - 0.5f) >= 0.001f) && ((a != 2 || Math.abs(currentVideoSpeed - 1.0f) >= 0.001f) && ((a != 3 || Math.abs(currentVideoSpeed - 1.5f) >= 0.001f) && (a != 4 || Math.abs(currentVideoSpeed - 2.0f) >= 0.001f))))) {
                this.speedItems[a].setColors(-328966, -328966);
            } else {
                this.speedItems[a].setColors(-9718023, -9718023);
            }
        }
    }
}
