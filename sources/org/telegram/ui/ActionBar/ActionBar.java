package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.FireworksEffect;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SnowflakesEffect;

public class ActionBar extends FrameLayout {
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    /* access modifiers changed from: private */
    public ActionBarMenu actionMode;
    /* access modifiers changed from: private */
    public AnimatorSet actionModeAnimation;
    /* access modifiers changed from: private */
    public View actionModeExtraView;
    /* access modifiers changed from: private */
    public View[] actionModeHidingViews;
    private View actionModeShowingView;
    /* access modifiers changed from: private */
    public View actionModeTop;
    private View actionModeTranslationView;
    private boolean actionModeVisible;
    private boolean addToContainer;
    private boolean allowOverlayTitle;
    private ImageView backButtonImageView;
    private boolean castShadows;
    private boolean clipContent;
    private int extraHeight;
    private FireworksEffect fireworksEffect;
    private Paint.FontMetricsInt fontMetricsInt;
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
    /* access modifiers changed from: private */
    public ActionBarMenu menu;
    /* access modifiers changed from: private */
    public boolean occupyStatusBar;
    protected BaseFragment parentFragment;
    private Rect rect;
    private SnowflakesEffect snowflakesEffect;
    /* access modifiers changed from: private */
    public SimpleTextView subtitleTextView;
    private boolean supportsHolidayImage;
    private Runnable titleActionRunnable;
    private boolean titleOverlayShown;
    private int titleRightMargin;
    /* access modifiers changed from: private */
    public SimpleTextView titleTextView;

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
        this.occupyStatusBar = Build.VERSION.SDK_INT >= 21;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.castShadows = true;
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBar.this.lambda$new$0$ActionBar(view);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$ActionBar(View view) {
        Runnable runnable;
        if (!isSearchFieldVisible() && (runnable = this.titleActionRunnable) != null) {
            runnable.run();
        }
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView == null) {
            this.backButtonImageView = new ImageView(getContext());
            this.backButtonImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
            int i = this.itemsColor;
            if (i != 0) {
                this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
            this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0f), 0, 0, 0);
            addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
            this.backButtonImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ActionBar.this.lambda$createBackButtonImage$1$ActionBar(view);
                }
            });
            this.backButtonImageView.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
        }
    }

    public /* synthetic */ void lambda$createBackButtonImage$1$ActionBar(View view) {
        if (this.actionModeVisible || !this.isSearchFieldVisible) {
            ActionBarMenuOnItemClick actionBarMenuOnItemClick2 = this.actionBarMenuOnItemClick;
            if (actionBarMenuOnItemClick2 != null) {
                actionBarMenuOnItemClick2.onItemClick(-1);
                return;
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
            this.fontMetricsInt = new Paint.FontMetricsInt();
            this.rect = new Rect();
        }
        invalidate();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Drawable currentHolidayDrawable;
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && motionEvent.getAction() == 0 && (currentHolidayDrawable = Theme.getCurrentHolidayDrawable()) != null && currentHolidayDrawable.getBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            this.manualStart = true;
            if (this.snowflakesEffect == null) {
                this.fireworksEffect = null;
                this.snowflakesEffect = new SnowflakesEffect();
                this.titleTextView.invalidate();
                invalidate();
            } else {
                this.snowflakesEffect = null;
                this.fireworksEffect = new FireworksEffect();
                this.titleTextView.invalidate();
                invalidate();
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        Drawable currentHolidayDrawable;
        boolean z = this.clipContent && (view == this.titleTextView || view == this.subtitleTextView || view == this.actionMode || view == this.menu || view == this.backButtonImageView);
        if (z) {
            canvas.save();
            canvas.clipRect(0.0f, (-getTranslationY()) + ((float) (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0)), (float) getMeasuredWidth(), (float) getMeasuredHeight());
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && view == this.titleTextView && (currentHolidayDrawable = Theme.getCurrentHolidayDrawable()) != null) {
            TextPaint textPaint = this.titleTextView.getTextPaint();
            textPaint.getFontMetricsInt(this.fontMetricsInt);
            textPaint.getTextBounds((String) this.titleTextView.getText(), 0, 1, this.rect);
            int textStartX = this.titleTextView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset() + ((this.rect.width() - (currentHolidayDrawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
            int textStartY = this.titleTextView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset() + ((int) Math.ceil((double) (((float) (this.titleTextView.getTextHeight() - this.rect.height())) / 2.0f)));
            currentHolidayDrawable.setBounds(textStartX, textStartY - currentHolidayDrawable.getIntrinsicHeight(), currentHolidayDrawable.getIntrinsicWidth() + textStartX, textStartY);
            currentHolidayDrawable.draw(canvas);
            if (Theme.canStartHolidayAnimation()) {
                if (this.snowflakesEffect == null) {
                    this.snowflakesEffect = new SnowflakesEffect();
                }
            } else if (!this.manualStart && this.snowflakesEffect != null) {
                this.snowflakesEffect = null;
            }
            SnowflakesEffect snowflakesEffect2 = this.snowflakesEffect;
            if (snowflakesEffect2 != null) {
                snowflakesEffect2.onDraw(this, canvas);
            } else {
                FireworksEffect fireworksEffect2 = this.fireworksEffect;
                if (fireworksEffect2 != null) {
                    fireworksEffect2.onDraw(this, canvas);
                }
            }
        }
        if (z) {
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

    public boolean shouldAddToContainer() {
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
            simpleTextView.setVisibility((TextUtils.isEmpty(charSequence) || this.isSearchFieldVisible) ? 8 : 0);
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
            simpleTextView.setVisibility((charSequence == null || this.isSearchFieldVisible) ? 4 : 0);
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

    public void setPopupItemsColor(int i, boolean z, boolean z2) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (z2 && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsColor(i, z);
        } else if (!z2 && (actionBarMenu = this.menu) != null) {
            actionBarMenu.setPopupItemsColor(i, z);
        }
    }

    public void setPopupItemsSelectorColor(int i, boolean z) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (z && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsSelectorColor(i);
        } else if (!z && (actionBarMenu = this.menu) != null) {
            actionBarMenu.setPopupItemsSelectorColor(i);
        }
    }

    public void setPopupBackgroundColor(int i, boolean z) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (z && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.redrawPopup(i);
        } else if (!z && (actionBarMenu = this.menu) != null) {
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

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick actionBarMenuOnItemClick2) {
        this.actionBarMenuOnItemClick = actionBarMenuOnItemClick2;
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
        ActionBarMenu actionBarMenu2 = this.actionMode;
        actionBarMenu2.isActionMode = true;
        actionBarMenu2.setClickable(true);
        this.actionMode.setBackgroundColor(Theme.getColor("actionBarActionModeDefault"));
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionMode.getLayoutParams();
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
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams2.height = AndroidUtilities.statusBarHeight;
            layoutParams2.width = -1;
            layoutParams2.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams2);
            this.actionModeTop.setVisibility(4);
        }
        return this.actionMode;
    }

    public void showActionMode() {
        showActionMode((View) null, (View) null, (View[]) null, (boolean[]) null, (View) null, 0);
    }

    public void showActionMode(View view, View view2, View[] viewArr, final boolean[] zArr, View view3, int i) {
        View view4;
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
            if (this.occupyStatusBar && (view4 = this.actionModeTop) != null) {
                arrayList.add(ObjectAnimator.ofFloat(view4, View.ALPHA, new float[]{0.0f, 1.0f}));
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
                    boolean[] zArr;
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                        AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
                        if (ActionBar.this.titleTextView != null) {
                            ActionBar.this.titleTextView.setVisibility(4);
                        }
                        if (ActionBar.this.subtitleTextView != null && !TextUtils.isEmpty(ActionBar.this.subtitleTextView.getText())) {
                            ActionBar.this.subtitleTextView.setVisibility(4);
                        }
                        if (ActionBar.this.menu != null) {
                            ActionBar.this.menu.setVisibility(4);
                        }
                        if (ActionBar.this.actionModeHidingViews != null) {
                            for (int i = 0; i < ActionBar.this.actionModeHidingViews.length; i++) {
                                if (ActionBar.this.actionModeHidingViews[i] != null && ((zArr = zArr) == null || i >= zArr.length || zArr[i])) {
                                    ActionBar.this.actionModeHidingViews[i].setVisibility(4);
                                }
                            }
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                        AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
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
        View view;
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
            View view2 = this.actionModeTranslationView;
            if (view2 != null) {
                arrayList.add(ObjectAnimator.ofFloat(view2, View.TRANSLATION_Y, new float[]{0.0f}));
                this.actionModeTranslationView = null;
            }
            View view3 = this.actionModeShowingView;
            if (view3 != null) {
                arrayList.add(ObjectAnimator.ofFloat(view3, View.ALPHA, new float[]{0.0f}));
            }
            if (this.occupyStatusBar && (view = this.actionModeTop) != null) {
                arrayList.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f}));
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
                        AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
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
                        AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
                    }
                }
            });
            this.actionModeAnimation.start();
            if (!this.isSearchFieldVisible) {
                SimpleTextView simpleTextView = this.titleTextView;
                if (simpleTextView != null) {
                    simpleTextView.setVisibility(0);
                }
                SimpleTextView simpleTextView2 = this.subtitleTextView;
                if (simpleTextView2 != null && !TextUtils.isEmpty(simpleTextView2.getText())) {
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
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionModeTop.getLayoutParams();
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

    public void onSearchFieldVisibilityChanged(boolean z) {
        this.isSearchFieldVisible = z;
        SimpleTextView simpleTextView = this.titleTextView;
        int i = 4;
        if (simpleTextView != null) {
            simpleTextView.setVisibility(z ? 4 : 0);
        }
        SimpleTextView simpleTextView2 = this.subtitleTextView;
        if (simpleTextView2 != null && !TextUtils.isEmpty(simpleTextView2.getText())) {
            SimpleTextView simpleTextView3 = this.subtitleTextView;
            if (!z) {
                i = 0;
            }
            simpleTextView3.setVisibility(i);
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
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionBarMenu.getLayoutParams();
            layoutParams.bottomMargin = this.extraHeight;
            this.actionMode.setLayoutParams(layoutParams);
        }
    }

    public void closeSearchField() {
        closeSearchField(true);
    }

    public void closeSearchField(boolean z) {
        ActionBarMenu actionBarMenu;
        if (this.isSearchFieldVisible && (actionBarMenu = this.menu) != null) {
            actionBarMenu.closeSearchField(z);
        }
    }

    public void openSearchField(String str, boolean z) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null && str != null) {
            actionBarMenu.openSearchField(!this.isSearchFieldVisible, str, z);
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
        ActionBarMenu actionBarMenu2 = this.actionMode;
        if (actionBarMenu2 != null) {
            actionBarMenu2.setEnabled(z);
        }
    }

    public void requestLayout() {
        if (!this.ignoreLayoutRequest) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        SimpleTextView simpleTextView;
        SimpleTextView simpleTextView2;
        int i4;
        int size = View.MeasureSpec.getSize(i);
        View.MeasureSpec.getSize(i2);
        int currentActionBarHeight = getCurrentActionBarHeight();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM);
        this.ignoreLayoutRequest = true;
        View view = this.actionModeTop;
        if (view != null) {
            ((FrameLayout.LayoutParams) view.getLayoutParams()).height = AndroidUtilities.statusBarHeight;
        }
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
        this.ignoreLayoutRequest = false;
        setMeasuredDimension(size, currentActionBarHeight + (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + this.extraHeight);
        ImageView imageView = this.backButtonImageView;
        if (imageView == null || imageView.getVisibility() == 8) {
            i3 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        } else {
            this.backButtonImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), NUM), makeMeasureSpec);
            i3 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (!(actionBarMenu2 == null || actionBarMenu2.getVisibility() == 8)) {
            if (this.isSearchFieldVisible) {
                i4 = View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f), NUM);
            } else {
                i4 = View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            }
            this.menu.measure(i4, makeMeasureSpec);
        }
        SimpleTextView simpleTextView3 = this.titleTextView;
        if (!((simpleTextView3 == null || simpleTextView3.getVisibility() == 8) && ((simpleTextView2 = this.subtitleTextView) == null || simpleTextView2.getVisibility() == 8))) {
            ActionBarMenu actionBarMenu3 = this.menu;
            int measuredWidth = (((size - (actionBarMenu3 != null ? actionBarMenu3.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - i3) - this.titleRightMargin;
            SimpleTextView simpleTextView4 = this.titleTextView;
            int i5 = 14;
            int i6 = 18;
            if (simpleTextView4 == null || simpleTextView4.getVisibility() == 8 || (simpleTextView = this.subtitleTextView) == null || simpleTextView.getVisibility() == 8) {
                SimpleTextView simpleTextView5 = this.titleTextView;
                if (!(simpleTextView5 == null || simpleTextView5.getVisibility() == 8)) {
                    SimpleTextView simpleTextView6 = this.titleTextView;
                    if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) {
                        i6 = 20;
                    }
                    simpleTextView6.setTextSize(i6);
                }
                SimpleTextView simpleTextView7 = this.subtitleTextView;
                if (!(simpleTextView7 == null || simpleTextView7.getVisibility() == 8)) {
                    SimpleTextView simpleTextView8 = this.subtitleTextView;
                    if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) {
                        i5 = 16;
                    }
                    simpleTextView8.setTextSize(i5);
                }
            } else {
                SimpleTextView simpleTextView9 = this.titleTextView;
                if (AndroidUtilities.isTablet()) {
                    i6 = 20;
                }
                simpleTextView9.setTextSize(i6);
                SimpleTextView simpleTextView10 = this.subtitleTextView;
                if (AndroidUtilities.isTablet()) {
                    i5 = 16;
                }
                simpleTextView10.setTextSize(i5);
            }
            SimpleTextView simpleTextView11 = this.titleTextView;
            if (!(simpleTextView11 == null || simpleTextView11.getVisibility() == 8)) {
                this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
            }
            SimpleTextView simpleTextView12 = this.subtitleTextView;
            if (!(simpleTextView12 == null || simpleTextView12.getVisibility() == 8)) {
                this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
            }
        }
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (!(childAt.getVisibility() == 8 || childAt == this.titleTextView || childAt == this.subtitleTextView || childAt == this.menu || childAt == this.backButtonImageView)) {
                measureChildWithMargins(childAt, i, 0, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM), 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x018b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
        /*
            r10 = this;
            boolean r11 = r10.occupyStatusBar
            r0 = 0
            if (r11 == 0) goto L_0x0008
            int r11 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x0009
        L_0x0008:
            r11 = 0
        L_0x0009:
            android.widget.ImageView r1 = r10.backButtonImageView
            r2 = 8
            if (r1 == 0) goto L_0x0035
            int r1 = r1.getVisibility()
            if (r1 == r2) goto L_0x0035
            android.widget.ImageView r1 = r10.backButtonImageView
            int r3 = r1.getMeasuredWidth()
            android.widget.ImageView r4 = r10.backButtonImageView
            int r4 = r4.getMeasuredHeight()
            int r4 = r4 + r11
            r1.layout(r0, r11, r3, r4)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x002e
            r1 = 1117782016(0x42a00000, float:80.0)
            goto L_0x0030
        L_0x002e:
            r1 = 1116733440(0x42900000, float:72.0)
        L_0x0030:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x0044
        L_0x0035:
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x003e
            r1 = 1104150528(0x41d00000, float:26.0)
            goto L_0x0040
        L_0x003e:
            r1 = 1099956224(0x41900000, float:18.0)
        L_0x0040:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
        L_0x0044:
            org.telegram.ui.ActionBar.ActionBarMenu r3 = r10.menu
            if (r3 == 0) goto L_0x007c
            int r3 = r3.getVisibility()
            if (r3 == r2) goto L_0x007c
            boolean r3 = r10.isSearchFieldVisible
            if (r3 == 0) goto L_0x0062
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x005b
            r3 = 1116995584(0x42940000, float:74.0)
            goto L_0x005d
        L_0x005b:
            r3 = 1115947008(0x42840000, float:66.0)
        L_0x005d:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x006b
        L_0x0062:
            int r3 = r14 - r12
            org.telegram.ui.ActionBar.ActionBarMenu r4 = r10.menu
            int r4 = r4.getMeasuredWidth()
            int r3 = r3 - r4
        L_0x006b:
            org.telegram.ui.ActionBar.ActionBarMenu r4 = r10.menu
            int r5 = r4.getMeasuredWidth()
            int r5 = r5 + r3
            org.telegram.ui.ActionBar.ActionBarMenu r6 = r10.menu
            int r6 = r6.getMeasuredHeight()
            int r6 = r6 + r11
            r4.layout(r3, r11, r5, r6)
        L_0x007c:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r10.titleTextView
            r4 = 2
            if (r3 == 0) goto L_0x00d9
            int r3 = r3.getVisibility()
            if (r3 == r2) goto L_0x00d9
            org.telegram.ui.ActionBar.SimpleTextView r3 = r10.subtitleTextView
            if (r3 == 0) goto L_0x00bb
            int r3 = r3.getVisibility()
            if (r3 == r2) goto L_0x00bb
            int r3 = getCurrentActionBarHeight()
            int r3 = r3 / r4
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.titleTextView
            int r5 = r5.getTextHeight()
            int r3 = r3 - r5
            int r3 = r3 / r4
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 != 0) goto L_0x00b3
            android.content.res.Resources r5 = r10.getResources()
            android.content.res.Configuration r5 = r5.getConfiguration()
            int r5 = r5.orientation
            if (r5 != r4) goto L_0x00b3
            r5 = 1073741824(0x40000000, float:2.0)
            goto L_0x00b5
        L_0x00b3:
            r5 = 1077936128(0x40400000, float:3.0)
        L_0x00b5:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 + r5
            goto L_0x00c7
        L_0x00bb:
            int r3 = getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.titleTextView
            int r5 = r5.getTextHeight()
            int r3 = r3 - r5
            int r3 = r3 / r4
        L_0x00c7:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.titleTextView
            int r3 = r3 + r11
            int r6 = r5.getMeasuredWidth()
            int r6 = r6 + r1
            org.telegram.ui.ActionBar.SimpleTextView r7 = r10.titleTextView
            int r7 = r7.getTextHeight()
            int r7 = r7 + r3
            r5.layout(r1, r3, r6, r7)
        L_0x00d9:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r10.subtitleTextView
            if (r3 == 0) goto L_0x011f
            int r3 = r3.getVisibility()
            if (r3 == r2) goto L_0x011f
            int r3 = getCurrentActionBarHeight()
            int r3 = r3 / r4
            int r5 = getCurrentActionBarHeight()
            int r5 = r5 / r4
            org.telegram.ui.ActionBar.SimpleTextView r6 = r10.subtitleTextView
            int r6 = r6.getTextHeight()
            int r5 = r5 - r6
            int r5 = r5 / r4
            int r3 = r3 + r5
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 != 0) goto L_0x0106
            android.content.res.Resources r5 = r10.getResources()
            android.content.res.Configuration r5 = r5.getConfiguration()
            int r5 = r5.orientation
        L_0x0106:
            r5 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 - r5
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.subtitleTextView
            int r11 = r11 + r3
            int r3 = r5.getMeasuredWidth()
            int r3 = r3 + r1
            org.telegram.ui.ActionBar.SimpleTextView r6 = r10.subtitleTextView
            int r6 = r6.getTextHeight()
            int r6 = r6 + r11
            r5.layout(r1, r11, r3, r6)
        L_0x011f:
            int r11 = r10.getChildCount()
        L_0x0123:
            if (r0 >= r11) goto L_0x019e
            android.view.View r1 = r10.getChildAt(r0)
            int r3 = r1.getVisibility()
            if (r3 == r2) goto L_0x019b
            org.telegram.ui.ActionBar.SimpleTextView r3 = r10.titleTextView
            if (r1 == r3) goto L_0x019b
            org.telegram.ui.ActionBar.SimpleTextView r3 = r10.subtitleTextView
            if (r1 == r3) goto L_0x019b
            org.telegram.ui.ActionBar.ActionBarMenu r3 = r10.menu
            if (r1 == r3) goto L_0x019b
            android.widget.ImageView r3 = r10.backButtonImageView
            if (r1 != r3) goto L_0x0140
            goto L_0x019b
        L_0x0140:
            android.view.ViewGroup$LayoutParams r3 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r5 = r1.getMeasuredWidth()
            int r6 = r1.getMeasuredHeight()
            int r7 = r3.gravity
            r8 = -1
            if (r7 != r8) goto L_0x0155
            r7 = 51
        L_0x0155:
            r8 = r7 & 7
            r7 = r7 & 112(0x70, float:1.57E-43)
            r8 = r8 & 7
            r9 = 1
            if (r8 == r9) goto L_0x0169
            r9 = 5
            if (r8 == r9) goto L_0x0164
            int r8 = r3.leftMargin
            goto L_0x0173
        L_0x0164:
            int r8 = r14 - r5
            int r9 = r3.rightMargin
            goto L_0x0172
        L_0x0169:
            int r8 = r14 - r12
            int r8 = r8 - r5
            int r8 = r8 / r4
            int r9 = r3.leftMargin
            int r8 = r8 + r9
            int r9 = r3.rightMargin
        L_0x0172:
            int r8 = r8 - r9
        L_0x0173:
            r9 = 16
            if (r7 == r9) goto L_0x018b
            r9 = 48
            if (r7 == r9) goto L_0x0188
            r9 = 80
            if (r7 == r9) goto L_0x0182
            int r3 = r3.topMargin
            goto L_0x0196
        L_0x0182:
            int r7 = r15 - r13
            int r7 = r7 - r6
            int r3 = r3.bottomMargin
            goto L_0x0194
        L_0x0188:
            int r3 = r3.topMargin
            goto L_0x0196
        L_0x018b:
            int r7 = r15 - r13
            int r7 = r7 - r6
            int r7 = r7 / r4
            int r9 = r3.topMargin
            int r7 = r7 + r9
            int r3 = r3.bottomMargin
        L_0x0194:
            int r3 = r7 - r3
        L_0x0196:
            int r5 = r5 + r8
            int r6 = r6 + r3
            r1.layout(r8, r3, r5, r6)
        L_0x019b:
            int r0 = r0 + 1
            goto L_0x0123
        L_0x019e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBar.onLayout(boolean, int, int, int, int):void");
    }

    public void onMenuButtonPressed() {
        ActionBarMenu actionBarMenu;
        if (!isActionModeShowed() && (actionBarMenu = this.menu) != null) {
            actionBarMenu.onMenuButtonPressed();
        }
    }

    /* access modifiers changed from: protected */
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
        if (z) {
            this.itemsActionModeBackgroundColor = i;
            if (this.actionModeVisible && (imageView = this.backButtonImageView) != null) {
                imageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
            }
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsBackgroundColor();
                return;
            }
            return;
        }
        this.itemsBackgroundColor = i;
        ImageView imageView2 = this.backButtonImageView;
        if (imageView2 != null) {
            imageView2.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int i, boolean z) {
        int i2;
        if (z) {
            this.itemsActionModeColor = i;
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsColor();
            }
            ImageView imageView = this.backButtonImageView;
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotatedColor(i);
                    return;
                }
                return;
            }
            return;
        }
        this.itemsColor = i;
        ImageView imageView2 = this.backButtonImageView;
        if (!(imageView2 == null || (i2 = this.itemsColor) == 0)) {
            imageView2.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
            Drawable drawable2 = this.backButtonImageView.getDrawable();
            if (drawable2 instanceof BackDrawable) {
                ((BackDrawable) drawable2).setColor(i);
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
