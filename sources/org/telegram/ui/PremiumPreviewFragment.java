package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.AboutPremiumView;
import org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.PremiumNotAvailableBottomSheet;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;

public class PremiumPreviewFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int PREMIUM_FEATURE_ADS = 3;
    public static final int PREMIUM_FEATURE_ADVANCED_CHAT_MANAGEMENT = 9;
    public static final int PREMIUM_FEATURE_ANIMATED_AVATARS = 7;
    public static final int PREMIUM_FEATURE_APPLICATION_ICONS = 10;
    public static final int PREMIUM_FEATURE_DOWNLOAD_SPEED = 2;
    public static final int PREMIUM_FEATURE_LIMITS = 0;
    public static final int PREMIUM_FEATURE_PROFILE_BADGE = 6;
    public static final int PREMIUM_FEATURE_REACTIONS = 4;
    public static final int PREMIUM_FEATURE_STICKERS = 5;
    public static final int PREMIUM_FEATURE_UPLOAD_LIMIT = 1;
    public static final int PREMIUM_FEATURE_VOICE_TO_TEXT = 8;
    BackgroundView backgroundView;
    private FrameLayout buttonContainer;
    private View buttonDivider;
    /* access modifiers changed from: private */
    public FrameLayout contentView;
    /* access modifiers changed from: private */
    public int currentYOffset;
    PremiumFeatureCell dummyCell;
    int featuresEndRow;
    int featuresStartRow;
    /* access modifiers changed from: private */
    public int firstViewHeight;
    /* access modifiers changed from: private */
    public boolean forcePremium;
    final Canvas gradientCanvas;
    Paint gradientPaint = new Paint(1);
    final Bitmap gradientTextureBitmap;
    PremiumGradient.GradientTools gradientTools;
    int helpUsRow;
    boolean inc;
    /* access modifiers changed from: private */
    public boolean isDialogVisible;
    boolean isLandscapeMode;
    int lastPaddingRow;
    FillLastLinearLayoutManager layoutManager;
    RecyclerListView listView;
    Matrix matrix = new Matrix();
    int paddingRow;
    StarParticlesView particlesView;
    private PremiumButtonView premiumButtonView;
    ArrayList<PremiumFeatureData> premiumFeatures = new ArrayList<>();
    int privacyRow;
    float progress;
    float progressToFull;
    int rowCount;
    int sectionRow;
    FrameLayout settingsView;
    Shader shader;
    Drawable shadowDrawable;
    private String source;
    /* access modifiers changed from: private */
    public int statusBarHeight;
    int statusRow;
    int totalGradientHeight;
    float totalProgress;

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int severStringToFeatureType(java.lang.String r13) {
        /*
            int r0 = r13.hashCode()
            r1 = 10
            r2 = 7
            r3 = 6
            r4 = 9
            r5 = 5
            r6 = 4
            r7 = 3
            r8 = 8
            r9 = 2
            r10 = 1
            r11 = 0
            r12 = -1
            switch(r0) {
                case -2145993328: goto L_0x007f;
                case -1755514268: goto L_0x0075;
                case -1040323278: goto L_0x006b;
                case -1023650261: goto L_0x0061;
                case -730864243: goto L_0x0056;
                case -448825858: goto L_0x004c;
                case -165039170: goto L_0x0042;
                case -96210874: goto L_0x0038;
                case 1182539900: goto L_0x002e;
                case 1219849581: goto L_0x0024;
                case 1832801148: goto L_0x0018;
                default: goto L_0x0016;
            }
        L_0x0016:
            goto L_0x008a
        L_0x0018:
            java.lang.String r0 = "app_icons"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 10
            goto L_0x008b
        L_0x0024:
            java.lang.String r0 = "advanced_chat_management"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 7
            goto L_0x008b
        L_0x002e:
            java.lang.String r0 = "unique_reactions"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 5
            goto L_0x008b
        L_0x0038:
            java.lang.String r0 = "double_limits"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 0
            goto L_0x008b
        L_0x0042:
            java.lang.String r0 = "premium_stickers"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 6
            goto L_0x008b
        L_0x004c:
            java.lang.String r0 = "faster_download"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 2
            goto L_0x008b
        L_0x0056:
            java.lang.String r0 = "profile_badge"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 8
            goto L_0x008b
        L_0x0061:
            java.lang.String r0 = "more_upload"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 1
            goto L_0x008b
        L_0x006b:
            java.lang.String r0 = "no_ads"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 4
            goto L_0x008b
        L_0x0075:
            java.lang.String r0 = "voice_to_text"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 3
            goto L_0x008b
        L_0x007f:
            java.lang.String r0 = "animated_userpics"
            boolean r0 = r13.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = 9
            goto L_0x008b
        L_0x008a:
            r0 = -1
        L_0x008b:
            switch(r0) {
                case 0: goto L_0x0099;
                case 1: goto L_0x0098;
                case 2: goto L_0x0097;
                case 3: goto L_0x0096;
                case 4: goto L_0x0095;
                case 5: goto L_0x0094;
                case 6: goto L_0x0093;
                case 7: goto L_0x0092;
                case 8: goto L_0x0091;
                case 9: goto L_0x0090;
                case 10: goto L_0x008f;
                default: goto L_0x008e;
            }
        L_0x008e:
            return r12
        L_0x008f:
            return r1
        L_0x0090:
            return r2
        L_0x0091:
            return r3
        L_0x0092:
            return r4
        L_0x0093:
            return r5
        L_0x0094:
            return r6
        L_0x0095:
            return r7
        L_0x0096:
            return r8
        L_0x0097:
            return r9
        L_0x0098:
            return r10
        L_0x0099:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.severStringToFeatureType(java.lang.String):int");
    }

    public static String featureTypeToServerString(int type) {
        switch (type) {
            case 0:
                return "double_limits";
            case 1:
                return "more_upload";
            case 2:
                return "faster_download";
            case 3:
                return "no_ads";
            case 4:
                return "unique_reactions";
            case 5:
                return "premium_stickers";
            case 6:
                return "profile_badge";
            case 7:
                return "animated_userpics";
            case 8:
                return "voice_to_text";
            case 9:
                return "advanced_chat_management";
            case 10:
                return "app_icons";
            default:
                return null;
        }
    }

    public PremiumPreviewFragment setForcePremium() {
        this.forcePremium = true;
        return this;
    }

    public PremiumPreviewFragment(String source2) {
        Bitmap createBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.gradientTextureBitmap = createBitmap;
        this.gradientCanvas = new Canvas(createBitmap);
        this.gradientTools = new PremiumGradient.GradientTools("premiumGradientBackground1", "premiumGradientBackground2", "premiumGradientBackground3", "premiumGradientBackground4");
        this.source = source2;
    }

    public View createView(Context context) {
        Context context2 = context;
        this.hasOwnBackground = true;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{-816858, -2401123, -5806081, -11164161}, new float[]{0.0f, 0.32f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
        this.shader = linearGradient;
        linearGradient.setLocalMatrix(this.matrix);
        this.gradientPaint.setShader(this.shader);
        this.dummyCell = new PremiumFeatureCell(context2);
        this.premiumFeatures.clear();
        fillPremiumFeaturesList(this.premiumFeatures, this.currentAccount);
        final Rect padding = new Rect();
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(padding);
        if (Build.VERSION.SDK_INT >= 21) {
            this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
        }
        AnonymousClass1 r4 = new FrameLayout(context2) {
            boolean iconInterceptedTouch;
            int lastSize;

            public boolean dispatchTouchEvent(MotionEvent ev) {
                float iconX = PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX();
                float iconY = PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY();
                AndroidUtilities.rectTmp.set(iconX, iconY, ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()) + iconX, ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight()) + iconY);
                if (!AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY()) && !this.iconInterceptedTouch) {
                    return super.dispatchTouchEvent(ev);
                }
                ev.offsetLocation(-iconX, -iconY);
                if (ev.getAction() == 0 || ev.getAction() == 2) {
                    this.iconInterceptedTouch = true;
                } else if (ev.getAction() == 1 || ev.getAction() == 3) {
                    this.iconInterceptedTouch = false;
                }
                PremiumPreviewFragment.this.backgroundView.imageView.dispatchTouchEvent(ev);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int i = 0;
                if (View.MeasureSpec.getSize(widthMeasureSpec) > View.MeasureSpec.getSize(heightMeasureSpec)) {
                    PremiumPreviewFragment.this.isLandscapeMode = true;
                } else {
                    PremiumPreviewFragment.this.isLandscapeMode = false;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    int unused = PremiumPreviewFragment.this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
                }
                PremiumPreviewFragment.this.backgroundView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                PremiumPreviewFragment.this.particlesView.getLayoutParams().height = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight();
                if (!PremiumPreviewFragment.this.getUserConfig().isPremium() && !PremiumPreviewFragment.this.forcePremium) {
                    i = AndroidUtilities.dp(68.0f);
                }
                int buttonHeight = i;
                PremiumPreviewFragment.this.layoutManager.setAdditionalHeight((PremiumPreviewFragment.this.statusBarHeight + buttonHeight) - AndroidUtilities.dp(16.0f));
                PremiumPreviewFragment.this.layoutManager.setMinimumLastViewHeight(buttonHeight);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (this.lastSize != ((getMeasuredHeight() + getMeasuredWidth()) << 16)) {
                    PremiumPreviewFragment.this.updateBackgroundImage();
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleX = ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()) / ((float) getMeasuredWidth());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleY = ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight()) / ((float) getMeasuredHeight());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = (PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX()) / ((float) getMeasuredWidth());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY()) / ((float) getMeasuredHeight());
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                PremiumPreviewFragment.this.measureGradient(w, h);
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                int i = 0;
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    if (PremiumPreviewFragment.this.inc) {
                        PremiumPreviewFragment.this.progress += 0.016f;
                        if (PremiumPreviewFragment.this.progress > 3.0f) {
                            PremiumPreviewFragment.this.inc = false;
                        }
                    } else {
                        PremiumPreviewFragment.this.progress -= 0.016f;
                        if (PremiumPreviewFragment.this.progress < 1.0f) {
                            PremiumPreviewFragment.this.inc = true;
                        }
                    }
                }
                View firstView = null;
                if (PremiumPreviewFragment.this.listView.getLayoutManager() != null) {
                    firstView = PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0);
                }
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                if (firstView != null) {
                    i = firstView.getBottom();
                }
                int unused = premiumPreviewFragment.currentYOffset = i;
                int h = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                PremiumPreviewFragment premiumPreviewFragment2 = PremiumPreviewFragment.this;
                premiumPreviewFragment2.totalProgress = 1.0f - (((float) (premiumPreviewFragment2.currentYOffset - h)) / ((float) (PremiumPreviewFragment.this.firstViewHeight - h)));
                PremiumPreviewFragment premiumPreviewFragment3 = PremiumPreviewFragment.this;
                premiumPreviewFragment3.totalProgress = Utilities.clamp(premiumPreviewFragment3.totalProgress, 1.0f, 0.0f);
                int maxTop = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                if (PremiumPreviewFragment.this.currentYOffset < maxTop) {
                    int unused2 = PremiumPreviewFragment.this.currentYOffset = maxTop;
                }
                float oldProgress = PremiumPreviewFragment.this.progressToFull;
                PremiumPreviewFragment.this.progressToFull = 0.0f;
                if (PremiumPreviewFragment.this.currentYOffset < AndroidUtilities.dp(30.0f) + maxTop) {
                    PremiumPreviewFragment.this.progressToFull = ((float) ((AndroidUtilities.dp(30.0f) + maxTop) - PremiumPreviewFragment.this.currentYOffset)) / ((float) AndroidUtilities.dp(30.0f));
                }
                if (PremiumPreviewFragment.this.isLandscapeMode) {
                    PremiumPreviewFragment.this.progressToFull = 1.0f;
                    PremiumPreviewFragment.this.totalProgress = 1.0f;
                }
                if (oldProgress != PremiumPreviewFragment.this.progressToFull) {
                    PremiumPreviewFragment.this.listView.invalidate();
                }
                float translationsY = Math.max((((((float) ((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() - PremiumPreviewFragment.this.statusBarHeight) - PremiumPreviewFragment.this.backgroundView.titleView.getMeasuredHeight())) / 2.0f) + ((float) PremiumPreviewFragment.this.statusBarHeight)) - ((float) PremiumPreviewFragment.this.backgroundView.getTop())) - ((float) PremiumPreviewFragment.this.backgroundView.titleView.getTop()), (float) ((PremiumPreviewFragment.this.currentYOffset - ((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() + PremiumPreviewFragment.this.backgroundView.getMeasuredHeight()) - PremiumPreviewFragment.this.statusBarHeight)) + AndroidUtilities.dp(16.0f)));
                float iconTranslationsY = ((-translationsY) / 4.0f) + ((float) AndroidUtilities.dp(16.0f));
                PremiumPreviewFragment.this.backgroundView.setTranslationY(translationsY);
                PremiumPreviewFragment.this.backgroundView.imageView.setTranslationY(((float) AndroidUtilities.dp(16.0f)) + iconTranslationsY);
                float s = ((1.0f - PremiumPreviewFragment.this.totalProgress) * 0.4f) + 0.6f;
                float alpha = 1.0f - (PremiumPreviewFragment.this.totalProgress > 0.5f ? (PremiumPreviewFragment.this.totalProgress - 0.5f) / 0.5f : 0.0f);
                PremiumPreviewFragment.this.backgroundView.imageView.setScaleX(s);
                PremiumPreviewFragment.this.backgroundView.imageView.setScaleY(s);
                PremiumPreviewFragment.this.backgroundView.imageView.setAlpha(alpha);
                PremiumPreviewFragment.this.backgroundView.subtitleView.setAlpha(alpha);
                PremiumPreviewFragment.this.particlesView.setAlpha(1.0f - PremiumPreviewFragment.this.totalProgress);
                PremiumPreviewFragment.this.particlesView.setTranslationY((((float) (-(PremiumPreviewFragment.this.particlesView.getMeasuredHeight() - PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()))) / 2.0f) + PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY());
                View view = firstView;
                PremiumPreviewFragment.this.backgroundView.titleView.setTranslationX((1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(1.0f - (PremiumPreviewFragment.this.totalProgress > 0.3f ? (PremiumPreviewFragment.this.totalProgress - 0.3f) / 0.7f : 0.0f))) * ((float) (AndroidUtilities.dp(72.0f) - PremiumPreviewFragment.this.backgroundView.titleView.getLeft())));
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = ((PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX()) + ((((float) getMeasuredWidth()) * 0.1f) * PremiumPreviewFragment.this.progress)) / ((float) getMeasuredWidth());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY()) / ((float) getMeasuredHeight());
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    invalidate();
                }
                PremiumPreviewFragment.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), ((float) (-getMeasuredWidth())) * 0.1f * PremiumPreviewFragment.this.progress, 0.0f);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (PremiumPreviewFragment.this.currentYOffset + AndroidUtilities.dp(20.0f)), PremiumPreviewFragment.this.gradientTools.paint);
                super.dispatchDraw(canvas);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != PremiumPreviewFragment.this.listView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                canvas.save();
                canvas.clipRect(0, PremiumPreviewFragment.this.actionBar.getBottom(), getMeasuredWidth(), getMeasuredHeight());
                super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return true;
            }
        };
        this.contentView = r4;
        r4.setFitsSystemWindows(true);
        AnonymousClass2 r42 = new RecyclerListView(context2) {
            public void onDraw(Canvas canvas) {
                PremiumPreviewFragment.this.shadowDrawable.setBounds((int) (((float) (-padding.left)) - (((float) AndroidUtilities.dp(16.0f)) * PremiumPreviewFragment.this.progressToFull)), (PremiumPreviewFragment.this.currentYOffset - padding.top) - AndroidUtilities.dp(16.0f), (int) (((float) (getMeasuredWidth() + padding.right)) + (((float) AndroidUtilities.dp(16.0f)) * PremiumPreviewFragment.this.progressToFull)), getMeasuredHeight());
                PremiumPreviewFragment.this.shadowDrawable.draw(canvas);
                super.onDraw(canvas);
            }
        };
        this.listView = r42;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context2, (AndroidUtilities.dp(68.0f) + this.statusBarHeight) - AndroidUtilities.dp(16.0f), this.listView);
        this.layoutManager = fillLastLinearLayoutManager;
        r42.setLayoutManager(fillLastLinearLayoutManager);
        this.layoutManager.setFixedLastItemHeight();
        this.listView.setAdapter(new Adapter());
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    int maxTop = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                    if (PremiumPreviewFragment.this.totalProgress > 0.5f) {
                        PremiumPreviewFragment.this.listView.smoothScrollBy(0, PremiumPreviewFragment.this.currentYOffset - maxTop);
                    } else {
                        View firstView = null;
                        if (PremiumPreviewFragment.this.listView.getLayoutManager() != null) {
                            firstView = PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0);
                        }
                        if (firstView != null && firstView.getTop() < 0) {
                            PremiumPreviewFragment.this.listView.smoothScrollBy(0, firstView.getTop());
                        }
                    }
                }
                PremiumPreviewFragment.this.checkButtonDivider();
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                PremiumPreviewFragment.this.contentView.invalidate();
                PremiumPreviewFragment.this.checkButtonDivider();
            }
        });
        this.backgroundView = new BackgroundView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        this.particlesView = new StarParticlesView(context2);
        this.backgroundView.imageView.setStarParticlesView(this.particlesView);
        this.contentView.addView(this.particlesView, LayoutHelper.createFrame(-1, -2.0f));
        this.contentView.addView(this.backgroundView, LayoutHelper.createFrame(-1, -2.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PremiumPreviewFragment$$ExternalSyntheticLambda2(this));
        this.contentView.addView(this.listView);
        PremiumButtonView premiumButtonView2 = new PremiumButtonView(context2, false);
        this.premiumButtonView = premiumButtonView2;
        premiumButtonView2.setButton(getPremiumButtonText(this.currentAccount), new PremiumPreviewFragment$$ExternalSyntheticLambda0(this));
        this.buttonContainer = new FrameLayout(context2);
        View view = new View(context2);
        this.buttonDivider = view;
        view.setBackgroundColor(Theme.getColor("divider"));
        this.buttonContainer.addView(this.buttonDivider, LayoutHelper.createFrame(-1, 1.0f));
        this.buttonDivider.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(this.buttonDivider, true, 1.0f, false);
        this.buttonContainer.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        this.buttonContainer.setBackgroundColor(getThemedColor("dialogBackground"));
        this.contentView.addView(this.buttonContainer, LayoutHelper.createFrame(-1, 68, 80));
        this.fragmentView = this.contentView;
        this.actionBar.setBackground((Drawable) null);
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PremiumPreviewFragment.this.finishFragment();
                }
            }
        });
        this.actionBar.setForceSkipTouches(true);
        updateColors();
        updateRows();
        this.backgroundView.imageView.startEnterAnimation(-180, 200);
        if (this.forcePremium) {
            AndroidUtilities.runOnUIThread(new PremiumPreviewFragment$$ExternalSyntheticLambda5(this), 400);
        }
        MediaDataController.getInstance(this.currentAccount).preloadPremiumPreviewStickers();
        sentShowScreenStat(this.source);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PremiumPreviewFragment  reason: not valid java name */
    public /* synthetic */ void m4354lambda$createView$0$orgtelegramuiPremiumPreviewFragment(View view, int position) {
        if (view instanceof PremiumFeatureCell) {
            PremiumFeatureCell cell = (PremiumFeatureCell) view;
            sentShowFeaturePreview(this.currentAccount, cell.data.type);
            if (cell.data.type == 0) {
                DoubledLimitsBottomSheet bottomSheet = new DoubledLimitsBottomSheet(this, this.currentAccount);
                bottomSheet.setParentFragment(this);
                showDialog(bottomSheet);
                return;
            }
            showDialog(new PremiumFeatureBottomSheet(this, cell.data.type, false));
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PremiumPreviewFragment  reason: not valid java name */
    public /* synthetic */ void m4355lambda$createView$1$orgtelegramuiPremiumPreviewFragment(View v) {
        buyPremium(this);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PremiumPreviewFragment  reason: not valid java name */
    public /* synthetic */ void m4356lambda$createView$2$orgtelegramuiPremiumPreviewFragment() {
        getMediaDataController().loadPremiumPromo(false);
    }

    public static void buyPremium(BaseFragment fragment) {
        buyPremium(fragment, "settings");
    }

    public static void fillPremiumFeaturesList(ArrayList<PremiumFeatureData> premiumFeatures2, int currentAccount) {
        MessagesController messagesController = MessagesController.getInstance(currentAccount);
        premiumFeatures2.add(new PremiumFeatureData(0, NUM, LocaleController.getString("PremiumPreviewLimits", NUM), LocaleController.formatString("PremiumPreviewLimitsDescription", NUM, Integer.valueOf(messagesController.channelsLimitPremium), Integer.valueOf(messagesController.dialogFiltersLimitPremium), Integer.valueOf(messagesController.dialogFiltersPinnedLimitPremium), Integer.valueOf(messagesController.publicLinksLimitPremium), 4)));
        premiumFeatures2.add(new PremiumFeatureData(1, NUM, LocaleController.getString("PremiumPreviewUploads", NUM), LocaleController.getString("PremiumPreviewUploadsDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(2, NUM, LocaleController.getString("PremiumPreviewDownloadSpeed", NUM), LocaleController.getString("PremiumPreviewDownloadSpeedDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(8, NUM, LocaleController.getString("PremiumPreviewVoiceToText", NUM), LocaleController.getString("PremiumPreviewVoiceToTextDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(3, NUM, LocaleController.getString("PremiumPreviewNoAds", NUM), LocaleController.getString("PremiumPreviewNoAdsDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(4, NUM, LocaleController.getString("PremiumPreviewReactions", NUM), LocaleController.getString("PremiumPreviewReactionsDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(5, NUM, LocaleController.getString("PremiumPreviewStickers", NUM), LocaleController.getString("PremiumPreviewStickersDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(9, NUM, LocaleController.getString("PremiumPreviewAdvancedChatManagement", NUM), LocaleController.getString("PremiumPreviewAdvancedChatManagementDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(6, NUM, LocaleController.getString("PremiumPreviewProfileBadge", NUM), LocaleController.getString("PremiumPreviewProfileBadgeDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(7, NUM, LocaleController.getString("PremiumPreviewAnimatedProfiles", NUM), LocaleController.getString("PremiumPreviewAnimatedProfilesDescription", NUM)));
        premiumFeatures2.add(new PremiumFeatureData(10, NUM, LocaleController.getString("PremiumPreviewAppIcon", NUM), LocaleController.getString("PremiumPreviewAppIconDescription", NUM)));
        if (messagesController.premiumFeaturesTypesToPosition.size() > 0) {
            int i = 0;
            while (i < premiumFeatures2.size()) {
                if (messagesController.premiumFeaturesTypesToPosition.get(premiumFeatures2.get(i).type, -1) == -1) {
                    premiumFeatures2.remove(i);
                    i--;
                }
                i++;
            }
        }
        Collections.sort(premiumFeatures2, new PremiumPreviewFragment$$ExternalSyntheticLambda6(messagesController));
    }

    static /* synthetic */ int lambda$fillPremiumFeaturesList$3(MessagesController messagesController, PremiumFeatureData o1, PremiumFeatureData o2) {
        return messagesController.premiumFeaturesTypesToPosition.get(o1.type, Integer.MAX_VALUE) - messagesController.premiumFeaturesTypesToPosition.get(o2.type, Integer.MAX_VALUE);
    }

    /* access modifiers changed from: private */
    public void updateBackgroundImage() {
        if (this.contentView.getMeasuredWidth() != 0 && this.contentView.getMeasuredHeight() != 0) {
            this.gradientTools.gradientMatrix(0, 0, this.contentView.getMeasuredWidth(), this.contentView.getMeasuredHeight(), 0.0f, 0.0f);
            this.gradientCanvas.save();
            this.gradientCanvas.scale(100.0f / ((float) this.contentView.getMeasuredWidth()), 100.0f / ((float) this.contentView.getMeasuredHeight()));
            this.gradientCanvas.drawRect(0.0f, 0.0f, (float) this.contentView.getMeasuredWidth(), (float) this.contentView.getMeasuredHeight(), this.gradientTools.paint);
            this.gradientCanvas.restore();
            this.backgroundView.imageView.setBackgroundBitmap(this.gradientTextureBitmap);
        }
    }

    /* access modifiers changed from: private */
    public void checkButtonDivider() {
        AndroidUtilities.updateViewVisibilityAnimated(this.buttonDivider, this.listView.canScrollVertically(1), 1.0f, true);
    }

    public static void buyPremium(BaseFragment fragment, String source2) {
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            fragment.showDialog(new PremiumNotAvailableBottomSheet(fragment));
            return;
        }
        sentPremiumButtonClick();
        if (BuildVars.useInvoiceBilling()) {
            Activity activity = fragment.getParentActivity();
            if (activity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) activity;
                if (!TextUtils.isEmpty(fragment.getMessagesController().premiumBotUsername)) {
                    launchActivity.setNavigateToPremiumBot(true);
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/" + fragment.getMessagesController().premiumBotUsername + "?start=" + source2)));
                } else if (!TextUtils.isEmpty(fragment.getMessagesController().premiumInvoiceSlug)) {
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/$" + fragment.getMessagesController().premiumInvoiceSlug)));
                }
            }
        } else if (BillingController.PREMIUM_PRODUCT_DETAILS != null) {
            List<ProductDetails.SubscriptionOfferDetails> offerDetails = BillingController.PREMIUM_PRODUCT_DETAILS.getSubscriptionOfferDetails();
            if (!offerDetails.isEmpty()) {
                BillingController.getInstance().addResultListener("telegram_premium", new PremiumPreviewFragment$$ExternalSyntheticLambda3(fragment));
                TLRPC.TL_payments_canPurchasePremium req = new TLRPC.TL_payments_canPurchasePremium();
                fragment.getConnectionsManager().sendRequest(req, new PremiumPreviewFragment$$ExternalSyntheticLambda7(fragment, offerDetails, req));
            }
        }
    }

    static /* synthetic */ void lambda$buyPremium$4(BaseFragment fragment, BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            if (fragment instanceof PremiumPreviewFragment) {
                PremiumPreviewFragment premiumPreviewFragment = (PremiumPreviewFragment) fragment;
                premiumPreviewFragment.setForcePremium();
                premiumPreviewFragment.getMediaDataController().loadPremiumPromo(false);
                premiumPreviewFragment.listView.smoothScrollToPosition(0);
            } else {
                fragment.presentFragment(new PremiumPreviewFragment((String) null).setForcePremium());
            }
            if (fragment.getParentActivity() instanceof LaunchActivity) {
                try {
                    fragment.getFragmentView().performHapticFeedback(3, 2);
                } catch (Exception e) {
                }
                ((LaunchActivity) fragment.getParentActivity()).getFireworksOverlay().start();
            }
        } else if (billingResult.getResponseCode() == 1) {
            sentPremiumBuyCanceled();
        }
    }

    static /* synthetic */ void lambda$buyPremium$5(TLObject response, BaseFragment fragment, List offerDetails, TLRPC.TL_error error, TLRPC.TL_payments_canPurchasePremium req) {
        if (response instanceof TLRPC.TL_boolTrue) {
            BillingController.getInstance().launchBillingFlow(fragment.getParentActivity(), Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS).setOfferToken(((ProductDetails.SubscriptionOfferDetails) offerDetails.get(0)).getOfferToken()).build()));
        } else {
            AlertsCreator.processError(fragment.getCurrentAccount(), error, fragment, req, new Object[0]);
        }
    }

    public static String getPremiumButtonText(int currentAccount) {
        Currency currency;
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            return LocaleController.getString(NUM);
        }
        if (BuildVars.useInvoiceBilling()) {
            TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(currentAccount).getPremiumPromo();
            if (premiumPromo == null || (currency = Currency.getInstance(premiumPromo.currency)) == null) {
                return LocaleController.getString(NUM);
            }
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
            numberFormat.setCurrency(currency);
            double d = (double) premiumPromo.monthly_amount;
            double pow = Math.pow(10.0d, (double) BillingController.getInstance().getCurrencyExp(premiumPromo.currency));
            Double.isNaN(d);
            return LocaleController.formatString(NUM, numberFormat.format(d / pow));
        }
        String price = null;
        if (BillingController.PREMIUM_PRODUCT_DETAILS != null) {
            List<ProductDetails.SubscriptionOfferDetails> details = BillingController.PREMIUM_PRODUCT_DETAILS.getSubscriptionOfferDetails();
            if (!details.isEmpty()) {
                Iterator<ProductDetails.PricingPhase> it = details.get(0).getPricingPhases().getPricingPhaseList().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ProductDetails.PricingPhase phase = it.next();
                    if (phase.getBillingPeriod().equals("P1M")) {
                        price = phase.getFormattedPrice();
                        break;
                    }
                }
            }
        }
        if (price == null) {
            return LocaleController.getString(NUM);
        }
        return LocaleController.formatString(NUM, price);
    }

    /* access modifiers changed from: private */
    public void measureGradient(int w, int h) {
        int yOffset = 0;
        for (int i = 0; i < this.premiumFeatures.size(); i++) {
            this.dummyCell.setData(this.premiumFeatures.get(i), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(w, NUM), View.MeasureSpec.makeMeasureSpec(h, Integer.MIN_VALUE));
            this.premiumFeatures.get(i).yOffset = yOffset;
            yOffset += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = yOffset;
    }

    private void updateRows() {
        int buttonHeight = 0;
        this.rowCount = 0;
        this.sectionRow = -1;
        this.statusRow = -1;
        this.privacyRow = -1;
        int i = 0 + 1;
        this.rowCount = i;
        this.paddingRow = 0;
        this.featuresStartRow = i;
        int size = i + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        int i2 = size + 1;
        this.rowCount = i2;
        this.statusRow = size;
        this.rowCount = i2 + 1;
        this.lastPaddingRow = i2;
        if (getUserConfig().isPremium() || this.forcePremium) {
            this.buttonContainer.setVisibility(8);
        } else {
            this.buttonContainer.setVisibility(0);
        }
        if (this.buttonContainer.getVisibility() == 0) {
            buttonHeight = AndroidUtilities.dp(64.0f);
        }
        this.layoutManager.setAdditionalHeight((this.statusBarHeight + buttonHeight) - AndroidUtilities.dp(16.0f));
        this.layoutManager.setMinimumLastViewHeight(buttonHeight);
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return true;
    }

    public boolean onFragmentCreate() {
        if (getMessagesController().premiumLocked) {
            return false;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.premiumPromoUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.premiumPromoUpdated);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.billingProductDetailsUpdated || id == NotificationCenter.premiumPromoUpdated) {
            this.premiumButtonView.buttonTextView.setText(getPremiumButtonText(this.currentAccount));
        }
        if (id == NotificationCenter.currentUserPremiumStatusChanged || id == NotificationCenter.premiumPromoUpdated) {
            this.backgroundView.updateText();
            updateRows();
            this.listView.getAdapter().notifyDataSetChanged();
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private static final int TYPE_BOTTOM_PADDING = 6;
        private static final int TYPE_BUTTON = 3;
        private static final int TYPE_FEATURE = 1;
        private static final int TYPE_HELP_US = 4;
        private static final int TYPE_PADDING = 0;
        private static final int TYPE_SHADOW_SECTION = 2;
        private static final int TYPE_STATUS_TEXT = 5;

        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 1:
                    view = new PremiumFeatureCell(context) {
                        /* access modifiers changed from: protected */
                        public void dispatchDraw(Canvas canvas) {
                            AndroidUtilities.rectTmp.set((float) this.imageView.getLeft(), (float) this.imageView.getTop(), (float) this.imageView.getRight(), (float) this.imageView.getBottom());
                            PremiumPreviewFragment.this.matrix.reset();
                            PremiumPreviewFragment.this.matrix.postScale(1.0f, ((float) PremiumPreviewFragment.this.totalGradientHeight) / 100.0f, 0.0f, 0.0f);
                            PremiumPreviewFragment.this.matrix.postTranslate(0.0f, (float) (-this.data.yOffset));
                            PremiumPreviewFragment.this.shader.setLocalMatrix(PremiumPreviewFragment.this.matrix);
                            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), PremiumPreviewFragment.this.gradientPaint);
                            super.dispatchDraw(canvas);
                        }
                    };
                    break;
                case 2:
                    ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, NUM, Theme.getColor("windowBackgroundGrayShadow")), 0, 0);
                    combinedDrawable.setFullsize(true);
                    shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                    ShadowSectionCell shadowSectionCell2 = shadowSectionCell;
                    view = shadowSectionCell;
                    break;
                case 4:
                    view = new AboutPremiumView(context);
                    break;
                case 5:
                    view = new TextInfoPrivacyCell(context);
                    break;
                case 6:
                    View view2 = new View(context);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
                    view = view2;
                    break;
                default:
                    view = new View(context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (PremiumPreviewFragment.this.isLandscapeMode) {
                                int unused = PremiumPreviewFragment.this.firstViewHeight = (PremiumPreviewFragment.this.statusBarHeight + PremiumPreviewFragment.this.actionBar.getMeasuredHeight()) - AndroidUtilities.dp(16.0f);
                            } else {
                                int h = AndroidUtilities.dp(300.0f) + PremiumPreviewFragment.this.statusBarHeight;
                                if (PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f) > h) {
                                    h = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                                }
                                int unused2 = PremiumPreviewFragment.this.firstViewHeight = h;
                            }
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(PremiumPreviewFragment.this.firstViewHeight, NUM));
                        }
                    };
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Drawable background;
            Drawable shadowDrawable;
            int i;
            TextStyleSpan.TextStyleRun run;
            TextStyleSpan[] textStyleSpanArr;
            int i2;
            RecyclerView.ViewHolder viewHolder = holder;
            int i3 = position;
            boolean z = true;
            int i4 = 0;
            if (i3 >= PremiumPreviewFragment.this.featuresStartRow && i3 < PremiumPreviewFragment.this.featuresEndRow) {
                PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) viewHolder.itemView;
                PremiumFeatureData premiumFeatureData = PremiumPreviewFragment.this.premiumFeatures.get(i3 - PremiumPreviewFragment.this.featuresStartRow);
                if (i3 == PremiumPreviewFragment.this.featuresEndRow - 1) {
                    z = false;
                }
                premiumFeatureCell.setData(premiumFeatureData, z);
            } else if (i3 == PremiumPreviewFragment.this.statusRow || i3 == PremiumPreviewFragment.this.privacyRow) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                Drawable shadowDrawable2 = Theme.getThemedDrawable(privacyCell.getContext(), NUM, Theme.getColor("windowBackgroundGrayShadow"));
                Drawable background2 = new ColorDrawable(Theme.getColor("windowBackgroundGray"));
                CombinedDrawable combinedDrawable = new CombinedDrawable(background2, shadowDrawable2, 0, 0);
                combinedDrawable.setFullsize(true);
                privacyCell.setBackground(combinedDrawable);
                if (i3 == PremiumPreviewFragment.this.statusRow) {
                    TLRPC.TL_help_premiumPromo premiumPromo = PremiumPreviewFragment.this.getMediaDataController().getPremiumPromo();
                    if (premiumPromo != null) {
                        SpannableString spannableString = new SpannableString(premiumPromo.status_text);
                        MediaDataController.addTextStyleRuns(premiumPromo.status_entities, (CharSequence) premiumPromo.status_text, (Spannable) spannableString);
                        TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) spannableString.getSpans(0, spannableString.length(), TextStyleSpan.class);
                        int length = textStyleSpanArr2.length;
                        while (i4 < length) {
                            TextStyleSpan.TextStyleRun run2 = textStyleSpanArr2[i4].getTextStyleRun();
                            boolean setRun = false;
                            String url = run2.urlEntity != null ? TextUtils.substring(premiumPromo.status_text, run2.urlEntity.offset, run2.urlEntity.offset + run2.urlEntity.length) : null;
                            if (run2.urlEntity instanceof TLRPC.TL_messageEntityBotCommand) {
                                spannableString.setSpan(new URLSpanBotCommand(url, 0, run2), run2.start, run2.end, 33);
                                shadowDrawable = shadowDrawable2;
                                background = background2;
                                run = run2;
                                i = length;
                                textStyleSpanArr = textStyleSpanArr2;
                            } else {
                                if ((run2.urlEntity instanceof TLRPC.TL_messageEntityHashtag) || (run2.urlEntity instanceof TLRPC.TL_messageEntityMention)) {
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                    i2 = 33;
                                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityCashtag) {
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                    i2 = 33;
                                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityEmail) {
                                    spannableString.setSpan(new URLSpanReplacement("mailto:" + url, run2), run2.start, run2.end, 33);
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityUrl) {
                                    String lowerCase = url.toLowerCase();
                                    if (!lowerCase.contains("://")) {
                                        String str = lowerCase;
                                        spannableString.setSpan(new URLSpanBrowser("http://" + url, run2), run2.start, run2.end, 33);
                                    } else {
                                        spannableString.setSpan(new URLSpanBrowser(url, run2), run2.start, run2.end, 33);
                                    }
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityBankCard) {
                                    spannableString.setSpan(new URLSpanNoUnderline("card:" + url, run2), run2.start, run2.end, 33);
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityPhone) {
                                    String tel = PhoneFormat.stripExceptNumbers(url);
                                    if (url.startsWith("+")) {
                                        tel = "+" + tel;
                                    }
                                    String str2 = tel;
                                    spannableString.setSpan(new URLSpanBrowser("tel:" + tel, run2), run2.start, run2.end, 33);
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                                    URLSpanReplacement spanReplacement = new URLSpanReplacement(run2.urlEntity.url, run2);
                                    spanReplacement.setNavigateToPremiumBot(true);
                                    spannableString.setSpan(spanReplacement, run2.start, run2.end, 33);
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("");
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    sb.append(((TLRPC.TL_messageEntityMentionName) run2.urlEntity).user_id);
                                    spannableString.setSpan(new URLSpanUserMention(sb.toString(), 0, run2), run2.start, run2.end, 33);
                                    run = run2;
                                    i = length;
                                    textStyleSpanArr = textStyleSpanArr2;
                                } else {
                                    shadowDrawable = shadowDrawable2;
                                    background = background2;
                                    if (run2.urlEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                                        spannableString.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName) run2.urlEntity).user_id.user_id, 0, run2), run2.start, run2.end, 33);
                                        run = run2;
                                        i = length;
                                        textStyleSpanArr = textStyleSpanArr2;
                                    } else if ((run2.flags & 4) != 0) {
                                        run = run2;
                                        i = length;
                                        textStyleSpanArr = textStyleSpanArr2;
                                        spannableString.setSpan(new URLSpanMono(spannableString, run2.start, run2.end, (byte) 0, run), run.start, run.end, 33);
                                    } else {
                                        run = run2;
                                        i = length;
                                        textStyleSpanArr = textStyleSpanArr2;
                                        setRun = true;
                                        spannableString.setSpan(new TextStyleSpan(run), run.start, run.end, 33);
                                    }
                                }
                                spannableString.setSpan(new URLSpanNoUnderline(url, run), run.start, run.end, i2);
                            }
                            if (!setRun && (run.flags & 256) != 0) {
                                spannableString.setSpan(new TextStyleSpan(run), run.start, run.end, 33);
                            }
                            i4++;
                            RecyclerView.ViewHolder viewHolder2 = holder;
                            textStyleSpanArr2 = textStyleSpanArr;
                            length = i;
                            shadowDrawable2 = shadowDrawable;
                            background2 = background;
                            int i5 = position;
                        }
                        Drawable drawable = background2;
                        privacyCell.setText(spannableString);
                        return;
                    }
                    return;
                }
                Drawable drawable2 = background2;
            }
        }

        public int getItemCount() {
            return PremiumPreviewFragment.this.rowCount;
        }

        public int getItemViewType(int position) {
            if (position == PremiumPreviewFragment.this.paddingRow) {
                return 0;
            }
            if (position >= PremiumPreviewFragment.this.featuresStartRow && position < PremiumPreviewFragment.this.featuresEndRow) {
                return 1;
            }
            if (position == PremiumPreviewFragment.this.sectionRow) {
                return 2;
            }
            if (position == PremiumPreviewFragment.this.helpUsRow) {
                return 4;
            }
            if (position == PremiumPreviewFragment.this.statusRow || position == PremiumPreviewFragment.this.privacyRow) {
                return 5;
            }
            if (position == PremiumPreviewFragment.this.lastPaddingRow) {
                return 6;
            }
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }
    }

    public static class PremiumFeatureData {
        public final String description;
        public final int icon;
        public final String title;
        public final int type;
        public int yOffset;

        public PremiumFeatureData(int type2, int icon2, String title2, String description2) {
            this.type = type2;
            this.icon = icon2;
            this.title = title2;
            this.description = description2;
        }
    }

    private class BackgroundView extends LinearLayout {
        /* access modifiers changed from: private */
        public final GLIconTextureView imageView;
        /* access modifiers changed from: private */
        public final TextView subtitleView;
        TextView titleView;

        public BackgroundView(Context context) {
            super(context);
            setOrientation(1);
            final PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
            final Context context2 = context;
            AnonymousClass1 r1 = new GLIconTextureView(context, 0) {
                public void onLongPress() {
                    super.onLongPress();
                    if (PremiumPreviewFragment.this.settingsView == null || BuildVars.DEBUG_PRIVATE_VERSION) {
                        PremiumPreviewFragment.this.settingsView = new FrameLayout(context2);
                        ScrollView scrollView = new ScrollView(context2);
                        scrollView.addView(new GLIconSettingsView(context2, BackgroundView.this.imageView.mRenderer));
                        PremiumPreviewFragment.this.settingsView.addView(scrollView);
                        PremiumPreviewFragment.this.settingsView.setBackgroundColor(Theme.getColor("dialogBackground"));
                        PremiumPreviewFragment.this.contentView.addView(PremiumPreviewFragment.this.settingsView, LayoutHelper.createFrame(-1, -1, 80));
                        ((ViewGroup.MarginLayoutParams) PremiumPreviewFragment.this.settingsView.getLayoutParams()).topMargin = PremiumPreviewFragment.this.currentYOffset;
                        PremiumPreviewFragment.this.settingsView.setTranslationY((float) AndroidUtilities.dp(1000.0f));
                        PremiumPreviewFragment.this.settingsView.animate().translationY(1.0f).setDuration(300);
                    }
                }
            };
            this.imageView = r1;
            addView(r1, LayoutHelper.createLinear(190, 190, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 22.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleView.setGravity(1);
            addView(this.titleView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 16, 20, 16, 0));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextSize(1, 14.0f);
            textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            textView2.setGravity(1);
            addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 7, 16, 0));
            updateText();
        }

        public void updateText() {
            this.titleView.setText(LocaleController.getString(PremiumPreviewFragment.this.forcePremium ? NUM : NUM));
            this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString((PremiumPreviewFragment.this.getUserConfig().isPremium() || PremiumPreviewFragment.this.forcePremium) ? NUM : NUM)));
        }
    }

    public boolean isLightStatusBar() {
        return false;
    }

    public void onResume() {
        super.onResume();
        this.backgroundView.imageView.setPaused(false);
        this.backgroundView.imageView.setDialogVisible(false);
        this.particlesView.setPaused(false);
    }

    public void onPause() {
        super.onPause();
        this.backgroundView.imageView.setDialogVisible(true);
        this.particlesView.setPaused(true);
    }

    public boolean canBeginSlide() {
        return !this.backgroundView.imageView.touched;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new PremiumPreviewFragment$$ExternalSyntheticLambda1(this), "premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4", "premiumGradientBackground1", "premiumGradientBackground2", "premiumGradientBackground3", "premiumGradientBackground4", "premiumGradientBackgroundOverlay", "premiumStarGradient1", "premiumStarGradient2", "premiumStartSmallStarsColor", "premiumStartSmallStarsColor2");
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        if (this.backgroundView != null && this.actionBar != null) {
            this.actionBar.setItemsColor(Theme.getColor("premiumGradientBackgroundOverlay"), false);
            this.actionBar.setItemsBackgroundColor(ColorUtils.setAlphaComponent(Theme.getColor("premiumGradientBackgroundOverlay"), 60), false);
            this.backgroundView.titleView.setTextColor(Theme.getColor("premiumGradientBackgroundOverlay"));
            this.backgroundView.subtitleView.setTextColor(Theme.getColor("premiumGradientBackgroundOverlay"));
            this.particlesView.drawable.updateColors();
            if (this.backgroundView.imageView.mRenderer != null) {
                this.backgroundView.imageView.mRenderer.updateColors();
            }
            updateBackgroundImage();
        }
    }

    public boolean onBackPressed() {
        if (this.settingsView == null) {
            return super.onBackPressed();
        }
        closeSetting();
        return false;
    }

    private void closeSetting() {
        this.settingsView.animate().translationY((float) AndroidUtilities.dp(1000.0f)).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                PremiumPreviewFragment.this.contentView.removeView(PremiumPreviewFragment.this.settingsView);
                PremiumPreviewFragment.this.settingsView = null;
                super.onAnimationEnd(animation);
            }
        });
    }

    public Dialog showDialog(Dialog dialog) {
        Dialog d = super.showDialog(dialog);
        updateDialogVisibility(d != null);
        return d;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        updateDialogVisibility(false);
    }

    private void updateDialogVisibility(boolean isVisible) {
        if (isVisible != this.isDialogVisible) {
            this.isDialogVisible = isVisible;
            this.backgroundView.imageView.setDialogVisible(isVisible);
            this.particlesView.setPaused(isVisible);
            this.contentView.invalidate();
        }
    }

    private void sentShowScreenStat() {
        String str = this.source;
        if (str != null) {
            sentShowScreenStat(str);
            this.source = null;
        }
    }

    public static void sentShowScreenStat(String source2) {
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = (double) connectionsManager.getCurrentTime();
        event.type = "premium.promo_screen_show";
        TLRPC.TL_jsonObject data = new TLRPC.TL_jsonObject();
        event.data = data;
        TLRPC.TL_jsonObjectValue sourceObj = new TLRPC.TL_jsonObjectValue();
        TLRPC.TL_jsonString jsonString = new TLRPC.TL_jsonString();
        jsonString.value = source2;
        sourceObj.key = "source";
        sourceObj.value = jsonString;
        data.value.add(sourceObj);
        req.events.add(event);
        connectionsManager.sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda11.INSTANCE);
    }

    static /* synthetic */ void lambda$sentShowScreenStat$7(TLObject response, TLRPC.TL_error error) {
    }

    public static void sentPremiumButtonClick() {
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = (double) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        event.type = "premium.promo_screen_accept";
        event.data = new TLRPC.TL_jsonNull();
        req.events.add(event);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda8.INSTANCE);
    }

    static /* synthetic */ void lambda$sentPremiumButtonClick$8(TLObject response, TLRPC.TL_error error) {
    }

    public static void sentPremiumBuyCanceled() {
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = (double) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        event.type = "premium.promo_screen_fail";
        event.data = new TLRPC.TL_jsonNull();
        req.events.add(event);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda9.INSTANCE);
    }

    static /* synthetic */ void lambda$sentPremiumBuyCanceled$9(TLObject response, TLRPC.TL_error error) {
    }

    public static void sentShowFeaturePreview(int currentAccount, int type) {
        TLRPC.TL_help_saveAppLog req = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent event = new TLRPC.TL_inputAppEvent();
        event.time = (double) ConnectionsManager.getInstance(currentAccount).getCurrentTime();
        event.type = "premium.promo_screen_tap";
        TLRPC.TL_jsonObject data = new TLRPC.TL_jsonObject();
        event.data = data;
        TLRPC.TL_jsonObjectValue item = new TLRPC.TL_jsonObjectValue();
        TLRPC.TL_jsonString jsonString = new TLRPC.TL_jsonString();
        jsonString.value = featureTypeToServerString(type);
        item.key = "item";
        item.value = jsonString;
        data.value.add(item);
        req.events.add(event);
        event.data = data;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, PremiumPreviewFragment$$ExternalSyntheticLambda10.INSTANCE);
    }

    static /* synthetic */ void lambda$sentShowFeaturePreview$10(TLObject response, TLRPC.TL_error error) {
    }
}
