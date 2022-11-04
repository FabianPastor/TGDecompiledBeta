package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.UUID;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;
/* loaded from: classes3.dex */
public class EntityView extends FrameLayout {
    private boolean announcedSelection;
    private EntityViewDelegate delegate;
    private GestureDetector gestureDetector;
    private boolean hasPanned;
    private boolean hasReleased;
    private boolean hasTransformed;
    protected Point position;
    private float previousLocationX;
    private float previousLocationY;
    private boolean recognizedLongPress;
    protected SelectionView selectionView;
    private UUID uuid;

    /* loaded from: classes3.dex */
    public interface EntityViewDelegate {
        boolean allowInteraction(EntityView entityView);

        int[] getCenterLocation(EntityView entityView);

        float getCropRotation();

        float[] getTransformedTouch(float f, float f2);

        boolean onEntityLongClicked(EntityView entityView);

        boolean onEntitySelected(EntityView entityView);
    }

    /* renamed from: createSelectionView */
    protected SelectionView mo1572createSelectionView() {
        return null;
    }

    public EntityView(Context context, Point point) {
        super(context);
        this.hasPanned = false;
        this.hasReleased = false;
        this.hasTransformed = false;
        this.announcedSelection = false;
        this.recognizedLongPress = false;
        this.uuid = UUID.randomUUID();
        this.position = point;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.Paint.Views.EntityView.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent motionEvent) {
                if (EntityView.this.hasPanned || EntityView.this.hasTransformed || EntityView.this.hasReleased) {
                    return;
                }
                EntityView.this.recognizedLongPress = true;
                if (EntityView.this.delegate == null) {
                    return;
                }
                EntityView.this.performHapticFeedback(0);
                EntityView.this.delegate.onEntityLongClicked(EntityView.this);
            }
        });
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point point) {
        this.position = point;
        updatePosition();
    }

    public float getScale() {
        return getScaleX();
    }

    public void setScale(float f) {
        setScaleX(f);
        setScaleY(f);
    }

    public void setDelegate(EntityViewDelegate entityViewDelegate) {
        this.delegate = entityViewDelegate;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.delegate.allowInteraction(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onTouchMove(float f, float f2) {
        float scaleX = ((View) getParent()).getScaleX();
        float f3 = (f - this.previousLocationX) / scaleX;
        float f4 = (f2 - this.previousLocationY) / scaleX;
        if (((float) Math.hypot(f3, f4)) > (this.hasPanned ? 6.0f : 16.0f)) {
            pan(f3, f4);
            this.previousLocationX = f;
            this.previousLocationY = f2;
            this.hasPanned = true;
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTouchUp() {
        EntityViewDelegate entityViewDelegate;
        if (!this.recognizedLongPress && !this.hasPanned && !this.hasTransformed && !this.announcedSelection && (entityViewDelegate = this.delegate) != null) {
            entityViewDelegate.onEntitySelected(this);
        }
        this.recognizedLongPress = false;
        this.hasPanned = false;
        this.hasTransformed = false;
        this.hasReleased = true;
        this.announcedSelection = false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0031, code lost:
        if (r3 != 6) goto L16;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getPointerCount()
            r1 = 0
            r2 = 1
            if (r0 > r2) goto L61
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r0 = r5.delegate
            boolean r0 = r0.allowInteraction(r5)
            if (r0 != 0) goto L11
            goto L61
        L11:
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r0 = r5.delegate
            float r3 = r6.getRawX()
            float r4 = r6.getRawY()
            float[] r0 = r0.getTransformedTouch(r3, r4)
            int r3 = r6.getActionMasked()
            if (r3 == 0) goto L42
            if (r3 == r2) goto L3d
            r4 = 2
            if (r3 == r4) goto L34
            r4 = 3
            if (r3 == r4) goto L3d
            r4 = 5
            if (r3 == r4) goto L42
            r0 = 6
            if (r3 == r0) goto L3d
            goto L5c
        L34:
            r1 = r0[r1]
            r0 = r0[r2]
            boolean r1 = r5.onTouchMove(r1, r0)
            goto L5c
        L3d:
            r5.onTouchUp()
        L40:
            r1 = 1
            goto L5c
        L42:
            boolean r3 = r5.isSelected()
            if (r3 != 0) goto L51
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r3 = r5.delegate
            if (r3 == 0) goto L51
            r3.onEntitySelected(r5)
            r5.announcedSelection = r2
        L51:
            r3 = r0[r1]
            r5.previousLocationX = r3
            r0 = r0[r2]
            r5.previousLocationY = r0
            r5.hasReleased = r1
            goto L40
        L5c:
            android.view.GestureDetector r0 = r5.gestureDetector
            r0.onTouchEvent(r6)
        L61:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void pan(float f, float f2) {
        Point point = this.position;
        point.x += f;
        point.y += f2;
        updatePosition();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePosition() {
        setX(this.position.x - (getMeasuredWidth() / 2.0f));
        setY(this.position.y - (getMeasuredHeight() / 2.0f));
        updateSelectionView();
    }

    public void scale(float f) {
        setScale(Math.max(getScale() * f, 0.1f));
        updateSelectionView();
    }

    public void rotate(float f) {
        setRotation(f);
        updateSelectionView();
    }

    protected Rect getSelectionBounds() {
        return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override // android.view.View
    public boolean isSelected() {
        return this.selectionView != null;
    }

    public void updateSelectionView() {
        SelectionView selectionView = this.selectionView;
        if (selectionView != null) {
            selectionView.updatePosition();
        }
    }

    public void select(ViewGroup viewGroup) {
        SelectionView mo1572createSelectionView = mo1572createSelectionView();
        this.selectionView = mo1572createSelectionView;
        viewGroup.addView(mo1572createSelectionView);
        mo1572createSelectionView.updatePosition();
    }

    public void deselect() {
        SelectionView selectionView = this.selectionView;
        if (selectionView == null) {
            return;
        }
        if (selectionView.getParent() != null) {
            ((ViewGroup) this.selectionView.getParent()).removeView(this.selectionView);
        }
        this.selectionView = null;
    }

    public void setSelectionVisibility(boolean z) {
        SelectionView selectionView = this.selectionView;
        if (selectionView == null) {
            return;
        }
        selectionView.setVisibility(z ? 0 : 8);
    }

    /* loaded from: classes3.dex */
    public class SelectionView extends FrameLayout {
        private int currentHandle;
        protected Paint dotPaint;
        protected Paint dotStrokePaint;
        protected Paint paint;

        protected int pointInsideHandle(float f, float f2) {
            throw null;
        }

        public SelectionView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.dotPaint = new Paint(1);
            this.dotStrokePaint = new Paint(1);
            setWillNotDraw(false);
            this.paint.setColor(-1);
            this.dotPaint.setColor(-12793105);
            this.dotStrokePaint.setColor(-1);
            this.dotStrokePaint.setStyle(Paint.Style.STROKE);
            this.dotStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        }

        protected void updatePosition() {
            Rect selectionBounds = EntityView.this.getSelectionBounds();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            layoutParams.leftMargin = (int) selectionBounds.x;
            layoutParams.topMargin = (int) selectionBounds.y;
            layoutParams.width = (int) selectionBounds.width;
            layoutParams.height = (int) selectionBounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        /* JADX WARN: Code restructure failed: missing block: B:11:0x002c, code lost:
            if (r1 != 6) goto L11;
         */
        /* JADX WARN: Removed duplicated region for block: B:41:0x0132  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r18) {
            /*
                Method dump skipped, instructions count: 318
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView.SelectionView.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }
}
