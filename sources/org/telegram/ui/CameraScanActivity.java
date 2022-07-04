package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import org.telegram.messenger.Utilities;
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
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoAlbumPickerActivity;

public class CameraScanActivity extends BaseFragment {
    public static final int TYPE_MRZ = 0;
    public static final int TYPE_QR = 1;
    public static final int TYPE_QR_LOGIN = 2;
    private float averageProcessTime = 0.0f;
    /* access modifiers changed from: private */
    public float backShadowAlpha = 0.5f;
    private HandlerThread backgroundHandlerThread = new HandlerThread("ScanCamera");
    private RectF bounds = new RectF();
    private final long boundsUpdateDuration = 75;
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
    /* access modifiers changed from: private */
    public boolean needGalleryButton;
    private RectF normalBounds;
    /* access modifiers changed from: private */
    public Paint paint = new Paint();
    /* access modifiers changed from: private */
    public Path path = new Path();
    private long processTimesCount = 0;
    private SpringAnimation qrAppearing = null;
    /* access modifiers changed from: private */
    public float qrAppearingValue = 0.0f;
    private boolean qrLoaded = false;
    private boolean qrLoading = false;
    private QRCodeReader qrReader = null;
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

        /* renamed from: lambda$run$0$org-telegram-ui-CameraScanActivity$7  reason: not valid java name */
        public /* synthetic */ void m2775lambda$run$0$orgtelegramuiCameraScanActivity$7() {
            if (CameraScanActivity.this.cameraView != null) {
                CameraScanActivity cameraScanActivity = CameraScanActivity.this;
                cameraScanActivity.processShot(cameraScanActivity.cameraView.getTextureView().getBitmap());
            }
        }
    };
    protected boolean shownAsBottomSheet = false;
    private int sps;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private float useRecognizedBounds = 0.0f;
    private SpringAnimation useRecognizedBoundsAnimator;
    private BarcodeDetector visionQrReader = null;

    public interface CameraScanActivityDelegate {
        void didFindMrzInfo(MrzRecognizer.Result result);

        void didFindQr(String str);

        boolean processQr(String str, Runnable runnable);

        /* renamed from: org.telegram.ui.CameraScanActivity$CameraScanActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didFindMrzInfo(CameraScanActivityDelegate _this, MrzRecognizer.Result result) {
            }

            public static void $default$didFindQr(CameraScanActivityDelegate _this, String text) {
            }

            public static boolean $default$processQr(CameraScanActivityDelegate _this, String text, Runnable onLoadEnd) {
                return false;
            }
        }
    }

    public static ActionBarLayout[] showAsSheet(BaseFragment parentFragment, boolean gallery, int type, CameraScanActivityDelegate cameraDelegate) {
        if (parentFragment == null || parentFragment.getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayout = {new ActionBarLayout(parentFragment.getParentActivity())};
        AnonymousClass1 r3 = new BottomSheet(parentFragment.getParentActivity(), false, actionBarLayout, type, gallery, cameraDelegate) {
            CameraScanActivity fragment;
            final /* synthetic */ ActionBarLayout[] val$actionBarLayout;
            final /* synthetic */ CameraScanActivityDelegate val$cameraDelegate;
            final /* synthetic */ boolean val$gallery;
            final /* synthetic */ int val$type;

            {
                this.val$actionBarLayout = r6;
                this.val$type = r7;
                this.val$gallery = r8;
                this.val$cameraDelegate = r9;
                r6[0].init(new ArrayList());
                AnonymousClass1 r1 = new CameraScanActivity(r7) {
                    public void finishFragment() {
                        AnonymousClass1.this.dismiss();
                    }

                    public void removeSelfFromStack() {
                        AnonymousClass1.this.dismiss();
                    }
                };
                this.fragment = r1;
                r1.shownAsBottomSheet = true;
                boolean unused = this.fragment.needGalleryButton = r8;
                r6[0].addFragmentToStack(this.fragment);
                r6[0].showLastFragment();
                r6[0].setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
                this.fragment.setDelegate(r9);
                this.containerView = r6[0];
                setApplyBottomPadding(false);
                setApplyBottomPadding(false);
                setOnDismissListener(new CameraScanActivity$1$$ExternalSyntheticLambda0(this));
            }

            /* renamed from: lambda$new$0$org-telegram-ui-CameraScanActivity$1  reason: not valid java name */
            public /* synthetic */ void m2774lambda$new$0$orgtelegramuiCameraScanActivity$1(DialogInterface dialog) {
                this.fragment.onFragmentDestroy();
            }

            /* access modifiers changed from: protected */
            public boolean canDismissWithSwipe() {
                return false;
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
                super.dismiss();
                this.val$actionBarLayout[0] = null;
            }
        };
        r3.setUseLightStatusBar(false);
        AndroidUtilities.setLightNavigationBar(r3.getWindow(), false);
        AndroidUtilities.setNavigationBarColor(r3.getWindow(), -16777216, false);
        r3.setUseLightStatusBar(false);
        r3.getWindow().addFlags(512);
        r3.show();
        return actionBarLayout;
    }

    public CameraScanActivity(int type) {
        this.currentType = type;
        if (isQr()) {
            Utilities.globalQueue.postRunnable(new CameraScanActivity$$ExternalSyntheticLambda19(this));
        }
        switch (SharedConfig.getDevicePerformanceClass()) {
            case 0:
                this.sps = 8;
                return;
            case 1:
                this.sps = 24;
                return;
            default:
                this.sps = 40;
                return;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2764lambda$new$0$orgtelegramuiCameraScanActivity() {
        this.qrReader = new QRCodeReader();
        this.visionQrReader = new BarcodeDetector.Builder(ApplicationLoader.applicationContext).setBarcodeFormats(256).build();
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
        if (this.shownAsBottomSheet) {
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(-1, false);
            this.actionBar.setTitleColor(-1);
        } else {
            this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
            this.actionBar.setTitleColor(Theme.getColor("actionBarDefaultTitle"));
        }
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet() && !isQr()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CameraScanActivity.this.finishFragment();
                }
            }
        });
        this.paint.setColor(NUM);
        this.cornerPaint.setColor(-1);
        this.cornerPaint.setStyle(Paint.Style.FILL);
        ViewGroup viewGroup = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                CameraScanActivity.this.actionBar.measure(widthMeasureSpec, heightMeasureSpec);
                if (CameraScanActivity.this.currentType != 0) {
                    if (CameraScanActivity.this.cameraView != null) {
                        CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
                    }
                    CameraScanActivity.this.recognizedMrzView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                    if (CameraScanActivity.this.galleryButton != null) {
                        CameraScanActivity.this.galleryButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                    }
                    CameraScanActivity.this.flashButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                } else if (CameraScanActivity.this.cameraView != null) {
                    CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.704f), NUM));
                }
                CameraScanActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(72.0f), NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                CameraScanActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.9f), NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                setMeasuredDimension(width, height);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int y;
                int x;
                int width = r - l;
                int height = b - t;
                if (CameraScanActivity.this.currentType == 0) {
                    if (CameraScanActivity.this.cameraView != null) {
                        CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight() + 0);
                    }
                    CameraScanActivity.this.recognizedMrzView.setTextSize(0, (float) (height / 22));
                    CameraScanActivity.this.recognizedMrzView.setPadding(0, 0, 0, height / 15);
                    int y2 = (int) (((float) height) * 0.65f);
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), y2, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + y2);
                } else {
                    CameraScanActivity.this.actionBar.layout(0, 0, CameraScanActivity.this.actionBar.getMeasuredWidth(), CameraScanActivity.this.actionBar.getMeasuredHeight());
                    if (CameraScanActivity.this.cameraView != null) {
                        CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight());
                    }
                    int size = (int) (((float) Math.min(width, height)) / 1.5f);
                    if (CameraScanActivity.this.currentType == 1) {
                        y = (((height - size) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(30.0f);
                    } else {
                        y = (((height - size) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(64.0f);
                    }
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), y, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + y);
                    CameraScanActivity.this.recognizedMrzView.layout(0, getMeasuredHeight() - CameraScanActivity.this.recognizedMrzView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                    if (CameraScanActivity.this.needGalleryButton) {
                        x = (width / 2) + AndroidUtilities.dp(35.0f);
                    } else {
                        x = (width / 2) - (CameraScanActivity.this.flashButton.getMeasuredWidth() / 2);
                    }
                    int y3 = ((height - size) / 2) + size + AndroidUtilities.dp(80.0f);
                    CameraScanActivity.this.flashButton.layout(x, y3, CameraScanActivity.this.flashButton.getMeasuredWidth() + x, CameraScanActivity.this.flashButton.getMeasuredHeight() + y3);
                    if (CameraScanActivity.this.galleryButton != null) {
                        int x2 = ((width / 2) - AndroidUtilities.dp(35.0f)) - CameraScanActivity.this.galleryButton.getMeasuredWidth();
                        CameraScanActivity.this.galleryButton.layout(x2, y3, CameraScanActivity.this.galleryButton.getMeasuredWidth() + x2, CameraScanActivity.this.galleryButton.getMeasuredHeight() + y3);
                    }
                    int i = y3;
                }
                int y4 = (int) (((float) height) * 0.74f);
                int x3 = (int) (((float) width) * 0.05f);
                CameraScanActivity.this.descriptionText.layout(x3, y4, CameraScanActivity.this.descriptionText.getMeasuredWidth() + x3, CameraScanActivity.this.descriptionText.getMeasuredHeight() + y4);
                CameraScanActivity.this.updateNormalBounds();
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                Canvas canvas2 = canvas;
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (CameraScanActivity.this.isQr() && child == CameraScanActivity.this.cameraView) {
                    RectF bounds = CameraScanActivity.this.getBounds();
                    int cx = (int) (((float) child.getWidth()) * bounds.centerX());
                    int cy = (int) (((float) child.getHeight()) * bounds.centerY());
                    int sizex = (int) (((float) ((int) (((float) child.getWidth()) * bounds.width()))) * ((CameraScanActivity.this.qrAppearingValue * 0.5f) + 0.5f));
                    int sizey = (int) (((float) ((int) (((float) child.getHeight()) * bounds.height()))) * ((CameraScanActivity.this.qrAppearingValue * 0.5f) + 0.5f));
                    int x = cx - (sizex / 2);
                    int y = cy - (sizey / 2);
                    CameraScanActivity.this.paint.setAlpha((int) ((1.0f - ((1.0f - CameraScanActivity.this.backShadowAlpha) * Math.min(1.0f, CameraScanActivity.this.qrAppearingValue))) * 255.0f));
                    float f = (float) y;
                    int y2 = y;
                    canvas.drawRect(0.0f, 0.0f, (float) child.getMeasuredWidth(), f, CameraScanActivity.this.paint);
                    canvas.drawRect(0.0f, (float) (y2 + sizey), (float) child.getMeasuredWidth(), (float) child.getMeasuredHeight(), CameraScanActivity.this.paint);
                    Canvas canvas3 = canvas;
                    canvas3.drawRect(0.0f, (float) y2, (float) x, (float) (y2 + sizey), CameraScanActivity.this.paint);
                    Canvas canvas4 = canvas;
                    canvas4.drawRect((float) (x + sizex), (float) y2, (float) child.getMeasuredWidth(), (float) (y2 + sizey), CameraScanActivity.this.paint);
                    CameraScanActivity.this.paint.setAlpha((int) (Math.max(0.0f, 1.0f - CameraScanActivity.this.qrAppearingValue) * 255.0f));
                    float f2 = (float) (y2 + sizey);
                    Canvas canvas5 = canvas;
                    RectF rectF = bounds;
                    canvas5.drawRect((float) x, (float) y2, (float) (x + sizex), f2, CameraScanActivity.this.paint);
                    int lineWidth = AndroidUtilities.lerp(0, AndroidUtilities.dp(4.0f), Math.min(1.0f, CameraScanActivity.this.qrAppearingValue * 20.0f));
                    int halfLineWidth = lineWidth / 2;
                    int i = cx;
                    int lineLength = AndroidUtilities.lerp(Math.min(sizex, sizey), AndroidUtilities.dp(20.0f), Math.min(1.2f, (float) Math.pow((double) CameraScanActivity.this.qrAppearingValue, 1.7999999523162842d)));
                    CameraScanActivity.this.cornerPaint.setAlpha((int) (Math.min(1.0f, CameraScanActivity.this.qrAppearingValue) * 255.0f));
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x, y2 + lineLength, halfLineWidth), 0.0f, 180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) x) + (((float) lineWidth) * 1.5f)), (int) (((float) y2) + (((float) lineWidth) * 1.5f)), lineWidth * 2), 180.0f, 90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + lineLength, y2, halfLineWidth), 270.0f, 180.0f);
                    CameraScanActivity.this.path.lineTo((float) (x + halfLineWidth), (float) (y2 + halfLineWidth));
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) x) + (((float) lineWidth) * 1.5f)), (int) (((float) y2) + (((float) lineWidth) * 1.5f)), lineWidth), 270.0f, -90.0f);
                    CameraScanActivity.this.path.close();
                    canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + sizex, y2 + lineLength, halfLineWidth), 180.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) (x + sizex)) - (((float) lineWidth) * 1.5f)), (int) (((float) y2) + (((float) lineWidth) * 1.5f)), lineWidth * 2), 0.0f, -90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((x + sizex) - lineLength, y2, halfLineWidth), 270.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) (x + sizex)) - (((float) lineWidth) * 1.5f)), (int) (((float) y2) + (((float) lineWidth) * 1.5f)), lineWidth), 270.0f, 90.0f);
                    CameraScanActivity.this.path.close();
                    canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x, (y2 + sizey) - lineLength, halfLineWidth), 0.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) x) + (((float) lineWidth) * 1.5f)), (int) (((float) (y2 + sizey)) - (((float) lineWidth) * 1.5f)), lineWidth * 2), 180.0f, -90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + lineLength, y2 + sizey, halfLineWidth), 90.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) x) + (((float) lineWidth) * 1.5f)), (int) (((float) (y2 + sizey)) - (((float) lineWidth) * 1.5f)), lineWidth), 90.0f, 90.0f);
                    CameraScanActivity.this.path.close();
                    canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + sizex, (y2 + sizey) - lineLength, halfLineWidth), 180.0f, 180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) (x + sizex)) - (((float) lineWidth) * 1.5f)), (int) (((float) (y2 + sizey)) - (((float) lineWidth) * 1.5f)), lineWidth * 2), 0.0f, 90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((x + sizex) - lineLength, y2 + sizey, halfLineWidth), 90.0f, 180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (((float) (x + sizex)) - (((float) lineWidth) * 1.5f)), (int) (((float) (y2 + sizey)) - (((float) lineWidth) * 1.5f)), lineWidth), 90.0f, -90.0f);
                    CameraScanActivity.this.path.close();
                    canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                }
                return result;
            }

            private RectF aroundPoint(int x, int y, int r) {
                AndroidUtilities.rectTmp.set((float) (x - r), (float) (y - r), (float) (x + r), (float) (y + r));
                return AndroidUtilities.rectTmp;
            }
        };
        viewGroup.setOnTouchListener(CameraScanActivity$$ExternalSyntheticLambda14.INSTANCE);
        this.fragmentView = viewGroup;
        int i = this.currentType;
        if (i == 1 || i == 2) {
            this.fragmentView.postDelayed(new CameraScanActivity$$ExternalSyntheticLambda6(this), 450);
        } else {
            initCameraView();
        }
        if (this.currentType == 0) {
            this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        } else {
            this.actionBar.setBackgroundDrawable((Drawable) null);
            this.actionBar.setAddToContainer(false);
            this.actionBar.setTitleColor(-1);
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            viewGroup.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
            viewGroup.addView(this.actionBar);
        }
        if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("AuthAnotherClientScan", NUM));
        }
        final Paint selectionPaint = new Paint(1);
        selectionPaint.setPathEffect(LinkPath.getRoundedEffect());
        selectionPaint.setColor(ColorUtils.setAlphaComponent(-1, 40));
        AnonymousClass4 r11 = new TextView(context2) {
            LinkSpanDrawable.LinkCollector links = new LinkSpanDrawable.LinkCollector(this);
            private LinkSpanDrawable<URLSpanNoUnderline> pressedLink;
            LinkPath textPath;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int i;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (getText() instanceof Spanned) {
                    Spanned spanned = (Spanned) getText();
                    URLSpanNoUnderline[] innerSpans = (URLSpanNoUnderline[]) spanned.getSpans(0, spanned.length(), URLSpanNoUnderline.class);
                    if (innerSpans != null && innerSpans.length > 0) {
                        LinkPath linkPath = new LinkPath(true);
                        this.textPath = linkPath;
                        linkPath.setAllowReset(false);
                        for (int a = 0; a < innerSpans.length; a++) {
                            int start = spanned.getSpanStart(innerSpans[a]);
                            int end = spanned.getSpanEnd(innerSpans[a]);
                            this.textPath.setCurrentLayout(getLayout(), start, 0.0f);
                            int shift = getText() != null ? getPaint().baselineShift : 0;
                            LinkPath linkPath2 = this.textPath;
                            if (shift != 0) {
                                i = AndroidUtilities.dp(shift > 0 ? 5.0f : -2.0f) + shift;
                            } else {
                                i = 0;
                            }
                            linkPath2.setBaselineShift(i);
                            getLayout().getSelectionPath(start, end, this.textPath);
                        }
                        this.textPath.setAllowReset(true);
                    }
                }
            }

            public boolean onTouchEvent(MotionEvent e) {
                Layout textLayout = getLayout();
                int x = (int) (e.getX() - ((float) 0));
                int y = (int) (e.getY() - ((float) 0));
                if (e.getAction() == 0 || e.getAction() == 1) {
                    int line = textLayout.getLineForVertical(y);
                    int off = textLayout.getOffsetForHorizontal(line, (float) x);
                    float left = textLayout.getLineLeft(line);
                    if (left <= ((float) x) && textLayout.getLineWidth(line) + left >= ((float) x) && y >= 0 && y <= textLayout.getHeight()) {
                        Spannable buffer = (Spannable) textLayout.getText();
                        ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                        if (link.length != 0) {
                            this.links.clear();
                            if (e.getAction() == 0) {
                                LinkSpanDrawable<URLSpanNoUnderline> linkSpanDrawable = new LinkSpanDrawable<>(link[0], (Theme.ResourcesProvider) null, e.getX(), e.getY());
                                this.pressedLink = linkSpanDrawable;
                                linkSpanDrawable.setColor(NUM);
                                this.links.addLink(this.pressedLink);
                                int start = buffer.getSpanStart(this.pressedLink.getSpan());
                                int end = buffer.getSpanEnd(this.pressedLink.getSpan());
                                LinkPath path = this.pressedLink.obtainNewPath();
                                path.setCurrentLayout(textLayout, start, (float) 0);
                                textLayout.getSelectionPath(start, end, path);
                                return true;
                            } else if (e.getAction() != 1) {
                                return true;
                            } else {
                                LinkSpanDrawable<URLSpanNoUnderline> linkSpanDrawable2 = this.pressedLink;
                                if (linkSpanDrawable2 != null && linkSpanDrawable2.getSpan() == link[0]) {
                                    link[0].onClick(this);
                                }
                                this.pressedLink = null;
                                return true;
                            }
                        }
                    }
                }
                if (e.getAction() == 1 || e.getAction() == 3) {
                    this.links.clear();
                    this.pressedLink = null;
                }
                return super.onTouchEvent(e);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                LinkPath linkPath = this.textPath;
                if (linkPath != null) {
                    canvas.drawPath(linkPath, selectionPaint);
                }
                if (this.links.draw(canvas)) {
                    invalidate();
                }
                super.onDraw(canvas);
            }
        };
        this.titleTextView = r11;
        r11.setGravity(1);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        TextView textView = new TextView(context2);
        this.descriptionText = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        viewGroup.addView(this.descriptionText);
        TextView textView2 = new TextView(context2);
        this.recognizedMrzView = textView2;
        textView2.setTextColor(-1);
        this.recognizedMrzView.setGravity(81);
        this.recognizedMrzView.setAlpha(0.0f);
        int i2 = this.currentType;
        if (i2 == 0) {
            this.titleTextView.setText(LocaleController.getString("PassportScanPassport", NUM));
            this.descriptionText.setText(LocaleController.getString("PassportScanPassportInfo", NUM));
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.recognizedMrzView.setTypeface(Typeface.MONOSPACE);
        } else {
            if (!this.needGalleryButton) {
                if (i2 == 1) {
                    this.titleTextView.setText(LocaleController.getString("AuthAnotherClientScan", NUM));
                } else {
                    SpannableStringBuilder spanned = new SpannableStringBuilder(LocaleController.getString("AuthAnotherClientInfo5", NUM));
                    String[] links = {LocaleController.getString("AuthAnotherClientDownloadClientUrl", NUM), LocaleController.getString("AuthAnotherWebClientUrl", NUM)};
                    for (int i3 = 0; i3 < links.length; i3++) {
                        String text = spanned.toString();
                        int index1 = text.indexOf(42);
                        int index2 = text.indexOf(42, index1 + 1);
                        if (index1 == -1 || index2 == -1 || index1 == index2) {
                            break;
                        }
                        this.titleTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spanned.replace(index2, index2 + 1, " ");
                        spanned.replace(index1, index1 + 1, " ");
                        int index12 = index1 + 1;
                        int index22 = index2 + 1;
                        spanned.setSpan(new URLSpanNoUnderline(links[i3], true), index12, index22 - 1, 33);
                        spanned.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), index12, index22 - 1, 33);
                    }
                    this.titleTextView.setLinkTextColor(-1);
                    this.titleTextView.setTextSize(1, 16.0f);
                    this.titleTextView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
                    this.titleTextView.setPadding(0, 0, 0, 0);
                    this.titleTextView.setText(spanned);
                }
            }
            this.titleTextView.setTextColor(-1);
            this.recognizedMrzView.setTextSize(1, 16.0f);
            this.recognizedMrzView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            if (!this.needGalleryButton) {
                this.recognizedMrzView.setText(LocaleController.getString("AuthAnotherClientNotFound", NUM));
            }
            viewGroup.addView(this.recognizedMrzView);
            if (this.needGalleryButton) {
                ImageView imageView = new ImageView(context2);
                this.galleryButton = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.galleryButton.setImageResource(NUM);
                this.galleryButton.setBackgroundDrawable(Theme.createSelectorDrawableFromDrawables(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM), Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM)));
                viewGroup.addView(this.galleryButton);
                this.galleryButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda12(this));
            }
            ImageView imageView2 = new ImageView(context2);
            this.flashButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.flashButton.setImageResource(NUM);
            this.flashButton.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM));
            viewGroup.addView(this.flashButton);
            this.flashButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda13(this));
        }
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
        this.fragmentView.setKeepScreenOn(true);
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2758lambda$createView$2$orgtelegramuiCameraScanActivity(View currentImage) {
        if (getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(PhotoAlbumPickerActivity.SELECT_TYPE_QR, false, false, (ChatActivity) null);
                fragment.setMaxSelectedPhotos(1, false);
                fragment.setAllowSearchImages(false);
                fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
                        try {
                            if (!photos.isEmpty()) {
                                try {
                                    SendMessagesHelper.SendingMediaInfo info = photos.get(0);
                                    if (info.path != null) {
                                        Point screenSize = AndroidUtilities.getRealScreenSize();
                                        QrResult res = CameraScanActivity.this.tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap(info.path, (Uri) null, (float) screenSize.x, (float) screenSize.y, true));
                                        if (res != null) {
                                            if (CameraScanActivity.this.delegate != null) {
                                                CameraScanActivity.this.delegate.didFindQr(res.text);
                                            }
                                            CameraScanActivity.this.removeSelfFromStack();
                                        }
                                    }
                                } catch (Throwable th) {
                                    e = th;
                                    FileLog.e(e);
                                }
                            } else {
                                ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList = photos;
                            }
                        } catch (Throwable th2) {
                            e = th2;
                            ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList2 = photos;
                            FileLog.e(e);
                        }
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                            photoPickerIntent.setType("image/*");
                            CameraScanActivity.this.getParentActivity().startActivityForResult(photoPickerIntent, 11);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                presentFragment(fragment);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2760lambda$createView$4$orgtelegramuiCameraScanActivity(View currentImage) {
        CameraSession session;
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null && (session = cameraView2.getCameraSession()) != null) {
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
            ObjectAnimator animator = ObjectAnimator.ofInt(shapeDrawable, property, iArr);
            animator.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda0(this));
            this.flashAnimator.playTogether(new Animator[]{animator});
            this.flashAnimator.setDuration(200);
            this.flashAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.flashAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = CameraScanActivity.this.flashAnimator = null;
                }
            });
            this.flashAnimator.start();
            if (this.flashButton.getTag() == null) {
                this.flashButton.setTag(1);
                session.setTorchEnabled(true);
                return;
            }
            this.flashButton.setTag((Object) null);
            session.setTorchEnabled(false);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2759lambda$createView$3$orgtelegramuiCameraScanActivity(ValueAnimator animation) {
        this.flashButton.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateRecognized() {
        ValueAnimator valueAnimator = this.recognizedAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float newRecognizedT = this.recognized ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.recognizedT, newRecognizedT});
        this.recognizedAnimator = ofFloat;
        ofFloat.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda11(this));
        this.recognizedAnimator.setDuration((long) (Math.abs(this.recognizedT - newRecognizedT) * 300.0f));
        this.recognizedAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.recognizedAnimator.start();
        SpringAnimation springAnimation = this.useRecognizedBoundsAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder((this.recognized ? this.useRecognizedBounds : 1.0f - this.useRecognizedBounds) * 500.0f));
        this.useRecognizedBoundsAnimator = springAnimation2;
        springAnimation2.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda17(this));
        this.useRecognizedBoundsAnimator.setSpring(new SpringForce(500.0f));
        this.useRecognizedBoundsAnimator.getSpring().setDampingRatio(1.0f);
        this.useRecognizedBoundsAnimator.getSpring().setStiffness(500.0f);
        this.useRecognizedBoundsAnimator.start();
    }

    /* renamed from: lambda$updateRecognized$5$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2772lambda$updateRecognized$5$orgtelegramuiCameraScanActivity(ValueAnimator a) {
        float floatValue = ((Float) a.getAnimatedValue()).floatValue();
        this.recognizedT = floatValue;
        this.titleTextView.setAlpha(1.0f - floatValue);
        this.flashButton.setAlpha(1.0f - this.recognizedT);
        this.backShadowAlpha = (this.recognizedT * 0.25f) + 0.5f;
        this.fragmentView.invalidate();
    }

    /* renamed from: lambda$updateRecognized$6$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2773lambda$updateRecognized$6$orgtelegramuiCameraScanActivity(DynamicAnimation animation, float value, float velocity) {
        this.useRecognizedBounds = this.recognized ? value / 500.0f : 1.0f - (value / 500.0f);
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public void initCameraView() {
        TextView textView;
        if (this.fragmentView != null) {
            CameraController.getInstance().initCamera((Runnable) null);
            CameraView cameraView2 = new CameraView(this.fragmentView.getContext(), false);
            this.cameraView = cameraView2;
            cameraView2.setUseMaxPreview(true);
            this.cameraView.setOptimizeForBarcode(true);
            this.cameraView.setDelegate(new CameraScanActivity$$ExternalSyntheticLambda10(this));
            ((ViewGroup) this.fragmentView).addView(this.cameraView, 0, LayoutHelper.createFrame(-1, -1.0f));
            if (this.currentType == 0 && (textView = this.recognizedMrzView) != null) {
                this.cameraView.addView(textView);
            }
        }
    }

    /* renamed from: lambda$initCameraView$9$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2763lambda$initCameraView$9$orgtelegramuiCameraScanActivity() {
        startRecognizing();
        if (isQr()) {
            SpringAnimation springAnimation = this.qrAppearing;
            if (springAnimation != null) {
                springAnimation.cancel();
                this.qrAppearing = null;
            }
            SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(0.0f));
            this.qrAppearing = springAnimation2;
            springAnimation2.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda16(this));
            this.qrAppearing.addEndListener(new CameraScanActivity$$ExternalSyntheticLambda15(this));
            this.qrAppearing.setSpring(new SpringForce(500.0f));
            this.qrAppearing.getSpring().setDampingRatio(0.8f);
            this.qrAppearing.getSpring().setStiffness(250.0f);
            this.qrAppearing.start();
        }
    }

    /* renamed from: lambda$initCameraView$7$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2761lambda$initCameraView$7$orgtelegramuiCameraScanActivity(DynamicAnimation animation, float value, float velocity) {
        this.qrAppearingValue = value / 500.0f;
        this.fragmentView.invalidate();
    }

    /* renamed from: lambda$initCameraView$8$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2762lambda$initCameraView$8$orgtelegramuiCameraScanActivity(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        SpringAnimation springAnimation = this.qrAppearing;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.qrAppearing = null;
        }
    }

    private void updateRecognizedBounds(RectF newBounds) {
        long now = SystemClock.elapsedRealtime();
        long j = this.lastBoundsUpdate;
        if (j == 0) {
            this.lastBoundsUpdate = now - 75;
            this.bounds.set(newBounds);
            this.fromBounds.set(newBounds);
        } else {
            RectF rectF = this.fromBounds;
            if (rectF == null || now - j >= 75) {
                if (rectF == null) {
                    this.fromBounds = new RectF();
                }
                this.fromBounds.set(this.bounds);
            } else {
                float t = Math.min(1.0f, Math.max(0.0f, ((float) (now - j)) / 75.0f));
                RectF rectF2 = this.fromBounds;
                AndroidUtilities.lerp(rectF2, this.bounds, t, rectF2);
            }
            this.bounds.set(newBounds);
            this.lastBoundsUpdate = now;
        }
        this.fragmentView.invalidate();
    }

    private RectF getRecognizedBounds() {
        if (this.fromBounds == null) {
            return this.bounds;
        }
        float t = Math.min(1.0f, Math.max(0.0f, ((float) (SystemClock.elapsedRealtime() - this.lastBoundsUpdate)) / 75.0f));
        if (t < 1.0f) {
            this.fragmentView.invalidate();
        }
        AndroidUtilities.lerp(this.fromBounds, this.bounds, t, AndroidUtilities.rectTmp);
        return AndroidUtilities.rectTmp;
    }

    /* access modifiers changed from: private */
    public void updateNormalBounds() {
        if (this.normalBounds == null) {
            this.normalBounds = new RectF();
        }
        int width = Math.max(AndroidUtilities.displaySize.x, this.fragmentView.getWidth());
        int height = Math.max(AndroidUtilities.displaySize.y, this.fragmentView.getHeight());
        int side = (int) (((float) Math.min(width, height)) / 1.5f);
        this.normalBounds.set((((float) (width - side)) / 2.0f) / ((float) width), (((float) (height - side)) / 2.0f) / ((float) height), (((float) (width + side)) / 2.0f) / ((float) width), (((float) (height + side)) / 2.0f) / ((float) height));
    }

    /* access modifiers changed from: private */
    public RectF getBounds() {
        RectF recognizedBounds = getRecognizedBounds();
        if (this.useRecognizedBounds < 1.0f) {
            if (this.normalBounds == null) {
                updateNormalBounds();
            }
            AndroidUtilities.lerp(this.normalBounds, recognizedBounds, this.useRecognizedBounds, recognizedBounds);
        }
        return recognizedBounds;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            int i = requestCode;
        } else if (requestCode != 11 || data == null || data.getData() == null) {
        } else {
            try {
                Point screenSize = AndroidUtilities.getRealScreenSize();
                QrResult res = tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap((String) null, data.getData(), (float) screenSize.x, (float) screenSize.y, true));
                if (res != null) {
                    try {
                        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
                        if (cameraScanActivityDelegate != null) {
                            cameraScanActivityDelegate.didFindQr(res.text);
                        }
                        finishFragment();
                    } catch (Throwable th) {
                        e = th;
                        FileLog.e(e);
                    }
                }
            } catch (Throwable th2) {
                e = th2;
                FileLog.e(e);
            }
        }
    }

    public void setDelegate(CameraScanActivityDelegate cameraScanActivityDelegate) {
        this.delegate = cameraScanActivityDelegate;
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.destroy(async, beforeDestroyRunnable);
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
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: lambda$onNoQrFound$10$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2765lambda$onNoQrFound$10$orgtelegramuiCameraScanActivity() {
        if (this.recognizedMrzView.getTag() != null) {
            this.recognizedMrzView.setTag((Object) null);
            this.recognizedMrzView.animate().setDuration(200).alpha(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x017a  */
    /* JADX WARNING: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processShot(android.graphics.Bitmap r17) {
        /*
            r16 = this;
            r8 = r16
            org.telegram.messenger.camera.CameraView r0 = r8.cameraView
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            long r9 = android.os.SystemClock.elapsedRealtime()
            r11 = 500(0x1f4, double:2.47E-321)
            org.telegram.messenger.camera.CameraView r0 = r8.cameraView     // Catch:{ all -> 0x0150 }
            org.telegram.messenger.camera.Size r3 = r0.getPreviewSize()     // Catch:{ all -> 0x0150 }
            int r0 = r8.currentType     // Catch:{ all -> 0x0150 }
            r13 = 1
            r14 = 0
            if (r0 != 0) goto L_0x0063
            r15 = r17
            org.telegram.messenger.MrzRecognizer$Result r0 = org.telegram.messenger.MrzRecognizer.recognize(r15, r14)     // Catch:{ all -> 0x014e }
            if (r0 == 0) goto L_0x0061
            java.lang.String r1 = r0.firstName     // Catch:{ all -> 0x014e }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014e }
            if (r1 != 0) goto L_0x0061
            java.lang.String r1 = r0.lastName     // Catch:{ all -> 0x014e }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014e }
            if (r1 != 0) goto L_0x0061
            java.lang.String r1 = r0.number     // Catch:{ all -> 0x014e }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x014e }
            if (r1 != 0) goto L_0x0061
            int r1 = r0.birthDay     // Catch:{ all -> 0x014e }
            if (r1 == 0) goto L_0x0061
            int r1 = r0.expiryDay     // Catch:{ all -> 0x014e }
            if (r1 != 0) goto L_0x0045
            boolean r1 = r0.doesNotExpire     // Catch:{ all -> 0x014e }
            if (r1 == 0) goto L_0x0061
        L_0x0045:
            int r1 = r0.gender     // Catch:{ all -> 0x014e }
            if (r1 == 0) goto L_0x0061
            r8.recognized = r13     // Catch:{ all -> 0x014e }
            org.telegram.messenger.camera.CameraController r1 = org.telegram.messenger.camera.CameraController.getInstance()     // Catch:{ all -> 0x014e }
            org.telegram.messenger.camera.CameraView r2 = r8.cameraView     // Catch:{ all -> 0x014e }
            org.telegram.messenger.camera.CameraSession r2 = r2.getCameraSession()     // Catch:{ all -> 0x014e }
            r1.stopPreview(r2)     // Catch:{ all -> 0x014e }
            org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda8     // Catch:{ all -> 0x014e }
            r1.<init>(r8, r0)     // Catch:{ all -> 0x014e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x014e }
            return
        L_0x0061:
            goto L_0x014d
        L_0x0063:
            r15 = r17
            int r0 = r3.getWidth()     // Catch:{ all -> 0x014e }
            int r1 = r3.getHeight()     // Catch:{ all -> 0x014e }
            int r0 = java.lang.Math.min(r0, r1)     // Catch:{ all -> 0x014e }
            float r0 = (float) r0     // Catch:{ all -> 0x014e }
            r1 = 1069547520(0x3fCLASSNAME, float:1.5)
            float r0 = r0 / r1
            int r0 = (int) r0     // Catch:{ all -> 0x014e }
            int r1 = r3.getWidth()     // Catch:{ all -> 0x014e }
            int r1 = r1 - r0
            int r4 = r1 / 2
            int r1 = r3.getHeight()     // Catch:{ all -> 0x014e }
            int r1 = r1 - r0
            int r5 = r1 / 2
            r2 = 0
            r1 = r16
            r6 = r0
            r7 = r17
            org.telegram.ui.CameraScanActivity$QrResult r1 = r1.tryReadQr(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x014e }
            boolean r2 = r8.recognized     // Catch:{ all -> 0x014e }
            if (r2 == 0) goto L_0x0097
            int r6 = r8.recognizeIndex     // Catch:{ all -> 0x014e }
            int r6 = r6 + r13
            r8.recognizeIndex = r6     // Catch:{ all -> 0x014e }
        L_0x0097:
            if (r1 == 0) goto L_0x00c9
            r8.recognizeFailed = r14     // Catch:{ all -> 0x014e }
            java.lang.String r2 = r1.text     // Catch:{ all -> 0x014e }
            r8.recognizedText = r2     // Catch:{ all -> 0x014e }
            boolean r6 = r8.recognized     // Catch:{ all -> 0x014e }
            if (r6 != 0) goto L_0x00c0
            r8.recognized = r13     // Catch:{ all -> 0x014e }
            org.telegram.ui.CameraScanActivity$CameraScanActivityDelegate r6 = r8.delegate     // Catch:{ all -> 0x014e }
            org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda3 r7 = new org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda3     // Catch:{ all -> 0x014e }
            r7.<init>(r8)     // Catch:{ all -> 0x014e }
            boolean r2 = r6.processQr(r2, r7)     // Catch:{ all -> 0x014e }
            r8.qrLoading = r2     // Catch:{ all -> 0x014e }
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x014e }
            r8.recognizedStart = r6     // Catch:{ all -> 0x014e }
            org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda7 r2 = new org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda7     // Catch:{ all -> 0x014e }
            r2.<init>(r8)     // Catch:{ all -> 0x014e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x014e }
        L_0x00c0:
            org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda9     // Catch:{ all -> 0x014e }
            r2.<init>(r8, r1)     // Catch:{ all -> 0x014e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x014e }
            goto L_0x00ec
        L_0x00c9:
            if (r2 == 0) goto L_0x00ec
            int r2 = r8.recognizeFailed     // Catch:{ all -> 0x014e }
            int r2 = r2 + r13
            r8.recognizeFailed = r2     // Catch:{ all -> 0x014e }
            r6 = 4
            if (r2 <= r6) goto L_0x00ec
            boolean r2 = r8.qrLoading     // Catch:{ all -> 0x014e }
            if (r2 != 0) goto L_0x00ec
            r8.recognized = r14     // Catch:{ all -> 0x014e }
            r8.recognizeIndex = r14     // Catch:{ all -> 0x014e }
            r2 = 0
            r8.recognizedText = r2     // Catch:{ all -> 0x014e }
            org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda7 r2 = new org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda7     // Catch:{ all -> 0x014e }
            r2.<init>(r8)     // Catch:{ all -> 0x014e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x014e }
            java.lang.Runnable r2 = r8.requestShot     // Catch:{ all -> 0x014e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r11)     // Catch:{ all -> 0x014e }
            return
        L_0x00ec:
            int r2 = r8.recognizeIndex     // Catch:{ all -> 0x014e }
            if (r2 != 0) goto L_0x00fa
            if (r1 == 0) goto L_0x00fa
            android.graphics.RectF r2 = r1.bounds     // Catch:{ all -> 0x014e }
            if (r2 != 0) goto L_0x00fa
            boolean r2 = r8.qrLoading     // Catch:{ all -> 0x014e }
            if (r2 == 0) goto L_0x010b
        L_0x00fa:
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x014e }
            long r13 = r8.recognizedStart     // Catch:{ all -> 0x014e }
            long r6 = r6 - r13
            r13 = 1000(0x3e8, double:4.94E-321)
            int r2 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r2 <= 0) goto L_0x012f
            boolean r2 = r8.qrLoading     // Catch:{ all -> 0x014e }
            if (r2 != 0) goto L_0x012f
        L_0x010b:
            java.lang.String r2 = r8.recognizedText     // Catch:{ all -> 0x014e }
            if (r2 == 0) goto L_0x012f
            org.telegram.messenger.camera.CameraView r2 = r8.cameraView     // Catch:{ all -> 0x014e }
            if (r2 == 0) goto L_0x0126
            org.telegram.messenger.camera.CameraSession r2 = r2.getCameraSession()     // Catch:{ all -> 0x014e }
            if (r2 == 0) goto L_0x0126
            org.telegram.messenger.camera.CameraController r2 = org.telegram.messenger.camera.CameraController.getInstance()     // Catch:{ all -> 0x014e }
            org.telegram.messenger.camera.CameraView r6 = r8.cameraView     // Catch:{ all -> 0x014e }
            org.telegram.messenger.camera.CameraSession r6 = r6.getCameraSession()     // Catch:{ all -> 0x014e }
            r2.stopPreview(r6)     // Catch:{ all -> 0x014e }
        L_0x0126:
            org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda4     // Catch:{ all -> 0x014e }
            r2.<init>(r8)     // Catch:{ all -> 0x014e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ all -> 0x014e }
            goto L_0x014d
        L_0x012f:
            boolean r2 = r8.recognized     // Catch:{ all -> 0x014e }
            if (r2 == 0) goto L_0x014d
            r6 = 16
            r2 = 1000(0x3e8, float:1.401E-42)
            int r13 = r8.sps     // Catch:{ all -> 0x014e }
            int r2 = r2 / r13
            long r13 = (long) r2     // Catch:{ all -> 0x014e }
            float r2 = r8.averageProcessTime     // Catch:{ all -> 0x014e }
            long r11 = (long) r2     // Catch:{ all -> 0x014e }
            long r13 = r13 - r11
            long r6 = java.lang.Math.max(r6, r13)     // Catch:{ all -> 0x014e }
            android.os.Handler r2 = r8.handler     // Catch:{ all -> 0x014e }
            org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda5 r11 = new org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda5     // Catch:{ all -> 0x014e }
            r11.<init>(r8)     // Catch:{ all -> 0x014e }
            r2.postDelayed(r11, r6)     // Catch:{ all -> 0x014e }
        L_0x014d:
            goto L_0x0156
        L_0x014e:
            r0 = move-exception
            goto L_0x0153
        L_0x0150:
            r0 = move-exception
            r15 = r17
        L_0x0153:
            r16.onNoQrFound()
        L_0x0156:
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r0 - r9
            float r4 = r8.averageProcessTime
            long r5 = r8.processTimesCount
            float r7 = (float) r5
            float r4 = r4 * r7
            float r7 = (float) r2
            float r4 = r4 + r7
            r11 = 1
            long r5 = r5 + r11
            r8.processTimesCount = r5
            float r7 = (float) r5
            float r4 = r4 / r7
            r8.averageProcessTime = r4
            r11 = 30
            long r4 = java.lang.Math.max(r5, r11)
            r8.processTimesCount = r4
            boolean r4 = r8.recognized
            if (r4 != 0) goto L_0x0181
            java.lang.Runnable r4 = r8.requestShot
            r5 = 500(0x1f4, double:2.47E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4, r5)
        L_0x0181:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CameraScanActivity.processShot(android.graphics.Bitmap):void");
    }

    /* renamed from: lambda$processShot$11$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2766lambda$processShot$11$orgtelegramuiCameraScanActivity(MrzRecognizer.Result res) {
        this.recognizedMrzView.setText(res.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(res);
        }
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda18(this), 1200);
    }

    /* renamed from: lambda$processShot$13$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2768lambda$processShot$13$orgtelegramuiCameraScanActivity() {
        CameraView cameraView2 = this.cameraView;
        if (!(cameraView2 == null || cameraView2.getCameraSession() == null)) {
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda2(this));
    }

    /* renamed from: lambda$processShot$12$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2767lambda$processShot$12$orgtelegramuiCameraScanActivity() {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(this.recognizedText);
        }
        finishFragment();
    }

    /* renamed from: lambda$processShot$14$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2769lambda$processShot$14$orgtelegramuiCameraScanActivity(QrResult res) {
        updateRecognizedBounds(res.bounds);
    }

    /* renamed from: lambda$processShot$15$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2770lambda$processShot$15$orgtelegramuiCameraScanActivity() {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(this.recognizedText);
        }
        finishFragment();
    }

    /* renamed from: lambda$processShot$16$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m2771lambda$processShot$16$orgtelegramuiCameraScanActivity() {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            processShot(cameraView2.getTextureView().getBitmap());
        }
    }

    private class QrResult {
        RectF bounds;
        String text;

        private QrResult() {
        }
    }

    /* JADX WARNING: type inference failed for: r2v34, types: [com.google.zxing.RGBLuminanceSource] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.ui.CameraScanActivity.QrResult tryReadQr(byte[] r27, org.telegram.messenger.camera.Size r28, int r29, int r30, int r31, android.graphics.Bitmap r32) {
        /*
            r26 = this;
            r1 = r26
            r10 = r32
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x021e }
            r0.<init>()     // Catch:{ all -> 0x021e }
            r12 = 1
            r13 = 1
            com.google.android.gms.vision.barcode.BarcodeDetector r2 = r1.visionQrReader     // Catch:{ all -> 0x021e }
            r14 = 0
            if (r2 == 0) goto L_0x00cd
            boolean r2 = r2.isOperational()     // Catch:{ all -> 0x021e }
            if (r2 == 0) goto L_0x00cd
            if (r10 == 0) goto L_0x0030
            com.google.android.gms.vision.Frame$Builder r2 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x021e }
            r2.<init>()     // Catch:{ all -> 0x021e }
            com.google.android.gms.vision.Frame$Builder r2 = r2.setBitmap(r10)     // Catch:{ all -> 0x021e }
            com.google.android.gms.vision.Frame r2 = r2.build()     // Catch:{ all -> 0x021e }
            int r3 = r32.getWidth()     // Catch:{ all -> 0x021e }
            int r4 = r32.getHeight()     // Catch:{ all -> 0x021e }
            r12 = r3
            r13 = r4
            goto L_0x0055
        L_0x0030:
            com.google.android.gms.vision.Frame$Builder r2 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x021e }
            r2.<init>()     // Catch:{ all -> 0x021e }
            java.nio.ByteBuffer r3 = java.nio.ByteBuffer.wrap(r27)     // Catch:{ all -> 0x021e }
            int r4 = r28.getWidth()     // Catch:{ all -> 0x021e }
            int r5 = r28.getHeight()     // Catch:{ all -> 0x021e }
            r6 = 17
            com.google.android.gms.vision.Frame$Builder r2 = r2.setImageData(r3, r4, r5, r6)     // Catch:{ all -> 0x021e }
            com.google.android.gms.vision.Frame r2 = r2.build()     // Catch:{ all -> 0x021e }
            int r3 = r28.getWidth()     // Catch:{ all -> 0x021e }
            int r4 = r28.getWidth()     // Catch:{ all -> 0x021e }
            r12 = r3
            r13 = r4
        L_0x0055:
            com.google.android.gms.vision.barcode.BarcodeDetector r3 = r1.visionQrReader     // Catch:{ all -> 0x021e }
            android.util.SparseArray r3 = r3.detect(r2)     // Catch:{ all -> 0x021e }
            if (r3 == 0) goto L_0x00c6
            int r4 = r3.size()     // Catch:{ all -> 0x021e }
            if (r4 <= 0) goto L_0x00c6
            java.lang.Object r4 = r3.valueAt(r14)     // Catch:{ all -> 0x021e }
            com.google.android.gms.vision.barcode.Barcode r4 = (com.google.android.gms.vision.barcode.Barcode) r4     // Catch:{ all -> 0x021e }
            java.lang.String r5 = r4.rawValue     // Catch:{ all -> 0x021e }
            android.graphics.Point[] r6 = r4.cornerPoints     // Catch:{ all -> 0x021e }
            if (r6 == 0) goto L_0x00c0
            android.graphics.Point[] r6 = r4.cornerPoints     // Catch:{ all -> 0x021e }
            int r6 = r6.length     // Catch:{ all -> 0x021e }
            if (r6 != 0) goto L_0x0079
            r18 = r2
            r17 = r3
            goto L_0x00c4
        L_0x0079:
            r6 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r7 = 1
            r8 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r9 = 1
            android.graphics.Point[] r15 = r4.cornerPoints     // Catch:{ all -> 0x021e }
            int r14 = r15.length     // Catch:{ all -> 0x021e }
            r11 = 0
        L_0x0085:
            if (r11 >= r14) goto L_0x00b8
            r16 = r15[r11]     // Catch:{ all -> 0x021e }
            r17 = r16
            r18 = r2
            r2 = r17
            r17 = r3
            int r3 = r2.x     // Catch:{ all -> 0x021e }
            float r3 = (float) r3     // Catch:{ all -> 0x021e }
            float r3 = java.lang.Math.min(r6, r3)     // Catch:{ all -> 0x021e }
            r6 = r3
            int r3 = r2.x     // Catch:{ all -> 0x021e }
            float r3 = (float) r3     // Catch:{ all -> 0x021e }
            float r3 = java.lang.Math.max(r7, r3)     // Catch:{ all -> 0x021e }
            r7 = r3
            int r3 = r2.y     // Catch:{ all -> 0x021e }
            float r3 = (float) r3     // Catch:{ all -> 0x021e }
            float r3 = java.lang.Math.min(r8, r3)     // Catch:{ all -> 0x021e }
            r8 = r3
            int r3 = r2.y     // Catch:{ all -> 0x021e }
            float r3 = (float) r3     // Catch:{ all -> 0x021e }
            float r3 = java.lang.Math.max(r9, r3)     // Catch:{ all -> 0x021e }
            r9 = r3
            int r11 = r11 + 1
            r3 = r17
            r2 = r18
            goto L_0x0085
        L_0x00b8:
            r18 = r2
            r17 = r3
            r0.set(r6, r8, r7, r9)     // Catch:{ all -> 0x021e }
            goto L_0x00c5
        L_0x00c0:
            r18 = r2
            r17 = r3
        L_0x00c4:
            r0 = 0
        L_0x00c5:
            goto L_0x00cb
        L_0x00c6:
            r18 = r2
            r17 = r3
            r5 = 0
        L_0x00cb:
            goto L_0x01a7
        L_0x00cd:
            com.google.zxing.qrcode.QRCodeReader r2 = r1.qrReader     // Catch:{ all -> 0x021e }
            if (r2 == 0) goto L_0x01a5
            if (r10 == 0) goto L_0x010d
            int r2 = r32.getWidth()     // Catch:{ all -> 0x021e }
            int r3 = r32.getHeight()     // Catch:{ all -> 0x021e }
            int r2 = r2 * r3
            int[] r2 = new int[r2]     // Catch:{ all -> 0x021e }
            r11 = r2
            r4 = 0
            int r5 = r32.getWidth()     // Catch:{ all -> 0x021e }
            r6 = 0
            r7 = 0
            int r8 = r32.getWidth()     // Catch:{ all -> 0x021e }
            int r9 = r32.getHeight()     // Catch:{ all -> 0x021e }
            r2 = r32
            r3 = r11
            r2.getPixels(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x021e }
            com.google.zxing.RGBLuminanceSource r2 = new com.google.zxing.RGBLuminanceSource     // Catch:{ all -> 0x021e }
            int r3 = r32.getWidth()     // Catch:{ all -> 0x021e }
            int r4 = r32.getHeight()     // Catch:{ all -> 0x021e }
            r2.<init>(r3, r4, r11)     // Catch:{ all -> 0x021e }
            int r3 = r32.getWidth()     // Catch:{ all -> 0x021e }
            int r4 = r32.getWidth()     // Catch:{ all -> 0x021e }
            r12 = r3
            r13 = r4
            goto L_0x0132
        L_0x010d:
            com.google.zxing.PlanarYUVLuminanceSource r2 = new com.google.zxing.PlanarYUVLuminanceSource     // Catch:{ all -> 0x021e }
            int r19 = r28.getWidth()     // Catch:{ all -> 0x021e }
            int r20 = r28.getHeight()     // Catch:{ all -> 0x021e }
            r25 = 0
            r17 = r2
            r18 = r27
            r21 = r29
            r22 = r30
            r23 = r31
            r24 = r31
            r17.<init>(r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ all -> 0x021e }
            int r3 = r28.getWidth()     // Catch:{ all -> 0x021e }
            int r4 = r28.getHeight()     // Catch:{ all -> 0x021e }
            r12 = r3
            r13 = r4
        L_0x0132:
            com.google.zxing.qrcode.QRCodeReader r3 = r1.qrReader     // Catch:{ all -> 0x021e }
            com.google.zxing.BinaryBitmap r4 = new com.google.zxing.BinaryBitmap     // Catch:{ all -> 0x021e }
            com.google.zxing.common.GlobalHistogramBinarizer r5 = new com.google.zxing.common.GlobalHistogramBinarizer     // Catch:{ all -> 0x021e }
            r5.<init>(r2)     // Catch:{ all -> 0x021e }
            r4.<init>(r5)     // Catch:{ all -> 0x021e }
            com.google.zxing.Result r3 = r3.decode(r4)     // Catch:{ all -> 0x021e }
            if (r3 != 0) goto L_0x0149
            r26.onNoQrFound()     // Catch:{ all -> 0x021e }
            r4 = 0
            return r4
        L_0x0149:
            java.lang.String r4 = r3.getText()     // Catch:{ all -> 0x021e }
            r5 = r4
            com.google.zxing.ResultPoint[] r4 = r3.getResultPoints()     // Catch:{ all -> 0x021e }
            if (r4 == 0) goto L_0x01a1
            com.google.zxing.ResultPoint[] r4 = r3.getResultPoints()     // Catch:{ all -> 0x021e }
            int r4 = r4.length     // Catch:{ all -> 0x021e }
            if (r4 != 0) goto L_0x015e
            r16 = r2
            goto L_0x01a3
        L_0x015e:
            r4 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r6 = 1
            r7 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r8 = 1
            com.google.zxing.ResultPoint[] r9 = r3.getResultPoints()     // Catch:{ all -> 0x021e }
            int r11 = r9.length     // Catch:{ all -> 0x021e }
            r14 = 0
        L_0x016c:
            if (r14 >= r11) goto L_0x019b
            r15 = r9[r14]     // Catch:{ all -> 0x021e }
            r16 = r2
            float r2 = r15.getX()     // Catch:{ all -> 0x021e }
            float r2 = java.lang.Math.min(r4, r2)     // Catch:{ all -> 0x021e }
            r4 = r2
            float r2 = r15.getX()     // Catch:{ all -> 0x021e }
            float r2 = java.lang.Math.max(r6, r2)     // Catch:{ all -> 0x021e }
            r6 = r2
            float r2 = r15.getY()     // Catch:{ all -> 0x021e }
            float r2 = java.lang.Math.min(r7, r2)     // Catch:{ all -> 0x021e }
            r7 = r2
            float r2 = r15.getY()     // Catch:{ all -> 0x021e }
            float r2 = java.lang.Math.max(r8, r2)     // Catch:{ all -> 0x021e }
            r8 = r2
            int r14 = r14 + 1
            r2 = r16
            goto L_0x016c
        L_0x019b:
            r16 = r2
            r0.set(r4, r7, r6, r8)     // Catch:{ all -> 0x021e }
            goto L_0x01a4
        L_0x01a1:
            r16 = r2
        L_0x01a3:
            r0 = 0
        L_0x01a4:
            goto L_0x01a7
        L_0x01a5:
            r2 = 0
            r5 = r2
        L_0x01a7:
            boolean r2 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x021e }
            if (r2 == 0) goto L_0x01b2
            r26.onNoQrFound()     // Catch:{ all -> 0x021e }
            r2 = 0
            return r2
        L_0x01b2:
            boolean r2 = r1.needGalleryButton     // Catch:{ all -> 0x021e }
            if (r2 == 0) goto L_0x01d1
            java.lang.String r2 = "ton://transfer/"
            boolean r2 = r5.startsWith(r2)     // Catch:{ all -> 0x021e }
            if (r2 != 0) goto L_0x01c0
            r2 = 0
            return r2
        L_0x01c0:
            android.net.Uri r2 = android.net.Uri.parse(r5)     // Catch:{ all -> 0x021e }
            java.lang.String r3 = r2.getPath()     // Catch:{ all -> 0x021e }
            java.lang.String r4 = "/"
            java.lang.String r6 = ""
            r3.replace(r4, r6)     // Catch:{ all -> 0x021e }
            r2 = 0
            goto L_0x01df
        L_0x01d1:
            java.lang.String r2 = "tg://login?token="
            boolean r2 = r5.startsWith(r2)     // Catch:{ all -> 0x021e }
            if (r2 != 0) goto L_0x01de
            r26.onNoQrFound()     // Catch:{ all -> 0x021e }
            r2 = 0
            return r2
        L_0x01de:
            r2 = 0
        L_0x01df:
            org.telegram.ui.CameraScanActivity$QrResult r3 = new org.telegram.ui.CameraScanActivity$QrResult     // Catch:{ all -> 0x021e }
            r3.<init>()     // Catch:{ all -> 0x021e }
            r2 = r3
            if (r0 == 0) goto L_0x0219
            r3 = 1103626240(0x41CLASSNAME, float:25.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ all -> 0x021e }
            r4 = 1097859072(0x41700000, float:15.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x021e }
            float r6 = r0.left     // Catch:{ all -> 0x021e }
            float r7 = (float) r3     // Catch:{ all -> 0x021e }
            float r6 = r6 - r7
            float r7 = r0.top     // Catch:{ all -> 0x021e }
            float r8 = (float) r4     // Catch:{ all -> 0x021e }
            float r7 = r7 - r8
            float r8 = r0.right     // Catch:{ all -> 0x021e }
            float r9 = (float) r3     // Catch:{ all -> 0x021e }
            float r8 = r8 + r9
            float r9 = r0.bottom     // Catch:{ all -> 0x021e }
            float r11 = (float) r4     // Catch:{ all -> 0x021e }
            float r9 = r9 + r11
            r0.set(r6, r7, r8, r9)     // Catch:{ all -> 0x021e }
            float r6 = r0.left     // Catch:{ all -> 0x021e }
            float r7 = (float) r12     // Catch:{ all -> 0x021e }
            float r6 = r6 / r7
            float r7 = r0.top     // Catch:{ all -> 0x021e }
            float r8 = (float) r13     // Catch:{ all -> 0x021e }
            float r7 = r7 / r8
            float r8 = r0.right     // Catch:{ all -> 0x021e }
            float r9 = (float) r12     // Catch:{ all -> 0x021e }
            float r8 = r8 / r9
            float r9 = r0.bottom     // Catch:{ all -> 0x021e }
            float r11 = (float) r13     // Catch:{ all -> 0x021e }
            float r9 = r9 / r11
            r0.set(r6, r7, r8, r9)     // Catch:{ all -> 0x021e }
        L_0x0219:
            r2.bounds = r0     // Catch:{ all -> 0x021e }
            r2.text = r5     // Catch:{ all -> 0x021e }
            return r2
        L_0x021e:
            r0 = move-exception
            r26.onNoQrFound()
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CameraScanActivity.tryReadQr(byte[], org.telegram.messenger.camera.Size, int, int, int, android.graphics.Bitmap):org.telegram.ui.CameraScanActivity$QrResult");
    }

    /* access modifiers changed from: private */
    public boolean isQr() {
        int i = this.currentType;
        return i == 1 || i == 2;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        if (isQr()) {
            return themeDescriptions;
        }
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarWhiteSelector"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        return themeDescriptions;
    }
}
