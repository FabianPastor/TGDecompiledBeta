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
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;
import org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
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
import org.telegram.tgnet.TLRPC$TL_payments_canPurchasePremium;
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
    int rowCount;
    int sectionRow;
    FrameLayout settingsView;
    Shader shader;
    Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public int statusBarHeight;
    int statusRow;
    int totalGradientHeight;
    float totalProgress;

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

    public boolean isLightStatusBar() {
        return false;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return true;
    }

    public PremiumPreviewFragment() {
        Bitmap createBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.gradientTextureBitmap = createBitmap;
        this.gradientCanvas = new Canvas(createBitmap);
        this.gradientTools = new PremiumGradient.GradientTools("premiumGradientBackground1", "premiumGradientBackground2", "premiumGradientBackground3", "premiumGradientBackground4");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int severStringToFeatureType(java.lang.String r13) {
        /*
            r13.hashCode()
            int r0 = r13.hashCode()
            r1 = 10
            r2 = 9
            r3 = 8
            r4 = 7
            r5 = 6
            r6 = 5
            r7 = 4
            r8 = 3
            r9 = 2
            r10 = 1
            r11 = 0
            r12 = -1
            switch(r0) {
                case -2145993328: goto L_0x0090;
                case -1755514268: goto L_0x0085;
                case -1040323278: goto L_0x007a;
                case -1023650261: goto L_0x006f;
                case -730864243: goto L_0x0064;
                case -448825858: goto L_0x0059;
                case -165039170: goto L_0x004e;
                case -96210874: goto L_0x0043;
                case 1182539900: goto L_0x0036;
                case 1219849581: goto L_0x0029;
                case 1832801148: goto L_0x001c;
                default: goto L_0x0019;
            }
        L_0x0019:
            r13 = -1
            goto L_0x009a
        L_0x001c:
            java.lang.String r0 = "app_icons"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x0025
            goto L_0x0019
        L_0x0025:
            r13 = 10
            goto L_0x009a
        L_0x0029:
            java.lang.String r0 = "advanced_chat_management"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x0032
            goto L_0x0019
        L_0x0032:
            r13 = 9
            goto L_0x009a
        L_0x0036:
            java.lang.String r0 = "unique_reactions"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x003f
            goto L_0x0019
        L_0x003f:
            r13 = 8
            goto L_0x009a
        L_0x0043:
            java.lang.String r0 = "double_limits"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x004c
            goto L_0x0019
        L_0x004c:
            r13 = 7
            goto L_0x009a
        L_0x004e:
            java.lang.String r0 = "premium_stickers"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x0057
            goto L_0x0019
        L_0x0057:
            r13 = 6
            goto L_0x009a
        L_0x0059:
            java.lang.String r0 = "faster_download"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x0062
            goto L_0x0019
        L_0x0062:
            r13 = 5
            goto L_0x009a
        L_0x0064:
            java.lang.String r0 = "profile_badge"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x006d
            goto L_0x0019
        L_0x006d:
            r13 = 4
            goto L_0x009a
        L_0x006f:
            java.lang.String r0 = "more_upload"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x0078
            goto L_0x0019
        L_0x0078:
            r13 = 3
            goto L_0x009a
        L_0x007a:
            java.lang.String r0 = "no_ads"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x0083
            goto L_0x0019
        L_0x0083:
            r13 = 2
            goto L_0x009a
        L_0x0085:
            java.lang.String r0 = "voice_to_text"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x008e
            goto L_0x0019
        L_0x008e:
            r13 = 1
            goto L_0x009a
        L_0x0090:
            java.lang.String r0 = "animated_userpics"
            boolean r13 = r13.equals(r0)
            if (r13 != 0) goto L_0x0099
            goto L_0x0019
        L_0x0099:
            r13 = 0
        L_0x009a:
            switch(r13) {
                case 0: goto L_0x00a8;
                case 1: goto L_0x00a7;
                case 2: goto L_0x00a6;
                case 3: goto L_0x00a5;
                case 4: goto L_0x00a4;
                case 5: goto L_0x00a3;
                case 6: goto L_0x00a2;
                case 7: goto L_0x00a1;
                case 8: goto L_0x00a0;
                case 9: goto L_0x009f;
                case 10: goto L_0x009e;
                default: goto L_0x009d;
            }
        L_0x009d:
            return r12
        L_0x009e:
            return r1
        L_0x009f:
            return r2
        L_0x00a0:
            return r7
        L_0x00a1:
            return r11
        L_0x00a2:
            return r6
        L_0x00a3:
            return r9
        L_0x00a4:
            return r5
        L_0x00a5:
            return r10
        L_0x00a6:
            return r8
        L_0x00a7:
            return r3
        L_0x00a8:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.severStringToFeatureType(java.lang.String):int");
    }

    public PremiumPreviewFragment setForcePremium() {
        this.forcePremium = true;
        return this;
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public View createView(Context context) {
        this.hasOwnBackground = true;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{-816858, -2401123, -5806081, -11164161}, new float[]{0.0f, 0.32f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
        this.shader = linearGradient;
        linearGradient.setLocalMatrix(this.matrix);
        this.gradientPaint.setShader(this.shader);
        this.dummyCell = new PremiumFeatureCell(context);
        this.premiumFeatures.clear();
        fillPremiumFeaturesList(this.premiumFeatures, this.currentAccount);
        final Rect rect = new Rect();
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(rect);
        if (Build.VERSION.SDK_INT >= 21) {
            this.statusBarHeight = AndroidUtilities.statusBarHeight;
        }
        AnonymousClass1 r2 = new FrameLayout(context) {
            boolean iconInterceptedTouch;
            int lastSize;
            float progressToFull;

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                float x = PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX();
                float y = PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(x, y, ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()) + x, ((float) PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight()) + y);
                if (!rectF.contains(motionEvent.getX(), motionEvent.getY()) && !this.iconInterceptedTouch) {
                    return super.dispatchTouchEvent(motionEvent);
                }
                motionEvent.offsetLocation(-x, -y);
                if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                    this.iconInterceptedTouch = true;
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.iconInterceptedTouch = false;
                }
                PremiumPreviewFragment.this.backgroundView.imageView.dispatchTouchEvent(motionEvent);
                return true;
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
                    int unused = PremiumPreviewFragment.this.statusBarHeight = AndroidUtilities.statusBarHeight;
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
                this.progressToFull = 0.0f;
                if (PremiumPreviewFragment.this.currentYOffset < AndroidUtilities.dp(30.0f) + bottom2) {
                    this.progressToFull = ((float) ((bottom2 + AndroidUtilities.dp(30.0f)) - PremiumPreviewFragment.this.currentYOffset)) / ((float) AndroidUtilities.dp(30.0f));
                }
                PremiumPreviewFragment premiumPreviewFragment5 = PremiumPreviewFragment.this;
                if (premiumPreviewFragment5.isLandscapeMode) {
                    this.progressToFull = 1.0f;
                    premiumPreviewFragment5.totalProgress = 1.0f;
                }
                float max = Math.max((((((float) ((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() - PremiumPreviewFragment.this.statusBarHeight) - PremiumPreviewFragment.this.backgroundView.titleView.getMeasuredHeight())) / 2.0f) + ((float) PremiumPreviewFragment.this.statusBarHeight)) - ((float) PremiumPreviewFragment.this.backgroundView.getTop())) - ((float) PremiumPreviewFragment.this.backgroundView.titleView.getTop()), (float) ((premiumPreviewFragment5.currentYOffset - ((PremiumPreviewFragment.this.actionBar.getMeasuredHeight() + PremiumPreviewFragment.this.backgroundView.getMeasuredHeight()) - PremiumPreviewFragment.this.statusBarHeight)) + AndroidUtilities.dp(16.0f)));
                float dp = ((-max) / 4.0f) + ((float) AndroidUtilities.dp(16.0f));
                PremiumPreviewFragment.this.backgroundView.setTranslationY(max);
                PremiumPreviewFragment.this.backgroundView.imageView.setTranslationY(dp + ((float) AndroidUtilities.dp(16.0f)));
                PremiumPreviewFragment premiumPreviewFragment6 = PremiumPreviewFragment.this;
                float f4 = premiumPreviewFragment6.totalProgress;
                float f5 = ((1.0f - f4) * 0.4f) + 0.6f;
                float f6 = 1.0f - (f4 > 0.5f ? (f4 - 0.5f) / 0.5f : 0.0f);
                premiumPreviewFragment6.backgroundView.imageView.setScaleX(f5);
                PremiumPreviewFragment.this.backgroundView.imageView.setScaleY(f5);
                PremiumPreviewFragment.this.backgroundView.imageView.setAlpha(f6);
                PremiumPreviewFragment.this.backgroundView.subtitleView.setAlpha(f6);
                PremiumPreviewFragment premiumPreviewFragment7 = PremiumPreviewFragment.this;
                premiumPreviewFragment7.particlesView.setAlpha(1.0f - premiumPreviewFragment7.totalProgress);
                StarParticlesView starParticlesView = PremiumPreviewFragment.this.particlesView;
                starParticlesView.setTranslationY((((float) (-(starParticlesView.getMeasuredHeight() - PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()))) / 2.0f) + PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY());
                float dp2 = (float) (AndroidUtilities.dp(72.0f) - PremiumPreviewFragment.this.backgroundView.titleView.getLeft());
                PremiumPreviewFragment premiumPreviewFragment8 = PremiumPreviewFragment.this;
                float f7 = premiumPreviewFragment8.totalProgress;
                if (f7 > 0.3f) {
                    f3 = (f7 - 0.3f) / 0.7f;
                }
                premiumPreviewFragment8.backgroundView.titleView.setTranslationX(dp2 * (1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(1.0f - f3)));
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = ((PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX()) + ((((float) getMeasuredWidth()) * 0.1f) * PremiumPreviewFragment.this.progress)) / ((float) getMeasuredWidth());
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY()) / ((float) getMeasuredHeight());
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    invalidate();
                }
                PremiumPreviewFragment.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), ((float) (-getMeasuredWidth())) * 0.1f * PremiumPreviewFragment.this.progress, 0.0f);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) PremiumPreviewFragment.this.currentYOffset, PremiumPreviewFragment.this.gradientTools.paint);
                super.dispatchDraw(canvas);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                if (view != premiumPreviewFragment.listView) {
                    return super.drawChild(canvas, view, j);
                }
                premiumPreviewFragment.shadowDrawable.setBounds((int) (((float) (-rect.left)) - (((float) AndroidUtilities.dp(16.0f)) * this.progressToFull)), (PremiumPreviewFragment.this.currentYOffset - rect.top) - AndroidUtilities.dp(16.0f), (int) (((float) (getMeasuredWidth() + rect.right)) + (((float) AndroidUtilities.dp(16.0f)) * this.progressToFull)), getMeasuredHeight());
                PremiumPreviewFragment.this.shadowDrawable.draw(canvas);
                canvas.save();
                canvas.clipRect(0, PremiumPreviewFragment.this.actionBar.getBottom(), getMeasuredWidth(), getMeasuredHeight());
                super.drawChild(canvas, view, j);
                canvas.restore();
                return true;
            }
        };
        this.contentView = r2;
        r2.setFitsSystemWindows(true);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, (AndroidUtilities.dp(68.0f) + this.statusBarHeight) - AndroidUtilities.dp(16.0f), this.listView);
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView.setLayoutManager(fillLastLinearLayoutManager);
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
        this.backgroundView = new BackgroundView(this, context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        this.particlesView = new StarParticlesView(context);
        this.backgroundView.imageView.setStarParticlesView(this.particlesView);
        this.contentView.addView(this.particlesView, LayoutHelper.createFrame(-1, -2.0f));
        this.contentView.addView(this.backgroundView, LayoutHelper.createFrame(-1, -2.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PremiumPreviewFragment$$ExternalSyntheticLambda7(this));
        this.contentView.addView(this.listView);
        PremiumButtonView premiumButtonView2 = new PremiumButtonView(context, false);
        this.premiumButtonView = premiumButtonView2;
        premiumButtonView2.setButton(getPremiumButtonText(this.currentAccount), new PremiumPreviewFragment$$ExternalSyntheticLambda0(this));
        this.buttonContainer = new FrameLayout(context);
        View view = new View(context);
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
            AndroidUtilities.runOnUIThread(new PremiumPreviewFragment$$ExternalSyntheticLambda3(this), 400);
        }
        MediaDataController.getInstance(this.currentAccount).preloadPremiumPreviewStickers();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
        if (view instanceof PremiumFeatureCell) {
            PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) view;
            if (premiumFeatureCell.data.type == 0) {
                DoubledLimitsBottomSheet doubledLimitsBottomSheet = new DoubledLimitsBottomSheet(this, this.currentAccount);
                doubledLimitsBottomSheet.setParentFragment(this);
                showDialog(doubledLimitsBottomSheet);
                return;
            }
            showDialog(new PremiumFeatureBottomSheet(this, premiumFeatureCell.data.type, false));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        buyPremium(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2() {
        getMediaDataController().loadPremiumPromo(false);
    }

    public static void buyPremium(BaseFragment baseFragment) {
        buyPremium(baseFragment, "settings");
    }

    public static void fillPremiumFeaturesList(ArrayList<PremiumFeatureData> arrayList, int i) {
        MessagesController instance = MessagesController.getInstance(i);
        int i2 = 0;
        arrayList.add(new PremiumFeatureData(0, NUM, LocaleController.getString("PremiumPreviewLimits", NUM), LocaleController.formatString("PremiumPreviewLimitsDescription", NUM, Integer.valueOf(instance.channelsLimitPremium), Integer.valueOf(instance.dialogFiltersLimitPremium), Integer.valueOf(instance.dialogFiltersPinnedLimitPremium), Integer.valueOf(instance.publicLinksLimitPremium), 4)));
        arrayList.add(new PremiumFeatureData(1, NUM, LocaleController.getString("PremiumPreviewUploads", NUM), LocaleController.getString("PremiumPreviewUploadsDescription", NUM)));
        arrayList.add(new PremiumFeatureData(2, NUM, LocaleController.getString("PremiumPreviewDownloadSpeed", NUM), LocaleController.getString("PremiumPreviewDownloadSpeedDescription", NUM)));
        arrayList.add(new PremiumFeatureData(8, NUM, LocaleController.getString("PremiumPreviewVoiceToText", NUM), LocaleController.getString("PremiumPreviewVoiceToTextDescription", NUM)));
        arrayList.add(new PremiumFeatureData(3, NUM, LocaleController.getString("PremiumPreviewNoAds", NUM), LocaleController.getString("PremiumPreviewNoAdsDescription", NUM)));
        arrayList.add(new PremiumFeatureData(4, NUM, LocaleController.getString("PremiumPreviewReactions", NUM), LocaleController.getString("PremiumPreviewReactionsDescription", NUM)));
        arrayList.add(new PremiumFeatureData(5, NUM, LocaleController.getString("PremiumPreviewStickers", NUM), LocaleController.getString("PremiumPreviewStickersDescription", NUM)));
        arrayList.add(new PremiumFeatureData(9, NUM, LocaleController.getString("PremiumPreviewAdvancedChatManagement", NUM), LocaleController.getString("PremiumPreviewAdvancedChatManagementDescription", NUM)));
        arrayList.add(new PremiumFeatureData(6, NUM, LocaleController.getString("PremiumPreviewProfileBadge", NUM), LocaleController.getString("PremiumPreviewProfileBadgeDescription", NUM)));
        arrayList.add(new PremiumFeatureData(7, NUM, LocaleController.getString("PremiumPreviewAnimatedProfiles", NUM), LocaleController.getString("PremiumPreviewAnimatedProfilesDescription", NUM)));
        arrayList.add(new PremiumFeatureData(10, NUM, LocaleController.getString("PremiumPreviewAppIcon", NUM), LocaleController.getString("PremiumPreviewAppIconDescription", NUM)));
        if (instance.premiumFeaturesTypesToPosition.size() > 0) {
            while (i2 < arrayList.size()) {
                if (instance.premiumFeaturesTypesToPosition.get(arrayList.get(i2).type, -1) == -1) {
                    arrayList.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
        Collections.sort(arrayList, new PremiumPreviewFragment$$ExternalSyntheticLambda4(instance));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$fillPremiumFeaturesList$3(MessagesController messagesController, PremiumFeatureData premiumFeatureData, PremiumFeatureData premiumFeatureData2) {
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
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            baseFragment.showDialog(new PremiumNotAvailableBottomSheet(baseFragment));
        } else if (BuildVars.useInvoiceBilling()) {
            Activity parentActivity = baseFragment.getParentActivity();
            if (parentActivity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) parentActivity;
                if (!TextUtils.isEmpty(baseFragment.getMessagesController().premiumBotUsername)) {
                    launchActivity.setNavigateToPremiumBot(true);
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/" + baseFragment.getMessagesController().premiumBotUsername + "?start=" + str)));
                } else if (!TextUtils.isEmpty(baseFragment.getMessagesController().premiumInvoiceSlug)) {
                    launchActivity.onNewIntent(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/$" + baseFragment.getMessagesController().premiumInvoiceSlug)));
                }
            }
        } else {
            ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
            if (productDetails != null) {
                List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails = productDetails.getSubscriptionOfferDetails();
                if (!subscriptionOfferDetails.isEmpty()) {
                    BillingController.getInstance().addResultListener("telegram_premium", new PremiumPreviewFragment$$ExternalSyntheticLambda1(baseFragment));
                    TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium = new TLRPC$TL_payments_canPurchasePremium();
                    baseFragment.getConnectionsManager().sendRequest(tLRPC$TL_payments_canPurchasePremium, new PremiumPreviewFragment$$ExternalSyntheticLambda5(baseFragment, subscriptionOfferDetails, tLRPC$TL_payments_canPurchasePremium));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$buyPremium$4(BaseFragment baseFragment, BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            if (baseFragment instanceof PremiumPreviewFragment) {
                PremiumPreviewFragment premiumPreviewFragment = (PremiumPreviewFragment) baseFragment;
                premiumPreviewFragment.setForcePremium();
                premiumPreviewFragment.getMediaDataController().loadPremiumPromo(false);
                premiumPreviewFragment.listView.smoothScrollToPosition(0);
            } else {
                baseFragment.presentFragment(new PremiumPreviewFragment().setForcePremium());
            }
            if (baseFragment.getParentActivity() instanceof LaunchActivity) {
                try {
                    baseFragment.getFragmentView().performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                ((LaunchActivity) baseFragment.getParentActivity()).getFireworksOverlay().start();
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$buyPremium$5(TLObject tLObject, BaseFragment baseFragment, List list, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            BillingController.getInstance().launchBillingFlow(baseFragment.getParentActivity(), Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS).setOfferToken(((ProductDetails.SubscriptionOfferDetails) list.get(0)).getOfferToken()).build()));
        } else {
            AlertsCreator.processError(baseFragment.getCurrentAccount(), tLRPC$TL_error, baseFragment, tLRPC$TL_payments_canPurchasePremium, new Object[0]);
        }
    }

    public static String getPremiumButtonText(int i) {
        Currency instance;
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            return LocaleController.getString(NUM);
        }
        if (BuildVars.useInvoiceBilling()) {
            TLRPC$TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(i).getPremiumPromo();
            if (premiumPromo == null || (instance = Currency.getInstance(premiumPromo.currency)) == null) {
                return LocaleController.getString(NUM);
            }
            NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
            currencyInstance.setCurrency(instance);
            return LocaleController.formatString(NUM, currencyInstance.format((double) (((float) premiumPromo.monthly_amount) / 100.0f)));
        }
        String str = null;
        ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
        if (productDetails != null) {
            List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails = productDetails.getSubscriptionOfferDetails();
            if (!subscriptionOfferDetails.isEmpty()) {
                Iterator<ProductDetails.PricingPhase> it = subscriptionOfferDetails.get(0).getPricingPhases().getPricingPhaseList().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ProductDetails.PricingPhase next = it.next();
                    if (next.getBillingPeriod().equals("P1M")) {
                        str = next.getFormattedPrice();
                        break;
                    }
                }
            }
        }
        if (str == null) {
            return LocaleController.getString(NUM);
        }
        return LocaleController.formatString(NUM, str);
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
        this.statusRow = -1;
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
            this.premiumButtonView.buttonTextView.setText(getPremiumButtonText(this.currentAccount));
        }
        if (i == NotificationCenter.currentUserPremiumStatusChanged || i == NotificationCenter.premiumPromoUpdated) {
            this.backgroundView.updateText();
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
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, NUM, Theme.getColor("windowBackgroundGrayShadow")), 0, 0);
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
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), NUM, Theme.getColor("windowBackgroundGrayShadow")), 0, 0);
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
            textView2.setTextSize(1, 13.0f);
            textView2.setGravity(1);
            addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 9, 16, 0));
            updateText();
        }

        public void updateText() {
            this.titleView.setText(LocaleController.getString(PremiumPreviewFragment.this.forcePremium ? NUM : NUM));
            this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString((PremiumPreviewFragment.this.getUserConfig().isPremium() || PremiumPreviewFragment.this.forcePremium) ? NUM : NUM)));
        }
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
        return SimpleThemeDescription.createThemeDescriptions(new PremiumPreviewFragment$$ExternalSyntheticLambda6(this), "premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4", "premiumGradientBackground1", "premiumGradientBackground2", "premiumGradientBackground3", "premiumGradientBackground4", "premiumGradientBackgroundOverlay", "premiumStarGradient1", "premiumStarGradient2", "premiumStartSmallStarsColor");
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
}
