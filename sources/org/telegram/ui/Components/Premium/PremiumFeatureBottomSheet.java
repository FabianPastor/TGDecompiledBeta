package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
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
    private FrameLayout buttonContainer;
    boolean containerViewsForward;
    float containerViewsProgress;
    FrameLayout content;
    int contentHeight;
    boolean enterAnimationIsRunning;
    /* access modifiers changed from: private */
    public final boolean onlySelectedType;
    private PremiumButtonView premiumButtonView;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures;
    private PremiumPreviewFragment.SubscriptionTier selectedTier;
    /* access modifiers changed from: private */
    public final int startType;
    SvgHelper.SvgDrawable svgIcon;
    ViewPager viewPager;

    public PremiumFeatureBottomSheet(BaseFragment baseFragment, int i, boolean z) {
        this(baseFragment, i, z, (PremiumPreviewFragment.SubscriptionTier) null);
    }

    public PremiumFeatureBottomSheet(BaseFragment baseFragment, int i, boolean z, PremiumPreviewFragment.SubscriptionTier subscriptionTier) {
        this(baseFragment, baseFragment.getContext(), baseFragment.getCurrentAccount(), i, z, subscriptionTier);
    }

    public PremiumFeatureBottomSheet(BaseFragment baseFragment, Context context, int i, int i2, boolean z) {
        this(baseFragment, context, i, i2, z, (PremiumPreviewFragment.SubscriptionTier) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PremiumFeatureBottomSheet(BaseFragment baseFragment, Context context, int i, int i2, boolean z, PremiumPreviewFragment.SubscriptionTier subscriptionTier) {
        super(context, false);
        BaseFragment baseFragment2 = baseFragment;
        int i3 = i2;
        boolean z2 = z;
        this.premiumFeatures = new ArrayList<>();
        if (baseFragment2 != null) {
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
                }
                if (this.premiumFeatures.get(i4).type == 0) {
                    this.premiumFeatures.remove(i4);
                    i4--;
                } else if (this.premiumFeatures.get(i4).type == i3) {
                    break;
                }
                i4++;
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
                    canvas.drawRoundRect(rectF, (float) (AndroidUtilities.dp(12.0f) - 1), (float) (AndroidUtilities.dp(12.0f) - 1), gradientTools.paint);
                    canvas.restore();
                    super.dispatchDraw(canvas);
                }
            };
            FrameLayout frameLayout = new FrameLayout(getContext());
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.msg_close);
            imageView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(12.0f), ColorUtils.setAlphaComponent(-1, 40), ColorUtils.setAlphaComponent(-1, 100)));
            frameLayout.addView(imageView, LayoutHelper.createFrame(24, 24, 17));
            frameLayout.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda1(this));
            r5.addView(this.content, LayoutHelper.createLinear(-1, -2, 1, 0, 16, 0, 0));
            AnonymousClass3 r9 = new ViewPager(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int dp = AndroidUtilities.dp(100.0f);
                    if (getChildCount() > 0) {
                        getChildAt(0).measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
                        dp = getChildAt(0).getMeasuredHeight();
                    }
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(dp, NUM));
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
            r9.setOffscreenPageLimit(0);
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
            r5.addView(frameLayout, LayoutHelper.createFrame(52, 52.0f, 53, 0.0f, 16.0f, 0.0f, 0.0f));
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
                    for (int i = 0; i < PremiumFeatureBottomSheet.this.viewPager.getChildCount(); i++) {
                        ViewPage viewPage = (ViewPage) PremiumFeatureBottomSheet.this.viewPager.getChildAt(i);
                        float f = 0.0f;
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
                    }
                    PremiumFeatureBottomSheet premiumFeatureBottomSheet = PremiumFeatureBottomSheet.this;
                    premiumFeatureBottomSheet.containerViewsProgress = this.progress;
                    if (this.toPosition > this.selectedPosition) {
                        z = true;
                    }
                    premiumFeatureBottomSheet.containerViewsForward = z;
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
            premiumButtonView2.buttonLayout.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda2(this, baseFragment2, z2, premiumFeatureData));
            this.premiumButtonView.overlayTextView.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda0(this));
            FrameLayout frameLayout2 = new FrameLayout(getContext());
            this.buttonContainer = frameLayout2;
            frameLayout2.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
            this.buttonContainer.setBackgroundColor(getThemedColor("dialogBackground"));
            linearLayout.addView(this.buttonContainer, LayoutHelper.createLinear(-1, 68, 80));
            if (UserConfig.getInstance(i).isPremium()) {
                this.premiumButtonView.setOverlayText(LocaleController.getString("OK", R.string.OK), false, false);
            }
            ScrollView scrollView = new ScrollView(getContext());
            scrollView.addView(linearLayout);
            setCustomView(scrollView);
            MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
            setButtonText();
            return;
        }
        throw new RuntimeException("fragmnet can't be null");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(BaseFragment baseFragment, boolean z, PremiumPreviewFragment.PremiumFeatureData premiumFeatureData, View view) {
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            chatActivity.closeMenu();
            ChatAttachAlert chatAttachAlert = chatActivity.chatAttachAlert;
            if (chatAttachAlert != null) {
                chatAttachAlert.dismiss(true);
            }
        }
        if (!(baseFragment == null || baseFragment.getVisibleDialog() == null)) {
            baseFragment.getVisibleDialog().dismiss();
        }
        if (!z || baseFragment == null) {
            PremiumPreviewFragment.buyPremium(baseFragment, this.selectedTier, PremiumPreviewFragment.featureTypeToServerString(premiumFeatureData.type));
        } else {
            baseFragment.presentFragment(new PremiumPreviewFragment(PremiumPreviewFragment.featureTypeToServerString(premiumFeatureData.type)));
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
            super.onMeasure(i, i2);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view != this.topView) {
                return super.drawChild(canvas, view, j);
            }
            if (view instanceof CarouselView) {
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
            if (!PremiumFeatureBottomSheet.this.onlySelectedType) {
                this.title.setText(premiumFeatureData.title);
                this.description.setText(premiumFeatureData.description);
            } else if (PremiumFeatureBottomSheet.this.startType == 4) {
                this.title.setText(LocaleController.getString("AdditionalReactions", R.string.AdditionalReactions));
                this.description.setText(LocaleController.getString("AdditionalReactionsDescription", R.string.AdditionalReactionsDescription));
            } else if (PremiumFeatureBottomSheet.this.startType == 3) {
                this.title.setText(LocaleController.getString("PremiumPreviewNoAds", R.string.PremiumPreviewNoAds));
                this.description.setText(LocaleController.getString("PremiumPreviewNoAdsDescription2", R.string.PremiumPreviewNoAdsDescription2));
            } else if (PremiumFeatureBottomSheet.this.startType == 10) {
                this.title.setText(LocaleController.getString("PremiumPreviewAppIcon", R.string.PremiumPreviewAppIcon));
                this.description.setText(LocaleController.getString("PremiumPreviewAppIconDescription2", R.string.PremiumPreviewAppIconDescription2));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public View getViewForPosition(Context context, int i) {
        PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = this.premiumFeatures.get(i);
        int i2 = premiumFeatureData.type;
        if (i2 == 4) {
            ArrayList arrayList = new ArrayList();
            List<TLRPC$TL_availableReaction> enabledReactionsList = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            ArrayList arrayList2 = new ArrayList();
            for (int i3 = 0; i3 < enabledReactionsList.size(); i3++) {
                if (enabledReactionsList.get(i3).premium) {
                    arrayList2.add(enabledReactionsList.get(i3));
                }
            }
            for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                ReactionDrawingObject reactionDrawingObject = new ReactionDrawingObject(i4);
                reactionDrawingObject.set((TLRPC$TL_availableReaction) arrayList2.get(i4));
                arrayList.add(reactionDrawingObject);
            }
            HashMap hashMap = new HashMap();
            hashMap.put("👌", 1);
            hashMap.put("😍", 2);
            hashMap.put("🤡", 3);
            hashMap.put("🕊", 4);
            hashMap.put("🥱", 5);
            hashMap.put("🥴", 6);
            hashMap.put("🐳", 7);
            Collections.sort(arrayList, new PremiumFeatureBottomSheet$$ExternalSyntheticLambda3(hashMap));
            return new CarouselView(context, arrayList);
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

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getViewForPosition$3(HashMap hashMap, ReactionDrawingObject reactionDrawingObject, ReactionDrawingObject reactionDrawingObject2) {
        int i = Integer.MAX_VALUE;
        int intValue = hashMap.containsKey(reactionDrawingObject.reaction.reaction) ? ((Integer) hashMap.get(reactionDrawingObject.reaction.reaction)).intValue() : Integer.MAX_VALUE;
        if (hashMap.containsKey(reactionDrawingObject2.reaction.reaction)) {
            i = ((Integer) hashMap.get(reactionDrawingObject2.reaction.reaction)).intValue();
        }
        return i - intValue;
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
}
