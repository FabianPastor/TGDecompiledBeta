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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
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
    /* access modifiers changed from: private */
    public int notificationIndex;
    protected boolean overrideMenuClick;
    /* access modifiers changed from: private */
    public ActionBarMenu parentMenu;
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

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, Theme.ResourcesProvider resourcesProvider2) {
        this(context, actionBarMenu, i, i2, false, resourcesProvider2);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, boolean z) {
        this(context, actionBarMenu, i, i2, z, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        new ArrayList();
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.animateClear = true;
        this.showSubmenuByMove = true;
        this.currentSearchFilters = new ArrayList<>();
        this.selectedFilterIndex = -1;
        this.notificationIndex = -1;
        this.resourcesProvider = resourcesProvider2;
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
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.iconView.setImportantForAccessibility(2);
        addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
        if (i2 != 0) {
            this.iconView.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setTranslationX(float f) {
        super.setTranslationX(f + this.transitionOffset);
    }

    public void setLongClickEnabled(boolean z) {
        this.longClickEnabled = z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        ActionBarPopupWindow actionBarPopupWindow2;
        ActionBarPopupWindow actionBarPopupWindow3;
        if (motionEvent.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow3 = this.popupWindow) == null || !actionBarPopupWindow3.isShowing())) {
                ActionBarMenuItem$$ExternalSyntheticLambda10 actionBarMenuItem$$ExternalSyntheticLambda10 = new ActionBarMenuItem$$ExternalSyntheticLambda10(this);
                this.showMenuRunnable = actionBarMenuItem$$ExternalSyntheticLambda10;
                AndroidUtilities.runOnUIThread(actionBarMenuItem$$ExternalSyntheticLambda10, 200);
            }
        } else if (motionEvent.getActionMasked() != 2) {
            ActionBarPopupWindow actionBarPopupWindow4 = this.popupWindow;
            if (actionBarPopupWindow4 == null || !actionBarPopupWindow4.isShowing() || motionEvent.getActionMasked() != 1) {
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
                    Object tag = itemAt.getTag();
                    if ((tag instanceof Integer) && ((Integer) tag).intValue() < 100) {
                        if (!this.rect.contains((int) f2, (int) f3)) {
                            itemAt.setPressed(false);
                            itemAt.setSelected(false);
                            if (Build.VERSION.SDK_INT == 21 && itemAt.getBackground() != null) {
                                itemAt.getBackground().setVisible(false, false);
                            }
                        } else {
                            itemAt.setPressed(true);
                            itemAt.setSelected(true);
                            int i2 = Build.VERSION.SDK_INT;
                            if (i2 >= 21) {
                                if (i2 == 21 && itemAt.getBackground() != null) {
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
    public /* synthetic */ void lambda$onTouchEvent$0() {
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
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setTextColor(i);
        }
        ImageView imageView = this.clearButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
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
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), NUM, this.resourcesProvider, 1);
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setOnTouchListener(new ActionBarMenuItem$$ExternalSyntheticLambda7(this));
            this.popupLayout.setDispatchKeyEventListener(new ActionBarMenuItem$$ExternalSyntheticLambda13(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createPopupLayout$1(View view, MotionEvent motionEvent) {
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
    public /* synthetic */ void lambda$createPopupLayout$2(KeyEvent keyEvent) {
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
            actionBarPopupWindowLayout.setShownFromBotton(z);
        }
    }

    public void addSubItem(int i, View view, int i2, int i3) {
        createPopupLayout();
        view.setLayoutParams(new LinearLayout.LayoutParams(i2, i3));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(i));
        view.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda2(this));
        view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addSubItem$3(View view) {
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
        textView2.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda1(this));
        return textView2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addSubItem$4(View view) {
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
        return addSubItem(i, i2, (Drawable) null, charSequence, true, false);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence, Theme.ResourcesProvider resourcesProvider2) {
        return addSubItem(i, i2, (Drawable) null, charSequence, true, false, resourcesProvider2);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence, boolean z) {
        return addSubItem(i, i2, (Drawable) null, charSequence, true, z);
    }

    public View addGap(int i) {
        createPopupLayout();
        View view = new View(getContext());
        view.setMinimumWidth(AndroidUtilities.dp(196.0f));
        view.setTag(Integer.valueOf(i));
        view.setTag(NUM, 1);
        this.popupLayout.addView(view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(6.0f);
        view.setLayoutParams(layoutParams);
        return view;
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2) {
        return addSubItem(i, i2, drawable, charSequence, z, z2, (Theme.ResourcesProvider) null);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider2) {
        createPopupLayout();
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), z2, false, false, resourcesProvider2);
        actionBarMenuSubItem.setTextAndIcon(charSequence, i2, drawable);
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
        actionBarMenuSubItem.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda4(this, z));
        return actionBarMenuSubItem;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addSubItem$5(boolean z, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing() && z) {
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

    public ActionBarMenuSubItem addSwipeBackItem(int i, String str, View view) {
        createPopupLayout();
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), false, false, false, this.resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(str, i, (Drawable) null);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarMenuSubItem.setRightIcon(NUM);
        this.popupLayout.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        actionBarMenuSubItem.openSwipeBackLayout = new ActionBarMenuItem$$ExternalSyntheticLambda12(this, this.popupLayout.addViewToSwipeBack(view));
        actionBarMenuSubItem.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda5(actionBarMenuSubItem));
        this.popupLayout.swipeBackGravityRight = true;
        return actionBarMenuSubItem;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addSwipeBackItem$6(int i) {
        if (this.popupLayout.getSwipeBack() != null) {
            this.popupLayout.getSwipeBack().openForeground(i);
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

    public ActionBarPopupWindow.ActionBarPopupWindowLayout getPopupLayout() {
        if (this.popupLayout == null) {
            createPopupLayout();
        }
        return this.popupLayout;
    }

    public void setMenuYOffset(int i) {
        this.yOffset = i;
    }

    /* JADX WARNING: type inference failed for: r12v6, types: [android.widget.LinearLayout, org.telegram.ui.ActionBar.ActionBarMenuItem$1] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void toggleSubMenu(final android.view.View r11, android.view.View r12) {
        /*
            r10 = this;
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = r10.popupLayout
            if (r0 == 0) goto L_0x016c
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r10.parentMenu
            if (r0 == 0) goto L_0x0018
            boolean r1 = r0.isActionMode
            if (r1 == 0) goto L_0x0018
            org.telegram.ui.ActionBar.ActionBar r0 = r0.parentActionBar
            if (r0 == 0) goto L_0x0018
            boolean r0 = r0.isActionModeShowed()
            if (r0 != 0) goto L_0x0018
            goto L_0x016c
        L_0x0018:
            java.lang.Runnable r0 = r10.showMenuRunnable
            if (r0 == 0) goto L_0x0022
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r0 = 0
            r10.showMenuRunnable = r0
        L_0x0022:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r10.popupWindow
            if (r0 == 0) goto L_0x0032
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x0032
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r11.dismiss()
            return
        L_0x0032:
            r10.showSubMenuFrom = r12
            org.telegram.ui.ActionBar.ActionBarMenuItem$ActionBarSubMenuItemDelegate r12 = r10.subMenuDelegate
            if (r12 == 0) goto L_0x003b
            r12.onShowSubMenu()
        L_0x003b:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r12 = r10.popupLayout
            android.view.ViewParent r12 = r12.getParent()
            if (r12 == 0) goto L_0x0050
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r12 = r10.popupLayout
            android.view.ViewParent r12 = r12.getParent()
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = r10.popupLayout
            r12.removeView(r0)
        L_0x0050:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r12 = r10.popupLayout
            r0 = -2
            r1 = 1
            if (r11 == 0) goto L_0x00c9
            org.telegram.ui.ActionBar.ActionBarMenuItem$1 r12 = new org.telegram.ui.ActionBar.ActionBarMenuItem$1
            android.content.Context r2 = r10.getContext()
            r12.<init>(r2, r11)
            r12.setOrientation(r1)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            android.content.Context r3 = r10.getContext()
            r2.<init>(r3)
            r3 = 0
            r2.setAlpha(r3)
            android.view.ViewPropertyAnimator r3 = r2.animate()
            r4 = 1065353216(0x3var_, float:1.0)
            android.view.ViewPropertyAnimator r3 = r3.alpha(r4)
            r4 = 100
            android.view.ViewPropertyAnimator r3 = r3.setDuration(r4)
            r3.start()
            android.content.Context r3 = r10.getContext()
            r4 = 2131166053(0x7var_, float:1.794634E38)
            android.graphics.drawable.Drawable r3 = androidx.core.content.ContextCompat.getDrawable(r3, r4)
            android.graphics.drawable.Drawable r3 = r3.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r5 = r10.popupLayout
            int r5 = r5.getBackgroundColor()
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r6)
            r3.setColorFilter(r4)
            r2.setBackground(r3)
            r2.addView(r11)
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r0, r0)
            r12.addView(r2, r11)
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r11 = r10.popupLayout
            r3 = -2
            r4 = -2
            r5 = 0
            r6 = 0
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = -r7
            r8 = 0
            r9 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9)
            r12.addView(r11, r3)
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r11 = r10.popupLayout
            r11.setTopView(r2)
        L_0x00c9:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = new org.telegram.ui.ActionBar.ActionBarPopupWindow
            r11.<init>(r12, r0, r0)
            r10.popupWindow = r11
            boolean r0 = r10.animationEnabled
            r2 = 0
            if (r0 == 0) goto L_0x00df
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x00df
            r11.setAnimationStyle(r2)
            goto L_0x00e5
        L_0x00df:
            r0 = 2131689479(0x7f0var_, float:1.9007975E38)
            r11.setAnimationStyle(r0)
        L_0x00e5:
            boolean r11 = r10.animationEnabled
            if (r11 != 0) goto L_0x00ee
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r10.popupWindow
            r0.setAnimationEnabled(r11)
        L_0x00ee:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r11.setOutsideTouchable(r1)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r11.setClippingEnabled(r1)
            boolean r11 = r10.layoutInScreen
            if (r11 == 0) goto L_0x0101
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r11.setLayoutInScreen(r1)
        L_0x0101:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r0 = 2
            r11.setInputMethodMode(r0)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r11.setSoftInputMode(r2)
            r12.setFocusableInTouchMode(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda6 r11 = new org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda6
            r11.<init>(r10)
            r12.setOnKeyListener(r11)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda8 r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda8
            r0.<init>(r10)
            r11.setOnDismissListener(r0)
            android.graphics.Point r11 = org.telegram.messenger.AndroidUtilities.displaySize
            int r11 = r11.x
            r0 = 1109393408(0x42200000, float:40.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r11 = r11 - r0
            r0 = -2147483648(0xfffffffvar_, float:-0.0)
            int r11 = android.view.View.MeasureSpec.makeMeasureSpec(r11, r0)
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r3.y
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r0)
            r12.measure(r11, r0)
            r10.processedPopupClick = r2
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r11.setFocusable(r1)
            int r11 = r12.getMeasuredWidth()
            if (r11 != 0) goto L_0x014e
            r10.updateOrShowPopup(r1, r1)
            goto L_0x0151
        L_0x014e:
            r10.updateOrShowPopup(r1, r2)
        L_0x0151:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r11 = r10.popupLayout
            r11.updateRadialSelectors()
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r11 = r10.popupLayout
            org.telegram.ui.Components.PopupSwipeBackLayout r11 = r11.getSwipeBack()
            if (r11 == 0) goto L_0x0167
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r11 = r10.popupLayout
            org.telegram.ui.Components.PopupSwipeBackLayout r11 = r11.getSwipeBack()
            r11.closeForeground(r2)
        L_0x0167:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r11 = r10.popupWindow
            r11.startAnimation()
        L_0x016c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarMenuItem.toggleSubMenu(android.view.View, android.view.View):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$toggleSubMenu$8(View view, int i, KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleSubMenu$9() {
        onDismiss();
        ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
        if (actionBarSubMenuItemDelegate != null) {
            actionBarSubMenuItemDelegate.onHideSubMenu();
        }
    }

    public void toggleSubMenu() {
        toggleSubMenu((View) null, (View) null);
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
        RLottieImageView iconView2;
        Animator customToggleTransition;
        if (this.searchContainer == null || ((actionBarMenuItemSearchListener = this.listener) != null && !actionBarMenuItemSearchListener.canToggleSearch())) {
            return false;
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
        if (actionBarMenuItemSearchListener2 == null || (customToggleTransition = actionBarMenuItemSearchListener2.getCustomToggleTransition()) == null) {
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.parentMenu.getChildCount(); i++) {
                View childAt = this.parentMenu.getChildAt(i);
                if ((childAt instanceof ActionBarMenuItem) && (iconView2 = ((ActionBarMenuItem) childAt).getIconView()) != null) {
                    arrayList.add(iconView2);
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
                FrameLayout frameLayout = this.searchContainer;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(frameLayout, View.ALPHA, new float[]{frameLayout.getAlpha(), 0.0f})});
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    ((View) arrayList.get(i2)).setAlpha(0.0f);
                    this.searchContainerAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat((View) arrayList.get(i2), View.ALPHA, new float[]{((View) arrayList.get(i2)).getAlpha(), 1.0f})});
                }
                this.searchContainerAnimator.setDuration(150);
                this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ActionBarMenuItem.this.searchContainer.setAlpha(0.0f);
                        for (int i = 0; i < arrayList.size(); i++) {
                            ((View) arrayList.get(i)).setAlpha(1.0f);
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
                if (z) {
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
            FrameLayout frameLayout2 = this.searchContainer;
            animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(frameLayout2, View.ALPHA, new float[]{frameLayout2.getAlpha(), 1.0f})});
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                this.searchContainerAnimator.playTogether(new Animator[]{ObjectAnimator.ofFloat((View) arrayList.get(i4), View.ALPHA, new float[]{((View) arrayList.get(i4)).getAlpha(), 0.0f})});
            }
            this.searchContainerAnimator.setDuration(150);
            this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ActionBarMenuItem.this.searchContainer.setAlpha(1.0f);
                    for (int i = 0; i < arrayList.size(); i++) {
                        ((View) arrayList.get(i)).setAlpha(0.0f);
                    }
                }
            });
            this.searchContainerAnimator.start();
            setVisibility(8);
            clearSearchFilters();
            this.searchField.setText("");
            this.searchField.requestFocus();
            if (z) {
                AndroidUtilities.showKeyboard(this.searchField);
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener4 = this.listener;
            if (actionBarMenuItemSearchListener4 != null) {
                actionBarMenuItemSearchListener4.onSearchExpand();
            }
            this.searchContainer.setTag(1);
            return true;
        }
        customToggleTransition.start();
        return true;
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
        boolean z = !this.currentSearchFilters.isEmpty();
        ArrayList arrayList = new ArrayList(this.currentSearchFilters);
        if (Build.VERSION.SDK_INT >= 19 && this.searchContainer.getTag() != null) {
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
            SearchFilterView searchFilterView = new SearchFilterView(getContext(), this.resourcesProvider);
            searchFilterView.setData((FiltersView.MediaFilterData) arrayList.get(i3));
            searchFilterView.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda3(this, searchFilterView));
            this.searchFilterLayout.addView(searchFilterView, LayoutHelper.createLinear(-2, -1, 0, 0, 0, 6, 0));
        }
        int i4 = 0;
        while (i4 < this.searchFilterLayout.getChildCount()) {
            ((SearchFilterView) this.searchFilterLayout.getChildAt(i4)).setExpanded(i4 == this.selectedFilterIndex);
            i4++;
        }
        this.searchFilterLayout.setTag(z ? 1 : null);
        final float x = this.searchField.getX();
        if (this.searchContainer.getTag() != null) {
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
        }
        checkClearButton();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFiltersChanged$10(SearchFilterView searchFilterView, View view) {
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

    public void setIcon(int i) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setImageResource(i);
        }
    }

    public void setText(CharSequence charSequence) {
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setText(charSequence);
        }
    }

    public View getContentView() {
        RLottieImageView rLottieImageView = this.iconView;
        return rLottieImageView != null ? rLottieImageView : this.textView;
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
        return setIsSearchField(z, false);
    }

    public ActionBarMenuItem setIsSearchField(boolean z, final boolean z2) {
        if (this.parentMenu == null) {
            return this;
        }
        if (z && this.searchContainer == null) {
            AnonymousClass7 r0 = new FrameLayout(getContext()) {
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

                public void setAlpha(float f) {
                    super.setAlpha(f);
                    if (ActionBarMenuItem.this.clearButton != null && ActionBarMenuItem.this.clearButton.getTag() != null) {
                        ActionBarMenuItem.this.clearButton.setAlpha(f);
                        ActionBarMenuItem.this.clearButton.setScaleX(f);
                        ActionBarMenuItem.this.clearButton.setScaleY(f);
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
                    measureChildWithMargins(ActionBarMenuItem.this.searchField, View.MeasureSpec.makeMeasureSpec(size2 - AndroidUtilities.dp(12.0f), 0), i3 + measuredWidth2, i2, 0);
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
                AnonymousClass8 r2 = new HorizontalScrollView(this, getContext()) {
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
            this.searchFieldCaption.setTextColor(getThemedColor("actionBarDefaultSearch"));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            AnonymousClass9 r22 = new EditTextBoldCursor(getContext()) {
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
            this.searchField.setCursorColor(getThemedColor("actionBarDefaultSearch"));
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(getThemedColor("actionBarDefaultSearchPlaceholder"));
            this.searchField.setTextColor(getThemedColor("actionBarDefaultSearch"));
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
            this.searchField.setOnEditorActionListener(new ActionBarMenuItem$$ExternalSyntheticLambda9(this));
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
            AnonymousClass13 r23 = new CloseProgressDrawable2() {
                public int getCurrentColor() {
                    return ActionBarMenuItem.this.parentMenu.parentActionBar.itemsColor;
                }
            };
            this.progressDrawable = r23;
            r1.setImageDrawable(r23);
            this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new ActionBarMenuItem$$ExternalSyntheticLambda0(this));
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
    public /* synthetic */ boolean lambda$setIsSearchField$11(TextView textView2, int i, KeyEvent keyEvent) {
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
    public /* synthetic */ void lambda$setIsSearchField$12(View view) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkClearButton$13() {
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

    public void forceUpdatePopupPosition() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
            updateOrShowPopup(true, true);
        }
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
        View view = this.showSubMenuFrom;
        if (view == null) {
            view = this;
        }
        ActionBarMenu actionBarMenu2 = this.parentMenu;
        if (actionBarMenu2 != null) {
            ActionBar actionBar = actionBarMenu2.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (z) {
                    this.popupWindow.showAsDropDown(actionBar, (((view.getLeft() + this.parentMenu.getLeft()) + view.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + ((int) getTranslationX()), i3);
                }
                if (z2) {
                    this.popupWindow.update(actionBar, (((view.getLeft() + this.parentMenu.getLeft()) + view.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + ((int) getTranslationX()), i3, -1, -1);
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
                View view2 = (View) getParent();
                if (z) {
                    this.popupWindow.showAsDropDown(view2, ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, i3);
                }
                if (z2) {
                    this.popupWindow.update(view2, ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, i3, -1, -1);
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
                this.popupWindow.showAsDropDown(this, (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, i3);
            }
            if (z2) {
                this.popupWindow.update(this, (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, i3, -1, -1);
            }
        }
    }

    public void hideSubItem(int i) {
        View findViewWithTag;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) != null && findViewWithTag.getVisibility() != 8) {
            findViewWithTag.setVisibility(8);
        }
    }

    public void checkHideMenuItem() {
        boolean z;
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= this.popupLayout.getItemsCount()) {
                z = false;
                break;
            } else if (this.popupLayout.getItemAt(i2).getVisibility() == 0) {
                z = true;
                break;
            } else {
                i2++;
            }
        }
        if (!z) {
            i = 8;
        }
        if (i != getVisibility()) {
            setVisibility(i);
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

    public void setTransitionOffset(int i) {
        this.transitionOffset = (float) i;
        setTranslationX(0.0f);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
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
            int themedColor = getThemedColor("groupcreate_spanBackground");
            int themedColor2 = getThemedColor("avatar_backgroundBlue");
            int themedColor3 = getThemedColor("windowBackgroundWhiteBlackText");
            int themedColor4 = getThemedColor("avatar_actionBarIconBlue");
            this.shapeDrawable.getPaint().setColor(ColorUtils.blendARGB(themedColor, themedColor2, this.selectedProgress));
            this.titleView.setTextColor(ColorUtils.blendARGB(themedColor3, themedColor4, this.selectedProgress));
            this.closeIconView.setColorFilter(themedColor4);
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
                setData(mediaFilterData);
            }
            invalidate();
        }

        public void setData(FiltersView.MediaFilterData mediaFilterData) {
            this.data = mediaFilterData;
            this.titleView.setText(mediaFilterData.title);
            CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), mediaFilterData.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor("avatar_backgroundBlue"), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
            int i = mediaFilterData.filterType;
            if (i == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == tLRPC$User.id) {
                        CombinedDrawable createCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                        createCircleDrawableWithIcon2.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, getThemedColor("avatar_backgroundSaved"), false);
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, getThemedColor("avatar_actionBarIconBlue"), true);
                        this.avatarImageView.setImageDrawable(createCircleDrawableWithIcon2);
                        return;
                    }
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat(tLRPC$User, this.thumbDrawable);
                } else if (tLObject instanceof TLRPC$Chat) {
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat((TLRPC$Chat) tLObject, this.thumbDrawable);
                }
            } else if (i == 7) {
                CombinedDrawable createCircleDrawableWithIcon3 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                createCircleDrawableWithIcon3.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon3, getThemedColor("avatar_backgroundArchived"), false);
                Theme.setCombinedDrawableColor(createCircleDrawableWithIcon3, getThemedColor("avatar_actionBarIconBlue"), true);
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
                ofFloat.addUpdateListener(new ActionBarMenuItem$SearchFilterView$$ExternalSyntheticLambda0(this));
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
        public /* synthetic */ void lambda$setSelectedForDelete$0(ValueAnimator valueAnimator) {
            this.selectedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateColors();
        }

        public FiltersView.MediaFilterData getFilter() {
            return this.data;
        }

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    public View addColoredGap() {
        createPopupLayout();
        ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(getContext(), "graySection");
        gapView.setTag(NUM, 1);
        this.popupLayout.addView(gapView, LayoutHelper.createLinear(-1, 8));
        return gapView;
    }

    public static ActionBarMenuSubItem addItem(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, int i, String str, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(actionBarPopupWindowLayout.getContext(), z, false, false, resourcesProvider2);
        actionBarMenuSubItem.setTextAndIcon(str, i);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        return actionBarMenuSubItem;
    }
}
