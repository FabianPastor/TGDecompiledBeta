package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.Components.ScrollSlidingTabStrip;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private boolean animateFromPosition;
    private int currentPosition;
    private LinearLayout.LayoutParams defaultExpandLayoutParams;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private SparseArray<View> futureTabsPositions = new SparseArray<>();
    private int indicatorColor = -10066330;
    private GradientDrawable indicatorDrawable = new GradientDrawable();
    private int indicatorHeight;
    private long lastAnimationTime;
    private int lastScrollX;
    private float positionAnimationProgress;
    private HashMap<String, View> prevTypes = new HashMap<>();
    private Paint rectPaint;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private boolean shouldExpand;
    private float startAnimationPosition;
    private int tabCount;
    private HashMap<String, View> tabTypes = new HashMap<>();
    private LinearLayout tabsContainer;
    private Type type = Type.LINE;
    private int underlineColor = NUM;
    private int underlineHeight = AndroidUtilities.dp(2.0f);

    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    public enum Type {
        LINE,
        TAB
    }

    public ScrollSlidingTabStrip(Context context) {
        super(context);
        AndroidUtilities.dp(12.0f);
        AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(52.0f), -1);
        this.defaultExpandLayoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type2) {
        if (type2 != null && this.type != type2) {
            this.type = type2;
            int i = AnonymousClass2.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[type2.ordinal()];
            if (i == 1) {
                this.indicatorDrawable.setCornerRadius(0.0f);
            } else if (i == 2) {
                float dpf2 = AndroidUtilities.dpf2(3.0f);
                this.indicatorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                org.telegram.ui.Components.ScrollSlidingTabStrip$Type[] r0 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type = r0
                org.telegram.ui.Components.ScrollSlidingTabStrip$Type r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.LINE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type     // Catch:{ NoSuchFieldError -> 0x001d }
                org.telegram.ui.Components.ScrollSlidingTabStrip$Type r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.AnonymousClass2.<clinit>():void");
        }
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
        if (z && Build.VERSION.SDK_INT >= 19) {
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(250);
            autoTransition.setOrdering(0);
            autoTransition.addTransition(new Transition() {
                public void captureEndValues(TransitionValues transitionValues) {
                }

                public void captureStartValues(TransitionValues transitionValues) {
                }

                public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ScrollSlidingTabStrip.AnonymousClass1.this.lambda$createAnimator$0$ScrollSlidingTabStrip$1(valueAnimator);
                        }
                    });
                    return ofFloat;
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$createAnimator$0 */
                public /* synthetic */ void lambda$createAnimator$0$ScrollSlidingTabStrip$1(ValueAnimator valueAnimator) {
                    ScrollSlidingTabStrip.this.invalidate();
                }
            });
            TransitionManager.beginDelayedTransition(this.tabsContainer, autoTransition);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$addIconTab$1 */
    public /* synthetic */ void lambda$addIconTab$1$ScrollSlidingTabStrip(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public void addStickerTab(TLRPC$Chat tLRPC$Chat) {
        String str = "chat" + tLRPC$Chat.id;
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
                    ScrollSlidingTabStrip.this.lambda$addStickerTab$2$ScrollSlidingTabStrip(view);
                }
            });
            this.tabsContainer.addView(frameLayout, i);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            avatarDrawable.setInfo(tLRPC$Chat);
            BackupImageView backupImageView = new BackupImageView(getContext());
            backupImageView.setLayerNum(1);
            backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
            backupImageView.setForUserOrChat(tLRPC$Chat, avatarDrawable);
            backupImageView.setAspectFit(true);
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
        }
        frameLayout.setTag(NUM, Integer.valueOf(i));
        if (i != this.currentPosition) {
            z = false;
        }
        frameLayout.setSelected(z);
        this.tabTypes.put(str, frameLayout);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addStickerTab$2 */
    public /* synthetic */ void lambda$addStickerTab$2$ScrollSlidingTabStrip(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public View addStickerTab(TLObject tLObject, SvgHelper.SvgDrawable svgDrawable, TLRPC$Document tLRPC$Document, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        String str = "set" + tLRPC$TL_messages_stickerSet.set.id;
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
        frameLayout.setTag(NUM, tLRPC$TL_messages_stickerSet);
        frameLayout.setTag(NUM, tLRPC$Document);
        frameLayout.setTag(NUM, svgDrawable);
        if (i != this.currentPosition) {
            z = false;
        }
        frameLayout.setSelected(z);
        this.tabTypes.put(str, frameLayout);
        return frameLayout;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addStickerTab$3 */
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
                smoothScrollTo(left, 0);
            } else if (this.scrollOffset + left > (scrollX + getWidth()) - (this.scrollOffset * 2)) {
                int width = (left - getWidth()) + (this.scrollOffset * 3);
                this.lastScrollX = width;
                smoothScrollTo(width, 0);
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
            SvgHelper.SvgDrawable svgDrawable = (SvgHelper.SvgDrawable) childAt.getTag(NUM);
            TLRPC$Document tLRPC$Document = (TLRPC$Document) childAt.getTag(NUM);
            boolean z = tag instanceof TLRPC$Document;
            if (z) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else if (tag instanceof TLRPC$PhotoSize) {
                imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) tag, tLRPC$Document);
            } else {
                scrollX++;
            }
            if (imageLocation != null) {
                BackupImageView backupImageView = (BackupImageView) ((FrameLayout) childAt).getChildAt(0);
                if (!z || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                    if (imageLocation.imageType == 1) {
                        backupImageView.setImage(imageLocation, "30_30", "tgs", (Drawable) svgDrawable, tag2);
                    } else {
                        backupImageView.setImage(imageLocation, (String) null, "webp", (Drawable) svgDrawable, tag2);
                    }
                } else if (svgDrawable != null) {
                    backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", (Drawable) svgDrawable, 0, tag2);
                } else {
                    backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", imageLocation, (String) null, 0, tag2);
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
                TLRPC$Document tLRPC$Document = (TLRPC$Document) childAt.getTag(NUM);
                boolean z = tag instanceof TLRPC$Document;
                if (z) {
                    imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
                } else if (tag instanceof TLRPC$PhotoSize) {
                    imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) tag, tLRPC$Document);
                }
                if (imageLocation != null) {
                    BackupImageView backupImageView = (BackupImageView) ((FrameLayout) childAt).getChildAt(0);
                    if (max < i6 || max >= i6 + ceil) {
                        backupImageView.setImageDrawable((Drawable) null);
                    } else if (z && MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                        backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", imageLocation, (String) null, 0, tag2);
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
                    float f2 = this.positionAnimationProgress + (((float) j) / 150.0f);
                    this.positionAnimationProgress = f2;
                    if (f2 >= 1.0f) {
                        this.positionAnimationProgress = 1.0f;
                        this.animateFromPosition = false;
                    }
                    float f3 = this.startAnimationPosition;
                    f = ((f - f3) * CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.positionAnimationProgress)) + f3;
                    invalidate();
                }
                int i2 = AnonymousClass2.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[this.type.ordinal()];
                if (i2 == 1) {
                    int i3 = this.indicatorHeight;
                    if (i3 == 0) {
                        int i4 = (int) f;
                        this.indicatorDrawable.setBounds(i4, 0, i + i4, height);
                    } else {
                        int i5 = (int) f;
                        this.indicatorDrawable.setBounds(i5, height - i3, i + i5, height);
                    }
                } else if (i2 == 2) {
                    int i6 = (int) f;
                    this.indicatorDrawable.setBounds(AndroidUtilities.dp(6.0f) + i6, height - AndroidUtilities.dp(3.0f), (i6 + i) - AndroidUtilities.dp(6.0f), height);
                }
                this.indicatorDrawable.setColor(this.indicatorColor);
                this.indicatorDrawable.draw(canvas);
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

    public void invalidateTabs() {
        int childCount = this.tabsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.tabsContainer.getChildAt(i).invalidate();
        }
    }

    public void setCurrentPosition(int i) {
        this.currentPosition = i;
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
