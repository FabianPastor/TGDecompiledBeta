package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarMenuItem extends FrameLayout {
    private int additionalOffset;
    private boolean allowCloseAnimation = true;
    private boolean animateClear = true;
    private boolean animationEnabled = true;
    private ImageView clearButton;
    private ActionBarMenuItemDelegate delegate;
    protected ImageView iconView;
    private boolean ignoreOnTextChange;
    private boolean isSearchField;
    private boolean layoutInScreen;
    private ActionBarMenuItemSearchListener listener;
    private int[] location;
    private boolean longClickEnabled = true;
    protected boolean overrideMenuClick;
    private ActionBarMenu parentMenu;
    private ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private CloseProgressDrawable2 progressDrawable;
    private Rect rect;
    private FrameLayout searchContainer;
    private EditTextBoldCursor searchField;
    private TextView searchFieldCaption;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private int subMenuOpenSide;
    private int yOffset;

    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    public static class ActionBarMenuItemSearchListener {
        public boolean canCollapseSearch() {
            return true;
        }

        public boolean forceShowClear() {
            return false;
        }

        public void onCaptionCleared() {
        }

        public void onSearchCollapse() {
        }

        public void onSearchExpand() {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onTextChanged(EditText editText) {
        }
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2) {
        super(context);
        if (i != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(i));
        }
        this.parentMenu = actionBarMenu;
        this.iconView = new ImageView(context);
        this.iconView.setScaleType(ScaleType.CENTER);
        addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
        if (i2 != 0) {
            this.iconView.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        }
    }

    public void setLongClickEnabled(boolean z) {
        this.longClickEnabled = z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu()) {
                actionBarPopupWindow = this.popupWindow;
                if (actionBarPopupWindow == null || !(actionBarPopupWindow == null || actionBarPopupWindow.isShowing())) {
                    this.showMenuRunnable = new -$$Lambda$ActionBarMenuItem$Y4Ro71_Kozj7zLr98hi2EHrr4-A(this);
                    AndroidUtilities.runOnUIThread(this.showMenuRunnable, 200);
                }
            }
        } else if (motionEvent.getActionMasked() == 2) {
            if (hasSubMenu()) {
                actionBarPopupWindow = this.popupWindow;
                if (actionBarPopupWindow == null || !(actionBarPopupWindow == null || actionBarPopupWindow.isShowing())) {
                    if (motionEvent.getY() > ((float) getHeight())) {
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        toggleSubMenu();
                        return true;
                    }
                }
            }
            actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                getLocationOnScreen(this.location);
                float x = motionEvent.getX() + ((float) this.location[0]);
                float y = motionEvent.getY();
                int[] iArr = this.location;
                y += (float) iArr[1];
                this.popupLayout.getLocationOnScreen(iArr);
                iArr = this.location;
                x -= (float) iArr[0];
                y -= (float) iArr[1];
                this.selectedMenuView = null;
                for (int i = 0; i < this.popupLayout.getItemsCount(); i++) {
                    View itemAt = this.popupLayout.getItemAt(i);
                    itemAt.getHitRect(this.rect);
                    if (((Integer) itemAt.getTag()).intValue() < 100) {
                        if (this.rect.contains((int) x, (int) y)) {
                            itemAt.setPressed(true);
                            itemAt.setSelected(true);
                            int i2 = VERSION.SDK_INT;
                            if (i2 >= 21) {
                                if (i2 == 21) {
                                    itemAt.getBackground().setVisible(true, false);
                                }
                                itemAt.drawableHotspotChanged(x, y - ((float) itemAt.getTop()));
                            }
                            this.selectedMenuView = itemAt;
                        } else {
                            itemAt.setPressed(false);
                            itemAt.setSelected(false);
                            if (VERSION.SDK_INT == 21) {
                                itemAt.getBackground().setVisible(false, false);
                            }
                        }
                    }
                }
            }
        } else {
            actionBarPopupWindow = this.popupWindow;
            View view;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing() && motionEvent.getActionMasked() == 1) {
                view = this.selectedMenuView;
                if (view != null) {
                    view.setSelected(false);
                    ActionBarMenu actionBarMenu = this.parentMenu;
                    if (actionBarMenu != null) {
                        actionBarMenu.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                    } else {
                        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
                        if (actionBarMenuItemDelegate != null) {
                            actionBarMenuItemDelegate.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                        }
                    }
                    this.popupWindow.dismiss(this.allowCloseAnimation);
                } else {
                    this.popupWindow.dismiss();
                }
            } else {
                view = this.selectedMenuView;
                if (view != null) {
                    view.setSelected(false);
                    this.selectedMenuView = null;
                }
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public /* synthetic */ void lambda$onTouchEvent$0$ActionBarMenuItem() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        toggleSubMenu();
    }

    public void setDelegate(ActionBarMenuItemDelegate actionBarMenuItemDelegate) {
        this.delegate = actionBarMenuItemDelegate;
    }

    public void setIconColor(int i) {
        this.iconView.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
        ImageView imageView = this.clearButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
        }
    }

    public void setSubMenuOpenSide(int i) {
        this.subMenuOpenSide = i;
    }

    public void setLayoutInScreen(boolean z) {
        this.layoutInScreen = z;
    }

    private void createPopupLayout() {
        if (this.popupLayout == null) {
            this.rect = new Rect();
            this.location = new int[2];
            this.popupLayout = new ActionBarPopupWindowLayout(getContext());
            this.popupLayout.setOnTouchListener(new -$$Lambda$ActionBarMenuItem$NEgOkBBuNcW0duabIaP5FYMgH5w(this));
            this.popupLayout.setDispatchKeyEventListener(new -$$Lambda$ActionBarMenuItem$yNUlqFN00bPmohrcR1AdkblquoQ(this));
        }
    }

    public /* synthetic */ boolean lambda$createPopupLayout$1$ActionBarMenuItem(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                view.getHitRect(this.rect);
                if (!this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    this.popupWindow.dismiss();
                }
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$createPopupLayout$2$ActionBarMenuItem(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.popupWindow.dismiss();
            }
        }
    }

    public void addSubItem(View view, int i, int i2) {
        createPopupLayout();
        this.popupLayout.addView(view, new LayoutParams(i, i2));
    }

    public void addSubItem(int i, View view, int i2, int i3) {
        createPopupLayout();
        view.setLayoutParams(new LayoutParams(i2, i3));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(i));
        view.setOnClickListener(new -$$Lambda$ActionBarMenuItem$9Z-bd6EyKyDSvICVkiunmSXpJnY(this));
        view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    }

    public /* synthetic */ void lambda$addSubItem$3$ActionBarMenuItem(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (!this.processedPopupClick) {
                this.processedPopupClick = true;
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                return;
            }
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
        } else {
            ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
            if (actionBarMenuItemDelegate != null) {
                actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
            }
        }
    }

    public TextView addSubItem(int i, CharSequence charSequence) {
        createPopupLayout();
        TextView textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (LocaleController.isRTL) {
            textView.setGravity(21);
        } else {
            textView.setGravity(16);
        }
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView.setTextSize(1, 16.0f);
        textView.setMinWidth(AndroidUtilities.dp(196.0f));
        textView.setTag(Integer.valueOf(i));
        textView.setText(charSequence);
        this.popupLayout.addView(textView);
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new -$$Lambda$ActionBarMenuItem$2OXLLOK5tdU8k_iUBLX7uda6XhM(this));
        return textView;
    }

    public /* synthetic */ void lambda$addSubItem$4$ActionBarMenuItem(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (!this.processedPopupClick) {
                this.processedPopupClick = true;
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                return;
            }
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
        } else {
            ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
            if (actionBarMenuItemDelegate != null) {
                actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
            }
        }
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence) {
        createPopupLayout();
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext());
        actionBarMenuSubItem.setTextAndIcon(charSequence, i2);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarMenuSubItem.setTag(Integer.valueOf(i));
        this.popupLayout.addView(actionBarMenuSubItem);
        LayoutParams layoutParams = (LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        actionBarMenuSubItem.setOnClickListener(new -$$Lambda$ActionBarMenuItem$37smKkAzmohk63TzSJQopQovar_I(this));
        return actionBarMenuSubItem;
    }

    public /* synthetic */ void lambda$addSubItem$5$ActionBarMenuItem(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (!this.processedPopupClick) {
                this.processedPopupClick = true;
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                return;
            }
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
        } else {
            ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
            if (actionBarMenuItemDelegate != null) {
                actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
            }
        }
    }

    public void redrawPopup(int i) {
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.backgroundDrawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
            this.popupLayout.invalidate();
        }
    }

    public void setPopupItemsColor(int i) {
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            int childCount = actionBarPopupWindowLayout.linearLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.popupLayout.linearLayout.getChildAt(i2);
                if (childAt instanceof TextView) {
                    ((TextView) childAt).setTextColor(i);
                } else if (childAt instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) childAt).setTextColor(i);
                }
            }
        }
    }

    public boolean hasSubMenu() {
        return this.popupLayout != null;
    }

    public void setMenuYOffset(int i) {
        this.yOffset = i;
    }

    /* JADX WARNING: Missing block: B:9:0x0014, code skipped:
            if (r0.isActionModeShowed() == false) goto L_0x00cc;
     */
    public void toggleSubMenu() {
        /*
        r6 = this;
        r0 = r6.popupLayout;
        if (r0 == 0) goto L_0x00cc;
    L_0x0004:
        r0 = r6.parentMenu;
        if (r0 == 0) goto L_0x0018;
    L_0x0008:
        r1 = r0.isActionMode;
        if (r1 == 0) goto L_0x0018;
    L_0x000c:
        r0 = r0.parentActionBar;
        if (r0 == 0) goto L_0x0018;
    L_0x0010:
        r0 = r0.isActionModeShowed();
        if (r0 != 0) goto L_0x0018;
    L_0x0016:
        goto L_0x00cc;
    L_0x0018:
        r0 = r6.showMenuRunnable;
        if (r0 == 0) goto L_0x0022;
    L_0x001c:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r0 = 0;
        r6.showMenuRunnable = r0;
    L_0x0022:
        r0 = r6.popupWindow;
        if (r0 == 0) goto L_0x0032;
    L_0x0026:
        r0 = r0.isShowing();
        if (r0 == 0) goto L_0x0032;
    L_0x002c:
        r0 = r6.popupWindow;
        r0.dismiss();
        return;
    L_0x0032:
        r0 = r6.popupWindow;
        r1 = 0;
        r2 = 1;
        if (r0 != 0) goto L_0x00b1;
    L_0x0038:
        r0 = new org.telegram.ui.ActionBar.ActionBarPopupWindow;
        r3 = r6.popupLayout;
        r4 = -2;
        r0.<init>(r3, r4, r4);
        r6.popupWindow = r0;
        r0 = r6.animationEnabled;
        if (r0 == 0) goto L_0x0052;
    L_0x0046:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 19;
        if (r0 < r3) goto L_0x0052;
    L_0x004c:
        r0 = r6.popupWindow;
        r0.setAnimationStyle(r1);
        goto L_0x005a;
    L_0x0052:
        r0 = r6.popupWindow;
        r3 = NUM; // 0x7f0e00b1 float:1.8875397E38 double:1.053162244E-314;
        r0.setAnimationStyle(r3);
    L_0x005a:
        r0 = r6.animationEnabled;
        if (r0 != 0) goto L_0x0063;
    L_0x005e:
        r3 = r6.popupWindow;
        r3.setAnimationEnabled(r0);
    L_0x0063:
        r0 = r6.popupWindow;
        r0.setOutsideTouchable(r2);
        r0 = r6.popupWindow;
        r0.setClippingEnabled(r2);
        r0 = r6.layoutInScreen;
        if (r0 == 0) goto L_0x0076;
    L_0x0071:
        r0 = r6.popupWindow;
        r0.setLayoutInScreen(r2);
    L_0x0076:
        r0 = r6.popupWindow;
        r3 = 2;
        r0.setInputMethodMode(r3);
        r0 = r6.popupWindow;
        r0.setSoftInputMode(r1);
        r0 = r6.popupLayout;
        r3 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r5);
        r0.measure(r4, r3);
        r0 = r6.popupWindow;
        r0 = r0.getContentView();
        r0.setFocusableInTouchMode(r2);
        r0 = r6.popupWindow;
        r0 = r0.getContentView();
        r3 = new org.telegram.ui.ActionBar.-$$Lambda$ActionBarMenuItem$9YLDoQyZnlPz968V4zO2iegYPb0;
        r3.<init>(r6);
        r0.setOnKeyListener(r3);
    L_0x00b1:
        r6.processedPopupClick = r1;
        r0 = r6.popupWindow;
        r0.setFocusable(r2);
        r0 = r6.popupLayout;
        r0 = r0.getMeasuredWidth();
        if (r0 != 0) goto L_0x00c4;
    L_0x00c0:
        r6.updateOrShowPopup(r2, r2);
        goto L_0x00c7;
    L_0x00c4:
        r6.updateOrShowPopup(r2, r1);
    L_0x00c7:
        r0 = r6.popupWindow;
        r0.startAnimation();
    L_0x00cc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarMenuItem.toggleSubMenu():void");
    }

    public /* synthetic */ boolean lambda$toggleSubMenu$6$ActionBarMenuItem(View view, int i, KeyEvent keyEvent) {
        if (i == 82 && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 1) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.popupWindow.dismiss();
                return true;
            }
        }
        return false;
    }

    public void openSearch(boolean z) {
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null && frameLayout.getVisibility() != 0) {
            ActionBarMenu actionBarMenu = this.parentMenu;
            if (actionBarMenu != null) {
                actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(z));
            }
        }
    }

    public boolean toggleSearch(boolean z) {
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout == null) {
            return false;
        }
        String str = "";
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        if (frameLayout.getVisibility() == 0) {
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
            if (actionBarMenuItemSearchListener2 == null || (actionBarMenuItemSearchListener2 != null && actionBarMenuItemSearchListener2.canCollapseSearch())) {
                if (z) {
                    AndroidUtilities.hideKeyboard(this.searchField);
                }
                this.searchField.setText(str);
                this.searchContainer.setVisibility(8);
                this.searchField.clearFocus();
                setVisibility(0);
                actionBarMenuItemSearchListener = this.listener;
                if (actionBarMenuItemSearchListener != null) {
                    actionBarMenuItemSearchListener.onSearchCollapse();
                }
            }
            return false;
        }
        this.searchContainer.setVisibility(0);
        setVisibility(8);
        this.searchField.setText(str);
        this.searchField.requestFocus();
        if (z) {
            AndroidUtilities.showKeyboard(this.searchField);
        }
        actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onSearchExpand();
        }
        return true;
    }

    public void closeSubMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void setIcon(int i) {
        this.iconView.setImageResource(i);
    }

    public void setIcon(Drawable drawable) {
        this.iconView.setImageDrawable(drawable);
    }

    public ImageView getImageView() {
        return this.iconView;
    }

    public void setSearchFieldHint(CharSequence charSequence) {
        if (this.searchFieldCaption != null) {
            this.searchField.setHint(charSequence);
            setContentDescription(charSequence);
        }
    }

    public void setSearchFieldText(CharSequence charSequence, boolean z) {
        if (this.searchFieldCaption != null) {
            this.animateClear = z;
            this.searchField.setText(charSequence);
            if (!TextUtils.isEmpty(charSequence)) {
                this.searchField.setSelection(charSequence.length());
            }
        }
    }

    public EditTextBoldCursor getSearchField() {
        return this.searchField;
    }

    public ActionBarMenuItem setOverrideMenuClick(boolean z) {
        this.overrideMenuClick = z;
        return this;
    }

    public ActionBarMenuItem setIsSearchField(boolean z) {
        if (this.parentMenu == null) {
            return this;
        }
        if (z && this.searchContainer == null) {
            this.searchContainer = new FrameLayout(getContext()) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    int measuredWidth;
                    measureChildWithMargins(ActionBarMenuItem.this.clearButton, i, 0, i2, 0);
                    if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i, MeasureSpec.getSize(i) / 2, i2, 0);
                        measuredWidth = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        measuredWidth = 0;
                    }
                    measureChildWithMargins(ActionBarMenuItem.this.searchField, i, measuredWidth, i2, 0);
                    MeasureSpec.getSize(i);
                    MeasureSpec.getSize(i2);
                    setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                }

                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    i = 0;
                    if (!LocaleController.isRTL && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        i = AndroidUtilities.dp(4.0f) + ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth();
                    }
                    ActionBarMenuItem.this.searchField.layout(i, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + i, ActionBarMenuItem.this.searchField.getBottom());
                }
            };
            this.parentMenu.addView(this.searchContainer, 0, LayoutHelper.createLinear(0, -1, 1.0f, 6, 0, 0, 0));
            this.searchContainer.setVisibility(8);
            this.searchFieldCaption = new TextView(getContext());
            this.searchFieldCaption.setTextSize(1, 18.0f);
            String str = "actionBarDefaultSearch";
            this.searchFieldCaption.setTextColor(Theme.getColor(str));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            this.searchField = new EditTextBoldCursor(getContext()) {
                public boolean onKeyDown(int i, KeyEvent keyEvent) {
                    if (i != 67 || ActionBarMenuItem.this.searchField.length() != 0 || ActionBarMenuItem.this.searchFieldCaption.getVisibility() != 0 || ActionBarMenuItem.this.searchFieldCaption.length() <= 0) {
                        return super.onKeyDown(i, keyEvent);
                    }
                    ActionBarMenuItem.this.clearButton.callOnClick();
                    return true;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.searchField.setCursorWidth(1.5f);
            this.searchField.setCursorColor(-1);
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(Theme.getColor("actionBarDefaultSearchPlaceholder"));
            this.searchField.setTextColor(Theme.getColor(str));
            this.searchField.setSingleLine(true);
            this.searchField.setBackgroundResource(0);
            this.searchField.setPadding(0, 0, 0, 0);
            this.searchField.setInputType(this.searchField.getInputType() | 524288);
            if (VERSION.SDK_INT < 23) {
                this.searchField.setCustomSelectionActionModeCallback(new Callback() {
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        return false;
                    }

                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode actionMode) {
                    }

                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }
                });
            }
            this.searchField.setOnEditorActionListener(new -$$Lambda$ActionBarMenuItem$DSACM5xoXBBb-9TAnJG5eS-F3HQ(this));
            this.searchField.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (ActionBarMenuItem.this.ignoreOnTextChange) {
                        ActionBarMenuItem.this.ignoreOnTextChange = false;
                        return;
                    }
                    if (ActionBarMenuItem.this.listener != null) {
                        ActionBarMenuItem.this.listener.onTextChanged(ActionBarMenuItem.this.searchField);
                    }
                    if (ActionBarMenuItem.this.clearButton != null) {
                        if (!TextUtils.isEmpty(charSequence) || ((ActionBarMenuItem.this.listener != null && ActionBarMenuItem.this.listener.forceShowClear()) || (ActionBarMenuItem.this.searchFieldCaption != null && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0))) {
                            if (ActionBarMenuItem.this.clearButton.getTag() == null) {
                                ActionBarMenuItem.this.clearButton.setTag(Integer.valueOf(1));
                                ActionBarMenuItem.this.clearButton.clearAnimation();
                                ActionBarMenuItem.this.clearButton.setVisibility(0);
                                if (ActionBarMenuItem.this.animateClear) {
                                    ActionBarMenuItem.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                                } else {
                                    ActionBarMenuItem.this.clearButton.setAlpha(1.0f);
                                    ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                                    ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                                    ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                                    ActionBarMenuItem.this.animateClear = true;
                                }
                            }
                        } else if (ActionBarMenuItem.this.clearButton.getTag() != null) {
                            ActionBarMenuItem.this.clearButton.setTag(null);
                            ActionBarMenuItem.this.clearButton.clearAnimation();
                            if (ActionBarMenuItem.this.animateClear) {
                                ActionBarMenuItem.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new -$$Lambda$ActionBarMenuItem$4$WTfAvwLi4l2rOq09wt3w9zvbBZY(this)).start();
                            } else {
                                ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                                ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                                ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                                ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                                ActionBarMenuItem.this.clearButton.setVisibility(4);
                                ActionBarMenuItem.this.animateClear = true;
                            }
                        }
                    }
                }

                public /* synthetic */ void lambda$onTextChanged$0$ActionBarMenuItem$4() {
                    ActionBarMenuItem.this.clearButton.setVisibility(4);
                }
            });
            this.searchField.setImeOptions(33554435);
            this.searchField.setTextIsSelectable(false);
            if (LocaleController.isRTL) {
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 21, 0.0f, 5.5f, 48.0f, 0.0f));
            } else {
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 19, 0.0f, 5.5f, 0.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
            }
            this.clearButton = new ImageView(getContext()) {
                /* Access modifiers changed, original: protected */
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    clearAnimation();
                    if (getTag() == null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(4);
                        ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                        ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                        ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                        ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                        return;
                    }
                    ActionBarMenuItem.this.clearButton.setAlpha(1.0f);
                    ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                    ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                    ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                }
            };
            ImageView imageView = this.clearButton;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.clearButton.setColorFilter(new PorterDuffColorFilter(this.parentMenu.parentActionBar.itemsColor, Mode.MULTIPLY));
            this.clearButton.setScaleType(ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new -$$Lambda$ActionBarMenuItem$_MHVU-Pdp5nAX3_6TiPCB165nO8(this));
            this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
            this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
        }
        this.isSearchField = z;
        return this;
    }

    public /* synthetic */ boolean lambda$setIsSearchField$7$ActionBarMenuItem(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent != null && ((keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 84) || (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66))) {
            AndroidUtilities.hideKeyboard(this.searchField);
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
            if (actionBarMenuItemSearchListener != null) {
                actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$setIsSearchField$8$ActionBarMenuItem(View view) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        } else {
            TextView textView = this.searchFieldCaption;
            if (textView != null && textView.getVisibility() == 0) {
                this.searchFieldCaption.setVisibility(8);
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
                if (actionBarMenuItemSearchListener != null) {
                    actionBarMenuItemSearchListener.onCaptionCleared();
                }
            }
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public void setShowSearchProgress(boolean z) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 != null) {
            if (z) {
                closeProgressDrawable2.startAnimation();
            } else {
                closeProgressDrawable2.stopAnimation();
            }
        }
    }

    public void setSearchFieldCaption(CharSequence charSequence) {
        if (this.searchFieldCaption != null) {
            if (TextUtils.isEmpty(charSequence)) {
                this.searchFieldCaption.setVisibility(8);
            } else {
                this.searchFieldCaption.setVisibility(0);
                this.searchFieldCaption.setText(charSequence);
            }
        }
    }

    public void setIgnoreOnTextChange() {
        this.ignoreOnTextChange = true;
    }

    public boolean isSearchField() {
        return this.isSearchField;
    }

    public void clearSearchText() {
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.setText("");
        }
    }

    public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener actionBarMenuItemSearchListener) {
        this.listener = actionBarMenuItemSearchListener;
        return this;
    }

    public ActionBarMenuItem setAllowCloseAnimation(boolean z) {
        this.allowCloseAnimation = z;
        return this;
    }

    public void setPopupAnimationEnabled(boolean z) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setAnimationEnabled(z);
        }
        this.animationEnabled = z;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
    }

    public void setAdditionalOffset(int i) {
        this.additionalOffset = i;
    }

    private void updateOrShowPopup(boolean z, boolean z2) {
        int top;
        int paddingTop;
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            top = (-actionBarMenu.parentActionBar.getMeasuredHeight()) + this.parentMenu.getTop();
            paddingTop = this.parentMenu.getPaddingTop();
        } else {
            float scaleY = getScaleY();
            top = -((int) ((((float) getMeasuredHeight()) * scaleY) - ((this.subMenuOpenSide != 2 ? getTranslationY() : 0.0f) / scaleY)));
            paddingTop = this.additionalOffset;
        }
        int i = (top + paddingTop) + this.yOffset;
        if (z) {
            this.popupLayout.scrollToTop();
        }
        actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            ActionBar actionBar = actionBarMenu.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (z) {
                    this.popupWindow.showAsDropDown(actionBar, (((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + ((int) getTranslationX()), i);
                }
                if (z2) {
                    this.popupWindow.update(actionBar, (((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + ((int) getTranslationX()), i, -1, -1);
                    return;
                }
                return;
            }
            if (z) {
                this.popupWindow.showAsDropDown(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i);
            }
            if (z2) {
                this.popupWindow.update(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i, -1, -1);
                return;
            }
            return;
        }
        top = this.subMenuOpenSide;
        if (top == 0) {
            if (getParent() != null) {
                View view = (View) getParent();
                if (z) {
                    this.popupWindow.showAsDropDown(view, (getLeft() + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth(), i);
                }
                if (z2) {
                    this.popupWindow.update(view, (getLeft() + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth(), i, -1, -1);
                }
            }
        } else if (top == 1) {
            if (z) {
                this.popupWindow.showAsDropDown(this, -AndroidUtilities.dp(8.0f), i);
            }
            if (z2) {
                this.popupWindow.update(this, -AndroidUtilities.dp(8.0f), i, -1, -1);
            }
        } else {
            if (z) {
                this.popupWindow.showAsDropDown(this, getMeasuredWidth() - this.popupLayout.getMeasuredWidth(), i);
            }
            if (z2) {
                this.popupWindow.update(this, getMeasuredWidth() - this.popupLayout.getMeasuredWidth(), i, -1, -1);
            }
        }
    }

    public void hideSubItem(int i) {
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            View findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i));
            if (!(findViewWithTag == null || findViewWithTag.getVisibility() == 8)) {
                findViewWithTag.setVisibility(8);
            }
        }
    }

    public boolean isSubItemVisible(int i) {
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        boolean z = false;
        if (actionBarPopupWindowLayout == null) {
            return false;
        }
        View findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i));
        if (findViewWithTag != null && findViewWithTag.getVisibility() == 0) {
            z = true;
        }
        return z;
    }

    public void showSubItem(int i) {
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            View findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i));
            if (!(findViewWithTag == null || findViewWithTag.getVisibility() == 0)) {
                findViewWithTag.setVisibility(0);
            }
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.ImageButton");
    }
}
