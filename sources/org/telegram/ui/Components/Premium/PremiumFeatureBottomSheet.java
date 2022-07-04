package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
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
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BottomPagesView;
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
    BaseFragment fragment;
    /* access modifiers changed from: private */
    public final boolean onlySelectedType;
    private PremiumButtonView premiumButtonView;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures = new ArrayList<>();
    /* access modifiers changed from: private */
    public final int startType;
    SvgHelper.SvgDrawable svgIcon;
    ViewPager viewPager;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PremiumFeatureBottomSheet(BaseFragment fragment2, int startType2, boolean onlySelectedType2) {
        super(fragment2.getParentActivity(), false);
        BaseFragment baseFragment = fragment2;
        int i = startType2;
        boolean z = onlySelectedType2;
        this.fragment = baseFragment;
        this.startType = i;
        this.onlySelectedType = z;
        this.svgIcon = SvgHelper.getDrawable(RLottieDrawable.readRes((File) null, NUM));
        final Context context = fragment2.getParentActivity();
        FrameLayout frameLayout = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (PremiumFeatureBottomSheet.this.isPortrait) {
                    PremiumFeatureBottomSheet.this.contentHeight = View.MeasureSpec.getSize(widthMeasureSpec);
                } else {
                    PremiumFeatureBottomSheet.this.contentHeight = (int) (((float) View.MeasureSpec.getSize(heightMeasureSpec)) * 0.65f);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, fragment2.getCurrentAccount());
        int selectedPosition = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= this.premiumFeatures.size()) {
                break;
            }
            if (this.premiumFeatures.get(i2).type == 0) {
                this.premiumFeatures.remove(i2);
                i2--;
            } else if (this.premiumFeatures.get(i2).type == i) {
                selectedPosition = i2;
                break;
            }
            i2++;
        }
        if (z) {
            this.premiumFeatures.clear();
            this.premiumFeatures.add(this.premiumFeatures.get(selectedPosition));
            selectedPosition = 0;
        }
        PremiumPreviewFragment.PremiumFeatureData featureData = this.premiumFeatures.get(selectedPosition);
        setApplyBottomPadding(false);
        this.useBackgroundTopPadding = false;
        final PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools("premiumGradientBottomSheet1", "premiumGradientBottomSheet2", "premiumGradientBottomSheet3", (String) null);
        gradientTools.x1 = 0.0f;
        gradientTools.y1 = 1.1f;
        gradientTools.x2 = 1.5f;
        gradientTools.y2 = -0.2f;
        gradientTools.exactly = true;
        this.content = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(2.0f) + PremiumFeatureBottomSheet.this.contentHeight, NUM));
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), 0.0f, 0.0f);
                AndroidUtilities.rectTmp.set(0.0f, (float) AndroidUtilities.dp(2.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + AndroidUtilities.dp(18.0f)));
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) (AndroidUtilities.dp(12.0f) - 1), (float) (AndroidUtilities.dp(12.0f) - 1), gradientTools.paint);
                canvas.restore();
                super.dispatchDraw(canvas);
            }
        };
        FrameLayout closeLayout = new FrameLayout(context);
        ImageView closeImage = new ImageView(context);
        closeImage.setImageResource(NUM);
        closeImage.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(12.0f), ColorUtils.setAlphaComponent(-1, 40), ColorUtils.setAlphaComponent(-1, 100)));
        closeLayout.addView(closeImage, LayoutHelper.createFrame(24, 24, 17));
        closeLayout.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda0(this));
        frameLayout.addView(this.content, LayoutHelper.createLinear(-1, -2, 1, 0, 16, 0, 0));
        AnonymousClass3 r5 = new ViewPager(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int h = AndroidUtilities.dp(100.0f);
                if (getChildCount() > 0) {
                    getChildAt(0).measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                    h = getChildAt(0).getMeasuredHeight();
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h, NUM));
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                try {
                    return super.onInterceptTouchEvent(ev);
                } catch (Exception e) {
                    return false;
                }
            }

            public boolean onTouchEvent(MotionEvent ev) {
                if (PremiumFeatureBottomSheet.this.enterAnimationIsRunning) {
                    return false;
                }
                return super.onTouchEvent(ev);
            }
        };
        this.viewPager = r5;
        r5.setOffscreenPageLimit(0);
        this.viewPager.setAdapter(new PagerAdapter() {
            public int getCount() {
                return PremiumFeatureBottomSheet.this.premiumFeatures.size();
            }

            public Object instantiateItem(ViewGroup container, int position) {
                ViewPage viewPage = new ViewPage(context, position);
                container.addView(viewPage);
                viewPage.position = position;
                viewPage.setFeatureDate(PremiumFeatureBottomSheet.this.premiumFeatures.get(position));
                return viewPage;
            }

            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        this.viewPager.setCurrentItem(selectedPosition);
        frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, 100.0f, 0, 0.0f, 18.0f, 0.0f, 0.0f));
        frameLayout.addView(closeLayout, LayoutHelper.createFrame(52, 52.0f, 53, 0.0f, 16.0f, 0.0f, 0.0f));
        final BottomPagesView bottomPages = new BottomPagesView(context, this.viewPager, this.premiumFeatures.size());
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            float progress;
            int selectedPosition;
            int toPosition;

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                bottomPages.setPageOffset(position, positionOffset);
                this.selectedPosition = position;
                this.toPosition = positionOffsetPixels > 0 ? position + 1 : position - 1;
                this.progress = positionOffset;
                checkPage();
            }

            public void onPageSelected(int i) {
                checkPage();
            }

            private void checkPage() {
                for (int i = 0; i < PremiumFeatureBottomSheet.this.viewPager.getChildCount(); i++) {
                    ViewPage page = (ViewPage) PremiumFeatureBottomSheet.this.viewPager.getChildAt(i);
                    float offset = 0.0f;
                    if (!PremiumFeatureBottomSheet.this.enterAnimationIsRunning || !(page.topView instanceof PremiumAppIconsPreviewView)) {
                        if (page.position == this.selectedPosition) {
                            PagerHeaderView pagerHeaderView = page.topHeader;
                            float f = ((float) (-page.getMeasuredWidth())) * this.progress;
                            offset = f;
                            pagerHeaderView.setOffset(f);
                        } else if (page.position == this.toPosition) {
                            PagerHeaderView pagerHeaderView2 = page.topHeader;
                            float measuredWidth = (((float) (-page.getMeasuredWidth())) * this.progress) + ((float) page.getMeasuredWidth());
                            offset = measuredWidth;
                            pagerHeaderView2.setOffset(measuredWidth);
                        } else {
                            page.topHeader.setOffset((float) page.getMeasuredWidth());
                        }
                    }
                    if (page.topView instanceof PremiumAppIconsPreviewView) {
                        page.setTranslationX(-offset);
                        page.title.setTranslationX(offset);
                        page.description.setTranslationX(offset);
                    }
                }
                PremiumFeatureBottomSheet.this.containerViewsProgress = this.progress;
                PremiumFeatureBottomSheet.this.containerViewsForward = this.toPosition > this.selectedPosition;
            }

            public void onPageScrollStateChanged(int i) {
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.addView(frameLayout);
        linearLayout.setOrientation(1);
        bottomPages.setColor("chats_unreadCounterMuted", "chats_actionBackground");
        if (!z) {
            linearLayout.addView(bottomPages, LayoutHelper.createLinear(this.premiumFeatures.size() * 11, 5, 1, 0, 0, 0, 10));
        }
        PremiumButtonView premiumButtonView2 = new PremiumButtonView(context, true);
        this.premiumButtonView = premiumButtonView2;
        premiumButtonView2.buttonLayout.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda2(this, baseFragment, z, featureData));
        this.premiumButtonView.overlayTextView.setOnClickListener(new PremiumFeatureBottomSheet$$ExternalSyntheticLambda1(this));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.buttonContainer = frameLayout2;
        frameLayout2.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        this.buttonContainer.setBackgroundColor(getThemedColor("dialogBackground"));
        linearLayout.addView(this.buttonContainer, LayoutHelper.createLinear(-1, 68, 80));
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.premiumButtonView.setOverlayText(LocaleController.getString("OK", NUM), false, false);
        }
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
        MediaDataController.getInstance(this.currentAccount).preloadPremiumPreviewStickers();
        setButtonText();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Premium-PremiumFeatureBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1251xa132acd3(View v) {
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Premium-PremiumFeatureBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1252x35711CLASSNAME(BaseFragment fragment2, boolean onlySelectedType2, PremiumPreviewFragment.PremiumFeatureData featureData, View v) {
        if (fragment2.getVisibleDialog() != null) {
            fragment2.getVisibleDialog().dismiss();
        }
        if (fragment2 instanceof ChatActivity) {
            ((ChatActivity) fragment2).closeMenu();
        }
        if (onlySelectedType2) {
            fragment2.presentFragment(new PremiumPreviewFragment(PremiumPreviewFragment.featureTypeToServerString(featureData.type)));
        } else {
            PremiumPreviewFragment.buyPremium(fragment2, PremiumPreviewFragment.featureTypeToServerString(featureData.type));
        }
        dismiss();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-Premium-PremiumFeatureBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1253xc9af8CLASSNAME(View v) {
        dismiss();
    }

    private void setButtonText() {
        if (this.onlySelectedType) {
            int i = this.startType;
            if (i == 4) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString(NUM));
                this.premiumButtonView.setIcon(NUM);
            } else if (i == 3) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString(NUM));
            } else if (i == 10) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString(NUM));
                this.premiumButtonView.setIcon(NUM);
            }
        } else {
            this.premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.billingProductDetailsUpdated || id == NotificationCenter.premiumPromoUpdated) {
            setButtonText();
        } else if (id != NotificationCenter.currentUserPremiumStatusChanged) {
        } else {
            if (UserConfig.getInstance(this.currentAccount).isPremium()) {
                this.premiumButtonView.setOverlayText(LocaleController.getString("OK", NUM), false, true);
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

        public ViewPage(Context context, int p) {
            super(context);
            setOrientation(1);
            View viewForPosition = PremiumFeatureBottomSheet.this.getViewForPosition(context, p);
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.topView.getLayoutParams().height = PremiumFeatureBottomSheet.this.contentHeight;
            this.description.setVisibility(PremiumFeatureBottomSheet.this.isPortrait ? 0 : 8);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.title.getLayoutParams();
            if (PremiumFeatureBottomSheet.this.isPortrait) {
                layoutParams.topMargin = AndroidUtilities.dp(20.0f);
                layoutParams.bottomMargin = 0;
            } else {
                layoutParams.topMargin = AndroidUtilities.dp(10.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp(10.0f);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child != this.topView) {
                return super.drawChild(canvas, child, drawingTime);
            }
            if (child instanceof CarouselView) {
                return super.drawChild(canvas, child, drawingTime);
            }
            canvas.save();
            canvas.clipRect(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            boolean b = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return b;
        }

        /* access modifiers changed from: package-private */
        public void setFeatureDate(PremiumPreviewFragment.PremiumFeatureData featureData) {
            if (!PremiumFeatureBottomSheet.this.onlySelectedType) {
                this.title.setText(featureData.title);
                this.description.setText(featureData.description);
            } else if (PremiumFeatureBottomSheet.this.startType == 4) {
                this.title.setText(LocaleController.getString("AdditionalReactions", NUM));
                this.description.setText(LocaleController.getString("AdditionalReactionsDescription", NUM));
            } else if (PremiumFeatureBottomSheet.this.startType == 3) {
                this.title.setText(LocaleController.getString("PremiumPreviewNoAds", NUM));
                this.description.setText(LocaleController.getString("PremiumPreviewNoAdsDescription2", NUM));
            } else if (PremiumFeatureBottomSheet.this.startType == 10) {
                this.title.setText(LocaleController.getString("PremiumPreviewAppIcon", NUM));
                this.description.setText(LocaleController.getString("PremiumPreviewAppIconDescription2", NUM));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public View getViewForPosition(Context context, int position) {
        PremiumPreviewFragment.PremiumFeatureData featureData = this.premiumFeatures.get(position);
        if (featureData.type == 4) {
            ArrayList<ReactionDrawingObject> drawingObjects = new ArrayList<>();
            List<TLRPC.TL_availableReaction> list = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            List<TLRPC.TL_availableReaction> premiumLockedReactions = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).premium) {
                    premiumLockedReactions.add(list.get(i));
                }
            }
            for (int i2 = 0; i2 < premiumLockedReactions.size(); i2++) {
                ReactionDrawingObject drawingObject = new ReactionDrawingObject(i2);
                drawingObject.set(premiumLockedReactions.get(i2));
                drawingObjects.add(drawingObject);
            }
            HashMap<String, Integer> sortRulesMap = new HashMap<>();
            sortRulesMap.put("👌", 1);
            sortRulesMap.put("😍", 2);
            sortRulesMap.put("🤡", 3);
            sortRulesMap.put("🕊", 4);
            sortRulesMap.put("🥱", 5);
            sortRulesMap.put("🥴", 6);
            sortRulesMap.put("🐳", 7);
            Collections.sort(drawingObjects, new PremiumFeatureBottomSheet$$ExternalSyntheticLambda3(sortRulesMap));
            return new CarouselView(context, drawingObjects);
        } else if (featureData.type == 5) {
            return new PremiumStickersPreviewRecycler(context, this.currentAccount) {
                public void setOffset(float v) {
                    setAutoPlayEnabled(v == 0.0f);
                    super.setOffset(v);
                }
            };
        } else {
            if (featureData.type == 10) {
                return new PremiumAppIconsPreviewView(context);
            }
            return new VideoScreenPreview(context, this.svgIcon, this.currentAccount, featureData.type);
        }
    }

    static /* synthetic */ int lambda$getViewForPosition$3(HashMap sortRulesMap, ReactionDrawingObject o1, ReactionDrawingObject o2) {
        int i2 = Integer.MAX_VALUE;
        int i1 = sortRulesMap.containsKey(o1.reaction.reaction) ? ((Integer) sortRulesMap.get(o1.reaction.reaction)).intValue() : Integer.MAX_VALUE;
        if (sortRulesMap.containsKey(o2.reaction.reaction)) {
            i2 = ((Integer) sortRulesMap.get(o2.reaction.reaction)).intValue();
        }
        return i2 - i1;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        if (this.viewPager.getChildCount() > 0) {
            ViewPage page = (ViewPage) this.viewPager.getChildAt(0);
            if (page.topView instanceof PremiumAppIconsPreviewView) {
                final PremiumAppIconsPreviewView premiumAppIconsPreviewView = (PremiumAppIconsPreviewView) page.topView;
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{(float) page.getMeasuredWidth(), 0.0f});
                premiumAppIconsPreviewView.setOffset((float) page.getMeasuredWidth());
                this.enterAnimationIsRunning = true;
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        premiumAppIconsPreviewView.setOffset(((Float) animation.getAnimatedValue()).floatValue());
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        PremiumFeatureBottomSheet.this.enterAnimationIsRunning = false;
                        premiumAppIconsPreviewView.setOffset(0.0f);
                        super.onAnimationEnd(animation);
                    }
                });
                valueAnimator.setDuration(500);
                valueAnimator.setStartDelay(100);
                valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                valueAnimator.start();
            }
        }
        return super.onCustomOpenAnimation();
    }
}
