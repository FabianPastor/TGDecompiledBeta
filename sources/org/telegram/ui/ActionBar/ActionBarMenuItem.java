package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarMenuItem extends FrameLayout {
    private int additionalXOffset;
    private int additionalYOffset;
    private boolean allowCloseAnimation;
    private boolean animateClear;
    private boolean animationEnabled;
    /* access modifiers changed from: private */
    public ImageView clearButton;
    private boolean clearsTextOnSearchCollapse;
    /* access modifiers changed from: private */
    public ArrayList<FiltersView.MediaFilterData> currentSearchFilters;
    private ActionBarMenuItemDelegate delegate;
    private boolean forceSmoothKeyboard;
    protected ImageView iconView;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    private boolean isSearchField;
    private boolean layoutInScreen;
    protected ActionBarMenuItemSearchListener listener;
    private int[] location;
    private boolean longClickEnabled;
    private boolean measurePopup;
    /* access modifiers changed from: private */
    public int notificationIndex;
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
    /* access modifiers changed from: private */
    public LinearLayout searchFilterLayout;
    /* access modifiers changed from: private */
    public int selectedFilterIndex;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private boolean showSubmenuByMove;
    private ActionBarSubMenuItemDelegate subMenuDelegate;
    private int subMenuOpenSide;
    protected TextView textView;
    /* access modifiers changed from: private */
    public FrameLayout wrappedSearchFrameLayout;
    private int yOffset;

    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    public static class ActionBarMenuItemSearchListener {
        public boolean canCollapseSearch() {
            return true;
        }

        public boolean canToggleSearch() {
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

        public void onLayout(int i, int i2, int i3, int i4) {
        }

        public void onSearchCollapse() {
        }

        public void onSearchExpand() {
        }

        public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onTextChanged(EditText editText) {
        }
    }

    public interface ActionBarSubMenuItemDelegate {
        void onHideSubMenu();

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
        new ArrayList();
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.animateClear = true;
        this.clearsTextOnSearchCollapse = true;
        this.measurePopup = true;
        this.showSubmenuByMove = true;
        this.currentSearchFilters = new ArrayList<>();
        this.selectedFilterIndex = -1;
        this.notificationIndex = -1;
        if (i != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(i, z ? 5 : 1));
        }
        this.parentMenu = actionBarMenu;
        if (z) {
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            this.textView.setImportantForAccessibility(2);
            if (i2 != 0) {
                this.textView.setTextColor(i2);
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f));
            return;
        }
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.iconView.setImportantForAccessibility(2);
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
        int i = Build.VERSION.SDK_INT;
        if (motionEvent.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow2 = this.popupWindow) == null || !actionBarPopupWindow2.isShowing())) {
                $$Lambda$ActionBarMenuItem$97ELGBMVBlZJv0MzTuldUHOShYc r0 = new Runnable() {
                    public final void run() {
                        ActionBarMenuItem.this.lambda$onTouchEvent$0$ActionBarMenuItem();
                    }
                };
                this.showMenuRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 200);
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
        } else if (!this.showSubmenuByMove || !hasSubMenu() || ((actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing())) {
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
                for (int i2 = 0; i2 < this.popupLayout.getItemsCount(); i2++) {
                    View itemAt = this.popupLayout.getItemAt(i2);
                    itemAt.getHitRect(this.rect);
                    Object tag = itemAt.getTag();
                    if ((tag instanceof Integer) && ((Integer) tag).intValue() < 100) {
                        if (!this.rect.contains((int) f2, (int) f3)) {
                            itemAt.setPressed(false);
                            itemAt.setSelected(false);
                            if (i == 21) {
                                itemAt.getBackground().setVisible(false, false);
                            }
                        } else {
                            itemAt.setPressed(true);
                            itemAt.setSelected(true);
                            if (i >= 21) {
                                if (i == 21) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTouchEvent$0 */
    public /* synthetic */ void lambda$onTouchEvent$0$ActionBarMenuItem() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        toggleSubMenu();
    }

    public void setDelegate(ActionBarMenuItemDelegate actionBarMenuItemDelegate) {
        this.delegate = actionBarMenuItemDelegate;
    }

    public void setSubMenuDelegate(ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate) {
        this.subMenuDelegate = actionBarSubMenuItemDelegate;
    }

    public void setShowSubmenuByMove(boolean z) {
        this.showSubmenuByMove = z;
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

    public void setForceSmoothKeyboard(boolean z) {
        this.forceSmoothKeyboard = z;
    }

    private void createPopupLayout() {
        if (this.popupLayout == null) {
            this.rect = new Rect();
            this.location = new int[2];
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createPopupLayout$1 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createPopupLayout$2 */
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

    public void setShowedFromBottom(boolean z) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setShowedFromBotton(z);
        }
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$addSubItem$3 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$addSubItem$4 */
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
        return addSubItem(i, i2, charSequence, false);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence, boolean z) {
        createPopupLayout();
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), z, false, false);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$addSubItem$5 */
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

    public View addDivider(int i) {
        createPopupLayout();
        TextView textView2 = new TextView(getContext());
        textView2.setBackgroundColor(i);
        textView2.setMinimumWidth(AndroidUtilities.dp(196.0f));
        this.popupLayout.addView(textView2);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView2.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = 1;
        int dp = AndroidUtilities.dp(3.0f);
        layoutParams.bottomMargin = dp;
        layoutParams.topMargin = dp;
        textView2.setLayoutParams(layoutParams);
        return textView2;
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

    public void setupPopupRadialSelectors(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setupRadialSelectors(i);
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
                    ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
                    if (actionBarSubMenuItemDelegate != null) {
                        actionBarSubMenuItemDelegate.onShowSubMenu();
                    }
                    if (this.popupWindow == null) {
                        ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                        this.popupWindow = actionBarPopupWindow2;
                        if (!this.animationEnabled || Build.VERSION.SDK_INT < 19) {
                            actionBarPopupWindow2.setAnimationStyle(NUM);
                        } else {
                            actionBarPopupWindow2.setAnimationStyle(0);
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
                                ActionBarMenuItem.this.lambda$toggleSubMenu$7$ActionBarMenuItem();
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
                    this.popupLayout.updateRadialSelectors();
                    this.popupWindow.startAnimation();
                    return;
                }
                this.popupWindow.dismiss();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleSubMenu$6 */
    public /* synthetic */ boolean lambda$toggleSubMenu$6$ActionBarMenuItem(View view, int i, KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleSubMenu$7 */
    public /* synthetic */ void lambda$toggleSubMenu$7$ActionBarMenuItem() {
        onDismiss();
        ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
        if (actionBarSubMenuItemDelegate != null) {
            actionBarSubMenuItemDelegate.onHideSubMenu();
        }
    }

    public void openSearch(boolean z) {
        ActionBarMenu actionBarMenu;
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null && frameLayout.getVisibility() != 0 && (actionBarMenu = this.parentMenu) != null) {
            actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(z));
        }
    }

    public boolean isSearchFieldVisible() {
        return this.searchContainer.getVisibility() == 0;
    }

    public boolean toggleSearch(boolean z) {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        Animator customToggleTransition;
        if (this.searchContainer == null || ((actionBarMenuItemSearchListener = this.listener) != null && !actionBarMenuItemSearchListener.canToggleSearch())) {
            return false;
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
        if (actionBarMenuItemSearchListener2 != null && (customToggleTransition = actionBarMenuItemSearchListener2.getCustomToggleTransition()) != null) {
            this.searchField.setText("");
            customToggleTransition.start();
            return true;
        } else if (this.searchContainer.getVisibility() == 0) {
            this.searchContainer.setVisibility(8);
            this.searchField.clearFocus();
            setVisibility(0);
            if (!this.currentSearchFilters.isEmpty()) {
                if (this.listener != null) {
                    for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                        if (this.currentSearchFilters.get(i).removable) {
                            this.listener.onSearchFilterCleared(this.currentSearchFilters.get(i));
                        }
                    }
                }
                clearSearchFilters();
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener3 = this.listener;
            if (actionBarMenuItemSearchListener3 != null) {
                actionBarMenuItemSearchListener3.onSearchCollapse();
            }
            if (z) {
                AndroidUtilities.hideKeyboard(this.searchField);
            }
            if (this.clearsTextOnSearchCollapse) {
                this.searchField.setText("");
            }
            return false;
        } else {
            this.searchContainer.setVisibility(0);
            this.searchContainer.setAlpha(1.0f);
            setVisibility(8);
            this.searchField.setText("");
            this.searchField.requestFocus();
            if (z) {
                AndroidUtilities.showKeyboard(this.searchField);
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener4 = this.listener;
            if (actionBarMenuItemSearchListener4 != null) {
                actionBarMenuItemSearchListener4.onSearchExpand();
            }
            return true;
        }
    }

    public void removeSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        if (mediaFilterData.removable) {
            this.currentSearchFilters.remove(mediaFilterData);
            int i = this.selectedFilterIndex;
            if (i < 0 || i > this.currentSearchFilters.size() - 1) {
                this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
            }
            onFiltersChanged();
            this.searchField.hideActionMode();
        }
    }

    public void addSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        this.currentSearchFilters.add(mediaFilterData);
        this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
        onFiltersChanged();
    }

    public void clearSearchFilters() {
        int i = 0;
        while (i < this.currentSearchFilters.size()) {
            if (this.currentSearchFilters.get(i).removable) {
                this.currentSearchFilters.remove(i);
                i--;
            }
            i++;
        }
        onFiltersChanged();
    }

    /* access modifiers changed from: private */
    public void onFiltersChanged() {
        boolean z = !this.currentSearchFilters.isEmpty();
        ArrayList arrayList = new ArrayList(this.currentSearchFilters);
        if (Build.VERSION.SDK_INT >= 19) {
            TransitionSet transitionSet = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150);
            transitionSet.addTransition(new Visibility(this) {
                public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    if (!(view instanceof SearchFilterView)) {
                        return ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f});
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.5f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.5f, 1.0f})});
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    return animatorSet;
                }

                public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    if (!(view instanceof SearchFilterView)) {
                        return ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f});
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{view.getAlpha(), 0.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{view.getScaleX(), 0.5f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{view.getScaleX(), 0.5f})});
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    return animatorSet;
                }
            }.setDuration(150)).addTransition(changeBounds);
            transitionSet.setOrdering(0);
            transitionSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            final int i = UserConfig.selectedAccount;
            transitionSet.addListener(new Transition.TransitionListener() {
                public void onTransitionPause(Transition transition) {
                }

                public void onTransitionResume(Transition transition) {
                }

                public void onTransitionStart(Transition transition) {
                    int unused = ActionBarMenuItem.this.notificationIndex = NotificationCenter.getInstance(i).setAnimationInProgress(ActionBarMenuItem.this.notificationIndex, (int[]) null);
                }

                public void onTransitionEnd(Transition transition) {
                    NotificationCenter.getInstance(i).onAnimationFinish(ActionBarMenuItem.this.notificationIndex);
                }

                public void onTransitionCancel(Transition transition) {
                    NotificationCenter.getInstance(i).onAnimationFinish(ActionBarMenuItem.this.notificationIndex);
                }
            });
            TransitionManager.beginDelayedTransition(this.searchFilterLayout, transitionSet);
        }
        int i2 = 0;
        while (i2 < this.searchFilterLayout.getChildCount()) {
            if (!arrayList.remove(((SearchFilterView) this.searchFilterLayout.getChildAt(i2)).getFilter())) {
                this.searchFilterLayout.removeViewAt(i2);
                i2--;
            }
            i2++;
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SearchFilterView searchFilterView = new SearchFilterView(getContext());
            searchFilterView.setData((FiltersView.MediaFilterData) arrayList.get(i3));
            searchFilterView.setOnClickListener(new View.OnClickListener(searchFilterView) {
                public final /* synthetic */ ActionBarMenuItem.SearchFilterView f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ActionBarMenuItem.this.lambda$onFiltersChanged$8$ActionBarMenuItem(this.f$1, view);
                }
            });
            this.searchFilterLayout.addView(searchFilterView, LayoutHelper.createLinear(-2, -1, 0, 0, 0, 6, 0));
        }
        int i4 = 0;
        while (i4 < this.searchFilterLayout.getChildCount()) {
            ((SearchFilterView) this.searchFilterLayout.getChildAt(i4)).setExpanded(i4 == this.selectedFilterIndex);
            i4++;
        }
        this.searchFilterLayout.setTag(z ? 1 : null);
        final float x = this.searchField.getX();
        this.searchField.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ActionBarMenuItem.this.searchField.getViewTreeObserver().removeOnPreDrawListener(this);
                if (ActionBarMenuItem.this.searchField.getX() != x) {
                    ActionBarMenuItem.this.searchField.setTranslationX(x - ActionBarMenuItem.this.searchField.getX());
                }
                ActionBarMenuItem.this.searchField.animate().translationX(0.0f).setDuration(250).setStartDelay(0).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                return true;
            }
        });
        checkClearButton();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFiltersChanged$8 */
    public /* synthetic */ void lambda$onFiltersChanged$8$ActionBarMenuItem(SearchFilterView searchFilterView, View view) {
        int indexOf = this.currentSearchFilters.indexOf(searchFilterView.getFilter());
        if (this.selectedFilterIndex != indexOf) {
            this.selectedFilterIndex = indexOf;
            onFiltersChanged();
        } else if (!searchFilterView.getFilter().removable) {
        } else {
            if (!searchFilterView.selectedForDelete) {
                searchFilterView.setSelectedForDelete(true);
                return;
            }
            FiltersView.MediaFilterData filter = searchFilterView.getFilter();
            removeSearchFilter(filter);
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
            if (actionBarMenuItemSearchListener != null) {
                actionBarMenuItemSearchListener.onSearchFilterCleared(filter);
                this.listener.onTextChanged(this.searchField);
            }
        }
    }

    public void setClearsTextOnSearchCollapse(boolean z) {
        this.clearsTextOnSearchCollapse = z;
    }

    public boolean isSubMenuShowing() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        return actionBarPopupWindow != null && actionBarPopupWindow.isShowing();
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

    public ImageView getIconView() {
        return this.iconView;
    }

    public TextView getTextView() {
        return this.textView;
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
        setIsSearchField(z, false);
        return this;
    }

    public ActionBarMenuItem setIsSearchField(boolean z, final boolean z2) {
        if (this.parentMenu == null) {
            return this;
        }
        if (z && this.searchContainer == null) {
            AnonymousClass4 r0 = new FrameLayout(getContext()) {
                private boolean ignoreRequestLayout;

                public void setVisibility(int i) {
                    super.setVisibility(i);
                    if (ActionBarMenuItem.this.clearButton != null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(i);
                    }
                    if (ActionBarMenuItem.this.wrappedSearchFrameLayout != null) {
                        ActionBarMenuItem.this.wrappedSearchFrameLayout.setVisibility(i);
                    }
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int i3;
                    int i4;
                    if (!z2) {
                        measureChildWithMargins(ActionBarMenuItem.this.clearButton, i, 0, i2, 0);
                    }
                    if (!LocaleController.isRTL) {
                        if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                            measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i, View.MeasureSpec.getSize(i) / 2, i2, 0);
                            i4 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                        } else {
                            i4 = 0;
                        }
                        int size = View.MeasureSpec.getSize(i);
                        this.ignoreRequestLayout = true;
                        measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, i, i4, i2, 0);
                        int measuredWidth = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                        measureChildWithMargins(ActionBarMenuItem.this.searchField, i, i4 + measuredWidth, i2, 0);
                        this.ignoreRequestLayout = false;
                        setMeasuredDimension(Math.max(measuredWidth + ActionBarMenuItem.this.searchField.getMeasuredWidth(), size), View.MeasureSpec.getSize(i2));
                        return;
                    }
                    if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i, View.MeasureSpec.getSize(i) / 2, i2, 0);
                        i3 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        i3 = 0;
                    }
                    int size2 = View.MeasureSpec.getSize(i);
                    this.ignoreRequestLayout = true;
                    measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, i, i3, i2, 0);
                    int measuredWidth2 = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                    measureChildWithMargins(ActionBarMenuItem.this.searchField, View.MeasureSpec.makeMeasureSpec(size2 - AndroidUtilities.dp(6.0f), 0), i3 + measuredWidth2, i2, 0);
                    this.ignoreRequestLayout = false;
                    setMeasuredDimension(Math.max(measuredWidth2 + ActionBarMenuItem.this.searchField.getMeasuredWidth(), size2), View.MeasureSpec.getSize(i2));
                }

                public void requestLayout() {
                    if (!this.ignoreRequestLayout) {
                        super.requestLayout();
                    }
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    int i5 = 0;
                    if (!LocaleController.isRTL && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        i5 = AndroidUtilities.dp(4.0f) + ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth();
                    }
                    if (ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0) {
                        i5 += ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth();
                    }
                    ActionBarMenuItem.this.searchField.layout(i5, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + i5, ActionBarMenuItem.this.searchField.getBottom());
                }
            };
            this.searchContainer = r0;
            r0.setClipChildren(false);
            this.wrappedSearchFrameLayout = null;
            if (z2) {
                this.wrappedSearchFrameLayout = new FrameLayout(getContext());
                AnonymousClass5 r2 = new HorizontalScrollView(this, getContext()) {
                    boolean isDragging;

                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        checkDragg(motionEvent);
                        return super.onInterceptTouchEvent(motionEvent);
                    }

                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        checkDragg(motionEvent);
                        return super.onTouchEvent(motionEvent);
                    }

                    private void checkDragg(MotionEvent motionEvent) {
                        if (motionEvent.getAction() == 0) {
                            this.isDragging = true;
                        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                            this.isDragging = false;
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onOverScrolled(int i, int i2, boolean z, boolean z2) {
                        if (this.isDragging) {
                            super.onOverScrolled(i, i2, z, z2);
                        }
                    }
                };
                r2.addView(this.searchContainer, LayoutHelper.createScroll(-2, -1, 0));
                r2.setHorizontalScrollBarEnabled(false);
                r2.setClipChildren(false);
                this.wrappedSearchFrameLayout.addView(r2, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 48.0f, 0.0f));
                this.parentMenu.addView(this.wrappedSearchFrameLayout, 0, LayoutHelper.createLinear(0, -1, 1.0f, 0, 0, 0, 0));
            } else {
                this.parentMenu.addView(this.searchContainer, 0, LayoutHelper.createLinear(0, -1, 1.0f, 6, 0, 0, 0));
            }
            this.searchContainer.setVisibility(8);
            TextView textView2 = new TextView(getContext());
            this.searchFieldCaption = textView2;
            textView2.setTextSize(1, 18.0f);
            this.searchFieldCaption.setTextColor(Theme.getColor("actionBarDefaultSearch"));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            AnonymousClass6 r22 = new EditTextBoldCursor(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    setMeasuredDimension(Math.max(View.MeasureSpec.getSize(i), getMeasuredWidth()) + AndroidUtilities.dp(3.0f), getMeasuredHeight());
                }

                /* access modifiers changed from: protected */
                public void onSelectionChanged(int i, int i2) {
                    super.onSelectionChanged(i, i2);
                }

                public boolean onKeyDown(int i, KeyEvent keyEvent) {
                    if (i != 67 || ActionBarMenuItem.this.searchField.length() != 0 || ((ActionBarMenuItem.this.searchFieldCaption.getVisibility() != 0 || ActionBarMenuItem.this.searchFieldCaption.length() <= 0) && !ActionBarMenuItem.this.hasRemovableFilters())) {
                        return super.onKeyDown(i, keyEvent);
                    }
                    if (ActionBarMenuItem.this.hasRemovableFilters()) {
                        FiltersView.MediaFilterData mediaFilterData = (FiltersView.MediaFilterData) ActionBarMenuItem.this.currentSearchFilters.get(ActionBarMenuItem.this.currentSearchFilters.size() - 1);
                        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = ActionBarMenuItem.this.listener;
                        if (actionBarMenuItemSearchListener != null) {
                            actionBarMenuItemSearchListener.onSearchFilterCleared(mediaFilterData);
                        }
                        ActionBarMenuItem.this.removeSearchFilter(mediaFilterData);
                    } else {
                        ActionBarMenuItem.this.clearButton.callOnClick();
                    }
                    return true;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    boolean onTouchEvent = super.onTouchEvent(motionEvent);
                    if (motionEvent.getAction() == 1 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return onTouchEvent;
                }
            };
            this.searchField = r22;
            r22.setScrollContainer(false);
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
                this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback(this) {
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
                    return ActionBarMenuItem.this.lambda$setIsSearchField$9$ActionBarMenuItem(textView, i, keyEvent);
                }
            });
            this.searchField.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (ActionBarMenuItem.this.ignoreOnTextChange) {
                        boolean unused = ActionBarMenuItem.this.ignoreOnTextChange = false;
                        return;
                    }
                    ActionBarMenuItem actionBarMenuItem = ActionBarMenuItem.this;
                    ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = actionBarMenuItem.listener;
                    if (actionBarMenuItemSearchListener != null) {
                        actionBarMenuItemSearchListener.onTextChanged(actionBarMenuItem.searchField);
                    }
                    ActionBarMenuItem.this.checkClearButton();
                    if (!ActionBarMenuItem.this.currentSearchFilters.isEmpty() && !TextUtils.isEmpty(ActionBarMenuItem.this.searchField.getText()) && ActionBarMenuItem.this.selectedFilterIndex >= 0) {
                        int unused2 = ActionBarMenuItem.this.selectedFilterIndex = -1;
                        ActionBarMenuItem.this.onFiltersChanged();
                    }
                }
            });
            this.searchField.setImeOptions(33554435);
            this.searchField.setTextIsSelectable(false);
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.searchFilterLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.searchFilterLayout.setVisibility(0);
            if (!LocaleController.isRTL) {
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 19, 0.0f, 5.5f, 0.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-2, 36.0f, 16, 6.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFilterLayout, LayoutHelper.createFrame(-2, 32.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
            } else {
                this.searchContainer.addView(this.searchFilterLayout, LayoutHelper.createFrame(-2, 32.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-2, 36.0f, 16, 0.0f, 0.0f, z2 ? 0.0f : 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 21, 0.0f, 5.5f, 48.0f, 0.0f));
            }
            this.searchFilterLayout.setClipChildren(false);
            AnonymousClass9 r1 = new ImageView(getContext()) {
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
            this.clearButton = r1;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            r1.setImageDrawable(closeProgressDrawable2);
            this.clearButton.setColorFilter(new PorterDuffColorFilter(this.parentMenu.parentActionBar.itemsColor, PorterDuff.Mode.MULTIPLY));
            this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ActionBarMenuItem.this.lambda$setIsSearchField$10$ActionBarMenuItem(view);
                }
            });
            this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
            if (z2) {
                this.wrappedSearchFrameLayout.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            } else {
                this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            }
        }
        this.isSearchField = z;
        return this;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setIsSearchField$9 */
    public /* synthetic */ boolean lambda$setIsSearchField$9$ActionBarMenuItem(TextView textView2, int i, KeyEvent keyEvent) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$setIsSearchField$10 */
    public /* synthetic */ void lambda$setIsSearchField$10$ActionBarMenuItem(View view) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        } else if (hasRemovableFilters()) {
            this.searchField.hideActionMode();
            for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                if (this.listener != null && this.currentSearchFilters.get(i).removable) {
                    this.listener.onSearchFilterCleared(this.currentSearchFilters.get(i));
                }
            }
            clearSearchFilters();
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

    /* access modifiers changed from: private */
    public void checkClearButton() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        TextView textView2;
        if (this.clearButton == null) {
            return;
        }
        if (hasRemovableFilters() || !TextUtils.isEmpty(this.searchField.getText()) || (((actionBarMenuItemSearchListener = this.listener) != null && actionBarMenuItemSearchListener.forceShowClear()) || ((textView2 = this.searchFieldCaption) != null && textView2.getVisibility() == 0))) {
            if (this.clearButton.getTag() == null) {
                this.clearButton.setTag(1);
                this.clearButton.clearAnimation();
                this.clearButton.setVisibility(0);
                if (this.animateClear) {
                    this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                    return;
                }
                this.clearButton.setAlpha(1.0f);
                this.clearButton.setRotation(0.0f);
                this.clearButton.setScaleX(1.0f);
                this.clearButton.setScaleY(1.0f);
                this.animateClear = true;
            }
        } else if (this.clearButton.getTag() != null) {
            this.clearButton.setTag((Object) null);
            this.clearButton.clearAnimation();
            if (this.animateClear) {
                this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new Runnable() {
                    public final void run() {
                        ActionBarMenuItem.this.lambda$checkClearButton$11$ActionBarMenuItem();
                    }
                }).start();
                return;
            }
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setVisibility(4);
            this.animateClear = true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkClearButton$11 */
    public /* synthetic */ void lambda$checkClearButton$11$ActionBarMenuItem() {
        this.clearButton.setVisibility(4);
    }

    /* access modifiers changed from: private */
    public boolean hasRemovableFilters() {
        if (this.currentSearchFilters.isEmpty()) {
            return false;
        }
        for (int i = 0; i < this.currentSearchFilters.size(); i++) {
            if (this.currentSearchFilters.get(i).removable) {
                return true;
            }
        }
        return false;
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
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onLayout(i, i2, i3, i4);
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
                if (this.forceSmoothKeyboard) {
                    this.popupWindow.showAtLocation(actionBar, 51, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i3);
                } else {
                    this.popupWindow.showAsDropDown(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i3);
                }
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
        if (this.iconView != null) {
            accessibilityNodeInfo.setClassName("android.widget.ImageButton");
        } else if (this.textView != null) {
            accessibilityNodeInfo.setClassName("android.widget.Button");
            if (TextUtils.isEmpty(accessibilityNodeInfo.getText())) {
                accessibilityNodeInfo.setText(this.textView.getText());
            }
        }
    }

    public void updateColor() {
        if (this.searchFilterLayout != null) {
            for (int i = 0; i < this.searchFilterLayout.getChildCount(); i++) {
                if (this.searchFilterLayout.getChildAt(i) instanceof SearchFilterView) {
                    ((SearchFilterView) this.searchFilterLayout.getChildAt(i)).updateColors();
                }
            }
        }
    }

    public void collapseSearchFilters() {
        this.selectedFilterIndex = -1;
        onFiltersChanged();
    }

    private static class SearchFilterView extends FrameLayout {
        BackupImageView avatarImageView;
        ImageView closeIconView;
        FiltersView.MediaFilterData data;
        Runnable removeSelectionRunnable = new Runnable() {
            public void run() {
                if (SearchFilterView.this.selectedForDelete) {
                    SearchFilterView.this.setSelectedForDelete(false);
                }
            }
        };
        ValueAnimator selectAnimator;
        /* access modifiers changed from: private */
        public boolean selectedForDelete;
        /* access modifiers changed from: private */
        public float selectedProgress;
        ShapeDrawable shapeDrawable;
        Drawable thumbDrawable;
        TextView titleView;

        public SearchFilterView(Context context) {
            super(context);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f));
            ImageView imageView = new ImageView(context);
            this.closeIconView = imageView;
            imageView.setImageResource(NUM);
            addView(this.closeIconView, LayoutHelper.createFrame(24, 24.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 38.0f, 0.0f, 16.0f, 0.0f));
            ShapeDrawable shapeDrawable2 = (ShapeDrawable) Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), -12292204);
            this.shapeDrawable = shapeDrawable2;
            setBackground(shapeDrawable2);
            updateColors();
        }

        /* access modifiers changed from: private */
        public void updateColors() {
            int color = Theme.getColor("groupcreate_spanBackground");
            int color2 = Theme.getColor("avatar_backgroundBlue");
            int color3 = Theme.getColor("windowBackgroundWhiteBlackText");
            int color4 = Theme.getColor("avatar_actionBarIconBlue");
            this.shapeDrawable.getPaint().setColor(ColorUtils.blendARGB(color, color2, this.selectedProgress));
            this.titleView.setTextColor(ColorUtils.blendARGB(color3, color4, this.selectedProgress));
            this.closeIconView.setColorFilter(color4);
            this.closeIconView.setAlpha(this.selectedProgress);
            this.closeIconView.setScaleX(this.selectedProgress * 0.82f);
            this.closeIconView.setScaleY(this.selectedProgress * 0.82f);
            Drawable drawable = this.thumbDrawable;
            if (drawable != null) {
                Theme.setCombinedDrawableColor(drawable, Theme.getColor("avatar_backgroundBlue"), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, Theme.getColor("avatar_actionBarIconBlue"), true);
            }
            this.avatarImageView.setAlpha(1.0f - this.selectedProgress);
            FiltersView.MediaFilterData mediaFilterData = this.data;
            if (mediaFilterData != null && mediaFilterData.filterType == 7) {
                setData(mediaFilterData);
            }
            invalidate();
        }

        public void setData(FiltersView.MediaFilterData mediaFilterData) {
            this.data = mediaFilterData;
            this.titleView.setText(mediaFilterData.title);
            CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), mediaFilterData.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, Theme.getColor("avatar_backgroundBlue"), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, Theme.getColor("avatar_actionBarIconBlue"), true);
            int i = mediaFilterData.filterType;
            if (i == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == tLRPC$User.id) {
                        CombinedDrawable createCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                        createCircleDrawableWithIcon2.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, Theme.getColor("avatar_backgroundSaved"), false);
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, Theme.getColor("avatar_actionBarIconBlue"), true);
                        this.avatarImageView.setImageDrawable(createCircleDrawableWithIcon2);
                        return;
                    }
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", this.thumbDrawable, (String) null, tLRPC$User, 0);
                } else if (tLObject instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setImage(ImageLocation.getForChat(tLRPC$Chat, false), "50_50", this.thumbDrawable, (String) null, tLRPC$Chat, 0);
                }
            } else if (i == 7) {
                CombinedDrawable createCircleDrawableWithIcon3 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                createCircleDrawableWithIcon3.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon3, Theme.getColor("avatar_backgroundArchived"), false);
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon3, Theme.getColor("avatar_actionBarIconBlue"), true);
                this.avatarImageView.setImageDrawable(createCircleDrawableWithIcon3);
            } else {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            }
        }

        public void setExpanded(boolean z) {
            if (z) {
                this.titleView.setVisibility(0);
                return;
            }
            this.titleView.setVisibility(8);
            setSelectedForDelete(false);
        }

        public void setSelectedForDelete(final boolean z) {
            if (this.selectedForDelete != z) {
                AndroidUtilities.cancelRunOnUIThread(this.removeSelectionRunnable);
                this.selectedForDelete = z;
                ValueAnimator valueAnimator = this.selectAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.selectAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.selectedProgress;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.selectAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ActionBarMenuItem.SearchFilterView.this.lambda$setSelectedForDelete$0$ActionBarMenuItem$SearchFilterView(valueAnimator);
                    }
                });
                this.selectAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        float unused = SearchFilterView.this.selectedProgress = z ? 1.0f : 0.0f;
                        SearchFilterView.this.updateColors();
                    }
                });
                this.selectAnimator.setDuration(150).start();
                if (this.selectedForDelete) {
                    AndroidUtilities.runOnUIThread(this.removeSelectionRunnable, 2000);
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$setSelectedForDelete$0 */
        public /* synthetic */ void lambda$setSelectedForDelete$0$ActionBarMenuItem$SearchFilterView(ValueAnimator valueAnimator) {
            this.selectedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateColors();
        }

        public FiltersView.MediaFilterData getFilter() {
            return this.data;
        }
    }
}
