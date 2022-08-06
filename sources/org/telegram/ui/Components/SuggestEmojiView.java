package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public class SuggestEmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final Adapter adapter;
    private Integer arrowToEnd;
    private Emoji.EmojiSpan arrowToSpan;
    private Integer arrowToStart;
    private float arrowX;
    private AnimatedFloat arrowXAnimated;
    private Paint backgroundPaint;
    private Path circlePath = new Path();
    private boolean clear;
    /* access modifiers changed from: private */
    public final FrameLayout containerView;
    /* access modifiers changed from: private */
    public final int currentAccount;
    private final ChatActivityEnterView enterView;
    private boolean forceClose;
    /* access modifiers changed from: private */
    public ArrayList<MediaDataController.KeywordResult> keywordResults = new ArrayList<>();
    private String[] lastLang;
    private String lastQuery;
    private int lastQueryId;
    private int lastQueryType;
    private float lastSpanY;
    private final LinearLayoutManager layout;
    private Drawable leftGradient;
    private AnimatedFloat leftGradientAlpha;
    /* access modifiers changed from: private */
    public final RecyclerListView listView;
    private AnimatedFloat listViewCenterAnimated;
    private AnimatedFloat listViewWidthAnimated;
    private Path path = new Path();
    private final Theme.ResourcesProvider resourcesProvider;
    private Drawable rightGradient;
    private AnimatedFloat rightGradientAlpha;
    private Runnable searchRunnable;
    private boolean show;
    private AnimatedFloat showFloat1;
    private AnimatedFloat showFloat2;
    private Runnable updateRunnable;

    public SuggestEmojiView(Context context, int i, final ChatActivityEnterView chatActivityEnterView, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        AnonymousClass1 r7 = new FrameLayout(getContext()) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                SuggestEmojiView.this.drawContainerBegin(canvas);
                super.dispatchDraw(canvas);
                SuggestEmojiView.this.drawContainerEnd(canvas);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.66f));
                super.onMeasure(i, i2);
            }

            public void setVisibility(int i) {
                boolean z = true;
                boolean z2 = getVisibility() == i;
                super.setVisibility(i);
                if (!z2) {
                    if (i != 0) {
                        z = false;
                    }
                    for (int i2 = 0; i2 < SuggestEmojiView.this.listView.getChildCount(); i2++) {
                        if (z) {
                            ((Adapter.EmojiImageView) SuggestEmojiView.this.listView.getChildAt(i2)).attach();
                        } else {
                            ((Adapter.EmojiImageView) SuggestEmojiView.this.listView.getChildAt(i2)).detach();
                        }
                    }
                }
            }
        };
        this.containerView = r7;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        AnonymousClass1 r1 = r7;
        CubicBezierInterpolator cubicBezierInterpolator2 = cubicBezierInterpolator;
        this.showFloat1 = new AnimatedFloat(r1, 120, 350, cubicBezierInterpolator2);
        this.showFloat2 = new AnimatedFloat(r1, 150, 600, cubicBezierInterpolator2);
        new OvershootInterpolator(0.4f);
        this.leftGradientAlpha = new AnimatedFloat((View) r7, 300, (TimeInterpolator) cubicBezierInterpolator);
        this.rightGradientAlpha = new AnimatedFloat((View) r7, 300, (TimeInterpolator) cubicBezierInterpolator);
        this.arrowXAnimated = new AnimatedFloat((View) r7, 200, (TimeInterpolator) cubicBezierInterpolator);
        this.listViewCenterAnimated = new AnimatedFloat((View) r7, 350, (TimeInterpolator) cubicBezierInterpolator);
        this.listViewWidthAnimated = new AnimatedFloat((View) r7, 350, (TimeInterpolator) cubicBezierInterpolator);
        this.currentAccount = i;
        this.enterView = chatActivityEnterView;
        this.resourcesProvider = resourcesProvider2;
        AnonymousClass2 r0 = new RecyclerListView(context) {
            private boolean left;
            private boolean right;

            public void onScrolled(int i, int i2) {
                super.onScrolled(i, i2);
                boolean canScrollHorizontally = canScrollHorizontally(-1);
                boolean canScrollHorizontally2 = canScrollHorizontally(1);
                if (this.left != canScrollHorizontally || this.right != canScrollHorizontally2) {
                    SuggestEmojiView.this.containerView.invalidate();
                    this.left = canScrollHorizontally;
                    this.right = canScrollHorizontally2;
                }
            }
        };
        this.listView = r0;
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        r0.setAdapter(adapter2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layout = linearLayoutManager;
        linearLayoutManager.setOrientation(0);
        r0.setLayoutManager(linearLayoutManager);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(45);
        defaultItemAnimator.setTranslationInterpolator(cubicBezierInterpolator);
        r0.setItemAnimator(defaultItemAnimator);
        r0.setSelectorDrawableColor(Theme.getColor("listSelectorSDK21", resourcesProvider2));
        r0.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SuggestEmojiView$$ExternalSyntheticLambda5(this));
        r7.addView(r0, LayoutHelper.createFrame(-1, 52.0f));
        addView(r7, LayoutHelper.createFrame(-1.0f, 66.66f, 80));
        chatActivityEnterView.getEditField().addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (chatActivityEnterView.getVisibility() == 0) {
                    SuggestEmojiView.this.fireUpdate();
                }
            }
        });
        Drawable mutate = getResources().getDrawable(NUM).mutate();
        this.leftGradient = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel", resourcesProvider2), PorterDuff.Mode.MULTIPLY));
        Drawable mutate2 = getResources().getDrawable(NUM).mutate();
        this.rightGradient = mutate2;
        mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel", resourcesProvider2), PorterDuff.Mode.MULTIPLY));
        MediaDataController.getInstance(i).checkStickers(5);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        onClick(((Adapter.EmojiImageView) view).emoji);
    }

    public void onTextSelectionChanged(int i, int i2) {
        fireUpdate();
    }

    public boolean isShown() {
        return this.show;
    }

    public void updateColors() {
        Paint paint = this.backgroundPaint;
        if (paint != null) {
            paint.setColor(Theme.getColor("chat_stickersHintPanel", this.resourcesProvider));
        }
        this.leftGradient.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel", this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
        this.rightGradient.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel", this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
    }

    public void forceClose() {
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRunnable = null;
        }
        this.show = false;
        this.forceClose = true;
        this.containerView.invalidate();
    }

    public void fireUpdate() {
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRunnable = null;
        }
        SuggestEmojiView$$ExternalSyntheticLambda0 suggestEmojiView$$ExternalSyntheticLambda0 = new SuggestEmojiView$$ExternalSyntheticLambda0(this);
        this.updateRunnable = suggestEmojiView$$ExternalSyntheticLambda0;
        AndroidUtilities.runOnUIThread(suggestEmojiView$$ExternalSyntheticLambda0, 16);
    }

    /* access modifiers changed from: private */
    public void update() {
        this.updateRunnable = null;
        ChatActivityEnterView chatActivityEnterView = this.enterView;
        if (chatActivityEnterView == null || chatActivityEnterView.getEditField() == null || this.enterView.getFieldText() == null) {
            this.show = false;
            this.forceClose = true;
            this.containerView.invalidate();
            return;
        }
        int selectionStart = this.enterView.getEditField().getSelectionStart();
        int selectionEnd = this.enterView.getEditField().getSelectionEnd();
        if (selectionStart != selectionEnd) {
            this.show = false;
            this.containerView.invalidate();
            return;
        }
        CharSequence fieldText = this.enterView.getFieldText();
        boolean z = fieldText instanceof Spanned;
        Emoji.EmojiSpan[] emojiSpanArr = z ? (Emoji.EmojiSpan[]) ((Spanned) fieldText).getSpans(Math.max(0, selectionEnd - 24), selectionEnd, Emoji.EmojiSpan.class) : null;
        if (emojiSpanArr == null || emojiSpanArr.length <= 0 || !SharedConfig.suggestAnimatedEmoji) {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = z ? (AnimatedEmojiSpan[]) ((Spanned) fieldText).getSpans(Math.max(0, selectionEnd), selectionEnd, AnimatedEmojiSpan.class) : null;
            if ((animatedEmojiSpanArr == null || animatedEmojiSpanArr.length == 0) && selectionEnd < 52) {
                this.show = true;
                this.containerView.setVisibility(0);
                this.arrowToSpan = null;
                searchKeywords(fieldText.toString().substring(0, selectionEnd));
                this.containerView.invalidate();
                return;
            }
        } else {
            Emoji.EmojiSpan emojiSpan = emojiSpanArr[emojiSpanArr.length - 1];
            if (emojiSpan != null) {
                Spanned spanned = (Spanned) fieldText;
                int spanStart = spanned.getSpanStart(emojiSpan);
                int spanEnd = spanned.getSpanEnd(emojiSpan);
                if (selectionStart == spanEnd) {
                    String substring = fieldText.toString().substring(spanStart, spanEnd);
                    this.show = true;
                    this.containerView.setVisibility(0);
                    this.arrowToSpan = emojiSpan;
                    this.arrowToEnd = null;
                    this.arrowToStart = null;
                    searchAnimated(substring);
                    this.containerView.invalidate();
                    return;
                }
            }
        }
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
        this.show = false;
        this.containerView.invalidate();
    }

    private void searchKeywords(String str) {
        if (str != null) {
            String str2 = this.lastQuery;
            if (str2 == null || this.lastQueryType != 1 || !str2.equals(str) || this.clear || this.keywordResults.isEmpty()) {
                int i = this.lastQueryId + 1;
                this.lastQueryId = i;
                String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                String[] strArr = this.lastLang;
                if (strArr == null || !Arrays.equals(currentKeyboardLanguage, strArr)) {
                    MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                }
                this.lastLang = currentKeyboardLanguage;
                Runnable runnable = this.searchRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.searchRunnable = null;
                }
                this.searchRunnable = new SuggestEmojiView$$ExternalSyntheticLambda3(this, currentKeyboardLanguage, str, i);
                if (this.keywordResults.isEmpty()) {
                    AndroidUtilities.runOnUIThread(this.searchRunnable, 600);
                } else {
                    this.searchRunnable.run();
                }
            } else {
                this.forceClose = false;
                this.containerView.setVisibility(0);
                this.lastSpanY = (float) AndroidUtilities.dp(10.0f);
                this.containerView.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchKeywords$2(String[] strArr, String str, int i) {
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(strArr, str, true, new SuggestEmojiView$$ExternalSyntheticLambda4(this, i, str), true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchKeywords$1(int i, String str, ArrayList arrayList, String str2) {
        if (i == this.lastQueryId) {
            this.lastQueryType = 1;
            this.lastQuery = str;
            if (arrayList == null || arrayList.isEmpty()) {
                this.clear = true;
                forceClose();
                return;
            }
            this.clear = false;
            this.forceClose = false;
            this.containerView.setVisibility(0);
            this.lastSpanY = (float) AndroidUtilities.dp(10.0f);
            this.keywordResults = arrayList;
            this.arrowToStart = 0;
            this.arrowToEnd = Integer.valueOf(str.length());
            this.containerView.invalidate();
            this.adapter.notifyDataSetChanged();
        }
    }

    private void searchAnimated(String str) {
        if (str != null) {
            String str2 = this.lastQuery;
            if (str2 == null || this.lastQueryType != 2 || !str2.equals(str) || this.clear || this.keywordResults.isEmpty()) {
                int i = this.lastQueryId + 1;
                this.lastQueryId = i;
                Runnable runnable = this.searchRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.searchRunnable = null;
                }
                this.searchRunnable = new SuggestEmojiView$$ExternalSyntheticLambda2(this, str, i);
                if (this.keywordResults.isEmpty()) {
                    AndroidUtilities.runOnUIThread(this.searchRunnable, 600);
                } else {
                    this.searchRunnable.run();
                }
            } else {
                this.forceClose = false;
                this.containerView.setVisibility(0);
                this.containerView.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchAnimated$4(String str, int i) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(new MediaDataController.KeywordResult(str, (String) null));
        MediaDataController.getInstance(this.currentAccount).fillWithAnimatedEmoji(arrayList, 15, new SuggestEmojiView$$ExternalSyntheticLambda1(this, i, str, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchAnimated$3(int i, String str, ArrayList arrayList) {
        if (i == this.lastQueryId) {
            this.lastQuery = str;
            this.lastQueryType = 2;
            arrayList.remove(arrayList.size() - 1);
            if (!arrayList.isEmpty()) {
                this.clear = false;
                this.forceClose = false;
                this.containerView.setVisibility(0);
                this.keywordResults = arrayList;
                this.adapter.notifyDataSetChanged();
                this.containerView.invalidate();
                return;
            }
            this.clear = true;
            forceClose();
        }
    }

    private CharSequence makeEmoji(String str) {
        AnimatedEmojiSpan animatedEmojiSpan;
        Paint.FontMetricsInt fontMetricsInt = this.enterView.getEditField().getPaint().getFontMetricsInt();
        if (str == null || !str.startsWith("animated_")) {
            return Emoji.replaceEmoji(str, fontMetricsInt, AndroidUtilities.dp(20.0f), true);
        }
        try {
            long parseLong = Long.parseLong(str.substring(9));
            TLRPC$Document findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, parseLong);
            SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(findDocument));
            if (findDocument == null) {
                animatedEmojiSpan = new AnimatedEmojiSpan(parseLong, fontMetricsInt);
            } else {
                animatedEmojiSpan = new AnimatedEmojiSpan(findDocument, fontMetricsInt);
            }
            spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
            return spannableString;
        } catch (Exception unused) {
            return null;
        }
    }

    private void onClick(String str) {
        ChatActivityEnterView chatActivityEnterView;
        int i;
        int i2;
        CharSequence makeEmoji;
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        if (this.show && (chatActivityEnterView = this.enterView) != null && (chatActivityEnterView.getFieldText() instanceof Spanned)) {
            if (this.arrowToSpan != null) {
                i2 = ((Spanned) this.enterView.getFieldText()).getSpanStart(this.arrowToSpan);
                i = ((Spanned) this.enterView.getFieldText()).getSpanEnd(this.arrowToSpan);
            } else {
                Integer num = this.arrowToStart;
                if (num != null && this.arrowToEnd != null) {
                    i2 = num.intValue();
                    i = this.arrowToEnd.intValue();
                    this.arrowToEnd = null;
                    this.arrowToStart = null;
                } else {
                    return;
                }
            }
            Editable text = this.enterView.getEditField().getText();
            if (text != null && i2 >= 0 && i >= 0 && i2 <= text.length() && i <= text.length()) {
                if (this.arrowToSpan != null) {
                    if (this.enterView.getFieldText() instanceof Spannable) {
                        ((Spannable) this.enterView.getFieldText()).removeSpan(this.arrowToSpan);
                    }
                    this.arrowToSpan = null;
                }
                String obj = text.toString();
                String substring = obj.substring(i2, i);
                int length = substring.length();
                while (true) {
                    i -= length;
                    if (i < 0) {
                        break;
                    }
                    int i3 = i + length;
                    if (!obj.substring(i, i3).equals(substring) || (makeEmoji = makeEmoji(str)) == null || ((animatedEmojiSpanArr = (AnimatedEmojiSpan[]) text.getSpans(i, i3, AnimatedEmojiSpan.class)) != null && animatedEmojiSpanArr.length > 0)) {
                        break;
                    }
                    Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) text.getSpans(i, i3, Emoji.EmojiSpan.class);
                    if (emojiSpanArr != null) {
                        for (Emoji.EmojiSpan removeSpan : emojiSpanArr) {
                            text.removeSpan(removeSpan);
                        }
                    }
                    text.replace(i, i3, makeEmoji);
                }
                try {
                    performHapticFeedback(3, 1);
                } catch (Exception unused) {
                }
                Emoji.addRecentEmoji(str);
                this.show = false;
                this.forceClose = true;
                this.lastQueryType = 0;
                this.containerView.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public void drawContainerBegin(Canvas canvas) {
        Canvas canvas2 = canvas;
        ChatActivityEnterView chatActivityEnterView = this.enterView;
        if (!(chatActivityEnterView == null || chatActivityEnterView.getEditField() == null)) {
            Emoji.EmojiSpan emojiSpan = this.arrowToSpan;
            if (emojiSpan != null && emojiSpan.drawn) {
                float x = this.enterView.getEditField().getX() + ((float) this.enterView.getEditField().getPaddingLeft());
                Emoji.EmojiSpan emojiSpan2 = this.arrowToSpan;
                this.arrowX = x + emojiSpan2.lastDrawX;
                this.lastSpanY = emojiSpan2.lastDrawY;
            } else if (!(this.arrowToStart == null || this.arrowToEnd == null)) {
                this.arrowX = this.enterView.getEditField().getX() + ((float) this.enterView.getEditField().getPaddingLeft()) + ((float) AndroidUtilities.dp(12.0f));
            }
        }
        boolean z = this.show && !this.forceClose && !this.keywordResults.isEmpty() && !this.clear;
        float f = this.showFloat1.set(z ? 1.0f : 0.0f);
        float f2 = this.showFloat2.set(z ? 1.0f : 0.0f);
        float f3 = this.arrowXAnimated.set(this.arrowX);
        if (f <= 0.0f && f2 <= 0.0f && !z) {
            this.containerView.setVisibility(8);
        }
        this.path.rewind();
        float left = (float) this.listView.getLeft();
        float left2 = (float) (this.listView.getLeft() + (this.keywordResults.size() * AndroidUtilities.dp(44.0f)));
        boolean z2 = this.listViewWidthAnimated.get() <= 0.0f;
        float f4 = left2 - left;
        float f5 = f4 <= 0.0f ? this.listViewWidthAnimated.get() : this.listViewWidthAnimated.set(f4, z2);
        float f6 = this.listViewCenterAnimated.set((left + left2) / 2.0f, z2);
        ChatActivityEnterView chatActivityEnterView2 = this.enterView;
        if (!(chatActivityEnterView2 == null || chatActivityEnterView2.getEditField() == null)) {
            this.containerView.setTranslationY(((float) ((-this.enterView.getEditField().getHeight()) - this.enterView.getEditField().getScrollY())) + this.lastSpanY + ((float) AndroidUtilities.dp(5.0f)));
        }
        float f7 = f5 / 4.0f;
        float f8 = f5 / 2.0f;
        int max = (int) Math.max((this.arrowX - Math.max(f7, Math.min(f8, (float) AndroidUtilities.dp(66.0f)))) - ((float) this.listView.getLeft()), 0.0f);
        if (this.listView.getPaddingLeft() != max) {
            this.listView.setPadding(max, 0, 0, 0);
            this.listView.scrollBy(this.listView.getPaddingLeft() - max, 0);
        }
        this.listView.setTranslationX((float) (((int) Math.max((f3 - Math.max(f7, Math.min(f8, (float) AndroidUtilities.dp(66.0f)))) - ((float) this.listView.getLeft()), 0.0f)) - max));
        float paddingLeft = (f6 - f8) + ((float) this.listView.getPaddingLeft()) + this.listView.getTranslationX();
        float top = ((float) this.listView.getTop()) + this.listView.getTranslationY() + ((float) this.listView.getPaddingTop());
        float min = Math.min(f6 + f8 + ((float) this.listView.getPaddingLeft()) + this.listView.getTranslationX(), (float) (getWidth() - this.containerView.getPaddingRight()));
        float bottom = (((float) this.listView.getBottom()) + this.listView.getTranslationY()) - ((float) AndroidUtilities.dp(6.66f));
        float min2 = Math.min((float) AndroidUtilities.dp(9.0f), f8) * 2.0f;
        RectF rectF = AndroidUtilities.rectTmp;
        float f9 = bottom - min2;
        float var_ = paddingLeft + min2;
        rectF.set(paddingLeft, f9, var_, bottom);
        this.path.arcTo(rectF, 90.0f, 90.0f);
        float var_ = top + min2;
        rectF.set(paddingLeft, top, var_, var_);
        this.path.arcTo(rectF, -180.0f, 90.0f);
        float var_ = min - min2;
        rectF.set(var_, top, min, var_);
        this.path.arcTo(rectF, -90.0f, 90.0f);
        rectF.set(var_, f9, min, bottom);
        this.path.arcTo(rectF, 0.0f, 90.0f);
        this.path.lineTo(((float) AndroidUtilities.dp(8.66f)) + f3, bottom);
        this.path.lineTo(f3, ((float) AndroidUtilities.dp(6.66f)) + bottom);
        this.path.lineTo(f3 - ((float) AndroidUtilities.dp(8.66f)), bottom);
        this.path.close();
        if (this.backgroundPaint == null) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setPathEffect(new CornerPathEffect((float) AndroidUtilities.dp(2.0f)));
            this.backgroundPaint.setShadowLayer((float) AndroidUtilities.dp(4.33f), 0.0f, (float) AndroidUtilities.dp(0.33333334f), NUM);
            this.backgroundPaint.setColor(Theme.getColor("chat_stickersHintPanel", this.resourcesProvider));
        }
        if (f < 1.0f) {
            this.circlePath.rewind();
            float dp = ((float) AndroidUtilities.dp(6.66f)) + bottom;
            double d = (double) (f3 - paddingLeft);
            double d2 = (double) (dp - top);
            double d3 = (double) (f3 - min);
            float var_ = f;
            double d4 = (double) (dp - bottom);
            this.circlePath.addCircle(f3, dp, ((float) Math.sqrt(Math.max(Math.max(Math.pow(d, 2.0d) + Math.pow(d2, 2.0d), Math.pow(d2, 2.0d) + Math.pow(d3, 2.0d)), Math.max(Math.pow(d, 2.0d) + Math.pow(d4, 2.0d), Math.pow(d3, 2.0d) + Math.pow(d4, 2.0d))))) * var_, Path.Direction.CW);
            canvas.save();
            canvas2 = canvas;
            canvas2.clipPath(this.circlePath);
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (int) (var_ * 255.0f), 31);
        }
        canvas2.drawPath(this.path, this.backgroundPaint);
        canvas.save();
        canvas2.clipPath(this.path);
    }

    public void drawContainerEnd(Canvas canvas) {
        float f = this.listViewWidthAnimated.get();
        float f2 = this.listViewCenterAnimated.get();
        float f3 = f / 2.0f;
        float paddingLeft = (f2 - f3) + ((float) this.listView.getPaddingLeft()) + this.listView.getTranslationX();
        float top = (float) (this.listView.getTop() + this.listView.getPaddingTop());
        float min = Math.min(f2 + f3 + ((float) this.listView.getPaddingLeft()) + this.listView.getTranslationX(), (float) (getWidth() - this.containerView.getPaddingRight()));
        float bottom = (float) this.listView.getBottom();
        float f4 = this.leftGradientAlpha.set(this.listView.canScrollHorizontally(-1) ? 1.0f : 0.0f);
        if (f4 > 0.0f) {
            int i = (int) paddingLeft;
            this.leftGradient.setBounds(i, (int) top, AndroidUtilities.dp(32.0f) + i, (int) bottom);
            this.leftGradient.setAlpha((int) (f4 * 255.0f));
            this.leftGradient.draw(canvas);
        }
        float f5 = this.rightGradientAlpha.set(this.listView.canScrollHorizontally(1) ? 1.0f : 0.0f);
        if (f5 > 0.0f) {
            int i2 = (int) min;
            this.rightGradient.setBounds(i2 - AndroidUtilities.dp(32.0f), (int) top, i2, (int) bottom);
            this.rightGradient.setAlpha((int) (f5 * 255.0f));
            this.rightGradient.draw(canvas);
        }
        canvas.restore();
        if (this.showFloat1.get() < 1.0f) {
            canvas.restore();
            canvas.restore();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        float f = this.listViewWidthAnimated.get();
        float f2 = this.listViewCenterAnimated.get();
        RectF rectF = AndroidUtilities.rectTmp;
        float f3 = f / 2.0f;
        rectF.set((f2 - f3) + ((float) this.listView.getPaddingLeft()) + this.listView.getTranslationX(), (float) (this.listView.getTop() + this.listView.getPaddingTop()), Math.min(f2 + f3 + ((float) this.listView.getPaddingLeft()) + this.listView.getTranslationX(), (float) (getWidth() - this.containerView.getPaddingRight())), (float) this.listView.getBottom());
        rectF.offset(this.containerView.getX(), this.containerView.getY());
        if (this.show && rectF.contains(motionEvent.getX(), motionEvent.getY())) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            motionEvent.setAction(3);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            if (!this.keywordResults.isEmpty()) {
                fireUpdate();
            }
        } else if (i == NotificationCenter.emojiLoaded) {
            for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
                this.listView.getChildAt(i3).invalidate();
            }
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private class EmojiImageView extends View {
            private boolean attached;
            private Drawable drawable;
            /* access modifiers changed from: private */
            public String emoji;
            private AnimatedFloat pressed = new AnimatedFloat((View) this, 350, (TimeInterpolator) new OvershootInterpolator(5.0f));

            public EmojiImageView(Context context) {
                super(context);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(9.66f));
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f), NUM));
            }

            /* access modifiers changed from: private */
            public void setEmoji(String str) {
                this.emoji = str;
                if (str == null || !str.startsWith("animated_")) {
                    setImageDrawable(Emoji.getEmojiBigDrawable(str));
                    return;
                }
                try {
                    long parseLong = Long.parseLong(str.substring(9));
                    Drawable drawable2 = this.drawable;
                    if (!(drawable2 instanceof AnimatedEmojiDrawable) || ((AnimatedEmojiDrawable) drawable2).getDocumentId() != parseLong) {
                        setImageDrawable(AnimatedEmojiDrawable.make(SuggestEmojiView.this.currentAccount, 2, parseLong));
                    }
                } catch (Exception unused) {
                    setImageDrawable((Drawable) null);
                }
            }

            public void setImageDrawable(Drawable drawable2) {
                Drawable drawable3 = this.drawable;
                if (drawable3 instanceof AnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable) drawable3).removeView((View) this);
                }
                this.drawable = drawable2;
                if ((drawable2 instanceof AnimatedEmojiDrawable) && this.attached) {
                    ((AnimatedEmojiDrawable) drawable2).addView((View) this);
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                float f = ((1.0f - this.pressed.set(isPressed() ? 1.0f : 0.0f)) * 0.2f) + 0.8f;
                if (this.drawable != null) {
                    int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
                    int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
                    float width2 = (float) (getWidth() / 2);
                    float f2 = ((float) (width / 2)) * f;
                    float height2 = (float) (((getHeight() - getPaddingBottom()) + getPaddingTop()) / 2);
                    float f3 = ((float) (height / 2)) * f;
                    this.drawable.setBounds((int) (width2 - f2), (int) (height2 - f3), (int) (width2 + f2), (int) (height2 + f3));
                    Drawable drawable2 = this.drawable;
                    if (drawable2 instanceof AnimatedEmojiDrawable) {
                        ((AnimatedEmojiDrawable) drawable2).setTime(System.currentTimeMillis());
                    }
                    this.drawable.draw(canvas);
                }
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                attach();
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                detach();
            }

            public void detach() {
                Drawable drawable2 = this.drawable;
                if (drawable2 instanceof AnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable) drawable2).removeView((View) this);
                }
                this.attached = false;
            }

            public void attach() {
                Drawable drawable2 = this.drawable;
                if (drawable2 instanceof AnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable) drawable2).addView((View) this);
                }
                this.attached = true;
            }
        }

        public Adapter() {
        }

        public long getItemId(int i) {
            return (long) ((MediaDataController.KeywordResult) SuggestEmojiView.this.keywordResults.get(i)).emoji.hashCode();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new EmojiImageView(SuggestEmojiView.this.getContext()));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((EmojiImageView) viewHolder.itemView).setEmoji(((MediaDataController.KeywordResult) SuggestEmojiView.this.keywordResults.get(i)).emoji);
        }

        public int getItemCount() {
            return SuggestEmojiView.this.keywordResults.size();
        }
    }
}
