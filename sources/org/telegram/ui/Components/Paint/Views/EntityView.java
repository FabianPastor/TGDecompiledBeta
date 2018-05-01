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

    /* renamed from: org.telegram.ui.Components.Paint.Views.EntityView$1 */
    class C12221 extends SimpleOnGestureListener {
        C12221() {
        }

        public void onLongPress(MotionEvent motionEvent) {
            if (EntityView.this.hasPanned == null && EntityView.this.hasTransformed == null) {
                if (EntityView.this.hasReleased == null) {
                    EntityView.this.recognizedLongPress = true;
                    if (EntityView.this.delegate != null) {
                        EntityView.this.performHapticFeedback(0);
                        EntityView.this.delegate.onEntityLongClicked(EntityView.this);
                    }
                }
            }
        }
    }

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

        protected int pointInsideHandle(float f, float f2) {
            return 0;
        }

        public SelectionView(Context context) {
            super(context);
            setWillNotDraw(null);
            this.paint.setColor(-1);
            this.dotPaint.setColor(-12793105);
            this.dotStrokePaint.setColor(-1);
            this.dotStrokePaint.setStyle(Style.STROKE);
            this.dotStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        }

        protected void updatePosition() {
            Rect selectionBounds = EntityView.this.getSelectionBounds();
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.leftMargin = ((int) selectionBounds.f26x) + EntityView.this.offsetX;
            layoutParams.topMargin = ((int) selectionBounds.f27y) + EntityView.this.offsetY;
            layoutParams.width = (int) selectionBounds.width;
            layoutParams.height = (int) selectionBounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z = false;
            switch (motionEvent.getActionMasked()) {
                case 0:
                case 5:
                    int pointInsideHandle = pointInsideHandle(motionEvent.getX(), motionEvent.getY());
                    if (pointInsideHandle != 0) {
                        this.currentHandle = pointInsideHandle;
                        EntityView.this.previousLocationX = motionEvent.getRawX();
                        EntityView.this.previousLocationY = motionEvent.getRawY();
                        EntityView.this.hasReleased = false;
                    }
                    break;
                case 1:
                case 3:
                case 6:
                    EntityView.this.onTouchUp();
                    this.currentHandle = 0;
                    z = true;
                    break;
                case 2:
                    if (this.currentHandle == 3) {
                        z = EntityView.this.onTouchMove(motionEvent.getRawX(), motionEvent.getRawY());
                        break;
                    } else if (this.currentHandle != 0) {
                        EntityView.this.hasTransformed = true;
                        Point point = new Point(motionEvent.getRawX() - EntityView.this.previousLocationX, motionEvent.getRawY() - EntityView.this.previousLocationY);
                        double toRadians = (double) ((float) Math.toRadians((double) getRotation()));
                        float cos = (float) ((((double) point.f24x) * Math.cos(toRadians)) + (((double) point.f25y) * Math.sin(toRadians)));
                        if (this.currentHandle == 1) {
                            cos *= -1.0f;
                        }
                        EntityView.this.scale(1.0f + ((cos * 2.0f) / ((float) getWidth())));
                        cos = (float) (getLeft() + (getWidth() / 2));
                        float top = (float) (getTop() + (getHeight() / 2));
                        float rawX = motionEvent.getRawX() - ((float) ((View) getParent()).getLeft());
                        float rawY = (motionEvent.getRawY() - ((float) ((View) getParent()).getTop())) - ((float) AndroidUtilities.statusBarHeight);
                        float f = 0.0f;
                        if (this.currentHandle == 1) {
                            f = (float) Math.atan2((double) (top - rawY), (double) (cos - rawX));
                        } else if (this.currentHandle == 2) {
                            f = (float) Math.atan2((double) (rawY - top), (double) (rawX - cos));
                        }
                        EntityView.this.rotate((float) Math.toDegrees((double) f));
                        EntityView.this.previousLocationX = motionEvent.getRawX();
                        EntityView.this.previousLocationY = motionEvent.getRawY();
                    }
                    break;
                default:
                    break;
            }
            z = true;
            if (this.currentHandle == 3) {
                EntityView.this.gestureDetector.onTouchEvent(motionEvent);
            }
            return z;
        }
    }

    protected SelectionView createSelectionView() {
        return null;
    }

    public EntityView(Context context, Point point) {
        super(context);
        this.position = point;
        this.gestureDetector = new GestureDetector(context, new C12221());
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
        if (((float) Math.hypot((double) point.f24x, (double) point.f25y)) <= (this.hasPanned ? 6.0f : 16.0f)) {
            return false;
        }
        pan(point);
        this.previousLocationX = f;
        this.previousLocationY = f2;
        this.hasPanned = true;
        return true;
    }

    private void onTouchUp() {
        if (!(this.recognizedLongPress || this.hasPanned || this.hasTransformed || this.announcedSelection || this.delegate == null)) {
            this.delegate.onEntitySelected(this);
        }
        this.recognizedLongPress = false;
        this.hasPanned = false;
        this.hasTransformed = false;
        this.hasReleased = true;
        this.announcedSelection = false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (motionEvent.getPointerCount() <= 1) {
            if (this.delegate.allowInteraction(this)) {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                switch (motionEvent.getActionMasked()) {
                    case 0:
                    case 5:
                        if (!(isSelected() || this.delegate == null)) {
                            this.delegate.onEntitySelected(this);
                            this.announcedSelection = true;
                        }
                        this.previousLocationX = rawX;
                        this.previousLocationY = rawY;
                        this.hasReleased = false;
                        break;
                    case 1:
                    case 3:
                    case 6:
                        onTouchUp();
                        break;
                    case 2:
                        z = onTouchMove(rawX, rawY);
                        break;
                    default:
                        break;
                }
                z = true;
                this.gestureDetector.onTouchEvent(motionEvent);
                return z;
            }
        }
        return false;
    }

    public void pan(Point point) {
        Point point2 = this.position;
        point2.f24x += point.f24x;
        point2 = this.position;
        point2.f25y += point.f25y;
        updatePosition();
    }

    protected void updatePosition() {
        float height = ((float) getHeight()) / 2.0f;
        setX(this.position.f24x - (((float) getWidth()) / 2.0f));
        setY(this.position.f25y - height);
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

    public boolean isSelected() {
        return this.selectionView != null;
    }

    public void updateSelectionView() {
        if (this.selectionView != null) {
            this.selectionView.updatePosition();
        }
    }

    public void select(ViewGroup viewGroup) {
        View createSelectionView = createSelectionView();
        this.selectionView = createSelectionView;
        viewGroup.addView(createSelectionView);
        createSelectionView.updatePosition();
    }

    public void deselect() {
        if (this.selectionView != null) {
            if (this.selectionView.getParent() != null) {
                ((ViewGroup) this.selectionView.getParent()).removeView(this.selectionView);
            }
            this.selectionView = null;
        }
    }

    public void setSelectionVisibility(boolean z) {
        if (this.selectionView != null) {
            this.selectionView.setVisibility(z ? false : true);
        }
    }
}
