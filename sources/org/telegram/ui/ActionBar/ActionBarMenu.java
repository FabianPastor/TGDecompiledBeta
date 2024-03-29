package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes3.dex */
public class ActionBarMenu extends LinearLayout {
    public boolean drawBlur;
    protected boolean isActionMode;
    protected ActionBar parentActionBar;

    public ActionBarMenu(Context context, ActionBar actionBar) {
        super(context);
        this.drawBlur = true;
        setOrientation(0);
        this.parentActionBar = actionBar;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateItemsBackgroundColor() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                childAt.setBackgroundDrawable(Theme.createSelectorDrawable(this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateItemsColor() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).setIconColor(this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor);
            }
        }
    }

    public ActionBarMenuItem addItem(int i, Drawable drawable) {
        return addItem(i, 0, null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, drawable, AndroidUtilities.dp(48.0f), null);
    }

    public ActionBarMenuItem addItem(int i, int i2) {
        return addItem(i, i2, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, null);
    }

    public ActionBarMenuItem addItem(int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        return addItem(i, i2, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, resourcesProvider);
    }

    public ActionBarMenuItem addItem(int i, CharSequence charSequence) {
        return addItem(i, 0, charSequence, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, null, 0, charSequence);
    }

    public ActionBarMenuItem addItem(int i, int i2, int i3, Theme.ResourcesProvider resourcesProvider) {
        return addItem(i, i2, null, i3, null, AndroidUtilities.dp(48.0f), null, resourcesProvider);
    }

    public ActionBarMenuItem addItemWithWidth(int i, int i2, int i3) {
        return addItem(i, i2, null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, null, i3, null);
    }

    public ActionBarMenuItem addItemWithWidth(int i, Drawable drawable, int i2, CharSequence charSequence) {
        return addItem(i, 0, null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, drawable, i2, charSequence);
    }

    public ActionBarMenuItem addItemWithWidth(int i, int i2, int i3, CharSequence charSequence) {
        return addItem(i, i2, null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, null, i3, charSequence);
    }

    public ActionBarMenuItem addItem(int i, int i2, CharSequence charSequence, int i3, Drawable drawable, int i4, CharSequence charSequence2) {
        return addItem(i, i2, charSequence, i3, drawable, i4, charSequence2, null);
    }

    public ActionBarMenuItem addItem(int i, int i2, CharSequence charSequence, int i3, Drawable drawable, int i4, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider) {
        int i5 = i4;
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(getContext(), this, i3, this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor, charSequence != null, resourcesProvider);
        actionBarMenuItem.setTag(Integer.valueOf(i));
        if (charSequence != null) {
            actionBarMenuItem.textView.setText(charSequence);
            if (i5 == 0) {
                i5 = -2;
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i5, -1);
            int dp = AndroidUtilities.dp(14.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            addView(actionBarMenuItem, layoutParams);
        } else {
            if (drawable != null) {
                if (drawable instanceof RLottieDrawable) {
                    actionBarMenuItem.iconView.setAnimation((RLottieDrawable) drawable);
                } else {
                    actionBarMenuItem.iconView.setImageDrawable(drawable);
                }
            } else if (i2 != 0) {
                actionBarMenuItem.iconView.setImageResource(i2);
            }
            addView(actionBarMenuItem, new LinearLayout.LayoutParams(i5, -1));
        }
        actionBarMenuItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenu$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ActionBarMenu.this.lambda$addItem$0(view);
            }
        });
        if (charSequence2 != null) {
            actionBarMenuItem.setContentDescription(charSequence2);
        }
        return actionBarMenuItem;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addItem$0(View view) {
        ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) view;
        if (actionBarMenuItem.hasSubMenu()) {
            if (!this.parentActionBar.actionBarMenuOnItemClick.canOpenMenu()) {
                return;
            }
            actionBarMenuItem.toggleSubMenu();
        } else if (actionBarMenuItem.isSearchField()) {
            this.parentActionBar.onSearchFieldVisibilityChanged(actionBarMenuItem.toggleSearch(true));
        } else {
            onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void hideAllPopupMenus() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).closeSubMenu();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPopupItemsColor(int i, boolean z) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).setPopupItemsColor(i, z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPopupItemsSelectorColor(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).setPopupItemsSelectorColor(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void redrawPopup(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).redrawPopup(i);
            }
        }
    }

    public void onItemClick(int i) {
        ActionBar.ActionBarMenuOnItemClick actionBarMenuOnItemClick = this.parentActionBar.actionBarMenuOnItemClick;
        if (actionBarMenuOnItemClick != null) {
            actionBarMenuOnItemClick.onItemClick(i);
        }
    }

    public void clearItems() {
        removeAllViews();
    }

    public void onMenuButtonPressed() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.getVisibility() != 0) {
                    continue;
                } else if (actionBarMenuItem.hasSubMenu()) {
                    actionBarMenuItem.toggleSubMenu();
                    return;
                } else if (actionBarMenuItem.overrideMenuClick) {
                    onItemClick(((Integer) actionBarMenuItem.getTag()).intValue());
                    return;
                }
            }
        }
    }

    public void closeSearchField(boolean z) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField() && actionBarMenuItem.isSearchFieldVisible()) {
                    ActionBarMenuItem.ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = actionBarMenuItem.listener;
                    if (actionBarMenuItemSearchListener != null && !actionBarMenuItemSearchListener.canCollapseSearch()) {
                        return;
                    }
                    this.parentActionBar.onSearchFieldVisibilityChanged(false);
                    actionBarMenuItem.toggleSearch(z);
                    return;
                }
            }
        }
    }

    public void setSearchCursorColor(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    actionBarMenuItem.getSearchField().setCursorColor(i);
                    return;
                }
            }
        }
    }

    public void setSearchTextColor(int i, boolean z) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    if (z) {
                        actionBarMenuItem.getSearchField().setHintTextColor(i);
                        return;
                    } else {
                        actionBarMenuItem.getSearchField().setTextColor(i);
                        return;
                    }
                }
            }
        }
    }

    public void setSearchFieldText(String str) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    actionBarMenuItem.setSearchFieldText(str, false);
                    actionBarMenuItem.getSearchField().setSelection(str.length());
                }
            }
        }
    }

    public void onSearchPressed() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    actionBarMenuItem.onSearchPressed();
                }
            }
        }
    }

    public void openSearchField(boolean z, boolean z2, String str, boolean z3) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    if (z) {
                        this.parentActionBar.onSearchFieldVisibilityChanged(actionBarMenuItem.toggleSearch(z2));
                    }
                    actionBarMenuItem.setSearchFieldText(str, z3);
                    actionBarMenuItem.getSearchField().setSelection(str.length());
                    return;
                }
            }
        }
    }

    public void setFilter(FiltersView.MediaFilterData mediaFilterData) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    actionBarMenuItem.addSearchFilter(mediaFilterData);
                    return;
                }
            }
        }
    }

    public ActionBarMenuItem getItem(int i) {
        View findViewWithTag = findViewWithTag(Integer.valueOf(i));
        if (findViewWithTag instanceof ActionBarMenuItem) {
            return (ActionBarMenuItem) findViewWithTag;
        }
        return null;
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setEnabled(z);
        }
    }

    public int getItemsMeasuredWidth() {
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                i += childAt.getMeasuredWidth();
            }
        }
        return i;
    }

    public int getVisibleItemsMeasuredWidth() {
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if ((childAt instanceof ActionBarMenuItem) && childAt.getVisibility() != 8) {
                i += childAt.getMeasuredWidth();
            }
        }
        return i;
    }

    public boolean searchFieldVisible() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.getSearchContainer() != null && actionBarMenuItem.getSearchContainer().getVisibility() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void translateXItems(float f) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).setTransitionOffset(f);
            }
        }
    }
}
