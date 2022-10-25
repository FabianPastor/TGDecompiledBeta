package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Components.LinkSpanDrawable;
/* loaded from: classes3.dex */
public class LinkSpanDrawable<S extends CharacterStyle> {
    private static final ArrayList<LinkPath> pathCache = new ArrayList<>();
    private final Path circlePath;
    private int color;
    private int cornerRadius;
    private android.graphics.Rect mBounds;
    private final long mDuration;
    private final long mLongPressDuration;
    private float mMaxRadius;
    private final ArrayList<LinkPath> mPathes;
    private int mPathesCount;
    private long mReleaseStart;
    private final Theme.ResourcesProvider mResourcesProvider;
    private int mRippleAlpha;
    private Paint mRipplePaint;
    private int mSelectionAlpha;
    private Paint mSelectionPaint;
    private final S mSpan;
    private long mStart;
    private final boolean mSupportsLongPress;
    private final float mTouchX;
    private final float mTouchY;

    public LinkSpanDrawable(S s, Theme.ResourcesProvider resourcesProvider, float f, float f2) {
        this(s, resourcesProvider, f, f2, true);
    }

    public LinkSpanDrawable(S s, Theme.ResourcesProvider resourcesProvider, float f, float f2, boolean z) {
        long j;
        this.mPathes = new ArrayList<>();
        this.mPathesCount = 0;
        this.circlePath = new Path();
        this.mStart = -1L;
        this.mReleaseStart = -1L;
        this.mSpan = s;
        this.mResourcesProvider = resourcesProvider;
        setColor(Theme.getColor("chat_linkSelectBackground", resourcesProvider));
        this.mTouchX = f;
        this.mTouchY = f2;
        this.mLongPressDuration = ViewConfiguration.getLongPressTimeout();
        this.mDuration = Math.min(ViewConfiguration.getTapTimeout() * 1.8f, ((float) j) * 0.8f);
        this.mSupportsLongPress = false;
    }

    public void setColor(int i) {
        this.color = i;
        Paint paint = this.mSelectionPaint;
        if (paint != null) {
            paint.setColor(i);
            this.mSelectionAlpha = this.mSelectionPaint.getAlpha();
        }
        Paint paint2 = this.mRipplePaint;
        if (paint2 != null) {
            paint2.setColor(i);
            this.mRippleAlpha = this.mRipplePaint.getAlpha();
        }
    }

    public void release() {
        this.mReleaseStart = Math.max(this.mStart + this.mDuration, SystemClock.elapsedRealtime());
    }

    public LinkPath obtainNewPath() {
        LinkPath linkPath;
        ArrayList<LinkPath> arrayList = pathCache;
        if (!arrayList.isEmpty()) {
            linkPath = arrayList.remove(0);
        } else {
            linkPath = new LinkPath(true);
        }
        linkPath.reset();
        this.mPathes.add(linkPath);
        this.mPathesCount = this.mPathes.size();
        return linkPath;
    }

    public void reset() {
        if (this.mPathes.isEmpty()) {
            return;
        }
        pathCache.addAll(this.mPathes);
        this.mPathes.clear();
        this.mPathesCount = 0;
    }

    public S getSpan() {
        return this.mSpan;
    }

