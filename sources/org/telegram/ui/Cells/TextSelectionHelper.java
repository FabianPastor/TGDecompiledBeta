package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback2;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Magnifier;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji.EmojiSpan;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.FloatingActionMode;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer.BlockTableCell;
import org.telegram.ui.Components.RecyclerListView;

public abstract class TextSelectionHelper<Cell extends SelectableView> {
    private ActionMode actionMode;
    protected boolean actionsIsShowing;
    private Callback callback;
    protected int capturedX;
    protected int capturedY;
    private TextView deleteView;
    private RectF endArea = new RectF();
    protected float enterProgress;
    protected float handleViewProgress;
    private final Runnable hideActionsRunnable = new Runnable() {
        public void run() {
            if (VERSION.SDK_INT >= 23 && TextSelectionHelper.this.actionMode != null) {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                if (!textSelectionHelper.actionsIsShowing) {
                    textSelectionHelper.actionMode.hide(Long.MAX_VALUE);
                    AndroidUtilities.runOnUIThread(TextSelectionHelper.this.hideActionsRunnable, 1000);
                }
            }
        }
    };
    private Interpolator interpolator = new OvershootInterpolator();
    private boolean isOneTouch;
    private int lastX;
    private int lastY;
    protected final LayoutBlock layoutBlock = new LayoutBlock();
    private int longpressDelay = ViewConfiguration.getLongPressTimeout();
    private Magnifier magnifier;
    private float magnifierDy;
    private float magnifierY;
    private float magnifierYanimated;
    protected Cell maybeSelectedView;
    protected int maybeTextX;
    protected int maybeTextY;
    protected boolean movingDirectionSettling;
    private boolean movingHandle;
    protected boolean movingHandleStart;
    float movingOffsetX;
    float movingOffsetY;
    protected boolean multiselect = true;
    private boolean parentIsScrolling;
    public RecyclerListView parentView;
    protected Path path = new Path();
    private ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private boolean scrollDown;
    private Runnable scrollRunnable = new Runnable() {
        public void run() {
            if (TextSelectionHelper.this.scrolling) {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                if (textSelectionHelper.parentView != null) {
                    int dp;
                    if (textSelectionHelper.multiselect && textSelectionHelper.selectedView == null) {
                        dp = AndroidUtilities.dp(8.0f);
                    } else {
                        textSelectionHelper = TextSelectionHelper.this;
                        if (textSelectionHelper.selectedView != null) {
                            dp = textSelectionHelper.getLineHeight() >> 1;
                        } else {
                            return;
                        }
                    }
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    if (!textSelectionHelper2.multiselect) {
                        if (textSelectionHelper2.scrollDown) {
                            if (TextSelectionHelper.this.selectedView.getBottom() - dp < TextSelectionHelper.this.parentView.getMeasuredHeight()) {
                                dp = TextSelectionHelper.this.selectedView.getBottom() - TextSelectionHelper.this.parentView.getMeasuredHeight();
                            }
                        } else if (TextSelectionHelper.this.selectedView.getTop() + dp > 0) {
                            dp = -TextSelectionHelper.this.selectedView.getTop();
                        }
                    }
                    textSelectionHelper2 = TextSelectionHelper.this;
                    RecyclerListView recyclerListView = textSelectionHelper2.parentView;
                    if (!textSelectionHelper2.scrollDown) {
                        dp = -dp;
                    }
                    recyclerListView.scrollBy(0, dp);
                    AndroidUtilities.runOnUIThread(this);
                }
            }
        }
    };
    private boolean scrolling;
    protected int selectedCellId;
    protected Cell selectedView;
    protected int selectionEnd = -1;
    protected Paint selectionPaint = new Paint();
    protected int selectionStart = -1;
    protected boolean showActionsAsPopupAlways = false;
    private boolean snap;
    private RectF startArea = new RectF();
    final Runnable startSelectionRunnable = new Runnable() {
        public void run() {
            TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
            SelectableView selectableView = textSelectionHelper.maybeSelectedView;
            if (!(selectableView == null || textSelectionHelper.textSelectionOverlay == null)) {
                int i;
                TextSelectionHelper textSelectionHelper2;
                SelectableView selectableView2 = textSelectionHelper.selectedView;
                CharSequence text = textSelectionHelper.getText(selectableView, true);
                TextSelectionHelper.this.parentView.cancelClickRunnables(false);
                TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                int i2 = textSelectionHelper3.capturedX;
                int i3 = textSelectionHelper3.capturedY;
                if (!textSelectionHelper3.textArea.isEmpty()) {
                    i = TextSelectionHelper.this.textArea.right;
                    if (i2 > i) {
                        i2 = i - 1;
                    }
                    i = TextSelectionHelper.this.textArea.left;
                    if (i2 < i) {
                        i2 = i + 1;
                    }
                    i = TextSelectionHelper.this.textArea.top;
                    if (i3 < i) {
                        i3 = i + 1;
                    }
                    i = TextSelectionHelper.this.textArea.bottom;
                    if (i3 > i) {
                        i3 = i - 1;
                    }
                }
                int i4 = i2;
                textSelectionHelper3 = TextSelectionHelper.this;
                i = textSelectionHelper3.getCharOffsetFromCord(i4, i3, textSelectionHelper3.maybeTextX, textSelectionHelper3.maybeTextY, selectableView, true);
                if (i >= text.length()) {
                    textSelectionHelper2 = TextSelectionHelper.this;
                    textSelectionHelper2.fillLayoutForOffset(i, textSelectionHelper2.layoutBlock, true);
                    textSelectionHelper2 = TextSelectionHelper.this;
                    StaticLayout staticLayout = textSelectionHelper2.layoutBlock.layout;
                    if (staticLayout == null) {
                        textSelectionHelper2.selectionEnd = -1;
                        textSelectionHelper2.selectionStart = -1;
                        return;
                    }
                    i2 = staticLayout.getLineCount() - 1;
                    TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                    float f = (float) (i4 - textSelectionHelper4.maybeTextX);
                    if (f < textSelectionHelper4.layoutBlock.layout.getLineRight(i2) + ((float) AndroidUtilities.dp(4.0f)) && f > TextSelectionHelper.this.layoutBlock.layout.getLineLeft(i2)) {
                        i = text.length() - 1;
                    }
                }
                if (i >= 0 && i < text.length() && text.charAt(i) != 10) {
                    textSelectionHelper2 = TextSelectionHelper.this;
                    i3 = textSelectionHelper2.maybeTextX;
                    int i5 = textSelectionHelper2.maybeTextY;
                    textSelectionHelper2.clear();
                    TextSelectionHelper.this.textSelectionOverlay.setVisibility(0);
                    TextSelectionHelper.this.onTextSelected(selectableView, selectableView2);
                    textSelectionHelper2 = TextSelectionHelper.this;
                    textSelectionHelper2.selectionStart = i;
                    textSelectionHelper2.selectionEnd = textSelectionHelper2.selectionStart;
                    if (text instanceof Spanned) {
                        Spanned spanned = (Spanned) text;
                        for (Object obj : (EmojiSpan[]) spanned.getSpans(0, text.length(), EmojiSpan.class)) {
                            int spanStart = spanned.getSpanStart(obj);
                            i4 = spanned.getSpanEnd(obj);
                            if (i >= spanStart && i <= i4) {
                                textSelectionHelper3 = TextSelectionHelper.this;
                                textSelectionHelper3.selectionStart = spanStart;
                                textSelectionHelper3.selectionEnd = i4;
                                break;
                            }
                        }
                    }
                    textSelectionHelper3 = TextSelectionHelper.this;
                    if (textSelectionHelper3.selectionStart == textSelectionHelper3.selectionEnd) {
                        while (true) {
                            i = TextSelectionHelper.this.selectionStart;
                            if (i <= 0 || !TextSelectionHelper.isInterruptedCharacter(text.charAt(i - 1))) {
                                while (TextSelectionHelper.this.selectionEnd < text.length() && TextSelectionHelper.isInterruptedCharacter(text.charAt(TextSelectionHelper.this.selectionEnd))) {
                                    textSelectionHelper3 = TextSelectionHelper.this;
                                    textSelectionHelper3.selectionEnd++;
                                }
                            } else {
                                textSelectionHelper3 = TextSelectionHelper.this;
                                textSelectionHelper3.selectionStart--;
                            }
                        }
                        while (TextSelectionHelper.this.selectionEnd < text.length()) {
                            textSelectionHelper3 = TextSelectionHelper.this;
                            textSelectionHelper3.selectionEnd++;
                        }
                    }
                    textSelectionHelper = TextSelectionHelper.this;
                    textSelectionHelper.textX = i3;
                    textSelectionHelper.textY = i5;
                    textSelectionHelper.selectedView = selectableView;
                    textSelectionHelper.textSelectionOverlay.performHapticFeedback(0);
                    TextSelectionHelper.this.showActions();
                    TextSelectionHelper.this.invalidate();
                    if (selectableView2 != null) {
                        selectableView2.invalidate();
                    }
                    if (TextSelectionHelper.this.callback != null) {
                        TextSelectionHelper.this.callback.onStateChanged(true);
                    }
                    TextSelectionHelper.this.movingHandle = true;
                    textSelectionHelper = TextSelectionHelper.this;
                    textSelectionHelper.movingDirectionSettling = true;
                    textSelectionHelper.isOneTouch = true;
                    textSelectionHelper = TextSelectionHelper.this;
                    textSelectionHelper.movingOffsetY = 0.0f;
                    textSelectionHelper.movingOffsetX = 0.0f;
                    textSelectionHelper.onOffsetChanged();
                }
                TextSelectionHelper.this.tryCapture = false;
            }
        }
    };
    protected final Rect textArea = new Rect();
    private final android.view.ActionMode.Callback textSelectActionCallback = createActionCallback();
    protected TextSelectionOverlay textSelectionOverlay;
    protected int textX;
    protected int textY;
    protected int[] tmpCoord = new int[2];
    private int topOffset;
    private int touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
    private boolean tryCapture;

    public interface Callback {
        void onStateChanged(boolean z);

        void onTextCopied();
    }

    public static class IngnoreCopySpanable {
    }

    private static class LayoutBlock {
        StaticLayout layout;
        float xOffset;
        float yOffset;

        private LayoutBlock() {
        }

        /* synthetic */ LayoutBlock(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public interface SelectableView {
        int getBottom();

        int getMeasuredWidth();

        int getTop();

        float getX();

        float getY();

        void invalidate();
    }

    public interface TextLayoutBlock {

        public final /* synthetic */ class -CC {
            public static CharSequence $default$getPrefix(TextLayoutBlock textLayoutBlock) {
                return null;
            }
        }

        StaticLayout getLayout();

        CharSequence getPrefix();

        int getRow();

        int getX();

        int getY();
    }

    public class TextSelectionOverlay extends View {
        Paint handleViewPaint = new Paint(1);
        Path path = new Path();
        long pressedTime = 0;
        float pressedX;
        float pressedY;

        public TextSelectionOverlay(Context context) {
            super(context);
            this.handleViewPaint.setStyle(Style.FILL);
        }

        public boolean checkOnTap(MotionEvent motionEvent) {
            if (TextSelectionHelper.this.isSelectionMode() && !TextSelectionHelper.this.movingHandle) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.pressedX = motionEvent.getX();
                    this.pressedY = motionEvent.getY();
                    this.pressedTime = System.currentTimeMillis();
                } else if (action == 1 && System.currentTimeMillis() - this.pressedTime < 200 && TextSelectionHelper.this.distance((int) this.pressedX, (int) this.pressedY, (int) motionEvent.getX(), (int) motionEvent.getY()) < ((float) TextSelectionHelper.this.touchSlop)) {
                    TextSelectionHelper.this.hideActions();
                    TextSelectionHelper.this.clear();
                    return true;
                }
            }
            return false;
        }

