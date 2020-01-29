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
    /* access modifiers changed from: private */
    public int offsetX;
    /* access modifiers changed from: private */
    public int offsetY;
    protected Point position = new Point();
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

    public void setOffset(int i, int i2) {
        this.offsetX = i;
        this.offsetY = i2;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.delegate.allowInteraction(this);
    }

    /* access modifiers changed from: private */
    public boolean onTouchMove(float f, float f2) {
        float scaleX = ((View) getParent()).getScaleX();
        Point point = new Point((f - this.previousLocationX) / scaleX, (f2 - this.previousLocationY) / scaleX);
        if (((float) Math.hypot((double) point.x, (double) point.y)) <= (this.hasPanned ? 6.0f : 16.0f)) {
            return false;
        }
        pan(point);
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

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        if (r4 != 6) goto L_0x004e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            int r0 = r7.getPointerCount()
            r1 = 0
            r2 = 1
            if (r0 > r2) goto L_0x0053
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r0 = r6.delegate
            boolean r0 = r0.allowInteraction(r6)
            if (r0 != 0) goto L_0x0011
            goto L_0x0053
        L_0x0011:
            float r0 = r7.getRawX()
            float r3 = r7.getRawY()
            int r4 = r7.getActionMasked()
            if (r4 == 0) goto L_0x0038
            if (r4 == r2) goto L_0x0033
            r5 = 2
            if (r4 == r5) goto L_0x002e
            r5 = 3
            if (r4 == r5) goto L_0x0033
            r5 = 5
            if (r4 == r5) goto L_0x0038
            r0 = 6
            if (r4 == r0) goto L_0x0033
            goto L_0x004e
        L_0x002e:
            boolean r1 = r6.onTouchMove(r0, r3)
            goto L_0x004e
        L_0x0033:
            r6.onTouchUp()
        L_0x0036:
            r1 = 1
            goto L_0x004e
        L_0x0038:
            boolean r4 = r6.isSelected()
            if (r4 != 0) goto L_0x0047
            org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r4 = r6.delegate
            if (r4 == 0) goto L_0x0047
            r4.onEntitySelected(r6)
            r6.announcedSelection = r2
        L_0x0047:
            r6.previousLocationX = r0
            r6.previousLocationY = r3
            r6.hasReleased = r1
            goto L_0x0036
        L_0x004e:
            android.view.GestureDetector r0 = r6.gestureDetector
            r0.onTouchEvent(r7)
        L_0x0053:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void pan(Point point) {
        Point point2 = this.position;
        point2.x += point.x;
        point2.y += point.y;
        updatePosition();
    }

    /* access modifiers changed from: protected */
    public void updatePosition() {
        setX(this.position.x - (((float) getWidth()) / 2.0f));
        setY(this.position.y - (((float) getHeight()) / 2.0f));
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
        public static final int SELECTION_LEFT_HANDLE = 1;
        public static final int SELECTION_RIGHT_HANDLE = 2;
        public static final int SELECTION_WHOLE_HANDLE = 3;
        private int currentHandle;
        protected Paint dotPaint = new Paint(1);
        protected Paint dotStrokePaint = new Paint(1);
        protected Paint paint = new Paint(1);

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float f, float f2) {
            return 0;
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
            layoutParams.leftMargin = ((int) selectionBounds.x) + EntityView.this.offsetX;
            layoutParams.topMargin = ((int) selectionBounds.y) + EntityView.this.offsetY;
            layoutParams.width = (int) selectionBounds.width;
            layoutParams.height = (int) selectionBounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
            if (r0 != 6) goto L_0x0130;
         */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0134  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
                r11 = this;
                int r0 = r12.getActionMasked()
                r1 = 3
                r2 = 0
                r3 = 1
                if (r0 == 0) goto L_0x0108
                if (r0 == r3) goto L_0x00ff
                r4 = 2
                if (r0 == r4) goto L_0x0018
                if (r0 == r1) goto L_0x00ff
                r4 = 5
                if (r0 == r4) goto L_0x0108
                r4 = 6
                if (r0 == r4) goto L_0x00ff
                goto L_0x0130
            L_0x0018:
                int r0 = r11.currentHandle
                if (r0 != r1) goto L_0x002c
                float r0 = r12.getRawX()
                float r2 = r12.getRawY()
                org.telegram.ui.Components.Paint.Views.EntityView r3 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean r2 = r3.onTouchMove(r0, r2)
                goto L_0x0130
            L_0x002c:
                if (r0 == 0) goto L_0x0130
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean unused = r0.hasTransformed = r3
                org.telegram.ui.Components.Point r0 = new org.telegram.ui.Components.Point
                float r2 = r12.getRawX()
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r5 = r5.previousLocationX
                float r2 = r2 - r5
                float r5 = r12.getRawY()
                org.telegram.ui.Components.Paint.Views.EntityView r6 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r6 = r6.previousLocationY
                float r5 = r5 - r6
                r0.<init>(r2, r5)
                float r2 = r11.getRotation()
                double r5 = (double) r2
                double r5 = java.lang.Math.toRadians(r5)
                float r2 = (float) r5
                float r5 = r0.x
                double r5 = (double) r5
                double r7 = (double) r2
                double r9 = java.lang.Math.cos(r7)
                java.lang.Double.isNaN(r5)
                double r5 = r5 * r9
                float r0 = r0.y
                double r9 = (double) r0
                double r7 = java.lang.Math.sin(r7)
                java.lang.Double.isNaN(r9)
                double r9 = r9 * r7
                double r5 = r5 + r9
                float r0 = (float) r5
                int r2 = r11.currentHandle
                if (r2 != r3) goto L_0x007b
                r2 = -1082130432(0xffffffffbvar_, float:-1.0)
                float r0 = r0 * r2
            L_0x007b:
                r2 = 1065353216(0x3var_, float:1.0)
                r5 = 1073741824(0x40000000, float:2.0)
                float r0 = r0 * r5
                int r5 = r11.getWidth()
                float r5 = (float) r5
                float r0 = r0 / r5
                float r0 = r0 + r2
                org.telegram.ui.Components.Paint.Views.EntityView r2 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r2.scale(r0)
                int r0 = r11.getLeft()
                int r2 = r11.getWidth()
                int r2 = r2 / r4
                int r0 = r0 + r2
                float r0 = (float) r0
                int r2 = r11.getTop()
                int r5 = r11.getHeight()
                int r5 = r5 / r4
                int r2 = r2 + r5
                float r2 = (float) r2
                float r5 = r12.getRawX()
                android.view.ViewParent r6 = r11.getParent()
                android.view.View r6 = (android.view.View) r6
                int r6 = r6.getLeft()
                float r6 = (float) r6
                float r5 = r5 - r6
                float r6 = r12.getRawY()
                android.view.ViewParent r7 = r11.getParent()
                android.view.View r7 = (android.view.View) r7
                int r7 = r7.getTop()
                float r7 = (float) r7
                float r6 = r6 - r7
                int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                float r7 = (float) r7
                float r6 = r6 - r7
                r7 = 0
                int r8 = r11.currentHandle
                if (r8 != r3) goto L_0x00d6
                float r2 = r2 - r6
                double r6 = (double) r2
                float r0 = r0 - r5
                double r4 = (double) r0
                double r4 = java.lang.Math.atan2(r6, r4)
            L_0x00d4:
                float r7 = (float) r4
                goto L_0x00e1
            L_0x00d6:
                if (r8 != r4) goto L_0x00e1
                float r6 = r6 - r2
                double r6 = (double) r6
                float r5 = r5 - r0
                double r4 = (double) r5
                double r4 = java.lang.Math.atan2(r6, r4)
                goto L_0x00d4
            L_0x00e1:
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                double r4 = (double) r7
                double r4 = java.lang.Math.toDegrees(r4)
                float r2 = (float) r4
                r0.rotate(r2)
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r2 = r12.getRawX()
                float unused = r0.previousLocationX = r2
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r2 = r12.getRawY()
                float unused = r0.previousLocationY = r2
                goto L_0x0106
            L_0x00ff:
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r0.onTouchUp()
                r11.currentHandle = r2
            L_0x0106:
                r2 = 1
                goto L_0x0130
            L_0x0108:
                float r0 = r12.getX()
                float r4 = r12.getY()
                int r0 = r11.pointInsideHandle(r0, r4)
                if (r0 == 0) goto L_0x0130
                r11.currentHandle = r0
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r4 = r12.getRawX()
                float unused = r0.previousLocationX = r4
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r4 = r12.getRawY()
                float unused = r0.previousLocationY = r4
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean unused = r0.hasReleased = r2
                goto L_0x0106
            L_0x0130:
                int r0 = r11.currentHandle
                if (r0 != r1) goto L_0x013d
                org.telegram.ui.Components.Paint.Views.EntityView r0 = org.telegram.ui.Components.Paint.Views.EntityView.this
                android.view.GestureDetector r0 = r0.gestureDetector
                r0.onTouchEvent(r12)
            L_0x013d:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView.SelectionView.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }
}