    public boolean draw(Canvas canvas) {
        float f;
        boolean z = this.cornerRadius != AndroidUtilities.dp(4.0f);
        if (this.mSelectionPaint == null || z) {
            Paint paint = new Paint(1);
            this.mSelectionPaint = paint;
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.mSelectionPaint.setColor(this.color);
            this.mSelectionAlpha = this.mSelectionPaint.getAlpha();
            Paint paint2 = this.mSelectionPaint;
            int dp = AndroidUtilities.dp(4.0f);
            this.cornerRadius = dp;
            paint2.setPathEffect(new CornerPathEffect(dp));
        }
        if (this.mRipplePaint == null || z) {
            Paint paint3 = new Paint(1);
            this.mRipplePaint = paint3;
            paint3.setStyle(Paint.Style.FILL_AND_STROKE);
            this.mRipplePaint.setColor(this.color);
            this.mRippleAlpha = this.mRipplePaint.getAlpha();
            Paint paint4 = this.mRipplePaint;
            int dp2 = AndroidUtilities.dp(4.0f);
            this.cornerRadius = dp2;
            paint4.setPathEffect(new CornerPathEffect(dp2));
        }
        if (this.mBounds == null && this.mPathesCount > 0) {
            RectF rectF = AndroidUtilities.rectTmp;
            this.mPathes.get(0).computeBounds(rectF, false);
            this.mBounds = new android.graphics.Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
            for (int i = 1; i < this.mPathesCount; i++) {
                RectF rectF2 = AndroidUtilities.rectTmp;
                this.mPathes.get(i).computeBounds(rectF2, false);
                android.graphics.Rect rect = this.mBounds;
                rect.left = Math.min(rect.left, (int) rectF2.left);
                android.graphics.Rect rect2 = this.mBounds;
                rect2.top = Math.min(rect2.top, (int) rectF2.top);
                android.graphics.Rect rect3 = this.mBounds;
                rect3.right = Math.max(rect3.right, (int) rectF2.right);
                android.graphics.Rect rect4 = this.mBounds;
                rect4.bottom = Math.max(rect4.bottom, (int) rectF2.bottom);
            }
            this.mMaxRadius = (float) Math.sqrt(Math.max(Math.max(Math.pow(this.mBounds.left - this.mTouchX, 2.0d) + Math.pow(this.mBounds.top - this.mTouchY, 2.0d), Math.pow(this.mBounds.right - this.mTouchX, 2.0d) + Math.pow(this.mBounds.top - this.mTouchY, 2.0d)), Math.max(Math.pow(this.mBounds.left - this.mTouchX, 2.0d) + Math.pow(this.mBounds.bottom - this.mTouchY, 2.0d), Math.pow(this.mBounds.right - this.mTouchX, 2.0d) + Math.pow(this.mBounds.bottom - this.mTouchY, 2.0d))));
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.mStart < 0) {
            this.mStart = elapsedRealtime;
        }
        float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(Math.min(1.0f, ((float) (elapsedRealtime - this.mStart)) / ((float) this.mDuration)));
        long j = this.mReleaseStart;
        float min = j < 0 ? 0.0f : Math.min(1.0f, Math.max(0.0f, ((float) ((elapsedRealtime - 75) - j)) / 100.0f));
        if (this.mSupportsLongPress) {
            long j2 = this.mDuration;
            float max = Math.max(0.0f, ((float) ((elapsedRealtime - this.mStart) - (j2 * 2))) / ((float) (this.mLongPressDuration - (j2 * 2))));
            f = (max > 1.0f ? 1.0f - (((float) ((elapsedRealtime - this.mStart) - this.mLongPressDuration)) / ((float) this.mDuration)) : max * 0.5f) * (1.0f - min);
        } else {
            f = 1.0f;
        }
        float f2 = 1.0f - min;
        this.mSelectionPaint.setAlpha((int) (this.mSelectionAlpha * 0.2f * Math.min(1.0f, interpolation * 5.0f) * f2));
        float f3 = 1.0f - f;
        this.mSelectionPaint.setStrokeWidth(Math.min(1.0f, f3) * AndroidUtilities.dp(5.0f));
        for (int i2 = 0; i2 < this.mPathesCount; i2++) {
            canvas.drawPath(this.mPathes.get(i2), this.mSelectionPaint);
        }
        this.mRipplePaint.setAlpha((int) (this.mRippleAlpha * 0.8f * f2));
        this.mRipplePaint.setStrokeWidth(Math.min(1.0f, f3) * AndroidUtilities.dp(5.0f));
        if (interpolation < 1.0f) {
            canvas.save();
            this.circlePath.reset();
            this.circlePath.addCircle(this.mTouchX, this.mTouchY, this.mMaxRadius * interpolation, Path.Direction.CW);
            canvas.clipPath(this.circlePath);
            for (int i3 = 0; i3 < this.mPathesCount; i3++) {
                canvas.drawPath(this.mPathes.get(i3), this.mRipplePaint);
            }
            canvas.restore();
        } else {
            for (int i4 = 0; i4 < this.mPathesCount; i4++) {
                canvas.drawPath(this.mPathes.get(i4), this.mRipplePaint);
            }
        }
        return interpolation < 1.0f || this.mReleaseStart >= 0 || (this.mSupportsLongPress && elapsedRealtime - this.mStart < this.mLongPressDuration + this.mDuration);
    }

    /* loaded from: classes3.dex */
    public static class LinkCollector {
        private ArrayList<Pair<LinkSpanDrawable, Object>> mLinks = new ArrayList<>();
        private int mLinksCount = 0;
        private View mParent;

        public LinkCollector() {
        }

        public LinkCollector(View view) {
            this.mParent = view;
        }

        public void addLink(LinkSpanDrawable linkSpanDrawable) {
            addLink(linkSpanDrawable, null);
        }

        public void addLink(LinkSpanDrawable linkSpanDrawable, Object obj) {
            this.mLinks.add(new Pair<>(linkSpanDrawable, obj));
            this.mLinksCount++;
            invalidate(obj);
        }

        public void removeLink(LinkSpanDrawable linkSpanDrawable) {
            removeLink(linkSpanDrawable, true);
        }

