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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import org.telegram.ui.PhotoAlbumPickerActivity;

@TargetApi(18)
public class CameraScanActivity extends BaseFragment implements Camera.PreviewCallback {
    private HandlerThread backgroundHandlerThread = new HandlerThread("ScanCamera");
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
    /* access modifiers changed from: private */
    public ImageView galleryButton;
    private Handler handler;
    /* access modifiers changed from: private */
    public boolean needGalleryButton;
    /* access modifiers changed from: private */
    public Paint paint = new Paint();
    /* access modifiers changed from: private */
    public Path path = new Path();
    private QRCodeReader qrReader;
    /* access modifiers changed from: private */
    public boolean recognized;
    /* access modifiers changed from: private */
    public TextView recognizedMrzView;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private BarcodeDetector visionQrReader;

    public interface CameraScanActivityDelegate {

        /* renamed from: org.telegram.ui.CameraScanActivity$CameraScanActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didFindMrzInfo(CameraScanActivityDelegate cameraScanActivityDelegate, MrzRecognizer.Result result) {
            }

            public static void $default$didFindQr(CameraScanActivityDelegate cameraScanActivityDelegate, String str) {
            }
        }

        void didFindMrzInfo(MrzRecognizer.Result result);

        void didFindQr(String str);
    }

    static /* synthetic */ boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public static ActionBarLayout[] showAsSheet(BaseFragment baseFragment, boolean z, CameraScanActivityDelegate cameraScanActivityDelegate) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayoutArr = {new ActionBarLayout(baseFragment.getParentActivity())};
        final ActionBarLayout[] actionBarLayoutArr2 = actionBarLayoutArr;
        final boolean z2 = z;
        final CameraScanActivityDelegate cameraScanActivityDelegate2 = cameraScanActivityDelegate;
        new BottomSheet(baseFragment.getParentActivity(), false) {
            /* access modifiers changed from: protected */
            public boolean canDismissWithSwipe() {
                return false;
            }

            {
                actionBarLayoutArr2[0].init(new ArrayList());
                AnonymousClass1 r1 = new CameraScanActivity(1) {
                    public void finishFragment() {
                        AnonymousClass1.this.dismiss();
                    }

                    public void removeSelfFromStack() {
                        AnonymousClass1.this.dismiss();
                    }
                };
                boolean unused = r1.needGalleryButton = z2;
                actionBarLayoutArr2[0].addFragmentToStack(r1);
                actionBarLayoutArr2[0].showLastFragment();
                ActionBarLayout actionBarLayout = actionBarLayoutArr2[0];
                int i = this.backgroundPaddingLeft;
                actionBarLayout.setPadding(i, 0, i, 0);
                r1.setDelegate(cameraScanActivityDelegate2);
                this.containerView = actionBarLayoutArr2[0];
                setApplyBottomPadding(false);
                setApplyBottomPadding(false);
                setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        CameraScanActivity.this.onFragmentDestroy();
                    }
                });
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
        CameraController.getInstance().initCamera(new Runnable() {
            public final void run() {
                CameraScanActivity.this.lambda$new$0$CameraScanActivity();
            }
        });
        this.currentType = i;
        if (i == 1) {
            this.qrReader = new QRCodeReader();
            BarcodeDetector.Builder builder = new BarcodeDetector.Builder(ApplicationLoader.applicationContext);
            builder.setBarcodeFormats(256);
            this.visionQrReader = builder.build();
        }
    }

    public /* synthetic */ void lambda$new$0$CameraScanActivity() {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.initCamera();
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

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet() && this.currentType != 1) {
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
        this.cornerPaint.setStyle(Paint.Style.STROKE);
        this.cornerPaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.cornerPaint.setStrokeJoin(Paint.Join.ROUND);
        AnonymousClass3 r0 = new ViewGroup(context) {
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
                CameraScanActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                CameraScanActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.9f), NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                setMeasuredDimension(size, size2);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                int i6 = i3 - i;
                int i7 = i4 - i2;
                if (CameraScanActivity.this.currentType == 0) {
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight() + 0);
                    int i8 = (int) (((float) i7) * 0.65f);
                    CameraScanActivity.this.titleTextView.layout(0, i8, CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + i8);
                    CameraScanActivity.this.recognizedMrzView.setTextSize(0, (float) (CameraScanActivity.this.cameraView.getMeasuredHeight() / 22));
                    CameraScanActivity.this.recognizedMrzView.setPadding(0, 0, 0, CameraScanActivity.this.cameraView.getMeasuredHeight() / 15);
                } else {
                    CameraScanActivity.this.actionBar.layout(0, 0, CameraScanActivity.this.actionBar.getMeasuredWidth(), CameraScanActivity.this.actionBar.getMeasuredHeight());
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight());
                    int min = (int) (((float) Math.min(CameraScanActivity.this.cameraView.getWidth(), CameraScanActivity.this.cameraView.getHeight())) / 1.5f);
                    int measuredHeight = (((CameraScanActivity.this.cameraView.getMeasuredHeight() - min) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(30.0f);
                    CameraScanActivity.this.titleTextView.layout(0, measuredHeight, CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + measuredHeight);
                    CameraScanActivity.this.recognizedMrzView.layout(0, getMeasuredHeight() - CameraScanActivity.this.recognizedMrzView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                    if (CameraScanActivity.this.needGalleryButton) {
                        i5 = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) + AndroidUtilities.dp(35.0f);
                    } else {
                        i5 = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - (CameraScanActivity.this.flashButton.getMeasuredWidth() / 2);
                    }
                    int measuredHeight2 = ((CameraScanActivity.this.cameraView.getMeasuredHeight() - min) / 2) + min + AndroidUtilities.dp(30.0f);
                    CameraScanActivity.this.flashButton.layout(i5, measuredHeight2, CameraScanActivity.this.flashButton.getMeasuredWidth() + i5, CameraScanActivity.this.flashButton.getMeasuredHeight() + measuredHeight2);
                    if (CameraScanActivity.this.galleryButton != null) {
                        int measuredWidth = ((CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - AndroidUtilities.dp(35.0f)) - CameraScanActivity.this.galleryButton.getMeasuredWidth();
                        CameraScanActivity.this.galleryButton.layout(measuredWidth, measuredHeight2, CameraScanActivity.this.galleryButton.getMeasuredWidth() + measuredWidth, CameraScanActivity.this.galleryButton.getMeasuredHeight() + measuredHeight2);
                    }
                }
                int i9 = (int) (((float) i7) * 0.74f);
                int i10 = (int) (((float) i6) * 0.05f);
                CameraScanActivity.this.descriptionText.layout(i10, i9, CameraScanActivity.this.descriptionText.getMeasuredWidth() + i10, CameraScanActivity.this.descriptionText.getMeasuredHeight() + i9);
            }

            /* access modifiers changed from: protected */
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
                int i2 = min + width;
                float f7 = (float) i2;
                boolean z = drawChild;
                float f8 = f7;
                canvas.drawRect(f7, f4, (float) view.getMeasuredWidth(), f5, CameraScanActivity.this.paint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f6, (float) (AndroidUtilities.dp(20.0f) + height));
                CameraScanActivity.this.path.lineTo(f6, f);
                CameraScanActivity.this.path.lineTo((float) (AndroidUtilities.dp(20.0f) + width), f);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f8, (float) (height + AndroidUtilities.dp(20.0f)));
                CameraScanActivity.this.path.lineTo(f8, f);
                CameraScanActivity.this.path.lineTo((float) (i2 - AndroidUtilities.dp(20.0f)), f);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f6, (float) (i - AndroidUtilities.dp(20.0f)));
                CameraScanActivity.this.path.lineTo(f6, f2);
                CameraScanActivity.this.path.lineTo((float) (width + AndroidUtilities.dp(20.0f)), f2);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                CameraScanActivity.this.path.reset();
                CameraScanActivity.this.path.moveTo(f8, (float) (i - AndroidUtilities.dp(20.0f)));
                CameraScanActivity.this.path.lineTo(f8, f2);
                CameraScanActivity.this.path.lineTo((float) (i2 - AndroidUtilities.dp(20.0f)), f2);
                canvas2.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                return z;
            }
        };
        r0.setOnTouchListener($$Lambda$CameraScanActivity$coK9fFDxJKElfMa1PuRYWa0XVmg.INSTANCE);
        this.fragmentView = r0;
        CameraView cameraView2 = new CameraView(context, false);
        this.cameraView = cameraView2;
        cameraView2.setUseMaxPreview(true);
        this.cameraView.setOptimizeForBarcode(true);
        this.cameraView.setDelegate(new CameraView.CameraViewDelegate() {
            public void onCameraCreated(Camera camera) {
            }

            public void onCameraInit() {
                CameraScanActivity.this.startRecognizing();
            }
        });
        r0.addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.currentType == 0) {
            this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        } else {
            this.actionBar.setBackgroundDrawable((Drawable) null);
            this.actionBar.setAddToContainer(false);
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            r0.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
            r0.addView(this.actionBar);
        }
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setGravity(1);
        this.titleTextView.setTextSize(1, 24.0f);
        r0.addView(this.titleTextView);
        TextView textView2 = new TextView(context);
        this.descriptionText = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        r0.addView(this.descriptionText);
        TextView textView3 = new TextView(context);
        this.recognizedMrzView = textView3;
        textView3.setTextColor(-1);
        this.recognizedMrzView.setGravity(81);
        this.recognizedMrzView.setAlpha(0.0f);
        if (this.currentType == 0) {
            this.titleTextView.setText(LocaleController.getString("PassportScanPassport", NUM));
            this.descriptionText.setText(LocaleController.getString("PassportScanPassportInfo", NUM));
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.recognizedMrzView.setTypeface(Typeface.MONOSPACE);
            this.cameraView.addView(this.recognizedMrzView);
        } else {
            if (!this.needGalleryButton) {
                this.titleTextView.setText(LocaleController.getString("AuthAnotherClientScan", NUM));
            }
            this.titleTextView.setTextColor(-1);
            this.recognizedMrzView.setTextSize(1, 16.0f);
            this.recognizedMrzView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            if (!this.needGalleryButton) {
                this.recognizedMrzView.setText(LocaleController.getString("AuthAnotherClientNotFound", NUM));
            }
            r0.addView(this.recognizedMrzView);
            if (this.needGalleryButton) {
                ImageView imageView = new ImageView(context);
                this.galleryButton = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.galleryButton.setImageResource(NUM);
                this.galleryButton.setBackgroundDrawable(Theme.createSelectorDrawableFromDrawables(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM), Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM)));
                r0.addView(this.galleryButton);
                this.galleryButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        CameraScanActivity.this.lambda$createView$2$CameraScanActivity(view);
                    }
                });
            }
            ImageView imageView2 = new ImageView(context);
            this.flashButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.flashButton.setImageResource(NUM);
            this.flashButton.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM));
            r0.addView(this.flashButton);
            this.flashButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    CameraScanActivity.this.lambda$createView$4$CameraScanActivity(view);
                }
            });
        }
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
        this.fragmentView.setKeepScreenOn(true);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$CameraScanActivity(View view) {
        if (getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(10, false, false, (ChatActivity) null);
                photoAlbumPickerActivity.setMaxSelectedPhotos(1, false);
                photoAlbumPickerActivity.setAllowSearchImages(false);
                photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                        try {
                            if (!arrayList.isEmpty()) {
                                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = arrayList.get(0);
                                if (sendingMediaInfo.path != null) {
                                    Point realScreenSize = AndroidUtilities.getRealScreenSize();
                                    String access$1800 = CameraScanActivity.this.tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap(sendingMediaInfo.path, (Uri) null, (float) realScreenSize.x, (float) realScreenSize.y, true));
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
            Property<ShapeDrawable, Integer> property = AnimationProperties.SHAPE_DRAWABLE_ALPHA;
            int[] iArr = new int[1];
            iArr[0] = this.flashButton.getTag() == null ? 68 : 34;
            ObjectAnimator ofInt = ObjectAnimator.ofInt(shapeDrawable, property, iArr);
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CameraScanActivity.this.lambda$null$3$CameraScanActivity(valueAnimator);
                }
            });
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

    public /* synthetic */ void lambda$null$3$CameraScanActivity(ValueAnimator valueAnimator) {
        this.flashButton.invalidate();
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1 && i == 11 && intent != null && intent.getData() != null) {
            try {
                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                String tryReadQr = tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap((String) null, intent.getData(), (float) realScreenSize.x, (float) realScreenSize.y, true));
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
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.destroy(z, runnable);
            this.cameraView = null;
        }
        this.backgroundHandlerThread.quitSafely();
    }

    /* access modifiers changed from: private */
    public void startRecognizing() {
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

    private void onNoQrFound() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                CameraScanActivity.this.lambda$onNoQrFound$5$CameraScanActivity();
            }
        });
    }

    public /* synthetic */ void lambda$onNoQrFound$5$CameraScanActivity() {
        if (this.recognizedMrzView.getTag() != null) {
            this.recognizedMrzView.setTag((Object) null);
            this.recognizedMrzView.animate().setDuration(200).alpha(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void onPreviewFrame(byte[] bArr, Camera camera) {
        this.handler.post(new Runnable(bArr, camera) {
            public final /* synthetic */ byte[] f$1;
            public final /* synthetic */ Camera f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CameraScanActivity.this.lambda$onPreviewFrame$8$CameraScanActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$onPreviewFrame$8$CameraScanActivity(byte[] bArr, Camera camera) {
        try {
            Size previewSize = this.cameraView.getPreviewSize();
            if (this.currentType == 0) {
                MrzRecognizer.Result recognize = MrzRecognizer.recognize(bArr, previewSize.getWidth(), previewSize.getHeight(), this.cameraView.getCameraSession().getDisplayOrientation());
                if (recognize != null && !TextUtils.isEmpty(recognize.firstName) && !TextUtils.isEmpty(recognize.lastName) && !TextUtils.isEmpty(recognize.number) && recognize.birthDay != 0) {
                    if ((recognize.expiryDay != 0 || recognize.doesNotExpire) && recognize.gender != 0) {
                        this.recognized = true;
                        camera.stopPreview();
                        AndroidUtilities.runOnUIThread(new Runnable(recognize) {
                            public final /* synthetic */ MrzRecognizer.Result f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                CameraScanActivity.this.lambda$null$6$CameraScanActivity(this.f$1);
                            }
                        });
                        return;
                    }
                    return;
                }
                return;
            }
            camera.getParameters().getPreviewFormat();
            int min = (int) (((float) Math.min(previewSize.getWidth(), previewSize.getHeight())) / 1.5f);
            String tryReadQr = tryReadQr(bArr, previewSize, (previewSize.getWidth() - min) / 2, (previewSize.getHeight() - min) / 2, min, (Bitmap) null);
            if (tryReadQr != null) {
                this.recognized = true;
                camera.stopPreview();
                AndroidUtilities.runOnUIThread(new Runnable(tryReadQr) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        CameraScanActivity.this.lambda$null$7$CameraScanActivity(this.f$1);
                    }
                });
            }
        } catch (Throwable unused) {
            onNoQrFound();
        }
    }

    public /* synthetic */ void lambda$null$6$CameraScanActivity(MrzRecognizer.Result result) {
        this.recognizedMrzView.setText(result.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(result);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                CameraScanActivity.this.finishFragment();
            }
        }, 1200);
    }

    public /* synthetic */ void lambda$null$7$CameraScanActivity(String str) {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(str);
        }
        finishFragment();
    }

    /* JADX WARNING: type inference failed for: r1v10, types: [com.google.zxing.RGBLuminanceSource] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String tryReadQr(byte[] r22, org.telegram.messenger.camera.Size r23, int r24, int r25, int r26, android.graphics.Bitmap r27) {
        /*
            r21 = this;
            r0 = r21
            r9 = r27
            r10 = 0
            com.google.android.gms.vision.barcode.BarcodeDetector r1 = r0.visionQrReader     // Catch:{ all -> 0x00e9 }
            boolean r1 = r1.isOperational()     // Catch:{ all -> 0x00e9 }
            if (r1 == 0) goto L_0x0050
            if (r9 == 0) goto L_0x001c
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x00e9 }
            r1.<init>()     // Catch:{ all -> 0x00e9 }
            r1.setBitmap(r9)     // Catch:{ all -> 0x00e9 }
            com.google.android.gms.vision.Frame r1 = r1.build()     // Catch:{ all -> 0x00e9 }
            goto L_0x0036
        L_0x001c:
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x00e9 }
            r1.<init>()     // Catch:{ all -> 0x00e9 }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.wrap(r22)     // Catch:{ all -> 0x00e9 }
            int r3 = r23.getWidth()     // Catch:{ all -> 0x00e9 }
            int r4 = r23.getHeight()     // Catch:{ all -> 0x00e9 }
            r5 = 17
            r1.setImageData(r2, r3, r4, r5)     // Catch:{ all -> 0x00e9 }
            com.google.android.gms.vision.Frame r1 = r1.build()     // Catch:{ all -> 0x00e9 }
        L_0x0036:
            com.google.android.gms.vision.barcode.BarcodeDetector r2 = r0.visionQrReader     // Catch:{ all -> 0x00e9 }
            android.util.SparseArray r1 = r2.detect(r1)     // Catch:{ all -> 0x00e9 }
            if (r1 == 0) goto L_0x004e
            int r2 = r1.size()     // Catch:{ all -> 0x00e9 }
            if (r2 <= 0) goto L_0x004e
            r2 = 0
            java.lang.Object r1 = r1.valueAt(r2)     // Catch:{ all -> 0x00e9 }
            com.google.android.gms.vision.barcode.Barcode r1 = (com.google.android.gms.vision.barcode.Barcode) r1     // Catch:{ all -> 0x00e9 }
            java.lang.String r1 = r1.rawValue     // Catch:{ all -> 0x00e9 }
            goto L_0x00b5
        L_0x004e:
            r1 = r10
            goto L_0x00b5
        L_0x0050:
            if (r9 == 0) goto L_0x0081
            int r1 = r27.getWidth()     // Catch:{ all -> 0x00e9 }
            int r2 = r27.getHeight()     // Catch:{ all -> 0x00e9 }
            int r1 = r1 * r2
            int[] r11 = new int[r1]     // Catch:{ all -> 0x00e9 }
            r3 = 0
            int r4 = r27.getWidth()     // Catch:{ all -> 0x00e9 }
            r5 = 0
            r6 = 0
            int r7 = r27.getWidth()     // Catch:{ all -> 0x00e9 }
            int r8 = r27.getHeight()     // Catch:{ all -> 0x00e9 }
            r1 = r27
            r2 = r11
            r1.getPixels(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00e9 }
            com.google.zxing.RGBLuminanceSource r1 = new com.google.zxing.RGBLuminanceSource     // Catch:{ all -> 0x00e9 }
            int r2 = r27.getWidth()     // Catch:{ all -> 0x00e9 }
            int r3 = r27.getHeight()     // Catch:{ all -> 0x00e9 }
            r1.<init>(r2, r3, r11)     // Catch:{ all -> 0x00e9 }
            goto L_0x009b
        L_0x0081:
            com.google.zxing.PlanarYUVLuminanceSource r1 = new com.google.zxing.PlanarYUVLuminanceSource     // Catch:{ all -> 0x00e9 }
            int r14 = r23.getWidth()     // Catch:{ all -> 0x00e9 }
            int r15 = r23.getHeight()     // Catch:{ all -> 0x00e9 }
            r20 = 0
            r12 = r1
            r13 = r22
            r16 = r24
            r17 = r25
            r18 = r26
            r19 = r26
            r12.<init>(r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x00e9 }
        L_0x009b:
            com.google.zxing.qrcode.QRCodeReader r2 = r0.qrReader     // Catch:{ all -> 0x00e9 }
            com.google.zxing.BinaryBitmap r3 = new com.google.zxing.BinaryBitmap     // Catch:{ all -> 0x00e9 }
            com.google.zxing.common.GlobalHistogramBinarizer r4 = new com.google.zxing.common.GlobalHistogramBinarizer     // Catch:{ all -> 0x00e9 }
            r4.<init>(r1)     // Catch:{ all -> 0x00e9 }
            r3.<init>(r4)     // Catch:{ all -> 0x00e9 }
            com.google.zxing.Result r1 = r2.decode(r3)     // Catch:{ all -> 0x00e9 }
            if (r1 != 0) goto L_0x00b1
            r21.onNoQrFound()     // Catch:{ all -> 0x00e9 }
            return r10
        L_0x00b1:
            java.lang.String r1 = r1.getText()     // Catch:{ all -> 0x00e9 }
        L_0x00b5:
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x00e9 }
            if (r2 == 0) goto L_0x00bf
            r21.onNoQrFound()     // Catch:{ all -> 0x00e9 }
            return r10
        L_0x00bf:
            boolean r2 = r0.needGalleryButton     // Catch:{ all -> 0x00e9 }
            if (r2 == 0) goto L_0x00dc
            java.lang.String r2 = "ton://transfer/"
            boolean r2 = r1.startsWith(r2)     // Catch:{ all -> 0x00e9 }
            if (r2 != 0) goto L_0x00cc
            return r10
        L_0x00cc:
            android.net.Uri r2 = android.net.Uri.parse(r1)     // Catch:{ all -> 0x00e9 }
            java.lang.String r2 = r2.getPath()     // Catch:{ all -> 0x00e9 }
            java.lang.String r3 = "/"
            java.lang.String r4 = ""
            r2.replace(r3, r4)     // Catch:{ all -> 0x00e9 }
            goto L_0x00e8
        L_0x00dc:
            java.lang.String r2 = "tg://login?token="
            boolean r2 = r1.startsWith(r2)     // Catch:{ all -> 0x00e9 }
            if (r2 != 0) goto L_0x00e8
            r21.onNoQrFound()     // Catch:{ all -> 0x00e9 }
            return r10
        L_0x00e8:
            return r1
        L_0x00e9:
            r21.onNoQrFound()
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CameraScanActivity.tryReadQr(byte[], org.telegram.messenger.camera.Size, int, int, int, android.graphics.Bitmap):java.lang.String");
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        if (this.currentType == 1) {
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
