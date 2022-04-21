package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.SystemClock;
import android.text.style.CharacterStyle;
import android.util.Pair;
import android.view.View;
import android.view.ViewConfiguration;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;

public class LinkSpanDrawable<S extends CharacterStyle> {
    private static final long mReleaseDelay = 75;
    private static final long mReleaseDuration = 100;
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
    private final float rippleAlpha;
    private final float selectionAlpha;

    public LinkSpanDrawable(S span, Theme.ResourcesProvider resourcesProvider, float touchX, float touchY) {
        this(span, resourcesProvider, touchX, touchY, true);
    }

    public LinkSpanDrawable(S span, Theme.ResourcesProvider resourcesProvider, float touchX, float touchY, boolean supportsLongPress) {
        this.mPathes = new ArrayList<>();
        this.mPathesCount = 0;
        this.circlePath = new Path();
        this.mStart = -1;
        this.mReleaseStart = -1;
        this.selectionAlpha = 0.2f;
        this.rippleAlpha = 0.8f;
        this.mSpan = span;
        this.mResourcesProvider = resourcesProvider;
        setColor(getThemedColor("chat_linkSelectBackground"));
        this.mTouchX = touchX;
        this.mTouchY = touchY;
        long longPressTimeout = (long) ViewConfiguration.getLongPressTimeout();
        this.mLongPressDuration = longPressTimeout;
        this.mDuration = (long) Math.min(((float) ((long) ViewConfiguration.getTapTimeout())) * 1.8f, ((float) longPressTimeout) * 0.8f);
        this.mSupportsLongPress = false;
    }

    public void setColor(int color2) {
        this.color = color2;
        Paint paint = this.mSelectionPaint;
        if (paint != null) {
            paint.setColor(color2);
            this.mSelectionAlpha = this.mSelectionPaint.getAlpha();
        }
        Paint paint2 = this.mRipplePaint;
        if (paint2 != null) {
            paint2.setColor(color2);
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
        float longPress;
        float longPress2;
        Canvas canvas2 = canvas;
        boolean cornerRadiusUpdate = this.cornerRadius != AndroidUtilities.dp(4.0f);
        if (this.mSelectionPaint == null || cornerRadiusUpdate) {
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
        if (this.mRipplePaint == null || cornerRadiusUpdate) {
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
            this.mPathes.get(0).computeBounds(AndroidUtilities.rectTmp, false);
            this.mBounds = new Rect((int) AndroidUtilities.rectTmp.left, (int) AndroidUtilities.rectTmp.top, (int) AndroidUtilities.rectTmp.right, (int) AndroidUtilities.rectTmp.bottom);
            for (int i = 1; i < this.mPathesCount; i++) {
                this.mPathes.get(i).computeBounds(AndroidUtilities.rectTmp, false);
                Rect rect = this.mBounds;
                rect.left = Math.min(rect.left, (int) AndroidUtilities.rectTmp.left);
                Rect rect2 = this.mBounds;
                rect2.top = Math.min(rect2.top, (int) AndroidUtilities.rectTmp.top);
                Rect rect3 = this.mBounds;
                rect3.right = Math.max(rect3.right, (int) AndroidUtilities.rectTmp.right);
                Rect rect4 = this.mBounds;
                rect4.bottom = Math.max(rect4.bottom, (int) AndroidUtilities.rectTmp.bottom);
            }
            this.mMaxRadius = (float) Math.sqrt(Math.max(Math.max(Math.pow((double) (((float) this.mBounds.left) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.top) - this.mTouchY), 2.0d), Math.pow((double) (((float) this.mBounds.right) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.top) - this.mTouchY), 2.0d)), Math.max(Math.pow((double) (((float) this.mBounds.left) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.bottom) - this.mTouchY), 2.0d), Math.pow((double) (((float) this.mBounds.right) - this.mTouchX), 2.0d) + Math.pow((double) (((float) this.mBounds.bottom) - this.mTouchY), 2.0d))));
        }
        long now = SystemClock.elapsedRealtime();
        if (this.mStart < 0) {
            this.mStart = now;
        }
        float pressT = CubicBezierInterpolator.DEFAULT.getInterpolation(Math.min(1.0f, ((float) (now - this.mStart)) / ((float) this.mDuration)));
        long j = this.mReleaseStart;
        float releaseT = j < 0 ? 0.0f : Math.min(1.0f, Math.max(0.0f, ((float) ((now - 75) - j)) / 100.0f));
        if (this.mSupportsLongPress) {
            long j2 = this.mDuration;
            float longPress3 = Math.max(0.0f, ((float) ((now - this.mStart) - (j2 * 2))) / ((float) (this.mLongPressDuration - (j2 * 2))));
            if (longPress3 > 1.0f) {
                longPress2 = 1.0f - (((float) ((now - this.mStart) - this.mLongPressDuration)) / ((float) this.mDuration));
            } else {
                longPress2 = longPress3 * 0.5f;
            }
            longPress = longPress2 * (1.0f - releaseT);
        } else {
            longPress = 1.0f;
        }
        this.mSelectionPaint.setAlpha((int) (((float) this.mSelectionAlpha) * 0.2f * Math.min(1.0f, pressT * 5.0f) * (1.0f - releaseT)));
        this.mSelectionPaint.setStrokeWidth(Math.min(1.0f, 1.0f - longPress) * ((float) AndroidUtilities.dp(5.0f)));
        for (int i2 = 0; i2 < this.mPathesCount; i2++) {
            canvas2.drawPath(this.mPathes.get(i2), this.mSelectionPaint);
        }
        this.mRipplePaint.setAlpha((int) (((float) this.mRippleAlpha) * 0.8f * (1.0f - releaseT)));
        this.mRipplePaint.setStrokeWidth(Math.min(1.0f, 1.0f - longPress) * ((float) AndroidUtilities.dp(5.0f)));
        if (pressT < 1.0f) {
            canvas.save();
            this.circlePath.reset();
            this.circlePath.addCircle(this.mTouchX, this.mTouchY, this.mMaxRadius * pressT, Path.Direction.CW);
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
        return pressT < 1.0f || this.mReleaseStart >= 0 || (this.mSupportsLongPress && now - this.mStart < this.mLongPressDuration + this.mDuration);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.mResourcesProvider;
        Integer color2 = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color2 != null ? color2.intValue() : Theme.getColor(key);
    }

