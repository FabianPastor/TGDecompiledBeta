package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

public class CameraScanActivity extends BaseFragment implements Camera.PreviewCallback {
    public static final int TYPE_MRZ = 0;
    public static final int TYPE_QR = 1;
    public static final int TYPE_QR_LOGIN = 2;
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
        void didFindMrzInfo(MrzRecognizer.Result result);

        void didFindQr(String str);

        /* renamed from: org.telegram.ui.CameraScanActivity$CameraScanActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didFindMrzInfo(CameraScanActivityDelegate _this, MrzRecognizer.Result result) {
            }

            public static void $default$didFindQr(CameraScanActivityDelegate _this, String text) {
            }
        }
    }

    public static ActionBarLayout[] showAsSheet(BaseFragment parentFragment, boolean gallery, int type, CameraScanActivityDelegate delegate2) {
        if (parentFragment == null || parentFragment.getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayout = {new ActionBarLayout(parentFragment.getParentActivity())};
        new BottomSheet(parentFragment.getParentActivity(), false, actionBarLayout, type, gallery, delegate2) {
            final /* synthetic */ ActionBarLayout[] val$actionBarLayout;
            final /* synthetic */ CameraScanActivityDelegate val$delegate;
            final /* synthetic */ boolean val$gallery;
            final /* synthetic */ int val$type;

