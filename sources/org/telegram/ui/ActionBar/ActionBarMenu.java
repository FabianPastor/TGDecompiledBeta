package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;

public class ActionBarMenu extends LinearLayout {
    protected boolean isActionMode;
    protected ActionBar parentActionBar;

    /* renamed from: org.telegram.ui.ActionBar.ActionBarMenu$1 */
    class C07211 implements OnClickListener {
        C07211() {
        }

        public void onClick(View view) {
            ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) view;
            if (actionBarMenuItem.hasSubMenu()) {
                if (ActionBarMenu.this.parentActionBar.actionBarMenuOnItemClick.canOpenMenu() != null) {
                    actionBarMenuItem.toggleSubMenu();
                }
            } else if (actionBarMenuItem.isSearchField()) {
                ActionBarMenu.this.parentActionBar.onSearchFieldVisibilityChanged(actionBarMenuItem.toggleSearch(true));
            } else {
                ActionBarMenu.this.onItemClick(((Integer) view.getTag()).intValue());
            }
        }
    }

    public ActionBarMenu(Context context, ActionBar actionBar) {
        super(context);
        setOrientation(null);
        this.parentActionBar = actionBar;
    }

    public ActionBarMenu(Context context) {
        super(context);
    }

    protected void updateItemsBackgroundColor() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                childAt.setBackgroundDrawable(Theme.createSelectorDrawable(this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor));
            }
        }
    }

    protected void updateItemsColor() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).setIconColor(this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor);
            }
        }
    }

    public ActionBarMenuItem addItem(int i, Drawable drawable) {
        return addItem(i, 0, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, drawable, AndroidUtilities.dp(48.0f));
    }

    public ActionBarMenuItem addItem(int i, int i2) {
        return addItem(i, i2, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor);
    }

    public ActionBarMenuItem addItem(int i, int i2, int i3) {
        return addItem(i, i2, i3, null, AndroidUtilities.dp(48.0f));
    }

    public ActionBarMenuItem addItemWithWidth(int i, int i2, int i3) {
        return addItem(i, i2, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, null, i3);
    }

    public ActionBarMenuItem addItem(int i, int i2, int i3, Drawable drawable, int i4) {
        View actionBarMenuItem = new ActionBarMenuItem(getContext(), this, i3, this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor);
        actionBarMenuItem.setTag(Integer.valueOf(i));
        if (drawable != null) {
            actionBarMenuItem.iconView.setImageDrawable(drawable);
        } else if (i2 != 0) {
            actionBarMenuItem.iconView.setImageResource(i2);
        }
        addView(actionBarMenuItem, new LayoutParams(i4, -1));
        actionBarMenuItem.setOnClickListener(new C07211());
        return actionBarMenuItem;
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

    protected void setPopupItemsColor(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).setPopupItemsColor(i);
            }
        }
    }

    protected void redrawPopup(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).redrawPopup(i);
            }
        }
    }

    public void onItemClick(int i) {
        if (this.parentActionBar.actionBarMenuOnItemClick != null) {
            this.parentActionBar.actionBarMenuOnItemClick.onItemClick(i);
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
                if (actionBarMenuItem.getVisibility() == 0) {
                    if (actionBarMenuItem.hasSubMenu()) {
                        actionBarMenuItem.toggleSubMenu();
                        return;
                    } else if (actionBarMenuItem.overrideMenuClick) {
                        onItemClick(((Integer) actionBarMenuItem.getTag()).intValue());
                        return;
                    }
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
                if (actionBarMenuItem.isSearchField()) {
                    this.parentActionBar.onSearchFieldVisibilityChanged(false);
                    actionBarMenuItem.toggleSearch(z);
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

    public void openSearchField(boolean z, String str) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    if (z) {
                        this.parentActionBar.onSearchFieldVisibilityChanged(actionBarMenuItem.toggleSearch(true));
                    }
                    actionBarMenuItem.getSearchField().setText(str);
                    actionBarMenuItem.getSearchField().setSelection(str.length());
                    return;
                }
            }
        }
    }

    public ActionBarMenuItem getItem(int i) {
        i = findViewWithTag(Integer.valueOf(i));
        return i instanceof ActionBarMenuItem ? (ActionBarMenuItem) i : 0;
    }
}
