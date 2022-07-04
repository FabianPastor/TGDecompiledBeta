package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.PremiumGradient;
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
    TLRPC.User user;

    public PremiumPreviewBottomSheet(final BaseFragment fragment2, final int currentAccount2, TLRPC.User user2) {
        super(fragment2, false, false);
        this.fragment = fragment2;
        this.topPadding = 0.26f;
        this.user = user2;
        this.currentAccount = currentAccount2;
        this.dummyCell = new PremiumFeatureCell(getContext());
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, currentAccount2);
        PremiumGradient.GradientTools gradientTools2 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.gradientTools = gradientTools2;
        gradientTools2.exactly = true;
        this.gradientTools.x1 = 0.0f;
        this.gradientTools.y1 = 1.0f;
        this.gradientTools.x2 = 0.0f;
        this.gradientTools.y2 = 0.0f;
        this.gradientTools.cx = 0.0f;
        this.gradientTools.cy = 0.0f;
        int i = this.rowCount;
        int i2 = i + 1;
        this.rowCount = i2;
        this.paddingRow = i;
        this.featuresStartRow = i2;
        int size = i2 + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        this.rowCount = size + 1;
        this.sectionRow = size;
        if (!UserConfig.getInstance(currentAccount2).isPremium()) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.buttonRow = i3;
        }
        this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (view instanceof PremiumFeatureCell) {
                    PremiumFeatureCell cell = (PremiumFeatureCell) view;
                    PremiumPreviewFragment.sentShowFeaturePreview(currentAccount2, cell.data.type);
                    if (cell.data.type == 0) {
                        PremiumPreviewBottomSheet.this.showDialog(new DoubledLimitsBottomSheet(fragment2, currentAccount2));
                        return;
                    }
                    PremiumPreviewBottomSheet.this.showDialog(new PremiumFeatureBottomSheet(fragment2, cell.data.type, false));
                }
            }
        });
        MediaDataController.getInstance(currentAccount2).preloadPremiumPreviewStickers();
        PremiumPreviewFragment.sentShowScreenStat("profile");
    }

    /* access modifiers changed from: private */
    public void showDialog(Dialog dialog) {
        this.iconTextureView.setDialogVisible(true);
        this.starParticlesView.setPaused(true);
        dialog.setOnDismissListener(new PremiumPreviewBottomSheet$$ExternalSyntheticLambda0(this));
        dialog.show();
    }

    /* renamed from: lambda$showDialog$0$org-telegram-ui-Components-Premium-PremiumPreviewBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1254xbc2e2CLASSNAME(DialogInterface dialog1) {
        this.iconTextureView.setDialogVisible(false);
        this.starParticlesView.setPaused(false);
    }

    public void onViewCreated(FrameLayout containerView) {
        super.onViewCreated(containerView);
        PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
        premiumButtonView.setButton(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount), new View.OnClickListener() {
            public void onClick(View v) {
                PremiumPreviewFragment.sentPremiumButtonClick();
                PremiumPreviewFragment.buyPremium(PremiumPreviewBottomSheet.this.fragment, "profile");
            }
        });
        FrameLayout buttonContainer = new FrameLayout(getContext());
        View buttonDivider = new View(getContext());
        buttonDivider.setBackgroundColor(Theme.getColor("divider"));
        buttonContainer.addView(buttonDivider, LayoutHelper.createFrame(-1, 1.0f));
        buttonDivider.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(buttonDivider, true, 1.0f, false);
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            buttonContainer.addView(premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
            buttonContainer.setBackgroundColor(getThemedColor("dialogBackground"));
            containerView.addView(buttonContainer, LayoutHelper.createFrame(-1, 68, 80));
        }
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onPreMeasure(widthMeasureSpec, heightMeasureSpec);
        measureGradient(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
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
        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 0:
                    LinearLayout linearLayout = new LinearLayout(context) {
                        /* access modifiers changed from: protected */
                        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                            if (child != PremiumPreviewBottomSheet.this.iconTextureView || !PremiumPreviewBottomSheet.this.enterTransitionInProgress) {
                                return super.drawChild(canvas, child, drawingTime);
                            }
                            return true;
                        }
                    };
                    PremiumPreviewBottomSheet.this.iconContainer = linearLayout;
                    linearLayout.setOrientation(1);
                    PremiumPreviewBottomSheet.this.iconTextureView = new GLIconTextureView(context, 1) {
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
                    Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                    new Canvas(bitmap).drawColor(ColorUtils.blendARGB(Theme.getColor("premiumGradient2"), Theme.getColor("dialogBackground"), 0.5f));
                    PremiumPreviewBottomSheet.this.iconTextureView.setBackgroundBitmap(bitmap);
                    PremiumPreviewBottomSheet.this.iconTextureView.mRenderer.colorKey1 = "premiumGradient1";
                    PremiumPreviewBottomSheet.this.iconTextureView.mRenderer.colorKey2 = "premiumGradient2";
                    PremiumPreviewBottomSheet.this.iconTextureView.mRenderer.updateColors();
                    linearLayout.addView(PremiumPreviewBottomSheet.this.iconTextureView, LayoutHelper.createLinear(160, 160, 1));
                    TextView titleView = new TextView(context);
                    titleView.setTextSize(1, 16.0f);
                    titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    titleView.setGravity(1);
                    titleView.setText(LocaleController.getString("TelegramPremium", NUM));
                    titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    titleView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                    linearLayout.addView(titleView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 40, 0, 40, 0));
                    TextView subtitleView = new TextView(context);
                    subtitleView.setTextSize(1, 14.0f);
                    subtitleView.setGravity(1);
                    subtitleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    linearLayout.addView(subtitleView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 9, 16, 20));
                    titleView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString("TelegramPremiumUserDialogTitle", NUM, ContactsController.formatName(PremiumPreviewBottomSheet.this.user.first_name, PremiumPreviewBottomSheet.this.user.last_name)), PremiumPreviewBottomSheet$Adapter$$ExternalSyntheticLambda0.INSTANCE));
                    subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString("TelegramPremiumUserDialogSubtitle", NUM)));
                    PremiumPreviewBottomSheet.this.starParticlesView = new StarParticlesView(context);
                    FrameLayout frameLayout = new FrameLayout(context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            PremiumPreviewBottomSheet.this.starParticlesView.setTranslationY((((float) PremiumPreviewBottomSheet.this.iconTextureView.getTop()) + (((float) PremiumPreviewBottomSheet.this.iconTextureView.getMeasuredHeight()) / 2.0f)) - (((float) PremiumPreviewBottomSheet.this.starParticlesView.getMeasuredHeight()) / 2.0f));
                        }
                    };
                    frameLayout.setClipChildren(false);
                    frameLayout.addView(PremiumPreviewBottomSheet.this.starParticlesView);
                    frameLayout.addView(linearLayout);
                    PremiumPreviewBottomSheet.this.starParticlesView.drawable.useGradient = true;
                    PremiumPreviewBottomSheet.this.starParticlesView.drawable.init();
                    PremiumPreviewBottomSheet.this.iconTextureView.setStarParticlesView(PremiumPreviewBottomSheet.this.starParticlesView);
                    view = frameLayout;
                    break;
                case 2:
                    view = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
                    break;
                case 3:
                    view = new View(context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), NUM));
                        }
                    };
                    break;
                case 4:
                    view = new AboutPremiumView(context);
                    break;
                default:
                    view = new PremiumFeatureCell(context) {
                        /* access modifiers changed from: protected */
                        public void dispatchDraw(Canvas canvas) {
                            AndroidUtilities.rectTmp.set((float) this.imageView.getLeft(), (float) this.imageView.getTop(), (float) this.imageView.getRight(), (float) this.imageView.getBottom());
                            PremiumPreviewBottomSheet.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewBottomSheet.this.totalGradientHeight, 0.0f, (float) (-this.data.yOffset));
                            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), PremiumPreviewBottomSheet.this.gradientTools.paint);
                            super.dispatchDraw(canvas);
                        }
                    };
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        static /* synthetic */ void lambda$onCreateViewHolder$0() {
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position >= PremiumPreviewBottomSheet.this.featuresStartRow && position < PremiumPreviewBottomSheet.this.featuresEndRow) {
                PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) holder.itemView;
                PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = PremiumPreviewBottomSheet.this.premiumFeatures.get(position - PremiumPreviewBottomSheet.this.featuresStartRow);
                boolean z = true;
                if (position == PremiumPreviewBottomSheet.this.featuresEndRow - 1) {
                    z = false;
                }
                premiumFeatureCell.setData(premiumFeatureData, z);
            }
        }

        public int getItemCount() {
            return PremiumPreviewBottomSheet.this.rowCount;
        }

        public int getItemViewType(int position) {
            if (position == PremiumPreviewBottomSheet.this.paddingRow) {
                return 0;
            }
            if (position >= PremiumPreviewBottomSheet.this.featuresStartRow && position < PremiumPreviewBottomSheet.this.featuresEndRow) {
                return 1;
            }
            if (position == PremiumPreviewBottomSheet.this.sectionRow) {
                return 2;
            }
            if (position == PremiumPreviewBottomSheet.this.buttonRow) {
                return 3;
            }
            if (position == PremiumPreviewBottomSheet.this.helpUsRow) {
                return 4;
            }
            return super.getItemViewType(position);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }
    }

    private void measureGradient(int w, int h) {
        int yOffset = 0;
        for (int i = 0; i < this.premiumFeatures.size(); i++) {
            this.dummyCell.setData(this.premiumFeatures.get(i), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(w, NUM), View.MeasureSpec.makeMeasureSpec(h, Integer.MIN_VALUE));
            this.premiumFeatures.get(i).yOffset = yOffset;
            yOffset += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = yOffset;
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

    /* JADX WARNING: type inference failed for: r15v7, types: [android.view.ViewParent] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void mainContainerDispatchDraw(android.graphics.Canvas r24) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            super.mainContainerDispatchDraw(r24)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.startEnterFromView
            if (r2 == 0) goto L_0x014f
            boolean r2 = r0.enterTransitionInProgress
            if (r2 == 0) goto L_0x014f
            r24.save()
            r2 = 2
            float[] r3 = new float[r2]
            float r4 = r0.startEnterFromX
            r5 = 0
            r3[r5] = r4
            float r4 = r0.startEnterFromY
            r6 = 1
            r3[r6] = r4
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.startEnterFromView
            android.graphics.Matrix r4 = r4.getMatrix()
            r4.mapPoints(r3)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.startEnterFromView
            android.graphics.drawable.Drawable r4 = r4.getRightDrawable()
            int[] r7 = r0.coords
            r8 = r7[r5]
            int r8 = -r8
            float r8 = (float) r8
            float r9 = r0.startEnterFromX1
            float r8 = r8 + r9
            r9 = r3[r5]
            float r8 = r8 + r9
            r7 = r7[r6]
            int r7 = -r7
            float r7 = (float) r7
            float r9 = r0.startEnterFromY1
            float r7 = r7 + r9
            r6 = r3[r6]
            float r7 = r7 + r6
            float r6 = r0.startEnterFromScale
            int r9 = r4.getIntrinsicWidth()
            float r9 = (float) r9
            float r6 = r6 * r9
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r9 = r0.iconTextureView
            int r9 = r9.getMeasuredHeight()
            float r9 = (float) r9
            r10 = 1061997773(0x3f4ccccd, float:0.8)
            float r9 = r9 * r10
            float r10 = r9 / r6
            float r11 = r6 / r9
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r12 = r0.iconTextureView
            int r12 = r12.getMeasuredWidth()
            float r12 = (float) r12
            r13 = 1073741824(0x40000000, float:2.0)
            float r12 = r12 / r13
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r14 = r0.iconTextureView
        L_0x0069:
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r15 = r0.container
            if (r14 == r15) goto L_0x007a
            float r15 = r14.getX()
            float r12 = r12 + r15
            android.view.ViewParent r15 = r14.getParent()
            r14 = r15
            android.view.View r14 = (android.view.View) r14
            goto L_0x0069
        L_0x007a:
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r15 = r0.iconTextureView
            float r15 = r15.getY()
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r5 = r0.iconTextureView
            android.view.ViewParent r5 = r5.getParent()
            android.view.View r5 = (android.view.View) r5
            float r5 = r5.getY()
            float r15 = r15 + r5
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r5 = r0.iconTextureView
            android.view.ViewParent r5 = r5.getParent()
            android.view.ViewParent r5 = r5.getParent()
            android.view.View r5 = (android.view.View) r5
            float r5 = r5.getY()
            float r15 = r15 + r5
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r5 = r0.iconTextureView
            int r5 = r5.getMeasuredHeight()
            float r5 = (float) r5
            float r5 = r5 / r13
            float r15 = r15 + r5
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r13 = r0.enterTransitionProgress
            float r5 = r5.getInterpolation(r13)
            float r5 = org.telegram.messenger.AndroidUtilities.lerp((float) r8, (float) r12, (float) r5)
            float r13 = r0.enterTransitionProgress
            float r13 = org.telegram.messenger.AndroidUtilities.lerp((float) r7, (float) r15, (float) r13)
            if (r4 == 0) goto L_0x0146
            float r2 = r0.startEnterFromScale
            r17 = r3
            float r3 = r0.enterTransitionProgress
            r18 = r6
            r6 = 1065353216(0x3var_, float:1.0)
            float r19 = r6 - r3
            float r2 = r2 * r19
            float r3 = r3 * r10
            float r2 = r2 + r3
            r24.save()
            r1.scale(r2, r2, r5, r13)
            int r3 = (int) r5
            int r19 = r4.getIntrinsicWidth()
            r16 = 2
            int r19 = r19 / 2
            int r3 = r3 - r19
            int r6 = (int) r13
            int r20 = r4.getIntrinsicHeight()
            int r20 = r20 / 2
            int r6 = r6 - r20
            r20 = r2
            int r2 = (int) r5
            int r21 = r4.getIntrinsicWidth()
            int r21 = r21 / 2
            int r2 = r2 + r21
            r21 = r7
            int r7 = (int) r13
            int r22 = r4.getIntrinsicHeight()
            int r22 = r22 / 2
            int r7 = r7 + r22
            r4.setBounds(r3, r6, r2, r7)
            r2 = 1132396544(0x437var_, float:255.0)
            float r3 = r0.enterTransitionProgress
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            float r3 = org.telegram.messenger.Utilities.clamp(r3, r7, r6)
            float r6 = r7 - r3
            float r6 = r6 * r2
            int r2 = (int) r6
            r4.setAlpha(r2)
            r4.draw(r1)
            r2 = 0
            r4.setAlpha(r2)
            r24.restore()
            float r2 = r0.enterTransitionProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = org.telegram.messenger.AndroidUtilities.lerp((float) r11, (float) r3, (float) r2)
            r1.scale(r2, r2, r5, r13)
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r3 = r0.iconTextureView
            int r3 = r3.getMeasuredWidth()
            float r3 = (float) r3
            r6 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r6
            float r3 = r5 - r3
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r7 = r0.iconTextureView
            int r7 = r7.getMeasuredHeight()
            float r7 = (float) r7
            float r7 = r7 / r6
            float r6 = r13 - r7
            r1.translate(r3, r6)
            org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r3 = r0.iconTextureView
            r3.draw(r1)
            goto L_0x014c
        L_0x0146:
            r17 = r3
            r18 = r6
            r21 = r7
        L_0x014c:
            r24.restore()
        L_0x014f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.mainContainerDispatchDraw(android.graphics.Canvas):void");
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
            public void onAnimationUpdate(ValueAnimator animation) {
                PremiumPreviewBottomSheet.this.enterTransitionProgress = ((Float) animation.getAnimatedValue()).floatValue();
                PremiumPreviewBottomSheet.this.container.invalidate();
            }
        });
        this.enterAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                PremiumPreviewBottomSheet.this.enterTransitionInProgress = false;
                PremiumPreviewBottomSheet.this.enterTransitionProgress = 1.0f;
                PremiumPreviewBottomSheet.this.iconContainer.invalidate();
                ValueAnimator iconAlphaBack = ValueAnimator.ofInt(new int[]{0, 255});
                final Drawable drawable = PremiumPreviewBottomSheet.this.startEnterFromView.getRightDrawable();
                iconAlphaBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        drawable.setAlpha(((Integer) animation.getAnimatedValue()).intValue());
                        PremiumPreviewBottomSheet.this.startEnterFromView.invalidate();
                    }
                });
                iconAlphaBack.start();
                super.onAnimationEnd(animation);
            }
        });
        this.enterAnimator.setDuration(600);
        this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.enterAnimator.start();
        return super.onCustomOpenAnimation();
    }
}