        /* JADX WARNING: Removed duplicated region for block: B:55:0x013f  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x016b  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0154  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x0192  */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x0108  */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x011b A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x013f  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0154  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x016b  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x0192  */
        public boolean onTouchEvent(android.view.MotionEvent r22) {
            /*
            r21 = this;
            r0 = r21;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.isSelectionMode();
            r2 = 0;
            if (r1 != 0) goto L_0x000c;
        L_0x000b:
            return r2;
        L_0x000c:
            r1 = r22.getPointerCount();
            r3 = 1;
            if (r1 <= r3) goto L_0x001a;
        L_0x0013:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.movingHandle;
            return r1;
        L_0x001a:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.lastX;
            r1 = (float) r1;
            r4 = r22.getX();
            r1 = r1 - r4;
            r1 = (int) r1;
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4.lastY;
            r22.getY();
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r22.getX();
            r5 = (int) r5;
            r4.lastX = r5;
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r22.getY();
            r5 = (int) r5;
            r4.lastY = r5;
            r4 = r22.getAction();
            r5 = 2;
            if (r4 == 0) goto L_0x0536;
        L_0x004a:
            if (r4 == r3) goto L_0x04fb;
        L_0x004c:
            if (r4 == r5) goto L_0x0050;
        L_0x004e:
            goto L_0x0609;
        L_0x0050:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.movingHandle;
            if (r4 == 0) goto L_0x0609;
        L_0x0058:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r4.movingHandleStart;
            if (r5 == 0) goto L_0x0062;
        L_0x005e:
            r4.pickStartView();
            goto L_0x0065;
        L_0x0062:
            r4.pickEndView();
        L_0x0065:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r4.selectedView;
            if (r5 != 0) goto L_0x0070;
        L_0x006b:
            r1 = r4.movingHandle;
            return r1;
        L_0x0070:
            r4 = r22.getY();
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.movingOffsetY;
            r4 = r4 + r5;
            r4 = (int) r4;
            r5 = r22.getX();
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r6.movingOffsetX;
            r5 = r5 + r7;
            r5 = (int) r5;
            r10 = r6.selectLayout(r5, r4);
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r6.selectedView;
            if (r7 != 0) goto L_0x008f;
        L_0x008e:
            return r3;
        L_0x008f:
            r7 = r6.movingHandleStart;
            if (r7 == 0) goto L_0x009b;
        L_0x0093:
            r7 = r6.selectionStart;
            r8 = r6.layoutBlock;
            r6.fillLayoutForOffset(r7, r8);
            goto L_0x00a2;
        L_0x009b:
            r7 = r6.selectionEnd;
            r8 = r6.layoutBlock;
            r6.fillLayoutForOffset(r7, r8);
        L_0x00a2:
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r6.layoutBlock;
            r8 = r7.layout;
            if (r8 != 0) goto L_0x00ab;
        L_0x00aa:
            return r3;
        L_0x00ab:
            r12 = r7.yOffset;
            r13 = r6.selectedView;
            r6 = r13.getTop();
            r4 = r4 - r6;
            r5 = (float) r5;
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6 = r6.selectedView;
            r6 = r6.getX();
            r5 = r5 - r6;
            r15 = (int) r5;
            r5 = r22.getY();
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6 = r6.touchSlop;
            r6 = (float) r6;
            r5 = r5 - r6;
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6 = r6.parentView;
            r6 = r6.getMeasuredHeight();
            r6 = (float) r6;
            r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
            if (r5 <= 0) goto L_0x00f0;
        L_0x00d8:
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6 = r5.multiselect;
            if (r6 != 0) goto L_0x00ee;
        L_0x00de:
            r5 = r5.selectedView;
            r5 = r5.getBottom();
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6 = r6.parentView;
            r6 = r6.getMeasuredHeight();
            if (r5 <= r6) goto L_0x00f0;
        L_0x00ee:
            r5 = 1;
            goto L_0x00f1;
        L_0x00f0:
            r5 = 0;
        L_0x00f1:
            r6 = r22.getY();
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r7.parentView;
            r7 = r7.getParent();
            r7 = (android.view.View) r7;
            r7 = r7.getTop();
            r7 = (float) r7;
            r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
            if (r6 >= 0) goto L_0x0118;
        L_0x0108:
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r6.multiselect;
            if (r7 != 0) goto L_0x0116;
        L_0x010e:
            r6 = r6.selectedView;
            r6 = r6.getTop();
            if (r6 >= 0) goto L_0x0118;
        L_0x0116:
            r6 = 1;
            goto L_0x0119;
        L_0x0118:
            r6 = 0;
        L_0x0119:
            if (r5 != 0) goto L_0x0137;
        L_0x011b:
            if (r6 == 0) goto L_0x011e;
        L_0x011d:
            goto L_0x0137;
        L_0x011e:
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.scrolling;
            if (r5 == 0) goto L_0x0134;
        L_0x0126:
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5.scrolling = r2;
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.scrollRunnable;
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5);
        L_0x0134:
            r16 = r4;
            goto L_0x017c;
        L_0x0137:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.scrolling;
            if (r4 != 0) goto L_0x014d;
        L_0x013f:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4.scrolling = r3;
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.scrollRunnable;
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        L_0x014d:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4.scrollDown = r5;
            if (r5 == 0) goto L_0x016b;
        L_0x0154:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.parentView;
            r4 = r4.getMeasuredHeight();
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.selectedView;
            r5 = r5.getTop();
            r4 = r4 - r5;
            r4 = (float) r4;
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.movingOffsetY;
            goto L_0x0179;
        L_0x016b:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.selectedView;
            r4 = r4.getTop();
            r4 = -r4;
            r4 = (float) r4;
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.movingOffsetY;
        L_0x0179:
            r4 = r4 + r5;
            r4 = (int) r4;
            goto L_0x0134;
        L_0x017c:
            r14 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r14.textX;
            r5 = r14.textY;
            r6 = r14.selectedView;
            r20 = 0;
            r17 = r4;
            r18 = r5;
            r19 = r6;
            r8 = r14.getCharOffsetFromCord(r15, r16, r17, r18, r19, r20);
            if (r8 < 0) goto L_0x04f0;
        L_0x0192:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r4.movingDirectionSettling;
            if (r5 == 0) goto L_0x01b4;
        L_0x0198:
            if (r10 == 0) goto L_0x019b;
        L_0x019a:
            return r3;
        L_0x019b:
            r5 = r4.selectionStart;
            if (r8 >= r5) goto L_0x01a7;
        L_0x019f:
            r4.movingDirectionSettling = r2;
            r4.movingHandleStart = r3;
            r4.hideActions();
            goto L_0x01b4;
        L_0x01a7:
            r5 = r4.selectionEnd;
            if (r8 <= r5) goto L_0x01b3;
        L_0x01ab:
            r4.movingDirectionSettling = r2;
            r4.movingHandleStart = r2;
            r4.hideActions();
            goto L_0x01b4;
        L_0x01b3:
            return r3;
        L_0x01b4:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r4.movingHandleStart;
            r6 = -1;
            if (r5 == 0) goto L_0x0372;
        L_0x01bb:
            r5 = r4.selectionStart;
            if (r5 == r8) goto L_0x04eb;
        L_0x01bf:
            r4 = r4.canSelect(r8);
            if (r4 == 0) goto L_0x04eb;
        L_0x01c5:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r4.selectedView;
            r4 = r4.getText(r5, r2);
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r5.layoutBlock;
            r5.fillLayoutForOffset(r8, r7);
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r5.layoutBlock;
            r9 = r7.layout;
            r11 = r5.selectionStart;
            r5.fillLayoutForOffset(r11, r7);
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.layoutBlock;
            r5 = r5.layout;
            if (r9 == 0) goto L_0x0371;
        L_0x01e7:
            if (r5 != 0) goto L_0x01eb;
        L_0x01e9:
            goto L_0x0371;
        L_0x01eb:
            r11 = r8;
        L_0x01ec:
            r7 = r11 + -1;
            if (r7 < 0) goto L_0x01fd;
        L_0x01f0:
            r7 = r4.charAt(r7);
            r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7);
            if (r7 == 0) goto L_0x01fd;
        L_0x01fa:
            r11 = r11 + -1;
            goto L_0x01ec;
        L_0x01fd:
            r7 = r5.getLineForOffset(r11);
            r14 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r14 = r14.selectionStart;
            r14 = r5.getLineForOffset(r14);
            r15 = r5.getLineForOffset(r8);
            if (r10 != 0) goto L_0x0350;
        L_0x020f:
            if (r9 != r5) goto L_0x0350;
        L_0x0211:
            r9 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r9 = r9.selectionStart;
            r9 = r5.getLineForOffset(r9);
            if (r15 == r9) goto L_0x021f;
        L_0x021b:
            if (r15 != r7) goto L_0x021f;
        L_0x021d:
            goto L_0x0350;
        L_0x021f:
            r9 = r5.getLineForOffset(r8);
            r9 = r5.getParagraphDirection(r9);
            if (r6 == r9) goto L_0x032a;
        L_0x0229:
            r5 = r5.isRtlCharAt(r8);
            if (r5 != 0) goto L_0x032a;
        L_0x022f:
            if (r7 != r14) goto L_0x032a;
        L_0x0231:
            if (r15 == r7) goto L_0x0235;
        L_0x0233:
            goto L_0x032a;
        L_0x0235:
            r5 = r8;
        L_0x0236:
            r6 = r5 + 1;
            r7 = r4.length();
            if (r6 >= r7) goto L_0x024a;
        L_0x023e:
            r7 = r4.charAt(r6);
            r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7);
            if (r7 == 0) goto L_0x024a;
        L_0x0248:
            r5 = r6;
            goto L_0x0236;
        L_0x024a:
            r6 = r8 - r11;
            r6 = java.lang.Math.abs(r6);
            r5 = r8 - r5;
            r5 = java.lang.Math.abs(r5);
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r7.snap;
            if (r7 == 0) goto L_0x0268;
        L_0x025e:
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            if (r1 < 0) goto L_0x0264;
        L_0x0262:
            r9 = 1;
            goto L_0x0265;
        L_0x0264:
            r9 = 0;
        L_0x0265:
            r7.snap = r9;
        L_0x0268:
            r7 = r8 + -1;
            if (r7 <= 0) goto L_0x0278;
        L_0x026c:
            r7 = r4.charAt(r7);
            r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7);
            if (r7 == 0) goto L_0x0278;
        L_0x0276:
            r7 = 1;
            goto L_0x0279;
        L_0x0278:
            r7 = 0;
        L_0x0279:
            r9 = r4.length();
            r10 = 10;
            if (r8 < r9) goto L_0x0288;
        L_0x0281:
            r8 = r4.length();
            r9 = 10;
            goto L_0x028c;
        L_0x0288:
            r9 = r4.charAt(r8);
        L_0x028c:
            r12 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r12 = r12.selectionStart;
            r13 = r4.length();
            if (r12 < r13) goto L_0x02a1;
        L_0x0296:
            r12 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.length();
            r12.selectionStart = r4;
            r4 = 10;
            goto L_0x02a9;
        L_0x02a1:
            r12 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r12 = r12.selectionStart;
            r4 = r4.charAt(r12);
        L_0x02a9:
            r12 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r12 = r12.selectionStart;
            if (r8 >= r12) goto L_0x02b1;
        L_0x02af:
            if (r6 < r5) goto L_0x02d3;
        L_0x02b1:
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.selectionStart;
            if (r8 <= r5) goto L_0x02b9;
        L_0x02b7:
            if (r1 < 0) goto L_0x02d3;
        L_0x02b9:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r9);
            if (r1 == 0) goto L_0x02d3;
        L_0x02bf:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4);
            if (r1 == 0) goto L_0x02cd;
        L_0x02c5:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.snap;
            if (r1 == 0) goto L_0x02d3;
        L_0x02cd:
            if (r8 == 0) goto L_0x02d3;
        L_0x02cf:
            if (r7 == 0) goto L_0x02d3;
        L_0x02d1:
            if (r4 != r10) goto L_0x04eb;
        L_0x02d3:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.snap;
            if (r1 == 0) goto L_0x02de;
        L_0x02db:
            if (r8 != r3) goto L_0x02de;
        L_0x02dd:
            return r3;
        L_0x02de:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.selectionStart;
            if (r8 >= r1) goto L_0x0302;
        L_0x02e4:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r9);
            if (r1 == 0) goto L_0x0302;
        L_0x02ea:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4);
            if (r1 == 0) goto L_0x02f8;
        L_0x02f0:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.snap;
            if (r1 == 0) goto L_0x0302;
        L_0x02f8:
            if (r4 == r10) goto L_0x0302;
        L_0x02fa:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.selectionStart = r11;
            r1.snap = r3;
            goto L_0x0306;
        L_0x0302:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.selectionStart = r8;
        L_0x0306:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r3 = r1.selectionStart;
            r4 = r1.selectionEnd;
            if (r3 <= r4) goto L_0x0314;
        L_0x030e:
            r1.selectionEnd = r3;
            r1.selectionStart = r4;
            r1.movingHandleStart = r2;
        L_0x0314:
            r1 = android.os.Build.VERSION.SDK_INT;
            r2 = 27;
            if (r1 < r2) goto L_0x0323;
        L_0x031a:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.textSelectionOverlay;
            r2 = 9;
            r1.performHapticFeedback(r2);
        L_0x0323:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.invalidate();
            goto L_0x04eb;
        L_0x032a:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.selectionStart = r8;
            r3 = r1.selectionStart;
            r4 = r1.selectionEnd;
            if (r3 <= r4) goto L_0x033a;
        L_0x0334:
            r1.selectionEnd = r3;
            r1.selectionStart = r4;
            r1.movingHandleStart = r2;
        L_0x033a:
            r1 = android.os.Build.VERSION.SDK_INT;
            r2 = 27;
            if (r1 < r2) goto L_0x0349;
        L_0x0340:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.textSelectionOverlay;
            r2 = 9;
            r1.performHapticFeedback(r2);
        L_0x0349:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.invalidate();
            goto L_0x04eb;
        L_0x0350:
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r7.layoutBlock;
            r1 = r1.yOffset;
            r9 = r11;
            r11 = r1;
            r7.jumpToLine(r8, r9, r10, r11, r12, r13);
            r1 = android.os.Build.VERSION.SDK_INT;
            r2 = 27;
            if (r1 < r2) goto L_0x036a;
        L_0x0361:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.textSelectionOverlay;
            r2 = 9;
            r1.performHapticFeedback(r2);
        L_0x036a:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.invalidate();
            goto L_0x04eb;
        L_0x0371:
            return r3;
        L_0x0372:
            r5 = r4.selectionEnd;
            if (r8 == r5) goto L_0x04eb;
        L_0x0376:
            r4 = r4.canSelect(r8);
            if (r4 == 0) goto L_0x04eb;
        L_0x037c:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r4.selectedView;
            r4 = r4.getText(r5, r2);
            r9 = r8;
        L_0x0385:
            r5 = r4.length();
            if (r9 >= r5) goto L_0x0398;
        L_0x038b:
            r5 = r4.charAt(r9);
            r5 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r5);
            if (r5 == 0) goto L_0x0398;
        L_0x0395:
            r9 = r9 + 1;
            goto L_0x0385;
        L_0x0398:
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r5.layoutBlock;
            r5.fillLayoutForOffset(r8, r7);
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r5.layoutBlock;
            r11 = r7.layout;
            r14 = r5.selectionEnd;
            r5.fillLayoutForOffset(r14, r7);
            r5 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r5 = r5.layoutBlock;
            r5 = r5.layout;
            if (r11 == 0) goto L_0x04ea;
        L_0x03b2:
            if (r5 != 0) goto L_0x03b6;
        L_0x03b4:
            goto L_0x04ea;
        L_0x03b6:
            r7 = r4.length();
            if (r8 <= r7) goto L_0x03c1;
        L_0x03bc:
            r7 = r4.length();
            r8 = r7;
        L_0x03c1:
            r7 = r5.getLineForOffset(r9);
            r14 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r14 = r14.selectionEnd;
            r14 = r5.getLineForOffset(r14);
            r15 = r5.getLineForOffset(r8);
            if (r10 != 0) goto L_0x04cc;
        L_0x03d3:
            if (r11 != r5) goto L_0x04cc;
        L_0x03d5:
            r11 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r11 = r11.selectionEnd;
            r11 = r5.getLineForOffset(r11);
            if (r15 == r11) goto L_0x03e3;
        L_0x03df:
            if (r15 != r7) goto L_0x03e3;
        L_0x03e1:
            goto L_0x04cc;
        L_0x03e3:
            r10 = r5.getLineForOffset(r8);
            r10 = r5.getParagraphDirection(r10);
            if (r6 == r10) goto L_0x04a7;
        L_0x03ed:
            r5 = r5.isRtlCharAt(r8);
            if (r5 != 0) goto L_0x04a7;
        L_0x03f3:
            if (r14 != r7) goto L_0x04a7;
        L_0x03f5:
            if (r15 == r7) goto L_0x03f9;
        L_0x03f7:
            goto L_0x04a7;
        L_0x03f9:
            r5 = r8;
        L_0x03fa:
            r6 = r5 + -1;
            if (r6 < 0) goto L_0x040b;
        L_0x03fe:
            r6 = r4.charAt(r6);
            r6 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r6);
            if (r6 == 0) goto L_0x040b;
        L_0x0408:
            r5 = r5 + -1;
            goto L_0x03fa;
        L_0x040b:
            r6 = r8 - r9;
            r6 = java.lang.Math.abs(r6);
            r5 = r8 - r5;
            r5 = java.lang.Math.abs(r5);
            r7 = r8 + -1;
            if (r7 <= 0) goto L_0x0427;
        L_0x041b:
            r7 = r4.charAt(r7);
            r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7);
            if (r7 == 0) goto L_0x0427;
        L_0x0425:
            r7 = 1;
            goto L_0x0428;
        L_0x0427:
            r7 = 0;
        L_0x0428:
            r10 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r10 = r10.snap;
            if (r10 == 0) goto L_0x043a;
        L_0x0430:
            r10 = org.telegram.ui.Cells.TextSelectionHelper.this;
            if (r1 > 0) goto L_0x0436;
        L_0x0434:
            r11 = 1;
            goto L_0x0437;
        L_0x0436:
            r11 = 0;
        L_0x0437:
            r10.snap = r11;
        L_0x043a:
            r10 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r10 = r10.selectionEnd;
            if (r10 <= 0) goto L_0x044c;
        L_0x0440:
            r10 = r10 - r3;
            r4 = r4.charAt(r10);
            r4 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4);
            if (r4 == 0) goto L_0x044c;
        L_0x044b:
            r2 = 1;
        L_0x044c:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.selectionEnd;
            if (r8 <= r4) goto L_0x0454;
        L_0x0452:
            if (r6 <= r5) goto L_0x0468;
        L_0x0454:
            r4 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r4.selectionEnd;
            if (r8 >= r4) goto L_0x045c;
        L_0x045a:
            if (r1 > 0) goto L_0x0468;
        L_0x045c:
            if (r7 == 0) goto L_0x0468;
        L_0x045e:
            if (r2 == 0) goto L_0x04eb;
        L_0x0460:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.snap;
            if (r1 != 0) goto L_0x04eb;
        L_0x0468:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r4 = r1.selectionEnd;
            if (r8 <= r4) goto L_0x0480;
        L_0x046e:
            if (r7 == 0) goto L_0x0480;
        L_0x0470:
            if (r2 == 0) goto L_0x0478;
        L_0x0472:
            r1 = r1.snap;
            if (r1 == 0) goto L_0x0480;
        L_0x0478:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.selectionEnd = r9;
            r1.snap = r3;
            goto L_0x0484;
        L_0x0480:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.selectionEnd = r8;
        L_0x0484:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r2 = r1.selectionStart;
            r4 = r1.selectionEnd;
            if (r2 <= r4) goto L_0x0492;
        L_0x048c:
            r1.selectionEnd = r2;
            r1.selectionStart = r4;
            r1.movingHandleStart = r3;
        L_0x0492:
            r1 = android.os.Build.VERSION.SDK_INT;
            r2 = 27;
            if (r1 < r2) goto L_0x04a1;
        L_0x0498:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.textSelectionOverlay;
            r2 = 9;
            r1.performHapticFeedback(r2);
        L_0x04a1:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.invalidate();
            goto L_0x04eb;
        L_0x04a7:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.selectionEnd = r8;
            r2 = r1.selectionStart;
            r4 = r1.selectionEnd;
            if (r2 <= r4) goto L_0x04b7;
        L_0x04b1:
            r1.selectionEnd = r2;
            r1.selectionStart = r4;
            r1.movingHandleStart = r3;
        L_0x04b7:
            r1 = android.os.Build.VERSION.SDK_INT;
            r2 = 27;
            if (r1 < r2) goto L_0x04c6;
        L_0x04bd:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.textSelectionOverlay;
            r2 = 9;
            r1.performHapticFeedback(r2);
        L_0x04c6:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.invalidate();
            goto L_0x04eb;
        L_0x04cc:
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r7.layoutBlock;
            r11 = r1.yOffset;
            r7.jumpToLine(r8, r9, r10, r11, r12, r13);
            r1 = android.os.Build.VERSION.SDK_INT;
            r2 = 27;
            if (r1 < r2) goto L_0x04e4;
        L_0x04db:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.textSelectionOverlay;
            r2 = 9;
            r1.performHapticFeedback(r2);
        L_0x04e4:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.invalidate();
            goto L_0x04eb;
        L_0x04ea:
            return r3;
        L_0x04eb:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.onOffsetChanged();
        L_0x04f0:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r2 = r1.lastX;
            r1.showMagnifier(r2);
            goto L_0x0609;
        L_0x04fb:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.hideMagnifier();
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.movingHandle = r2;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.movingDirectionSettling = r2;
            r1.isOneTouch = r2;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.isSelectionMode();
            if (r1 == 0) goto L_0x051e;
        L_0x0514:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.showActions();
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.showHandleViews();
        L_0x051e:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.scrolling;
            if (r1 == 0) goto L_0x0609;
        L_0x0526:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.scrolling = r2;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.scrollRunnable;
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
            goto L_0x0609;
        L_0x0536:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.movingHandle;
            if (r1 == 0) goto L_0x053f;
        L_0x053e:
            return r3;
        L_0x053f:
            r1 = r22.getX();
            r1 = (int) r1;
            r4 = r22.getY();
            r4 = (int) r4;
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6 = r6.startArea;
            r1 = (float) r1;
            r7 = (float) r4;
            r6 = r6.contains(r1, r7);
            if (r6 == 0) goto L_0x05a3;
        L_0x0557:
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6.pickStartView();
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r6.selectedView;
            if (r7 != 0) goto L_0x0563;
        L_0x0562:
            return r2;
        L_0x0563:
            r6.movingHandle = r3;
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6.movingHandleStart = r3;
            r7 = r6.selectionStart;
            r6 = r6.offsetToCord(r7);
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r7.getLineHeight();
            r7 = r7 / r5;
            r5 = (float) r7;
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r2 = r6[r2];
            r8 = r7.textX;
            r2 = r2 + r8;
            r2 = (float) r2;
            r8 = r7.selectedView;
            r8 = r8.getX();
            r2 = r2 + r8;
            r2 = r2 - r1;
            r7.movingOffsetX = r2;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r2 = r6[r3];
            r6 = r1.textY;
            r2 = r2 + r6;
            r6 = r1.selectedView;
            r6 = r6.getTop();
            r2 = r2 + r6;
            r2 = r2 - r4;
            r2 = (float) r2;
            r2 = r2 - r5;
            r1.movingOffsetY = r2;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.hideActions();
            return r3;
        L_0x05a3:
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6 = r6.endArea;
            r6 = r6.contains(r1, r7);
            if (r6 == 0) goto L_0x0604;
        L_0x05af:
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6.pickEndView();
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r6.selectedView;
            if (r7 != 0) goto L_0x05bb;
        L_0x05ba:
            return r2;
        L_0x05bb:
            r6.movingHandle = r3;
            r6 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r6.movingHandleStart = r2;
            r7 = r6.selectionEnd;
            r6 = r6.offsetToCord(r7);
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r7 = r7.getLineHeight();
            r7 = r7 / r5;
            r5 = (float) r7;
            r7 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r2 = r6[r2];
            r8 = r7.textX;
            r2 = r2 + r8;
            r2 = (float) r2;
            r8 = r7.selectedView;
            r8 = r8.getX();
            r2 = r2 + r8;
            r2 = r2 - r1;
            r7.movingOffsetX = r2;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r2 = r6[r3];
            r6 = r1.textY;
            r2 = r2 + r6;
            r6 = r1.selectedView;
            r6 = r6.getTop();
            r2 = r2 + r6;
            r2 = r2 - r4;
            r2 = (float) r2;
            r2 = r2 - r5;
            r1.movingOffsetY = r2;
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r2 = r1.lastX;
            r1.showMagnifier(r2);
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.hideActions();
            return r3;
        L_0x0604:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1.movingHandle = r2;
        L_0x0609:
            r1 = org.telegram.ui.Cells.TextSelectionHelper.this;
            r1 = r1.movingHandle;
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper$TextSelectionOverlay.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            if (TextSelectionHelper.this.isSelectionMode()) {
                float x;
                TextSelectionHelper textSelectionHelper;
                TextSelectionHelper textSelectionHelper2;
                int i;
                StaticLayout staticLayout;
                float primaryHorizontal;
                float lineBottom;
                TextSelectionHelper textSelectionHelper3;
                LayoutBlock layoutBlock;
                int i2;
                int dp = AndroidUtilities.dp(22.0f);
                int access$2000 = TextSelectionHelper.this.topOffset;
                TextSelectionHelper.this.pickEndView();
                if (TextSelectionHelper.this.selectedView != null) {
                    canvas.save();
                    float y = TextSelectionHelper.this.selectedView.getY();
                    TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                    y += (float) textSelectionHelper4.textY;
                    x = textSelectionHelper4.selectedView.getX() + ((float) TextSelectionHelper.this.textX);
                    canvas2.translate(x, y);
                    this.handleViewPaint.setColor(Theme.getColor("chat_TextSelectionCursor"));
                    textSelectionHelper = TextSelectionHelper.this;
                    int length = textSelectionHelper.getText(textSelectionHelper.selectedView, false).length();
                    textSelectionHelper2 = TextSelectionHelper.this;
                    i = textSelectionHelper2.selectionEnd;
                    if (i >= 0 && i <= length) {
                        textSelectionHelper2.fillLayoutForOffset(i, textSelectionHelper2.layoutBlock);
                        textSelectionHelper = TextSelectionHelper.this;
                        staticLayout = textSelectionHelper.layoutBlock.layout;
                        if (staticLayout != null) {
                            length = textSelectionHelper.selectionEnd;
                            i = staticLayout.getText().length();
                            if (length > i) {
                                length = i;
                            }
                            i = staticLayout.getLineForOffset(length);
                            primaryHorizontal = staticLayout.getPrimaryHorizontal(length);
                            lineBottom = (float) staticLayout.getLineBottom(i);
                            textSelectionHelper3 = TextSelectionHelper.this;
                            layoutBlock = textSelectionHelper3.layoutBlock;
                            primaryHorizontal += layoutBlock.xOffset;
                            lineBottom = (float) ((int) (lineBottom + layoutBlock.yOffset));
                            y += lineBottom;
                            float f;
                            float interpolation;
                            if (y <= ((float) access$2000) || y >= ((float) textSelectionHelper3.parentView.getMeasuredHeight())) {
                                TextSelectionHelper.this.endArea.setEmpty();
                            } else if (staticLayout.isRtlCharAt(TextSelectionHelper.this.selectionEnd)) {
                                canvas.save();
                                f = (float) dp;
                                canvas2.translate(primaryHorizontal - f, lineBottom);
                                interpolation = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                float f2 = f / 2.0f;
                                canvas2.scale(interpolation, interpolation, f2, f2);
                                this.path.reset();
                                this.path.addCircle(f2, f2, f2, Direction.CCW);
                                float f3 = f;
                                this.path.addRect(f2, 0.0f, f, f2, Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                x += primaryHorizontal;
                                TextSelectionHelper.this.endArea.set(x - f3, y - f3, x, y + f3);
                                TextSelectionHelper.this.endArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                            } else {
                                canvas.save();
                                canvas2.translate(primaryHorizontal, lineBottom);
                                interpolation = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                lineBottom = (float) dp;
                                f = lineBottom / 2.0f;
                                canvas2.scale(interpolation, interpolation, f, f);
                                this.path.reset();
                                this.path.addCircle(f, f, f, Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, f, f, Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                x += primaryHorizontal;
                                TextSelectionHelper.this.endArea.set(x, y - lineBottom, x + lineBottom, y + lineBottom);
                                TextSelectionHelper.this.endArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                                i2 = 1;
                                canvas.restore();
                            }
                        }
                    }
                    i2 = 0;
                    canvas.restore();
                } else {
                    i2 = 0;
                }
                TextSelectionHelper.this.pickStartView();
                if (TextSelectionHelper.this.selectedView != null) {
                    canvas.save();
                    x = TextSelectionHelper.this.selectedView.getY();
                    textSelectionHelper = TextSelectionHelper.this;
                    x += (float) textSelectionHelper.textY;
                    primaryHorizontal = textSelectionHelper.selectedView.getX() + ((float) TextSelectionHelper.this.textX);
                    canvas2.translate(primaryHorizontal, x);
                    textSelectionHelper2 = TextSelectionHelper.this;
                    int length2 = textSelectionHelper2.getText(textSelectionHelper2.selectedView, false).length();
                    textSelectionHelper2 = TextSelectionHelper.this;
                    i = textSelectionHelper2.selectionStart;
                    if (i >= 0 && i <= length2) {
                        textSelectionHelper2.fillLayoutForOffset(i, textSelectionHelper2.layoutBlock);
                        TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                        staticLayout = textSelectionHelper5.layoutBlock.layout;
                        if (staticLayout != null) {
                            length2 = staticLayout.getLineForOffset(textSelectionHelper5.selectionStart);
                            lineBottom = staticLayout.getPrimaryHorizontal(TextSelectionHelper.this.selectionStart);
                            float lineBottom2 = (float) staticLayout.getLineBottom(length2);
                            textSelectionHelper3 = TextSelectionHelper.this;
                            layoutBlock = textSelectionHelper3.layoutBlock;
                            lineBottom += layoutBlock.xOffset;
                            lineBottom2 = (float) ((int) (lineBottom2 + layoutBlock.yOffset));
                            x += lineBottom2;
                            float interpolation2;
                            float f4;
                            if (x <= ((float) access$2000) || x >= ((float) textSelectionHelper3.parentView.getMeasuredHeight())) {
                                if (x > 0.0f && x - ((float) TextSelectionHelper.this.getLineHeight()) < ((float) TextSelectionHelper.this.parentView.getMeasuredHeight())) {
                                    i2++;
                                }
                                TextSelectionHelper.this.startArea.setEmpty();
                            } else if (staticLayout.isRtlCharAt(TextSelectionHelper.this.selectionStart)) {
                                canvas.save();
                                canvas2.translate(lineBottom, lineBottom2);
                                interpolation2 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                f4 = (float) dp;
                                lineBottom2 = f4 / 2.0f;
                                canvas2.scale(interpolation2, interpolation2, lineBottom2, lineBottom2);
                                this.path.reset();
                                this.path.addCircle(lineBottom2, lineBottom2, lineBottom2, Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, lineBottom2, lineBottom2, Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                primaryHorizontal += lineBottom;
                                TextSelectionHelper.this.startArea.set(primaryHorizontal, x - f4, primaryHorizontal + f4, x + f4);
                                TextSelectionHelper.this.startArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                            } else {
                                canvas.save();
                                f4 = (float) dp;
                                canvas2.translate(lineBottom - f4, lineBottom2);
                                interpolation2 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                lineBottom2 = f4 / 2.0f;
                                canvas2.scale(interpolation2, interpolation2, lineBottom2, lineBottom2);
                                this.path.reset();
                                this.path.addCircle(lineBottom2, lineBottom2, lineBottom2, Direction.CCW);
                                this.path.addRect(lineBottom2, 0.0f, f4, lineBottom2, Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                primaryHorizontal += lineBottom;
                                TextSelectionHelper.this.startArea.set(primaryHorizontal - f4, x - f4, primaryHorizontal, x + f4);
                                TextSelectionHelper.this.startArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                                i2++;
                            }
                        }
                    }
                    canvas.restore();
                }
                if (i2 != 0 && TextSelectionHelper.this.movingHandle) {
                    TextSelectionHelper textSelectionHelper6 = TextSelectionHelper.this;
                    if (!textSelectionHelper6.movingHandleStart) {
                        textSelectionHelper6.pickEndView();
                    }
                    textSelectionHelper6 = TextSelectionHelper.this;
                    textSelectionHelper6.showMagnifier(textSelectionHelper6.lastX);
                    if (TextSelectionHelper.this.magnifierY != TextSelectionHelper.this.magnifierYanimated) {
                        invalidate();
                    }
                }
                if (!TextSelectionHelper.this.parentIsScrolling) {
                    TextSelectionHelper.this.showActions();
                }
                if (VERSION.SDK_INT >= 23 && TextSelectionHelper.this.actionMode != null) {
                    TextSelectionHelper.this.actionMode.invalidateContentRect();
                    if (TextSelectionHelper.this.actionMode != null) {
                        ((FloatingActionMode) TextSelectionHelper.this.actionMode).updateViewLocationInWindow();
                    }
                }
                if (TextSelectionHelper.this.isOneTouch) {
                    invalidate();
                }
            }
        }
    }

    public interface ArticleSelectableView extends SelectableView {
        void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList);
    }

    public static class ArticleTextSelectionHelper extends TextSelectionHelper<ArticleSelectableView> {
        public ArrayList<TextLayoutBlock> arrayList = new ArrayList();
        SparseIntArray childCountByPosition = new SparseIntArray();
        int endViewChildPosition = -1;
        int endViewOffset;
        int endViewPosition = -1;
        public LinearLayoutManager layoutManager;
        int maybeTextIndex = -1;
        SparseArray<CharSequence> prefixTextByPosition = new SparseArray();
        boolean startPeek;
        int startViewChildPosition = -1;
        int startViewOffset;
        int startViewPosition = -1;
        SparseArray<CharSequence> textByPosition = new SparseArray();

        /* Access modifiers changed, original: protected */
        public CharSequence getText(ArticleSelectableView articleSelectableView, boolean z) {
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            int i = z ? this.maybeTextIndex : this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (this.arrayList.isEmpty()) {
                return "";
            }
            return ((TextLayoutBlock) this.arrayList.get(i)).getLayout().getText();
        }

        /* Access modifiers changed, original: protected */
        public int getCharOffsetFromCord(int i, int i2, int i3, int i4, ArticleSelectableView articleSelectableView, boolean z) {
            if (articleSelectableView == null) {
                return -1;
            }
            i -= i3;
            i2 -= i4;
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            i3 = z ? this.maybeTextIndex : this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            StaticLayout layout = ((TextLayoutBlock) this.arrayList.get(i3)).getLayout();
            if (i < 0) {
                i = 1;
            }
            if (i2 < 0) {
                i2 = 1;
            }
            if (i > layout.getWidth()) {
                i = layout.getWidth();
            }
            if (i2 > layout.getLineBottom(layout.getLineCount() - 1)) {
                i2 = layout.getLineBottom(layout.getLineCount() - 1) - 1;
            }
            i4 = 0;
            while (i4 < layout.getLineCount()) {
                if (i2 > layout.getLineTop(i4) && i2 < layout.getLineBottom(i4)) {
                    break;
                }
                i4++;
            }
            i4 = -1;
            if (i4 >= 0) {
                return layout.getOffsetForHorizontal(i4, (float) i);
            }
            return -1;
        }

        /* Access modifiers changed, original: protected */
        public void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z) {
            this.arrayList.clear();
            ArticleSelectableView articleSelectableView = (ArticleSelectableView) (z ? this.maybeSelectedView : this.selectedView);
            if (articleSelectableView == null) {
                layoutBlock.layout = null;
                return;
            }
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (z) {
                layoutBlock.layout = ((TextLayoutBlock) this.arrayList.get(this.maybeTextIndex)).getLayout();
            } else {
                layoutBlock.layout = ((TextLayoutBlock) this.arrayList.get(this.startPeek ? this.startViewChildPosition : this.endViewChildPosition)).getLayout();
            }
            layoutBlock.yOffset = 0.0f;
            layoutBlock.xOffset = 0.0f;
        }

