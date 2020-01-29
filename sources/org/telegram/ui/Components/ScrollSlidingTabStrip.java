package org.telegram.ui.Components;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private boolean animateFromPosition;
    private int currentPosition;
    private LinearLayout.LayoutParams defaultExpandLayoutParams;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private SparseArray<View> futureTabsPositions = new SparseArray<>();
    private int indicatorColor = -10066330;
    private int indicatorHeight;
    private long lastAnimationTime;
    private int lastScrollX = 0;
    private LayoutTransition layoutTransition;
    private float positionAnimationProgress;
    private HashMap<String, View> prevTypes = new HashMap<>();
    private Paint rectPaint;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private boolean shouldExpand;
    private float startAnimationPosition;
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(24.0f);
    private HashMap<String, View> tabTypes = new HashMap<>();
    /* access modifiers changed from: private */
    public LinearLayout tabsContainer;
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
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(52.0f), -1);
        this.defaultExpandLayoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
        this.layoutTransition = new LayoutTransition();
        this.layoutTransition.setAnimateParentHierarchy(false);
        this.layoutTransition.setDuration(250);
        this.layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            private boolean inTransition;

            public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            }

            public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                if (!this.inTransition) {
                    this.inTransition = true;
                    ScrollSlidingTabStrip.this.tabsContainer.setLayoutTransition((LayoutTransition) null);
                    this.inTransition = false;
                }
            }
        });
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabTypes.clear();
        this.prevTypes.clear();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        this.currentPosition = 0;
        this.animateFromPosition = false;
    }

    public void beginUpdate(boolean z) {
        this.prevTypes = this.tabTypes;
        this.tabTypes = new HashMap<>();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        if (z) {
            this.tabsContainer.setLayoutTransition(this.layoutTransition);
        }
    }

    public void commitUpdate() {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            for (Map.Entry<String, View> value : hashMap.entrySet()) {
                this.tabsContainer.removeView((View) value.getValue());
            }
            this.prevTypes.clear();
        }
        int size = this.futureTabsPositions.size();
        for (int i = 0; i < size; i++) {
            int keyAt = this.futureTabsPositions.keyAt(i);
            View valueAt = this.futureTabsPositions.valueAt(i);
            if (this.tabsContainer.indexOfChild(valueAt) != keyAt) {
                this.tabsContainer.removeView(valueAt);
                this.tabsContainer.addView(valueAt, keyAt);
            }
        }
        this.futureTabsPositions.clear();
    }

    public void selectTab(int i) {
        if (i >= 0 && i < this.tabCount) {
            this.tabsContainer.getChildAt(i).performClick();
        }
    }

    private void checkViewIndex(String str, View view, int i) {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            hashMap.remove(str);
        }
        this.futureTabsPositions.put(i, view);
    }

    public TextView addIconTabWithCounter(int i, Drawable drawable) {
        TextView textView;
        String str = "textTab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        FrameLayout frameLayout = (FrameLayout) this.prevTypes.get(str);
        boolean z = false;
        if (frameLayout != null) {
            textView = (TextView) frameLayout.getChildAt(1);
            checkViewIndex(str, frameLayout, i2);
        } else {
            frameLayout = new FrameLayout(getContext());
            frameLayout.setFocusable(true);
            this.tabsContainer.addView(frameLayout, i2);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addIconTabWithCounter$0$ScrollSlidingTabStrip(view);
                }
            });
            frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            textView = new TextView(getContext());
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setTextSize(1, 12.0f);
            textView.setTextColor(Theme.getColor("chat_emojiPanelBadgeText"));
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(9.0f), Theme.getColor("chat_emojiPanelBadgeBackground")));
            textView.setMinWidth(AndroidUtilities.dp(18.0f));
            textView.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.0f));
            frameLayout.addView(textView, LayoutHelper.createFrame(-2, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
        }
        frameLayout.setTag(NUM, Integer.valueOf(i2));
        if (i2 == this.currentPosition) {
            z = true;
        }
        frameLayout.setSelected(z);
        this.tabTypes.put(str, frameLayout);
        return textView;
    }

    public /* synthetic */ void lambda$addIconTabWithCounter$0$ScrollSlidingTabStrip(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public ImageView addIconTab(int i, Drawable drawable) {
        String str = "tab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        ImageView imageView = (ImageView) this.prevTypes.get(str);
        boolean z = true;
        if (imageView != null) {
            checkViewIndex(str, imageView, i2);
        } else {
            imageView = new ImageView(getContext());
            imageView.setFocusable(true);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addIconTab$1$ScrollSlidingTabStrip(view);
                }
            });
            this.tabsContainer.addView(imageView, i2);
        }
        imageView.setTag(NUM, Integer.valueOf(i2));
        if (i2 != this.currentPosition) {
            z = false;
        }
        imageView.setSelected(z);
        this.tabTypes.put(str, imageView);
        return imageView;
    }

    public /* synthetic */ void lambda$addIconTab$1$ScrollSlidingTabStrip(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public void addStickerTab(TLRPC.Chat chat) {
        String str = "chat" + chat.id;
        int i = this.tabCount;
        this.tabCount = i + 1;
        FrameLayout frameLayout = (FrameLayout) this.prevTypes.get(str);
        boolean z = false;
        if (frameLayout != null) {
            checkViewIndex(str, frameLayout, i);
        } else {
            frameLayout = new FrameLayout(getContext());
            frameLayout.setFocusable(true);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addStickerTab$2$ScrollSlidingTabStrip(view);
                }
            });
            this.tabsContainer.addView(frameLayout, i);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            avatarDrawable.setInfo(chat);
            BackupImageView backupImageView = new BackupImageView(getContext());
            backupImageView.setLayerNum(1);
            backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
            backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) avatarDrawable, (Object) chat);
            backupImageView.setAspectFit(true);
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
        }
        frameLayout.setTag(NUM, Integer.valueOf(i));
        if (i == this.currentPosition) {
            z = true;
        }
        frameLayout.setSelected(z);
        this.tabTypes.put(str, frameLayout);
    }

    public /* synthetic */ void lambda$addStickerTab$2$ScrollSlidingTabStrip(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public View addStickerTab(TLObject tLObject, TLRPC.Document document, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        String str = "set" + tL_messages_stickerSet.set.id;
        int i = this.tabCount;
        this.tabCount = i + 1;
        FrameLayout frameLayout = (FrameLayout) this.prevTypes.get(str);
        boolean z = true;
        if (frameLayout != null) {
            checkViewIndex(str, frameLayout, i);
        } else {
            frameLayout = new FrameLayout(getContext());
            frameLayout.setFocusable(true);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.lambda$addStickerTab$3$ScrollSlidingTabStrip(view);
                }
            });
            this.tabsContainer.addView(frameLayout, i);
            BackupImageView backupImageView = new BackupImageView(getContext());
            backupImageView.setLayerNum(1);
            backupImageView.setAspectFit(true);
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
        }
        frameLayout.setTag(tLObject);
        frameLayout.setTag(NUM, Integer.valueOf(i));
        frameLayout.setTag(NUM, tL_messages_stickerSet);
        frameLayout.setTag(NUM, document);
        if (i != this.currentPosition) {
            z = false;
        }
        frameLayout.setSelected(z);
        this.tabTypes.put(str, frameLayout);
        return frameLayout;
    }

    public /* synthetic */ void lambda$addStickerTab$3$ScrollSlidingTabStrip(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
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
            int scrollX = getScrollX();
            if (left == this.lastScrollX) {
                return;
            }
            if (left < scrollX) {
                this.lastScrollX = left;
                smoothScrollTo(this.lastScrollX, 0);
            } else if (this.scrollOffset + left > (scrollX + getWidth()) - (this.scrollOffset * 2)) {
                this.lastScrollX = (left - getWidth()) + (this.scrollOffset * 3);
                smoothScrollTo(this.lastScrollX, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setImages();
    }

    public void setImages() {
        ImageLocation imageLocation;
        int dp = AndroidUtilities.dp(52.0f);
        int scrollX = getScrollX() / dp;
        int min = Math.min(this.tabsContainer.getChildCount(), ((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) dp)))) + scrollX + 1);
        while (scrollX < min) {
            View childAt = this.tabsContainer.getChildAt(scrollX);
            Object tag = childAt.getTag();
            Object tag2 = childAt.getTag(NUM);
            TLRPC.Document document = (TLRPC.Document) childAt.getTag(NUM);
            boolean z = tag instanceof TLRPC.Document;
            if (z) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
            } else if (tag instanceof TLRPC.PhotoSize) {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) tag, document);
            } else {
                scrollX++;
            }
            if (imageLocation != null) {
                BackupImageView backupImageView = (BackupImageView) ((FrameLayout) childAt).getChildAt(0);
                if (z && MessageObject.isAnimatedStickerDocument(document, true)) {
                    backupImageView.setImage(ImageLocation.getForDocument(document), "30_30", imageLocation, (String) null, 0, tag2);
                } else if (imageLocation.imageType == 1) {
                    backupImageView.setImage(imageLocation, "30_30", "tgs", (Drawable) null, tag2);
                } else {
                    backupImageView.setImage(imageLocation, (String) null, "webp", (Drawable) null, tag2);
                }
            }
            scrollX++;
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        ImageLocation imageLocation;
        super.onScrollChanged(i, i2, i3, i4);
        int dp = AndroidUtilities.dp(52.0f);
        int i5 = i3 / dp;
        int i6 = i / dp;
        int ceil = ((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) dp)))) + 1;
        int min = Math.min(this.tabsContainer.getChildCount(), Math.max(i5, i6) + ceil);
        for (int max = Math.max(0, Math.min(i5, i6)); max < min; max++) {
            View childAt = this.tabsContainer.getChildAt(max);
            if (childAt != null) {
                Object tag = childAt.getTag();
                Object tag2 = childAt.getTag(NUM);
                TLRPC.Document document = (TLRPC.Document) childAt.getTag(NUM);
                boolean z = tag instanceof TLRPC.Document;
                if (z) {
                    imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
                } else if (tag instanceof TLRPC.PhotoSize) {
                    imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) tag, document);
                }
                if (imageLocation != null) {
                    BackupImageView backupImageView = (BackupImageView) ((FrameLayout) childAt).getChildAt(0);
                    if (max < i6 || max >= i6 + ceil) {
                        backupImageView.setImageDrawable((Drawable) null);
                    } else if (z && MessageObject.isAnimatedStickerDocument(document, true)) {
                        backupImageView.setImage(ImageLocation.getForDocument(document), "30_30", imageLocation, (String) null, 0, tag2);
                    } else if (imageLocation.imageType == 1) {
                        backupImageView.setImage(imageLocation, "30_30", "tgs", (Drawable) null, tag2);
                    } else {
                        backupImageView.setImage(imageLocation, (String) null, "webp", (Drawable) null, tag2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            if (this.underlineHeight > 0) {
                this.rectPaint.setColor(this.underlineColor);
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            }
            if (this.indicatorHeight >= 0) {
                View childAt = this.tabsContainer.getChildAt(this.currentPosition);
                float f = 0.0f;
                if (childAt != null) {
                    f = (float) childAt.getLeft();
                    i = childAt.getMeasuredWidth();
                } else {
                    i = 0;
                }
                if (this.animateFromPosition) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long j = elapsedRealtime - this.lastAnimationTime;
                    this.lastAnimationTime = elapsedRealtime;
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
                int i2 = this.indicatorHeight;
                if (i2 == 0) {
                    canvas.drawRect(f3, 0.0f, f3 + ((float) i), (float) height, this.rectPaint);
                    return;
                }
                canvas.drawRect(f3, (float) (height - i2), f3 + ((float) i), (float) height, this.rectPaint);
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
                this.lastAnimationTime = SystemClock.elapsedRealtime();
            } else {
                this.animateFromPosition = false;
            }
            this.currentPosition = i;
            if (i < this.tabsContainer.getChildCount()) {
                this.positionAnimationProgress = 0.0f;
                int i4 = 0;
                while (i4 < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(i4).setSelected(i4 == i);
                    i4++;
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
