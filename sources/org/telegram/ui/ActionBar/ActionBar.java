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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
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
            return view == this.titleTextView[0] || view == this.subtitleTextView || view == this.menu || view == this.backButtonImageView;
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
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && view == this.titleTextView[0] && (currentHolidayDrawable = Theme.getCurrentHolidayDrawable()) != null) {
            TextPaint textPaint = this.titleTextView[0].getTextPaint();
            textPaint.getFontMetricsInt(this.fontMetricsInt);
            textPaint.getTextBounds((String) this.titleTextView[0].getText(), 0, 1, this.rect);
            int textStartX = this.titleTextView[0].getTextStartX() + Theme.getCurrentHolidayDrawableXOffset() + ((this.rect.width() - (currentHolidayDrawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
            int textStartY = this.titleTextView[0].getTextStartY() + Theme.getCurrentHolidayDrawableYOffset() + ((int) Math.ceil((double) (((float) (this.titleTextView[0].getTextHeight() - this.rect.height())) / 2.0f)));
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
        if (this.actionModeTag == null && str == null) {
            return true;
        }
        String str2 = this.actionModeTag;
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
        ActionBarMenu actionBarMenu2 = new ActionBarMenu(getContext(), this);
        this.actionMode = actionBarMenu2;
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
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionModeAnimation = animatorSet2;
            animatorSet2.playTogether(arrayList);
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
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionModeAnimation = animatorSet2;
            animatorSet2.playTogether(arrayList);
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
                SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
                int i6 = 14;
                int i7 = 18;
                if (simpleTextViewArr2[0] == null || simpleTextViewArr2[0].getVisibility() == 8 || (simpleTextView = this.subtitleTextView) == null || simpleTextView.getVisibility() == 8) {
                    SimpleTextView[] simpleTextViewArr3 = this.titleTextView;
                    if (!(simpleTextViewArr3[i5] == null || simpleTextViewArr3[i5].getVisibility() == 8)) {
                        SimpleTextView simpleTextView3 = this.titleTextView[i5];
                        if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) {
                            i7 = 20;
                        }
                        simpleTextView3.setTextSize(i7);
                    }
                    SimpleTextView simpleTextView4 = this.subtitleTextView;
                    if (!(simpleTextView4 == null || simpleTextView4.getVisibility() == 8)) {
                        SimpleTextView simpleTextView5 = this.subtitleTextView;
                        if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) {
                            i6 = 16;
                        }
                        simpleTextView5.setTextSize(i6);
                    }
                } else {
                    SimpleTextView[] simpleTextViewArr4 = this.titleTextView;
                    if (simpleTextViewArr4[i5] != null) {
                        SimpleTextView simpleTextView6 = simpleTextViewArr4[i5];
                        if (AndroidUtilities.isTablet()) {
                            i7 = 20;
                        }
                        simpleTextView6.setTextSize(i7);
                    }
                    SimpleTextView simpleTextView7 = this.subtitleTextView;
                    if (AndroidUtilities.isTablet()) {
                        i6 = 16;
                    }
                    simpleTextView7.setTextSize(i6);
                }
                SimpleTextView[] simpleTextViewArr5 = this.titleTextView;
                if (!(simpleTextViewArr5[i5] == null || simpleTextViewArr5[i5].getVisibility() == 8)) {
                    CharSequence text = this.titleTextView[i5].getText();
                    SimpleTextView[] simpleTextViewArr6 = this.titleTextView;
                    simpleTextViewArr6[i5].setPivotX(simpleTextViewArr6[i5].getTextPaint().measureText(text, 0, text.length()) / 2.0f);
                    this.titleTextView[i5].setPivotY((float) (AndroidUtilities.dp(24.0f) >> 1));
                    this.titleTextView[i5].measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
                }
                SimpleTextView simpleTextView8 = this.subtitleTextView;
                if (!(simpleTextView8 == null || simpleTextView8.getVisibility() == 8)) {
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
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01a0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean r14, int r15, int r16, int r17, int r18) {
        /*
            r13 = this;
            r0 = r13
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
            int r5 = r17 - r15
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
            r6 = 2
            if (r5 >= r6) goto L_0x00ee
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.titleTextView
            r8 = r7[r5]
            if (r8 == 0) goto L_0x00eb
            r7 = r7[r5]
            int r7 = r7.getVisibility()
            if (r7 == r4) goto L_0x00eb
            org.telegram.ui.ActionBar.SimpleTextView r7 = r0.subtitleTextView
            if (r7 == 0) goto L_0x00c5
            int r7 = r7.getVisibility()
            if (r7 == r4) goto L_0x00c5
            int r7 = getCurrentActionBarHeight()
            int r7 = r7 / r6
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.titleTextView
            r8 = r8[r5]
            int r8 = r8.getTextHeight()
            int r7 = r7 - r8
            int r7 = r7 / r6
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 != 0) goto L_0x00bd
            android.content.res.Resources r8 = r13.getResources()
            android.content.res.Configuration r8 = r8.getConfiguration()
            int r8 = r8.orientation
            if (r8 != r6) goto L_0x00bd
            r6 = 1073741824(0x40000000, float:2.0)
            goto L_0x00bf
        L_0x00bd:
            r6 = 1077936128(0x40400000, float:3.0)
        L_0x00bf:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r7 = r7 + r6
            goto L_0x00d3
        L_0x00c5:
            int r7 = getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.titleTextView
            r8 = r8[r5]
            int r8 = r8.getTextHeight()
            int r7 = r7 - r8
            int r7 = r7 / r6
        L_0x00d3:
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.titleTextView
            r8 = r6[r5]
            int r7 = r7 + r1
            r6 = r6[r5]
            int r6 = r6.getMeasuredWidth()
            int r6 = r6 + r3
            org.telegram.ui.ActionBar.SimpleTextView[] r9 = r0.titleTextView
            r9 = r9[r5]
            int r9 = r9.getTextHeight()
            int r9 = r9 + r7
            r8.layout(r3, r7, r6, r9)
        L_0x00eb:
            int r5 = r5 + 1
            goto L_0x007e
        L_0x00ee:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.subtitleTextView
            if (r5 == 0) goto L_0x0134
            int r5 = r5.getVisibility()
            if (r5 == r4) goto L_0x0134
            int r5 = getCurrentActionBarHeight()
            int r5 = r5 / r6
            int r7 = getCurrentActionBarHeight()
            int r7 = r7 / r6
            org.telegram.ui.ActionBar.SimpleTextView r8 = r0.subtitleTextView
            int r8 = r8.getTextHeight()
            int r7 = r7 - r8
            int r7 = r7 / r6
            int r5 = r5 + r7
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r7 != 0) goto L_0x011b
            android.content.res.Resources r7 = r13.getResources()
            android.content.res.Configuration r7 = r7.getConfiguration()
            int r7 = r7.orientation
        L_0x011b:
            r7 = 1065353216(0x3var_, float:1.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r5 - r7
            org.telegram.ui.ActionBar.SimpleTextView r7 = r0.subtitleTextView
            int r1 = r1 + r5
            int r5 = r7.getMeasuredWidth()
            int r5 = r5 + r3
            org.telegram.ui.ActionBar.SimpleTextView r8 = r0.subtitleTextView
            int r8 = r8.getTextHeight()
            int r8 = r8 + r1
            r7.layout(r3, r1, r5, r8)
        L_0x0134:
            int r1 = r13.getChildCount()
            r3 = 0
        L_0x0139:
            if (r3 >= r1) goto L_0x01b3
            android.view.View r5 = r13.getChildAt(r3)
            int r7 = r5.getVisibility()
            if (r7 == r4) goto L_0x01b0
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.titleTextView
            r8 = r7[r2]
            if (r5 == r8) goto L_0x01b0
            r8 = 1
            r7 = r7[r8]
            if (r5 == r7) goto L_0x01b0
            org.telegram.ui.ActionBar.SimpleTextView r7 = r0.subtitleTextView
            if (r5 == r7) goto L_0x01b0
            org.telegram.ui.ActionBar.ActionBarMenu r7 = r0.menu
            if (r5 == r7) goto L_0x01b0
            android.widget.ImageView r7 = r0.backButtonImageView
            if (r5 != r7) goto L_0x015d
            goto L_0x01b0
        L_0x015d:
            android.view.ViewGroup$LayoutParams r7 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r7 = (android.widget.FrameLayout.LayoutParams) r7
            int r9 = r5.getMeasuredWidth()
            int r10 = r5.getMeasuredHeight()
            int r11 = r7.gravity
            r12 = -1
            if (r11 != r12) goto L_0x0172
            r11 = 51
        L_0x0172:
            r12 = r11 & 7
            r11 = r11 & 112(0x70, float:1.57E-43)
            r12 = r12 & 7
            if (r12 == r8) goto L_0x0185
            r8 = 5
            if (r12 == r8) goto L_0x0180
            int r8 = r7.leftMargin
            goto L_0x018f
        L_0x0180:
            int r8 = r17 - r9
            int r12 = r7.rightMargin
            goto L_0x018e
        L_0x0185:
            int r8 = r17 - r15
            int r8 = r8 - r9
            int r8 = r8 / r6
            int r12 = r7.leftMargin
            int r8 = r8 + r12
            int r12 = r7.rightMargin
        L_0x018e:
            int r8 = r8 - r12
        L_0x018f:
            r12 = 16
            if (r11 == r12) goto L_0x01a0
            r12 = 80
            if (r11 == r12) goto L_0x019a
            int r7 = r7.topMargin
            goto L_0x01ab
        L_0x019a:
            int r11 = r18 - r16
            int r11 = r11 - r10
            int r7 = r7.bottomMargin
            goto L_0x01a9
        L_0x01a0:
            int r11 = r18 - r16
            int r11 = r11 - r10
            int r11 = r11 / r6
            int r12 = r7.topMargin
            int r11 = r11 + r12
            int r7 = r7.bottomMargin
        L_0x01a9:
            int r7 = r11 - r7
        L_0x01ab:
            int r9 = r9 + r8
            int r10 = r10 + r7
            r5.layout(r8, r7, r9, r10)
        L_0x01b0:
            int r3 = r3 + 1
            goto L_0x0139
        L_0x01b3:
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

    /* JADX WARNING: Removed duplicated region for block: B:60:0x0152  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTitleOverlayText(java.lang.String r5, int r6, java.lang.Runnable r7) {
        /*
            r4 = this;
            boolean r0 = r4.allowOverlayTitle
            if (r0 == 0) goto L_0x0156
            org.telegram.ui.ActionBar.BaseFragment r0 = r4.parentFragment
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r0.parentLayout
            if (r0 != 0) goto L_0x000c
            goto L_0x0156
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
            if (r5 == 0) goto L_0x0032
        L_0x0028:
            java.lang.CharSequence r0 = r4.lastOverlayTitle
            if (r0 == 0) goto L_0x0033
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x0033
        L_0x0032:
            return
        L_0x0033:
            r4.lastOverlayTitle = r5
            if (r5 == 0) goto L_0x003c
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r5, r6)
            goto L_0x003e
        L_0x003c:
            java.lang.CharSequence r6 = r4.lastTitle
        L_0x003e:
            if (r5 == 0) goto L_0x0053
            java.lang.String r0 = "..."
            int r0 = android.text.TextUtils.indexOf(r6, r0)
            if (r0 < 0) goto L_0x0053
            android.text.SpannableString r6 = android.text.SpannableString.valueOf(r6)
            org.telegram.ui.Components.EllipsizeSpanAnimator r2 = r4.ellipsizeSpanAnimator
            r2.wrap(r6, r0)
            r0 = 1
            goto L_0x0054
        L_0x0053:
            r0 = 0
        L_0x0054:
            if (r5 == 0) goto L_0x0058
            r5 = 1
            goto L_0x0059
        L_0x0058:
            r5 = 0
        L_0x0059:
            r4.titleOverlayShown = r5
            if (r6 == 0) goto L_0x0063
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            if (r5 == 0) goto L_0x0122
        L_0x0063:
            int r5 = r4.getMeasuredWidth()
            if (r5 == 0) goto L_0x0122
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r2 = r5[r1]
            if (r2 == 0) goto L_0x0079
            r5 = r5[r1]
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x0079
            goto L_0x0122
        L_0x0079:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r2 = r5[r1]
            if (r2 == 0) goto L_0x014f
            r5 = r5[r1]
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r5.cancel()
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r2 = r5[r3]
            if (r2 == 0) goto L_0x0097
            r5 = r5[r3]
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r5.cancel()
        L_0x0097:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r3]
            if (r5 != 0) goto L_0x00a0
            r4.createTitleTextView(r3)
        L_0x00a0:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r3]
            r5.setText(r6)
            if (r0 == 0) goto L_0x00b2
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r4.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r4.titleTextView
            r6 = r6[r3]
            r5.addView(r6)
        L_0x00b2:
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
            if (r6 != 0) goto L_0x0107
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r6 = (float) r6
            r5.translationY(r6)
            goto L_0x0111
        L_0x0107:
            r6 = 1060320051(0x3var_, float:0.7)
            android.view.ViewPropertyAnimator r0 = r5.scaleY(r6)
            r0.scaleX(r6)
        L_0x0111:
            android.view.ViewPropertyAnimator r5 = r5.setDuration(r1)
            org.telegram.ui.ActionBar.ActionBar$3 r6 = new org.telegram.ui.ActionBar.ActionBar$3
            r6.<init>()
            android.view.ViewPropertyAnimator r5 = r5.setListener(r6)
            r5.start()
            goto L_0x014f
        L_0x0122:
            r4.createTitleTextView(r1)
            boolean r5 = r4.supportsHolidayImage
            if (r5 == 0) goto L_0x0133
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            r5.invalidate()
            r4.invalidate()
        L_0x0133:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r4.titleTextView
            r5 = r5[r1]
            r5.setText(r6)
            if (r0 == 0) goto L_0x0146
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r4.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r4.titleTextView
            r6 = r6[r1]
            r5.addView(r6)
            goto L_0x014f
        L_0x0146:
            org.telegram.ui.Components.EllipsizeSpanAnimator r5 = r4.ellipsizeSpanAnimator
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r4.titleTextView
            r6 = r6[r1]
            r5.removeView(r6)
        L_0x014f:
            if (r7 == 0) goto L_0x0152
            goto L_0x0154
        L_0x0152:
            java.lang.Runnable r7 = r4.lastRunnable
        L_0x0154:
            r4.titleActionRunnable = r7
        L_0x0156:
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

    public void setTitleAnimated(CharSequence charSequence, boolean z, long j) {
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] == null) {
            setTitle(charSequence);
            return;
        }
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
        this.titleTextView[0].setAlpha(0.0f);
        SimpleTextView simpleTextView = this.titleTextView[0];
        int dp = AndroidUtilities.dp(20.0f);
        if (!z) {
            dp = -dp;
        }
        simpleTextView.setTranslationY((float) dp);
        this.titleTextView[0].animate().alpha(1.0f).translationY(0.0f).setDuration(j).start();
        this.titleTextView[1].animate().alpha(0.0f).translationY((float) (z ? -AndroidUtilities.dp(20.0f) : AndroidUtilities.dp(20.0f))).setDuration(j).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!(ActionBar.this.titleTextView[1] == null || ActionBar.this.titleTextView[1].getParent() == null)) {
                    ((ViewGroup) ActionBar.this.titleTextView[1].getParent()).removeView(ActionBar.this.titleTextView[1]);
                }
                ActionBar.this.titleTextView[1] = null;
            }
        }).start();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.ellipsizeSpanAnimator.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.ellipsizeSpanAnimator.onDetachedFromWindow();
    }
}
