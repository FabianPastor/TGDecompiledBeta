package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.barcode.BarcodeDetector.Builder;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.messenger.camera.Size;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;

@TargetApi(18)
public class CameraScanActivity extends BaseFragment implements PreviewCallback {
    public static final int TYPE_MRZ = 0;
    public static final int TYPE_QR = 1;
    private HandlerThread backgroundHandlerThread = new HandlerThread("ScanCamera");
    private CameraView cameraView;
    private Paint cornerPaint = new Paint(1);
    private int currentType;
    private CameraScanActivityDelegate delegate;
    private TextView descriptionText;
    private AnimatorSet flashAnimator;
    private ImageView flashButton;
    private ImageView galleryButton;
    private Handler handler;
    private boolean needGalleryButton;
    private Paint paint = new Paint();
    private Path path = new Path();
    private QRCodeReader qrReader;
    private boolean recognized;
    private TextView recognizedMrzView;
    private TextView titleTextView;
    private BarcodeDetector visionQrReader;

    public interface CameraScanActivityDelegate {

        public final /* synthetic */ class -CC {
            public static void $default$didFindMrzInfo(CameraScanActivityDelegate cameraScanActivityDelegate, Result result) {
            }

            public static void $default$didFindQr(CameraScanActivityDelegate cameraScanActivityDelegate, String str) {
            }
        }

        void didFindMrzInfo(Result result);

        void didFindQr(String str);
    }

