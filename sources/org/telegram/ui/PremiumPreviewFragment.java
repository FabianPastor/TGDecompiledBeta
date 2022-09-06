package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;
import org.telegram.tgnet.TLRPC$TL_help_saveAppLog;
import org.telegram.tgnet.TLRPC$TL_inputAppEvent;
import org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_inputStorePaymentPremiumSubscription;
import org.telegram.tgnet.TLRPC$TL_jsonNull;
import org.telegram.tgnet.TLRPC$TL_jsonObject;
import org.telegram.tgnet.TLRPC$TL_jsonObjectValue;
import org.telegram.tgnet.TLRPC$TL_jsonString;
import org.telegram.tgnet.TLRPC$TL_messageEntityBankCard;
import org.telegram.tgnet.TLRPC$TL_messageEntityBotCommand;
import org.telegram.tgnet.TLRPC$TL_messageEntityCashtag;
import org.telegram.tgnet.TLRPC$TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC$TL_messageEntityHashtag;
import org.telegram.tgnet.TLRPC$TL_messageEntityMention;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageEntityPhone;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC$TL_payments_assignPlayMarketTransaction;
import org.telegram.tgnet.TLRPC$TL_payments_canPurchasePremium;
import org.telegram.tgnet.TLRPC$TL_premiumSubscriptionOption;
import org.telegram.tgnet.TLRPC$Updates;
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
import org.telegram.ui.Components.Premium.PremiumTierCell;
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
    BackgroundView backgroundView;
    private FrameLayout buttonContainer;
    private View buttonDivider;
    /* access modifiers changed from: private */
    public FrameLayout contentView;
    /* access modifiers changed from: private */
    public int currentYOffset;
    PremiumFeatureCell dummyCell;
    PremiumTierCell dummyTierCell;
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
    int selectedTierIndex = 0;
    FrameLayout settingsView;
    Shader shader;
    Drawable shadowDrawable;
    private String source;
    /* access modifiers changed from: private */
    public int statusBarHeight;
    int statusRow;
    ArrayList<SubscriptionTier> subscriptionTiers = new ArrayList<>();
    PremiumGradient.GradientTools tiersGradientTools;
    int totalGradientHeight;
    float totalProgress;
    int totalTiersGradientHeight;

    public static String featureTypeToServerString(int i) {
        switch (i) {
            case 0:
                return "double_limits";
            case 1:
                return "more_upload";
            case 2:
                return "faster_download";
            case 3:
                return "no_ads";
            case 4:
                return "infinite_reactions";
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
            case 11:
                return "animated_emoji";
            case 12:
                return "emoji_status";
            default:
                return null;
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sentPremiumButtonClick$15(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sentPremiumBuyCanceled$16(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sentShowFeaturePreview$17(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sentShowScreenStat$14(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateButtonText$12(View view) {
    }

    public boolean isLightStatusBar() {
        return false;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return true;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int serverStringToFeatureType(java.lang.String r15) {
        /*
            r15.hashCode()
            int r0 = r15.hashCode()
            r1 = 12
            r2 = 11
            r3 = 10
            r4 = 9
            r5 = 8
            r6 = 7
            r7 = 6
            r8 = 5
            r9 = 4
            r10 = 3
            r11 = 2
            r12 = 1
            r13 = 0
            r14 = -1
            switch(r0) {
                case -2145993328: goto L_0x00b0;
                case -2080028929: goto L_0x00a4;
                case -1755514268: goto L_0x0098;
                case -1425144150: goto L_0x008d;
                case -1040323278: goto L_0x0082;
                case -1023650261: goto L_0x0077;
                case -969043445: goto L_0x006c;
                case -730864243: goto L_0x0061;
                case -448825858: goto L_0x0054;
                case -165039170: goto L_0x0047;
                case -96210874: goto L_0x003a;
                case 1219849581: goto L_0x002d;
                case 1832801148: goto L_0x0020;
                default: goto L_0x001d;
            }
        L_0x001d:
            r15 = -1
            goto L_0x00bb
        L_0x0020:
            java.lang.String r0 = "app_icons"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x0029
            goto L_0x001d
        L_0x0029:
            r15 = 12
            goto L_0x00bb
        L_0x002d:
            java.lang.String r0 = "advanced_chat_management"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x0036
            goto L_0x001d
        L_0x0036:
            r15 = 11
            goto L_0x00bb
        L_0x003a:
            java.lang.String r0 = "double_limits"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x0043
            goto L_0x001d
        L_0x0043:
            r15 = 10
            goto L_0x00bb
        L_0x0047:
            java.lang.String r0 = "premium_stickers"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x0050
            goto L_0x001d
        L_0x0050:
            r15 = 9
            goto L_0x00bb
        L_0x0054:
            java.lang.String r0 = "faster_download"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x005d
            goto L_0x001d
        L_0x005d:
            r15 = 8
            goto L_0x00bb
        L_0x0061:
            java.lang.String r0 = "profile_badge"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x006a
            goto L_0x001d
        L_0x006a:
            r15 = 7
            goto L_0x00bb
        L_0x006c:
            java.lang.String r0 = "emoji_status"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x0075
            goto L_0x001d
        L_0x0075:
            r15 = 6
            goto L_0x00bb
        L_0x0077:
            java.lang.String r0 = "more_upload"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x0080
            goto L_0x001d
        L_0x0080:
            r15 = 5
            goto L_0x00bb
        L_0x0082:
            java.lang.String r0 = "no_ads"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x008b
            goto L_0x001d
        L_0x008b:
            r15 = 4
            goto L_0x00bb
        L_0x008d:
            java.lang.String r0 = "animated_emoji"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x0096
            goto L_0x001d
        L_0x0096:
            r15 = 3
            goto L_0x00bb
        L_0x0098:
            java.lang.String r0 = "voice_to_text"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x00a2
            goto L_0x001d
        L_0x00a2:
            r15 = 2
            goto L_0x00bb
        L_0x00a4:
            java.lang.String r0 = "infinite_reactions"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x00ae
            goto L_0x001d
        L_0x00ae:
            r15 = 1
            goto L_0x00bb
        L_0x00b0:
            java.lang.String r0 = "animated_userpics"
            boolean r15 = r15.equals(r0)
            if (r15 != 0) goto L_0x00ba
            goto L_0x001d
        L_0x00ba:
            r15 = 0
        L_0x00bb:
            switch(r15) {
                case 0: goto L_0x00cb;
                case 1: goto L_0x00ca;
                case 2: goto L_0x00c9;
                case 3: goto L_0x00c8;
                case 4: goto L_0x00c7;
                case 5: goto L_0x00c6;
                case 6: goto L_0x00c5;
                case 7: goto L_0x00c4;
                case 8: goto L_0x00c3;
                case 9: goto L_0x00c2;
                case 10: goto L_0x00c1;
                case 11: goto L_0x00c0;
                case 12: goto L_0x00bf;
                default: goto L_0x00be;
            }
        L_0x00be:
            return r14
        L_0x00bf:
            return r3
        L_0x00c0:
            return r4
        L_0x00c1:
            return r13
        L_0x00c2:
            return r8
        L_0x00c3:
            return r11
        L_0x00c4:
            return r7
        L_0x00c5:
            return r1
        L_0x00c6:
            return r12
        L_0x00c7:
            return r10
        L_0x00c8:
            return r2
        L_0x00c9:
            return r5
        L_0x00ca:
            return r9
        L_0x00cb:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.serverStringToFeatureType(java.lang.String):int");
    }

    public PremiumPreviewFragment setForcePremium() {
        this.forcePremium = true;
        return this;
    }

    public PremiumPreviewFragment(String str) {
        Bitmap createBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.gradientTextureBitmap = createBitmap;
        this.gradientCanvas = new Canvas(createBitmap);
        this.gradientTools = new PremiumGradient.GradientTools("premiumGradientBackground1", "premiumGradientBackground2", "premiumGradientBackground3", "premiumGradientBackground4");
        PremiumGradient.GradientTools gradientTools2 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", (String) null, (String) null);
        this.tiersGradientTools = gradientTools2;
        gradientTools2.exactly = true;
        gradientTools2.x1 = 0.0f;
        gradientTools2.y1 = 0.0f;
        gradientTools2.x2 = 0.0f;
        gradientTools2.y2 = 1.0f;
        gradientTools2.cx = 0.0f;
        gradientTools2.cy = 0.0f;
        this.source = str;
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public View createView(Context context) {
        Context context2 = context;
        this.hasOwnBackground = true;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{Theme.getColor("premiumGradient4"), Theme.getColor("premiumGradient3"), Theme.getColor("premiumGradient2"), Theme.getColor("premiumGradient1"), Theme.getColor("premiumGradient0")}, new float[]{0.0f, 0.32f, 0.5f, 0.7f, 1.0f}, Shader.TileMode.CLAMP);
        this.shader = linearGradient;
        linearGradient.setLocalMatrix(this.matrix);
        this.gradientPaint.setShader(this.shader);
        this.dummyCell = new PremiumFeatureCell(context2);
        this.dummyTierCell = new PremiumTierCell(context2);
        this.premiumFeatures.clear();
        fillPremiumFeaturesList(this.premiumFeatures, this.currentAccount);
        final Rect rect = new Rect();
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(rect);
        if (Build.VERSION.SDK_INT >= 21) {
            this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
        }
        AnonymousClass1 r4 = new FrameLayout(context2) {
            boolean iconInterceptedTouch;
            int lastSize;
            boolean listInterceptedTouch;

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                float x = PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX();
                float y = PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(x, y, ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()) + x, ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight()) + y);
                if ((rectF.contains(motionEvent.getX(), motionEvent.getY()) || this.iconInterceptedTouch) && !PremiumPreviewFragment.this.listView.scrollingByUser) {
                    motionEvent.offsetLocation(-x, -y);
                    if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                        this.iconInterceptedTouch = true;
                    } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        this.iconInterceptedTouch = false;
                    }
                    PremiumPreviewFragment.this.backgroundView.imageView.dispatchTouchEvent(motionEvent);
                    return true;
                }
                float x2 = PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.tierListView.getX();
                float y2 = PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.tierListView.getY();
                rectF.set(x2, y2, ((float) PremiumPreviewFragment.this.backgroundView.tierListView.getWidth()) + x2, ((float) PremiumPreviewFragment.this.backgroundView.tierListView.getHeight()) + y2);
                if ((rectF.contains(motionEvent.getX(), motionEvent.getY()) || this.listInterceptedTouch) && !PremiumPreviewFragment.this.listView.scrollingByUser) {
                    motionEvent.offsetLocation(-x2, -y2);
                    if (motionEvent.getAction() == 0) {
                        this.listInterceptedTouch = true;
                    } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        this.listInterceptedTouch = false;
                    }
                    PremiumPreviewFragment.this.backgroundView.tierListView.dispatchTouchEvent(motionEvent);
                    if (this.listInterceptedTouch) {
                        return true;
                    }
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3 = 0;
                if (View.MeasureSpec.getSize(i) > View.MeasureSpec.getSize(i2)) {
                    PremiumPreviewFragment.this.isLandscapeMode = true;
                } else {
                    PremiumPreviewFragment.this.isLandscapeMode = false;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    int unused = PremiumPreviewFragment.this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
                }
                PremiumPreviewFragment.this.backgroundView.measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
                PremiumPreviewFragment.this.particlesView.getLayoutParams().height = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight();
                if (!PremiumPreviewFragment.this.getUserConfig().isPremium() && !PremiumPreviewFragment.this.forcePremium) {
                    i3 = AndroidUtilities.dp(68.0f);
                }
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                premiumPreviewFragment.layoutManager.setAdditionalHeight((premiumPreviewFragment.statusBarHeight + i3) - AndroidUtilities.dp(16.0f));
                PremiumPreviewFragment.this.layoutManager.setMinimumLastViewHeight(i3);
                super.onMeasure(i, i2);
                if (this.lastSize != ((getMeasuredHeight() + getMeasuredWidth()) << 16)) {
                    PremiumPreviewFragment.this.updateBackgroundImage();
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleX = ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()) / ((float) getMeasuredWidth());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleY = ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight()) / ((float) getMeasuredHeight());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = (PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX()) / ((float) getMeasuredWidth());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY()) / ((float) getMeasuredHeight());
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int i, int i2, int i3, int i4) {
                super.onSizeChanged(i, i2, i3, i4);
                PremiumPreviewFragment.this.measureGradient(i, i2);
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                int i = 0;
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                    if (premiumPreviewFragment.inc) {
                        float f = premiumPreviewFragment.progress + 0.016f;
                        premiumPreviewFragment.progress = f;
                        if (f > 3.0f) {
                            premiumPreviewFragment.inc = false;
                        }
                    } else {
                        float f2 = premiumPreviewFragment.progress - 0.016f;
                        premiumPreviewFragment.progress = f2;
                        if (f2 < 1.0f) {
                            premiumPreviewFragment.inc = true;
                        }
                    }
                }
                View view = null;
                if (PremiumPreviewFragment.this.listView.getLayoutManager() != null) {
                    view = PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0);
                }
                PremiumPreviewFragment premiumPreviewFragment2 = PremiumPreviewFragment.this;
                if (view != null) {
                    i = view.getBottom();
                }
                int unused = premiumPreviewFragment2.currentYOffset = i;
                int bottom = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                PremiumPreviewFragment premiumPreviewFragment3 = PremiumPreviewFragment.this;
                premiumPreviewFragment3.totalProgress = 1.0f - (((float) (premiumPreviewFragment3.currentYOffset - bottom)) / ((float) (PremiumPreviewFragment.this.firstViewHeight - bottom)));
                PremiumPreviewFragment premiumPreviewFragment4 = PremiumPreviewFragment.this;
                float f3 = 0.0f;
                premiumPreviewFragment4.totalProgress = Utilities.clamp(premiumPreviewFragment4.totalProgress, 1.0f, 0.0f);
                int bottom2 = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                if (PremiumPreviewFragment.this.currentYOffset < bottom2) {
                    int unused2 = PremiumPreviewFragment.this.currentYOffset = bottom2;
                }
                PremiumPreviewFragment premiumPreviewFragment5 = PremiumPreviewFragment.this;
                float f4 = premiumPreviewFragment5.progressToFull;
                premiumPreviewFragment5.progressToFull = 0.0f;
                if (premiumPreviewFragment5.currentYOffset < AndroidUtilities.dp(30.0f) + bottom2) {
                    PremiumPreviewFragment.this.progressToFull = ((float) ((bottom2 + AndroidUtilities.dp(30.0f)) - PremiumPreviewFragment.this.currentYOffset)) / ((float) AndroidUtilities.dp(30.0f));
                }
                PremiumPreviewFragment premiumPreviewFragment6 = PremiumPreviewFragment.this;
                if (premiumPreviewFragment6.isLandscapeMode) {
                    premiumPreviewFragment6.progressToFull = 1.0f;
                    premiumPreviewFragment6.totalProgress = 1.0f;
                }
                if (f4 != premiumPreviewFragment6.progressToFull) {
                    premiumPreviewFragment6.listView.invalidate();
                }
                float max = Math.max((((((float) ((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() - PremiumPreviewFragment.this.statusBarHeight) - PremiumPreviewFragment.this.backgroundView.titleView.getMeasuredHeight())) / 2.0f) + ((float) PremiumPreviewFragment.this.statusBarHeight)) - ((float) PremiumPreviewFragment.this.backgroundView.getTop())) - ((float) PremiumPreviewFragment.this.backgroundView.titleView.getTop()), (float) ((PremiumPreviewFragment.this.currentYOffset - ((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() + PremiumPreviewFragment.this.backgroundView.getMeasuredHeight()) - PremiumPreviewFragment.this.statusBarHeight)) + AndroidUtilities.dp(PremiumPreviewFragment.this.backgroundView.tierListView.getVisibility() == 0 ? 24.0f : 16.0f)));
                float dp = ((-max) / 4.0f) + ((float) AndroidUtilities.dp(16.0f));
                PremiumPreviewFragment.this.backgroundView.setTranslationY(max);
                PremiumPreviewFragment.this.backgroundView.imageView.setTranslationY(dp + ((float) AndroidUtilities.dp(16.0f)));
                PremiumPreviewFragment premiumPreviewFragment7 = PremiumPreviewFragment.this;
                float f5 = premiumPreviewFragment7.totalProgress;
                float f6 = ((1.0f - f5) * 0.4f) + 0.6f;
                float f7 = 1.0f - (f5 > 0.5f ? (f5 - 0.5f) / 0.5f : 0.0f);
                premiumPreviewFragment7.backgroundView.imageView.setScaleX(f6);
                PremiumPreviewFragment.this.backgroundView.imageView.setScaleY(f6);
                PremiumPreviewFragment.this.backgroundView.imageView.setAlpha(f7);
                PremiumPreviewFragment.this.backgroundView.subtitleView.setAlpha(f7);
                PremiumPreviewFragment.this.backgroundView.tierListView.setAlpha(f7);
                PremiumPreviewFragment premiumPreviewFragment8 = PremiumPreviewFragment.this;
                premiumPreviewFragment8.particlesView.setAlpha(1.0f - premiumPreviewFragment8.totalProgress);
                StarParticlesView starParticlesView = PremiumPreviewFragment.this.particlesView;
                starParticlesView.setTranslationY((((float) (-(starParticlesView.getMeasuredHeight() - PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()))) / 2.0f) + PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY());
                float dp2 = (float) (AndroidUtilities.dp(72.0f) - PremiumPreviewFragment.this.backgroundView.titleView.getLeft());
                PremiumPreviewFragment premiumPreviewFragment9 = PremiumPreviewFragment.this;
                float f8 = premiumPreviewFragment9.totalProgress;
                if (f8 > 0.3f) {
                    f3 = (f8 - 0.3f) / 0.7f;
                }
                premiumPreviewFragment9.backgroundView.titleView.setTranslationX(dp2 * (1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(1.0f - f3)));
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
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view != PremiumPreviewFragment.this.listView) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                canvas.clipRect(0, PremiumPreviewFragment.this.actionBar.getBottom(), getMeasuredWidth(), getMeasuredHeight());
                super.drawChild(canvas, view, j);
                canvas.restore();
                return true;
            }
        };
        this.contentView = r4;
        r4.setFitsSystemWindows(true);
        AnonymousClass2 r42 = new RecyclerListView(context2) {
            public void onDraw(Canvas canvas) {
                Drawable drawable = PremiumPreviewFragment.this.shadowDrawable;
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                drawable.setBounds((int) (((float) (-rect.left)) - (((float) AndroidUtilities.dp(16.0f)) * premiumPreviewFragment.progressToFull)), (premiumPreviewFragment.currentYOffset - rect.top) - AndroidUtilities.dp(16.0f), (int) (((float) (getMeasuredWidth() + rect.right)) + (((float) AndroidUtilities.dp(16.0f)) * PremiumPreviewFragment.this.progressToFull)), getMeasuredHeight());
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
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 0) {
                    int bottom = PremiumPreviewFragment.this.actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                    PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                    if (premiumPreviewFragment.totalProgress > 0.5f) {
                        premiumPreviewFragment.listView.smoothScrollBy(0, premiumPreviewFragment.currentYOffset - bottom);
                    } else {
                        View view = null;
                        if (premiumPreviewFragment.listView.getLayoutManager() != null) {
                            view = PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0);
                        }
                        if (view != null && view.getTop() < 0) {
                            PremiumPreviewFragment.this.listView.smoothScrollBy(0, view.getTop());
                        }
                    }
                }
                PremiumPreviewFragment.this.checkButtonDivider();
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                PremiumPreviewFragment.this.contentView.invalidate();
                PremiumPreviewFragment.this.checkButtonDivider();
            }
        });
        this.backgroundView = new BackgroundView(this, context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        this.particlesView = new StarParticlesView(context2);
        this.backgroundView.imageView.setStarParticlesView(this.particlesView);
        this.contentView.addView(this.particlesView, LayoutHelper.createFrame(-1, -2.0f));
        this.contentView.addView(this.backgroundView, LayoutHelper.createFrame(-1, -2.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PremiumPreviewFragment$$ExternalSyntheticLambda18(this));
        this.contentView.addView(this.listView);
        this.premiumButtonView = new PremiumButtonView(context2, false);
        updateButtonText(false);
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PremiumPreviewFragment.this.finishFragment();
                }
            }
        });
        this.actionBar.setForceSkipTouches(true);
        updateColors();
        updateRows();
        this.backgroundView.imageView.startEnterAnimation(-180, 200);
        if (this.forcePremium) {
            AndroidUtilities.runOnUIThread(new PremiumPreviewFragment$$ExternalSyntheticLambda9(this), 400);
        }
        MediaDataController.getInstance(this.currentAccount).preloadPremiumPreviewStickers();
        sentShowScreenStat(this.source);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
        if (view instanceof PremiumFeatureCell) {
            PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) view;
            sentShowFeaturePreview(this.currentAccount, premiumFeatureCell.data.type);
            if (premiumFeatureCell.data.type == 0) {
                DoubledLimitsBottomSheet doubledLimitsBottomSheet = new DoubledLimitsBottomSheet(this, this.currentAccount, this.subscriptionTiers.get(this.selectedTierIndex));
                doubledLimitsBottomSheet.setParentFragment(this);
                showDialog(doubledLimitsBottomSheet);
                return;
            }
            showDialog(new PremiumFeatureBottomSheet(this, premiumFeatureCell.data.type, false, this.subscriptionTiers.get(this.selectedTierIndex)));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1() {
        getMediaDataController().loadPremiumPromo(false);
    }

    public static void buyPremium(BaseFragment baseFragment) {
        buyPremium(baseFragment, "settings");
    }

    public static void fillPremiumFeaturesList(ArrayList<PremiumFeatureData> arrayList, int i) {
        MessagesController instance = MessagesController.getInstance(i);
        int i2 = 0;
        arrayList.add(new PremiumFeatureData(0, R.drawable.msg_premium_limits, LocaleController.getString("PremiumPreviewLimits", R.string.PremiumPreviewLimits), LocaleController.formatString("PremiumPreviewLimitsDescription", R.string.PremiumPreviewLimitsDescription, Integer.valueOf(instance.channelsLimitPremium), Integer.valueOf(instance.dialogFiltersLimitPremium), Integer.valueOf(instance.dialogFiltersPinnedLimitPremium), Integer.valueOf(instance.publicLinksLimitPremium), 4)));
        arrayList.add(new PremiumFeatureData(1, R.drawable.msg_premium_uploads, LocaleController.getString("PremiumPreviewUploads", R.string.PremiumPreviewUploads), LocaleController.getString("PremiumPreviewUploadsDescription", R.string.PremiumPreviewUploadsDescription)));
        arrayList.add(new PremiumFeatureData(2, R.drawable.msg_premium_speed, LocaleController.getString("PremiumPreviewDownloadSpeed", R.string.PremiumPreviewDownloadSpeed), LocaleController.getString("PremiumPreviewDownloadSpeedDescription", R.string.PremiumPreviewDownloadSpeedDescription)));
        arrayList.add(new PremiumFeatureData(8, R.drawable.msg_premium_voice, LocaleController.getString("PremiumPreviewVoiceToText", R.string.PremiumPreviewVoiceToText), LocaleController.getString("PremiumPreviewVoiceToTextDescription", R.string.PremiumPreviewVoiceToTextDescription)));
        arrayList.add(new PremiumFeatureData(3, R.drawable.msg_premium_ads, LocaleController.getString("PremiumPreviewNoAds", R.string.PremiumPreviewNoAds), LocaleController.getString("PremiumPreviewNoAdsDescription", R.string.PremiumPreviewNoAdsDescription)));
        arrayList.add(new PremiumFeatureData(4, R.drawable.msg_premium_reactions, LocaleController.getString("PremiumPreviewReactions2", R.string.PremiumPreviewReactions2), LocaleController.getString("PremiumPreviewReactions2Description", R.string.PremiumPreviewReactions2Description)));
        arrayList.add(new PremiumFeatureData(5, R.drawable.msg_premium_stickers, LocaleController.getString("PremiumPreviewStickers", R.string.PremiumPreviewStickers), LocaleController.getString("PremiumPreviewStickersDescription", R.string.PremiumPreviewStickersDescription)));
        arrayList.add(new PremiumFeatureData(11, R.drawable.msg_premium_emoji, LocaleController.getString("PremiumPreviewEmoji", R.string.PremiumPreviewEmoji), LocaleController.getString("PremiumPreviewEmojiDescription", R.string.PremiumPreviewEmojiDescription)));
        arrayList.add(new PremiumFeatureData(9, R.drawable.msg_premium_tools, LocaleController.getString("PremiumPreviewAdvancedChatManagement", R.string.PremiumPreviewAdvancedChatManagement), LocaleController.getString("PremiumPreviewAdvancedChatManagementDescription", R.string.PremiumPreviewAdvancedChatManagementDescription)));
        arrayList.add(new PremiumFeatureData(6, R.drawable.msg_premium_badge, LocaleController.getString("PremiumPreviewProfileBadge", R.string.PremiumPreviewProfileBadge), LocaleController.getString("PremiumPreviewProfileBadgeDescription", R.string.PremiumPreviewProfileBadgeDescription)));
        arrayList.add(new PremiumFeatureData(7, R.drawable.msg_premium_avatar, LocaleController.getString("PremiumPreviewAnimatedProfiles", R.string.PremiumPreviewAnimatedProfiles), LocaleController.getString("PremiumPreviewAnimatedProfilesDescription", R.string.PremiumPreviewAnimatedProfilesDescription)));
        int i3 = R.drawable.msg_premium_icons;
        arrayList.add(new PremiumFeatureData(10, i3, LocaleController.getString("PremiumPreviewAppIcon", R.string.PremiumPreviewAppIcon), LocaleController.getString("PremiumPreviewAppIconDescription", R.string.PremiumPreviewAppIconDescription)));
        arrayList.add(new PremiumFeatureData(12, i3, LocaleController.getString("PremiumPreviewEmojiStatus", R.string.PremiumPreviewEmojiStatus), LocaleController.getString("PremiumPreviewEmojiStatusDescription", R.string.PremiumPreviewEmojiStatusDescription)));
        if (instance.premiumFeaturesTypesToPosition.size() > 0) {
            while (i2 < arrayList.size()) {
                if (instance.premiumFeaturesTypesToPosition.get(arrayList.get(i2).type, -1) == -1) {
                    arrayList.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
        Collections.sort(arrayList, new PremiumPreviewFragment$$ExternalSyntheticLambda10(instance));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$fillPremiumFeaturesList$2(MessagesController messagesController, PremiumFeatureData premiumFeatureData, PremiumFeatureData premiumFeatureData2) {
        return messagesController.premiumFeaturesTypesToPosition.get(premiumFeatureData.type, Integer.MAX_VALUE) - messagesController.premiumFeaturesTypesToPosition.get(premiumFeatureData2.type, Integer.MAX_VALUE);
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

    public static void buyPremium(BaseFragment baseFragment, String str) {
        buyPremium(baseFragment, (SubscriptionTier) null, str);
    }

    public static void buyPremium(BaseFragment baseFragment, SubscriptionTier subscriptionTier, String str) {
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            baseFragment.showDialog(new PremiumNotAvailableBottomSheet(baseFragment));
            return;
        }
        if (subscriptionTier == null) {
            Iterator<TLRPC$TL_premiumSubscriptionOption> it = baseFragment.getAccountInstance().getMediaDataController().getPremiumPromo().period_options.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TLRPC$TL_premiumSubscriptionOption next = it.next();
                if (next.months == 1) {
                    subscriptionTier = new SubscriptionTier(next);
                    break;
                }
            }
        }
        sentPremiumButtonClick();
        if (BuildVars.useInvoiceBilling()) {
            Activity parentActivity = baseFragment.getParentActivity();
            if (parentActivity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) parentActivity;
                String str2 = subscriptionTier.subscriptionOption.bot_url;
                if (str2 != null) {
                    Uri parse = Uri.parse(str2);
                    if (parse.getHost().equals("t.me") && !parse.getPath().startsWith("/$") && !parse.getPath().startsWith("/invoice/")) {
                        launchActivity.setNavigateToPremiumBot(true);
                    }
                    Browser.openUrl((Context) launchActivity, subscriptionTier.subscriptionOption.bot_url);
                    return;
                } else if (!TextUtils.isEmpty(baseFragment.getMessagesController().premiumBotUsername)) {
                    launchActivity.setNavigateToPremiumBot(true);
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/" + baseFragment.getMessagesController().premiumBotUsername + "?start=" + str)));
                    return;
                } else if (!TextUtils.isEmpty(baseFragment.getMessagesController().premiumInvoiceSlug)) {
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/$" + baseFragment.getMessagesController().premiumInvoiceSlug)));
                    return;
                } else {
                    return;
                }
            }
        }
        ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
        if (productDetails != null && !productDetails.getSubscriptionOfferDetails().isEmpty()) {
            if (subscriptionTier.getGooglePlayProductDetails() == null) {
                subscriptionTier.setGooglePlayProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS);
            }
            if (subscriptionTier.getOfferDetails() != null) {
                BillingController.getInstance().queryPurchases("subs", new PremiumPreviewFragment$$ExternalSyntheticLambda4(baseFragment, subscriptionTier));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$buyPremium$9(BillingResult billingResult, BaseFragment baseFragment, List list, SubscriptionTier subscriptionTier) {
        if (billingResult.getResponseCode() == 0) {
            PremiumPreviewFragment$$ExternalSyntheticLambda7 premiumPreviewFragment$$ExternalSyntheticLambda7 = new PremiumPreviewFragment$$ExternalSyntheticLambda7(baseFragment);
            if (list != null && !list.isEmpty()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    Purchase purchase = (Purchase) it.next();
                    if (purchase.getProducts().contains("telegram_premium")) {
                        TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction = new TLRPC$TL_payments_assignPlayMarketTransaction();
                        TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                        tLRPC$TL_payments_assignPlayMarketTransaction.receipt = tLRPC$TL_dataJSON;
                        tLRPC$TL_dataJSON.data = purchase.getOriginalJson();
                        TLRPC$TL_inputStorePaymentPremiumSubscription tLRPC$TL_inputStorePaymentPremiumSubscription = new TLRPC$TL_inputStorePaymentPremiumSubscription();
                        tLRPC$TL_inputStorePaymentPremiumSubscription.restore = true;
                        tLRPC$TL_payments_assignPlayMarketTransaction.purpose = tLRPC$TL_inputStorePaymentPremiumSubscription;
                        baseFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_assignPlayMarketTransaction, new PremiumPreviewFragment$$ExternalSyntheticLambda11(baseFragment, premiumPreviewFragment$$ExternalSyntheticLambda7, tLRPC$TL_payments_assignPlayMarketTransaction), 66);
                        return;
                    }
                }
            }
            BillingController.getInstance().addResultListener("telegram_premium", new PremiumPreviewFragment$$ExternalSyntheticLambda3(premiumPreviewFragment$$ExternalSyntheticLambda7));
            TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium = new TLRPC$TL_payments_canPurchasePremium();
            tLRPC$TL_payments_canPurchasePremium.purpose = new TLRPC$TL_inputStorePaymentPremiumSubscription();
            baseFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_canPurchasePremium, new PremiumPreviewFragment$$ExternalSyntheticLambda12(baseFragment, subscriptionTier, tLRPC$TL_payments_canPurchasePremium));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$buyPremium$3(BaseFragment baseFragment) {
        if (baseFragment instanceof PremiumPreviewFragment) {
            PremiumPreviewFragment premiumPreviewFragment = (PremiumPreviewFragment) baseFragment;
            premiumPreviewFragment.setForcePremium();
            premiumPreviewFragment.getMediaDataController().loadPremiumPromo(false);
            premiumPreviewFragment.listView.smoothScrollToPosition(0);
        } else {
            baseFragment.presentFragment(new PremiumPreviewFragment((String) null).setForcePremium());
        }
        if (baseFragment.getParentActivity() instanceof LaunchActivity) {
            try {
                baseFragment.getFragmentView().performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            ((LaunchActivity) baseFragment.getParentActivity()).getFireworksOverlay().start();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$buyPremium$5(BaseFragment baseFragment, Runnable runnable, TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$Updates) {
            baseFragment.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            runnable.run();
        } else if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new PremiumPreviewFragment$$ExternalSyntheticLambda8(baseFragment, tLRPC$TL_error, tLRPC$TL_payments_assignPlayMarketTransaction));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$buyPremium$6(Runnable runnable, BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$buyPremium$7(TLObject tLObject, BaseFragment baseFragment, SubscriptionTier subscriptionTier, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            BillingController.getInstance().launchBillingFlow(baseFragment.getParentActivity(), baseFragment.getAccountInstance(), new TLRPC$TL_inputStorePaymentPremiumSubscription(), Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS).setOfferToken(subscriptionTier.getOfferDetails().getOfferToken()).build()));
        } else {
            AlertsCreator.processError(baseFragment.getCurrentAccount(), tLRPC$TL_error, baseFragment, tLRPC$TL_payments_canPurchasePremium, new Object[0]);
        }
    }

    public static String getPremiumButtonText(int i, SubscriptionTier subscriptionTier) {
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            return LocaleController.getString(R.string.SubscribeToPremiumNotAvailable);
        }
        if (subscriptionTier == null) {
            if (BuildVars.useInvoiceBilling()) {
                TLRPC$TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(i).getPremiumPromo();
                if (premiumPromo == null) {
                    return LocaleController.getString(R.string.SubscribeToPremiumNoPrice);
                }
                long j = 0;
                Iterator<TLRPC$TL_premiumSubscriptionOption> it = premiumPromo.period_options.iterator();
                String str = "USD";
                while (it.hasNext()) {
                    TLRPC$TL_premiumSubscriptionOption next = it.next();
                    if (next.months == 1) {
                        j = next.amount;
                        str = next.currency;
                    }
                }
                return LocaleController.formatString(R.string.SubscribeToPremium, BillingController.getInstance().formatCurrency(j, str));
            }
            String str2 = null;
            ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
            if (productDetails != null) {
                List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails = productDetails.getSubscriptionOfferDetails();
                if (!subscriptionOfferDetails.isEmpty()) {
                    Iterator<ProductDetails.PricingPhase> it2 = subscriptionOfferDetails.get(0).getPricingPhases().getPricingPhaseList().iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        ProductDetails.PricingPhase next2 = it2.next();
                        if (next2.getBillingPeriod().equals("P1M")) {
                            str2 = next2.getFormattedPrice();
                            break;
                        }
                    }
                }
            }
            if (str2 == null) {
                return LocaleController.getString(R.string.Loading);
            }
            return LocaleController.formatString(R.string.SubscribeToPremium, str2);
        } else if (!BuildVars.useInvoiceBilling() && subscriptionTier.getOfferDetails() == null) {
            return LocaleController.getString(R.string.Loading);
        } else {
            return LocaleController.formatString(R.string.SubscribeToPremium, subscriptionTier.getFormattedPricePerMonth());
        }
    }

    /* access modifiers changed from: private */
    public void measureGradient(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < this.premiumFeatures.size(); i4++) {
            this.dummyCell.setData(this.premiumFeatures.get(i4), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.premiumFeatures.get(i4).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    private void updateRows() {
        int i = 0;
        this.rowCount = 0;
        this.sectionRow = -1;
        this.privacyRow = -1;
        int i2 = 0 + 1;
        this.rowCount = i2;
        this.paddingRow = 0;
        this.featuresStartRow = i2;
        int size = i2 + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        int i3 = size + 1;
        this.rowCount = i3;
        this.statusRow = size;
        this.rowCount = i3 + 1;
        this.lastPaddingRow = i3;
        if (getUserConfig().isPremium() || this.forcePremium) {
            this.buttonContainer.setVisibility(8);
        } else {
            this.buttonContainer.setVisibility(0);
        }
        if (this.buttonContainer.getVisibility() == 0) {
            i = AndroidUtilities.dp(64.0f);
        }
        this.layoutManager.setAdditionalHeight((this.statusBarHeight + i) - AndroidUtilities.dp(16.0f));
        this.layoutManager.setMinimumLastViewHeight(i);
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

    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.billingProductDetailsUpdated || i == NotificationCenter.premiumPromoUpdated) {
            updateButtonText(false);
        }
        if (i == NotificationCenter.currentUserPremiumStatusChanged || i == NotificationCenter.premiumPromoUpdated) {
            this.backgroundView.updateText();
            this.backgroundView.updatePremiumTiers();
            updateRows();
            this.listView.getAdapter().notifyDataSetChanged();
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            Context context = viewGroup.getContext();
            if (i == 1) {
                view = new PremiumFeatureCell(context) {
                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set((float) this.imageView.getLeft(), (float) this.imageView.getTop(), (float) this.imageView.getRight(), (float) this.imageView.getBottom());
                        PremiumPreviewFragment.this.matrix.reset();
                        PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                        premiumPreviewFragment.matrix.postScale(1.0f, ((float) premiumPreviewFragment.totalGradientHeight) / 100.0f, 0.0f, 0.0f);
                        PremiumPreviewFragment.this.matrix.postTranslate(0.0f, (float) (-this.data.yOffset));
                        PremiumPreviewFragment premiumPreviewFragment2 = PremiumPreviewFragment.this;
                        premiumPreviewFragment2.shader.setLocalMatrix(premiumPreviewFragment2.matrix);
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), PremiumPreviewFragment.this.gradientPaint);
                        super.dispatchDraw(canvas);
                    }
                };
            } else if (i == 2) {
                view = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.getColor("windowBackgroundGrayShadow")), 0, 0);
                combinedDrawable.setFullsize(true);
                view.setBackgroundDrawable(combinedDrawable);
            } else if (i == 4) {
                view = new AboutPremiumView(context);
            } else if (i == 5) {
                view = new TextInfoPrivacyCell(context);
            } else if (i != 6) {
                view = new View(context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                        if (premiumPreviewFragment.isLandscapeMode) {
                            int unused = premiumPreviewFragment.firstViewHeight = (premiumPreviewFragment.statusBarHeight + PremiumPreviewFragment.this.actionBar.getMeasuredHeight()) - AndroidUtilities.dp(16.0f);
                        } else {
                            int dp = AndroidUtilities.dp(300.0f) + PremiumPreviewFragment.this.statusBarHeight;
                            if (PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f) > dp) {
                                dp = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                            }
                            int unused2 = PremiumPreviewFragment.this.firstViewHeight = dp;
                        }
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(PremiumPreviewFragment.this.firstViewHeight, NUM));
                    }
                };
            } else {
                view = new View(context);
                view.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$TL_help_premiumPromo premiumPromo;
            String str;
            int i2;
            boolean z;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i3 = i;
            PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
            int i4 = premiumPreviewFragment.featuresStartRow;
            boolean z2 = true;
            int i5 = 0;
            if (i3 >= i4 && i3 < premiumPreviewFragment.featuresEndRow) {
                PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) viewHolder2.itemView;
                PremiumFeatureData premiumFeatureData = premiumPreviewFragment.premiumFeatures.get(i3 - i4);
                if (i3 == PremiumPreviewFragment.this.featuresEndRow - 1) {
                    z2 = false;
                }
                premiumFeatureCell.setData(premiumFeatureData, z2);
            } else if (i3 == premiumPreviewFragment.statusRow || i3 == premiumPreviewFragment.privacyRow) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), R.drawable.greydivider, Theme.getColor("windowBackgroundGrayShadow")), 0, 0);
                combinedDrawable.setFullsize(true);
                textInfoPrivacyCell.setBackground(combinedDrawable);
                PremiumPreviewFragment premiumPreviewFragment2 = PremiumPreviewFragment.this;
                if (i3 == premiumPreviewFragment2.statusRow && (premiumPromo = premiumPreviewFragment2.getMediaDataController().getPremiumPromo()) != null) {
                    SpannableString spannableString = new SpannableString(premiumPromo.status_text);
                    MediaDataController.addTextStyleRuns(premiumPromo.status_entities, (CharSequence) premiumPromo.status_text, (Spannable) spannableString);
                    TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) spannableString.getSpans(0, spannableString.length(), TextStyleSpan.class);
                    int length = textStyleSpanArr.length;
                    int i6 = 0;
                    while (i6 < length) {
                        TextStyleSpan.TextStyleRun textStyleRun = textStyleSpanArr[i6].getTextStyleRun();
                        TLRPC$MessageEntity tLRPC$MessageEntity = textStyleRun.urlEntity;
                        if (tLRPC$MessageEntity != null) {
                            String str2 = premiumPromo.status_text;
                            int i7 = tLRPC$MessageEntity.offset;
                            str = TextUtils.substring(str2, i7, tLRPC$MessageEntity.length + i7);
                        } else {
                            str = null;
                        }
                        TLRPC$MessageEntity tLRPC$MessageEntity2 = textStyleRun.urlEntity;
                        if (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityBotCommand) {
                            spannableString.setSpan(new URLSpanBotCommand(str, i5, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                        } else if ((tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityHashtag) || (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityMention) || (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityCashtag)) {
                            i2 = 33;
                            spannableString.setSpan(new URLSpanNoUnderline(str, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                            z = false;
                            if (!z && (textStyleRun.flags & 256) != 0) {
                                spannableString.setSpan(new TextStyleSpan(textStyleRun), textStyleRun.start, textStyleRun.end, i2);
                            }
                            i6++;
                            z2 = true;
                            i5 = 0;
                        } else if (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityEmail) {
                            spannableString.setSpan(new URLSpanReplacement("mailto:" + str, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                        } else if (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityUrl) {
                            if (!str.toLowerCase().contains("://")) {
                                spannableString.setSpan(new URLSpanBrowser("http://" + str, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                            } else {
                                spannableString.setSpan(new URLSpanBrowser(str, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                            }
                        } else if (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityBankCard) {
                            spannableString.setSpan(new URLSpanNoUnderline("card:" + str, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                        } else if (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityPhone) {
                            String stripExceptNumbers = PhoneFormat.stripExceptNumbers(str);
                            if (str.startsWith("+")) {
                                stripExceptNumbers = "+" + stripExceptNumbers;
                            }
                            spannableString.setSpan(new URLSpanBrowser("tel:" + stripExceptNumbers, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                        } else if (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityTextUrl) {
                            URLSpanReplacement uRLSpanReplacement = new URLSpanReplacement(textStyleRun.urlEntity.url, textStyleRun);
                            uRLSpanReplacement.setNavigateToPremiumBot(z2);
                            spannableString.setSpan(uRLSpanReplacement, textStyleRun.start, textStyleRun.end, 33);
                        } else if (tLRPC$MessageEntity2 instanceof TLRPC$TL_messageEntityMentionName) {
                            spannableString.setSpan(new URLSpanUserMention("" + ((TLRPC$TL_messageEntityMentionName) textStyleRun.urlEntity).user_id, i5, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                        } else if (tLRPC$MessageEntity2 instanceof TLRPC$TL_inputMessageEntityMentionName) {
                            spannableString.setSpan(new URLSpanUserMention("" + ((TLRPC$TL_inputMessageEntityMentionName) textStyleRun.urlEntity).user_id.user_id, i5, textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                        } else if ((textStyleRun.flags & 4) != 0) {
                            URLSpanMono uRLSpanMono = r7;
                            i2 = 33;
                            URLSpanMono uRLSpanMono2 = new URLSpanMono(spannableString, textStyleRun.start, textStyleRun.end, (byte) 0, textStyleRun);
                            spannableString.setSpan(uRLSpanMono, textStyleRun.start, textStyleRun.end, 33);
                            z = false;
                            spannableString.setSpan(new TextStyleSpan(textStyleRun), textStyleRun.start, textStyleRun.end, i2);
                            i6++;
                            z2 = true;
                            i5 = 0;
                        } else {
                            i2 = 33;
                            spannableString.setSpan(new TextStyleSpan(textStyleRun), textStyleRun.start, textStyleRun.end, 33);
                            z = true;
                            spannableString.setSpan(new TextStyleSpan(textStyleRun), textStyleRun.start, textStyleRun.end, i2);
                            i6++;
                            z2 = true;
                            i5 = 0;
                        }
                        i2 = 33;
                        z = false;
                        spannableString.setSpan(new TextStyleSpan(textStyleRun), textStyleRun.start, textStyleRun.end, i2);
                        i6++;
                        z2 = true;
                        i5 = 0;
                    }
                    textInfoPrivacyCell.setText(spannableString);
                }
            }
        }

        public int getItemCount() {
            return PremiumPreviewFragment.this.rowCount;
        }

        public int getItemViewType(int i) {
            PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
            if (i == premiumPreviewFragment.paddingRow) {
                return 0;
            }
            if (i >= premiumPreviewFragment.featuresStartRow && i < premiumPreviewFragment.featuresEndRow) {
                return 1;
            }
            if (i == premiumPreviewFragment.sectionRow) {
                return 2;
            }
            if (i == premiumPreviewFragment.helpUsRow) {
                return 4;
            }
            if (i == premiumPreviewFragment.statusRow || i == premiumPreviewFragment.privacyRow) {
                return 5;
            }
            if (i == premiumPreviewFragment.lastPaddingRow) {
                return 6;
            }
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }
    }

    public static class PremiumFeatureData {
        public final String description;
        public final int icon;
        public final String title;
        public final int type;
        public int yOffset;

        public PremiumFeatureData(int i, int i2, String str, String str2) {
            this.type = i;
            this.icon = i2;
            this.title = str;
            this.description = str2;
        }
    }

    private class BackgroundView extends LinearLayout {
        /* access modifiers changed from: private */
        public final GLIconTextureView imageView;
        /* access modifiers changed from: private */
        public final TextView subtitleView;
        /* access modifiers changed from: private */
        public RecyclerListView tierListView;
        TextView titleView;

        public BackgroundView(Context context) {
            super(context);
            setOrientation(1);
            AnonymousClass1 r1 = new GLIconTextureView(context, 0, PremiumPreviewFragment.this, context) {
                final /* synthetic */ Context val$context;

                {
                    this.val$context = r5;
                }

                public void onLongPress() {
                    super.onLongPress();
                    PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                    if (premiumPreviewFragment.settingsView == null || BuildVars.DEBUG_PRIVATE_VERSION) {
                        premiumPreviewFragment.settingsView = new FrameLayout(this.val$context);
                        ScrollView scrollView = new ScrollView(this.val$context);
                        scrollView.addView(new GLIconSettingsView(this.val$context, BackgroundView.this.imageView.mRenderer));
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
            AnonymousClass2 r0 = new RecyclerListView(context, PremiumPreviewFragment.this) {
                Paint paint;

                {
                    Paint paint2 = new Paint(1);
                    this.paint = paint2;
                    paint2.setColor(Theme.getColor("dialogBackground"));
                }

                public void draw(Canvas canvas) {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                    canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), this.paint);
                    super.draw(canvas);
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int i, int i2, int i3, int i4) {
                    super.onSizeChanged(i, i2, i3, i4);
                    BackgroundView.this.measureGradient(i, i2);
                }
            };
            this.tierListView = r0;
            r0.setOverScrollMode(2);
            this.tierListView.setLayoutManager(new LinearLayoutManager(context));
            this.tierListView.setAdapter(new RecyclerListView.SelectionAdapter(PremiumPreviewFragment.this, context) {
                final /* synthetic */ Context val$context;

                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return true;
                }

                {
                    this.val$context = r3;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    AnonymousClass1 r1 = new PremiumTierCell(this.val$context) {
                        /* access modifiers changed from: protected */
                        public void dispatchDraw(Canvas canvas) {
                            if (this.discountView.getVisibility() == 0) {
                                RectF rectF = AndroidUtilities.rectTmp;
                                rectF.set((float) this.discountView.getLeft(), (float) this.discountView.getTop(), (float) this.discountView.getRight(), (float) this.discountView.getBottom());
                                PremiumPreviewFragment.this.tiersGradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewFragment.this.totalTiersGradientHeight, 0.0f, (float) (-this.tier.yOffset));
                                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), PremiumPreviewFragment.this.tiersGradientTools.paint);
                            }
                            super.dispatchDraw(canvas);
                        }
                    };
                    r1.setCirclePaintProvider(new PremiumPreviewFragment$BackgroundView$3$$ExternalSyntheticLambda0(this, r1));
                    return new RecyclerListView.Holder(r1);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ Paint lambda$onCreateViewHolder$0(PremiumTierCell premiumTierCell, Void voidR) {
                    PremiumPreviewFragment.this.tiersGradientTools.gradientMatrix(0, 0, premiumTierCell.getMeasuredWidth(), PremiumPreviewFragment.this.totalTiersGradientHeight, 0.0f, (float) (-premiumTierCell.getTier().yOffset));
                    return PremiumPreviewFragment.this.tiersGradientTools.paint;
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    PremiumTierCell premiumTierCell = (PremiumTierCell) viewHolder.itemView;
                    boolean z = true;
                    premiumTierCell.bind(PremiumPreviewFragment.this.subscriptionTiers.get(i), i != getItemCount() - 1);
                    if (PremiumPreviewFragment.this.selectedTierIndex != i) {
                        z = false;
                    }
                    premiumTierCell.setChecked(z, false);
                }

                public int getItemCount() {
                    return PremiumPreviewFragment.this.subscriptionTiers.size();
                }
            });
            this.tierListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PremiumPreviewFragment$BackgroundView$$ExternalSyntheticLambda1(this));
            this.tierListView.setSelectorTransformer(new PremiumPreviewFragment$BackgroundView$$ExternalSyntheticLambda0(this, new Path(), new float[8]));
            addView(this.tierListView, LayoutHelper.createLinear(-1, -2, 12.0f, 16.0f, 12.0f, 0.0f));
            updateText();
            updatePremiumTiers();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view, int i) {
            if (view instanceof PremiumTierCell) {
                PremiumTierCell premiumTierCell = (PremiumTierCell) view;
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                premiumPreviewFragment.selectedTierIndex = premiumPreviewFragment.subscriptionTiers.indexOf(premiumTierCell.getTier());
                PremiumPreviewFragment.this.updateButtonText(true);
                premiumTierCell.setChecked(true, true);
                for (int i2 = 0; i2 < this.tierListView.getChildCount(); i2++) {
                    View childAt = this.tierListView.getChildAt(i2);
                    if (childAt instanceof PremiumTierCell) {
                        PremiumTierCell premiumTierCell2 = (PremiumTierCell) childAt;
                        if (premiumTierCell2.getTier() != premiumTierCell.getTier()) {
                            premiumTierCell2.setChecked(false, true);
                        }
                    }
                }
                for (int i3 = 0; i3 < this.tierListView.getHiddenChildCount(); i3++) {
                    View hiddenChildAt = this.tierListView.getHiddenChildAt(i3);
                    if (hiddenChildAt instanceof PremiumTierCell) {
                        PremiumTierCell premiumTierCell3 = (PremiumTierCell) hiddenChildAt;
                        if (premiumTierCell3.getTier() != premiumTierCell.getTier()) {
                            premiumTierCell3.setChecked(false, true);
                        }
                    }
                }
                for (int i4 = 0; i4 < this.tierListView.getCachedChildCount(); i4++) {
                    View cachedChildAt = this.tierListView.getCachedChildAt(i4);
                    if (cachedChildAt instanceof PremiumTierCell) {
                        PremiumTierCell premiumTierCell4 = (PremiumTierCell) cachedChildAt;
                        if (premiumTierCell4.getTier() != premiumTierCell.getTier()) {
                            premiumTierCell4.setChecked(false, true);
                        }
                    }
                }
                for (int i5 = 0; i5 < this.tierListView.getAttachedScrapChildCount(); i5++) {
                    View attachedScrapChildAt = this.tierListView.getAttachedScrapChildAt(i5);
                    if (attachedScrapChildAt instanceof PremiumTierCell) {
                        PremiumTierCell premiumTierCell5 = (PremiumTierCell) attachedScrapChildAt;
                        if (premiumTierCell5.getTier() != premiumTierCell.getTier()) {
                            premiumTierCell5.setChecked(false, true);
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(Path path, float[] fArr, Canvas canvas) {
            int i;
            View pressedChildView = this.tierListView.getPressedChildView();
            if (pressedChildView == null) {
                i = -1;
            } else {
                i = this.tierListView.getChildViewHolder(pressedChildView).getAdapterPosition();
            }
            path.rewind();
            Rect selectorRect = this.tierListView.getSelectorRect();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set((float) selectorRect.left, (float) selectorRect.top, (float) selectorRect.right, (float) selectorRect.bottom);
            Arrays.fill(fArr, 0.0f);
            if (i == 0) {
                Arrays.fill(fArr, 0, 4, (float) AndroidUtilities.dp(12.0f));
            }
            if (i == this.tierListView.getAdapter().getItemCount() - 1) {
                Arrays.fill(fArr, 4, 8, (float) AndroidUtilities.dp(12.0f));
            }
            path.addRoundRect(rectF, fArr, Path.Direction.CW);
            canvas.clipPath(path);
        }

        /* access modifiers changed from: private */
        public void measureGradient(int i, int i2) {
            int i3 = 0;
            for (int i4 = 0; i4 < PremiumPreviewFragment.this.subscriptionTiers.size(); i4++) {
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                premiumPreviewFragment.dummyTierCell.bind(premiumPreviewFragment.subscriptionTiers.get(i4), false);
                PremiumPreviewFragment.this.dummyTierCell.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
                PremiumPreviewFragment.this.subscriptionTiers.get(i4).yOffset = i3;
                i3 += PremiumPreviewFragment.this.dummyTierCell.getMeasuredHeight();
            }
            PremiumPreviewFragment.this.totalTiersGradientHeight = i3;
        }

        @SuppressLint({"NotifyDataSetChanged"})
        public void updatePremiumTiers() {
            long j;
            PremiumPreviewFragment.this.subscriptionTiers.clear();
            long j2 = 0;
            if (PremiumPreviewFragment.this.getMediaDataController().getPremiumPromo() != null) {
                Iterator<TLRPC$TL_premiumSubscriptionOption> it = PremiumPreviewFragment.this.getMediaDataController().getPremiumPromo().period_options.iterator();
                j = 0;
                while (it.hasNext()) {
                    SubscriptionTier subscriptionTier = new SubscriptionTier(it.next());
                    PremiumPreviewFragment.this.subscriptionTiers.add(subscriptionTier);
                    if (BuildVars.useInvoiceBilling() && subscriptionTier.getPricePerYear() > j) {
                        j = subscriptionTier.getPricePerYear();
                    }
                }
            } else {
                j = 0;
            }
            if (BuildVars.useInvoiceBilling()) {
                Iterator<SubscriptionTier> it2 = PremiumPreviewFragment.this.subscriptionTiers.iterator();
                while (it2.hasNext()) {
                    it2.next().setPricePerYearRegular(j);
                }
            } else if (BillingController.getInstance().isReady() && BillingController.PREMIUM_PRODUCT_DETAILS != null) {
                Iterator<SubscriptionTier> it3 = PremiumPreviewFragment.this.subscriptionTiers.iterator();
                while (it3.hasNext()) {
                    SubscriptionTier next = it3.next();
                    next.setGooglePlayProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS);
                    if (next.getPricePerYear() > j2) {
                        j2 = next.getPricePerYear();
                    }
                }
                Iterator<SubscriptionTier> it4 = PremiumPreviewFragment.this.subscriptionTiers.iterator();
                while (it4.hasNext()) {
                    it4.next().setPricePerYearRegular(j2);
                }
            }
            int i = 0;
            while (true) {
                if (i >= PremiumPreviewFragment.this.subscriptionTiers.size()) {
                    break;
                } else if (PremiumPreviewFragment.this.subscriptionTiers.get(i).getMonths() == 1) {
                    PremiumPreviewFragment.this.selectedTierIndex = i;
                    break;
                } else {
                    i++;
                }
            }
            PremiumPreviewFragment.this.updateButtonText(false);
            this.tierListView.getAdapter().notifyDataSetChanged();
        }

        public void updateText() {
            this.titleView.setText(LocaleController.getString(PremiumPreviewFragment.this.forcePremium ? R.string.TelegramPremiumSubscribedTitle : R.string.TelegramPremium));
            this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString((PremiumPreviewFragment.this.getUserConfig().isPremium() || PremiumPreviewFragment.this.forcePremium) ? R.string.TelegramPremiumSubscribedSubtitle : R.string.TelegramPremiumSubtitle)));
            RecyclerListView recyclerListView = this.tierListView;
            if (!PremiumPreviewFragment.this.getUserConfig().isPremium() && !PremiumPreviewFragment.this.forcePremium) {
                boolean z = BuildVars.IS_BILLING_UNAVAILABLE;
            }
            recyclerListView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void updateButtonText(boolean z) {
        PremiumButtonView premiumButtonView2 = this.premiumButtonView;
        if (premiumButtonView2 != null) {
            if (BuildVars.IS_BILLING_UNAVAILABLE) {
                premiumButtonView2.setButton(getPremiumButtonText(this.currentAccount, this.subscriptionTiers.get(this.selectedTierIndex)), new PremiumPreviewFragment$$ExternalSyntheticLambda0(this), z);
            } else if (BuildVars.useInvoiceBilling() || (BillingController.getInstance().isReady() && this.subscriptionTiers.get(this.selectedTierIndex).googlePlayProductDetails != null)) {
                this.premiumButtonView.setButton(getPremiumButtonText(this.currentAccount, this.subscriptionTiers.get(this.selectedTierIndex)), new PremiumPreviewFragment$$ExternalSyntheticLambda1(this), z);
                this.premiumButtonView.setFlickerDisabled(false);
            } else {
                this.premiumButtonView.setButton(LocaleController.getString(R.string.Loading), PremiumPreviewFragment$$ExternalSyntheticLambda2.INSTANCE, z);
                this.premiumButtonView.setFlickerDisabled(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButtonText$11(View view) {
        buyPremium(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButtonText$13(View view) {
        buyPremium(this, this.subscriptionTiers.get(this.selectedTierIndex), "settings");
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
        return SimpleThemeDescription.createThemeDescriptions(new PremiumPreviewFragment$$ExternalSyntheticLambda17(this), "premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4", "premiumGradientBackground1", "premiumGradientBackground2", "premiumGradientBackground3", "premiumGradientBackground4", "premiumGradientBackgroundOverlay", "premiumStarGradient1", "premiumStarGradient2", "premiumStartSmallStarsColor", "premiumStartSmallStarsColor2");
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        ActionBar actionBar;
        if (this.backgroundView != null && (actionBar = this.actionBar) != null) {
            actionBar.setItemsColor(Theme.getColor("premiumGradientBackgroundOverlay"), false);
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
            public void onAnimationEnd(Animator animator) {
                PremiumPreviewFragment.this.contentView.removeView(PremiumPreviewFragment.this.settingsView);
                PremiumPreviewFragment.this.settingsView = null;
                super.onAnimationEnd(animator);
            }
        });
    }

    public Dialog showDialog(Dialog dialog) {
        Dialog showDialog = super.showDialog(dialog);
        updateDialogVisibility(showDialog != null);
        return showDialog;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        updateDialogVisibility(false);
    }

    private void updateDialogVisibility(boolean z) {
        if (z != this.isDialogVisible) {
            this.isDialogVisible = z;
            this.backgroundView.imageView.setDialogVisible(z);
            this.particlesView.setPaused(z);
            this.contentView.invalidate();
        }
    }

    public static void sentShowScreenStat(String str) {
        ConnectionsManager instance = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
        tLRPC$TL_inputAppEvent.time = (double) instance.getCurrentTime();
        tLRPC$TL_inputAppEvent.type = "premium.promo_screen_show";
        TLRPC$TL_jsonObject tLRPC$TL_jsonObject = new TLRPC$TL_jsonObject();
        tLRPC$TL_inputAppEvent.data = tLRPC$TL_jsonObject;
        TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue = new TLRPC$TL_jsonObjectValue();
        TLRPC$TL_jsonString tLRPC$TL_jsonString = new TLRPC$TL_jsonString();
        tLRPC$TL_jsonString.value = str;
        tLRPC$TL_jsonObjectValue.key = "source";
        tLRPC$TL_jsonObjectValue.value = tLRPC$TL_jsonString;
        tLRPC$TL_jsonObject.value.add(tLRPC$TL_jsonObjectValue);
        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
        instance.sendRequest(tLRPC$TL_help_saveAppLog, PremiumPreviewFragment$$ExternalSyntheticLambda16.INSTANCE);
    }

    public static void sentPremiumButtonClick() {
        TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
        tLRPC$TL_inputAppEvent.time = (double) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        tLRPC$TL_inputAppEvent.type = "premium.promo_screen_accept";
        tLRPC$TL_inputAppEvent.data = new TLRPC$TL_jsonNull();
        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_help_saveAppLog, PremiumPreviewFragment$$ExternalSyntheticLambda13.INSTANCE);
    }

    public static void sentPremiumBuyCanceled() {
        TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
        tLRPC$TL_inputAppEvent.time = (double) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        tLRPC$TL_inputAppEvent.type = "premium.promo_screen_fail";
        tLRPC$TL_inputAppEvent.data = new TLRPC$TL_jsonNull();
        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_help_saveAppLog, PremiumPreviewFragment$$ExternalSyntheticLambda15.INSTANCE);
    }

    public static void sentShowFeaturePreview(int i, int i2) {
        TLRPC$TL_help_saveAppLog tLRPC$TL_help_saveAppLog = new TLRPC$TL_help_saveAppLog();
        TLRPC$TL_inputAppEvent tLRPC$TL_inputAppEvent = new TLRPC$TL_inputAppEvent();
        tLRPC$TL_inputAppEvent.time = (double) ConnectionsManager.getInstance(i).getCurrentTime();
        tLRPC$TL_inputAppEvent.type = "premium.promo_screen_tap";
        TLRPC$TL_jsonObject tLRPC$TL_jsonObject = new TLRPC$TL_jsonObject();
        tLRPC$TL_inputAppEvent.data = tLRPC$TL_jsonObject;
        TLRPC$TL_jsonObjectValue tLRPC$TL_jsonObjectValue = new TLRPC$TL_jsonObjectValue();
        TLRPC$TL_jsonString tLRPC$TL_jsonString = new TLRPC$TL_jsonString();
        tLRPC$TL_jsonString.value = featureTypeToServerString(i2);
        tLRPC$TL_jsonObjectValue.key = "item";
        tLRPC$TL_jsonObjectValue.value = tLRPC$TL_jsonString;
        tLRPC$TL_jsonObject.value.add(tLRPC$TL_jsonObjectValue);
        tLRPC$TL_help_saveAppLog.events.add(tLRPC$TL_inputAppEvent);
        tLRPC$TL_inputAppEvent.data = tLRPC$TL_jsonObject;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_help_saveAppLog, PremiumPreviewFragment$$ExternalSyntheticLambda14.INSTANCE);
    }

    public static final class SubscriptionTier {
        private int discount;
        /* access modifiers changed from: private */
        public ProductDetails googlePlayProductDetails;
        private ProductDetails.SubscriptionOfferDetails offerDetails;
        private long pricePerMonth;
        private long pricePerYear;
        private long pricePerYearRegular;
        public final TLRPC$TL_premiumSubscriptionOption subscriptionOption;
        public int yOffset;

        public SubscriptionTier(TLRPC$TL_premiumSubscriptionOption tLRPC$TL_premiumSubscriptionOption) {
            this.subscriptionOption = tLRPC$TL_premiumSubscriptionOption;
        }

        public ProductDetails getGooglePlayProductDetails() {
            return this.googlePlayProductDetails;
        }

        public ProductDetails.SubscriptionOfferDetails getOfferDetails() {
            checkOfferDetails();
            return this.offerDetails;
        }

        public void setGooglePlayProductDetails(ProductDetails productDetails) {
            this.googlePlayProductDetails = productDetails;
        }

        public void setPricePerYearRegular(long j) {
            this.pricePerYearRegular = j;
        }

        public int getMonths() {
            return this.subscriptionOption.months;
        }

        public int getDiscount() {
            if (this.discount == 0) {
                if (getPricePerMonth() == 0) {
                    return 0;
                }
                if (this.pricePerYearRegular != 0) {
                    double pricePerYear2 = (double) getPricePerYear();
                    double d = (double) this.pricePerYearRegular;
                    Double.isNaN(pricePerYear2);
                    Double.isNaN(d);
                    int i = (int) ((1.0d - (pricePerYear2 / d)) * 100.0d);
                    this.discount = i;
                    if (i == 0) {
                        this.discount = -1;
                    }
                }
            }
            return this.discount;
        }

        public long getPricePerYear() {
            if (this.pricePerYear == 0) {
                long price = getPrice();
                if (price != 0) {
                    double d = (double) price;
                    double d2 = (double) this.subscriptionOption.months;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    this.pricePerYear = (long) ((d / d2) * 12.0d);
                }
            }
            return this.pricePerYear;
        }

        public long getPricePerMonth() {
            if (this.pricePerMonth == 0) {
                long price = getPrice();
                if (price != 0) {
                    this.pricePerMonth = price / ((long) this.subscriptionOption.months);
                }
            }
            return this.pricePerMonth;
        }

        public String getFormattedPricePerYearRegular() {
            if (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) {
                return BillingController.getInstance().formatCurrency(this.pricePerYearRegular, getCurrency());
            }
            return this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(this.pricePerYearRegular, getCurrency(), 6);
        }

        public String getFormattedPricePerYear() {
            if (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) {
                return BillingController.getInstance().formatCurrency(getPricePerYear(), getCurrency());
            }
            return this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPricePerYear(), getCurrency(), 6);
        }

        public String getFormattedPricePerMonth() {
            if (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) {
                return BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency());
            }
            return this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency(), 6);
        }

        public long getPrice() {
            if (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) {
                return this.subscriptionOption.amount;
            }
            if (this.googlePlayProductDetails == null) {
                return 0;
            }
            checkOfferDetails();
            ProductDetails.SubscriptionOfferDetails subscriptionOfferDetails = this.offerDetails;
            if (subscriptionOfferDetails == null) {
                return 0;
            }
            return subscriptionOfferDetails.getPricingPhases().getPricingPhaseList().get(0).getPriceAmountMicros();
        }

        public String getCurrency() {
            if (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) {
                return this.subscriptionOption.currency;
            }
            if (this.googlePlayProductDetails == null) {
                return "";
            }
            checkOfferDetails();
            ProductDetails.SubscriptionOfferDetails subscriptionOfferDetails = this.offerDetails;
            if (subscriptionOfferDetails == null) {
                return "";
            }
            return subscriptionOfferDetails.getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode();
        }

        private void checkOfferDetails() {
            ProductDetails productDetails = this.googlePlayProductDetails;
            if (productDetails != null && this.offerDetails == null) {
                for (ProductDetails.SubscriptionOfferDetails next : productDetails.getSubscriptionOfferDetails()) {
                    String billingPeriod = next.getPricingPhases().getPricingPhaseList().get(0).getBillingPeriod();
                    if (getMonths() != 12) {
                        if (billingPeriod.equals(String.format(Locale.ROOT, "P%dM", new Object[]{Integer.valueOf(getMonths())}))) {
                        }
                    } else if (billingPeriod.equals("P1Y")) {
                    }
                    this.offerDetails = next;
                    return;
                }
            }
        }
    }
}