        /* Access modifiers changed, original: protected */
        public int getLineHeight() {
            int i = 0;
            if (this.selectedView == null) {
                return 0;
            }
            this.arrayList.clear();
            ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
            StaticLayout layout = ((TextLayoutBlock) this.arrayList.get(this.startPeek ? this.startViewChildPosition : this.endViewChildPosition)).getLayout();
            int i2 = Integer.MAX_VALUE;
            while (i < layout.getLineCount()) {
                int lineBottom = layout.getLineBottom(i) - layout.getLineTop(i);
                if (lineBottom < i2) {
                    i2 = lineBottom;
                }
                i++;
            }
            return i2;
        }

        public void trySelect(View view) {
            if (this.maybeSelectedView != null) {
                this.startSelectionRunnable.run();
            }
        }

        public void setMaybeView(int i, int i2, View view) {
            if (view instanceof ArticleSelectableView) {
                this.capturedX = i;
                this.capturedY = i2;
                this.maybeSelectedView = (ArticleSelectableView) view;
                this.maybeTextIndex = findClosestLayoutIndex(i, i2, (ArticleSelectableView) this.maybeSelectedView);
                i = this.maybeTextIndex;
                if (i < 0) {
                    this.maybeSelectedView = null;
                    return;
                }
                this.maybeTextX = ((TextLayoutBlock) this.arrayList.get(i)).getX();
                this.maybeTextY = ((TextLayoutBlock) this.arrayList.get(this.maybeTextIndex)).getY();
            }
        }

