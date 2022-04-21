package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
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
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.webrtc.RendererCommon;

public abstract class PrivateVideoPreviewDialog extends FrameLayout implements VoIPService.StateListener {
    private boolean cameraReady;
    /* access modifiers changed from: private */
    public int currentPage;
    /* access modifiers changed from: private */
    public int currentTexturePage = 1;
    private boolean isDismissed;
    public boolean micEnabled;
    private RLottieImageView micIconView;
    /* access modifiers changed from: private */
    public boolean needScreencast;
    private float outProgress;
    /* access modifiers changed from: private */
    public float pageOffset;
    private TextView positiveButton;
    private VoIPTextureView textureView;
    /* access modifiers changed from: private */
    public TextView[] titles;
    private LinearLayout titlesLayout;
    private ViewPager viewPager;
    private int visibleCameraPage = 1;

    public /* synthetic */ void onAudioSettingsChanged() {
        VoIPService.StateListener.CC.$default$onAudioSettingsChanged(this);
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

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PrivateVideoPreviewDialog(Context context, boolean mic, boolean screencast) {
        super(context);
        Context context2 = context;
        boolean z = screencast;
        this.needScreencast = z;
        this.titles = new TextView[(z ? 3 : 2)];
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

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int unused = PrivateVideoPreviewDialog.this.currentPage = position;
                float unused2 = PrivateVideoPreviewDialog.this.pageOffset = positionOffset;
                PrivateVideoPreviewDialog.this.updateTitlesLayout();
            }

            public void onPageSelected(int i) {
                if (this.scrollState == 0) {
                    if (i <= PrivateVideoPreviewDialog.this.needScreencast) {
                        int unused = PrivateVideoPreviewDialog.this.currentTexturePage = 1;
                    } else {
                        int unused2 = PrivateVideoPreviewDialog.this.currentTexturePage = 2;
                    }
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                } else if (i <= PrivateVideoPreviewDialog.this.needScreencast) {
                    this.willSetPage = 1;
                } else {
                    this.willSetPage = 2;
                }
            }

            public void onPageScrollStateChanged(int state) {
                this.scrollState = state;
                if (state == 0) {
                    int unused = PrivateVideoPreviewDialog.this.currentTexturePage = this.willSetPage;
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                }
            }
        });
        VoIPTextureView voIPTextureView = new VoIPTextureView(context2, false, false);
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        this.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        this.textureView.clipToTexture = true;
        this.textureView.renderer.setAlpha(0.0f);
        this.textureView.renderer.setRotateTextureWithScreen(true);
        this.textureView.renderer.setUseCameraRotation(true);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        ActionBar actionBar = new ActionBar(context2);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setBackgroundColor(0);
        actionBar.setItemsColor(Theme.getColor("voipgroup_actionBarItems"), false);
        actionBar.setOccupyStatusBar(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivateVideoPreviewDialog.this.dismiss(false, false);
                }
            }
        });
        addView(actionBar);
        AnonymousClass3 r7 = new TextView(getContext()) {
            private Paint[] gradientPaint;

            {
                this.gradientPaint = new Paint[PrivateVideoPreviewDialog.this.titles.length];
                int a = 0;
                while (true) {
                    Paint[] paintArr = this.gradientPaint;
                    if (a < paintArr.length) {
                        paintArr[a] = new Paint(1);
                        a++;
                    } else {
                        return;
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                int color3;
                int color2;
                int color1;
                Shader gradient;
                super.onSizeChanged(w, h, oldw, oldh);
                for (int a = 0; a < this.gradientPaint.length; a++) {
                    if (a == 0 && PrivateVideoPreviewDialog.this.needScreencast) {
                        color1 = -8919716;
                        color2 = -11089922;
                        color3 = 0;
                    } else if (a == 0 || (a == 1 && PrivateVideoPreviewDialog.this.needScreencast)) {
                        color1 = -11033346;
                        color2 = -9015575;
                        color3 = 0;
                    } else {
                        color1 = -9015575;
                        color2 = -1026983;
                        color3 = -1792170;
                    }
                    if (color3 != 0) {
                        gradient = new LinearGradient(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, new int[]{color1, color2, color3}, (float[]) null, Shader.TileMode.CLAMP);
                    } else {
                        gradient = new LinearGradient(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, new int[]{color1, color2}, (float[]) null, Shader.TileMode.CLAMP);
                    }
                    this.gradientPaint[a].setShader(gradient);
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage].setAlpha(255);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage]);
                if (PrivateVideoPreviewDialog.this.pageOffset > 0.0f) {
                    int access$100 = PrivateVideoPreviewDialog.this.currentPage + 1;
                    Paint[] paintArr = this.gradientPaint;
                    if (access$100 < paintArr.length) {
                        paintArr[PrivateVideoPreviewDialog.this.currentPage + 1].setAlpha((int) (PrivateVideoPreviewDialog.this.pageOffset * 255.0f));
                        canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage + 1]);
                    }
                }
                super.onDraw(canvas);
            }
        };
        this.positiveButton = r7;
        r7.setMinWidth(AndroidUtilities.dp(64.0f));
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
        this.positiveButton.setOnClickListener(new PrivateVideoPreviewDialog$$ExternalSyntheticLambda0(this));
        addView(this.positiveButton, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 64.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.titlesLayout = linearLayout;
        addView(linearLayout, LayoutHelper.createFrame(-2, 64, 80));
        int a = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (a >= textViewArr.length) {
                break;
            }
            textViewArr[a] = new TextView(context2);
            this.titles[a].setTextSize(1, 12.0f);
            this.titles[a].setTextColor(-1);
            this.titles[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titles[a].setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.titles[a].setGravity(16);
            this.titles[a].setSingleLine(true);
            this.titlesLayout.addView(this.titles[a], LayoutHelper.createLinear(-2, -1));
            if (a == 0 && this.needScreencast) {
                this.titles[a].setText(LocaleController.getString("VoipPhoneScreen", NUM));
            } else if (a == 0 || (a == 1 && this.needScreencast)) {
                this.titles[a].setText(LocaleController.getString("VoipFrontCamera", NUM));
            } else {
                this.titles[a].setText(LocaleController.getString("VoipBackCamera", NUM));
            }
            this.titles[a].setOnClickListener(new PrivateVideoPreviewDialog$$ExternalSyntheticLambda1(this, a));
            a++;
        }
        setAlpha(0.0f);
        setTranslationX((float) AndroidUtilities.dp(32.0f));
        animate().alpha(1.0f).translationX(0.0f).setDuration(150).start();
        setWillNotDraw(false);
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            this.textureView.renderer.setMirror(service.isFrontFaceCamera());
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
                public void onFirstFrameRendered() {
                }

                public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
                }
            });
            service.setLocalSink(this.textureView.renderer, false);
        }
        this.viewPager.setCurrentItem(this.needScreencast ? 1 : 0);
        if (mic) {
            RLottieImageView rLottieImageView = new RLottieImageView(context2);
            this.micIconView = rLottieImageView;
            rLottieImageView.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f));
            this.micIconView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(48.0f), ColorUtils.setAlphaComponent(-16777216, 76)));
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
            this.micIconView.setAnimation(rLottieDrawable);
            this.micIconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.micEnabled = true;
            rLottieDrawable.setCurrentFrame(69);
            this.micIconView.setOnClickListener(new PrivateVideoPreviewDialog$$ExternalSyntheticLambda2(this, rLottieDrawable));
            addView(this.micIconView, LayoutHelper.createFrame(48, 48.0f, 83, 24.0f, 0.0f, 0.0f, 136.0f));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-PrivateVideoPreviewDialog  reason: not valid java name */
    public /* synthetic */ void m4568x9var_a97(View view) {
        if (!this.isDismissed) {
            if (this.currentPage != 0 || !this.needScreencast) {
                dismiss(false, true);
            } else {
                ((Activity) getContext()).startActivityForResult(((MediaProjectionManager) getContext().getSystemService("media_projection")).createScreenCaptureIntent(), 520);
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-PrivateVideoPreviewDialog  reason: not valid java name */
    public /* synthetic */ void m4569x1aab5758(int num, View view) {
        this.viewPager.setCurrentItem(num, true);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-PrivateVideoPreviewDialog  reason: not valid java name */
    public /* synthetic */ void m4570x2b612419(RLottieDrawable micIcon, View v) {
        boolean z = !this.micEnabled;
        this.micEnabled = z;
        if (z) {
            micIcon.setCurrentFrame(36);
            micIcon.setCustomEndFrame(69);
        } else {
            micIcon.setCurrentFrame(69);
            micIcon.setCustomEndFrame(99);
        }
        micIcon.start();
    }

    public void setBottomPadding(int padding) {
        ((FrameLayout.LayoutParams) this.positiveButton.getLayoutParams()).bottomMargin = AndroidUtilities.dp(64.0f) + padding;
        ((FrameLayout.LayoutParams) this.titlesLayout.getLayoutParams()).bottomMargin = padding;
    }

    /* access modifiers changed from: private */
    public void updateTitlesLayout() {
        float alpha;
        float scale;
        View[] viewArr = this.titles;
        int i = this.currentPage;
        View current = viewArr[i];
        View next = i < viewArr.length + -1 ? viewArr[i + 1] : null;
        float measuredWidth = (float) (getMeasuredWidth() / 2);
        float currentCx = (float) (current.getLeft() + (current.getMeasuredWidth() / 2));
        float tx = ((float) (getMeasuredWidth() / 2)) - currentCx;
        if (next != null) {
            tx -= (((float) (next.getLeft() + (next.getMeasuredWidth() / 2))) - currentCx) * this.pageOffset;
        }
        int a = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (a >= textViewArr.length) {
                break;
            }
            int i2 = this.currentPage;
            if (a < i2 || a > i2 + 1) {
                alpha = 0.7f;
                scale = 0.9f;
            } else if (a == i2) {
                float f = this.pageOffset;
                alpha = 1.0f - (0.3f * f);
                scale = 1.0f - (f * 0.1f);
            } else {
                float f2 = this.pageOffset;
                alpha = (0.3f * f2) + 0.7f;
                scale = (f2 * 0.1f) + 0.9f;
            }
            textViewArr[a].setAlpha(alpha);
            this.titles[a].setScaleX(scale);
            this.titles[a].setScaleY(scale);
            a++;
        }
        this.titlesLayout.setTranslationX(tx);
        this.positiveButton.invalidate();
        if (!this.needScreencast || this.currentPage != 0 || this.pageOffset > 0.0f) {
            this.textureView.setVisibility(0);
            if (this.currentPage + (this.needScreencast ^ true ? 1 : 0) == this.currentTexturePage) {
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
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.registerStateListener(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
    }

    /* access modifiers changed from: private */
    public void onFinishMoveCameraPage() {
        VoIPService service = VoIPService.getSharedInstance();
        if (this.currentTexturePage != this.visibleCameraPage && service != null) {
            boolean currentFrontface = service.isFrontFaceCamera();
            int i = this.currentTexturePage;
            if ((i == 1 && !currentFrontface) || (i == 2 && currentFrontface)) {
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
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.textureView.renderer.getMatrix(), true);
                    bitmap.recycle();
                    Bitmap bitmap2 = newBitmap;
                    int i = 1;
                    Bitmap lastBitmap = Bitmap.createScaledBitmap(bitmap2, 80, (int) (((float) bitmap2.getHeight()) / (((float) bitmap2.getWidth()) / 80.0f)), true);
                    if (lastBitmap != null) {
                        if (lastBitmap != bitmap2) {
                            bitmap2.recycle();
                        }
                        Utilities.blurBitmap(lastBitmap, 7, 1, lastBitmap.getWidth(), lastBitmap.getHeight(), lastBitmap.getRowBytes());
                        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                        lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(filesDirFixed, "cthumb" + this.visibleCameraPage + ".jpg")));
                        ViewPager viewPager2 = this.viewPager;
                        int i2 = this.visibleCameraPage;
                        if (this.needScreencast) {
                            i = 0;
                        }
                        View view = viewPager2.findViewWithTag(Integer.valueOf(i2 - i));
                        if (view instanceof ImageView) {
                            ((ImageView) view).setImageBitmap(lastBitmap);
                        }
                    }
                }
            } catch (Throwable th) {
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
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateTitlesLayout();
    }

    public void dismiss(boolean screencast, boolean apply) {
        if (!this.isDismissed) {
            this.isDismissed = true;
            saveLastCameraBitmap();
            onDismiss(screencast, apply);
            animate().alpha(0.0f).translationX((float) AndroidUtilities.dp(32.0f)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (PrivateVideoPreviewDialog.this.getParent() != null) {
                        ((ViewGroup) PrivateVideoPreviewDialog.this.getParent()).removeView(PrivateVideoPreviewDialog.this);
                    }
                }
            });
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDismiss(boolean screencast, boolean apply) {
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean isLandscape = View.MeasureSpec.getSize(widthMeasureSpec) > View.MeasureSpec.getSize(heightMeasureSpec);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.positiveButton.getLayoutParams();
        if (isLandscape) {
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
            if (isLandscape) {
                int dp3 = AndroidUtilities.dp(88.0f);
                marginLayoutParams2.leftMargin = dp3;
                marginLayoutParams2.rightMargin = dp3;
            } else {
                int dp4 = AndroidUtilities.dp(24.0f);
                marginLayoutParams2.leftMargin = dp4;
                marginLayoutParams2.rightMargin = dp4;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

    public void onCameraSwitch(boolean isFrontFace) {
        update();
    }

    public void update() {
        if (VoIPService.getSharedInstance() != null) {
            this.textureView.renderer.setMirror(VoIPService.getSharedInstance().isFrontFaceCamera());
        }
    }

    private class Adapter extends PagerAdapter {
        private Adapter() {
        }

        public int getCount() {
            return PrivateVideoPreviewDialog.this.titles.length;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: android.widget.ImageView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: android.widget.ImageView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: android.widget.FrameLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: android.widget.ImageView} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Object instantiateItem(android.view.ViewGroup r13, int r14) {
            /*
                r12 = this;
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r0 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                boolean r0 = r0.needScreencast
                r1 = 1
                if (r0 == 0) goto L_0x00a1
                if (r14 != 0) goto L_0x00a1
                android.widget.FrameLayout r0 = new android.widget.FrameLayout
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r2 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r2 = r2.getContext()
                r0.<init>(r2)
                org.telegram.ui.Components.MotionBackgroundDrawable r8 = new org.telegram.ui.Components.MotionBackgroundDrawable
                r3 = -14602694(0xfffffffffvar_e3a, float:-2.1424573E38)
                r4 = -13935795(0xffffffffff2b5b4d, float:-2.2777205E38)
                r5 = -14395293(0xfffffffffvar_, float:-2.1845232E38)
                r6 = -14203560(0xfffffffffvar_, float:-2.2234113E38)
                r7 = 1
                r2 = r8
                r2.<init>(r3, r4, r5, r6, r7)
                r0.setBackground(r8)
                r2 = r0
                android.widget.ImageView r3 = new android.widget.ImageView
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r4 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r4 = r4.getContext()
                r3.<init>(r4)
                android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
                r3.setScaleType(r4)
                r4 = 2131166098(0x7var_, float:1.7946432E38)
                r3.setImageResource(r4)
                r5 = 82
                r6 = 1118044160(0x42a40000, float:82.0)
                r7 = 17
                r8 = 0
                r9 = 0
                r10 = 0
                r11 = 1114636288(0x42700000, float:60.0)
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
                r0.addView(r3, r4)
                android.widget.TextView r4 = new android.widget.TextView
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r5 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r5 = r5.getContext()
                r4.<init>(r5)
                r5 = 2131628973(0x7f0e13ad, float:1.8885254E38)
                java.lang.String r6 = "VoipVideoPrivateScreenSharing"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                r5 = 17
                r4.setGravity(r5)
                r5 = 1073741824(0x40000000, float:2.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                r6 = 1065353216(0x3var_, float:1.0)
                r4.setLineSpacing(r5, r6)
                r5 = -1
                r4.setTextColor(r5)
                r5 = 1097859072(0x41700000, float:15.0)
                r4.setTextSize(r1, r5)
                java.lang.String r1 = "fonts/rmedium.ttf"
                android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
                r4.setTypeface(r1)
                r5 = -1
                r6 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r8 = 1101529088(0x41a80000, float:21.0)
                r9 = 1105199104(0x41e00000, float:28.0)
                r10 = 1101529088(0x41a80000, float:21.0)
                r11 = 0
                android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
                r0.addView(r4, r1)
                goto L_0x0100
            L_0x00a1:
                android.widget.ImageView r0 = new android.widget.ImageView
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r2 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this
                android.content.Context r2 = r2.getContext()
                r0.<init>(r2)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
                r0.setTag(r2)
                r2 = 0
                java.io.File r3 = new java.io.File     // Catch:{ all -> 0x00ec }
                java.io.File r4 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x00ec }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ec }
                r5.<init>()     // Catch:{ all -> 0x00ec }
                java.lang.String r6 = "cthumb"
                r5.append(r6)     // Catch:{ all -> 0x00ec }
                if (r14 == 0) goto L_0x00d2
                if (r14 != r1) goto L_0x00d1
                org.telegram.ui.Components.voip.PrivateVideoPreviewDialog r6 = org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.this     // Catch:{ all -> 0x00ec }
                boolean r6 = r6.needScreencast     // Catch:{ all -> 0x00ec }
                if (r6 == 0) goto L_0x00d1
                goto L_0x00d2
            L_0x00d1:
                r1 = 2
            L_0x00d2:
                r5.append(r1)     // Catch:{ all -> 0x00ec }
                java.lang.String r1 = ".jpg"
                r5.append(r1)     // Catch:{ all -> 0x00ec }
                java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x00ec }
                r3.<init>(r4, r1)     // Catch:{ all -> 0x00ec }
                r1 = r3
                java.lang.String r3 = r1.getAbsolutePath()     // Catch:{ all -> 0x00ec }
                android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFile(r3)     // Catch:{ all -> 0x00ec }
                r2 = r3
                goto L_0x00ed
            L_0x00ec:
                r1 = move-exception
            L_0x00ed:
                if (r2 == 0) goto L_0x00f3
                r0.setImageBitmap(r2)
                goto L_0x00f9
            L_0x00f3:
                r1 = 2131165564(0x7var_c, float:1.7945349E38)
                r0.setImageResource(r1)
            L_0x00f9:
                android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.FIT_XY
                r0.setScaleType(r1)
                r1 = r0
                r2 = r1
            L_0x0100:
                android.view.ViewParent r0 = r2.getParent()
                if (r0 == 0) goto L_0x010f
                android.view.ViewParent r0 = r2.getParent()
                android.view.ViewGroup r0 = (android.view.ViewGroup) r0
                r0.removeView(r2)
            L_0x010f:
                r0 = 0
                r13.addView(r2, r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.Adapter.instantiateItem(android.view.ViewGroup, int):java.lang.Object");
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}
