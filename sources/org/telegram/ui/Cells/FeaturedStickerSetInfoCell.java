package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    /* access modifiers changed from: private */
    public ProgressButton addButton;
    private AnimatorSet animatorSet;
    private boolean canAddRemove;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public TextView delButton;
    private boolean hasOnClick;
    private TextView infoTextView;
    /* access modifiers changed from: private */
    public boolean isInstalled;
    private boolean isUnread;
    private TextView nameTextView;
    private boolean needDivider;
    private Paint paint = new Paint(1);
    private final Theme.ResourcesProvider resourcesProvider;
    private TLRPC.StickerSetCovered set;
    private int stickerSetNameSearchIndex;
    private int stickerSetNameSearchLength;
    float unreadProgress;
    private CharSequence url;
    private int urlSearchLength;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FeaturedStickerSetInfoCell(Context context, int left, boolean supportRtl, boolean canAddRemove2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        FrameLayout.LayoutParams lp;
        FrameLayout.LayoutParams lp2;
        FrameLayout.LayoutParams lp3;
        FrameLayout.LayoutParams lp4;
        Context context2 = context;
        int i = left;
        boolean z = canAddRemove2;
        this.canAddRemove = z;
        this.resourcesProvider = resourcesProvider2;
        TextView textView = new TextView(context2);
        this.nameTextView = textView;
        textView.setTextColor(getThemedColor("chat_emojiPanelTrendingTitle"));
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        if (supportRtl) {
            lp = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, (float) i, 8.0f, 40.0f, 0.0f);
        } else {
            lp = LayoutHelper.createFrame(-2, -2.0f, 51, (float) i, 8.0f, 40.0f, 0.0f);
        }
        addView(this.nameTextView, lp);
        TextView textView2 = new TextView(context2);
        this.infoTextView = textView2;
        textView2.setTextColor(getThemedColor("chat_emojiPanelTrendingDescription"));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        if (supportRtl) {
            lp2 = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, (float) i, 30.0f, 100.0f, 0.0f);
        } else {
            lp2 = LayoutHelper.createFrame(-2, -2.0f, 51, (float) i, 30.0f, 100.0f, 0.0f);
        }
        addView(this.infoTextView, lp2);
        if (z) {
            ProgressButton progressButton = new ProgressButton(context2);
            this.addButton = progressButton;
            progressButton.setTextColor(getThemedColor("featuredStickers_buttonText"));
            this.addButton.setText(LocaleController.getString("Add", NUM));
            if (supportRtl) {
                lp3 = LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f);
            } else {
                lp3 = LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f);
            }
            addView(this.addButton, lp3);
            TextView textView3 = new TextView(context2);
            this.delButton = textView3;
            textView3.setGravity(17);
            this.delButton.setTextColor(getThemedColor("featuredStickers_removeButtonText"));
            this.delButton.setTextSize(1, 14.0f);
            this.delButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.delButton.setText(LocaleController.getString("StickersRemove", NUM));
            if (supportRtl) {
                lp4 = LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f);
            } else {
                lp4 = LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f);
            }
            addView(this.delButton, lp4);
        }
        setWillNotDraw(false);
        updateColors();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
        if (this.canAddRemove) {
            int width = this.addButton.getMeasuredWidth();
            int width2 = this.delButton.getMeasuredWidth();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.delButton.getLayoutParams();
            if (width2 < width) {
                layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((width - width2) / 2);
            } else {
                layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
            }
            measureChildWithMargins(this.nameTextView, widthMeasureSpec, width, heightMeasureSpec, 0);
        }
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        if (this.canAddRemove) {
            this.hasOnClick = true;
            this.addButton.setOnClickListener(onClickListener);
            this.delButton.setOnClickListener(onClickListener);
        }
    }

    public void setAddDrawProgress(boolean drawProgress, boolean animated) {
        if (this.canAddRemove) {
            this.addButton.setDrawProgress(drawProgress, animated);
        }
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread) {
        setStickerSet(stickerSet, unread, false, 0, 0, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread, boolean animated) {
        setStickerSet(stickerSet, unread, animated, 0, 0, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread, boolean animated, int index, int searchLength) {
        setStickerSet(stickerSet, unread, animated, index, searchLength, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread, boolean animated, int index, int searchLength, boolean forceInstalled) {
        TLRPC.StickerSetCovered stickerSetCovered = stickerSet;
        boolean z = unread;
        int i = searchLength;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        float f = 1.0f;
        if (this.set != stickerSetCovered) {
            this.unreadProgress = z ? 1.0f : 0.0f;
            invalidate();
        }
        this.set = stickerSetCovered;
        this.stickerSetNameSearchIndex = index;
        this.stickerSetNameSearchLength = i;
        if (i != 0) {
            updateStickerSetNameSearchSpan();
        } else {
            this.nameTextView.setText(stickerSetCovered.set.title);
        }
        this.infoTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count, new Object[0]));
        this.isUnread = z;
        if (!this.canAddRemove) {
            return;
        }
        if (this.hasOnClick) {
            this.addButton.setVisibility(0);
            boolean z2 = forceInstalled || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
            this.isInstalled = z2;
            if (animated) {
                if (z2) {
                    this.delButton.setVisibility(0);
                } else {
                    this.addButton.setVisibility(0);
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.setDuration(250);
                AnimatorSet animatorSet4 = this.animatorSet;
                Animator[] animatorArr = new Animator[6];
                TextView textView = this.delButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, property, fArr);
                TextView textView2 = this.delButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView2, property2, fArr2);
                TextView textView3 = this.delButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView3, property3, fArr3);
                ProgressButton progressButton = this.addButton;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = this.isInstalled ? 0.0f : 1.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(progressButton, property4, fArr4);
                ProgressButton progressButton2 = this.addButton;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = this.isInstalled ? 0.0f : 1.0f;
                animatorArr[4] = ObjectAnimator.ofFloat(progressButton2, property5, fArr5);
                ProgressButton progressButton3 = this.addButton;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                if (this.isInstalled) {
                    f = 0.0f;
                }
                fArr6[0] = f;
                animatorArr[5] = ObjectAnimator.ofFloat(progressButton3, property6, fArr6);
                animatorSet4.playTogether(animatorArr);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FeaturedStickerSetInfoCell.this.isInstalled) {
                            FeaturedStickerSetInfoCell.this.addButton.setVisibility(4);
                        } else {
                            FeaturedStickerSetInfoCell.this.delButton.setVisibility(4);
                        }
                    }
                });
                this.animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                this.animatorSet.start();
            } else if (z2) {
                this.delButton.setVisibility(0);
                this.delButton.setAlpha(1.0f);
                this.delButton.setScaleX(1.0f);
                this.delButton.setScaleY(1.0f);
                this.addButton.setVisibility(4);
                this.addButton.setAlpha(0.0f);
                this.addButton.setScaleX(0.0f);
                this.addButton.setScaleY(0.0f);
            } else {
                this.addButton.setVisibility(0);
                this.addButton.setAlpha(1.0f);
                this.addButton.setScaleX(1.0f);
                this.addButton.setScaleY(1.0f);
                this.delButton.setVisibility(4);
                this.delButton.setAlpha(0.0f);
                this.delButton.setScaleX(0.0f);
                this.delButton.setScaleY(0.0f);
            }
        } else {
            this.addButton.setVisibility(8);
        }
    }

    private void updateStickerSetNameSearchSpan() {
        if (this.stickerSetNameSearchLength != 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.set.set.title);
            try {
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getThemedColor("windowBackgroundWhiteBlueText4"));
                int i = this.stickerSetNameSearchIndex;
                builder.setSpan(foregroundColorSpan, i, this.stickerSetNameSearchLength + i, 33);
            } catch (Exception e) {
            }
            this.nameTextView.setText(builder);
        }
    }

    public void setUrl(CharSequence text, int searchLength) {
        this.url = text;
        this.urlSearchLength = searchLength;
        updateUrlSearchSpan();
    }

    private void updateUrlSearchSpan() {
        if (this.url != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.url);
            try {
                builder.setSpan(new ColorSpanUnderline(getThemedColor("windowBackgroundWhiteBlueText4")), 0, this.urlSearchLength, 33);
                builder.setSpan(new ColorSpanUnderline(getThemedColor("chat_emojiPanelTrendingDescription")), this.urlSearchLength, this.url.length(), 33);
            } catch (Exception e) {
            }
            this.infoTextView.setText(builder);
        }
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.set;
    }

    public boolean isNeedDivider() {
        return this.needDivider;
    }

    public void setNeedDivider(boolean needDivider2) {
        this.needDivider = needDivider2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean z = this.isUnread;
        if (z || this.unreadProgress != 0.0f) {
            if (z) {
                float f = this.unreadProgress;
                if (f != 1.0f) {
                    float f2 = f + 0.16f;
                    this.unreadProgress = f2;
                    if (f2 > 1.0f) {
                        this.unreadProgress = 1.0f;
                    } else {
                        invalidate();
                    }
                    this.paint.setColor(getThemedColor("featuredStickers_unread"));
                    canvas.drawCircle((float) (this.nameTextView.getRight() + AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(20.0f), ((float) AndroidUtilities.dp(4.0f)) * this.unreadProgress, this.paint);
                }
            }
            if (!z) {
                float f3 = this.unreadProgress;
                if (f3 != 0.0f) {
                    float f4 = f3 - 0.16f;
                    this.unreadProgress = f4;
                    if (f4 < 0.0f) {
                        this.unreadProgress = 0.0f;
                    } else {
                        invalidate();
                    }
                }
            }
            this.paint.setColor(getThemedColor("featuredStickers_unread"));
            canvas.drawCircle((float) (this.nameTextView.getRight() + AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(20.0f), ((float) AndroidUtilities.dp(4.0f)) * this.unreadProgress, this.paint);
        }
        if (this.needDivider) {
            canvas.drawLine(0.0f, 0.0f, (float) getWidth(), 0.0f, Theme.dividerPaint);
        }
    }

    public void updateColors() {
        if (this.canAddRemove) {
            this.addButton.setProgressColor(getThemedColor("featuredStickers_buttonProgress"));
            this.addButton.setBackgroundRoundRect(getThemedColor("featuredStickers_addButton"), getThemedColor("featuredStickers_addButtonPressed"));
        }
        updateStickerSetNameSearchSpan();
        updateUrlSearchSpan();
    }

    public static void createThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate) {
        List<ThemeDescription> list = descriptions;
        Class<FeaturedStickerSetInfoCell> cls = FeaturedStickerSetInfoCell.class;
        list.add(new ThemeDescription((View) listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{cls}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_emojiPanelTrendingTitle"));
        RecyclerListView recyclerListView = listView;
        list.add(new ThemeDescription((View) recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{cls}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_emojiPanelTrendingDescription"));
        RecyclerListView recyclerListView2 = listView;
        list.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{cls}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        list.add(new ThemeDescription((View) recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{cls}, new String[]{"delButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_removeButtonText"));
        list.add(new ThemeDescription(recyclerListView2, 0, new Class[]{cls}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_unread"));
        RecyclerListView recyclerListView3 = listView;
        list.add(new ThemeDescription(recyclerListView3, 0, new Class[]{cls}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        list.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, delegate, "featuredStickers_buttonProgress"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = delegate;
        list.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "featuredStickers_addButton"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = delegate;
        list.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "featuredStickers_addButtonPressed"));
        list.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText4"));
        list.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "chat_emojiPanelTrendingDescription"));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