            {
                this.val$actionBarLayout = r6;
                this.val$type = r7;
                this.val$gallery = r8;
                this.val$delegate = r9;
                r6[0].init(new ArrayList());
                CameraScanActivity fragment = new CameraScanActivity(r7) {
                    public void finishFragment() {
                        AnonymousClass1.this.dismiss();
                    }

                    public void removeSelfFromStack() {
                        AnonymousClass1.this.dismiss();
                    }
                };
                boolean unused = fragment.needGalleryButton = r8;
                r6[0].addFragmentToStack(fragment);
                r6[0].showLastFragment();
                r6[0].setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
                fragment.setDelegate(r9);
                this.containerView = r6[0];
                setApplyBottomPadding(false);
                setApplyBottomPadding(false);
                setOnDismissListener(new CameraScanActivity$1$$ExternalSyntheticLambda0(fragment));
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
        }.show();
        return actionBarLayout;
    }

    public CameraScanActivity(int type) {
        CameraController.getInstance().initCamera(CameraScanActivity$$ExternalSyntheticLambda9.INSTANCE);
        this.currentType = type;
        if (isQr()) {
            this.qrReader = new QRCodeReader();
            this.visionQrReader = new BarcodeDetector.Builder(ApplicationLoader.applicationContext).setBarcodeFormats(256).build();
        }
    }

    static /* synthetic */ void lambda$new$0() {
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
        boolean z;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
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
        this.cornerPaint.setStyle(Paint.Style.STROKE);
        this.cornerPaint.setStrokeWidth((float) AndroidUtilities.dp(4.0f));
        this.cornerPaint.setStrokeJoin(Paint.Join.ROUND);
        ViewGroup viewGroup = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                CameraScanActivity.this.actionBar.measure(widthMeasureSpec, heightMeasureSpec);
                if (CameraScanActivity.this.currentType == 0) {
                    CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.704f), NUM));
                } else {
                    CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
                    CameraScanActivity.this.recognizedMrzView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                    if (CameraScanActivity.this.galleryButton != null) {
                        CameraScanActivity.this.galleryButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                    }
                    CameraScanActivity.this.flashButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
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
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight() + 0);
                    int y2 = (int) (((float) height) * 0.65f);
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), y2, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + y2);
                    CameraScanActivity.this.recognizedMrzView.setTextSize(0, (float) (CameraScanActivity.this.cameraView.getMeasuredHeight() / 22));
                    CameraScanActivity.this.recognizedMrzView.setPadding(0, 0, 0, CameraScanActivity.this.cameraView.getMeasuredHeight() / 15);
                } else {
                    CameraScanActivity.this.actionBar.layout(0, 0, CameraScanActivity.this.actionBar.getMeasuredWidth(), CameraScanActivity.this.actionBar.getMeasuredHeight());
                    CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight());
                    int size = (int) (((float) Math.min(CameraScanActivity.this.cameraView.getWidth(), CameraScanActivity.this.cameraView.getHeight())) / 1.5f);
                    if (CameraScanActivity.this.currentType == 1) {
                        y = (((CameraScanActivity.this.cameraView.getMeasuredHeight() - size) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(30.0f);
                    } else {
                        y = (((CameraScanActivity.this.cameraView.getMeasuredHeight() - size) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(64.0f);
                    }
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), y, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + y);
                    CameraScanActivity.this.recognizedMrzView.layout(0, getMeasuredHeight() - CameraScanActivity.this.recognizedMrzView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                    if (CameraScanActivity.this.needGalleryButton) {
                        x = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) + AndroidUtilities.dp(35.0f);
                    } else {
                        x = (CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - (CameraScanActivity.this.flashButton.getMeasuredWidth() / 2);
                    }
                    int y3 = ((CameraScanActivity.this.cameraView.getMeasuredHeight() - size) / 2) + size + AndroidUtilities.dp(30.0f);
                    CameraScanActivity.this.flashButton.layout(x, y3, CameraScanActivity.this.flashButton.getMeasuredWidth() + x, CameraScanActivity.this.flashButton.getMeasuredHeight() + y3);
                    if (CameraScanActivity.this.galleryButton != null) {
                        int x2 = ((CameraScanActivity.this.cameraView.getMeasuredWidth() / 2) - AndroidUtilities.dp(35.0f)) - CameraScanActivity.this.galleryButton.getMeasuredWidth();
                        CameraScanActivity.this.galleryButton.layout(x2, y3, CameraScanActivity.this.galleryButton.getMeasuredWidth() + x2, CameraScanActivity.this.galleryButton.getMeasuredHeight() + y3);
                    }
                    int i = y3;
                }
                int y4 = (int) (((float) height) * 0.74f);
                int x3 = (int) (((float) width) * 0.05f);
                CameraScanActivity.this.descriptionText.layout(x3, y4, CameraScanActivity.this.descriptionText.getMeasuredWidth() + x3, CameraScanActivity.this.descriptionText.getMeasuredHeight() + y4);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                Canvas canvas2 = canvas;
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (!CameraScanActivity.this.isQr()) {
                    View view = child;
                } else if (child == CameraScanActivity.this.cameraView) {
                    int size = (int) (((float) Math.min(child.getWidth(), child.getHeight())) / 1.5f);
                    int x = (child.getWidth() - size) / 2;
                    int y = (child.getHeight() - size) / 2;
                    Canvas canvas3 = canvas;
                    canvas3.drawRect(0.0f, 0.0f, (float) child.getMeasuredWidth(), (float) y, CameraScanActivity.this.paint);
                    canvas.drawRect(0.0f, (float) (y + size), (float) child.getMeasuredWidth(), (float) child.getMeasuredHeight(), CameraScanActivity.this.paint);
                    Canvas canvas4 = canvas;
                    canvas4.drawRect(0.0f, (float) y, (float) x, (float) (y + size), CameraScanActivity.this.paint);
                    Canvas canvas5 = canvas;
                    canvas5.drawRect((float) (x + size), (float) y, (float) child.getMeasuredWidth(), (float) (y + size), CameraScanActivity.this.paint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.moveTo((float) x, (float) (AndroidUtilities.dp(20.0f) + y));
                    CameraScanActivity.this.path.lineTo((float) x, (float) y);
                    CameraScanActivity.this.path.lineTo((float) (AndroidUtilities.dp(20.0f) + x), (float) y);
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.moveTo((float) (x + size), (float) (AndroidUtilities.dp(20.0f) + y));
                    CameraScanActivity.this.path.lineTo((float) (x + size), (float) y);
                    CameraScanActivity.this.path.lineTo((float) ((x + size) - AndroidUtilities.dp(20.0f)), (float) y);
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.moveTo((float) x, (float) ((y + size) - AndroidUtilities.dp(20.0f)));
                    CameraScanActivity.this.path.lineTo((float) x, (float) (y + size));
                    CameraScanActivity.this.path.lineTo((float) (AndroidUtilities.dp(20.0f) + x), (float) (y + size));
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.moveTo((float) (x + size), (float) ((y + size) - AndroidUtilities.dp(20.0f)));
                    CameraScanActivity.this.path.lineTo((float) (x + size), (float) (y + size));
                    CameraScanActivity.this.path.lineTo((float) ((x + size) - AndroidUtilities.dp(20.0f)), (float) (y + size));
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                }
                return result;
            }
        };
        viewGroup.setOnTouchListener(CameraScanActivity$$ExternalSyntheticLambda3.INSTANCE);
        this.fragmentView = viewGroup;
        CameraView cameraView2 = new CameraView(context2, false);
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
        viewGroup.addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.currentType == 0) {
            this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        } else {
            this.actionBar.setBackgroundDrawable((Drawable) null);
            this.actionBar.setAddToContainer(false);
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            viewGroup.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
            viewGroup.addView(this.actionBar);
        }
        if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("AuthAnotherClientScan", NUM));
        }
        final Paint selectionPaint = new Paint(1);
        selectionPaint.setColor(ColorUtils.setAlphaComponent(-1, 100));
        AnonymousClass5 r8 = new TextView(context2) {
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

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                LinkPath linkPath = this.textPath;
                if (linkPath != null) {
                    canvas.drawPath(linkPath, selectionPaint);
                }
                super.onDraw(canvas);
            }
        };
        this.titleTextView = r8;
        r8.setGravity(1);
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
                    String text = LocaleController.getString("AuthAnotherClientInfo5", NUM);
                    SpannableStringBuilder spanned = new SpannableStringBuilder(text);
                    int index1 = text.indexOf(42);
                    int index2 = text.indexOf(42, index1 + 1);
                    if (!(index1 == -1 || index2 == -1 || index1 == index2)) {
                        this.titleTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spanned.replace(index2, index2 + 1, " ");
                        spanned.replace(index1, index1 + 1, " ");
                        int index12 = index1 + 1;
                        int index22 = index2 + 1;
                        spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherClientDownloadClientUrl", NUM)), index12, index22 - 1, 33);
                        spanned.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), index12, index22 - 1, 33);
                    }
                    String text2 = spanned.toString();
                    int index13 = text2.indexOf(42);
                    int index23 = text2.indexOf(42, index13 + 1);
                    if (!(index13 == -1 || index23 == -1 || index13 == index23)) {
                        spanned.replace(index23, index23 + 1, " ");
                        spanned.replace(index13, index13 + 1, " ");
                        int index14 = index13 + 1;
                        int index24 = index23 + 1;
                        spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherWebClientUrl", NUM)), index14, index24 - 1, 33);
                        spanned.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), index14, index24 - 1, 33);
                    }
                    this.titleTextView.setLinkTextColor(-1);
                    this.titleTextView.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
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
                this.galleryButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda1(this));
            }
            ImageView imageView2 = new ImageView(context2);
            this.flashButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.flashButton.setImageResource(NUM);
            this.flashButton.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), NUM));
            viewGroup.addView(this.flashButton);
            this.flashButton.setOnClickListener(new CameraScanActivity$$ExternalSyntheticLambda2(this));
        }
        if (getParentActivity() != null) {
            z = true;
            getParentActivity().setRequestedOrientation(1);
        } else {
            z = true;
        }
        this.fragmentView.setKeepScreenOn(z);
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m1502lambda$createView$2$orgtelegramuiCameraScanActivity(View currentImage) {
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
                                        String text = CameraScanActivity.this.tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap(info.path, (Uri) null, (float) screenSize.x, (float) screenSize.y, true));
                                        if (text != null) {
                                            if (CameraScanActivity.this.delegate != null) {
                                                CameraScanActivity.this.delegate.didFindQr(text);
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
    public /* synthetic */ void m1504lambda$createView$4$orgtelegramuiCameraScanActivity(View currentImage) {
        CameraSession session = this.cameraView.getCameraSession();
        if (session != null) {
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
    public /* synthetic */ void m1503lambda$createView$3$orgtelegramuiCameraScanActivity(ValueAnimator animation) {
        this.flashButton.invalidate();
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            int i = requestCode;
        } else if (requestCode != 11) {
        } else if (data == null) {
        } else if (data.getData() == null) {
        } else {
            try {
                Point screenSize = AndroidUtilities.getRealScreenSize();
                String text = tryReadQr((byte[]) null, (Size) null, 0, 0, 0, ImageLoader.loadBitmap((String) null, data.getData(), (float) screenSize.x, (float) screenSize.y, true));
                if (text != null) {
                    try {
                        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
                        if (cameraScanActivityDelegate != null) {
                            cameraScanActivityDelegate.didFindQr(text);
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
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda5(this));
    }

    /* renamed from: lambda$onNoQrFound$5$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m1505lambda$onNoQrFound$5$orgtelegramuiCameraScanActivity() {
        if (this.recognizedMrzView.getTag() != null) {
            this.recognizedMrzView.setTag((Object) null);
            this.recognizedMrzView.animate().setDuration(200).alpha(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        this.handler.post(new CameraScanActivity$$ExternalSyntheticLambda8(this, data, camera));
    }

    /* renamed from: lambda$onPreviewFrame$8$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m1508lambda$onPreviewFrame$8$orgtelegramuiCameraScanActivity(byte[] data, Camera camera) {
        try {
            Size size = this.cameraView.getPreviewSize();
            if (this.currentType == 0) {
                MrzRecognizer.Result res = MrzRecognizer.recognize(data, size.getWidth(), size.getHeight(), this.cameraView.getCameraSession().getDisplayOrientation());
                if (res != null && !TextUtils.isEmpty(res.firstName) && !TextUtils.isEmpty(res.lastName) && !TextUtils.isEmpty(res.number) && res.birthDay != 0 && ((res.expiryDay != 0 || res.doesNotExpire) && res.gender != 0)) {
                    this.recognized = true;
                    camera.stopPreview();
                    AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda7(this, res));
                }
                return;
            }
            int previewFormat = camera.getParameters().getPreviewFormat();
            int side = (int) (((float) Math.min(size.getWidth(), size.getHeight())) / 1.5f);
            String text = tryReadQr(data, size, (size.getWidth() - side) / 2, (size.getHeight() - side) / 2, side, (Bitmap) null);
            if (text != null) {
                this.recognized = true;
                camera.stopPreview();
                AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda6(this, text));
            }
        } catch (Throwable th) {
            onNoQrFound();
        }
    }

    /* renamed from: lambda$onPreviewFrame$6$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m1506lambda$onPreviewFrame$6$orgtelegramuiCameraScanActivity(MrzRecognizer.Result res) {
        this.recognizedMrzView.setText(res.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(res);
        }
        AndroidUtilities.runOnUIThread(new CameraScanActivity$$ExternalSyntheticLambda4(this), 1200);
    }

    /* renamed from: lambda$onPreviewFrame$7$org-telegram-ui-CameraScanActivity  reason: not valid java name */
    public /* synthetic */ void m1507lambda$onPreviewFrame$7$orgtelegramuiCameraScanActivity(String text) {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(text);
        }
        finishFragment();
    }

    /* JADX WARNING: type inference failed for: r2v5, types: [com.google.zxing.RGBLuminanceSource] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String tryReadQr(byte[] r22, org.telegram.messenger.camera.Size r23, int r24, int r25, int r26, android.graphics.Bitmap r27) {
        /*
            r21 = this;
            r1 = r21
            r10 = r27
            r11 = 0
            com.google.android.gms.vision.barcode.BarcodeDetector r0 = r1.visionQrReader     // Catch:{ all -> 0x00ec }
            boolean r0 = r0.isOperational()     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x0052
            if (r10 == 0) goto L_0x001d
            com.google.android.gms.vision.Frame$Builder r0 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x00ec }
            r0.<init>()     // Catch:{ all -> 0x00ec }
            com.google.android.gms.vision.Frame$Builder r0 = r0.setBitmap(r10)     // Catch:{ all -> 0x00ec }
            com.google.android.gms.vision.Frame r0 = r0.build()     // Catch:{ all -> 0x00ec }
            goto L_0x0038
        L_0x001d:
            com.google.android.gms.vision.Frame$Builder r0 = new com.google.android.gms.vision.Frame$Builder     // Catch:{ all -> 0x00ec }
            r0.<init>()     // Catch:{ all -> 0x00ec }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.wrap(r22)     // Catch:{ all -> 0x00ec }
            int r3 = r23.getWidth()     // Catch:{ all -> 0x00ec }
            int r4 = r23.getHeight()     // Catch:{ all -> 0x00ec }
            r5 = 17
            com.google.android.gms.vision.Frame$Builder r0 = r0.setImageData(r2, r3, r4, r5)     // Catch:{ all -> 0x00ec }
            com.google.android.gms.vision.Frame r0 = r0.build()     // Catch:{ all -> 0x00ec }
        L_0x0038:
            com.google.android.gms.vision.barcode.BarcodeDetector r2 = r1.visionQrReader     // Catch:{ all -> 0x00ec }
            android.util.SparseArray r2 = r2.detect(r0)     // Catch:{ all -> 0x00ec }
            if (r2 == 0) goto L_0x0050
            int r3 = r2.size()     // Catch:{ all -> 0x00ec }
            if (r3 <= 0) goto L_0x0050
            r3 = 0
            java.lang.Object r3 = r2.valueAt(r3)     // Catch:{ all -> 0x00ec }
            com.google.android.gms.vision.barcode.Barcode r3 = (com.google.android.gms.vision.barcode.Barcode) r3     // Catch:{ all -> 0x00ec }
            java.lang.String r3 = r3.rawValue     // Catch:{ all -> 0x00ec }
            goto L_0x0051
        L_0x0050:
            r3 = 0
        L_0x0051:
            goto L_0x00b8
        L_0x0052:
            if (r10 == 0) goto L_0x0084
            int r0 = r27.getWidth()     // Catch:{ all -> 0x00ec }
            int r2 = r27.getHeight()     // Catch:{ all -> 0x00ec }
            int r0 = r0 * r2
            int[] r0 = new int[r0]     // Catch:{ all -> 0x00ec }
            r4 = 0
            int r5 = r27.getWidth()     // Catch:{ all -> 0x00ec }
            r6 = 0
            r7 = 0
            int r8 = r27.getWidth()     // Catch:{ all -> 0x00ec }
            int r9 = r27.getHeight()     // Catch:{ all -> 0x00ec }
            r2 = r27
            r3 = r0
            r2.getPixels(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x00ec }
            com.google.zxing.RGBLuminanceSource r2 = new com.google.zxing.RGBLuminanceSource     // Catch:{ all -> 0x00ec }
            int r3 = r27.getWidth()     // Catch:{ all -> 0x00ec }
            int r4 = r27.getHeight()     // Catch:{ all -> 0x00ec }
            r2.<init>(r3, r4, r0)     // Catch:{ all -> 0x00ec }
            r0 = r2
            goto L_0x009e
        L_0x0084:
            com.google.zxing.PlanarYUVLuminanceSource r0 = new com.google.zxing.PlanarYUVLuminanceSource     // Catch:{ all -> 0x00ec }
            int r14 = r23.getWidth()     // Catch:{ all -> 0x00ec }
            int r15 = r23.getHeight()     // Catch:{ all -> 0x00ec }
            r20 = 0
            r12 = r0
            r13 = r22
            r16 = r24
            r17 = r25
            r18 = r26
            r19 = r26
            r12.<init>(r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x00ec }
        L_0x009e:
            com.google.zxing.qrcode.QRCodeReader r2 = r1.qrReader     // Catch:{ all -> 0x00ec }
            com.google.zxing.BinaryBitmap r3 = new com.google.zxing.BinaryBitmap     // Catch:{ all -> 0x00ec }
            com.google.zxing.common.GlobalHistogramBinarizer r4 = new com.google.zxing.common.GlobalHistogramBinarizer     // Catch:{ all -> 0x00ec }
            r4.<init>(r0)     // Catch:{ all -> 0x00ec }
            r3.<init>(r4)     // Catch:{ all -> 0x00ec }
            com.google.zxing.Result r2 = r2.decode(r3)     // Catch:{ all -> 0x00ec }
            if (r2 != 0) goto L_0x00b4
            r21.onNoQrFound()     // Catch:{ all -> 0x00ec }
            return r11
        L_0x00b4:
            java.lang.String r3 = r2.getText()     // Catch:{ all -> 0x00ec }
        L_0x00b8:
            boolean r0 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x00c2
            r21.onNoQrFound()     // Catch:{ all -> 0x00ec }
            return r11
        L_0x00c2:
            boolean r0 = r1.needGalleryButton     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x00df
            java.lang.String r0 = "ton://transfer/"
            boolean r0 = r3.startsWith(r0)     // Catch:{ all -> 0x00ec }
            if (r0 != 0) goto L_0x00cf
            return r11
        L_0x00cf:
            android.net.Uri r0 = android.net.Uri.parse(r3)     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = r0.getPath()     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = "/"
            java.lang.String r5 = ""
            r2.replace(r4, r5)     // Catch:{ all -> 0x00ec }
            goto L_0x00eb
        L_0x00df:
            java.lang.String r0 = "tg://login?token="
            boolean r0 = r3.startsWith(r0)     // Catch:{ all -> 0x00ec }
            if (r0 != 0) goto L_0x00eb
            r21.onNoQrFound()     // Catch:{ all -> 0x00ec }
            return r11
        L_0x00eb:
            return r3
        L_0x00ec:
            r0 = move-exception
            r21.onNoQrFound()
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CameraScanActivity.tryReadQr(byte[], org.telegram.messenger.camera.Size, int, int, int, android.graphics.Bitmap):java.lang.String");
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