    public static ActionBarLayout[] showAsSheet(BaseFragment baseFragment, boolean z, CameraScanActivityDelegate cameraScanActivityDelegate) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayoutArr = new ActionBarLayout[]{new ActionBarLayout(baseFragment.getParentActivity())};
        final ActionBarLayout[] actionBarLayoutArr2 = actionBarLayoutArr;
        final boolean z2 = z;
        final CameraScanActivityDelegate cameraScanActivityDelegate2 = cameraScanActivityDelegate;
        new BottomSheet(baseFragment.getParentActivity(), false) {
            /* Access modifiers changed, original: protected */
            public boolean canDismissWithSwipe() {
                return false;
            }

            public void onBackPressed() {
                ActionBarLayout[] actionBarLayoutArr = actionBarLayoutArr2;
                if (actionBarLayoutArr[0] == null || actionBarLayoutArr[0].fragmentsStack.size() <= 1) {
                    super.onBackPressed();
                } else {
                    actionBarLayoutArr2[0].onBackPressed();
                }
            }

            public void dismiss() {
                super.dismiss();
                actionBarLayoutArr2[0] = null;
            }
        }.show();
        return actionBarLayoutArr;
    }

    public CameraScanActivity(int i) {
        CameraController.getInstance().initCamera(new -$$Lambda$CameraScanActivity$MpjUCnNpRqL7_BAyOkpBVyW42YI(this));
        this.currentType = i;
        if (this.currentType == 1) {
            this.qrReader = new QRCodeReader();
            Builder builder = new Builder(ApplicationLoader.applicationContext);
            builder.setBarcodeFormats(256);
            this.visionQrReader = builder.build();
        }
    }

    public /* synthetic */ void lambda$new$0$CameraScanActivity() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.initCamera();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        destroy(false, null);
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(-1);
        }
        BarcodeDetector barcodeDetector = this.visionQrReader;
        if (barcodeDetector != null) {
            barcodeDetector.release();
        }
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        if (!(AndroidUtilities.isTablet() || this.currentType == 1)) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    CameraScanActivity.this.finishFragment();
                }
            }
        });
        this.paint.setColor(NUM);
        this.cornerPaint.setColor(-1);
        this.cornerPaint.setStyle(Style.STROKE);
        this.cornerPaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.cornerPaint.setStrokeJoin(Join.ROUND);
        AnonymousClass3 anonymousClass3 = new ViewGroup(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                CameraScanActivity.this.actionBar.measure(i, i2);
                if (CameraScanActivity.this.currentType == 0) {
                    CameraScanActivity.this.cameraView.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.704f), NUM));
                } else {
                    CameraScanActivity.this.cameraView.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, NUM));
                    CameraScanActivity.this.recognizedMrzView.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, 0));
                    if (CameraScanActivity.this.galleryButton != null) {
                        CameraScanActivity.this.galleryButton.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                    }
                    CameraScanActivity.this.flashButton.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                }
                CameraScanActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, 0));
                CameraScanActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.9f), NUM), MeasureSpec.makeMeasureSpec(size2, 0));
                setMeasuredDimension(size, size2);
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                i3 -= i;
                i4 -= i2;
                if (CameraScanActivity.this.currentType == 0) {
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight() + 0);
                    i5 = (int) (((float) i4) * 0.65f);
                    CameraScanActivity.this.titleTextView.layout(0, i5, CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + i5);
                    CameraScanActivity.this.recognizedMrzView.setTextSize(0, (float) (CameraScanActivity.this.cameraView.getMeasuredHeight() / 22));
                    CameraScanActivity.this.recognizedMrzView.setPadding(0, 0, 0, CameraScanActivity.this.cameraView.getMeasuredHeight() / 15);
                } else {
                    CameraScanActivity.this.actionBar.layout(0, 0, CameraScanActivity.this.actionBar.getMeasuredWidth(), CameraScanActivity.this.actionBar.getMeasuredHeight());
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight());
                    i5 = (int) (((float) Math.min(CameraScanActivity.this.cameraView.getWidth(), CameraScanActivity.this.cameraView.getHeight())) / 1.5f);
                    i2 = (((CameraScanActivity.this.cameraView.getMeasuredHeight() - i5) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(30.0f);
                    CameraScanActivity.this.titleTextView.layout(0, i2, CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + i2);
                    CameraScanActivity.this.recognizedMrzView.layout(0, getMeasuredHeight() - CameraScanActivity.this.recognizedMrzView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                    if (CameraScanActivity.this.needGalleryButton) {
                        i = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) + AndroidUtilities.dp(35.0f);
                    } else {
                        i = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - (CameraScanActivity.this.flashButton.getMeasuredWidth() / 2);
                    }
                    int measuredHeight = (((CameraScanActivity.this.cameraView.getMeasuredHeight() - i5) / 2) + i5) + AndroidUtilities.dp(30.0f);
                    CameraScanActivity.this.flashButton.layout(i, measuredHeight, CameraScanActivity.this.flashButton.getMeasuredWidth() + i, CameraScanActivity.this.flashButton.getMeasuredHeight() + measuredHeight);
                    if (CameraScanActivity.this.galleryButton != null) {
                        i5 = ((CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - AndroidUtilities.dp(35.0f)) - CameraScanActivity.this.galleryButton.getMeasuredWidth();
                        CameraScanActivity.this.galleryButton.layout(i5, measuredHeight, CameraScanActivity.this.galleryButton.getMeasuredWidth() + i5, CameraScanActivity.this.galleryButton.getMeasuredHeight() + measuredHeight);
                    }
                }
                i5 = (int) (((float) i4) * 0.74f);
                i = (int) (((float) i3) * 0.05f);
                CameraScanActivity.this.descriptionText.layout(i, i5, CameraScanActivity.this.descriptionText.getMeasuredWidth() + i, CameraScanActivity.this.descriptionText.getMeasuredHeight() + i5);
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                Canvas canvas2 = canvas;
                boolean drawChild = super.drawChild(canvas, view, j);
                if (CameraScanActivity.this.currentType != 1 || view != CameraScanActivity.this.cameraView) {
                    return drawChild;
                }
                int min = (int) (((float) Math.min(view.getWidth(), view.getHeight())) / 1.5f);
                int width = (view.getWidth() - min) / 2;
                int height = (view.getHeight() - min) / 2;
                float f = (float) height;
                canvas.drawRect(0.0f, 0.0f, (float) view.getMeasuredWidth(), f, CameraScanActivity.this.paint);
                int i = height + min;
                float f2 = (float) i;
                canvas.drawRect(0.0f, f2, (float) view.getMeasuredWidth(), (float) view.getMeasuredHeight(), CameraScanActivity.this.paint);
                float f3 = (float) width;
                float f4 = f;
                float f5 = f2;
                float f6 = f3;
                canvas.drawRect(0.0f, f4, f3, f5, CameraScanActivity.this.paint);
                min += width;
                f3 = (float) min;
                boolean z = drawChild;
                float f7 = f3;
                canvas.drawRect(f3, f4, (float) view.getMeasuredWidth(), f5, CameraScanActivity.this.paint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f6, (float) (AndroidUtilities.dp(20.0f) + height));
                CameraScanActivity.this.path.lineTo(f6, f);
                CameraScanActivity.this.path.lineTo((float) (AndroidUtilities.dp(20.0f) + width), f);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f7, (float) (height + AndroidUtilities.dp(20.0f)));
                CameraScanActivity.this.path.lineTo(f7, f);
                CameraScanActivity.this.path.lineTo((float) (min - AndroidUtilities.dp(20.0f)), f);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f6, (float) (i - AndroidUtilities.dp(20.0f)));
                CameraScanActivity.this.path.lineTo(f6, f2);
                CameraScanActivity.this.path.lineTo((float) (width + AndroidUtilities.dp(20.0f)), f2);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f7, (float) (i - AndroidUtilities.dp(20.0f)));
                CameraScanActivity.this.path.lineTo(f7, f2);
                CameraScanActivity.this.path.lineTo((float) (min - AndroidUtilities.dp(20.0f)), f2);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                return z;
            }
        };
        anonymousClass3.setOnTouchListener(-$$Lambda$CameraScanActivity$coK9fFDxJKElfMa1PuRYWa0XVmg.INSTANCE);
        this.fragmentView = anonymousClass3;
        this.cameraView = new CameraView(context, false);
        this.cameraView.setUseMaxPreview(true);
        this.cameraView.setOptimizeForBarcode(true);
        this.cameraView.setDelegate(new CameraViewDelegate() {
            public void onCameraCreated(Camera camera) {
            }

            public void onCameraInit() {
                CameraScanActivity.this.startRecognizing();
            }
        });
        anonymousClass3.addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.currentType == 0) {
            String str = "windowBackgroundWhite";
            this.actionBar.setBackgroundColor(Theme.getColor(str));
            this.fragmentView.setBackgroundColor(Theme.getColor(str));
        } else {
            this.actionBar.setBackgroundDrawable(null);
            this.actionBar.setAddToContainer(false);
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            anonymousClass3.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
            anonymousClass3.addView(this.actionBar);
        }
        this.titleTextView = new TextView(context);
        this.titleTextView.setGravity(1);
        this.titleTextView.setTextSize(1, 24.0f);
        anonymousClass3.addView(this.titleTextView);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        anonymousClass3.addView(this.descriptionText);
        this.recognizedMrzView = new TextView(context);
        this.recognizedMrzView.setTextColor(-1);
        this.recognizedMrzView.setGravity(81);
        this.recognizedMrzView.setAlpha(0.0f);
        if (this.currentType == 0) {
            this.titleTextView.setText(LocaleController.getString("PassportScanPassport", NUM));
            this.descriptionText.setText(LocaleController.getString("PassportScanPassportInfo", NUM));
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.recognizedMrzView.setTypeface(Typeface.MONOSPACE);
            this.cameraView.addView(this.recognizedMrzView);
        } else {
            if (this.needGalleryButton) {
                this.titleTextView.setText(LocaleController.getString("WalletScanCode", NUM));
            } else {
                this.titleTextView.setText(LocaleController.getString("AuthAnotherClientScan", NUM));
            }
            this.titleTextView.setTextColor(-1);
            this.recognizedMrzView.setTextSize(1, 16.0f);
            this.recognizedMrzView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            if (this.needGalleryButton) {
                this.recognizedMrzView.setText(LocaleController.getString("WalletScanCodeNotFound", NUM));
            } else {
                this.recognizedMrzView.setText(LocaleController.getString("AuthAnotherClientNotFound", NUM));
            }
            anonymousClass3.addView(this.recognizedMrzView);
            if (this.needGalleryButton) {
                this.galleryButton = new ImageView(context);
                this.galleryButton.setScaleType(ScaleType.CENTER);
                this.galleryButton.setImageResource(NUM);
                this.galleryButton.setBackgroundDrawable(Theme.createSelectorDrawableFromDrawables(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM), Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM)));
                anonymousClass3.addView(this.galleryButton);
                this.galleryButton.setOnClickListener(new -$$Lambda$CameraScanActivity$elYH43-16s4YXL1e30sBSwZ-5V8(this));
            }
            this.flashButton = new ImageView(context);
            this.flashButton.setScaleType(ScaleType.CENTER);
            this.flashButton.setImageResource(NUM);
            this.flashButton.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM));
            anonymousClass3.addView(this.flashButton);
            this.flashButton.setOnClickListener(new -$$Lambda$CameraScanActivity$KKa95LeROqiNbgpnOnp7zBLDcDQ(this));
        }
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
        this.fragmentView.setKeepScreenOn(true);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$CameraScanActivity(View view) {
        if (getParentActivity() != null) {
            if (VERSION.SDK_INT >= 23) {
                if (getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    getParentActivity().requestPermissions(new String[]{r0}, 4);
                    return;
                }
            }
            PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(10, false, false, null);
            photoAlbumPickerActivity.setMaxSelectedPhotos(1, false);
            photoAlbumPickerActivity.setAllowSearchImages(false);
            photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                public void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList, boolean z, int i) {
                    try {
                        if (!arrayList.isEmpty()) {
                            SendingMediaInfo sendingMediaInfo = (SendingMediaInfo) arrayList.get(0);
                            if (sendingMediaInfo.path != null) {
                                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                                String access$1800 = CameraScanActivity.this.tryReadQr(null, null, 0, 0, 0, ImageLoader.loadBitmap(sendingMediaInfo.path, null, (float) realScreenSize.x, (float) realScreenSize.y, true));
                                if (access$1800 != null) {
                                    if (CameraScanActivity.this.delegate != null) {
                                        CameraScanActivity.this.delegate.didFindQr(access$1800);
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
                        FileLog.e(e);
                    }
                }
            });
            presentFragment(photoAlbumPickerActivity);
        }
    }

    public /* synthetic */ void lambda$createView$4$CameraScanActivity(View view) {
        CameraSession cameraSession = this.cameraView.getCameraSession();
        if (cameraSession != null) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) this.flashButton.getBackground();
            AnimatorSet animatorSet = this.flashAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.flashAnimator = null;
            }
            this.flashAnimator = new AnimatorSet();
            Property property = AnimationProperties.SHAPE_DRAWABLE_ALPHA;
            int[] iArr = new int[1];
            iArr[0] = this.flashButton.getTag() == null ? 68 : 34;
            ObjectAnimator.ofInt(shapeDrawable, property, iArr).addUpdateListener(new -$$Lambda$CameraScanActivity$N1Wh2GuhHlYp6ViAs-1ebaly21k(this));
            this.flashAnimator.playTogether(new Animator[]{r0});
            this.flashAnimator.setDuration(200);
            this.flashAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.flashAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CameraScanActivity.this.flashAnimator = null;
                }
            });
            this.flashAnimator.start();
            if (this.flashButton.getTag() == null) {
                this.flashButton.setTag(Integer.valueOf(1));
                cameraSession.setTorchEnabled(true);
                return;
            }
            this.flashButton.setTag(null);
            cameraSession.setTorchEnabled(false);
        }
    }

    public /* synthetic */ void lambda$null$3$CameraScanActivity(ValueAnimator valueAnimator) {
        this.flashButton.invalidate();
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1 && i == 11 && intent != null && intent.getData() != null) {
            try {
                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                String tryReadQr = tryReadQr(null, null, 0, 0, 0, ImageLoader.loadBitmap(null, intent.getData(), (float) realScreenSize.x, (float) realScreenSize.y, true));
                if (tryReadQr != null) {
                    if (this.delegate != null) {
                        this.delegate.didFindQr(tryReadQr);
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
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.destroy(z, runnable);
            this.cameraView = null;
        }
        this.backgroundHandlerThread.quitSafely();
    }

    private void startRecognizing() {
        this.backgroundHandlerThread.start();
        this.handler = new Handler(this.backgroundHandlerThread.getLooper());
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (CameraScanActivity.this.cameraView != null && !CameraScanActivity.this.recognized && CameraScanActivity.this.cameraView.getCameraSession() != null) {
                    CameraScanActivity.this.cameraView.getCameraSession().setOneShotPreviewCallback(CameraScanActivity.this);
                    AndroidUtilities.runOnUIThread(this, 500);
                }
            }
        });
    }

    private void showErrorAlert() {
        AlertsCreator.createSimpleAlert(getParentActivity(), LocaleController.getString("WalletQRCode", NUM), LocaleController.getString("WalletScanImageNotFound", NUM)).show();
    }

    private void onNoQrFound(boolean z) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$CameraScanActivity$m5kfdtWqcnP_KOQE_6erM--Ks0k(this, z));
    }

    public /* synthetic */ void lambda$onNoQrFound$5$CameraScanActivity(boolean z) {
        if (z) {
            showErrorAlert();
            return;
        }
        if (this.recognizedMrzView.getTag() != null) {
            this.recognizedMrzView.setTag(null);
            this.recognizedMrzView.animate().setDuration(200).alpha(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    private void onNoWalletFound(boolean z) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$CameraScanActivity$3xU_PldajQ8XL9oKbZAOvyjG1yA(this, z));
    }

    public /* synthetic */ void lambda$onNoWalletFound$6$CameraScanActivity(boolean z) {
        if (z) {
            showErrorAlert();
            return;
        }
        if (this.recognizedMrzView.getTag() == null) {
            this.recognizedMrzView.setTag(Integer.valueOf(1));
            this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void onPreviewFrame(byte[] bArr, Camera camera) {
        this.handler.post(new -$$Lambda$CameraScanActivity$FPC8ls1Kvfk3YwhYqcwlb81o_CY(this, bArr, camera));
    }

    public /* synthetic */ void lambda$onPreviewFrame$9$CameraScanActivity(byte[] bArr, Camera camera) {
        try {
            Size previewSize = this.cameraView.getPreviewSize();
            if (this.currentType == 0) {
                Result recognize = MrzRecognizer.recognize(bArr, previewSize.getWidth(), previewSize.getHeight(), this.cameraView.getCameraSession().getDisplayOrientation());
                if (recognize != null && !TextUtils.isEmpty(recognize.firstName) && !TextUtils.isEmpty(recognize.lastName) && !TextUtils.isEmpty(recognize.number) && recognize.birthDay != 0) {
                    if ((recognize.expiryDay != 0 || recognize.doesNotExpire) && recognize.gender != 0) {
                        this.recognized = true;
                        camera.stopPreview();
                        AndroidUtilities.runOnUIThread(new -$$Lambda$CameraScanActivity$kw6kHhNasqXZ7fVPUG2YhWbP1dw(this, recognize));
                        return;
                    }
                    return;
                }
                return;
            }
            camera.getParameters().getPreviewFormat();
            int min = (int) (((float) Math.min(previewSize.getWidth(), previewSize.getHeight())) / 1.5f);
            String tryReadQr = tryReadQr(bArr, previewSize, (previewSize.getWidth() - min) / 2, (previewSize.getHeight() - min) / 2, min, null);
            if (tryReadQr != null) {
                this.recognized = true;
                camera.stopPreview();
                AndroidUtilities.runOnUIThread(new -$$Lambda$CameraScanActivity$hj1aQIfoNw7CjX9y2HQOGw2mRHA(this, tryReadQr));
            }
        } catch (Throwable unused) {
            onNoQrFound(false);
        }
    }

    public /* synthetic */ void lambda$null$7$CameraScanActivity(Result result) {
        this.recognizedMrzView.setText(result.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(result);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MzFbAazkaEtZMbQ8aOmMfDz5rpA(this), 1200);
    }

    public /* synthetic */ void lambda$null$8$CameraScanActivity(String str) {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(str);
        }
        finishFragment();
    }

    private String tryReadQr(byte[] bArr, Size size, int i, int i2, int i3, Bitmap bitmap) {
        Bitmap bitmap2 = bitmap;
        boolean z = true;
        try {
            CharSequence charSequence;
            if (this.visionQrReader.isOperational()) {
                Frame build;
                Frame.Builder builder;
                if (bitmap2 != null) {
                    builder = new Frame.Builder();
                    builder.setBitmap(bitmap2);
                    build = builder.build();
                } else {
                    builder = new Frame.Builder();
                    builder.setImageData(ByteBuffer.wrap(bArr), size.getWidth(), size.getHeight(), 17);
                    build = builder.build();
                }
                SparseArray detect = this.visionQrReader.detect(build);
                charSequence = (detect == null || detect.size() <= 0) ? null : ((Barcode) detect.valueAt(0)).rawValue;
            } else {
                LuminanceSource rGBLuminanceSource;
                if (bitmap2 != null) {
                    int[] iArr = new int[(bitmap.getWidth() * bitmap.getHeight())];
                    bitmap.getPixels(iArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                    rGBLuminanceSource = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), iArr);
                } else {
                    LuminanceSource planarYUVLuminanceSource = new PlanarYUVLuminanceSource(bArr, size.getWidth(), size.getHeight(), i, i2, i3, i3, false);
                }
                com.google.zxing.Result decode = this.qrReader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(rGBLuminanceSource)));
                if (decode == null) {
                    onNoQrFound(bitmap2 != null);
                    return null;
                }
                charSequence = decode.getText();
            }
            if (TextUtils.isEmpty(charSequence)) {
                onNoQrFound(bitmap2 != null);
                return null;
            }
            if (this.needGalleryButton) {
                if (charSequence.startsWith("ton://transfer/")) {
                    Uri.parse(charSequence).getPath().replace("/", "");
                } else {
                    onNoWalletFound(bitmap2 != null);
                    return null;
                }
            } else if (!charSequence.startsWith("tg://login?token=")) {
                onNoWalletFound(false);
                return null;
            }
            return charSequence;
        } catch (Throwable unused) {
            if (bitmap2 == null) {
                z = false;
            }
            onNoQrFound(z);
            return null;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        if (this.currentType == 1) {
            return new ThemeDescription[0];
        }
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6")};
    }
}