        public void removeLink(final LinkSpanDrawable linkSpanDrawable, boolean z) {
            if (linkSpanDrawable == null) {
                return;
            }
            Pair<LinkSpanDrawable, Object> pair = null;
            int i = 0;
            while (true) {
                if (i >= this.mLinksCount) {
                    break;
                } else if (this.mLinks.get(i).first == linkSpanDrawable) {
                    pair = this.mLinks.get(i);
                    break;
                } else {
                    i++;
                }
            }
            if (pair == null) {
                return;
            }
            if (z) {
                if (linkSpanDrawable.mReleaseStart >= 0) {
                    return;
                }
                linkSpanDrawable.release();
                invalidate(pair.second);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LinkSpanDrawable.LinkCollector.this.lambda$removeLink$0(linkSpanDrawable);
                    }
                }, Math.max(0L, (linkSpanDrawable.mReleaseStart - SystemClock.elapsedRealtime()) + 75 + 100));
                return;
            }
            this.mLinks.remove(pair);
            linkSpanDrawable.reset();
            this.mLinksCount = this.mLinks.size();
            invalidate(pair.second);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeLink$0(LinkSpanDrawable linkSpanDrawable) {
            removeLink(linkSpanDrawable, false);
        }

        private void removeLink(int i, boolean z) {
            if (i < 0 || i >= this.mLinksCount) {
                return;
            }
            if (z) {
                Pair<LinkSpanDrawable, Object> pair = this.mLinks.get(i);
                final LinkSpanDrawable linkSpanDrawable = (LinkSpanDrawable) pair.first;
                if (linkSpanDrawable.mReleaseStart >= 0) {
                    return;
                }
                linkSpanDrawable.release();
                invalidate(pair.second);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        LinkSpanDrawable.LinkCollector.this.lambda$removeLink$1(linkSpanDrawable);
                    }
                }, Math.max(0L, (linkSpanDrawable.mReleaseStart - SystemClock.elapsedRealtime()) + 75 + 100));
                return;
            }
            Pair<LinkSpanDrawable, Object> remove = this.mLinks.remove(i);
            ((LinkSpanDrawable) remove.first).reset();
            this.mLinksCount = this.mLinks.size();
            invalidate(remove.second);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeLink$1(LinkSpanDrawable linkSpanDrawable) {
            removeLink(linkSpanDrawable, false);
        }

        public void clear() {
            clear(true);
        }

        public void clear(boolean z) {
            if (z) {
                for (int i = 0; i < this.mLinksCount; i++) {
                    removeLink(i, true);
                }
            } else if (this.mLinksCount > 0) {
                for (int i2 = 0; i2 < this.mLinksCount; i2++) {
                    ((LinkSpanDrawable) this.mLinks.get(i2).first).reset();
                    invalidate(this.mLinks.get(i2).second, false);
                }
                this.mLinks.clear();
                this.mLinksCount = 0;
                invalidate();
            }
        }

        public void removeLinks(Object obj) {
            removeLinks(obj, true);
        }

        public void removeLinks(Object obj, boolean z) {
            for (int i = 0; i < this.mLinksCount; i++) {
                if (this.mLinks.get(i).second == obj) {
                    removeLink(i, z);
                }
            }
        }

        public boolean draw(Canvas canvas) {
            boolean z = false;
            for (int i = 0; i < this.mLinksCount; i++) {
                z = ((LinkSpanDrawable) this.mLinks.get(i).first).draw(canvas) || z;
            }
            return z;
        }

        public boolean draw(Canvas canvas, Object obj) {
            boolean z = false;
            for (int i = 0; i < this.mLinksCount; i++) {
                if (this.mLinks.get(i).second == obj) {
                    z = ((LinkSpanDrawable) this.mLinks.get(i).first).draw(canvas) || z;
                }
            }
            invalidate(obj, false);
            return z;
        }

        public boolean isEmpty() {
            return this.mLinksCount <= 0;
        }

        private void invalidate() {
            invalidate(null, true);
        }

        private void invalidate(Object obj) {
            invalidate(obj, true);
        }

        private void invalidate(Object obj, boolean z) {
            View view;
            if (obj instanceof View) {
                ((View) obj).invalidate();
            } else if (obj instanceof ArticleViewer.DrawingText) {
                View view2 = ((ArticleViewer.DrawingText) obj).latestParentView;
                if (view2 == null) {
                    return;
                }
                view2.invalidate();
            } else if (!z || (view = this.mParent) == null) {
            } else {
                view.invalidate();
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class LinksTextView extends TextView {
        private boolean isCustomLinkCollector;
        private LinkCollector links;
        private OnLinkPress onLongPressListener;
        private OnLinkPress onPressListener;
        private LinkSpanDrawable<ClickableSpan> pressedLink;
        private Theme.ResourcesProvider resourcesProvider;

        /* loaded from: classes3.dex */
        public interface OnLinkPress {
            void run(ClickableSpan clickableSpan);
        }

        public LinksTextView(Context context) {
            this(context, null);
        }

        public LinksTextView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.isCustomLinkCollector = false;
            this.links = new LinkCollector(this);
            this.resourcesProvider = resourcesProvider;
        }

        public LinksTextView(Context context, LinkCollector linkCollector, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.isCustomLinkCollector = true;
            this.links = linkCollector;
            this.resourcesProvider = resourcesProvider;
        }

        public void setOnLinkPressListener(OnLinkPress onLinkPress) {
            this.onPressListener = onLinkPress;
        }

        public void setOnLinkLongPressListener(OnLinkPress onLinkPress) {
            this.onLongPressListener = onLinkPress;
        }

        public ClickableSpan hit(int i, int i2) {
            Layout layout = getLayout();
            if (layout == null) {
                return null;
            }
            int paddingLeft = i - getPaddingLeft();
            int paddingTop = i2 - getPaddingTop();
            int lineForVertical = layout.getLineForVertical(paddingTop);
            float f = paddingLeft;
            int offsetForHorizontal = layout.getOffsetForHorizontal(lineForVertical, f);
            float lineLeft = getLayout().getLineLeft(lineForVertical);
            if (lineLeft <= f && lineLeft + layout.getLineWidth(lineForVertical) >= f && paddingTop >= 0 && paddingTop <= layout.getHeight()) {
                ClickableSpan[] clickableSpanArr = (ClickableSpan[]) new SpannableString(layout.getText()).getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                if (clickableSpanArr.length != 0 && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                    return clickableSpanArr[0];
                }
            }
            return null;
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.links != null) {
                Layout layout = getLayout();
                final ClickableSpan hit = hit((int) motionEvent.getX(), (int) motionEvent.getY());
                if (hit != null && motionEvent.getAction() == 0) {
                    final LinkSpanDrawable<ClickableSpan> linkSpanDrawable = new LinkSpanDrawable<>(hit, this.resourcesProvider, motionEvent.getX(), motionEvent.getY());
                    this.pressedLink = linkSpanDrawable;
                    this.links.addLink(linkSpanDrawable);
                    SpannableString spannableString = new SpannableString(layout.getText());
                    int spanStart = spannableString.getSpanStart(this.pressedLink.getSpan());
                    int spanEnd = spannableString.getSpanEnd(this.pressedLink.getSpan());
                    LinkPath obtainNewPath = this.pressedLink.obtainNewPath();
                    obtainNewPath.setCurrentLayout(layout, spanStart, getPaddingTop());
                    layout.getSelectionPath(spanStart, spanEnd, obtainNewPath);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.LinkSpanDrawable$LinksTextView$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            LinkSpanDrawable.LinksTextView.this.lambda$onTouchEvent$0(linkSpanDrawable, hit);
                        }
                    }, ViewConfiguration.getLongPressTimeout());
                    return true;
                } else if (motionEvent.getAction() == 1) {
                    this.links.clear();
                    LinkSpanDrawable<ClickableSpan> linkSpanDrawable2 = this.pressedLink;
                    if (linkSpanDrawable2 != null && linkSpanDrawable2.getSpan() == hit) {
                        OnLinkPress onLinkPress = this.onPressListener;
                        if (onLinkPress != null) {
                            onLinkPress.run(this.pressedLink.getSpan());
                        } else if (this.pressedLink.getSpan() != null) {
                            this.pressedLink.getSpan().onClick(this);
                        }
                    }
                    this.pressedLink = null;
                    return true;
                } else if (motionEvent.getAction() == 3) {
                    this.links.clear();
                    this.pressedLink = null;
                    return true;
                }
            }
            return this.pressedLink != null || super.onTouchEvent(motionEvent);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$0(LinkSpanDrawable linkSpanDrawable, ClickableSpan clickableSpan) {
            OnLinkPress onLinkPress = this.onLongPressListener;
            if (onLinkPress == null || this.pressedLink != linkSpanDrawable) {
                return;
            }
            onLinkPress.run(clickableSpan);
            this.pressedLink = null;
            this.links.clear();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.TextView, android.view.View
        public void onDraw(Canvas canvas) {
            if (!this.isCustomLinkCollector) {
                canvas.save();
                canvas.translate(getPaddingLeft(), getPaddingTop());
                if (this.links.draw(canvas)) {
                    invalidate();
                }
                canvas.restore();
            }
            super.onDraw(canvas);
        }
    }
}
