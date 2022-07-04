package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.TextViewSwitcher;

public class DialogsEmptyCell extends LinearLayout {
    public static final int TYPE_FILTER_ADDING_CHATS = 3;
    public static final int TYPE_FILTER_NO_CHATS_TO_DISPLAY = 2;
    private static final int TYPE_UNSPECIFIED = -1;
    public static final int TYPE_WELCOME_NO_CONTACTS = 0;
    public static final int TYPE_WELCOME_WITH_CONTACTS = 1;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType = -1;
    private RLottieImageView imageView;
    /* access modifiers changed from: private */
    public Runnable onUtyanAnimationEndListener;
    private Consumer<Float> onUtyanAnimationUpdateListener;
    private int prevIcon;
    private TextViewSwitcher subtitleView;
    private TextView titleView;
    private boolean utyanAnimationTriggered;
    /* access modifiers changed from: private */
    public ValueAnimator utyanAnimator;
    private float utyanCollapseProgress;

    @Retention(RetentionPolicy.SOURCE)
    public @interface EmptyType {
    }

    public DialogsEmptyCell(Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener(DialogsEmptyCell$$ExternalSyntheticLambda3.INSTANCE);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 52.0f, 4.0f, 52.0f, 0.0f));
        this.imageView.setOnClickListener(new DialogsEmptyCell$$ExternalSyntheticLambda2(this));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor("chats_nameMessage_threeLines"));
        this.titleView.setTextSize(1, 20.0f);
        this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleView.setGravity(17);
        addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 10.0f, 52.0f, 0.0f));
        TextViewSwitcher textViewSwitcher = new TextViewSwitcher(context);
        this.subtitleView = textViewSwitcher;
        textViewSwitcher.setFactory(new DialogsEmptyCell$$ExternalSyntheticLambda4(context));
        this.subtitleView.setInAnimation(context, NUM);
        this.subtitleView.setOutAnimation(context, NUM);
        addView(this.subtitleView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 7.0f, 52.0f, 0.0f));
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-DialogsEmptyCell  reason: not valid java name */
    public /* synthetic */ void m2793lambda$new$1$orgtelegramuiCellsDialogsEmptyCell(View v) {
        if (!this.imageView.isPlaying()) {
            this.imageView.setProgress(0.0f);
            this.imageView.playAnimation();
        }
    }

    static /* synthetic */ View lambda$new$2(Context context) {
        TextView tv = new TextView(context);
        tv.setTextColor(Theme.getColor("chats_message"));
        tv.setTextSize(1, 14.0f);
        tv.setGravity(17);
        tv.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        return tv;
    }

    public void setOnUtyanAnimationEndListener(Runnable onUtyanAnimationEndListener2) {
        this.onUtyanAnimationEndListener = onUtyanAnimationEndListener2;
    }

    public void setOnUtyanAnimationUpdateListener(Consumer<Float> onUtyanAnimationUpdateListener2) {
        this.onUtyanAnimationUpdateListener = onUtyanAnimationUpdateListener2;
    }

    public void setType(int value) {
        String help;
        int icon;
        if (this.currentType != value) {
            this.currentType = value;
            switch (value) {
                case 0:
                case 1:
                    icon = NUM;
                    help = LocaleController.getString("NoChatsHelp", NUM);
                    this.titleView.setText(LocaleController.getString("NoChats", NUM));
                    break;
                case 2:
                    this.imageView.setAutoRepeat(false);
                    icon = NUM;
                    help = LocaleController.getString("FilterNoChatsToDisplayInfo", NUM);
                    this.titleView.setText(LocaleController.getString("FilterNoChatsToDisplay", NUM));
                    break;
                default:
                    this.imageView.setAutoRepeat(true);
                    icon = NUM;
                    help = LocaleController.getString("FilterAddingChatsInfo", NUM);
                    this.titleView.setText(LocaleController.getString("FilterAddingChats", NUM));
                    break;
            }
            if (icon != 0) {
                this.imageView.setVisibility(0);
                if (this.currentType == 1) {
                    if (isUtyanAnimationTriggered()) {
                        this.utyanCollapseProgress = 1.0f;
                        String noChatsContactsHelp = LocaleController.getString("NoChatsContactsHelp", NUM);
                        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                            noChatsContactsHelp = noChatsContactsHelp.replace(10, ' ');
                        }
                        this.subtitleView.setText(noChatsContactsHelp, true);
                        requestLayout();
                    } else {
                        startUtyanCollapseAnimation(true);
                    }
                }
                if (this.prevIcon != icon) {
                    this.imageView.setAnimation(icon, 100, 100);
                    this.imageView.playAnimation();
                    this.prevIcon = icon;
                }
            } else {
                this.imageView.setVisibility(8);
            }
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace(10, ' ');
            }
            this.subtitleView.setText(help, false);
        }
    }

    public boolean isUtyanAnimationTriggered() {
        return this.utyanAnimationTriggered;
    }

    public void startUtyanExpandAnimation() {
        ValueAnimator valueAnimator = this.utyanAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.utyanAnimationTriggered = false;
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.utyanCollapseProgress, 0.0f}).setDuration(250);
        this.utyanAnimator = duration;
        duration.setInterpolator(Easings.easeOutQuad);
        this.utyanAnimator.addUpdateListener(new DialogsEmptyCell$$ExternalSyntheticLambda1(this));
        this.utyanAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (DialogsEmptyCell.this.onUtyanAnimationEndListener != null) {
                    DialogsEmptyCell.this.onUtyanAnimationEndListener.run();
                }
                if (animation == DialogsEmptyCell.this.utyanAnimator) {
                    ValueAnimator unused = DialogsEmptyCell.this.utyanAnimator = null;
                }
            }
        });
        this.utyanAnimator.start();
    }

    /* renamed from: lambda$startUtyanExpandAnimation$3$org-telegram-ui-Cells-DialogsEmptyCell  reason: not valid java name */
    public /* synthetic */ void m2795x3a8422e3(ValueAnimator animation) {
        this.utyanCollapseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        requestLayout();
        Consumer<Float> consumer = this.onUtyanAnimationUpdateListener;
        if (consumer != null) {
            consumer.accept(Float.valueOf(this.utyanCollapseProgress));
        }
    }

    public void startUtyanCollapseAnimation(boolean changeContactsHelp) {
        ValueAnimator valueAnimator = this.utyanAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.utyanAnimationTriggered = true;
        if (changeContactsHelp) {
            String noChatsContactsHelp = LocaleController.getString("NoChatsContactsHelp", NUM);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                noChatsContactsHelp = noChatsContactsHelp.replace(10, ' ');
            }
            this.subtitleView.setText(noChatsContactsHelp, true);
        }
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.utyanCollapseProgress, 1.0f}).setDuration(250);
        this.utyanAnimator = duration;
        duration.setInterpolator(Easings.easeOutQuad);
        this.utyanAnimator.addUpdateListener(new DialogsEmptyCell$$ExternalSyntheticLambda0(this));
        this.utyanAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (DialogsEmptyCell.this.onUtyanAnimationEndListener != null) {
                    DialogsEmptyCell.this.onUtyanAnimationEndListener.run();
                }
                if (animation == DialogsEmptyCell.this.utyanAnimator) {
                    ValueAnimator unused = DialogsEmptyCell.this.utyanAnimator = null;
                }
            }
        });
        this.utyanAnimator.start();
    }

    /* renamed from: lambda$startUtyanCollapseAnimation$4$org-telegram-ui-Cells-DialogsEmptyCell  reason: not valid java name */
    public /* synthetic */ void m2794x5deb8595(ValueAnimator animation) {
        this.utyanCollapseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        requestLayout();
        Consumer<Float> consumer = this.onUtyanAnimationUpdateListener;
        if (consumer != null) {
            consumer.accept(Float.valueOf(this.utyanCollapseProgress));
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateLayout();
    }

    public void offsetTopAndBottom(int offset) {
        super.offsetTopAndBottom(offset);
        updateLayout();
    }

    public void updateLayout() {
        int i;
        int offset = 0;
        if ((getParent() instanceof View) && (((i = this.currentType) == 2 || i == 3) && ((View) getParent()).getPaddingTop() != 0)) {
            offset = 0 - (getTop() / 2);
        }
        int i2 = this.currentType;
        if (i2 == 0 || i2 == 1) {
            offset = (int) (((float) offset) - (((float) ((int) (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f))) * (1.0f - this.utyanCollapseProgress)));
        }
        this.imageView.setTranslationY((float) offset);
        this.titleView.setTranslationY((float) offset);
        this.subtitleView.setTranslationY((float) offset);
    }

    private int measureUtyanHeight(int heightMeasureSpec) {
        int totalHeight;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            totalHeight = view.getMeasuredHeight();
            if (view.getPaddingTop() != 0 && Build.VERSION.SDK_INT >= 21) {
                totalHeight -= AndroidUtilities.statusBarHeight;
            }
        } else {
            totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        }
        if (totalHeight == 0) {
            totalHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        if (getParent() instanceof BlurredRecyclerView) {
            totalHeight -= ((BlurredRecyclerView) getParent()).blurTopPadding;
        }
        return (int) (((float) totalHeight) + (((float) (AndroidUtilities.dp(320.0f) - totalHeight)) * this.utyanCollapseProgress));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalHeight;
        int i = this.currentType;
        if (i == 0 || i == 1) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(measureUtyanHeight(heightMeasureSpec), NUM));
        } else if (i == 2 || i == 3) {
            if (getParent() instanceof View) {
                View view = (View) getParent();
                totalHeight = view.getMeasuredHeight();
                if (view.getPaddingTop() != 0 && Build.VERSION.SDK_INT >= 21) {
                    totalHeight -= AndroidUtilities.statusBarHeight;
                }
            } else {
                totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            }
            if (totalHeight == 0) {
                totalHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            }
            if (getParent() instanceof BlurredRecyclerView) {
                totalHeight -= ((BlurredRecyclerView) getParent()).blurTopPadding;
            }
            ArrayList<TLRPC.RecentMeUrl> arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                totalHeight -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(totalHeight, NUM));
        } else {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(166.0f), NUM));
        }
    }
}
