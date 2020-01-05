package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    private boolean allowItemsInteractionDuringAnimation = true;
    public boolean animationRunning;
    private Runnable clickRunnable;
    private int currentChildPosition;
    private View currentChildView;
    private int currentFirst = -1;
    private int currentVisible = -1;
    private boolean disableHighlightState;
    private boolean disallowInterceptTouchEvents;
    private View emptyView;
    private FastScroll fastScroll;
    private GestureDetector gestureDetector;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean hideIfEmpty = true;
    private boolean ignoreOnScroll;
    private boolean instantClick;
    private boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private boolean isHidden;
    private long lastAlphaAnimationTime;
    private boolean longPressCalled;
    private AdapterDataObserver observer = new AdapterDataObserver() {
        public void onChanged() {
            RecyclerListView.this.checkIfEmpty();
            RecyclerListView.this.currentFirst = -1;
            if (RecyclerListView.this.removeHighlighSelectionRunnable == null) {
                RecyclerListView.this.selectorRect.setEmpty();
            }
            RecyclerListView.this.invalidate();
        }

        public void onItemRangeInserted(int i, int i2) {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeRemoved(int i, int i2) {
            RecyclerListView.this.checkIfEmpty();
        }
    };
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListenerExtended onItemClickListenerExtended;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    private OnScrollListener onScrollListener;
    private FrameLayout overlayContainer;
    private IntReturnCallback pendingHighlightPosition;
    private View pinnedHeader;
    private float pinnedHeaderShadowAlpha;
    private Drawable pinnedHeaderShadowDrawable;
    private float pinnedHeaderShadowTargetAlpha;
    private Runnable removeHighlighSelectionRunnable;
    private boolean scrollEnabled = true;
    private boolean scrollingByUser;
    private int sectionOffset;
    private SectionsAdapter sectionsAdapter;
    private int sectionsCount;
    private int sectionsType;
    private Runnable selectChildRunnable;
    protected Drawable selectorDrawable;
    protected int selectorPosition;
    protected Rect selectorRect = new Rect();
    private boolean selfOnLayout;
    private int startSection;
    private boolean wasPressed;

    private class FastScroll extends View {
        private float bubbleProgress;
        private int[] colors = new int[6];
        private String currentLetter;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint = new TextPaint(1);
        private StaticLayout oldLetterLayout;
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
            this.letterPaint.setTextSize((float) AndroidUtilities.dp(45.0f));
            for (int i = 0; i < 8; i++) {
                this.radii[i] = (float) AndroidUtilities.dp(44.0f);
            }
            this.scrollX = AndroidUtilities.dp(LocaleController.isRTL ? 10.0f : 117.0f);
            updateColors();
        }

        private void updateColors() {
            int color = Theme.getColor("fastScrollInactive");
            int color2 = Theme.getColor("fastScrollActive");
            this.paint.setColor(color);
            this.letterPaint.setColor(Theme.getColor("fastScrollText"));
            this.colors[0] = Color.red(color);
            this.colors[1] = Color.red(color2);
            this.colors[2] = Color.green(color);
            this.colors[3] = Color.green(color2);
            this.colors[4] = Color.blue(color);
            this.colors[5] = Color.blue(color2);
            invalidate();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            float y;
            float dp;
            if (action != 0) {
                if (action != 1) {
                    if (action != 2) {
                        if (action != 3) {
                            return super.onTouchEvent(motionEvent);
                        }
                    } else if (!this.pressed) {
                        return true;
                    } else {
                        y = motionEvent.getY();
                        dp = ((float) AndroidUtilities.dp(12.0f)) + this.startDy;
                        float measuredHeight = ((float) (getMeasuredHeight() - AndroidUtilities.dp(42.0f))) + this.startDy;
                        if (y < dp) {
                            y = dp;
                        } else if (y > measuredHeight) {
                            y = measuredHeight;
                        }
                        dp = y - this.lastY;
                        this.lastY = y;
                        this.progress += dp / ((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f)));
                        y = this.progress;
                        if (y < 0.0f) {
                            this.progress = 0.0f;
                        } else if (y > 1.0f) {
                            this.progress = 1.0f;
                        }
                        getCurrentLetter();
                        invalidate();
                        return true;
                    }
                }
                this.pressed = false;
                this.lastUpdateTime = System.currentTimeMillis();
                invalidate();
                return true;
            }
            dp = motionEvent.getX();
            this.lastY = motionEvent.getY();
            y = ((float) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress))) + ((float) AndroidUtilities.dp(12.0f));
            if ((!LocaleController.isRTL || dp <= ((float) AndroidUtilities.dp(25.0f))) && (LocaleController.isRTL || dp >= ((float) AndroidUtilities.dp(107.0f)))) {
                dp = this.lastY;
                if (dp >= y && dp <= ((float) AndroidUtilities.dp(30.0f)) + y) {
                    this.startDy = this.lastY - y;
                    this.pressed = true;
                    this.lastUpdateTime = System.currentTimeMillis();
                    getCurrentLetter();
                    invalidate();
                    return true;
                }
            }
            return false;
        }

        private void getCurrentLetter() {
            LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    Adapter adapter = RecyclerListView.this.getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        int positionForScrollProgress = fastScrollAdapter.getPositionForScrollProgress(this.progress);
                        linearLayoutManager.scrollToPositionWithOffset(positionForScrollProgress, RecyclerListView.this.sectionOffset);
                        String letter = fastScrollAdapter.getLetter(positionForScrollProgress);
                        if (letter == null) {
                            StaticLayout staticLayout = this.letterLayout;
                            if (staticLayout != null) {
                                this.oldLetterLayout = staticLayout;
                            }
                            this.letterLayout = null;
                        } else if (!letter.equals(this.currentLetter)) {
                            this.letterLayout = new StaticLayout(letter, this.letterPaint, 1000, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.oldLetterLayout = null;
                            if (this.letterLayout.getLineCount() > 0) {
                                this.letterLayout.getLineWidth(0);
                                this.letterLayout.getLineLeft(0);
                                if (LocaleController.isRTL) {
                                    this.textX = (((float) AndroidUtilities.dp(10.0f)) + ((((float) AndroidUtilities.dp(88.0f)) - this.letterLayout.getLineWidth(0)) / 2.0f)) - this.letterLayout.getLineLeft(0);
                                } else {
                                    this.textX = ((((float) AndroidUtilities.dp(88.0f)) - this.letterLayout.getLineWidth(0)) / 2.0f) - this.letterLayout.getLineLeft(0);
                                }
                                this.textY = (float) ((AndroidUtilities.dp(88.0f) - this.letterLayout.getHeight()) / 2);
                            }
                        }
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(132.0f), MeasureSpec.getSize(i2));
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x0151  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0144  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x016c  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0166  */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0174  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0171  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x019b  */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x019f  */
        /* JADX WARNING: Missing block: B:17:0x012c, code skipped:
            if (r8[6] == r9) goto L_0x012e;
     */
        /* JADX WARNING: Missing block: B:23:0x013e, code skipped:
            if (r8[4] == r9) goto L_0x0196;
     */
        public void onDraw(android.graphics.Canvas r21) {
            /*
            r20 = this;
            r0 = r20;
            r1 = r21;
            r2 = r0.paint;
            r3 = r0.colors;
            r4 = 0;
            r5 = r3[r4];
            r6 = 1;
            r7 = r3[r6];
            r8 = r3[r4];
            r7 = r7 - r8;
            r7 = (float) r7;
            r8 = r0.bubbleProgress;
            r7 = r7 * r8;
            r7 = (int) r7;
            r5 = r5 + r7;
            r7 = 2;
            r9 = r3[r7];
            r10 = 3;
            r11 = r3[r10];
            r12 = r3[r7];
            r11 = r11 - r12;
            r11 = (float) r11;
            r11 = r11 * r8;
            r11 = (int) r11;
            r9 = r9 + r11;
            r11 = 4;
            r12 = r3[r11];
            r13 = 5;
            r14 = r3[r13];
            r3 = r3[r11];
            r14 = r14 - r3;
            r3 = (float) r14;
            r3 = r3 * r8;
            r3 = (int) r3;
            r12 = r12 + r3;
            r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
            r3 = android.graphics.Color.argb(r3, r5, r9, r12);
            r2.setColor(r3);
            r2 = r20.getMeasuredHeight();
            r3 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r2 = r2 - r3;
            r2 = (float) r2;
            r3 = r0.progress;
            r2 = r2 * r3;
            r2 = (double) r2;
            r2 = java.lang.Math.ceil(r2);
            r2 = (int) r2;
            r3 = r0.rect;
            r5 = r0.scrollX;
            r5 = (float) r5;
            r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
            r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r9 = r9 + r2;
            r9 = (float) r9;
            r12 = r0.scrollX;
            r14 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r12 = r12 + r14;
            r12 = (float) r12;
            r14 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r14 = r14 + r2;
            r14 = (float) r14;
            r3.set(r5, r9, r12, r14);
            r3 = r0.rect;
            r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r9 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r9 = (float) r9;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r5 = (float) r5;
            r12 = r0.paint;
            r1.drawRoundRect(r3, r9, r5, r12);
            r3 = r0.pressed;
            r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r9 = 0;
            if (r3 != 0) goto L_0x0095;
        L_0x008f:
            r3 = r0.bubbleProgress;
            r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1));
            if (r3 == 0) goto L_0x01c0;
        L_0x0095:
            r3 = r0.paint;
            r12 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
            r14 = r0.bubbleProgress;
            r14 = r14 * r12;
            r12 = (int) r14;
            r3.setAlpha(r12);
            r3 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r3 = r3 + r2;
            r12 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;
            r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r2 = r2 - r12;
            r12 = org.telegram.messenger.AndroidUtilities.dp(r8);
            if (r2 > r12) goto L_0x00c5;
        L_0x00b5:
            r12 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r12 = r12 - r2;
            r2 = (float) r12;
            r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r19 = r8;
            r8 = r2;
            r2 = r19;
            goto L_0x00c6;
        L_0x00c5:
            r8 = 0;
        L_0x00c6:
            r12 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r14 = (float) r14;
            r15 = (float) r2;
            r1.translate(r14, r15);
            r14 = NUM; // 0x41e80000 float:29.0 double:5.46299942E-315;
            r15 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r15 = (float) r15;
            r16 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
            r17 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            r18 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
            r15 = (r8 > r15 ? 1 : (r8 == r15 ? 0 : -1));
            if (r15 > 0) goto L_0x00fb;
        L_0x00e2:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r18);
            r15 = (float) r15;
            r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
            r9 = (float) r9;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r14 = (float) r14;
            r8 = r8 / r14;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r16);
            r14 = (float) r14;
            r8 = r8 * r14;
            r9 = r9 + r8;
            goto L_0x011b;
        L_0x00fb:
            r9 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r9 = (float) r9;
            r8 = r8 - r9;
            r9 = org.telegram.messenger.AndroidUtilities.dp(r18);
            r9 = (float) r9;
            r15 = org.telegram.messenger.AndroidUtilities.dp(r17);
            r15 = (float) r15;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r14 = (float) r14;
            r8 = r8 / r14;
            r8 = r5 - r8;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r16);
            r14 = (float) r14;
            r8 = r8 * r14;
            r15 = r15 + r8;
        L_0x011b:
            r8 = org.telegram.messenger.LocaleController.isRTL;
            if (r8 == 0) goto L_0x012e;
        L_0x011f:
            r8 = r0.radii;
            r14 = r8[r4];
            r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1));
            if (r14 != 0) goto L_0x0140;
        L_0x0127:
            r14 = 6;
            r8 = r8[r14];
            r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1));
            if (r8 != 0) goto L_0x0140;
        L_0x012e:
            r8 = org.telegram.messenger.LocaleController.isRTL;
            if (r8 != 0) goto L_0x0196;
        L_0x0132:
            r8 = r0.radii;
            r14 = r8[r7];
            r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1));
            if (r14 != 0) goto L_0x0140;
        L_0x013a:
            r8 = r8[r11];
            r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1));
            if (r8 == 0) goto L_0x0196;
        L_0x0140:
            r8 = org.telegram.messenger.LocaleController.isRTL;
            if (r8 == 0) goto L_0x0151;
        L_0x0144:
            r7 = r0.radii;
            r7[r6] = r15;
            r7[r4] = r15;
            r4 = 6;
            r6 = 7;
            r7[r6] = r9;
            r7[r4] = r9;
            goto L_0x015b;
        L_0x0151:
            r4 = r0.radii;
            r4[r10] = r15;
            r4[r7] = r15;
            r4[r13] = r9;
            r4[r11] = r9;
        L_0x015b:
            r4 = r0.path;
            r4.reset();
            r4 = r0.rect;
            r6 = org.telegram.messenger.LocaleController.isRTL;
            if (r6 == 0) goto L_0x016c;
        L_0x0166:
            r6 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r9 = (float) r6;
            goto L_0x016d;
        L_0x016c:
            r9 = 0;
        L_0x016d:
            r6 = org.telegram.messenger.LocaleController.isRTL;
            if (r6 == 0) goto L_0x0174;
        L_0x0171:
            r6 = NUM; // 0x42CLASSNAME float:98.0 double:5.534233407E-315;
            goto L_0x0176;
        L_0x0174:
            r6 = NUM; // 0x42b00000 float:88.0 double:5.52775759E-315;
        L_0x0176:
            r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r6 = (float) r6;
            r7 = NUM; // 0x42b00000 float:88.0 double:5.52775759E-315;
            r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
            r7 = (float) r7;
            r8 = 0;
            r4.set(r9, r8, r6, r7);
            r4 = r0.path;
            r6 = r0.rect;
            r7 = r0.radii;
            r8 = android.graphics.Path.Direction.CW;
            r4.addRoundRect(r6, r7, r8);
            r4 = r0.path;
            r4.close();
        L_0x0196:
            r4 = r0.letterLayout;
            if (r4 == 0) goto L_0x019b;
        L_0x019a:
            goto L_0x019d;
        L_0x019b:
            r4 = r0.oldLetterLayout;
        L_0x019d:
            if (r4 == 0) goto L_0x01c0;
        L_0x019f:
            r21.save();
            r6 = r0.bubbleProgress;
            r7 = r0.scrollX;
            r7 = (float) r7;
            r3 = r3 - r2;
            r2 = (float) r3;
            r1.scale(r6, r6, r7, r2);
            r2 = r0.path;
            r3 = r0.paint;
            r1.drawPath(r2, r3);
            r2 = r0.textX;
            r3 = r0.textY;
            r1.translate(r2, r3);
            r4.draw(r1);
            r21.restore();
        L_0x01c0:
            r1 = r0.pressed;
            if (r1 == 0) goto L_0x01ce;
        L_0x01c4:
            r1 = r0.letterLayout;
            if (r1 == 0) goto L_0x01ce;
        L_0x01c8:
            r1 = r0.bubbleProgress;
            r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
            if (r1 < 0) goto L_0x01dd;
        L_0x01ce:
            r1 = r0.pressed;
            if (r1 == 0) goto L_0x01d6;
        L_0x01d2:
            r1 = r0.letterLayout;
            if (r1 != 0) goto L_0x0224;
        L_0x01d6:
            r1 = r0.bubbleProgress;
            r2 = 0;
            r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r1 <= 0) goto L_0x0224;
        L_0x01dd:
            r1 = java.lang.System.currentTimeMillis();
            r3 = r0.lastUpdateTime;
            r3 = r1 - r3;
            r6 = 0;
            r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
            if (r8 < 0) goto L_0x01f1;
        L_0x01eb:
            r6 = 17;
            r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
            if (r8 <= 0) goto L_0x01f3;
        L_0x01f1:
            r3 = 17;
        L_0x01f3:
            r0.lastUpdateTime = r1;
            r20.invalidate();
            r1 = r0.pressed;
            if (r1 == 0) goto L_0x0212;
        L_0x01fc:
            r1 = r0.letterLayout;
            if (r1 == 0) goto L_0x0212;
        L_0x0200:
            r1 = r0.bubbleProgress;
            r2 = (float) r3;
            r3 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
            r2 = r2 / r3;
            r1 = r1 + r2;
            r0.bubbleProgress = r1;
            r1 = r0.bubbleProgress;
            r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
            if (r1 <= 0) goto L_0x0224;
        L_0x020f:
            r0.bubbleProgress = r5;
            goto L_0x0224;
        L_0x0212:
            r1 = r0.bubbleProgress;
            r2 = (float) r3;
            r3 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
            r2 = r2 / r3;
            r1 = r1 - r2;
            r0.bubbleProgress = r1;
            r1 = r0.bubbleProgress;
            r2 = 0;
            r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r1 >= 0) goto L_0x0224;
        L_0x0222:
            r0.bubbleProgress = r2;
        L_0x0224:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView$FastScroll.onDraw(android.graphics.Canvas):void");
        }

        public void layout(int i, int i2, int i3, int i4) {
            if (RecyclerListView.this.selfOnLayout) {
                super.layout(i, i2, i3, i4);
            }
        }

        private void setProgress(float f) {
            this.progress = f;
            invalidate();
        }
    }

    public interface IntReturnCallback {
        int run();
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemClickListenerExtended {
        void onItemClick(View view, int i, float f, float f2);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    public interface OnItemLongClickListenerExtended {
        boolean onItemClick(View view, int i, float f, float f2);

        void onLongClickRelease();

        void onMove(float f, float f2);
    }

    public static class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    private class RecyclerListViewItemClickListener implements OnItemTouchListener {
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }

        public RecyclerListViewItemClickListener(Context context) {
            RecyclerListView.this.gestureDetector = new GestureDetector(context, new OnGestureListener(RecyclerListView.this) {
                public boolean onDown(MotionEvent motionEvent) {
                    return false;
                }

                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    return false;
                }

                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    return false;
                }

                public void onShowPress(MotionEvent motionEvent) {
                }

                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    if (!(RecyclerListView.this.currentChildView == null || (RecyclerListView.this.onItemClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null))) {
                        final float x = motionEvent.getX();
                        final float y = motionEvent.getY();
                        RecyclerListView recyclerListView = RecyclerListView.this;
                        recyclerListView.onChildPressed(recyclerListView.currentChildView, x, y, true);
                        final View access$300 = RecyclerListView.this.currentChildView;
                        final int access$600 = RecyclerListView.this.currentChildPosition;
                        if (RecyclerListView.this.instantClick && access$600 != -1) {
                            access$300.playSoundEffect(0);
                            access$300.sendAccessibilityEvent(1);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(access$300, access$600);
                            } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                RecyclerListView.this.onItemClickListenerExtended.onItemClick(access$300, access$600, x - access$300.getX(), y - access$300.getY());
                            }
                        }
                        AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() {
                            public void run() {
                                if (this == RecyclerListView.this.clickRunnable) {
                                    RecyclerListView.this.clickRunnable = null;
                                }
                                View view = access$300;
                                if (view != null) {
                                    RecyclerListView.this.onChildPressed(view, 0.0f, 0.0f, false);
                                    if (!RecyclerListView.this.instantClick) {
                                        access$300.playSoundEffect(0);
                                        access$300.sendAccessibilityEvent(1);
                                        if (access$600 == -1) {
                                            return;
                                        }
                                        if (RecyclerListView.this.onItemClickListener != null) {
                                            RecyclerListView.this.onItemClickListener.onItemClick(access$300, access$600);
                                        } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                            OnItemClickListenerExtended access$500 = RecyclerListView.this.onItemClickListenerExtended;
                                            View view2 = access$300;
                                            access$500.onItemClick(view2, access$600, x - view2.getX(), y - access$300.getY());
                                        }
                                    }
                                }
                            }
                        }, (long) ViewConfiguration.getPressedStateDuration());
                        if (RecyclerListView.this.selectChildRunnable != null) {
                            View access$3002 = RecyclerListView.this.currentChildView;
                            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                            RecyclerListView.this.selectChildRunnable = null;
                            RecyclerListView.this.currentChildView = null;
                            RecyclerListView.this.interceptedByChild = false;
                            RecyclerListView.this.removeSelection(access$3002, motionEvent);
                        }
                    }
                    return true;
                }

                public void onLongPress(MotionEvent motionEvent) {
                    if (RecyclerListView.this.currentChildView != null && RecyclerListView.this.currentChildPosition != -1) {
                        if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemLongClickListenerExtended != null) {
                            View access$300 = RecyclerListView.this.currentChildView;
                            if (RecyclerListView.this.onItemLongClickListener != null) {
                                if (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                                    access$300.performHapticFeedback(0);
                                    access$300.sendAccessibilityEvent(2);
                                }
                            } else if (RecyclerListView.this.onItemLongClickListenerExtended != null && RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, motionEvent.getX() - RecyclerListView.this.currentChildView.getX(), motionEvent.getY() - RecyclerListView.this.currentChildView.getY())) {
                                access$300.performHapticFeedback(0);
                                access$300.sendAccessibilityEvent(2);
                                RecyclerListView.this.longPressCalled = true;
                            }
                        }
                    }
                }
            });
            RecyclerListView.this.gestureDetector.setIsLongpressEnabled(false);
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            RecyclerListView recyclerListView;
            MotionEvent motionEvent2 = motionEvent;
            int actionMasked = motionEvent.getActionMasked();
            Object obj = RecyclerListView.this.getScrollState() == 0 ? 1 : null;
            if ((actionMasked == 0 || actionMasked == 5) && RecyclerListView.this.currentChildView == null && obj != null) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                RecyclerListView.this.longPressCalled = false;
                ItemAnimator itemAnimator = RecyclerListView.this.getItemAnimator();
                if ((RecyclerListView.this.allowItemsInteractionDuringAnimation || itemAnimator == null || !itemAnimator.isRunning()) && RecyclerListView.this.allowSelectChildAtPosition(x, y)) {
                    View findChildViewUnder = RecyclerListView.this.findChildViewUnder(x, y);
                    if (findChildViewUnder != null && RecyclerListView.this.allowSelectChildAtPosition(findChildViewUnder)) {
                        RecyclerListView.this.currentChildView = findChildViewUnder;
                    }
                }
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    x = motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    y = motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                        View childAt = viewGroup.getChildAt(childCount);
                        if (x >= ((float) childAt.getLeft()) && x <= ((float) childAt.getRight()) && y >= ((float) childAt.getTop()) && y <= ((float) childAt.getBottom()) && childAt.isClickable()) {
                            RecyclerListView.this.currentChildView = null;
                            break;
                        }
                    }
                }
                RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    recyclerListView = RecyclerListView.this;
                    recyclerListView.currentChildPosition = recyclerView.getChildPosition(recyclerListView.currentChildView);
                    MotionEvent obtain = MotionEvent.obtain(0, 0, motionEvent.getActionMasked(), motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(obtain)) {
                        RecyclerListView.this.interceptedByChild = true;
                    }
                    obtain.recycle();
                }
            }
            if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.interceptedByChild || motionEvent2 == null)) {
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(motionEvent2);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (actionMasked == 0 || actionMasked == 5) {
                if (RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null) {
                    RecyclerListView.this.selectorRect.setEmpty();
                } else {
                    RecyclerListView.this.selectChildRunnable = new -$$Lambda$RecyclerListView$RecyclerListViewItemClickListener$SK_thRYGfpljnQ09MrG-_RQm5SU(this, motionEvent.getX(), motionEvent.getY());
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                    if (RecyclerListView.this.currentChildView.isEnabled()) {
                        recyclerListView = RecyclerListView.this;
                        recyclerListView.positionSelector(recyclerListView.currentChildPosition, RecyclerListView.this.currentChildView);
                        Drawable drawable = RecyclerListView.this.selectorDrawable;
                        if (drawable != null) {
                            drawable = drawable.getCurrent();
                            if (drawable instanceof TransitionDrawable) {
                                if (RecyclerListView.this.onItemLongClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null) {
                                    ((TransitionDrawable) drawable).resetTransition();
                                } else {
                                    ((TransitionDrawable) drawable).startTransition(ViewConfiguration.getLongPressTimeout());
                                }
                            }
                            if (VERSION.SDK_INT >= 21) {
                                RecyclerListView.this.selectorDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                            }
                        }
                        RecyclerListView.this.updateSelectorState();
                    } else {
                        RecyclerListView.this.selectorRect.setEmpty();
                    }
                }
            } else if ((actionMasked == 1 || actionMasked == 6 || actionMasked == 3 || obj == null) && RecyclerListView.this.currentChildView != null) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                View access$300 = RecyclerListView.this.currentChildView;
                RecyclerListView recyclerListView2 = RecyclerListView.this;
                recyclerListView2.onChildPressed(recyclerListView2.currentChildView, 0.0f, 0.0f, false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
                RecyclerListView.this.removeSelection(access$300, motionEvent2);
                if ((actionMasked == 1 || actionMasked == 6 || actionMasked == 3) && RecyclerListView.this.onItemLongClickListenerExtended != null && RecyclerListView.this.longPressCalled) {
                    RecyclerListView.this.onItemLongClickListenerExtended.onLongClickRelease();
                    RecyclerListView.this.longPressCalled = false;
                }
            }
            return false;
        }

        public /* synthetic */ void lambda$onInterceptTouchEvent$0$RecyclerListView$RecyclerListViewItemClickListener(float f, float f2) {
            if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                RecyclerListView recyclerListView = RecyclerListView.this;
                recyclerListView.onChildPressed(recyclerListView.currentChildView, f, f2, true);
                RecyclerListView.this.selectChildRunnable = null;
            }
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    public static abstract class SelectionAdapter extends Adapter {
        public int getSelectionBottomPadding(View view) {
            return 0;
        }

        public abstract boolean isEnabled(ViewHolder viewHolder);
    }

    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public abstract String getLetter(int i);

        public abstract int getPositionForScrollProgress(float f);
    }

    public static abstract class SectionsAdapter extends FastScrollAdapter {
        private int count;
        private SparseIntArray sectionCache;
        private int sectionCount;
        private SparseIntArray sectionCountCache;
        private SparseIntArray sectionPositionCache;

        public abstract int getCountForSection(int i);

        public abstract Object getItem(int i, int i2);

        public abstract int getItemViewType(int i, int i2);

        public abstract int getSectionCount();

        public abstract View getSectionHeaderView(int i, View view);

        public abstract boolean isEnabled(int i, int i2);

        public abstract void onBindViewHolder(int i, int i2, ViewHolder viewHolder);

        private void cleanupCache() {
            SparseIntArray sparseIntArray = this.sectionCache;
            if (sparseIntArray == null) {
                this.sectionCache = new SparseIntArray();
                this.sectionPositionCache = new SparseIntArray();
                this.sectionCountCache = new SparseIntArray();
            } else {
                sparseIntArray.clear();
                this.sectionPositionCache.clear();
                this.sectionCountCache.clear();
            }
            this.count = -1;
            this.sectionCount = -1;
        }

        public void notifySectionsChanged() {
            cleanupCache();
        }

        public SectionsAdapter() {
            cleanupCache();
        }

        public void notifyDataSetChanged() {
            cleanupCache();
            super.notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return isEnabled(getSectionForPosition(adapterPosition), getPositionInSectionForPosition(adapterPosition));
        }

        public int getItemCount() {
            int i = this.count;
            if (i >= 0) {
                return i;
            }
            i = 0;
            this.count = 0;
            int internalGetSectionCount = internalGetSectionCount();
            while (i < internalGetSectionCount) {
                this.count += internalGetCountForSection(i);
                i++;
            }
            return this.count;
        }

        public final Object getItem(int i) {
            return getItem(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        public final int getItemViewType(int i) {
            return getItemViewType(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        public final void onBindViewHolder(ViewHolder viewHolder, int i) {
            onBindViewHolder(getSectionForPosition(i), getPositionInSectionForPosition(i), viewHolder);
        }

        private int internalGetCountForSection(int i) {
            int i2 = this.sectionCountCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            i2 = getCountForSection(i);
            this.sectionCountCache.put(i, i2);
            return i2;
        }

        private int internalGetSectionCount() {
            int i = this.sectionCount;
            if (i >= 0) {
                return i;
            }
            this.sectionCount = getSectionCount();
            return this.sectionCount;
        }

        public final int getSectionForPosition(int i) {
            int i2 = this.sectionCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            i2 = internalGetSectionCount();
            int i3 = 0;
            int i4 = 0;
            while (i3 < i2) {
                int internalGetCountForSection = internalGetCountForSection(i3) + i4;
                if (i < i4 || i >= internalGetCountForSection) {
                    i3++;
                    i4 = internalGetCountForSection;
                } else {
                    this.sectionCache.put(i, i3);
                    return i3;
                }
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int i) {
            int i2 = this.sectionPositionCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            i2 = internalGetSectionCount();
            int i3 = 0;
            int i4 = 0;
            while (i3 < i2) {
                int internalGetCountForSection = internalGetCountForSection(i3) + i4;
                if (i < i4 || i >= internalGetCountForSection) {
                    i3++;
                    i4 = internalGetCountForSection;
                } else {
                    i2 = i - i4;
                    this.sectionPositionCache.put(i, i2);
                    return i2;
                }
            }
            return -1;
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean allowSelectChildAtPosition(float f, float f2) {
        return true;
    }

    /* Access modifiers changed, original: protected */
    public boolean allowSelectChildAtPosition(View view) {
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public View findChildViewUnder(float f, float f2) {
        int childCount = getChildCount();
        int i = 0;
        while (i < 2) {
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                View childAt = getChildAt(i2);
                float f3 = 0.0f;
                float translationX = i == 0 ? childAt.getTranslationX() : 0.0f;
                if (i == 0) {
                    f3 = childAt.getTranslationY();
                }
                if (f >= ((float) childAt.getLeft()) + translationX && f <= ((float) childAt.getRight()) + translationX && f2 >= ((float) childAt.getTop()) + f3 && f2 <= ((float) childAt.getBottom()) + f3) {
                    return childAt;
                }
            }
            i++;
        }
        return null;
    }

    public void setDisableHighlightState(boolean z) {
        this.disableHighlightState = z;
    }

    /* Access modifiers changed, original: protected */
    public View getPressedChildView() {
        return this.currentChildView;
    }

    /* Access modifiers changed, original: protected */
    public void onChildPressed(View view, float f, float f2, boolean z) {
        if (!this.disableHighlightState) {
            view.setPressed(z);
        }
    }

    private void removeSelection(View view, MotionEvent motionEvent) {
        if (view != null && !this.selectorRect.isEmpty()) {
            if (view.isEnabled()) {
                positionSelector(this.currentChildPosition, view);
                Drawable drawable = this.selectorDrawable;
                if (drawable != null) {
                    drawable = drawable.getCurrent();
                    if (drawable instanceof TransitionDrawable) {
                        ((TransitionDrawable) drawable).resetTransition();
                    }
                    if (motionEvent != null && VERSION.SDK_INT >= 21) {
                        this.selectorDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                    }
                }
            } else {
                this.selectorRect.setEmpty();
            }
            updateSelectorState();
        }
    }

    public void cancelClickRunnables(boolean z) {
        Runnable runnable = this.selectChildRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.selectChildRunnable = null;
        }
        View view = this.currentChildView;
        if (view != null) {
            if (z) {
                onChildPressed(view, 0.0f, 0.0f, false);
            }
            this.currentChildView = null;
            removeSelection(view, null);
        }
        Runnable runnable2 = this.clickRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(String str, String str2) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(".R$styleable");
            Field field = Class.forName(stringBuilder.toString()).getField(str2);
            if (field != null) {
                return (int[]) field.get(null);
            }
        } catch (Throwable unused) {
        }
        return null;
    }

    @SuppressLint({"PrivateApi"})
    public RecyclerListView(Context context) {
        super(context);
        setGlowColor(Theme.getColor("actionBarDefault"));
        this.selectorDrawable = Theme.getSelectorDrawable(false);
        this.selectorDrawable.setCallback(this);
        try {
            if (!gotAttributes) {
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{obtainStyledAttributes});
            obtainStyledAttributes.recycle();
        } catch (Throwable th) {
            FileLog.e(th);
        }
        super.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean z = false;
                if (!(i == 0 || RecyclerListView.this.currentChildView == null)) {
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        RecyclerListView.this.selectChildRunnable = null;
                    }
                    MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    try {
                        RecyclerListView.this.gestureDetector.onTouchEvent(obtain);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    RecyclerListView.this.currentChildView.onTouchEvent(obtain);
                    obtain.recycle();
                    View access$300 = RecyclerListView.this.currentChildView;
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.onChildPressed(recyclerListView.currentChildView, 0.0f, 0.0f, false);
                    RecyclerListView.this.currentChildView = null;
                    RecyclerListView.this.removeSelection(access$300, null);
                    RecyclerListView.this.interceptedByChild = false;
                }
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, i);
                }
                RecyclerListView recyclerListView2 = RecyclerListView.this;
                if (i == 1 || i == 2) {
                    z = true;
                }
                recyclerListView2.scrollingByUser = z;
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrolled(recyclerView, i, i2);
                }
                RecyclerListView recyclerListView = RecyclerListView.this;
                if (recyclerListView.selectorPosition != -1) {
                    recyclerListView.selectorRect.offset(-i, -i2);
                    recyclerListView = RecyclerListView.this;
                    recyclerListView.selectorDrawable.setBounds(recyclerListView.selectorRect);
                    RecyclerListView.this.invalidate();
                } else {
                    recyclerListView.selectorRect.setEmpty();
                }
                RecyclerListView.this.checkSection();
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    public void setVerticalScrollBarEnabled(boolean z) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(z);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.fastScroll != null) {
            i = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            this.fastScroll.getLayoutParams().height = i;
            this.fastScroll.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), NUM), MeasureSpec.makeMeasureSpec(i, NUM));
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.fastScroll != null) {
            this.selfOnLayout = true;
            i2 += getPaddingTop();
            if (LocaleController.isRTL) {
                FastScroll fastScroll = this.fastScroll;
                fastScroll.layout(0, i2, fastScroll.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + i2);
            } else {
                int measuredWidth = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
                FastScroll fastScroll2 = this.fastScroll;
                fastScroll2.layout(measuredWidth, i2, fastScroll2.getMeasuredWidth() + measuredWidth, this.fastScroll.getMeasuredHeight() + i2);
            }
            this.selfOnLayout = false;
        }
        checkSection();
        IntReturnCallback intReturnCallback = this.pendingHighlightPosition;
        if (intReturnCallback != null) {
            highlightRowInternal(intReturnCallback, false);
        }
    }

    public void setSelectorDrawableColor(int i) {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        this.selectorDrawable = Theme.getSelectorDrawable(i, false);
        this.selectorDrawable.setCallback(this);
    }

    public void checkSection() {
        if ((this.scrollingByUser && this.fastScroll != null) || !(this.sectionsType == 0 || this.sectionsAdapter == null)) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    int paddingTop;
                    int abs;
                    if (this.sectionsAdapter != null) {
                        paddingTop = getPaddingTop();
                        int i = this.sectionsType;
                        int i2 = Integer.MAX_VALUE;
                        int i3 = 0;
                        int i4;
                        int bottom;
                        int countForSection;
                        int i5;
                        View childAt;
                        if (i == 1) {
                            int max;
                            i = getChildCount();
                            View view = null;
                            int i6 = Integer.MAX_VALUE;
                            i4 = 0;
                            int i7 = Integer.MAX_VALUE;
                            for (i2 = 0; i2 < i; i2++) {
                                View childAt2 = getChildAt(i2);
                                bottom = childAt2.getBottom();
                                if (bottom > this.sectionOffset + paddingTop) {
                                    if (bottom < i6) {
                                        i6 = bottom;
                                    } else {
                                        childAt2 = view;
                                    }
                                    max = Math.max(i4, bottom);
                                    if (bottom >= (this.sectionOffset + paddingTop) + AndroidUtilities.dp(32.0f) && bottom < i7) {
                                        i4 = max;
                                        i7 = bottom;
                                    } else {
                                        i4 = max;
                                    }
                                    view = childAt2;
                                }
                            }
                            if (view != null) {
                                ViewHolder childViewHolder = getChildViewHolder(view);
                                if (childViewHolder != null) {
                                    i = childViewHolder.getAdapterPosition();
                                    abs = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - i) + 1;
                                    if (this.scrollingByUser && this.fastScroll != null) {
                                        Adapter adapter = getAdapter();
                                        if (adapter instanceof FastScrollAdapter) {
                                            this.fastScroll.setProgress(Math.min(1.0f, ((float) i) / ((float) ((adapter.getItemCount() - abs) + 1))));
                                        }
                                    }
                                    this.headersCache.addAll(this.headers);
                                    this.headers.clear();
                                    if (this.sectionsAdapter.getItemCount() != 0) {
                                        if (!(this.currentFirst == i && this.currentVisible == abs)) {
                                            this.currentFirst = i;
                                            this.currentVisible = abs;
                                            this.sectionsCount = 1;
                                            this.startSection = this.sectionsAdapter.getSectionForPosition(i);
                                            countForSection = (this.sectionsAdapter.getCountForSection(this.startSection) + i) - this.sectionsAdapter.getPositionInSectionForPosition(i);
                                            while (countForSection < i + abs) {
                                                countForSection += this.sectionsAdapter.getCountForSection(this.startSection + this.sectionsCount);
                                                this.sectionsCount++;
                                            }
                                        }
                                        i5 = i;
                                        for (abs = this.startSection; abs < this.startSection + this.sectionsCount; abs++) {
                                            View view2;
                                            if (this.headersCache.isEmpty()) {
                                                view2 = null;
                                            } else {
                                                view2 = (View) this.headersCache.get(0);
                                                this.headersCache.remove(0);
                                            }
                                            View sectionHeaderView = getSectionHeaderView(abs, view2);
                                            this.headers.add(sectionHeaderView);
                                            int countForSection2 = this.sectionsAdapter.getCountForSection(abs);
                                            if (abs == this.startSection) {
                                                max = this.sectionsAdapter.getPositionInSectionForPosition(i5);
                                                if (max == countForSection2 - 1) {
                                                    sectionHeaderView.setTag(Integer.valueOf((-sectionHeaderView.getHeight()) + paddingTop));
                                                } else if (max == countForSection2 - 2) {
                                                    childAt = getChildAt(i5 - i);
                                                    if (childAt != null) {
                                                        max = childAt.getTop() + paddingTop;
                                                    } else {
                                                        max = -AndroidUtilities.dp(100.0f);
                                                    }
                                                    if (max < 0) {
                                                        sectionHeaderView.setTag(Integer.valueOf(max));
                                                    } else {
                                                        sectionHeaderView.setTag(Integer.valueOf(0));
                                                    }
                                                } else {
                                                    sectionHeaderView.setTag(Integer.valueOf(0));
                                                }
                                                countForSection2 -= this.sectionsAdapter.getPositionInSectionForPosition(i);
                                            } else {
                                                childAt = getChildAt(i5 - i);
                                                if (childAt != null) {
                                                    sectionHeaderView.setTag(Integer.valueOf(childAt.getTop() + paddingTop));
                                                } else {
                                                    sectionHeaderView.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                                                }
                                            }
                                            i5 += countForSection2;
                                        }
                                    } else {
                                        return;
                                    }
                                }
                                return;
                            }
                            return;
                        } else if (i == 2) {
                            this.pinnedHeaderShadowTargetAlpha = 0.0f;
                            if (this.sectionsAdapter.getItemCount() != 0) {
                                abs = getChildCount();
                                childAt = null;
                                bottom = 0;
                                int i8 = Integer.MAX_VALUE;
                                Object obj = null;
                                for (i = 0; i < abs; i++) {
                                    View childAt3 = getChildAt(i);
                                    i4 = childAt3.getBottom();
                                    if (i4 > this.sectionOffset + paddingTop) {
                                        if (i4 < i2) {
                                            childAt = childAt3;
                                            i2 = i4;
                                        }
                                        bottom = Math.max(bottom, i4);
                                        if (i4 >= (this.sectionOffset + paddingTop) + AndroidUtilities.dp(32.0f) && i4 < i8) {
                                            obj = childAt3;
                                            i8 = i4;
                                        }
                                    }
                                }
                                if (childAt != null) {
                                    ViewHolder childViewHolder2 = getChildViewHolder(childAt);
                                    if (childViewHolder2 != null) {
                                        abs = childViewHolder2.getAdapterPosition();
                                        i = this.sectionsAdapter.getSectionForPosition(abs);
                                        if (i >= 0) {
                                            if (this.currentFirst != i || this.pinnedHeader == null) {
                                                this.pinnedHeader = getSectionHeaderView(i, this.pinnedHeader);
                                                this.pinnedHeader.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0));
                                                View view3 = this.pinnedHeader;
                                                view3.layout(0, 0, view3.getMeasuredWidth(), this.pinnedHeader.getMeasuredHeight());
                                                this.currentFirst = i;
                                            }
                                            if (!(this.pinnedHeader == null || obj == null || obj.getClass() == this.pinnedHeader.getClass())) {
                                                this.pinnedHeaderShadowTargetAlpha = 1.0f;
                                            }
                                            countForSection = this.sectionsAdapter.getCountForSection(i);
                                            abs = this.sectionsAdapter.getPositionInSectionForPosition(abs);
                                            if (bottom == 0 || bottom >= getMeasuredHeight() - getPaddingBottom()) {
                                                i3 = this.sectionOffset;
                                            }
                                            if (abs == countForSection - 1) {
                                                abs = this.pinnedHeader.getHeight();
                                                if (childAt != null) {
                                                    i5 = ((childAt.getTop() - paddingTop) - this.sectionOffset) + childAt.getHeight();
                                                    abs = i5 < abs ? i5 - abs : paddingTop;
                                                } else {
                                                    abs = -AndroidUtilities.dp(100.0f);
                                                }
                                                if (abs < 0) {
                                                    this.pinnedHeader.setTag(Integer.valueOf((paddingTop + i3) + abs));
                                                } else {
                                                    this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i3));
                                                }
                                            } else {
                                                this.pinnedHeader.setTag(Integer.valueOf(paddingTop + i3));
                                            }
                                            invalidate();
                                        } else {
                                            return;
                                        }
                                    }
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                    }
                    paddingTop = linearLayoutManager.findFirstVisibleItemPosition();
                    abs = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - paddingTop) + 1;
                    if (!(paddingTop == -1 || !this.scrollingByUser || this.fastScroll == null)) {
                        Adapter adapter2 = getAdapter();
                        if (adapter2 instanceof FastScrollAdapter) {
                            this.fastScroll.setProgress(Math.min(1.0f, ((float) paddingTop) / ((float) ((adapter2.getItemCount() - abs) + 1))));
                        }
                    }
                }
            }
        }
    }

    public void setListSelectorColor(int i) {
        Theme.setSelectorDrawableColor(this.selectorDrawable, i, true);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListenerExtended onItemClickListenerExtended) {
        this.onItemClickListenerExtended = onItemClickListenerExtended;
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        this.gestureDetector.setIsLongpressEnabled(onItemLongClickListener != null);
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended onItemLongClickListenerExtended) {
        this.onItemLongClickListenerExtended = onItemLongClickListenerExtended;
        this.gestureDetector.setIsLongpressEnabled(onItemLongClickListenerExtended != null);
    }

    public void setEmptyView(View view) {
        if (this.emptyView != view) {
            this.emptyView = view;
            if (this.isHidden) {
                view = this.emptyView;
                if (view != null) {
                    view.setVisibility(8);
                }
            } else {
                checkIfEmpty();
            }
        }
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public void invalidateViews() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).invalidate();
        }
    }

    public void updateFastScrollColors() {
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null) {
            fastScroll.updateColors();
        }
    }

    public void setPinnedHeaderShadowDrawable(Drawable drawable) {
        this.pinnedHeaderShadowDrawable = drawable;
    }

    public boolean canScrollVertically(int i) {
        return this.scrollEnabled && super.canScrollVertically(i);
    }

    public void setScrollEnabled(boolean z) {
        this.scrollEnabled = z;
    }

    public void highlightRow(IntReturnCallback intReturnCallback) {
        highlightRowInternal(intReturnCallback, true);
    }

    private void highlightRowInternal(IntReturnCallback intReturnCallback, boolean z) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
        }
        ViewHolder findViewHolderForAdapterPosition = findViewHolderForAdapterPosition(intReturnCallback.run());
        if (findViewHolderForAdapterPosition != null) {
            positionSelector(findViewHolderForAdapterPosition.getLayoutPosition(), findViewHolderForAdapterPosition.itemView);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null) {
                drawable = drawable.getCurrent();
                if (drawable instanceof TransitionDrawable) {
                    if (this.onItemLongClickListener == null && this.onItemClickListenerExtended == null) {
                        ((TransitionDrawable) drawable).resetTransition();
                    } else {
                        ((TransitionDrawable) drawable).startTransition(ViewConfiguration.getLongPressTimeout());
                    }
                }
                if (VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setHotspot((float) (findViewHolderForAdapterPosition.itemView.getMeasuredWidth() / 2), (float) (findViewHolderForAdapterPosition.itemView.getMeasuredHeight() / 2));
                }
            }
            drawable = this.selectorDrawable;
            if (drawable != null && drawable.isStateful() && this.selectorDrawable.setState(getDrawableStateForSelector())) {
                invalidateDrawable(this.selectorDrawable);
            }
            -$$Lambda$RecyclerListView$9OyE8_R-oHAnqiquiqoGBlUXLQE -__lambda_recyclerlistview_9oye8_r-ohanqiquiqogbluxlqe = new -$$Lambda$RecyclerListView$9OyE8_R-oHAnqiquiqoGBlUXLQE(this);
            this.removeHighlighSelectionRunnable = -__lambda_recyclerlistview_9oye8_r-ohanqiquiqogbluxlqe;
            AndroidUtilities.runOnUIThread(-__lambda_recyclerlistview_9oye8_r-ohanqiquiqogbluxlqe, 700);
        } else if (z) {
            this.pendingHighlightPosition = intReturnCallback;
        }
    }

    public /* synthetic */ void lambda$highlightRowInternal$0$RecyclerListView() {
        this.removeHighlighSelectionRunnable = null;
        this.pendingHighlightPosition = null;
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable = drawable.getCurrent();
            if (drawable instanceof TransitionDrawable) {
                ((TransitionDrawable) drawable).resetTransition();
            }
        }
        drawable = this.selectorDrawable;
        if (drawable != null && drawable.isStateful()) {
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!isEnabled()) {
            return false;
        }
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        OnInterceptTouchListener onInterceptTouchListener = this.onInterceptTouchListener;
        if ((onInterceptTouchListener != null && onInterceptTouchListener.onInterceptTouchEvent(motionEvent)) || super.onInterceptTouchEvent(motionEvent)) {
            z = true;
        }
        return z;
    }

    private void checkIfEmpty() {
        if (!this.isHidden) {
            int i = 0;
            if (getAdapter() == null || this.emptyView == null) {
                if (this.hiddenByEmptyView && getVisibility() != 0) {
                    setVisibility(0);
                    this.hiddenByEmptyView = false;
                }
                return;
            }
            Object obj = getAdapter().getItemCount() == 0 ? 1 : null;
            int i2 = obj != null ? 0 : 8;
            if (this.emptyView.getVisibility() != i2) {
                this.emptyView.setVisibility(i2);
            }
            if (this.hideIfEmpty) {
                if (obj != null) {
                    i = 4;
                }
                if (getVisibility() != i) {
                    setVisibility(i);
                }
                this.hiddenByEmptyView = true;
            }
        }
    }

    public void hide() {
        if (!this.isHidden) {
            this.isHidden = true;
            if (getVisibility() != 8) {
                setVisibility(8);
            }
            View view = this.emptyView;
            if (!(view == null || view.getVisibility() == 8)) {
                this.emptyView.setVisibility(8);
            }
        }
    }

    public void show() {
        if (this.isHidden) {
            this.isHidden = false;
            checkIfEmpty();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 0) {
            this.hiddenByEmptyView = false;
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setHideIfEmpty(boolean z) {
        this.hideIfEmpty = z;
    }

    public OnScrollListener getOnScrollListener() {
        return this.onScrollListener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener onInterceptTouchListener) {
        this.onInterceptTouchListener = onInterceptTouchListener;
    }

    public void setInstantClick(boolean z) {
        this.instantClick = z;
    }

    public void setDisallowInterceptTouchEvents(boolean z) {
        this.disallowInterceptTouchEvents = z;
    }

    public void setFastScrollEnabled() {
        this.fastScroll = new FastScroll(getContext());
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setFastScrollVisible(boolean z) {
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null) {
            fastScroll.setVisibility(z ? 0 : 8);
        }
    }

    public void setSectionsType(int i) {
        this.sectionsType = i;
        if (this.sectionsType == 1) {
            this.headers = new ArrayList();
            this.headersCache = new ArrayList();
        }
    }

    public void setPinnedSectionOffsetY(int i) {
        this.sectionOffset = i;
        invalidate();
    }

    private void positionSelector(int i, View view) {
        positionSelector(i, view, false, -1.0f, -1.0f);
    }

    private void positionSelector(int i, View view, boolean z, float f, float f2) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
            this.pendingHighlightPosition = null;
        }
        if (this.selectorDrawable != null) {
            Object obj = i != this.selectorPosition ? 1 : null;
            int selectionBottomPadding = getAdapter() instanceof SelectionAdapter ? ((SelectionAdapter) getAdapter()).getSelectionBottomPadding(view) : 0;
            if (i != -1) {
                this.selectorPosition = i;
            }
            this.selectorRect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom() - selectionBottomPadding);
            boolean isEnabled = view.isEnabled();
            if (this.isChildViewEnabled != isEnabled) {
                this.isChildViewEnabled = isEnabled;
            }
            if (obj != null) {
                this.selectorDrawable.setVisible(false, false);
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            this.selectorDrawable.setBounds(this.selectorRect);
            if (obj != null && getVisibility() == 0) {
                this.selectorDrawable.setVisible(true, false);
            }
            if (VERSION.SDK_INT >= 21 && z) {
                this.selectorDrawable.setHotspot(f, f2);
            }
        }
    }

    public void setAllowItemsInteractionDuringAnimation(boolean z) {
        this.allowItemsInteractionDuringAnimation = z;
    }

    public void hideSelector(boolean z) {
        View view = this.currentChildView;
        if (view != null) {
            onChildPressed(view, 0.0f, 0.0f, false);
            this.currentChildView = null;
            if (z) {
                removeSelection(view, null);
            }
        }
        if (!z) {
            this.selectorDrawable.setState(StateSet.NOTHING);
            this.selectorRect.setEmpty();
        }
    }

    private void updateSelectorState() {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null && drawable.isStateful()) {
            if (this.currentChildView != null) {
                if (this.selectorDrawable.setState(getDrawableStateForSelector())) {
                    invalidateDrawable(this.selectorDrawable);
                }
            } else if (this.removeHighlighSelectionRunnable == null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
        }
    }

    private int[] getDrawableStateForSelector() {
        int[] onCreateDrawableState = onCreateDrawableState(1);
        onCreateDrawableState[onCreateDrawableState.length - 1] = 16842919;
        return onCreateDrawableState;
    }

    public void onChildAttachedToWindow(View view) {
        if (getAdapter() instanceof SelectionAdapter) {
            ViewHolder findContainingViewHolder = findContainingViewHolder(view);
            if (findContainingViewHolder != null) {
                view.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(findContainingViewHolder));
            }
        } else {
            view.setEnabled(false);
        }
        super.onChildAttachedToWindow(view);
    }

    /* Access modifiers changed, original: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    public boolean verifyDrawable(Drawable drawable) {
        return this.selectorDrawable == drawable || super.verifyDrawable(drawable);
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null && fastScroll.getParent() != getParent()) {
            ViewGroup viewGroup = (ViewGroup) this.fastScroll.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.fastScroll);
            }
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setAdapter(Adapter adapter) {
        Adapter adapter2 = getAdapter();
        if (adapter2 != null) {
            adapter2.unregisterAdapterDataObserver(this.observer);
        }
        ArrayList arrayList = this.headers;
        if (arrayList != null) {
            arrayList.clear();
            this.headersCache.clear();
        }
        this.currentFirst = -1;
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
        this.pinnedHeader = null;
        if (adapter instanceof SectionsAdapter) {
            this.sectionsAdapter = (SectionsAdapter) adapter;
        } else {
            this.sectionsAdapter = null;
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
        } catch (NullPointerException unused) {
        }
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        if (!this.longPressCalled) {
            return super.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
        }
        OnItemLongClickListenerExtended onItemLongClickListenerExtended = this.onItemLongClickListenerExtended;
        if (onItemLongClickListenerExtended != null) {
            onItemLongClickListenerExtended.onMove((float) i, (float) i2);
        }
        iArr[0] = i;
        iArr[1] = i2;
        return true;
    }

    private View getSectionHeaderView(int i, View view) {
        Object obj = view == null ? 1 : null;
        View sectionHeaderView = this.sectionsAdapter.getSectionHeaderView(i, view);
        if (obj != null) {
            ensurePinnedHeaderLayout(sectionHeaderView, false);
        }
        return sectionHeaderView;
    }

    private void ensurePinnedHeaderLayout(View view, boolean z) {
        if (view != null) {
            if (view.isLayoutRequested() || z) {
                int i = this.sectionsType;
                if (i == 1) {
                    LayoutParams layoutParams = view.getLayoutParams();
                    try {
                        view.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, NUM), MeasureSpec.makeMeasureSpec(layoutParams.height, NUM));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (i == 2) {
                    try {
                        view.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(0, 0));
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.requestLayout();
        }
        i = this.sectionsType;
        if (i == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (i = 0; i < this.headers.size(); i++) {
                    ensurePinnedHeaderLayout((View) this.headers.get(i), true);
                }
            }
        } else if (i == 2 && this.sectionsAdapter != null) {
            View view = this.pinnedHeader;
            if (view != null) {
                ensurePinnedHeaderLayout(view, true);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            this.selectorDrawable.draw(canvas);
        }
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.draw(canvas);
        }
        int i = this.sectionsType;
        float f = 0.0f;
        if (i == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (i = 0; i < this.headers.size(); i++) {
                    View view = (View) this.headers.get(i);
                    int save = canvas.save();
                    canvas.translate(LocaleController.isRTL ? (float) (getWidth() - view.getWidth()) : 0.0f, (float) ((Integer) view.getTag()).intValue());
                    canvas.clipRect(0, 0, getWidth(), view.getMeasuredHeight());
                    view.draw(canvas);
                    canvas.restoreToCount(save);
                }
            }
        } else if (i == 2 && this.sectionsAdapter != null) {
            View view2 = this.pinnedHeader;
            if (view2 != null && view2.getAlpha() != 0.0f) {
                i = canvas.save();
                int intValue = ((Integer) this.pinnedHeader.getTag()).intValue();
                if (LocaleController.isRTL) {
                    f = (float) (getWidth() - this.pinnedHeader.getWidth());
                }
                canvas.translate(f, (float) intValue);
                Drawable drawable = this.pinnedHeaderShadowDrawable;
                if (drawable != null) {
                    drawable.setBounds(0, this.pinnedHeader.getMeasuredHeight(), getWidth(), this.pinnedHeader.getMeasuredHeight() + this.pinnedHeaderShadowDrawable.getIntrinsicHeight());
                    this.pinnedHeaderShadowDrawable.setAlpha((int) (this.pinnedHeaderShadowAlpha * 255.0f));
                    this.pinnedHeaderShadowDrawable.draw(canvas);
                    long uptimeMillis = SystemClock.uptimeMillis();
                    long min = Math.min(20, uptimeMillis - this.lastAlphaAnimationTime);
                    this.lastAlphaAnimationTime = uptimeMillis;
                    float f2 = this.pinnedHeaderShadowAlpha;
                    f = this.pinnedHeaderShadowTargetAlpha;
                    if (f2 < f) {
                        this.pinnedHeaderShadowAlpha = f2 + (((float) min) / 180.0f);
                        if (this.pinnedHeaderShadowAlpha > f) {
                            this.pinnedHeaderShadowAlpha = f;
                        }
                        invalidate();
                    } else if (f2 > f) {
                        this.pinnedHeaderShadowAlpha = f2 - (((float) min) / 180.0f);
                        if (this.pinnedHeaderShadowAlpha < f) {
                            this.pinnedHeaderShadowAlpha = f;
                        }
                        invalidate();
                    }
                }
                canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
                this.pinnedHeader.draw(canvas);
                canvas.restoreToCount(i);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
    }

    public void addOverlayView(View view, FrameLayout.LayoutParams layoutParams) {
        if (this.overlayContainer == null) {
            this.overlayContainer = new FrameLayout(getContext()) {
                public void requestLayout() {
                    super.requestLayout();
                    try {
                        measure(MeasureSpec.makeMeasureSpec(RecyclerListView.this.getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(RecyclerListView.this.getMeasuredHeight(), NUM));
                        layout(0, 0, RecyclerListView.this.overlayContainer.getMeasuredWidth(), RecyclerListView.this.overlayContainer.getMeasuredHeight());
                    } catch (Exception unused) {
                    }
                }
            };
        }
        this.overlayContainer.addView(view, layoutParams);
    }

    public void removeOverlayView(View view) {
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.removeView(view);
        }
    }

    public ArrayList<View> getHeaders() {
        return this.headers;
    }

    public ArrayList<View> getHeadersCache() {
        return this.headersCache;
    }

    public View getPinnedHeader() {
        return this.pinnedHeader;
    }

    public void requestLayout() {
        if (!this.animationRunning) {
            super.requestLayout();
        }
    }
}
