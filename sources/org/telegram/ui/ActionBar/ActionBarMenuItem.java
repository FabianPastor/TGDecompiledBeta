package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarMenuItem extends FrameLayout {
    private int additionalXOffset;
    private int additionalYOffset;
    private boolean allowCloseAnimation;
    /* access modifiers changed from: private */
    public boolean animateClear;
    private boolean animationEnabled;
    /* access modifiers changed from: private */
    public ImageView clearButton;
    private boolean clearsTextOnSearchCollapse;
    private ActionBarMenuItemDelegate delegate;
    protected ImageView iconView;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    private boolean isSearchField;
    private boolean layoutInScreen;
    protected ActionBarMenuItemSearchListener listener;
    private int[] location;
    private boolean longClickEnabled;
    private boolean measurePopup;
    protected boolean overrideMenuClick;
    private ActionBarMenu parentMenu;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private CloseProgressDrawable2 progressDrawable;
    private Rect rect;
    private FrameLayout searchContainer;
    /* access modifiers changed from: private */
    public EditTextBoldCursor searchField;
    /* access modifiers changed from: private */
    public TextView searchFieldCaption;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private ActionBarSumMenuItemDelegate subMenuDelegate;
    private int subMenuOpenSide;
    protected TextView textView;
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

        public Animator getCustomToggleTransition() {
            return null;
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

    public interface ActionBarSumMenuItemDelegate {
        void onShowSubMenu();
    }

    /* access modifiers changed from: protected */
    public void onDismiss() {
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2) {
        this(context, actionBarMenu, i, i2, false);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, boolean z) {
        super(context);
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.longClickEnabled = true;
        this.animateClear = true;
        this.clearsTextOnSearchCollapse = true;
        this.measurePopup = true;
        if (i != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(i, z ? 5 : 1));
        }
        this.parentMenu = actionBarMenu;
        if (z) {
            this.textView = new TextView(context);
            this.textView.setTextSize(1, 15.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            if (i2 != 0) {
                this.textView.setTextColor(i2);
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f));
            return;
        }
        this.iconView = new ImageView(context);
        this.iconView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
        if (i2 != 0) {
            this.iconView.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setLongClickEnabled(boolean z) {
        this.longClickEnabled = z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        ActionBarPopupWindow actionBarPopupWindow2;
        if (motionEvent.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow2 = this.popupWindow) == null || (actionBarPopupWindow2 != null && !actionBarPopupWindow2.isShowing()))) {
                this.showMenuRunnable = new Runnable() {
                    public final void run() {
                        ActionBarMenuItem.this.lambda$onTouchEvent$0$ActionBarMenuItem();
                    }
                };
                AndroidUtilities.runOnUIThread(this.showMenuRunnable, 200);
            }
        } else if (motionEvent.getActionMasked() != 2) {
            ActionBarPopupWindow actionBarPopupWindow3 = this.popupWindow;
            if (actionBarPopupWindow3 == null || !actionBarPopupWindow3.isShowing() || motionEvent.getActionMasked() != 1) {
                View view = this.selectedMenuView;
                if (view != null) {
                    view.setSelected(false);
                    this.selectedMenuView = null;
                }
            } else {
                View view2 = this.selectedMenuView;
                if (view2 != null) {
                    view2.setSelected(false);
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
            }
        } else if (!hasSubMenu() || ((actionBarPopupWindow = this.popupWindow) != null && (actionBarPopupWindow == null || actionBarPopupWindow.isShowing()))) {
            ActionBarPopupWindow actionBarPopupWindow4 = this.popupWindow;
            if (actionBarPopupWindow4 != null && actionBarPopupWindow4.isShowing()) {
                getLocationOnScreen(this.location);
                float x = motionEvent.getX() + ((float) this.location[0]);
                float y = motionEvent.getY();
                int[] iArr = this.location;
                float f = y + ((float) iArr[1]);
                this.popupLayout.getLocationOnScreen(iArr);
                int[] iArr2 = this.location;
                float f2 = x - ((float) iArr2[0]);
                float f3 = f - ((float) iArr2[1]);
                this.selectedMenuView = null;
                for (int i = 0; i < this.popupLayout.getItemsCount(); i++) {
                    View itemAt = this.popupLayout.getItemAt(i);
                    itemAt.getHitRect(this.rect);
                    if (((Integer) itemAt.getTag()).intValue() < 100) {
                        if (!this.rect.contains((int) f2, (int) f3)) {
                            itemAt.setPressed(false);
                            itemAt.setSelected(false);
                            if (Build.VERSION.SDK_INT == 21) {
                                itemAt.getBackground().setVisible(false, false);
                            }
                        } else {
                            itemAt.setPressed(true);
                            itemAt.setSelected(true);
                            int i2 = Build.VERSION.SDK_INT;
                            if (i2 >= 21) {
                                if (i2 == 21) {
                                    itemAt.getBackground().setVisible(true, false);
                                }
                                itemAt.drawableHotspotChanged(f2, f3 - ((float) itemAt.getTop()));
                            }
                            this.selectedMenuView = itemAt;
                        }
                    }
                }
            }
        } else if (motionEvent.getY() > ((float) getHeight())) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            toggleSubMenu();
            return true;
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

    public void setSubMenuDelegate(ActionBarSumMenuItemDelegate actionBarSumMenuItemDelegate) {
        this.subMenuDelegate = actionBarSumMenuItemDelegate;
    }

    public void setIconColor(int i) {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setTextColor(i);
        }
        ImageView imageView2 = this.clearButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
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
            this.popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
            this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ActionBarMenuItem.this.lambda$createPopupLayout$1$ActionBarMenuItem(view, motionEvent);
                }
            });
            this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    ActionBarMenuItem.this.lambda$createPopupLayout$2$ActionBarMenuItem(keyEvent);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$createPopupLayout$1$ActionBarMenuItem(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.rect);
        if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public /* synthetic */ void lambda$createPopupLayout$2$ActionBarMenuItem(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void removeAllSubItems() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.removeInnerViews();
        }
    }

    public void addSubItem(View view, int i, int i2) {
        createPopupLayout();
        this.popupLayout.addView(view, new LinearLayout.LayoutParams(i, i2));
    }

    public void addSubItem(int i, View view, int i2, int i3) {
        createPopupLayout();
        view.setLayoutParams(new LinearLayout.LayoutParams(i2, i3));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(i));
        view.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$3$ActionBarMenuItem(view);
            }
        });
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
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public TextView addSubItem(int i, CharSequence charSequence) {
        createPopupLayout();
        TextView textView2 = new TextView(getContext());
        textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        textView2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (!LocaleController.isRTL) {
            textView2.setGravity(16);
        } else {
            textView2.setGravity(21);
        }
        textView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView2.setTextSize(1, 16.0f);
        textView2.setMinWidth(AndroidUtilities.dp(196.0f));
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setTag(Integer.valueOf(i));
        textView2.setText(charSequence);
        this.popupLayout.addView(textView2);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView2.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView2.setLayoutParams(layoutParams);
        textView2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$4$ActionBarMenuItem(view);
            }
        });
        return textView2;
    }

    public /* synthetic */ void lambda$addSubItem$4$ActionBarMenuItem(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (!this.processedPopupClick) {
                this.processedPopupClick = true;
                if (!this.allowCloseAnimation) {
                    this.popupWindow.setAnimationStyle(NUM);
                }
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                return;
            }
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence) {
        createPopupLayout();
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext());
        actionBarMenuSubItem.setTextAndIcon(charSequence, i2);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarMenuSubItem.setTag(Integer.valueOf(i));
        this.popupLayout.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$5$ActionBarMenuItem(view);
            }
        });
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
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void redrawPopup(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && actionBarPopupWindowLayout.getBackgroundColor() != i) {
            this.popupLayout.setBackgroundColor(i);
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.popupLayout.invalidate();
            }
        }
    }

    public void setPopupItemsColor(int i, boolean z) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            LinearLayout linearLayout = actionBarPopupWindowLayout.linearLayout;
            int childCount = linearLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = linearLayout.getChildAt(i2);
                if (childAt instanceof TextView) {
                    ((TextView) childAt).setTextColor(i);
                } else if (childAt instanceof ActionBarMenuSubItem) {
                    if (z) {
                        ((ActionBarMenuSubItem) childAt).setIconColor(i);
                    } else {
                        ((ActionBarMenuSubItem) childAt).setTextColor(i);
                    }
                }
            }
        }
    }

    public void setPopupItemsSelectorColor(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            LinearLayout linearLayout = actionBarPopupWindowLayout.linearLayout;
            int childCount = linearLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = linearLayout.getChildAt(i2);
                if (childAt instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) childAt).setSelectorColor(i);
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

    public void toggleSubMenu() {
        ActionBar actionBar;
        if (this.popupLayout != null) {
            ActionBarMenu actionBarMenu = this.parentMenu;
            if (actionBarMenu == null || !actionBarMenu.isActionMode || (actionBar = actionBarMenu.parentActionBar) == null || actionBar.isActionModeShowed()) {
                Runnable runnable = this.showMenuRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.showMenuRunnable = null;
                }
                ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
                if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
                    ActionBarSumMenuItemDelegate actionBarSumMenuItemDelegate = this.subMenuDelegate;
                    if (actionBarSumMenuItemDelegate != null) {
                        actionBarSumMenuItemDelegate.onShowSubMenu();
                    }
                    if (this.popupWindow == null) {
                        this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                        if (!this.animationEnabled || Build.VERSION.SDK_INT < 19) {
                            this.popupWindow.setAnimationStyle(NUM);
                        } else {
                            this.popupWindow.setAnimationStyle(0);
                        }
                        boolean z = this.animationEnabled;
                        if (!z) {
                            this.popupWindow.setAnimationEnabled(z);
                        }
                        this.popupWindow.setOutsideTouchable(true);
                        this.popupWindow.setClippingEnabled(true);
                        if (this.layoutInScreen) {
                            this.popupWindow.setLayoutInScreen(true);
                        }
                        this.popupWindow.setInputMethodMode(2);
                        this.popupWindow.setSoftInputMode(0);
                        this.popupWindow.getContentView().setFocusableInTouchMode(true);
                        this.popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                                return ActionBarMenuItem.this.lambda$toggleSubMenu$6$ActionBarMenuItem(view, i, keyEvent);
                            }
                        });
                        this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            public final void onDismiss() {
                                ActionBarMenuItem.this.onDismiss();
                            }
                        });
                    }
                    if (this.measurePopup) {
                        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
                        this.measurePopup = false;
                    }
                    this.processedPopupClick = false;
                    this.popupWindow.setFocusable(true);
                    if (this.popupLayout.getMeasuredWidth() == 0) {
                        updateOrShowPopup(true, true);
                    } else {
                        updateOrShowPopup(true, false);
                    }
                    this.popupWindow.startAnimation();
                    return;
                }
                this.popupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ boolean lambda$toggleSubMenu$6$ActionBarMenuItem(View view, int i, KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        return true;
    }

    public void openSearch(boolean z) {
        ActionBarMenu actionBarMenu;
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null && frameLayout.getVisibility() != 0 && (actionBarMenu = this.parentMenu) != null) {
            actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(z));
        }
    }

    public boolean toggleSearch(boolean z) {
        Animator customToggleTransition;
        if (this.searchContainer == null) {
            return false;
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null && (customToggleTransition = actionBarMenuItemSearchListener.getCustomToggleTransition()) != null) {
            this.searchField.setText("");
            customToggleTransition.start();
            return true;
        } else if (this.searchContainer.getVisibility() == 0) {
            if (z) {
                AndroidUtilities.hideKeyboard(this.searchField);
            }
            if (this.clearsTextOnSearchCollapse) {
                this.searchField.setText("");
            }
            this.searchContainer.setVisibility(8);
            this.searchField.clearFocus();
            setVisibility(0);
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
            if (actionBarMenuItemSearchListener2 != null) {
                actionBarMenuItemSearchListener2.onSearchCollapse();
            }
            return false;
        } else {
            this.searchContainer.setVisibility(0);
            setVisibility(8);
            this.searchField.setText("");
            this.searchField.requestFocus();
            if (z) {
                AndroidUtilities.showKeyboard(this.searchField);
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener3 = this.listener;
            if (actionBarMenuItemSearchListener3 != null) {
                actionBarMenuItemSearchListener3.onSearchExpand();
            }
            return true;
        }
    }

    public void setClearsTextOnSearchCollapse(boolean z) {
        this.clearsTextOnSearchCollapse = z;
    }

    public void closeSubMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void setIcon(Drawable drawable) {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    public void setIcon(int i) {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            imageView.setImageResource(i);
        }
    }

    public void setText(CharSequence charSequence) {
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setText(charSequence);
        }
    }

    public View getContentView() {
        ImageView imageView = this.iconView;
        return imageView != null ? imageView : this.textView;
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

    public void onSearchPressed() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
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
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int i3;
                    measureChildWithMargins(ActionBarMenuItem.this.clearButton, i, 0, i2, 0);
                    if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i, View.MeasureSpec.getSize(i) / 2, i2, 0);
                        i3 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        i3 = 0;
                    }
                    measureChildWithMargins(ActionBarMenuItem.this.searchField, i, i3, i2, 0);
                    View.MeasureSpec.getSize(i);
                    View.MeasureSpec.getSize(i2);
                    setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    int i5 = 0;
                    if (!LocaleController.isRTL && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        i5 = AndroidUtilities.dp(4.0f) + ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth();
                    }
                    ActionBarMenuItem.this.searchField.layout(i5, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + i5, ActionBarMenuItem.this.searchField.getBottom());
                }
            };
            this.parentMenu.addView(this.searchContainer, 0, LayoutHelper.createLinear(0, -1, 1.0f, 6, 0, 0, 0));
            this.searchContainer.setVisibility(8);
            this.searchFieldCaption = new TextView(getContext());
            this.searchFieldCaption.setTextSize(1, 18.0f);
            this.searchFieldCaption.setTextColor(Theme.getColor("actionBarDefaultSearch"));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
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
            this.searchField.setCursorColor(Theme.getColor("actionBarDefaultSearch"));
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(Theme.getColor("actionBarDefaultSearchPlaceholder"));
            this.searchField.setTextColor(Theme.getColor("actionBarDefaultSearch"));
            this.searchField.setSingleLine(true);
            this.searchField.setBackgroundResource(0);
            this.searchField.setPadding(0, 0, 0, 0);
            this.searchField.setInputType(this.searchField.getInputType() | 524288);
            if (Build.VERSION.SDK_INT < 23) {
                this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
            this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ActionBarMenuItem.this.lambda$setIsSearchField$7$ActionBarMenuItem(textView, i, keyEvent);
                }
            });
            this.searchField.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
                    if (ActionBarMenuItem.this.ignoreOnTextChange) {
                        boolean unused = ActionBarMenuItem.this.ignoreOnTextChange = false;
                        return;
                    }
                    ActionBarMenuItem actionBarMenuItem = ActionBarMenuItem.this;
                    ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = actionBarMenuItem.listener;
                    if (actionBarMenuItemSearchListener2 != null) {
                        actionBarMenuItemSearchListener2.onTextChanged(actionBarMenuItem.searchField);
                    }
                    if (ActionBarMenuItem.this.clearButton == null) {
                        return;
                    }
                    if (!TextUtils.isEmpty(charSequence) || (((actionBarMenuItemSearchListener = ActionBarMenuItem.this.listener) != null && actionBarMenuItemSearchListener.forceShowClear()) || (ActionBarMenuItem.this.searchFieldCaption != null && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0))) {
                        if (ActionBarMenuItem.this.clearButton.getTag() == null) {
                            ActionBarMenuItem.this.clearButton.setTag(1);
                            ActionBarMenuItem.this.clearButton.clearAnimation();
                            ActionBarMenuItem.this.clearButton.setVisibility(0);
                            if (ActionBarMenuItem.this.animateClear) {
                                ActionBarMenuItem.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                                return;
                            }
                            ActionBarMenuItem.this.clearButton.setAlpha(1.0f);
                            ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                            ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                            ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                            boolean unused2 = ActionBarMenuItem.this.animateClear = true;
                        }
                    } else if (ActionBarMenuItem.this.clearButton.getTag() != null) {
                        ActionBarMenuItem.this.clearButton.setTag((Object) null);
                        ActionBarMenuItem.this.clearButton.clearAnimation();
                        if (ActionBarMenuItem.this.animateClear) {
                            ActionBarMenuItem.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new Runnable() {
                                public final void run() {
                                    ActionBarMenuItem.AnonymousClass4.this.lambda$onTextChanged$0$ActionBarMenuItem$4();
                                }
                            }).start();
                            return;
                        }
                        ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                        ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                        ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                        ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                        ActionBarMenuItem.this.clearButton.setVisibility(4);
                        boolean unused3 = ActionBarMenuItem.this.animateClear = true;
                    }
                }

                public /* synthetic */ void lambda$onTextChanged$0$ActionBarMenuItem$4() {
                    ActionBarMenuItem.this.clearButton.setVisibility(4);
                }
            });
            this.searchField.setImeOptions(33554435);
            this.searchField.setTextIsSelectable(false);
            if (!LocaleController.isRTL) {
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 19, 0.0f, 5.5f, 0.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
            } else {
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 21, 0.0f, 5.5f, 48.0f, 0.0f));
            }
            this.clearButton = new ImageView(getContext()) {
                /* access modifiers changed from: protected */
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
            this.clearButton.setColorFilter(new PorterDuffColorFilter(this.parentMenu.parentActionBar.itemsColor, PorterDuff.Mode.MULTIPLY));
            this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ActionBarMenuItem.this.lambda$setIsSearchField$8$ActionBarMenuItem(view);
                }
            });
            this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
            this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
        }
        this.isSearchField = z;
        return this;
    }

    public /* synthetic */ boolean lambda$setIsSearchField$7$ActionBarMenuItem(TextView textView2, int i, KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchField);
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener == null) {
            return false;
        }
        actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        return false;
    }

    public /* synthetic */ void lambda$setIsSearchField$8$ActionBarMenuItem(View view) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        } else {
            TextView textView2 = this.searchFieldCaption;
            if (textView2 != null && textView2.getVisibility() == 0) {
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
                return;
            }
            this.searchFieldCaption.setVisibility(0);
            this.searchFieldCaption.setText(charSequence);
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

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
    }

    public void setAdditionalYOffset(int i) {
        this.additionalYOffset = i;
    }

    public void setAdditionalXOffset(int i) {
        this.additionalXOffset = i;
    }

    private void updateOrShowPopup(boolean z, boolean z2) {
        int i;
        int i2;
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            i = (-actionBarMenu.parentActionBar.getMeasuredHeight()) + this.parentMenu.getTop();
            i2 = this.parentMenu.getPaddingTop();
        } else {
            float scaleY = getScaleY();
            i = -((int) ((((float) getMeasuredHeight()) * scaleY) - ((this.subMenuOpenSide != 2 ? getTranslationY() : 0.0f) / scaleY)));
            i2 = this.additionalYOffset;
        }
        int i3 = i + i2 + this.yOffset;
        if (z) {
            this.popupLayout.scrollToTop();
        }
        ActionBarMenu actionBarMenu2 = this.parentMenu;
        if (actionBarMenu2 != null) {
            ActionBar actionBar = actionBarMenu2.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (z) {
                    this.popupWindow.showAsDropDown(actionBar, (((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + ((int) getTranslationX()), i3);
                }
                if (z2) {
                    this.popupWindow.update(actionBar, (((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + ((int) getTranslationX()), i3, -1, -1);
                    return;
                }
                return;
            }
            if (z) {
                this.popupWindow.showAsDropDown(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i3);
            }
            if (z2) {
                this.popupWindow.update(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i3, -1, -1);
                return;
            }
            return;
        }
        int i4 = this.subMenuOpenSide;
        if (i4 == 0) {
            if (getParent() != null) {
                View view = (View) getParent();
                if (z) {
                    this.popupWindow.showAsDropDown(view, ((getLeft() + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + this.additionalXOffset, i3);
                }
                if (z2) {
                    this.popupWindow.update(view, ((getLeft() + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + this.additionalXOffset, i3, -1, -1);
                }
            }
        } else if (i4 == 1) {
            if (z) {
                this.popupWindow.showAsDropDown(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset, i3);
            }
            if (z2) {
                this.popupWindow.update(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset, i3, -1, -1);
            }
        } else {
            if (z) {
                this.popupWindow.showAsDropDown(this, (getMeasuredWidth() - this.popupLayout.getMeasuredWidth()) + this.additionalXOffset, i3);
            }
            if (z2) {
                this.popupWindow.update(this, (getMeasuredWidth() - this.popupLayout.getMeasuredWidth()) + this.additionalXOffset, i3, -1, -1);
            }
        }
    }

    public void hideSubItem(int i) {
        View findViewWithTag;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) != null && findViewWithTag.getVisibility() != 8) {
            findViewWithTag.setVisibility(8);
            this.measurePopup = true;
        }
    }

    public boolean isSubItemVisible(int i) {
        View findViewWithTag;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || (findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) == null || findViewWithTag.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    public void showSubItem(int i) {
        View findViewWithTag;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) != null && findViewWithTag.getVisibility() != 0) {
            findViewWithTag.setVisibility(0);
            this.measurePopup = true;
        }
    }

    public void requestFocusOnSearchView() {
        if (this.searchContainer.getWidth() != 0 && !this.searchField.isFocused()) {
            this.searchField.requestFocus();
            AndroidUtilities.showKeyboard(this.searchField);
        }
    }

    public void clearFocusOnSearchView() {
        this.searchField.clearFocus();
        AndroidUtilities.hideKeyboard(this.searchField);
    }

    public FrameLayout getSearchContainer() {
        return this.searchContainer;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.ImageButton");
    }
}
