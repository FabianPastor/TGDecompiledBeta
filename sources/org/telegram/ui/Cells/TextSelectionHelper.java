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
    public RectF endArea;
    protected float enterProgress;
    protected float handleViewProgress;
    /* access modifiers changed from: private */
    public final Runnable hideActionsRunnable;
    /* access modifiers changed from: private */
    public Interpolator interpolator;
    /* access modifiers changed from: private */
    public boolean isOneTouch;
    int keyboardSize;
    /* access modifiers changed from: private */
    public int lastX;
    /* access modifiers changed from: private */
    public int lastY;
    protected final LayoutBlock layoutBlock;
    private int longpressDelay;
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
    public OnTranslateListener onTranslateListener;
    /* access modifiers changed from: private */
    public boolean parentIsScrolling;
    protected RecyclerListView parentRecyclerView;
    protected ViewGroup parentView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    /* access modifiers changed from: private */
    public boolean scrollDown;
    /* access modifiers changed from: private */
    public Runnable scrollRunnable;
    /* access modifiers changed from: private */
    public boolean scrolling;
    protected int selectedCellId;
    protected Cell selectedView;
    protected int selectionEnd;
    protected Paint selectionHandlePaint;
    protected Paint selectionPaint;
    protected Path selectionPath;
    protected PathCopyTo selectionPathMirror;
    protected int selectionStart;
    protected boolean showActionsAsPopupAlways;
    /* access modifiers changed from: private */
    public boolean snap;
    /* access modifiers changed from: private */
    public RectF startArea;
    final Runnable startSelectionRunnable;
    protected Path tempPath;
    protected final Rect textArea;
    private final ActionMode.Callback textSelectActionCallback;
    protected TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay;
    protected int textX;
    protected int textY;
    protected int[] tmpCoord = new int[2];
    /* access modifiers changed from: private */
    public int topOffset;
    /* access modifiers changed from: private */
    public int touchSlop;
    /* access modifiers changed from: private */
    public boolean tryCapture;

    public interface ArticleSelectableView extends SelectableView {
        void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList);
    }

    public static class Callback {
        public void onStateChanged(boolean z) {
            throw null;
        }

        public void onTextCopied() {
        }
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

    public interface TextLayoutBlock {
        StaticLayout getLayout();

        CharSequence getPrefix();

        int getRow();

        int getX();

        int getY();
    }

    /* access modifiers changed from: protected */
    public abstract void fillLayoutForOffset(int i, LayoutBlock layoutBlock2, boolean z);

    /* access modifiers changed from: protected */
    public abstract int getCharOffsetFromCord(int i, int i2, int i3, int i4, Cell cell, boolean z);

    /* access modifiers changed from: protected */
    public abstract int getLineHeight();

    public int getParentBottomPadding() {
        return 0;
    }

    public int getParentTopPadding() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public Theme.ResourcesProvider getResourcesProvider() {
        return null;
    }

    /* access modifiers changed from: protected */
    public abstract CharSequence getText(Cell cell, boolean z);

    /* access modifiers changed from: protected */
    public void onExitSelectionMode(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onOffsetChanged() {
    }

    /* access modifiers changed from: protected */
    public abstract void onTextSelected(Cell cell, Cell cell2);

    /* access modifiers changed from: protected */
    public void pickEndView() {
    }

    /* access modifiers changed from: protected */
    public void pickStartView() {
    }

    /* access modifiers changed from: protected */
    public boolean selectLayout(int i, int i2) {
        return false;
    }

    public TextSelectionHelper() {
        new PathWithSavedBottom();
        this.selectionPaint = new Paint();
        this.selectionHandlePaint = new Paint();
        this.selectionPath = new Path();
        this.selectionPathMirror = new PathCopyTo(this.selectionPath);
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.textSelectActionCallback = createActionCallback();
        this.textArea = new Rect();
        this.startArea = new RectF();
        this.endArea = new RectF();
        this.layoutBlock = new LayoutBlock();
        this.interpolator = new OvershootInterpolator();
        this.showActionsAsPopupAlways = false;
        this.scrollRunnable = new Runnable() {
            public void run() {
                int i;
                int i2;
                int i3;
                if (TextSelectionHelper.this.scrolling) {
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    if (textSelectionHelper.parentRecyclerView != null) {
                        if (textSelectionHelper.multiselect && textSelectionHelper.selectedView == null) {
                            i = AndroidUtilities.dp(8.0f);
                        } else if (textSelectionHelper.selectedView != null) {
                            i = textSelectionHelper.getLineHeight() >> 1;
                        } else {
                            return;
                        }
                        TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                        if (!textSelectionHelper2.multiselect) {
                            if (textSelectionHelper2.scrollDown) {
                                if (TextSelectionHelper.this.selectedView.getBottom() - i < TextSelectionHelper.this.parentView.getMeasuredHeight() - TextSelectionHelper.this.getParentBottomPadding()) {
                                    i3 = TextSelectionHelper.this.selectedView.getBottom() - TextSelectionHelper.this.parentView.getMeasuredHeight();
                                    i2 = TextSelectionHelper.this.getParentBottomPadding();
                                }
                            } else if (TextSelectionHelper.this.selectedView.getTop() + i > TextSelectionHelper.this.getParentTopPadding()) {
                                i3 = -TextSelectionHelper.this.selectedView.getTop();
                                i2 = TextSelectionHelper.this.getParentTopPadding();
                            }
                            i = i3 + i2;
                        }
                        TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                        RecyclerListView recyclerListView = textSelectionHelper3.parentRecyclerView;
                        if (!textSelectionHelper3.scrollDown) {
                            i = -i;
                        }
                        recyclerListView.scrollBy(0, i);
                        AndroidUtilities.runOnUIThread(this);
                    }
                }
            }
        };
        this.startSelectionRunnable = new Runnable() {
            public void run() {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                Cell cell = textSelectionHelper.maybeSelectedView;
                if (cell != null && textSelectionHelper.textSelectionOverlay != null) {
                    Cell cell2 = textSelectionHelper.selectedView;
                    CharSequence text = textSelectionHelper.getText(cell, true);
                    RecyclerListView recyclerListView = TextSelectionHelper.this.parentRecyclerView;
                    if (recyclerListView != null) {
                        recyclerListView.cancelClickRunnables(false);
                    }
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    int i = textSelectionHelper2.capturedX;
                    int i2 = textSelectionHelper2.capturedY;
                    if (!textSelectionHelper2.textArea.isEmpty()) {
                        Rect rect = TextSelectionHelper.this.textArea;
                        int i3 = rect.right;
                        if (i > i3) {
                            i = i3 - 1;
                        }
                        int i4 = rect.left;
                        if (i < i4) {
                            i = i4 + 1;
                        }
                        int i5 = rect.top;
                        if (i2 < i5) {
                            i2 = i5 + 1;
                        }
                        int i6 = rect.bottom;
                        if (i2 > i6) {
                            i2 = i6 - 1;
                        }
                    }
                    int i7 = i;
                    TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                    int charOffsetFromCord = textSelectionHelper3.getCharOffsetFromCord(i7, i2, textSelectionHelper3.maybeTextX, textSelectionHelper3.maybeTextY, cell, true);
                    if (charOffsetFromCord >= text.length()) {
                        TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                        textSelectionHelper4.fillLayoutForOffset(charOffsetFromCord, textSelectionHelper4.layoutBlock, true);
                        TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                        StaticLayout staticLayout = textSelectionHelper5.layoutBlock.layout;
                        if (staticLayout == null) {
                            textSelectionHelper5.selectionEnd = -1;
                            textSelectionHelper5.selectionStart = -1;
                            return;
                        }
                        int lineCount = staticLayout.getLineCount() - 1;
                        TextSelectionHelper textSelectionHelper6 = TextSelectionHelper.this;
                        float f = (float) (i7 - textSelectionHelper6.maybeTextX);
                        if (f < textSelectionHelper6.layoutBlock.layout.getLineRight(lineCount) + ((float) AndroidUtilities.dp(4.0f)) && f > TextSelectionHelper.this.layoutBlock.layout.getLineLeft(lineCount)) {
                            charOffsetFromCord = text.length() - 1;
                        }
                    }
                    if (charOffsetFromCord >= 0 && charOffsetFromCord < text.length() && text.charAt(charOffsetFromCord) != 10) {
                        TextSelectionHelper textSelectionHelper7 = TextSelectionHelper.this;
                        int i8 = textSelectionHelper7.maybeTextX;
                        int i9 = textSelectionHelper7.maybeTextY;
                        textSelectionHelper7.clear();
                        TextSelectionHelper.this.textSelectionOverlay.setVisibility(0);
                        TextSelectionHelper.this.onTextSelected(cell, cell2);
                        TextSelectionHelper textSelectionHelper8 = TextSelectionHelper.this;
                        textSelectionHelper8.selectionStart = charOffsetFromCord;
                        textSelectionHelper8.selectionEnd = charOffsetFromCord;
                        if (text instanceof Spanned) {
                            Spanned spanned = (Spanned) text;
                            Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spanned.getSpans(0, text.length(), Emoji.EmojiSpan.class);
                            int length = emojiSpanArr.length;
                            int i10 = 0;
                            while (true) {
                                if (i10 >= length) {
                                    break;
                                }
                                Emoji.EmojiSpan emojiSpan = emojiSpanArr[i10];
                                int spanStart = spanned.getSpanStart(emojiSpan);
                                int spanEnd = spanned.getSpanEnd(emojiSpan);
                                if (charOffsetFromCord >= spanStart && charOffsetFromCord <= spanEnd) {
                                    TextSelectionHelper textSelectionHelper9 = TextSelectionHelper.this;
                                    textSelectionHelper9.selectionStart = spanStart;
                                    textSelectionHelper9.selectionEnd = spanEnd;
                                    break;
                                }
                                i10++;
                            }
                        }
                        TextSelectionHelper textSelectionHelper10 = TextSelectionHelper.this;
                        if (textSelectionHelper10.selectionStart == textSelectionHelper10.selectionEnd) {
                            while (true) {
                                int i11 = TextSelectionHelper.this.selectionStart;
                                if (i11 > 0 && TextSelectionHelper.isInterruptedCharacter(text.charAt(i11 - 1))) {
                                    TextSelectionHelper.this.selectionStart--;
                                }
                            }
                            while (TextSelectionHelper.this.selectionEnd < text.length() && TextSelectionHelper.isInterruptedCharacter(text.charAt(TextSelectionHelper.this.selectionEnd))) {
                                TextSelectionHelper.this.selectionEnd++;
                            }
                        }
                        TextSelectionHelper textSelectionHelper11 = TextSelectionHelper.this;
                        textSelectionHelper11.textX = i8;
                        textSelectionHelper11.textY = i9;
                        textSelectionHelper11.selectedView = cell;
                        textSelectionHelper11.textSelectionOverlay.performHapticFeedback(0);
                        TextSelectionHelper.this.showActions();
                        TextSelectionHelper.this.invalidate();
                        if (cell2 != null) {
                            cell2.invalidate();
                        }
                        if (TextSelectionHelper.this.callback != null) {
                            TextSelectionHelper.this.callback.onStateChanged(true);
                        }
                        boolean unused = TextSelectionHelper.this.movingHandle = true;
                        TextSelectionHelper textSelectionHelper12 = TextSelectionHelper.this;
                        textSelectionHelper12.movingDirectionSettling = true;
                        boolean unused2 = textSelectionHelper12.isOneTouch = true;
                        TextSelectionHelper textSelectionHelper13 = TextSelectionHelper.this;
                        textSelectionHelper13.movingOffsetY = 0.0f;
                        textSelectionHelper13.movingOffsetX = 0.0f;
                        textSelectionHelper13.onOffsetChanged();
                    }
                    boolean unused3 = TextSelectionHelper.this.tryCapture = false;
                }
            }
        };
        this.onTranslateListener = null;
        this.hideActionsRunnable = new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= 23 && TextSelectionHelper.this.actionMode != null) {
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    if (!textSelectionHelper.actionsIsShowing) {
                        textSelectionHelper.actionMode.hide(Long.MAX_VALUE);
                        AndroidUtilities.runOnUIThread(TextSelectionHelper.this.hideActionsRunnable, 1000);
                    }
                }
            }
        };
        this.tempPath = new Path();
        this.longpressDelay = ViewConfiguration.getLongPressTimeout();
        this.touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
        Paint paint = this.selectionPaint;
        float dp = (float) AndroidUtilities.dp(6.0f);
        this.cornerRadius = dp;
        paint.setPathEffect(new CornerPathEffect(dp));
    }

    public void setOnTranslate(OnTranslateListener onTranslateListener2) {
        this.onTranslateListener = onTranslateListener2;
    }

    public void setParentView(ViewGroup viewGroup) {
        if (viewGroup instanceof RecyclerListView) {
            this.parentRecyclerView = (RecyclerListView) viewGroup;
        }
        this.parentView = viewGroup;
    }

    public void setMaybeTextCord(int i, int i2) {
        this.maybeTextX = i;
        this.maybeTextY = i2;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action == 2) {
                    int y = (int) motionEvent.getY();
                    int x = (int) motionEvent.getX();
                    int i = this.capturedY;
                    int i2 = this.capturedX;
                    if (((i - y) * (i - y)) + ((i2 - x) * (i2 - x)) > this.touchSlop) {
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
            int i3 = this.capturedX;
            int i4 = this.capturedY;
            Rect rect = this.textArea;
            int i5 = rect.right;
            if (i3 > i5) {
                i3 = i5 - 1;
            }
            int i6 = rect.left;
            if (i3 < i6) {
                i3 = i6 + 1;
            }
            int i7 = rect.top;
            if (i4 < i7) {
                i4 = i7 + 1;
            }
            int i8 = rect.bottom;
            int charOffsetFromCord = getCharOffsetFromCord(i3, i4 > i8 ? i8 - 1 : i4, this.maybeTextX, this.maybeTextY, this.maybeSelectedView, true);
            CharSequence text = getText(this.maybeSelectedView, true);
            if (charOffsetFromCord >= text.length()) {
                fillLayoutForOffset(charOffsetFromCord, this.layoutBlock, true);
                StaticLayout staticLayout = this.layoutBlock.layout;
                if (staticLayout == null) {
                    this.tryCapture = false;
                    return false;
                }
                int lineCount = staticLayout.getLineCount() - 1;
                float f = (float) (i3 - this.maybeTextX);
                if (f < this.layoutBlock.layout.getLineRight(lineCount) + ((float) AndroidUtilities.dp(4.0f)) && f > this.layoutBlock.layout.getLineLeft(lineCount)) {
                    charOffsetFromCord = text.length() - 1;
                }
            }
            if (charOffsetFromCord >= 0 && charOffsetFromCord < text.length() && text.charAt(charOffsetFromCord) != 10) {
                AndroidUtilities.runOnUIThread(this.startSelectionRunnable, (long) this.longpressDelay);
                this.tryCapture = true;
            }
        }
        return this.tryCapture;
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
    public void showMagnifier(int i) {
        int i2;
        int i3;
        if (Build.VERSION.SDK_INT >= 28 && this.selectedView != null && !this.isOneTouch && this.movingHandle && this.textSelectionOverlay != null) {
            int i4 = this.movingHandleStart ? this.selectionStart : this.selectionEnd;
            fillLayoutForOffset(i4, this.layoutBlock);
            StaticLayout staticLayout = this.layoutBlock.layout;
            if (staticLayout != null) {
                int lineForOffset = staticLayout.getLineForOffset(i4);
                int lineBottom = staticLayout.getLineBottom(lineForOffset) - staticLayout.getLineTop(lineForOffset);
                float lineTop = (float) ((int) (((float) ((((int) (((float) (staticLayout.getLineTop(lineForOffset) + this.textY)) + this.selectedView.getY())) - lineBottom) - AndroidUtilities.dp(8.0f))) + this.layoutBlock.yOffset));
                if (this.magnifierY != lineTop) {
                    this.magnifierY = lineTop;
                    this.magnifierDy = (lineTop - this.magnifierYanimated) / 200.0f;
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
                    i3 = (int) cell.getX();
                    i2 = ((int) this.selectedView.getX()) + this.selectedView.getMeasuredWidth();
                } else {
                    i2 = (int) (this.selectedView.getX() + ((float) this.textX) + staticLayout.getLineRight(lineForOffset));
                    i3 = (int) (cell.getX() + ((float) this.textX) + staticLayout.getLineLeft(lineForOffset));
                }
                if (i < i3) {
                    i = i3;
                } else if (i > i2) {
                    i = i2;
                }
                this.magnifier.show((float) i, this.magnifierYanimated + (((float) lineBottom) * 1.5f) + ((float) AndroidUtilities.dp(8.0f)));
                this.magnifier.update();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void showHandleViews() {
        if (this.handleViewProgress != 1.0f && this.textSelectionOverlay != null) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new TextSelectionHelper$$ExternalSyntheticLambda0(this));
            ofFloat.setDuration(250);
            ofFloat.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showHandleViews$0(ValueAnimator valueAnimator) {
        this.handleViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.textSelectionOverlay.invalidate();
    }

    public boolean isSelectionMode() {
        return this.selectionStart >= 0 && this.selectionEnd >= 0;
    }

    /* access modifiers changed from: private */
    public void showActions() {
        int i;
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
                if (this.selectedView == null || (i = (((int) (((float) (offsetToCord(this.selectionStart)[1] + this.textY)) + this.selectedView.getY())) + ((-getLineHeight()) / 2)) - AndroidUtilities.dp(4.0f)) < 0) {
                    i = 0;
                }
                this.popupWindow.showAtLocation(this.textSelectionOverlay, 48, 0, i - AndroidUtilities.dp(48.0f));
                this.popupWindow.startAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$showActions$1(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.popupRect);
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showActions$2(View view) {
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
        return messageObject != null && this.selectedCellId == messageObject.getId();
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

    public void setCallback(Callback callback2) {
        this.callback = callback2;
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

    public void setTopOffset(int i) {
        this.topOffset = i;
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

        public boolean checkOnTap(MotionEvent motionEvent) {
            if (TextSelectionHelper.this.isSelectionMode() && !TextSelectionHelper.this.movingHandle) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.pressedX = motionEvent.getX();
                    this.pressedY = motionEvent.getY();
                    this.pressedTime = System.currentTimeMillis();
                } else if (action == 1 && System.currentTimeMillis() - this.pressedTime < 200 && MathUtils.distance((int) this.pressedX, (int) this.pressedY, (int) motionEvent.getX(), (int) motionEvent.getY()) < ((float) TextSelectionHelper.this.touchSlop)) {
                    TextSelectionHelper.this.hideActions();
                    TextSelectionHelper.this.clear();
                    return true;
                }
            }
            return false;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x004f, code lost:
            if (r4 != 3) goto L_0x0611;
         */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x0120  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0139 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x015d  */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x0172  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0189  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x01b0  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r22) {
            /*
                r21 = this;
                r0 = r21
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.isSelectionMode()
                r2 = 0
                if (r1 != 0) goto L_0x000c
                return r2
            L_0x000c:
                int r1 = r22.getPointerCount()
                r3 = 1
                if (r1 <= r3) goto L_0x001a
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.movingHandle
                return r1
            L_0x001a:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r1 = r1.lastX
                float r1 = (float) r1
                float r4 = r22.getX()
                float r1 = r1 - r4
                int r1 = (int) r1
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                int unused = r4.lastY
                r22.getY()
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r5 = r22.getX()
                int r5 = (int) r5
                int unused = r4.lastX = r5
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r5 = r22.getY()
                int r5 = (int) r5
                int unused = r4.lastY = r5
                int r4 = r22.getAction()
                r5 = 2
                if (r4 == 0) goto L_0x053e
                if (r4 == r3) goto L_0x0503
                if (r4 == r5) goto L_0x0053
                r1 = 3
                if (r4 == r1) goto L_0x0503
                goto L_0x0611
            L_0x0053:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r4 = r4.movingHandle
                if (r4 == 0) goto L_0x0611
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r5 = r4.movingHandleStart
                if (r5 == 0) goto L_0x0065
                r4.pickStartView()
                goto L_0x0068
            L_0x0065:
                r4.pickEndView()
            L_0x0068:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r5 = r4.selectedView
                if (r5 != 0) goto L_0x0073
                boolean r1 = r4.movingHandle
                return r1
            L_0x0073:
                float r4 = r22.getY()
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r5 = r5.movingOffsetY
                float r4 = r4 + r5
                int r4 = (int) r4
                float r5 = r22.getX()
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r7 = r6.movingOffsetX
                float r5 = r5 + r7
                int r5 = (int) r5
                boolean r10 = r6.selectLayout(r5, r4)
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r7 = r6.selectedView
                if (r7 != 0) goto L_0x0092
                return r3
            L_0x0092:
                boolean r7 = r6.movingHandleStart
                if (r7 == 0) goto L_0x009e
                int r7 = r6.selectionStart
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r8 = r6.layoutBlock
                r6.fillLayoutForOffset(r7, r8)
                goto L_0x00a5
            L_0x009e:
                int r7 = r6.selectionEnd
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r8 = r6.layoutBlock
                r6.fillLayoutForOffset(r7, r8)
            L_0x00a5:
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r7 = r6.layoutBlock
                android.text.StaticLayout r8 = r7.layout
                if (r8 != 0) goto L_0x00ae
                return r3
            L_0x00ae:
                float r12 = r7.yOffset
                Cell r13 = r6.selectedView
                int r6 = r13.getTop()
                int r4 = r4 - r6
                float r5 = (float) r5
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r6 = r6.selectedView
                float r6 = r6.getX()
                float r5 = r5 - r6
                int r15 = (int) r5
                float r5 = r22.getY()
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r6 = r6.touchSlop
                float r6 = (float) r6
                float r5 = r5 - r6
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r6 = r6.parentView
                int r6 = r6.getMeasuredHeight()
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r7 = r7.getParentBottomPadding()
                int r6 = r6 - r7
                float r6 = (float) r6
                int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                if (r5 <= 0) goto L_0x0101
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r6 = r5.multiselect
                if (r6 != 0) goto L_0x00ff
                Cell r5 = r5.selectedView
                int r5 = r5.getBottom()
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r6 = r6.parentView
                int r6 = r6.getMeasuredHeight()
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r7 = r7.getParentBottomPadding()
                int r6 = r6 - r7
                if (r5 <= r6) goto L_0x0101
            L_0x00ff:
                r5 = 1
                goto L_0x0102
            L_0x0101:
                r5 = 0
            L_0x0102:
                float r6 = r22.getY()
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r7 = r7.parentView
                android.view.ViewParent r7 = r7.getParent()
                android.view.View r7 = (android.view.View) r7
                int r7 = r7.getTop()
                org.telegram.ui.Cells.TextSelectionHelper r8 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r8 = r8.getParentTopPadding()
                int r7 = r7 + r8
                float r7 = (float) r7
                int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                if (r6 >= 0) goto L_0x0136
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r7 = r6.multiselect
                if (r7 != 0) goto L_0x0134
                Cell r6 = r6.selectedView
                int r6 = r6.getTop()
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r7 = r7.getParentTopPadding()
                if (r6 >= r7) goto L_0x0136
            L_0x0134:
                r6 = 1
                goto L_0x0137
            L_0x0136:
                r6 = 0
            L_0x0137:
                if (r5 != 0) goto L_0x0155
                if (r6 == 0) goto L_0x013c
                goto L_0x0155
            L_0x013c:
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r5 = r5.scrolling
                if (r5 == 0) goto L_0x0152
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r5.scrolling = r2
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                java.lang.Runnable r5 = r5.scrollRunnable
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5)
            L_0x0152:
                r16 = r4
                goto L_0x019a
            L_0x0155:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r4 = r4.scrolling
                if (r4 != 0) goto L_0x016b
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r4.scrolling = r3
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                java.lang.Runnable r4 = r4.scrollRunnable
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            L_0x016b:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r4.scrollDown = r5
                if (r5 == 0) goto L_0x0189
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.view.ViewGroup r4 = r4.parentView
                int r4 = r4.getMeasuredHeight()
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r5 = r5.selectedView
                int r5 = r5.getTop()
                int r4 = r4 - r5
                float r4 = (float) r4
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r5 = r5.movingOffsetY
                goto L_0x0197
            L_0x0189:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r4 = r4.selectedView
                int r4 = r4.getTop()
                int r4 = -r4
                float r4 = (float) r4
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                float r5 = r5.movingOffsetY
            L_0x0197:
                float r4 = r4 + r5
                int r4 = (int) r4
                goto L_0x0152
            L_0x019a:
                org.telegram.ui.Cells.TextSelectionHelper r14 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r14.textX
                int r5 = r14.textY
                Cell r6 = r14.selectedView
                r20 = 0
                r17 = r4
                r18 = r5
                r19 = r6
                int r8 = r14.getCharOffsetFromCord(r15, r16, r17, r18, r19, r20)
                if (r8 < 0) goto L_0x04f8
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r5 = r4.movingDirectionSettling
                if (r5 == 0) goto L_0x01d2
                if (r10 == 0) goto L_0x01b9
                return r3
            L_0x01b9:
                int r5 = r4.selectionStart
                if (r8 >= r5) goto L_0x01c5
                r4.movingDirectionSettling = r2
                r4.movingHandleStart = r3
                r4.hideActions()
                goto L_0x01d2
            L_0x01c5:
                int r5 = r4.selectionEnd
                if (r8 <= r5) goto L_0x01d1
                r4.movingDirectionSettling = r2
                r4.movingHandleStart = r2
                r4.hideActions()
                goto L_0x01d2
            L_0x01d1:
                return r3
            L_0x01d2:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r5 = r4.movingHandleStart
                r6 = -1
                if (r5 == 0) goto L_0x0386
                int r5 = r4.selectionStart
                if (r5 == r8) goto L_0x04f3
                boolean r4 = r4.canSelect(r8)
                if (r4 == 0) goto L_0x04f3
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r5 = r4.selectedView
                java.lang.CharSequence r4 = r4.getText(r5, r2)
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r7 = r5.layoutBlock
                r5.fillLayoutForOffset(r8, r7)
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r7 = r5.layoutBlock
                android.text.StaticLayout r9 = r7.layout
                int r11 = r5.selectionStart
                r5.fillLayoutForOffset(r11, r7)
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r5 = r5.layoutBlock
                android.text.StaticLayout r5 = r5.layout
                if (r9 == 0) goto L_0x0385
                if (r5 != 0) goto L_0x0209
                goto L_0x0385
            L_0x0209:
                r11 = r8
            L_0x020a:
                int r7 = r11 + -1
                if (r7 < 0) goto L_0x021b
                char r7 = r4.charAt(r7)
                boolean r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7)
                if (r7 == 0) goto L_0x021b
                int r11 = r11 + -1
                goto L_0x020a
            L_0x021b:
                int r7 = r5.getLineForOffset(r11)
                org.telegram.ui.Cells.TextSelectionHelper r14 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r14 = r14.selectionStart
                int r14 = r5.getLineForOffset(r14)
                int r15 = r5.getLineForOffset(r8)
                if (r10 != 0) goto L_0x0364
                if (r9 != r5) goto L_0x0364
                org.telegram.ui.Cells.TextSelectionHelper r9 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r9 = r9.selectionStart
                int r9 = r5.getLineForOffset(r9)
                if (r15 == r9) goto L_0x023d
                if (r15 != r7) goto L_0x023d
                goto L_0x0364
            L_0x023d:
                int r9 = r5.getLineForOffset(r8)
                int r9 = r5.getParagraphDirection(r9)
                if (r6 == r9) goto L_0x0342
                boolean r5 = r5.isRtlCharAt(r8)
                if (r5 != 0) goto L_0x0342
                if (r7 != r14) goto L_0x0342
                if (r15 == r7) goto L_0x0253
                goto L_0x0342
            L_0x0253:
                r5 = r8
            L_0x0254:
                int r6 = r5 + 1
                int r7 = r4.length()
                if (r6 >= r7) goto L_0x0268
                char r7 = r4.charAt(r6)
                boolean r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7)
                if (r7 == 0) goto L_0x0268
                r5 = r6
                goto L_0x0254
            L_0x0268:
                int r6 = r8 - r11
                int r6 = java.lang.Math.abs(r6)
                int r5 = r8 - r5
                int r5 = java.lang.Math.abs(r5)
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r7 = r7.snap
                if (r7 == 0) goto L_0x0286
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                if (r1 < 0) goto L_0x0282
                r9 = 1
                goto L_0x0283
            L_0x0282:
                r9 = 0
            L_0x0283:
                boolean unused = r7.snap = r9
            L_0x0286:
                int r7 = r8 + -1
                if (r7 <= 0) goto L_0x0296
                char r7 = r4.charAt(r7)
                boolean r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7)
                if (r7 == 0) goto L_0x0296
                r7 = 1
                goto L_0x0297
            L_0x0296:
                r7 = 0
            L_0x0297:
                int r9 = r4.length()
                r10 = 10
                if (r8 < r9) goto L_0x02a6
                int r8 = r4.length()
                r9 = 10
                goto L_0x02aa
            L_0x02a6:
                char r9 = r4.charAt(r8)
            L_0x02aa:
                org.telegram.ui.Cells.TextSelectionHelper r12 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r12 = r12.selectionStart
                int r13 = r4.length()
                if (r12 < r13) goto L_0x02bf
                org.telegram.ui.Cells.TextSelectionHelper r12 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r4.length()
                r12.selectionStart = r4
                r4 = 10
                goto L_0x02c7
            L_0x02bf:
                org.telegram.ui.Cells.TextSelectionHelper r12 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r12 = r12.selectionStart
                char r4 = r4.charAt(r12)
            L_0x02c7:
                org.telegram.ui.Cells.TextSelectionHelper r12 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r12 = r12.selectionStart
                if (r8 >= r12) goto L_0x02cf
                if (r6 < r5) goto L_0x02ed
            L_0x02cf:
                if (r8 <= r12) goto L_0x02d3
                if (r1 < 0) goto L_0x02ed
            L_0x02d3:
                boolean r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r9)
                if (r1 == 0) goto L_0x02ed
                boolean r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4)
                if (r1 == 0) goto L_0x02e7
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.snap
                if (r1 == 0) goto L_0x02ed
            L_0x02e7:
                if (r8 == 0) goto L_0x02ed
                if (r7 == 0) goto L_0x02ed
                if (r4 != r10) goto L_0x04f3
            L_0x02ed:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.snap
                if (r1 == 0) goto L_0x02f8
                if (r8 != r3) goto L_0x02f8
                return r3
            L_0x02f8:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r1 = r1.selectionStart
                if (r8 >= r1) goto L_0x031c
                boolean r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r9)
                if (r1 == 0) goto L_0x031c
                boolean r1 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4)
                if (r1 == 0) goto L_0x0312
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.snap
                if (r1 == 0) goto L_0x031c
            L_0x0312:
                if (r4 == r10) goto L_0x031c
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionStart = r11
                boolean unused = r1.snap = r3
                goto L_0x0320
            L_0x031c:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionStart = r8
            L_0x0320:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r3 = r1.selectionStart
                int r4 = r1.selectionEnd
                if (r3 <= r4) goto L_0x032e
                r1.selectionEnd = r3
                r1.selectionStart = r4
                r1.movingHandleStart = r2
            L_0x032e:
                int r2 = android.os.Build.VERSION.SDK_INT
                r3 = 27
                if (r2 < r3) goto L_0x033b
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r2 = 9
                r1.performHapticFeedback(r2)
            L_0x033b:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x04f3
            L_0x0342:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionStart = r8
                int r3 = r1.selectionEnd
                if (r8 <= r3) goto L_0x0350
                r1.selectionEnd = r8
                r1.selectionStart = r3
                r1.movingHandleStart = r2
            L_0x0350:
                int r2 = android.os.Build.VERSION.SDK_INT
                r3 = 27
                if (r2 < r3) goto L_0x035d
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r2 = 9
                r1.performHapticFeedback(r2)
            L_0x035d:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x04f3
            L_0x0364:
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r1 = r7.layoutBlock
                float r1 = r1.yOffset
                r9 = r11
                r11 = r1
                r7.jumpToLine(r8, r9, r10, r11, r12, r13)
                int r1 = android.os.Build.VERSION.SDK_INT
                r2 = 27
                if (r1 < r2) goto L_0x037e
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r2 = 9
                r1.performHapticFeedback(r2)
            L_0x037e:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x04f3
            L_0x0385:
                return r3
            L_0x0386:
                int r5 = r4.selectionEnd
                if (r8 == r5) goto L_0x04f3
                boolean r4 = r4.canSelect(r8)
                if (r4 == 0) goto L_0x04f3
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r5 = r4.selectedView
                java.lang.CharSequence r4 = r4.getText(r5, r2)
                r9 = r8
            L_0x0399:
                int r5 = r4.length()
                if (r9 >= r5) goto L_0x03ac
                char r5 = r4.charAt(r9)
                boolean r5 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r5)
                if (r5 == 0) goto L_0x03ac
                int r9 = r9 + 1
                goto L_0x0399
            L_0x03ac:
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r7 = r5.layoutBlock
                r5.fillLayoutForOffset(r8, r7)
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r7 = r5.layoutBlock
                android.text.StaticLayout r11 = r7.layout
                int r14 = r5.selectionEnd
                r5.fillLayoutForOffset(r14, r7)
                org.telegram.ui.Cells.TextSelectionHelper r5 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r5 = r5.layoutBlock
                android.text.StaticLayout r5 = r5.layout
                if (r11 == 0) goto L_0x04f2
                if (r5 != 0) goto L_0x03ca
                goto L_0x04f2
            L_0x03ca:
                int r7 = r4.length()
                if (r8 <= r7) goto L_0x03d5
                int r7 = r4.length()
                r8 = r7
            L_0x03d5:
                int r7 = r5.getLineForOffset(r9)
                org.telegram.ui.Cells.TextSelectionHelper r14 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r14 = r14.selectionEnd
                int r14 = r5.getLineForOffset(r14)
                int r15 = r5.getLineForOffset(r8)
                if (r10 != 0) goto L_0x04d4
                if (r11 != r5) goto L_0x04d4
                org.telegram.ui.Cells.TextSelectionHelper r11 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r11 = r11.selectionEnd
                int r11 = r5.getLineForOffset(r11)
                if (r15 == r11) goto L_0x03f7
                if (r15 != r7) goto L_0x03f7
                goto L_0x04d4
            L_0x03f7:
                int r10 = r5.getLineForOffset(r8)
                int r10 = r5.getParagraphDirection(r10)
                if (r6 == r10) goto L_0x04b3
                boolean r5 = r5.isRtlCharAt(r8)
                if (r5 != 0) goto L_0x04b3
                if (r14 != r7) goto L_0x04b3
                if (r15 == r7) goto L_0x040d
                goto L_0x04b3
            L_0x040d:
                r5 = r8
            L_0x040e:
                int r6 = r5 + -1
                if (r6 < 0) goto L_0x041f
                char r6 = r4.charAt(r6)
                boolean r6 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r6)
                if (r6 == 0) goto L_0x041f
                int r5 = r5 + -1
                goto L_0x040e
            L_0x041f:
                int r6 = r8 - r9
                int r6 = java.lang.Math.abs(r6)
                int r5 = r8 - r5
                int r5 = java.lang.Math.abs(r5)
                int r7 = r8 + -1
                if (r7 <= 0) goto L_0x043b
                char r7 = r4.charAt(r7)
                boolean r7 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r7)
                if (r7 == 0) goto L_0x043b
                r7 = 1
                goto L_0x043c
            L_0x043b:
                r7 = 0
            L_0x043c:
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r10 = r10.snap
                if (r10 == 0) goto L_0x044e
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                if (r1 > 0) goto L_0x044a
                r11 = 1
                goto L_0x044b
            L_0x044a:
                r11 = 0
            L_0x044b:
                boolean unused = r10.snap = r11
            L_0x044e:
                org.telegram.ui.Cells.TextSelectionHelper r10 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r10.selectionEnd
                if (r10 <= 0) goto L_0x0460
                int r10 = r10 - r3
                char r4 = r4.charAt(r10)
                boolean r4 = org.telegram.ui.Cells.TextSelectionHelper.isInterruptedCharacter(r4)
                if (r4 == 0) goto L_0x0460
                r2 = 1
            L_0x0460:
                org.telegram.ui.Cells.TextSelectionHelper r4 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r10 = r4.selectionEnd
                if (r8 <= r10) goto L_0x0468
                if (r6 <= r5) goto L_0x0476
            L_0x0468:
                if (r8 >= r10) goto L_0x046c
                if (r1 > 0) goto L_0x0476
            L_0x046c:
                if (r7 == 0) goto L_0x0476
                if (r2 == 0) goto L_0x04f3
                boolean r1 = r4.snap
                if (r1 != 0) goto L_0x04f3
            L_0x0476:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r4 = r1.selectionEnd
                if (r8 <= r4) goto L_0x048e
                if (r7 == 0) goto L_0x048e
                if (r2 == 0) goto L_0x0486
                boolean r1 = r1.snap
                if (r1 == 0) goto L_0x048e
            L_0x0486:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionEnd = r9
                boolean unused = r1.snap = r3
                goto L_0x0492
            L_0x048e:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionEnd = r8
            L_0x0492:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r2 = r1.selectionStart
                int r4 = r1.selectionEnd
                if (r2 <= r4) goto L_0x04a0
                r1.selectionEnd = r2
                r1.selectionStart = r4
                r1.movingHandleStart = r3
            L_0x04a0:
                int r2 = android.os.Build.VERSION.SDK_INT
                r3 = 27
                if (r2 < r3) goto L_0x04ad
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r2 = 9
                r1.performHapticFeedback(r2)
            L_0x04ad:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x04f3
            L_0x04b3:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.selectionEnd = r8
                int r2 = r1.selectionStart
                if (r2 <= r8) goto L_0x04c1
                r1.selectionEnd = r2
                r1.selectionStart = r8
                r1.movingHandleStart = r3
            L_0x04c1:
                int r2 = android.os.Build.VERSION.SDK_INT
                r3 = 27
                if (r2 < r3) goto L_0x04ce
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r2 = 9
                r1.performHapticFeedback(r2)
            L_0x04ce:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x04f3
            L_0x04d4:
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper$LayoutBlock r1 = r7.layoutBlock
                float r11 = r1.yOffset
                r7.jumpToLine(r8, r9, r10, r11, r12, r13)
                int r1 = android.os.Build.VERSION.SDK_INT
                r2 = 27
                if (r1 < r2) goto L_0x04ec
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                org.telegram.ui.Cells.TextSelectionHelper<Cell>$TextSelectionOverlay r1 = r1.textSelectionOverlay
                r2 = 9
                r1.performHapticFeedback(r2)
            L_0x04ec:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.invalidate()
                goto L_0x04f3
            L_0x04f2:
                return r3
            L_0x04f3:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.onOffsetChanged()
            L_0x04f8:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r2 = r1.lastX
                r1.showMagnifier(r2)
                goto L_0x0611
            L_0x0503:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.hideMagnifier()
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r1.movingHandle = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.movingDirectionSettling = r2
                boolean unused = r1.isOneTouch = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.isSelectionMode()
                if (r1 == 0) goto L_0x0526
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.showActions()
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.showHandleViews()
            L_0x0526:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.scrolling
                if (r1 == 0) goto L_0x0611
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r1.scrolling = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                java.lang.Runnable r1 = r1.scrollRunnable
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
                goto L_0x0611
            L_0x053e:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.movingHandle
                if (r1 == 0) goto L_0x0547
                return r3
            L_0x0547:
                float r1 = r22.getX()
                int r1 = (int) r1
                float r4 = r22.getY()
                int r4 = (int) r4
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.graphics.RectF r6 = r6.startArea
                float r1 = (float) r1
                float r7 = (float) r4
                boolean r6 = r6.contains(r1, r7)
                if (r6 == 0) goto L_0x05ab
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6.pickStartView()
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r7 = r6.selectedView
                if (r7 != 0) goto L_0x056b
                return r2
            L_0x056b:
                boolean unused = r6.movingHandle = r3
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6.movingHandleStart = r3
                int r7 = r6.selectionStart
                int[] r6 = r6.offsetToCord(r7)
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r7 = r7.getLineHeight()
                int r7 = r7 / r5
                float r5 = (float) r7
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2 = r6[r2]
                int r8 = r7.textX
                int r2 = r2 + r8
                float r2 = (float) r2
                Cell r8 = r7.selectedView
                float r8 = r8.getX()
                float r2 = r2 + r8
                float r2 = r2 - r1
                r7.movingOffsetX = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2 = r6[r3]
                int r6 = r1.textY
                int r2 = r2 + r6
                Cell r6 = r1.selectedView
                int r6 = r6.getTop()
                int r2 = r2 + r6
                int r2 = r2 - r4
                float r2 = (float) r2
                float r2 = r2 - r5
                r1.movingOffsetY = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.hideActions()
                return r3
            L_0x05ab:
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                android.graphics.RectF r6 = r6.endArea
                boolean r6 = r6.contains(r1, r7)
                if (r6 == 0) goto L_0x060c
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6.pickEndView()
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                Cell r7 = r6.selectedView
                if (r7 != 0) goto L_0x05c3
                return r2
            L_0x05c3:
                boolean unused = r6.movingHandle = r3
                org.telegram.ui.Cells.TextSelectionHelper r6 = org.telegram.ui.Cells.TextSelectionHelper.this
                r6.movingHandleStart = r2
                int r7 = r6.selectionEnd
                int[] r6 = r6.offsetToCord(r7)
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r7 = r7.getLineHeight()
                int r7 = r7 / r5
                float r5 = (float) r7
                org.telegram.ui.Cells.TextSelectionHelper r7 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2 = r6[r2]
                int r8 = r7.textX
                int r2 = r2 + r8
                float r2 = (float) r2
                Cell r8 = r7.selectedView
                float r8 = r8.getX()
                float r2 = r2 + r8
                float r2 = r2 - r1
                r7.movingOffsetX = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r2 = r6[r3]
                int r6 = r1.textY
                int r2 = r2 + r6
                Cell r6 = r1.selectedView
                int r6 = r6.getTop()
                int r2 = r2 + r6
                int r2 = r2 - r4
                float r2 = (float) r2
                float r2 = r2 - r5
                r1.movingOffsetY = r2
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                int r2 = r1.lastX
                r1.showMagnifier(r2)
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                r1.hideActions()
                return r3
            L_0x060c:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean unused = r1.movingHandle = r2
            L_0x0611:
                org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                boolean r1 = r1.movingHandle
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.TextSelectionOverlay.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            Canvas canvas2 = canvas;
            if (TextSelectionHelper.this.isSelectionMode()) {
                int dp = AndroidUtilities.dp(22.0f);
                int access$2100 = TextSelectionHelper.this.topOffset;
                TextSelectionHelper.this.pickEndView();
                if (TextSelectionHelper.this.selectedView != null) {
                    canvas.save();
                    float y = TextSelectionHelper.this.selectedView.getY();
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    float f = y + ((float) textSelectionHelper.textY);
                    float x = textSelectionHelper.selectedView.getX() + ((float) TextSelectionHelper.this.textX);
                    canvas2.translate(x, f);
                    this.handleViewPaint.setColor(TextSelectionHelper.this.getThemedColor("chat_TextSelectionCursor"));
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    int length = textSelectionHelper2.getText(textSelectionHelper2.selectedView, false).length();
                    TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                    int i2 = textSelectionHelper3.selectionEnd;
                    if (i2 >= 0 && i2 <= length) {
                        textSelectionHelper3.fillLayoutForOffset(i2, textSelectionHelper3.layoutBlock);
                        TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                        StaticLayout staticLayout = textSelectionHelper4.layoutBlock.layout;
                        if (staticLayout != null) {
                            int i3 = textSelectionHelper4.selectionEnd;
                            int length2 = staticLayout.getText().length();
                            if (i3 > length2) {
                                i3 = length2;
                            }
                            int lineForOffset = staticLayout.getLineForOffset(i3);
                            float primaryHorizontal = staticLayout.getPrimaryHorizontal(i3);
                            TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                            LayoutBlock layoutBlock = textSelectionHelper5.layoutBlock;
                            float f2 = primaryHorizontal + layoutBlock.xOffset;
                            float lineBottom = (float) ((int) (((float) staticLayout.getLineBottom(lineForOffset)) + layoutBlock.yOffset));
                            float f3 = f + lineBottom;
                            if (f3 <= ((float) (textSelectionHelper5.keyboardSize + access$2100)) || f3 >= ((float) textSelectionHelper5.parentView.getMeasuredHeight())) {
                                TextSelectionHelper.this.endArea.setEmpty();
                            } else if (!staticLayout.isRtlCharAt(TextSelectionHelper.this.selectionEnd)) {
                                canvas.save();
                                canvas2.translate(f2, lineBottom);
                                float interpolation = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                float f4 = (float) dp;
                                float f5 = f4 / 2.0f;
                                canvas2.scale(interpolation, interpolation, f5, f5);
                                this.path.reset();
                                this.path.addCircle(f5, f5, f5, Path.Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, f5, f5, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                float f6 = x + f2;
                                TextSelectionHelper.this.endArea.set(f6, f3 - f4, f6 + f4, f3 + f4);
                                TextSelectionHelper.this.endArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                                i = 1;
                                canvas.restore();
                            } else {
                                canvas.save();
                                float f7 = (float) dp;
                                canvas2.translate(f2 - f7, lineBottom);
                                float interpolation2 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                float f8 = f7 / 2.0f;
                                canvas2.scale(interpolation2, interpolation2, f8, f8);
                                this.path.reset();
                                this.path.addCircle(f8, f8, f8, Path.Direction.CCW);
                                float f9 = f7;
                                this.path.addRect(f8, 0.0f, f7, f8, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                float var_ = x + f2;
                                TextSelectionHelper.this.endArea.set(var_ - f9, f3 - f9, var_, f3 + f9);
                                TextSelectionHelper.this.endArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                            }
                        }
                    }
                    i = 0;
                    canvas.restore();
                } else {
                    i = 0;
                }
                TextSelectionHelper.this.pickStartView();
                if (TextSelectionHelper.this.selectedView != null) {
                    canvas.save();
                    float y2 = TextSelectionHelper.this.selectedView.getY();
                    TextSelectionHelper textSelectionHelper6 = TextSelectionHelper.this;
                    float var_ = y2 + ((float) textSelectionHelper6.textY);
                    float x2 = textSelectionHelper6.selectedView.getX() + ((float) TextSelectionHelper.this.textX);
                    canvas2.translate(x2, var_);
                    TextSelectionHelper textSelectionHelper7 = TextSelectionHelper.this;
                    int length3 = textSelectionHelper7.getText(textSelectionHelper7.selectedView, false).length();
                    TextSelectionHelper textSelectionHelper8 = TextSelectionHelper.this;
                    int i4 = textSelectionHelper8.selectionStart;
                    if (i4 >= 0 && i4 <= length3) {
                        textSelectionHelper8.fillLayoutForOffset(i4, textSelectionHelper8.layoutBlock);
                        TextSelectionHelper textSelectionHelper9 = TextSelectionHelper.this;
                        StaticLayout staticLayout2 = textSelectionHelper9.layoutBlock.layout;
                        if (staticLayout2 != null) {
                            int lineForOffset2 = staticLayout2.getLineForOffset(textSelectionHelper9.selectionStart);
                            float primaryHorizontal2 = staticLayout2.getPrimaryHorizontal(TextSelectionHelper.this.selectionStart);
                            TextSelectionHelper textSelectionHelper10 = TextSelectionHelper.this;
                            LayoutBlock layoutBlock2 = textSelectionHelper10.layoutBlock;
                            float var_ = primaryHorizontal2 + layoutBlock2.xOffset;
                            float lineBottom2 = (float) ((int) (((float) staticLayout2.getLineBottom(lineForOffset2)) + layoutBlock2.yOffset));
                            float var_ = var_ + lineBottom2;
                            if (var_ <= ((float) (access$2100 + textSelectionHelper10.keyboardSize)) || var_ >= ((float) textSelectionHelper10.parentView.getMeasuredHeight())) {
                                if (var_ > 0.0f && var_ - ((float) TextSelectionHelper.this.getLineHeight()) < ((float) TextSelectionHelper.this.parentView.getMeasuredHeight())) {
                                    i++;
                                }
                                TextSelectionHelper.this.startArea.setEmpty();
                            } else if (!staticLayout2.isRtlCharAt(TextSelectionHelper.this.selectionStart)) {
                                canvas.save();
                                float var_ = (float) dp;
                                canvas2.translate(var_ - var_, lineBottom2);
                                float interpolation3 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                float var_ = var_ / 2.0f;
                                canvas2.scale(interpolation3, interpolation3, var_, var_);
                                this.path.reset();
                                this.path.addCircle(var_, var_, var_, Path.Direction.CCW);
                                this.path.addRect(var_, 0.0f, var_, var_, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                float var_ = x2 + var_;
                                TextSelectionHelper.this.startArea.set(var_ - var_, var_ - var_, var_, var_ + var_);
                                TextSelectionHelper.this.startArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                                i++;
                            } else {
                                canvas.save();
                                canvas2.translate(var_, lineBottom2);
                                float interpolation4 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                float var_ = (float) dp;
                                float var_ = var_ / 2.0f;
                                canvas2.scale(interpolation4, interpolation4, var_, var_);
                                this.path.reset();
                                this.path.addCircle(var_, var_, var_, Path.Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, var_, var_, Path.Direction.CCW);
                                canvas2.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                float var_ = x2 + var_;
                                TextSelectionHelper.this.startArea.set(var_, var_ - var_, var_ + var_, var_ + var_);
                                TextSelectionHelper.this.startArea.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(8.0f)));
                            }
                        }
                    }
                    canvas.restore();
                }
                if (i != 0 && TextSelectionHelper.this.movingHandle) {
                    TextSelectionHelper textSelectionHelper11 = TextSelectionHelper.this;
                    if (!textSelectionHelper11.movingHandleStart) {
                        textSelectionHelper11.pickEndView();
                    }
                    TextSelectionHelper textSelectionHelper12 = TextSelectionHelper.this;
                    textSelectionHelper12.showMagnifier(textSelectionHelper12.lastX);
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
    public void jumpToLine(int i, int i2, boolean z, float f, float f2, Cell cell) {
        int i3;
        int i4;
        if (this.movingHandleStart) {
            this.selectionStart = i2;
            if (!z && i2 > (i4 = this.selectionEnd)) {
                this.selectionEnd = i2;
                this.selectionStart = i4;
                this.movingHandleStart = false;
            }
            this.snap = true;
            return;
        }
        this.selectionEnd = i2;
        if (!z && (i3 = this.selectionStart) > i2) {
            this.selectionEnd = i3;
            this.selectionStart = i2;
            this.movingHandleStart = true;
        }
        this.snap = true;
    }

    /* access modifiers changed from: protected */
    public boolean canSelect(int i) {
        return (i == this.selectionStart || i == this.selectionEnd) ? false : true;
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
        final AnonymousClass4 r0 = new ActionMode.Callback() {
            private String translateFromLanguage = null;

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                menu.add(0, 16908321, 0, 17039361);
                menu.add(0, 16908319, 1, 17039373);
                menu.add(0, 3, 2, LocaleController.getString("TranslateMessage", NUM));
                return true;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                Cell cell = textSelectionHelper.selectedView;
                if (cell != null) {
                    CharSequence text = textSelectionHelper.getText(cell, false);
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    if (textSelectionHelper2.multiselect || (textSelectionHelper2.selectionStart <= 0 && textSelectionHelper2.selectionEnd >= text.length() - 1)) {
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

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPrepareActionMode$0(Menu menu, String str) {
                this.translateFromLanguage = str;
                updateTranslateButton(menu);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPrepareActionMode$1(Menu menu, Exception exc) {
                FileLog.e("mlkit: failed to detect language in selection");
                FileLog.e((Throwable) exc);
                this.translateFromLanguage = null;
                updateTranslateButton(menu);
            }

            /* JADX WARNING: Code restructure failed: missing block: B:2:0x0019, code lost:
                r1 = r2.translateFromLanguage;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            private void updateTranslateButton(android.view.Menu r3) {
                /*
                    r2 = this;
                    org.telegram.messenger.LocaleController r0 = org.telegram.messenger.LocaleController.getInstance()
                    java.util.Locale r0 = r0.getCurrentLocale()
                    java.lang.String r0 = r0.getLanguage()
                    r1 = 2
                    android.view.MenuItem r3 = r3.getItem(r1)
                    org.telegram.ui.Cells.TextSelectionHelper r1 = org.telegram.ui.Cells.TextSelectionHelper.this
                    org.telegram.ui.Cells.TextSelectionHelper$OnTranslateListener r1 = r1.onTranslateListener
                    if (r1 == 0) goto L_0x0041
                    java.lang.String r1 = r2.translateFromLanguage
                    if (r1 == 0) goto L_0x0039
                    boolean r0 = r1.equals(r0)
                    if (r0 == 0) goto L_0x002d
                    java.lang.String r0 = r2.translateFromLanguage
                    java.lang.String r1 = "und"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x0039
                L_0x002d:
                    java.util.HashSet r0 = org.telegram.ui.RestrictedLanguagesSelectActivity.getRestrictedLanguages()
                    java.lang.String r1 = r2.translateFromLanguage
                    boolean r0 = r0.contains(r1)
                    if (r0 == 0) goto L_0x003f
                L_0x0039:
                    boolean r0 = org.telegram.messenger.LanguageDetector.hasSupport()
                    if (r0 != 0) goto L_0x0041
                L_0x003f:
                    r0 = 1
                    goto L_0x0042
                L_0x0041:
                    r0 = 0
                L_0x0042:
                    r3.setVisible(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.AnonymousClass4.updateTranslateButton(android.view.Menu):void");
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (!TextSelectionHelper.this.isSelectionMode()) {
                    return true;
                }
                int itemId = menuItem.getItemId();
                if (itemId == 3) {
                    if (TextSelectionHelper.this.onTranslateListener != null) {
                        TextSelectionHelper.this.onTranslateListener.run(TextSelectionHelper.this.getSelectedText(), this.translateFromLanguage, LocaleController.getInstance().getCurrentLocale().getLanguage(), new TextSelectionHelper$4$$ExternalSyntheticLambda0(this));
                    }
                    TextSelectionHelper.this.hideActions();
                    return true;
                } else if (itemId == 16908319) {
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    CharSequence text = textSelectionHelper.getText(textSelectionHelper.selectedView, false);
                    if (text == null) {
                        return true;
                    }
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    textSelectionHelper2.selectionStart = 0;
                    textSelectionHelper2.selectionEnd = text.length();
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

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onActionItemClicked$2() {
                TextSelectionHelper.this.showActions();
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                if (Build.VERSION.SDK_INT < 23) {
                    TextSelectionHelper.this.clear();
                }
            }
        };
        return Build.VERSION.SDK_INT >= 23 ? new ActionMode.Callback2() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return r0.onCreateActionMode(actionMode, menu);
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return r0.onPrepareActionMode(actionMode, menu);
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return r0.onActionItemClicked(actionMode, menuItem);
            }

            public void onDestroyActionMode(ActionMode actionMode) {
                r0.onDestroyActionMode(actionMode);
            }

            public void onGetContentRect(ActionMode actionMode, View view, Rect rect) {
                int i;
                if (TextSelectionHelper.this.isSelectionMode()) {
                    TextSelectionHelper.this.pickStartView();
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    int i2 = 1;
                    if (textSelectionHelper.selectedView != null) {
                        TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                        int[] offsetToCord = textSelectionHelper2.offsetToCord(textSelectionHelper2.selectionStart);
                        int i3 = offsetToCord[0];
                        TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                        i = i3 + textSelectionHelper3.textX;
                        int y = (((int) (((float) (offsetToCord[1] + textSelectionHelper3.textY)) + textSelectionHelper3.selectedView.getY())) + ((-textSelectionHelper.getLineHeight()) / 2)) - AndroidUtilities.dp(4.0f);
                        if (y >= 1) {
                            i2 = y;
                        }
                    } else {
                        i = 0;
                    }
                    int width = TextSelectionHelper.this.parentView.getWidth();
                    TextSelectionHelper.this.pickEndView();
                    TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                    if (textSelectionHelper4.selectedView != null) {
                        width = textSelectionHelper4.offsetToCord(textSelectionHelper4.selectionEnd)[0] + TextSelectionHelper.this.textX;
                    }
                    rect.set(Math.min(i, width), i2, Math.max(i, width), i2 + 1);
                }
            }
        } : r0;
    }

    /* access modifiers changed from: private */
    public void copyText() {
        CharSequence selectedText;
        if (isSelectionMode() && (selectedText = getSelectedText()) != null) {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", selectedText));
            hideActions();
            clear(true);
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onTextCopied();
            }
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
    public int[] offsetToCord(int i) {
        fillLayoutForOffset(i, this.layoutBlock);
        StaticLayout staticLayout = this.layoutBlock.layout;
        if (staticLayout == null || i > staticLayout.getText().length()) {
            return this.tmpCoord;
        }
        int lineForOffset = staticLayout.getLineForOffset(i);
        this.tmpCoord[0] = (int) (staticLayout.getPrimaryHorizontal(i) + this.layoutBlock.xOffset);
        this.tmpCoord[1] = staticLayout.getLineBottom(lineForOffset);
        int[] iArr = this.tmpCoord;
        iArr[1] = (int) (((float) iArr[1]) + this.layoutBlock.yOffset);
        return iArr;
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Canvas canvas, StaticLayout staticLayout, int i, int i2) {
        float f;
        Canvas canvas2 = canvas;
        StaticLayout staticLayout2 = staticLayout;
        int i3 = i2;
        this.selectionPath.reset();
        int lineForOffset = staticLayout.getLineForOffset(i);
        int lineForOffset2 = staticLayout2.getLineForOffset(i3);
        if (lineForOffset == lineForOffset2) {
            drawLine(canvas, staticLayout, lineForOffset, i, i2);
        } else {
            int lineEnd = staticLayout2.getLineEnd(lineForOffset);
            if (staticLayout2.getParagraphDirection(lineForOffset) != -1 && lineEnd > 0) {
                lineEnd--;
                CharSequence text = staticLayout.getText();
                int primaryHorizontal = (int) staticLayout2.getPrimaryHorizontal(lineEnd);
                if (staticLayout2.isRtlCharAt(lineEnd)) {
                    int i4 = lineEnd;
                    while (staticLayout2.isRtlCharAt(i4) && i4 != 0) {
                        i4--;
                    }
                    f = staticLayout2.getLineForOffset(i4) == staticLayout2.getLineForOffset(lineEnd) ? staticLayout2.getPrimaryHorizontal(i4 + 1) : staticLayout2.getLineLeft(lineForOffset);
                } else {
                    f = staticLayout2.getLineRight(lineForOffset);
                }
                int i5 = (int) f;
                int min = Math.min(primaryHorizontal, i5);
                int max = Math.max(primaryHorizontal, i5);
                if (lineEnd > 0 && lineEnd < text.length() && !Character.isWhitespace(text.charAt(lineEnd - 1))) {
                    this.selectionPath.addRect((float) min, (float) staticLayout2.getLineTop(lineForOffset), (float) max, (float) staticLayout2.getLineBottom(lineForOffset), Path.Direction.CW);
                }
            }
            int i6 = lineEnd;
            Canvas canvas3 = canvas;
            StaticLayout staticLayout3 = staticLayout;
            drawLine(canvas3, staticLayout3, lineForOffset, i, i6);
            drawLine(canvas3, staticLayout3, lineForOffset2, staticLayout2.getLineStart(lineForOffset2), i2);
            for (int i7 = lineForOffset + 1; i7 < lineForOffset2; i7++) {
                int lineLeft = (int) staticLayout2.getLineLeft(i7);
                int lineRight = (int) staticLayout2.getLineRight(i7);
                this.selectionPath.addRect((float) Math.min(lineLeft, lineRight), (float) staticLayout2.getLineTop(i7), (float) Math.max(lineLeft, lineRight), (float) staticLayout2.getLineBottom(i7), Path.Direction.CW);
            }
        }
        canvas2.drawPath(this.selectionPath, this.selectionPaint);
        int i8 = (int) (this.cornerRadius * 1.65f);
        float primaryHorizontal2 = staticLayout.getPrimaryHorizontal(i);
        float primaryHorizontal3 = staticLayout2.getPrimaryHorizontal(i3);
        if (i + 1 < staticLayout2.getLineEnd(lineForOffset) && (lineForOffset == lineForOffset2 || (lineForOffset + 1 == lineForOffset2 && primaryHorizontal2 > primaryHorizontal3))) {
            float lineBottom = (float) staticLayout2.getLineBottom(lineForOffset);
            this.tempPath.reset();
            float f2 = (float) i8;
            float f3 = primaryHorizontal2 + f2;
            this.tempPath.moveTo(f3, lineBottom);
            this.tempPath.lineTo(primaryHorizontal2, lineBottom);
            float f4 = lineBottom - f2;
            this.tempPath.lineTo(primaryHorizontal2, f4);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(primaryHorizontal2, f4, f3, lineBottom);
            this.tempPath.arcTo(rectF, 180.0f, -90.0f);
            canvas2.drawPath(this.tempPath, this.selectionHandlePaint);
        }
        if (staticLayout2.getLineStart(lineForOffset2) < i3) {
            float lineBottom2 = (float) staticLayout2.getLineBottom(lineForOffset2);
            this.tempPath.reset();
            float f5 = (float) i8;
            float f6 = primaryHorizontal3 - f5;
            this.tempPath.moveTo(f6, lineBottom2);
            this.tempPath.lineTo(primaryHorizontal3, lineBottom2);
            float f7 = lineBottom2 - f5;
            this.tempPath.lineTo(primaryHorizontal3, f7);
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(f6, f7, primaryHorizontal3, lineBottom2);
            this.tempPath.arcTo(rectF2, 0.0f, 90.0f);
            canvas2.drawPath(this.tempPath, this.selectionHandlePaint);
        }
    }

    private void drawLine(Canvas canvas, StaticLayout staticLayout, int i, int i2, int i3) {
        staticLayout.getSelectionPath(i2, i3, this.selectionPathMirror);
    }

    private static class LayoutBlock {
        StaticLayout layout;
        float xOffset;
        float yOffset;

        private LayoutBlock() {
        }
    }

    /* access modifiers changed from: protected */
    public void fillLayoutForOffset(int i, LayoutBlock layoutBlock2) {
        fillLayoutForOffset(i, layoutBlock2, false);
    }

    public static class ChatListTextSelectionHelper extends TextSelectionHelper<ChatMessageCell> {
        public static int TYPE_CAPTION = 1;
        public static int TYPE_DESCRIPTION = 2;
        public static int TYPE_MESSAGE;
        SparseArray<Animator> animatorSparseArray = new SparseArray<>();
        private boolean isDescription;
        private boolean maybeIsDescription;

        /* access modifiers changed from: protected */
        public int getLineHeight() {
            Cell cell = this.selectedView;
            if (cell == null || ((ChatMessageCell) cell).getMessageObject() == null) {
                return 0;
            }
            MessageObject messageObject = ((ChatMessageCell) this.selectedView).getMessageObject();
            StaticLayout staticLayout = null;
            if (this.isDescription) {
                staticLayout = ((ChatMessageCell) this.selectedView).getDescriptionlayout();
            } else if (((ChatMessageCell) this.selectedView).hasCaptionLayout()) {
                staticLayout = ((ChatMessageCell) this.selectedView).getCaptionLayout();
            } else {
                ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
                if (arrayList != null) {
                    staticLayout = arrayList.get(0).textLayout;
                }
            }
            if (staticLayout == null) {
                return 0;
            }
            return staticLayout.getLineBottom(0) - staticLayout.getLineTop(0);
        }

        public void setMessageObject(ChatMessageCell chatMessageCell) {
            ArrayList<MessageObject.TextLayoutBlock> arrayList;
            this.maybeSelectedView = chatMessageCell;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (this.maybeIsDescription && chatMessageCell.getDescriptionlayout() != null) {
                Rect rect = this.textArea;
                int i = this.maybeTextX;
                rect.set(i, this.maybeTextY, chatMessageCell.getDescriptionlayout().getWidth() + i, this.maybeTextY + chatMessageCell.getDescriptionlayout().getHeight());
            } else if (chatMessageCell.hasCaptionLayout()) {
                Rect rect2 = this.textArea;
                int i2 = this.maybeTextX;
                rect2.set(i2, this.maybeTextY, chatMessageCell.getCaptionLayout().getWidth() + i2, this.maybeTextY + chatMessageCell.getCaptionLayout().getHeight());
            } else if (messageObject != null && (arrayList = messageObject.textLayoutBlocks) != null && arrayList.size() > 0) {
                ArrayList<MessageObject.TextLayoutBlock> arrayList2 = messageObject.textLayoutBlocks;
                MessageObject.TextLayoutBlock textLayoutBlock = arrayList2.get(arrayList2.size() - 1);
                Rect rect3 = this.textArea;
                int i3 = this.maybeTextX;
                rect3.set(i3, this.maybeTextY, textLayoutBlock.textLayout.getWidth() + i3, (int) (((float) this.maybeTextY) + textLayoutBlock.textYOffset + ((float) textLayoutBlock.textLayout.getHeight())));
            }
        }

        /* access modifiers changed from: protected */
        public CharSequence getText(ChatMessageCell chatMessageCell, boolean z) {
            if (chatMessageCell == null || chatMessageCell.getMessageObject() == null) {
                return null;
            }
            if (!z ? this.isDescription : this.maybeIsDescription) {
                return chatMessageCell.getDescriptionlayout().getText();
            }
            if (chatMessageCell.hasCaptionLayout()) {
                return chatMessageCell.getCaptionLayout().getText();
            }
            return chatMessageCell.getMessageObject().messageText;
        }

        /* access modifiers changed from: protected */
        public void onTextSelected(ChatMessageCell chatMessageCell, ChatMessageCell chatMessageCell2) {
            boolean z = chatMessageCell2 == null || !(chatMessageCell2.getMessageObject() == null || chatMessageCell2.getMessageObject().getId() == chatMessageCell.getMessageObject().getId());
            int id = chatMessageCell.getMessageObject().getId();
            this.selectedCellId = id;
            this.enterProgress = 0.0f;
            this.isDescription = this.maybeIsDescription;
            Animator animator = this.animatorSparseArray.get(id);
            if (animator != null) {
                animator.removeAllListeners();
                animator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda1(this, z));
            ofFloat.setDuration(250);
            ofFloat.start();
            this.animatorSparseArray.put(this.selectedCellId, ofFloat);
            if (!z) {
                chatMessageCell.setSelectedBackgroundProgress(0.0f);
            }
            SharedConfig.removeTextSelectionHint();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTextSelected$0(boolean z, ValueAnimator valueAnimator) {
            this.enterProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
            if (textSelectionOverlay != null) {
                textSelectionOverlay.invalidate();
            }
            Cell cell = this.selectedView;
            if (cell != null && ((ChatMessageCell) cell).getCurrentMessagesGroup() == null && z) {
                ((ChatMessageCell) this.selectedView).setSelectedBackgroundProgress(1.0f - this.enterProgress);
            }
        }

        public void draw(MessageObject messageObject, MessageObject.TextLayoutBlock textLayoutBlock, Canvas canvas) {
            MessageObject messageObject2;
            Cell cell = this.selectedView;
            if (cell != null && ((ChatMessageCell) cell).getMessageObject() != null && !this.isDescription && (messageObject2 = ((ChatMessageCell) this.selectedView).getMessageObject()) != null && messageObject2.textLayoutBlocks != null && messageObject.getId() == this.selectedCellId) {
                int i = this.selectionStart;
                int i2 = this.selectionEnd;
                if (messageObject2.textLayoutBlocks.size() > 1) {
                    int i3 = textLayoutBlock.charactersOffset;
                    if (i < i3) {
                        i = i3;
                    }
                    int i4 = textLayoutBlock.charactersEnd;
                    if (i > i4) {
                        i = i4;
                    }
                    if (i2 < i3) {
                        i2 = i3;
                    }
                    if (i2 > i4) {
                        i2 = i4;
                    }
                }
                if (i != i2) {
                    if (messageObject2.isOutOwner()) {
                        this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                        this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                    } else {
                        this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                        this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                    }
                    drawSelection(canvas, textLayoutBlock.textLayout, i, i2);
                }
            }
        }

        /* access modifiers changed from: protected */
        public int getCharOffsetFromCord(int i, int i2, int i3, int i4, ChatMessageCell chatMessageCell, boolean z) {
            StaticLayout staticLayout;
            int i5 = 0;
            if (chatMessageCell == null) {
                return 0;
            }
            int i6 = i - i3;
            int i7 = i2 - i4;
            float f = 0.0f;
            if (z ? this.maybeIsDescription : this.isDescription) {
                staticLayout = chatMessageCell.getDescriptionlayout();
            } else if (chatMessageCell.hasCaptionLayout()) {
                staticLayout = chatMessageCell.getCaptionLayout();
            } else {
                MessageObject.TextLayoutBlock textLayoutBlock = chatMessageCell.getMessageObject().textLayoutBlocks.get(chatMessageCell.getMessageObject().textLayoutBlocks.size() - 1);
                staticLayout = textLayoutBlock.textLayout;
                f = textLayoutBlock.textYOffset;
            }
            if (i7 < 0) {
                i7 = 1;
            }
            if (((float) i7) > ((float) staticLayout.getLineBottom(staticLayout.getLineCount() - 1)) + f) {
                i7 = (int) ((f + ((float) staticLayout.getLineBottom(staticLayout.getLineCount() - 1))) - 1.0f);
            }
            fillLayoutForCoords(i6, i7, chatMessageCell, this.layoutBlock, z);
            LayoutBlock layoutBlock = this.layoutBlock;
            StaticLayout staticLayout2 = layoutBlock.layout;
            if (staticLayout2 == null) {
                return -1;
            }
            int i8 = (int) (((float) i6) - layoutBlock.xOffset);
            while (true) {
                if (i5 >= staticLayout2.getLineCount()) {
                    i5 = -1;
                    break;
                }
                float f2 = (float) i7;
                if (f2 > this.layoutBlock.yOffset + ((float) staticLayout2.getLineTop(i5)) && f2 < this.layoutBlock.yOffset + ((float) staticLayout2.getLineBottom(i5))) {
                    break;
                }
                i5++;
            }
            if (i5 >= 0) {
                return staticLayout2.getOffsetForHorizontal(i5, (float) i8);
            }
            return -1;
        }

        private void fillLayoutForCoords(int i, int i2, ChatMessageCell chatMessageCell, LayoutBlock layoutBlock, boolean z) {
            if (chatMessageCell != null) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (!z ? this.isDescription : this.maybeIsDescription) {
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
                        MessageObject.TextLayoutBlock textLayoutBlock = messageObject.textLayoutBlocks.get(i4);
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

        /* access modifiers changed from: protected */
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
                ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
                if (arrayList == null) {
                    layoutBlock.layout = null;
                    return;
                }
                int i2 = 0;
                if (arrayList.size() == 1) {
                    layoutBlock.layout = messageObject.textLayoutBlocks.get(0).textLayout;
                    layoutBlock.yOffset = 0.0f;
                    if (messageObject.textLayoutBlocks.get(0).isRtl()) {
                        i2 = (int) Math.ceil((double) messageObject.textXOffset);
                    }
                    layoutBlock.xOffset = (float) (-i2);
                    return;
                }
                int i3 = 0;
                while (i3 < messageObject.textLayoutBlocks.size()) {
                    MessageObject.TextLayoutBlock textLayoutBlock = messageObject.textLayoutBlocks.get(i3);
                    if (i < textLayoutBlock.charactersOffset || i > textLayoutBlock.charactersEnd) {
                        i3++;
                    } else {
                        layoutBlock.layout = messageObject.textLayoutBlocks.get(i3).textLayout;
                        layoutBlock.yOffset = messageObject.textLayoutBlocks.get(i3).textYOffset;
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

        /* access modifiers changed from: protected */
        public void onExitSelectionMode(boolean z) {
            Cell cell = this.selectedView;
            if (cell != null && ((ChatMessageCell) cell).isDrawingSelectionBackground() && !z) {
                Cell cell2 = this.selectedView;
                final ChatMessageCell chatMessageCell = (ChatMessageCell) cell2;
                int id = ((ChatMessageCell) cell2).getMessageObject().getId();
                Animator animator = this.animatorSparseArray.get(id);
                if (animator != null) {
                    animator.removeAllListeners();
                    animator.cancel();
                }
                chatMessageCell.setSelectedBackgroundProgress(0.01f);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.01f, 1.0f});
                ofFloat.addUpdateListener(new TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda0(chatMessageCell, id));
                ofFloat.addListener(new AnimatorListenerAdapter(this) {
                    public void onAnimationEnd(Animator animator) {
                        chatMessageCell.setSelectedBackgroundProgress(0.0f);
                    }
                });
                ofFloat.setDuration(300);
                ofFloat.start();
                this.animatorSparseArray.put(id, ofFloat);
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onExitSelectionMode$1(ChatMessageCell chatMessageCell, int i, ValueAnimator valueAnimator) {
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
                    this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                } else {
                    this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                }
                drawSelection(canvas, staticLayout, this.selectionStart, this.selectionEnd);
            }
        }

        public void drawDescription(boolean z, StaticLayout staticLayout, Canvas canvas) {
            if (this.isDescription) {
                if (z) {
                    this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                } else {
                    this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                    this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                }
                drawSelection(canvas, staticLayout, this.selectionStart, this.selectionEnd);
            }
        }

        public void invalidate() {
            TextSelectionHelper.super.invalidate();
            Cell cell = this.selectedView;
            if (cell != null && ((ChatMessageCell) cell).getCurrentMessagesGroup() != null) {
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

        public void setIsDescription(boolean z) {
            this.maybeIsDescription = z;
        }

        public void clear(boolean z) {
            TextSelectionHelper.super.clear(z);
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
        public CharSequence getText(ArticleSelectableView articleSelectableView, boolean z) {
            int i;
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (z) {
                i = this.maybeTextIndex;
            } else {
                i = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            }
            return (this.arrayList.isEmpty() || i < 0) ? "" : this.arrayList.get(i).getLayout().getText();
        }

        /* access modifiers changed from: protected */
        public int getCharOffsetFromCord(int i, int i2, int i3, int i4, ArticleSelectableView articleSelectableView, boolean z) {
            int i5;
            if (articleSelectableView == null) {
                return -1;
            }
            int i6 = i - i3;
            int i7 = i2 - i4;
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (z) {
                i5 = this.maybeTextIndex;
            } else {
                i5 = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            }
            StaticLayout layout = this.arrayList.get(i5).getLayout();
            if (i6 < 0) {
                i6 = 1;
            }
            if (i7 < 0) {
                i7 = 1;
            }
            if (i6 > layout.getWidth()) {
                i6 = layout.getWidth();
            }
            if (i7 > layout.getLineBottom(layout.getLineCount() - 1)) {
                i7 = layout.getLineBottom(layout.getLineCount() - 1) - 1;
            }
            int i8 = 0;
            while (true) {
                if (i8 < layout.getLineCount()) {
                    if (i7 > layout.getLineTop(i8) && i7 < layout.getLineBottom(i8)) {
                        break;
                    }
                    i8++;
                } else {
                    i8 = -1;
                    break;
                }
            }
            if (i8 >= 0) {
                return layout.getOffsetForHorizontal(i8, (float) i6);
            }
            return -1;
        }

        /* access modifiers changed from: protected */
        public void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z) {
            this.arrayList.clear();
            ArticleSelectableView articleSelectableView = (ArticleSelectableView) (z ? this.maybeSelectedView : this.selectedView);
            if (articleSelectableView == null) {
                layoutBlock.layout = null;
                return;
            }
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (z) {
                layoutBlock.layout = this.arrayList.get(this.maybeTextIndex).getLayout();
            } else {
                int i2 = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
                if (i2 < 0 || i2 >= this.arrayList.size()) {
                    layoutBlock.layout = null;
                    return;
                }
                layoutBlock.layout = this.arrayList.get(i2).getLayout();
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
            int i = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (i < 0 || i >= this.arrayList.size()) {
                return 0;
            }
            StaticLayout layout = this.arrayList.get(i).getLayout();
            int i2 = Integer.MAX_VALUE;
            for (int i3 = 0; i3 < layout.getLineCount(); i3++) {
                int lineBottom = layout.getLineBottom(i3) - layout.getLineTop(i3);
                if (lineBottom < i2) {
                    i2 = lineBottom;
                }
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
                Cell cell = (ArticleSelectableView) view;
                this.maybeSelectedView = cell;
                int findClosestLayoutIndex = findClosestLayoutIndex(i, i2, (ArticleSelectableView) cell);
                this.maybeTextIndex = findClosestLayoutIndex;
                if (findClosestLayoutIndex < 0) {
                    this.maybeSelectedView = null;
                    return;
                }
                this.maybeTextX = this.arrayList.get(findClosestLayoutIndex).getX();
                this.maybeTextY = this.arrayList.get(this.maybeTextIndex).getY();
            }
        }

        private int findClosestLayoutIndex(int i, int i2, ArticleSelectableView articleSelectableView) {
            int i3 = 0;
            if (articleSelectableView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) articleSelectableView;
                for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                    View childAt = viewGroup.getChildAt(i4);
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
            int i5 = Integer.MAX_VALUE;
            int i6 = Integer.MAX_VALUE;
            int i7 = -1;
            while (true) {
                if (size < 0) {
                    i3 = i6;
                    size = i7;
                    break;
                }
                TextLayoutBlock textLayoutBlock = this.arrayList.get(size);
                int y = textLayoutBlock.getY();
                int height = textLayoutBlock.getLayout().getHeight() + y;
                if (i2 >= y && i2 < height) {
                    break;
                }
                int min = Math.min(Math.abs(i2 - y), Math.abs(i2 - height));
                if (min < i6) {
                    i7 = size;
                    i6 = min;
                }
                size--;
            }
            if (size < 0) {
                return -1;
            }
            int row = this.arrayList.get(size).getRow();
            if (row > 0 && i3 < AndroidUtilities.dp(24.0f)) {
                for (int size2 = this.arrayList.size() - 1; size2 >= 0; size2--) {
                    TextLayoutBlock textLayoutBlock2 = this.arrayList.get(size2);
                    if (textLayoutBlock2.getRow() == row) {
                        int x = textLayoutBlock2.getX();
                        int x2 = textLayoutBlock2.getX() + textLayoutBlock2.getLayout().getWidth();
                        if (i >= x && i <= x2) {
                            return size2;
                        }
                        int min2 = Math.min(Math.abs(i - x), Math.abs(i - x2));
                        if (min2 < i5) {
                            size = size2;
                            i5 = min2;
                        }
                    }
                }
            }
            return size;
        }

        public void draw(Canvas canvas, ArticleSelectableView articleSelectableView, int i) {
            this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            int adapterPosition = getAdapterPosition(articleSelectableView);
            if (adapterPosition >= 0) {
                this.arrayList.clear();
                articleSelectableView.fillTextLayoutBlocks(this.arrayList);
                if (!this.arrayList.isEmpty()) {
                    TextLayoutBlock textLayoutBlock = this.arrayList.get(i);
                    int i2 = this.endViewOffset;
                    int length = textLayoutBlock.getLayout().getText().length();
                    if (i2 > length) {
                        i2 = length;
                    }
                    int i3 = this.startViewPosition;
                    if (adapterPosition == i3 && adapterPosition == this.endViewPosition) {
                        int i4 = this.startViewChildPosition;
                        int i5 = this.endViewChildPosition;
                        if (i4 == i5 && i4 == i) {
                            drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, i2);
                        } else if (i == i4) {
                            drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, length);
                        } else if (i == i5) {
                            drawSelection(canvas, textLayoutBlock.getLayout(), 0, i2);
                        } else if (i > i4 && i < i5) {
                            drawSelection(canvas, textLayoutBlock.getLayout(), 0, length);
                        }
                    } else if (adapterPosition == i3 && this.startViewChildPosition == i) {
                        drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, length);
                    } else {
                        int i6 = this.endViewPosition;
                        if (adapterPosition == i6 && this.endViewChildPosition == i) {
                            drawSelection(canvas, textLayoutBlock.getLayout(), 0, i2);
                        } else if ((adapterPosition > i3 && adapterPosition < i6) || ((adapterPosition == i3 && i > this.startViewChildPosition) || (adapterPosition == i6 && i < this.endViewChildPosition))) {
                            drawSelection(canvas, textLayoutBlock.getLayout(), 0, length);
                        }
                    }
                }
            }
        }

        private int getAdapterPosition(ArticleSelectableView articleSelectableView) {
            ViewGroup viewGroup;
            View view = (View) articleSelectableView;
            ViewParent parent = view.getParent();
            while (true) {
                viewGroup = this.parentView;
                if (parent != viewGroup && parent != null) {
                    if (!(parent instanceof View)) {
                        parent = null;
                        break;
                    }
                    view = (View) parent;
                    parent = view.getParent();
                } else {
                    break;
                }
            }
            if (parent == null) {
                return -1;
            }
            RecyclerListView recyclerListView = this.parentRecyclerView;
            if (recyclerListView != null) {
                return recyclerListView.getChildAdapterPosition(view);
            }
            return viewGroup.indexOfChild(view);
        }

        public boolean isSelectable(View view) {
            if (!(view instanceof ArticleSelectableView)) {
                return false;
            }
            this.arrayList.clear();
            ((ArticleSelectableView) view).fillTextLayoutBlocks(this.arrayList);
            if (view instanceof ArticleViewer.BlockTableCell) {
                return true;
            }
            return !this.arrayList.isEmpty();
        }

        /* access modifiers changed from: protected */
        public void onTextSelected(ArticleSelectableView articleSelectableView, ArticleSelectableView articleSelectableView2) {
            int adapterPosition = getAdapterPosition(articleSelectableView);
            if (adapterPosition >= 0) {
                this.endViewPosition = adapterPosition;
                this.startViewPosition = adapterPosition;
                int i = this.maybeTextIndex;
                this.endViewChildPosition = i;
                this.startViewChildPosition = i;
                this.arrayList.clear();
                articleSelectableView.fillTextLayoutBlocks(this.arrayList);
                int size = this.arrayList.size();
                this.childCountByPosition.put(adapterPosition, size);
                for (int i2 = 0; i2 < size; i2++) {
                    int i3 = (i2 << 16) + adapterPosition;
                    this.textByPosition.put(i3, this.arrayList.get(i2).getLayout().getText());
                    this.prefixTextByPosition.put(i3, this.arrayList.get(i2).getPrefix());
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onNewViewSelected(ArticleSelectableView articleSelectableView, ArticleSelectableView articleSelectableView2, int i) {
            int i2;
            int adapterPosition = getAdapterPosition(articleSelectableView2);
            int adapterPosition2 = articleSelectableView != null ? getAdapterPosition(articleSelectableView) : -1;
            invalidate();
            if (!this.movingDirectionSettling || (i2 = this.startViewPosition) != this.endViewPosition) {
                if (this.movingHandleStart) {
                    if (adapterPosition == adapterPosition2) {
                        int i3 = this.endViewChildPosition;
                        if (i <= i3 || adapterPosition < this.endViewPosition) {
                            this.startViewPosition = adapterPosition;
                            this.startViewChildPosition = i;
                            pickStartView();
                            this.startViewOffset = this.selectionEnd;
                        } else {
                            this.endViewPosition = adapterPosition;
                            this.startViewChildPosition = i3;
                            this.endViewChildPosition = i;
                            this.startViewOffset = this.endViewOffset;
                            pickEndView();
                            this.endViewOffset = 0;
                            this.movingHandleStart = false;
                        }
                    } else if (adapterPosition <= this.endViewPosition) {
                        this.startViewPosition = adapterPosition;
                        this.startViewChildPosition = i;
                        pickStartView();
                        this.startViewOffset = this.selectionEnd;
                    } else {
                        this.endViewPosition = adapterPosition;
                        this.startViewChildPosition = this.endViewChildPosition;
                        this.endViewChildPosition = i;
                        this.startViewOffset = this.endViewOffset;
                        pickEndView();
                        this.endViewOffset = 0;
                        this.movingHandleStart = false;
                    }
                } else if (adapterPosition == adapterPosition2) {
                    int i4 = this.startViewChildPosition;
                    if (i >= i4 || adapterPosition > this.startViewPosition) {
                        this.endViewPosition = adapterPosition;
                        this.endViewChildPosition = i;
                        pickEndView();
                        this.endViewOffset = 0;
                    } else {
                        this.startViewPosition = adapterPosition;
                        this.endViewChildPosition = i4;
                        this.startViewChildPosition = i;
                        this.endViewOffset = this.startViewOffset;
                        pickStartView();
                        this.movingHandleStart = true;
                        this.startViewOffset = this.selectionEnd;
                    }
                } else if (adapterPosition >= this.startViewPosition) {
                    this.endViewPosition = adapterPosition;
                    this.endViewChildPosition = i;
                    pickEndView();
                    this.endViewOffset = 0;
                } else {
                    this.startViewPosition = adapterPosition;
                    this.endViewChildPosition = this.startViewChildPosition;
                    this.startViewChildPosition = i;
                    this.endViewOffset = this.startViewOffset;
                    pickStartView();
                    this.movingHandleStart = true;
                    this.startViewOffset = this.selectionEnd;
                }
            } else if (adapterPosition == i2) {
                if (i < this.startViewChildPosition) {
                    this.startViewChildPosition = i;
                    pickStartView();
                    this.movingHandleStart = true;
                    int i5 = this.selectionEnd;
                    this.startViewOffset = i5;
                    this.selectionStart = i5 - 1;
                } else {
                    this.endViewChildPosition = i;
                    pickEndView();
                    this.movingHandleStart = false;
                    this.endViewOffset = 0;
                }
            } else if (adapterPosition < i2) {
                this.startViewPosition = adapterPosition;
                this.startViewChildPosition = i;
                pickStartView();
                this.movingHandleStart = true;
                int i6 = this.selectionEnd;
                this.startViewOffset = i6;
                this.selectionStart = i6 - 1;
            } else {
                this.endViewPosition = adapterPosition;
                this.endViewChildPosition = i;
                pickEndView();
                this.movingHandleStart = false;
                this.endViewOffset = 0;
            }
            this.arrayList.clear();
            articleSelectableView2.fillTextLayoutBlocks(this.arrayList);
            int size = this.arrayList.size();
            this.childCountByPosition.put(adapterPosition, size);
            for (int i7 = 0; i7 < size; i7++) {
                int i8 = (i7 << 16) + adapterPosition;
                this.textByPosition.put(i8, this.arrayList.get(i7).getLayout().getText());
                this.prefixTextByPosition.put(i8, this.arrayList.get(i7).getPrefix());
            }
        }

        /* access modifiers changed from: protected */
        public void pickEndView() {
            Cell cell;
            if (isSelectionMode()) {
                this.startPeek = false;
                int i = this.endViewPosition;
                if (i >= 0) {
                    LinearLayoutManager linearLayoutManager = this.layoutManager;
                    if (linearLayoutManager != null) {
                        cell = (ArticleSelectableView) linearLayoutManager.findViewByPosition(i);
                    } else {
                        cell = i < this.parentView.getChildCount() ? (ArticleSelectableView) this.parentView.getChildAt(this.endViewPosition) : null;
                    }
                    if (cell == null) {
                        this.selectedView = null;
                        return;
                    }
                    this.selectedView = cell;
                    if (this.startViewPosition != this.endViewPosition) {
                        this.selectionStart = 0;
                    } else if (this.startViewChildPosition != this.endViewChildPosition) {
                        this.selectionStart = 0;
                    } else {
                        this.selectionStart = this.startViewOffset;
                    }
                    this.selectionEnd = this.endViewOffset;
                    CharSequence text = getText((ArticleSelectableView) cell, false);
                    if (this.selectionEnd > text.length()) {
                        this.selectionEnd = text.length();
                    }
                    this.arrayList.clear();
                    ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
                    if (!this.arrayList.isEmpty()) {
                        this.textX = this.arrayList.get(this.endViewChildPosition).getX();
                        this.textY = this.arrayList.get(this.endViewChildPosition).getY();
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void pickStartView() {
            Cell cell;
            if (isSelectionMode()) {
                this.startPeek = true;
                int i = this.startViewPosition;
                if (i >= 0) {
                    LinearLayoutManager linearLayoutManager = this.layoutManager;
                    if (linearLayoutManager != null) {
                        cell = (ArticleSelectableView) linearLayoutManager.findViewByPosition(i);
                    } else {
                        cell = this.endViewPosition < this.parentView.getChildCount() ? (ArticleSelectableView) this.parentView.getChildAt(this.startViewPosition) : null;
                    }
                    if (cell == null) {
                        this.selectedView = null;
                        return;
                    }
                    this.selectedView = cell;
                    if (this.startViewPosition != this.endViewPosition) {
                        this.selectionEnd = getText((ArticleSelectableView) cell, false).length();
                    } else if (this.startViewChildPosition != this.endViewChildPosition) {
                        this.selectionEnd = getText((ArticleSelectableView) cell, false).length();
                    } else {
                        this.selectionEnd = this.endViewOffset;
                    }
                    this.selectionStart = this.startViewOffset;
                    this.arrayList.clear();
                    ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
                    if (!this.arrayList.isEmpty()) {
                        this.textX = this.arrayList.get(this.startViewChildPosition).getX();
                        this.textY = this.arrayList.get(this.startViewChildPosition).getY();
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
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
            TextSelectionHelper.super.invalidate();
            for (int i = 0; i < this.parentView.getChildCount(); i++) {
                this.parentView.getChildAt(i).invalidate();
            }
        }

        public void clear(boolean z) {
            TextSelectionHelper.super.clear(z);
            this.startViewPosition = -1;
            this.endViewPosition = -1;
            this.startViewChildPosition = -1;
            this.endViewChildPosition = -1;
            this.textByPosition.clear();
            this.childCountByPosition.clear();
        }

        /* access modifiers changed from: protected */
        public CharSequence getSelectedText() {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            int i = this.startViewPosition;
            while (true) {
                int i2 = this.endViewPosition;
                if (i > i2) {
                    break;
                }
                int i3 = this.startViewPosition;
                if (i == i3) {
                    int i4 = i3 == i2 ? this.endViewChildPosition : this.childCountByPosition.get(i) - 1;
                    for (int i5 = this.startViewChildPosition; i5 <= i4; i5++) {
                        int i6 = (i5 << 16) + i;
                        CharSequence charSequence = this.textByPosition.get(i6);
                        if (charSequence != null) {
                            int i7 = this.startViewPosition;
                            int i8 = this.endViewPosition;
                            if (i7 == i8 && i5 == this.endViewChildPosition && i5 == this.startViewChildPosition) {
                                int i9 = this.endViewOffset;
                                int i10 = this.startViewOffset;
                                if (i9 >= i10) {
                                    int i11 = i10;
                                    i10 = i9;
                                    i9 = i11;
                                }
                                if (i9 < charSequence.length()) {
                                    if (i10 > charSequence.length()) {
                                        i10 = charSequence.length();
                                    }
                                    spannableStringBuilder.append(charSequence.subSequence(i9, i10));
                                    spannableStringBuilder.append(10);
                                }
                            } else if (i7 == i8 && i5 == this.endViewChildPosition) {
                                CharSequence charSequence2 = this.prefixTextByPosition.get(i6);
                                if (charSequence2 != null) {
                                    spannableStringBuilder.append(charSequence2).append(' ');
                                }
                                int i12 = this.endViewOffset;
                                if (i12 > charSequence.length()) {
                                    i12 = charSequence.length();
                                }
                                spannableStringBuilder.append(charSequence.subSequence(0, i12));
                                spannableStringBuilder.append(10);
                            } else if (i5 == this.startViewChildPosition) {
                                int i13 = this.startViewOffset;
                                if (i13 < charSequence.length()) {
                                    spannableStringBuilder.append(charSequence.subSequence(i13, charSequence.length()));
                                    spannableStringBuilder.append(10);
                                }
                            } else {
                                CharSequence charSequence3 = this.prefixTextByPosition.get(i6);
                                if (charSequence3 != null) {
                                    spannableStringBuilder.append(charSequence3).append(' ');
                                }
                                spannableStringBuilder.append(charSequence);
                                spannableStringBuilder.append(10);
                            }
                        }
                    }
                } else if (i == i2) {
                    for (int i14 = 0; i14 <= this.endViewChildPosition; i14++) {
                        int i15 = (i14 << 16) + i;
                        CharSequence charSequence4 = this.textByPosition.get(i15);
                        if (charSequence4 != null) {
                            if (this.startViewPosition == this.endViewPosition && i14 == this.endViewChildPosition && i14 == this.startViewChildPosition) {
                                int i16 = this.endViewOffset;
                                int i17 = this.startViewOffset;
                                if (i17 < charSequence4.length()) {
                                    if (i16 > charSequence4.length()) {
                                        i16 = charSequence4.length();
                                    }
                                    spannableStringBuilder.append(charSequence4.subSequence(i17, i16));
                                    spannableStringBuilder.append(10);
                                }
                            } else if (i14 == this.endViewChildPosition) {
                                CharSequence charSequence5 = this.prefixTextByPosition.get(i15);
                                if (charSequence5 != null) {
                                    spannableStringBuilder.append(charSequence5).append(' ');
                                }
                                int i18 = this.endViewOffset;
                                if (i18 > charSequence4.length()) {
                                    i18 = charSequence4.length();
                                }
                                spannableStringBuilder.append(charSequence4.subSequence(0, i18));
                                spannableStringBuilder.append(10);
                            } else {
                                CharSequence charSequence6 = this.prefixTextByPosition.get(i15);
                                if (charSequence6 != null) {
                                    spannableStringBuilder.append(charSequence6).append(' ');
                                }
                                spannableStringBuilder.append(charSequence4);
                                spannableStringBuilder.append(10);
                            }
                        }
                    }
                } else {
                    int i19 = this.childCountByPosition.get(i);
                    for (int i20 = this.startViewChildPosition; i20 < i19; i20++) {
                        int i21 = (i20 << 16) + i;
                        CharSequence charSequence7 = this.prefixTextByPosition.get(i21);
                        if (charSequence7 != null) {
                            spannableStringBuilder.append(charSequence7).append(' ');
                        }
                        spannableStringBuilder.append(this.textByPosition.get(i21));
                        spannableStringBuilder.append(10);
                    }
                }
                i++;
            }
            if (spannableStringBuilder.length() <= 0) {
                return null;
            }
            for (IgnoreCopySpannable ignoreCopySpannable : (IgnoreCopySpannable[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length() - 1, IgnoreCopySpannable.class)) {
                spannableStringBuilder.delete(spannableStringBuilder.getSpanStart(ignoreCopySpannable), spannableStringBuilder.getSpanEnd(ignoreCopySpannable));
            }
            return spannableStringBuilder.subSequence(0, spannableStringBuilder.length() - 1);
        }

        /* access modifiers changed from: protected */
        public boolean selectLayout(int i, int i2) {
            if (!this.multiselect) {
                return false;
            }
            if (i2 <= ((ArticleSelectableView) this.selectedView).getTop() || i2 >= ((ArticleSelectableView) this.selectedView).getBottom()) {
                int childCount = this.parentView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    if (isSelectable(this.parentView.getChildAt(i3))) {
                        Cell cell = (ArticleSelectableView) this.parentView.getChildAt(i3);
                        if (i2 > cell.getTop() && i2 < cell.getBottom()) {
                            int findClosestLayoutIndex = findClosestLayoutIndex((int) (((float) i) - cell.getX()), (int) (((float) i2) - cell.getY()), cell);
                            if (findClosestLayoutIndex < 0) {
                                return false;
                            }
                            onNewViewSelected((ArticleSelectableView) this.selectedView, cell, findClosestLayoutIndex);
                            this.selectedView = cell;
                            return true;
                        }
                    }
                }
                return false;
            }
            int i4 = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            int findClosestLayoutIndex2 = findClosestLayoutIndex((int) (((float) i) - ((ArticleSelectableView) this.selectedView).getX()), (int) (((float) i2) - ((ArticleSelectableView) this.selectedView).getY()), (ArticleSelectableView) this.selectedView);
            if (findClosestLayoutIndex2 == i4 || findClosestLayoutIndex2 < 0) {
                return false;
            }
            Cell cell2 = this.selectedView;
            onNewViewSelected((ArticleSelectableView) cell2, (ArticleSelectableView) cell2, findClosestLayoutIndex2);
            return true;
        }

        /* access modifiers changed from: protected */
        public boolean canSelect(int i) {
            if (this.startViewPosition == this.endViewPosition && this.startViewChildPosition == this.endViewChildPosition) {
                return TextSelectionHelper.super.canSelect(i);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void jumpToLine(int i, int i2, boolean z, float f, float f2, ArticleSelectableView articleSelectableView) {
            if (!z || articleSelectableView != this.selectedView || f2 != f) {
                TextSelectionHelper.super.jumpToLine(i, i2, z, f, f2, articleSelectableView);
            } else if (this.movingHandleStart) {
                this.selectionStart = i;
            } else {
                this.selectionEnd = i;
            }
        }

        /* access modifiers changed from: protected */
        public boolean canShowActions() {
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            if (linearLayoutManager == null) {
                return true;
            }
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
            int i = this.startViewPosition;
            if ((findFirstVisibleItemPosition >= i && findFirstVisibleItemPosition <= this.endViewPosition) || (findLastVisibleItemPosition >= i && findLastVisibleItemPosition <= this.endViewPosition)) {
                return true;
            }
            if (i < findFirstVisibleItemPosition || this.endViewPosition > findLastVisibleItemPosition) {
                return false;
            }
            return true;
        }
    }

    private static class PathCopyTo extends Path {
        private Path destination;

        public PathCopyTo(Path path) {
            this.destination = path;
        }

        public void reset() {
            super.reset();
        }

        public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
            this.destination.addRect(f, f2, f3, f4, direction);
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

        public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
            super.addRect(f, f2, f3, f4, direction);
            if (f4 > this.lastBottom) {
                this.lastBottom = f4;
            }
        }
    }

    public void setKeyboardSize(int i) {
        this.keyboardSize = i;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String str) {
        return Theme.getColor(str);
    }
}
