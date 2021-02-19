package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.Adapters.FiltersView;
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
    private boolean allowOverlayTitle;
    private ImageView backButtonImageView;
    private boolean castShadows;
    private boolean clipContent;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    private int extraHeight;
    private FireworksEffect fireworksEffect;
    private Paint.FontMetricsInt fontMetricsInt;
    private boolean fromBottom;
    private boolean ignoreLayoutRequest;
    private boolean interceptTouches;
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
    private SnowflakesEffect snowflakesEffect;
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
    public SimpleTextView[] titleTextView = new SimpleTextView[2];

    public static class ActionBarMenuOnItemClick {
        public boolean canOpenMenu() {
            return true;
        }

        public void onItemClick(int i) {
            throw null;
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
        this.overlayTitleToSet = new Object[3];
        this.castShadows = true;
        this.titleColorToSet = 0;
        this.ellipsizeSpanAnimator = new EllipsizeSpanAnimator(this);
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBar.this.lambda$new$0$ActionBar(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ActionBar(View view) {
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
            this.backButtonImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ActionBar.this.lambda$createBackButtonImage$1$ActionBar(view);
                }
            });
            this.backButtonImageView.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createBackButtonImage$1 */
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
        if (z) {
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
                this.titleTextView[0].invalidate();
                invalidate();
            } else {
                this.snowflakesEffect = null;
                this.fireworksEffect = new FireworksEffect();
                this.titleTextView[0].invalidate();
                invalidate();
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public boolean shouldClipChild(View view) {
        if (this.clipContent) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (view == simpleTextViewArr[0] || view == simpleTextViewArr[1] || view == this.subtitleTextView || view == this.menu || view == this.backButtonImageView) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        Drawable currentHolidayDrawable;
        boolean shouldClipChild = shouldClipChild(view);
        if (shouldClipChild) {
            canvas.save();
            canvas.clipRect(0.0f, (-getTranslationY()) + ((float) (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0)), (float) getMeasuredWidth(), (float) getMeasuredHeight());
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if ((view == simpleTextViewArr[0] || view == simpleTextViewArr[1]) && (currentHolidayDrawable = Theme.getCurrentHolidayDrawable()) != null) {
                SimpleTextView simpleTextView = (SimpleTextView) view;
                if (simpleTextView.getVisibility() == 0 && (simpleTextView.getText() instanceof String)) {
                    TextPaint textPaint = simpleTextView.getTextPaint();
                    textPaint.getFontMetricsInt(this.fontMetricsInt);
                    textPaint.getTextBounds((String) simpleTextView.getText(), 0, 1, this.rect);
                    int textStartX = simpleTextView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset() + ((this.rect.width() - (currentHolidayDrawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
                    int textStartY = simpleTextView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset() + ((int) Math.ceil((double) (((float) (simpleTextView.getTextHeight() - this.rect.height())) / 2.0f)));
                    currentHolidayDrawable.setBounds(textStartX, textStartY - currentHolidayDrawable.getIntrinsicHeight(), currentHolidayDrawable.getIntrinsicWidth() + textStartX, textStartY);
                    currentHolidayDrawable.setAlpha((int) (simpleTextView.getAlpha() * 255.0f));
                    currentHolidayDrawable.draw(canvas);
                    if (this.overlayTitleAnimationInProgress) {
                        view.invalidate();
                        invalidate();
                    }
                }
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
        }
        if (shouldClipChild) {
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
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.subtitleTextView = simpleTextView;
            simpleTextView.setGravity(3);
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
            this.subtitleTextView.setAlpha(1.0f);
            this.subtitleTextView.setText(charSequence);
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
                this.titleTextView[i].setTextColor(Theme.getColor("actionBarDefaultTitle"));
            }
            this.titleTextView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.titleTextView[i], 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setTitleRightMargin(int i) {
        this.titleRightMargin = i;
    }

    public void setTitle(CharSequence charSequence) {
        if (charSequence != null && this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] != null) {
            this.lastTitle = charSequence;
            simpleTextViewArr[0].setVisibility((charSequence == null || this.isSearchFieldVisible) ? 4 : 0);
            this.titleTextView[0].setText(charSequence);
        }
        this.fromBottom = false;
    }

    public void setTitleColor(int i) {
        if (this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        this.titleColorToSet = i;
        this.titleTextView[0].setTextColor(i);
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[1] != null) {
            simpleTextViewArr[1].setTextColor(i);
        }
    }

    public void setSubtitleColor(int i) {
        if (this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        this.subtitleTextView.setTextColor(i);
    }

    public void setTitleScrollNonFitText(boolean z) {
        this.titleTextView[0].setScrollNonFitText(z);
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
        ActionBarMenu actionBarMenu2 = new ActionBarMenu(getContext(), this);
        this.menu = actionBarMenu2;
        addView(actionBarMenu2, 0, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick actionBarMenuOnItemClick2) {
        this.actionBarMenuOnItemClick = actionBarMenuOnItemClick2;
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

    public boolean actionModeIsExist(String str) {
        if (this.actionMode == null) {
            return false;
        }
        String str2 = this.actionModeTag;
        if (str2 == null && str == null) {
            return true;
        }
        return str2 != null && str2.equals(str);
    }

    public ActionBarMenu createActionMode(boolean z, String str) {
        if (actionModeIsExist(str)) {
            return this.actionMode;
        }
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            removeView(actionBarMenu);
            this.actionMode = null;
        }
        this.actionModeTag = str;
        AnonymousClass1 r4 = new ActionBarMenu(getContext(), this) {
            public void setBackgroundColor(int i) {
                int unused = ActionBar.this.actionModeColor = i;
                super.setBackgroundColor(i);
            }
        };
        this.actionMode = r4;
        r4.isActionMode = true;
        r4.setClickable(true);
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
            View view = new View(getContext());
            this.actionModeTop = view;
            view.setBackgroundColor(Theme.getColor("actionBarActionModeDefaultTop"));
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

    public void showActionMode(boolean z, View view, View view2, View[] viewArr, boolean[] zArr, View view3, int i) {
        View view4;
        View view5;
        View view6;
        View view7 = view;
        View view8 = view2;
        View[] viewArr2 = viewArr;
        final boolean[] zArr2 = zArr;
        View view9 = view3;
        int i2 = i;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null && !this.actionModeVisible) {
            this.actionModeVisible = true;
            if (z) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.actionMode, View.ALPHA, new float[]{0.0f, 1.0f}));
                if (viewArr2 != null) {
                    for (int i3 = 0; i3 < viewArr2.length; i3++) {
                        if (viewArr2[i3] != null) {
                            arrayList.add(ObjectAnimator.ofFloat(viewArr2[i3], View.ALPHA, new float[]{1.0f, 0.0f}));
                        }
                    }
                }
                if (view8 != null) {
                    arrayList.add(ObjectAnimator.ofFloat(view8, View.ALPHA, new float[]{0.0f, 1.0f}));
                }
                if (view9 != null) {
                    arrayList.add(ObjectAnimator.ofFloat(view9, View.TRANSLATION_Y, new float[]{(float) i2}));
                    this.actionModeTranslationView = view9;
                }
                this.actionModeExtraView = view7;
                this.actionModeShowingView = view8;
                this.actionModeHidingViews = viewArr2;
                if (this.occupyStatusBar && (view6 = this.actionModeTop) != null && !SharedConfig.noStatusBar) {
                    arrayList.add(ObjectAnimator.ofFloat(view6, View.ALPHA, new float[]{0.0f, 1.0f}));
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
                animatorSet2.playTogether(arrayList);
                this.actionModeAnimation.setDuration(200);
                this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        ActionBar.this.actionMode.setVisibility(0);
                        if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null && !SharedConfig.noStatusBar) {
                            ActionBar.this.actionModeTop.setVisibility(0);
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        boolean[] zArr;
                        if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                            AnimatorSet unused = ActionBar.this.actionModeAnimation = null;
                            if (ActionBar.this.titleTextView[0] != null) {
                                ActionBar.this.titleTextView[0].setVisibility(4);
                            }
                            if (ActionBar.this.subtitleTextView != null && !TextUtils.isEmpty(ActionBar.this.subtitleTextView.getText())) {
                                ActionBar.this.subtitleTextView.setVisibility(4);
                            }
                            if (ActionBar.this.menu != null) {
                                ActionBar.this.menu.setVisibility(4);
                            }
                            if (ActionBar.this.actionModeHidingViews != null) {
                                for (int i = 0; i < ActionBar.this.actionModeHidingViews.length; i++) {
                                    if (ActionBar.this.actionModeHidingViews[i] != null && ((zArr = zArr2) == null || i >= zArr.length || zArr[i])) {
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
                    return;
                }
                return;
            }
            actionBarMenu.setAlpha(1.0f);
            if (viewArr2 != null) {
                for (int i4 = 0; i4 < viewArr2.length; i4++) {
                    if (viewArr2[i4] != null) {
                        viewArr2[i4].setAlpha(0.0f);
                    }
                }
            }
            if (view8 != null) {
                view8.setAlpha(1.0f);
            }
            if (view9 != null) {
                view9.setTranslationY((float) i2);
                this.actionModeTranslationView = view9;
            }
            this.actionModeExtraView = view7;
            this.actionModeShowingView = view8;
            this.actionModeHidingViews = viewArr2;
            if (this.occupyStatusBar && (view5 = this.actionModeTop) != null && !SharedConfig.noStatusBar) {
                view5.setAlpha(1.0f);
            }
            if (SharedConfig.noStatusBar) {
                if (AndroidUtilities.computePerceivedBrightness(this.actionModeColor) < 0.721f) {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
                } else {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
                }
            }
            this.actionMode.setVisibility(0);
            if (this.occupyStatusBar && (view4 = this.actionModeTop) != null && !SharedConfig.noStatusBar) {
                view4.setVisibility(0);
            }
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (simpleTextViewArr[0] != null) {
                simpleTextViewArr[0].setVisibility(4);
            }
            SimpleTextView simpleTextView = this.subtitleTextView;
            if (simpleTextView != null && !TextUtils.isEmpty(simpleTextView.getText())) {
                this.subtitleTextView.setVisibility(4);
            }
            ActionBarMenu actionBarMenu2 = this.menu;
            if (actionBarMenu2 != null) {
                actionBarMenu2.setVisibility(4);
            }
            if (this.actionModeHidingViews != null) {
                int i5 = 0;
                while (true) {
                    View[] viewArr3 = this.actionModeHidingViews;
                    if (i5 >= viewArr3.length) {
                        break;
                    }
                    if (viewArr3[i5] != null && (zArr2 == null || i5 >= zArr2.length || zArr2[i5])) {
                        viewArr3[i5].setVisibility(4);
                    }
                    i5++;
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
                    if (viewArr[i] != null) {
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
            if (this.occupyStatusBar && (view = this.actionModeTop) != null && !SharedConfig.noStatusBar) {
                arrayList.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f}));
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
            animatorSet2.playTogether(arrayList);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
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

                public void onAnimationCancel(Animator animator) {
                    if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
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
                SimpleTextView simpleTextView = this.subtitleTextView;
                if (simpleTextView != null && !TextUtils.isEmpty(simpleTextView.getText())) {
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
            view.setBackgroundColor(Theme.getColor("actionBarActionModeDefaultTop"));
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

    public void setActionModeOverrideColor(int i) {
        this.actionModeColor = i;
    }

    public void setBackgroundColor(int i) {
        this.actionBarColor = i;
        super.setBackgroundColor(i);
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    public void onSearchFieldVisibilityChanged(boolean z) {
        this.isSearchFieldVisible = z;
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        int i = 0;
        if (simpleTextViewArr[0] != null) {
            simpleTextViewArr[0].setVisibility(z ? 4 : 0);
        }
        SimpleTextView simpleTextView = this.subtitleTextView;
        if (simpleTextView != null && !TextUtils.isEmpty(simpleTextView.getText())) {
            SimpleTextView simpleTextView2 = this.subtitleTextView;
            if (z) {
                i = 4;
            }
            simpleTextView2.setVisibility(i);
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

    public void setSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setFilter(mediaFilterData);
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
        for (int i5 = 0; i5 < 2; i5++) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (!((simpleTextViewArr[0] == null || simpleTextViewArr[0].getVisibility() == 8) && ((simpleTextView2 = this.subtitleTextView) == null || simpleTextView2.getVisibility() == 8))) {
                ActionBarMenu actionBarMenu3 = this.menu;
                int measuredWidth = (((size - (actionBarMenu3 != null ? actionBarMenu3.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - i3) - this.titleRightMargin;
                boolean z = this.fromBottom;
                int i6 = 18;
                if (((!z || i5 != 0) && (z || i5 != 1)) || !this.overlayTitleAnimation || !this.titleAnimationRunning) {
                    SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
                    int i7 = 14;
                    if (simpleTextViewArr2[0] == null || simpleTextViewArr2[0].getVisibility() == 8 || (simpleTextView = this.subtitleTextView) == null || simpleTextView.getVisibility() == 8) {
                        SimpleTextView[] simpleTextViewArr3 = this.titleTextView;
                        if (!(simpleTextViewArr3[i5] == null || simpleTextViewArr3[i5].getVisibility() == 8)) {
                            SimpleTextView simpleTextView3 = this.titleTextView[i5];
                            if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) {
                                i6 = 20;
                            }
                            simpleTextView3.setTextSize(i6);
                        }
                        SimpleTextView simpleTextView4 = this.subtitleTextView;
                        if (!(simpleTextView4 == null || simpleTextView4.getVisibility() == 8)) {
                            SimpleTextView simpleTextView5 = this.subtitleTextView;
                            if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) {
                                i7 = 16;
                            }
                            simpleTextView5.setTextSize(i7);
                        }
                    } else {
                        SimpleTextView[] simpleTextViewArr4 = this.titleTextView;
                        if (simpleTextViewArr4[i5] != null) {
                            SimpleTextView simpleTextView6 = simpleTextViewArr4[i5];
                            if (AndroidUtilities.isTablet()) {
                                i6 = 20;
                            }
                            simpleTextView6.setTextSize(i6);
                        }
                        SimpleTextView simpleTextView7 = this.subtitleTextView;
                        if (AndroidUtilities.isTablet()) {
                            i7 = 16;
                        }
                        simpleTextView7.setTextSize(i7);
                    }
                } else {
                    SimpleTextView simpleTextView8 = this.titleTextView[i5];
                    if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) {
                        i6 = 20;
                    }
                    simpleTextView8.setTextSize(i6);
                }
                SimpleTextView[] simpleTextViewArr5 = this.titleTextView;
                if (!(simpleTextViewArr5[i5] == null || simpleTextViewArr5[i5].getVisibility() == 8)) {
                    CharSequence text = this.titleTextView[i5].getText();
                    SimpleTextView[] simpleTextViewArr6 = this.titleTextView;
                    simpleTextViewArr6[i5].setPivotX(simpleTextViewArr6[i5].getTextPaint().measureText(text, 0, text.length()) / 2.0f);
                    this.titleTextView[i5].setPivotY((float) (AndroidUtilities.dp(24.0f) >> 1));
                    this.titleTextView[i5].measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
                }
                SimpleTextView simpleTextView9 = this.subtitleTextView;
                if (!(simpleTextView9 == null || simpleTextView9.getVisibility() == 8)) {
                    this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
            }
        }
        int childCount = getChildCount();
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            if (childAt.getVisibility() != 8) {
                SimpleTextView[] simpleTextViewArr7 = this.titleTextView;
                if (!(childAt == simpleTextViewArr7[0] || childAt == simpleTextViewArr7[1] || childAt == this.subtitleTextView || childAt == this.menu || childAt == this.backButtonImageView)) {
                    measureChildWithMargins(childAt, i, 0, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM), 0);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean r15, int r16, int r17, int r18, int r19) {
        /*
            r14 = this;
            r0 = r14
            boolean r1 = r0.occupyStatusBar
            r2 = 0
            if (r1 == 0) goto L_0x0009
            int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x000a
        L_0x0009:
            r1 = 0
        L_0x000a:
            android.widget.ImageView r3 = r0.backButtonImageView
            r4 = 8
            if (r3 == 0) goto L_0x0036
            int r3 = r3.getVisibility()
            if (r3 == r4) goto L_0x0036
            android.widget.ImageView r3 = r0.backButtonImageView
            int r5 = r3.getMeasuredWidth()
            android.widget.ImageView r6 = r0.backButtonImageView
            int r6 = r6.getMeasuredHeight()
            int r6 = r6 + r1
            r3.layout(r2, r1, r5, r6)
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x002f
            r3 = 1117782016(0x42a00000, float:80.0)
            goto L_0x0031
        L_0x002f:
            r3 = 1116733440(0x42900000, float:72.0)
        L_0x0031:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0045
        L_0x0036:
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x003f
            r3 = 1104150528(0x41d00000, float:26.0)
            goto L_0x0041
        L_0x003f:
            r3 = 1099956224(0x41900000, float:18.0)
        L_0x0041:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x0045:
            org.telegram.ui.ActionBar.ActionBarMenu r5 = r0.menu
            if (r5 == 0) goto L_0x007d
            int r5 = r5.getVisibility()
            if (r5 == r4) goto L_0x007d
            boolean r5 = r0.isSearchFieldVisible
            if (r5 == 0) goto L_0x0063
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x005c
            r5 = 1116995584(0x42940000, float:74.0)
            goto L_0x005e
        L_0x005c:
            r5 = 1115947008(0x42840000, float:66.0)
        L_0x005e:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x006c
        L_0x0063:
            int r5 = r18 - r16
            org.telegram.ui.ActionBar.ActionBarMenu r6 = r0.menu
            int r6 = r6.getMeasuredWidth()
            int r5 = r5 - r6
        L_0x006c:
            org.telegram.ui.ActionBar.ActionBarMenu r6 = r0.menu
            int r7 = r6.getMeasuredWidth()
            int r7 = r7 + r5
            org.telegram.ui.ActionBar.ActionBarMenu r8 = r0.menu
            int r8 = r8.getMeasuredHeight()
            int r8 = r8 + r1
            r6.layout(r5, r1, r7, r8)
        L_0x007d:
            r5 = 0
        L_0x007e:
            r6 = 1
            r7 = 2
            if (r5 >= r7) goto L_0x0111
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.titleTextView
            r9 = r8[r5]
            if (r9 == 0) goto L_0x010d
            r8 = r8[r5]
            int r8 = r8.getVisibility()
            if (r8 == r4) goto L_0x010d
            boolean r8 = r0.fromBottom
            if (r8 == 0) goto L_0x0096
            if (r5 == 0) goto L_0x009a
        L_0x0096:
            if (r8 != 0) goto L_0x00b1
            if (r5 != r6) goto L_0x00b1
        L_0x009a:
            boolean r6 = r0.overlayTitleAnimation
            if (r6 == 0) goto L_0x00b1
            boolean r6 = r0.titleAnimationRunning
            if (r6 == 0) goto L_0x00b1
            int r6 = getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.titleTextView
            r8 = r8[r5]
            int r8 = r8.getTextHeight()
            int r6 = r6 - r8
            int r6 = r6 / r7
            goto L_0x00f5
        L_0x00b1:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.subtitleTextView
            if (r6 == 0) goto L_0x00e7
            int r6 = r6.getVisibility()
            if (r6 == r4) goto L_0x00e7
            int r6 = getCurrentActionBarHeight()
            int r6 = r6 / r7
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.titleTextView
            r8 = r8[r5]
            int r8 = r8.getTextHeight()
            int r6 = r6 - r8
            int r6 = r6 / r7
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 != 0) goto L_0x00df
            android.content.res.Resources r8 = r14.getResources()
            android.content.res.Configuration r8 = r8.getConfiguration()
            int r8 = r8.orientation
            if (r8 != r7) goto L_0x00df
            r7 = 1073741824(0x40000000, float:2.0)
            goto L_0x00e1
        L_0x00df:
            r7 = 1077936128(0x40400000, float:3.0)
        L_0x00e1:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r7
            goto L_0x00f5
        L_0x00e7:
            int r6 = getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.titleTextView
            r8 = r8[r5]
            int r8 = r8.getTextHeight()
            int r6 = r6 - r8
            int r6 = r6 / r7
        L_0x00f5:
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.titleTextView
            r8 = r7[r5]
            int r6 = r6 + r1
            r7 = r7[r5]
            int r7 = r7.getMeasuredWidth()
            int r7 = r7 + r3
            org.telegram.ui.ActionBar.SimpleTextView[] r9 = r0.titleTextView
            r9 = r9[r5]
            int r9 = r9.getTextHeight()
            int r9 = r9 + r6
            r8.layout(r3, r6, r7, r9)
        L_0x010d:
            int r5 = r5 + 1
            goto L_0x007e
        L_0x0111:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.subtitleTextView
            if (r5 == 0) goto L_0x0157
            int r5 = r5.getVisibility()
            if (r5 == r4) goto L_0x0157
            int r5 = getCurrentActionBarHeight()
            int r5 = r5 / r7
            int r8 = getCurrentActionBarHeight()
            int r8 = r8 / r7
            org.telegram.ui.ActionBar.SimpleTextView r9 = r0.subtitleTextView
            int r9 = r9.getTextHeight()
            int r8 = r8 - r9
            int r8 = r8 / r7
            int r5 = r5 + r8
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 != 0) goto L_0x013e
            android.content.res.Resources r8 = r14.getResources()
            android.content.res.Configuration r8 = r8.getConfiguration()
            int r8 = r8.orientation
        L_0x013e:
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            org.telegram.ui.ActionBar.SimpleTextView r8 = r0.subtitleTextView
            int r1 = r1 + r5
            int r5 = r8.getMeasuredWidth()
            int r5 = r5 + r3
            org.telegram.ui.ActionBar.SimpleTextView r9 = r0.subtitleTextView
            int r9 = r9.getTextHeight()
            int r9 = r9 + r1
            r8.layout(r3, r1, r5, r9)
        L_0x0157:
            int r1 = r14.getChildCount()
            r3 = 0
        L_0x015c:
            if (r3 >= r1) goto L_0x01d5
            android.view.View r5 = r14.getChildAt(r3)
            int r8 = r5.getVisibility()
            if (r8 == r4) goto L_0x01d2
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.titleTextView
            r9 = r8[r2]
            if (r5 == r9) goto L_0x01d2
            r8 = r8[r6]
            if (r5 == r8) goto L_0x01d2
            org.telegram.ui.ActionBar.SimpleTextView r8 = r0.subtitleTextView
            if (r5 == r8) goto L_0x01d2
            org.telegram.ui.ActionBar.ActionBarMenu r8 = r0.menu
            if (r5 == r8) goto L_0x01d2
            android.widget.ImageView r8 = r0.backButtonImageView
            if (r5 != r8) goto L_0x017f
            goto L_0x01d2
        L_0x017f:
            android.view.ViewGroup$LayoutParams r8 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            int r9 = r5.getMeasuredWidth()
            int r10 = r5.getMeasuredHeight()
            int r11 = r8.gravity
            r12 = -1
            if (r11 != r12) goto L_0x0194
            r11 = 51
        L_0x0194:
            r12 = r11 & 7
            r11 = r11 & 112(0x70, float:1.57E-43)
            r12 = r12 & 7
            if (r12 == r6) goto L_0x01a7
            r13 = 5
            if (r12 == r13) goto L_0x01a2
            int r12 = r8.leftMargin
            goto L_0x01b1
        L_0x01a2:
            int r12 = r18 - r9
            int r13 = r8.rightMargin
            goto L_0x01b0
        L_0x01a7:
            int r12 = r18 - r16
            int r12 = r12 - r9
            int r12 = r12 / r7
            int r13 = r8.leftMargin
            int r12 = r12 + r13
            int r13 = r8.rightMargin
        L_0x01b0:
            int r12 = r12 - r13
        L_0x01b1:
            r13 = 16
            if (r11 == r13) goto L_0x01c2
            r13 = 80
            if (r11 == r13) goto L_0x01bc
            int r8 = r8.topMargin
            goto L_0x01cd
        L_0x01bc:
            int r11 = r19 - r17
            int r11 = r11 - r10
            int r8 = r8.bottomMargin
            goto L_0x01cb
        L_0x01c2:
            int r11 = r19 - r17
            int r11 = r11 - r10
            int r11 = r11 / r7
            int r13 = r8.topMargin
            int r11 = r11 + r13
            int r8 = r8.bottomMargin
        L_0x01cb:
            int r8 = r11 - r8
        L_0x01cd:
            int r9 = r9 + r12
            int r10 = r10 + r8
            r5.layout(r12, r8, r9, r10)
        L_0x01d2:
            int r3 = r3 + 1
            goto L_0x015c
        L_0x01d5:
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

    /* JADX WARNING: Removed duplicated region for block: B:59:0x0150  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTitleOverlayText(java.lang.String r5, int r6, java.lang.Runnable r7) {
        /*
            r4 = this;
            boolean r0 = r4.allowOverlayTitle
            if (r0 == 0) goto L_0x0154
            org.telegram.ui.ActionBar.BaseFragment r0 = r4.parentFragment
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r0.parentLayout
            if (r0 != 0) goto L_0x000c
            goto L_0x0154
        L_0x000c:
            java.lang.Object[] r0 = r4.overlayTitleToSet
            r1 = 0
            r0[r1] = r5
            java.lang.Integer r2 = java.lang.Integer.valueOf(r6)
            r3 = 1
            r0[r3] = r2
            java.lang.Object[] r0 = r4.overlayTitleToSet
            r2 = 2
            r0[r2] = r7
            boolean r0 = r4.overlayTitleAnimationInProgress
            if (r0 == 0) goto L_0x0022
            return
        L_0x0022:
            java.lang.CharSequence r0 = r4.lastOverlayTitle
            if (r0 != 0) goto L_0x0028
            if (r5 == 0) goto L_0x0030
        L_0x0028:
            if (r0 == 0) goto L_0x0031
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x0031
        L_0x0030:
            return
        L_0x0031:
            r4.lastOverlayTitle = r5
            if (r5 == 0) goto L_0x003a
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r5, r6)
            goto L_0x003c
        L_0x003a:
            java.lang.CharSequence r6 = r4.lastTitle
        L_0x003c:
            if (r5 == 0) goto L_0x0051
            java.lang.String r0 = "..."
            int r0 = android.text.TextUtils.indexOf(r6, r0)
            if (r0 < 0) goto L_0x0051
            android.text.SpannableString r6 = android.text.SpannableString.valueOf(r6)
            org.telegram.ui.Components.EllipsizeSpanAnimator r2 = r4.ellipsizeSpanAnimator
            r2.wrap(r6, r0)
            r0 = 1
            goto L_0x0052
        L_0x0051:
            r0 = 0
        L_0x0052:
            if (r5 == 0) goto L_0x0056
            r5 = 1
            goto L_0x0057
        L_0x0056:
            r5 = 0
        L_0x0057:
            r4.titleOverlayShown = r5
            if (r6 == 0) goto L_0x0061
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            if (r5 == 0) goto L_0x0120
        L_0x0061:
            int r5 = r4.getMeasuredWidth()
            if (r5 == 0) goto L_0x0120
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r2 = r5[r1]
            if (r2 == 0) goto L_0x0077
            r5 = r5[r1]
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x0077
            goto L_0x0120
        L_0x0077:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r2 = r5[r1]
            if (r2 == 0) goto L_0x014d
            r5 = r5[r1]
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r5.cancel()
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r2 = r5[r3]
            if (r2 == 0) goto L_0x0095
            r5 = r5[r3]
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r5.cancel()
        L_0x0095:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r3]
            if (r5 != 0) goto L_0x009e
            r4.createTitleTextView(r3)
        L_0x009e:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r3]
            r5.setText(r6)
            if (r0 == 0) goto L_0x00b0
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r4.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r4.titleTextView
            r6 = r6[r3]
            r5.addView(r6)
        L_0x00b0:
            r4.overlayTitleAnimationInProgress = r3
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r6 = r5[r3]
            r0 = r5[r1]
            r5[r3] = r0
            r5[r1] = r6
            r5 = r5[r1]
            r6 = 0
            r5.setAlpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            r0 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = -r2
            float r2 = (float) r2
            r5.setTranslationY(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r1 = 1065353216(0x3var_, float:1.0)
            android.view.ViewPropertyAnimator r5 = r5.alpha(r1)
            android.view.ViewPropertyAnimator r5 = r5.translationY(r6)
            r1 = 220(0xdc, double:1.087E-321)
            android.view.ViewPropertyAnimator r5 = r5.setDuration(r1)
            r5.start()
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r3]
            android.view.ViewPropertyAnimator r5 = r5.animate()
            android.view.ViewPropertyAnimator r5 = r5.alpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r4.subtitleTextView
            if (r6 != 0) goto L_0x0105
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r6 = (float) r6
            r5.translationY(r6)
            goto L_0x010f
        L_0x0105:
            r6 = 1060320051(0x3var_, float:0.7)
            android.view.ViewPropertyAnimator r0 = r5.scaleY(r6)
            r0.scaleX(r6)
        L_0x010f:
            android.view.ViewPropertyAnimator r5 = r5.setDuration(r1)
            org.telegram.ui.ActionBar.ActionBar$4 r6 = new org.telegram.ui.ActionBar.ActionBar$4
            r6.<init>()
            android.view.ViewPropertyAnimator r5 = r5.setListener(r6)
            r5.start()
            goto L_0x014d
        L_0x0120:
            r4.createTitleTextView(r1)
            boolean r5 = r4.supportsHolidayImage
            if (r5 == 0) goto L_0x0131
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            r5.invalidate()
            r4.invalidate()
        L_0x0131:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            r5.setText(r6)
            if (r0 == 0) goto L_0x0144
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r4.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r4.titleTextView
            r6 = r6[r1]
            r5.addView(r6)
            goto L_0x014d
        L_0x0144:
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r4.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r4.titleTextView
            r6 = r6[r1]
            r5.removeView(r6)
        L_0x014d:
            if (r7 == 0) goto L_0x0150
            goto L_0x0152
        L_0x0150:
            java.lang.Runnable r7 = r4.lastRunnable
        L_0x0152:
            r4.titleActionRunnable = r7
        L_0x0154:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBar.setTitleOverlayText(java.lang.String, int, java.lang.Runnable):void");
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, z ? AndroidUtilities.statusBarHeight : 0, 0, 0);
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
                imageView.setBackgroundDrawable(Theme.createSelectorDrawable(i));
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
            imageView2.setBackgroundDrawable(Theme.createSelectorDrawable(i));
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int i, boolean z) {
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
        if (!(imageView2 == null || i == 0)) {
            imageView2.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.MULTIPLY));
            Drawable drawable2 = this.backButtonImageView.getDrawable();
            if (drawable2 instanceof BackDrawable) {
                ((BackDrawable) drawable2).setColor(i);
            } else if (drawable2 instanceof MenuDrawable) {
                ((MenuDrawable) drawable2).setIconColor(i);
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

    public void setTitleAnimated(CharSequence charSequence, final boolean z, long j) {
        if (this.titleTextView[0] == null || charSequence == null) {
            setTitle(charSequence);
            return;
        }
        final boolean z2 = this.overlayTitleAnimation && !TextUtils.isEmpty(this.subtitleTextView.getText());
        if (z2) {
            if (this.subtitleTextView.getVisibility() != 0) {
                this.subtitleTextView.setVisibility(0);
                this.subtitleTextView.setAlpha(0.0f);
            }
            this.subtitleTextView.animate().alpha(z ? 0.0f : 1.0f).setDuration(220).start();
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
        setTitle(charSequence);
        this.fromBottom = z;
        this.titleTextView[0].setAlpha(0.0f);
        if (!z2) {
            SimpleTextView simpleTextView = this.titleTextView[0];
            int dp = AndroidUtilities.dp(20.0f);
            if (!z) {
                dp = -dp;
            }
            simpleTextView.setTranslationY((float) dp);
        }
        this.titleTextView[0].animate().alpha(1.0f).translationY(0.0f).setDuration(j).start();
        this.titleAnimationRunning = true;
        ViewPropertyAnimator alpha = this.titleTextView[1].animate().alpha(0.0f);
        if (!z2) {
            int dp2 = AndroidUtilities.dp(20.0f);
            if (z) {
                dp2 = -dp2;
            }
            alpha.translationY((float) dp2);
        }
        alpha.setDuration(j).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!(ActionBar.this.titleTextView[1] == null || ActionBar.this.titleTextView[1].getParent() == null)) {
                    ((ViewGroup) ActionBar.this.titleTextView[1].getParent()).removeView(ActionBar.this.titleTextView[1]);
                }
                ActionBar.this.titleTextView[1] = null;
                boolean unused = ActionBar.this.titleAnimationRunning = false;
                if (z2 && z) {
                    ActionBar.this.subtitleTextView.setVisibility(8);
                }
                ActionBar.this.requestLayout();
            }
        }).start();
        requestLayout();
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

    public void setOverlayTitleAnimation(boolean z) {
        this.overlayTitleAnimation = z;
    }
}
