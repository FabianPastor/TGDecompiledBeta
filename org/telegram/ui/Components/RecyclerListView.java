package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.lang.reflect.Field;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.AdapterDataObserver;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.ActionBar.Theme;

public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    private Runnable clickRunnable;
    private int currentChildPosition;
    private View currentChildView;
    private boolean disallowInterceptTouchEvents;
    private View emptyView;
    private FastScroll fastScroll;
    private boolean ignoreOnScroll;
    private boolean instantClick;
    private boolean interceptedByChild;
    private GestureDetector mGestureDetector;
    private AdapterDataObserver observer = new AdapterDataObserver() {
        public void onChanged() {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            RecyclerListView.this.checkIfEmpty();
        }
    };
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnScrollListener onScrollListener;
    private Runnable selectChildRunnable;
    private boolean wasPressed;

    private class FastScroll extends View {
        private float bubbleProgress;
        private int[] colors = new int[6];
        private String currentLetter;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint = new TextPaint(1);
        private Paint paint = new Paint(1);
        private Path path = new Path();
        private boolean pressed;
        private float progress;
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private int scrollX;
        private float startDy;
        private float textX;
        private float textY;

        public FastScroll(Context context) {
            super(context);
            this.paint.setColor(Theme.GROUP_CREATE_INACTIVE_SCROLL_COLOR);
            this.letterPaint.setColor(-1);
            this.letterPaint.setTextSize((float) AndroidUtilities.dp(45.0f));
            for (int a = 0; a < 8; a++) {
                this.radii[a] = (float) AndroidUtilities.dp(44.0f);
            }
            this.colors[0] = Color.red(Theme.GROUP_CREATE_INACTIVE_SCROLL_COLOR);
            this.colors[1] = Color.red(-11361317);
            this.colors[2] = Color.green(Theme.GROUP_CREATE_INACTIVE_SCROLL_COLOR);
            this.colors[3] = Color.green(-11361317);
            this.colors[4] = Color.blue(Theme.GROUP_CREATE_INACTIVE_SCROLL_COLOR);
            this.colors[5] = Color.blue(-11361317);
            this.scrollX = LocaleController.isRTL ? AndroidUtilities.dp(10.0f) : AndroidUtilities.dp(117.0f);
        }

        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    float x = event.getX();
                    this.lastY = event.getY();
                    float currectY = ((float) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress))) + ((float) AndroidUtilities.dp(12.0f));
                    if ((LocaleController.isRTL && x > ((float) AndroidUtilities.dp(25.0f))) || ((!LocaleController.isRTL && x < ((float) AndroidUtilities.dp(107.0f))) || this.lastY < currectY || this.lastY > ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) + currectY)) {
                        return false;
                    }
                    this.startDy = this.lastY - currectY;
                    this.pressed = true;
                    this.lastUpdateTime = System.currentTimeMillis();
                    getCurrentLetter();
                    invalidate();
                    return true;
                case 1:
                case 3:
                    this.pressed = false;
                    this.lastUpdateTime = System.currentTimeMillis();
                    invalidate();
                    return true;
                case 2:
                    if (!this.pressed) {
                        return true;
                    }
                    float newY = event.getY();
                    float minY = ((float) AndroidUtilities.dp(12.0f)) + this.startDy;
                    float maxY = ((float) (getMeasuredHeight() - AndroidUtilities.dp(42.0f))) + this.startDy;
                    if (newY < minY) {
                        newY = minY;
                    } else if (newY > maxY) {
                        newY = maxY;
                    }
                    float dy = newY - this.lastY;
                    this.lastY = newY;
                    this.progress += dy / ((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f)));
                    if (this.progress < 0.0f) {
                        this.progress = 0.0f;
                    } else if (this.progress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        this.progress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    }
                    getCurrentLetter();
                    invalidate();
                    return true;
                default:
                    return super.onTouchEvent(event);
            }
        }

        private void getCurrentLetter() {
            LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    Adapter adapter = RecyclerListView.this.getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        int position = fastScrollAdapter.getPositionForScrollProgress(this.progress);
                        linearLayoutManager.scrollToPositionWithOffset(position, 0);
                        String newLetter = fastScrollAdapter.getLetter(position);
                        if (newLetter == null) {
                            this.letterLayout = null;
                        } else if (!newLetter.equals(this.currentLetter)) {
                            this.letterLayout = new StaticLayout(newLetter, this.letterPaint, 1000, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                            if (this.letterLayout.getLineCount() > 0) {
                                if (LocaleController.isRTL) {
                                    float lWidth = this.letterLayout.getLineWidth(0);
                                    float lleft = this.letterLayout.getLineLeft(0);
                                    this.textX = ((float) AndroidUtilities.dp(10.0f)) + ((((float) AndroidUtilities.dp(88.0f)) - (this.letterLayout.getLineWidth(0) - this.letterLayout.getLineLeft(0))) / 2.0f);
                                } else {
                                    this.textX = (((float) AndroidUtilities.dp(88.0f)) - (this.letterLayout.getLineWidth(0) - this.letterLayout.getLineLeft(0))) / 2.0f;
                                }
                                this.textY = (float) ((AndroidUtilities.dp(88.0f) - this.letterLayout.getHeight()) / 2);
                            }
                        }
                    }
                }
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(132.0f), MeasureSpec.getSize(heightMeasureSpec));
        }

        protected void onDraw(Canvas canvas) {
            this.paint.setColor(Color.argb(255, this.colors[0] + ((int) (((float) (this.colors[1] - this.colors[0])) * this.bubbleProgress)), this.colors[2] + ((int) (((float) (this.colors[3] - this.colors[2])) * this.bubbleProgress)), this.colors[4] + ((int) (((float) (this.colors[5] - this.colors[4])) * this.bubbleProgress))));
            int y = (int) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress));
            this.rect.set((float) this.scrollX, (float) (AndroidUtilities.dp(12.0f) + y), (float) (this.scrollX + AndroidUtilities.dp(5.0f)), (float) (AndroidUtilities.dp(42.0f) + y));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
            if ((this.pressed || this.bubbleProgress != 0.0f) && this.letterLayout != null) {
                float raduisTop;
                float raduisBottom;
                this.paint.setAlpha((int) (255.0f * this.bubbleProgress));
                int progressY = y + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                y -= AndroidUtilities.dp(46.0f);
                float diff = 0.0f;
                if (y <= AndroidUtilities.dp(12.0f)) {
                    diff = (float) (AndroidUtilities.dp(12.0f) - y);
                    y = AndroidUtilities.dp(12.0f);
                }
                canvas.translate((float) AndroidUtilities.dp(10.0f), (float) y);
                if (diff <= ((float) AndroidUtilities.dp(29.0f))) {
                    raduisTop = (float) AndroidUtilities.dp(44.0f);
                    raduisBottom = ((float) AndroidUtilities.dp(4.0f)) + ((diff / ((float) AndroidUtilities.dp(29.0f))) * ((float) AndroidUtilities.dp(40.0f)));
                } else {
                    raduisBottom = (float) AndroidUtilities.dp(44.0f);
                    raduisTop = ((float) AndroidUtilities.dp(4.0f)) + ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - ((diff - ((float) AndroidUtilities.dp(29.0f))) / ((float) AndroidUtilities.dp(29.0f)))) * ((float) AndroidUtilities.dp(40.0f)));
                }
                if ((LocaleController.isRTL && !(this.radii[0] == raduisTop && this.radii[6] == raduisBottom)) || !(LocaleController.isRTL || (this.radii[2] == raduisTop && this.radii[4] == raduisBottom))) {
                    float f;
                    float[] fArr;
                    if (LocaleController.isRTL) {
                        fArr = this.radii;
                        this.radii[1] = raduisTop;
                        fArr[0] = raduisTop;
                        fArr = this.radii;
                        this.radii[7] = raduisBottom;
                        fArr[6] = raduisBottom;
                    } else {
                        fArr = this.radii;
                        this.radii[3] = raduisTop;
                        fArr[2] = raduisTop;
                        fArr = this.radii;
                        this.radii[5] = raduisBottom;
                        fArr[4] = raduisBottom;
                    }
                    this.path.reset();
                    RectF rectF = this.rect;
                    float dp = LocaleController.isRTL ? (float) AndroidUtilities.dp(10.0f) : 0.0f;
                    if (LocaleController.isRTL) {
                        f = 98.0f;
                    } else {
                        f = 88.0f;
                    }
                    rectF.set(dp, 0.0f, (float) AndroidUtilities.dp(f), (float) AndroidUtilities.dp(88.0f));
                    this.path.addRoundRect(this.rect, this.radii, Direction.CW);
                    this.path.close();
                }
                canvas.save();
                canvas.scale(this.bubbleProgress, this.bubbleProgress, (float) this.scrollX, (float) (progressY - y));
                canvas.drawPath(this.path, this.paint);
                canvas.translate(this.textX, this.textY);
                this.letterLayout.draw(canvas);
                canvas.restore();
            }
            if ((this.pressed && this.bubbleProgress < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) || (!this.pressed && this.bubbleProgress > 0.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                if (dt < 0 || dt > 17) {
                    dt = 17;
                }
                this.lastUpdateTime = newTime;
                invalidate();
                if (this.pressed) {
                    this.bubbleProgress += ((float) dt) / BitmapDescriptorFactory.HUE_GREEN;
                    if (this.bubbleProgress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        this.bubbleProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                        return;
                    }
                    return;
                }
                this.bubbleProgress -= ((float) dt) / BitmapDescriptorFactory.HUE_GREEN;
                if (this.bubbleProgress < 0.0f) {
                    this.bubbleProgress = 0.0f;
                }
            }
        }

        private void setProgress(float value) {
            this.progress = value;
            invalidate();
        }
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    public static abstract class FastScrollAdapter extends Adapter {
        public abstract String getLetter(int i);

        public abstract int getPositionForScrollProgress(float f);
    }

    private class RecyclerListViewItemClickListener implements OnItemTouchListener {
        public RecyclerListViewItemClickListener(Context context) {
            RecyclerListView.this.mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener(RecyclerListView.this) {
                public boolean onSingleTapUp(MotionEvent e) {
                    if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.onItemClickListener == null)) {
                        RecyclerListView.this.currentChildView.setPressed(true);
                        final View view = RecyclerListView.this.currentChildView;
                        if (RecyclerListView.this.instantClick) {
                            view.playSoundEffect(0);
                            RecyclerListView.this.onItemClickListener.onItemClick(view, RecyclerListView.this.currentChildPosition);
                        }
                        AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() {
                            public void run() {
                                if (this == RecyclerListView.this.clickRunnable) {
                                    RecyclerListView.this.clickRunnable = null;
                                }
                                if (view != null) {
                                    view.setPressed(false);
                                    if (!RecyclerListView.this.instantClick) {
                                        view.playSoundEffect(0);
                                        if (RecyclerListView.this.onItemClickListener != null) {
                                            RecyclerListView.this.onItemClickListener.onItemClick(view, RecyclerListView.this.currentChildPosition);
                                        }
                                    }
                                }
                            }
                        }, (long) ViewConfiguration.getPressedStateDuration());
                        if (RecyclerListView.this.selectChildRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                            RecyclerListView.this.selectChildRunnable = null;
                            RecyclerListView.this.currentChildView = null;
                            RecyclerListView.this.interceptedByChild = false;
                        }
                    }
                    return true;
                }

                public void onLongPress(MotionEvent event) {
                    if (RecyclerListView.this.currentChildView != null) {
                        View child = RecyclerListView.this.currentChildView;
                        if (RecyclerListView.this.onItemLongClickListener != null && RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                            child.performHapticFeedback(0);
                        }
                    }
                }
            });
        }

        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
            int action = event.getActionMasked();
            boolean isScrollIdle = RecyclerListView.this.getScrollState() == 0;
            if ((action == 0 || action == 5) && RecyclerListView.this.currentChildView == null && isScrollIdle) {
                RecyclerListView.this.currentChildView = view.findChildViewUnder(event.getX(), event.getY());
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    float x = event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    float y = event.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                        View child = viewGroup.getChildAt(i);
                        if (x >= ((float) child.getLeft()) && x <= ((float) child.getRight()) && y >= ((float) child.getTop()) && y <= ((float) child.getBottom()) && child.isClickable()) {
                            RecyclerListView.this.currentChildView = null;
                            break;
                        }
                    }
                }
                RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView.this.currentChildPosition = view.getChildPosition(RecyclerListView.this.currentChildView);
                    MotionEvent childEvent = MotionEvent.obtain(0, 0, event.getActionMasked(), event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), event.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(childEvent)) {
                        RecyclerListView.this.interceptedByChild = true;
                    }
                    childEvent.recycle();
                }
            }
            if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.interceptedByChild || event == null)) {
                try {
                    RecyclerListView.this.mGestureDetector.onTouchEvent(event);
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
            if (action == 0 || action == 5) {
                if (!(RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null)) {
                    RecyclerListView.this.selectChildRunnable = new Runnable() {
                        public void run() {
                            if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                                RecyclerListView.this.currentChildView.setPressed(true);
                                RecyclerListView.this.selectChildRunnable = null;
                            }
                        }
                    };
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                }
            } else if (RecyclerListView.this.currentChildView != null && (action == 1 || action == 6 || action == 3 || !isScrollIdle)) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                RecyclerListView.this.currentChildView.setPressed(false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
            }
            return false;
        }

        public void onTouchEvent(RecyclerView view, MotionEvent event) {
        }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    public void cancelClickRunnables(boolean uncheck) {
        if (this.selectChildRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.selectChildRunnable);
            this.selectChildRunnable = null;
        }
        if (this.currentChildView != null) {
            if (uncheck) {
                this.currentChildView.setPressed(false);
            }
            this.currentChildView = null;
        }
        if (this.clickRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.clickRunnable);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(String packageName, String name) {
        try {
            Field f = Class.forName(packageName + ".R$styleable").getField(name);
            if (f != null) {
                return (int[]) f.get(null);
            }
        } catch (Throwable th) {
        }
        return null;
    }

    public RecyclerListView(Context context) {
        super(context);
        try {
            if (!gotAttributes) {
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray a = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{a});
            a.recycle();
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        super.setOnScrollListener(new OnScrollListener() {
            boolean scrollingByUser;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = false;
                if (!(newState == 0 || RecyclerListView.this.currentChildView == null)) {
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        RecyclerListView.this.selectChildRunnable = null;
                    }
                    MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    try {
                        RecyclerListView.this.mGestureDetector.onTouchEvent(event);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                    RecyclerListView.this.currentChildView.onTouchEvent(event);
                    event.recycle();
                    RecyclerListView.this.currentChildView.setPressed(false);
                    RecyclerListView.this.currentChildView = null;
                    RecyclerListView.this.interceptedByChild = false;
                }
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                if (newState == 1 || newState == 2) {
                    z = true;
                }
                this.scrollingByUser = z;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrolled(recyclerView, dx, dy);
                }
                if (this.scrollingByUser && RecyclerListView.this.fastScroll != null) {
                    LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        if (linearLayoutManager.getOrientation() == 1) {
                            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                            Adapter adapter = RecyclerListView.this.getAdapter();
                            if (adapter instanceof FastScrollAdapter) {
                                RecyclerListView.this.fastScroll.setProgress(((float) firstVisibleItem) / ((float) adapter.getItemCount()));
                            }
                        }
                    }
                }
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
        }
    }

    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (this.fastScroll != null) {
            this.fastScroll.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C.ENCODING_PCM_32BIT));
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.fastScroll == null) {
            return;
        }
        if (LocaleController.isRTL) {
            this.fastScroll.layout(0, t, this.fastScroll.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + t);
            return;
        }
        int x = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
        this.fastScroll.layout(x, t, this.fastScroll.getMeasuredWidth() + x, this.fastScroll.getMeasuredHeight() + t);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void setEmptyView(View view) {
        if (this.emptyView != view) {
            this.emptyView = view;
            checkIfEmpty();
        }
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public void invalidateViews() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).invalidate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        if ((this.onInterceptTouchListener == null || !this.onInterceptTouchListener.onInterceptTouchEvent(e)) && !super.onInterceptTouchEvent(e)) {
            return false;
        }
        return true;
    }

    private void checkIfEmpty() {
        int i = 0;
        if (this.emptyView != null && getAdapter() != null) {
            boolean emptyViewVisible;
            if (getAdapter().getItemCount() == 0) {
                emptyViewVisible = true;
            } else {
                emptyViewVisible = false;
            }
            this.emptyView.setVisibility(emptyViewVisible ? 0 : 8);
            if (emptyViewVisible) {
                i = 4;
            }
            setVisibility(i);
        }
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        this.onInterceptTouchListener = listener;
    }

    public void setInstantClick(boolean value) {
        this.instantClick = value;
    }

    public void setDisallowInterceptTouchEvents(boolean value) {
        this.disallowInterceptTouchEvents = value;
    }

    public void setFastScrollEnabled() {
        this.fastScroll = new FastScroll(getContext());
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setFastScrollVisible(boolean value) {
        if (this.fastScroll != null) {
            this.fastScroll.setVisibility(value ? 0 : 8);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.fastScroll != null && this.fastScroll.getParent() != getParent()) {
            ViewGroup parent = (ViewGroup) this.fastScroll.getParent();
            if (parent != null) {
                parent.removeView(this.fastScroll);
            }
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(this.observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.observer);
        }
        checkIfEmpty();
    }

    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException e) {
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
