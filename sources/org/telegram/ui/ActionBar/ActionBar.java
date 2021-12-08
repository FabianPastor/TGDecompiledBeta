package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.FireworksEffect;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SnowflakesEffect;

public class ActionBar extends FrameLayout {
    private int actionBarColor;
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    /* access modifiers changed from: private */
    public ActionBarMenu actionMode;
    /* access modifiers changed from: private */
    public AnimatorSet actionModeAnimation;
    /* access modifiers changed from: private */
    public int actionModeColor;
    /* access modifiers changed from: private */
    public View actionModeExtraView;
    /* access modifiers changed from: private */
    public View[] actionModeHidingViews;
    private View actionModeShowingView;
    private String actionModeTag;
    /* access modifiers changed from: private */
    public View actionModeTop;
    private View actionModeTranslationView;
    private boolean actionModeVisible;
    private boolean addToContainer;
    private SimpleTextView additionalSubtitleTextView;
    private boolean allowOverlayTitle;
    private ImageView backButtonImageView;
    private boolean castShadows;
    private boolean centerScale;
    private boolean clipContent;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    private int extraHeight;
    private FireworksEffect fireworksEffect;
    private Paint.FontMetricsInt fontMetricsInt;
    private boolean fromBottom;
    private boolean ignoreLayoutRequest;
    private View.OnTouchListener interceptTouchEventListener;
    private boolean interceptTouches;
    private boolean isBackOverlayVisible;
    protected boolean isSearchFieldVisible;
    protected int itemsActionModeBackgroundColor;
    protected int itemsActionModeColor;
    protected int itemsBackgroundColor;
    protected int itemsColor;
    private CharSequence lastOverlayTitle;
    private Runnable lastRunnable;
    private CharSequence lastTitle;
    private boolean manualStart;
    /* access modifiers changed from: private */
    public ActionBarMenu menu;
    /* access modifiers changed from: private */
    public boolean occupyStatusBar;
    private boolean overlayTitleAnimation;
    boolean overlayTitleAnimationInProgress;
    /* access modifiers changed from: private */
    public Object[] overlayTitleToSet;
    protected BaseFragment parentFragment;
    private Rect rect;
    private final Theme.ResourcesProvider resourcesProvider;
    AnimatorSet searchVisibleAnimator;
    private SnowflakesEffect snowflakesEffect;
    /* access modifiers changed from: private */
    public CharSequence subtitle;
    /* access modifiers changed from: private */
    public SimpleTextView subtitleTextView;
    private boolean supportsHolidayImage;
    private Runnable titleActionRunnable;
    /* access modifiers changed from: private */
    public boolean titleAnimationRunning;
    private int titleColorToSet;
    private boolean titleOverlayShown;
    private int titleRightMargin;
    /* access modifiers changed from: private */
    public SimpleTextView[] titleTextView;

    public static class ActionBarMenuOnItemClick {
        public void onItemClick(int id) {
        }

        public boolean canOpenMenu() {
            return true;
        }
    }

