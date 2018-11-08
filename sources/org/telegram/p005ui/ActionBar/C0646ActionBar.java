package org.telegram.p005ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.Components.FireworksEffect;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.SnowflakesEffect;

/* renamed from: org.telegram.ui.ActionBar.ActionBar */
public class C0646ActionBar extends FrameLayout {
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    private ActionBarMenu actionMode;
    private AnimatorSet actionModeAnimation;
    private View actionModeTop;
    private boolean actionModeVisible;
    private boolean addToContainer;
    private boolean allowOverlayTitle;
    private ImageView backButtonImageView;
    private boolean castShadows;
    private int extraHeight;
    private FireworksEffect fireworksEffect;
    private FontMetricsInt fontMetricsInt;
    private boolean interceptTouches;
    private boolean isBackOverlayVisible;
    protected boolean isSearchFieldVisible;
    protected int itemsActionModeBackgroundColor;
    protected int itemsActionModeColor;
    protected int itemsBackgroundColor;
    protected int itemsColor;
    private Runnable lastRunnable;
    private CharSequence lastSubtitle;
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

    /* renamed from: org.telegram.ui.ActionBar.ActionBar$1 */
    class C06441 extends AnimatorListenerAdapter {
        C06441() {
        }

        public void onAnimationStart(Animator animation) {
            C0646ActionBar.this.actionMode.setVisibility(0);
            if (C0646ActionBar.this.occupyStatusBar && C0646ActionBar.this.actionModeTop != null) {
                C0646ActionBar.this.actionModeTop.setVisibility(0);
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (C0646ActionBar.this.actionModeAnimation != null && C0646ActionBar.this.actionModeAnimation.equals(animation)) {
                C0646ActionBar.this.actionModeAnimation = null;
                if (C0646ActionBar.this.titleTextView != null) {
                    C0646ActionBar.this.titleTextView.setVisibility(4);
                }
                if (!(C0646ActionBar.this.subtitleTextView == null || TextUtils.isEmpty(C0646ActionBar.this.subtitleTextView.getText()))) {
                    C0646ActionBar.this.subtitleTextView.setVisibility(4);
                }
                if (C0646ActionBar.this.menu != null) {
                    C0646ActionBar.this.menu.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (C0646ActionBar.this.actionModeAnimation != null && C0646ActionBar.this.actionModeAnimation.equals(animation)) {
                C0646ActionBar.this.actionModeAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBar$2 */
    class C06452 extends AnimatorListenerAdapter {
        C06452() {
        }

        public void onAnimationEnd(Animator animation) {
            if (C0646ActionBar.this.actionModeAnimation != null && C0646ActionBar.this.actionModeAnimation.equals(animation)) {
                C0646ActionBar.this.actionModeAnimation = null;
                C0646ActionBar.this.actionMode.setVisibility(4);
                if (C0646ActionBar.this.occupyStatusBar && C0646ActionBar.this.actionModeTop != null) {
                    C0646ActionBar.this.actionModeTop.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (C0646ActionBar.this.actionModeAnimation != null && C0646ActionBar.this.actionModeAnimation.equals(animation)) {
                C0646ActionBar.this.actionModeAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBar$ActionBarMenuOnItemClick */
    public static class ActionBarMenuOnItemClick {
        public void onItemClick(int id) {
        }

        public boolean canOpenMenu() {
            return true;
        }
    }

    public C0646ActionBar(Context context) {
        super(context);
        this.occupyStatusBar = VERSION.SDK_INT >= 21;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.castShadows = true;
        setOnClickListener(new ActionBar$$Lambda$0(this));
    }

    final /* synthetic */ void lambda$new$0$ActionBar(View v) {
        if (this.titleActionRunnable != null) {
            this.titleActionRunnable.run();
        }
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView == null) {
            this.backButtonImageView = new ImageView(getContext());
            this.backButtonImageView.setScaleType(ScaleType.CENTER);
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
            if (this.itemsColor != 0) {
                this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, Mode.MULTIPLY));
            }
            this.backButtonImageView.setPadding(AndroidUtilities.m10dp(1.0f), 0, 0, 0);
            addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
            this.backButtonImageView.setOnClickListener(new ActionBar$$Lambda$1(this));
        }
    }

    final /* synthetic */ void lambda$createBackButtonImage$1$ActionBar(View v) {
        if (!this.actionModeVisible && this.isSearchFieldVisible) {
            closeSearchField();
        } else if (this.actionBarMenuOnItemClick != null) {
            this.actionBarMenuOnItemClick.onItemClick(-1);
        }
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

    public void setSupportsHolidayImage(boolean value) {
        this.supportsHolidayImage = value;
        if (this.supportsHolidayImage) {
            this.fontMetricsInt = new FontMetricsInt();
            this.rect = new Rect();
        }
        invalidate();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && ev.getAction() == 0) {
            Drawable drawable = Theme.getCurrentHolidayDrawable();
            if (drawable != null && drawable.getBounds().contains((int) ev.getX(), (int) ev.getY())) {
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
        return super.onInterceptTouchEvent(ev);
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && child == this.titleTextView) {
            Drawable drawable = Theme.getCurrentHolidayDrawable();
            if (drawable != null) {
                TextPaint textPaint = this.titleTextView.getTextPaint();
                textPaint.getFontMetricsInt(this.fontMetricsInt);
                textPaint.getTextBounds((String) this.titleTextView.getText(), 0, 1, this.rect);
                int x = (this.titleTextView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset()) + ((this.rect.width() - (drawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
                int y = (this.titleTextView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset()) + ((int) Math.ceil((double) (((float) (this.titleTextView.getTextHeight() - this.rect.height())) / 2.0f)));
                drawable.setBounds(x, y - drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth() + x, y);
                drawable.draw(canvas);
                if (Theme.canStartHolidayAnimation()) {
                    if (this.snowflakesEffect == null) {
                        this.snowflakesEffect = new SnowflakesEffect();
                    }
                } else if (!(this.manualStart || this.snowflakesEffect == null)) {
                    this.snowflakesEffect = null;
                }
                if (this.snowflakesEffect != null) {
                    this.snowflakesEffect.onDraw(this, canvas);
                } else if (this.fireworksEffect != null) {
                    this.fireworksEffect.onDraw(this, canvas);
                }
            }
        }
        return result;
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
            this.subtitleTextView = new SimpleTextView(getContext());
            this.subtitleTextView.setGravity(3);
            this.subtitleTextView.setVisibility(8);
            this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
            addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setAddToContainer(boolean value) {
        this.addToContainer = value;
    }

    public boolean getAddToContainer() {
        return this.addToContainer;
    }

    public void setSubtitle(CharSequence value) {
        if (value != null && this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        if (this.subtitleTextView != null) {
            this.lastSubtitle = value;
            SimpleTextView simpleTextView = this.subtitleTextView;
            int i = (TextUtils.isEmpty(value) || this.isSearchFieldVisible) ? 8 : 0;
            simpleTextView.setVisibility(i);
            this.subtitleTextView.setText(value);
        }
    }

    private void createTitleTextView() {
        if (this.titleTextView == null) {
            this.titleTextView = new SimpleTextView(getContext());
            this.titleTextView.setGravity(3);
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.titleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setTitleRightMargin(int value) {
        this.titleRightMargin = value;
    }

    public void setTitle(CharSequence value) {
        if (value != null && this.titleTextView == null) {
            createTitleTextView();
        }
        if (this.titleTextView != null) {
            this.lastTitle = value;
            SimpleTextView simpleTextView = this.titleTextView;
            int i = (value == null || this.isSearchFieldVisible) ? 4 : 0;
            simpleTextView.setVisibility(i);
            this.titleTextView.setText(value);
        }
    }

    public void setTitleColor(int color) {
        if (this.titleTextView == null) {
            createTitleTextView();
        }
        this.titleTextView.setTextColor(color);
    }

    public void setSubtitleColor(int color) {
        if (this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        this.subtitleTextView.setTextColor(color);
    }

    public void setPopupItemsColor(int color) {
        if (this.menu != null) {
            this.menu.setPopupItemsColor(color);
        }
    }

    public void setPopupBackgroundColor(int color) {
        if (this.menu != null) {
            this.menu.redrawPopup(color);
        }
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView;
    }

    public String getTitle() {
        if (this.titleTextView == null) {
            return null;
        }
        return this.titleTextView.getText().toString();
    }

    public String getSubtitle() {
        if (this.subtitleTextView == null) {
            return null;
        }
        return this.subtitleTextView.getText().toString();
    }

    public ActionBarMenu createMenu() {
        if (this.menu != null) {
            return this.menu;
        }
        this.menu = new ActionBarMenu(getContext(), this);
        addView(this.menu, 0, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick listener) {
        this.actionBarMenuOnItemClick = listener;
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

    public ActionBarMenu createActionMode(boolean needTop) {
        if (this.actionMode != null) {
            return this.actionMode;
        }
        int i;
        this.actionMode = new ActionBarMenu(getContext(), this);
        this.actionMode.isActionMode = true;
        this.actionMode.setBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefault));
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        ActionBarMenu actionBarMenu = this.actionMode;
        if (this.occupyStatusBar) {
            i = AndroidUtilities.statusBarHeight;
        } else {
            i = 0;
        }
        actionBarMenu.setPadding(0, i, 0, 0);
        LayoutParams layoutParams = (LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.bottomMargin = this.extraHeight;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        if (this.occupyStatusBar && needTop && this.actionModeTop == null) {
            this.actionModeTop = new View(getContext());
            this.actionModeTop.setBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultTop));
            addView(this.actionModeTop);
            layoutParams = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
            this.actionModeTop.setVisibility(4);
        }
        return this.actionMode;
    }

    public void showActionMode() {
        if (this.actionMode != null && !this.actionModeVisible) {
            this.actionModeVisible = true;
            ArrayList<Animator> animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f, 1.0f}));
            if (this.occupyStatusBar && this.actionModeTop != null) {
                animators.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f, 1.0f}));
            }
            if (this.actionModeAnimation != null) {
                this.actionModeAnimation.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(animators);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new C06441());
            this.actionModeAnimation.start();
            if (this.backButtonImageView != null) {
                Drawable drawable = this.backButtonImageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(1.0f, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
            }
        }
    }

    public void hideActionMode() {
        if (this.actionMode != null && this.actionModeVisible) {
            this.actionModeVisible = false;
            ArrayList<Animator> animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f}));
            if (this.occupyStatusBar && this.actionModeTop != null) {
                animators.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f}));
            }
            if (this.actionModeAnimation != null) {
                this.actionModeAnimation.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(animators);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new C06452());
            this.actionModeAnimation.start();
            if (this.titleTextView != null) {
                this.titleTextView.setVisibility(0);
            }
            if (!(this.subtitleTextView == null || TextUtils.isEmpty(this.subtitleTextView.getText()))) {
                this.subtitleTextView.setVisibility(0);
            }
            if (this.menu != null) {
                this.menu.setVisibility(0);
            }
            if (this.backButtonImageView != null) {
                Drawable drawable = this.backButtonImageView.getDrawable();
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
            this.actionModeTop.setBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultTop));
            addView(this.actionModeTop);
            LayoutParams layoutParams = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
        }
    }

    public void setActionModeTopColor(int color) {
        if (this.actionModeTop != null) {
            this.actionModeTop.setBackgroundColor(color);
        }
    }

    public void setSearchTextColor(int color, boolean placeholder) {
        if (this.menu != null) {
            this.menu.setSearchTextColor(color, placeholder);
        }
    }

    public void setActionModeColor(int color) {
        if (this.actionMode != null) {
            this.actionMode.setBackgroundColor(color);
        }
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    protected void onSearchFieldVisibilityChanged(boolean visible) {
        int i = 4;
        this.isSearchFieldVisible = visible;
        if (this.titleTextView != null) {
            this.titleTextView.setVisibility(visible ? 4 : 0);
        }
        if (!(this.subtitleTextView == null || TextUtils.isEmpty(this.subtitleTextView.getText()))) {
            SimpleTextView simpleTextView = this.subtitleTextView;
            if (!visible) {
                i = 0;
            }
            simpleTextView.setVisibility(i);
        }
        Drawable drawable = this.backButtonImageView.getDrawable();
        if (drawable != null && (drawable instanceof MenuDrawable)) {
            ((MenuDrawable) drawable).setRotation(visible ? 1.0f : 0.0f, true);
        }
    }

    public void setInterceptTouches(boolean value) {
        this.interceptTouches = value;
    }

    public void setExtraHeight(int value) {
        this.extraHeight = value;
        if (this.actionMode != null) {
            LayoutParams layoutParams = (LayoutParams) this.actionMode.getLayoutParams();
            layoutParams.bottomMargin = this.extraHeight;
            this.actionMode.setLayoutParams(layoutParams);
        }
    }

    public void closeSearchField() {
        closeSearchField(true);
    }

    public void closeSearchField(boolean closeKeyboard) {
        if (this.isSearchFieldVisible && this.menu != null) {
            this.menu.closeSearchField(closeKeyboard);
        }
    }

    public void openSearchField(String text) {
        if (this.menu != null && text != null) {
            this.menu.openSearchField(!this.isSearchFieldVisible, text);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (this.backButtonImageView != null) {
            this.backButtonImageView.setEnabled(enabled);
        }
        if (this.menu != null) {
            this.menu.setEnabled(enabled);
        }
        if (this.actionMode != null) {
            this.actionMode.setEnabled(enabled);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textLeft;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int actionBarHeight = C0646ActionBar.getCurrentActionBarHeight();
        int actionBarHeightSpec = MeasureSpec.makeMeasureSpec(actionBarHeight, NUM);
        setMeasuredDimension(width, ((this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + actionBarHeight) + this.extraHeight);
        if (this.backButtonImageView == null || this.backButtonImageView.getVisibility() == 8) {
            textLeft = AndroidUtilities.m10dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        } else {
            this.backButtonImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(54.0f), NUM), actionBarHeightSpec);
            textLeft = AndroidUtilities.m10dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        if (!(this.menu == null || this.menu.getVisibility() == 8)) {
            int menuWidth;
            if (this.isSearchFieldVisible) {
                menuWidth = MeasureSpec.makeMeasureSpec(width - AndroidUtilities.m10dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f), NUM);
            } else {
                menuWidth = MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE);
            }
            this.menu.measure(menuWidth, actionBarHeightSpec);
        }
        if (!((this.titleTextView == null || this.titleTextView.getVisibility() == 8) && (this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8))) {
            SimpleTextView simpleTextView;
            int i;
            int availableWidth = (((width - (this.menu != null ? this.menu.getMeasuredWidth() : 0)) - AndroidUtilities.m10dp(16.0f)) - textLeft) - this.titleRightMargin;
            if (!(this.titleTextView == null || this.titleTextView.getVisibility() == 8)) {
                simpleTextView = this.titleTextView;
                i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18;
                simpleTextView.setTextSize(i);
                this.titleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(24.0f), Integer.MIN_VALUE));
            }
            if (!(this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8)) {
                simpleTextView = this.subtitleTextView;
                i = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14;
                simpleTextView.setTextSize(i);
                this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(20.0f), Integer.MIN_VALUE));
            }
        }
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View child = getChildAt(i2);
            if (!(child.getVisibility() == 8 || child == this.titleTextView || child == this.subtitleTextView || child == this.menu || child == this.backButtonImageView)) {
                measureChildWithMargins(child, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM), 0);
            }
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int textLeft;
        int textTop;
        int currentActionBarHeight;
        float f;
        int additionalTop = this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0;
        if (this.backButtonImageView == null || this.backButtonImageView.getVisibility() == 8) {
            textLeft = AndroidUtilities.m10dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        } else {
            this.backButtonImageView.layout(0, additionalTop, this.backButtonImageView.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + additionalTop);
            textLeft = AndroidUtilities.m10dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        if (!(this.menu == null || this.menu.getVisibility() == 8)) {
            int menuLeft;
            if (this.isSearchFieldVisible) {
                menuLeft = AndroidUtilities.m10dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f);
            } else {
                menuLeft = (right - left) - this.menu.getMeasuredWidth();
            }
            this.menu.layout(menuLeft, additionalTop, this.menu.getMeasuredWidth() + menuLeft, this.menu.getMeasuredHeight() + additionalTop);
        }
        if (!(this.titleTextView == null || this.titleTextView.getVisibility() == 8)) {
            if (this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8) {
                textTop = (C0646ActionBar.getCurrentActionBarHeight() - this.titleTextView.getTextHeight()) / 2;
            } else {
                currentActionBarHeight = ((C0646ActionBar.getCurrentActionBarHeight() / 2) - this.titleTextView.getTextHeight()) / 2;
                f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 3.0f : 2.0f;
                textTop = currentActionBarHeight + AndroidUtilities.m10dp(f);
            }
            this.titleTextView.layout(textLeft, additionalTop + textTop, this.titleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.titleTextView.getTextHeight());
        }
        if (!(this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8)) {
            currentActionBarHeight = (((C0646ActionBar.getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2) + (C0646ActionBar.getCurrentActionBarHeight() / 2);
            f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 1.0f : 1.0f;
            textTop = currentActionBarHeight - AndroidUtilities.m10dp(f);
            this.subtitleTextView.layout(textLeft, additionalTop + textTop, this.subtitleTextView.getMeasuredWidth() + textLeft, (additionalTop + textTop) + this.subtitleTextView.getTextHeight());
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (!(child.getVisibility() == 8 || child == this.titleTextView || child == this.subtitleTextView || child == this.menu || child == this.backButtonImageView)) {
                int childLeft;
                int childTop;
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = 51;
                }
                int verticalGravity = gravity & 112;
                switch ((gravity & 7) & 7) {
                    case 1:
                        childLeft = ((((right - left) - width) / 2) + lp.leftMargin) - lp.rightMargin;
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
                        childTop = ((((bottom - top) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        break;
                    case 48:
                        childTop = lp.topMargin;
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
    }

    public void onMenuButtonPressed() {
        if (this.menu != null) {
            this.menu.onMenuButtonPressed();
        }
    }

    protected void onPause() {
        if (this.menu != null) {
            this.menu.hideAllPopupMenus();
        }
    }

    public void setAllowOverlayTitle(boolean value) {
        this.allowOverlayTitle = value;
    }

    public void setTitleActionRunnable(Runnable action) {
        this.titleActionRunnable = action;
        this.lastRunnable = action;
    }

    public void setTitleOverlayText(String title, String subtitle, Runnable action) {
        int i = 0;
        if (this.allowOverlayTitle && this.parentFragment.parentLayout != null) {
            CharSequence textToSet = title != null ? title : this.lastTitle;
            if (textToSet != null && this.titleTextView == null) {
                createTitleTextView();
            }
            if (this.titleTextView != null) {
                boolean z;
                if (title != null) {
                    z = true;
                } else {
                    z = false;
                }
                this.titleOverlayShown = z;
                if (this.supportsHolidayImage) {
                    this.titleTextView.invalidate();
                    invalidate();
                }
                SimpleTextView simpleTextView = this.titleTextView;
                int i2 = (textToSet == null || this.isSearchFieldVisible) ? 4 : 0;
                simpleTextView.setVisibility(i2);
                this.titleTextView.setText(textToSet);
            }
            textToSet = subtitle != null ? subtitle : this.lastSubtitle;
            if (textToSet != null && this.subtitleTextView == null) {
                createSubtitleTextView();
            }
            if (this.subtitleTextView != null) {
                SimpleTextView simpleTextView2 = this.subtitleTextView;
                if (TextUtils.isEmpty(textToSet) || this.isSearchFieldVisible) {
                    i = 8;
                }
                simpleTextView2.setVisibility(i);
                this.subtitleTextView.setText(textToSet);
            }
            if (action == null) {
                action = this.lastRunnable;
            }
            this.titleActionRunnable = action;
        }
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
        if (this.actionMode != null) {
            int i;
            ActionBarMenu actionBarMenu = this.actionMode;
            if (this.occupyStatusBar) {
                i = AndroidUtilities.statusBarHeight;
            } else {
                i = 0;
            }
            actionBarMenu.setPadding(0, i, 0, 0);
        }
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public void setItemsBackgroundColor(int color, boolean isActionMode) {
        if (isActionMode) {
            this.itemsActionModeBackgroundColor = color;
            if (this.actionModeVisible && this.backButtonImageView != null) {
                this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
            }
            if (this.actionMode != null) {
                this.actionMode.updateItemsBackgroundColor();
                return;
            }
            return;
        }
        this.itemsBackgroundColor = color;
        if (this.backButtonImageView != null) {
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        }
        if (this.menu != null) {
            this.menu.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int color, boolean isActionMode) {
        Drawable drawable;
        if (isActionMode) {
            this.itemsActionModeColor = color;
            if (this.actionMode != null) {
                this.actionMode.updateItemsColor();
            }
            if (this.backButtonImageView != null) {
                drawable = this.backButtonImageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotatedColor(color);
                    return;
                }
                return;
            }
            return;
        }
        this.itemsColor = color;
        if (!(this.backButtonImageView == null || this.itemsColor == 0)) {
            this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, Mode.MULTIPLY));
            drawable = this.backButtonImageView.getDrawable();
            if (drawable instanceof BackDrawable) {
                ((BackDrawable) drawable).setColor(color);
            }
        }
        if (this.menu != null) {
            this.menu.updateItemsColor();
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
            return AndroidUtilities.m10dp(64.0f);
        }
        if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            return AndroidUtilities.m10dp(48.0f);
        }
        return AndroidUtilities.m10dp(56.0f);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
