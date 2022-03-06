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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
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
    private SpringAnimation qrAppearing = null;
    /* access modifiers changed from: private */
    public float qrAppearingValue = 0.0f;
    private QRCodeReader qrReader;
    /* access modifiers changed from: private */
    public boolean recognized;
    /* access modifiers changed from: private */
    public TextView recognizedMrzView;
    private Runnable requestShot = new Runnable() {
        public void run() {
            if (CameraScanActivity.this.cameraView != null && !CameraScanActivity.this.recognized && CameraScanActivity.this.cameraView.getCameraSession() != null) {
                CameraScanActivity cameraScanActivity = CameraScanActivity.this;
                cameraScanActivity.onShot(cameraScanActivity.cameraView.getTextureView().getBitmap());
            }
        }
    };
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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public static ActionBarLayout[] showAsSheet(BaseFragment baseFragment, boolean z, int i, CameraScanActivityDelegate cameraScanActivityDelegate) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayoutArr = {new ActionBarLayout(baseFragment.getParentActivity())};
        new BottomSheet(baseFragment.getParentActivity(), false, actionBarLayoutArr, i, z, cameraScanActivityDelegate) {
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
                boolean unused = r3.needGalleryButton = r6;
                r4[0].addFragmentToStack(r3);
                r4[0].showLastFragment();
                ActionBarLayout actionBarLayout = r4[0];
                int i = this.backgroundPaddingLeft;
                actionBarLayout.setPadding(i, 0, i, 0);
                r3.setDelegate(r7);
                this.containerView = r4[0];
                setApplyBottomPadding(false);
                setApplyBottomPadding(false);
                setOnDismissListener(new CameraScanActivity$1$$ExternalSyntheticLambda0(r3));
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
        this.cornerPaint.setStyle(Paint.Style.STROKE);
        this.cornerPaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.cornerPaint.setStrokeJoin(Paint.Join.ROUND);
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
                int i12 = (int) (((float) i9) * 0.74f);
                int i13 = (int) (((float) i8) * 0.05f);
                CameraScanActivity.this.descriptionText.layout(i13, i12, CameraScanActivity.this.descriptionText.getMeasuredWidth() + i13, CameraScanActivity.this.descriptionText.getMeasuredHeight() + i12);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (CameraScanActivity.this.isQr() && view == CameraScanActivity.this.cameraView) {
                    int min = (int) (((float) Math.min(view.getWidth(), view.getHeight())) / 1.5f);
                    int width = (view.getWidth() - min) / 2;
                    int height = (view.getHeight() - min) / 2;
                    float f = (float) width;
                    CameraScanActivity.this.path.moveTo(f, (float) (AndroidUtilities.dp(20.0f) + height));
                    float f2 = (float) height;
                    CameraScanActivity.this.path.lineTo(f, f2);
                    CameraScanActivity.this.path.lineTo((float) (AndroidUtilities.dp(20.0f) + width), f2);
                    int i = width + min;
                    float f3 = (float) i;
                    CameraScanActivity.this.path.moveTo(f3, (float) (AndroidUtilities.dp(20.0f) + height));
                    CameraScanActivity.this.path.lineTo(f3, f2);
                    CameraScanActivity.this.path.lineTo((float) (i - AndroidUtilities.dp(20.0f)), f2);
                    int i2 = height + min;
                    CameraScanActivity.this.path.moveTo(f, (float) (i2 - AndroidUtilities.dp(20.0f)));
                    float f4 = (float) i2;
                    CameraScanActivity.this.path.lineTo(f, f4);
                    CameraScanActivity.this.path.lineTo((float) (width + AndroidUtilities.dp(20.0f)), f4);
                    CameraScanActivity.this.path.moveTo(f3, (float) (i2 - AndroidUtilities.dp(20.0f)));
                    CameraScanActivity.this.path.lineTo(f3, f4);
                    CameraScanActivity.this.path.lineTo((float) (i - AndroidUtilities.dp(20.0f)), f4);
                    CameraScanActivity.this.paint.setAlpha((int) (Math.min(1.0f, CameraScanActivity.this.qrAppearingValue) * 127.0f));
                    canvas.drawRect(0.0f, 0.0f, (float) view.getMeasuredWidth(), f2, CameraScanActivity.this.paint);
                    canvas.drawRect(0.0f, f4, (float) view.getMeasuredWidth(), (float) view.getMeasuredHeight(), CameraScanActivity.this.paint);
                    canvas.drawRect(0.0f, f2, f, f4, CameraScanActivity.this.paint);
                    canvas.drawRect(f3, f2, (float) view.getMeasuredWidth(), f4, CameraScanActivity.this.paint);
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                }
                return drawChild;
            }
        };
        r2.setOnTouchListener(CameraScanActivity$$ExternalSyntheticLambda3.INSTANCE);
        this.fragmentView = r2;
        CameraView cameraView2 = new CameraView(context2, false);
        this.cameraView = cameraView2;
        cameraView2.setUseMaxPreview(true);
        this.cameraView.setOptimizeForBarcode(true);
        this.cameraView.setDelegate(new CameraScanActivity$$ExternalSyntheticLambda14(this));
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
                this.galleryButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda2(this));
            }
            ImageView imageView2 = new ImageView(context2);
            this.flashButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.flashButton.setImageResource(NUM);
            this.flashButton.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM));
            r2.addView(this.flashButton);
            this.flashButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda1(this));
        }
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
        this.fragmentView.setKeepScreenOn(true);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3() {
        startRecognizing();
        if (isQr()) {
            SpringAnimation springAnimation = this.qrAppearing;
            if (springAnimation != null) {
                springAnimation.cancel();
                this.qrAppearing = null;
            }
            SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(0.0f));
            this.qrAppearing = springAnimation2;
            springAnimation2.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda5(this));
            this.qrAppearing.addEndListener(new CameraScanActivity$$ExternalSyntheticLambda4(this));
            this.qrAppearing.setSpring(new SpringForce(500.0f));
            this.qrAppearing.getSpring().setDampingRatio(0.8f);
            this.qrAppearing.getSpring().setStiffness(250.0f);
            this.qrAppearing.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.qrAppearingValue = f / 500.0f;
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        SpringAnimation springAnimation = this.qrAppearing;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.qrAppearing = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view) {
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
                                    String access$1900 = CameraScanActivity.this.tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap(sendingMediaInfo.path, (Uri) null, (float) realScreenSize.x, (float) realScreenSize.y, true));
                                    if (access$1900 != null) {
                                        if (CameraScanActivity.this.delegate != null) {
                                            CameraScanActivity.this.delegate.didFindQr(access$1900);
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
    public /* synthetic */ void lambda$createView$6(View view) {
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
            ofInt.addUpdateListener(new CameraScanActivity$$ExternalSyntheticLambda0(this));
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
    public /* synthetic */ void lambda$createView$5(ValueAnimator valueAnimator) {
        this.flashButton.invalidate();
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1 && i == 11 && intent != null && intent.getData() != null) {
            try {
                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                String tryReadQr = tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap((String) null, intent.getData(), (float) realScreenSize.x, (float) realScreenSize.y, true));
                if (tryReadQr != null) {
                    CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
                    if (cameraScanActivityDelegate != null) {
                        cameraScanActivityDelegate.didFindQr(tryReadQr);
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
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda7(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNoQrFound$7() {
        if (this.recognizedMrzView.getTag() != null) {
            this.recognizedMrzView.setTag((Object) null);
            this.recognizedMrzView.animate().setDuration(200).alpha(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void onPreviewFrame(byte[] bArr, Camera camera) {
        this.handler.post(new CameraScanActivity$$ExternalSyntheticLambda13(this, bArr, camera));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreviewFrame$10(byte[] bArr, Camera camera) {
        try {
            Size previewSize = this.cameraView.getPreviewSize();
            if (this.currentType == 0) {
                MrzRecognizer.Result recognize = MrzRecognizer.recognize(bArr, previewSize.getWidth(), previewSize.getHeight(), this.cameraView.getCameraSession().getDisplayOrientation());
                if (recognize != null && !TextUtils.isEmpty(recognize.firstName) && !TextUtils.isEmpty(recognize.lastName) && !TextUtils.isEmpty(recognize.number) && recognize.birthDay != 0) {
                    if ((recognize.expiryDay != 0 || recognize.doesNotExpire) && recognize.gender != 0) {
                        this.recognized = true;
                        camera.stopPreview();
                        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda11(this, recognize));
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
                AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda9(this, tryReadQr));
            }
        } catch (Throwable unused) {
            onNoQrFound();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreviewFrame$8(MrzRecognizer.Result result) {
        this.recognizedMrzView.setText(result.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(result);
        }
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda6(this), 1200);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreviewFrame$9(String str) {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(str);
        }
        finishFragment();
    }

    public void onShot(Bitmap bitmap) {
        this.handler.post(new CameraScanActivity$$ExternalSyntheticLambda8(this, bitmap));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShot$13(Bitmap bitmap) {
        try {
            Size previewSize = this.cameraView.getPreviewSize();
            if (this.currentType == 0) {
                MrzRecognizer.Result recognize = MrzRecognizer.recognize(bitmap, false);
                if (recognize != null && !TextUtils.isEmpty(recognize.firstName) && !TextUtils.isEmpty(recognize.lastName) && !TextUtils.isEmpty(recognize.number) && recognize.birthDay != 0 && ((recognize.expiryDay != 0 || recognize.doesNotExpire) && recognize.gender != 0)) {
                    this.recognized = true;
                    CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
                    AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda12(this, recognize));
                }
            } else {
                int min = (int) (((float) Math.min(previewSize.getWidth(), previewSize.getHeight())) / 1.5f);
                String tryReadQr = tryReadQr((byte[]) null, previewSize, (previewSize.getWidth() - min) / 2, (previewSize.getHeight() - min) / 2, min, bitmap);
                if (tryReadQr != null) {
                    this.recognized = true;
                    CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
                    AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda10(this, tryReadQr));
                    return;
                }
            }
        } catch (Throwable unused) {
            onNoQrFound();
        }
        AndroidUtilities.runOnUIThread(this.requestShot, 500);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShot$11(MrzRecognizer.Result result) {
        this.recognizedMrzView.setText(result.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(result);
        }
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda6(this), 1200);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShot$12(String str) {
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
            com.google.android.gms.vision.barcode.BarcodeDetector r1 = r0.visionQrReader     // Catch:{ all -> 0x00eb }
            boolean r1 = r1.isOperational()     // Catch:{ all -> 0x00eb }
            if (r1 == 0) goto L_0x0052
            if (r9 == 0) goto L_0x001d
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x00eb }
            r1.<init>()     // Catch:{ all -> 0x00eb }
            com.google.android.gms.vision.Frame$Builder r1 = r1.setBitmap(r9)     // Catch:{ all -> 0x00eb }
            com.google.android.gms.vision.Frame r1 = r1.build()     // Catch:{ all -> 0x00eb }
            goto L_0x0038
        L_0x001d:
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x00eb }
            r1.<init>()     // Catch:{ all -> 0x00eb }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.wrap(r22)     // Catch:{ all -> 0x00eb }
            int r3 = r23.getWidth()     // Catch:{ all -> 0x00eb }
            int r4 = r23.getHeight()     // Catch:{ all -> 0x00eb }
            r5 = 17
            com.google.android.gms.vision.Frame$Builder r1 = r1.setImageData(r2, r3, r4, r5)     // Catch:{ all -> 0x00eb }
            com.google.android.gms.vision.Frame r1 = r1.build()     // Catch:{ all -> 0x00eb }
        L_0x0038:
            com.google.android.gms.vision.barcode.BarcodeDetector r2 = r0.visionQrReader     // Catch:{ all -> 0x00eb }
            android.util.SparseArray r1 = r2.detect(r1)     // Catch:{ all -> 0x00eb }
            if (r1 == 0) goto L_0x0050
            int r2 = r1.size()     // Catch:{ all -> 0x00eb }
            if (r2 <= 0) goto L_0x0050
            r2 = 0
            java.lang.Object r1 = r1.valueAt(r2)     // Catch:{ all -> 0x00eb }
            com.google.android.gms.vision.barcode.Barcode r1 = (com.google.android.gms.vision.barcode.Barcode) r1     // Catch:{ all -> 0x00eb }
            java.lang.String r1 = r1.rawValue     // Catch:{ all -> 0x00eb }
            goto L_0x00b7
        L_0x0050:
            r1 = r10
            goto L_0x00b7
        L_0x0052:
            if (r9 == 0) goto L_0x0083
            int r1 = r27.getWidth()     // Catch:{ all -> 0x00eb }
            int r2 = r27.getHeight()     // Catch:{ all -> 0x00eb }
            int r1 = r1 * r2
            int[] r11 = new int[r1]     // Catch:{ all -> 0x00eb }
            r3 = 0
            int r4 = r27.getWidth()     // Catch:{ all -> 0x00eb }
            r5 = 0
            r6 = 0
            int r7 = r27.getWidth()     // Catch:{ all -> 0x00eb }
            int r8 = r27.getHeight()     // Catch:{ all -> 0x00eb }
            r1 = r27
            r2 = r11
            r1.getPixels(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00eb }
            com.google.zxing.RGBLuminanceSource r1 = new com.google.zxing.RGBLuminanceSource     // Catch:{ all -> 0x00eb }
            int r2 = r27.getWidth()     // Catch:{ all -> 0x00eb }
            int r3 = r27.getHeight()     // Catch:{ all -> 0x00eb }
            r1.<init>(r2, r3, r11)     // Catch:{ all -> 0x00eb }
            goto L_0x009d
        L_0x0083:
            com.google.zxing.PlanarYUVLuminanceSource r1 = new com.google.zxing.PlanarYUVLuminanceSource     // Catch:{ all -> 0x00eb }
            int r14 = r23.getWidth()     // Catch:{ all -> 0x00eb }
            int r15 = r23.getHeight()     // Catch:{ all -> 0x00eb }
            r20 = 0
            r12 = r1
            r13 = r22
            r16 = r24
            r17 = r25
            r18 = r26
            r19 = r26
            r12.<init>(r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x00eb }
        L_0x009d:
            com.google.zxing.qrcode.QRCodeReader r2 = r0.qrReader     // Catch:{ all -> 0x00eb }
            com.google.zxing.BinaryBitmap r3 = new com.google.zxing.BinaryBitmap     // Catch:{ all -> 0x00eb }
            com.google.zxing.common.GlobalHistogramBinarizer r4 = new com.google.zxing.common.GlobalHistogramBinarizer     // Catch:{ all -> 0x00eb }
            r4.<init>(r1)     // Catch:{ all -> 0x00eb }
            r3.<init>(r4)     // Catch:{ all -> 0x00eb }
            com.google.zxing.Result r1 = r2.decode(r3)     // Catch:{ all -> 0x00eb }
            if (r1 != 0) goto L_0x00b3
            r21.onNoQrFound()     // Catch:{ all -> 0x00eb }
            return r10
        L_0x00b3:
            java.lang.String r1 = r1.getText()     // Catch:{ all -> 0x00eb }
        L_0x00b7:
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x00eb }
            if (r2 == 0) goto L_0x00c1
            r21.onNoQrFound()     // Catch:{ all -> 0x00eb }
            return r10
        L_0x00c1:
            boolean r2 = r0.needGalleryButton     // Catch:{ all -> 0x00eb }
            if (r2 == 0) goto L_0x00de
            java.lang.String r2 = "ton://transfer/"
            boolean r2 = r1.startsWith(r2)     // Catch:{ all -> 0x00eb }
            if (r2 != 0) goto L_0x00ce
            return r10
        L_0x00ce:
            android.net.Uri r2 = android.net.Uri.parse(r1)     // Catch:{ all -> 0x00eb }
            java.lang.String r2 = r2.getPath()     // Catch:{ all -> 0x00eb }
            java.lang.String r3 = "/"
            java.lang.String r4 = ""
            r2.replace(r3, r4)     // Catch:{ all -> 0x00eb }
            goto L_0x00ea
        L_0x00de:
            java.lang.String r2 = "tg://login?token="
            boolean r2 = r1.startsWith(r2)     // Catch:{ all -> 0x00eb }
            if (r2 != 0) goto L_0x00ea
            r21.onNoQrFound()     // Catch:{ all -> 0x00eb }
            return r10
        L_0x00ea:
            return r1
        L_0x00eb:
            r21.onNoQrFound()
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CameraScanActivity.tryReadQr(byte[], org.telegram.messenger.camera.Size, int, int, int, android.graphics.Bitmap):java.lang.String");
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
