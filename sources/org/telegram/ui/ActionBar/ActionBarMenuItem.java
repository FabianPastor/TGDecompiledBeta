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
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class ActionBarMenuItem extends FrameLayout {
    private int additionalXOffset;
    private int additionalYOffset;
    private boolean allowCloseAnimation;
    private boolean animateClear;
    private boolean animationEnabled;
    /* access modifiers changed from: private */
    public ImageView clearButton;
    /* access modifiers changed from: private */
    public ArrayList<FiltersView.MediaFilterData> currentSearchFilters;
    private ActionBarMenuItemDelegate delegate;
    private boolean forceSmoothKeyboard;
    protected RLottieImageView iconView;
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
    /* access modifiers changed from: private */
    public ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private CloseProgressDrawable2 progressDrawable;
    private Rect rect;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public FrameLayout searchContainer;
    AnimatorSet searchContainerAnimator;
    /* access modifiers changed from: private */
    public EditTextBoldCursor searchField;
    /* access modifiers changed from: private */
    public TextView searchFieldCaption;
    /* access modifiers changed from: private */
    public LinearLayout searchFilterLayout;
    private ArrayList<SearchFilterView> searchFilterViews;
    /* access modifiers changed from: private */
    public int selectedFilterIndex;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private View showSubMenuFrom;
    private boolean showSubmenuByMove;
    private ActionBarSubMenuItemDelegate subMenuDelegate;
    private int subMenuOpenSide;
    protected TextView textView;
    private float transitionOffset;
    /* access modifiers changed from: private */
    public FrameLayout wrappedSearchFrameLayout;
    private int yOffset;

    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    public interface ActionBarSubMenuItemDelegate {
        void onHideSubMenu();

        void onShowSubMenu();
    }

    public static class ActionBarMenuItemSearchListener {
        public void onSearchExpand() {
        }

        public boolean canCollapseSearch() {
            return true;
        }

        public void onSearchCollapse() {
        }

        public void onTextChanged(EditText editText) {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onCaptionCleared() {
        }

        public boolean forceShowClear() {
            return false;
        }

        public Animator getCustomToggleTransition() {
            return null;
        }

        public void onLayout(int l, int t, int r, int b) {
        }

        public void onSearchFilterCleared(FiltersView.MediaFilterData filterData) {
        }

        public boolean canToggleSearch() {
            return true;
        }
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor) {
        this(context, menu, backgroundColor, iconColor, false);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor, Theme.ResourcesProvider resourcesProvider2) {
        this(context, menu, backgroundColor, iconColor, false, resourcesProvider2);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor, boolean text) {
        this(context, menu, backgroundColor, iconColor, text, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor, boolean text, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.searchFilterViews = new ArrayList<>();
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.animateClear = true;
        this.measurePopup = true;
        this.showSubmenuByMove = true;
        this.currentSearchFilters = new ArrayList<>();
        this.selectedFilterIndex = -1;
        this.notificationIndex = -1;
        this.resourcesProvider = resourcesProvider2;
        if (backgroundColor != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(backgroundColor, text ? 5 : 1));
        }
        this.parentMenu = menu;
        if (text) {
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            this.textView.setImportantForAccessibility(2);
            if (iconColor != 0) {
                this.textView.setTextColor(iconColor);
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f));
            return;
        }
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.iconView.setImportantForAccessibility(2);
        addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
        if (iconColor != 0) {
            this.iconView.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setTranslationX(float translationX) {
        super.setTranslationX(this.transitionOffset + translationX);
    }

    public void setLongClickEnabled(boolean value) {
        this.longClickEnabled = value;
    }

    public boolean onTouchEvent(MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        ActionBarPopupWindow actionBarPopupWindow2;
        ActionBarPopupWindow actionBarPopupWindow3;
        if (event.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow3 = this.popupWindow) == null || !actionBarPopupWindow3.isShowing())) {
                ActionBarMenuItem$$ExternalSyntheticLambda1 actionBarMenuItem$$ExternalSyntheticLambda1 = new ActionBarMenuItem$$ExternalSyntheticLambda1(this);
                this.showMenuRunnable = actionBarMenuItem$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(actionBarMenuItem$$ExternalSyntheticLambda1, 200);
            }
        } else if (event.getActionMasked() != 2) {
            ActionBarPopupWindow actionBarPopupWindow4 = this.popupWindow;
            if (actionBarPopupWindow4 == null || !actionBarPopupWindow4.isShowing() || event.getActionMasked() != 1) {
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
                } else if (this.showSubmenuByMove) {
                    this.popupWindow.dismiss();
                }
            }
        } else if (!this.showSubmenuByMove || !hasSubMenu() || ((actionBarPopupWindow2 = this.popupWindow) != null && actionBarPopupWindow2.isShowing())) {
            if (this.showSubmenuByMove && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
                getLocationOnScreen(this.location);
                float x = event.getX() + ((float) this.location[0]);
                float y = event.getY();
                int[] iArr = this.location;
                float y2 = y + ((float) iArr[1]);
                this.popupLayout.getLocationOnScreen(iArr);
                int[] iArr2 = this.location;
                float x2 = x - ((float) iArr2[0]);
                float y3 = y2 - ((float) iArr2[1]);
                this.selectedMenuView = null;
                for (int a = 0; a < this.popupLayout.getItemsCount(); a++) {
                    View child = this.popupLayout.getItemAt(a);
                    child.getHitRect(this.rect);
                    Object tag = child.getTag();
                    if ((tag instanceof Integer) && ((Integer) tag).intValue() < 100) {
                        if (!this.rect.contains((int) x2, (int) y3)) {
                            child.setPressed(false);
                            child.setSelected(false);
                            if (Build.VERSION.SDK_INT == 21 && child.getBackground() != null) {
                                child.getBackground().setVisible(false, false);
                            }
                        } else {
                            child.setPressed(true);
                            child.setSelected(true);
                            if (Build.VERSION.SDK_INT >= 21) {
                                if (Build.VERSION.SDK_INT == 21 && child.getBackground() != null) {
                                    child.getBackground().setVisible(true, false);
                                }
                                child.drawableHotspotChanged(x2, y3 - ((float) child.getTop()));
                            }
                            this.selectedMenuView = child;
                        }
                    }
                }
            }
        } else if (event.getY() > ((float) getHeight())) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            toggleSubMenu();
            return true;
        }
        return super.onTouchEvent(event);
    }

    /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1302x5aa2d208() {
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

    public void setShowSubmenuByMove(boolean value) {
        this.showSubmenuByMove = value;
    }

    public void setIconColor(int color) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setTextColor(color);
        }
        ImageView imageView = this.clearButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setSubMenuOpenSide(int side) {
        this.subMenuOpenSide = side;
    }

    public void setLayoutInScreen(boolean value) {
        this.layoutInScreen = value;
    }

    public void setForceSmoothKeyboard(boolean value) {
        this.forceSmoothKeyboard = value;
    }

    private void createPopupLayout() {
        if (this.popupLayout == null) {
            this.rect = new Rect();
            this.location = new int[2];
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), this.resourcesProvider);
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setOnTouchListener(new ActionBarMenuItem$$ExternalSyntheticLambda8(this));
            this.popupLayout.setDispatchKeyEventListener(new ActionBarMenuItem$$ExternalSyntheticLambda2(this));
        }
    }

    /* renamed from: lambda$createPopupLayout$1$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ boolean m1299x2a5fvar_f(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(this.rect);
        if (this.rect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    /* renamed from: lambda$createPopupLayout$2$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1300xCLASSNAMEb5b0(KeyEvent keyEvent) {
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

    public void setShowedFromBottom(boolean value) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setShownFromBotton(value);
        }
    }

    public void addSubItem(View view, int width, int height) {
        createPopupLayout();
        this.popupLayout.addView(view, new LinearLayout.LayoutParams(width, height));
    }

    public void addSubItem(int id, View view, int width, int height) {
        createPopupLayout();
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(id));
        view.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda0(this));
        view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    }

    /* renamed from: lambda$addSubItem$3$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1295lambda$addSubItem$3$orgtelegramuiActionBarActionBarMenuItem(View view1) {
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
            actionBarMenu.onItemClick(((Integer) view1.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view1.getTag()).intValue());
        }
    }

    public TextView addSubItem(int id, CharSequence text) {
        createPopupLayout();
        TextView textView2 = new TextView(getContext());
        textView2.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
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
        textView2.setTag(Integer.valueOf(id));
        textView2.setText(text);
        this.popupLayout.addView(textView2);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView2.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView2.setLayoutParams(layoutParams);
        textView2.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda3(this));
        return textView2;
    }

    /* renamed from: lambda$addSubItem$4$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1296lambda$addSubItem$4$orgtelegramuiActionBarActionBarMenuItem(View view) {
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

    public ActionBarMenuSubItem addSubItem(int id, int icon, CharSequence text) {
        return addSubItem(id, icon, (Drawable) null, text, true, false);
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, CharSequence text, Theme.ResourcesProvider resourcesProvider2) {
        return addSubItem(id, icon, (Drawable) null, text, true, false, resourcesProvider2);
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, CharSequence text, boolean needCheck) {
        return addSubItem(id, icon, (Drawable) null, text, true, needCheck);
    }

    public View addGap(int id) {
        createPopupLayout();
        View cell = new View(getContext());
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        cell.setTag(Integer.valueOf(id));
        cell.setTag(NUM, 1);
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(6.0f);
        cell.setLayoutParams(layoutParams);
        return cell;
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, Drawable iconDrawable, CharSequence text, boolean dismiss, boolean needCheck) {
        return addSubItem(id, icon, iconDrawable, text, dismiss, needCheck, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, Drawable iconDrawable, CharSequence text, boolean dismiss, boolean needCheck, Theme.ResourcesProvider resourcesProvider2) {
        createPopupLayout();
        ActionBarMenuSubItem cell = new ActionBarMenuSubItem(getContext(), needCheck, false, false, resourcesProvider2);
        cell.setTextAndIcon(text, icon, iconDrawable);
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        cell.setTag(Integer.valueOf(id));
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        cell.setLayoutParams(layoutParams);
        cell.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda6(this, dismiss));
        return cell;
    }

    /* renamed from: lambda$addSubItem$5$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1297lambda$addSubItem$5$orgtelegramuiActionBarActionBarMenuItem(boolean dismiss, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing() && dismiss) {
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

    public View addDivider(int color) {
        createPopupLayout();
        TextView cell = new TextView(getContext());
        cell.setBackgroundColor(color);
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = 1;
        int dp = AndroidUtilities.dp(3.0f);
        layoutParams.bottomMargin = dp;
        layoutParams.topMargin = dp;
        cell.setLayoutParams(layoutParams);
        return cell;
    }

    public void redrawPopup(int color) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && actionBarPopupWindowLayout.getBackgroundColor() != color) {
            this.popupLayout.setBackgroundColor(color);
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.popupLayout.invalidate();
            }
        }
    }

    public void setPopupItemsColor(int color, boolean icon) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            LinearLayout layout = actionBarPopupWindowLayout.linearLayout;
            int count = layout.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = layout.getChildAt(a);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(color);
                } else if (child instanceof ActionBarMenuSubItem) {
                    if (icon) {
                        ((ActionBarMenuSubItem) child).setIconColor(color);
                    } else {
                        ((ActionBarMenuSubItem) child).setTextColor(color);
                    }
                }
            }
        }
    }

    public void setPopupItemsSelectorColor(int color) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            LinearLayout layout = actionBarPopupWindowLayout.linearLayout;
            int count = layout.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = layout.getChildAt(a);
                if (child instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) child).setSelectorColor(color);
                }
            }
        }
    }

    public void setupPopupRadialSelectors(int color) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setupRadialSelectors(color);
        }
    }

    public boolean hasSubMenu() {
        return this.popupLayout != null;
    }

    public ActionBarPopupWindow.ActionBarPopupWindowLayout getPopupLayout() {
        return this.popupLayout;
    }

    public void setMenuYOffset(int offset) {
        this.yOffset = offset;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void toggleSubMenu(android.view.View r18, android.view.View r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r2 = r0.popupLayout
            if (r2 == 0) goto L_0x016d
            org.telegram.ui.ActionBar.ActionBarMenu r2 = r0.parentMenu
            if (r2 == 0) goto L_0x0024
            boolean r2 = r2.isActionMode
            if (r2 == 0) goto L_0x0024
            org.telegram.ui.ActionBar.ActionBarMenu r2 = r0.parentMenu
            org.telegram.ui.ActionBar.ActionBar r2 = r2.parentActionBar
            if (r2 == 0) goto L_0x0024
            org.telegram.ui.ActionBar.ActionBarMenu r2 = r0.parentMenu
            org.telegram.ui.ActionBar.ActionBar r2 = r2.parentActionBar
            boolean r2 = r2.isActionModeShowed()
            if (r2 != 0) goto L_0x0024
            r2 = r19
            goto L_0x016f
        L_0x0024:
            java.lang.Runnable r2 = r0.showMenuRunnable
            if (r2 == 0) goto L_0x002e
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r2)
            r2 = 0
            r0.showMenuRunnable = r2
        L_0x002e:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r0.popupWindow
            if (r2 == 0) goto L_0x003e
            boolean r2 = r2.isShowing()
            if (r2 == 0) goto L_0x003e
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r0.popupWindow
            r2.dismiss()
            return
        L_0x003e:
            r2 = r19
            r0.showSubMenuFrom = r2
            org.telegram.ui.ActionBar.ActionBarMenuItem$ActionBarSubMenuItemDelegate r3 = r0.subMenuDelegate
            if (r3 == 0) goto L_0x0049
            r3.onShowSubMenu()
        L_0x0049:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r3 = r0.popupLayout
            android.view.ViewParent r3 = r3.getParent()
            if (r3 == 0) goto L_0x005e
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r3 = r0.popupLayout
            android.view.ViewParent r3 = r3.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r4 = r0.popupLayout
            r3.removeView(r4)
        L_0x005e:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r3 = r0.popupLayout
            r4 = -2
            r5 = 1
            if (r1 == 0) goto L_0x00d4
            org.telegram.ui.ActionBar.ActionBarMenuItem$1 r6 = new org.telegram.ui.ActionBar.ActionBarMenuItem$1
            android.content.Context r7 = r17.getContext()
            r6.<init>(r7, r1)
            r6.setOrientation(r5)
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            android.content.Context r8 = r17.getContext()
            r7.<init>(r8)
            r8 = 0
            r7.setAlpha(r8)
            android.view.ViewPropertyAnimator r8 = r7.animate()
            r9 = 1065353216(0x3var_, float:1.0)
            android.view.ViewPropertyAnimator r8 = r8.alpha(r9)
            r9 = 100
            android.view.ViewPropertyAnimator r8 = r8.setDuration(r9)
            r8.start()
            android.content.Context r8 = r17.getContext()
            r9 = 2131166009(0x7var_, float:1.7946251E38)
            android.graphics.drawable.Drawable r8 = androidx.core.content.ContextCompat.getDrawable(r8, r9)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r10 = r0.popupLayout
            int r10 = r10.getBackgroundColor()
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r10, r11)
            r8.setColorFilter(r9)
            r7.setBackground(r8)
            r7.addView(r1)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r4)
            r6.addView(r7, r9)
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r9 = r0.popupLayout
            r10 = -2
            r11 = -2
            r12 = 0
            r13 = 0
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = -r14
            r15 = 0
            r16 = 0
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
            r6.addView(r9, r10)
            r3 = r6
        L_0x00d4:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r6 = new org.telegram.ui.ActionBar.ActionBarPopupWindow
            r6.<init>(r3, r4, r4)
            r0.popupWindow = r6
            boolean r4 = r0.animationEnabled
            r6 = 0
            if (r4 == 0) goto L_0x00ec
            int r4 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r4 < r7) goto L_0x00ec
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r4.setAnimationStyle(r6)
            goto L_0x00f4
        L_0x00ec:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r7 = 2131689479(0x7f0var_, float:1.9007975E38)
            r4.setAnimationStyle(r7)
        L_0x00f4:
            boolean r4 = r0.animationEnabled
            if (r4 != 0) goto L_0x00fd
            org.telegram.ui.ActionBar.ActionBarPopupWindow r7 = r0.popupWindow
            r7.setAnimationEnabled(r4)
        L_0x00fd:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r4.setOutsideTouchable(r5)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r4.setClippingEnabled(r5)
            boolean r4 = r0.layoutInScreen
            if (r4 == 0) goto L_0x0110
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r4.setLayoutInScreen(r5)
        L_0x0110:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r7 = 2
            r4.setInputMethodMode(r7)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r4.setSoftInputMode(r6)
            r3.setFocusableInTouchMode(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda7 r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda7
            r4.<init>(r0)
            r3.setOnKeyListener(r4)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda9 r7 = new org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda9
            r7.<init>(r0)
            r4.setOnDismissListener(r7)
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            r7 = 1109393408(0x42200000, float:40.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r7
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r7)
            android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.displaySize
            int r8 = r8.y
            int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r7)
            r3.measure(r4, r7)
            r0.measurePopup = r6
            r0.processedPopupClick = r6
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r4.setFocusable(r5)
            int r4 = r3.getMeasuredWidth()
            if (r4 != 0) goto L_0x015f
            r0.updateOrShowPopup(r5, r5)
            goto L_0x0162
        L_0x015f:
            r0.updateOrShowPopup(r5, r6)
        L_0x0162:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r4 = r0.popupLayout
            r4.updateRadialSelectors()
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r0.popupWindow
            r4.startAnimation()
            return
        L_0x016d:
            r2 = r19
        L_0x016f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarMenuItem.toggleSubMenu(android.view.View, android.view.View):void");
    }

    /* renamed from: lambda$toggleSubMenu$6$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ boolean m1305x824d50e5(View v, int keyCode, KeyEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        return true;
    }

    /* renamed from: lambda$toggleSubMenu$7$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1306x1cee1366() {
        onDismiss();
        ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
        if (actionBarSubMenuItemDelegate != null) {
            actionBarSubMenuItemDelegate.onHideSubMenu();
        }
    }

    public void toggleSubMenu() {
        toggleSubMenu((View) null, (View) null);
    }

    public void openSearch(boolean openKeyboard) {
        ActionBarMenu actionBarMenu;
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null && frameLayout.getVisibility() != 0 && (actionBarMenu = this.parentMenu) != null) {
            actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(openKeyboard));
        }
    }

    /* access modifiers changed from: protected */
    public void onDismiss() {
    }

    public boolean isSearchFieldVisible() {
        return this.searchContainer.getVisibility() == 0;
    }

    public boolean toggleSearch(boolean openKeyboard) {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        View iconView2;
        Animator animator;
        if (this.searchContainer == null || ((actionBarMenuItemSearchListener = this.listener) != null && !actionBarMenuItemSearchListener.canToggleSearch())) {
            return false;
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
        if (actionBarMenuItemSearchListener2 == null || (animator = actionBarMenuItemSearchListener2.getCustomToggleTransition()) == null) {
            final ArrayList<View> menuIcons = new ArrayList<>();
            for (int i = 0; i < this.parentMenu.getChildCount(); i++) {
                View view = this.parentMenu.getChildAt(i);
                if ((view instanceof ActionBarMenuItem) && (iconView2 = ((ActionBarMenuItem) view).getIconView()) != null) {
                    menuIcons.add(iconView2);
                }
            }
            if (this.searchContainer.getTag() != null) {
                this.searchContainer.setTag((Object) null);
                AnimatorSet animatorSet = this.searchContainerAnimator;
                if (animatorSet != null) {
                    animatorSet.removeAllListeners();
                    this.searchContainerAnimator.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.searchContainerAnimator = animatorSet2;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.searchContainer, View.ALPHA, new float[]{this.searchContainer.getAlpha(), 0.0f})});
                for (int i2 = 0; i2 < menuIcons.size(); i2++) {
                    menuIcons.get(i2).setAlpha(0.0f);
                    this.searchContainerAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(menuIcons.get(i2), View.ALPHA, new float[]{menuIcons.get(i2).getAlpha(), 1.0f})});
                }
                this.searchContainerAnimator.setDuration(150);
                this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ActionBarMenuItem.this.searchContainer.setAlpha(0.0f);
                        for (int i = 0; i < menuIcons.size(); i++) {
                            ((View) menuIcons.get(i)).setAlpha(1.0f);
                        }
                        ActionBarMenuItem.this.searchContainer.setVisibility(8);
                    }
                });
                this.searchContainerAnimator.start();
                this.searchField.clearFocus();
                setVisibility(0);
                if (!this.currentSearchFilters.isEmpty()) {
                    if (this.listener != null) {
                        for (int i3 = 0; i3 < this.currentSearchFilters.size(); i3++) {
                            if (this.currentSearchFilters.get(i3).removable) {
                                this.listener.onSearchFilterCleared(this.currentSearchFilters.get(i3));
                            }
                        }
                    }
                    clearSearchFilters();
                }
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener3 = this.listener;
                if (actionBarMenuItemSearchListener3 != null) {
                    actionBarMenuItemSearchListener3.onSearchCollapse();
                }
                if (openKeyboard) {
                    AndroidUtilities.hideKeyboard(this.searchField);
                }
                this.parentMenu.requestLayout();
                requestLayout();
                return false;
            }
            this.searchContainer.setVisibility(0);
            this.searchContainer.setAlpha(0.0f);
            AnimatorSet animatorSet3 = this.searchContainerAnimator;
            if (animatorSet3 != null) {
                animatorSet3.removeAllListeners();
                this.searchContainerAnimator.cancel();
            }
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.searchContainerAnimator = animatorSet4;
            animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.searchContainer, View.ALPHA, new float[]{this.searchContainer.getAlpha(), 1.0f})});
            for (int i4 = 0; i4 < menuIcons.size(); i4++) {
                this.searchContainerAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat(menuIcons.get(i4), View.ALPHA, new float[]{menuIcons.get(i4).getAlpha(), 0.0f})});
            }
            this.searchContainerAnimator.setDuration(150);
            this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ActionBarMenuItem.this.searchContainer.setAlpha(1.0f);
                    for (int i = 0; i < menuIcons.size(); i++) {
                        ((View) menuIcons.get(i)).setAlpha(0.0f);
                    }
                }
            });
            this.searchContainerAnimator.start();
            setVisibility(8);
            clearSearchFilters();
            this.searchField.setText("");
            this.searchField.requestFocus();
            if (openKeyboard) {
                AndroidUtilities.showKeyboard(this.searchField);
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener4 = this.listener;
            if (actionBarMenuItemSearchListener4 != null) {
                actionBarMenuItemSearchListener4.onSearchExpand();
            }
            this.searchContainer.setTag(1);
            return true;
        }
        animator.start();
        return true;
    }

    public void removeSearchFilter(FiltersView.MediaFilterData filter) {
        if (filter.removable) {
            this.currentSearchFilters.remove(filter);
            int i = this.selectedFilterIndex;
            if (i < 0 || i > this.currentSearchFilters.size() - 1) {
                this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
            }
            onFiltersChanged();
            this.searchField.hideActionMode();
        }
    }

    public void addSearchFilter(FiltersView.MediaFilterData filter) {
        this.currentSearchFilters.add(filter);
        if (this.searchContainer.getTag() != null) {
            this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
        }
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
        boolean visible = !this.currentSearchFilters.isEmpty();
        ArrayList<FiltersView.MediaFilterData> localFilters = new ArrayList<>(this.currentSearchFilters);
        if (Build.VERSION.SDK_INT >= 19 && this.searchContainer.getTag() != null) {
            TransitionSet transition = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150);
            transition.addTransition(new Visibility() {
                public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                    if (!(view instanceof SearchFilterView)) {
                        return ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f});
                    }
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.5f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.5f, 1.0f})});
                    set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    return set;
                }

                public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                    if (!(view instanceof SearchFilterView)) {
                        return ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f});
                    }
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{view.getAlpha(), 0.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{view.getScaleX(), 0.5f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{view.getScaleX(), 0.5f})});
                    set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    return set;
                }
            }.setDuration(150)).addTransition(changeBounds);
            transition.setOrdering(0);
            transition.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            final int selectedAccount = UserConfig.selectedAccount;
            transition.addListener(new Transition.TransitionListener() {
                public void onTransitionStart(Transition transition) {
                    int unused = ActionBarMenuItem.this.notificationIndex = NotificationCenter.getInstance(selectedAccount).setAnimationInProgress(ActionBarMenuItem.this.notificationIndex, (int[]) null);
                }

                public void onTransitionEnd(Transition transition) {
                    NotificationCenter.getInstance(selectedAccount).onAnimationFinish(ActionBarMenuItem.this.notificationIndex);
                }

                public void onTransitionCancel(Transition transition) {
                    NotificationCenter.getInstance(selectedAccount).onAnimationFinish(ActionBarMenuItem.this.notificationIndex);
                }

                public void onTransitionPause(Transition transition) {
                }

                public void onTransitionResume(Transition transition) {
                }
            });
            TransitionManager.beginDelayedTransition(this.searchFilterLayout, transition);
        }
        int i = 0;
        while (i < this.searchFilterLayout.getChildCount()) {
            if (!localFilters.remove(((SearchFilterView) this.searchFilterLayout.getChildAt(i)).getFilter())) {
                this.searchFilterLayout.removeViewAt(i);
                i--;
            }
            i++;
        }
        for (int i2 = 0; i2 < localFilters.size(); i2++) {
            SearchFilterView searchFilterView = new SearchFilterView(getContext(), this.resourcesProvider);
            searchFilterView.setData(localFilters.get(i2));
            searchFilterView.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda5(this, searchFilterView));
            this.searchFilterLayout.addView(searchFilterView, LayoutHelper.createLinear(-2, -1, 0, 0, 0, 6, 0));
        }
        int i3 = 0;
        while (i3 < this.searchFilterLayout.getChildCount()) {
            ((SearchFilterView) this.searchFilterLayout.getChildAt(i3)).setExpanded(i3 == this.selectedFilterIndex);
            i3++;
        }
        this.searchFilterLayout.setTag(visible ? 1 : null);
        final float oldX = this.searchField.getX();
        if (this.searchContainer.getTag() != null) {
            this.searchField.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    ActionBarMenuItem.this.searchField.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (ActionBarMenuItem.this.searchField.getX() != oldX) {
                        ActionBarMenuItem.this.searchField.setTranslationX(oldX - ActionBarMenuItem.this.searchField.getX());
                    }
                    ActionBarMenuItem.this.searchField.animate().translationX(0.0f).setDuration(250).setStartDelay(0).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    return true;
                }
            });
        }
        checkClearButton();
    }

    /* renamed from: lambda$onFiltersChanged$8$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1301xacbe872e(SearchFilterView searchFilterView, View view) {
        int index = this.currentSearchFilters.indexOf(searchFilterView.getFilter());
        if (this.selectedFilterIndex != index) {
            this.selectedFilterIndex = index;
            onFiltersChanged();
        } else if (!searchFilterView.getFilter().removable) {
        } else {
            if (!searchFilterView.selectedForDelete) {
                searchFilterView.setSelectedForDelete(true);
                return;
            }
            FiltersView.MediaFilterData filterToRemove = searchFilterView.getFilter();
            removeSearchFilter(filterToRemove);
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
            if (actionBarMenuItemSearchListener != null) {
                actionBarMenuItemSearchListener.onSearchFilterCleared(filterToRemove);
                this.listener.onTextChanged(this.searchField);
            }
        }
    }

    public static boolean checkRtl(String string) {
        char c;
        if (!TextUtils.isEmpty(string) && (c = string.charAt(0)) >= 1424 && c <= 1791) {
            return true;
        }
        return false;
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
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            if (drawable instanceof RLottieDrawable) {
                rLottieImageView.setAnimation((RLottieDrawable) drawable);
            } else {
                rLottieImageView.setImageDrawable(drawable);
            }
        }
    }

    public RLottieImageView getIconView() {
        return this.iconView;
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setIcon(int resId) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setImageResource(resId);
        }
    }

    public void setText(CharSequence text) {
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setText(text);
        }
    }

    public View getContentView() {
        RLottieImageView rLottieImageView = this.iconView;
        return rLottieImageView != null ? rLottieImageView : this.textView;
    }

    public void setSearchFieldHint(CharSequence hint) {
        if (this.searchFieldCaption != null) {
            this.searchField.setHint(hint);
            setContentDescription(hint);
        }
    }

    public void setSearchFieldText(CharSequence text, boolean animated) {
        if (this.searchFieldCaption != null) {
            this.animateClear = animated;
            this.searchField.setText(text);
            if (!TextUtils.isEmpty(text)) {
                this.searchField.setSelection(text.length());
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

    public ActionBarMenuItem setOverrideMenuClick(boolean value) {
        this.overrideMenuClick = value;
        return this;
    }

    public ActionBarMenuItem setIsSearchField(boolean value) {
        return setIsSearchField(value, false);
    }

    public ActionBarMenuItem setIsSearchField(boolean value, final boolean wrapInScrollView) {
        if (this.parentMenu == null) {
            return this;
        }
        if (value && this.searchContainer == null) {
            AnonymousClass7 r0 = new FrameLayout(getContext()) {
                private boolean ignoreRequestLayout;

                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (ActionBarMenuItem.this.clearButton != null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(visibility);
                    }
                    if (ActionBarMenuItem.this.wrappedSearchFrameLayout != null) {
                        ActionBarMenuItem.this.wrappedSearchFrameLayout.setVisibility(visibility);
                    }
                }

                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (ActionBarMenuItem.this.clearButton != null && ActionBarMenuItem.this.clearButton.getTag() != null) {
                        ActionBarMenuItem.this.clearButton.setAlpha(alpha);
                        ActionBarMenuItem.this.clearButton.setScaleX(alpha);
                        ActionBarMenuItem.this.clearButton.setScaleY(alpha);
                    }
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width;
                    int width2;
                    if (!wrapInScrollView) {
                        measureChildWithMargins(ActionBarMenuItem.this.clearButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                    if (!LocaleController.isRTL) {
                        if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                            measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, widthMeasureSpec, View.MeasureSpec.getSize(widthMeasureSpec) / 2, heightMeasureSpec, 0);
                            width2 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                        } else {
                            width2 = 0;
                        }
                        int minWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                        this.ignoreRequestLayout = true;
                        measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, widthMeasureSpec, width2, heightMeasureSpec, 0);
                        int filterWidth = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                        measureChildWithMargins(ActionBarMenuItem.this.searchField, widthMeasureSpec, width2 + filterWidth, heightMeasureSpec, 0);
                        this.ignoreRequestLayout = false;
                        setMeasuredDimension(Math.max(ActionBarMenuItem.this.searchField.getMeasuredWidth() + filterWidth, minWidth), View.MeasureSpec.getSize(heightMeasureSpec));
                        return;
                    }
                    if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, widthMeasureSpec, View.MeasureSpec.getSize(widthMeasureSpec) / 2, heightMeasureSpec, 0);
                        width = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        width = 0;
                    }
                    int minWidth2 = View.MeasureSpec.getSize(widthMeasureSpec);
                    this.ignoreRequestLayout = true;
                    measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, widthMeasureSpec, width, heightMeasureSpec, 0);
                    int filterWidth2 = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                    measureChildWithMargins(ActionBarMenuItem.this.searchField, View.MeasureSpec.makeMeasureSpec(minWidth2 - AndroidUtilities.dp(12.0f), 0), width + filterWidth2, heightMeasureSpec, 0);
                    this.ignoreRequestLayout = false;
                    setMeasuredDimension(Math.max(ActionBarMenuItem.this.searchField.getMeasuredWidth() + filterWidth2, minWidth2), View.MeasureSpec.getSize(heightMeasureSpec));
                }

                public void requestLayout() {
                    if (!this.ignoreRequestLayout) {
                        super.requestLayout();
                    }
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int x;
                    super.onLayout(changed, left, top, right, bottom);
                    if (LocaleController.isRTL) {
                        x = 0;
                    } else if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        x = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        x = 0;
                    }
                    if (ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0) {
                        x += ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth();
                    }
                    ActionBarMenuItem.this.searchField.layout(x, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + x, ActionBarMenuItem.this.searchField.getBottom());
                }
            };
            this.searchContainer = r0;
            r0.setClipChildren(false);
            this.wrappedSearchFrameLayout = null;
            if (wrapInScrollView) {
                this.wrappedSearchFrameLayout = new FrameLayout(getContext());
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext()) {
                    boolean isDragging;

                    public boolean onInterceptTouchEvent(MotionEvent ev) {
                        checkDragg(ev);
                        return super.onInterceptTouchEvent(ev);
                    }

                    public boolean onTouchEvent(MotionEvent ev) {
                        checkDragg(ev);
                        return super.onTouchEvent(ev);
                    }

                    private void checkDragg(MotionEvent ev) {
                        if (ev.getAction() == 0) {
                            this.isDragging = true;
                        } else if (ev.getAction() == 1 || ev.getAction() == 3) {
                            this.isDragging = false;
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                        if (this.isDragging) {
                            super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
                        }
                    }
                };
                horizontalScrollView.addView(this.searchContainer, LayoutHelper.createScroll(-2, -1, 0));
                horizontalScrollView.setHorizontalScrollBarEnabled(false);
                horizontalScrollView.setClipChildren(false);
                this.wrappedSearchFrameLayout.addView(horizontalScrollView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 48.0f, 0.0f));
                this.parentMenu.addView(this.wrappedSearchFrameLayout, 0, LayoutHelper.createLinear(0, -1, 1.0f, 0, 0, 0, 0));
            } else {
                this.parentMenu.addView(this.searchContainer, 0, LayoutHelper.createLinear(0, -1, 1.0f, 6, 0, 0, 0));
            }
            this.searchContainer.setVisibility(8);
            TextView textView2 = new TextView(getContext());
            this.searchFieldCaption = textView2;
            textView2.setTextSize(1, 18.0f);
            this.searchFieldCaption.setTextColor(getThemedColor("actionBarDefaultSearch"));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            AnonymousClass9 r2 = new EditTextBoldCursor(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    setMeasuredDimension(Math.max(View.MeasureSpec.getSize(widthMeasureSpec), getMeasuredWidth()) + AndroidUtilities.dp(3.0f), getMeasuredHeight());
                }

                /* access modifiers changed from: protected */
                public void onSelectionChanged(int selStart, int selEnd) {
                    super.onSelectionChanged(selStart, selEnd);
                }

                public boolean onKeyDown(int keyCode, KeyEvent event) {
                    if (keyCode != 67 || ActionBarMenuItem.this.searchField.length() != 0 || ((ActionBarMenuItem.this.searchFieldCaption.getVisibility() != 0 || ActionBarMenuItem.this.searchFieldCaption.length() <= 0) && !ActionBarMenuItem.this.hasRemovableFilters())) {
                        return super.onKeyDown(keyCode, event);
                    }
                    if (ActionBarMenuItem.this.hasRemovableFilters()) {
                        FiltersView.MediaFilterData filterToRemove = (FiltersView.MediaFilterData) ActionBarMenuItem.this.currentSearchFilters.get(ActionBarMenuItem.this.currentSearchFilters.size() - 1);
                        if (ActionBarMenuItem.this.listener != null) {
                            ActionBarMenuItem.this.listener.onSearchFilterCleared(filterToRemove);
                        }
                        ActionBarMenuItem.this.removeSearchFilter(filterToRemove);
                    } else {
                        ActionBarMenuItem.this.clearButton.callOnClick();
                    }
                    return true;
                }

                public boolean onTouchEvent(MotionEvent event) {
                    boolean result = super.onTouchEvent(event);
                    if (event.getAction() == 1 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return result;
                }
            };
            this.searchField = r2;
            r2.setScrollContainer(false);
            this.searchField.setCursorWidth(1.5f);
            this.searchField.setCursorColor(getThemedColor("actionBarDefaultSearch"));
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(getThemedColor("actionBarDefaultSearchPlaceholder"));
            this.searchField.setTextColor(getThemedColor("actionBarDefaultSearch"));
            this.searchField.setSingleLine(true);
            this.searchField.setBackgroundResource(0);
            this.searchField.setPadding(0, 0, 0, 0);
            this.searchField.setInputType(this.searchField.getInputType() | 524288);
            if (Build.VERSION.SDK_INT < 23) {
                this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }
                });
            }
            this.searchField.setOnEditorActionListener(new ActionBarMenuItem$$ExternalSyntheticLambda10(this));
            this.searchField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (ActionBarMenuItem.this.ignoreOnTextChange) {
                        boolean unused = ActionBarMenuItem.this.ignoreOnTextChange = false;
                        return;
                    }
                    if (ActionBarMenuItem.this.listener != null) {
                        ActionBarMenuItem.this.listener.onTextChanged(ActionBarMenuItem.this.searchField);
                    }
                    ActionBarMenuItem.this.checkClearButton();
                    if (!ActionBarMenuItem.this.currentSearchFilters.isEmpty() && !TextUtils.isEmpty(ActionBarMenuItem.this.searchField.getText()) && ActionBarMenuItem.this.selectedFilterIndex >= 0) {
                        int unused2 = ActionBarMenuItem.this.selectedFilterIndex = -1;
                        ActionBarMenuItem.this.onFiltersChanged();
                    }
                }

                public void afterTextChanged(Editable s) {
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
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-2, 36.0f, 16, 0.0f, 0.0f, wrapInScrollView ? 0.0f : 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 21, 0.0f, 5.5f, 48.0f, 0.0f));
            }
            this.searchFilterLayout.setClipChildren(false);
            AnonymousClass12 r1 = new ImageView(getContext()) {
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
            this.clearButton.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda4(this));
            this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
            if (wrapInScrollView) {
                this.wrappedSearchFrameLayout.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            } else {
                this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            }
        }
        this.isSearchField = value;
        return this;
    }

    /* renamed from: lambda$setIsSearchField$9$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ boolean m1304x96adae9d(TextView v, int actionId, KeyEvent event) {
        if (event == null) {
            return false;
        }
        if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
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

    /* renamed from: lambda$setIsSearchField$10$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1303xCLASSNAMEd23e5(View v) {
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
                this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new ActionBarMenuItem$$ExternalSyntheticLambda11(this)).start();
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

    /* renamed from: lambda$checkClearButton$11$org-telegram-ui-ActionBar-ActionBarMenuItem  reason: not valid java name */
    public /* synthetic */ void m1298xd667c7f5() {
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

    public void setShowSearchProgress(boolean show) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 != null) {
            if (show) {
                closeProgressDrawable2.startAnimation();
            } else {
                closeProgressDrawable2.stopAnimation();
            }
        }
    }

    public void setSearchFieldCaption(CharSequence caption) {
        if (this.searchFieldCaption != null) {
            if (TextUtils.isEmpty(caption)) {
                this.searchFieldCaption.setVisibility(8);
                return;
            }
            this.searchFieldCaption.setVisibility(0);
            this.searchFieldCaption.setText(caption);
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

    public ActionBarMenuItem setAllowCloseAnimation(boolean value) {
        this.allowCloseAnimation = value;
        return this;
    }

    public void setPopupAnimationEnabled(boolean value) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setAnimationEnabled(value);
        }
        this.animationEnabled = value;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onLayout(left, top, right, bottom);
        }
    }

    public void setAdditionalYOffset(int value) {
        this.additionalYOffset = value;
    }

    public void setAdditionalXOffset(int value) {
        this.additionalXOffset = value;
    }

    public void forceUpdatePopupPosition() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
            updateOrShowPopup(true, true);
        }
    }

    private void updateOrShowPopup(boolean show, boolean update) {
        int offsetY;
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            offsetY = (-actionBarMenu.parentActionBar.getMeasuredHeight()) + this.parentMenu.getTop() + this.parentMenu.getPaddingTop();
        } else {
            float scaleY = getScaleY();
            offsetY = (-((int) ((((float) getMeasuredHeight()) * scaleY) - ((this.subMenuOpenSide != 2 ? getTranslationY() : 0.0f) / scaleY)))) + this.additionalYOffset;
        }
        int offsetY2 = offsetY + this.yOffset;
        if (show) {
            this.popupLayout.scrollToTop();
        }
        View fromView = this.showSubMenuFrom;
        if (fromView == null) {
            fromView = this;
        }
        ActionBarMenu actionBarMenu2 = this.parentMenu;
        if (actionBarMenu2 != null) {
            ActionBar actionBar = actionBarMenu2.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (show) {
                    this.popupWindow.showAsDropDown(actionBar, (((fromView.getLeft() + this.parentMenu.getLeft()) + fromView.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + ((int) getTranslationX()), offsetY2);
                }
                if (update) {
                    this.popupWindow.update(actionBar, ((int) getTranslationX()) + (((fromView.getLeft() + this.parentMenu.getLeft()) + fromView.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()), offsetY2, -1, -1);
                    return;
                }
                return;
            }
            if (show) {
                if (this.forceSmoothKeyboard) {
                    this.popupWindow.showAtLocation(actionBar, 51, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2);
                } else {
                    this.popupWindow.showAsDropDown(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2);
                }
            }
            if (update) {
                this.popupWindow.update(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2, -1, -1);
                return;
            }
            return;
        }
        int i = this.subMenuOpenSide;
        if (i == 0) {
            if (getParent() != null) {
                View parent = (View) getParent();
                if (show) {
                    this.popupWindow.showAsDropDown(parent, ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, offsetY2);
                }
                if (update) {
                    this.popupWindow.update(parent, this.additionalXOffset + ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()), offsetY2, -1, -1);
                }
            }
        } else if (i == 1) {
            if (show) {
                this.popupWindow.showAsDropDown(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset, offsetY2);
            }
            if (update) {
                this.popupWindow.update(this, this.additionalXOffset + (-AndroidUtilities.dp(8.0f)), offsetY2, -1, -1);
            }
        } else {
            if (show) {
                this.popupWindow.showAsDropDown(this, (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, offsetY2);
            }
            if (update) {
                this.popupWindow.update(this, this.additionalXOffset + (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()), offsetY2, -1, -1);
            }
        }
    }

    public void hideSubItem(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) != null && view.getVisibility() != 8) {
            view.setVisibility(8);
            this.measurePopup = true;
            checkHideMenuItem();
        }
    }

    public void checkHideMenuItem() {
        boolean isVisible = false;
        int i = 0;
        while (true) {
            if (i >= this.popupLayout.getItemsCount()) {
                break;
            } else if (this.popupLayout.getItemAt(i).getVisibility() == 0) {
                isVisible = true;
                break;
            } else {
                i++;
            }
        }
        int v = isVisible ? 0 : 8;
        if (v != getVisibility()) {
            setVisibility(v);
        }
    }

    public void hideAllSubItems() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            int N = actionBarPopupWindowLayout.getItemsCount();
            for (int a = 0; a < N; a++) {
                this.popupLayout.getItemAt(a).setVisibility(8);
            }
            this.measurePopup = true;
            checkHideMenuItem();
        }
    }

    public boolean isSubItemVisible(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) == null || view.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    public void showSubItem(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) != null && view.getVisibility() != 0) {
            view.setVisibility(0);
            this.measurePopup = true;
            checkHideMenuItem();
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.iconView != null) {
            info.setClassName("android.widget.ImageButton");
        } else if (this.textView != null) {
            info.setClassName("android.widget.Button");
            if (TextUtils.isEmpty(info.getText())) {
                info.setText(this.textView.getText());
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
        if (this.popupLayout != null) {
            for (int i2 = 0; i2 < this.popupLayout.getItemsCount(); i2++) {
                if (this.popupLayout.getItemAt(i2) instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) this.popupLayout.getItemAt(i2)).setSelectorColor(getThemedColor("dialogButtonSelector"));
                }
            }
        }
    }

    public void collapseSearchFilters() {
        this.selectedFilterIndex = -1;
        onFiltersChanged();
    }

    public void setTransitionOffset(int offset) {
        this.transitionOffset = (float) offset;
        setTranslationX(0.0f);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
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
        private final Theme.ResourcesProvider resourcesProvider;
        ValueAnimator selectAnimator;
        /* access modifiers changed from: private */
        public boolean selectedForDelete;
        /* access modifiers changed from: private */
        public float selectedProgress;
        ShapeDrawable shapeDrawable;
        Drawable thumbDrawable;
        TextView titleView;

        public SearchFilterView(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
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
            int defaultBackgroundColor = getThemedColor("groupcreate_spanBackground");
            int selectedBackgroundColor = getThemedColor("avatar_backgroundBlue");
            int textDefaultColor = getThemedColor("windowBackgroundWhiteBlackText");
            int textSelectedColor = getThemedColor("avatar_actionBarIconBlue");
            this.shapeDrawable.getPaint().setColor(ColorUtils.blendARGB(defaultBackgroundColor, selectedBackgroundColor, this.selectedProgress));
            this.titleView.setTextColor(ColorUtils.blendARGB(textDefaultColor, textSelectedColor, this.selectedProgress));
            this.closeIconView.setColorFilter(textSelectedColor);
            this.closeIconView.setAlpha(this.selectedProgress);
            this.closeIconView.setScaleX(this.selectedProgress * 0.82f);
            this.closeIconView.setScaleY(this.selectedProgress * 0.82f);
            Drawable drawable = this.thumbDrawable;
            if (drawable != null) {
                Theme.setCombinedDrawableColor(drawable, getThemedColor("avatar_backgroundBlue"), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
            }
            this.avatarImageView.setAlpha(1.0f - this.selectedProgress);
            FiltersView.MediaFilterData mediaFilterData = this.data;
            if (mediaFilterData != null && mediaFilterData.filterType == 7) {
                setData(this.data);
            }
            invalidate();
        }

        public void setData(FiltersView.MediaFilterData data2) {
            this.data = data2;
            this.titleView.setText(data2.title);
            CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), data2.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor("avatar_backgroundBlue"), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
            if (data2.filterType == 4) {
                if (data2.chat instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) data2.chat;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                        CombinedDrawable combinedDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                        combinedDrawable.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor("avatar_backgroundSaved"), false);
                        Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
                        this.avatarImageView.setImageDrawable(combinedDrawable);
                        return;
                    }
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat(user, this.thumbDrawable);
                } else if (data2.chat instanceof TLRPC.Chat) {
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat((TLRPC.Chat) data2.chat, this.thumbDrawable);
                }
            } else if (data2.filterType == 7) {
                CombinedDrawable combinedDrawable2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                combinedDrawable2.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(combinedDrawable2, getThemedColor("avatar_backgroundArchived"), false);
                Theme.setCombinedDrawableColor(combinedDrawable2, getThemedColor("avatar_actionBarIconBlue"), true);
                this.avatarImageView.setImageDrawable(combinedDrawable2);
            } else {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            }
        }

        public void setExpanded(boolean expanded) {
            if (expanded) {
                this.titleView.setVisibility(0);
                return;
            }
            this.titleView.setVisibility(8);
            setSelectedForDelete(false);
        }

        public void setSelectedForDelete(final boolean select) {
            if (this.selectedForDelete != select) {
                AndroidUtilities.cancelRunOnUIThread(this.removeSelectionRunnable);
                this.selectedForDelete = select;
                ValueAnimator valueAnimator = this.selectAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.selectAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.selectedProgress;
                fArr[1] = select ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.selectAnimator = ofFloat;
                ofFloat.addUpdateListener(new ActionBarMenuItem$SearchFilterView$$ExternalSyntheticLambda0(this));
                this.selectAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        float unused = SearchFilterView.this.selectedProgress = select ? 1.0f : 0.0f;
                        SearchFilterView.this.updateColors();
                    }
                });
                this.selectAnimator.setDuration(150).start();
                if (this.selectedForDelete) {
                    AndroidUtilities.runOnUIThread(this.removeSelectionRunnable, 2000);
                }
            }
        }

        /* renamed from: lambda$setSelectedForDelete$0$org-telegram-ui-ActionBar-ActionBarMenuItem$SearchFilterView  reason: not valid java name */
        public /* synthetic */ void m1307x8690ed44(ValueAnimator valueAnimator) {
            this.selectedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateColors();
        }

        public FiltersView.MediaFilterData getFilter() {
            return this.data;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }
}
