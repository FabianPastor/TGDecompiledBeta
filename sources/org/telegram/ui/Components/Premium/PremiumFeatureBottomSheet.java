package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.PremiumPreviewFragment;

public class PremiumFeatureBottomSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    ActionBar actionBar;
    private final BaseFragment baseFragment;
    private FrameLayout buttonContainer;
    FrameLayout closeLayout;
    boolean containerViewsForward;
    float containerViewsProgress;
    FrameLayout content;
    int contentHeight;
    boolean enterAnimationIsRunning;
    /* access modifiers changed from: private */
    public int gradientAlpha;
    /* access modifiers changed from: private */
    public final boolean onlySelectedType;
    private PremiumButtonView premiumButtonView;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures;
    float progressToFullscreenView;
    private PremiumPreviewFragment.SubscriptionTier selectedTier;
    /* access modifiers changed from: private */
    public final int startType;
    SvgHelper.SvgDrawable svgIcon;
    int topCurrentOffset;
    int topGlobalOffset;
    ViewPager viewPager;

    public PremiumFeatureBottomSheet(BaseFragment baseFragment2, int i, boolean z) {
        this(baseFragment2, i, z, (PremiumPreviewFragment.SubscriptionTier) null);
    }

    public PremiumFeatureBottomSheet(BaseFragment baseFragment2, int i, boolean z, PremiumPreviewFragment.SubscriptionTier subscriptionTier) {
        this(baseFragment2, baseFragment2.getContext(), baseFragment2.getCurrentAccount(), i, z, subscriptionTier);
    }

    public PremiumFeatureBottomSheet(BaseFragment baseFragment2, Context context, int i, int i2, boolean z) {
        this(baseFragment2, context, i, i2, z, (PremiumPreviewFragment.SubscriptionTier) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PremiumFeatureBottomSheet(BaseFragment baseFragment2, Context context, int i, int i2, boolean z, PremiumPreviewFragment.SubscriptionTier subscriptionTier) {
        super(context, false);
        BaseFragment baseFragment3 = baseFragment2;
        int i3 = i2;
        boolean z2 = z;
        this.premiumFeatures = new ArrayList<>();
        this.gradientAlpha = 255;
        this.baseFragment = baseFragment3;
        if (baseFragment3 != null) {
            this.selectedTier = subscriptionTier;
            fixNavigationBar();
            this.startType = i3;
            this.onlySelectedType = z2;
            this.svgIcon = SvgHelper.getDrawable(RLottieDrawable.readRes((File) null, R.raw.star_loader));
            AnonymousClass1 r5 = new FrameLayout(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    if (PremiumFeatureBottomSheet.this.isPortrait) {
                        PremiumFeatureBottomSheet.this.contentHeight = View.MeasureSpec.getSize(i);
                    } else {
                        PremiumFeatureBottomSheet.this.contentHeight = (int) (((float) View.MeasureSpec.getSize(i2)) * 0.65f);
                    }
                    super.onMeasure(i, i2);
                }
            };
            PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, i);
            int i4 = 0;
            while (true) {
                if (i4 >= this.premiumFeatures.size()) {
                    i4 = 0;
                    break;
                } else if (this.premiumFeatures.get(i4).type == i3) {
                    break;
                } else {
                    i4++;
                }
            }
            if (z2) {
                this.premiumFeatures.clear();
                this.premiumFeatures.add(this.premiumFeatures.get(i4));
                i4 = 0;
            }
            PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = this.premiumFeatures.get(i4);
            setApplyBottomPadding(false);
            this.useBackgroundTopPadding = false;
            final PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools("premiumGradientBottomSheet1", "premiumGradientBottomSheet2", "premiumGradientBottomSheet3", (String) null);
            gradientTools.x1 = 0.0f;
            gradientTools.y1 = 1.1f;
            gradientTools.x2 = 1.5f;
            gradientTools.y2 = -0.2f;
            gradientTools.exactly = true;
            this.content = new FrameLayout(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(PremiumFeatureBottomSheet.this.contentHeight + AndroidUtilities.dp(2.0f), NUM));
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), 0.0f, 0.0f);
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, (float) AndroidUtilities.dp(2.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + AndroidUtilities.dp(18.0f)));
                    canvas.save();
                    canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    gradientTools.paint.setAlpha(PremiumFeatureBottomSheet.this.gradientAlpha);
                    canvas.drawRoundRect(rectF, (float) (AndroidUtilities.dp(12.0f) - 1), (float) (AndroidUtilities.dp(12.0f) - 1), gradientTools.paint);
                    canvas.restore();
                    super.dispatchDraw(canvas);
                }
            };
            this.closeLayout = new FrameLayout(getContext());
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.msg_close);
            imageView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(12.0f), ColorUtils.setAlphaComponent(-1, 40), ColorUtils.setAlphaComponent(-1, 100)));
            this.closeLayout.addView(imageView, LayoutHelper.createFrame(24, 24, 17));
            this.closeLayout.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda1(this));
            r5.addView(this.content, LayoutHelper.createLinear(-1, -2, 1, 0, 16, 0, 0));
            AnonymousClass3 r9 = new ViewPager(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int dp = AndroidUtilities.dp(100.0f);
                    if (getChildCount() > 0) {
                        getChildAt(0).measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
                        dp = getChildAt(0).getMeasuredHeight();
                    }
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(dp + PremiumFeatureBottomSheet.this.topGlobalOffset, NUM));
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    try {
                        return super.onInterceptTouchEvent(motionEvent);
                    } catch (Exception unused) {
                        return false;
                    }
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (PremiumFeatureBottomSheet.this.enterAnimationIsRunning) {
                        return false;
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.viewPager = r9;
            r9.setOverScrollMode(2);
            this.viewPager.setOffscreenPageLimit(0);
            this.viewPager.setAdapter(new PagerAdapter() {
                public boolean isViewFromObject(View view, Object obj) {
                    return view == obj;
                }

                public int getCount() {
                    return PremiumFeatureBottomSheet.this.premiumFeatures.size();
                }

                public Object instantiateItem(ViewGroup viewGroup, int i) {
                    PremiumFeatureBottomSheet premiumFeatureBottomSheet = PremiumFeatureBottomSheet.this;
                    ViewPage viewPage = new ViewPage(premiumFeatureBottomSheet.getContext(), i);
                    viewGroup.addView(viewPage);
                    viewPage.position = i;
                    viewPage.setFeatureDate(PremiumFeatureBottomSheet.this.premiumFeatures.get(i));
                    return viewPage;
                }

                public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                    viewGroup.removeView((View) obj);
                }
            });
            this.viewPager.setCurrentItem(i4);
            r5.addView(this.viewPager, LayoutHelper.createFrame(-1, 100.0f, 0, 0.0f, 18.0f, 0.0f, 0.0f));
            r5.addView(this.closeLayout, LayoutHelper.createFrame(52, 52.0f, 53, 0.0f, 16.0f, 0.0f, 0.0f));
            final BottomPagesView bottomPagesView = new BottomPagesView(getContext(), this.viewPager, this.premiumFeatures.size());
            this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                float progress;
                int selectedPosition;
                int toPosition;

                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                    bottomPagesView.setPageOffset(i, f);
                    this.selectedPosition = i;
                    this.toPosition = i2 > 0 ? i + 1 : i - 1;
                    this.progress = f;
                    checkPage();
                }

                public void onPageSelected(int i) {
                    checkPage();
                }

                private void checkPage() {
                    float measuredWidth;
                    boolean z = false;
                    int i = 0;
                    while (true) {
                        float f = 0.0f;
                        if (i >= PremiumFeatureBottomSheet.this.viewPager.getChildCount()) {
                            break;
                        }
                        ViewPage viewPage = (ViewPage) PremiumFeatureBottomSheet.this.viewPager.getChildAt(i);
                        if (!PremiumFeatureBottomSheet.this.enterAnimationIsRunning || !(viewPage.topView instanceof PremiumAppIconsPreviewView)) {
                            int i2 = viewPage.position;
                            if (i2 == this.selectedPosition) {
                                PagerHeaderView pagerHeaderView = viewPage.topHeader;
                                measuredWidth = ((float) (-viewPage.getMeasuredWidth())) * this.progress;
                                pagerHeaderView.setOffset(measuredWidth);
                            } else if (i2 == this.toPosition) {
                                PagerHeaderView pagerHeaderView2 = viewPage.topHeader;
                                measuredWidth = (((float) (-viewPage.getMeasuredWidth())) * this.progress) + ((float) viewPage.getMeasuredWidth());
                                pagerHeaderView2.setOffset(measuredWidth);
                            } else {
                                viewPage.topHeader.setOffset((float) viewPage.getMeasuredWidth());
                            }
                            f = measuredWidth;
                        }
                        if (viewPage.topView instanceof PremiumAppIconsPreviewView) {
                            viewPage.setTranslationX(-f);
                            viewPage.title.setTranslationX(f);
                            viewPage.description.setTranslationX(f);
                        }
                        i++;
                    }
                    PremiumFeatureBottomSheet premiumFeatureBottomSheet = PremiumFeatureBottomSheet.this;
                    premiumFeatureBottomSheet.containerViewsProgress = this.progress;
                    int i3 = this.toPosition;
                    int i4 = this.selectedPosition;
                    if (i3 > i4) {
                        z = true;
                    }
                    premiumFeatureBottomSheet.containerViewsForward = z;
                    if (premiumFeatureBottomSheet.premiumFeatures.get(i4).type == 0) {
                        PremiumFeatureBottomSheet.this.progressToFullscreenView = 1.0f - this.progress;
                    } else if (PremiumFeatureBottomSheet.this.premiumFeatures.get(this.toPosition).type == 0) {
                        PremiumFeatureBottomSheet.this.progressToFullscreenView = this.progress;
                    } else {
                        PremiumFeatureBottomSheet.this.progressToFullscreenView = 0.0f;
                    }
                    PremiumFeatureBottomSheet premiumFeatureBottomSheet2 = PremiumFeatureBottomSheet.this;
                    int i5 = (int) ((1.0f - premiumFeatureBottomSheet2.progressToFullscreenView) * 255.0f);
                    if (i5 != premiumFeatureBottomSheet2.gradientAlpha) {
                        int unused = PremiumFeatureBottomSheet.this.gradientAlpha = i5;
                        PremiumFeatureBottomSheet.this.content.invalidate();
                        PremiumFeatureBottomSheet.this.checkTopOffset();
                    }
                }
            });
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.addView(r5);
            linearLayout.setOrientation(1);
            bottomPagesView.setColor("chats_unreadCounterMuted", "chats_actionBackground");
            if (!z2) {
                linearLayout.addView(bottomPagesView, LayoutHelper.createLinear(this.premiumFeatures.size() * 11, 5, 1, 0, 0, 0, 10));
            }
            PremiumButtonView premiumButtonView2 = new PremiumButtonView(getContext(), true);
            this.premiumButtonView = premiumButtonView2;
            premiumButtonView2.buttonLayout.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda2(this, baseFragment3, z2, premiumFeatureData));
            this.premiumButtonView.overlayTextView.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda0(this));
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.buttonContainer = frameLayout;
            frameLayout.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
            this.buttonContainer.setBackgroundColor(getThemedColor("dialogBackground"));
            linearLayout.addView(this.buttonContainer, LayoutHelper.createLinear(-1, 68, 80));
            if (UserConfig.getInstance(i).isPremium()) {
                this.premiumButtonView.setOverlayText(LocaleController.getString("OK", R.string.OK), false, false);
            }
            final ScrollView scrollView = new ScrollView(getContext());
            scrollView.addView(linearLayout);
            setCustomView(scrollView);
            MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
            setButtonText();
            this.customViewGravity = 83;
            final Drawable mutate = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();
            AnonymousClass6 r3 = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    PremiumFeatureBottomSheet.this.onContainerTranslationYChanged(f);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    PremiumFeatureBottomSheet.this.topGlobalOffset = 0;
                    scrollView.measure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), Integer.MIN_VALUE));
                    PremiumFeatureBottomSheet.this.topGlobalOffset = (View.MeasureSpec.getSize(i2) - scrollView.getMeasuredHeight()) + PremiumFeatureBottomSheet.this.backgroundPaddingTop;
                    super.onMeasure(i, i2);
                    PremiumFeatureBottomSheet.this.checkTopOffset();
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    Drawable access$500 = PremiumFeatureBottomSheet.this.shadowDrawable;
                    PremiumFeatureBottomSheet premiumFeatureBottomSheet = PremiumFeatureBottomSheet.this;
                    access$500.setBounds(0, (premiumFeatureBottomSheet.topCurrentOffset - premiumFeatureBottomSheet.backgroundPaddingTop) + AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
                    PremiumFeatureBottomSheet.this.shadowDrawable.draw(canvas);
                    super.dispatchDraw(canvas);
                    ActionBar actionBar = PremiumFeatureBottomSheet.this.actionBar;
                    if (actionBar != null && actionBar.getVisibility() == 0 && PremiumFeatureBottomSheet.this.actionBar.getAlpha() != 0.0f) {
                        mutate.setBounds(0, PremiumFeatureBottomSheet.this.actionBar.getBottom(), getMeasuredWidth(), PremiumFeatureBottomSheet.this.actionBar.getBottom() + mutate.getIntrinsicHeight());
                        mutate.setAlpha((int) (PremiumFeatureBottomSheet.this.actionBar.getAlpha() * 255.0f));
                        mutate.draw(canvas);
                    }
                }

                /* access modifiers changed from: protected */
                public boolean drawChild(Canvas canvas, View view, long j) {
                    if (view != scrollView) {
                        return super.drawChild(canvas, view, j);
                    }
                    canvas.save();
                    canvas.clipRect(0, PremiumFeatureBottomSheet.this.topCurrentOffset + AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
                    super.drawChild(canvas, view, j);
                    canvas.restore();
                    return true;
                }

                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        float y = motionEvent.getY();
                        PremiumFeatureBottomSheet premiumFeatureBottomSheet = PremiumFeatureBottomSheet.this;
                        if (y < ((float) ((premiumFeatureBottomSheet.topCurrentOffset - premiumFeatureBottomSheet.backgroundPaddingTop) + AndroidUtilities.dp(2.0f)))) {
                            PremiumFeatureBottomSheet.this.dismiss();
                        }
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.containerView = r3;
            int i5 = this.backgroundPaddingLeft;
            r3.setPadding(i5, this.backgroundPaddingTop - 1, i5, 0);
            return;
        }
        throw new RuntimeException("fragmnet can't be null");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(BaseFragment baseFragment2, boolean z, PremiumPreviewFragment.PremiumFeatureData premiumFeatureData, View view) {
        if (baseFragment2 instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment2;
            chatActivity.closeMenu();
            ChatAttachAlert chatAttachAlert = chatActivity.chatAttachAlert;
            if (chatAttachAlert != null) {
                chatAttachAlert.dismiss(true);
            }
        }
        if (!(baseFragment2 == null || baseFragment2.getVisibleDialog() == null)) {
            baseFragment2.getVisibleDialog().dismiss();
        }
        if (!z || baseFragment2 == null) {
            PremiumPreviewFragment.buyPremium(baseFragment2, this.selectedTier, PremiumPreviewFragment.featureTypeToServerString(premiumFeatureData.type));
        } else {
            baseFragment2.presentFragment(new PremiumPreviewFragment(PremiumPreviewFragment.featureTypeToServerString(premiumFeatureData.type)));
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        dismiss();
    }

    private void setButtonText() {
        if (this.onlySelectedType) {
            int i = this.startType;
            if (i == 4) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString(R.string.UnlockPremiumReactions));
                this.premiumButtonView.setIcon(R.raw.unlock_icon);
            } else if (i == 3) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString(R.string.AboutTelegramPremium));
            } else if (i == 10) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString(R.string.UnlockPremiumIcons));
                this.premiumButtonView.setIcon(R.raw.unlock_icon);
            }
        } else {
            this.premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount, this.selectedTier));
        }
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 16);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        AnonymousClass7 r11 = new ActionBar(getContext()) {
            public void setAlpha(float f) {
                if (getAlpha() != f) {
                    super.setAlpha(f);
                    PremiumFeatureBottomSheet.this.containerView.invalidate();
                }
            }

            public void setTag(Object obj) {
                super.setTag(obj);
                PremiumFeatureBottomSheet.this.updateStatusBar();
            }
        };
        this.actionBar = r11;
        r11.setBackgroundColor(getThemedColor("dialogBackground"));
        this.actionBar.setTitleColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsBackgroundColor(getThemedColor("actionBarActionModeDefaultSelector"), false);
        this.actionBar.setItemsColor(getThemedColor("actionBarActionModeDefaultIcon"), false);
        this.actionBar.setCastShadows(true);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("DoubledLimits", R.string.DoubledLimits));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PremiumFeatureBottomSheet.this.dismiss();
                }
            }
        });
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, (float) (-this.backgroundPaddingTop), 0.0f, 0.0f));
        AndroidUtilities.updateViewVisibilityAnimated(this.actionBar, false, 1.0f, false);
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 16);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.billingProductDetailsUpdated || i == NotificationCenter.premiumPromoUpdated) {
            setButtonText();
        } else if (i != NotificationCenter.currentUserPremiumStatusChanged) {
        } else {
            if (UserConfig.getInstance(this.currentAccount).isPremium()) {
                this.premiumButtonView.setOverlayText(LocaleController.getString("OK", R.string.OK), false, true);
            } else {
                this.premiumButtonView.clearOverlayText();
            }
        }
    }

    private class ViewPage extends LinearLayout {
        TextView description;
        public int position;
        TextView title;
        PagerHeaderView topHeader = ((PagerHeaderView) this.topView);
        View topView;
        boolean topViewOnFullHeight;

        public ViewPage(Context context, int i) {
            super(context);
            setOrientation(1);
            View viewForPosition = PremiumFeatureBottomSheet.this.getViewForPosition(context, i);
            this.topView = viewForPosition;
            addView(viewForPosition);
            TextView textView = new TextView(context);
            this.title = textView;
            textView.setGravity(1);
            this.title.setTextColor(Theme.getColor("dialogTextBlack"));
            this.title.setTextSize(1, 20.0f);
            this.title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 20.0f, 21.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.description = textView2;
            textView2.setGravity(1);
            this.description.setTextSize(1, 15.0f);
            this.description.setTextColor(Theme.getColor("dialogTextBlack"));
            if (!PremiumFeatureBottomSheet.this.onlySelectedType) {
                this.description.setLines(2);
            }
            addView(this.description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 10.0f, 21.0f, 16.0f));
            setClipChildren(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            this.title.setVisibility(0);
            View view = this.topView;
            if (view instanceof DoubleLimitsPageView) {
                ((DoubleLimitsPageView) view).setTopOffset(PremiumFeatureBottomSheet.this.topGlobalOffset);
            }
            ViewGroup.LayoutParams layoutParams = this.topView.getLayoutParams();
            PremiumFeatureBottomSheet premiumFeatureBottomSheet = PremiumFeatureBottomSheet.this;
            layoutParams.height = premiumFeatureBottomSheet.contentHeight;
            this.description.setVisibility(premiumFeatureBottomSheet.isPortrait ? 0 : 8);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.title.getLayoutParams();
            if (PremiumFeatureBottomSheet.this.isPortrait) {
                marginLayoutParams.topMargin = AndroidUtilities.dp(20.0f);
                marginLayoutParams.bottomMargin = 0;
            } else {
                marginLayoutParams.topMargin = AndroidUtilities.dp(10.0f);
                marginLayoutParams.bottomMargin = AndroidUtilities.dp(10.0f);
            }
            ((ViewGroup.MarginLayoutParams) this.topView.getLayoutParams()).bottomMargin = 0;
            super.onMeasure(i, i2);
            if (this.topViewOnFullHeight) {
                this.topView.getLayoutParams().height = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                ((ViewGroup.MarginLayoutParams) this.topView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(16.0f);
                this.title.setVisibility(8);
                this.description.setVisibility(8);
                super.onMeasure(i, i2);
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view != this.topView) {
                return super.drawChild(canvas, view, j);
            }
            boolean z = view instanceof DoubleLimitsPageView;
            if (z) {
                setTranslationY(0.0f);
            } else {
                setTranslationY((float) PremiumFeatureBottomSheet.this.topGlobalOffset);
            }
            if ((view instanceof CarouselView) || z) {
                return super.drawChild(canvas, view, j);
            }
            canvas.save();
            canvas.clipRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }

        /* access modifiers changed from: package-private */
        public void setFeatureDate(PremiumPreviewFragment.PremiumFeatureData premiumFeatureData) {
            if (premiumFeatureData.type == 0) {
                this.title.setText("");
                this.description.setText("");
                this.topViewOnFullHeight = true;
            } else if (PremiumFeatureBottomSheet.this.onlySelectedType) {
                if (PremiumFeatureBottomSheet.this.startType == 4) {
                    this.title.setText(LocaleController.getString("AdditionalReactions", R.string.AdditionalReactions));
                    this.description.setText(LocaleController.getString("AdditionalReactionsDescription", R.string.AdditionalReactionsDescription));
                } else if (PremiumFeatureBottomSheet.this.startType == 3) {
                    this.title.setText(LocaleController.getString("PremiumPreviewNoAds", R.string.PremiumPreviewNoAds));
                    this.description.setText(LocaleController.getString("PremiumPreviewNoAdsDescription2", R.string.PremiumPreviewNoAdsDescription2));
                } else if (PremiumFeatureBottomSheet.this.startType == 10) {
                    this.title.setText(LocaleController.getString("PremiumPreviewAppIcon", R.string.PremiumPreviewAppIcon));
                    this.description.setText(LocaleController.getString("PremiumPreviewAppIconDescription2", R.string.PremiumPreviewAppIconDescription2));
                }
                this.topViewOnFullHeight = false;
            } else {
                this.title.setText(premiumFeatureData.title);
                this.description.setText(premiumFeatureData.description);
                this.topViewOnFullHeight = false;
            }
            requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public View getViewForPosition(Context context, int i) {
        PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = this.premiumFeatures.get(i);
        int i2 = premiumFeatureData.type;
        if (i2 == 0) {
            DoubleLimitsPageView doubleLimitsPageView = new DoubleLimitsPageView(context);
            doubleLimitsPageView.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    PremiumFeatureBottomSheet.this.checkTopOffset();
                }
            });
            return doubleLimitsPageView;
        } else if (i2 == 5) {
            return new PremiumStickersPreviewRecycler(this, context, this.currentAccount) {
                public void setOffset(float f) {
                    setAutoPlayEnabled(f == 0.0f);
                    super.setOffset(f);
                }
            };
        } else {
            if (i2 == 10) {
                return new PremiumAppIconsPreviewView(context);
            }
            return new VideoScreenPreview(context, this.svgIcon, this.currentAccount, premiumFeatureData.type);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        if (this.viewPager.getChildCount() > 0) {
            ViewPage viewPage = (ViewPage) this.viewPager.getChildAt(0);
            View view = viewPage.topView;
            if (view instanceof PremiumAppIconsPreviewView) {
                final PremiumAppIconsPreviewView premiumAppIconsPreviewView = (PremiumAppIconsPreviewView) view;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{(float) viewPage.getMeasuredWidth(), 0.0f});
                premiumAppIconsPreviewView.setOffset((float) viewPage.getMeasuredWidth());
                this.enterAnimationIsRunning = true;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(this) {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        premiumAppIconsPreviewView.setOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                });
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PremiumFeatureBottomSheet.this.enterAnimationIsRunning = false;
                        premiumAppIconsPreviewView.setOffset(0.0f);
                        super.onAnimationEnd(animator);
                    }
                });
                ofFloat.setDuration(500);
                ofFloat.setStartDelay(100);
                ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                ofFloat.start();
            }
        }
        return super.onCustomOpenAnimation();
    }

    /* access modifiers changed from: package-private */
    public void checkTopOffset() {
        int i;
        int i2;
        boolean z = false;
        int i3 = 0;
        while (true) {
            if (i3 >= this.viewPager.getChildCount()) {
                i = -1;
                break;
            } else if (((ViewPage) this.viewPager.getChildAt(i3)).topView instanceof DoubleLimitsPageView) {
                View findViewByPosition = ((DoubleLimitsPageView) ((ViewPage) this.viewPager.getChildAt(i3)).topView).layoutManager.findViewByPosition(0);
                if (findViewByPosition == null || (i = findViewByPosition.getTop()) < 0) {
                    i = 0;
                }
            } else {
                i3++;
            }
        }
        if (i >= 0) {
            float f = this.progressToFullscreenView;
            i2 = (int) ((((float) i) * f) + (((float) this.topGlobalOffset) * (1.0f - f)));
        } else {
            i2 = this.topGlobalOffset;
        }
        this.closeLayout.setAlpha(1.0f - this.progressToFullscreenView);
        if (this.progressToFullscreenView == 1.0f) {
            this.closeLayout.setVisibility(4);
        } else {
            this.closeLayout.setVisibility(0);
        }
        FrameLayout frameLayout = this.content;
        frameLayout.setTranslationX(((float) frameLayout.getMeasuredWidth()) * this.progressToFullscreenView);
        if (i2 != this.topCurrentOffset) {
            this.topCurrentOffset = i2;
            for (int i4 = 0; i4 < this.viewPager.getChildCount(); i4++) {
                if (!((ViewPage) this.viewPager.getChildAt(i4)).topViewOnFullHeight) {
                    this.viewPager.getChildAt(i4).setTranslationY((float) this.topCurrentOffset);
                }
            }
            this.content.setTranslationY((float) this.topCurrentOffset);
            this.closeLayout.setTranslationY((float) this.topCurrentOffset);
            this.containerView.invalidate();
            if (this.topCurrentOffset < AndroidUtilities.dp(30.0f)) {
                z = true;
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.actionBar, z, 1.0f, true);
        }
    }

    /* access modifiers changed from: private */
    public void updateStatusBar() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null && actionBar2.getTag() != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), isLightStatusBar());
        } else if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor("dialogBackground")) > 0.699999988079071d;
    }
}
