package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.PremiumLockIconView;

public class EmojiTabsStrip extends ScrollableHorizontalScrollView {
    /* access modifiers changed from: private */
    public static int[] emojiTabsDrawableIds = {NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    /* access modifiers changed from: private */
    public LinearLayout contentView;
    /* access modifiers changed from: private */
    public EmojiTabsView emojiTabs;
    private ArrayList<EmojiTabButton> emojipackTabs;
    private boolean includeAnimated;
    private int recentDrawableId = NUM;
    private EmojiTabButton recentTab;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public float selectAnimationT = 0.0f;
    private ValueAnimator selectAnimator;
    /* access modifiers changed from: private */
    public float selectT = 0.0f;
    private int selected = 0;
    private int wasIndex = 0;

    /* access modifiers changed from: protected */
    public boolean onTabClick(int i) {
        throw null;
    }

    public EmojiTabsStrip(Context context, final Theme.ResourcesProvider resourcesProvider2, final boolean z) {
        super(context);
        this.includeAnimated = z;
        this.resourcesProvider = resourcesProvider2;
        AnonymousClass1 r1 = new LinearLayout(context) {
            private RectF from = new RectF();
            private Paint paint = new Paint(1);
            private Path path = new Path();
            private RectF rect = new RectF();
            private RectF to = new RectF();

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int paddingLeft = getPaddingLeft();
                int i5 = (i4 - i2) / 2;
                for (int i6 = 0; i6 < getChildCount(); i6++) {
                    View childAt = getChildAt(i6);
                    if (childAt != null) {
                        childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                        paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(99999999, Integer.MIN_VALUE);
                int paddingLeft = getPaddingLeft() + getPaddingRight();
                for (int i3 = 0; i3 < getChildCount(); i3++) {
                    View childAt = getChildAt(i3);
                    if (childAt != null) {
                        childAt.measure(makeMeasureSpec, i2);
                        paddingLeft += childAt.getMeasuredWidth() + (i3 + 1 < getChildCount() ? AndroidUtilities.dp(8.0f) : 0);
                    }
                }
                if (!z) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                } else {
                    setMeasuredDimension(paddingLeft, View.MeasureSpec.getSize(i2));
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                int floor = (int) Math.floor((double) EmojiTabsStrip.this.selectT);
                getChildBounds(floor, this.from);
                getChildBounds((int) Math.ceil((double) EmojiTabsStrip.this.selectT), this.to);
                AndroidUtilities.lerp(this.from, this.to, EmojiTabsStrip.this.selectT - ((float) floor), this.rect);
                float f = 0.0f;
                if (EmojiTabsStrip.this.emojiTabs != null) {
                    f = MathUtils.clamp(1.0f - Math.abs(EmojiTabsStrip.this.selectT - 1.0f), 0.0f, 1.0f);
                }
                float width = (this.rect.width() / 2.0f) * ((EmojiTabsStrip.this.selectAnimationT * 4.0f * (1.0f - EmojiTabsStrip.this.selectAnimationT) * 0.3f) + 1.0f);
                float height = this.rect.height() / 2.0f;
                RectF rectF = this.rect;
                rectF.set(rectF.centerX() - width, this.rect.centerY() - height, this.rect.centerX() + width, this.rect.centerY() + height);
                float dp = (float) AndroidUtilities.dp(AndroidUtilities.lerp(8.0f, 16.0f, f));
                int color = Theme.getColor("chat_emojiSearchBackground", resourcesProvider2);
                this.paint.setColor(ColorUtils.blendARGB(Theme.blendOver(color, Theme.isCurrentThemeDark() ? 83886079 : 67108864), color, f));
                this.path.rewind();
                this.path.addRoundRect(this.rect, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                EmojiTabsStrip.this.emojiTabs.draw(canvas, this.path);
                super.dispatchDraw(canvas);
            }

            private void getChildBounds(int i, RectF rectF) {
                View childAt = getChildAt(MathUtils.clamp(i, 0, getChildCount() - 1));
                rectF.set((float) childAt.getLeft(), (float) childAt.getTop(), (float) childAt.getRight(), (float) childAt.getBottom());
            }
        };
        this.contentView = r1;
        r1.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
        this.contentView.setOrientation(0);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        addView(this.contentView);
        LinearLayout linearLayout = this.contentView;
        EmojiTabButton emojiTabButton = new EmojiTabButton(context, this.recentDrawableId, false);
        this.recentTab = emojiTabButton;
        linearLayout.addView(emojiTabButton);
        if (!z) {
            int i = 0;
            while (true) {
                int[] iArr = emojiTabsDrawableIds;
                if (i < iArr.length) {
                    this.contentView.addView(new EmojiTabButton(context, iArr[i], false));
                    i++;
                } else {
                    updateClickListeners();
                    return;
                }
            }
        } else {
            LinearLayout linearLayout2 = this.contentView;
            EmojiTabsView emojiTabsView = new EmojiTabsView(context);
            this.emojiTabs = emojiTabsView;
            linearLayout2.addView(emojiTabsView);
            updateEmojiPacks();
        }
    }

    private boolean isFreeEmojiPack(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents == null) {
            return false;
        }
        for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
            if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i))) {
                return false;
            }
        }
        return true;
    }

    private TLRPC$Document getThumbDocument(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        if (tLRPC$TL_messages_stickerSet == null) {
            return null;
        }
        if (!(tLRPC$TL_messages_stickerSet.set == null || tLRPC$TL_messages_stickerSet.documents == null)) {
            for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i);
                if (tLRPC$Document.id == tLRPC$TL_messages_stickerSet.set.thumb_document_id) {
                    return tLRPC$Document;
                }
            }
        }
        ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
        if (arrayList == null || arrayList.size() < 1) {
            return null;
        }
        return tLRPC$TL_messages_stickerSet.documents.get(0);
    }

    public void updateEmojiPacks() {
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;
        if (this.includeAnimated && (stickerSets = MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSets(5)) != null) {
            if (this.emojipackTabs == null) {
                this.emojipackTabs = new ArrayList<>();
            }
            int i = 0;
            while (i < Math.max(this.emojipackTabs.size(), stickerSets.size())) {
                AnimatedEmojiDrawable animatedEmojiDrawable = null;
                EmojiTabButton emojiTabButton = i >= this.emojipackTabs.size() ? null : this.emojipackTabs.get(i);
                if (emojiTabButton != null) {
                    animatedEmojiDrawable = (AnimatedEmojiDrawable) emojiTabButton.getDrawable();
                }
                if (i >= stickerSets.size()) {
                    this.contentView.removeView(this.emojipackTabs.remove(i));
                    i--;
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets.get(i);
                    TLRPC$Document thumbDocument = getThumbDocument(tLRPC$TL_messages_stickerSet);
                    if (thumbDocument != null && (animatedEmojiDrawable == null || animatedEmojiDrawable.getDocumentId() != thumbDocument.id)) {
                        animatedEmojiDrawable = AnimatedEmojiDrawable.make(3, thumbDocument);
                    }
                    if (emojiTabButton == null) {
                        emojiTabButton = new EmojiTabButton(getContext(), animatedEmojiDrawable, isFreeEmojiPack(tLRPC$TL_messages_stickerSet), false);
                        if (animatedEmojiDrawable != null) {
                            animatedEmojiDrawable.addView((View) emojiTabButton.imageView);
                        }
                    } else {
                        if (emojiTabButton.getDrawable() instanceof AnimatedEmojiDrawable) {
                            ((AnimatedEmojiDrawable) emojiTabButton.getDrawable()).removeView((View) emojiTabButton.imageView);
                        }
                        emojiTabButton.setDrawable(animatedEmojiDrawable);
                        if (animatedEmojiDrawable != null) {
                            animatedEmojiDrawable.addView((View) emojiTabButton.imageView);
                        }
                    }
                    if (i >= this.emojipackTabs.size()) {
                        this.emojipackTabs.add(emojiTabButton);
                        this.contentView.addView(emojiTabButton);
                    } else {
                        this.emojipackTabs.set(i, emojiTabButton);
                    }
                }
                i++;
            }
            updateClickListeners();
        }
    }

    public void updateClickListeners() {
        int i = 0;
        int i2 = 0;
        while (i < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i3 = 0;
                while (i3 < emojiTabsView.contentView.getChildCount()) {
                    emojiTabsView.contentView.getChildAt(i3).setOnClickListener(new EmojiTabsStrip$$ExternalSyntheticLambda2(this, i2));
                    i3++;
                    i2++;
                }
                i2--;
            } else if (childAt != null) {
                childAt.setOnClickListener(new EmojiTabsStrip$$ExternalSyntheticLambda1(this, i2));
            }
            i++;
            i2++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateClickListeners$0(int i, View view) {
        if (onTabClick(i)) {
            select(i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateClickListeners$1(int i, View view) {
        if (onTabClick(i)) {
            select(i);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.contentView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
        super.onMeasure(i, i2);
    }

    public void updateColors() {
        EmojiTabButton emojiTabButton = this.recentTab;
        if (emojiTabButton != null) {
            emojiTabButton.updateColor();
        }
    }

    public void select(int i) {
        int i2;
        int i3 = this.selected;
        int i4 = 0;
        int i5 = 0;
        while (i4 < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i4);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i6 = i5;
                int i7 = 0;
                while (i7 < emojiTabsView.contentView.getChildCount()) {
                    View childAt2 = emojiTabsView.contentView.getChildAt(i7);
                    if (childAt2 instanceof EmojiTabButton) {
                        ((EmojiTabButton) childAt2).updateSelect(i == i6, true);
                    }
                    i7++;
                    i6++;
                }
                i2 = i6 - 1;
            } else {
                if (childAt instanceof EmojiTabButton) {
                    ((EmojiTabButton) childAt).updateSelect(i == i5, true);
                }
                i2 = i5;
            }
            if (i >= i5 && i <= i2) {
                this.selected = i4;
            }
            i4++;
            i5 = i2 + 1;
        }
        if (i3 != this.selected) {
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.selectAnimator = ofFloat;
            ofFloat.addUpdateListener(new EmojiTabsStrip$$ExternalSyntheticLambda0(this, this.selectT, (float) this.selected));
            this.selectAnimator.setDuration(350);
            this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.selectAnimator.start();
            EmojiTabsView emojiTabsView2 = this.emojiTabs;
            if (emojiTabsView2 != null) {
                emojiTabsView2.show(this.selected == 1);
            }
            View childAt3 = this.contentView.getChildAt(this.selected);
            if (this.selected >= 2) {
                scrollToVisible(childAt3.getLeft(), childAt3.getRight());
            } else {
                scrollTo(0);
            }
        }
        if (this.wasIndex != i) {
            EmojiTabsView emojiTabsView3 = this.emojiTabs;
            if (emojiTabsView3 != null && this.selected == 1 && i >= 1 && i <= emojiTabsView3.contentView.getChildCount() + 1) {
                int i8 = ((i - 1) * 36) - 6;
                this.emojiTabs.scrollToVisible(AndroidUtilities.dp((float) i8), AndroidUtilities.dp((float) (i8 + 30)));
            }
            this.wasIndex = i;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$select$2(float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.selectAnimationT = floatValue;
        this.selectT = AndroidUtilities.lerp(f, f2, floatValue);
        this.contentView.invalidate();
    }

    private class EmojiTabButton extends ViewGroup {
        /* access modifiers changed from: private */
        public ImageView imageView;
        private PremiumLockIconView lockView;
        /* access modifiers changed from: private */
        public boolean round;
        private ValueAnimator selectAnimator;
        private float selectT;
        private boolean selected;

        public EmojiTabButton(Context context, int i, boolean z) {
            super(context);
            this.round = z;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(Theme.getColor("listSelectorSDK21", EmojiTabsStrip.this.resourcesProvider), 0, 0));
            }
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageDrawable(context.getResources().getDrawable(i).mutate());
            setColor(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider));
            addView(this.imageView);
        }

        public EmojiTabButton(Context context, Drawable drawable, boolean z, boolean z2) {
            super(context);
            this.round = z2;
            if (z2) {
                setBackground(Theme.createCircleSelectorDrawable(Theme.getColor("listSelectorSDK21", EmojiTabsStrip.this.resourcesProvider), 0, 0));
            }
            AnonymousClass1 r6 = new ImageView(context, EmojiTabsStrip.this) {
                public void invalidate() {
                    super.invalidate();
                    EmojiTabButton.this.updateLockImageReceiver();
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    if (getDrawable() != null) {
                        getDrawable().setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        getDrawable().setAlpha(255);
                        getDrawable().draw(canvas);
                    }
                }
            };
            this.imageView = r6;
            r6.setImageDrawable(drawable);
            addView(this.imageView);
            if (!z && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                this.lockView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
                updateLockImageReceiver();
                this.lockView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                updateLockImageReceiver();
                addView(this.lockView);
            }
        }

        public void updateLockImageReceiver() {
            ImageReceiver imageReceiver;
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null && !premiumLockIconView.ready() && (getDrawable() instanceof AnimatedEmojiDrawable) && (imageReceiver = ((AnimatedEmojiDrawable) getDrawable()).getImageReceiver()) != null) {
                this.lockView.setImageReceiver(imageReceiver);
                this.lockView.invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                imageView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                premiumLockIconView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM));
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                int i5 = (i3 - i) / 2;
                int i6 = (i4 - i2) / 2;
                imageView2.layout(i5 - (imageView2.getMeasuredWidth() / 2), i6 - (this.imageView.getMeasuredHeight() / 2), i5 + (this.imageView.getMeasuredWidth() / 2), i6 + (this.imageView.getMeasuredHeight() / 2));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                int i7 = i3 - i;
                int i8 = i4 - i2;
                premiumLockIconView.layout(i7 - AndroidUtilities.dp(12.0f), i8 - AndroidUtilities.dp(12.0f), i7, i8);
            }
        }

        public Drawable getDrawable() {
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                return imageView2.getDrawable();
            }
            return null;
        }

        public void setDrawable(Drawable drawable) {
            ImageReceiver imageReceiver;
            if (!(this.lockView == null || !(drawable instanceof AnimatedEmojiDrawable) || (imageReceiver = ((AnimatedEmojiDrawable) drawable).getImageReceiver()) == null)) {
                this.lockView.setImageReceiver(imageReceiver);
            }
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                imageView2.setImageDrawable(drawable);
            }
        }

        public void updateSelect(final boolean z, boolean z2) {
            ImageView imageView2 = this.imageView;
            if ((imageView2 == null || imageView2.getDrawable() != null) && this.selected != z) {
                this.selected = z;
                ValueAnimator valueAnimator = this.selectAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.selectAnimator = null;
                }
                float f = 1.0f;
                if (z2) {
                    float[] fArr = new float[2];
                    fArr[0] = this.selectT;
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.selectAnimator = ofFloat;
                    ofFloat.addUpdateListener(new EmojiTabsStrip$EmojiTabButton$$ExternalSyntheticLambda0(this));
                    this.selectAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (EmojiTabButton.this.round) {
                                return;
                            }
                            if (!z) {
                                EmojiTabButton.this.setBackground((Drawable) null);
                            } else if (EmojiTabButton.this.getBackground() == null) {
                                EmojiTabButton emojiTabButton = EmojiTabButton.this;
                                emojiTabButton.setBackground(Theme.createRadSelectorDrawable(Theme.getColor("listSelectorSDK21", EmojiTabsStrip.this.resourcesProvider), 8, 8));
                            }
                        }
                    });
                    this.selectAnimator.setDuration(350);
                    this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.selectAnimator.start();
                    return;
                }
                if (!z) {
                    f = 0.0f;
                }
                this.selectT = f;
                updateColor();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSelect$0(ValueAnimator valueAnimator) {
            this.selectT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        public void updateColor() {
            Theme.setSelectorDrawableColor(getBackground(), Theme.getColor("listSelectorSDK21", EmojiTabsStrip.this.resourcesProvider), false);
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        private void setColor(int i) {
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                imageView2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                this.imageView.invalidate();
            }
        }

        public boolean callOnClick() {
            return super.callOnClick();
        }
    }

    private class EmojiTabsView extends ScrollableHorizontalScrollView {
        private Path circlePath;
        private float circlePathR;
        /* access modifiers changed from: private */
        public LinearLayout contentView;
        private boolean scrollingAnimation = false;
        private ValueAnimator showAnimator;
        private float showT = 0.0f;
        private boolean shown = false;
        private boolean touching = false;

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
        }

        public EmojiTabsView(Context context) {
            super(context);
            setSmoothScrollingEnabled(true);
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
            if (Build.VERSION.SDK_INT >= 21) {
                setNestedScrollingEnabled(true);
            }
            AnonymousClass1 r1 = new LinearLayout(context, EmojiTabsStrip.this) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int paddingLeft = getPaddingLeft();
                    int i5 = (i4 - i2) / 2;
                    for (int i6 = 0; i6 < getChildCount(); i6++) {
                        View childAt = getChildAt(i6);
                        if (childAt != null) {
                            childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(6.0f);
                        }
                    }
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((EmojiTabsView.this.contentView.getChildCount() * 36) - 6)), NUM), i2);
                }
            };
            this.contentView = r1;
            r1.setOrientation(0);
            addView(this.contentView, new FrameLayout.LayoutParams(-2, -1));
            for (int r5 : EmojiTabsStrip.emojiTabsDrawableIds) {
                this.contentView.addView(new EmojiTabButton(context, r5, true, EmojiTabsStrip.this) {
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        EmojiTabsView.this.intercept(motionEvent);
                        return super.onTouchEvent(motionEvent);
                    }
                });
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view == getChildAt(0)) {
                return false;
            }
            return super.drawChild(canvas, view, j);
        }

        public void draw(Canvas canvas, Path path) {
            LinearLayout linearLayout = this.contentView;
            if (linearLayout != null) {
                View childAt = linearLayout.getChildAt(0);
                if (childAt != null) {
                    canvas.save();
                    canvas.translate((float) (getLeft() + this.contentView.getLeft() + childAt.getLeft()), (float) (getTop() + this.contentView.getTop() + childAt.getTop()));
                    if (this.circlePath == null || this.circlePathR != ((float) AndroidUtilities.dp(15.0f))) {
                        Path path2 = this.circlePath;
                        if (path2 == null) {
                            this.circlePath = new Path();
                        } else {
                            path2.rewind();
                        }
                        Path path3 = this.circlePath;
                        float dp = (float) AndroidUtilities.dp(15.0f);
                        this.circlePathR = dp;
                        path3.addCircle(dp, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), Path.Direction.CW);
                    }
                    canvas.clipPath(this.circlePath);
                    canvas.translate((float) (-getScrollX()), 0.0f);
                    childAt.setAlpha(1.0f - this.showT);
                    childAt.draw(canvas);
                    canvas.restore();
                }
                if (childAt != null) {
                    childAt.setAlpha(0.0f);
                }
                if (this.showT > 0.0f) {
                    canvas.save();
                    canvas.clipPath(path);
                    canvas.translate((float) (getLeft() + this.contentView.getLeft()), (float) (getTop() + this.contentView.getTop()));
                    canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (this.showT * 255.0f), 31);
                    canvas.translate((float) (-getScrollX()), 0.0f);
                    this.contentView.setAlpha(this.showT);
                    this.contentView.draw(canvas);
                    canvas.restore();
                    canvas.restore();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(AndroidUtilities.dp(30.0f), maxWidth(), this.showT), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        }

        public int maxWidth() {
            return AndroidUtilities.dp((Math.min(4.7f, (float) this.contentView.getChildCount()) * 36.0f) - 6.0f);
        }

        /* access modifiers changed from: protected */
        public void onScrollChanged(int i, int i2, int i3, int i4) {
            super.onScrollChanged(i, i2, i3, i4);
            if ((Math.abs(i2 - i4) < 2 || i2 >= getMeasuredHeight() || i2 == 0) && !this.touching) {
                EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(false);
            }
        }

        /* access modifiers changed from: private */
        public void intercept(MotionEvent motionEvent) {
            if (this.shown && !this.scrollingAnimation) {
                int action = motionEvent.getAction();
                if (action != 0) {
                    if (action == 1) {
                        this.touching = false;
                        return;
                    } else if (action != 2) {
                        return;
                    }
                }
                this.touching = true;
                if (!this.scrollingAnimation) {
                    resetScrollTo();
                }
                EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(true);
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            intercept(motionEvent);
            return super.onTouchEvent(motionEvent);
        }

        public void show(boolean z) {
            if (z != this.shown) {
                this.shown = z;
                if (!z) {
                    scrollTo(0);
                }
                ValueAnimator valueAnimator = this.showAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.showT;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.showAnimator = ofFloat;
                ofFloat.addUpdateListener(new EmojiTabsStrip$EmojiTabsView$$ExternalSyntheticLambda0(this));
                this.showAnimator.setDuration(475);
                this.showAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.showAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$show$0(ValueAnimator valueAnimator) {
            this.showT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
            requestLayout();
            EmojiTabsStrip.this.contentView.invalidate();
        }
    }
}
