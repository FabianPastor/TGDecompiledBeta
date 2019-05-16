package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.util.UUID;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;

public class EntityView extends FrameLayout {
    private boolean announcedSelection = false;
    private EntityViewDelegate delegate;
    private GestureDetector gestureDetector;
    private boolean hasPanned = false;
    private boolean hasReleased = false;
    private boolean hasTransformed = false;
    private int offsetX;
    private int offsetY;
    protected Point position = new Point();
    private float previousLocationX;
    private float previousLocationY;
    private boolean recognizedLongPress = false;
    protected SelectionView selectionView;
    private UUID uuid = UUID.randomUUID();

    public interface EntityViewDelegate {
        boolean allowInteraction(EntityView entityView);

        boolean onEntityLongClicked(EntityView entityView);

        boolean onEntitySelected(EntityView entityView);
    }

    public class SelectionView extends FrameLayout {
        public static final int SELECTION_LEFT_HANDLE = 1;
        public static final int SELECTION_RIGHT_HANDLE = 2;
        public static final int SELECTION_WHOLE_HANDLE = 3;
        private int currentHandle;
        protected Paint dotPaint = new Paint(1);
        protected Paint dotStrokePaint = new Paint(1);
        protected Paint paint = new Paint(1);

        /* Access modifiers changed, original: protected */
        public int pointInsideHandle(float f, float f2) {
            return 0;
        }

        public SelectionView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.paint.setColor(-1);
            this.dotPaint.setColor(-12793105);
            this.dotStrokePaint.setColor(-1);
            this.dotStrokePaint.setStyle(Style.STROKE);
            this.dotStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        }

