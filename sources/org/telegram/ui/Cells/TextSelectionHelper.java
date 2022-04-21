package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ActionMode;
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
import com.google.zxing.common.detector.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.FloatingActionMode;
import org.telegram.ui.ActionBar.FloatingToolbar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.TextSelectionHelper.SelectableView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public abstract class TextSelectionHelper<Cell extends SelectableView> {
    private static final int TRANSLATE = 3;
    /* access modifiers changed from: private */
    public ActionMode actionMode;
    protected boolean actionsIsShowing;
    /* access modifiers changed from: private */
    public Callback callback;
    protected int capturedX;
    protected int capturedY;
    protected float cornerRadius;
    private TextView deleteView;
    /* access modifiers changed from: private */
    public RectF endArea = new RectF();
    protected float enterProgress;
    protected float handleViewProgress;
    /* access modifiers changed from: private */
    public final Runnable hideActionsRunnable = new Runnable() {
        public void run() {
            if (Build.VERSION.SDK_INT >= 23 && TextSelectionHelper.this.actionMode != null && !TextSelectionHelper.this.actionsIsShowing) {
                TextSelectionHelper.this.actionMode.hide(Long.MAX_VALUE);
                AndroidUtilities.runOnUIThread(TextSelectionHelper.this.hideActionsRunnable, 1000);
            }
        }
    };
    /* access modifiers changed from: private */
    public Interpolator interpolator = new OvershootInterpolator();
    /* access modifiers changed from: private */
    public boolean isOneTouch;
    int keyboardSize;
    /* access modifiers changed from: private */
    public int lastX;
    /* access modifiers changed from: private */
    public int lastY;
    protected final LayoutBlock layoutBlock = new LayoutBlock();
    private int longpressDelay = ViewConfiguration.getLongPressTimeout();
    private Magnifier magnifier;
    private float magnifierDy;
    /* access modifiers changed from: private */
    public float magnifierY;
    /* access modifiers changed from: private */
    public float magnifierYanimated;
    protected Cell maybeSelectedView;
    protected int maybeTextX;
    protected int maybeTextY;
    protected boolean movingDirectionSettling;
    /* access modifiers changed from: private */
    public boolean movingHandle;
    protected boolean movingHandleStart;
    float movingOffsetX;
    float movingOffsetY;
    protected boolean multiselect;
    /* access modifiers changed from: private */
    public OnTranslateListener onTranslateListener = null;
    /* access modifiers changed from: private */
    public boolean parentIsScrolling;
    protected RecyclerListView parentRecyclerView;
    protected ViewGroup parentView;
    protected PathWithSavedBottom path = new PathWithSavedBottom();
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    /* access modifiers changed from: private */
    public boolean scrollDown;
    /* access modifiers changed from: private */
    public Runnable scrollRunnable = new Runnable() {
        public void run() {
            int dy;
            if (TextSelectionHelper.this.scrolling && TextSelectionHelper.this.parentRecyclerView != null) {
                if (TextSelectionHelper.this.multiselect && TextSelectionHelper.this.selectedView == null) {
                    dy = AndroidUtilities.dp(8.0f);
                } else if (TextSelectionHelper.this.selectedView != null) {
                    dy = TextSelectionHelper.this.getLineHeight() >> 1;
                } else {
                    return;
                }
                if (!TextSelectionHelper.this.multiselect) {
                    if (TextSelectionHelper.this.scrollDown) {
                        if (TextSelectionHelper.this.selectedView.getBottom() - dy < TextSelectionHelper.this.parentView.getMeasuredHeight() - TextSelectionHelper.this.getParentBottomPadding()) {
                            dy = (TextSelectionHelper.this.selectedView.getBottom() - TextSelectionHelper.this.parentView.getMeasuredHeight()) + TextSelectionHelper.this.getParentBottomPadding();
                        }
                    } else if (TextSelectionHelper.this.selectedView.getTop() + dy > TextSelectionHelper.this.getParentTopPadding()) {
                        dy = (-TextSelectionHelper.this.selectedView.getTop()) + TextSelectionHelper.this.getParentTopPadding();
                    }
                }
                TextSelectionHelper.this.parentRecyclerView.scrollBy(0, TextSelectionHelper.this.scrollDown ? dy : -dy);
                AndroidUtilities.runOnUIThread(this);
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean scrolling;
    protected int selectedCellId;
    protected Cell selectedView;
    protected int selectionEnd = -1;
    protected Paint selectionHandlePaint = new Paint();
    protected Paint selectionPaint = new Paint();
    protected Path selectionPath = new Path();
    protected PathCopyTo selectionPathMirror = new PathCopyTo(this.selectionPath);
    protected int selectionStart = -1;
    protected boolean showActionsAsPopupAlways = false;
    /* access modifiers changed from: private */
    public boolean snap;
    /* access modifiers changed from: private */
    public RectF startArea = new RectF();
    final Runnable startSelectionRunnable = new Runnable() {
        public void run() {
            int y;
            int x;
            if (TextSelectionHelper.this.maybeSelectedView != null && TextSelectionHelper.this.textSelectionOverlay != null) {
                Cell oldView = TextSelectionHelper.this.selectedView;
                Cell newView = TextSelectionHelper.this.maybeSelectedView;
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                CharSequence text = textSelectionHelper.getText(textSelectionHelper.maybeSelectedView, true);
                if (TextSelectionHelper.this.parentRecyclerView != null) {
                    TextSelectionHelper.this.parentRecyclerView.cancelClickRunnables(false);
                }
                int x2 = TextSelectionHelper.this.capturedX;
                int y2 = TextSelectionHelper.this.capturedY;
                if (!TextSelectionHelper.this.textArea.isEmpty()) {
                    if (x2 > TextSelectionHelper.this.textArea.right) {
                        x2 = TextSelectionHelper.this.textArea.right - 1;
                    }
                    if (x2 < TextSelectionHelper.this.textArea.left) {
                        x2 = TextSelectionHelper.this.textArea.left + 1;
                    }
                    if (y2 < TextSelectionHelper.this.textArea.top) {
                        y2 = TextSelectionHelper.this.textArea.top + 1;
                    }
                    if (y2 > TextSelectionHelper.this.textArea.bottom) {
                        y2 = TextSelectionHelper.this.textArea.bottom - 1;
                    }
                    x = x2;
                    y = y2;
                } else {
                    x = x2;
                    y = y2;
                }
                TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                int offset = textSelectionHelper2.getCharOffsetFromCord(x, y, textSelectionHelper2.maybeTextX, TextSelectionHelper.this.maybeTextY, newView, true);
                if (offset >= text.length()) {
                    TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                    textSelectionHelper3.fillLayoutForOffset(offset, textSelectionHelper3.layoutBlock, true);
                    if (TextSelectionHelper.this.layoutBlock.layout == null) {
                        TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                        textSelectionHelper4.selectionEnd = -1;
                        textSelectionHelper4.selectionStart = -1;
                        return;
                    }
                    int endLine = TextSelectionHelper.this.layoutBlock.layout.getLineCount() - 1;
                    int x3 = x - TextSelectionHelper.this.maybeTextX;
                    if (((float) x3) < TextSelectionHelper.this.layoutBlock.layout.getLineRight(endLine) + ((float) AndroidUtilities.dp(4.0f)) && ((float) x3) > TextSelectionHelper.this.layoutBlock.layout.getLineLeft(endLine)) {
                        offset = text.length() - 1;
                    }
                }
                if (offset >= 0 && offset < text.length() && text.charAt(offset) != 10) {
                    int maybeTextX = TextSelectionHelper.this.maybeTextX;
                    int maybeTextY = TextSelectionHelper.this.maybeTextY;
                    TextSelectionHelper.this.clear();
                    TextSelectionHelper.this.textSelectionOverlay.setVisibility(0);
                    TextSelectionHelper.this.onTextSelected(newView, oldView);
                    TextSelectionHelper.this.selectionStart = offset;
                    TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                    textSelectionHelper5.selectionEnd = textSelectionHelper5.selectionStart;
                    if (text instanceof Spanned) {
                        Emoji.EmojiSpan[] spans = (Emoji.EmojiSpan[]) ((Spanned) text).getSpans(0, text.length(), Emoji.EmojiSpan.class);
                        int length = spans.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            Emoji.EmojiSpan emojiSpan = spans[i];
                            int s = ((Spanned) text).getSpanStart(emojiSpan);
                            int e = ((Spanned) text).getSpanEnd(emojiSpan);
                            if (offset >= s && offset <= e) {
                                TextSelectionHelper.this.selectionStart = s;
                                TextSelectionHelper.this.selectionEnd = e;
                                break;
                            }
                            i++;
                        }
                    }
                    if (TextSelectionHelper.this.selectionStart == TextSelectionHelper.this.selectionEnd) {
                        while (TextSelectionHelper.this.selectionStart > 0 && TextSelectionHelper.isInterruptedCharacter(text.charAt(TextSelectionHelper.this.selectionStart - 1))) {
                            TextSelectionHelper.this.selectionStart--;
                        }
                        while (TextSelectionHelper.this.selectionEnd < text.length() && TextSelectionHelper.isInterruptedCharacter(text.charAt(TextSelectionHelper.this.selectionEnd))) {
                            TextSelectionHelper.this.selectionEnd++;
                        }
                    }
                    TextSelectionHelper.this.textX = maybeTextX;
                    TextSelectionHelper.this.textY = maybeTextY;
                    TextSelectionHelper.this.selectedView = newView;
                    TextSelectionHelper.this.textSelectionOverlay.performHapticFeedback(0);
                    TextSelectionHelper.this.showActions();
                    TextSelectionHelper.this.invalidate();
                    if (oldView != null) {
                        oldView.invalidate();
                    }
                    if (TextSelectionHelper.this.callback != null) {
                        TextSelectionHelper.this.callback.onStateChanged(true);
                    }
                    boolean unused = TextSelectionHelper.this.movingHandle = true;
                    TextSelectionHelper.this.movingDirectionSettling = true;
                    boolean unused2 = TextSelectionHelper.this.isOneTouch = true;
                    TextSelectionHelper.this.movingOffsetY = 0.0f;
                    TextSelectionHelper.this.movingOffsetX = 0.0f;
                    TextSelectionHelper.this.onOffsetChanged();
                }
                boolean unused3 = TextSelectionHelper.this.tryCapture = false;
            }
        }
    };
    protected Path tempPath = new Path();
    private final ScalablePath tempPath2 = new ScalablePath();
    protected final Rect textArea = new Rect();
    private final ActionMode.Callback textSelectActionCallback = createActionCallback();
    protected TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay;
    protected int textX;
    protected int textY;
    protected int[] tmpCoord = new int[2];
    /* access modifiers changed from: private */
    public int topOffset;
    /* access modifiers changed from: private */
    public int touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
    /* access modifiers changed from: private */
    public boolean tryCapture;

    public interface ArticleSelectableView extends SelectableView {
        void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList);
    }

    public static class IgnoreCopySpannable {
    }

    public interface OnTranslateListener {
        void run(CharSequence charSequence, String str, String str2, Runnable runnable);
    }

    public interface SelectableView {
        int getBottom();

        int getMeasuredWidth();

        int getTop();

        float getX();

        float getY();

        void invalidate();
    }

    /* access modifiers changed from: protected */
    public abstract void fillLayoutForOffset(int i, LayoutBlock layoutBlock2, boolean z);

    /* access modifiers changed from: protected */
    public abstract int getCharOffsetFromCord(int i, int i2, int i3, int i4, Cell cell, boolean z);

    /* access modifiers changed from: protected */
    public abstract int getLineHeight();

    /* access modifiers changed from: protected */
    public abstract CharSequence getText(Cell cell, boolean z);

    /* access modifiers changed from: protected */
    public abstract void onTextSelected(Cell cell, Cell cell2);

    public TextSelectionHelper() {
        Paint paint = this.selectionPaint;
        float dp = (float) AndroidUtilities.dp(6.0f);
        this.cornerRadius = dp;
        paint.setPathEffect(new CornerPathEffect(dp));
    }

    public void setOnTranslate(OnTranslateListener listener) {
        this.onTranslateListener = listener;
    }

    public void setParentView(ViewGroup view) {
        if (view instanceof RecyclerListView) {
            this.parentRecyclerView = (RecyclerListView) view;
        }
        this.parentView = view;
    }

    public void setMaybeTextCord(int x, int y) {
        this.maybeTextX = x;
        this.maybeTextY = y;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.capturedX = (int) event.getX();
                this.capturedY = (int) event.getY();
                this.tryCapture = false;
                this.textArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                if (this.textArea.contains(this.capturedX, this.capturedY)) {
                    this.textArea.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                    int x = this.capturedX;
                    int y = this.capturedY;
                    if (x > this.textArea.right) {
                        x = this.textArea.right - 1;
                    }
                    if (x < this.textArea.left) {
                        x = this.textArea.left + 1;
                    }
                    if (y < this.textArea.top) {
                        y = this.textArea.top + 1;
                    }
                    if (y > this.textArea.bottom) {
                        y = this.textArea.bottom - 1;
                    }
                    int offset = getCharOffsetFromCord(x, y, this.maybeTextX, this.maybeTextY, this.maybeSelectedView, true);
                    CharSequence text = getText(this.maybeSelectedView, true);
                    if (offset >= text.length()) {
                        fillLayoutForOffset(offset, this.layoutBlock, true);
                        if (this.layoutBlock.layout == null) {
                            this.tryCapture = false;
                            return false;
                        }
                        int endLine = this.layoutBlock.layout.getLineCount() - 1;
                        int x2 = x - this.maybeTextX;
                        if (((float) x2) < this.layoutBlock.layout.getLineRight(endLine) + ((float) AndroidUtilities.dp(4.0f)) && ((float) x2) > this.layoutBlock.layout.getLineLeft(endLine)) {
                            offset = text.length() - 1;
                        }
                    }
                    if (offset >= 0 && offset < text.length() && text.charAt(offset) != 10) {
                        AndroidUtilities.runOnUIThread(this.startSelectionRunnable, (long) this.longpressDelay);
                        this.tryCapture = true;
                    }
                }
                return this.tryCapture;
            case 1:
            case 3:
                AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
                this.tryCapture = false;
                return false;
            case 2:
                int y2 = (int) event.getY();
                int x3 = (int) event.getX();
                int i = this.capturedY;
                int i2 = (i - y2) * (i - y2);
                int i3 = this.capturedX;
                if (i2 + ((i3 - x3) * (i3 - x3)) > this.touchSlop) {
                    AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
                    this.tryCapture = false;
                }
                return this.tryCapture;
            default:
                return false;
        }
    }

    /* access modifiers changed from: private */
    public void hideMagnifier() {
        Magnifier magnifier2;
        if (Build.VERSION.SDK_INT >= 28 && (magnifier2 = this.magnifier) != null) {
            magnifier2.dismiss();
            this.magnifier = null;
        }
    }

    /* access modifiers changed from: private */
    public void showMagnifier(int x) {
        int endLine;
        int startLine;
        if (Build.VERSION.SDK_INT >= 28 && this.selectedView != null && !this.isOneTouch && this.movingHandle && this.textSelectionOverlay != null) {
            int offset = this.movingHandleStart ? this.selectionStart : this.selectionEnd;
            fillLayoutForOffset(offset, this.layoutBlock);
            StaticLayout layout = this.layoutBlock.layout;
            if (layout != null) {
                int line = layout.getLineForOffset(offset);
                int lineHeight = layout.getLineBottom(line) - layout.getLineTop(line);
                int newY = (int) (((float) ((((int) (((float) (layout.getLineTop(line) + this.textY)) + this.selectedView.getY())) - lineHeight) - AndroidUtilities.dp(8.0f))) + this.layoutBlock.yOffset);
                if (this.magnifierY != ((float) newY)) {
                    this.magnifierY = (float) newY;
                    this.magnifierDy = (((float) newY) - this.magnifierYanimated) / 200.0f;
                }
                if (this.magnifier == null) {
                    this.magnifier = new Magnifier(this.textSelectionOverlay);
                    this.magnifierYanimated = this.magnifierY;
                }
                float f = this.magnifierYanimated;
                float f2 = this.magnifierY;
                if (f != f2) {
                    this.magnifierYanimated = f + (this.magnifierDy * 16.0f);
                }
                float f3 = this.magnifierDy;
                if (f3 > 0.0f && this.magnifierYanimated > f2) {
                    this.magnifierYanimated = f2;
                } else if (f3 < 0.0f && this.magnifierYanimated < f2) {
                    this.magnifierYanimated = f2;
                }
                Cell cell = this.selectedView;
                if (cell instanceof ArticleViewer.BlockTableCell) {
                    startLine = (int) cell.getX();
                    endLine = ((int) this.selectedView.getX()) + this.selectedView.getMeasuredWidth();
                } else {
                    startLine = (int) (cell.getX() + ((float) this.textX) + layout.getLineLeft(line));
                    endLine = (int) (this.selectedView.getX() + ((float) this.textX) + layout.getLineRight(line));
                }
                if (x < startLine) {
                    x = startLine;
                } else if (x > endLine) {
                    x = endLine;
                }
                this.magnifier.show((float) x, this.magnifierYanimated + (((float) lineHeight) * 1.5f) + ((float) AndroidUtilities.dp(8.0f)));
                this.magnifier.update();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void showHandleViews() {
        if (this.handleViewProgress != 1.0f && this.textSelectionOverlay != null) {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            animator.addUpdateListener(new TextSelectionHelper$$ExternalSyntheticLambda0(this));
            animator.setDuration(250);
            animator.start();
        }
    }

    /* renamed from: lambda$showHandleViews$0$org-telegram-ui-Cells-TextSelectionHelper  reason: not valid java name */
    public /* synthetic */ void m1527xb705f8cf(ValueAnimator animation) {
        this.handleViewProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.textSelectionOverlay.invalidate();
    }

    public boolean isSelectionMode() {
        return this.selectionStart >= 0 && this.selectionEnd >= 0;
    }

    /* access modifiers changed from: private */
    public void showActions() {
        if (this.textSelectionOverlay != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!this.movingHandle && isSelectionMode() && canShowActions()) {
                    if (!this.actionsIsShowing) {
                        if (this.actionMode == null) {
                            FloatingActionMode floatingActionMode = new FloatingActionMode(this.textSelectionOverlay.getContext(), (ActionMode.Callback2) this.textSelectActionCallback, this.textSelectionOverlay, new FloatingToolbar(this.textSelectionOverlay.getContext(), this.textSelectionOverlay, 1, getResourcesProvider()));
                            this.actionMode = floatingActionMode;
                            this.textSelectActionCallback.onCreateActionMode(floatingActionMode, floatingActionMode.getMenu());
                        }
                        ActionMode.Callback callback2 = this.textSelectActionCallback;
                        ActionMode actionMode2 = this.actionMode;
                        callback2.onPrepareActionMode(actionMode2, actionMode2.getMenu());
                        this.actionMode.hide(1);
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.hideActionsRunnable);
                    this.actionsIsShowing = true;
                }
            } else if (!this.showActionsAsPopupAlways) {
                if (this.actionMode == null && isSelectionMode()) {
                    this.actionMode = this.textSelectionOverlay.startActionMode(this.textSelectActionCallback);
                }
            } else if (!this.movingHandle && isSelectionMode() && canShowActions()) {
                if (this.popupLayout == null) {
                    this.popupRect = new Rect();
                    ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.textSelectionOverlay.getContext());
                    this.popupLayout = actionBarPopupWindowLayout;
                    actionBarPopupWindowLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                    this.popupLayout.setBackgroundDrawable(this.textSelectionOverlay.getContext().getResources().getDrawable(NUM));
                    this.popupLayout.setAnimationEnabled(false);
                    this.popupLayout.setOnTouchListener(new TextSelectionHelper$$ExternalSyntheticLambda2(this));
                    this.popupLayout.setShownFromBotton(false);
                    TextView textView = new TextView(this.textSelectionOverlay.getContext());
                    this.deleteView = textView;
                    textView.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21"), 2));
                    this.deleteView.setGravity(16);
                    this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                    this.deleteView.setTextSize(1, 15.0f);
                    this.deleteView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    this.deleteView.setText(this.textSelectionOverlay.getContext().getString(17039361));
                    this.deleteView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
                    this.deleteView.setOnClickListener(new TextSelectionHelper$$ExternalSyntheticLambda1(this));
                    this.popupLayout.addView(this.deleteView, LayoutHelper.createFrame(-2, 48.0f));
                    ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                    this.popupWindow = actionBarPopupWindow;
                    actionBarPopupWindow.setAnimationEnabled(false);
                    this.popupWindow.setAnimationStyle(NUM);
                    this.popupWindow.setOutsideTouchable(true);
                    ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = this.popupLayout;
                    if (actionBarPopupWindowLayout2 != null) {
                        actionBarPopupWindowLayout2.setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
                    }
                }
                int y = 0;
                if (this.selectedView != null && (y = (((int) (((float) (offsetToCord(this.selectionStart)[1] + this.textY)) + this.selectedView.getY())) + ((-getLineHeight()) / 2)) - AndroidUtilities.dp(4.0f)) < 0) {
                    y = 0;
                }
                this.popupWindow.showAtLocation(this.textSelectionOverlay, 48, 0, y - AndroidUtilities.dp(48.0f));
                this.popupWindow.startAnimation();
            }
        }
    }

    /* renamed from: lambda$showActions$1$org-telegram-ui-Cells-TextSelectionHelper  reason: not valid java name */
    public /* synthetic */ boolean m1525lambda$showActions$1$orgtelegramuiCellsTextSelectionHelper(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(this.popupRect);
        return false;
    }

    /* renamed from: lambda$showActions$2$org-telegram-ui-Cells-TextSelectionHelper  reason: not valid java name */
    public /* synthetic */ void m1526lambda$showActions$2$orgtelegramuiCellsTextSelectionHelper(View v) {
        copyText();
    }

    /* access modifiers changed from: protected */
    public boolean canShowActions() {
        return this.selectedView != null;
    }

    /* access modifiers changed from: private */
    public void hideActions() {
        ActionMode actionMode2;
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.actionMode != null && this.actionsIsShowing) {
                this.actionsIsShowing = false;
                this.hideActionsRunnable.run();
            }
            this.actionsIsShowing = false;
        }
        if (!isSelectionMode() && (actionMode2 = this.actionMode) != null) {
            actionMode2.finish();
            this.actionMode = null;
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    public TextSelectionHelper<Cell>.TextSelectionOverlay getOverlayView(Context context) {
        if (this.textSelectionOverlay == null) {
            this.textSelectionOverlay = new TextSelectionOverlay(context);
        }
        return this.textSelectionOverlay;
    }

    public boolean isSelected(MessageObject messageObject) {
        if (messageObject != null && this.selectedCellId == messageObject.getId()) {
            return true;
        }
        return false;
    }

    public void checkSelectionCancel(MotionEvent e) {
        if (e.getAction() == 1 || e.getAction() == 3) {
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

    public void clear(boolean instant) {
        onExitSelectionMode(instant);
        this.selectionStart = -1;
        this.selectionEnd = -1;
        hideMagnifier();
        hideActions();
        invalidate();
        this.selectedView = null;
        this.selectedCellId = 0;
        AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
        this.tryCapture = false;
        TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay2 = this.textSelectionOverlay;
        if (textSelectionOverlay2 != null) {
            textSelectionOverlay2.setVisibility(8);
        }
        this.handleViewProgress = 0.0f;
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onStateChanged(false);
        }
        this.capturedX = -1;
        this.capturedY = -1;
        this.maybeTextX = -1;
        this.maybeTextY = -1;
        this.movingOffsetX = 0.0f;
        this.movingOffsetY = 0.0f;
        this.movingHandle = false;
    }

    /* access modifiers changed from: protected */
    public void onExitSelectionMode(boolean didAction) {
    }

    public void setCallback(Callback listener) {
        this.callback = listener;
    }

    public boolean isTryingSelect() {
        return this.tryCapture;
    }

    public void onParentScrolled() {
        TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay2;
        if (isSelectionMode() && (textSelectionOverlay2 = this.textSelectionOverlay) != null) {
            this.parentIsScrolling = true;
            textSelectionOverlay2.invalidate();
            hideActions();
        }
    }

    public void stopScrolling() {
        this.parentIsScrolling = false;
        showActions();
    }

    public static boolean isInterruptedCharacter(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }

    public void setTopOffset(int topOffset2) {
        this.topOffset = topOffset2;
    }

    public class TextSelectionOverlay extends View {
        Paint handleViewPaint = new Paint(1);
        Path path = new Path();
        long pressedTime = 0;
        float pressedX;
        float pressedY;

        public TextSelectionOverlay(Context context) {
            super(context);
            this.handleViewPaint.setStyle(Paint.Style.FILL);
        }

        public boolean checkOnTap(MotionEvent event) {
            if (!TextSelectionHelper.this.isSelectionMode() || TextSelectionHelper.this.movingHandle) {
                return false;
            }
            switch (event.getAction()) {
                case 0:
                    this.pressedX = event.getX();
                    this.pressedY = event.getY();
                    this.pressedTime = System.currentTimeMillis();
                    break;
                case 1:
                    if (System.currentTimeMillis() - this.pressedTime < 200 && MathUtils.distance((int) this.pressedX, (int) this.pressedY, (int) event.getX(), (int) event.getY()) < ((float) TextSelectionHelper.this.touchSlop)) {
                        TextSelectionHelper.this.hideActions();
                        TextSelectionHelper.this.clear();
                        return true;
                    }
            }
            return false;
        }

        /* JADX WARNING: Removed duplicated region for block: B:233:0x05ab  */
        /* JADX WARNING: Removed duplicated region for block: B:238:0x05b8  */
        /* JADX WARNING: Removed duplicated region for block: B:243:0x05d0  */
        /* JADX WARNING: Removed duplicated region for block: B:244:0x05d3  */
        /* JADX WARNING: Removed duplicated region for block: B:268:0x062b  */
        /* JADX WARNING: Removed duplicated region for block: B:269:0x0641  */
        /* JADX WARNING: Removed duplicated region for block: B:272:0x0649  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r33) {
            /*
                r32 = this;
                r0 = r32
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.isSelectionMode()
                r2 = 0
                if (r1 != 0) goto L_0x000c
                return r2
            L_0x000c:
                int r1 = r33.getPointerCount()
                r3 = 1
                if (r1 <= r3) goto L_0x001a
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.movingHandle
                return r1
            L_0x001a:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r1 = r1.lastX
                float r1 = (float) r1
                float r4 = r33.getX()
                float r1 = r1 - r4
                int r1 = (int) r1
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r4.lastY
                float r4 = (float) r4
                float r5 = r33.getY()
                float r4 = r4 - r5
                int r4 = (int) r4
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r6 = r33.getX()
                int r6 = (int) r6
                int unused = r5.lastX = r6
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r6 = r33.getY()
                int r6 = (int) r6
                int unused = r5.lastY = r6
                int r5 = r33.getAction()
                switch(r5) {
                    case 0: goto L_0x0741;
                    case 1: goto L_0x06fe;
                    case 2: goto L_0x0055;
                    case 3: goto L_0x06fe;
                    default: goto L_0x004f;
                }
            L_0x004f:
                r29 = r1
                r21 = r4
                goto L_0x083c
            L_0x0055:
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r5 = r5.movingHandle
                if (r5 == 0) goto L_0x06f8
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r5 = r5.movingHandleStart
                if (r5 == 0) goto L_0x0069
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r5.pickStartView()
                goto L_0x006e
            L_0x0069:
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r5.pickEndView()
            L_0x006e:
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r5 = r5.selectedView
                if (r5 != 0) goto L_0x007b
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r2 = r2.movingHandle
                return r2
            L_0x007b:
                float r5 = r33.getY()
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r6 = r6.movingOffsetY
                float r5 = r5 + r6
                int r5 = (int) r5
                float r6 = r33.getX()
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r7 = r7.movingOffsetX
                float r6 = r6 + r7
                int r6 = (int) r6
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r7 = r7.selectLayout(r6, r5)
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r8 = r8.selectedView
                if (r8 != 0) goto L_0x009c
                return r3
            L_0x009c:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.movingHandleStart
                if (r8 == 0) goto L_0x00ae
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r9 = r8.selectionStart
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r10 = r10.layoutBlock
                r8.fillLayoutForOffset(r9, r10)
                goto L_0x00b9
            L_0x00ae:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r9 = r8.selectionEnd
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r10 = r10.layoutBlock
                r8.fillLayoutForOffset(r9, r10)
            L_0x00b9:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r8 = r8.layoutBlock
                android.text.StaticLayout r15 = r8.layout
                if (r15 != 0) goto L_0x00c2
                return r3
            L_0x00c2:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r8 = r8.layoutBlock
                float r14 = r8.yOffset
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r13 = r8.selectedView
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r8 = r8.selectedView
                int r8 = r8.getTop()
                int r5 = r5 - r8
                float r8 = (float) r6
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r9 = r9.selectedView
                float r9 = r9.getX()
                float r8 = r8 - r9
                int r6 = (int) r8
                float r8 = r33.getY()
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r9 = r9.touchSlop
                float r9 = (float) r9
                float r8 = r8 - r9
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r9 = r9.parentView
                int r9 = r9.getMeasuredHeight()
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.getParentBottomPadding()
                int r9 = r9 - r10
                float r9 = (float) r9
                int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r8 <= 0) goto L_0x0121
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.multiselect
                if (r8 != 0) goto L_0x011f
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r8 = r8.selectedView
                int r8 = r8.getBottom()
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r9 = r9.parentView
                int r9 = r9.getMeasuredHeight()
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.getParentBottomPadding()
                int r9 = r9 - r10
                if (r8 <= r9) goto L_0x0121
            L_0x011f:
                r8 = 1
                goto L_0x0122
            L_0x0121:
                r8 = 0
            L_0x0122:
                r12 = r8
                float r8 = r33.getY()
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r9 = r9.parentView
                android.view.ViewParent r9 = r9.getParent()
                android.view.View r9 = (android.view.View) r9
                int r9 = r9.getTop()
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.getParentTopPadding()
                int r9 = r9 + r10
                float r9 = (float) r9
                int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r8 >= 0) goto L_0x0159
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.multiselect
                if (r8 != 0) goto L_0x0157
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r8 = r8.selectedView
                int r8 = r8.getTop()
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r9 = r9.getParentTopPadding()
                if (r8 >= r9) goto L_0x0159
            L_0x0157:
                r8 = 1
                goto L_0x015a
            L_0x0159:
                r8 = 0
            L_0x015a:
                r23 = r8
                if (r12 != 0) goto L_0x0178
                if (r23 == 0) goto L_0x0161
                goto L_0x0178
            L_0x0161:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.scrolling
                if (r8 == 0) goto L_0x01be
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r8.scrolling = r2
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                java.lang.Runnable r8 = r8.scrollRunnable
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r8)
                goto L_0x01be
            L_0x0178:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.scrolling
                if (r8 != 0) goto L_0x018e
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r8.scrolling = r3
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                java.lang.Runnable r8 = r8.scrollRunnable
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
            L_0x018e:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r8.scrollDown = r12
                if (r12 == 0) goto L_0x01ae
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r8 = r8.parentView
                int r8 = r8.getMeasuredHeight()
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r9 = r9.selectedView
                int r9 = r9.getTop()
                int r8 = r8 - r9
                float r8 = (float) r8
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r9 = r9.movingOffsetY
                float r8 = r8 + r9
                int r5 = (int) r8
                goto L_0x01be
            L_0x01ae:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r8 = r8.selectedView
                int r8 = r8.getTop()
                int r8 = -r8
                float r8 = (float) r8
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r9 = r9.movingOffsetY
                float r8 = r8 + r9
                int r5 = (int) r8
            L_0x01be:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r9 = r8.textX
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.textY
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r11 = r11.selectedView
                r22 = 0
                r16 = r8
                r17 = r6
                r18 = r5
                r19 = r9
                r20 = r10
                r21 = r11
                int r11 = r16.getCharOffsetFromCord(r17, r18, r19, r20, r21, r22)
                if (r11 < 0) goto L_0x06df
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.movingDirectionSettling
                if (r8 == 0) goto L_0x0210
                if (r7 == 0) goto L_0x01e7
                return r3
            L_0x01e7:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionStart
                if (r11 >= r8) goto L_0x01fb
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.movingDirectionSettling = r2
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.movingHandleStart = r3
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.hideActions()
                goto L_0x0210
            L_0x01fb:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionEnd
                if (r11 <= r8) goto L_0x020f
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.movingDirectionSettling = r2
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.movingHandleStart = r2
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.hideActions()
                goto L_0x0210
            L_0x020f:
                return r3
            L_0x0210:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.movingHandleStart
                if (r8 == 0) goto L_0x04af
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionStart
                if (r8 == r11) goto L_0x049c
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.canSelect(r11)
                if (r8 == 0) goto L_0x049c
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r10 = r8.selectedView
                java.lang.CharSequence r10 = r8.getText(r10, r2)
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r2 = r8.layoutBlock
                r8.fillLayoutForOffset(r11, r2)
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r2 = r2.layoutBlock
                android.text.StaticLayout r2 = r2.layout
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r3 = r8.selectionStart
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r9 = r9.layoutBlock
                r8.fillLayoutForOffset(r3, r9)
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r3 = r3.layoutBlock
                android.text.StaticLayout r3 = r3.layout
                if (r2 == 0) goto L_0x0488
                if (r3 != 0) goto L_0x0262
                r24 = r2
                r20 = r3
                r21 = r4
                r22 = r5
                r25 = r6
                r3 = r10
                r31 = r11
                r17 = r12
                r2 = r13
                r26 = r14
                goto L_0x049a
            L_0x0262:
                r8 = r11
                r9 = r8
            L_0x0264:
                int r8 = r9 + -1
                if (r8 < 0) goto L_0x0277
                int r8 = r9 + -1
                char r8 = r10.charAt(r8)
                boolean r8 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r8)
                if (r8 == 0) goto L_0x0277
                int r9 = r9 + -1
                goto L_0x0264
            L_0x0277:
                int r8 = r3.getLineForOffset(r9)
                r21 = r4
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r4.selectionStart
                int r4 = r3.getLineForOffset(r4)
                r22 = r5
                int r5 = r3.getLineForOffset(r11)
                if (r7 != 0) goto L_0x0441
                if (r2 != r3) goto L_0x0441
                r24 = r2
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r2 = r2.selectionStart
                int r2 = r3.getLineForOffset(r2)
                if (r5 == r2) goto L_0x02af
                if (r5 != r8) goto L_0x02af
                r20 = r3
                r27 = r4
                r28 = r5
                r25 = r6
                r29 = r8
                r30 = r10
                r3 = 27
                r4 = 9
                goto L_0x0453
            L_0x02af:
                int r2 = r3.getLineForOffset(r11)
                int r2 = r3.getParagraphDirection(r2)
                r25 = r6
                r6 = -1
                if (r6 == r2) goto L_0x03fa
                boolean r2 = r3.isRtlCharAt(r11)
                if (r2 != 0) goto L_0x03fa
                if (r8 != r4) goto L_0x03fa
                if (r5 == r8) goto L_0x02d2
                r20 = r3
                r27 = r4
                r28 = r5
                r29 = r8
                r30 = r10
                goto L_0x0404
            L_0x02d2:
                r2 = r11
            L_0x02d3:
                int r6 = r2 + 1
                r20 = r3
                int r3 = r10.length()
                if (r6 >= r3) goto L_0x02ee
                int r3 = r2 + 1
                char r3 = r10.charAt(r3)
                boolean r3 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r3)
                if (r3 == 0) goto L_0x02ee
                int r2 = r2 + 1
                r3 = r20
                goto L_0x02d3
            L_0x02ee:
                int r3 = r11 - r9
                int r3 = java.lang.Math.abs(r3)
                int r6 = r11 - r2
                int r6 = java.lang.Math.abs(r6)
                r26 = r2
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r2 = r2.snap
                if (r2 == 0) goto L_0x0311
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                r27 = r4
                if (r1 < 0) goto L_0x030c
                r4 = 1
                goto L_0x030d
            L_0x030c:
                r4 = 0
            L_0x030d:
                boolean unused = r2.snap = r4
                goto L_0x0313
            L_0x0311:
                r27 = r4
            L_0x0313:
                int r2 = r11 + -1
                if (r2 <= 0) goto L_0x0325
                int r2 = r11 + -1
                char r2 = r10.charAt(r2)
                boolean r2 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r2)
                if (r2 == 0) goto L_0x0325
                r2 = 1
                goto L_0x0326
            L_0x0325:
                r2 = 0
            L_0x0326:
                int r4 = r10.length()
                if (r11 < r4) goto L_0x0333
                int r11 = r10.length()
                r4 = 10
                goto L_0x0337
            L_0x0333:
                char r4 = r10.charAt(r11)
            L_0x0337:
                r28 = r5
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r5 = r5.selectionStart
                r29 = r8
                int r8 = r10.length()
                if (r5 < r8) goto L_0x0350
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r10.length()
                r5.selectionStart = r8
                r5 = 10
                goto L_0x0358
            L_0x0350:
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r5 = r5.selectionStart
                char r5 = r10.charAt(r5)
            L_0x0358:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionStart
                r30 = r10
                r10 = 10
                if (r11 >= r8) goto L_0x0364
                if (r3 < r6) goto L_0x0386
            L_0x0364:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionStart
                if (r11 <= r8) goto L_0x036c
                if (r1 < 0) goto L_0x0386
            L_0x036c:
                boolean r8 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4)
                if (r8 == 0) goto L_0x0386
                boolean r8 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r5)
                if (r8 == 0) goto L_0x0380
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.snap
                if (r8 == 0) goto L_0x0386
            L_0x0380:
                if (r11 == 0) goto L_0x0386
                if (r2 == 0) goto L_0x0386
                if (r5 != r10) goto L_0x03f3
            L_0x0386:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.snap
                if (r8 == 0) goto L_0x0392
                r8 = 1
                if (r11 != r8) goto L_0x0392
                return r8
            L_0x0392:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionStart
                if (r11 >= r8) goto L_0x03b9
                boolean r8 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4)
                if (r8 == 0) goto L_0x03b9
                boolean r8 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r5)
                if (r8 == 0) goto L_0x03ac
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r8 = r8.snap
                if (r8 == 0) goto L_0x03b9
            L_0x03ac:
                if (r5 == r10) goto L_0x03b9
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.selectionStart = r9
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r10 = 1
                boolean unused = r8.snap = r10
                goto L_0x03bd
            L_0x03b9:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.selectionStart = r11
            L_0x03bd:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionStart
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.selectionEnd
                if (r8 <= r10) goto L_0x03dd
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionEnd
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                r19 = r2
                int r2 = r10.selectionStart
                r10.selectionEnd = r2
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2.selectionStart = r8
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                r10 = 0
                r2.movingHandleStart = r10
                goto L_0x03df
            L_0x03dd:
                r19 = r2
            L_0x03df:
                int r2 = android.os.Build.VERSION.SDK_INT
                r8 = 27
                if (r2 < r8) goto L_0x03ee
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r2 = r2.textSelectionOverlay
                r8 = 9
                r2.performHapticFeedback(r8)
            L_0x03ee:
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2.invalidate()
            L_0x03f3:
                r17 = r12
                r2 = r13
                r26 = r14
                goto L_0x0484
            L_0x03fa:
                r20 = r3
                r27 = r4
                r28 = r5
                r29 = r8
                r30 = r10
            L_0x0404:
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2.selectionStart = r11
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r2 = r2.selectionStart
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r3 = r3.selectionEnd
                if (r2 <= r3) goto L_0x0425
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r2 = r2.selectionEnd
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r3.selectionStart
                r3.selectionEnd = r4
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r3.selectionStart = r2
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r4 = 0
                r3.movingHandleStart = r4
            L_0x0425:
                int r2 = android.os.Build.VERSION.SDK_INT
                r3 = 27
                if (r2 < r3) goto L_0x0434
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r2 = r2.textSelectionOverlay
                r4 = 9
                r2.performHapticFeedback(r4)
            L_0x0434:
                org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2.invalidate()
                r31 = r11
                r17 = r12
                r2 = r13
                r26 = r14
                goto L_0x0482
            L_0x0441:
                r24 = r2
                r20 = r3
                r27 = r4
                r28 = r5
                r25 = r6
                r29 = r8
                r30 = r10
                r3 = 27
                r4 = 9
            L_0x0453:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r2 = r8.layoutBlock
                float r2 = r2.yOffset
                r5 = r29
                r6 = r9
                r9 = r11
                r3 = r30
                r4 = 27
                r10 = r6
                r31 = r11
                r11 = r7
                r17 = r12
                r12 = r2
                r2 = r13
                r13 = r14
                r26 = r14
                r14 = r2
                r8.jumpToLine(r9, r10, r11, r12, r13, r14)
                int r8 = android.os.Build.VERSION.SDK_INT
                if (r8 < r4) goto L_0x047d
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r4 = r4.textSelectionOverlay
                r8 = 9
                r4.performHapticFeedback(r8)
            L_0x047d:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                r4.invalidate()
            L_0x0482:
                r11 = r31
            L_0x0484:
                r29 = r1
                goto L_0x06d9
            L_0x0488:
                r24 = r2
                r20 = r3
                r21 = r4
                r22 = r5
                r25 = r6
                r3 = r10
                r31 = r11
                r17 = r12
                r2 = r13
                r26 = r14
            L_0x049a:
                r4 = 1
                return r4
            L_0x049c:
                r21 = r4
                r22 = r5
                r25 = r6
                r31 = r11
                r17 = r12
                r2 = r13
                r26 = r14
                r29 = r1
                r5 = r31
                goto L_0x06d8
            L_0x04af:
                r21 = r4
                r22 = r5
                r25 = r6
                r31 = r11
                r17 = r12
                r2 = r13
                r26 = r14
                r4 = 27
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r3 = r3.selectionEnd
                r5 = r31
                if (r5 == r3) goto L_0x06d6
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r3 = r3.canSelect(r5)
                if (r3 == 0) goto L_0x06d6
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r6 = r3.selectedView
                r8 = 0
                java.lang.CharSequence r3 = r3.getText(r6, r8)
                r6 = r5
            L_0x04d8:
                int r8 = r3.length()
                if (r6 >= r8) goto L_0x04eb
                char r8 = r3.charAt(r6)
                boolean r8 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r8)
                if (r8 == 0) goto L_0x04eb
                int r6 = r6 + 1
                goto L_0x04d8
            L_0x04eb:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r9 = r8.layoutBlock
                r8.fillLayoutForOffset(r5, r9)
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r8 = r8.layoutBlock
                android.text.StaticLayout r14 = r8.layout
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r9 = r8.selectionEnd
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r10 = r10.layoutBlock
                r8.fillLayoutForOffset(r9, r10)
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r8 = r8.layoutBlock
                android.text.StaticLayout r13 = r8.layout
                if (r14 == 0) goto L_0x06cd
                if (r13 != 0) goto L_0x0516
                r29 = r1
                r30 = r3
                r1 = r13
                r20 = r14
                goto L_0x06d4
            L_0x0516:
                int r8 = r3.length()
                if (r5 <= r8) goto L_0x0521
                int r11 = r3.length()
                r5 = r11
            L_0x0521:
                int r12 = r13.getLineForOffset(r6)
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionEnd
                int r11 = r13.getLineForOffset(r8)
                int r10 = r13.getLineForOffset(r5)
                if (r7 != 0) goto L_0x0697
                if (r14 != r13) goto L_0x0697
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.selectionEnd
                int r8 = r13.getLineForOffset(r8)
                if (r10 == r8) goto L_0x054b
                if (r10 != r12) goto L_0x054b
                r29 = r1
                r30 = r3
                r27 = r10
                r28 = r11
                goto L_0x069f
            L_0x054b:
                int r8 = r13.getLineForOffset(r5)
                int r8 = r13.getParagraphDirection(r8)
                r9 = -1
                if (r9 == r8) goto L_0x0659
                boolean r8 = r13.isRtlCharAt(r5)
                if (r8 != 0) goto L_0x0659
                if (r11 != r12) goto L_0x0659
                if (r10 == r12) goto L_0x056a
                r29 = r1
                r30 = r3
                r27 = r10
                r28 = r11
                goto L_0x0661
            L_0x056a:
                r8 = r5
            L_0x056b:
                int r9 = r8 + -1
                if (r9 < 0) goto L_0x057e
                int r9 = r8 + -1
                char r9 = r3.charAt(r9)
                boolean r9 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r9)
                if (r9 == 0) goto L_0x057e
                int r8 = r8 + -1
                goto L_0x056b
            L_0x057e:
                int r9 = r5 - r6
                int r9 = java.lang.Math.abs(r9)
                int r20 = r5 - r8
                int r4 = java.lang.Math.abs(r20)
                int r20 = r5 + -1
                if (r20 <= 0) goto L_0x059e
                r20 = r8
                int r8 = r5 + -1
                char r8 = r3.charAt(r8)
                boolean r8 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r8)
                if (r8 == 0) goto L_0x05a0
                r8 = 1
                goto L_0x05a1
            L_0x059e:
                r20 = r8
            L_0x05a0:
                r8 = 0
            L_0x05a1:
                r27 = r10
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r10 = r10.snap
                if (r10 == 0) goto L_0x05b8
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                r28 = r11
                if (r1 > 0) goto L_0x05b3
                r11 = 1
                goto L_0x05b4
            L_0x05b3:
                r11 = 0
            L_0x05b4:
                boolean unused = r10.snap = r11
                goto L_0x05ba
            L_0x05b8:
                r28 = r11
            L_0x05ba:
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.selectionEnd
                if (r10 <= 0) goto L_0x05d3
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.selectionEnd
                r11 = 1
                int r10 = r10 - r11
                char r10 = r3.charAt(r10)
                boolean r10 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r10)
                if (r10 == 0) goto L_0x05d3
                r18 = 1
                goto L_0x05d5
            L_0x05d3:
                r18 = 0
            L_0x05d5:
                r10 = r18
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r11 = r11.selectionEnd
                if (r5 <= r11) goto L_0x05df
                if (r9 <= r4) goto L_0x05f8
            L_0x05df:
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r11 = r11.selectionEnd
                if (r5 >= r11) goto L_0x05e7
                if (r1 > 0) goto L_0x05f8
            L_0x05e7:
                if (r8 == 0) goto L_0x05f8
                if (r10 == 0) goto L_0x05f4
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r11 = r11.snap
                if (r11 != 0) goto L_0x05f4
                goto L_0x05f8
            L_0x05f4:
                r29 = r1
                goto L_0x06d8
            L_0x05f8:
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r11 = r11.selectionEnd
                if (r5 <= r11) goto L_0x061b
                if (r8 == 0) goto L_0x061b
                if (r10 == 0) goto L_0x060e
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r11 = r11.snap
                if (r11 == 0) goto L_0x060b
                goto L_0x060e
            L_0x060b:
                r29 = r1
                goto L_0x061d
            L_0x060e:
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                r11.selectionEnd = r6
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                r29 = r1
                r1 = 1
                boolean unused = r11.snap = r1
                goto L_0x0621
            L_0x061b:
                r29 = r1
            L_0x061d:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionEnd = r5
            L_0x0621:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r1 = r1.selectionStart
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r11 = r11.selectionEnd
                if (r1 <= r11) goto L_0x0641
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r1 = r1.selectionEnd
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                r30 = r3
                int r3 = r11.selectionStart
                r11.selectionEnd = r3
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r3.selectionStart = r1
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r11 = 1
                r3.movingHandleStart = r11
                goto L_0x0643
            L_0x0641:
                r30 = r3
            L_0x0643:
                int r1 = android.os.Build.VERSION.SDK_INT
                r3 = 27
                if (r1 < r3) goto L_0x0652
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r3 = 9
                r1.performHapticFeedback(r3)
            L_0x0652:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x06d8
            L_0x0659:
                r29 = r1
                r30 = r3
                r27 = r10
                r28 = r11
            L_0x0661:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionEnd = r5
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r1 = r1.selectionStart
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r3 = r3.selectionEnd
                if (r1 <= r3) goto L_0x0682
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r1 = r1.selectionEnd
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r3.selectionStart
                r3.selectionEnd = r4
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r3.selectionStart = r1
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r4 = 1
                r3.movingHandleStart = r4
            L_0x0682:
                int r1 = android.os.Build.VERSION.SDK_INT
                r3 = 27
                if (r1 < r3) goto L_0x0691
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r3 = 9
                r1.performHapticFeedback(r3)
            L_0x0691:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x06d8
            L_0x0697:
                r29 = r1
                r30 = r3
                r27 = r10
                r28 = r11
            L_0x069f:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r1 = r8.layoutBlock
                float r1 = r1.yOffset
                r9 = r5
                r3 = r27
                r10 = r6
                r4 = r28
                r11 = r7
                r18 = r12
                r12 = r1
                r1 = r13
                r13 = r26
                r20 = r14
                r14 = r2
                r8.jumpToLine(r9, r10, r11, r12, r13, r14)
                int r8 = android.os.Build.VERSION.SDK_INT
                r9 = 27
                if (r8 < r9) goto L_0x06c7
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r8 = r8.textSelectionOverlay
                r9 = 9
                r8.performHapticFeedback(r9)
            L_0x06c7:
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                r8.invalidate()
                goto L_0x06d8
            L_0x06cd:
                r29 = r1
                r30 = r3
                r1 = r13
                r20 = r14
            L_0x06d4:
                r3 = 1
                return r3
            L_0x06d6:
                r29 = r1
            L_0x06d8:
                r11 = r5
            L_0x06d9:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.onOffsetChanged()
                goto L_0x06ed
            L_0x06df:
                r29 = r1
                r21 = r4
                r22 = r5
                r25 = r6
                r5 = r11
                r17 = r12
                r2 = r13
                r26 = r14
            L_0x06ed:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r3 = r1.lastX
                r1.showMagnifier(r3)
                goto L_0x083c
            L_0x06f8:
                r29 = r1
                r21 = r4
                goto L_0x083c
            L_0x06fe:
                r29 = r1
                r21 = r4
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.hideMagnifier()
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2 = 0
                boolean unused = r1.movingHandle = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.movingDirectionSettling = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r1.isOneTouch = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.isSelectionMode()
                if (r1 == 0) goto L_0x0728
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.showActions()
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.showHandleViews()
            L_0x0728:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.scrolling
                if (r1 == 0) goto L_0x083c
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2 = 0
                boolean unused = r1.scrolling = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                java.lang.Runnable r1 = r1.scrollRunnable
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
                goto L_0x083c
            L_0x0741:
                r29 = r1
                r21 = r4
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.movingHandle
                if (r1 == 0) goto L_0x074f
                r1 = 1
                return r1
            L_0x074f:
                float r1 = r33.getX()
                int r1 = (int) r1
                float r2 = r33.getY()
                int r2 = (int) r2
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.graphics.RectF r3 = r3.startArea
                float r4 = (float) r1
                float r5 = (float) r2
                boolean r3 = r3.contains(r4, r5)
                if (r3 == 0) goto L_0x07c2
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r3.pickStartView()
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r3 = r3.selectedView
                if (r3 != 0) goto L_0x0774
                r3 = 0
                return r3
            L_0x0774:
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r4 = 1
                boolean unused = r3.movingHandle = r4
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r3.movingHandleStart = r4
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r3.selectionStart
                int[] r3 = r3.offsetToCord(r4)
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r4.getLineHeight()
                int r4 = r4 / 2
                float r4 = (float) r4
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6 = 0
                r6 = r3[r6]
                int r7 = r5.textX
                int r6 = r6 + r7
                float r6 = (float) r6
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r7 = r7.selectedView
                float r7 = r7.getX()
                float r6 = r6 + r7
                float r7 = (float) r1
                float r6 = r6 - r7
                r5.movingOffsetX = r6
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6 = 1
                r7 = r3[r6]
                int r6 = r5.textY
                int r7 = r7 + r6
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r6 = r6.selectedView
                int r6 = r6.getTop()
                int r7 = r7 + r6
                int r7 = r7 - r2
                float r6 = (float) r7
                float r6 = r6 - r4
                r5.movingOffsetY = r6
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r5.hideActions()
                r5 = 1
                return r5
            L_0x07c2:
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.graphics.RectF r3 = r3.endArea
                float r4 = (float) r1
                float r5 = (float) r2
                boolean r3 = r3.contains(r4, r5)
                if (r3 == 0) goto L_0x0835
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r3.pickEndView()
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r3 = r3.selectedView
                if (r3 != 0) goto L_0x07dd
                r3 = 0
                return r3
            L_0x07dd:
                r3 = 0
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                r5 = 1
                boolean unused = r4.movingHandle = r5
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                r4.movingHandleStart = r3
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r3.selectionEnd
                int[] r3 = r3.offsetToCord(r4)
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r4.getLineHeight()
                int r4 = r4 / 2
                float r4 = (float) r4
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6 = 0
                r6 = r3[r6]
                int r7 = r5.textX
                int r6 = r6 + r7
                float r6 = (float) r6
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r7 = r7.selectedView
                float r7 = r7.getX()
                float r6 = r6 + r7
                float r7 = (float) r1
                float r6 = r6 - r7
                r5.movingOffsetX = r6
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6 = 1
                r7 = r3[r6]
                int r6 = r5.textY
                int r7 = r7 + r6
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r6 = r6.selectedView
                int r6 = r6.getTop()
                int r7 = r7 + r6
                int r7 = r7 - r2
                float r6 = (float) r7
                float r6 = r6 - r4
                r5.movingOffsetY = r6
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r6 = r5.lastX
                r5.showMagnifier(r6)
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                r5.hideActions()
                r5 = 1
                return r5
            L_0x0835:
                org.telegram.ui.Cells.TextSelectionHelper r3 = org.telegram.ui.Cells.TextSelectionHelper.this
                r4 = 0
                boolean unused = r3.movingHandle = r4
            L_0x083c:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.movingHandle
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.TextSelectionOverlay.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int count;
            Canvas canvas2 = canvas;
            if (TextSelectionHelper.this.isSelectionMode()) {
                int handleViewSize = AndroidUtilities.dp(22.0f);
                int count2 = 0;
                int top = TextSelectionHelper.this.topOffset;
                TextSelectionHelper.this.pickEndView();
                if (TextSelectionHelper.this.selectedView != null) {
                    canvas.save();
                    float yOffset = TextSelectionHelper.this.selectedView.getY() + ((float) TextSelectionHelper.this.textY);
                    float xOffset = TextSelectionHelper.this.selectedView.getX() + ((float) TextSelectionHelper.this.textX);
                    canvas2.translate(xOffset, yOffset);
                    this.handleViewPaint.setColor(TextSelectionHelper.this.getThemedColor("chat_TextSelectionCursor"));
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    int len = textSelectionHelper.getText(textSelectionHelper.selectedView, false).length();
                    if (TextSelectionHelper.this.selectionEnd < 0 || TextSelectionHelper.this.selectionEnd > len) {
                        count = 0;
                        int i = len;
                    } else {
                        TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                        textSelectionHelper2.fillLayoutForOffset(textSelectionHelper2.selectionEnd, TextSelectionHelper.this.layoutBlock);
                        StaticLayout layout = TextSelectionHelper.this.layoutBlock.layout;
                        if (layout != null) {
                            int end = TextSelectionHelper.this.selectionEnd;
                            int textLen = layout.getText().length();
                            if (end > textLen) {
                                end = textLen;
                            }
                            int line = layout.getLineForOffset(end);
                            float x = layout.getPrimaryHorizontal(end);
                            int y = (int) (((float) layout.getLineBottom(line)) + TextSelectionHelper.this.layoutBlock.yOffset);
                            float x2 = x + TextSelectionHelper.this.layoutBlock.xOffset;
                            if (((float) y) + yOffset <= ((float) (TextSelectionHelper.this.keyboardSize + top)) || ((float) y) + yOffset >= ((float) TextSelectionHelper.this.parentView.getMeasuredHeight())) {
                                count = 0;
                                int i2 = len;
                                StaticLayout staticLayout = layout;
                                int i3 = end;
                                int i4 = textLen;
                                TextSelectionHelper.this.endArea.setEmpty();
                            } else if (!layout.isRtlCharAt(TextSelectionHelper.this.selectionEnd)) {
                                canvas.save();
                                canvas2.translate(x2, (float) y);
                                float v = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                int i5 = len;
                                canvas2.scale(v, v, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f);
                                this.path.reset();
                                float f = v;
                                StaticLayout staticLayout2 = layout;
                                int i6 = end;
                                this.path.addCircle(((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                int i7 = textLen;
                                TextSelectionHelper.this.endArea.set(xOffset + x2, (((float) y) + yOffset) - ((float) handleViewSize), xOffset + x2 + ((float) handleViewSize), ((float) y) + yOffset + ((float) handleViewSize));
                                TextSelectionHelper.this.endArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                                count2 = 0 + 1;
                                canvas.restore();
                            } else {
                                StaticLayout staticLayout3 = layout;
                                int i8 = end;
                                int i9 = textLen;
                                canvas.save();
                                canvas2.translate(x2 - ((float) handleViewSize), (float) y);
                                float v2 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                canvas2.scale(v2, v2, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f);
                                this.path.reset();
                                this.path.addCircle(((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                this.path.addRect(((float) handleViewSize) / 2.0f, 0.0f, (float) handleViewSize, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                count = 0;
                                TextSelectionHelper.this.endArea.set((xOffset + x2) - ((float) handleViewSize), (((float) y) + yOffset) - ((float) handleViewSize), xOffset + x2, ((float) y) + yOffset + ((float) handleViewSize));
                                TextSelectionHelper.this.endArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                            }
                        } else {
                            count = 0;
                            int i10 = len;
                            StaticLayout staticLayout4 = layout;
                        }
                    }
                    count2 = count;
                    canvas.restore();
                }
                TextSelectionHelper.this.pickStartView();
                if (TextSelectionHelper.this.selectedView != null) {
                    canvas.save();
                    float yOffset2 = TextSelectionHelper.this.selectedView.getY() + ((float) TextSelectionHelper.this.textY);
                    float xOffset2 = TextSelectionHelper.this.selectedView.getX() + ((float) TextSelectionHelper.this.textX);
                    canvas2.translate(xOffset2, yOffset2);
                    TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                    int len2 = textSelectionHelper3.getText(textSelectionHelper3.selectedView, false).length();
                    if (TextSelectionHelper.this.selectionStart < 0 || TextSelectionHelper.this.selectionStart > len2) {
                        int i11 = len2;
                    } else {
                        TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                        textSelectionHelper4.fillLayoutForOffset(textSelectionHelper4.selectionStart, TextSelectionHelper.this.layoutBlock);
                        StaticLayout layout2 = TextSelectionHelper.this.layoutBlock.layout;
                        if (layout2 != null) {
                            int line2 = layout2.getLineForOffset(TextSelectionHelper.this.selectionStart);
                            float x3 = layout2.getPrimaryHorizontal(TextSelectionHelper.this.selectionStart);
                            int y2 = (int) (((float) layout2.getLineBottom(line2)) + TextSelectionHelper.this.layoutBlock.yOffset);
                            float x4 = x3 + TextSelectionHelper.this.layoutBlock.xOffset;
                            if (((float) y2) + yOffset2 <= ((float) (TextSelectionHelper.this.keyboardSize + top)) || ((float) y2) + yOffset2 >= ((float) TextSelectionHelper.this.parentView.getMeasuredHeight())) {
                                int i12 = len2;
                                StaticLayout staticLayout5 = layout2;
                                if (((float) y2) + yOffset2 > 0.0f && (((float) y2) + yOffset2) - ((float) TextSelectionHelper.this.getLineHeight()) < ((float) TextSelectionHelper.this.parentView.getMeasuredHeight())) {
                                    count2++;
                                }
                                TextSelectionHelper.this.startArea.setEmpty();
                            } else if (!layout2.isRtlCharAt(TextSelectionHelper.this.selectionStart)) {
                                canvas.save();
                                canvas2.translate(x4 - ((float) handleViewSize), (float) y2);
                                float v3 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                canvas2.scale(v3, v3, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f);
                                this.path.reset();
                                int i13 = top;
                                int i14 = len2;
                                this.path.addCircle(((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                this.path.addRect(((float) handleViewSize) / 2.0f, 0.0f, (float) handleViewSize, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                StaticLayout staticLayout6 = layout2;
                                TextSelectionHelper.this.startArea.set((xOffset2 + x4) - ((float) handleViewSize), (((float) y2) + yOffset2) - ((float) handleViewSize), xOffset2 + x4, ((float) y2) + yOffset2 + ((float) handleViewSize));
                                TextSelectionHelper.this.startArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                                count2++;
                            } else {
                                int i15 = len2;
                                StaticLayout staticLayout7 = layout2;
                                canvas.save();
                                canvas2.translate(x4, (float) y2);
                                float v4 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                canvas2.scale(v4, v4, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f);
                                this.path.reset();
                                this.path.addCircle(((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, ((float) handleViewSize) / 2.0f, ((float) handleViewSize) / 2.0f, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                TextSelectionHelper.this.startArea.set(xOffset2 + x4, (((float) y2) + yOffset2) - ((float) handleViewSize), xOffset2 + x4 + ((float) handleViewSize), ((float) y2) + yOffset2 + ((float) handleViewSize));
                                TextSelectionHelper.this.startArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                            }
                        } else {
                            int i16 = len2;
                            StaticLayout staticLayout8 = layout2;
                        }
                    }
                    canvas.restore();
                }
                if (count2 != 0 && TextSelectionHelper.this.movingHandle) {
                    if (!TextSelectionHelper.this.movingHandleStart) {
                        TextSelectionHelper.this.pickEndView();
                    }
                    TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                    textSelectionHelper5.showMagnifier(textSelectionHelper5.lastX);
                    if (TextSelectionHelper.this.magnifierY != TextSelectionHelper.this.magnifierYanimated) {
                        invalidate();
                    }
                }
                if (!TextSelectionHelper.this.parentIsScrolling) {
                    TextSelectionHelper.this.showActions();
                }
                if (Build.VERSION.SDK_INT >= 23 && TextSelectionHelper.this.actionMode != null) {
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

    /* access modifiers changed from: protected */
    public void jumpToLine(int newSelection, int nextWhitespace, boolean viewChanged, float newYoffset, float oldYoffset, Cell cell) {
        int i;
        if (this.movingHandleStart) {
            this.selectionStart = nextWhitespace;
            if (!viewChanged && nextWhitespace > this.selectionEnd) {
                int k = this.selectionEnd;
                this.selectionEnd = nextWhitespace;
                this.selectionStart = k;
                this.movingHandleStart = false;
            }
            this.snap = true;
            return;
        }
        this.selectionEnd = nextWhitespace;
        if (!viewChanged && (i = this.selectionStart) > nextWhitespace) {
            int k2 = this.selectionEnd;
            this.selectionEnd = i;
            this.selectionStart = k2;
            this.movingHandleStart = true;
        }
        this.snap = true;
    }

    /* access modifiers changed from: protected */
    public boolean canSelect(int newSelection) {
        return (newSelection == this.selectionStart || newSelection == this.selectionEnd) ? false : true;
    }

    /* access modifiers changed from: protected */
    public boolean selectLayout(int x, int y) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onOffsetChanged() {
    }

    /* access modifiers changed from: protected */
    public void pickEndView() {
    }

    /* access modifiers changed from: protected */
    public void pickStartView() {
    }

    /* access modifiers changed from: protected */
    public boolean isSelectable(View child) {
        return true;
    }

    public void invalidate() {
        Cell cell = this.selectedView;
        if (cell != null) {
            cell.invalidate();
        }
        TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay2 = this.textSelectionOverlay;
        if (textSelectionOverlay2 != null) {
            textSelectionOverlay2.invalidate();
        }
    }

    private ActionMode.Callback createActionCallback() {
        final ActionMode.Callback callback2 = new ActionMode.Callback() {
            private String translateFromLanguage = null;

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(0, 16908321, 0, 17039361);
                menu.add(0, 16908319, 1, 17039373);
                menu.add(0, 3, 2, LocaleController.getString("TranslateMessage", NUM));
                return true;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                if (TextSelectionHelper.this.selectedView != null) {
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    CharSequence charSequence = textSelectionHelper.getText(textSelectionHelper.selectedView, false);
                    if (TextSelectionHelper.this.multiselect || (TextSelectionHelper.this.selectionStart <= 0 && TextSelectionHelper.this.selectionEnd >= charSequence.length() - 1)) {
                        menu.getItem(1).setVisible(false);
                    } else {
                        menu.getItem(1).setVisible(true);
                    }
                }
                if (TextSelectionHelper.this.onTranslateListener == null || !LanguageDetector.hasSupport() || TextSelectionHelper.this.getSelectedText() == null) {
                    this.translateFromLanguage = null;
                    updateTranslateButton(menu);
                } else {
                    LanguageDetector.detectLanguage(TextSelectionHelper.this.getSelectedText().toString(), new TextSelectionHelper$4$$ExternalSyntheticLambda2(this, menu), new TextSelectionHelper$4$$ExternalSyntheticLambda1(this, menu));
                }
                return true;
            }

            /* renamed from: lambda$onPrepareActionMode$0$org-telegram-ui-Cells-TextSelectionHelper$4  reason: not valid java name */
            public /* synthetic */ void m1529xfe5e6977(Menu menu, String lng) {
                this.translateFromLanguage = lng;
                updateTranslateButton(menu);
            }

            /* renamed from: lambda$onPrepareActionMode$1$org-telegram-ui-Cells-TextSelectionHelper$4  reason: not valid java name */
            public /* synthetic */ void m1530x98ff2bf8(Menu menu, Exception err) {
                FileLog.e("mlkit: failed to detect language in selection");
                FileLog.e((Throwable) err);
                this.translateFromLanguage = null;
                updateTranslateButton(menu);
            }

            /* JADX WARNING: Code restructure failed: missing block: B:2:0x0019, code lost:
                r2 = r4.translateFromLanguage;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            private void updateTranslateButton(android.view.Menu r5) {
                /*
                    r4 = this;
                    org.telegram.messenger.LocaleController r0 = org.telegram.messenger.LocaleController.getInstance()
                    java.util.Locale r0 = r0.getCurrentLocale()
                    java.lang.String r0 = r0.getLanguage()
                    r1 = 2
                    android.view.MenuItem r1 = r5.getItem(r1)
                    org.telegram.ui.Cells.TextSelectionHelper r2 = org.telegram.ui.Cells.TextSelectionHelper.this
                    org.telegram.ui.Cells.TextSelectionHelper$OnTranslateListener r2 = r2.onTranslateListener
                    if (r2 == 0) goto L_0x0041
                    java.lang.String r2 = r4.translateFromLanguage
                    if (r2 == 0) goto L_0x0039
                    boolean r2 = r2.equals(r0)
                    if (r2 == 0) goto L_0x002d
                    java.lang.String r2 = r4.translateFromLanguage
                    java.lang.String r3 = "und"
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0039
                L_0x002d:
                    java.util.HashSet r2 = org.telegram.ui.RestrictedLanguagesSelectActivity.getRestrictedLanguages()
                    java.lang.String r3 = r4.translateFromLanguage
                    boolean r2 = r2.contains(r3)
                    if (r2 == 0) goto L_0x003f
                L_0x0039:
                    boolean r2 = org.telegram.messenger.LanguageDetector.hasSupport()
                    if (r2 != 0) goto L_0x0041
                L_0x003f:
                    r2 = 1
                    goto L_0x0042
                L_0x0041:
                    r2 = 0
                L_0x0042:
                    r1.setVisible(r2)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.AnonymousClass4.updateTranslateButton(android.view.Menu):void");
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (!TextSelectionHelper.this.isSelectionMode()) {
                    return true;
                }
                switch (item.getItemId()) {
                    case 3:
                        if (TextSelectionHelper.this.onTranslateListener != null) {
                            TextSelectionHelper.this.onTranslateListener.run(TextSelectionHelper.this.getSelectedText(), this.translateFromLanguage, LocaleController.getInstance().getCurrentLocale().getLanguage(), new TextSelectionHelper$4$$ExternalSyntheticLambda0(this));
                        }
                        TextSelectionHelper.this.hideActions();
                        return true;
                    case 16908319:
                        TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                        CharSequence text = textSelectionHelper.getText(textSelectionHelper.selectedView, false);
                        if (text == null) {
                            return true;
                        }
                        TextSelectionHelper.this.selectionStart = 0;
                        TextSelectionHelper.this.selectionEnd = text.length();
                        TextSelectionHelper.this.hideActions();
                        TextSelectionHelper.this.invalidate();
                        TextSelectionHelper.this.showActions();
                        return true;
                    case 16908321:
                        TextSelectionHelper.this.copyText();
                        return true;
                    default:
                        TextSelectionHelper.this.clear();
                        return true;
                }
            }

            /* renamed from: lambda$onActionItemClicked$2$org-telegram-ui-Cells-TextSelectionHelper$4  reason: not valid java name */
            public /* synthetic */ void m1528x2de5aa17() {
                TextSelectionHelper.this.showActions();
            }

            public void onDestroyActionMode(ActionMode mode) {
                if (Build.VERSION.SDK_INT < 23) {
                    TextSelectionHelper.this.clear();
                }
            }
        };
        if (Build.VERSION.SDK_INT >= 23) {
            return new ActionMode.Callback2() {
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return callback2.onCreateActionMode(mode, menu);
                }

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return callback2.onPrepareActionMode(mode, menu);
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return callback2.onActionItemClicked(mode, item);
                }

                public void onDestroyActionMode(ActionMode mode) {
                    callback2.onDestroyActionMode(mode);
                }

                public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                    if (TextSelectionHelper.this.isSelectionMode()) {
                        TextSelectionHelper.this.pickStartView();
                        int x1 = 0;
                        int y1 = 1;
                        if (TextSelectionHelper.this.selectedView != null) {
                            TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                            int[] coords = textSelectionHelper.offsetToCord(textSelectionHelper.selectionStart);
                            x1 = coords[0] + TextSelectionHelper.this.textX;
                            y1 = (((int) (((float) (coords[1] + TextSelectionHelper.this.textY)) + TextSelectionHelper.this.selectedView.getY())) + ((-TextSelectionHelper.this.getLineHeight()) / 2)) - AndroidUtilities.dp(4.0f);
                            if (y1 < 1) {
                                y1 = 1;
                            }
                        }
                        int x2 = TextSelectionHelper.this.parentView.getWidth();
                        TextSelectionHelper.this.pickEndView();
                        if (TextSelectionHelper.this.selectedView != null) {
                            TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                            x2 = textSelectionHelper2.offsetToCord(textSelectionHelper2.selectionEnd)[0] + TextSelectionHelper.this.textX;
                        }
                        outRect.set(Math.min(x1, x2), y1, Math.max(x1, x2), y1 + 1);
                    }
                }
            };
        }
        return callback2;
    }

    /* access modifiers changed from: private */
    public void copyText() {
        CharSequence str;
        if (isSelectionMode() && (str = getSelectedText()) != null) {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
            hideActions();
            clear(true);
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onTextCopied();
            }
        }
    }

    private void translateText() {
        if (isSelectionMode()) {
            CharSequence selectedText = getSelectedText();
        }
    }

    /* access modifiers changed from: protected */
    public CharSequence getSelectedText() {
        CharSequence text = getText(this.selectedView, false);
        if (text != null) {
            return text.subSequence(this.selectionStart, this.selectionEnd);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public int[] offsetToCord(int offset) {
        fillLayoutForOffset(offset, this.layoutBlock);
        StaticLayout layout = this.layoutBlock.layout;
        if (layout == null || offset > layout.getText().length()) {
            return this.tmpCoord;
        }
        int line = layout.getLineForOffset(offset);
        this.tmpCoord[0] = (int) (layout.getPrimaryHorizontal(offset) + this.layoutBlock.xOffset);
        this.tmpCoord[1] = layout.getLineBottom(line);
        int[] iArr = this.tmpCoord;
        iArr[1] = (int) (((float) iArr[1]) + this.layoutBlock.yOffset);
        return this.tmpCoord;
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas, StaticLayout layout, int selectionStart2, int selectionEnd2) {
        int endIndex;
        int end;
        Canvas canvas2 = canvas;
        StaticLayout staticLayout = layout;
        int i = selectionStart2;
        int i2 = selectionEnd2;
        this.selectionPath.reset();
        int startLine = layout.getLineForOffset(selectionStart2);
        int endLine = staticLayout.getLineForOffset(i2);
        if (startLine == endLine) {
            drawLine(staticLayout, startLine, i, i2);
        } else {
            int end2 = staticLayout.getLineEnd(startLine);
            if (staticLayout.getParagraphDirection(startLine) != -1 && end2 > 0) {
                int end3 = end2 - 1;
                CharSequence text = layout.getText();
                int s = (int) staticLayout.getPrimaryHorizontal(end3);
                if (staticLayout.isRtlCharAt(end3)) {
                    int endIndex2 = end3;
                    while (staticLayout.isRtlCharAt(endIndex2) && endIndex2 != 0) {
                        endIndex2--;
                    }
                    endIndex = (int) (staticLayout.getLineForOffset(endIndex2) == staticLayout.getLineForOffset(end3) ? staticLayout.getPrimaryHorizontal(endIndex2 + 1) : staticLayout.getLineLeft(startLine));
                } else {
                    endIndex = (int) staticLayout.getLineRight(startLine);
                }
                int l = Math.min(s, endIndex);
                int r = Math.max(s, endIndex);
                if (end3 <= 0 || end3 >= text.length() || Character.isWhitespace(text.charAt(end3 - 1))) {
                    end = end3;
                    CharSequence charSequence = text;
                } else {
                    end = end3;
                    CharSequence charSequence2 = text;
                    this.selectionPath.addRect((float) l, (float) staticLayout.getLineTop(startLine), (float) r, (float) staticLayout.getLineBottom(startLine), Path.Direction.CW);
                }
                end2 = end;
            }
            drawLine(staticLayout, startLine, i, end2);
            drawLine(staticLayout, endLine, staticLayout.getLineStart(endLine), i2);
            int i3 = startLine + 1;
            while (i3 < endLine) {
                int s2 = (int) staticLayout.getLineLeft(i3);
                int e = (int) staticLayout.getLineRight(i3);
                int i4 = s2;
                this.selectionPath.addRect((float) Math.min(s2, e), (float) (staticLayout.getLineTop(i3) - 1), (float) Math.max(s2, e), (float) (staticLayout.getLineBottom(i3) + 1), Path.Direction.CW);
                i3++;
                end2 = end2;
            }
        }
        canvas2.drawPath(this.selectionPath, this.selectionPaint);
        float R = this.cornerRadius * 1.9f;
        float startLeft = layout.getPrimaryHorizontal(selectionStart2);
        float endLeft = staticLayout.getPrimaryHorizontal(i2);
        if (i + 1 < staticLayout.getLineEnd(startLine) && (startLine == endLine || (startLine + 1 == endLine && startLeft > endLeft))) {
            float x = startLeft;
            float b = (float) staticLayout.getLineBottom(startLine);
            this.tempPath.reset();
            this.tempPath.moveTo(x + R, b);
            this.tempPath.lineTo(x, b);
            this.tempPath.lineTo(x, b - R);
            AndroidUtilities.rectTmp.set(x, b - R, x + R, b);
            this.tempPath.arcTo(AndroidUtilities.rectTmp, 180.0f, -90.0f);
            canvas2.drawPath(this.tempPath, this.selectionHandlePaint);
        }
        if (staticLayout.getLineStart(endLine) < i2) {
            float x2 = endLeft;
            float b2 = (float) staticLayout.getLineBottom(endLine);
            this.tempPath.reset();
            this.tempPath.moveTo(x2 - R, b2);
            this.tempPath.lineTo(x2, b2);
            this.tempPath.lineTo(x2, b2 - R);
            AndroidUtilities.rectTmp.set(x2 - R, b2 - R, x2, b2);
            this.tempPath.arcTo(AndroidUtilities.rectTmp, 0.0f, 90.0f);
            canvas2.drawPath(this.tempPath, this.selectionHandlePaint);
        }
    }

    private void drawLine(StaticLayout layout, int line, int start, int end) {
        this.tempPath2.reset();
        layout.getSelectionPath(start, end, this.tempPath2);
        if (this.tempPath2.lastBottom < ((float) layout.getLineBottom(line))) {
            int lineTop = layout.getLineTop(line);
            this.tempPath2.scaleY(((float) (layout.getLineBottom(line) - lineTop)) / (this.tempPath2.lastBottom - ((float) lineTop)), (float) lineTop, this.selectionPath);
            return;
        }
        this.tempPath2.scaleY(1.0f, 0.0f, this.selectionPath);
    }

    private static class LayoutBlock {
        StaticLayout layout;
        float xOffset;
        float yOffset;

        private LayoutBlock() {
        }
    }

    public static class Callback {
        public void onStateChanged(boolean isSelected) {
        }

        public void onTextCopied() {
        }
    }

    /* access modifiers changed from: protected */
    public void fillLayoutForOffset(int offset, LayoutBlock layoutBlock2) {
        fillLayoutForOffset(offset, layoutBlock2, false);
    }

    public static class ChatListTextSelectionHelper extends TextSelectionHelper<ChatMessageCell> {
        public static int TYPE_CAPTION = 1;
        public static int TYPE_DESCRIPTION = 2;
        public static int TYPE_MESSAGE = 0;
        SparseArray<Animator> animatorSparseArray = new SparseArray<>();
        private boolean isDescription;
        private boolean maybeIsDescription;

        /* access modifiers changed from: protected */
        public int getLineHeight() {
            if (this.selectedView == null || ((ChatMessageCell) this.selectedView).getMessageObject() == null) {
                return 0;
            }
            MessageObject object = ((ChatMessageCell) this.selectedView).getMessageObject();
            StaticLayout layout = null;
            if (this.isDescription) {
                layout = ((ChatMessageCell) this.selectedView).getDescriptionlayout();
            } else if (((ChatMessageCell) this.selectedView).hasCaptionLayout()) {
                layout = ((ChatMessageCell) this.selectedView).getCaptionLayout();
            } else if (object.textLayoutBlocks != null) {
                layout = object.textLayoutBlocks.get(0).textLayout;
            }
            if (layout == null) {
                return 0;
            }
            return layout.getLineBottom(0) - layout.getLineTop(0);
        }

        public void setMessageObject(ChatMessageCell chatMessageCell) {
            this.maybeSelectedView = chatMessageCell;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (this.maybeIsDescription && chatMessageCell.getDescriptionlayout() != null) {
                this.textArea.set(this.maybeTextX, this.maybeTextY, this.maybeTextX + chatMessageCell.getDescriptionlayout().getWidth(), this.maybeTextY + chatMessageCell.getDescriptionlayout().getHeight());
            } else if (chatMessageCell.hasCaptionLayout()) {
                this.textArea.set(this.maybeTextX, this.maybeTextY, this.maybeTextX + chatMessageCell.getCaptionLayout().getWidth(), this.maybeTextY + chatMessageCell.getCaptionLayout().getHeight());
            } else if (messageObject != null && messageObject.textLayoutBlocks != null && messageObject.textLayoutBlocks.size() > 0) {
                MessageObject.TextLayoutBlock block = messageObject.textLayoutBlocks.get(messageObject.textLayoutBlocks.size() - 1);
                this.textArea.set(this.maybeTextX, this.maybeTextY, this.maybeTextX + block.textLayout.getWidth(), (int) (((float) this.maybeTextY) + block.textYOffset + ((float) block.textLayout.getHeight())));
            }
        }

        /* access modifiers changed from: protected */
        public CharSequence getText(ChatMessageCell cell, boolean maybe) {
            if (cell == null || cell.getMessageObject() == null) {
                return null;
            }
            if (!maybe ? this.isDescription : this.maybeIsDescription) {
                return cell.getDescriptionlayout().getText();
            }
            if (cell.hasCaptionLayout()) {
                return cell.getCaptionLayout().getText();
            }
            return cell.getMessageObject().messageText;
        }

        /* access modifiers changed from: protected */
        public void onTextSelected(ChatMessageCell newView, ChatMessageCell oldView) {
            boolean idChanged = oldView == null || !(oldView.getMessageObject() == null || oldView.getMessageObject().getId() == newView.getMessageObject().getId());
            this.selectedCellId = newView.getMessageObject().getId();
            this.enterProgress = 0.0f;
            this.isDescription = this.maybeIsDescription;
            Animator oldAnimator = this.animatorSparseArray.get(this.selectedCellId);
            if (oldAnimator != null) {
                oldAnimator.removeAllListeners();
                oldAnimator.cancel();
            }
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            animator.addUpdateListener(new TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda1(this, idChanged));
            animator.setDuration(250);
            animator.start();
            this.animatorSparseArray.put(this.selectedCellId, animator);
            if (!idChanged) {
                newView.setSelectedBackgroundProgress(0.0f);
            }
            SharedConfig.removeTextSelectionHint();
        }

        /* renamed from: lambda$onTextSelected$0$org-telegram-ui-Cells-TextSelectionHelper$ChatListTextSelectionHelper  reason: not valid java name */
        public /* synthetic */ void m1531x6ff5dada(boolean idChanged, ValueAnimator animation) {
            this.enterProgress = ((Float) animation.getAnimatedValue()).floatValue();
            if (this.textSelectionOverlay != null) {
                this.textSelectionOverlay.invalidate();
            }
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).getCurrentMessagesGroup() == null && idChanged) {
                ((ChatMessageCell) this.selectedView).setSelectedBackgroundProgress(1.0f - this.enterProgress);
            }
        }

        public void draw(MessageObject messageObject, MessageObject.TextLayoutBlock block, Canvas canvas) {
            MessageObject selectedMessageObject;
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).getMessageObject() != null && !this.isDescription && (selectedMessageObject = ((ChatMessageCell) this.selectedView).getMessageObject()) != null && selectedMessageObject.textLayoutBlocks != null && messageObject.getId() == this.selectedCellId) {
                int selectionStart = this.selectionStart;
                int selectionEnd = this.selectionEnd;
                if (selectedMessageObject.textLayoutBlocks.size() > 1) {
                    if (selectionStart < block.charactersOffset) {
                        selectionStart = block.charactersOffset;
                    }
                    if (selectionStart > block.charactersEnd) {
                        selectionStart = block.charactersEnd;
                    }
                    if (selectionEnd < block.charactersOffset) {
                        selectionEnd = block.charactersOffset;
                    }
                    if (selectionEnd > block.charactersEnd) {
                        selectionEnd = block.charactersEnd;
                    }
                }
                if (selectionStart != selectionEnd) {
                    if (selectedMessageObject.isOutOwner()) {
                        this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                        this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                    } else {
                        this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                        this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                    }
                    drawSelection(canvas, block.textLayout, selectionStart, selectionEnd);
                }
            }
        }

        /* access modifiers changed from: protected */
        public int getCharOffsetFromCord(int x, int y, int offsetX, int offsetY, ChatMessageCell cell, boolean maybe) {
            StaticLayout lastLayout;
            float yOffset;
            int y2;
            if (cell == null) {
                return 0;
            }
            int line = -1;
            int x2 = x - offsetX;
            int y3 = y - offsetY;
            if (maybe ? this.maybeIsDescription : this.isDescription) {
                yOffset = 0.0f;
                lastLayout = cell.getDescriptionlayout();
            } else if (cell.hasCaptionLayout()) {
                yOffset = 0.0f;
                lastLayout = cell.getCaptionLayout();
            } else {
                MessageObject.TextLayoutBlock lastBlock = cell.getMessageObject().textLayoutBlocks.get(cell.getMessageObject().textLayoutBlocks.size() - 1);
                StaticLayout lastLayout2 = lastBlock.textLayout;
                yOffset = lastBlock.textYOffset;
                lastLayout = lastLayout2;
            }
            if (y3 < 0) {
                y3 = 1;
            }
            if (((float) y3) > ((float) lastLayout.getLineBottom(lastLayout.getLineCount() - 1)) + yOffset) {
                y2 = (int) ((((float) lastLayout.getLineBottom(lastLayout.getLineCount() - 1)) + yOffset) - 1.0f);
            } else {
                y2 = y3;
            }
            fillLayoutForCoords(x2, y2, cell, this.layoutBlock, maybe);
            if (this.layoutBlock.layout == null) {
                return -1;
            }
            StaticLayout layout = this.layoutBlock.layout;
            int x3 = (int) (((float) x2) - this.layoutBlock.xOffset);
            int i = 0;
            while (true) {
                if (i < layout.getLineCount()) {
                    if (((float) y2) > this.layoutBlock.yOffset + ((float) layout.getLineTop(i)) && ((float) y2) < this.layoutBlock.yOffset + ((float) layout.getLineBottom(i))) {
                        line = i;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (line >= 0) {
                return layout.getOffsetForHorizontal(line, (float) x3);
            }
            return -1;
        }

        private void fillLayoutForCoords(int x, int y, ChatMessageCell cell, LayoutBlock layoutBlock, boolean maybe) {
            if (cell != null) {
                MessageObject messageObject = cell.getMessageObject();
                if (!maybe ? this.isDescription : this.maybeIsDescription) {
                    layoutBlock.layout = cell.getDescriptionlayout();
                    layoutBlock.xOffset = 0.0f;
                    layoutBlock.yOffset = 0.0f;
                } else if (cell.hasCaptionLayout()) {
                    layoutBlock.layout = cell.getCaptionLayout();
                    layoutBlock.xOffset = 0.0f;
                    layoutBlock.yOffset = 0.0f;
                } else {
                    int i = 0;
                    while (i < messageObject.textLayoutBlocks.size()) {
                        MessageObject.TextLayoutBlock block = messageObject.textLayoutBlocks.get(i);
                        if (((float) y) < block.textYOffset || ((float) y) > block.textYOffset + ((float) block.height)) {
                            i++;
                        } else {
                            layoutBlock.layout = block.textLayout;
                            layoutBlock.yOffset = block.textYOffset;
                            layoutBlock.xOffset = (float) (-(block.isRtl() ? (int) Math.ceil((double) messageObject.textXOffset) : 0));
                            return;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void fillLayoutForOffset(int offset, LayoutBlock layoutBlock, boolean maybe) {
            ChatMessageCell selectedView = (ChatMessageCell) (maybe ? this.maybeSelectedView : this.selectedView);
            if (selectedView == null) {
                layoutBlock.layout = null;
                return;
            }
            MessageObject messageObject = selectedView.getMessageObject();
            if (this.isDescription) {
                layoutBlock.layout = selectedView.getDescriptionlayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else if (selectedView.hasCaptionLayout()) {
                layoutBlock.layout = selectedView.getCaptionLayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else if (messageObject.textLayoutBlocks == null) {
                layoutBlock.layout = null;
            } else {
                int i = 0;
                if (messageObject.textLayoutBlocks.size() == 1) {
                    layoutBlock.layout = messageObject.textLayoutBlocks.get(0).textLayout;
                    layoutBlock.yOffset = 0.0f;
                    if (messageObject.textLayoutBlocks.get(0).isRtl()) {
                        i = (int) Math.ceil((double) messageObject.textXOffset);
                    }
                    layoutBlock.xOffset = (float) (-i);
                    return;
                }
                int i2 = 0;
                while (i2 < messageObject.textLayoutBlocks.size()) {
                    MessageObject.TextLayoutBlock block = messageObject.textLayoutBlocks.get(i2);
                    if (offset < block.charactersOffset || offset > block.charactersEnd) {
                        i2++;
                    } else {
                        layoutBlock.layout = messageObject.textLayoutBlocks.get(i2).textLayout;
                        layoutBlock.yOffset = messageObject.textLayoutBlocks.get(i2).textYOffset;
                        if (block.isRtl()) {
                            i = (int) Math.ceil((double) messageObject.textXOffset);
                        }
                        layoutBlock.xOffset = (float) (-i);
                        return;
                    }
                }
                layoutBlock.layout = null;
            }
        }

        /* access modifiers changed from: protected */
        public void onExitSelectionMode(boolean instant) {
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).isDrawingSelectionBackground() && !instant) {
                final ChatMessageCell cell = (ChatMessageCell) this.selectedView;
                int id = ((ChatMessageCell) this.selectedView).getMessageObject().getId();
                Animator oldAnimator = this.animatorSparseArray.get(id);
                if (oldAnimator != null) {
                    oldAnimator.removeAllListeners();
                    oldAnimator.cancel();
                }
                cell.setSelectedBackgroundProgress(0.01f);
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.01f, 1.0f});
                animator.addUpdateListener(new TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda0(cell, id));
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        cell.setSelectedBackgroundProgress(0.0f);
                    }
                });
                animator.setDuration(300);
                animator.start();
                this.animatorSparseArray.put(id, animator);
            }
        }

        static /* synthetic */ void lambda$onExitSelectionMode$1(ChatMessageCell cell, int id, ValueAnimator animation) {
            float exit = ((Float) animation.getAnimatedValue()).floatValue();
            if (cell.getMessageObject() != null && cell.getMessageObject().getId() == id) {
                cell.setSelectedBackgroundProgress(exit);
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

        public void drawCaption(boolean isOut, StaticLayout captionLayout, Canvas canvas) {
            if (!this.isDescription) {
                if (isOut) {
                    this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                } else {
                    this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                }
                drawSelection(canvas, captionLayout, this.selectionStart, this.selectionEnd);
            }
        }

        public void drawDescription(boolean isOut, StaticLayout layout, Canvas canvas) {
            if (this.isDescription) {
                if (isOut) {
                    this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                } else {
                    this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                }
                drawSelection(canvas, layout, this.selectionStart, this.selectionEnd);
            }
        }

        public void invalidate() {
            TextSelectionHelper.super.invalidate();
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).getCurrentMessagesGroup() != null) {
                this.parentView.invalidate();
            }
        }

        public void cancelAllAnimators() {
            for (int i = 0; i < this.animatorSparseArray.size(); i++) {
                SparseArray<Animator> sparseArray = this.animatorSparseArray;
                sparseArray.get(sparseArray.keyAt(i)).cancel();
            }
            this.animatorSparseArray.clear();
        }

        public void setIsDescription(boolean b) {
            this.maybeIsDescription = b;
        }

        public void clear(boolean instant) {
            TextSelectionHelper.super.clear(instant);
            this.isDescription = false;
        }

        public int getTextSelectionType(ChatMessageCell cell) {
            if (this.isDescription) {
                return TYPE_DESCRIPTION;
            }
            if (cell.hasCaptionLayout()) {
                return TYPE_CAPTION;
            }
            return TYPE_MESSAGE;
        }

        public void updateTextPosition(int textX, int textY) {
            if (this.textX != textX || this.textY != textY) {
                this.textX = textX;
                this.textY = textY;
                invalidate();
            }
        }

        public void checkDataChanged(MessageObject messageObject) {
            if (this.selectedCellId == messageObject.getId()) {
                clear(true);
            }
        }
    }

    public static class ArticleTextSelectionHelper extends TextSelectionHelper<ArticleSelectableView> {
        public ArrayList<TextLayoutBlock> arrayList = new ArrayList<>();
        SparseIntArray childCountByPosition = new SparseIntArray();
        int endViewChildPosition = -1;
        int endViewOffset;
        int endViewPosition = -1;
        public LinearLayoutManager layoutManager;
        int maybeTextIndex = -1;
        SparseArray<CharSequence> prefixTextByPosition = new SparseArray<>();
        boolean startPeek;
        int startViewChildPosition = -1;
        int startViewOffset;
        int startViewPosition = -1;
        SparseArray<CharSequence> textByPosition = new SparseArray<>();

        public ArticleTextSelectionHelper() {
            this.multiselect = true;
            this.showActionsAsPopupAlways = true;
        }

        /* access modifiers changed from: protected */
        public CharSequence getText(ArticleSelectableView view, boolean maybe) {
            int i;
            this.arrayList.clear();
            view.fillTextLayoutBlocks(this.arrayList);
            if (maybe) {
                i = this.maybeTextIndex;
            } else {
                i = this.startPeek != 0 ? this.startViewChildPosition : this.endViewChildPosition;
            }
            if (this.arrayList.isEmpty() || i < 0) {
                return "";
            }
            return this.arrayList.get(i).getLayout().getText();
        }

        /* access modifiers changed from: protected */
        public int getCharOffsetFromCord(int x, int y, int offsetX, int offsetY, ArticleSelectableView view, boolean maybe) {
            int childIndex;
            if (view == null) {
                return -1;
            }
            int line = -1;
            int x2 = x - offsetX;
            int y2 = y - offsetY;
            this.arrayList.clear();
            view.fillTextLayoutBlocks(this.arrayList);
            if (maybe) {
                childIndex = this.maybeTextIndex;
            } else {
                childIndex = this.startPeek != 0 ? this.startViewChildPosition : this.endViewChildPosition;
            }
            StaticLayout layout = this.arrayList.get(childIndex).getLayout();
            if (x2 < 0) {
                x2 = 1;
            }
            if (y2 < 0) {
                y2 = 1;
            }
            if (x2 > layout.getWidth()) {
                x2 = layout.getWidth();
            }
            if (y2 > layout.getLineBottom(layout.getLineCount() - 1)) {
                y2 = layout.getLineBottom(layout.getLineCount() - 1) - 1;
            }
            int i = 0;
            while (true) {
                if (i < layout.getLineCount()) {
                    if (y2 > layout.getLineTop(i) && y2 < layout.getLineBottom(i)) {
                        line = i;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (line >= 0) {
                return layout.getOffsetForHorizontal(line, (float) x2);
            }
            return -1;
        }

        /* access modifiers changed from: protected */
        public void fillLayoutForOffset(int offset, LayoutBlock layoutBlock, boolean maybe) {
            this.arrayList.clear();
            ArticleSelectableView selectedView = (ArticleSelectableView) (maybe ? this.maybeSelectedView : this.selectedView);
            if (selectedView == null) {
                layoutBlock.layout = null;
                return;
            }
            selectedView.fillTextLayoutBlocks(this.arrayList);
            if (maybe) {
                layoutBlock.layout = this.arrayList.get(this.maybeTextIndex).getLayout();
            } else {
                int index = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
                if (index < 0 || index >= this.arrayList.size()) {
                    layoutBlock.layout = null;
                    return;
                }
                layoutBlock.layout = this.arrayList.get(index).getLayout();
            }
            layoutBlock.yOffset = 0.0f;
            layoutBlock.xOffset = 0.0f;
        }

        /* access modifiers changed from: protected */
        public int getLineHeight() {
            if (this.selectedView == null) {
                return 0;
            }
            this.arrayList.clear();
            ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
            int index = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (index < 0 || index >= this.arrayList.size()) {
                return 0;
            }
            StaticLayout layout = this.arrayList.get(index).getLayout();
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < layout.getLineCount(); i++) {
                int h = layout.getLineBottom(i) - layout.getLineTop(i);
                if (h < min) {
                    min = h;
                }
            }
            return min;
        }

        public void trySelect(View view) {
            if (this.maybeSelectedView != null) {
                this.startSelectionRunnable.run();
            }
        }

        public void setMaybeView(int x, int y, View parentView) {
            if (parentView instanceof ArticleSelectableView) {
                this.capturedX = x;
                this.capturedY = y;
                this.maybeSelectedView = (ArticleSelectableView) parentView;
                int findClosestLayoutIndex = findClosestLayoutIndex(x, y, (ArticleSelectableView) this.maybeSelectedView);
                this.maybeTextIndex = findClosestLayoutIndex;
                if (findClosestLayoutIndex < 0) {
                    this.maybeSelectedView = null;
                    return;
                }
                this.maybeTextX = this.arrayList.get(findClosestLayoutIndex).getX();
                this.maybeTextY = this.arrayList.get(this.maybeTextIndex).getY();
            }
        }

        private int findClosestLayoutIndex(int x, int y, ArticleSelectableView maybeSelectedView) {
            if (maybeSelectedView instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) maybeSelectedView;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if ((child instanceof ArticleSelectableView) && ((float) y) > child.getY() && ((float) y) < child.getY() + ((float) child.getHeight())) {
                        return findClosestLayoutIndex((int) (((float) x) - child.getX()), (int) (((float) y) - child.getY()), (ArticleSelectableView) child);
                    }
                }
            }
            this.arrayList.clear();
            maybeSelectedView.fillTextLayoutBlocks(this.arrayList);
            if (this.arrayList.isEmpty()) {
                return -1;
            }
            int minDistance = Integer.MAX_VALUE;
            int minIndex = -1;
            int i2 = this.arrayList.size() - 1;
            while (true) {
                if (i2 < 0) {
                    break;
                }
                TextLayoutBlock block = this.arrayList.get(i2);
                int top = block.getY();
                int bottom = block.getLayout().getHeight() + top;
                if (y >= top && y < bottom) {
                    minDistance = 0;
                    minIndex = i2;
                    break;
                }
                int d = Math.min(Math.abs(y - top), Math.abs(y - bottom));
                if (d < minDistance) {
                    minDistance = d;
                    minIndex = i2;
                }
                i2--;
            }
            if (minIndex < 0) {
                return -1;
            }
            int row = this.arrayList.get(minIndex).getRow();
            if (row <= 0 || minDistance >= AndroidUtilities.dp(24.0f)) {
                return minIndex;
            }
            int minDistanceX = Integer.MAX_VALUE;
            int minIndexX = minIndex;
            for (int i3 = this.arrayList.size() - 1; i3 >= 0; i3--) {
                TextLayoutBlock block2 = this.arrayList.get(i3);
                if (block2.getRow() == row) {
                    int left = block2.getX();
                    int right = block2.getX() + block2.getLayout().getWidth();
                    if (x >= left && x <= right) {
                        return i3;
                    }
                    int d2 = Math.min(Math.abs(x - left), Math.abs(x - right));
                    if (d2 < minDistanceX) {
                        minDistanceX = d2;
                        minIndexX = i3;
                    }
                }
            }
            return minIndexX;
        }

        public void draw(Canvas canvas, ArticleSelectableView view, int i) {
            this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            int position = getAdapterPosition(view);
            if (position >= 0) {
                this.arrayList.clear();
                view.fillTextLayoutBlocks(this.arrayList);
                if (!this.arrayList.isEmpty()) {
                    TextLayoutBlock layoutBlock = this.arrayList.get(i);
                    int endOffset = this.endViewOffset;
                    int textLen = layoutBlock.getLayout().getText().length();
                    if (endOffset > textLen) {
                        endOffset = textLen;
                    }
                    int i2 = this.startViewPosition;
                    if (position == i2 && position == this.endViewPosition) {
                        int i3 = this.startViewChildPosition;
                        int i4 = this.endViewChildPosition;
                        if (i3 == i4 && i3 == i) {
                            drawSelection(canvas, layoutBlock.getLayout(), this.startViewOffset, endOffset);
                        } else if (i == i3) {
                            drawSelection(canvas, layoutBlock.getLayout(), this.startViewOffset, textLen);
                        } else if (i == i4) {
                            drawSelection(canvas, layoutBlock.getLayout(), 0, endOffset);
                        } else if (i > i3 && i < i4) {
                            drawSelection(canvas, layoutBlock.getLayout(), 0, textLen);
                        }
                    } else if (position == i2 && this.startViewChildPosition == i) {
                        drawSelection(canvas, layoutBlock.getLayout(), this.startViewOffset, textLen);
                    } else {
                        int i5 = this.endViewPosition;
                        if (position == i5 && this.endViewChildPosition == i) {
                            drawSelection(canvas, layoutBlock.getLayout(), 0, endOffset);
                        } else if ((position > i2 && position < i5) || ((position == i2 && i > this.startViewChildPosition) || (position == i5 && i < this.endViewChildPosition))) {
                            drawSelection(canvas, layoutBlock.getLayout(), 0, textLen);
                        }
                    }
                }
            }
        }

        private int getAdapterPosition(ArticleSelectableView view) {
            View child = (View) view;
            ViewParent parent = child.getParent();
            while (true) {
                if (parent != this.parentView && parent != null) {
                    if (!(parent instanceof View)) {
                        parent = null;
                        break;
                    }
                    child = (View) parent;
                    parent = child.getParent();
                } else {
                    break;
                }
            }
            if (parent == null) {
                return -1;
            }
            if (this.parentRecyclerView != null) {
                return this.parentRecyclerView.getChildAdapterPosition(child);
            }
            return this.parentView.indexOfChild(child);
        }

        public boolean isSelectable(View child) {
            if (!(child instanceof ArticleSelectableView)) {
                return false;
            }
            this.arrayList.clear();
            ((ArticleSelectableView) child).fillTextLayoutBlocks(this.arrayList);
            if (child instanceof ArticleViewer.BlockTableCell) {
                return true;
            }
            return !this.arrayList.isEmpty();
        }

        /* access modifiers changed from: protected */
        public void onTextSelected(ArticleSelectableView newView, ArticleSelectableView oldView) {
            int position = getAdapterPosition(newView);
            if (position >= 0) {
                this.endViewPosition = position;
                this.startViewPosition = position;
                int i = this.maybeTextIndex;
                this.endViewChildPosition = i;
                this.startViewChildPosition = i;
                this.arrayList.clear();
                newView.fillTextLayoutBlocks(this.arrayList);
                int n = this.arrayList.size();
                this.childCountByPosition.put(position, n);
                for (int i2 = 0; i2 < n; i2++) {
                    this.textByPosition.put((i2 << 16) + position, this.arrayList.get(i2).getLayout().getText());
                    this.prefixTextByPosition.put((i2 << 16) + position, this.arrayList.get(i2).getPrefix());
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onNewViewSelected(ArticleSelectableView oldView, ArticleSelectableView newView, int childPosition) {
            int i;
            int position = getAdapterPosition(newView);
            int oldPosition = -1;
            if (oldView != null) {
                oldPosition = getAdapterPosition(oldView);
            }
            invalidate();
            if (!this.movingDirectionSettling || (i = this.startViewPosition) != this.endViewPosition) {
                if (this.movingHandleStart) {
                    if (position == oldPosition) {
                        int i2 = this.endViewChildPosition;
                        if (childPosition <= i2 || position < this.endViewPosition) {
                            this.startViewPosition = position;
                            this.startViewChildPosition = childPosition;
                            pickStartView();
                            this.startViewOffset = this.selectionEnd;
                        } else {
                            this.endViewPosition = position;
                            this.startViewChildPosition = i2;
                            this.endViewChildPosition = childPosition;
                            this.startViewOffset = this.endViewOffset;
                            pickEndView();
                            this.endViewOffset = 0;
                            this.movingHandleStart = false;
                        }
                    } else if (position <= this.endViewPosition) {
                        this.startViewPosition = position;
                        this.startViewChildPosition = childPosition;
                        pickStartView();
                        this.startViewOffset = this.selectionEnd;
                    } else {
                        this.endViewPosition = position;
                        this.startViewChildPosition = this.endViewChildPosition;
                        this.endViewChildPosition = childPosition;
                        this.startViewOffset = this.endViewOffset;
                        pickEndView();
                        this.endViewOffset = 0;
                        this.movingHandleStart = false;
                    }
                } else if (position == oldPosition) {
                    int i3 = this.startViewChildPosition;
                    if (childPosition >= i3 || position > this.startViewPosition) {
                        this.endViewPosition = position;
                        this.endViewChildPosition = childPosition;
                        pickEndView();
                        this.endViewOffset = 0;
                    } else {
                        this.startViewPosition = position;
                        this.endViewChildPosition = i3;
                        this.startViewChildPosition = childPosition;
                        this.endViewOffset = this.startViewOffset;
                        pickStartView();
                        this.movingHandleStart = true;
                        this.startViewOffset = this.selectionEnd;
                    }
                } else if (position >= this.startViewPosition) {
                    this.endViewPosition = position;
                    this.endViewChildPosition = childPosition;
                    pickEndView();
                    this.endViewOffset = 0;
                } else {
                    this.startViewPosition = position;
                    this.endViewChildPosition = this.startViewChildPosition;
                    this.startViewChildPosition = childPosition;
                    this.endViewOffset = this.startViewOffset;
                    pickStartView();
                    this.movingHandleStart = true;
                    this.startViewOffset = this.selectionEnd;
                }
            } else if (position == i) {
                if (childPosition < this.startViewChildPosition) {
                    this.startViewChildPosition = childPosition;
                    pickStartView();
                    this.movingHandleStart = true;
                    this.startViewOffset = this.selectionEnd;
                    this.selectionStart = this.selectionEnd - 1;
                } else {
                    this.endViewChildPosition = childPosition;
                    pickEndView();
                    this.movingHandleStart = false;
                    this.endViewOffset = 0;
                }
            } else if (position < i) {
                this.startViewPosition = position;
                this.startViewChildPosition = childPosition;
                pickStartView();
                this.movingHandleStart = true;
                this.startViewOffset = this.selectionEnd;
                this.selectionStart = this.selectionEnd - 1;
            } else {
                this.endViewPosition = position;
                this.endViewChildPosition = childPosition;
                pickEndView();
                this.movingHandleStart = false;
                this.endViewOffset = 0;
            }
            this.arrayList.clear();
            newView.fillTextLayoutBlocks(this.arrayList);
            int n = this.arrayList.size();
            this.childCountByPosition.put(position, n);
            for (int i4 = 0; i4 < n; i4++) {
                this.textByPosition.put((i4 << 16) + position, this.arrayList.get(i4).getLayout().getText());
                this.prefixTextByPosition.put((i4 << 16) + position, this.arrayList.get(i4).getPrefix());
            }
        }

        /* JADX WARNING: type inference failed for: r1v23, types: [android.view.View] */
        /* JADX WARNING: type inference failed for: r1v24, types: [android.view.View] */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void pickEndView() {
            /*
                r4 = this;
                boolean r0 = r4.isSelectionMode()
                if (r0 != 0) goto L_0x0007
                return
            L_0x0007:
                r0 = 0
                r4.startPeek = r0
                int r1 = r4.endViewPosition
                if (r1 < 0) goto L_0x009c
                r2 = 0
                androidx.recyclerview.widget.LinearLayoutManager r3 = r4.layoutManager
                if (r3 == 0) goto L_0x001b
                android.view.View r1 = r3.findViewByPosition(r1)
                r2 = r1
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r2 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r2
                goto L_0x002e
            L_0x001b:
                android.view.ViewGroup r3 = r4.parentView
                int r3 = r3.getChildCount()
                if (r1 >= r3) goto L_0x002e
                android.view.ViewGroup r1 = r4.parentView
                int r3 = r4.endViewPosition
                android.view.View r1 = r1.getChildAt(r3)
                r2 = r1
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r2 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r2
            L_0x002e:
                if (r2 != 0) goto L_0x0034
                r0 = 0
                r4.selectedView = r0
                return
            L_0x0034:
                r4.selectedView = r2
                int r1 = r4.startViewPosition
                int r3 = r4.endViewPosition
                if (r1 == r3) goto L_0x003f
                r4.selectionStart = r0
                goto L_0x004c
            L_0x003f:
                int r1 = r4.startViewChildPosition
                int r3 = r4.endViewChildPosition
                if (r1 == r3) goto L_0x0048
                r4.selectionStart = r0
                goto L_0x004c
            L_0x0048:
                int r1 = r4.startViewOffset
                r4.selectionStart = r1
            L_0x004c:
                int r1 = r4.endViewOffset
                r4.selectionEnd = r1
                org.telegram.ui.Cells.TextSelectionHelper$SelectableView r1 = r4.selectedView
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r1 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r1
                java.lang.CharSequence r0 = r4.getText((org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r1, (boolean) r0)
                int r1 = r4.selectionEnd
                int r3 = r0.length()
                if (r1 <= r3) goto L_0x0066
                int r1 = r0.length()
                r4.selectionEnd = r1
            L_0x0066:
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r1 = r4.arrayList
                r1.clear()
                org.telegram.ui.Cells.TextSelectionHelper$SelectableView r1 = r4.selectedView
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r1 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r1
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r3 = r4.arrayList
                r1.fillTextLayoutBlocks(r3)
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r1 = r4.arrayList
                boolean r1 = r1.isEmpty()
                if (r1 != 0) goto L_0x009c
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r1 = r4.arrayList
                int r3 = r4.endViewChildPosition
                java.lang.Object r1 = r1.get(r3)
                org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1
                int r1 = r1.getX()
                r4.textX = r1
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r1 = r4.arrayList
                int r3 = r4.endViewChildPosition
                java.lang.Object r1 = r1.get(r3)
                org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1
                int r1 = r1.getY()
                r4.textY = r1
            L_0x009c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.ArticleTextSelectionHelper.pickEndView():void");
        }

        /* JADX WARNING: type inference failed for: r0v31, types: [android.view.View] */
        /* JADX WARNING: type inference failed for: r0v32, types: [android.view.View] */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void pickStartView() {
            /*
                r4 = this;
                boolean r0 = r4.isSelectionMode()
                if (r0 != 0) goto L_0x0007
                return
            L_0x0007:
                r0 = 1
                r4.startPeek = r0
                int r0 = r4.startViewPosition
                if (r0 < 0) goto L_0x00a1
                r1 = 0
                androidx.recyclerview.widget.LinearLayoutManager r2 = r4.layoutManager
                if (r2 == 0) goto L_0x001b
                android.view.View r0 = r2.findViewByPosition(r0)
                r1 = r0
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r1 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r1
                goto L_0x0030
            L_0x001b:
                int r0 = r4.endViewPosition
                android.view.ViewGroup r2 = r4.parentView
                int r2 = r2.getChildCount()
                if (r0 >= r2) goto L_0x0030
                android.view.ViewGroup r0 = r4.parentView
                int r2 = r4.startViewPosition
                android.view.View r0 = r0.getChildAt(r2)
                r1 = r0
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r1 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r1
            L_0x0030:
                if (r1 != 0) goto L_0x0036
                r0 = 0
                r4.selectedView = r0
                return
            L_0x0036:
                r4.selectedView = r1
                int r0 = r4.startViewPosition
                int r2 = r4.endViewPosition
                r3 = 0
                if (r0 == r2) goto L_0x004e
                org.telegram.ui.Cells.TextSelectionHelper$SelectableView r0 = r4.selectedView
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r0 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0
                java.lang.CharSequence r0 = r4.getText((org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0, (boolean) r3)
                int r0 = r0.length()
                r4.selectionEnd = r0
                goto L_0x0067
            L_0x004e:
                int r0 = r4.startViewChildPosition
                int r2 = r4.endViewChildPosition
                if (r0 == r2) goto L_0x0063
                org.telegram.ui.Cells.TextSelectionHelper$SelectableView r0 = r4.selectedView
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r0 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0
                java.lang.CharSequence r0 = r4.getText((org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0, (boolean) r3)
                int r0 = r0.length()
                r4.selectionEnd = r0
                goto L_0x0067
            L_0x0063:
                int r0 = r4.endViewOffset
                r4.selectionEnd = r0
            L_0x0067:
                int r0 = r4.startViewOffset
                r4.selectionStart = r0
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r4.arrayList
                r0.clear()
                org.telegram.ui.Cells.TextSelectionHelper$SelectableView r0 = r4.selectedView
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r0 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r2 = r4.arrayList
                r0.fillTextLayoutBlocks(r2)
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r4.arrayList
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00a1
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r4.arrayList
                int r2 = r4.startViewChildPosition
                java.lang.Object r0 = r0.get(r2)
                org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock r0 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r0
                int r0 = r0.getX()
                r4.textX = r0
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r4.arrayList
                int r2 = r4.startViewChildPosition
                java.lang.Object r0 = r0.get(r2)
                org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock r0 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r0
                int r0 = r0.getY()
                r4.textY = r0
            L_0x00a1:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.ArticleTextSelectionHelper.pickStartView():void");
        }

        /* access modifiers changed from: protected */
        public void onOffsetChanged() {
            int position = getAdapterPosition((ArticleSelectableView) this.selectedView);
            int childPosition = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (position == this.startViewPosition && childPosition == this.startViewChildPosition) {
                this.startViewOffset = this.selectionStart;
            }
            if (position == this.endViewPosition && childPosition == this.endViewChildPosition) {
                this.endViewOffset = this.selectionEnd;
            }
        }

        public void invalidate() {
            TextSelectionHelper.super.invalidate();
            for (int i = 0; i < this.parentView.getChildCount(); i++) {
                this.parentView.getChildAt(i).invalidate();
            }
        }

        public void clear(boolean instant) {
            TextSelectionHelper.super.clear(instant);
            this.startViewPosition = -1;
            this.endViewPosition = -1;
            this.startViewChildPosition = -1;
            this.endViewChildPosition = -1;
            this.textByPosition.clear();
            this.childCountByPosition.clear();
        }

        /* access modifiers changed from: protected */
        public CharSequence getSelectedText() {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            int i = this.startViewPosition;
            while (true) {
                int i2 = this.endViewPosition;
                if (i > i2) {
                    break;
                }
                int i3 = this.startViewPosition;
                if (i == i3) {
                    int n = i3 == i2 ? this.endViewChildPosition : this.childCountByPosition.get(i) - 1;
                    for (int k = this.startViewChildPosition; k <= n; k++) {
                        CharSequence text = this.textByPosition.get((k << 16) + i);
                        if (text != null) {
                            int i4 = this.startViewPosition;
                            int i5 = this.endViewPosition;
                            if (i4 == i5 && k == this.endViewChildPosition && k == this.startViewChildPosition) {
                                int e = this.endViewOffset;
                                int s = this.startViewOffset;
                                if (e < s) {
                                    int tmp = s;
                                    s = e;
                                    e = tmp;
                                }
                                if (s < text.length()) {
                                    if (e > text.length()) {
                                        e = text.length();
                                    }
                                    stringBuilder.append(text.subSequence(s, e));
                                    stringBuilder.append(10);
                                }
                            } else if (i4 == i5 && k == this.endViewChildPosition) {
                                CharSequence prefix = this.prefixTextByPosition.get((k << 16) + i);
                                if (prefix != null) {
                                    stringBuilder.append(prefix).append(' ');
                                }
                                int e2 = this.endViewOffset;
                                if (e2 > text.length()) {
                                    e2 = text.length();
                                }
                                stringBuilder.append(text.subSequence(0, e2));
                                stringBuilder.append(10);
                            } else if (k == this.startViewChildPosition) {
                                int s2 = this.startViewOffset;
                                if (s2 < text.length()) {
                                    stringBuilder.append(text.subSequence(s2, text.length()));
                                    stringBuilder.append(10);
                                }
                            } else {
                                CharSequence prefix2 = this.prefixTextByPosition.get((k << 16) + i);
                                if (prefix2 != null) {
                                    stringBuilder.append(prefix2).append(' ');
                                }
                                stringBuilder.append(text);
                                stringBuilder.append(10);
                            }
                        }
                    }
                } else if (i == i2) {
                    for (int k2 = 0; k2 <= this.endViewChildPosition; k2++) {
                        CharSequence text2 = this.textByPosition.get((k2 << 16) + i);
                        if (text2 != null) {
                            if (this.startViewPosition == this.endViewPosition && k2 == this.endViewChildPosition && k2 == this.startViewChildPosition) {
                                int e3 = this.endViewOffset;
                                int s3 = this.startViewOffset;
                                if (s3 < text2.length()) {
                                    if (e3 > text2.length()) {
                                        e3 = text2.length();
                                    }
                                    stringBuilder.append(text2.subSequence(s3, e3));
                                    stringBuilder.append(10);
                                }
                            } else if (k2 == this.endViewChildPosition) {
                                CharSequence prefix3 = this.prefixTextByPosition.get((k2 << 16) + i);
                                if (prefix3 != null) {
                                    stringBuilder.append(prefix3).append(' ');
                                }
                                int e4 = this.endViewOffset;
                                if (e4 > text2.length()) {
                                    e4 = text2.length();
                                }
                                stringBuilder.append(text2.subSequence(0, e4));
                                stringBuilder.append(10);
                            } else {
                                CharSequence prefix4 = this.prefixTextByPosition.get((k2 << 16) + i);
                                if (prefix4 != null) {
                                    stringBuilder.append(prefix4).append(' ');
                                }
                                stringBuilder.append(text2);
                                stringBuilder.append(10);
                            }
                        }
                    }
                } else {
                    int n2 = this.childCountByPosition.get(i);
                    for (int k3 = this.startViewChildPosition; k3 < n2; k3++) {
                        CharSequence prefix5 = this.prefixTextByPosition.get((k3 << 16) + i);
                        if (prefix5 != null) {
                            stringBuilder.append(prefix5).append(' ');
                        }
                        stringBuilder.append(this.textByPosition.get((k3 << 16) + i));
                        stringBuilder.append(10);
                    }
                }
                i++;
            }
            if (stringBuilder.length() <= 0) {
                return null;
            }
            for (IgnoreCopySpannable span : (IgnoreCopySpannable[]) stringBuilder.getSpans(0, stringBuilder.length() - 1, IgnoreCopySpannable.class)) {
                stringBuilder.delete(stringBuilder.getSpanStart(span), stringBuilder.getSpanEnd(span));
            }
            return stringBuilder.subSequence(0, stringBuilder.length() - 1);
        }

        /* access modifiers changed from: protected */
        public boolean selectLayout(int x, int y) {
            if (!this.multiselect) {
                return false;
            }
            if (y <= ((ArticleSelectableView) this.selectedView).getTop() || y >= ((ArticleSelectableView) this.selectedView).getBottom()) {
                int n = this.parentView.getChildCount();
                for (int i = 0; i < n; i++) {
                    if (isSelectable(this.parentView.getChildAt(i))) {
                        ArticleSelectableView child = (ArticleSelectableView) this.parentView.getChildAt(i);
                        if (y > child.getTop() && y < child.getBottom()) {
                            int index = findClosestLayoutIndex((int) (((float) x) - child.getX()), (int) (((float) y) - child.getY()), child);
                            if (index < 0) {
                                return false;
                            }
                            onNewViewSelected((ArticleSelectableView) this.selectedView, child, index);
                            this.selectedView = child;
                            return true;
                        }
                    }
                }
                return false;
            }
            int currentChildPosition = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            int k = findClosestLayoutIndex((int) (((float) x) - ((ArticleSelectableView) this.selectedView).getX()), (int) (((float) y) - ((ArticleSelectableView) this.selectedView).getY()), (ArticleSelectableView) this.selectedView);
            if (k == currentChildPosition || k < 0) {
                return false;
            }
            onNewViewSelected((ArticleSelectableView) this.selectedView, (ArticleSelectableView) this.selectedView, k);
            return true;
        }

        /* access modifiers changed from: protected */
        public boolean canSelect(int newSelection) {
            if (this.startViewPosition == this.endViewPosition && this.startViewChildPosition == this.endViewChildPosition) {
                return TextSelectionHelper.super.canSelect(newSelection);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void jumpToLine(int newSelection, int nextWhitespace, boolean viewChanged, float newYoffset, float oldYoffset, ArticleSelectableView oldSelectedView) {
            if (!viewChanged || oldSelectedView != this.selectedView || oldYoffset != newYoffset) {
                TextSelectionHelper.super.jumpToLine(newSelection, nextWhitespace, viewChanged, newYoffset, oldYoffset, oldSelectedView);
            } else if (this.movingHandleStart) {
                this.selectionStart = newSelection;
            } else {
                this.selectionEnd = newSelection;
            }
        }

        /* access modifiers changed from: protected */
        public boolean canShowActions() {
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            if (linearLayoutManager == null) {
                return true;
            }
            int firstV = linearLayoutManager.findFirstVisibleItemPosition();
            int lastV = this.layoutManager.findLastVisibleItemPosition();
            int i = this.startViewPosition;
            if ((firstV >= i && firstV <= this.endViewPosition) || (lastV >= i && lastV <= this.endViewPosition)) {
                return true;
            }
            if (i < firstV || this.endViewPosition > lastV) {
                return false;
            }
            return true;
        }
    }

    public interface TextLayoutBlock {
        StaticLayout getLayout();

        CharSequence getPrefix();

        int getRow();

        int getX();

        int getY();

        /* renamed from: org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static CharSequence $default$getPrefix(TextLayoutBlock _this) {
                return null;
            }
        }
    }

    private static class PathCopyTo extends Path {
        private Path destination;

        public PathCopyTo(Path destination2) {
            this.destination = destination2;
        }

        public void reset() {
            super.reset();
        }

        public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
            this.destination.addRect(left, top, right, bottom, dir);
        }
    }

    private static class PathWithSavedBottom extends Path {
        float lastBottom;

        private PathWithSavedBottom() {
            this.lastBottom = 0.0f;
        }

        public void reset() {
            super.reset();
            this.lastBottom = 0.0f;
        }

        public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
            super.addRect(left, top, right, bottom, dir);
            if (bottom > this.lastBottom) {
                this.lastBottom = bottom;
            }
        }
    }

    private static class ScalablePath extends Path {
        float lastBottom;
        private ArrayList<RectF> rects;
        private int rectsCount;

        private ScalablePath() {
            this.lastBottom = 0.0f;
            this.rects = new ArrayList<>(1);
            this.rectsCount = 0;
        }

        public void reset() {
            super.reset();
            this.rects.clear();
            this.rectsCount = 0;
            this.lastBottom = 0.0f;
        }

        public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
            this.rects.add(new RectF(left, top, right, bottom));
            this.rectsCount++;
            super.addRect(left, top, right, bottom, dir);
            if (bottom > this.lastBottom) {
                this.lastBottom = bottom;
            }
        }

        public void scaleY(float sy, float cy, Path copyTo) {
            if (copyTo != null) {
                for (int i = 0; i < this.rectsCount; i++) {
                    RectF rect = this.rects.get(i);
                    copyTo.addRect(rect.left, ((rect.top - cy) * sy) + cy, rect.right, ((rect.bottom - cy) * sy) + cy, Path.Direction.CW);
                }
                return;
            }
            super.reset();
            for (int i2 = 0; i2 < this.rectsCount; i2++) {
                RectF rect2 = this.rects.get(i2);
                super.addRect(rect2.left, ((rect2.top - cy) * sy) + cy, rect2.right, ((rect2.bottom - cy) * sy) + cy, Path.Direction.CW);
            }
        }
    }

    public void setKeyboardSize(int keyboardSize2) {
        this.keyboardSize = keyboardSize2;
        invalidate();
    }

    public int getParentTopPadding() {
        return 0;
    }

    public int getParentBottomPadding() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String key) {
        return Theme.getColor(key);
    }

    /* access modifiers changed from: protected */
    public Theme.ResourcesProvider getResourcesProvider() {
        return null;
    }
}
