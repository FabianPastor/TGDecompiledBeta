package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.webrtc.RendererCommon;
@TargetApi(21)
/* loaded from: classes3.dex */
public abstract class PrivateVideoPreviewDialog extends FrameLayout implements VoIPService.StateListener {
    private boolean cameraReady;
    private int currentPage;
    private int currentTexturePage;
    private boolean isDismissed;
    public boolean micEnabled;
    private RLottieImageView micIconView;
    private boolean needScreencast;
    private float outProgress;
    private float pageOffset;
    private TextView positiveButton;
    private VoIPTextureView textureView;
    private TextView[] titles;
    private LinearLayout titlesLayout;
    private ViewPager viewPager;
    private int visibleCameraPage;

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onAudioSettingsChanged() {
        VoIPService.StateListener.CC.$default$onAudioSettingsChanged(this);
    }

    protected void onDismiss(boolean z, boolean z2) {
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onStateChanged(int i) {
        VoIPService.StateListener.CC.$default$onStateChanged(this, i);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public PrivateVideoPreviewDialog(Context context, boolean z, boolean z2) {
        super(context);
        this.currentTexturePage = 1;
        this.visibleCameraPage = 1;
        this.needScreencast = z2;
        this.titles = new TextView[z2 ? 3 : 2];
        ViewPager viewPager = new ViewPager(context);
        this.viewPager = viewPager;
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, NUM);
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.1
            private int scrollState = 0;
            private int willSetPage;

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int i, float f, int i2) {
                PrivateVideoPreviewDialog.this.currentPage = i;
                PrivateVideoPreviewDialog.this.pageOffset = f;
                PrivateVideoPreviewDialog.this.updateTitlesLayout();
            }

            /* JADX WARN: Type inference failed for: r0v2, types: [boolean] */
            /* JADX WARN: Type inference failed for: r0v4, types: [boolean] */
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                if (this.scrollState == 0) {
                    if (i <= PrivateVideoPreviewDialog.this.needScreencast) {
                        PrivateVideoPreviewDialog.this.currentTexturePage = 1;
                    } else {
                        PrivateVideoPreviewDialog.this.currentTexturePage = 2;
                    }
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                } else if (i <= PrivateVideoPreviewDialog.this.needScreencast) {
                    this.willSetPage = 1;
                } else {
                    this.willSetPage = 2;
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i) {
                this.scrollState = i;
                if (i == 0) {
                    PrivateVideoPreviewDialog.this.currentTexturePage = this.willSetPage;
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                }
            }
        });
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, false);
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        VoIPTextureView voIPTextureView2 = this.textureView;
        voIPTextureView2.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        voIPTextureView2.clipToTexture = true;
        voIPTextureView2.renderer.setAlpha(0.0f);
        this.textureView.renderer.setRotateTextureWithScreen(true);
        this.textureView.renderer.setUseCameraRotation(true);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setBackgroundColor(0);
        actionBar.setItemsColor(Theme.getColor("voipgroup_actionBarItems"), false);
        actionBar.setOccupyStatusBar(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    PrivateVideoPreviewDialog.this.dismiss(false, false);
                }
            }
        });
        addView(actionBar);
        TextView textView = new TextView(getContext()) { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.3
            private Paint[] gradientPaint;

            {
                this.gradientPaint = new Paint[PrivateVideoPreviewDialog.this.titles.length];
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

            /* JADX WARN: Removed duplicated region for block: B:20:0x0042  */
            /* JADX WARN: Removed duplicated region for block: B:21:0x005e  */
            @Override // android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            protected void onSizeChanged(int r26, int r27, int r28, int r29) {
                /*
                    r25 = this;
                    r0 = r25
                    super.onSizeChanged(r26, r27, r28, r29)
                    r1 = 0
                    r2 = 0
                L7:
                    android.graphics.Paint[] r3 = r0.gradientPaint
                    int r3 = r3.length
                    if (r2 >= r3) goto L8b
                    r3 = -9015575(0xfffffffffvar_ee9, float:-3.2756597E38)
                    r4 = 1
                    if (r2 != 0) goto L21
                    org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r5 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                    boolean r5 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.access$400(r5)
                    if (r5 == 0) goto L21
                    r3 = -8919716(0xfffffffffvar_e55c, float:-3.2951022E38)
                    r5 = -11089922(0xfffffffffvar_c7fe, float:-2.854932E38)
                    goto L3e
                L21:
                    if (r2 == 0) goto L35
                    if (r2 != r4) goto L2e
                    org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r5 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                    boolean r5 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.access$400(r5)
                    if (r5 == 0) goto L2e
                    goto L35
                L2e:
                    r5 = -1026983(0xffffffffffvar_, float:NaN)
                    r6 = -1792170(0xffffffffffe4a756, float:NaN)
                    goto L3f
                L35:
                    r5 = -11033346(0xfffffffffvar_a4fe, float:-2.866407E38)
                    r3 = -11033346(0xfffffffffvar_a4fe, float:-2.866407E38)
                    r5 = -9015575(0xfffffffffvar_ee9, float:-3.2756597E38)
                L3e:
                    r6 = 0
                L3f:
                    r7 = 2
                    if (r6 == 0) goto L5e
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
                    goto L7e
                L5e:
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
                L7e:
                    r3 = r16
                    android.graphics.Paint[] r4 = r0.gradientPaint
                    r4 = r4[r2]
                    r4.setShader(r3)
                    int r2 = r2 + 1
                    goto L7
                L8b:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.AnonymousClass3.onSizeChanged(int, int, int, int):void");
            }

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage].setAlpha(255);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage]);
                if (PrivateVideoPreviewDialog.this.pageOffset > 0.0f) {
                    int i = PrivateVideoPreviewDialog.this.currentPage + 1;
                    Paint[] paintArr = this.gradientPaint;
                    if (i < paintArr.length) {
                        paintArr[PrivateVideoPreviewDialog.this.currentPage + 1].setAlpha((int) (PrivateVideoPreviewDialog.this.pageOffset * 255.0f));
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage + 1]);
                    }
                }
                super.onDraw(canvas);
            }
        };
        this.positiveButton = textView;
        textView.setMinWidth(AndroidUtilities.dp(64.0f));
        this.positiveButton.setTag(-1);
        this.positiveButton.setTextSize(1, 14.0f);
        this.positiveButton.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.positiveButton.setGravity(17);
        this.positiveButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.positiveButton.setText(LocaleController.getString("VoipShareVideo", R.string.VoipShareVideo));
        if (Build.VERSION.SDK_INT >= 23) {
            this.positiveButton.setForeground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_nameText"), 76)));
        }
        this.positiveButton.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        this.positiveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PrivateVideoPreviewDialog.this.lambda$new$0(view);
            }
        });
        addView(this.positiveButton, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 64.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.titlesLayout = linearLayout;
        addView(linearLayout, LayoutHelper.createFrame(-2, 64, 80));
        final int i = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (i >= textViewArr.length) {
                break;
            }
            textViewArr[i] = new TextView(context);
            this.titles[i].setTextSize(1, 12.0f);
            this.titles[i].setTextColor(-1);
            this.titles[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titles[i].setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.titles[i].setGravity(16);
            this.titles[i].setSingleLine(true);
            this.titlesLayout.addView(this.titles[i], LayoutHelper.createLinear(-2, -1));
            if (i == 0 && this.needScreencast) {
                this.titles[i].setText(LocaleController.getString("VoipPhoneScreen", R.string.VoipPhoneScreen));
            } else if (i == 0 || (i == 1 && this.needScreencast)) {
                this.titles[i].setText(LocaleController.getString("VoipFrontCamera", R.string.VoipFrontCamera));
            } else {
                this.titles[i].setText(LocaleController.getString("VoipBackCamera", R.string.VoipBackCamera));
            }
            this.titles[i].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PrivateVideoPreviewDialog.this.lambda$new$1(i, view);
                }
            });
            i++;
        }
        setAlpha(0.0f);
        setTranslationX(AndroidUtilities.dp(32.0f));
        animate().alpha(1.0f).translationX(0.0f).setDuration(150L).start();
        setWillNotDraw(false);
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            this.textureView.renderer.setMirror(sharedInstance.isFrontFaceCamera());
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().mo2462getEglBaseContext(), new RendererCommon.RendererEvents(this) { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.4
                @Override // org.webrtc.RendererCommon.RendererEvents
                public void onFirstFrameRendered() {
                }

                @Override // org.webrtc.RendererCommon.RendererEvents
                public void onFrameResolutionChanged(int i2, int i3, int i4) {
                }
            });
            sharedInstance.setLocalSink(this.textureView.renderer, false);
        }
        this.viewPager.setCurrentItem(this.needScreencast ? 1 : 0);
        if (z) {
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.micIconView = rLottieImageView;
            rLottieImageView.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f));
            this.micIconView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(48.0f), ColorUtils.setAlphaComponent(-16777216, 76)));
            int i2 = R.raw.voice_mini;
            final RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
            this.micIconView.setAnimation(rLottieDrawable);
            this.micIconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.micEnabled = true;
            rLottieDrawable.setCurrentFrame(69);
            this.micIconView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PrivateVideoPreviewDialog.this.lambda$new$2(rLottieDrawable, view);
                }
            });
            addView(this.micIconView, LayoutHelper.createFrame(48, 48.0f, 83, 24.0f, 0.0f, 0.0f, 136.0f));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (this.isDismissed) {
            return;
        }
        if (this.currentPage == 0 && this.needScreencast) {
            ((Activity) getContext()).startActivityForResult(((MediaProjectionManager) getContext().getSystemService("media_projection")).createScreenCaptureIntent(), 520);
        } else {
            dismiss(false, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i, View view) {
        this.viewPager.setCurrentItem(i, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(RLottieDrawable rLottieDrawable, View view) {
        boolean z = !this.micEnabled;
        this.micEnabled = z;
        if (z) {
            rLottieDrawable.setCurrentFrame(36);
            rLottieDrawable.setCustomEndFrame(69);
        } else {
            rLottieDrawable.setCurrentFrame(69);
            rLottieDrawable.setCustomEndFrame(99);
        }
        rLottieDrawable.start();
    }

    public void setBottomPadding(int i) {
        ((FrameLayout.LayoutParams) this.positiveButton.getLayoutParams()).bottomMargin = AndroidUtilities.dp(64.0f) + i;
        ((FrameLayout.LayoutParams) this.titlesLayout.getLayoutParams()).bottomMargin = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTitlesLayout() {
        TextView[] textViewArr = this.titles;
        int i = this.currentPage;
        TextView textView = textViewArr[i];
        TextView textView2 = i < textViewArr.length + (-1) ? textViewArr[i + 1] : null;
        int measuredWidth = getMeasuredWidth() / 2;
        float left = textView.getLeft() + (textView.getMeasuredWidth() / 2);
        float measuredWidth2 = (getMeasuredWidth() / 2) - left;
        if (textView2 != null) {
            measuredWidth2 -= ((textView2.getLeft() + (textView2.getMeasuredWidth() / 2)) - left) * this.pageOffset;
        }
        int i2 = 0;
        while (true) {
            TextView[] textViewArr2 = this.titles;
            if (i2 >= textViewArr2.length) {
                break;
            }
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
            textViewArr2[i2].setAlpha(f2);
            this.titles[i2].setScaleX(f);
            this.titles[i2].setScaleY(f);
            i2++;
        }
        this.titlesLayout.setTranslationX(measuredWidth2);
        this.positiveButton.invalidate();
        if (this.needScreencast && this.currentPage == 0 && this.pageOffset <= 0.0f) {
            this.textureView.setVisibility(4);
            return;
        }
        this.textureView.setVisibility(0);
        if (this.currentPage + (!this.needScreencast ? 1 : 0) == this.currentTexturePage) {
            this.textureView.setTranslationX((-this.pageOffset) * getMeasuredWidth());
        } else {
            this.textureView.setTranslationX((1.0f - this.pageOffset) * getMeasuredWidth());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.registerStateListener(this);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFinishMoveCameraPage() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (this.currentTexturePage == this.visibleCameraPage || sharedInstance == null) {
            return;
        }
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

    private void saveLastCameraBitmap() {
        if (!this.cameraReady) {
            return;
        }
        try {
            Bitmap bitmap = this.textureView.renderer.getBitmap();
            if (bitmap == null) {
                return;
            }
            Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.textureView.renderer.getMatrix(), true);
            bitmap.recycle();
            int i = 1;
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap, 80, (int) (createBitmap.getHeight() / (createBitmap.getWidth() / 80.0f)), true);
            if (createScaledBitmap == null) {
                return;
            }
            if (createScaledBitmap != createBitmap) {
                createBitmap.recycle();
            }
            Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), createScaledBitmap.getHeight(), createScaledBitmap.getRowBytes());
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(filesDirFixed, "cthumb" + this.visibleCameraPage + ".jpg")));
            ViewPager viewPager = this.viewPager;
            int i2 = this.visibleCameraPage;
            if (this.needScreencast) {
                i = 0;
            }
            View findViewWithTag = viewPager.findViewWithTag(Integer.valueOf(i2 - i));
            if (!(findViewWithTag instanceof ImageView)) {
                return;
            }
            ((ImageView) findViewWithTag).setImageBitmap(createScaledBitmap);
        } catch (Throwable unused) {
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onCameraFirstFrameAvailable() {
        if (!this.cameraReady) {
            this.cameraReady = true;
            this.textureView.animate().alpha(1.0f).setDuration(250L);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateTitlesLayout();
    }

    public void dismiss(boolean z, boolean z2) {
        if (this.isDismissed) {
            return;
        }
        this.isDismissed = true;
        saveLastCameraBitmap();
        onDismiss(z, z2);
        animate().alpha(0.0f).translationX(AndroidUtilities.dp(32.0f)).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (PrivateVideoPreviewDialog.this.getParent() != null) {
                    ((ViewGroup) PrivateVideoPreviewDialog.this.getParent()).removeView(PrivateVideoPreviewDialog.this);
                }
            }
        });
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        boolean z = View.MeasureSpec.getSize(i) > View.MeasureSpec.getSize(i2);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.positiveButton.getLayoutParams();
        if (z) {
            int dp = AndroidUtilities.dp(80.0f);
            marginLayoutParams.leftMargin = dp;
            marginLayoutParams.rightMargin = dp;
        } else {
            int dp2 = AndroidUtilities.dp(16.0f);
            marginLayoutParams.leftMargin = dp2;
            marginLayoutParams.rightMargin = dp2;
        }
        RLottieImageView rLottieImageView = this.micIconView;
        if (rLottieImageView != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) rLottieImageView.getLayoutParams();
            if (z) {
                int dp3 = AndroidUtilities.dp(88.0f);
                marginLayoutParams2.leftMargin = dp3;
                marginLayoutParams2.rightMargin = dp3;
            } else {
                int dp4 = AndroidUtilities.dp(24.0f);
                marginLayoutParams2.leftMargin = dp4;
                marginLayoutParams2.rightMargin = dp4;
            }
        }
        super.onMeasure(i, i2);
        measureChildWithMargins(this.titlesLayout, View.MeasureSpec.makeMeasureSpec(0, 0), 0, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM), 0);
    }

    public int getBackgroundColor() {
        return ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_actionBar"), (int) (getAlpha() * (1.0f - this.outProgress) * 255.0f));
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onCameraSwitch(boolean z) {
        update();
    }

    public void update() {
        if (VoIPService.getSharedInstance() != null) {
            this.textureView.renderer.setMirror(VoIPService.getSharedInstance().isFrontFaceCamera());
        }
    }

    /* loaded from: classes3.dex */
    private class Adapter extends PagerAdapter {
        @Override // androidx.viewpager.widget.PagerAdapter
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        private Adapter() {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return PrivateVideoPreviewDialog.this.titles.length;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.viewpager.widget.PagerAdapter
        /* renamed from: instantiateItem */
        public Object mo1615instantiateItem(ViewGroup viewGroup, int i) {
            ImageView imageView;
            int i2 = 1;
            if (PrivateVideoPreviewDialog.this.needScreencast && i == 0) {
                FrameLayout frameLayout = new FrameLayout(PrivateVideoPreviewDialog.this.getContext());
                frameLayout.setBackground(new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
                ImageView imageView2 = new ImageView(PrivateVideoPreviewDialog.this.getContext());
                imageView2.setScaleType(ImageView.ScaleType.CENTER);
                imageView2.setImageResource(R.drawable.screencast_big);
                frameLayout.addView(imageView2, LayoutHelper.createFrame(82, 82.0f, 17, 0.0f, 0.0f, 0.0f, 60.0f));
                TextView textView = new TextView(PrivateVideoPreviewDialog.this.getContext());
                textView.setText(LocaleController.getString("VoipVideoPrivateScreenSharing", R.string.VoipVideoPrivateScreenSharing));
                textView.setGravity(17);
                textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                textView.setTextColor(-1);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 17, 21.0f, 28.0f, 21.0f, 0.0f));
                imageView = frameLayout;
            } else {
                ImageView imageView3 = new ImageView(PrivateVideoPreviewDialog.this.getContext());
                imageView3.setTag(Integer.valueOf(i));
                Bitmap bitmap = null;
                try {
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    StringBuilder sb = new StringBuilder();
                    sb.append("cthumb");
                    if (i != 0 && (i != 1 || !PrivateVideoPreviewDialog.this.needScreencast)) {
                        i2 = 2;
                    }
                    sb.append(i2);
                    sb.append(".jpg");
                    bitmap = BitmapFactory.decodeFile(new File(filesDirFixed, sb.toString()).getAbsolutePath());
                } catch (Throwable unused) {
                }
                if (bitmap != null) {
                    imageView3.setImageBitmap(bitmap);
                } else {
                    imageView3.setImageResource(R.drawable.icplaceholder);
                }
                imageView3.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView = imageView3;
            }
            if (imageView.getParent() != null) {
                ((ViewGroup) imageView.getParent()).removeView(imageView);
            }
            viewGroup.addView(imageView, 0);
            return imageView;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }
}
