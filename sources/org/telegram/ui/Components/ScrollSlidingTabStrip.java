package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private int currentPosition;
    private LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private int indicatorColor = -10066330;
    private int indicatorHeight;
    private int lastScrollX = 0;
    private Paint rectPaint;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(24.0f);
    private LinearLayout tabsContainer;
    private int underlineColor = 436207616;
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
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabCount = 0;
        this.currentPosition = 0;
    }

    public void selectTab(int i) {
        if (i >= 0) {
            if (i < this.tabCount) {
                this.tabsContainer.getChildAt(i).performClick();
            }
        }
    }

    public TextView addIconTabWithCounter(Drawable drawable) {
        final int i = this.tabCount;
        this.tabCount = i + 1;
        View frameLayout = new FrameLayout(getContext());
        frameLayout.setFocusable(true);
        this.tabsContainer.addView(frameLayout);
        View imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ScaleType.CENTER);
        frameLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
            }
        });
        frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.setSelected(i == this.currentPosition);
        View textView = new TextView(getContext());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 12.0f);
        textView.setTextColor(-1);
        textView.setGravity(17);
        textView.setBackgroundResource(C0446R.drawable.sticker_badge);
        textView.setMinWidth(AndroidUtilities.dp(18.0f));
        textView.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.0f));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
        return textView;
    }

    public void addIconTab(Drawable drawable) {
        final int i = this.tabCount;
        this.tabCount = i + 1;
        View imageView = new ImageView(getContext());
        boolean z = true;
        imageView.setFocusable(true);
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
            }
        });
        this.tabsContainer.addView(imageView);
        if (i != this.currentPosition) {
            z = false;
        }
        imageView.setSelected(z);
    }

    public void addStickerTab(Chat chat) {
        final int i = this.tabCount;
        this.tabCount = i + 1;
        View frameLayout = new FrameLayout(getContext());
        frameLayout.setFocusable(true);
        frameLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
            }
        });
        this.tabsContainer.addView(frameLayout);
        frameLayout.setSelected(i == this.currentPosition);
        View backupImageView = new BackupImageView(getContext());
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        TLObject tLObject = null;
        Drawable avatarDrawable = new AvatarDrawable();
        if (chat.photo != null) {
            tLObject = chat.photo.photo_small;
        }
        avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
        avatarDrawable.setInfo(chat);
        backupImageView.setImage(tLObject, "50_50", avatarDrawable);
        backupImageView.setAspectFit(true);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
    }

    public void addStickerTab(Document document) {
        final int i = this.tabCount;
        this.tabCount = i + 1;
        View frameLayout = new FrameLayout(getContext());
        frameLayout.setTag(document);
        frameLayout.setFocusable(true);
        frameLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
            }
        });
        this.tabsContainer.addView(frameLayout);
        frameLayout.setSelected(i == this.currentPosition);
        View backupImageView = new BackupImageView(getContext());
        backupImageView.setAspectFit(true);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            this.tabsContainer.getChildAt(i).setLayoutParams(this.defaultTabLayoutParams);
        }
    }

    private void scrollToChild(int i) {
        if (this.tabCount != 0) {
            if (this.tabsContainer.getChildAt(i) != null) {
                int left = this.tabsContainer.getChildAt(i).getLeft();
                if (i > 0) {
                    left -= this.scrollOffset;
                }
                i = getScrollX();
                if (left != this.lastScrollX) {
                    if (left < i) {
                        this.lastScrollX = left;
                        smoothScrollTo(this.lastScrollX, 0);
                    } else if (this.scrollOffset + left > (i + getWidth()) - (this.scrollOffset * 2)) {
                        this.lastScrollX = (left - getWidth()) + (this.scrollOffset * 3);
                        smoothScrollTo(this.lastScrollX, 0);
                    }
                }
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setImages();
    }

    public void setImages() {
        int dp = AndroidUtilities.dp(52.0f);
        int scrollX = getScrollX() / dp;
        dp = Math.min(this.tabsContainer.getChildCount(), (((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) dp)))) + scrollX) + 1);
        while (scrollX < dp) {
            View childAt = this.tabsContainer.getChildAt(scrollX);
            Object tag = childAt.getTag();
            if (tag instanceof Document) {
                ((BackupImageView) ((FrameLayout) childAt).getChildAt(0)).setImage(((Document) tag).thumb.location, null, "webp", null);
            }
            scrollX++;
        }
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        i2 = AndroidUtilities.dp(NUM);
        i3 /= i2;
        i /= i2;
        i2 = ((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) i2)))) + 1;
        i3 = Math.min(this.tabsContainer.getChildCount(), Math.max(i3, i) + i2);
        for (i4 = Math.max(0, Math.min(i3, i)); i4 < i3; i4++) {
            View childAt = this.tabsContainer.getChildAt(i4);
            if (childAt != null) {
                Object tag = childAt.getTag();
                if (tag instanceof Document) {
                    BackupImageView backupImageView = (BackupImageView) ((FrameLayout) childAt).getChildAt(0);
                    if (i4 >= i) {
                        if (i4 < i + i2) {
                            Document document = (Document) tag;
                            if (document.thumb != null) {
                                backupImageView.setImage(document.thumb.location, null, "webp", null);
                            }
                        }
                    }
                    backupImageView.setImageDrawable(null);
                }
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            if (this.tabCount != 0) {
                float right;
                float left;
                int height = getHeight();
                this.rectPaint.setColor(this.underlineColor);
                float f = (float) height;
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), f, this.rectPaint);
                View childAt = this.tabsContainer.getChildAt(this.currentPosition);
                if (childAt != null) {
                    right = (float) childAt.getRight();
                    left = (float) childAt.getLeft();
                } else {
                    left = 0.0f;
                    right = left;
                }
                this.rectPaint.setColor(this.indicatorColor);
                if (this.indicatorHeight == 0) {
                    canvas.drawRect(left, 0.0f, right, f, this.rectPaint);
                } else {
                    canvas.drawRect(left, (float) (height - this.indicatorHeight), right, f, this.rectPaint);
                }
            }
        }
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void onPageScrolled(int i, int i2) {
        if (this.currentPosition != i) {
            this.currentPosition = i;
            if (i < this.tabsContainer.getChildCount()) {
                int i3 = 0;
                while (true) {
                    boolean z = true;
                    if (i3 >= this.tabsContainer.getChildCount()) {
                        break;
                    }
                    View childAt = this.tabsContainer.getChildAt(i3);
                    if (i3 != i) {
                        z = false;
                    }
                    childAt.setSelected(z);
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
