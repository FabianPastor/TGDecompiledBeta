package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class PremiumStickersPreviewRecycler extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate, PagerHeaderView {
    boolean autoPlayEnabled;
    Runnable autoScrollRunnable = new Runnable() {
        public void run() {
            View findViewByPosition;
            PremiumStickersPreviewRecycler premiumStickersPreviewRecycler = PremiumStickersPreviewRecycler.this;
            if (premiumStickersPreviewRecycler.autoPlayEnabled) {
                if (!premiumStickersPreviewRecycler.sortedView.isEmpty()) {
                    ArrayList<StickerView> arrayList = PremiumStickersPreviewRecycler.this.sortedView;
                    int childAdapterPosition = PremiumStickersPreviewRecycler.this.getChildAdapterPosition(arrayList.get(arrayList.size() - 1));
                    if (childAdapterPosition >= 0 && (findViewByPosition = PremiumStickersPreviewRecycler.this.layoutManager.findViewByPosition(childAdapterPosition + 1)) != null) {
                        PremiumStickersPreviewRecycler premiumStickersPreviewRecycler2 = PremiumStickersPreviewRecycler.this;
                        premiumStickersPreviewRecycler2.haptic = false;
                        premiumStickersPreviewRecycler2.drawEffectForView(findViewByPosition, true);
                        PremiumStickersPreviewRecycler.this.smoothScrollBy(0, findViewByPosition.getTop() - ((PremiumStickersPreviewRecycler.this.getMeasuredHeight() - findViewByPosition.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
                    }
                }
                PremiumStickersPreviewRecycler.this.scheduleAutoScroll();
            }
        }
    };
    private boolean checkEffect;
    Comparator<StickerView> comparator = PremiumStickersPreviewRecycler$$ExternalSyntheticLambda1.INSTANCE;
    private final int currentAccount;
    boolean firstDraw = true;
    boolean firstMeasure = true;
    boolean haptic;
    boolean hasSelectedView;
    CubicBezierInterpolator interpolator = new CubicBezierInterpolator(0.0f, 0.5f, 0.5f, 1.0f);
    LinearLayoutManager layoutManager;
    View oldSelectedView;
    /* access modifiers changed from: private */
    public final ArrayList<TLRPC$Document> premiumStickers = new ArrayList<>();
    ArrayList<StickerView> sortedView = new ArrayList<>();

    public boolean drawChild(Canvas canvas, View view, long j) {
        return true;
    }

    public void setOffset(float f) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$new$0(StickerView stickerView, StickerView stickerView2) {
        return (int) ((stickerView.progress * 100.0f) - (stickerView2.progress * 100.0f));
    }

    public PremiumStickersPreviewRecycler(Context context, int i) {
        super(context);
        this.currentAccount = i;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        setLayoutManager(linearLayoutManager);
        setAdapter(new Adapter());
        setClipChildren(false);
        setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                if (recyclerView.getScrollState() == 1) {
                    PremiumStickersPreviewRecycler.this.drawEffectForView((View) null, true);
                }
                PremiumStickersPreviewRecycler.this.invalidate();
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 1) {
                    PremiumStickersPreviewRecycler.this.haptic = true;
                }
                if (i == 0) {
                    StickerView stickerView = null;
                    for (int i2 = 0; i2 < recyclerView.getChildCount(); i2++) {
                        StickerView stickerView2 = (StickerView) PremiumStickersPreviewRecycler.this.getChildAt(i2);
                        if (stickerView == null || stickerView2.progress > stickerView.progress) {
                            stickerView = stickerView2;
                        }
                    }
                    if (stickerView != null) {
                        PremiumStickersPreviewRecycler.this.drawEffectForView(stickerView, true);
                        PremiumStickersPreviewRecycler premiumStickersPreviewRecycler = PremiumStickersPreviewRecycler.this;
                        premiumStickersPreviewRecycler.haptic = false;
                        premiumStickersPreviewRecycler.smoothScrollBy(0, stickerView.getTop() - ((PremiumStickersPreviewRecycler.this.getMeasuredHeight() - stickerView.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
                    }
                    PremiumStickersPreviewRecycler.this.scheduleAutoScroll();
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(PremiumStickersPreviewRecycler.this.autoScrollRunnable);
            }
        });
        setOnItemClickListener((RecyclerListView.OnItemClickListener) new PremiumStickersPreviewRecycler$$ExternalSyntheticLambda2(this));
        MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
        setStickers();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view, int i) {
        if (view != null) {
            drawEffectForView(view, true);
            this.haptic = false;
            smoothScrollBy(0, view.getTop() - ((getMeasuredHeight() - view.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
        }
    }

    /* access modifiers changed from: private */
    public void scheduleAutoScroll() {
        if (this.autoPlayEnabled) {
            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            AndroidUtilities.runOnUIThread(this.autoScrollRunnable, 2700);
        }
    }

    /* access modifiers changed from: private */
    public void drawEffectForView(View view, boolean z) {
        this.hasSelectedView = view != null;
        for (int i = 0; i < getChildCount(); i++) {
            StickerView stickerView = (StickerView) getChildAt(i);
            if (stickerView == view) {
                stickerView.setDrawImage(true, true, z);
            } else {
                stickerView.setDrawImage(!this.hasSelectedView, false, z);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.firstMeasure && !this.premiumStickers.isEmpty() && getChildCount() > 0) {
            this.firstMeasure = false;
            AndroidUtilities.runOnUIThread(new PremiumStickersPreviewRecycler$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLayout$2() {
        this.layoutManager.scrollToPositionWithOffset(NUM - (NUM % this.premiumStickers.size()), (getMeasuredHeight() - getChildAt(0).getMeasuredHeight()) >> 1);
        drawEffectForView((View) null, false);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.sortedView.clear();
        for (int i = 0; i < getChildCount(); i++) {
            StickerView stickerView = (StickerView) getChildAt(i);
            float top = ((float) ((stickerView.getTop() + stickerView.getMeasuredHeight()) + (stickerView.getMeasuredHeight() >> 1))) / ((float) ((getMeasuredHeight() >> 1) + stickerView.getMeasuredHeight()));
            if (top > 1.0f) {
                top = 2.0f - top;
            }
            float clamp = Utilities.clamp(top, 1.0f, 0.0f);
            stickerView.progress = clamp;
            stickerView.view.setTranslationX(((float) (-getMeasuredWidth())) * 2.0f * (1.0f - this.interpolator.getInterpolation(clamp)));
            this.sortedView.add(stickerView);
        }
        Collections.sort(this.sortedView, this.comparator);
        if ((this.firstDraw || this.checkEffect) && this.sortedView.size() > 0 && !this.premiumStickers.isEmpty()) {
            ArrayList<StickerView> arrayList = this.sortedView;
            View view = arrayList.get(arrayList.size() - 1);
            this.oldSelectedView = view;
            drawEffectForView(view, !this.firstDraw);
            this.firstDraw = false;
            this.checkEffect = false;
        } else {
            View view2 = this.oldSelectedView;
            ArrayList<StickerView> arrayList2 = this.sortedView;
            if (view2 != arrayList2.get(arrayList2.size() - 1)) {
                ArrayList<StickerView> arrayList3 = this.sortedView;
                this.oldSelectedView = arrayList3.get(arrayList3.size() - 1);
                if (this.haptic) {
                    performHapticFeedback(3);
                }
            }
        }
        for (int i2 = 0; i2 < this.sortedView.size(); i2++) {
            canvas.save();
            canvas.translate(this.sortedView.get(i2).getX(), this.sortedView.get(i2).getY());
            this.sortedView.get(i2).draw(canvas);
            canvas.restore();
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerView stickerView = new StickerView(viewGroup.getContext());
            stickerView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(stickerView);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (!PremiumStickersPreviewRecycler.this.premiumStickers.isEmpty()) {
                StickerView stickerView = (StickerView) viewHolder.itemView;
                stickerView.setSticker((TLRPC$Document) PremiumStickersPreviewRecycler.this.premiumStickers.get(i % PremiumStickersPreviewRecycler.this.premiumStickers.size()));
                stickerView.setDrawImage(!PremiumStickersPreviewRecycler.this.hasSelectedView, false, false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumStickersPreviewLoaded);
        scheduleAutoScroll();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumStickersPreviewLoaded);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.premiumStickersPreviewLoaded) {
            setStickers();
        }
    }

    private void setStickers() {
        this.premiumStickers.clear();
        this.premiumStickers.addAll(MediaDataController.getInstance(this.currentAccount).premiumPreviewStickers);
        getAdapter().notifyDataSetChanged();
        invalidate();
    }

    private static class StickerView extends FrameLayout {
        boolean animateImage = true;
        /* access modifiers changed from: private */
        public float animateImageProgress;
        ImageReceiver centerImage;
        TLRPC$Document document;
        boolean drawEffect;
        ImageReceiver effectImage;
        /* access modifiers changed from: private */
        public float effectProgress;
        public float progress;
        boolean update;
        View view;

        static /* synthetic */ float access$416(StickerView stickerView, float f) {
            float f2 = stickerView.effectProgress + f;
            stickerView.effectProgress = f2;
            return f2;
        }

        static /* synthetic */ float access$424(StickerView stickerView, float f) {
            float f2 = stickerView.effectProgress - f;
            stickerView.effectProgress = f2;
            return f2;
        }

        static /* synthetic */ float access$516(StickerView stickerView, float f) {
            float f2 = stickerView.animateImageProgress + f;
            stickerView.animateImageProgress = f2;
            return f2;
        }

        static /* synthetic */ float access$524(StickerView stickerView, float f) {
            float f2 = stickerView.animateImageProgress - f;
            stickerView.animateImageProgress = f2;
            return f2;
        }

        public StickerView(Context context) {
            super(context);
            this.view = new View(context) {
                public void draw(Canvas canvas) {
                    super.draw(canvas);
                    StickerView stickerView = StickerView.this;
                    if (stickerView.update) {
                        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(stickerView.document, "windowBackgroundGray", 0.5f);
                        StickerView stickerView2 = StickerView.this;
                        stickerView2.centerImage.setImage(ImageLocation.getForDocument(stickerView2.document), (String) null, svgThumb, "webp", (Object) null, 1);
                        if (MessageObject.isPremiumSticker(StickerView.this.document)) {
                            StickerView stickerView3 = StickerView.this;
                            stickerView3.effectImage.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(stickerView3.document), StickerView.this.document), "140_140", (ImageLocation) null, (String) null, "tgs", (Object) null, 1);
                        }
                    }
                    StickerView stickerView4 = StickerView.this;
                    if (stickerView4.drawEffect) {
                        if (stickerView4.effectProgress == 0.0f) {
                            float unused = StickerView.this.effectProgress = 1.0f;
                            if (StickerView.this.effectImage.getLottieAnimation() != null) {
                                StickerView.this.effectImage.getLottieAnimation().setCurrentFrame(0, false);
                            }
                        }
                        if (StickerView.this.effectImage.getLottieAnimation() != null) {
                            StickerView.this.effectImage.getLottieAnimation().start();
                        }
                    } else if (stickerView4.effectImage.getLottieAnimation() != null) {
                        StickerView.this.effectImage.getLottieAnimation().stop();
                    }
                    StickerView stickerView5 = StickerView.this;
                    if (stickerView5.animateImage) {
                        if (stickerView5.centerImage.getLottieAnimation() != null) {
                            StickerView.this.centerImage.getLottieAnimation().start();
                        }
                    } else if (stickerView5.centerImage.getLottieAnimation() != null) {
                        StickerView.this.centerImage.getLottieAnimation().stop();
                    }
                    StickerView stickerView6 = StickerView.this;
                    if (!stickerView6.animateImage || stickerView6.animateImageProgress == 1.0f) {
                        StickerView stickerView7 = StickerView.this;
                        if (!stickerView7.animateImage && stickerView7.animateImageProgress != 0.0f) {
                            StickerView.access$524(StickerView.this, 0.10666667f);
                            invalidate();
                        }
                    } else {
                        StickerView.access$516(StickerView.this, 0.10666667f);
                        invalidate();
                    }
                    StickerView stickerView8 = StickerView.this;
                    float unused2 = stickerView8.animateImageProgress = Utilities.clamp(stickerView8.animateImageProgress, 1.0f, 0.0f);
                    StickerView stickerView9 = StickerView.this;
                    if (!stickerView9.drawEffect || stickerView9.effectProgress == 1.0f) {
                        StickerView stickerView10 = StickerView.this;
                        if (!stickerView10.drawEffect && stickerView10.effectProgress != 0.0f) {
                            StickerView.access$424(StickerView.this, 0.10666667f);
                            invalidate();
                        }
                    } else {
                        StickerView.access$416(StickerView.this, 0.10666667f);
                        invalidate();
                    }
                    StickerView stickerView11 = StickerView.this;
                    float unused3 = stickerView11.effectProgress = Utilities.clamp(stickerView11.effectProgress, 1.0f, 0.0f);
                    float measuredWidth = ((float) StickerView.this.getMeasuredWidth()) * 0.45f;
                    float f = 1.499267f * measuredWidth;
                    float measuredWidth2 = ((float) getMeasuredWidth()) - f;
                    float measuredHeight = (((float) getMeasuredHeight()) - f) / 2.0f;
                    float f2 = f - measuredWidth;
                    StickerView.this.centerImage.setImageCoords((f2 - (0.02f * f)) + measuredWidth2, (f2 / 2.0f) + measuredHeight, measuredWidth, measuredWidth);
                    StickerView stickerView12 = StickerView.this;
                    stickerView12.centerImage.setAlpha((stickerView12.animateImageProgress * 0.7f) + 0.3f);
                    StickerView.this.centerImage.draw(canvas);
                    if (StickerView.this.effectProgress != 0.0f) {
                        StickerView.this.effectImage.setImageCoords(measuredWidth2, measuredHeight, f, f);
                        StickerView stickerView13 = StickerView.this;
                        stickerView13.effectImage.setAlpha(stickerView13.effectProgress);
                        StickerView.this.effectImage.draw(canvas);
                    }
                }
            };
            this.centerImage = new ImageReceiver(this.view);
            this.effectImage = new ImageReceiver(this.view);
            this.centerImage.setAllowStartAnimation(false);
            this.effectImage.setAllowStartAnimation(false);
            setClipChildren(false);
            addView(this.view, LayoutHelper.createFrame(-1, -2, 21));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = (int) (((float) View.MeasureSpec.getSize(i)) * 0.6f);
            ViewGroup.LayoutParams layoutParams = this.view.getLayoutParams();
            ViewGroup.LayoutParams layoutParams2 = this.view.getLayoutParams();
            int dp = size - AndroidUtilities.dp(16.0f);
            layoutParams2.height = dp;
            layoutParams.width = dp;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.7f), NUM));
        }

        public void setSticker(TLRPC$Document tLRPC$Document) {
            this.document = tLRPC$Document;
            this.update = true;
        }

        public void setDrawImage(boolean z, boolean z2, boolean z3) {
            float f = 1.0f;
            if (this.drawEffect != z2) {
                this.drawEffect = z2;
                if (!z3) {
                    this.effectProgress = z2 ? 1.0f : 0.0f;
                }
                this.view.invalidate();
            }
            if (this.animateImage != z) {
                this.animateImage = z;
                if (!z3) {
                    if (!z) {
                        f = 0.0f;
                    }
                    this.animateImageProgress = f;
                }
                this.view.invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.centerImage.onAttachedToWindow();
            this.effectImage.onAttachedToWindow();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.centerImage.onDetachedFromWindow();
            this.effectImage.onDetachedFromWindow();
        }
    }

    public void setAutoPlayEnabled(boolean z) {
        if (this.autoPlayEnabled != z) {
            this.autoPlayEnabled = z;
            if (z) {
                scheduleAutoScroll();
                this.checkEffect = true;
                invalidate();
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            drawEffectForView((View) null, true);
        }
    }
}
