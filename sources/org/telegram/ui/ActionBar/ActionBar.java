package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.FireworksEffect;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SnowflakesEffect;

public class ActionBar extends FrameLayout {
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    private ActionBarMenu actionMode;
    private AnimatorSet actionModeAnimation;
    private View actionModeExtraView;
    private View[] actionModeHidingViews;
    private View actionModeShowingView;
    private View actionModeTop;
    private View actionModeTranslationView;
    private boolean actionModeVisible;
    private boolean addToContainer;
    private boolean allowOverlayTitle;
    private ImageView backButtonImageView;
    private boolean castShadows;
    private boolean clipContent;
    private int extraHeight;
    private FireworksEffect fireworksEffect;
    private FontMetricsInt fontMetricsInt;
    private boolean ignoreLayoutRequest;
    private boolean interceptTouches;
    private boolean isBackOverlayVisible;
    protected boolean isSearchFieldVisible;
    protected int itemsActionModeBackgroundColor;
    protected int itemsActionModeColor;
    protected int itemsBackgroundColor;
    protected int itemsColor;
    private Runnable lastRunnable;
    private CharSequence lastTitle;
    private boolean manualStart;
    private ActionBarMenu menu;
    private boolean occupyStatusBar;
    protected BaseFragment parentFragment;
    private Rect rect;
    private SnowflakesEffect snowflakesEffect;
    private SimpleTextView subtitleTextView;
    private boolean supportsHolidayImage;
    private Runnable titleActionRunnable;
    private boolean titleOverlayShown;
    private int titleRightMargin;
    private SimpleTextView titleTextView;

    public static class ActionBarMenuOnItemClick {
        public boolean canOpenMenu() {
            return true;
        }

        public void onItemClick(int i) {
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ActionBar(Context context) {
        super(context);
        this.occupyStatusBar = VERSION.SDK_INT >= 21;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.castShadows = true;
        setOnClickListener(new -$$Lambda$ActionBar$ipLNSE-u7HuyPKHoD94Vr_WlQ-U(this));
    }

    public /* synthetic */ void lambda$new$0$ActionBar(View view) {
        if (!isSearchFieldVisible()) {
            Runnable runnable = this.titleActionRunnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView == null) {
            this.backButtonImageView = new ImageView(getContext());
            this.backButtonImageView.setScaleType(ScaleType.CENTER);
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
            int i = this.itemsColor;
            if (i != 0) {
                this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
            }
            this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0f), 0, 0, 0);
            addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
            this.backButtonImageView.setOnClickListener(new -$$Lambda$ActionBar$VS8eczH_GWXmmp1KP0J3EUbIWcg(this));
            this.backButtonImageView.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
        }
    }

    public /* synthetic */ void lambda$createBackButtonImage$1$ActionBar(View view) {
        if (this.actionModeVisible || !this.isSearchFieldVisible) {
            ActionBarMenuOnItemClick actionBarMenuOnItemClick = this.actionBarMenuOnItemClick;
            if (actionBarMenuOnItemClick != null) {
                actionBarMenuOnItemClick.onItemClick(-1);
            }
            return;
        }
        closeSearchField();
    }