        /* Access modifiers changed, original: protected */
        public void updatePosition() {
            Rect selectionBounds = EntityView.this.getSelectionBounds();
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.leftMargin = ((int) selectionBounds.x) + EntityView.this.offsetX;
            layoutParams.topMargin = ((int) selectionBounds.y) + EntityView.this.offsetY;
            layoutParams.width = (int) selectionBounds.width;
            layoutParams.height = (int) selectionBounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        /* JADX WARNING: Removed duplicated region for block: B:31:0x0134  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0134  */
        /* JADX WARNING: Missing block: B:9:0x0014, code skipped:
            if (r0 != 6) goto L_0x0130;
     */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
            r11 = this;
            r0 = r12.getActionMasked();
            r1 = 3;
            r2 = 0;
            r3 = 1;
            if (r0 == 0) goto L_0x0108;
        L_0x0009:
            if (r0 == r3) goto L_0x00ff;
        L_0x000b:
            r4 = 2;
            if (r0 == r4) goto L_0x0018;
        L_0x000e:
            if (r0 == r1) goto L_0x00ff;
        L_0x0010:
            r4 = 5;
            if (r0 == r4) goto L_0x0108;
        L_0x0013:
            r4 = 6;
            if (r0 == r4) goto L_0x00ff;
        L_0x0016:
            goto L_0x0130;
        L_0x0018:
            r0 = r11.currentHandle;
            if (r0 != r1) goto L_0x002c;
        L_0x001c:
            r0 = r12.getRawX();
            r2 = r12.getRawY();
            r3 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r2 = r3.onTouchMove(r0, r2);
            goto L_0x0130;
        L_0x002c:
            if (r0 == 0) goto L_0x0130;
        L_0x002e:
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r0.hasTransformed = r3;
            r0 = new org.telegram.ui.Components.Point;
            r2 = r12.getRawX();
            r5 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r5 = r5.previousLocationX;
            r2 = r2 - r5;
            r5 = r12.getRawY();
            r6 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r6 = r6.previousLocationY;
            r5 = r5 - r6;
            r0.<init>(r2, r5);
            r2 = r11.getRotation();
            r5 = (double) r2;
            r5 = java.lang.Math.toRadians(r5);
            r2 = (float) r5;
            r5 = r0.x;
            r5 = (double) r5;
            r7 = (double) r2;
            r9 = java.lang.Math.cos(r7);
            java.lang.Double.isNaN(r5);
            r5 = r5 * r9;
            r0 = r0.y;
            r9 = (double) r0;
            r7 = java.lang.Math.sin(r7);
            java.lang.Double.isNaN(r9);
            r9 = r9 * r7;
            r5 = r5 + r9;
            r0 = (float) r5;
            r2 = r11.currentHandle;
            if (r2 != r3) goto L_0x007b;
        L_0x0077:
            r2 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
            r0 = r0 * r2;
        L_0x007b:
            r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r0 = r0 * r5;
            r5 = r11.getWidth();
            r5 = (float) r5;
            r0 = r0 / r5;
            r0 = r0 + r2;
            r2 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r2.scale(r0);
            r0 = r11.getLeft();
            r2 = r11.getWidth();
            r2 = r2 / r4;
            r0 = r0 + r2;
            r0 = (float) r0;
            r2 = r11.getTop();
            r5 = r11.getHeight();
            r5 = r5 / r4;
            r2 = r2 + r5;
            r2 = (float) r2;
            r5 = r12.getRawX();
            r6 = r11.getParent();
            r6 = (android.view.View) r6;
            r6 = r6.getLeft();
            r6 = (float) r6;
            r5 = r5 - r6;
            r6 = r12.getRawY();
            r7 = r11.getParent();
            r7 = (android.view.View) r7;
            r7 = r7.getTop();
            r7 = (float) r7;
            r6 = r6 - r7;
            r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
            r7 = (float) r7;
            r6 = r6 - r7;
            r7 = 0;
            r8 = r11.currentHandle;
            if (r8 != r3) goto L_0x00d6;
        L_0x00cc:
            r2 = r2 - r6;
            r6 = (double) r2;
            r0 = r0 - r5;
            r4 = (double) r0;
            r4 = java.lang.Math.atan2(r6, r4);
        L_0x00d4:
            r7 = (float) r4;
            goto L_0x00e1;
        L_0x00d6:
            if (r8 != r4) goto L_0x00e1;
        L_0x00d8:
            r6 = r6 - r2;
            r6 = (double) r6;
            r5 = r5 - r0;
            r4 = (double) r5;
            r4 = java.lang.Math.atan2(r6, r4);
            goto L_0x00d4;
        L_0x00e1:
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r4 = (double) r7;
            r4 = java.lang.Math.toDegrees(r4);
            r2 = (float) r4;
            r0.rotate(r2);
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r2 = r12.getRawX();
            r0.previousLocationX = r2;
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r2 = r12.getRawY();
            r0.previousLocationY = r2;
            goto L_0x0106;
        L_0x00ff:
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r0.onTouchUp();
            r11.currentHandle = r2;
        L_0x0106:
            r2 = 1;
            goto L_0x0130;
        L_0x0108:
            r0 = r12.getX();
            r4 = r12.getY();
            r0 = r11.pointInsideHandle(r0, r4);
            if (r0 == 0) goto L_0x0130;
        L_0x0116:
            r11.currentHandle = r0;
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r4 = r12.getRawX();
            r0.previousLocationX = r4;
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r4 = r12.getRawY();
            r0.previousLocationY = r4;
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r0.hasReleased = r2;
            goto L_0x0106;
        L_0x0130:
            r0 = r11.currentHandle;
            if (r0 != r1) goto L_0x013d;
        L_0x0134:
            r0 = org.telegram.ui.Components.Paint.Views.EntityView.this;
            r0 = r0.gestureDetector;
            r0.onTouchEvent(r12);
        L_0x013d:
            return r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView$SelectionView.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }

    /* Access modifiers changed, original: protected */
    public SelectionView createSelectionView() {
        return null;
    }

