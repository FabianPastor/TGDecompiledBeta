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

public class EntityView extends FrameLayout {
    private boolean announcedSelection = false;
    /* access modifiers changed from: private */
    public EntityViewDelegate delegate;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetector;
    /* access modifiers changed from: private */
    public boolean hasPanned = false;
    /* access modifiers changed from: private */
    public boolean hasReleased = false;
    /* access modifiers changed from: private */
    public boolean hasTransformed = false;
    protected Point position;
    /* access modifiers changed from: private */
    public float previousLocationX;
    /* access modifiers changed from: private */
    public float previousLocationY;
    /* access modifiers changed from: private */
    public boolean recognizedLongPress = false;
    protected SelectionView selectionView;
    private UUID uuid = UUID.randomUUID();

    public interface EntityViewDelegate {
        boolean allowInteraction(EntityView entityView);

        int[] getCenterLocation(EntityView entityView);

        float getCropRotation();

        float[] getTransformedTouch(float f, float f2);

        boolean onEntityLongClicked(EntityView entityView);

        boolean onEntitySelected(EntityView entityView);
    }

    /* access modifiers changed from: protected */
    public SelectionView createSelectionView() {
        return null;
    }

    public EntityView(Context context, Point point) {
        super(context);
        this.position = point;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent motionEvent) {
                if (!EntityView.this.hasPanned && !EntityView.this.hasTransformed && !EntityView.this.hasReleased) {
                    boolean unused = EntityView.this.recognizedLongPress = true;
                    if (EntityView.this.delegate != null) {
                        EntityView.this.performHapticFeedback(0);
                        EntityView.this.delegate.onEntityLongClicked(EntityView.this);
                    }
                }
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

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.delegate.allowInteraction(this);
    }

    /* access modifiers changed from: private */
    public boolean onTouchMove(float f, float f2) {
        float scaleX = ((View) getParent()).getScaleX();
        float f3 = (f - this.previousLocationX) / scaleX;
        float f4 = (f2 - this.previousLocationY) / scaleX;
        if (((float) Math.hypot((double) f3, (double) f4)) <= (this.hasPanned ? 6.0f : 16.0f)) {
            return false;
        }
        pan(f3, f4);
        this.previousLocationX = f;
        this.previousLocationY = f2;
        this.hasPanned = true;
        return true;
    }

    /* access modifiers changed from: private */
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

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0031, code lost:
        if (r3 != 6) goto L_0x005c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getPointerCount()
            r1 = 0
            r2 = 1
            if (r0 > r2) goto L_0x0061
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r0 = r5.delegate
            boolean r0 = r0.allowInteraction(r5)
            if (r0 != 0) goto L_0x0011
            goto L_0x0061
        L_0x0011:
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r0 = r5.delegate
            float r3 = r6.getRawX()
            float r4 = r6.getRawY()
            float[] r0 = r0.getTransformedTouch(r3, r4)
            int r3 = r6.getActionMasked()
            if (r3 == 0) goto L_0x0042
            if (r3 == r2) goto L_0x003d
            r4 = 2
            if (r3 == r4) goto L_0x0034
            r4 = 3
            if (r3 == r4) goto L_0x003d
            r4 = 5
            if (r3 == r4) goto L_0x0042
            r0 = 6
            if (r3 == r0) goto L_0x003d
            goto L_0x005c
        L_0x0034:
            r1 = r0[r1]
            r0 = r0[r2]
            boolean r1 = r5.onTouchMove(r1, r0)
            goto L_0x005c
        L_0x003d:
            r5.onTouchUp()
        L_0x0040:
            r1 = 1
            goto L_0x005c
        L_0x0042:
            boolean r3 = r5.isSelected()
            if (r3 != 0) goto L_0x0051
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r3 = r5.delegate
            if (r3 == 0) goto L_0x0051
            r3.onEntitySelected(r5)
            r5.announcedSelection = r2
        L_0x0051:
            r3 = r0[r1]
            r5.previousLocationX = r3
            r0 = r0[r2]
            r5.previousLocationY = r0
            r5.hasReleased = r1
            goto L_0x0040
        L_0x005c:
            android.view.GestureDetector r0 = r5.gestureDetector
            r0.onTouchEvent(r6)
        L_0x0061:
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

