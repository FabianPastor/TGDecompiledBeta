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

    public EntityView(Context context, Point pos) {
        super(context);
        this.position = pos;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
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

    public void setPosition(Point value) {
        this.position = value;
        updatePosition();
    }

    public float getScale() {
        return getScaleX();
    }

    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    public void setDelegate(EntityViewDelegate entityViewDelegate) {
        this.delegate = entityViewDelegate;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.delegate.allowInteraction(this);
    }

    /* access modifiers changed from: private */
    public boolean onTouchMove(float x, float y) {
        float scale = ((View) getParent()).getScaleX();
        float tx = (x - this.previousLocationX) / scale;
        float ty = (y - this.previousLocationY) / scale;
        if (((float) Math.hypot((double) tx, (double) ty)) <= (this.hasPanned ? 6.0f : 16.0f)) {
            return false;
        }
        pan(tx, ty);
        this.previousLocationX = x;
        this.previousLocationY = y;
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

    public boolean onTouchEvent(MotionEvent event) {
        EntityViewDelegate entityViewDelegate;
        if (event.getPointerCount() > 1 || !this.delegate.allowInteraction(this)) {
            return false;
        }
        float[] xy = this.delegate.getTransformedTouch(event.getRawX(), event.getRawY());
        boolean handled = false;
        switch (event.getActionMasked()) {
            case 0:
            case 5:
                if (!isSelected() && (entityViewDelegate = this.delegate) != null) {
                    entityViewDelegate.onEntitySelected(this);
                    this.announcedSelection = true;
                }
                this.previousLocationX = xy[0];
                this.previousLocationY = xy[1];
                handled = true;
                this.hasReleased = false;
                break;
            case 1:
            case 3:
            case 6:
                onTouchUp();
                handled = true;
                break;
            case 2:
                handled = onTouchMove(xy[0], xy[1]);
                break;
        }
        this.gestureDetector.onTouchEvent(event);
        return handled;
    }

    public void pan(float tx, float ty) {
        this.position.x += tx;
        this.position.y += ty;
        updatePosition();
    }

    /* access modifiers changed from: protected */
    public void updatePosition() {
        setX(this.position.x - (((float) getMeasuredWidth()) / 2.0f));
        setY(this.position.y - (((float) getMeasuredHeight()) / 2.0f));
        updateSelectionView();
    }

    public void scale(float scale) {
        setScale(Math.max(getScale() * scale, 0.1f));
        updateSelectionView();
    }

    public void rotate(float angle) {
        setRotation(angle);
        updateSelectionView();
    }

    /* access modifiers changed from: protected */
    public Rect getSelectionBounds() {
        return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public boolean isSelected() {
        return this.selectionView != null;
    }

    /* access modifiers changed from: protected */
    public SelectionView createSelectionView() {
        return null;
    }

    public void updateSelectionView() {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            selectionView2.updatePosition();
        }
    }

    public void select(ViewGroup selectionContainer) {
        SelectionView selectionView2 = createSelectionView();
        this.selectionView = selectionView2;
        selectionContainer.addView(selectionView2);
        selectionView2.updatePosition();
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

    public void setSelectionVisibility(boolean visible) {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            selectionView2.setVisibility(visible ? 0 : 8);
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
            Rect bounds = EntityView.this.getSelectionBounds();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            layoutParams.leftMargin = (int) bounds.x;
            layoutParams.topMargin = (int) bounds.y;
            layoutParams.width = (int) bounds.width;
            layoutParams.height = (int) bounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float x, float y) {
            return 0;
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r21) {
            /*
                r20 = this;
                r0 = r20
                int r1 = r21.getActionMasked()
                r2 = 0
                float r3 = r21.getRawX()
                float r4 = r21.getRawY()
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r5 = r5.delegate
                float[] r5 = r5.getTransformedTouch(r3, r4)
                r6 = 0
                r7 = r5[r6]
                r8 = 1
                r9 = r5[r8]
                r10 = 3
                switch(r1) {
                    case 0: goto L_0x0144;
                    case 1: goto L_0x0134;
                    case 2: goto L_0x002b;
                    case 3: goto L_0x0134;
                    case 4: goto L_0x0023;
                    case 5: goto L_0x0144;
                    case 6: goto L_0x0134;
                    default: goto L_0x0023;
                }
            L_0x0023:
                r15 = r1
                r17 = r2
                r18 = r5
                r2 = r7
                goto L_0x0170
            L_0x002b:
                int r11 = r0.currentHandle
                if (r11 != r10) goto L_0x003c
                org.telegram.ui.Components.Paint.Views.EntityView r6 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean r2 = r6.onTouchMove(r7, r9)
                r15 = r1
                r1 = r2
                r18 = r5
                r2 = r7
                goto L_0x0172
            L_0x003c:
                if (r11 == 0) goto L_0x012d
                org.telegram.ui.Components.Paint.Views.EntityView r11 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r11 = r11.previousLocationX
                float r11 = r7 - r11
                org.telegram.ui.Components.Paint.Views.EntityView r12 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float r12 = r12.previousLocationY
                float r12 = r9 - r12
                org.telegram.ui.Components.Paint.Views.EntityView r13 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean r13 = r13.hasTransformed
                r14 = 1073741824(0x40000000, float:2.0)
                if (r13 != 0) goto L_0x007d
                float r13 = java.lang.Math.abs(r11)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r15 = (float) r15
                int r13 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
                if (r13 > 0) goto L_0x007d
                float r13 = java.lang.Math.abs(r12)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r15 = (float) r15
                int r13 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
                if (r13 <= 0) goto L_0x0073
                goto L_0x007d
            L_0x0073:
                r15 = r1
                r17 = r2
                r18 = r5
                r2 = r7
                r16 = r11
                goto L_0x012b
            L_0x007d:
                org.telegram.ui.Components.Paint.Views.EntityView r13 = org.telegram.ui.Components.Paint.Views.EntityView.this
                boolean unused = r13.hasTransformed = r8
                float r13 = r20.getRotation()
                r15 = r7
                double r6 = (double) r13
                double r6 = java.lang.Math.toRadians(r6)
                float r6 = (float) r6
                r13 = r15
                double r14 = (double) r11
                r16 = r11
                double r10 = (double) r6
                double r10 = java.lang.Math.cos(r10)
                java.lang.Double.isNaN(r14)
                double r14 = r14 * r10
                double r10 = (double) r12
                double r7 = (double) r6
                double r7 = java.lang.Math.sin(r7)
                java.lang.Double.isNaN(r10)
                double r10 = r10 * r7
                double r14 = r14 + r10
                float r7 = (float) r14
                int r8 = r0.currentHandle
                r10 = 1
                if (r8 != r10) goto L_0x00b1
                r8 = -1082130432(0xffffffffbvar_, float:-1.0)
                float r7 = r7 * r8
            L_0x00b1:
                r8 = 1065353216(0x3var_, float:1.0)
                r10 = 1073741824(0x40000000, float:2.0)
                float r14 = r7 * r10
                int r10 = r20.getMeasuredWidth()
                float r10 = (float) r10
                float r14 = r14 / r10
                float r14 = r14 + r8
                org.telegram.ui.Components.Paint.Views.EntityView r8 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r8.scale(r14)
                org.telegram.ui.Components.Paint.Views.EntityView r8 = org.telegram.ui.Components.Paint.Views.EntityView.this
                org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r8 = r8.delegate
                org.telegram.ui.Components.Paint.Views.EntityView r10 = org.telegram.ui.Components.Paint.Views.EntityView.this
                int[] r8 = r8.getCenterLocation(r10)
                r10 = 0
                int r11 = r0.currentHandle
                r15 = 1
                if (r11 != r15) goto L_0x00ed
                r11 = r8[r15]
                float r11 = (float) r11
                float r11 = r11 - r4
                r15 = r1
                r17 = r2
                double r1 = (double) r11
                r11 = 0
                r11 = r8[r11]
                float r11 = (float) r11
                float r11 = r11 - r3
                r18 = r5
                r19 = r6
                double r5 = (double) r11
                double r1 = java.lang.Math.atan2(r1, r5)
                float r10 = (float) r1
                goto L_0x010a
            L_0x00ed:
                r15 = r1
                r17 = r2
                r18 = r5
                r19 = r6
                r1 = 2
                if (r11 != r1) goto L_0x010a
                r1 = 1
                r1 = r8[r1]
                float r1 = (float) r1
                float r1 = r4 - r1
                double r1 = (double) r1
                r5 = 0
                r5 = r8[r5]
                float r5 = (float) r5
                float r5 = r3 - r5
                double r5 = (double) r5
                double r1 = java.lang.Math.atan2(r1, r5)
                float r10 = (float) r1
            L_0x010a:
                org.telegram.ui.Components.Paint.Views.EntityView r1 = org.telegram.ui.Components.Paint.Views.EntityView.this
                double r5 = (double) r10
                double r5 = java.lang.Math.toDegrees(r5)
                float r2 = (float) r5
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                org.telegram.ui.Components.Paint.Views.EntityView$EntityViewDelegate r5 = r5.delegate
                float r5 = r5.getCropRotation()
                float r2 = r2 - r5
                r1.rotate(r2)
                org.telegram.ui.Components.Paint.Views.EntityView r1 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r2 = r13
                float unused = r1.previousLocationX = r2
                org.telegram.ui.Components.Paint.Views.EntityView r1 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float unused = r1.previousLocationY = r9
            L_0x012b:
                r1 = 1
                goto L_0x0172
            L_0x012d:
                r15 = r1
                r17 = r2
                r18 = r5
                r2 = r7
                goto L_0x0170
            L_0x0134:
                r15 = r1
                r17 = r2
                r18 = r5
                r2 = r7
                org.telegram.ui.Components.Paint.Views.EntityView r1 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r1.onTouchUp()
                r1 = 0
                r0.currentHandle = r1
                r1 = 1
                goto L_0x0172
            L_0x0144:
                r15 = r1
                r17 = r2
                r18 = r5
                r2 = r7
                float r1 = r21.getX()
                float r5 = r21.getY()
                int r1 = r0.pointInsideHandle(r1, r5)
                if (r1 == 0) goto L_0x016d
                r0.currentHandle = r1
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float unused = r5.previousLocationX = r2
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                float unused = r5.previousLocationY = r9
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                r6 = 0
                boolean unused = r5.hasReleased = r6
                r5 = 1
                r17 = r5
            L_0x016d:
                r1 = r17
                goto L_0x0172
            L_0x0170:
                r1 = r17
            L_0x0172:
                int r5 = r0.currentHandle
                r6 = 3
                if (r5 != r6) goto L_0x0183
                org.telegram.ui.Components.Paint.Views.EntityView r5 = org.telegram.ui.Components.Paint.Views.EntityView.this
                android.view.GestureDetector r5 = r5.gestureDetector
                r6 = r21
                r5.onTouchEvent(r6)
                goto L_0x0185
            L_0x0183:
                r6 = r21
            L_0x0185:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView.SelectionView.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }
}
