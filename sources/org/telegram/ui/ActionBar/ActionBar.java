package org.telegram.ui.ActionBar;

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
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Collection;
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
    class C07081 implements OnClickListener {
        C07081() {
        }

        public void onClick(View view) {
            if (ActionBar.this.titleActionRunnable != null) {
                ActionBar.this.titleActionRunnable.run();
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBar$2 */
    class C07092 implements OnClickListener {
        C07092() {
        }

        public void onClick(View view) {
            if (ActionBar.this.actionModeVisible != null || ActionBar.this.isSearchFieldVisible == null) {
                if (ActionBar.this.actionBarMenuOnItemClick != null) {
                    ActionBar.this.actionBarMenuOnItemClick.onItemClick(-1);
                }
                return;
            }
            ActionBar.this.closeSearchField();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBar$3 */
    class C07103 extends AnimatorListenerAdapter {
        C07103() {
        }

        public void onAnimationStart(Animator animator) {
            ActionBar.this.actionMode.setVisibility(0);
            if (ActionBar.this.occupyStatusBar != null && ActionBar.this.actionModeTop != null) {
                ActionBar.this.actionModeTop.setVisibility(0);
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator) != null) {
                ActionBar.this.actionModeAnimation = null;
                if (ActionBar.this.titleTextView != null) {
                    ActionBar.this.titleTextView.setVisibility(4);
                }
                if (ActionBar.this.subtitleTextView != null && TextUtils.isEmpty(ActionBar.this.subtitleTextView.getText()) == null) {
                    ActionBar.this.subtitleTextView.setVisibility(4);
                }
                if (ActionBar.this.menu != null) {
                    ActionBar.this.menu.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator) != null) {
                ActionBar.this.actionModeAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBar$4 */
    class C07114 extends AnimatorListenerAdapter {
        C07114() {
        }

        public void onAnimationEnd(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator) != null) {
                ActionBar.this.actionModeAnimation = null;
                ActionBar.this.actionMode.setVisibility(4);
                if (ActionBar.this.occupyStatusBar != null && ActionBar.this.actionModeTop != null) {
                    ActionBar.this.actionModeTop.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator) != null) {
                ActionBar.this.actionModeAnimation = null;
            }
        }
    }

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
        this.occupyStatusBar = VERSION.SDK_INT >= 21 ? 1 : null;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.castShadows = true;
        setOnClickListener(new C07081());
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView == null) {
            this.backButtonImageView = new ImageView(getContext());
            this.backButtonImageView.setScaleType(ScaleType.CENTER);
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
            if (this.itemsColor != 0) {
                this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, Mode.MULTIPLY));
            }
            this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0f), 0, 0, 0);
            addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
            this.backButtonImageView.setOnClickListener(new C07092());
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

    protected boolean drawChild(Canvas canvas, View view, long j) {
        j = super.drawChild(canvas, view, j);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && view == this.titleTextView) {
            view = Theme.getCurrentHolidayDrawable();
            if (view != null) {
                TextPaint textPaint = this.titleTextView.getTextPaint();
                textPaint.getFontMetricsInt(this.fontMetricsInt);
                textPaint.getTextBounds((String) this.titleTextView.getText(), 0, 1, this.rect);
                int textStartX = (this.titleTextView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset()) + ((this.rect.width() - (view.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
                int textStartY = (this.titleTextView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset()) + ((int) Math.ceil((double) (((float) (this.titleTextView.getTextHeight() - this.rect.height())) / 2.0f)));
                view.setBounds(textStartX, textStartY - view.getIntrinsicHeight(), view.getIntrinsicWidth() + textStartX, textStartY);
                view.draw(canvas);
                if (Theme.canStartHolidayAnimation() != null) {
                    if (this.snowflakesEffect == null) {
                        this.snowflakesEffect = new SnowflakesEffect();
                    }
                } else if (this.manualStart == null && this.snowflakesEffect != null) {
                    this.snowflakesEffect = null;
                }
                if (this.snowflakesEffect != null) {
                    this.snowflakesEffect.onDraw(this, canvas);
                } else if (this.fireworksEffect != null) {
                    this.fireworksEffect.onDraw(this, canvas);
                }
            }
        }
        return j;
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
            this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
            addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public void setAddToContainer(boolean z) {
        this.addToContainer = z;
    }

    public boolean getAddToContainer() {
        return this.addToContainer;
    }

    public void setSubtitle(CharSequence charSequence) {
        if (charSequence != null && this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        if (this.subtitleTextView != null) {
            this.lastSubtitle = charSequence;
            SimpleTextView simpleTextView = this.subtitleTextView;
            int i = (TextUtils.isEmpty(charSequence) || this.isSearchFieldVisible) ? 8 : 0;
            simpleTextView.setVisibility(i);
            this.subtitleTextView.setText(charSequence);
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

    public void setTitleRightMargin(int i) {
        this.titleRightMargin = i;
    }

    public void setTitle(CharSequence charSequence) {
        if (charSequence != null && this.titleTextView == null) {
            createTitleTextView();
        }
        if (this.titleTextView != null) {
            this.lastTitle = charSequence;
            SimpleTextView simpleTextView = this.titleTextView;
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

    public void setPopupItemsColor(int i) {
        if (this.menu != null) {
            this.menu.setPopupItemsColor(i);
        }
    }

    public void setPopupBackgroundColor(int i) {
        if (this.menu != null) {
            this.menu.redrawPopup(i);
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
        if (this.actionMode != null) {
            return this.actionMode;
        }
        this.actionMode = new ActionBarMenu(getContext(), this);
        this.actionMode.isActionMode = true;
        this.actionMode.setBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefault));
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        LayoutParams layoutParams = (LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        if (this.occupyStatusBar && this.actionModeTop == null) {
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
        if (this.actionMode != null) {
            if (!this.actionModeVisible) {
                this.actionModeVisible = true;
                Collection arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f, 1.0f}));
                if (this.occupyStatusBar && this.actionModeTop != null) {
                    arrayList.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f, 1.0f}));
                }
                if (this.actionModeAnimation != null) {
                    this.actionModeAnimation.cancel();
                }
                this.actionModeAnimation = new AnimatorSet();
                this.actionModeAnimation.playTogether(arrayList);
                this.actionModeAnimation.setDuration(200);
                this.actionModeAnimation.addListener(new C07103());
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
    }

    public void hideActionMode() {
        if (this.actionMode != null) {
            if (this.actionModeVisible) {
                this.actionModeVisible = false;
                Collection arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f}));
                if (this.occupyStatusBar && this.actionModeTop != null) {
                    arrayList.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f}));
                }
                if (this.actionModeAnimation != null) {
                    this.actionModeAnimation.cancel();
                }
                this.actionModeAnimation = new AnimatorSet();
                this.actionModeAnimation.playTogether(arrayList);
                this.actionModeAnimation.setDuration(200);
                this.actionModeAnimation.addListener(new C07114());
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

    public void setActionModeTopColor(int i) {
        if (this.actionModeTop != null) {
            this.actionModeTop.setBackgroundColor(i);
        }
    }

    public void setSearchTextColor(int i, boolean z) {
        if (this.menu != null) {
            this.menu.setSearchTextColor(i, z);
        }
    }

    public void setActionModeColor(int i) {
        if (this.actionMode != null) {
            this.actionMode.setBackgroundColor(i);
        }
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    protected void onSearchFieldVisibilityChanged(boolean z) {
        this.isSearchFieldVisible = z;
        int i = 0;
        if (this.titleTextView != null) {
            this.titleTextView.setVisibility(z ? 4 : 0);
        }
        if (!(this.subtitleTextView == null || TextUtils.isEmpty(this.subtitleTextView.getText()))) {
            SimpleTextView simpleTextView = this.subtitleTextView;
            if (z) {
                i = 4;
            }
            simpleTextView.setVisibility(i);
        }
        Drawable drawable = this.backButtonImageView.getDrawable();
        if (drawable != null && (drawable instanceof MenuDrawable)) {
            ((MenuDrawable) drawable).setRotation(z ? true : false, true);
        }
    }

    public void setInterceptTouches(boolean z) {
        this.interceptTouches = z;
    }

    public void setExtraHeight(int i) {
        this.extraHeight = i;
    }

    public void closeSearchField() {
        closeSearchField(true);
    }

    public void closeSearchField(boolean z) {
        if (this.isSearchFieldVisible) {
            if (this.menu != null) {
                this.menu.closeSearchField(z);
            }
        }
    }

    public void openSearchField(String str) {
        if (this.menu != null) {
            if (str != null) {
                this.menu.openSearchField(this.isSearchFieldVisible ^ 1, str);
            }
        }
    }

    protected void onMeasure(int i, int i2) {
        int makeMeasureSpec;
        int size = MeasureSpec.getSize(i);
        MeasureSpec.getSize(i2);
        i2 = getCurrentActionBarHeight();
        int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(i2, NUM);
        int i3 = 0;
        setMeasuredDimension(size, (i2 + (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0)) + this.extraHeight);
        if (this.backButtonImageView == 0 || this.backButtonImageView.getVisibility() == 8) {
            i2 = AndroidUtilities.dp(AndroidUtilities.isTablet() != 0 ? NUM : NUM);
        } else {
            this.backButtonImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), NUM), makeMeasureSpec2);
            i2 = AndroidUtilities.dp(AndroidUtilities.isTablet() != 0 ? NUM : NUM);
        }
        if (!(this.menu == null || this.menu.getVisibility() == 8)) {
            if (this.isSearchFieldVisible) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f), NUM);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            }
            this.menu.measure(makeMeasureSpec, makeMeasureSpec2);
        }
        if (!((this.titleTextView == null || this.titleTextView.getVisibility() == 8) && (this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8))) {
            size = (((size - (this.menu != null ? this.menu.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - i2) - this.titleRightMargin;
            if (!(this.titleTextView == 0 || this.titleTextView.getVisibility() == 8)) {
                i2 = this.titleTextView;
                makeMeasureSpec = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18;
                i2.setTextSize(makeMeasureSpec);
                this.titleTextView.measure(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
            }
            if (!(this.subtitleTextView == 0 || this.subtitleTextView.getVisibility() == 8)) {
                i2 = this.subtitleTextView;
                makeMeasureSpec2 = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14;
                i2.setTextSize(makeMeasureSpec2);
                this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
            }
        }
        i2 = getChildCount();
        while (i3 < i2) {
            View childAt = getChildAt(i3);
            if (!(childAt.getVisibility() == 8 || childAt == this.titleTextView || childAt == this.subtitleTextView || childAt == this.menu)) {
                if (childAt != this.backButtonImageView) {
                    measureChildWithMargins(childAt, i, 0, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM), 0);
                }
            }
            i3++;
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int dp;
        int dp2;
        boolean z2 = false;
        z = this.occupyStatusBar ? AndroidUtilities.statusBarHeight : false;
        if (this.backButtonImageView == null || this.backButtonImageView.getVisibility() == 8) {
            dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        } else {
            this.backButtonImageView.layout(0, z, this.backButtonImageView.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + z);
            dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        if (!(this.menu == null || this.menu.getVisibility() == 8)) {
            if (this.isSearchFieldVisible) {
                dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f);
            } else {
                dp2 = (i3 - i) - this.menu.getMeasuredWidth();
            }
            this.menu.layout(dp2, z, this.menu.getMeasuredWidth() + dp2, this.menu.getMeasuredHeight() + z);
        }
        if (!(this.titleTextView == null || this.titleTextView.getVisibility() == 8)) {
            if (this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8) {
                dp2 = (getCurrentActionBarHeight() - this.titleTextView.getTextHeight()) / 2;
            } else {
                dp2 = ((getCurrentActionBarHeight() / 2) - this.titleTextView.getTextHeight()) / 2;
                float f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 3.0f : 2.0f;
                dp2 += AndroidUtilities.dp(f);
            }
            dp2 += z;
            this.titleTextView.layout(dp, dp2, this.titleTextView.getMeasuredWidth() + dp, this.titleTextView.getTextHeight() + dp2);
        }
        if (!(this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8)) {
            dp2 = (getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2);
            if (!AndroidUtilities.isTablet()) {
                int i5 = getResources().getConfiguration().orientation;
            }
            z += dp2 - AndroidUtilities.dp(1.0f);
            this.subtitleTextView.layout(dp, z, this.subtitleTextView.getMeasuredWidth() + dp, this.subtitleTextView.getTextHeight() + z);
        }
        z = getChildCount();
        while (z2 < z) {
            View childAt = getChildAt(z2);
            if (!(childAt.getVisibility() == 8 || childAt == this.titleTextView || childAt == this.subtitleTextView || childAt == this.menu)) {
                if (childAt != this.backButtonImageView) {
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    i5 = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    int i6 = layoutParams.gravity;
                    if (i6 == -1) {
                        i6 = 51;
                    }
                    int i7 = i6 & 7;
                    i6 &= 112;
                    i7 &= 7;
                    if (i7 == 1) {
                        i7 = ((((i3 - i) - i5) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                    } else if (i7 != 5) {
                        i7 = layoutParams.leftMargin;
                    } else {
                        i7 = (i3 - i5) - layoutParams.rightMargin;
                    }
                    dp2 = i6 != 16 ? i6 != 48 ? i6 != 80 ? layoutParams.topMargin : ((i4 - i2) - measuredHeight) - layoutParams.bottomMargin : layoutParams.topMargin : ((((i4 - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                    childAt.layout(i7, dp2, i5 + i7, measuredHeight + dp2);
                }
            }
            z2++;
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

    public void setAllowOverlayTitle(boolean z) {
        this.allowOverlayTitle = z;
    }

    public void setTitleOverlayText(String str, String str2, Runnable runnable) {
        if (this.allowOverlayTitle) {
            if (this.parentFragment.parentLayout != null) {
                CharSequence charSequence;
                if (str != null) {
                    charSequence = str;
                } else {
                    charSequence = this.lastTitle;
                }
                if (charSequence != null && this.titleTextView == null) {
                    createTitleTextView();
                }
                int i = 0;
                if (this.titleTextView != null) {
                    this.titleOverlayShown = str != null ? true : null;
                    if (this.supportsHolidayImage != null) {
                        this.titleTextView.invalidate();
                        invalidate();
                    }
                    str = this.titleTextView;
                    int i2 = (charSequence == null || this.isSearchFieldVisible) ? 4 : 0;
                    str.setVisibility(i2);
                    this.titleTextView.setText(charSequence);
                }
                if (str2 == null) {
                    str2 = this.lastSubtitle;
                }
                if (str2 != null && this.subtitleTextView == null) {
                    createSubtitleTextView();
                }
                if (this.subtitleTextView != null) {
                    str = this.subtitleTextView;
                    if (TextUtils.isEmpty(str2) || this.isSearchFieldVisible) {
                        i = 8;
                    }
                    str.setVisibility(i);
                    this.subtitleTextView.setText(str2);
                }
                this.titleActionRunnable = runnable;
            }
        }
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
        if (this.actionMode) {
            this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public void setItemsBackgroundColor(int i, boolean z) {
        if (z) {
            this.itemsActionModeBackgroundColor = i;
            if (!(this.actionModeVisible == 0 || this.backButtonImageView == 0)) {
                this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
            }
            if (this.actionMode != 0) {
                this.actionMode.updateItemsBackgroundColor();
                return;
            }
            return;
        }
        this.itemsBackgroundColor = i;
        if (this.backButtonImageView != 0) {
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        }
        if (this.menu != 0) {
            this.menu.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int i, boolean z) {
        if (z) {
            this.itemsActionModeColor = i;
            if (this.actionMode) {
                this.actionMode.updateItemsColor();
            }
            if (this.backButtonImageView) {
                z = this.backButtonImageView.getDrawable();
                if (z instanceof BackDrawable) {
                    ((BackDrawable) z).setRotatedColor(i);
                    return;
                }
                return;
            }
            return;
        }
        this.itemsColor = i;
        if (this.backButtonImageView && this.itemsColor) {
            this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, Mode.MULTIPLY));
            z = this.backButtonImageView.getDrawable();
            if (z instanceof BackDrawable) {
                ((BackDrawable) z).setColor(i);
            }
        }
        if (this.menu != 0) {
            this.menu.updateItemsColor();
        }
    }

    public void setCastShadows(boolean z) {
        this.castShadows = z;
    }

    public boolean getCastShadows() {
        return this.castShadows;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (super.onTouchEvent(motionEvent) == null) {
            if (this.interceptTouches == null) {
                return null;
            }
        }
        return true;
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
}