    /* access modifiers changed from: protected */
    public void updatePosition() {
        setX(this.position.x - (((float) getMeasuredWidth()) / 2.0f));
        setY(this.position.y - (((float) getMeasuredHeight()) / 2.0f));
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

    /* access modifiers changed from: protected */
    public Rect getSelectionBounds() {
        return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public boolean isSelected() {
        return this.selectionView != null;
    }

    public void updateSelectionView() {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            selectionView2.updatePosition();
        }
    }

    public void select(ViewGroup viewGroup) {
        SelectionView createSelectionView = createSelectionView();
        this.selectionView = createSelectionView;
        viewGroup.addView(createSelectionView);
        createSelectionView.updatePosition();
    }

    public void deselect() {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            if (selectionView2.getParent() != null) {
                ((ViewGroup) this.selectionView.getParent()).removeView(this.selectionView);
            }
            this.selectionView = null;
        }
    }

    public void setSelectionVisibility(boolean z) {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            selectionView2.setVisibility(z ? 0 : 8);
        }
    }

    public class SelectionView extends FrameLayout {
        private int currentHandle;
        protected Paint dotPaint = new Paint(1);
        protected Paint dotStrokePaint = new Paint(1);
        protected Paint paint = new Paint(1);

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float f, float f2) {
            throw null;
        }

        public SelectionView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.paint.setColor(-1);
            this.dotPaint.setColor(-12793105);
            this.dotStrokePaint.setColor(-1);
            this.dotStrokePaint.setStyle(Paint.Style.STROKE);
            this.dotStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        }

        /* access modifiers changed from: protected */
        public void updatePosition() {
            Rect selectionBounds = EntityView.this.getSelectionBounds();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            layoutParams.leftMargin = (int) selectionBounds.x;
            layoutParams.topMargin = (int) selectionBounds.y;
            layoutParams.width = (int) selectionBounds.width;
            layoutParams.height = (int) selectionBounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x002c, code lost:
            if (r1 != 6) goto L_0x012d;
         */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x0132  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r18) {
            /*
                r17 = this;
                r0 = r17
                int r1 = r18.getActionMasked()
                float r2 = r18.getRawX()
                float r3 = r18.getRawY()
                org.telegram.ui.Components.Paint.Views.EntityView r4 = org.telegram.ui.Components.Paint.Views.EntityView.this
                org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r4 = r4.delegate
                float[] r4 = r4.getTransformedTouch(r2, r3)
                r5 = 0
                r6 = r4[r5]
                r7 = 1
                r4 = r4[r7]
                r8 = 3
                if (r1 == 0) goto L_0x010d
                if (r1 == r7) goto L_0x0104
                r9 = 2
                if (r1 == r9) goto L_0x0030
                if (r1 == r8) goto L_0x0104
                r2 = 5
                if (r1 == r2) goto L_0x010d
                r2 = 6
                if (r1 == r2) goto L_0x0104
                goto L_0x012d
            L_0x0030:
                int r1 = r0.currentHandle
                if (r1 != r8) goto L_0x003c
                org.telegram.ui.Components.Paint.Views.EntityView r1 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean r5 = r1.onTouchMove(r6, r4)
                goto L_0x012d
            L_0x003c:
                if (r1 == 0) goto L_0x012d
                org.telegram.ui.Components.Paint.Views.EntityView r1 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r1 = r1.previousLocationX
                float r1 = r6 - r1
                org.telegram.ui.Components.Paint.Views.EntityView r10 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r10 = r10.previousLocationY
                float r10 = r4 - r10
                org.telegram.ui.Components.Paint.Views.EntityView r11 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean r11 = r11.hasTransformed
                r12 = 1073741824(0x40000000, float:2.0)
                if (r11 != 0) goto L_0x0072
                float r11 = java.lang.Math.abs(r1)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r13 = (float) r13
                int r11 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
                if (r11 > 0) goto L_0x0072
                float r11 = java.lang.Math.abs(r10)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r13 = (float) r13
                int r11 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
                if (r11 <= 0) goto L_0x010b
            L_0x0072:
                org.telegram.ui.Components.Paint.Views.EntityView r11 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean unused = r11.hasTransformed = r7
                float r11 = r17.getRotation()
                double r13 = (double) r11
                double r13 = java.lang.Math.toRadians(r13)
                float r11 = (float) r13
                double r13 = (double) r1
                double r8 = (double) r11
                double r15 = java.lang.Math.cos(r8)
                java.lang.Double.isNaN(r13)
                double r13 = r13 * r15
                double r10 = (double) r10
                double r8 = java.lang.Math.sin(r8)
                java.lang.Double.isNaN(r10)
                double r10 = r10 * r8
                double r13 = r13 + r10
                float r8 = (float) r13
                int r9 = r0.currentHandle
                if (r9 != r7) goto L_0x00a0
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                float r8 = r8 * r9
            L_0x00a0:
                r9 = 1065353216(0x3var_, float:1.0)
                float r8 = r8 * r12
                int r10 = r17.getMeasuredWidth()
                float r10 = (float) r10
                float r8 = r8 / r10
                float r8 = r8 + r9
                org.telegram.ui.Components.Paint.Views.EntityView r9 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r9.scale(r8)
                org.telegram.ui.Components.Paint.Views.EntityView r8 = org.telegram.ui.Components.Paint.Views.EntityView.this
                org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r8 = r8.delegate
                org.telegram.ui.Components.Paint.Views.EntityView r9 = org.telegram.ui.Components.Paint.Views.EntityView.this
                int[] r8 = r8.getCenterLocation(r9)
                r9 = 0
                int r10 = r0.currentHandle
                if (r10 != r7) goto L_0x00d1
                r9 = r8[r7]
                float r9 = (float) r9
                float r9 = r9 - r3
                double r9 = (double) r9
                r3 = r8[r5]
                float r3 = (float) r3
                float r3 = r3 - r2
                double r2 = (double) r3
                double r2 = java.lang.Math.atan2(r9, r2)
            L_0x00cf:
                float r9 = (float) r2
                goto L_0x00e3
            L_0x00d1:
                r11 = 2
                if (r10 != r11) goto L_0x00e3
                r9 = r8[r7]
                float r9 = (float) r9
                float r3 = r3 - r9
                double r9 = (double) r3
                r3 = r8[r5]
                float r3 = (float) r3
                float r2 = r2 - r3
                double r2 = (double) r2
                double r2 = java.lang.Math.atan2(r9, r2)
                goto L_0x00cf
            L_0x00e3:
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                double r8 = (double) r9
                double r8 = java.lang.Math.toDegrees(r8)
                float r3 = (float) r8
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r5 = r5.delegate
                float r5 = r5.getCropRotation()
                float r3 = r3 - r5
                r2.rotate(r3)
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float unused = r2.previousLocationX = r6
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float unused = r2.previousLocationY = r4
                goto L_0x010b
            L_0x0104:
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r2.onTouchUp()
                r0.currentHandle = r5
            L_0x010b:
                r5 = 1
                goto L_0x012d
            L_0x010d:
                float r2 = r18.getX()
                float r3 = r18.getY()
                int r2 = r0.pointInsideHandle(r2, r3)
                if (r2 == 0) goto L_0x012d
                r0.currentHandle = r2
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float unused = r2.previousLocationX = r6
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float unused = r2.previousLocationY = r4
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean unused = r2.hasReleased = r5
                goto L_0x010b
            L_0x012d:
                int r2 = r0.currentHandle
                r1 = 3
                if (r2 != r1) goto L_0x013d
                org.telegram.ui.Components.Paint.Views.EntityView r1 = org.telegram.ui.Components.Paint.Views.EntityView.this
                android.view.GestureDetector r1 = r1.gestureDetector
                r2 = r18
                r1.onTouchEvent(r2)
            L_0x013d:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView.SelectionView.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }
}
