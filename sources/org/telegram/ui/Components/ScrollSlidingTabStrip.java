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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
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

    public void selectTab(int i) {
        if (i >= 0 && i < this.tabCount) {
            this.tabsContainer.getChildAt(i).performClick();
        }
    }

    public TextView addIconTabWithCounter(Drawable drawable) {
        int i = this.tabCount;
        this.tabCount = i + 1;
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setFocusable(true);
        this.tabsContainer.addView(frameLayout);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ScaleType.CENTER);
        frameLayout.setOnClickListener(new -$$Lambda$ScrollSlidingTabStrip$mNTU5l0eITNwV9vJo7-FdGx3xIs(this, i));
        frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.setSelected(i == this.currentPosition);
        TextView textView = new TextView(getContext());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 12.0f);
        textView.setTextColor(Theme.getColor("chat_emojiPanelBadgeText"));
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(9.0f), Theme.getColor("chat_emojiPanelBadgeBackground")));
        textView.setMinWidth(AndroidUtilities.dp(18.0f));
        textView.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.0f));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
        return textView;
    }

    public /* synthetic */ void lambda$addIconTabWithCounter$0$ScrollSlidingTabStrip(int i, View view) {
        this.delegate.onPageSelected(i);
    }

    public ImageView addIconTab(Drawable drawable) {
        int i = this.tabCount;
        this.tabCount = i + 1;
        ImageView imageView = new ImageView(getContext());
        boolean z = true;
        imageView.setFocusable(true);
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setOnClickListener(new -$$Lambda$ScrollSlidingTabStrip$_7-oyaI-rm6dAg9Sbg63YJVLh4k(this, i));
        this.tabsContainer.addView(imageView);
        if (i != this.currentPosition) {
            z = false;
        }
        imageView.setSelected(z);
        return imageView;
    }

    public /* synthetic */ void lambda$addIconTab$1$ScrollSlidingTabStrip(int i, View view) {
        this.delegate.onPageSelected(i);
    }

    public void addStickerTab(Chat chat) {
        int i = this.tabCount;
        this.tabCount = i + 1;
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setFocusable(true);
        frameLayout.setOnClickListener(new -$$Lambda$ScrollSlidingTabStrip$CxqiesXBMvO9L7nEGHWQr5LTQDY(this, i));
        this.tabsContainer.addView(frameLayout);
        frameLayout.setSelected(i == this.currentPosition);
        BackupImageView backupImageView = new BackupImageView(getContext());
        backupImageView.setLayerNum(1);
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        Drawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
        avatarDrawable.setInfo(chat);
        backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", avatarDrawable, (Object) chat);
        backupImageView.setAspectFit(true);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
    }

    public /* synthetic */ void lambda$addStickerTab$2$ScrollSlidingTabStrip(int i, View view) {
        this.delegate.onPageSelected(i);
    }

    public View addStickerTab(TLObject tLObject, Document document, Object obj) {
        int i = this.tabCount;
        this.tabCount = i + 1;
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setTag(tLObject);
        frameLayout.setTag(NUM, obj);
        frameLayout.setTag(NUM, document);
        frameLayout.setFocusable(true);
        frameLayout.setOnClickListener(new -$$Lambda$ScrollSlidingTabStrip$VOvZofAy7NE0HRe6ap1qMeWlOr0(this, i));
        this.tabsContainer.addView(frameLayout);
        frameLayout.setSelected(i == this.currentPosition);
        BackupImageView backupImageView = new BackupImageView(getContext());
        backupImageView.setLayerNum(1);
        backupImageView.setAspectFit(true);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
        return frameLayout;
    }

    public /* synthetic */ void lambda$addStickerTab$3$ScrollSlidingTabStrip(int i, View view) {
        this.delegate.onPageSelected(i);
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View childAt = this.tabsContainer.getChildAt(i);
            if (this.shouldExpand) {
                childAt.setLayoutParams(this.defaultExpandLayoutParams);
            } else {
                childAt.setLayoutParams(this.defaultTabLayoutParams);
            }
        }
    }

    private void scrollToChild(int i) {
        if (this.tabCount != 0 && this.tabsContainer.getChildAt(i) != null) {
            int left = this.tabsContainer.getChildAt(i).getLeft();
            if (i > 0) {
                left -= this.scrollOffset;
            }
            i = getScrollX();
            if (left == this.lastScrollX) {
                return;
            }
            if (left < i) {
                this.lastScrollX = left;
                smoothScrollTo(this.lastScrollX, 0);
            } else if (this.scrollOffset + left > (i + getWidth()) - (this.scrollOffset * 2)) {
                this.lastScrollX = (left - getWidth()) + (this.scrollOffset * 3);
                smoothScrollTo(this.lastScrollX, 0);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setImages();
    }

    public void setImages() {
        int dp = AndroidUtilities.dp(52.0f);
        int scrollX = getScrollX() / dp;
        dp = Math.min(this.tabsContainer.getChildCount(), (((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) dp)))) + scrollX) + 1);
        while (scrollX < dp) {
            ImageLocation forDocument;
            View childAt = this.tabsContainer.getChildAt(scrollX);
            Object tag = childAt.getTag();
            Object tag2 = childAt.getTag(NUM);
            Document document = (Document) childAt.getTag(NUM);
            boolean z = tag instanceof Document;
            if (z) {
                forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
            } else if (tag instanceof PhotoSize) {
                forDocument = ImageLocation.getForSticker((PhotoSize) tag, document);
            } else {
                scrollX++;
            }
            BackupImageView backupImageView = (BackupImageView) ((FrameLayout) childAt).getChildAt(0);
            if (z && MessageObject.isAnimatedStickerDocument(document)) {
                backupImageView.setImage(ImageLocation.getForDocument(document), "30_30", forDocument, null, 0, tag2);
                scrollX++;
            } else {
                if (forDocument.lottieAnimation) {
                    backupImageView.setImage(forDocument, "30_30", "tgs", null, tag2);
                } else {
                    backupImageView.setImage(forDocument, null, "webp", null, tag2);
                }
                scrollX++;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        i2 = AndroidUtilities.dp(52.0f);
        i3 /= i2;
        i /= i2;
        i2 = ((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) i2)))) + 1;
        i4 = Math.max(0, Math.min(i3, i));
        i3 = Math.min(this.tabsContainer.getChildCount(), Math.max(i3, i) + i2);
        while (i4 < i3) {
            View childAt = this.tabsContainer.getChildAt(i4);
            if (childAt != null) {
                ImageLocation forDocument;
                Object tag = childAt.getTag();
                Object tag2 = childAt.getTag(NUM);
                Document document = (Document) childAt.getTag(NUM);
                boolean z = tag instanceof Document;
                if (z) {
                    forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
                } else if (tag instanceof PhotoSize) {
                    forDocument = ImageLocation.getForSticker((PhotoSize) tag, document);
                }
                BackupImageView backupImageView = (BackupImageView) ((FrameLayout) childAt).getChildAt(0);
                if (i4 < i || i4 >= i + i2) {
                    backupImageView.setImageDrawable(null);
                } else if (z && MessageObject.isAnimatedStickerDocument(document)) {
                    backupImageView.setImage(ImageLocation.getForDocument(document), "30_30", forDocument, null, 0, tag2);
                } else if (forDocument.lottieAnimation) {
                    backupImageView.setImage(forDocument, "30_30", "tgs", null, tag2);
                } else {
                    backupImageView.setImage(forDocument, null, "webp", null, tag2);
                }
            }
            i4++;
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
                int measuredWidth;
                View childAt = this.tabsContainer.getChildAt(this.currentPosition);
                float f = 0.0f;
                if (childAt != null) {
                    f = (float) childAt.getLeft();
                    measuredWidth = childAt.getMeasuredWidth();
                } else {
                    measuredWidth = 0;
                }
                if (this.animateFromPosition) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    long j = uptimeMillis - this.lastAnimationTime;
                    this.lastAnimationTime = uptimeMillis;
                    this.positionAnimationProgress += ((float) j) / 150.0f;
                    if (this.positionAnimationProgress >= 1.0f) {
                        this.positionAnimationProgress = 1.0f;
                        this.animateFromPosition = false;
                    }
                    float f2 = this.startAnimationPosition;
                    f = ((f - f2) * CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.positionAnimationProgress)) + f2;
                    invalidate();
                }
                float f3 = f;
                this.rectPaint.setColor(this.indicatorColor);
                int i = this.indicatorHeight;
                if (i == 0) {
                    canvas.drawRect(f3, 0.0f, f3 + ((float) measuredWidth), (float) height, this.rectPaint);
                    return;
                }
                canvas.drawRect(f3, (float) (height - i), f3 + ((float) measuredWidth), (float) height, this.rectPaint);
            }
        }
    }

    public void setShouldExpand(boolean z) {
        this.shouldExpand = z;
        requestLayout();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void cancelPositionAnimation() {
        this.animateFromPosition = false;
        this.positionAnimationProgress = 1.0f;
    }

    public void onPageScrolled(int i, int i2) {
        int i3 = this.currentPosition;
        if (i3 != i) {
            View childAt = this.tabsContainer.getChildAt(i3);
            if (childAt != null) {
                this.startAnimationPosition = (float) childAt.getLeft();
                this.positionAnimationProgress = 0.0f;
                this.animateFromPosition = true;
                this.lastAnimationTime = SystemClock.uptimeMillis();
            } else {
                this.animateFromPosition = false;
            }
            this.currentPosition = i;
            if (i < this.tabsContainer.getChildCount()) {
                this.positionAnimationProgress = 0.0f;
                i3 = 0;
                while (i3 < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(i3).setSelected(i3 == i);
                    i3++;
                }
                if (i2 != i || i <= 1) {
                    scrollToChild(i);
                } else {
                    scrollToChild(i - 1);
                }
                invalidate();
            }
        }
    }

    public void setIndicatorHeight(int i) {
        this.indicatorHeight = i;
        invalidate();
    }

    public void setIndicatorColor(int i) {
        this.indicatorColor = i;
        invalidate();
    }

    public void setUnderlineColor(int i) {
        this.underlineColor = i;
        invalidate();
    }

    public void setUnderlineColorResource(int i) {
        this.underlineColor = getResources().getColor(i);
        invalidate();
    }

    public void setUnderlineHeight(int i) {
        this.underlineHeight = i;
        invalidate();
    }
}
