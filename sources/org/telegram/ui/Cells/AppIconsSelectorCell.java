package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LauncherIconController;

public class AppIconsSelectorCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    public static final float ICONS_ROUND_RADIUS = 18.0f;
    /* access modifiers changed from: private */
    public List<LauncherIconController.LauncherIcon> availableIcons = new ArrayList();
    private int currentAccount;
    private LinearLayoutManager linearLayoutManager;

    public AppIconsSelectorCell(Context context, BaseFragment fragment, int currentAccount2) {
        super(context);
        this.currentAccount = currentAccount2;
        setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        setFocusable(false);
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        setItemAnimator((RecyclerView.ItemAnimator) null);
        setLayoutAnimation((LayoutAnimationController) null);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context, 0, false);
        this.linearLayoutManager = linearLayoutManager2;
        setLayoutManager(linearLayoutManager2);
        setAdapter(new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new IconHolderView(parent.getContext()));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                IconHolderView holderView = (IconHolderView) holder.itemView;
                LauncherIconController.LauncherIcon icon = (LauncherIconController.LauncherIcon) AppIconsSelectorCell.this.availableIcons.get(position);
                holderView.bind(icon);
                holderView.iconView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(18.0f), 0, Theme.getColor("listSelectorSDK21"), -16777216));
                holderView.iconView.setForeground(icon.foreground);
            }

            public int getItemCount() {
                return AppIconsSelectorCell.this.availableIcons.size();
            }
        });
        addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildViewHolder(view).getAdapterPosition();
                if (pos == 0) {
                    outRect.left = AndroidUtilities.dp(18.0f);
                }
                if (pos == AppIconsSelectorCell.this.getAdapter().getItemCount() - 1) {
                    outRect.right = AndroidUtilities.dp(18.0f);
                    return;
                }
                int itemCount = AppIconsSelectorCell.this.getAdapter().getItemCount();
                if (itemCount == 4) {
                    outRect.right = ((AppIconsSelectorCell.this.getWidth() - AndroidUtilities.dp(36.0f)) - (AndroidUtilities.dp(58.0f) * itemCount)) / (itemCount - 1);
                } else {
                    outRect.right = AndroidUtilities.dp(24.0f);
                }
            }
        });
        setOnItemClickListener((RecyclerListView.OnItemClickListener) new AppIconsSelectorCell$$ExternalSyntheticLambda0(this, fragment, context));
        updateIconsVisibility();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-AppIconsSelectorCell  reason: not valid java name */
    public /* synthetic */ void m2780lambda$new$0$orgtelegramuiCellsAppIconsSelectorCell(BaseFragment fragment, Context context, View view, int position) {
        IconHolderView holderView = (IconHolderView) view;
        LauncherIconController.LauncherIcon icon = this.availableIcons.get(position);
        if (icon.premium && !UserConfig.hasPremiumOnAccounts()) {
            fragment.showDialog(new PremiumFeatureBottomSheet(fragment, 10, true));
        } else if (!LauncherIconController.isEnabled(icon)) {
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {
                public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                    return (boxStart - viewStart) + AndroidUtilities.dp(16.0f);
                }

                /* access modifiers changed from: protected */
                public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return super.calculateSpeedPerPixel(displayMetrics) * 3.0f;
                }
            };
            smoothScroller.setTargetPosition(position);
            this.linearLayoutManager.startSmoothScroll(smoothScroller);
            LauncherIconController.setIcon(icon);
            holderView.setSelected(true, true);
            for (int i = 0; i < getChildCount(); i++) {
                IconHolderView otherView = (IconHolderView) getChildAt(i);
                if (otherView != holderView) {
                    otherView.setSelected(false, true);
                }
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 5, icon);
        }
    }

    private void updateIconsVisibility() {
        this.availableIcons.clear();
        this.availableIcons.addAll(Arrays.asList(LauncherIconController.LauncherIcon.values()));
        if (MessagesController.getInstance(this.currentAccount).premiumLocked) {
            int i = 0;
            while (i < this.availableIcons.size()) {
                if (this.availableIcons.get(i).premium) {
                    this.availableIcons.remove(i);
                    i--;
                }
                i++;
            }
        }
        getAdapter().notifyDataSetChanged();
        invalidateItemDecorations();
        for (int i2 = 0; i2 < this.availableIcons.size(); i2++) {
            if (LauncherIconController.isEnabled(this.availableIcons.get(i2))) {
                this.linearLayoutManager.scrollToPositionWithOffset(i2, AndroidUtilities.dp(16.0f));
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateItemDecorations();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthSpec), NUM), heightSpec);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.premiumStatusChangedGlobal) {
            updateIconsVisibility();
        }
    }

    private static final class IconHolderView extends LinearLayout {
        /* access modifiers changed from: private */
        public AdaptiveIconImageView iconView;
        private Paint outlinePaint;
        private float progress;
        private TextView titleView;

        private IconHolderView(Context context) {
            super(context);
            this.outlinePaint = new Paint(1);
            setOrientation(1);
            setWillNotDraw(false);
            AdaptiveIconImageView adaptiveIconImageView = new AdaptiveIconImageView(context);
            this.iconView = adaptiveIconImageView;
            adaptiveIconImageView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            addView(this.iconView, LayoutHelper.createLinear(58, 58, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setSingleLine();
            this.titleView.setTextSize(1, 13.0f);
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            addView(this.titleView, LayoutHelper.createLinear(-2, -2, 1, 0, 4, 0, 0));
            this.outlinePaint.setStyle(Paint.Style.STROKE);
            this.outlinePaint.setStrokeWidth((float) Math.max(2, AndroidUtilities.dp(0.5f)));
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            float stroke = this.outlinePaint.getStrokeWidth();
            AndroidUtilities.rectTmp.set(((float) this.iconView.getLeft()) + stroke, ((float) this.iconView.getTop()) + stroke, ((float) this.iconView.getRight()) - stroke, ((float) this.iconView.getBottom()) - stroke);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f), this.outlinePaint);
        }

        private void setProgress(float progress2) {
            this.progress = progress2;
            this.titleView.setTextColor(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteBlackText"), Theme.getColor("windowBackgroundWhiteValueText"), progress2));
            this.outlinePaint.setColor(ColorUtils.blendARGB(ColorUtils.setAlphaComponent(Theme.getColor("switchTrack"), 63), Theme.getColor("windowBackgroundWhiteValueText"), progress2));
            this.outlinePaint.setStrokeWidth((float) Math.max(2, AndroidUtilities.dp(AndroidUtilities.lerp(0.5f, 2.0f, progress2))));
            invalidate();
        }

        /* access modifiers changed from: private */
        public void setSelected(boolean selected, boolean animate) {
            float to = selected ? 1.0f : 0.0f;
            float f = this.progress;
            if (to == f && animate) {
                return;
            }
            if (animate) {
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{f, to}).setDuration(250);
                animator.setInterpolator(Easings.easeInOutQuad);
                animator.addUpdateListener(new AppIconsSelectorCell$IconHolderView$$ExternalSyntheticLambda0(this));
                animator.start();
                return;
            }
            setProgress(to);
        }

        /* renamed from: lambda$setSelected$0$org-telegram-ui-Cells-AppIconsSelectorCell$IconHolderView  reason: not valid java name */
        public /* synthetic */ void m2781xfd226fea(ValueAnimator animation) {
            setProgress(((Float) animation.getAnimatedValue()).floatValue());
        }

        /* access modifiers changed from: private */
        public void bind(LauncherIconController.LauncherIcon icon) {
            this.iconView.setImageResource(icon.background);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.titleView.getLayoutParams();
            if (!icon.premium || UserConfig.hasPremiumOnAccounts()) {
                params.rightMargin = 0;
                this.titleView.setText(LocaleController.getString(icon.title));
            } else {
                SpannableString str = new SpannableString("d " + LocaleController.getString(icon.title));
                ColoredImageSpan span = new ColoredImageSpan(NUM);
                span.setTopOffset(1);
                span.setSize(AndroidUtilities.dp(13.0f));
                str.setSpan(span, 0, 1, 33);
                params.rightMargin = AndroidUtilities.dp(4.0f);
                this.titleView.setText(str);
            }
            setSelected(LauncherIconController.isEnabled(icon), false);
        }
    }

    public static class AdaptiveIconImageView extends ImageView {
        private int backgroundOuterPadding = AndroidUtilities.dp(42.0f);
        private Drawable foreground;
        private int outerPadding = AndroidUtilities.dp(5.0f);
        private Path path = new Path();

        public AdaptiveIconImageView(Context context) {
            super(context);
        }

        public void setForeground(int res) {
            this.foreground = ContextCompat.getDrawable(getContext(), res);
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            updatePath();
        }

        public void setPadding(int padding) {
            setPadding(padding, padding, padding, padding);
        }

        public void setOuterPadding(int outerPadding2) {
            this.outerPadding = outerPadding2;
        }

        public void setBackgroundOuterPadding(int backgroundOuterPadding2) {
            this.backgroundOuterPadding = backgroundOuterPadding2;
        }

        public void draw(Canvas canvas) {
            canvas.save();
            canvas.clipPath(this.path);
            canvas.scale((((float) this.backgroundOuterPadding) / ((float) getWidth())) + 1.0f, (((float) this.backgroundOuterPadding) / ((float) getHeight())) + 1.0f, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
            super.draw(canvas);
            canvas.restore();
            Drawable drawable = this.foreground;
            if (drawable != null) {
                int i = this.outerPadding;
                drawable.setBounds(-i, -i, getWidth() + this.outerPadding, getHeight() + this.outerPadding);
                this.foreground.draw(canvas);
            }
        }

        private void updatePath() {
            this.path.rewind();
            this.path.addCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, ((float) Math.min((getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom())) / 2.0f, Path.Direction.CW);
        }
    }
}
