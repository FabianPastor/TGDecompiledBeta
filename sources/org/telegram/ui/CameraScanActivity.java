package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.qrcode.QRCodeReader;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.Size;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoAlbumPickerActivity;

@TargetApi(18)
public class CameraScanActivity extends BaseFragment {
    private float averageProcessTime = 0.0f;
    /* access modifiers changed from: private */
    public float backShadowAlpha = 0.5f;
    private HandlerThread backgroundHandlerThread = new HandlerThread("ScanCamera");
    private RectF bounds = new RectF();
    /* access modifiers changed from: private */
    public CameraView cameraView;
    /* access modifiers changed from: private */
    public Paint cornerPaint = new Paint(1);
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public CameraScanActivityDelegate delegate;
    /* access modifiers changed from: private */
    public TextView descriptionText;
    /* access modifiers changed from: private */
    public AnimatorSet flashAnimator;
    /* access modifiers changed from: private */
    public ImageView flashButton;
    private RectF fromBounds = new RectF();
    /* access modifiers changed from: private */
    public ImageView galleryButton;
    /* access modifiers changed from: private */
    public Handler handler;
    private long lastBoundsUpdate = 0;
    private ValueAnimator navigationBarColorAnimator;
    /* access modifiers changed from: private */
    public boolean needGalleryButton;
    /* access modifiers changed from: private */
    public Paint paint = new Paint();
    /* access modifiers changed from: private */
    public Path path = new Path();
    private long processTimesCount = 0;
    private SpringAnimation qrAppearing = null;
    /* access modifiers changed from: private */
    public float qrAppearingValue = 0.0f;
    private boolean qrLoading = false;
    private QRCodeReader qrReader;
    private int recognizeFailed = 0;
    private int recognizeIndex = 0;
    /* access modifiers changed from: private */
    public boolean recognized;
    private ValueAnimator recognizedAnimator;
    /* access modifiers changed from: private */
    public TextView recognizedMrzView;
    private long recognizedStart;
    private float recognizedT = 0.0f;
    private String recognizedText;
    private Runnable requestShot = new Runnable() {
        public void run() {
            if (CameraScanActivity.this.cameraView != null && !CameraScanActivity.this.recognized && CameraScanActivity.this.cameraView.getCameraSession() != null) {
                CameraScanActivity.this.handler.post(new CameraScanActivity$7$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            if (CameraScanActivity.this.cameraView != null) {
                CameraScanActivity cameraScanActivity = CameraScanActivity.this;
                cameraScanActivity.processShot(cameraScanActivity.cameraView.getTextureView().getBitmap());
            }
        }
    };
    private int sps;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private BarcodeDetector visionQrReader;
    /* access modifiers changed from: private */
    public boolean wasLightNavigationBar;
    /* access modifiers changed from: private */
    public int wasNavigationBarColor;

    public interface CameraScanActivityDelegate {

        /* renamed from: org.telegram.ui.CameraScanActivity$CameraScanActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didFindMrzInfo(CameraScanActivityDelegate cameraScanActivityDelegate, MrzRecognizer.Result result) {
            }

            public static void $default$didFindQr(CameraScanActivityDelegate cameraScanActivityDelegate, String str) {
            }

            public static boolean $default$processQr(CameraScanActivityDelegate cameraScanActivityDelegate, String str, Runnable runnable) {
                return false;
            }
        }

        void didFindMrzInfo(MrzRecognizer.Result result);

        void didFindQr(String str);

        boolean processQr(String str, Runnable runnable);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public static ActionBarLayout[] showAsSheet(BaseFragment baseFragment, boolean z, int i, CameraScanActivityDelegate cameraScanActivityDelegate) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayoutArr = {new ActionBarLayout(baseFragment.getParentActivity())};
        new BottomSheet(baseFragment.getParentActivity(), false, actionBarLayoutArr, i, z, cameraScanActivityDelegate) {
            CameraScanActivity fragment;
            final /* synthetic */ ActionBarLayout[] val$actionBarLayout;
            final /* synthetic */ CameraScanActivityDelegate val$cameraDelegate;
            final /* synthetic */ boolean val$gallery;
            final /* synthetic */ int val$type;

            /* access modifiers changed from: protected */
            public boolean canDismissWithSwipe() {
                return false;
            }

            {
                this.val$actionBarLayout = r4;
                this.val$type = r5;
                this.val$gallery = r6;
                this.val$cameraDelegate = r7;
                r4[0].init(new ArrayList());
                AnonymousClass1 r3 = new CameraScanActivity(r5) {
                    public void finishFragment() {
                        AnonymousClass1.this.dismiss();
                    }

                    public void removeSelfFromStack() {
                        AnonymousClass1.this.dismiss();
                    }
                };
                this.fragment = r3;
                boolean unused = r3.needGalleryButton = r6;
                r4[0].addFragmentToStack(this.fragment);
                r4[0].showLastFragment();
                ActionBarLayout actionBarLayout = r4[0];
                int i = this.backgroundPaddingLeft;
                actionBarLayout.setPadding(i, 0, i, 0);
                this.fragment.setDelegate(r7);
                this.containerView = r4[0];
                setApplyBottomPadding(false);
                setApplyBottomPadding(false);
                setOnDismissListener(new CameraScanActivity$1$$ExternalSyntheticLambda0(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$new$0(DialogInterface dialogInterface) {
                this.fragment.onFragmentDestroy();
            }

            public void onBackPressed() {
                ActionBarLayout[] actionBarLayoutArr = this.val$actionBarLayout;
                if (actionBarLayoutArr[0] == null || actionBarLayoutArr[0].fragmentsStack.size() <= 1) {
                    super.onBackPressed();
                } else {
                    this.val$actionBarLayout[0].onBackPressed();
                }
            }

            public void dismiss() {
                if (Build.VERSION.SDK_INT >= 26) {
                    Window window = this.fragment.getParentActivity().getWindow();
                    CameraScanActivity cameraScanActivity = this.fragment;
                    cameraScanActivity.updateNavigationBarColor(cameraScanActivity.wasNavigationBarColor);
                    AndroidUtilities.setLightNavigationBar(window, this.fragment.wasLightNavigationBar);
                }
                super.dismiss();
                this.val$actionBarLayout[0] = null;
            }
        }.show();
        return actionBarLayoutArr;
    }

    public CameraScanActivity(int i) {
        CameraController.getInstance().initCamera((Runnable) null);
        this.currentType = i;
        if (isQr()) {
            this.qrReader = new QRCodeReader();
            this.visionQrReader = new BarcodeDetector.Builder(ApplicationLoader.applicationContext).setBarcodeFormats(256).build();
        }
        int devicePerformanceClass = SharedConfig.getDevicePerformanceClass();
        if (devicePerformanceClass == 0) {
            this.sps = 8;
        } else if (devicePerformanceClass != 1) {
            this.sps = 40;
        } else {
            this.sps = 24;
        }
    }

    public void onResume() {
        Window window = getParentActivity().getWindow();
        if (Build.VERSION.SDK_INT >= 26) {
            this.wasNavigationBarColor = window.getNavigationBarColor();
            this.wasLightNavigationBar = AndroidUtilities.getLightNavigationBar(window);
            AndroidUtilities.setLightNavigationBar(window, false);
            updateNavigationBarColor(-16777216);
        }
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void updateNavigationBarColor(int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            ValueAnimator valueAnimator = this.navigationBarColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            Window window = getParentActivity().getWindow();
            ValueAnimator ofArgb = ValueAnimator.ofArgb(new int[]{window.getNavigationBarColor(), i});
            this.navigationBarColorAnimator = ofArgb;
            ofArgb.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda0(window));
            this.navigationBarColorAnimator.setDuration(200);
            this.navigationBarColorAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.navigationBarColorAnimator.start();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        destroy(false, (Runnable) null);
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(-1);
        }
        BarcodeDetector barcodeDetector = this.visionQrReader;
        if (barcodeDetector != null) {
            barcodeDetector.release();
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet() && !isQr()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    CameraScanActivity.this.finishFragment();
                }
            }
        });
        this.paint.setColor(NUM);
        this.cornerPaint.setColor(-1);
        this.cornerPaint.setStyle(Paint.Style.FILL);
        AnonymousClass3 r2 = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                CameraScanActivity.this.actionBar.measure(i, i2);
                if (CameraScanActivity.this.currentType == 0) {
                    CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.704f), NUM));
                } else {
                    CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                    CameraScanActivity.this.recognizedMrzView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                    if (CameraScanActivity.this.galleryButton != null) {
                        CameraScanActivity.this.galleryButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                    }
                    CameraScanActivity.this.flashButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                }
                CameraScanActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(72.0f), NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                CameraScanActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.9f), NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                setMeasuredDimension(size, size2);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                int i6;
                int i7;
                int i8 = i3 - i;
                int i9 = i4 - i2;
                if (CameraScanActivity.this.currentType == 0) {
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight() + 0);
                    int i10 = (int) (((float) i9) * 0.65f);
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), i10, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + i10);
                    CameraScanActivity.this.recognizedMrzView.setTextSize(0, (float) (CameraScanActivity.this.cameraView.getMeasuredHeight() / 22));
                    CameraScanActivity.this.recognizedMrzView.setPadding(0, 0, 0, CameraScanActivity.this.cameraView.getMeasuredHeight() / 15);
                } else {
                    CameraScanActivity.this.actionBar.layout(0, 0, CameraScanActivity.this.actionBar.getMeasuredWidth(), CameraScanActivity.this.actionBar.getMeasuredHeight());
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight());
                    int min = (int) (((float) Math.min(CameraScanActivity.this.cameraView.getWidth(), CameraScanActivity.this.cameraView.getHeight())) / 1.5f);
                    if (CameraScanActivity.this.currentType == 1) {
                        i6 = ((CameraScanActivity.this.cameraView.getMeasuredHeight() - min) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight();
                        i5 = AndroidUtilities.dp(30.0f);
                    } else {
                        i6 = ((CameraScanActivity.this.cameraView.getMeasuredHeight() - min) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight();
                        i5 = AndroidUtilities.dp(64.0f);
                    }
                    int i11 = i6 - i5;
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), i11, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + i11);
                    CameraScanActivity.this.recognizedMrzView.layout(0, getMeasuredHeight() - CameraScanActivity.this.recognizedMrzView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                    if (CameraScanActivity.this.needGalleryButton) {
                        i7 = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) + AndroidUtilities.dp(35.0f);
                    } else {
                        i7 = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - (CameraScanActivity.this.flashButton.getMeasuredWidth() / 2);
                    }
                    int measuredHeight = ((CameraScanActivity.this.cameraView.getMeasuredHeight() - min) / 2) + min + AndroidUtilities.dp(80.0f);
                    CameraScanActivity.this.flashButton.layout(i7, measuredHeight, CameraScanActivity.this.flashButton.getMeasuredWidth() + i7, CameraScanActivity.this.flashButton.getMeasuredHeight() + measuredHeight);
                    if (CameraScanActivity.this.galleryButton != null) {
                        int measuredWidth = ((CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - AndroidUtilities.dp(35.0f)) - CameraScanActivity.this.galleryButton.getMeasuredWidth();
                        CameraScanActivity.this.galleryButton.layout(measuredWidth, measuredHeight, CameraScanActivity.this.galleryButton.getMeasuredWidth() + measuredWidth, CameraScanActivity.this.galleryButton.getMeasuredHeight() + measuredHeight);
                    }
                }
                float f = (float) i9;
                int i12 = (int) (0.74f * f);
                float f2 = (float) i8;
                int i13 = (int) (0.05f * f2);
                CameraScanActivity.this.descriptionText.layout(i13, i12, CameraScanActivity.this.descriptionText.getMeasuredWidth() + i13, CameraScanActivity.this.descriptionText.getMeasuredHeight() + i12);
                if (!CameraScanActivity.this.recognized) {
                    int min2 = (int) (((float) Math.min(i8, i9)) / 1.5f);
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set((((float) (i8 - min2)) / 2.0f) / f2, (((float) (i9 - min2)) / 2.0f) / f, (((float) (i8 + min2)) / 2.0f) / f2, (((float) (i9 + min2)) / 2.0f) / f);
                    CameraScanActivity.this.updateBounds(rectF);
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                Canvas canvas2 = canvas;
                boolean drawChild = super.drawChild(canvas, view, j);
                if (!CameraScanActivity.this.isQr() || view != CameraScanActivity.this.cameraView) {
                    return drawChild;
                }
                RectF access$1700 = CameraScanActivity.this.getBounds();
                int height = (int) (((float) view.getHeight()) * access$1700.centerY());
                int width = (int) (((float) ((int) (((float) view.getWidth()) * access$1700.width()))) * ((CameraScanActivity.this.qrAppearingValue * 0.5f) + 0.5f));
                int height2 = (int) (((float) ((int) (((float) view.getHeight()) * access$1700.height()))) * ((CameraScanActivity.this.qrAppearingValue * 0.5f) + 0.5f));
                int width2 = ((int) (((float) view.getWidth()) * access$1700.centerX())) - (width / 2);
                int i = height - (height2 / 2);
                CameraScanActivity.this.paint.setAlpha((int) (CameraScanActivity.this.backShadowAlpha * 255.0f * Math.min(1.0f, CameraScanActivity.this.qrAppearingValue)));
                float f = (float) i;
                float f2 = f;
                canvas.drawRect(0.0f, 0.0f, (float) view.getMeasuredWidth(), f, CameraScanActivity.this.paint);
                int i2 = i + height2;
                float f3 = (float) i2;
                float measuredWidth = (float) view.getMeasuredWidth();
                float measuredHeight = (float) view.getMeasuredHeight();
                float f4 = measuredHeight;
                float f5 = f3;
                int i3 = i2;
                canvas.drawRect(0.0f, f3, measuredWidth, f4, CameraScanActivity.this.paint);
                float f6 = (float) width2;
                float f7 = f2;
                float f8 = f6;
                canvas.drawRect(0.0f, f7, f6, f5, CameraScanActivity.this.paint);
                int i4 = width2 + width;
                float f9 = (float) i4;
                float var_ = f9;
                int i5 = i4;
                canvas.drawRect(f9, f7, (float) view.getMeasuredWidth(), f5, CameraScanActivity.this.paint);
                int lerp = AndroidUtilities.lerp(0, AndroidUtilities.dp(4.0f), (float) Math.pow((double) CameraScanActivity.this.qrAppearingValue, 0.125d));
                int i6 = lerp / 2;
                int lerp2 = AndroidUtilities.lerp(Math.min(width, height2), AndroidUtilities.dp(20.0f), Math.min(1.2f, (float) Math.pow((double) CameraScanActivity.this.qrAppearingValue, 1.7999999523162842d)));
                CameraScanActivity.this.cornerPaint.setAlpha((int) (Math.min(1.0f, CameraScanActivity.this.qrAppearingValue) * 255.0f));
                CameraScanActivity.this.path.reset();
                int i7 = i + lerp2;
                CameraScanActivity.this.path.arcTo(aroundPoint(width2, i7, i6), 0.0f, 180.0f);
                float var_ = ((float) lerp) * 1.5f;
                int i8 = (int) (f8 + var_);
                int i9 = (int) (f2 + var_);
                int i10 = lerp * 2;
                boolean z = drawChild;
                CameraScanActivity.this.path.arcTo(aroundPoint(i8, i9, i10), 180.0f, 90.0f);
                int i11 = width2 + lerp2;
                int i12 = i11;
                CameraScanActivity.this.path.arcTo(aroundPoint(i11, i, i6), 270.0f, 180.0f);
                CameraScanActivity.this.path.lineTo((float) (width2 + i6), (float) (i + i6));
                CameraScanActivity.this.path.arcTo(aroundPoint(i8, i9, lerp), 270.0f, -90.0f);
                CameraScanActivity.this.path.close();
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                int i13 = i5;
                CameraScanActivity.this.path.arcTo(aroundPoint(i13, i7, i6), 180.0f, -180.0f);
                int i14 = (int) (var_ - var_);
                CameraScanActivity.this.path.arcTo(aroundPoint(i14, i9, i10), 0.0f, -90.0f);
                int i15 = i13 - lerp2;
                CameraScanActivity.this.path.arcTo(aroundPoint(i15, i, i6), 270.0f, -180.0f);
                CameraScanActivity.this.path.arcTo(aroundPoint(i14, i9, lerp), 270.0f, 90.0f);
                CameraScanActivity.this.path.close();
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                int i16 = i3;
                int i17 = i16 - lerp2;
                CameraScanActivity.this.path.arcTo(aroundPoint(width2, i17, i6), 0.0f, -180.0f);
                int i18 = (int) (f5 - var_);
                int i19 = i8;
                CameraScanActivity.this.path.arcTo(aroundPoint(i19, i18, i10), 180.0f, -90.0f);
                CameraScanActivity.this.path.arcTo(aroundPoint(i12, i16, i6), 90.0f, -180.0f);
                CameraScanActivity.this.path.arcTo(aroundPoint(i19, i18, lerp), 90.0f, 90.0f);
                CameraScanActivity.this.path.close();
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.arcTo(aroundPoint(i13, i17, i6), 180.0f, 180.0f);
                CameraScanActivity.this.path.arcTo(aroundPoint(i14, i18, i10), 0.0f, 90.0f);
                CameraScanActivity.this.path.arcTo(aroundPoint(i15, i16, i6), 90.0f, 180.0f);
                CameraScanActivity.this.path.arcTo(aroundPoint(i14, i18, lerp), 90.0f, -90.0f);
                CameraScanActivity.this.path.close();
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                return z;
            }

            private RectF aroundPoint(int i, int i2, int i3) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((float) (i - i3), (float) (i2 - i3), (float) (i + i3), (float) (i2 + i3));
                return rectF;
            }
        };
        r2.setOnTouchListener(CameraScanActivity$$ExternalSyntheticLambda5.INSTANCE);
        this.fragmentView = r2;
        CameraView cameraView2 = new CameraView(context2, false);
        this.cameraView = cameraView2;
        cameraView2.setUseMaxPreview(true);
        this.cameraView.setOptimizeForBarcode(true);
        this.cameraView.setDelegate(new CameraScanActivity$$ExternalSyntheticLambda18(this));
        r2.addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.currentType == 0) {
            this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        } else {
            this.actionBar.setBackgroundDrawable((Drawable) null);
            this.actionBar.setAddToContainer(false);
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            r2.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
            r2.addView(this.actionBar);
        }
        if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("AuthAnotherClientScan", NUM));
        }
        final Paint paint2 = new Paint(1);
        paint2.setPathEffect(LinkPath.roundedEffect);
        paint2.setColor(ColorUtils.setAlphaComponent(-1, 50));
        AnonymousClass4 r11 = new TextView(this, context2) {
            LinkPath textPath;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                super.onMeasure(i, i2);
                if (getText() instanceof Spanned) {
                    Spanned spanned = (Spanned) getText();
                    URLSpanNoUnderline[] uRLSpanNoUnderlineArr = (URLSpanNoUnderline[]) spanned.getSpans(0, spanned.length(), URLSpanNoUnderline.class);
                    if (uRLSpanNoUnderlineArr != null && uRLSpanNoUnderlineArr.length > 0) {
                        LinkPath linkPath = new LinkPath(true);
                        this.textPath = linkPath;
                        linkPath.setAllowReset(false);
                        for (int i4 = 0; i4 < uRLSpanNoUnderlineArr.length; i4++) {
                            int spanStart = spanned.getSpanStart(uRLSpanNoUnderlineArr[i4]);
                            int spanEnd = spanned.getSpanEnd(uRLSpanNoUnderlineArr[i4]);
                            this.textPath.setCurrentLayout(getLayout(), spanStart, 0.0f);
                            int i5 = getText() != null ? getPaint().baselineShift : 0;
                            LinkPath linkPath2 = this.textPath;
                            if (i5 != 0) {
                                i3 = i5 + AndroidUtilities.dp(i5 > 0 ? 5.0f : -2.0f);
                            } else {
                                i3 = 0;
                            }
                            linkPath2.setBaselineShift(i3);
                            getLayout().getSelectionPath(spanStart, spanEnd, this.textPath);
                        }
                        this.textPath.setAllowReset(true);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                LinkPath linkPath = this.textPath;
                if (linkPath != null) {
                    canvas.drawPath(linkPath, paint2);
                }
                super.onDraw(canvas);
            }
        };
        this.titleTextView = r11;
        r11.setGravity(1);
        this.titleTextView.setTextSize(1, 24.0f);
        r2.addView(this.titleTextView);
        TextView textView = new TextView(context2);
        this.descriptionText = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        r2.addView(this.descriptionText);
        TextView textView2 = new TextView(context2);
        this.recognizedMrzView = textView2;
        textView2.setTextColor(-1);
        this.recognizedMrzView.setGravity(81);
        this.recognizedMrzView.setAlpha(0.0f);
        int i = this.currentType;
        if (i == 0) {
            this.titleTextView.setText(LocaleController.getString("PassportScanPassport", NUM));
            this.descriptionText.setText(LocaleController.getString("PassportScanPassportInfo", NUM));
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.recognizedMrzView.setTypeface(Typeface.MONOSPACE);
            this.cameraView.addView(this.recognizedMrzView);
        } else {
            if (!this.needGalleryButton) {
                if (i == 1) {
                    this.titleTextView.setText(LocaleController.getString("AuthAnotherClientScan", NUM));
                } else {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("AuthAnotherClientInfo5", NUM));
                    String[] strArr = {LocaleController.getString("AuthAnotherWebClientUrl", NUM), LocaleController.getString("AuthAnotherClientDownloadClientUrl", NUM)};
                    int i2 = 0;
                    for (int i3 = 2; i2 < i3; i3 = 2) {
                        String spannableStringBuilder2 = spannableStringBuilder.toString();
                        int indexOf = spannableStringBuilder2.indexOf(42);
                        int i4 = indexOf + 1;
                        int indexOf2 = spannableStringBuilder2.indexOf(42, i4);
                        if (indexOf == -1 || indexOf2 == -1 || indexOf == indexOf2) {
                            break;
                        }
                        this.titleTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        int i5 = indexOf2 + 1;
                        spannableStringBuilder.replace(indexOf2, i5, " ");
                        spannableStringBuilder.replace(indexOf, i4, " ");
                        int i6 = i5 - 1;
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline(strArr[i2]), i4, i6, 33);
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), i4, i6, 33);
                        i2++;
                    }
                    this.titleTextView.setLinkTextColor(-1);
                    this.titleTextView.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
                    this.titleTextView.setTextSize(1, 16.0f);
                    this.titleTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
                    this.titleTextView.setPadding(0, 0, 0, 0);
                    this.titleTextView.setText(spannableStringBuilder);
                }
            }
            this.titleTextView.setTextColor(-1);
            this.recognizedMrzView.setTextSize(1, 16.0f);
            this.recognizedMrzView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            if (!this.needGalleryButton) {
                this.recognizedMrzView.setText(LocaleController.getString("AuthAnotherClientNotFound", NUM));
            }
            r2.addView(this.recognizedMrzView);
            if (this.needGalleryButton) {
                ImageView imageView = new ImageView(context2);
                this.galleryButton = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.galleryButton.setImageResource(NUM);
                this.galleryButton.setBackgroundDrawable(Theme.createSelectorDrawableFromDrawables(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM), Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM)));
                r2.addView(this.galleryButton);
                this.galleryButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda3(this));
            }
            ImageView imageView2 = new ImageView(context2);
            this.flashButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.flashButton.setImageResource(NUM);
            this.flashButton.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM));
            r2.addView(this.flashButton);
            this.flashButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda4(this));
        }
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
        this.fragmentView.setKeepScreenOn(true);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4() {
        startRecognizing();
        if (isQr()) {
            SpringAnimation springAnimation = this.qrAppearing;
            if (springAnimation != null) {
                springAnimation.cancel();
                this.qrAppearing = null;
            }
            SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(0.0f));
            this.qrAppearing = springAnimation2;
            springAnimation2.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda7(this));
            this.qrAppearing.addEndListener(new CameraScanActivity$$ExternalSyntheticLambda6(this));
            this.qrAppearing.setSpring(new SpringForce(500.0f));
            this.qrAppearing.getSpring().setDampingRatio(0.8f);
            this.qrAppearing.getSpring().setStiffness(250.0f);
            this.qrAppearing.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.qrAppearingValue = f / 500.0f;
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        SpringAnimation springAnimation = this.qrAppearing;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.qrAppearing = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view) {
        if (getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(PhotoAlbumPickerActivity.SELECT_TYPE_QR, false, false, (ChatActivity) null);
                photoAlbumPickerActivity.setMaxSelectedPhotos(1, false);
                photoAlbumPickerActivity.setAllowSearchImages(false);
                photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                        try {
                            if (!arrayList.isEmpty()) {
                                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = arrayList.get(0);
                                if (sendingMediaInfo.path != null) {
                                    Point realScreenSize = AndroidUtilities.getRealScreenSize();
                                    QrResult access$2600 = CameraScanActivity.this.tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap(sendingMediaInfo.path, (Uri) null, (float) realScreenSize.x, (float) realScreenSize.y, true));
                                    if (access$2600 != null) {
                                        if (CameraScanActivity.this.delegate != null) {
                                            CameraScanActivity.this.delegate.didFindQr(access$2600.text);
                                        }
                                        CameraScanActivity.this.removeSelfFromStack();
                                    }
                                }
                            }
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent intent = new Intent("android.intent.action.PICK");
                            intent.setType("image/*");
                            CameraScanActivity.this.getParentActivity().startActivityForResult(intent, 11);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                presentFragment(photoAlbumPickerActivity);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(View view) {
        CameraSession cameraSession = this.cameraView.getCameraSession();
        if (cameraSession != null) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) this.flashButton.getBackground();
            AnimatorSet animatorSet = this.flashAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.flashAnimator = null;
            }
            this.flashAnimator = new AnimatorSet();
            Property<ShapeDrawable, Integer> property = AnimationProperties.SHAPE_DRAWABLE_ALPHA;
            int[] iArr = new int[1];
            iArr[0] = this.flashButton.getTag() == null ? 68 : 34;
            ObjectAnimator ofInt = ObjectAnimator.ofInt(shapeDrawable, property, iArr);
            ofInt.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda2(this));
            this.flashAnimator.playTogether(new Animator[]{ofInt});
            this.flashAnimator.setDuration(200);
            this.flashAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.flashAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = CameraScanActivity.this.flashAnimator = null;
                }
            });
            this.flashAnimator.start();
            if (this.flashButton.getTag() == null) {
                this.flashButton.setTag(1);
                cameraSession.setTorchEnabled(true);
                return;
            }
            this.flashButton.setTag((Object) null);
            cameraSession.setTorchEnabled(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(ValueAnimator valueAnimator) {
        this.flashButton.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateRecognized() {
        ValueAnimator valueAnimator = this.recognizedAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float f = this.recognized ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.recognizedT, f});
        this.recognizedAnimator = ofFloat;
        ofFloat.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda1(this));
        this.recognizedAnimator.setDuration((long) (Math.abs(this.recognizedT - f) * 200.0f));
        this.recognizedAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.recognizedAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRecognized$8(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.recognizedT = floatValue;
        this.titleTextView.setAlpha(1.0f - floatValue);
        this.flashButton.setAlpha(1.0f - this.recognizedT);
        this.backShadowAlpha = (this.recognizedT * 0.25f) + 0.5f;
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateBounds(RectF rectF) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = this.lastBoundsUpdate;
        if (j == 0) {
            this.lastBoundsUpdate = elapsedRealtime - 75;
            this.bounds.set(rectF);
            this.fromBounds.set(rectF);
        } else {
            RectF rectF2 = this.fromBounds;
            if (rectF2 == null || elapsedRealtime - j >= 75) {
                if (rectF2 == null) {
                    this.fromBounds = new RectF();
                }
                this.fromBounds.set(this.bounds);
            } else {
                AndroidUtilities.lerp(rectF2, this.bounds, ((float) (elapsedRealtime - j)) / 75.0f, rectF2);
            }
            this.bounds.set(rectF);
            this.lastBoundsUpdate = elapsedRealtime;
        }
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public RectF getBounds() {
        if (this.fromBounds == null) {
            return this.bounds;
        }
        float elapsedRealtime = ((float) (SystemClock.elapsedRealtime() - this.lastBoundsUpdate)) / 75.0f;
        if (elapsedRealtime < 1.0f) {
            this.fragmentView.invalidate();
        }
        RectF rectF = this.fromBounds;
        RectF rectF2 = this.bounds;
        float min = Math.min(1.0f, Math.max(0.0f, elapsedRealtime));
        RectF rectF3 = AndroidUtilities.rectTmp;
        AndroidUtilities.lerp(rectF, rectF2, min, rectF3);
        return rectF3;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1 && i == 11 && intent != null && intent.getData() != null) {
            try {
                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                QrResult tryReadQr = tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap((String) null, intent.getData(), (float) realScreenSize.x, (float) realScreenSize.y, true));
                if (tryReadQr != null) {
                    CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
                    if (cameraScanActivityDelegate != null) {
                        cameraScanActivityDelegate.didFindQr(tryReadQr.text);
                    }
                    finishFragment();
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public void setDelegate(CameraScanActivityDelegate cameraScanActivityDelegate) {
        this.delegate = cameraScanActivityDelegate;
    }

    public void destroy(boolean z, Runnable runnable) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.destroy(z, runnable);
            this.cameraView = null;
        }
        this.backgroundHandlerThread.quitSafely();
    }

    private void startRecognizing() {
        this.backgroundHandlerThread.start();
        this.handler = new Handler(this.backgroundHandlerThread.getLooper());
        AndroidUtilities.runOnUIThread(this.requestShot, 0);
    }

    private void onNoQrFound() {
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda15(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNoQrFound$9() {
        if (this.recognizedMrzView.getTag() != null) {
            this.recognizedMrzView.setTag((Object) null);
            this.recognizedMrzView.animate().setDuration(200).alpha(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void processShot(Bitmap bitmap) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        try {
            Size previewSize = this.cameraView.getPreviewSize();
            if (this.currentType == 0) {
                MrzRecognizer.Result recognize = MrzRecognizer.recognize(bitmap, false);
                if (recognize != null && !TextUtils.isEmpty(recognize.firstName) && !TextUtils.isEmpty(recognize.lastName) && !TextUtils.isEmpty(recognize.number) && recognize.birthDay != 0 && ((recognize.expiryDay != 0 || recognize.doesNotExpire) && recognize.gender != 0)) {
                    this.recognized = true;
                    CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
                    AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda16(this, recognize));
                    return;
                }
            } else {
                int min = (int) (((float) Math.min(previewSize.getWidth(), previewSize.getHeight())) / 1.5f);
                QrResult tryReadQr = tryReadQr((byte[]) null, previewSize, (previewSize.getWidth() - min) / 2, (previewSize.getHeight() - min) / 2, min, bitmap);
                boolean z = this.recognized;
                if (z) {
                    this.recognizeIndex++;
                }
                if (tryReadQr != null) {
                    this.recognizeFailed = 0;
                    String str = tryReadQr.text;
                    this.recognizedText = str;
                    if (!z) {
                        this.recognized = true;
                        this.qrLoading = this.delegate.processQr(str, new CameraScanActivity$$ExternalSyntheticLambda9(this));
                        this.recognizedStart = SystemClock.elapsedRealtime();
                        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda14(this));
                    }
                    AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda17(this, tryReadQr));
                } else if (z) {
                    int i = this.recognizeFailed + 1;
                    this.recognizeFailed = i;
                    if (i > 4 && !this.qrLoading) {
                        this.recognized = false;
                        this.recognizeIndex = 0;
                        this.recognizedText = null;
                        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda11(this));
                        AndroidUtilities.runOnUIThread(this.requestShot, 500);
                        return;
                    }
                }
                if (((this.recognizeIndex == 0 && tryReadQr != null && tryReadQr.bounds == null) || (SystemClock.elapsedRealtime() - this.recognizedStart > 1000 && !this.qrLoading)) && this.recognizedText != null) {
                    CameraView cameraView2 = this.cameraView;
                    if (!(cameraView2 == null || cameraView2.getCameraSession() == null)) {
                        CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
                    }
                    AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda10(this));
                } else if (this.recognized) {
                    this.handler.postDelayed(new CameraScanActivity$$ExternalSyntheticLambda12(this), Math.max(16, ((long) (1000 / this.sps)) - ((long) this.averageProcessTime)));
                }
            }
        } catch (Throwable unused) {
            onNoQrFound();
        }
        long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
        float f = this.averageProcessTime;
        long j = this.processTimesCount;
        long j2 = j + 1;
        this.processTimesCount = j2;
        this.averageProcessTime = ((f * ((float) j)) + ((float) elapsedRealtime2)) / ((float) j2);
        FileLog.e("qr scan timeout = " + elapsedRealtime2 + "ms (avg=" + this.averageProcessTime + "ms)");
        this.processTimesCount = Math.max(this.processTimesCount, 30);
        if (!this.recognized) {
            AndroidUtilities.runOnUIThread(this.requestShot, 500);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processShot$10(MrzRecognizer.Result result) {
        this.recognizedMrzView.setText(result.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(result);
        }
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda8(this), 1200);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processShot$12() {
        CameraView cameraView2 = this.cameraView;
        if (!(cameraView2 == null || cameraView2.getCameraSession() == null)) {
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda13(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processShot$11() {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(this.recognizedText);
        }
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processShot$13(QrResult qrResult) {
        updateBounds(qrResult.bounds);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processShot$14() {
        updateRecognized();
        Point point = AndroidUtilities.displaySize;
        int i = point.x;
        int i2 = point.y;
        int min = (int) (((float) Math.min(i, i2)) / 1.5f);
        RectF rectF = AndroidUtilities.rectTmp;
        float f = (float) i;
        float f2 = (float) i2;
        rectF.set((((float) (i - min)) / 2.0f) / f, (((float) (i2 - min)) / 2.0f) / f2, (((float) (i + min)) / 2.0f) / f, (((float) (i2 + min)) / 2.0f) / f2);
        updateBounds(rectF);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processShot$15() {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(this.recognizedText);
        }
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processShot$16() {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            processShot(cameraView2.getTextureView().getBitmap());
        }
    }

    private class QrResult {
        RectF bounds;
        String text;

        private QrResult(CameraScanActivity cameraScanActivity) {
        }
    }

    /* JADX WARNING: type inference failed for: r1v18, types: [com.google.zxing.RGBLuminanceSource] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x015f A[Catch:{ all -> 0x01c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0163 A[Catch:{ all -> 0x01c6 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.ui.CameraScanActivity.QrResult tryReadQr(byte[] r25, org.telegram.messenger.camera.Size r26, int r27, int r28, int r29, android.graphics.Bitmap r30) {
        /*
            r24 = this;
            r0 = r24
            r9 = r30
            r10 = 0
            android.graphics.RectF r11 = new android.graphics.RectF     // Catch:{ all -> 0x01c6 }
            r11.<init>()     // Catch:{ all -> 0x01c6 }
            com.google.android.gms.vision.barcode.BarcodeDetector r1 = r0.visionQrReader     // Catch:{ all -> 0x01c6 }
            boolean r1 = r1.isOperational()     // Catch:{ all -> 0x01c6 }
            r12 = 0
            r13 = 1
            r14 = 2139095039(0x7f7fffff, float:3.4028235E38)
            if (r1 == 0) goto L_0x00a1
            if (r9 == 0) goto L_0x002f
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x01c6 }
            r1.<init>()     // Catch:{ all -> 0x01c6 }
            com.google.android.gms.vision.Frame$Builder r1 = r1.setBitmap(r9)     // Catch:{ all -> 0x01c6 }
            com.google.android.gms.vision.Frame r1 = r1.build()     // Catch:{ all -> 0x01c6 }
            int r2 = r30.getWidth()     // Catch:{ all -> 0x01c6 }
            int r3 = r30.getHeight()     // Catch:{ all -> 0x01c6 }
            goto L_0x0052
        L_0x002f:
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x01c6 }
            r1.<init>()     // Catch:{ all -> 0x01c6 }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.wrap(r25)     // Catch:{ all -> 0x01c6 }
            int r3 = r26.getWidth()     // Catch:{ all -> 0x01c6 }
            int r4 = r26.getHeight()     // Catch:{ all -> 0x01c6 }
            r5 = 17
            com.google.android.gms.vision.Frame$Builder r1 = r1.setImageData(r2, r3, r4, r5)     // Catch:{ all -> 0x01c6 }
            com.google.android.gms.vision.Frame r1 = r1.build()     // Catch:{ all -> 0x01c6 }
            int r2 = r26.getWidth()     // Catch:{ all -> 0x01c6 }
            int r3 = r26.getWidth()     // Catch:{ all -> 0x01c6 }
        L_0x0052:
            com.google.android.gms.vision.barcode.BarcodeDetector r4 = r0.visionQrReader     // Catch:{ all -> 0x01c6 }
            android.util.SparseArray r1 = r4.detect(r1)     // Catch:{ all -> 0x01c6 }
            if (r1 == 0) goto L_0x009e
            int r4 = r1.size()     // Catch:{ all -> 0x01c6 }
            if (r4 <= 0) goto L_0x009e
            java.lang.Object r1 = r1.valueAt(r12)     // Catch:{ all -> 0x01c6 }
            com.google.android.gms.vision.barcode.Barcode r1 = (com.google.android.gms.vision.barcode.Barcode) r1     // Catch:{ all -> 0x01c6 }
            java.lang.String r4 = r1.rawValue     // Catch:{ all -> 0x01c6 }
            android.graphics.Point[] r1 = r1.cornerPoints     // Catch:{ all -> 0x01c6 }
            if (r1 == 0) goto L_0x0158
            int r5 = r1.length     // Catch:{ all -> 0x01c6 }
            if (r5 != 0) goto L_0x0071
            goto L_0x0158
        L_0x0071:
            int r5 = r1.length     // Catch:{ all -> 0x01c6 }
            r6 = 1
            r7 = 2139095039(0x7f7fffff, float:3.4028235E38)
        L_0x0076:
            if (r12 >= r5) goto L_0x0099
            r8 = r1[r12]     // Catch:{ all -> 0x01c6 }
            int r9 = r8.x     // Catch:{ all -> 0x01c6 }
            float r9 = (float) r9     // Catch:{ all -> 0x01c6 }
            float r14 = java.lang.Math.min(r14, r9)     // Catch:{ all -> 0x01c6 }
            int r9 = r8.x     // Catch:{ all -> 0x01c6 }
            float r9 = (float) r9     // Catch:{ all -> 0x01c6 }
            float r13 = java.lang.Math.max(r13, r9)     // Catch:{ all -> 0x01c6 }
            int r9 = r8.y     // Catch:{ all -> 0x01c6 }
            float r9 = (float) r9     // Catch:{ all -> 0x01c6 }
            float r7 = java.lang.Math.min(r7, r9)     // Catch:{ all -> 0x01c6 }
            int r8 = r8.y     // Catch:{ all -> 0x01c6 }
            float r8 = (float) r8     // Catch:{ all -> 0x01c6 }
            float r6 = java.lang.Math.max(r6, r8)     // Catch:{ all -> 0x01c6 }
            int r12 = r12 + 1
            goto L_0x0076
        L_0x0099:
            r11.set(r14, r7, r13, r6)     // Catch:{ all -> 0x01c6 }
            goto L_0x0159
        L_0x009e:
            r4 = r10
            goto L_0x0159
        L_0x00a1:
            if (r9 == 0) goto L_0x00da
            int r1 = r30.getWidth()     // Catch:{ all -> 0x01c6 }
            int r2 = r30.getHeight()     // Catch:{ all -> 0x01c6 }
            int r1 = r1 * r2
            int[] r15 = new int[r1]     // Catch:{ all -> 0x01c6 }
            r3 = 0
            int r4 = r30.getWidth()     // Catch:{ all -> 0x01c6 }
            r5 = 0
            r6 = 0
            int r7 = r30.getWidth()     // Catch:{ all -> 0x01c6 }
            int r8 = r30.getHeight()     // Catch:{ all -> 0x01c6 }
            r1 = r30
            r2 = r15
            r1.getPixels(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x01c6 }
            com.google.zxing.RGBLuminanceSource r1 = new com.google.zxing.RGBLuminanceSource     // Catch:{ all -> 0x01c6 }
            int r2 = r30.getWidth()     // Catch:{ all -> 0x01c6 }
            int r3 = r30.getHeight()     // Catch:{ all -> 0x01c6 }
            r1.<init>(r2, r3, r15)     // Catch:{ all -> 0x01c6 }
            int r2 = r30.getWidth()     // Catch:{ all -> 0x01c6 }
            int r3 = r30.getWidth()     // Catch:{ all -> 0x01c6 }
            goto L_0x00fc
        L_0x00da:
            com.google.zxing.PlanarYUVLuminanceSource r1 = new com.google.zxing.PlanarYUVLuminanceSource     // Catch:{ all -> 0x01c6 }
            int r17 = r26.getWidth()     // Catch:{ all -> 0x01c6 }
            int r18 = r26.getHeight()     // Catch:{ all -> 0x01c6 }
            r23 = 0
            r15 = r1
            r16 = r25
            r19 = r27
            r20 = r28
            r21 = r29
            r22 = r29
            r15.<init>(r16, r17, r18, r19, r20, r21, r22, r23)     // Catch:{ all -> 0x01c6 }
            int r2 = r26.getWidth()     // Catch:{ all -> 0x01c6 }
            int r3 = r26.getHeight()     // Catch:{ all -> 0x01c6 }
        L_0x00fc:
            com.google.zxing.qrcode.QRCodeReader r4 = r0.qrReader     // Catch:{ all -> 0x01c6 }
            com.google.zxing.BinaryBitmap r5 = new com.google.zxing.BinaryBitmap     // Catch:{ all -> 0x01c6 }
            com.google.zxing.common.GlobalHistogramBinarizer r6 = new com.google.zxing.common.GlobalHistogramBinarizer     // Catch:{ all -> 0x01c6 }
            r6.<init>(r1)     // Catch:{ all -> 0x01c6 }
            r5.<init>(r6)     // Catch:{ all -> 0x01c6 }
            com.google.zxing.Result r1 = r4.decode(r5)     // Catch:{ all -> 0x01c6 }
            if (r1 != 0) goto L_0x0112
            r24.onNoQrFound()     // Catch:{ all -> 0x01c6 }
            return r10
        L_0x0112:
            java.lang.String r4 = r1.getText()     // Catch:{ all -> 0x01c6 }
            com.google.zxing.ResultPoint[] r5 = r1.getResultPoints()     // Catch:{ all -> 0x01c6 }
            if (r5 == 0) goto L_0x0158
            com.google.zxing.ResultPoint[] r5 = r1.getResultPoints()     // Catch:{ all -> 0x01c6 }
            int r5 = r5.length     // Catch:{ all -> 0x01c6 }
            if (r5 != 0) goto L_0x0124
            goto L_0x0158
        L_0x0124:
            com.google.zxing.ResultPoint[] r1 = r1.getResultPoints()     // Catch:{ all -> 0x01c6 }
            int r5 = r1.length     // Catch:{ all -> 0x01c6 }
            r6 = 1
            r7 = 2139095039(0x7f7fffff, float:3.4028235E38)
        L_0x012d:
            if (r12 >= r5) goto L_0x0154
            r8 = r1[r12]     // Catch:{ all -> 0x01c6 }
            float r9 = r8.getX()     // Catch:{ all -> 0x01c6 }
            float r14 = java.lang.Math.min(r14, r9)     // Catch:{ all -> 0x01c6 }
            float r9 = r8.getX()     // Catch:{ all -> 0x01c6 }
            float r13 = java.lang.Math.max(r13, r9)     // Catch:{ all -> 0x01c6 }
            float r9 = r8.getY()     // Catch:{ all -> 0x01c6 }
            float r7 = java.lang.Math.min(r7, r9)     // Catch:{ all -> 0x01c6 }
            float r8 = r8.getY()     // Catch:{ all -> 0x01c6 }
            float r6 = java.lang.Math.max(r6, r8)     // Catch:{ all -> 0x01c6 }
            int r12 = r12 + 1
            goto L_0x012d
        L_0x0154:
            r11.set(r14, r7, r13, r6)     // Catch:{ all -> 0x01c6 }
            goto L_0x0159
        L_0x0158:
            r11 = r10
        L_0x0159:
            boolean r1 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x01c6 }
            if (r1 == 0) goto L_0x0163
            r24.onNoQrFound()     // Catch:{ all -> 0x01c6 }
            return r10
        L_0x0163:
            boolean r1 = r0.needGalleryButton     // Catch:{ all -> 0x01c6 }
            if (r1 == 0) goto L_0x0180
            java.lang.String r1 = "ton://transfer/"
            boolean r1 = r4.startsWith(r1)     // Catch:{ all -> 0x01c6 }
            if (r1 != 0) goto L_0x0170
            return r10
        L_0x0170:
            android.net.Uri r1 = android.net.Uri.parse(r4)     // Catch:{ all -> 0x01c6 }
            java.lang.String r1 = r1.getPath()     // Catch:{ all -> 0x01c6 }
            java.lang.String r5 = "/"
            java.lang.String r6 = ""
            r1.replace(r5, r6)     // Catch:{ all -> 0x01c6 }
            goto L_0x018c
        L_0x0180:
            java.lang.String r1 = "tg://login?token="
            boolean r1 = r4.startsWith(r1)     // Catch:{ all -> 0x01c6 }
            if (r1 != 0) goto L_0x018c
            r24.onNoQrFound()     // Catch:{ all -> 0x01c6 }
            return r10
        L_0x018c:
            org.telegram.ui.CameraScanActivity$QrResult r1 = new org.telegram.ui.CameraScanActivity$QrResult     // Catch:{ all -> 0x01c6 }
            r1.<init>()     // Catch:{ all -> 0x01c6 }
            if (r11 == 0) goto L_0x01c1
            r5 = 1103626240(0x41CLASSNAME, float:25.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ all -> 0x01c6 }
            r6 = 1097859072(0x41700000, float:15.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ all -> 0x01c6 }
            float r7 = r11.left     // Catch:{ all -> 0x01c6 }
            float r5 = (float) r5     // Catch:{ all -> 0x01c6 }
            float r7 = r7 - r5
            float r8 = r11.top     // Catch:{ all -> 0x01c6 }
            float r6 = (float) r6     // Catch:{ all -> 0x01c6 }
            float r8 = r8 - r6
            float r9 = r11.right     // Catch:{ all -> 0x01c6 }
            float r9 = r9 + r5
            float r5 = r11.bottom     // Catch:{ all -> 0x01c6 }
            float r5 = r5 + r6
            r11.set(r7, r8, r9, r5)     // Catch:{ all -> 0x01c6 }
            float r5 = r11.left     // Catch:{ all -> 0x01c6 }
            float r2 = (float) r2     // Catch:{ all -> 0x01c6 }
            float r5 = r5 / r2
            float r6 = r11.top     // Catch:{ all -> 0x01c6 }
            float r3 = (float) r3     // Catch:{ all -> 0x01c6 }
            float r6 = r6 / r3
            float r7 = r11.right     // Catch:{ all -> 0x01c6 }
            float r7 = r7 / r2
            float r2 = r11.bottom     // Catch:{ all -> 0x01c6 }
            float r2 = r2 / r3
            r11.set(r5, r6, r7, r2)     // Catch:{ all -> 0x01c6 }
        L_0x01c1:
            r1.bounds = r11     // Catch:{ all -> 0x01c6 }
            r1.text = r4     // Catch:{ all -> 0x01c6 }
            return r1
        L_0x01c6:
            r24.onNoQrFound()
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CameraScanActivity.tryReadQr(byte[], org.telegram.messenger.camera.Size, int, int, int, android.graphics.Bitmap):org.telegram.ui.CameraScanActivity$QrResult");
    }

    /* access modifiers changed from: private */
    public boolean isQr() {
        int i = this.currentType;
        return i == 1 || i == 2;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        if (isQr()) {
            return arrayList;
        }
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarWhiteSelector"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        return arrayList;
    }
}