        private int findClosestLayoutIndex(int i, int i2, ArticleSelectableView articleSelectableView) {
            int i3;
            int i4 = 0;
            if (articleSelectableView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) articleSelectableView;
                for (i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                    View childAt = viewGroup.getChildAt(i3);
                    if (childAt instanceof ArticleSelectableView) {
                        float f = (float) i2;
                        if (f > childAt.getY() && f < childAt.getY() + ((float) childAt.getHeight())) {
                            return findClosestLayoutIndex((int) (((float) i) - childAt.getX()), (int) (f - childAt.getY()), (ArticleSelectableView) childAt);
                        }
                    }
                }
            }
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (this.arrayList.isEmpty()) {
                return -1;
            }
            int size = this.arrayList.size() - 1;
            i3 = Integer.MAX_VALUE;
            int i5 = Integer.MAX_VALUE;
            int i6 = -1;
            while (size >= 0) {
                TextLayoutBlock textLayoutBlock = (TextLayoutBlock) this.arrayList.get(size);
                int y = textLayoutBlock.getY();
                int height = textLayoutBlock.getLayout().getHeight() + y;
                if (i2 >= y && i2 < height) {
                    break;
                }
                height = Math.min(Math.abs(i2 - y), Math.abs(i2 - height));
                if (height < i5) {
                    i6 = size;
                    i5 = height;
                }
                size--;
            }
            i4 = i5;
            size = i6;
            if (size < 0) {
                return -1;
            }
            i2 = ((TextLayoutBlock) this.arrayList.get(size)).getRow();
            if (i2 > 0 && i4 < AndroidUtilities.dp(24.0f)) {
                for (int size2 = this.arrayList.size() - 1; size2 >= 0; size2--) {
                    TextLayoutBlock textLayoutBlock2 = (TextLayoutBlock) this.arrayList.get(size2);
                    if (textLayoutBlock2.getRow() == i2) {
                        i5 = textLayoutBlock2.getX();
                        i6 = textLayoutBlock2.getX() + textLayoutBlock2.getLayout().getWidth();
                        if (i >= i5 && i <= i6) {
                            return size2;
                        }
                        i4 = Math.min(Math.abs(i - i5), Math.abs(i - i6));
                        if (i4 < i3) {
                            size = size2;
                            i3 = i4;
                        }
                    }
                }
            }
            return size;
        }

        public void draw(Canvas canvas, ArticleSelectableView articleSelectableView) {
            draw(canvas, articleSelectableView, 0);
        }

