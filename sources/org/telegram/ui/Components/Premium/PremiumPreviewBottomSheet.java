package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumFeatureCell;
import org.telegram.ui.PremiumPreviewFragment;

public class PremiumPreviewBottomSheet extends BottomSheetWithRecyclerListView {
    int buttonRow;
    int[] coords = new int[2];
    int currentAccount;
    PremiumFeatureCell dummyCell;
    ValueAnimator enterAnimator;
    boolean enterTransitionInProgress;
    float enterTransitionProgress = 0.0f;
    int featuresEndRow;
    int featuresStartRow;
    BaseFragment fragment;
    PremiumGradient.GradientTools gradientTools;
    int helpUsRow;
    ViewGroup iconContainer;
    GLIconTextureView iconTextureView;
    int paddingRow;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures = new ArrayList<>();
    int rowCount;
    int sectionRow;
    StarParticlesView starParticlesView;
    public float startEnterFromScale;
    public SimpleTextView startEnterFromView;
    public float startEnterFromX;
    public float startEnterFromX1;
    public float startEnterFromY;
    public float startEnterFromY1;
    int totalGradientHeight;
    TLRPC$User user;

    public PremiumPreviewBottomSheet(final BaseFragment baseFragment, final int i, TLRPC$User tLRPC$User) {
        super(baseFragment, false, false);
        this.fragment = baseFragment;
        this.topPadding = 0.26f;
        this.user = tLRPC$User;
        this.currentAccount = i;
        this.dummyCell = new PremiumFeatureCell(getContext());
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, i);
        PremiumGradient.GradientTools gradientTools2 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.gradientTools = gradientTools2;
        gradientTools2.exactly = true;
        gradientTools2.x1 = 0.0f;
        gradientTools2.y1 = 1.0f;
        gradientTools2.x2 = 0.0f;
        gradientTools2.y2 = 0.0f;
        gradientTools2.cx = 0.0f;
        gradientTools2.cy = 0.0f;
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.paddingRow = i2;
        this.featuresStartRow = i3;
        int size = i3 + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        int i4 = size + 1;
        this.rowCount = i4;
        this.sectionRow = size;
        this.rowCount = i4 + 1;
        this.buttonRow = i4;
        this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (view instanceof PremiumFeatureCell) {
                    PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) view;
                    if (premiumFeatureCell.data.type == 0) {
                        PremiumPreviewBottomSheet.this.showDialog(new DoubledLimitsBottomSheet(baseFragment, i));
                        return;
                    }
                    PremiumPreviewBottomSheet.this.showDialog(new PremiumFeatureBottomSheet(baseFragment, premiumFeatureCell.data.type, false));
                }
            }
        });
        MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
    }

    /* access modifiers changed from: private */
    public void showDialog(Dialog dialog) {
        this.iconTextureView.setDialogVisible(true);
        this.starParticlesView.setPaused(true);
        dialog.setOnDismissListener(new PremiumPreviewBottomSheet$$ExternalSyntheticLambda0(this));
        dialog.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$0(DialogInterface dialogInterface) {
        this.iconTextureView.setDialogVisible(false);
        this.starParticlesView.setPaused(false);
    }

    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
        premiumButtonView.setButton(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount), new View.OnClickListener() {
            public void onClick(View view) {
                PremiumPreviewFragment.buyPremium(PremiumPreviewBottomSheet.this.fragment, "profile");
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(getContext());
        View view = new View(getContext());
        view.setBackgroundColor(Theme.getColor("divider"));
        frameLayout2.addView(view, LayoutHelper.createFrame(-1, 1.0f));
        view.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(view, true, 1.0f, false);
        frameLayout2.addView(premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        frameLayout2.setBackgroundColor(getThemedColor("dialogBackground"));
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 68, 80));
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int i, int i2) {
        super.onPreMeasure(i, i2);
        measureGradient(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        this.container.getLocationOnScreen(this.coords);
    }

    /* access modifiers changed from: protected */
    public CharSequence getTitle() {
        return LocaleController.getString("TelegramPremium", NUM);
    }

    /* access modifiers changed from: protected */
    public RecyclerListView.SelectionAdapter createAdapter() {
        return new Adapter();
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onCreateViewHolder$0() {
        }

        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            Context context = viewGroup.getContext();
            if (i == 0) {
                AnonymousClass1 r14 = new LinearLayout(context) {
                    /* access modifiers changed from: protected */
                    public boolean drawChild(Canvas canvas, View view, long j) {
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                        if (view != premiumPreviewBottomSheet.iconTextureView || !premiumPreviewBottomSheet.enterTransitionInProgress) {
                            return super.drawChild(canvas, view, j);
                        }
                        return true;
                    }
                };
                PremiumPreviewBottomSheet.this.iconContainer = r14;
                r14.setOrientation(1);
                PremiumPreviewBottomSheet.this.iconTextureView = new GLIconTextureView(this, context, 1) {
                    /* access modifiers changed from: protected */
                    public void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        setPaused(false);
                    }

                    /* access modifiers changed from: protected */
                    public void onDetachedFromWindow() {
                        super.onDetachedFromWindow();
                        setPaused(true);
                    }
                };
                Bitmap createBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                new Canvas(createBitmap).drawColor(ColorUtils.blendARGB(Theme.getColor("premiumGradient2"), Theme.getColor("dialogBackground"), 0.5f));
                PremiumPreviewBottomSheet.this.iconTextureView.setBackgroundBitmap(createBitmap);
                GLIconRenderer gLIconRenderer = PremiumPreviewBottomSheet.this.iconTextureView.mRenderer;
                gLIconRenderer.colorKey1 = "premiumGradient1";
                gLIconRenderer.colorKey2 = "premiumGradient2";
                gLIconRenderer.updateColors();
                r14.addView(PremiumPreviewBottomSheet.this.iconTextureView, LayoutHelper.createLinear(160, 160, 1));
                TextView textView = new TextView(context);
                textView.setTextSize(1, 16.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setGravity(1);
                textView.setText(LocaleController.getString("TelegramPremium", NUM));
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                r14.addView(textView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 40, 0, 40, 0));
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setGravity(1);
                textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                r14.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 9, 16, 20));
                TLRPC$User tLRPC$User = PremiumPreviewBottomSheet.this.user;
                textView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString("TelegramPremiumUserDialogTitle", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)), PremiumPreviewBottomSheet$Adapter$$ExternalSyntheticLambda0.INSTANCE));
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("TelegramPremiumUserDialogSubtitle", NUM)));
                PremiumPreviewBottomSheet.this.starParticlesView = new StarParticlesView(context);
                AnonymousClass3 r1 = new FrameLayout(context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                        premiumPreviewBottomSheet.starParticlesView.setTranslationY((((float) premiumPreviewBottomSheet.iconTextureView.getTop()) + (((float) PremiumPreviewBottomSheet.this.iconTextureView.getMeasuredHeight()) / 2.0f)) - (((float) PremiumPreviewBottomSheet.this.starParticlesView.getMeasuredHeight()) / 2.0f));
                    }
                };
                r1.setClipChildren(false);
                r1.addView(PremiumPreviewBottomSheet.this.starParticlesView);
                r1.addView(r14);
                StarParticlesView.Drawable drawable = PremiumPreviewBottomSheet.this.starParticlesView.drawable;
                drawable.useGradient = true;
                drawable.init();
                PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                premiumPreviewBottomSheet.iconTextureView.setStarParticlesView(premiumPreviewBottomSheet.starParticlesView);
                view = r1;
            } else if (i == 2) {
                view = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
            } else if (i != 3) {
                view = i != 4 ? new PremiumFeatureCell(context) {
                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set((float) this.imageView.getLeft(), (float) this.imageView.getTop(), (float) this.imageView.getRight(), (float) this.imageView.getBottom());
                        PremiumPreviewBottomSheet.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewBottomSheet.this.totalGradientHeight, 0.0f, (float) (-this.data.yOffset));
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), PremiumPreviewBottomSheet.this.gradientTools.paint);
                        super.dispatchDraw(canvas);
                    }
                } : new AboutPremiumView(context);
            } else {
                view = new View(this, context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), NUM));
                    }
                };
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            int i2 = premiumPreviewBottomSheet.featuresStartRow;
            if (i >= i2 && i < premiumPreviewBottomSheet.featuresEndRow) {
                PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) viewHolder.itemView;
                PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = premiumPreviewBottomSheet.premiumFeatures.get(i - i2);
                boolean z = true;
                if (i == PremiumPreviewBottomSheet.this.featuresEndRow - 1) {
                    z = false;
                }
                premiumFeatureCell.setData(premiumFeatureData, z);
            }
        }

        public int getItemCount() {
            return PremiumPreviewBottomSheet.this.rowCount;
        }

        public int getItemViewType(int i) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            if (i == premiumPreviewBottomSheet.paddingRow) {
                return 0;
            }
            if (i >= premiumPreviewBottomSheet.featuresStartRow && i < premiumPreviewBottomSheet.featuresEndRow) {
                return 1;
            }
            if (i == premiumPreviewBottomSheet.sectionRow) {
                return 2;
            }
            if (i == premiumPreviewBottomSheet.buttonRow) {
                return 3;
            }
            if (i == premiumPreviewBottomSheet.helpUsRow) {
                return 4;
            }
            return super.getItemViewType(i);
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }
    }

    private void measureGradient(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < this.premiumFeatures.size(); i4++) {
            this.dummyCell.setData(this.premiumFeatures.get(i4), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.premiumFeatures.get(i4).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public void mainContainerDispatchDraw(Canvas canvas) {
        super.mainContainerDispatchDraw(canvas);
        if (this.startEnterFromView != null && this.enterTransitionInProgress) {
            canvas.save();
            float[] fArr = {this.startEnterFromX, this.startEnterFromY};
            this.startEnterFromView.getMatrix().mapPoints(fArr);
            Drawable rightDrawable = this.startEnterFromView.getRightDrawable();
            int[] iArr = this.coords;
            float f = ((float) (-iArr[0])) + this.startEnterFromX1 + fArr[0];
            float f2 = ((float) (-iArr[1])) + this.startEnterFromY1 + fArr[1];
            float intrinsicWidth = this.startEnterFromScale * ((float) rightDrawable.getIntrinsicWidth());
            float measuredHeight = ((float) this.iconTextureView.getMeasuredHeight()) * 0.8f;
            float f3 = measuredHeight / intrinsicWidth;
            float f4 = intrinsicWidth / measuredHeight;
            float measuredWidth = ((float) this.iconTextureView.getMeasuredWidth()) / 2.0f;
            for (View view = this.iconTextureView; view != this.container; view = (View) view.getParent()) {
                measuredWidth += view.getX();
            }
            float y = this.iconTextureView.getY() + ((View) this.iconTextureView.getParent()).getY() + ((View) this.iconTextureView.getParent().getParent()).getY() + (((float) this.iconTextureView.getMeasuredHeight()) / 2.0f);
            float lerp = AndroidUtilities.lerp(f, measuredWidth, CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.enterTransitionProgress));
            float lerp2 = AndroidUtilities.lerp(f2, y, this.enterTransitionProgress);
            float f5 = this.startEnterFromScale;
            float f6 = this.enterTransitionProgress;
            float f7 = (f5 * (1.0f - f6)) + (f3 * f6);
            canvas.save();
            canvas.scale(f7, f7, lerp, lerp2);
            int i = (int) lerp;
            int i2 = (int) lerp2;
            rightDrawable.setBounds(i - (rightDrawable.getIntrinsicWidth() / 2), i2 - (rightDrawable.getIntrinsicHeight() / 2), i + (rightDrawable.getIntrinsicWidth() / 2), i2 + (rightDrawable.getIntrinsicHeight() / 2));
            rightDrawable.setAlpha((int) ((1.0f - Utilities.clamp(this.enterTransitionProgress, 1.0f, 0.0f)) * 255.0f));
            rightDrawable.draw(canvas);
            rightDrawable.setAlpha(0);
            canvas.restore();
            float lerp3 = AndroidUtilities.lerp(f4, 1.0f, this.enterTransitionProgress);
            canvas.scale(lerp3, lerp3, lerp, lerp2);
            canvas.translate(lerp - (((float) this.iconTextureView.getMeasuredWidth()) / 2.0f), lerp2 - (((float) this.iconTextureView.getMeasuredHeight()) / 2.0f));
            this.iconTextureView.draw(canvas);
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        if (this.startEnterFromView == null) {
            return true;
        }
        this.enterAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.enterTransitionProgress = 0.0f;
        this.enterTransitionInProgress = true;
        this.iconContainer.invalidate();
        this.startEnterFromView.getRightDrawable().setAlpha(0);
        this.startEnterFromView.invalidate();
        this.iconTextureView.startEnterAnimation(-360, 100);
        this.enterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PremiumPreviewBottomSheet.this.enterTransitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                PremiumPreviewBottomSheet.this.container.invalidate();
            }
        });
        this.enterAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                premiumPreviewBottomSheet.enterTransitionInProgress = false;
                premiumPreviewBottomSheet.enterTransitionProgress = 1.0f;
                premiumPreviewBottomSheet.iconContainer.invalidate();
                ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{0, 255});
                final Drawable rightDrawable = PremiumPreviewBottomSheet.this.startEnterFromView.getRightDrawable();
                ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        rightDrawable.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
                        PremiumPreviewBottomSheet.this.startEnterFromView.invalidate();
                    }
                });
                ofInt.start();
                super.onAnimationEnd(animator);
            }
        });
        this.enterAnimator.setDuration(600);
        this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.enterAnimator.start();
        return super.onCustomOpenAnimation();
    }
}
