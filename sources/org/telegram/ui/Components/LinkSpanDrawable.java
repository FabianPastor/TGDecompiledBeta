package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;

public class LinkSpanDrawable<S extends CharacterStyle> {
    private static final ArrayList<LinkPath> pathCache = new ArrayList<>();
    private final Path circlePath;
    private int color;
    private int cornerRadius;
    private Rect mBounds;
    private final long mDuration;
    private final long mLongPressDuration;
    private float mMaxRadius;
    private final ArrayList<LinkPath> mPathes;
    private int mPathesCount;
    /* access modifiers changed from: private */
    public long mReleaseStart;
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
        this.mPathes = new ArrayList<>();
        this.mPathesCount = 0;
        this.circlePath = new Path();
        this.mStart = -1;
        this.mReleaseStart = -1;
        this.mSpan = s;
        this.mResourcesProvider = resourcesProvider;
        setColor(Theme.getColor("chat_linkSelectBackground", resourcesProvider));
        this.mTouchX = f;
        this.mTouchY = f2;
        long longPressTimeout = (long) ViewConfiguration.getLongPressTimeout();
        this.mLongPressDuration = longPressTimeout;
        this.mDuration = (long) Math.min(((float) ((long) ViewConfiguration.getTapTimeout())) * 1.8f, ((float) longPressTimeout) * 0.8f);
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
        if (!this.mPathes.isEmpty()) {
            pathCache.addAll(this.mPathes);
            this.mPathes.clear();
            this.mPathesCount = 0;
        }
    }

    public S getSpan() {
        return this.mSpan;
    }

    public boolean draw(Canvas canvas) {
        float f;
        Canvas canvas2 = canvas;
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
            paint2.setPathEffect(new CornerPathEffect((float) dp));
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
            paint4.setPathEffect(new CornerPathEffect((float) dp2));
        }
        if (this.mBounds == null && this.mPathesCount > 0) {
            RectF rectF = AndroidUtilities.rectTmp;
            this.mPathes.get(0).computeBounds(rectF, false);
            this.mBounds = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
            for (int i = 1; i < this.mPathesCount; i++) {
                RectF rectF2 = AndroidUtilities.rectTmp;
                this.mPathes.get(i).computeBounds(rectF2, false);
                Rect rect = this.mBounds;
                rect.left = Math.min(rect.left, (int) rectF2.left);
                Rect rect2 = this.mBounds;
                rect2.top = Math.min(rect2.top, (int) rectF2.top);
                Rect rect3 = this.mBounds;
                rect3.right = Math.max(rect3.right, (int) rectF2.right);
                Rect rect4 = this.mBounds;
                rect4.bottom = Math.max(rect4.bottom, (int) rectF2.bottom);
            }
            this.mMaxRadius = (float) Math.sqrt(Math.max(Math.max(Math.pow((double) (((float) this.mBounds.left) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.top) - this.mTouchY), 2.0d), Math.pow((double) (((float) this.mBounds.right) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.top) - this.mTouchY), 2.0d)), Math.max(Math.pow((double) (((float) this.mBounds.left) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.bottom) - this.mTouchY), 2.0d), Math.pow((double) (((float) this.mBounds.right) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.bottom) - this.mTouchY), 2.0d))));
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
        this.mSelectionPaint.setAlpha((int) (((float) this.mSelectionAlpha) * 0.2f * Math.min(1.0f, interpolation * 5.0f) * f2));
        float f3 = 1.0f - f;
        this.mSelectionPaint.setStrokeWidth(Math.min(1.0f, f3) * ((float) AndroidUtilities.dp(5.0f)));
        for (int i2 = 0; i2 < this.mPathesCount; i2++) {
            canvas2.drawPath(this.mPathes.get(i2), this.mSelectionPaint);
        }
        this.mRipplePaint.setAlpha((int) (((float) this.mRippleAlpha) * 0.8f * f2));
        this.mRipplePaint.setStrokeWidth(Math.min(1.0f, f3) * ((float) AndroidUtilities.dp(5.0f)));
        if (interpolation < 1.0f) {
            canvas.save();
            this.circlePath.reset();
            this.circlePath.addCircle(this.mTouchX, this.mTouchY, this.mMaxRadius * interpolation, Path.Direction.CW);
            canvas2.clipPath(this.circlePath);
            for (int i3 = 0; i3 < this.mPathesCount; i3++) {
                canvas2.drawPath(this.mPathes.get(i3), this.mRipplePaint);
            }
            canvas.restore();
        } else {
            for (int i4 = 0; i4 < this.mPathesCount; i4++) {
                canvas2.drawPath(this.mPathes.get(i4), this.mRipplePaint);
            }
        }
        return interpolation < 1.0f || this.mReleaseStart >= 0 || (this.mSupportsLongPress && elapsedRealtime - this.mStart < this.mLongPressDuration + this.mDuration);
    }

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
            addLink(linkSpanDrawable, (Object) null);
        }

        public void addLink(LinkSpanDrawable linkSpanDrawable, Object obj) {
            this.mLinks.add(new Pair(linkSpanDrawable, obj));
            this.mLinksCount++;
            invalidate(obj);
        }

        public void removeLink(LinkSpanDrawable linkSpanDrawable) {
            removeLink(linkSpanDrawable, true);
        }

        public void removeLink(LinkSpanDrawable linkSpanDrawable, boolean z) {
            if (linkSpanDrawable != null) {
                Pair pair = null;
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
                if (pair != null) {
                    if (!z) {
                        this.mLinks.remove(pair);
                        linkSpanDrawable.reset();
                        this.mLinksCount = this.mLinks.size();
                        invalidate(pair.second);
                    } else if (linkSpanDrawable.mReleaseStart < 0) {
                        linkSpanDrawable.release();
                        invalidate(pair.second);
                        AndroidUtilities.runOnUIThread(new LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda0(this, linkSpanDrawable), Math.max(0, (linkSpanDrawable.mReleaseStart - SystemClock.elapsedRealtime()) + 75 + 100));
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$removeLink$0(LinkSpanDrawable linkSpanDrawable) {
            removeLink(linkSpanDrawable, false);
        }

        private void removeLink(int i, boolean z) {
            if (i >= 0 && i < this.mLinksCount) {
                if (z) {
                    Pair pair = this.mLinks.get(i);
                    LinkSpanDrawable linkSpanDrawable = (LinkSpanDrawable) pair.first;
                    if (linkSpanDrawable.mReleaseStart < 0) {
                        linkSpanDrawable.release();
                        invalidate(pair.second);
                        AndroidUtilities.runOnUIThread(new LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda1(this, linkSpanDrawable), Math.max(0, (linkSpanDrawable.mReleaseStart - SystemClock.elapsedRealtime()) + 75 + 100));
                        return;
                    }
                    return;
                }
                Pair remove = this.mLinks.remove(i);
                ((LinkSpanDrawable) remove.first).reset();
                this.mLinksCount = this.mLinks.size();
                invalidate(remove.second);
            }
        }

        /* access modifiers changed from: private */
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
            invalidate((Object) null, true);
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
                if (view2 != null) {
                    view2.invalidate();
                }
            } else if (z && (view = this.mParent) != null) {
                view.invalidate();
            }
        }
    }

    public static class LinksTextView extends TextView {
        private boolean isCustomLinkCollector;
        private LinkCollector links;
        private OnLinkPress onLongPressListener;
        private OnLinkPress onPressListener;
        private LinkSpanDrawable<ClickableSpan> pressedLink;
        private Theme.ResourcesProvider resourcesProvider;

        public interface OnLinkPress {
            void run(ClickableSpan clickableSpan);
        }

        public LinksTextView(Context context) {
            this(context, (Theme.ResourcesProvider) null);
        }

        public LinksTextView(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.isCustomLinkCollector = false;
            this.links = new LinkCollector(this);
            this.resourcesProvider = resourcesProvider2;
        }

        public LinksTextView(Context context, LinkCollector linkCollector, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.isCustomLinkCollector = true;
            this.links = linkCollector;
            this.resourcesProvider = resourcesProvider2;
        }

        public void setOnLinkPressListener(OnLinkPress onLinkPress) {
            this.onPressListener = onLinkPress;
        }

        public void setOnLinkLongPressListener(OnLinkPress onLinkPress) {
            this.onLongPressListener = onLinkPress;
        }

        /* JADX WARNING: Removed duplicated region for block: B:20:0x00b9  */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x00ee  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r11) {
            /*
                r10 = this;
                org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r0 = r10.links
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x00fd
                android.text.Layout r0 = r10.getLayout()
                float r3 = r11.getX()
                int r4 = r10.getPaddingLeft()
                float r4 = (float) r4
                float r3 = r3 - r4
                int r3 = (int) r3
                float r4 = r11.getY()
                int r5 = r10.getPaddingTop()
                float r5 = (float) r5
                float r4 = r4 - r5
                int r4 = (int) r4
                int r5 = r0.getLineForVertical(r4)
                float r3 = (float) r3
                int r6 = r0.getOffsetForHorizontal(r5, r3)
                android.text.Layout r7 = r10.getLayout()
                float r7 = r7.getLineLeft(r5)
                r8 = 0
                int r9 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
                if (r9 > 0) goto L_0x00b2
                float r5 = r0.getLineWidth(r5)
                float r7 = r7 + r5
                int r3 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
                if (r3 < 0) goto L_0x00b2
                if (r4 < 0) goto L_0x00b2
                int r3 = r0.getHeight()
                if (r4 > r3) goto L_0x00b2
                android.text.SpannableString r3 = new android.text.SpannableString
                java.lang.CharSequence r4 = r0.getText()
                r3.<init>(r4)
                java.lang.Class<android.text.style.ClickableSpan> r4 = android.text.style.ClickableSpan.class
                java.lang.Object[] r4 = r3.getSpans(r6, r6, r4)
                android.text.style.ClickableSpan[] r4 = (android.text.style.ClickableSpan[]) r4
                int r5 = r4.length
                if (r5 == 0) goto L_0x00b2
                boolean r5 = org.telegram.messenger.AndroidUtilities.isAccessibilityScreenReaderEnabled()
                if (r5 != 0) goto L_0x00b2
                r5 = r4[r1]
                int r6 = r11.getAction()
                if (r6 != 0) goto L_0x00b3
                org.telegram.ui.Components.LinkSpanDrawable r1 = new org.telegram.ui.Components.LinkSpanDrawable
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r10.resourcesProvider
                float r7 = r11.getX()
                float r11 = r11.getY()
                r1.<init>(r5, r6, r7, r11)
                r10.pressedLink = r1
                org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r11 = r10.links
                r11.addLink(r1)
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r11 = r10.pressedLink
                android.text.style.CharacterStyle r11 = r11.getSpan()
                int r11 = r3.getSpanStart(r11)
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r1 = r10.pressedLink
                android.text.style.CharacterStyle r1 = r1.getSpan()
                int r1 = r3.getSpanEnd(r1)
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r3 = r10.pressedLink
                org.telegram.ui.Components.LinkPath r3 = r3.obtainNewPath()
                int r5 = r10.getPaddingTop()
                float r5 = (float) r5
                r3.setCurrentLayout(r0, r11, r5)
                r0.getSelectionPath(r11, r1, r3)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView$$ExternalSyntheticLambda0 r11 = new org.telegram.ui.Components.LinkSpanDrawable$LinksTextView$$ExternalSyntheticLambda0
                r11.<init>(r10, r4)
                int r0 = android.view.ViewConfiguration.getLongPressTimeout()
                long r0 = (long) r0
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r11, r0)
                return r2
            L_0x00b2:
                r5 = r8
            L_0x00b3:
                int r0 = r11.getAction()
                if (r0 != r2) goto L_0x00ee
                org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r11 = r10.links
                r11.clear()
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r11 = r10.pressedLink
                if (r11 == 0) goto L_0x00eb
                android.text.style.CharacterStyle r11 = r11.getSpan()
                if (r11 != r5) goto L_0x00eb
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView$OnLinkPress r11 = r10.onPressListener
                if (r11 == 0) goto L_0x00d8
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r0 = r10.pressedLink
                android.text.style.CharacterStyle r0 = r0.getSpan()
                android.text.style.ClickableSpan r0 = (android.text.style.ClickableSpan) r0
                r11.run(r0)
                goto L_0x00eb
            L_0x00d8:
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r11 = r10.pressedLink
                android.text.style.CharacterStyle r11 = r11.getSpan()
                if (r11 == 0) goto L_0x00eb
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r11 = r10.pressedLink
                android.text.style.CharacterStyle r11 = r11.getSpan()
                android.text.style.ClickableSpan r11 = (android.text.style.ClickableSpan) r11
                r11.onClick(r10)
            L_0x00eb:
                r10.pressedLink = r8
                return r2
            L_0x00ee:
                int r0 = r11.getAction()
                r3 = 3
                if (r0 != r3) goto L_0x00fd
                org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r11 = r10.links
                r11.clear()
                r10.pressedLink = r8
                return r2
            L_0x00fd:
                org.telegram.ui.Components.LinkSpanDrawable<android.text.style.ClickableSpan> r0 = r10.pressedLink
                if (r0 != 0) goto L_0x0107
                boolean r11 = super.onTouchEvent(r11)
                if (r11 == 0) goto L_0x0108
            L_0x0107:
                r1 = 1
            L_0x0108:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.LinkSpanDrawable.LinksTextView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$0(ClickableSpan[] clickableSpanArr) {
            OnLinkPress onLinkPress = this.onLongPressListener;
            if (onLinkPress != null) {
                onLinkPress.run(clickableSpanArr[0]);
                this.pressedLink = null;
                this.links.clear();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (!this.isCustomLinkCollector) {
                canvas.save();
                canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
                if (this.links.draw(canvas)) {
                    invalidate();
                }
                canvas.restore();
            }
            super.onDraw(canvas);
        }
    }
}