    public void setBackButtonDrawable(Drawable drawable) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(drawable == null ? 8 : 0);
        this.backButtonImageView.setImageDrawable(drawable);
        if (drawable instanceof BackDrawable) {
            BackDrawable backDrawable = (BackDrawable) drawable;
            backDrawable.setRotation(isActionModeShowed() ? 1.0f : 0.0f, false);
            backDrawable.setRotatedColor(this.itemsActionModeColor);
            backDrawable.setColor(this.itemsColor);
        }
    }

    public void setBackButtonContentDescription(CharSequence charSequence) {
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setContentDescription(charSequence);
        }
    }

    public void setSupportsHolidayImage(boolean z) {
        this.supportsHolidayImage = z;
        if (this.supportsHolidayImage) {
            this.fontMetricsInt = new FontMetricsInt();
            this.rect = new Rect();
        }
        invalidate();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && motionEvent.getAction() == 0) {
            Drawable currentHolidayDrawable = Theme.getCurrentHolidayDrawable();
            if (currentHolidayDrawable != null && currentHolidayDrawable.getBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                this.manualStart = true;
                if (this.snowflakesEffect == null) {
                    this.fireworksEffect = null;
                    this.snowflakesEffect = new SnowflakesEffect();
                    this.titleTextView.invalidate();
                    invalidate();
                } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    this.snowflakesEffect = null;
                    this.fireworksEffect = new FireworksEffect();
                    this.titleTextView.invalidate();
                    invalidate();
                }
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* Access modifiers changed, original: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        Object obj = (this.clipContent && (view == this.titleTextView || view == this.subtitleTextView || view == this.actionMode || view == this.menu || view == this.backButtonImageView)) ? 1 : null;
        if (obj != null) {
            canvas.save();
            canvas.clipRect(0.0f, (-getTranslationY()) + ((float) (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0)), (float) getMeasuredWidth(), (float) getMeasuredHeight());
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && view == this.titleTextView) {
            Drawable currentHolidayDrawable = Theme.getCurrentHolidayDrawable();
            if (currentHolidayDrawable != null) {
                TextPaint textPaint = this.titleTextView.getTextPaint();
                textPaint.getFontMetricsInt(this.fontMetricsInt);
                textPaint.getTextBounds((String) this.titleTextView.getText(), 0, 1, this.rect);
                int textStartX = (this.titleTextView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset()) + ((this.rect.width() - (currentHolidayDrawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
                int textStartY = (this.titleTextView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset()) + ((int) Math.ceil((double) (((float) (this.titleTextView.getTextHeight() - this.rect.height())) / 2.0f)));
                currentHolidayDrawable.setBounds(textStartX, textStartY - currentHolidayDrawable.getIntrinsicHeight(), currentHolidayDrawable.getIntrinsicWidth() + textStartX, textStartY);
                currentHolidayDrawable.draw(canvas);
                if (Theme.canStartHolidayAnimation()) {
                    if (this.snowflakesEffect == null) {
                        this.snowflakesEffect = new SnowflakesEffect();
                    }
                } else if (!(this.manualStart || this.snowflakesEffect == null)) {
                    this.snowflakesEffect = null;
                }
                SnowflakesEffect snowflakesEffect = this.snowflakesEffect;
                if (snowflakesEffect != null) {
                    snowflakesEffect.onDraw(this, canvas);
                } else {
                    FireworksEffect fireworksEffect = this.fireworksEffect;
                    if (fireworksEffect != null) {
                        fireworksEffect.onDraw(this, canvas);
                    }
                }
            }
        }
        if (obj != null) {
            canvas.restore();
        }
        return drawChild;
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        if (this.clipContent) {
            invalidate();
        }
    }

    public void setBackButtonImage(int i) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(i == 0 ? 8 : 0);
        this.backButtonImageView.setImageResource(i);
    }

    private void createSubtitleTextView() {
        if (this.subtitleTextView == null) {
            this.subtitleTextView = new SimpleTextView(getContext());
            this.subtitleTextView.setGravity(3);
            this.subtitleTextView.setVisibility(8);
            this.subtitleTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
            addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setAddToContainer(boolean z) {
        this.addToContainer = z;
    }

    public boolean getAddToContainer() {
        return this.addToContainer;
    }

    public void setClipContent(boolean z) {
        this.clipContent = z;
    }

    public void setSubtitle(CharSequence charSequence) {
        if (charSequence != null && this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        SimpleTextView simpleTextView = this.subtitleTextView;
        if (simpleTextView != null) {
            int i = (TextUtils.isEmpty(charSequence) || this.isSearchFieldVisible) ? 8 : 0;
            simpleTextView.setVisibility(i);
            this.subtitleTextView.setText(charSequence);
        }
    }

    private void createTitleTextView() {
        if (this.titleTextView == null) {
            this.titleTextView = new SimpleTextView(getContext());
            this.titleTextView.setGravity(3);
            this.titleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.titleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setTitleRightMargin(int i) {
        this.titleRightMargin = i;
    }

    public void setTitle(CharSequence charSequence) {
        if (charSequence != null && this.titleTextView == null) {
            createTitleTextView();
        }
        SimpleTextView simpleTextView = this.titleTextView;
        if (simpleTextView != null) {
            this.lastTitle = charSequence;
            int i = (charSequence == null || this.isSearchFieldVisible) ? 4 : 0;
            simpleTextView.setVisibility(i);
            this.titleTextView.setText(charSequence);
        }
    }

    public void setTitleColor(int i) {
        if (this.titleTextView == null) {
            createTitleTextView();
        }
        this.titleTextView.setTextColor(i);
    }

    public void setSubtitleColor(int i) {
        if (this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        this.subtitleTextView.setTextColor(i);
    }

    public void setPopupItemsColor(int i, boolean z) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setPopupItemsColor(i, z);
        }
    }

    public void setPopupBackgroundColor(int i) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.redrawPopup(i);
        }
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView;
    }

    public String getTitle() {
        SimpleTextView simpleTextView = this.titleTextView;
        if (simpleTextView == null) {
            return null;
        }
        return simpleTextView.getText().toString();
    }

    public String getSubtitle() {
        SimpleTextView simpleTextView = this.subtitleTextView;
        if (simpleTextView == null) {
            return null;
        }
        return simpleTextView.getText().toString();
    }

    public ActionBarMenu createMenu() {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            return actionBarMenu;
        }
        this.menu = new ActionBarMenu(getContext(), this);
        addView(this.menu, 0, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick actionBarMenuOnItemClick) {
        this.actionBarMenuOnItemClick = actionBarMenuOnItemClick;
    }

    public ActionBarMenuOnItemClick getActionBarMenuOnItemClick() {
        return this.actionBarMenuOnItemClick;
    }

    public View getBackButton() {
        return this.backButtonImageView;
    }

    public ActionBarMenu createActionMode() {
        return createActionMode(true);
    }

    public ActionBarMenu createActionMode(boolean z) {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            return actionBarMenu;
        }
        this.actionMode = new ActionBarMenu(getContext(), this);
        actionBarMenu = this.actionMode;
        actionBarMenu.isActionMode = true;
        actionBarMenu.setBackgroundColor(Theme.getColor("actionBarActionModeDefault"));
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        LayoutParams layoutParams = (LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.bottomMargin = this.extraHeight;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        if (this.occupyStatusBar && z && this.actionModeTop == null) {
            this.actionModeTop = new View(getContext());
            this.actionModeTop.setBackgroundColor(Theme.getColor("actionBarActionModeDefaultTop"));
            addView(this.actionModeTop);
            LayoutParams layoutParams2 = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams2.height = AndroidUtilities.statusBarHeight;
            layoutParams2.width = -1;
            layoutParams2.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams2);
            this.actionModeTop.setVisibility(4);
        }
        return this.actionMode;
    }

    public void showActionMode() {
        showActionMode(null, null, null, null, null, 0);
    }

    public void showActionMode(View view, View view2, View[] viewArr, final boolean[] zArr, View view3, int i) {
        if (this.actionMode != null && !this.actionModeVisible) {
            this.actionModeVisible = true;
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.actionMode, View.ALPHA, new float[]{0.0f, 1.0f}));
            if (viewArr != null) {
                for (int i2 = 0; i2 < viewArr.length; i2++) {
                    if (viewArr[i2] != null) {
                        arrayList.add(ObjectAnimator.ofFloat(viewArr[i2], View.ALPHA, new float[]{1.0f, 0.0f}));
                    }
                }
            }
            if (view2 != null) {
                arrayList.add(ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{0.0f, 1.0f}));
            }
            if (view3 != null) {
                arrayList.add(ObjectAnimator.ofFloat(view3, View.TRANSLATION_Y, new float[]{(float) i}));
                this.actionModeTranslationView = view3;
            }
            this.actionModeExtraView = view;
            this.actionModeShowingView = view2;
            this.actionModeHidingViews = viewArr;
            if (this.occupyStatusBar) {
                view = this.actionModeTop;
                if (view != null) {
                    arrayList.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}));
                }
            }
            AnimatorSet animatorSet = this.actionModeAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(arrayList);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    ActionBar.this.actionMode.setVisibility(0);
                    if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null) {
                        ActionBar.this.actionModeTop.setVisibility(0);
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                        ActionBar.this.actionModeAnimation = null;
                        if (ActionBar.this.titleTextView != null) {
                            ActionBar.this.titleTextView.setVisibility(4);
                        }
                        if (!(ActionBar.this.subtitleTextView == null || TextUtils.isEmpty(ActionBar.this.subtitleTextView.getText()))) {
                            ActionBar.this.subtitleTextView.setVisibility(4);
                        }
                        if (ActionBar.this.menu != null) {
                            ActionBar.this.menu.setVisibility(4);
                        }
                        if (ActionBar.this.actionModeHidingViews != null) {
                            int i = 0;
                            while (i < ActionBar.this.actionModeHidingViews.length) {
                                if (ActionBar.this.actionModeHidingViews[i] != null) {
                                    boolean[] zArr = zArr;
                                    if (zArr == null || i >= zArr.length || zArr[i]) {
                                        ActionBar.this.actionModeHidingViews[i].setVisibility(4);
                                    }
                                }
                                i++;
                            }
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                        ActionBar.this.actionModeAnimation = null;
                    }
                }
            });
            this.actionModeAnimation.start();
            ImageView imageView = this.backButtonImageView;
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(1.0f, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
            }
        }
    }

    public void hideActionMode() {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null && this.actionModeVisible) {
            actionBarMenu.hideAllPopupMenus();
            this.actionModeVisible = false;
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.actionMode, View.ALPHA, new float[]{0.0f}));
            if (this.actionModeHidingViews != null) {
                int i = 0;
                while (true) {
                    View[] viewArr = this.actionModeHidingViews;
                    if (i >= viewArr.length) {
                        break;
                    }
                    if (viewArr != null) {
                        viewArr[i].setVisibility(0);
                        arrayList.add(ObjectAnimator.ofFloat(this.actionModeHidingViews[i], View.ALPHA, new float[]{1.0f}));
                    }
                    i++;
                }
            }
            View view = this.actionModeTranslationView;
            if (view != null) {
                arrayList.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f}));
                this.actionModeTranslationView = null;
            }
            view = this.actionModeShowingView;
            if (view != null) {
                arrayList.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f}));
            }
            if (this.occupyStatusBar) {
                view = this.actionModeTop;
                if (view != null) {
                    arrayList.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f}));
                }
            }
            AnimatorSet animatorSet = this.actionModeAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(arrayList);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                        ActionBar.this.actionModeAnimation = null;
                        ActionBar.this.actionMode.setVisibility(4);
                        if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null) {
                            ActionBar.this.actionModeTop.setVisibility(4);
                        }
                        if (ActionBar.this.actionModeExtraView != null) {
                            ActionBar.this.actionModeExtraView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                        ActionBar.this.actionModeAnimation = null;
                    }
                }
            });
            this.actionModeAnimation.start();
            if (!this.isSearchFieldVisible) {
                SimpleTextView simpleTextView = this.titleTextView;
                if (simpleTextView != null) {
                    simpleTextView.setVisibility(0);
                }
                simpleTextView = this.subtitleTextView;
                if (!(simpleTextView == null || TextUtils.isEmpty(simpleTextView.getText()))) {
                    this.subtitleTextView.setVisibility(0);
                }
            }
            ActionBarMenu actionBarMenu2 = this.menu;
            if (actionBarMenu2 != null) {
                actionBarMenu2.setVisibility(0);
            }
            ImageView imageView = this.backButtonImageView;
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(0.0f, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
            }
        }
    }

    public void showActionModeTop() {
        if (this.occupyStatusBar && this.actionModeTop == null) {
            this.actionModeTop = new View(getContext());
            this.actionModeTop.setBackgroundColor(Theme.getColor("actionBarActionModeDefaultTop"));
            addView(this.actionModeTop);
            LayoutParams layoutParams = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
        }
    }

    public void setActionModeTopColor(int i) {
        View view = this.actionModeTop;
        if (view != null) {
            view.setBackgroundColor(i);
        }
    }

    public void setSearchTextColor(int i, boolean z) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setSearchTextColor(i, z);
        }
    }

    public void setActionModeColor(int i) {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setBackgroundColor(i);
        }
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    /* Access modifiers changed, original: protected */
    public void onSearchFieldVisibilityChanged(boolean z) {
        this.isSearchFieldVisible = z;
        SimpleTextView simpleTextView = this.titleTextView;
        int i = 4;
        if (simpleTextView != null) {
            simpleTextView.setVisibility(z ? 4 : 0);
        }
        simpleTextView = this.subtitleTextView;
        if (!(simpleTextView == null || TextUtils.isEmpty(simpleTextView.getText()))) {
            simpleTextView = this.subtitleTextView;
            if (!z) {
                i = 0;
            }
            simpleTextView.setVisibility(i);
        }
        Drawable drawable = this.backButtonImageView.getDrawable();
        if (drawable instanceof MenuDrawable) {
            MenuDrawable menuDrawable = (MenuDrawable) drawable;
            menuDrawable.setRotateToBack(true);
            menuDrawable.setRotation(z ? 1.0f : 0.0f, true);
        }
    }

    public void setInterceptTouches(boolean z) {
        this.interceptTouches = z;
    }

    public void setExtraHeight(int i) {
        this.extraHeight = i;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            LayoutParams layoutParams = (LayoutParams) actionBarMenu.getLayoutParams();
            layoutParams.bottomMargin = this.extraHeight;
            this.actionMode.setLayoutParams(layoutParams);
        }
    }

    public void closeSearchField() {
        closeSearchField(true);
    }

    public void closeSearchField(boolean z) {
        if (this.isSearchFieldVisible) {
            ActionBarMenu actionBarMenu = this.menu;
            if (actionBarMenu != null) {
                actionBarMenu.closeSearchField(z);
            }
        }
    }

    public void openSearchField(String str, boolean z) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null && str != null) {
            actionBarMenu.openSearchField(this.isSearchFieldVisible ^ 1, str, z);
        }
    }

    public void setSearchFieldText(String str) {
        this.menu.setSearchFieldText(str);
    }

    public void onSearchPressed() {
        this.menu.onSearchPressed();
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setEnabled(z);
        }
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setEnabled(z);
        }
        actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setEnabled(z);
        }
    }

    public void requestLayout() {
        if (!this.ignoreLayoutRequest) {
            super.requestLayout();
        }
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0196  */
    /* JADX WARNING: Missing block: B:48:0x00be, code skipped:
            if (r2.getVisibility() != 8) goto L_0x00c0;
     */
    public void onMeasure(int r12, int r13) {
        /*
        r11 = this;
        r0 = android.view.View.MeasureSpec.getSize(r12);
        android.view.View.MeasureSpec.getSize(r13);
        r13 = getCurrentActionBarHeight();
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r1);
        r3 = 1;
        r11.ignoreLayoutRequest = r3;
        r3 = r11.actionModeTop;
        if (r3 == 0) goto L_0x0022;
    L_0x0018:
        r3 = r3.getLayoutParams();
        r3 = (android.widget.FrameLayout.LayoutParams) r3;
        r4 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r3.height = r4;
    L_0x0022:
        r3 = r11.actionMode;
        r4 = 0;
        if (r3 == 0) goto L_0x0032;
    L_0x0027:
        r5 = r11.occupyStatusBar;
        if (r5 == 0) goto L_0x002e;
    L_0x002b:
        r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x002f;
    L_0x002e:
        r5 = 0;
    L_0x002f:
        r3.setPadding(r4, r5, r4, r4);
    L_0x0032:
        r11.ignoreLayoutRequest = r4;
        r3 = r11.occupyStatusBar;
        if (r3 == 0) goto L_0x003b;
    L_0x0038:
        r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x003c;
    L_0x003b:
        r3 = 0;
    L_0x003c:
        r13 = r13 + r3;
        r3 = r11.extraHeight;
        r13 = r13 + r3;
        r11.setMeasuredDimension(r0, r13);
        r13 = r11.backButtonImageView;
        r3 = 8;
        if (r13 == 0) goto L_0x006e;
    L_0x0049:
        r13 = r13.getVisibility();
        if (r13 == r3) goto L_0x006e;
    L_0x004f:
        r13 = r11.backButtonImageView;
        r5 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r1);
        r13.measure(r5, r2);
        r13 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r13 == 0) goto L_0x0067;
    L_0x0064:
        r13 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        goto L_0x0069;
    L_0x0067:
        r13 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
    L_0x0069:
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        goto L_0x007d;
    L_0x006e:
        r13 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r13 == 0) goto L_0x0077;
    L_0x0074:
        r13 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        goto L_0x0079;
    L_0x0077:
        r13 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
    L_0x0079:
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
    L_0x007d:
        r5 = r11.menu;
        r6 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r5 == 0) goto L_0x00ac;
    L_0x0083:
        r5 = r5.getVisibility();
        if (r5 == r3) goto L_0x00ac;
    L_0x0089:
        r5 = r11.isSearchFieldVisible;
        if (r5 == 0) goto L_0x00a3;
    L_0x008d:
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r5 == 0) goto L_0x0096;
    L_0x0093:
        r5 = NUM; // 0x42940000 float:74.0 double:5.518691446E-315;
        goto L_0x0098;
    L_0x0096:
        r5 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
    L_0x0098:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r0 - r5;
        r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r1);
        goto L_0x00a7;
    L_0x00a3:
        r5 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
    L_0x00a7:
        r7 = r11.menu;
        r7.measure(r5, r2);
    L_0x00ac:
        r2 = r11.titleTextView;
        if (r2 == 0) goto L_0x00b6;
    L_0x00b0:
        r2 = r2.getVisibility();
        if (r2 != r3) goto L_0x00c0;
    L_0x00b6:
        r2 = r11.subtitleTextView;
        if (r2 == 0) goto L_0x0190;
    L_0x00ba:
        r2 = r2.getVisibility();
        if (r2 == r3) goto L_0x0190;
    L_0x00c0:
        r2 = r11.menu;
        if (r2 == 0) goto L_0x00c9;
    L_0x00c4:
        r2 = r2.getMeasuredWidth();
        goto L_0x00ca;
    L_0x00c9:
        r2 = 0;
    L_0x00ca:
        r0 = r0 - r2;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r0 = r0 - r13;
        r13 = r11.titleRightMargin;
        r0 = r0 - r13;
        r13 = r11.titleTextView;
        r2 = 14;
        r5 = 18;
        r7 = 16;
        r8 = 20;
        if (r13 == 0) goto L_0x010d;
    L_0x00e2:
        r13 = r13.getVisibility();
        if (r13 == r3) goto L_0x010d;
    L_0x00e8:
        r13 = r11.subtitleTextView;
        if (r13 == 0) goto L_0x010d;
    L_0x00ec:
        r13 = r13.getVisibility();
        if (r13 == r3) goto L_0x010d;
    L_0x00f2:
        r13 = r11.titleTextView;
        r9 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r9 == 0) goto L_0x00fc;
    L_0x00fa:
        r5 = 20;
    L_0x00fc:
        r13.setTextSize(r5);
        r13 = r11.subtitleTextView;
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r5 == 0) goto L_0x0109;
    L_0x0107:
        r2 = 16;
    L_0x0109:
        r13.setTextSize(r2);
        goto L_0x0156;
    L_0x010d:
        r13 = r11.titleTextView;
        r9 = 2;
        if (r13 == 0) goto L_0x0132;
    L_0x0112:
        r13 = r13.getVisibility();
        if (r13 == r3) goto L_0x0132;
    L_0x0118:
        r13 = r11.titleTextView;
        r10 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r10 != 0) goto L_0x012d;
    L_0x0120:
        r10 = r11.getResources();
        r10 = r10.getConfiguration();
        r10 = r10.orientation;
        if (r10 != r9) goto L_0x012d;
    L_0x012c:
        goto L_0x012f;
    L_0x012d:
        r5 = 20;
    L_0x012f:
        r13.setTextSize(r5);
    L_0x0132:
        r13 = r11.subtitleTextView;
        if (r13 == 0) goto L_0x0156;
    L_0x0136:
        r13 = r13.getVisibility();
        if (r13 == r3) goto L_0x0156;
    L_0x013c:
        r13 = r11.subtitleTextView;
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r5 != 0) goto L_0x0151;
    L_0x0144:
        r5 = r11.getResources();
        r5 = r5.getConfiguration();
        r5 = r5.orientation;
        if (r5 != r9) goto L_0x0151;
    L_0x0150:
        goto L_0x0153;
    L_0x0151:
        r2 = 16;
    L_0x0153:
        r13.setTextSize(r2);
    L_0x0156:
        r13 = r11.titleTextView;
        if (r13 == 0) goto L_0x0173;
    L_0x015a:
        r13 = r13.getVisibility();
        if (r13 == r3) goto L_0x0173;
    L_0x0160:
        r13 = r11.titleTextView;
        r2 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
        r5 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r6);
        r13.measure(r2, r5);
    L_0x0173:
        r13 = r11.subtitleTextView;
        if (r13 == 0) goto L_0x0190;
    L_0x0177:
        r13 = r13.getVisibility();
        if (r13 == r3) goto L_0x0190;
    L_0x017d:
        r13 = r11.subtitleTextView;
        r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
        r2 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r6);
        r13.measure(r0, r2);
    L_0x0190:
        r13 = r11.getChildCount();
    L_0x0194:
        if (r4 >= r13) goto L_0x01c3;
    L_0x0196:
        r6 = r11.getChildAt(r4);
        r0 = r6.getVisibility();
        if (r0 == r3) goto L_0x01c0;
    L_0x01a0:
        r0 = r11.titleTextView;
        if (r6 == r0) goto L_0x01c0;
    L_0x01a4:
        r0 = r11.subtitleTextView;
        if (r6 == r0) goto L_0x01c0;
    L_0x01a8:
        r0 = r11.menu;
        if (r6 == r0) goto L_0x01c0;
    L_0x01ac:
        r0 = r11.backButtonImageView;
        if (r6 != r0) goto L_0x01b1;
    L_0x01b0:
        goto L_0x01c0;
    L_0x01b1:
        r8 = 0;
        r0 = r11.getMeasuredHeight();
        r9 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r1);
        r10 = 0;
        r5 = r11;
        r7 = r12;
        r5.measureChildWithMargins(r6, r7, r8, r9, r10);
    L_0x01c0:
        r4 = r4 + 1;
        goto L_0x0194;
    L_0x01c3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBar.onMeasure(int, int):void");
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0177  */
    public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
        /*
        r10 = this;
        r11 = r10.occupyStatusBar;
        r0 = 0;
        if (r11 == 0) goto L_0x0008;
    L_0x0005:
        r11 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x0009;
    L_0x0008:
        r11 = 0;
    L_0x0009:
        r1 = r10.backButtonImageView;
        r2 = 8;
        if (r1 == 0) goto L_0x0035;
    L_0x000f:
        r1 = r1.getVisibility();
        if (r1 == r2) goto L_0x0035;
    L_0x0015:
        r1 = r10.backButtonImageView;
        r3 = r1.getMeasuredWidth();
        r4 = r10.backButtonImageView;
        r4 = r4.getMeasuredHeight();
        r4 = r4 + r11;
        r1.layout(r0, r11, r3, r4);
        r1 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r1 == 0) goto L_0x002e;
    L_0x002b:
        r1 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        goto L_0x0030;
    L_0x002e:
        r1 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
    L_0x0030:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        goto L_0x0044;
    L_0x0035:
        r1 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r1 == 0) goto L_0x003e;
    L_0x003b:
        r1 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        goto L_0x0040;
    L_0x003e:
        r1 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
    L_0x0040:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
    L_0x0044:
        r3 = r10.menu;
        if (r3 == 0) goto L_0x007c;
    L_0x0048:
        r3 = r3.getVisibility();
        if (r3 == r2) goto L_0x007c;
    L_0x004e:
        r3 = r10.isSearchFieldVisible;
        if (r3 == 0) goto L_0x0062;
    L_0x0052:
        r3 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r3 == 0) goto L_0x005b;
    L_0x0058:
        r3 = NUM; // 0x42940000 float:74.0 double:5.518691446E-315;
        goto L_0x005d;
    L_0x005b:
        r3 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
    L_0x005d:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        goto L_0x006b;
    L_0x0062:
        r3 = r14 - r12;
        r4 = r10.menu;
        r4 = r4.getMeasuredWidth();
        r3 = r3 - r4;
    L_0x006b:
        r4 = r10.menu;
        r5 = r4.getMeasuredWidth();
        r5 = r5 + r3;
        r6 = r10.menu;
        r6 = r6.getMeasuredHeight();
        r6 = r6 + r11;
        r4.layout(r3, r11, r5, r6);
    L_0x007c:
        r3 = r10.titleTextView;
        r4 = 2;
        if (r3 == 0) goto L_0x00d9;
    L_0x0081:
        r3 = r3.getVisibility();
        if (r3 == r2) goto L_0x00d9;
    L_0x0087:
        r3 = r10.subtitleTextView;
        if (r3 == 0) goto L_0x00bb;
    L_0x008b:
        r3 = r3.getVisibility();
        if (r3 == r2) goto L_0x00bb;
    L_0x0091:
        r3 = getCurrentActionBarHeight();
        r3 = r3 / r4;
        r5 = r10.titleTextView;
        r5 = r5.getTextHeight();
        r3 = r3 - r5;
        r3 = r3 / r4;
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r5 != 0) goto L_0x00b3;
    L_0x00a4:
        r5 = r10.getResources();
        r5 = r5.getConfiguration();
        r5 = r5.orientation;
        if (r5 != r4) goto L_0x00b3;
    L_0x00b0:
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x00b5;
    L_0x00b3:
        r5 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
    L_0x00b5:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 + r5;
        goto L_0x00c7;
    L_0x00bb:
        r3 = getCurrentActionBarHeight();
        r5 = r10.titleTextView;
        r5 = r5.getTextHeight();
        r3 = r3 - r5;
        r3 = r3 / r4;
    L_0x00c7:
        r5 = r10.titleTextView;
        r3 = r3 + r11;
        r6 = r5.getMeasuredWidth();
        r6 = r6 + r1;
        r7 = r10.titleTextView;
        r7 = r7.getTextHeight();
        r7 = r7 + r3;
        r5.layout(r1, r3, r6, r7);
    L_0x00d9:
        r3 = r10.subtitleTextView;
        if (r3 == 0) goto L_0x011f;
    L_0x00dd:
        r3 = r3.getVisibility();
        if (r3 == r2) goto L_0x011f;
    L_0x00e3:
        r3 = getCurrentActionBarHeight();
        r3 = r3 / r4;
        r5 = getCurrentActionBarHeight();
        r5 = r5 / r4;
        r6 = r10.subtitleTextView;
        r6 = r6.getTextHeight();
        r5 = r5 - r6;
        r5 = r5 / r4;
        r3 = r3 + r5;
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r5 != 0) goto L_0x0106;
    L_0x00fc:
        r5 = r10.getResources();
        r5 = r5.getConfiguration();
        r5 = r5.orientation;
    L_0x0106:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 - r5;
        r5 = r10.subtitleTextView;
        r11 = r11 + r3;
        r3 = r5.getMeasuredWidth();
        r3 = r3 + r1;
        r6 = r10.subtitleTextView;
        r6 = r6.getTextHeight();
        r6 = r6 + r11;
        r5.layout(r1, r11, r3, r6);
    L_0x011f:
        r11 = r10.getChildCount();
    L_0x0123:
        if (r0 >= r11) goto L_0x019e;
    L_0x0125:
        r1 = r10.getChildAt(r0);
        r3 = r1.getVisibility();
        if (r3 == r2) goto L_0x019b;
    L_0x012f:
        r3 = r10.titleTextView;
        if (r1 == r3) goto L_0x019b;
    L_0x0133:
        r3 = r10.subtitleTextView;
        if (r1 == r3) goto L_0x019b;
    L_0x0137:
        r3 = r10.menu;
        if (r1 == r3) goto L_0x019b;
    L_0x013b:
        r3 = r10.backButtonImageView;
        if (r1 != r3) goto L_0x0140;
    L_0x013f:
        goto L_0x019b;
    L_0x0140:
        r3 = r1.getLayoutParams();
        r3 = (android.widget.FrameLayout.LayoutParams) r3;
        r5 = r1.getMeasuredWidth();
        r6 = r1.getMeasuredHeight();
        r7 = r3.gravity;
        r8 = -1;
        if (r7 != r8) goto L_0x0155;
    L_0x0153:
        r7 = 51;
    L_0x0155:
        r8 = r7 & 7;
        r7 = r7 & 112;
        r8 = r8 & 7;
        r9 = 1;
        if (r8 == r9) goto L_0x0169;
    L_0x015e:
        r9 = 5;
        if (r8 == r9) goto L_0x0164;
    L_0x0161:
        r8 = r3.leftMargin;
        goto L_0x0173;
    L_0x0164:
        r8 = r14 - r5;
        r9 = r3.rightMargin;
        goto L_0x0172;
    L_0x0169:
        r8 = r14 - r12;
        r8 = r8 - r5;
        r8 = r8 / r4;
        r9 = r3.leftMargin;
        r8 = r8 + r9;
        r9 = r3.rightMargin;
    L_0x0172:
        r8 = r8 - r9;
    L_0x0173:
        r9 = 16;
        if (r7 == r9) goto L_0x018b;
    L_0x0177:
        r9 = 48;
        if (r7 == r9) goto L_0x0188;
    L_0x017b:
        r9 = 80;
        if (r7 == r9) goto L_0x0182;
    L_0x017f:
        r3 = r3.topMargin;
        goto L_0x0196;
    L_0x0182:
        r7 = r15 - r13;
        r7 = r7 - r6;
        r3 = r3.bottomMargin;
        goto L_0x0194;
    L_0x0188:
        r3 = r3.topMargin;
        goto L_0x0196;
    L_0x018b:
        r7 = r15 - r13;
        r7 = r7 - r6;
        r7 = r7 / r4;
        r9 = r3.topMargin;
        r7 = r7 + r9;
        r3 = r3.bottomMargin;
    L_0x0194:
        r3 = r7 - r3;
    L_0x0196:
        r5 = r5 + r8;
        r6 = r6 + r3;
        r1.layout(r8, r3, r5, r6);
    L_0x019b:
        r0 = r0 + 1;
        goto L_0x0123;
    L_0x019e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBar.onLayout(boolean, int, int, int, int):void");
    }

    public void onMenuButtonPressed() {
        if (!isActionModeShowed()) {
            ActionBarMenu actionBarMenu = this.menu;
            if (actionBarMenu != null) {
                actionBarMenu.onMenuButtonPressed();
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.hideAllPopupMenus();
        }
    }

    public void setAllowOverlayTitle(boolean z) {
        this.allowOverlayTitle = z;
    }

    public void setTitleActionRunnable(Runnable runnable) {
        this.titleActionRunnable = runnable;
        this.lastRunnable = runnable;
    }

    public void setTitleOverlayText(String str, int i, Runnable runnable) {
        if (this.allowOverlayTitle && this.parentFragment.parentLayout != null) {
            CharSequence string = str != null ? LocaleController.getString(str, i) : this.lastTitle;
            if (string != null && this.titleTextView == null) {
                createTitleTextView();
            }
            if (this.titleTextView != null) {
                int i2 = 0;
                this.titleOverlayShown = str != null;
                if (this.supportsHolidayImage) {
                    this.titleTextView.invalidate();
                    invalidate();
                }
                SimpleTextView simpleTextView = this.titleTextView;
                if (string == null || this.isSearchFieldVisible) {
                    i2 = 4;
                }
                simpleTextView.setVisibility(i2);
                this.titleTextView.setText(string);
            }
            if (runnable == null) {
                runnable = this.lastRunnable;
            }
            this.titleActionRunnable = runnable;
        }
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public void setItemsBackgroundColor(int i, boolean z) {
        ImageView imageView;
        ActionBarMenu actionBarMenu;
        if (z) {
            this.itemsActionModeBackgroundColor = i;
            if (this.actionModeVisible) {
                imageView = this.backButtonImageView;
                if (imageView != null) {
                    imageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
                }
            }
            actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsBackgroundColor();
                return;
            }
            return;
        }
        this.itemsBackgroundColor = i;
        imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        }
        actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int i, boolean z) {
        ImageView imageView;
        Drawable drawable;
        if (z) {
            this.itemsActionModeColor = i;
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsColor();
            }
            imageView = this.backButtonImageView;
            if (imageView != null) {
                drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotatedColor(i);
                    return;
                }
                return;
            }
            return;
        }
        this.itemsColor = i;
        imageView = this.backButtonImageView;
        if (imageView != null) {
            int i2 = this.itemsColor;
            if (i2 != 0) {
                imageView.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
                drawable = this.backButtonImageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setColor(i);
                }
            }
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsColor();
        }
    }

    public void setCastShadows(boolean z) {
        this.castShadows = z;
    }

    public boolean getCastShadows() {
        return this.castShadows;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent) || this.interceptTouches;
    }

    public static int getCurrentActionBarHeight() {
        if (AndroidUtilities.isTablet()) {
            return AndroidUtilities.dp(64.0f);
        }
        Point point = AndroidUtilities.displaySize;
        if (point.x > point.y) {
            return AndroidUtilities.dp(48.0f);
        }
        return AndroidUtilities.dp(56.0f);
    }
}