    public static class LinkCollector {
        private ArrayList<Pair<LinkSpanDrawable, Object>> mLinks = new ArrayList<>();
        private int mLinksCount = 0;
        private View mParent;

        public LinkCollector() {
        }

        public LinkCollector(View parentView) {
            this.mParent = parentView;
        }

        public void addLink(LinkSpanDrawable link) {
            addLink(link, (Object) null);
        }

        public void addLink(LinkSpanDrawable link, Object obj) {
            this.mLinks.add(new Pair(link, obj));
            this.mLinksCount++;
            invalidate(obj);
        }

        public void removeLink(LinkSpanDrawable link) {
            removeLink(link, true);
        }

        public void removeLink(LinkSpanDrawable link, boolean animated) {
            if (link != null) {
                Pair<LinkSpanDrawable, Object> pair = null;
                int i = 0;
                while (true) {
                    if (i >= this.mLinksCount) {
                        break;
                    } else if (this.mLinks.get(i).first == link) {
                        pair = this.mLinks.get(i);
                        break;
                    } else {
                        i++;
                    }
                }
                if (pair != null) {
                    if (!animated) {
                        this.mLinks.remove(pair);
                        link.reset();
                        this.mLinksCount = this.mLinks.size();
                        invalidate(pair.second);
                    } else if (link.mReleaseStart < 0) {
                        link.release();
                        invalidate(pair.second);
                        AndroidUtilities.runOnUIThread(new LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda0(this, link), Math.max(0, (link.mReleaseStart - SystemClock.elapsedRealtime()) + 75 + 100));
                    }
                }
            }
        }

        /* renamed from: lambda$removeLink$0$org-telegram-ui-Components-LinkSpanDrawable$LinkCollector  reason: not valid java name */
        public /* synthetic */ void m4131xCLASSNAME(LinkSpanDrawable link) {
            removeLink(link, false);
        }

        private void removeLink(int index, boolean animated) {
            if (index >= 0 && index < this.mLinksCount) {
                if (animated) {
                    Pair<LinkSpanDrawable, Object> pair = this.mLinks.get(index);
                    LinkSpanDrawable link = (LinkSpanDrawable) pair.first;
                    if (link.mReleaseStart < 0) {
                        link.release();
                        invalidate(pair.second);
                        AndroidUtilities.runOnUIThread(new LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda1(this, link), Math.max(0, (link.mReleaseStart - SystemClock.elapsedRealtime()) + 75 + 100));
                        return;
                    }
                    return;
                }
                Pair<LinkSpanDrawable, Object> pair2 = this.mLinks.remove(index);
                ((LinkSpanDrawable) pair2.first).reset();
                this.mLinksCount = this.mLinks.size();
                invalidate(pair2.second);
            }
        }

        /* renamed from: lambda$removeLink$1$org-telegram-ui-Components-LinkSpanDrawable$LinkCollector  reason: not valid java name */
        public /* synthetic */ void m4132xd5d9var_(LinkSpanDrawable link) {
            removeLink(link, false);
        }

        public void clear() {
            clear(true);
        }

        public void clear(boolean animated) {
            if (animated) {
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

        public void removeLinks(Object obj, boolean animated) {
            for (int i = 0; i < this.mLinksCount; i++) {
                if (this.mLinks.get(i).second == obj) {
                    removeLink(i, animated);
                }
            }
        }

        public boolean draw(Canvas canvas) {
            boolean invalidate = false;
            for (int i = 0; i < this.mLinksCount; i++) {
                invalidate = ((LinkSpanDrawable) this.mLinks.get(i).first).draw(canvas) || invalidate;
            }
            return invalidate;
        }

        public boolean draw(Canvas canvas, Object obj) {
            boolean invalidate = false;
            int i = 0;
            while (true) {
                boolean z = false;
                if (i < this.mLinksCount) {
                    if (this.mLinks.get(i).second == obj) {
                        if (((LinkSpanDrawable) this.mLinks.get(i).first).draw(canvas) || invalidate) {
                            z = true;
                        }
                        invalidate = z;
                    }
                    i++;
                } else {
                    invalidate(obj, false);
                    return invalidate;
                }
            }
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

        private void invalidate(Object obj, boolean tryParent) {
            View view;
            if (obj instanceof View) {
                ((View) obj).invalidate();
            } else if (obj instanceof ArticleViewer.DrawingText) {
                ArticleViewer.DrawingText text = (ArticleViewer.DrawingText) obj;
                if (text.latestParentView != null) {
                    text.latestParentView.invalidate();
                }
            } else if (tryParent && (view = this.mParent) != null) {
                view.invalidate();
            }
        }
    }
}