    public ActionBar(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public ActionBar(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.titleTextView = new SimpleTextView[2];
        this.occupyStatusBar = Build.VERSION.SDK_INT >= 21;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.overlayTitleToSet = new Object[3];
        this.castShadows = true;
        this.titleColorToSet = 0;
        this.ellipsizeSpanAnimator = new EllipsizeSpanAnimator(this);
        this.resourcesProvider = resourcesProvider2;
        setOnClickListener(new ActionBar$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-ActionBar  reason: not valid java name */
    public /* synthetic */ void m1288lambda$new$0$orgtelegramuiActionBarActionBar(View v) {
        Runnable runnable;
        if (!isSearchFieldVisible() && (runnable = this.titleActionRunnable) != null) {
            runnable.run();
        }
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView == null) {
            ImageView imageView = new ImageView(getContext());
            this.backButtonImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
            if (this.itemsColor != 0) {
                this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.MULTIPLY));
            }
            this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0f), 0, 0, 0);
            addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
            this.backButtonImageView.setOnClickListener(new ActionBar$$ExternalSyntheticLambda0(this));
            this.backButtonImageView.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
        }
    }

    /* renamed from: lambda$createBackButtonImage$1$org-telegram-ui-ActionBar-ActionBar  reason: not valid java name */
    public /* synthetic */ void m1287x3228e969(View v) {
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
        } else if (drawable instanceof MenuDrawable) {
            MenuDrawable menuDrawable = (MenuDrawable) drawable;
            menuDrawable.setBackColor(this.actionBarColor);
            menuDrawable.setIconColor(this.itemsColor);
        }
    }

    public void setBackButtonContentDescription(CharSequence description) {
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setContentDescription(description);
        }
    }

    public void setSupportsHolidayImage(boolean value) {
        this.supportsHolidayImage = value;
        if (value) {
            this.fontMetricsInt = new Paint.FontMetricsInt();
            this.rect = new Rect();
        }
        invalidate();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Drawable drawable;
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && ev.getAction() == 0 && (drawable = Theme.getCurrentHolidayDrawable()) != null && drawable.getBounds().contains((int) ev.getX(), (int) ev.getY())) {
            this.manualStart = true;
            if (this.snowflakesEffect == null) {
                this.fireworksEffect = null;
                this.snowflakesEffect = new SnowflakesEffect();
                this.titleTextView[0].invalidate();
                invalidate();
            } else {
                this.snowflakesEffect = null;
                this.fireworksEffect = new FireworksEffect();
                this.titleTextView[0].invalidate();
                invalidate();
            }
        }
        View.OnTouchListener onTouchListener = this.interceptTouchEventListener;
        if ((onTouchListener == null || !onTouchListener.onTouch(this, ev)) && !super.onInterceptTouchEvent(ev)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldClipChild(View child) {
        if (this.clipContent) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (child == simpleTextViewArr[0] || child == simpleTextViewArr[1] || child == this.subtitleTextView || child == this.menu || child == this.backButtonImageView || child == this.additionalSubtitleTextView) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Drawable drawable;
        boolean clip = shouldClipChild(child);
        if (clip) {
            canvas.save();
            canvas.clipRect(0.0f, (-getTranslationY()) + ((float) (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0)), (float) getMeasuredWidth(), (float) getMeasuredHeight());
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if ((child == simpleTextViewArr[0] || child == simpleTextViewArr[1]) && (drawable = Theme.getCurrentHolidayDrawable()) != null) {
                SimpleTextView titleView = (SimpleTextView) child;
                if (titleView.getVisibility() == 0 && (titleView.getText() instanceof String)) {
                    TextPaint textPaint = titleView.getTextPaint();
                    textPaint.getFontMetricsInt(this.fontMetricsInt);
                    textPaint.getTextBounds((String) titleView.getText(), 0, 1, this.rect);
                    int x = titleView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset() + ((this.rect.width() - (drawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
                    int y = titleView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset() + ((int) Math.ceil((double) (((float) (titleView.getTextHeight() - this.rect.height())) / 2.0f)));
                    drawable.setBounds(x, y - drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth() + x, y);
                    drawable.setAlpha((int) (titleView.getAlpha() * 255.0f));
                    drawable.draw(canvas);
                    if (this.overlayTitleAnimationInProgress) {
                        child.invalidate();
                        invalidate();
                    }
                }
                if (Theme.canStartHolidayAnimation() != 0) {
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
        }
        if (clip) {
            canvas.restore();
        }
        return result;
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        if (this.clipContent) {
            invalidate();
        }
    }

    public void setBackButtonImage(int resource) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(resource == 0 ? 8 : 0);
        this.backButtonImageView.setImageResource(resource);
    }

    private void createSubtitleTextView() {
        if (this.subtitleTextView == null) {
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.subtitleTextView = simpleTextView;
            simpleTextView.setGravity(3);
            this.subtitleTextView.setVisibility(8);
            this.subtitleTextView.setTextColor(getThemedColor("actionBarDefaultSubtitle"));
            addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void createAdditionalSubtitleTextView() {
        if (this.additionalSubtitleTextView == null) {
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.additionalSubtitleTextView = simpleTextView;
            simpleTextView.setGravity(3);
            this.additionalSubtitleTextView.setVisibility(8);
            this.additionalSubtitleTextView.setTextColor(getThemedColor("actionBarDefaultSubtitle"));
            addView(this.additionalSubtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public SimpleTextView getAdditionalSubtitleTextView() {
        return this.additionalSubtitleTextView;
    }

    public void setAddToContainer(boolean value) {
        this.addToContainer = value;
    }

    public boolean shouldAddToContainer() {
        return this.addToContainer;
    }

    public void setClipContent(boolean value) {
        this.clipContent = value;
    }

    public void setSubtitle(CharSequence value) {
        if (value != null && this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        if (this.subtitleTextView != null) {
            boolean isEmpty = TextUtils.isEmpty(value);
            this.subtitleTextView.setVisibility((isEmpty || this.isSearchFieldVisible) ? 8 : 0);
            this.subtitleTextView.setAlpha(1.0f);
            if (!isEmpty) {
                this.subtitleTextView.setText(value);
            }
            this.subtitle = value;
        }
    }

    private void createTitleTextView(int i) {
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[i] == null) {
            simpleTextViewArr[i] = new SimpleTextView(getContext());
            this.titleTextView[i].setGravity(3);
            int i2 = this.titleColorToSet;
            if (i2 != 0) {
                this.titleTextView[i].setTextColor(i2);
            } else {
                this.titleTextView[i].setTextColor(getThemedColor("actionBarDefaultTitle"));
            }
            this.titleTextView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.titleTextView[i], 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setTitleRightMargin(int value) {
        this.titleRightMargin = value;
    }

    public void setTitle(CharSequence value) {
        if (value != null && this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] != null) {
            this.lastTitle = value;
            simpleTextViewArr[0].setVisibility((value == null || this.isSearchFieldVisible) ? 4 : 0);
            this.titleTextView[0].setText(value);
        }
        this.fromBottom = false;
    }

    public void setTitleColor(int color) {
        if (this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        this.titleColorToSet = color;
        this.titleTextView[0].setTextColor(color);
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[1] != null) {
            simpleTextViewArr[1].setTextColor(color);
        }
    }

    public void setSubtitleColor(int color) {
        if (this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        this.subtitleTextView.setTextColor(color);
    }

    public void setTitleScrollNonFitText(boolean b) {
        this.titleTextView[0].setScrollNonFitText(b);
    }

    public void setPopupItemsColor(int color, boolean icon, boolean forActionMode) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (forActionMode && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsColor(color, icon);
        } else if (!forActionMode && (actionBarMenu = this.menu) != null) {
            actionBarMenu.setPopupItemsColor(color, icon);
        }
    }

    public void setPopupItemsSelectorColor(int color, boolean forActionMode) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (forActionMode && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsSelectorColor(color);
        } else if (!forActionMode && (actionBarMenu = this.menu) != null) {
            actionBarMenu.setPopupItemsSelectorColor(color);
        }
    }

    public void setPopupBackgroundColor(int color, boolean forActionMode) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (forActionMode && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.redrawPopup(color);
        } else if (!forActionMode && (actionBarMenu = this.menu) != null) {
            actionBarMenu.redrawPopup(color);
        }
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView[0];
    }

    public String getTitle() {
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] == null) {
            return null;
        }
        return simpleTextViewArr[0].getText().toString();
    }

    public String getSubtitle() {
        CharSequence charSequence;
        if (this.subtitleTextView == null || (charSequence = this.subtitle) == null) {
            return null;
        }
        return charSequence.toString();
    }

    public ActionBarMenu createMenu() {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            return actionBarMenu;
        }
        ActionBarMenu actionBarMenu2 = new ActionBarMenu(getContext(), this);
        this.menu = actionBarMenu2;
        addView(actionBarMenu2, 0, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick listener) {
        this.actionBarMenuOnItemClick = listener;
    }

    public ActionBarMenuOnItemClick getActionBarMenuOnItemClick() {
        return this.actionBarMenuOnItemClick;
    }

    public ImageView getBackButton() {
        return this.backButtonImageView;
    }

    public ActionBarMenu createActionMode() {
        return createActionMode(true, (String) null);
    }

    public boolean actionModeIsExist(String tag) {
        if (this.actionMode == null) {
            return false;
        }
        String str = this.actionModeTag;
        if (str == null && tag == null) {
            return true;
        }
        if (str == null || !str.equals(tag)) {
            return false;
        }
        return true;
    }

    public ActionBarMenu createActionMode(boolean needTop, String tag) {
        if (actionModeIsExist(tag)) {
            return this.actionMode;
        }
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            removeView(actionBarMenu);
            this.actionMode = null;
        }
        this.actionModeTag = tag;
        AnonymousClass1 r0 = new ActionBarMenu(getContext(), this) {
            public void setBackgroundColor(int color) {
                super.setBackgroundColor(ActionBar.this.actionModeColor = color);
            }
        };
        this.actionMode = r0;
        r0.isActionMode = true;
        this.actionMode.setClickable(true);
        this.actionMode.setBackgroundColor(getThemedColor("actionBarActionModeDefault"));
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.bottomMargin = this.extraHeight;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        if (this.occupyStatusBar && needTop && this.actionModeTop == null) {
            View view = new View(getContext());
            this.actionModeTop = view;
            view.setBackgroundColor(getThemedColor("actionBarActionModeDefaultTop"));
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
        showActionMode(true, (View) null, (View) null, (View[]) null, (boolean[]) null, (View) null, 0);
    }

    public void showActionMode(boolean animated) {
        showActionMode(animated, (View) null, (View) null, (View[]) null, (boolean[]) null, (View) null, 0);
    }

    public void showActionMode(boolean animated, View extraView, View showingView, View[] hidingViews, boolean[] hideView, View translationView, int translation) {
        View view = extraView;
        View view2 = showingView;
        View[] viewArr = hidingViews;
        final boolean[] zArr = hideView;
        View view3 = translationView;
        int i = translation;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null && !this.actionModeVisible) {
            this.actionModeVisible = true;
            if (animated) {
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this.actionMode, View.ALPHA, new float[]{0.0f, 1.0f}));
                if (viewArr != null) {
                    for (int a = 0; a < viewArr.length; a++) {
                        if (viewArr[a] != null) {
                            animators.add(ObjectAnimator.ofFloat(viewArr[a], View.ALPHA, new float[]{1.0f, 0.0f}));
                        }
                    }
                }
                if (view2 != null) {
                    animators.add(ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{0.0f, 1.0f}));
                }
                if (view3 != null) {
                    animators.add(ObjectAnimator.ofFloat(view3, View.TRANSLATION_Y, new float[]{(float) i}));
                    this.actionModeTranslationView = view3;
                }
                this.actionModeExtraView = view;
                this.actionModeShowingView = view2;
                this.actionModeHidingViews = viewArr;
                if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
                    animators.add(ObjectAnimator.ofFloat(this.actionModeTop, View.ALPHA, new float[]{0.0f, 1.0f}));
                }
                if (SharedConfig.noStatusBar) {
                    if (AndroidUtilities.computePerceivedBrightness(this.actionModeColor) < 0.721f) {
                        AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
                    } else {
                        AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
                    }
                }
                AnimatorSet animatorSet = this.actionModeAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionModeAnimation = animatorSet2;
                animatorSet2.playTogether(animators);
                this.actionModeAnimation.setDuration(200);
                this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        ActionBar.this.actionMode.setVisibility(0);
                        if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null && !SharedConfig.noStatusBar) {
                            ActionBar.this.actionModeTop.setVisibility(0);
                        }
                    }

                    public void onAnimationEnd(Animator animation) {
                        boolean[] zArr;
                        if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                            AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
                            if (ActionBar.this.titleTextView[0] != null) {
                                ActionBar.this.titleTextView[0].setVisibility(4);
                            }
                            if (ActionBar.this.subtitleTextView != null && !TextUtils.isEmpty(ActionBar.this.subtitle)) {
                                ActionBar.this.subtitleTextView.setVisibility(4);
                            }
                            if (ActionBar.this.menu != null) {
                                ActionBar.this.menu.setVisibility(4);
                            }
                            if (ActionBar.this.actionModeHidingViews != null) {
                                for (int a = 0; a < ActionBar.this.actionModeHidingViews.length; a++) {
                                    if (ActionBar.this.actionModeHidingViews[a] != null && ((zArr = zArr) == null || a >= zArr.length || zArr[a])) {
                                        ActionBar.this.actionModeHidingViews[a].setVisibility(4);
                                    }
                                }
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
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
                    return;
                }
                return;
            }
            actionBarMenu.setAlpha(1.0f);
            if (viewArr != null) {
                for (int a2 = 0; a2 < viewArr.length; a2++) {
                    if (viewArr[a2] != null) {
                        viewArr[a2].setAlpha(0.0f);
                    }
                }
            }
            if (view2 != null) {
                view2.setAlpha(1.0f);
            }
            if (view3 != null) {
                view3.setTranslationY((float) i);
                this.actionModeTranslationView = view3;
            }
            this.actionModeExtraView = view;
            this.actionModeShowingView = view2;
            this.actionModeHidingViews = viewArr;
            if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
                this.actionModeTop.setAlpha(1.0f);
            }
            if (SharedConfig.noStatusBar) {
                if (AndroidUtilities.computePerceivedBrightness(this.actionModeColor) < 0.721f) {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
                } else {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
                }
            }
            this.actionMode.setVisibility(0);
            if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
                this.actionModeTop.setVisibility(0);
            }
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (simpleTextViewArr[0] != null) {
                simpleTextViewArr[0].setVisibility(4);
            }
            if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
                this.subtitleTextView.setVisibility(4);
            }
            ActionBarMenu actionBarMenu2 = this.menu;
            if (actionBarMenu2 != null) {
                actionBarMenu2.setVisibility(4);
            }
            if (this.actionModeHidingViews != null) {
                int a3 = 0;
                while (true) {
                    View[] viewArr2 = this.actionModeHidingViews;
                    if (a3 >= viewArr2.length) {
                        break;
                    }
                    if (viewArr2[a3] != null && (zArr == null || a3 >= zArr.length || zArr[a3])) {
                        viewArr2[a3].setVisibility(4);
                    }
                    a3++;
                }
            }
            ImageView imageView2 = this.backButtonImageView;
            if (imageView2 != null) {
                Drawable drawable2 = imageView2.getDrawable();
                if (drawable2 instanceof BackDrawable) {
                    ((BackDrawable) drawable2).setRotation(1.0f, false);
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
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(this.actionMode, View.ALPHA, new float[]{0.0f}));
            if (this.actionModeHidingViews != null) {
                int a = 0;
                while (true) {
                    View[] viewArr = this.actionModeHidingViews;
                    if (a >= viewArr.length) {
                        break;
                    }
                    if (viewArr[a] != null) {
                        viewArr[a].setVisibility(0);
                        animators.add(ObjectAnimator.ofFloat(this.actionModeHidingViews[a], View.ALPHA, new float[]{1.0f}));
                    }
                    a++;
                }
            }
            View view = this.actionModeTranslationView;
            if (view != null) {
                animators.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f}));
                this.actionModeTranslationView = null;
            }
            View view2 = this.actionModeShowingView;
            if (view2 != null) {
                animators.add(ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{0.0f}));
            }
            if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
                animators.add(ObjectAnimator.ofFloat(this.actionModeTop, View.ALPHA, new float[]{0.0f}));
            }
            if (SharedConfig.noStatusBar) {
                if (AndroidUtilities.computePerceivedBrightness(this.actionBarColor) < 0.721f) {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
                } else {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
                }
            }
            AnimatorSet animatorSet = this.actionModeAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionModeAnimation = animatorSet2;
            animatorSet2.playTogether(animators);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                        AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
                        ActionBar.this.actionMode.setVisibility(4);
                        if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null && !SharedConfig.noStatusBar) {
                            ActionBar.this.actionModeTop.setVisibility(4);
                        }
                        if (ActionBar.this.actionModeExtraView != null) {
                            ActionBar.this.actionModeExtraView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                        AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
                    }
                }
            });
            this.actionModeAnimation.start();
            if (!this.isSearchFieldVisible) {
                SimpleTextView[] simpleTextViewArr = this.titleTextView;
                if (simpleTextViewArr[0] != null) {
                    simpleTextViewArr[0].setVisibility(0);
                }
                if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
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
            View view = new View(getContext());
            this.actionModeTop = view;
            view.setBackgroundColor(getThemedColor("actionBarActionModeDefaultTop"));
            addView(this.actionModeTop);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
        }
    }

    public void setActionModeTopColor(int color) {
        View view = this.actionModeTop;
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    public void setSearchTextColor(int color, boolean placeholder) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setSearchTextColor(color, placeholder);
        }
    }

    public void setActionModeColor(int color) {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setBackgroundColor(color);
        }
    }

    public void setActionModeOverrideColor(int color) {
        this.actionModeColor = color;
    }

    public void setBackgroundColor(int color) {
        this.actionBarColor = color;
        super.setBackgroundColor(color);
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof MenuDrawable) {
                ((MenuDrawable) drawable).setBackColor(color);
            }
        }
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r1.actionModeTag;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isActionModeShowed(java.lang.String r2) {
        /*
            r1 = this;
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r1.actionMode
            if (r0 == 0) goto L_0x0018
            boolean r0 = r1.actionModeVisible
            if (r0 == 0) goto L_0x0018
            java.lang.String r0 = r1.actionModeTag
            if (r0 != 0) goto L_0x000e
            if (r2 == 0) goto L_0x0016
        L_0x000e:
            if (r0 == 0) goto L_0x0018
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0018
        L_0x0016:
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBar.isActionModeShowed(java.lang.String):boolean");
    }

    public void onSearchFieldVisibilityChanged(final boolean visible) {
        float f;
        this.isSearchFieldVisible = visible;
        AnimatorSet animatorSet = this.searchVisibleAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.searchVisibleAnimator = new AnimatorSet();
        final ArrayList<View> viewsToHide = new ArrayList<>();
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] != null) {
            viewsToHide.add(simpleTextViewArr[0]);
        }
        if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
            viewsToHide.add(this.subtitleTextView);
            this.subtitleTextView.setVisibility(visible ? 4 : 0);
        }
        int i = 0;
        while (true) {
            f = 0.0f;
            float f2 = 1.0f;
            if (i >= viewsToHide.size()) {
                break;
            }
            View view = viewsToHide.get(i);
            if (!visible) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.setScaleX(0.95f);
                view.setScaleY(0.95f);
            }
            AnimatorSet animatorSet2 = this.searchVisibleAnimator;
            Animator[] animatorArr = new Animator[1];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!visible) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            AnimatorSet animatorSet3 = this.searchVisibleAnimator;
            Animator[] animatorArr2 = new Animator[1];
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = visible ? 0.95f : 1.0f;
            animatorArr2[0] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr2);
            AnimatorSet animatorSet4 = this.searchVisibleAnimator;
            Animator[] animatorArr3 = new Animator[1];
            Property property3 = View.SCALE_X;
            float[] fArr3 = new float[1];
            if (visible) {
                f2 = 0.95f;
            }
            fArr3[0] = f2;
            animatorArr3[0] = ObjectAnimator.ofFloat(view, property3, fArr3);
            animatorSet4.playTogether(animatorArr3);
            i++;
        }
        this.centerScale = true;
        requestLayout();
        this.searchVisibleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < viewsToHide.size(); i++) {
                    View view = (View) viewsToHide.get(i);
                    if (visible) {
                        view.setVisibility(4);
                        view.setAlpha(0.0f);
                    } else {
                        view.setAlpha(1.0f);
                    }
                }
                if (visible != 0) {
                    if (ActionBar.this.titleTextView[0] != null) {
                        ActionBar.this.titleTextView[0].setVisibility(8);
                    }
                    if (ActionBar.this.titleTextView[1] != null) {
                        ActionBar.this.titleTextView[1].setVisibility(8);
                    }
                }
            }
        });
        this.searchVisibleAnimator.setDuration(150).start();
        Drawable drawable = this.backButtonImageView.getDrawable();
        if (drawable instanceof MenuDrawable) {
            MenuDrawable menuDrawable = (MenuDrawable) drawable;
            menuDrawable.setRotateToBack(true);
            if (visible) {
                f = 1.0f;
            }
            menuDrawable.setRotation(f, true);
        }
    }

    public void setInterceptTouches(boolean value) {
        this.interceptTouches = value;
    }

    public void setInterceptTouchEventListener(View.OnTouchListener listener) {
        this.interceptTouchEventListener = listener;
    }

    public void setExtraHeight(int value) {
        this.extraHeight = value;
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

    public void closeSearchField(boolean closeKeyboard) {
        ActionBarMenu actionBarMenu;
        if (this.isSearchFieldVisible && (actionBarMenu = this.menu) != null) {
            actionBarMenu.closeSearchField(closeKeyboard);
        }
    }

    public void openSearchField(String text, boolean animated) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null && text != null) {
            actionBarMenu.openSearchField(!this.isSearchFieldVisible, text, animated);
        }
    }

    public void setSearchFilter(FiltersView.MediaFilterData filter) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setFilter(filter);
        }
    }

    public void setSearchFieldText(String text) {
        this.menu.setSearchFieldText(text);
    }

    public void onSearchPressed() {
        this.menu.onSearchPressed();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setEnabled(enabled);
        }
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setEnabled(enabled);
        }
        ActionBarMenu actionBarMenu2 = this.actionMode;
        if (actionBarMenu2 != null) {
            actionBarMenu2.setEnabled(enabled);
        }
    }

    public void requestLayout() {
        if (!this.ignoreLayoutRequest) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textLeft;
        int i;
        SimpleTextView simpleTextView;
        SimpleTextView simpleTextView2;
        int menuWidth;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        int actionBarHeight = getCurrentActionBarHeight();
        int actionBarHeightSpec = View.MeasureSpec.makeMeasureSpec(actionBarHeight, NUM);
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
        setMeasuredDimension(width, (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + actionBarHeight + this.extraHeight);
        ImageView imageView = this.backButtonImageView;
        if (imageView == null || imageView.getVisibility() == 8) {
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() != 0 ? 26.0f : 18.0f);
        } else {
            this.backButtonImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), NUM), actionBarHeightSpec);
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        int i2 = Integer.MIN_VALUE;
        if (!(actionBarMenu2 == null || actionBarMenu2.getVisibility() == 8)) {
            float f = 74.0f;
            if (this.menu.searchFieldVisible() && !this.isSearchFieldVisible) {
                this.menu.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), actionBarHeightSpec);
                int itemsWidth = this.menu.getItemsMeasuredWidth();
                if (!AndroidUtilities.isTablet()) {
                    f = 66.0f;
                }
                menuWidth = View.MeasureSpec.makeMeasureSpec((width - AndroidUtilities.dp(f)) + this.menu.getItemsMeasuredWidth(), NUM);
                this.menu.translateXItems(-itemsWidth);
            } else if (this.isSearchFieldVisible) {
                if (!AndroidUtilities.isTablet()) {
                    f = 66.0f;
                }
                menuWidth = View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(f), NUM);
                this.menu.translateXItems(0);
            } else {
                menuWidth = View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE);
                this.menu.translateXItems(0);
            }
            this.menu.measure(menuWidth, actionBarHeightSpec);
        }
        int i3 = 0;
        while (i3 < 2) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if ((simpleTextViewArr[0] != null && simpleTextViewArr[0].getVisibility() != 8) || ((simpleTextView2 = this.subtitleTextView) != null && simpleTextView2.getVisibility() != 8)) {
                ActionBarMenu actionBarMenu3 = this.menu;
                int availableWidth = (((width - (actionBarMenu3 != null ? actionBarMenu3.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - textLeft) - this.titleRightMargin;
                boolean z = this.fromBottom;
                if (((!z || i3 != 0) && (z || i3 != 1)) || !this.overlayTitleAnimation || !this.titleAnimationRunning) {
                    SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
                    if (simpleTextViewArr2[0] == null || simpleTextViewArr2[0].getVisibility() == 8 || (simpleTextView = this.subtitleTextView) == null || simpleTextView.getVisibility() == 8) {
                        SimpleTextView[] simpleTextViewArr3 = this.titleTextView;
                        if (!(simpleTextViewArr3[i3] == null || simpleTextViewArr3[i3].getVisibility() == 8)) {
                            this.titleTextView[i3].setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18);
                        }
                        SimpleTextView simpleTextView3 = this.subtitleTextView;
                        if (!(simpleTextView3 == null || simpleTextView3.getVisibility() == 8)) {
                            this.subtitleTextView.setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14);
                        }
                        SimpleTextView simpleTextView4 = this.additionalSubtitleTextView;
                        if (simpleTextView4 != null) {
                            simpleTextView4.setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14);
                        }
                    } else {
                        SimpleTextView[] simpleTextViewArr4 = this.titleTextView;
                        if (simpleTextViewArr4[i3] != null) {
                            simpleTextViewArr4[i3].setTextSize(AndroidUtilities.isTablet() ? 20 : 18);
                        }
                        this.subtitleTextView.setTextSize(AndroidUtilities.isTablet() ? 16 : 14);
                        SimpleTextView simpleTextView5 = this.additionalSubtitleTextView;
                        if (simpleTextView5 != null) {
                            simpleTextView5.setTextSize(AndroidUtilities.isTablet() ? 16 : 14);
                        }
                    }
                } else {
                    this.titleTextView[i3].setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18);
                }
                SimpleTextView[] simpleTextViewArr5 = this.titleTextView;
                if (!(simpleTextViewArr5[i3] == null || simpleTextViewArr5[i3].getVisibility() == 8)) {
                    this.titleTextView[i3].measure(View.MeasureSpec.makeMeasureSpec(availableWidth, i2), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), i2));
                    if (this.centerScale) {
                        CharSequence text = this.titleTextView[i3].getText();
                        SimpleTextView[] simpleTextViewArr6 = this.titleTextView;
                        simpleTextViewArr6[i3].setPivotX(simpleTextViewArr6[i3].getTextPaint().measureText(text, 0, text.length()) / 2.0f);
                        this.titleTextView[i3].setPivotY((float) (AndroidUtilities.dp(24.0f) >> 1));
                    } else {
                        this.titleTextView[i3].setPivotX(0.0f);
                        this.titleTextView[i3].setPivotY(0.0f);
                    }
                }
                SimpleTextView simpleTextView6 = this.subtitleTextView;
                if (!(simpleTextView6 == null || simpleTextView6.getVisibility() == 8)) {
                    this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
                SimpleTextView simpleTextView7 = this.additionalSubtitleTextView;
                if (simpleTextView7 != null && simpleTextView7.getVisibility() != 8) {
                    this.additionalSubtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
            }
            i3++;
            i2 = Integer.MIN_VALUE;
        }
        int childCount = getChildCount();
        int i4 = 0;
        while (i4 < childCount) {
            View child = getChildAt(i4);
            if (child.getVisibility() != 8) {
                SimpleTextView[] simpleTextViewArr7 = this.titleTextView;
                if (!(child == simpleTextViewArr7[0] || child == simpleTextViewArr7[1] || child == this.subtitleTextView || child == this.menu || child == this.backButtonImageView)) {
                    if (child == this.additionalSubtitleTextView) {
                        i = i4;
                    } else {
                        View view2 = child;
                        i = i4;
                        measureChildWithMargins(child, widthMeasureSpec, 0, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM), 0);
                    }
                    i4 = i + 1;
                }
            }
            i = i4;
            i4 = i + 1;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int textLeft;
        char c;
        int i;
        int childLeft;
        int childTop;
        int textTop;
        int menuLeft;
        char c2 = 0;
        int additionalTop = this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0;
        ImageView imageView = this.backButtonImageView;
        int i2 = 8;
        if (imageView == null || imageView.getVisibility() == 8) {
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() != 0 ? 26.0f : 18.0f);
        } else {
            ImageView imageView2 = this.backButtonImageView;
            imageView2.layout(0, additionalTop, imageView2.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + additionalTop);
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        ActionBarMenu actionBarMenu = this.menu;
        if (!(actionBarMenu == null || actionBarMenu.getVisibility() == 8)) {
            if (this.menu.searchFieldVisible()) {
                menuLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f);
            } else {
                menuLeft = (right - left) - this.menu.getMeasuredWidth();
            }
            ActionBarMenu actionBarMenu2 = this.menu;
            actionBarMenu2.layout(menuLeft, additionalTop, actionBarMenu2.getMeasuredWidth() + menuLeft, this.menu.getMeasuredHeight() + additionalTop);
        }
        int i3 = 0;
        while (true) {
            c = 1;
            i = 2;
            if (i3 >= 2) {
                break;
            }
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (!(simpleTextViewArr[i3] == null || simpleTextViewArr[i3].getVisibility() == 8)) {
                boolean z = this.fromBottom;
                if (((!z || i3 != 0) && (z || i3 != 1)) || !this.overlayTitleAnimation || !this.titleAnimationRunning) {
                    SimpleTextView simpleTextView = this.subtitleTextView;
                    if (simpleTextView == null || simpleTextView.getVisibility() == 8) {
                        textTop = (getCurrentActionBarHeight() - this.titleTextView[i3].getTextHeight()) / 2;
                    } else {
                        textTop = (((getCurrentActionBarHeight() / 2) - this.titleTextView[i3].getTextHeight()) / 2) + AndroidUtilities.dp((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 3.0f : 2.0f);
                    }
                } else {
                    textTop = (getCurrentActionBarHeight() - this.titleTextView[i3].getTextHeight()) / 2;
                }
                SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
                simpleTextViewArr2[i3].layout(textLeft, additionalTop + textTop, simpleTextViewArr2[i3].getMeasuredWidth() + textLeft, additionalTop + textTop + this.titleTextView[i3].getTextHeight());
            }
            i3++;
        }
        SimpleTextView simpleTextView2 = this.subtitleTextView;
        if (!(simpleTextView2 == null || simpleTextView2.getVisibility() == 8)) {
            int currentActionBarHeight = (getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2);
            if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation == 2) {
            }
            int textTop2 = currentActionBarHeight - AndroidUtilities.dp(1.0f);
            SimpleTextView simpleTextView3 = this.subtitleTextView;
            simpleTextView3.layout(textLeft, additionalTop + textTop2, simpleTextView3.getMeasuredWidth() + textLeft, additionalTop + textTop2 + this.subtitleTextView.getTextHeight());
        }
        SimpleTextView simpleTextView4 = this.additionalSubtitleTextView;
        if (!(simpleTextView4 == null || simpleTextView4.getVisibility() == 8)) {
            int currentActionBarHeight2 = (getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.additionalSubtitleTextView.getTextHeight()) / 2);
            if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation == 2) {
            }
            int textTop3 = currentActionBarHeight2 - AndroidUtilities.dp(1.0f);
            SimpleTextView simpleTextView5 = this.additionalSubtitleTextView;
            simpleTextView5.layout(textLeft, additionalTop + textTop3, simpleTextView5.getMeasuredWidth() + textLeft, additionalTop + textTop3 + this.additionalSubtitleTextView.getTextHeight());
        }
        int childCount = getChildCount();
        int i4 = 0;
        while (i4 < childCount) {
            View child = getChildAt(i4);
            if (child.getVisibility() != i2) {
                SimpleTextView[] simpleTextViewArr3 = this.titleTextView;
                if (!(child == simpleTextViewArr3[c2] || child == simpleTextViewArr3[c] || child == this.subtitleTextView || child == this.menu || child == this.backButtonImageView || child == this.additionalSubtitleTextView)) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    switch (gravity & 7 & 7) {
                        case 1:
                            childLeft = ((((right - left) - width) / i) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case 5:
                            childLeft = (right - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
                            break;
                    }
                    switch (verticalGravity) {
                        case 16:
                            childTop = ((((bottom - top) - height) / i) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case 80:
                            childTop = ((bottom - top) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
            i4++;
            c2 = 0;
            i2 = 8;
            c = 1;
            i = 2;
        }
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

    public void setAllowOverlayTitle(boolean value) {
        this.allowOverlayTitle = value;
    }

    public void setTitleActionRunnable(Runnable action) {
        this.titleActionRunnable = action;
        this.lastRunnable = action;
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x0154  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0156  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTitleOverlayText(java.lang.String r11, int r12, java.lang.Runnable r13) {
        /*
            r10 = this;
            boolean r0 = r10.allowOverlayTitle
            if (r0 == 0) goto L_0x015b
            org.telegram.ui.ActionBar.BaseFragment r0 = r10.parentFragment
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r0.parentLayout
            if (r0 != 0) goto L_0x000c
            goto L_0x015b
        L_0x000c:
            java.lang.Object[] r0 = r10.overlayTitleToSet
            r1 = 0
            r0[r1] = r11
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)
            r3 = 1
            r0[r3] = r2
            java.lang.Object[] r0 = r10.overlayTitleToSet
            r2 = 2
            r0[r2] = r13
            boolean r0 = r10.overlayTitleAnimationInProgress
            if (r0 == 0) goto L_0x0022
            return
        L_0x0022:
            java.lang.CharSequence r0 = r10.lastOverlayTitle
            if (r0 != 0) goto L_0x0028
            if (r11 == 0) goto L_0x0030
        L_0x0028:
            if (r0 == 0) goto L_0x0031
            boolean r0 = r0.equals(r11)
            if (r0 == 0) goto L_0x0031
        L_0x0030:
            return
        L_0x0031:
            r10.lastOverlayTitle = r11
            if (r11 == 0) goto L_0x003a
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r12)
            goto L_0x003c
        L_0x003a:
            java.lang.CharSequence r0 = r10.lastTitle
        L_0x003c:
            r2 = 0
            if (r11 == 0) goto L_0x0052
            java.lang.String r4 = "..."
            int r4 = android.text.TextUtils.indexOf(r0, r4)
            if (r4 < 0) goto L_0x0052
            android.text.SpannableString r5 = android.text.SpannableString.valueOf(r0)
            org.telegram.ui.Components.EllipsizeSpanAnimator r6 = r10.ellipsizeSpanAnimator
            r6.wrap(r5, r4)
            r0 = r5
            r2 = 1
        L_0x0052:
            if (r11 == 0) goto L_0x0056
            r4 = 1
            goto L_0x0057
        L_0x0056:
            r4 = 0
        L_0x0057:
            r10.titleOverlayShown = r4
            if (r0 == 0) goto L_0x0061
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r4 = r4[r1]
            if (r4 == 0) goto L_0x0125
        L_0x0061:
            int r4 = r10.getMeasuredWidth()
            if (r4 == 0) goto L_0x0125
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r5 = r4[r1]
            if (r5 == 0) goto L_0x0077
            r4 = r4[r1]
            int r4 = r4.getVisibility()
            if (r4 == 0) goto L_0x0077
            goto L_0x0125
        L_0x0077:
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r5 = r4[r1]
            if (r5 == 0) goto L_0x0152
            r4 = r4[r1]
            android.view.ViewPropertyAnimator r4 = r4.animate()
            r4.cancel()
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r5 = r4[r3]
            if (r5 == 0) goto L_0x0095
            r4 = r4[r3]
            android.view.ViewPropertyAnimator r4 = r4.animate()
            r4.cancel()
        L_0x0095:
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r4 = r4[r3]
            if (r4 != 0) goto L_0x009e
            r10.createTitleTextView(r3)
        L_0x009e:
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r4 = r4[r3]
            r4.setText(r0)
            if (r2 == 0) goto L_0x00b0
            org.telegram.ui.Components.EllipsizeSpanAnimator r4 = r10.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r10.titleTextView
            r5 = r5[r3]
            r4.addView(r5)
        L_0x00b0:
            r10.overlayTitleAnimationInProgress = r3
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r5 = r4[r3]
            r6 = r4[r1]
            r4[r3] = r6
            r4[r1] = r5
            r4 = r4[r1]
            r6 = 0
            r4.setAlpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r4 = r4[r1]
            r7 = 1101004800(0x41a00000, float:20.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r8 = -r8
            float r8 = (float) r8
            r4.setTranslationY(r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r1 = r4[r1]
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r4 = 1065353216(0x3var_, float:1.0)
            android.view.ViewPropertyAnimator r1 = r1.alpha(r4)
            android.view.ViewPropertyAnimator r1 = r1.translationY(r6)
            r8 = 220(0xdc, double:1.087E-321)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r8)
            r1.start()
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r10.titleTextView
            r1 = r1[r3]
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.subtitleTextView
            if (r4 != 0) goto L_0x0105
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r4 = (float) r4
            r1.translationY(r4)
            goto L_0x010f
        L_0x0105:
            r4 = 1060320051(0x3var_, float:0.7)
            android.view.ViewPropertyAnimator r6 = r1.scaleY(r4)
            r6.scaleX(r4)
        L_0x010f:
            r10.requestLayout()
            r10.centerScale = r3
            android.view.ViewPropertyAnimator r3 = r1.setDuration(r8)
            org.telegram.ui.ActionBar.ActionBar$5 r4 = new org.telegram.ui.ActionBar.ActionBar$5
            r4.<init>()
            android.view.ViewPropertyAnimator r3 = r3.setListener(r4)
            r3.start()
            goto L_0x0152
        L_0x0125:
            r10.createTitleTextView(r1)
            boolean r3 = r10.supportsHolidayImage
            if (r3 == 0) goto L_0x0136
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.titleTextView
            r3 = r3[r1]
            r3.invalidate()
            r10.invalidate()
        L_0x0136:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.titleTextView
            r3 = r3[r1]
            r3.setText(r0)
            if (r2 == 0) goto L_0x0149
            org.telegram.ui.Components.EllipsizeSpanAnimator r3 = r10.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r1 = r4[r1]
            r3.addView(r1)
            goto L_0x0152
        L_0x0149:
            org.telegram.ui.Components.EllipsizeSpanAnimator r3 = r10.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r10.titleTextView
            r1 = r4[r1]
            r3.removeView(r1)
        L_0x0152:
            if (r13 == 0) goto L_0x0156
            r1 = r13
            goto L_0x0158
        L_0x0156:
            java.lang.Runnable r1 = r10.lastRunnable
        L_0x0158:
            r10.titleActionRunnable = r1
            return
        L_0x015b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBar.setTitleOverlayText(java.lang.String, int, java.lang.Runnable):void");
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, value ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public void setItemsBackgroundColor(int color, boolean isActionMode) {
        ImageView imageView;
        if (isActionMode) {
            this.itemsActionModeBackgroundColor = color;
            if (this.actionModeVisible && (imageView = this.backButtonImageView) != null) {
                imageView.setBackgroundDrawable(Theme.createSelectorDrawable(color));
            }
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsBackgroundColor();
                return;
            }
            return;
        }
        this.itemsBackgroundColor = color;
        ImageView imageView2 = this.backButtonImageView;
        if (imageView2 != null) {
            imageView2.setBackgroundDrawable(Theme.createSelectorDrawable(color));
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int color, boolean isActionMode) {
        if (isActionMode) {
            this.itemsActionModeColor = color;
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsColor();
            }
            ImageView imageView = this.backButtonImageView;
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotatedColor(color);
                    return;
                }
                return;
            }
            return;
        }
        this.itemsColor = color;
        ImageView imageView2 = this.backButtonImageView;
        if (!(imageView2 == null || color == 0)) {
            imageView2.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.MULTIPLY));
            Drawable drawable2 = this.backButtonImageView.getDrawable();
            if (drawable2 instanceof BackDrawable) {
                ((BackDrawable) drawable2).setColor(color);
            } else if (drawable2 instanceof MenuDrawable) {
                ((MenuDrawable) drawable2).setIconColor(color);
            }
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsColor();
        }
    }

    public void setCastShadows(boolean value) {
        this.castShadows = value;
    }

    public boolean getCastShadows() {
        return this.castShadows;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || this.interceptTouches;
    }

    public static int getCurrentActionBarHeight() {
        if (AndroidUtilities.isTablet()) {
            return AndroidUtilities.dp(64.0f);
        }
        if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            return AndroidUtilities.dp(48.0f);
        }
        return AndroidUtilities.dp(56.0f);
    }

    public void setTitleAnimated(CharSequence title, final boolean fromBottom2, long duration) {
        if (this.titleTextView[0] == null || title == null) {
            setTitle(title);
            return;
        }
        final boolean crossfade = this.overlayTitleAnimation && !TextUtils.isEmpty(this.subtitle);
        if (crossfade) {
            if (this.subtitleTextView.getVisibility() != 0) {
                this.subtitleTextView.setVisibility(0);
                this.subtitleTextView.setAlpha(0.0f);
            }
            this.subtitleTextView.animate().alpha(fromBottom2 ? 0.0f : 1.0f).setDuration(220).start();
        }
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[1] != null) {
            if (simpleTextViewArr[1].getParent() != null) {
                ((ViewGroup) this.titleTextView[1].getParent()).removeView(this.titleTextView[1]);
            }
            this.titleTextView[1] = null;
        }
        SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
        simpleTextViewArr2[1] = simpleTextViewArr2[0];
        simpleTextViewArr2[0] = null;
        setTitle(title);
        this.fromBottom = fromBottom2;
        this.titleTextView[0].setAlpha(0.0f);
        if (!crossfade) {
            SimpleTextView simpleTextView = this.titleTextView[0];
            int dp = AndroidUtilities.dp(20.0f);
            if (!fromBottom2) {
                dp = -dp;
            }
            simpleTextView.setTranslationY((float) dp);
        }
        this.titleTextView[0].animate().alpha(1.0f).translationY(0.0f).setDuration(duration).start();
        this.titleAnimationRunning = true;
        ViewPropertyAnimator a = this.titleTextView[1].animate().alpha(0.0f);
        if (!crossfade) {
            int dp2 = AndroidUtilities.dp(20.0f);
            if (fromBottom2) {
                dp2 = -dp2;
            }
            a.translationY((float) dp2);
        }
        a.setDuration(duration).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!(ActionBar.this.titleTextView[1] == null || ActionBar.this.titleTextView[1].getParent() == null)) {
                    ((ViewGroup) ActionBar.this.titleTextView[1].getParent()).removeView(ActionBar.this.titleTextView[1]);
                }
                ActionBar.this.titleTextView[1] = null;
                boolean unused = ActionBar.this.titleAnimationRunning = false;
                if (crossfade && fromBottom2) {
                    ActionBar.this.subtitleTextView.setVisibility(8);
                }
                ActionBar.this.requestLayout();
            }
        }).start();
        requestLayout();
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.ellipsizeSpanAnimator.onAttachedToWindow();
        if (SharedConfig.noStatusBar && this.actionModeVisible) {
            if (AndroidUtilities.computePerceivedBrightness(this.actionModeColor) < 0.721f) {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
            } else {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.ellipsizeSpanAnimator.onDetachedFromWindow();
        if (SharedConfig.noStatusBar && this.actionModeVisible) {
            if (AndroidUtilities.computePerceivedBrightness(this.actionBarColor) < 0.721f) {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
            } else {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
            }
        }
    }

    public ActionBarMenu getActionMode() {
        return this.actionMode;
    }

    public void setOverlayTitleAnimation(boolean ovelayTitleAnimation) {
        this.overlayTitleAnimation = ovelayTitleAnimation;
    }

    public void beginDelayedTransition() {
        if (Build.VERSION.SDK_INT >= 19) {
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.setOrdering(0);
            transitionSet.addTransition(new Fade());
            transitionSet.addTransition(new ChangeBounds() {
                public void captureStartValues(TransitionValues transitionValues) {
                    super.captureStartValues(transitionValues);
                    if (transitionValues.view instanceof SimpleTextView) {
                        transitionValues.values.put("text_size", Float.valueOf(((SimpleTextView) transitionValues.view).getTextPaint().getTextSize()));
                    }
                }

                public void captureEndValues(TransitionValues transitionValues) {
                    super.captureEndValues(transitionValues);
                    if (transitionValues.view instanceof SimpleTextView) {
                        transitionValues.values.put("text_size", Float.valueOf(((SimpleTextView) transitionValues.view).getTextPaint().getTextSize()));
                    }
                }

                public Animator createAnimator(ViewGroup sceneRoot, final TransitionValues startValues, TransitionValues endValues) {
                    if (startValues == null || !(startValues.view instanceof SimpleTextView)) {
                        return super.createAnimator(sceneRoot, startValues, endValues);
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    if (!(startValues == null || endValues == null)) {
                        Animator animator = super.createAnimator(sceneRoot, startValues, endValues);
                        float s = ((Float) startValues.values.get("text_size")).floatValue() / ((Float) endValues.values.get("text_size")).floatValue();
                        startValues.view.setScaleX(s);
                        startValues.view.setScaleY(s);
                        if (animator != null) {
                            animatorSet.playTogether(new Animator[]{animator});
                        }
                    }
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(startValues.view, View.SCALE_X, new float[]{1.0f})});
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(startValues.view, View.SCALE_Y, new float[]{1.0f})});
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            startValues.view.setLayerType(2, (Paint) null);
                        }

                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            startValues.view.setLayerType(0, (Paint) null);
                        }
                    });
                    return animatorSet;
                }
            });
            this.centerScale = false;
            transitionSet.setDuration(220);
            transitionSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            TransitionManager.beginDelayedTransition(this, transitionSet);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer num = null;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        if (color == null) {
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment != null) {
                num = Integer.valueOf(baseFragment.getThemedColor(key));
            }
            color = num;
        }
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