    public EntityView(Context context, Point point) {
        super(context);
        this.position = point;
        this.gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
            public void onLongPress(MotionEvent motionEvent) {
                if (!EntityView.this.hasPanned && !EntityView.this.hasTransformed && !EntityView.this.hasReleased) {
                    EntityView.this.recognizedLongPress = true;
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

    private boolean onTouchMove(float f, float f2) {
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

    private void onTouchUp() {
        if (!(this.recognizedLongPress || this.hasPanned || this.hasTransformed || this.announcedSelection)) {
            EntityViewDelegate entityViewDelegate = this.delegate;
            if (entityViewDelegate != null) {
                entityViewDelegate.onEntitySelected(this);
            }
        }
        this.recognizedLongPress = false;
        this.hasPanned = false;
        this.hasTransformed = false;
        this.hasReleased = true;
        this.announcedSelection = false;
    }

    /* JADX WARNING: Missing block: B:14:0x002b, code skipped:
            if (r4 != 6) goto L_0x004e;
     */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
        r6 = this;
        r0 = r7.getPointerCount();
        r1 = 0;
        r2 = 1;
        if (r0 > r2) goto L_0x0053;
    L_0x0008:
        r0 = r6.delegate;
        r0 = r0.allowInteraction(r6);
        if (r0 != 0) goto L_0x0011;
    L_0x0010:
        goto L_0x0053;
    L_0x0011:
        r0 = r7.getRawX();
        r3 = r7.getRawY();
        r4 = r7.getActionMasked();
        if (r4 == 0) goto L_0x0038;
    L_0x001f:
        if (r4 == r2) goto L_0x0033;
    L_0x0021:
        r5 = 2;
        if (r4 == r5) goto L_0x002e;
    L_0x0024:
        r5 = 3;
        if (r4 == r5) goto L_0x0033;
    L_0x0027:
        r5 = 5;
        if (r4 == r5) goto L_0x0038;
    L_0x002a:
        r0 = 6;
        if (r4 == r0) goto L_0x0033;
    L_0x002d:
        goto L_0x004e;
    L_0x002e:
        r1 = r6.onTouchMove(r0, r3);
        goto L_0x004e;
    L_0x0033:
        r6.onTouchUp();
    L_0x0036:
        r1 = 1;
        goto L_0x004e;
    L_0x0038:
        r4 = r6.isSelected();
        if (r4 != 0) goto L_0x0047;
    L_0x003e:
        r4 = r6.delegate;
        if (r4 == 0) goto L_0x0047;
    L_0x0042:
        r4.onEntitySelected(r6);
        r6.announcedSelection = r2;
    L_0x0047:
        r6.previousLocationX = r0;
        r6.previousLocationY = r3;
        r6.hasReleased = r1;
        goto L_0x0036;
    L_0x004e:
        r0 = r6.gestureDetector;
        r0.onTouchEvent(r7);
    L_0x0053:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.EntityView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void pan(Point point) {
        Point point2 = this.position;
        point2.x += point.x;
        point2.y += point.y;
        updatePosition();
    }

    /* Access modifiers changed, original: protected */
    public void updatePosition() {
        float height = ((float) getHeight()) / 2.0f;
        setX(this.position.x - (((float) getWidth()) / 2.0f));
        setY(this.position.y - height);
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

    /* Access modifiers changed, original: protected */
    public Rect getSelectionBounds() {
        return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
    }

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
        SelectionView createSelectionView = createSelectionView();
        this.selectionView = createSelectionView;
        viewGroup.addView(createSelectionView);
        createSelectionView.updatePosition();
    }

    public void deselect() {
        SelectionView selectionView = this.selectionView;
        if (selectionView != null) {
            if (selectionView.getParent() != null) {
                ((ViewGroup) this.selectionView.getParent()).removeView(this.selectionView);
            }
            this.selectionView = null;
        }
    }

    public void setSelectionVisibility(boolean z) {
        SelectionView selectionView = this.selectionView;
        if (selectionView != null) {
            selectionView.setVisibility(z ? 0 : 8);
        }
    }
}
