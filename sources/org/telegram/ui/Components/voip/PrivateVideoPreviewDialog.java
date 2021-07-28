package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.webrtc.RendererCommon;

@TargetApi(21)
public abstract class PrivateVideoPreviewDialog extends FrameLayout implements VoIPService.StateListener {
    private ActionBar actionBar;
    private boolean cameraReady;
    /* access modifiers changed from: private */
    public int currentPage;
    /* access modifiers changed from: private */
    public int currentTexturePage = 1;
    private boolean isDismissed;
    private float outProgress;
    /* access modifiers changed from: private */
    public float pageOffset;
    private TextView positiveButton;
    private VoIPTextureView textureView;
    private TextView[] titles = new TextView[3];
    private LinearLayout titlesLayout;
    private ViewPager viewPager;
    private int visibleCameraPage = 1;

    public /* synthetic */ void onAudioSettingsChanged() {
        VoIPService.StateListener.CC.$default$onAudioSettingsChanged(this);
    }

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    /* access modifiers changed from: protected */
    public void onDismiss(boolean z, boolean z2) {
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onStateChanged(int i) {
        VoIPService.StateListener.CC.$default$onStateChanged(this, i);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PrivateVideoPreviewDialog(Context context) {
        super(context);
        Context context2 = context;
        ViewPager viewPager2 = new ViewPager(context2);
        this.viewPager = viewPager2;
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager2, NUM);
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int scrollState = 0;
            private int willSetPage;

            public void onPageScrolled(int i, float f, int i2) {
                int unused = PrivateVideoPreviewDialog.this.currentPage = i;
                float unused2 = PrivateVideoPreviewDialog.this.pageOffset = f;
                PrivateVideoPreviewDialog.this.updateTitlesLayout();
            }

            public void onPageSelected(int i) {
                if (this.scrollState == 0) {
                    if (i <= 1) {
                        int unused = PrivateVideoPreviewDialog.this.currentTexturePage = 1;
                    } else {
                        int unused2 = PrivateVideoPreviewDialog.this.currentTexturePage = 2;
                    }
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                } else if (i <= 1) {
                    this.willSetPage = 1;
                } else {
                    this.willSetPage = 2;
                }
            }

            public void onPageScrollStateChanged(int i) {
                this.scrollState = i;
                if (i == 0) {
                    int unused = PrivateVideoPreviewDialog.this.currentTexturePage = this.willSetPage;
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                }
            }
        });
        VoIPTextureView voIPTextureView = new VoIPTextureView(context2, false, false);
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        VoIPTextureView voIPTextureView2 = this.textureView;
        voIPTextureView2.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        voIPTextureView2.clipToTexture = true;
        voIPTextureView2.renderer.setAlpha(0.0f);
        this.textureView.renderer.setRotateTextureWitchScreen(true);
        this.textureView.renderer.setUseCameraRotation(true);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        ActionBar actionBar2 = new ActionBar(context2);
        this.actionBar = actionBar2;
        actionBar2.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setBackgroundColor(0);
        this.actionBar.setItemsColor(Theme.getColor("voipgroup_actionBarItems"), false);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PrivateVideoPreviewDialog.this.dismiss(false, false);
                }
            }
        });
        addView(this.actionBar);
        AnonymousClass3 r4 = new TextView(getContext()) {
            private Paint[] gradientPaint = new Paint[3];

            {
                int i = 0;
                while (true) {
                    Paint[] paintArr = this.gradientPaint;
                    if (i < paintArr.length) {
                        paintArr[i] = new Paint(1);
                        i++;
                    } else {
                        return;
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:12:0x002f  */
            /* JADX WARNING: Removed duplicated region for block: B:13:0x004b  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onSizeChanged(int r26, int r27, int r28, int r29) {
                /*
                    r25 = this;
                    r0 = r25
                    super.onSizeChanged(r26, r27, r28, r29)
                    r1 = 0
                    r2 = 0
                L_0x0007:
                    android.graphics.Paint[] r3 = r0.gradientPaint
                    int r3 = r3.length
                    if (r2 >= r3) goto L_0x0077
                    r3 = -9015575(0xfffffffffvar_ee9, float:-3.2756597E38)
                    r4 = 1
                    if (r2 == 0) goto L_0x0025
                    if (r2 == r4) goto L_0x001b
                    r5 = -1026983(0xffffffffffvar_, float:NaN)
                    r6 = -1792170(0xffffffffffe4a756, float:NaN)
                    goto L_0x002c
                L_0x001b:
                    r5 = -11033346(0xfffffffffvar_a4fe, float:-2.866407E38)
                    r3 = -11033346(0xfffffffffvar_a4fe, float:-2.866407E38)
                    r5 = -9015575(0xfffffffffvar_ee9, float:-3.2756597E38)
                    goto L_0x002b
                L_0x0025:
                    r3 = -8919716(0xfffffffffvar_e55c, float:-3.2951022E38)
                    r5 = -11089922(0xfffffffffvar_c7fe, float:-2.854932E38)
                L_0x002b:
                    r6 = 0
                L_0x002c:
                    r7 = 2
                    if (r6 == 0) goto L_0x004b
                    android.graphics.LinearGradient r16 = new android.graphics.LinearGradient
                    r9 = 0
                    r10 = 0
                    int r8 = r25.getMeasuredWidth()
                    float r11 = (float) r8
                    r12 = 0
                    r8 = 3
                    int[] r13 = new int[r8]
                    r13[r1] = r3
                    r13[r4] = r5
                    r13[r7] = r6
                    r14 = 0
                    android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP
                    r8 = r16
                    r8.<init>(r9, r10, r11, r12, r13, r14, r15)
                    goto L_0x006b
                L_0x004b:
                    android.graphics.LinearGradient r16 = new android.graphics.LinearGradient
                    r18 = 0
                    r19 = 0
                    int r6 = r25.getMeasuredWidth()
                    float r6 = (float) r6
                    r21 = 0
                    int[] r7 = new int[r7]
                    r7[r1] = r3
                    r7[r4] = r5
                    r23 = 0
                    android.graphics.Shader$TileMode r24 = android.graphics.Shader.TileMode.CLAMP
                    r17 = r16
                    r20 = r6
                    r22 = r7
                    r17.<init>(r18, r19, r20, r21, r22, r23, r24)
                L_0x006b:
                    r3 = r16
                    android.graphics.Paint[] r4 = r0.gradientPaint
                    r4 = r4[r2]
                    r4.setShader(r3)
                    int r2 = r2 + 1
                    goto L_0x0007
                L_0x0077:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.AnonymousClass3.onSizeChanged(int, int, int, int):void");
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage].setAlpha(255);
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage]);
                if (PrivateVideoPreviewDialog.this.pageOffset > 0.0f) {
                    int access$100 = PrivateVideoPreviewDialog.this.currentPage + 1;
                    Paint[] paintArr = this.gradientPaint;
                    if (access$100 < paintArr.length) {
                        paintArr[PrivateVideoPreviewDialog.this.currentPage + 1].setAlpha((int) (PrivateVideoPreviewDialog.this.pageOffset * 255.0f));
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage + 1]);
                    }
                }
                super.onDraw(canvas);
            }
        };
        this.positiveButton = r4;
        r4.setMinWidth(AndroidUtilities.dp(64.0f));
        this.positiveButton.setTag(-1);
        this.positiveButton.setTextSize(1, 14.0f);
        this.positiveButton.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.positiveButton.setGravity(17);
        this.positiveButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.positiveButton.setText(LocaleController.getString("VoipShareVideo", NUM));
        if (Build.VERSION.SDK_INT >= 23) {
            this.positiveButton.setForeground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_nameText"), 76)));
        }
        this.positiveButton.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        this.positiveButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PrivateVideoPreviewDialog.this.lambda$new$0$PrivateVideoPreviewDialog(view);
            }
        });
        addView(this.positiveButton, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 64.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.titlesLayout = linearLayout;
        addView(linearLayout, LayoutHelper.createFrame(-2, 64, 80));
        for (int i = 0; i < 3; i++) {
            this.titles[i] = new TextView(context2);
            this.titles[i].setTextSize(1, 12.0f);
            this.titles[i].setTextColor(-1);
            this.titles[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titles[i].setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.titles[i].setGravity(16);
            this.titles[i].setSingleLine(true);
            this.titlesLayout.addView(this.titles[i], LayoutHelper.createLinear(-2, -1));
            if (i == 0) {
                this.titles[i].setText(LocaleController.getString("VoipPhoneScreen", NUM));
            } else if (i == 1) {
                this.titles[i].setText(LocaleController.getString("VoipFrontCamera", NUM));
            } else if (i == 2) {
                this.titles[i].setText(LocaleController.getString("VoipBackCamera", NUM));
            }
            this.titles[i].setOnClickListener(new View.OnClickListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    PrivateVideoPreviewDialog.this.lambda$new$1$PrivateVideoPreviewDialog(this.f$1, view);
                }
            });
        }
        setAlpha(0.0f);
        setTranslationX((float) AndroidUtilities.dp(32.0f));
        animate().alpha(1.0f).translationX(0.0f).setDuration(150).start();
        setWillNotDraw(false);
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            this.textureView.renderer.setMirror(sharedInstance.isFrontFaceCamera());
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
                public void onFirstFrameRendered() {
                }

                public void onFrameResolutionChanged(int i, int i2, int i3) {
                }
            });
            sharedInstance.setLocalSink(this.textureView.renderer, false);
        }
        this.viewPager.setCurrentItem(1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PrivateVideoPreviewDialog(View view) {
        if (!this.isDismissed) {
            if (this.currentPage == 0) {
                ((Activity) getContext()).startActivityForResult(((MediaProjectionManager) getContext().getSystemService("media_projection")).createScreenCaptureIntent(), 520);
            } else {
                dismiss(false, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$PrivateVideoPreviewDialog(int i, View view) {
        this.viewPager.setCurrentItem(i, true);
    }

    public void setBottomPadding(int i) {
        ((FrameLayout.LayoutParams) this.positiveButton.getLayoutParams()).bottomMargin = AndroidUtilities.dp(64.0f) + i;
        ((FrameLayout.LayoutParams) this.titlesLayout.getLayoutParams()).bottomMargin = i;
    }

    /* access modifiers changed from: private */
    public void updateTitlesLayout() {
        TextView[] textViewArr = this.titles;
        int i = this.currentPage;
        TextView textView = textViewArr[i];
        TextView textView2 = i < 2 ? textViewArr[i + 1] : null;
        int measuredWidth = getMeasuredWidth() / 2;
        float left = (float) (textView.getLeft() + (textView.getMeasuredWidth() / 2));
        float measuredWidth2 = ((float) (getMeasuredWidth() / 2)) - left;
        if (textView2 != null) {
            measuredWidth2 -= (((float) (textView2.getLeft() + (textView2.getMeasuredWidth() / 2))) - left) * this.pageOffset;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            int i3 = this.currentPage;
            float f = 0.9f;
            float f2 = 0.7f;
            if (i2 >= i3 && i2 <= i3 + 1) {
                if (i2 == i3) {
                    float f3 = this.pageOffset;
                    f2 = 1.0f - (0.3f * f3);
                    f = 1.0f - (f3 * 0.1f);
                } else {
                    float f4 = this.pageOffset;
                    f2 = 0.7f + (0.3f * f4);
                    f = 0.9f + (f4 * 0.1f);
                }
            }
            this.titles[i2].setAlpha(f2);
            this.titles[i2].setScaleX(f);
            this.titles[i2].setScaleY(f);
        }
        this.titlesLayout.setTranslationX(measuredWidth2);
        this.positiveButton.invalidate();
        if (this.currentPage != 0 || this.pageOffset > 0.0f) {
            this.textureView.setVisibility(0);
            if (this.currentPage == this.currentTexturePage) {
                this.textureView.setTranslationX((-this.pageOffset) * ((float) getMeasuredWidth()));
            } else {
                this.textureView.setTranslationX((1.0f - this.pageOffset) * ((float) getMeasuredWidth()));
            }
        } else {
            this.textureView.setVisibility(4);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.registerStateListener(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
    }

    /* access modifiers changed from: private */
    public void onFinishMoveCameraPage() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (this.currentTexturePage != this.visibleCameraPage && sharedInstance != null) {
            boolean isFrontFaceCamera = sharedInstance.isFrontFaceCamera();
            int i = this.currentTexturePage;
            if ((i == 1 && !isFrontFaceCamera) || (i == 2 && isFrontFaceCamera)) {
                saveLastCameraBitmap();
                this.cameraReady = false;
                VoIPService.getSharedInstance().switchCamera();
                this.textureView.setAlpha(0.0f);
            }
            this.visibleCameraPage = this.currentTexturePage;
        }
    }

    private void saveLastCameraBitmap() {
        if (this.cameraReady) {
            try {
                Bitmap bitmap = this.textureView.renderer.getBitmap();
                if (bitmap != null) {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.textureView.renderer.getMatrix(), true);
                    bitmap.recycle();
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap, 80, (int) (((float) createBitmap.getHeight()) / (((float) createBitmap.getWidth()) / 80.0f)), true);
                    if (createScaledBitmap != null) {
                        if (createScaledBitmap != createBitmap) {
                            createBitmap.recycle();
                        }
                        Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), createScaledBitmap.getHeight(), createScaledBitmap.getRowBytes());
                        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                        createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(filesDirFixed, "cthumb" + this.visibleCameraPage + ".jpg")));
                        View findViewWithTag = this.viewPager.findViewWithTag(Integer.valueOf(this.visibleCameraPage));
                        if (findViewWithTag instanceof ImageView) {
                            ((ImageView) findViewWithTag).setImageBitmap(createScaledBitmap);
                        }
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public void onCameraFirstFrameAvailable() {
        if (!this.cameraReady) {
            this.cameraReady = true;
            this.textureView.animate().alpha(1.0f).setDuration(250);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateTitlesLayout();
    }

    public void dismiss(final boolean z, final boolean z2) {
        if (!this.isDismissed) {
            this.isDismissed = true;
            saveLastCameraBitmap();
            animate().alpha(0.0f).translationX((float) AndroidUtilities.dp(32.0f)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    if (PrivateVideoPreviewDialog.this.getParent() != null) {
                        ((ViewGroup) PrivateVideoPreviewDialog.this.getParent()).removeView(PrivateVideoPreviewDialog.this);
                    }
                    PrivateVideoPreviewDialog.this.onDismiss(z, z2);
                }
            });
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (View.MeasureSpec.getSize(i) > View.MeasureSpec.getSize(i2)) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.positiveButton.getLayoutParams();
            int dp = AndroidUtilities.dp(80.0f);
            marginLayoutParams.leftMargin = dp;
            marginLayoutParams.rightMargin = dp;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.positiveButton.getLayoutParams();
            int dp2 = AndroidUtilities.dp(16.0f);
            marginLayoutParams2.leftMargin = dp2;
            marginLayoutParams2.rightMargin = dp2;
        }
        super.onMeasure(i, i2);
        measureChildWithMargins(this.titlesLayout, View.MeasureSpec.makeMeasureSpec(0, 0), 0, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM), 0);
    }

    public int getBackgroundColor() {
        return ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_actionBar"), (int) (getAlpha() * (1.0f - this.outProgress) * 255.0f));
    }

    public void invalidate() {
        super.invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    private class Adapter extends PagerAdapter {
        public int getCount() {
            return 3;
        }

        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

        private Adapter() {
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: android.widget.ImageView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: android.widget.ImageView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: android.widget.FrameLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: android.widget.ImageView} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Object instantiateItem(android.view.ViewGroup r10, int r11) {
            /*
                r9 = this;
                if (r11 != 0) goto L_0x0098
                android.widget.FrameLayout r11 = new android.widget.FrameLayout
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r0 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r0 = r0.getContext()
                r11.<init>(r0)
                org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable
                r2 = -14602694(0xfffffffffvar_e3a, float:-2.1424573E38)
                r3 = -13935795(0xffffffffff2b5b4d, float:-2.2777205E38)
                r4 = -14395293(0xfffffffffvar_, float:-2.1845232E38)
                r5 = -14203560(0xfffffffffvar_, float:-2.2234113E38)
                r6 = 1
                r1 = r0
                r1.<init>(r2, r3, r4, r5, r6)
                r11.setBackground(r0)
                android.widget.ImageView r0 = new android.widget.ImageView
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r1 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r1 = r1.getContext()
                r0.<init>(r1)
                android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
                r0.setScaleType(r1)
                r1 = 2131166005(0x7var_, float:1.7946243E38)
                r0.setImageResource(r1)
                r2 = 82
                r3 = 1118044160(0x42a40000, float:82.0)
                r4 = 17
                r5 = 0
                r6 = 0
                r7 = 0
                r8 = 1114636288(0x42700000, float:60.0)
                android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
                r11.addView(r0, r1)
                android.widget.TextView r0 = new android.widget.TextView
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r1 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r1 = r1.getContext()
                r0.<init>(r1)
                r1 = 2131628299(0x7f0e110b, float:1.8883887E38)
                java.lang.String r2 = "VoipVideoPrivateScreenSharing"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                r1 = 17
                r0.setGravity(r1)
                r1 = 1073741824(0x40000000, float:2.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
                r2 = 1065353216(0x3var_, float:1.0)
                r0.setLineSpacing(r1, r2)
                r1 = -1
                r0.setTextColor(r1)
                r1 = 1
                r2 = 1097859072(0x41700000, float:15.0)
                r0.setTextSize(r1, r2)
                java.lang.String r1 = "fonts/rmedium.ttf"
                android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
                r0.setTypeface(r1)
                r2 = -1
                r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r5 = 1101529088(0x41a80000, float:21.0)
                r6 = 1105199104(0x41e00000, float:28.0)
                r7 = 1101529088(0x41a80000, float:21.0)
                r8 = 0
                android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
                r11.addView(r0, r1)
                goto L_0x00e9
            L_0x0098:
                android.widget.ImageView r0 = new android.widget.ImageView
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r1 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r1 = r1.getContext()
                r0.<init>(r1)
                r1 = 2131165523(0x7var_, float:1.7945266E38)
                r0.setImageResource(r1)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r11)
                r0.setTag(r2)
                r2 = 0
                java.io.File r3 = new java.io.File     // Catch:{ all -> 0x00d9 }
                java.io.File r4 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x00d9 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d9 }
                r5.<init>()     // Catch:{ all -> 0x00d9 }
                java.lang.String r6 = "cthumb"
                r5.append(r6)     // Catch:{ all -> 0x00d9 }
                r5.append(r11)     // Catch:{ all -> 0x00d9 }
                java.lang.String r11 = ".jpg"
                r5.append(r11)     // Catch:{ all -> 0x00d9 }
                java.lang.String r11 = r5.toString()     // Catch:{ all -> 0x00d9 }
                r3.<init>(r4, r11)     // Catch:{ all -> 0x00d9 }
                java.lang.String r11 = r3.getAbsolutePath()     // Catch:{ all -> 0x00d9 }
                android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r11)     // Catch:{ all -> 0x00d9 }
                goto L_0x00da
            L_0x00d9:
            L_0x00da:
                if (r2 == 0) goto L_0x00e0
                r0.setImageBitmap(r2)
                goto L_0x00e3
            L_0x00e0:
                r0.setImageResource(r1)
            L_0x00e3:
                android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.FIT_XY
                r0.setScaleType(r11)
                r11 = r0
            L_0x00e9:
                android.view.ViewParent r0 = r11.getParent()
                if (r0 == 0) goto L_0x00f8
                android.view.ViewParent r0 = r11.getParent()
                android.view.ViewGroup r0 = (android.view.ViewGroup) r0
                r0.removeView(r11)
            L_0x00f8:
                r0 = 0
                r10.addView(r11, r0)
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.Adapter.instantiateItem(android.view.ViewGroup, int):java.lang.Object");
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }
}