        public void draw(Canvas canvas, ArticleSelectableView articleSelectableView, int i) {
            this.selectionPaint.setColor(Theme.getColor("chat_inTextSelectionHighlight"));
            int adapterPosition = getAdapterPosition(articleSelectableView);
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (!this.arrayList.isEmpty()) {
                TextLayoutBlock textLayoutBlock = (TextLayoutBlock) this.arrayList.get(i);
                int i2 = this.endViewOffset;
                int length = textLayoutBlock.getLayout().getText().length();
                if (i2 > length) {
                    i2 = length;
                }
                if (adapterPosition == this.startViewPosition && adapterPosition == this.endViewPosition) {
                    adapterPosition = this.startViewChildPosition;
                    if (adapterPosition == this.endViewChildPosition && adapterPosition == i) {
                        drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, i2);
                        return;
                    }
                    adapterPosition = this.startViewChildPosition;
                    if (i == adapterPosition) {
                        drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, length);
                        return;
                    }
                    int i3 = this.endViewChildPosition;
                    if (i == i3) {
                        drawSelection(canvas, textLayoutBlock.getLayout(), 0, i2);
                    } else if (i > adapterPosition && i < i3) {
                        drawSelection(canvas, textLayoutBlock.getLayout(), 0, length);
                    }
                } else if (adapterPosition == this.startViewPosition && this.startViewChildPosition == i) {
                    drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, length);
                } else if (adapterPosition == this.endViewPosition && this.endViewChildPosition == i) {
                    drawSelection(canvas, textLayoutBlock.getLayout(), 0, i2);
                } else if ((adapterPosition > this.startViewPosition && adapterPosition < this.endViewPosition) || ((adapterPosition == this.startViewPosition && i > this.startViewChildPosition) || (adapterPosition == this.endViewPosition && i < this.endViewChildPosition))) {
                    drawSelection(canvas, textLayoutBlock.getLayout(), 0, length);
                }
            }
        }

        private int getAdapterPosition(ArticleSelectableView articleSelectableView) {
            View view = (View) articleSelectableView;
            ViewParent parent = view.getParent();
            if (!(parent instanceof ArticleSelectableView)) {
                return this.parentView.getChildAdapterPosition(view);
            }
            if (parent.getParent() instanceof ArticleSelectableView) {
                return getAdapterPosition((ArticleSelectableView) parent);
            }
            return this.parentView.getChildAdapterPosition((View) parent);
        }

        public boolean isSelectable(View view) {
            if (!(view instanceof ArticleSelectableView)) {
                return false;
            }
            this.arrayList.clear();
            ((ArticleSelectableView) view).fillTextLayoutBlocks(this.arrayList);
            if (view instanceof BlockTableCell) {
                return true;
            }
            return this.arrayList.isEmpty() ^ 1;
        }

        /* Access modifiers changed, original: protected */
        public void onTextSelected(ArticleSelectableView articleSelectableView, ArticleSelectableView articleSelectableView2) {
            int adapterPosition = getAdapterPosition(articleSelectableView);
            this.endViewPosition = adapterPosition;
            this.startViewPosition = adapterPosition;
            int i = this.maybeTextIndex;
            this.endViewChildPosition = i;
            this.startViewChildPosition = i;
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            int size = this.arrayList.size();
            this.childCountByPosition.put(adapterPosition, size);
            for (i = 0; i < size; i++) {
                int i2 = (i << 16) + adapterPosition;
                this.textByPosition.put(i2, ((TextLayoutBlock) this.arrayList.get(i)).getLayout().getText());
                this.prefixTextByPosition.put(i2, ((TextLayoutBlock) this.arrayList.get(i)).getPrefix());
            }
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x0113 A:{LOOP_END, LOOP:0: B:41:0x0111->B:42:0x0113} */
        public void onNewViewSelected(org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView r6, org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView r7, int r8) {
            /*
            r5 = this;
            r0 = r5.getAdapterPosition(r7);
            if (r6 == 0) goto L_0x000b;
        L_0x0006:
            r6 = r5.getAdapterPosition(r6);
            goto L_0x000c;
        L_0x000b:
            r6 = -1;
        L_0x000c:
            r5.invalidate();
            r1 = r5.movingDirectionSettling;
            r2 = 1;
            r3 = 0;
            if (r1 == 0) goto L_0x005d;
        L_0x0015:
            r1 = r5.startViewPosition;
            r4 = r5.endViewPosition;
            if (r1 != r4) goto L_0x005d;
        L_0x001b:
            if (r0 != r1) goto L_0x003c;
        L_0x001d:
            r6 = r5.startViewChildPosition;
            if (r8 >= r6) goto L_0x0031;
        L_0x0021:
            r5.startViewChildPosition = r8;
            r5.pickStartView();
            r5.movingHandleStart = r2;
            r6 = r5.selectionEnd;
            r5.startViewOffset = r6;
            r6 = r6 - r2;
            r5.selectionStart = r6;
            goto L_0x00fc;
        L_0x0031:
            r5.endViewChildPosition = r8;
            r5.pickEndView();
            r5.movingHandleStart = r3;
            r5.endViewOffset = r3;
            goto L_0x00fc;
        L_0x003c:
            if (r0 >= r1) goto L_0x0050;
        L_0x003e:
            r5.startViewPosition = r0;
            r5.startViewChildPosition = r8;
            r5.pickStartView();
            r5.movingHandleStart = r2;
            r6 = r5.selectionEnd;
            r5.startViewOffset = r6;
            r6 = r6 - r2;
            r5.selectionStart = r6;
            goto L_0x00fc;
        L_0x0050:
            r5.endViewPosition = r0;
            r5.endViewChildPosition = r8;
            r5.pickEndView();
            r5.movingHandleStart = r3;
            r5.endViewOffset = r3;
            goto L_0x00fc;
        L_0x005d:
            r1 = r5.movingHandleStart;
            if (r1 == 0) goto L_0x00b0;
        L_0x0061:
            if (r0 != r6) goto L_0x008c;
        L_0x0063:
            r6 = r5.endViewChildPosition;
            if (r8 <= r6) goto L_0x007f;
        L_0x0067:
            r1 = r5.endViewPosition;
            if (r0 >= r1) goto L_0x006c;
        L_0x006b:
            goto L_0x007f;
        L_0x006c:
            r5.endViewPosition = r0;
            r5.startViewChildPosition = r6;
            r5.endViewChildPosition = r8;
            r6 = r5.endViewOffset;
            r5.startViewOffset = r6;
            r5.pickEndView();
            r5.endViewOffset = r3;
            r5.movingHandleStart = r3;
            goto L_0x00fc;
        L_0x007f:
            r5.startViewPosition = r0;
            r5.startViewChildPosition = r8;
            r5.pickStartView();
            r6 = r5.selectionEnd;
            r5.startViewOffset = r6;
            goto L_0x00fc;
        L_0x008c:
            r6 = r5.endViewPosition;
            if (r0 > r6) goto L_0x009c;
        L_0x0090:
            r5.startViewPosition = r0;
            r5.startViewChildPosition = r8;
            r5.pickStartView();
            r6 = r5.selectionEnd;
            r5.startViewOffset = r6;
            goto L_0x00fc;
        L_0x009c:
            r5.endViewPosition = r0;
            r6 = r5.endViewChildPosition;
            r5.startViewChildPosition = r6;
            r5.endViewChildPosition = r8;
            r6 = r5.endViewOffset;
            r5.startViewOffset = r6;
            r5.pickEndView();
            r5.endViewOffset = r3;
            r5.movingHandleStart = r3;
            goto L_0x00fc;
        L_0x00b0:
            if (r0 != r6) goto L_0x00d9;
        L_0x00b2:
            r6 = r5.startViewChildPosition;
            if (r8 >= r6) goto L_0x00cf;
        L_0x00b6:
            r1 = r5.startViewPosition;
            if (r0 <= r1) goto L_0x00bb;
        L_0x00ba:
            goto L_0x00cf;
        L_0x00bb:
            r5.startViewPosition = r0;
            r5.endViewChildPosition = r6;
            r5.startViewChildPosition = r8;
            r6 = r5.startViewOffset;
            r5.endViewOffset = r6;
            r5.pickStartView();
            r5.movingHandleStart = r2;
            r6 = r5.selectionEnd;
            r5.startViewOffset = r6;
            goto L_0x00fc;
        L_0x00cf:
            r5.endViewPosition = r0;
            r5.endViewChildPosition = r8;
            r5.pickEndView();
            r5.endViewOffset = r3;
            goto L_0x00fc;
        L_0x00d9:
            r6 = r5.startViewPosition;
            if (r0 < r6) goto L_0x00e7;
        L_0x00dd:
            r5.endViewPosition = r0;
            r5.endViewChildPosition = r8;
            r5.pickEndView();
            r5.endViewOffset = r3;
            goto L_0x00fc;
        L_0x00e7:
            r5.startViewPosition = r0;
            r6 = r5.startViewChildPosition;
            r5.endViewChildPosition = r6;
            r5.startViewChildPosition = r8;
            r6 = r5.startViewOffset;
            r5.endViewOffset = r6;
            r5.pickStartView();
            r5.movingHandleStart = r2;
            r6 = r5.selectionEnd;
            r5.startViewOffset = r6;
        L_0x00fc:
            r6 = r5.arrayList;
            r6.clear();
            r6 = r5.arrayList;
            r7.fillTextLayoutBlocks(r6);
            r6 = r5.arrayList;
            r6 = r6.size();
            r7 = r5.childCountByPosition;
            r7.put(r0, r6);
        L_0x0111:
            if (r3 >= r6) goto L_0x013f;
        L_0x0113:
            r7 = r5.textByPosition;
            r8 = r3 << 16;
            r8 = r8 + r0;
            r1 = r5.arrayList;
            r1 = r1.get(r3);
            r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1;
            r1 = r1.getLayout();
            r1 = r1.getText();
            r7.put(r8, r1);
            r7 = r5.prefixTextByPosition;
            r1 = r5.arrayList;
            r1 = r1.get(r3);
            r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1;
            r1 = r1.getPrefix();
            r7.put(r8, r1);
            r3 = r3 + 1;
            goto L_0x0111;
        L_0x013f:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper.onNewViewSelected(org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView, org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void pickEndView() {
            if (isSelectionMode()) {
                this.startPeek = false;
                int i = this.endViewPosition;
                if (i >= 0) {
                    ArticleSelectableView articleSelectableView = (ArticleSelectableView) this.layoutManager.findViewByPosition(i);
                    if (articleSelectableView == null) {
                        this.selectedView = null;
                        return;
                    }
                    this.selectedView = articleSelectableView;
                    if (this.startViewPosition != this.endViewPosition) {
                        this.selectionStart = 0;
                    } else if (this.startViewChildPosition != this.endViewChildPosition) {
                        this.selectionStart = 0;
                    } else {
                        this.selectionStart = this.startViewOffset;
                    }
                    this.selectionEnd = this.endViewOffset;
                    CharSequence text = getText((ArticleSelectableView) this.selectedView, false);
                    if (this.selectionEnd > text.length()) {
                        this.selectionEnd = text.length();
                    }
                    this.arrayList.clear();
                    ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
                    if (!this.arrayList.isEmpty()) {
                        this.textX = ((TextLayoutBlock) this.arrayList.get(this.endViewChildPosition)).getX();
                        this.textY = ((TextLayoutBlock) this.arrayList.get(this.endViewChildPosition)).getY();
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void pickStartView() {
            if (isSelectionMode()) {
                this.startPeek = true;
                int i = this.startViewPosition;
                if (i >= 0) {
                    ArticleSelectableView articleSelectableView = (ArticleSelectableView) this.layoutManager.findViewByPosition(i);
                    if (articleSelectableView == null) {
                        this.selectedView = null;
                        return;
                    }
                    this.selectedView = articleSelectableView;
                    if (this.startViewPosition != this.endViewPosition) {
                        this.selectionEnd = getText((ArticleSelectableView) this.selectedView, false).length();
                    } else if (this.startViewChildPosition != this.endViewChildPosition) {
                        this.selectionEnd = getText((ArticleSelectableView) this.selectedView, false).length();
                    } else {
                        this.selectionEnd = this.endViewOffset;
                    }
                    this.selectionStart = this.startViewOffset;
                    this.arrayList.clear();
                    ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
                    if (!this.arrayList.isEmpty()) {
                        this.textX = ((TextLayoutBlock) this.arrayList.get(this.startViewChildPosition)).getX();
                        this.textY = ((TextLayoutBlock) this.arrayList.get(this.startViewChildPosition)).getY();
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onOffsetChanged() {
            int adapterPosition = getAdapterPosition((ArticleSelectableView) this.selectedView);
            int i = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (adapterPosition == this.startViewPosition && i == this.startViewChildPosition) {
                this.startViewOffset = this.selectionStart;
            }
            if (adapterPosition == this.endViewPosition && i == this.endViewChildPosition) {
                this.endViewOffset = this.selectionEnd;
            }
        }

        public void invalidate() {
            super.invalidate();
            for (int i = 0; i < this.parentView.getChildCount(); i++) {
                this.parentView.getChildAt(i).invalidate();
            }
        }

        public void clear(boolean z) {
            super.clear(z);
            this.startViewPosition = -1;
            this.endViewPosition = -1;
            this.startViewChildPosition = -1;
            this.endViewChildPosition = -1;
            this.textByPosition.clear();
            this.childCountByPosition.clear();
        }

        /* Access modifiers changed, original: protected */
        public CharSequence getTextForCopy() {
            int i;
            int i2;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            int i3 = this.startViewPosition;
            while (true) {
                i = this.endViewPosition;
                if (i3 > i) {
                    break;
                }
                i2 = this.startViewPosition;
                int i4;
                int i5;
                CharSequence charSequence;
                CharSequence charSequence2;
                if (i3 == i2) {
                    i = i2 == i ? this.endViewChildPosition : this.childCountByPosition.get(i3) - 1;
                    i4 = this.startViewChildPosition;
                    while (i4 <= i) {
                        i5 = (i4 << 16) + i3;
                        charSequence = (CharSequence) this.textByPosition.get(i5);
                        if (this.startViewPosition == this.endViewPosition && i4 == this.endViewChildPosition && i4 == this.startViewChildPosition) {
                            spannableStringBuilder.append(charSequence.subSequence(this.startViewOffset, this.endViewOffset));
                            spannableStringBuilder.append(10);
                        } else if (i4 == this.startViewChildPosition) {
                            spannableStringBuilder.append(charSequence.subSequence(this.startViewOffset, charSequence.length()));
                            spannableStringBuilder.append(10);
                        } else {
                            charSequence2 = (CharSequence) this.prefixTextByPosition.get(i5);
                            if (charSequence2 != null) {
                                spannableStringBuilder.append(charSequence2).append(' ');
                            }
                            spannableStringBuilder.append(charSequence);
                            spannableStringBuilder.append(10);
                        }
                        i4++;
                    }
                } else if (i3 == i) {
                    i = 0;
                    while (i <= this.endViewChildPosition) {
                        i5 = (i << 16) + i3;
                        charSequence = (CharSequence) this.textByPosition.get(i5);
                        if (this.startViewPosition == this.endViewPosition && i == this.endViewChildPosition && i == this.startViewChildPosition) {
                            spannableStringBuilder.append(charSequence.subSequence(this.startViewOffset, this.endViewOffset));
                            spannableStringBuilder.append(10);
                        } else if (i == this.endViewChildPosition) {
                            charSequence2 = (CharSequence) this.prefixTextByPosition.get(i5);
                            if (charSequence2 != null) {
                                spannableStringBuilder.append(charSequence2).append(' ');
                            }
                            spannableStringBuilder.append(charSequence.subSequence(0, this.endViewOffset));
                            spannableStringBuilder.append(10);
                        } else {
                            charSequence2 = (CharSequence) this.prefixTextByPosition.get(i5);
                            if (charSequence2 != null) {
                                spannableStringBuilder.append(charSequence2).append(' ');
                            }
                            spannableStringBuilder.append(charSequence);
                            spannableStringBuilder.append(10);
                        }
                        i++;
                    }
                } else {
                    i = this.childCountByPosition.get(i3);
                    for (i4 = this.startViewChildPosition; i4 < i; i4++) {
                        i5 = (i4 << 16) + i3;
                        charSequence = (CharSequence) this.prefixTextByPosition.get(i5);
                        if (charSequence != null) {
                            spannableStringBuilder.append(charSequence).append(' ');
                        }
                        spannableStringBuilder.append((CharSequence) this.textByPosition.get(i5));
                        spannableStringBuilder.append(10);
                    }
                }
                i3++;
            }
            if (spannableStringBuilder.length() <= 0) {
                return null;
            }
            for (Object obj : (IngnoreCopySpanable[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length() - 1, IngnoreCopySpanable.class)) {
                spannableStringBuilder.delete(spannableStringBuilder.getSpanStart(obj), spannableStringBuilder.getSpanEnd(obj));
            }
            return spannableStringBuilder.subSequence(0, spannableStringBuilder.length() - 1);
        }

        /* Access modifiers changed, original: protected */
        public boolean selectLayout(int i, int i2) {
            if (!this.multiselect) {
                return false;
            }
            int childCount;
            if (i2 <= ((ArticleSelectableView) this.selectedView).getTop() || i2 >= ((ArticleSelectableView) this.selectedView).getBottom()) {
                childCount = this.parentView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    if (isSelectable(this.parentView.getChildAt(i3))) {
                        ArticleSelectableView articleSelectableView = (ArticleSelectableView) this.parentView.getChildAt(i3);
                        if (i2 > articleSelectableView.getTop() && i2 < articleSelectableView.getBottom()) {
                            i = findClosestLayoutIndex((int) (((float) i) - articleSelectableView.getX()), (int) (((float) i2) - articleSelectableView.getY()), articleSelectableView);
                            if (i < 0) {
                                return false;
                            }
                            onNewViewSelected((ArticleSelectableView) this.selectedView, articleSelectableView, i);
                            this.selectedView = articleSelectableView;
                            return true;
                        }
                    }
                }
                return false;
            }
            childCount = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            i = findClosestLayoutIndex((int) (((float) i) - ((ArticleSelectableView) this.selectedView).getX()), (int) (((float) i2) - ((ArticleSelectableView) this.selectedView).getY()), (ArticleSelectableView) this.selectedView);
            if (i == childCount || i < 0) {
                return false;
            }
            SelectableView selectableView = this.selectedView;
            onNewViewSelected((ArticleSelectableView) selectableView, (ArticleSelectableView) selectableView, i);
            return true;
        }

        /* Access modifiers changed, original: protected */
        public boolean canSelect(int i) {
            return (this.startViewPosition == this.endViewPosition && this.startViewChildPosition == this.endViewChildPosition) ? super.canSelect(i) : true;
        }

        /* Access modifiers changed, original: protected */
        public void jumpToLine(int i, int i2, boolean z, float f, float f2, ArticleSelectableView articleSelectableView) {
            if (!z || articleSelectableView != this.selectedView || f2 != f) {
                super.jumpToLine(i, i2, z, f, f2, articleSelectableView);
            } else if (this.movingHandleStart) {
                this.selectionStart = i;
            } else {
                this.selectionEnd = i;
            }
        }

        /* Access modifiers changed, original: protected */
        public boolean canShowActions() {
            int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
            if ((findFirstVisibleItemPosition >= this.startViewPosition && findFirstVisibleItemPosition <= this.endViewPosition) || (findLastVisibleItemPosition >= this.startViewPosition && findLastVisibleItemPosition <= this.endViewPosition)) {
                return true;
            }
            if (this.startViewPosition < findFirstVisibleItemPosition || this.endViewPosition > findLastVisibleItemPosition) {
                return false;
            }
            return true;
        }
    }

    public static class ChatListTextSelectionHelper extends TextSelectionHelper<ChatMessageCell> {
        public static int TYPE_CAPTION = 1;
        public static int TYPE_DESCRIPTION = 2;
        public static int TYPE_MESSAGE;
        SparseArray<Animator> animatorSparseArray = new SparseArray();
        private boolean isDescription;
        private boolean maybeIsDescription;

        /* Access modifiers changed, original: protected */
        public int getLineHeight() {
            SelectableView selectableView = this.selectedView;
            if (selectableView == null || ((ChatMessageCell) selectableView).getMessageObject() == null) {
                return 0;
            }
            StaticLayout descriptionlayout;
            MessageObject messageObject = ((ChatMessageCell) this.selectedView).getMessageObject();
            if (this.isDescription) {
                descriptionlayout = ((ChatMessageCell) this.selectedView).getDescriptionlayout();
            } else if (((ChatMessageCell) this.selectedView).hasCaptionLayout()) {
                descriptionlayout = ((ChatMessageCell) this.selectedView).getCaptionLayout();
            } else {
                descriptionlayout = ((org.telegram.messenger.MessageObject.TextLayoutBlock) messageObject.textLayoutBlocks.get(0)).textLayout;
            }
            return descriptionlayout.getLineBottom(0) - descriptionlayout.getLineTop(0);
        }

        public void setMessageObject(ChatMessageCell chatMessageCell) {
            this.maybeSelectedView = chatMessageCell;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            Rect rect;
            int i;
            if (this.maybeIsDescription) {
                rect = this.textArea;
                i = this.maybeTextX;
                rect.set(i, this.maybeTextY, chatMessageCell.getDescriptionlayout().getWidth() + i, this.maybeTextY + chatMessageCell.getDescriptionlayout().getHeight());
            } else if (chatMessageCell.hasCaptionLayout()) {
                rect = this.textArea;
                i = this.maybeTextX;
                rect.set(i, this.maybeTextY, chatMessageCell.getCaptionLayout().getWidth() + i, this.maybeTextY + chatMessageCell.getCaptionLayout().getHeight());
            } else if (messageObject != null && messageObject.textLayoutBlocks.size() > 0) {
                ArrayList arrayList = messageObject.textLayoutBlocks;
                org.telegram.messenger.MessageObject.TextLayoutBlock textLayoutBlock = (org.telegram.messenger.MessageObject.TextLayoutBlock) arrayList.get(arrayList.size() - 1);
                rect = this.textArea;
                i = this.maybeTextX;
                rect.set(i, this.maybeTextY, textLayoutBlock.textLayout.getWidth() + i, (int) ((((float) this.maybeTextY) + textLayoutBlock.textYOffset) + ((float) textLayoutBlock.textLayout.getHeight())));
            }
        }

        /* Access modifiers changed, original: protected */
        public CharSequence getText(ChatMessageCell chatMessageCell, boolean z) {
            if (chatMessageCell == null || chatMessageCell.getMessageObject() == null) {
                return null;
            }
            if (!z ? !this.isDescription : !this.maybeIsDescription) {
                return chatMessageCell.getDescriptionlayout().getText();
            }
            if (chatMessageCell.hasCaptionLayout()) {
                return chatMessageCell.getCaptionLayout().getText();
            }
            return chatMessageCell.getMessageObject().messageText;
        }

        /* Access modifiers changed, original: protected */
        public void onTextSelected(ChatMessageCell chatMessageCell, ChatMessageCell chatMessageCell2) {
            boolean z = chatMessageCell2 == null || !(chatMessageCell2.getMessageObject() == null || chatMessageCell2.getMessageObject().getId() == chatMessageCell.getMessageObject().getId());
            this.selectedCellId = chatMessageCell.getMessageObject().getId();
            this.enterProgress = 0.0f;
            this.isDescription = this.maybeIsDescription;
            Animator animator = (Animator) this.animatorSparseArray.get(this.selectedCellId);
            if (animator != null) {
                animator.removeAllListeners();
                animator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new -$$Lambda$TextSelectionHelper$ChatListTextSelectionHelper$WrmBbG-q9A0E3E_wl1gt8Iu4iw0(this, z));
            ofFloat.setDuration(250);
            ofFloat.start();
            this.animatorSparseArray.put(this.selectedCellId, ofFloat);
            if (!z) {
                chatMessageCell.setSelectedBackgroundProgress(0.0f);
            }
            SharedConfig.removeTextSelectionHint();
        }

        public /* synthetic */ void lambda$onTextSelected$0$TextSelectionHelper$ChatListTextSelectionHelper(boolean z, ValueAnimator valueAnimator) {
            this.enterProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
            if (textSelectionOverlay != null) {
                textSelectionOverlay.invalidate();
            }
            SelectableView selectableView = this.selectedView;
            if (selectableView != null && ((ChatMessageCell) selectableView).getCurrentMessagesGroup() == null && z) {
                ((ChatMessageCell) this.selectedView).setSelectedBackgroundProgress(1.0f - this.enterProgress);
            }
        }

        public void draw(MessageObject messageObject, org.telegram.messenger.MessageObject.TextLayoutBlock textLayoutBlock, Canvas canvas) {
            SelectableView selectableView = this.selectedView;
            if (!(selectableView == null || ((ChatMessageCell) selectableView).getMessageObject() == null || this.isDescription)) {
                MessageObject messageObject2 = ((ChatMessageCell) this.selectedView).getMessageObject();
                if (messageObject2 != null && messageObject.getId() == this.selectedCellId) {
                    int i = this.selectionStart;
                    int i2 = this.selectionEnd;
                    if (((ChatMessageCell) this.selectedView).getMessageObject().textLayoutBlocks.size() > 1) {
                        int i3 = textLayoutBlock.charactersOffset;
                        if (i < i3) {
                            i = i3;
                        }
                        i3 = textLayoutBlock.charactersEnd;
                        if (i > i3) {
                            i = i3;
                        }
                        i3 = textLayoutBlock.charactersOffset;
                        if (i2 < i3) {
                            i2 = i3;
                        }
                        i3 = textLayoutBlock.charactersEnd;
                        if (i2 > i3) {
                            i2 = i3;
                        }
                    }
                    if (i != i2) {
                        if (messageObject2.isOutOwner()) {
                            this.selectionPaint.setColor(Theme.getColor("chat_outTextSelectionHighlight"));
                        } else {
                            this.selectionPaint.setColor(Theme.getColor("chat_inTextSelectionHighlight"));
                        }
                        drawSelection(canvas, textLayoutBlock.textLayout, i, i2);
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public int getCharOffsetFromCord(int i, int i2, int i3, int i4, ChatMessageCell chatMessageCell, boolean z) {
            int i5 = 0;
            if (chatMessageCell == null) {
                return 0;
            }
            StaticLayout descriptionlayout;
            i -= i3;
            i2 -= i4;
            float f = 0.0f;
            if (z ? this.maybeIsDescription : this.isDescription) {
                descriptionlayout = chatMessageCell.getDescriptionlayout();
            } else if (chatMessageCell.hasCaptionLayout()) {
                descriptionlayout = chatMessageCell.getCaptionLayout();
            } else {
                org.telegram.messenger.MessageObject.TextLayoutBlock textLayoutBlock = (org.telegram.messenger.MessageObject.TextLayoutBlock) chatMessageCell.getMessageObject().textLayoutBlocks.get(chatMessageCell.getMessageObject().textLayoutBlocks.size() - 1);
                descriptionlayout = textLayoutBlock.textLayout;
                f = textLayoutBlock.textYOffset;
            }
            if (i2 < 0) {
                i2 = 1;
            }
            if (((float) i2) > ((float) descriptionlayout.getLineBottom(descriptionlayout.getLineCount() - 1)) + f) {
                i2 = (int) ((f + ((float) descriptionlayout.getLineBottom(descriptionlayout.getLineCount() - 1))) - 1.0f);
            }
            fillLayoutForCoords(i, i2, chatMessageCell, this.layoutBlock, z);
            LayoutBlock layoutBlock = this.layoutBlock;
            descriptionlayout = layoutBlock.layout;
            if (descriptionlayout == null) {
                return -1;
            }
            i = (int) (((float) i) - layoutBlock.xOffset);
            while (i5 < descriptionlayout.getLineCount()) {
                f = (float) i2;
                if (f > this.layoutBlock.yOffset + ((float) descriptionlayout.getLineTop(i5)) && f < this.layoutBlock.yOffset + ((float) descriptionlayout.getLineBottom(i5))) {
                    break;
                }
                i5++;
            }
            i5 = -1;
            if (i5 >= 0) {
                return descriptionlayout.getOffsetForHorizontal(i5, (float) i);
            }
            return -1;
        }

        private void fillLayoutForCoords(int i, int i2, ChatMessageCell chatMessageCell, LayoutBlock layoutBlock, boolean z) {
            if (chatMessageCell != null) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (!z ? !this.isDescription : !this.maybeIsDescription) {
                    layoutBlock.layout = chatMessageCell.getDescriptionlayout();
                    layoutBlock.xOffset = 0.0f;
                    layoutBlock.yOffset = 0.0f;
                } else if (chatMessageCell.hasCaptionLayout()) {
                    layoutBlock.layout = chatMessageCell.getCaptionLayout();
                    layoutBlock.xOffset = 0.0f;
                    layoutBlock.yOffset = 0.0f;
                } else {
                    int i3 = 0;
                    int i4 = 0;
                    while (i4 < messageObject.textLayoutBlocks.size()) {
                        org.telegram.messenger.MessageObject.TextLayoutBlock textLayoutBlock = (org.telegram.messenger.MessageObject.TextLayoutBlock) messageObject.textLayoutBlocks.get(i4);
                        float f = (float) i2;
                        float f2 = textLayoutBlock.textYOffset;
                        if (f < f2 || f > ((float) textLayoutBlock.height) + f2) {
                            i4++;
                        } else {
                            layoutBlock.layout = textLayoutBlock.textLayout;
                            layoutBlock.yOffset = f2;
                            if (textLayoutBlock.isRtl()) {
                                i3 = (int) Math.ceil((double) messageObject.textXOffset);
                            }
                            layoutBlock.xOffset = (float) (-i3);
                            return;
                        }
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) (z ? this.maybeSelectedView : this.selectedView);
            if (chatMessageCell == null) {
                layoutBlock.layout = null;
                return;
            }
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (this.isDescription) {
                layoutBlock.layout = chatMessageCell.getDescriptionlayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else if (chatMessageCell.hasCaptionLayout()) {
                layoutBlock.layout = chatMessageCell.getCaptionLayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else {
                int i2 = 0;
                if (messageObject.textLayoutBlocks.size() == 1) {
                    layoutBlock.layout = ((org.telegram.messenger.MessageObject.TextLayoutBlock) messageObject.textLayoutBlocks.get(0)).textLayout;
                    layoutBlock.yOffset = 0.0f;
                    if (((org.telegram.messenger.MessageObject.TextLayoutBlock) messageObject.textLayoutBlocks.get(0)).isRtl()) {
                        i2 = (int) Math.ceil((double) messageObject.textXOffset);
                    }
                    layoutBlock.xOffset = (float) (-i2);
                    return;
                }
                int i3 = 0;
                while (i3 < messageObject.textLayoutBlocks.size()) {
                    org.telegram.messenger.MessageObject.TextLayoutBlock textLayoutBlock = (org.telegram.messenger.MessageObject.TextLayoutBlock) messageObject.textLayoutBlocks.get(i3);
                    if (i < textLayoutBlock.charactersOffset || i > textLayoutBlock.charactersEnd) {
                        i3++;
                    } else {
                        layoutBlock.layout = ((org.telegram.messenger.MessageObject.TextLayoutBlock) messageObject.textLayoutBlocks.get(i3)).textLayout;
                        layoutBlock.yOffset = ((org.telegram.messenger.MessageObject.TextLayoutBlock) messageObject.textLayoutBlocks.get(i3)).textYOffset;
                        if (textLayoutBlock.isRtl()) {
                            i2 = (int) Math.ceil((double) messageObject.textXOffset);
                        }
                        layoutBlock.xOffset = (float) (-i2);
                        return;
                    }
                }
                layoutBlock.layout = null;
            }
        }

        /* Access modifiers changed, original: protected */
        public void onExitSelectionMode(boolean z) {
            SelectableView selectableView = this.selectedView;
            if (selectableView != null && ((ChatMessageCell) selectableView).isDrawingSelectionBackground() && !z) {
                SelectableView selectableView2 = this.selectedView;
                final ChatMessageCell chatMessageCell = (ChatMessageCell) selectableView2;
                int id = ((ChatMessageCell) selectableView2).getMessageObject().getId();
                Animator animator = (Animator) this.animatorSparseArray.get(id);
                if (animator != null) {
                    animator.removeAllListeners();
                    animator.cancel();
                }
                chatMessageCell.setSelectedBackgroundProgress(0.01f);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.01f, 1.0f});
                ofFloat.addUpdateListener(new -$$Lambda$TextSelectionHelper$ChatListTextSelectionHelper$GhFjRffOqivFT1D6v-74EEa8DCc(chatMessageCell, id));
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        chatMessageCell.setSelectedBackgroundProgress(0.0f);
                    }
                });
                ofFloat.setDuration(300);
                ofFloat.start();
                this.animatorSparseArray.put(id, ofFloat);
            }
        }

        static /* synthetic */ void lambda$onExitSelectionMode$1(ChatMessageCell chatMessageCell, int i, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() == i) {
                chatMessageCell.setSelectedBackgroundProgress(floatValue);
            }
        }

        public void onChatMessageCellAttached(ChatMessageCell chatMessageCell) {
            if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() == this.selectedCellId) {
                this.selectedView = chatMessageCell;
            }
        }

        public void onChatMessageCellDetached(ChatMessageCell chatMessageCell) {
            if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() == this.selectedCellId) {
                this.selectedView = null;
            }
        }

        public void drawCaption(boolean z, StaticLayout staticLayout, Canvas canvas) {
            if (!this.isDescription) {
                if (z) {
                    this.selectionPaint.setColor(Theme.getColor("chat_outTextSelectionHighlight"));
                } else {
                    this.selectionPaint.setColor(Theme.getColor("chat_inTextSelectionHighlight"));
                }
                drawSelection(canvas, staticLayout, this.selectionStart, this.selectionEnd);
            }
        }

        public void drawDescription(boolean z, StaticLayout staticLayout, Canvas canvas) {
            if (this.isDescription) {
                if (z) {
                    this.selectionPaint.setColor(Theme.getColor("chat_outTextSelectionHighlight"));
                } else {
                    this.selectionPaint.setColor(Theme.getColor("chat_inTextSelectionHighlight"));
                }
                drawSelection(canvas, staticLayout, this.selectionStart, this.selectionEnd);
            }
        }

        public void invalidate() {
            super.invalidate();
            SelectableView selectableView = this.selectedView;
            if (selectableView != null && ((ChatMessageCell) selectableView).getCurrentMessagesGroup() != null) {
                this.parentView.invalidate();
            }
        }

        public void cancelAllAnimators() {
            for (int i = 0; i < this.animatorSparseArray.size(); i++) {
                SparseArray sparseArray = this.animatorSparseArray;
                ((Animator) sparseArray.get(sparseArray.keyAt(i))).cancel();
            }
            this.animatorSparseArray.clear();
        }

        public void setIsDescription(boolean z) {
            this.maybeIsDescription = z;
        }

        public void clear(boolean z) {
            super.clear(z);
            this.isDescription = false;
        }

        public int getTextSelectionType(ChatMessageCell chatMessageCell) {
            if (this.isDescription) {
                return TYPE_DESCRIPTION;
            }
            if (chatMessageCell.hasCaptionLayout()) {
                return TYPE_CAPTION;
            }
            return TYPE_MESSAGE;
        }

        public void updateTextPosition(int i, int i2) {
            if (this.textX != i || this.textY != i2) {
                this.textX = i;
                this.textY = i2;
                invalidate();
            }
        }
    }

    public abstract void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z);

    public abstract int getCharOffsetFromCord(int i, int i2, int i3, int i4, Cell cell, boolean z);

    public abstract int getLineHeight();

    public abstract CharSequence getText(Cell cell, boolean z);

    /* Access modifiers changed, original: protected */
    public boolean isSelectable(View view) {
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onExitSelectionMode(boolean z) {
    }

    /* Access modifiers changed, original: protected */
    public void onOffsetChanged() {
    }

    public abstract void onTextSelected(Cell cell, Cell cell2);

    /* Access modifiers changed, original: protected */
    public void pickEndView() {
    }

    /* Access modifiers changed, original: protected */
    public void pickStartView() {
    }

    /* Access modifiers changed, original: protected */
    public boolean selectLayout(int i, int i2) {
        return false;
    }

    public void setMaybeTextCord(int i, int i2) {
        this.maybeTextX = i;
        this.maybeTextY = i2;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int x;
        int i;
        if (action != 0) {
            if (action != 1) {
                if (action == 2) {
                    action = (int) motionEvent.getY();
                    x = (int) motionEvent.getX();
                    int i2 = this.capturedY;
                    i = (i2 - action) * (i2 - action);
                    action = this.capturedX;
                    if (i + ((action - x) * (action - x)) > this.touchSlop) {
                        AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
                        this.tryCapture = false;
                    }
                    return this.tryCapture;
                } else if (action != 3) {
                    return false;
                }
            }
            AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
            this.tryCapture = false;
            return false;
        }
        this.capturedX = (int) motionEvent.getX();
        this.capturedY = (int) motionEvent.getY();
        this.tryCapture = false;
        this.textArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
        if (this.textArea.contains(this.capturedX, this.capturedY)) {
            this.textArea.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            x = this.capturedX;
            action = this.capturedY;
            i = this.textArea.right;
            if (x > i) {
                x = i - 1;
            }
            i = this.textArea.left;
            if (x < i) {
                x = i + 1;
            }
            i = this.textArea.top;
            if (action < i) {
                action = i + 1;
            }
            i = this.textArea.bottom;
            action = getCharOffsetFromCord(x, action > i ? i - 1 : action, this.maybeTextX, this.maybeTextY, this.maybeSelectedView, true);
            CharSequence text = getText(this.maybeSelectedView, true);
            if (action >= text.length()) {
                fillLayoutForOffset(action, this.layoutBlock, true);
                StaticLayout staticLayout = this.layoutBlock.layout;
                if (staticLayout == null) {
                    this.tryCapture = false;
                    return this.tryCapture;
                }
                int lineCount = staticLayout.getLineCount() - 1;
                float f = (float) (x - this.maybeTextX);
                if (f < this.layoutBlock.layout.getLineRight(lineCount) + ((float) AndroidUtilities.dp(4.0f)) && f > this.layoutBlock.layout.getLineLeft(lineCount)) {
                    action = text.length() - 1;
                }
            }
            if (action >= 0 && action < text.length() && text.charAt(action) != 10) {
                AndroidUtilities.runOnUIThread(this.startSelectionRunnable, (long) this.longpressDelay);
                this.tryCapture = true;
            }
        }
        return this.tryCapture;
    }

    private void hideMagnifier() {
        if (VERSION.SDK_INT >= 28) {
            Magnifier magnifier = this.magnifier;
            if (magnifier != null) {
                magnifier.dismiss();
                this.magnifier = null;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00c8  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ea  */
    private void showMagnifier(int r8) {
        /*
        r7 = this;
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 28;
        if (r0 < r1) goto L_0x0109;
    L_0x0006:
        r0 = r7.selectedView;
        if (r0 == 0) goto L_0x0109;
    L_0x000a:
        r0 = r7.isOneTouch;
        if (r0 != 0) goto L_0x0109;
    L_0x000e:
        r0 = r7.movingHandle;
        if (r0 == 0) goto L_0x0109;
    L_0x0012:
        r0 = r7.textSelectionOverlay;
        if (r0 != 0) goto L_0x0018;
    L_0x0016:
        goto L_0x0109;
    L_0x0018:
        r0 = r7.movingHandleStart;
        if (r0 == 0) goto L_0x001f;
    L_0x001c:
        r0 = r7.selectionStart;
        goto L_0x0021;
    L_0x001f:
        r0 = r7.selectionEnd;
    L_0x0021:
        r1 = r7.layoutBlock;
        r7.fillLayoutForOffset(r0, r1);
        r1 = r7.layoutBlock;
        r1 = r1.layout;
        if (r1 != 0) goto L_0x002d;
    L_0x002c:
        return;
    L_0x002d:
        r0 = r1.getLineForOffset(r0);
        r2 = r1.getLineBottom(r0);
        r3 = r1.getLineTop(r0);
        r2 = r2 - r3;
        r3 = r1.getLineTop(r0);
        r4 = r7.textY;
        r3 = r3 + r4;
        r3 = (float) r3;
        r4 = r7.selectedView;
        r4 = r4.getY();
        r3 = r3 + r4;
        r3 = (int) r3;
        r3 = r3 - r2;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r5;
        r3 = (float) r3;
        r5 = r7.layoutBlock;
        r5 = r5.yOffset;
        r3 = r3 + r5;
        r3 = (int) r3;
        r5 = r7.magnifierY;
        r3 = (float) r3;
        r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x006a;
    L_0x0060:
        r7.magnifierY = r3;
        r5 = r7.magnifierYanimated;
        r3 = r3 - r5;
        r5 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r3 = r3 / r5;
        r7.magnifierDy = r3;
    L_0x006a:
        r3 = r7.magnifier;
        if (r3 != 0) goto L_0x007b;
    L_0x006e:
        r3 = new android.widget.Magnifier;
        r5 = r7.textSelectionOverlay;
        r3.<init>(r5);
        r7.magnifier = r3;
        r3 = r7.magnifierY;
        r7.magnifierYanimated = r3;
    L_0x007b:
        r3 = r7.magnifierYanimated;
        r5 = r7.magnifierY;
        r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r5 == 0) goto L_0x008c;
    L_0x0083:
        r5 = r7.magnifierDy;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5 = r5 * r6;
        r3 = r3 + r5;
        r7.magnifierYanimated = r3;
    L_0x008c:
        r3 = r7.magnifierDy;
        r5 = 0;
        r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r3 <= 0) goto L_0x009e;
    L_0x0093:
        r3 = r7.magnifierYanimated;
        r6 = r7.magnifierY;
        r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
        if (r3 <= 0) goto L_0x009e;
    L_0x009b:
        r7.magnifierYanimated = r6;
        goto L_0x00ae;
    L_0x009e:
        r3 = r7.magnifierDy;
        r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r3 >= 0) goto L_0x00ae;
    L_0x00a4:
        r3 = r7.magnifierYanimated;
        r5 = r7.magnifierY;
        r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r3 >= 0) goto L_0x00ae;
    L_0x00ac:
        r7.magnifierYanimated = r5;
    L_0x00ae:
        r3 = r7.selectedView;
        r5 = r3 instanceof org.telegram.ui.ArticleViewer.BlockTableCell;
        if (r5 == 0) goto L_0x00c8;
    L_0x00b4:
        r0 = r3.getX();
        r0 = (int) r0;
        r1 = r7.selectedView;
        r1 = r1.getX();
        r1 = (int) r1;
        r3 = r7.selectedView;
        r3 = r3.getMeasuredWidth();
        r1 = r1 + r3;
        goto L_0x00e8;
    L_0x00c8:
        r3 = r3.getX();
        r5 = r7.textX;
        r5 = (float) r5;
        r3 = r3 + r5;
        r5 = r1.getLineLeft(r0);
        r3 = r3 + r5;
        r3 = (int) r3;
        r5 = r7.selectedView;
        r5 = r5.getX();
        r6 = r7.textX;
        r6 = (float) r6;
        r5 = r5 + r6;
        r0 = r1.getLineRight(r0);
        r5 = r5 + r0;
        r0 = (int) r5;
        r1 = r0;
        r0 = r3;
    L_0x00e8:
        if (r8 >= r0) goto L_0x00ec;
    L_0x00ea:
        r8 = r0;
        goto L_0x00ef;
    L_0x00ec:
        if (r8 <= r1) goto L_0x00ef;
    L_0x00ee:
        r8 = r1;
    L_0x00ef:
        r0 = r7.magnifier;
        r8 = (float) r8;
        r1 = r7.magnifierYanimated;
        r2 = (float) r2;
        r3 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r2 = r2 * r3;
        r1 = r1 + r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = (float) r2;
        r1 = r1 + r2;
        r0.show(r8, r1);
        r8 = r7.magnifier;
        r8.update();
    L_0x0109:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.showMagnifier(int):void");
    }

    /* Access modifiers changed, original: protected */
    public void showHandleViews() {
        if (this.handleViewProgress != 1.0f && this.textSelectionOverlay != null) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new -$$Lambda$TextSelectionHelper$DsXmQZvfGpCcORdBIu4jWbyY2gE(this));
            ofFloat.setDuration(250);
            ofFloat.start();
        }
    }

    public /* synthetic */ void lambda$showHandleViews$0$TextSelectionHelper(ValueAnimator valueAnimator) {
        this.handleViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.textSelectionOverlay.invalidate();
    }

    public boolean isSelectionMode() {
        return this.selectionStart >= 0 && this.selectionEnd >= 0;
    }

    /* Access modifiers changed, original: 0000 */
    public float distance(int i, int i2, int i3, int i4) {
        i -= i3;
        i2 -= i4;
        return (float) Math.sqrt((double) ((i * i) + (i2 * i2)));
    }

    /* JADX WARNING: Missing block: B:39:0x01b7, code skipped:
            if (r0 < 0) goto L_0x01b9;
     */
    private void showActions() {
        /*
        r9 = this;
        r0 = r9.textSelectionOverlay;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 23;
        r2 = 1;
        if (r0 < r1) goto L_0x006a;
    L_0x000c:
        r0 = r9.movingHandle;
        if (r0 != 0) goto L_0x01cd;
    L_0x0010:
        r0 = r9.isSelectionMode();
        if (r0 == 0) goto L_0x01cd;
    L_0x0016:
        r0 = r9.canShowActions();
        if (r0 == 0) goto L_0x01cd;
    L_0x001c:
        r0 = r9.actionsIsShowing;
        if (r0 != 0) goto L_0x0061;
    L_0x0020:
        r0 = r9.actionMode;
        if (r0 != 0) goto L_0x004f;
    L_0x0024:
        r0 = new org.telegram.ui.ActionBar.FloatingToolbar;
        r1 = r9.textSelectionOverlay;
        r1 = r1.getContext();
        r3 = r9.textSelectionOverlay;
        r0.<init>(r1, r3, r2);
        r1 = new org.telegram.ui.ActionBar.FloatingActionMode;
        r3 = r9.textSelectionOverlay;
        r3 = r3.getContext();
        r4 = r9.textSelectActionCallback;
        r4 = (android.view.ActionMode.Callback2) r4;
        r5 = r9.textSelectionOverlay;
        r1.<init>(r3, r4, r5, r0);
        r9.actionMode = r1;
        r0 = r9.textSelectActionCallback;
        r1 = r9.actionMode;
        r3 = r1.getMenu();
        r0.onCreateActionMode(r1, r3);
    L_0x004f:
        r0 = r9.textSelectActionCallback;
        r1 = r9.actionMode;
        r3 = r1.getMenu();
        r0.onPrepareActionMode(r1, r3);
        r0 = r9.actionMode;
        r3 = 1;
        r0.hide(r3);
    L_0x0061:
        r0 = r9.hideActionsRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r9.actionsIsShowing = r2;
        goto L_0x01cd;
    L_0x006a:
        r0 = r9.showActionsAsPopupAlways;
        if (r0 != 0) goto L_0x0084;
    L_0x006e:
        r0 = r9.actionMode;
        if (r0 != 0) goto L_0x01cd;
    L_0x0072:
        r0 = r9.isSelectionMode();
        if (r0 == 0) goto L_0x01cd;
    L_0x0078:
        r0 = r9.textSelectionOverlay;
        r1 = r9.textSelectActionCallback;
        r0 = r0.startActionMode(r1);
        r9.actionMode = r0;
        goto L_0x01cd;
    L_0x0084:
        r0 = r9.movingHandle;
        if (r0 != 0) goto L_0x01cd;
    L_0x0088:
        r0 = r9.isSelectionMode();
        if (r0 == 0) goto L_0x01cd;
    L_0x008e:
        r0 = r9.canShowActions();
        if (r0 == 0) goto L_0x01cd;
    L_0x0094:
        r0 = r9.popupLayout;
        r1 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r3 = 2;
        r4 = 0;
        if (r0 != 0) goto L_0x0190;
    L_0x009c:
        r0 = new android.graphics.Rect;
        r0.<init>();
        r9.popupRect = r0;
        r0 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout;
        r5 = r9.textSelectionOverlay;
        r5 = r5.getContext();
        r0.<init>(r5);
        r9.popupLayout = r0;
        r0 = r9.popupLayout;
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r7 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r8 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.setPadding(r6, r7, r8, r5);
        r0 = r9.popupLayout;
        r5 = r9.textSelectionOverlay;
        r5 = r5.getContext();
        r5 = r5.getResources();
        r6 = NUM; // 0x7var_ float:1.7945403E38 double:1.052935704E-314;
        r5 = r5.getDrawable(r6);
        r0.setBackgroundDrawable(r5);
        r0 = r9.popupLayout;
        r0.setAnimationEnabled(r4);
        r0 = r9.popupLayout;
        r5 = new org.telegram.ui.Cells.-$$Lambda$TextSelectionHelper$x06-6ENfoy8lQmk5kqSAGHI_e2k;
        r5.<init>(r9);
        r0.setOnTouchListener(r5);
        r0 = r9.popupLayout;
        r0.setShowedFromBotton(r4);
        r0 = new android.widget.TextView;
        r5 = r9.textSelectionOverlay;
        r5 = r5.getContext();
        r0.<init>(r5);
        r9.deleteView = r0;
        r0 = r9.deleteView;
        r5 = "listSelectorSDK21";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r3);
        r0.setBackgroundDrawable(r5);
        r0 = r9.deleteView;
        r5 = 16;
        r0.setGravity(r5);
        r0 = r9.deleteView;
        r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.setPadding(r6, r4, r5, r4);
        r0 = r9.deleteView;
        r5 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r0.setTextSize(r2, r5);
        r0 = r9.deleteView;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r0.setTypeface(r5);
        r0 = r9.deleteView;
        r5 = r9.textSelectionOverlay;
        r5 = r5.getContext();
        r6 = 17039361; // 0x1040001 float:2.4244574E-38 double:8.418563E-317;
        r5 = r5.getString(r6);
        r0.setText(r5);
        r0 = r9.deleteView;
        r5 = "actionBarDefaultSubmenuItem";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.setTextColor(r5);
        r0 = r9.deleteView;
        r5 = new org.telegram.ui.Cells.-$$Lambda$TextSelectionHelper$JzqfpQBLTcb1Gp68Thh0vMrMa5c;
        r5.<init>(r9);
        r0.setOnClickListener(r5);
        r0 = r9.popupLayout;
        r5 = r9.deleteView;
        r6 = -2;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r1);
        r0.addView(r5, r7);
        r0 = new org.telegram.ui.ActionBar.ActionBarPopupWindow;
        r5 = r9.popupLayout;
        r0.<init>(r5, r6, r6);
        r9.popupWindow = r0;
        r0 = r9.popupWindow;
        r0.setAnimationEnabled(r4);
        r0 = r9.popupWindow;
        r5 = NUM; // 0x7f0var_ float:1.9007977E38 double:1.0531945397E-314;
        r0.setAnimationStyle(r5);
        r0 = r9.popupWindow;
        r0.setOutsideTouchable(r2);
        r0 = r9.popupLayout;
        if (r0 == 0) goto L_0x0190;
    L_0x0187:
        r5 = "actionBarDefaultSubmenuBackground";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.setBackgroundColor(r5);
    L_0x0190:
        r0 = r9.selectedView;
        if (r0 == 0) goto L_0x01b9;
    L_0x0194:
        r0 = r9.getLineHeight();
        r0 = -r0;
        r5 = r9.selectionStart;
        r5 = r9.offsetToCord(r5);
        r2 = r5[r2];
        r5 = r9.textY;
        r2 = r2 + r5;
        r2 = (float) r2;
        r5 = r9.selectedView;
        r5 = r5.getY();
        r2 = r2 + r5;
        r2 = (int) r2;
        r0 = r0 / r3;
        r2 = r2 + r0;
        r0 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r2 - r0;
        if (r0 >= 0) goto L_0x01ba;
    L_0x01b9:
        r0 = 0;
    L_0x01ba:
        r2 = r9.popupWindow;
        r3 = r9.textSelectionOverlay;
        r5 = 48;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = r0 - r1;
        r2.showAtLocation(r3, r5, r4, r0);
        r0 = r9.popupWindow;
        r0.startAnimation();
    L_0x01cd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.showActions():void");
    }

    public /* synthetic */ boolean lambda$showActions$1$TextSelectionHelper(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                view.getHitRect(this.popupRect);
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$showActions$2$TextSelectionHelper(View view) {
        copyText();
    }

    /* Access modifiers changed, original: protected */
    public boolean canShowActions() {
        return this.selectedView != null;
    }

    private void hideActions() {
        if (VERSION.SDK_INT >= 23) {
            if (this.actionMode != null && this.actionsIsShowing) {
                this.actionsIsShowing = false;
                this.hideActionsRunnable.run();
            }
            this.actionsIsShowing = false;
        }
        if (!isSelectionMode()) {
            ActionMode actionMode = this.actionMode;
            if (actionMode != null) {
                actionMode.finish();
                this.actionMode = null;
            }
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    public TextSelectionOverlay getOverlayView(Context context) {
        if (this.textSelectionOverlay == null) {
            this.textSelectionOverlay = new TextSelectionOverlay(context);
        }
        return this.textSelectionOverlay;
    }

    public boolean isSelected(MessageObject messageObject) {
        boolean z = false;
        if (messageObject == null) {
            return false;
        }
        if (this.selectedCellId == messageObject.getId()) {
            z = true;
        }
        return z;
    }

    public void checkSelectionCancel(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            cancelTextSelectionRunnable();
        }
    }

    public void cancelTextSelectionRunnable() {
        AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
        this.tryCapture = false;
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean z) {
        onExitSelectionMode(z);
        this.selectionStart = -1;
        this.selectionEnd = -1;
        hideMagnifier();
        hideActions();
        invalidate();
        this.selectedView = null;
        this.selectedCellId = 0;
        AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
        this.tryCapture = false;
        TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
        if (textSelectionOverlay != null) {
            textSelectionOverlay.setVisibility(8);
        }
        this.handleViewProgress = 0.0f;
        Callback callback = this.callback;
        if (callback != null) {
            callback.onStateChanged(false);
        }
        this.capturedX = -1;
        this.capturedY = -1;
        this.maybeTextX = -1;
        this.maybeTextY = -1;
        this.movingOffsetX = 0.0f;
        this.movingOffsetY = 0.0f;
        this.movingHandle = false;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean isTryingSelect() {
        return this.tryCapture;
    }

    public void onParentScrolled() {
        if (isSelectionMode()) {
            TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
            if (textSelectionOverlay != null) {
                this.parentIsScrolling = true;
                textSelectionOverlay.invalidate();
                hideActions();
            }
        }
    }

    public void stopScrolling() {
        this.parentIsScrolling = false;
        showActions();
    }

    public static boolean isInterruptedCharacter(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }

    public void setTopOffset(int i) {
        this.topOffset = i;
    }

    /* Access modifiers changed, original: protected */
    public void jumpToLine(int i, int i2, boolean z, float f, float f2, Cell cell) {
        if (this.movingHandleStart) {
            this.selectionStart = i2;
            if (!z) {
                i = this.selectionStart;
                i2 = this.selectionEnd;
                if (i > i2) {
                    this.selectionEnd = i;
                    this.selectionStart = i2;
                    this.movingHandleStart = false;
                }
            }
            this.snap = true;
            return;
        }
        this.selectionEnd = i2;
        if (!z) {
            i = this.selectionStart;
            i2 = this.selectionEnd;
            if (i > i2) {
                this.selectionEnd = i;
                this.selectionStart = i2;
                this.movingHandleStart = true;
            }
        }
        this.snap = true;
    }

    /* Access modifiers changed, original: protected */
    public boolean canSelect(int i) {
        return (i == this.selectionStart || i == this.selectionEnd) ? false : true;
    }

    public void invalidate() {
        SelectableView selectableView = this.selectedView;
        if (selectableView != null) {
            selectableView.invalidate();
        }
        TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
        if (textSelectionOverlay != null) {
            textSelectionOverlay.invalidate();
        }
    }

    private android.view.ActionMode.Callback createActionCallback() {
        final AnonymousClass4 anonymousClass4 = new android.view.ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                menu.add(0, 16908321, 0, 17039361);
                menu.add(0, 16908319, 1, 17039373);
                return true;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                SelectableView selectableView = textSelectionHelper.selectedView;
                if (selectableView != null) {
                    CharSequence text = textSelectionHelper.getText(selectableView, false);
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    if (textSelectionHelper2.multiselect || (textSelectionHelper2.selectionStart <= 0 && textSelectionHelper2.selectionEnd >= text.length() - 1)) {
                        menu.getItem(1).setVisible(false);
                    } else {
                        menu.getItem(1).setVisible(true);
                    }
                }
                return true;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (!TextSelectionHelper.this.isSelectionMode()) {
                    return true;
                }
                int itemId = menuItem.getItemId();
                if (itemId == 16908319) {
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    textSelectionHelper.selectionStart = 0;
                    textSelectionHelper.selectionEnd = textSelectionHelper.getText(textSelectionHelper.selectedView, false).length();
                    TextSelectionHelper.this.hideActions();
                    TextSelectionHelper.this.invalidate();
                    TextSelectionHelper.this.showActions();
                    return true;
                } else if (itemId != 16908321) {
                    TextSelectionHelper.this.clear();
                    return true;
                } else {
                    TextSelectionHelper.this.copyText();
                    return true;
                }
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                if (VERSION.SDK_INT < 23) {
                    TextSelectionHelper.this.clear();
                }
            }
        };
        return VERSION.SDK_INT >= 23 ? new Callback2() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return anonymousClass4.onCreateActionMode(actionMode, menu);
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return anonymousClass4.onPrepareActionMode(actionMode, menu);
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return anonymousClass4.onActionItemClicked(actionMode, menuItem);
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                anonymousClass4.onDestroyActionMode(actionMode);
            }

            public void onGetContentRect(ActionMode actionMode, View view, Rect rect) {
                if (TextSelectionHelper.this.isSelectionMode()) {
                    int i;
                    TextSelectionHelper textSelectionHelper;
                    int i2;
                    TextSelectionHelper.this.pickStartView();
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    int i3 = 1;
                    if (textSelectionHelper2.selectedView != null) {
                        i = -textSelectionHelper2.getLineHeight();
                        textSelectionHelper = TextSelectionHelper.this;
                        int[] offsetToCord = textSelectionHelper.offsetToCord(textSelectionHelper.selectionStart);
                        i2 = offsetToCord[0];
                        TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                        i2 += textSelectionHelper3.textX;
                        i = (((int) (((float) (offsetToCord[1] + textSelectionHelper3.textY)) + textSelectionHelper3.selectedView.getY())) + (i / 2)) - AndroidUtilities.dp(4.0f);
                        if (i >= 1) {
                            i3 = i;
                        }
                    } else {
                        i2 = 0;
                    }
                    i = TextSelectionHelper.this.parentView.getWidth();
                    TextSelectionHelper.this.pickEndView();
                    textSelectionHelper = TextSelectionHelper.this;
                    if (textSelectionHelper.selectedView != null) {
                        i = textSelectionHelper.offsetToCord(textSelectionHelper.selectionEnd)[0] + TextSelectionHelper.this.textX;
                    }
                    rect.set(Math.min(i2, i), i3, Math.max(i2, i), i3 + 1);
                }
            }
        } : anonymousClass4;
    }

    private void copyText() {
        if (isSelectionMode()) {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", getTextForCopy()));
            hideActions();
            clear(true);
            Callback callback = this.callback;
            if (callback != null) {
                callback.onTextCopied();
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public CharSequence getTextForCopy() {
        return getText(this.selectedView, false).subSequence(this.selectionStart, this.selectionEnd);
    }

    /* Access modifiers changed, original: protected */
    public int[] offsetToCord(int i) {
        fillLayoutForOffset(i, this.layoutBlock);
        StaticLayout staticLayout = this.layoutBlock.layout;
        if (staticLayout == null || i >= staticLayout.getText().length()) {
            return this.tmpCoord;
        }
        int lineForOffset = staticLayout.getLineForOffset(i);
        this.tmpCoord[0] = (int) (staticLayout.getPrimaryHorizontal(i) + this.layoutBlock.xOffset);
        this.tmpCoord[1] = staticLayout.getLineBottom(lineForOffset);
        int[] iArr = this.tmpCoord;
        iArr[1] = (int) (((float) iArr[1]) + this.layoutBlock.yOffset);
        return iArr;
    }

    /* Access modifiers changed, original: protected */
    public void drawSelection(Canvas canvas, StaticLayout staticLayout, int i, int i2) {
        StaticLayout staticLayout2 = staticLayout;
        int lineForOffset = staticLayout.getLineForOffset(i);
        int lineForOffset2 = staticLayout2.getLineForOffset(i2);
        if (lineForOffset == lineForOffset2) {
            drawLine(canvas, staticLayout, lineForOffset, i, i2);
            return;
        }
        int lineEnd = staticLayout2.getLineEnd(lineForOffset);
        if (staticLayout2.getParagraphDirection(lineForOffset) != -1 && lineEnd > 0) {
            int i3;
            float primaryHorizontal;
            lineEnd--;
            CharSequence text = staticLayout.getText();
            int primaryHorizontal2 = (int) staticLayout2.getPrimaryHorizontal(lineEnd);
            if (staticLayout2.isRtlCharAt(lineEnd)) {
                i3 = lineEnd;
                while (staticLayout2.isRtlCharAt(i3) && i3 != 0) {
                    i3--;
                }
                primaryHorizontal = staticLayout2.getLineForOffset(i3) == staticLayout2.getLineForOffset(lineEnd) ? staticLayout2.getPrimaryHorizontal(i3 + 1) : staticLayout2.getLineLeft(lineForOffset);
            } else {
                primaryHorizontal = staticLayout2.getLineRight(lineForOffset);
            }
            i3 = (int) primaryHorizontal;
            int min = Math.min(primaryHorizontal2, i3);
            primaryHorizontal2 = Math.max(primaryHorizontal2, i3);
            if (lineEnd > 0 && lineEnd < text.length() && !Character.isWhitespace(text.charAt(lineEnd - 1))) {
                canvas.drawRect((float) min, (float) staticLayout2.getLineTop(lineForOffset), (float) primaryHorizontal2, (float) staticLayout2.getLineBottom(lineForOffset), this.selectionPaint);
            }
        }
        int i4 = lineEnd;
        Canvas canvas2 = canvas;
        StaticLayout staticLayout3 = staticLayout;
        drawLine(canvas2, staticLayout3, lineForOffset, i, i4);
        drawLine(canvas2, staticLayout3, lineForOffset2, staticLayout2.getLineStart(lineForOffset2), i2);
        while (true) {
            lineForOffset++;
            if (lineForOffset < lineForOffset2) {
                lineEnd = (int) staticLayout2.getLineLeft(lineForOffset);
                int lineRight = (int) staticLayout2.getLineRight(lineForOffset);
                canvas.drawRect((float) Math.min(lineEnd, lineRight), (float) staticLayout2.getLineTop(lineForOffset), (float) Math.max(lineEnd, lineRight), (float) staticLayout2.getLineBottom(lineForOffset), this.selectionPaint);
            } else {
                return;
            }
        }
    }

    private void drawLine(Canvas canvas, StaticLayout staticLayout, int i, int i2, int i3) {
        staticLayout.getSelectionPath(i2, i3, this.path);
        if ((staticLayout.getParagraphDirection(i) == -1 ? 1 : null) != null || staticLayout.getSpacingAdd() <= 0.0f || i >= staticLayout.getLineCount() - 1) {
            canvas.drawPath(this.path, this.selectionPaint);
            return;
        }
        i2 = staticLayout.getLineBottom(i);
        i = staticLayout.getLineTop(i);
        float f = (float) (i2 - i);
        float spacingAdd = ((float) i2) - staticLayout.getSpacingAdd();
        float f2 = (float) i;
        spacingAdd -= f2;
        canvas.save();
        canvas.scale(1.0f, f / spacingAdd, 0.0f, f2);
        canvas.drawPath(this.path, this.selectionPaint);
        canvas.restore();
    }

    /* Access modifiers changed, original: protected */
    public void fillLayoutForOffset(int i, LayoutBlock layoutBlock) {
        fillLayoutForOffset(i, layoutBlock, false);
    }
}
