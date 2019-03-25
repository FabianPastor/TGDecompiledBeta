package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.Theme;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private boolean animateFromPosition;
    private int currentPosition;
    private LayoutParams defaultExpandLayoutParams;
    private LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private int indicatorColor = -10066330;
    private int indicatorHeight;
    private long lastAnimationTime;
    private int lastScrollX = 0;
    private float positionAnimationProgress;
    private Paint rectPaint;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private boolean shouldExpand;
    private float startAnimationPosition;
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(24.0f);
    private LinearLayout tabsContainer;
    private int underlineColor = NUM;
    private int underlineHeight = AndroidUtilities.dp(2.0f);

    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    public ScrollSlidingTabStrip(Context context) {
        super(context);
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.defaultTabLayoutParams = new LayoutParams(AndroidUtilities.dp(52.0f), -1);
        this.defaultExpandLayoutParams = new LayoutParams(0, -1, 1.0f);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabCount = 0;
        this.currentPosition = 0;
        this.animateFromPosition = false;
    }

    public void selectTab(int num) {
        if (num >= 0 && num < this.tabCount) {
            this.tabsContainer.getChildAt(num).performClick();
        }
    }

    public TextView addIconTabWithCounter(Drawable drawable) {
        boolean z;
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setFocusable(true);
        this.tabsContainer.addView(tab);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ScaleType.CENTER);
        tab.setOnClickListener(new ScrollSlidingTabStrip$$Lambda$0(this, position));
        tab.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        if (position == this.currentPosition) {
            z = true;
        } else {
            z = false;
        }
        tab.setSelected(z);
        TextView textView = new TextView(getContext());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 12.0f);
        textView.setTextColor(Theme.getColor("chat_emojiPanelBadgeText"));
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(9.0f), Theme.getColor("chat_emojiPanelBadgeBackground")));
        textView.setMinWidth(AndroidUtilities.dp(18.0f));
        textView.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.0f));
        tab.addView(textView, LayoutHelper.createFrame(-2, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
        return textView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addIconTabWithCounter$0$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public ImageView addIconTab(Drawable drawable) {
        boolean z = true;
        int position = this.tabCount;
        this.tabCount = position + 1;
        ImageView tab = new ImageView(getContext());
        tab.setFocusable(true);
        tab.setImageDrawable(drawable);
        tab.setScaleType(ScaleType.CENTER);
        tab.setOnClickListener(new ScrollSlidingTabStrip$$Lambda$1(this, position));
        this.tabsContainer.addView(tab);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        return tab;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addIconTab$1$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public void addStickerTab(Chat chat) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setFocusable(true);
        tab.setOnClickListener(new ScrollSlidingTabStrip$$Lambda$2(this, position));
        this.tabsContainer.addView(tab);
        tab.setSelected(position == this.currentPosition);
        BackupImageView imageView = new BackupImageView(getContext());
        imageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        TLObject photo = null;
        Drawable avatarDrawable = new AvatarDrawable();
        if (chat.photo != null) {
            photo = chat.photo.photo_small;
        }
        avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
        avatarDrawable.setInfo(chat);
        imageView.setImage(photo, "50_50", avatarDrawable, (Object) chat);
        imageView.setAspectFit(true);
        tab.addView(imageView, LayoutHelper.createFrame(30, 30, 17));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addStickerTab$2$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public View addStickerTab(TLObject sticker, Object parentObject) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setTag(sticker);
        tab.setTag(NUM, parentObject);
        tab.setFocusable(true);
        tab.setOnClickListener(new ScrollSlidingTabStrip$$Lambda$3(this, position));
        this.tabsContainer.addView(tab);
        tab.setSelected(position == this.currentPosition);
        BackupImageView imageView = new BackupImageView(getContext());
        imageView.setAspectFit(true);
        tab.addView(imageView, LayoutHelper.createFrame(30, 30, 17));
        return tab;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addStickerTab$3$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            if (this.shouldExpand) {
                v.setLayoutParams(this.defaultExpandLayoutParams);
            } else {
                v.setLayoutParams(this.defaultTabLayoutParams);
            }
        }
    }

    private void scrollToChild(int position) {
        if (this.tabCount != 0 && this.tabsContainer.getChildAt(position) != null) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft();
            if (position > 0) {
                newScrollX -= this.scrollOffset;
            }
            int currentScrollX = getScrollX();
            if (newScrollX == this.lastScrollX) {
                return;
            }
            if (newScrollX < currentScrollX) {
                this.lastScrollX = newScrollX;
                smoothScrollTo(this.lastScrollX, 0);
            } else if (this.scrollOffset + newScrollX > (getWidth() + currentScrollX) - (this.scrollOffset * 2)) {
                this.lastScrollX = (newScrollX - getWidth()) + (this.scrollOffset * 3);
                smoothScrollTo(this.lastScrollX, 0);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setImages();
    }

    public void setImages() {
        int tabSize = AndroidUtilities.dp(52.0f);
        int start = getScrollX() / tabSize;
        int end = Math.min(this.tabsContainer.getChildCount(), (((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) tabSize)))) + start) + 1);
        for (int a = start; a < end; a++) {
            TLObject thumb;
            View child = this.tabsContainer.getChildAt(a);
            Document object = child.getTag();
            Object parentObject = child.getTag(NUM);
            if (object instanceof Document) {
                thumb = FileLoader.getClosestPhotoSizeWithSize(object.thumbs, 90);
            } else if (object instanceof PhotoSize) {
                PhotoSize thumb2 = (PhotoSize) object;
            } else {
            }
            ((BackupImageView) ((FrameLayout) child).getChildAt(0)).setImage(thumb2, null, "webp", null, parentObject);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int tabSize = AndroidUtilities.dp(52.0f);
        int oldStart = oldl / tabSize;
        int newStart = l / tabSize;
        int count = ((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) tabSize)))) + 1;
        int start = Math.max(0, Math.min(oldStart, newStart));
        int end = Math.min(this.tabsContainer.getChildCount(), Math.max(oldStart, newStart) + count);
        int a = start;
        while (a < end) {
            View child = this.tabsContainer.getChildAt(a);
            if (child != null) {
                TLObject thumb;
                Document object = child.getTag();
                Object parentObject = child.getTag(NUM);
                if (object instanceof Document) {
                    thumb = FileLoader.getClosestPhotoSizeWithSize(object.thumbs, 90);
                } else if (object instanceof PhotoSize) {
                    PhotoSize thumb2 = (PhotoSize) object;
                }
                BackupImageView imageView = (BackupImageView) ((FrameLayout) child).getChildAt(0);
                if (a < newStart || a >= newStart + count) {
                    imageView.setImageDrawable(null);
                } else {
                    imageView.setImage(thumb2, null, "webp", null, parentObject);
                }
            }
            a++;
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            if (this.underlineHeight > 0) {
                this.rectPaint.setColor(this.underlineColor);
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            }
            if (this.indicatorHeight >= 0) {
                View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
                float lineLeft = 0.0f;
                int width = 0;
                if (currentTab != null) {
                    lineLeft = (float) currentTab.getLeft();
                    width = currentTab.getMeasuredWidth();
                }
                if (this.animateFromPosition) {
                    long newTime = SystemClock.uptimeMillis();
                    long dt = newTime - this.lastAnimationTime;
                    this.lastAnimationTime = newTime;
                    this.positionAnimationProgress += ((float) dt) / 150.0f;
                    if (this.positionAnimationProgress >= 1.0f) {
                        this.positionAnimationProgress = 1.0f;
                        this.animateFromPosition = false;
                    }
                    lineLeft = this.startAnimationPosition + ((lineLeft - this.startAnimationPosition) * CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.positionAnimationProgress));
                    invalidate();
                }
                this.rectPaint.setColor(this.indicatorColor);
                if (this.indicatorHeight == 0) {
                    canvas.drawRect(lineLeft, 0.0f, lineLeft + ((float) width), (float) height, this.rectPaint);
                    return;
                }
                canvas.drawRect(lineLeft, (float) (height - this.indicatorHeight), lineLeft + ((float) width), (float) height, this.rectPaint);
            }
        }
    }

    public void setShouldExpand(boolean value) {
        this.shouldExpand = value;
        requestLayout();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void onPageScrolled(int position, int first) {
        if (this.currentPosition != position) {
            View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
            if (currentTab != null) {
                this.startAnimationPosition = (float) currentTab.getLeft();
                this.positionAnimationProgress = 0.0f;
                this.animateFromPosition = true;
                this.lastAnimationTime = SystemClock.uptimeMillis();
            } else {
                this.animateFromPosition = false;
            }
            this.currentPosition = position;
            if (position < this.tabsContainer.getChildCount()) {
                this.positionAnimationProgress = 0.0f;
                for (int a = 0; a < this.tabsContainer.getChildCount(); a++) {
                    boolean z;
                    View childAt = this.tabsContainer.getChildAt(a);
                    if (a == position) {
                        z = true;
                    } else {
                        z = false;
                    }
                    childAt.setSelected(z);
                }
                if (first != position || position <= 1) {
                    scrollToChild(position);
                } else {
                    scrollToChild(position - 1);
                }
                invalidate();
            }
        }
    }

    public void setIndicatorHeight(int value) {
        this.indicatorHeight = value;
        invalidate();
    }

    public void setIndicatorColor(int value) {
        this.indicatorColor = value;
        invalidate();
    }

    public void setUnderlineColor(int value) {
        this.underlineColor = value;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public void setUnderlineHeight(int value) {
        this.underlineHeight = value;
        invalidate();
    }
}
