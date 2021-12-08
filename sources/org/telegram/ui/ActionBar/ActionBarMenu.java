package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.RLottieDrawable;

public class ActionBarMenu extends LinearLayout {
    protected boolean isActionMode;
    protected ActionBar parentActionBar;

    public ActionBarMenu(Context context, ActionBar layer) {
        super(context);
        setOrientation(0);
        this.parentActionBar = layer;
    }

    public ActionBarMenu(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void updateItemsBackgroundColor() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                view.setBackgroundDrawable(Theme.createSelectorDrawable(this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateItemsColor() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).setIconColor(this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor);
            }
        }
    }

    public ActionBarMenuItem addItem(int id, Drawable drawable) {
        return addItem(id, 0, (CharSequence) null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, drawable, AndroidUtilities.dp(48.0f), (CharSequence) null);
    }

    public ActionBarMenuItem addItem(int id, int icon) {
        return addItem(id, icon, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuItem addItem(int id, int icon, Theme.ResourcesProvider resourcesProvider) {
        return addItem(id, icon, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, resourcesProvider);
    }

    public ActionBarMenuItem addItem(int id, CharSequence text) {
        return addItem(id, 0, text, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Drawable) null, 0, text);
    }

    public ActionBarMenuItem addItem(int id, int icon, int backgroundColor) {
        return addItem(id, icon, backgroundColor, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuItem addItem(int id, int icon, int backgroundColor, Theme.ResourcesProvider resourcesProvider) {
        return addItem(id, icon, (CharSequence) null, backgroundColor, (Drawable) null, AndroidUtilities.dp(48.0f), (CharSequence) null, resourcesProvider);
    }

    public ActionBarMenuItem addItemWithWidth(int id, int icon, int width) {
        return addItem(id, icon, (CharSequence) null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Drawable) null, width, (CharSequence) null);
    }

    public ActionBarMenuItem addItemWithWidth(int id, int icon, int width, CharSequence title) {
        return addItem(id, icon, (CharSequence) null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Drawable) null, width, title);
    }

    public ActionBarMenuItem addItem(int id, int icon, CharSequence text, int backgroundColor, Drawable drawable, int width, CharSequence title) {
        return addItem(id, icon, text, backgroundColor, drawable, width, title, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuItem addItem(int id, int icon, CharSequence text, int backgroundColor, Drawable drawable, int width, CharSequence title, Theme.ResourcesProvider resourcesProvider) {
        int i = icon;
        CharSequence charSequence = text;
        Drawable drawable2 = drawable;
        int i2 = width;
        CharSequence charSequence2 = title;
        ActionBarMenuItem menuItem = new ActionBarMenuItem(getContext(), this, backgroundColor, this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor, charSequence != null, resourcesProvider);
        menuItem.setTag(Integer.valueOf(id));
        if (charSequence != null) {
            menuItem.textView.setText(charSequence);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i2 != 0 ? i2 : -2, -1);
            int dp = AndroidUtilities.dp(14.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            addView(menuItem, layoutParams);
        } else {
            if (drawable2 != null) {
                if (drawable2 instanceof RLottieDrawable) {
                    menuItem.iconView.setAnimation((RLottieDrawable) drawable2);
                } else {
                    menuItem.iconView.setImageDrawable(drawable2);
                }
            } else if (i != 0) {
                menuItem.iconView.setImageResource(i);
            }
            addView(menuItem, new LinearLayout.LayoutParams(i2, -1));
        }
        menuItem.setOnClickListener(new ActionBarMenu$$ExternalSyntheticLambda0(this));
        if (charSequence2 != null) {
            menuItem.setContentDescription(charSequence2);
        }
        return menuItem;
    }

    /* renamed from: lambda$addItem$0$org-telegram-ui-ActionBar-ActionBarMenu  reason: not valid java name */
    public /* synthetic */ void m1294lambda$addItem$0$orgtelegramuiActionBarActionBarMenu(View view) {
        ActionBarMenuItem item = (ActionBarMenuItem) view;
        if (item.hasSubMenu()) {
            if (this.parentActionBar.actionBarMenuOnItemClick.canOpenMenu()) {
                item.toggleSubMenu();
            }
        } else if (item.isSearchField()) {
            this.parentActionBar.onSearchFieldVisibilityChanged(item.toggleSearch(true));
        } else {
            onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void hideAllPopupMenus() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).closeSubMenu();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setPopupItemsColor(int color, boolean icon) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).setPopupItemsColor(color, icon);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setPopupItemsSelectorColor(int color) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).setPopupItemsSelectorColor(color);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void redrawPopup(int color) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).redrawPopup(color);
            }
        }
    }

    public void onItemClick(int id) {
        if (this.parentActionBar.actionBarMenuOnItemClick != null) {
            this.parentActionBar.actionBarMenuOnItemClick.onItemClick(id);
        }
    }

    public void clearItems() {
        removeAllViews();
    }

    public void onMenuButtonPressed() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.getVisibility() != 0) {
                    continue;
                } else if (item.hasSubMenu()) {
                    item.toggleSubMenu();
                    return;
                } else if (item.overrideMenuClick) {
                    onItemClick(((Integer) item.getTag()).intValue());
                    return;
                }
            }
        }
    }

    public void closeSearchField(boolean closeKeyboard) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField() && item.isSearchFieldVisible()) {
                    if (item.listener == null || item.listener.canCollapseSearch()) {
                        this.parentActionBar.onSearchFieldVisibilityChanged(false);
                        item.toggleSearch(closeKeyboard);
                        return;
                    }
                    return;
                }
            }
        }
    }

    public void setSearchTextColor(int color, boolean placeholder) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    if (placeholder) {
                        item.getSearchField().setHintTextColor(color);
                        return;
                    } else {
                        item.getSearchField().setTextColor(color);
                        return;
                    }
                }
            }
        }
    }

    public void setSearchFieldText(String text) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    item.setSearchFieldText(text, false);
                    item.getSearchField().setSelection(text.length());
                }
            }
        }
    }

    public void onSearchPressed() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    item.onSearchPressed();
                }
            }
        }
    }

    public void openSearchField(boolean toggle, String text, boolean animated) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    if (toggle) {
                        this.parentActionBar.onSearchFieldVisibilityChanged(item.toggleSearch(true));
                    }
                    item.setSearchFieldText(text, animated);
                    item.getSearchField().setSelection(text.length());
                    return;
                }
            }
        }
    }

    public void setFilter(FiltersView.MediaFilterData filter) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    item.addSearchFilter(filter);
                    return;
                }
            }
        }
    }

    public ActionBarMenuItem getItem(int id) {
        View v = findViewWithTag(Integer.valueOf(id));
        if (v instanceof ActionBarMenuItem) {
            return (ActionBarMenuItem) v;
        }
        return null;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).setEnabled(enabled);
        }
    }

    public int getItemsMeasuredWidth() {
        int w = 0;
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                w += view.getMeasuredWidth();
            }
        }
        return w;
    }

    public boolean searchFieldVisible() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if ((view instanceof ActionBarMenuItem) && ((ActionBarMenuItem) view).getSearchContainer() != null && ((ActionBarMenuItem) view).getSearchContainer().getVisibility() == 0) {
                return true;
            }
        }
        return false;
    }

    public void translateXItems(int offset) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).setTransitionOffset(offset);
            }
        }
    }

    public void clearSearchFilters() {
    }
}
