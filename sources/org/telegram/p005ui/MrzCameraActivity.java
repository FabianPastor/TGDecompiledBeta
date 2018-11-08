package org.telegram.p005ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0541R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.messenger.camera.Size;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0704ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Components.CubicBezierInterpolator;
import org.telegram.p005ui.Components.LayoutHelper;

@TargetApi(18)
/* renamed from: org.telegram.ui.MrzCameraActivity */
public class MrzCameraActivity extends BaseFragment implements PreviewCallback {
    private HandlerThread backgroundHandlerThread = new HandlerThread("MrzCamera");
    private CameraView cameraView;
    private MrzCameraActivityDelegate delegate;
    private TextView descriptionText;
    private Handler handler;
    private boolean recognized;
    private TextView recognizedMrzView;
    private TextView titleTextView;

    /* renamed from: org.telegram.ui.MrzCameraActivity$1 */
    class C18491 extends ActionBarMenuOnItemClick {
        C18491() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                MrzCameraActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.MrzCameraActivity$3 */
    class C18513 implements OnTouchListener {
        C18513() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.MrzCameraActivity$4 */
    class C18524 implements CameraViewDelegate {
        C18524() {
        }

        public void onCameraCreated(Camera camera) {
            Parameters params = camera.getParameters();
            float evStep = params.getExposureCompensationStep();
            params.setExposureCompensation(((float) params.getMaxExposureCompensation()) * evStep <= 2.0f ? params.getMaxExposureCompensation() : Math.round(2.0f / evStep));
            camera.setParameters(params);
        }

        public void onCameraInit() {
            MrzCameraActivity.this.startRecognizing();
        }
    }

    /* renamed from: org.telegram.ui.MrzCameraActivity$5 */
    class C18535 implements Runnable {
        C18535() {
        }

        public void run() {
            if (MrzCameraActivity.this.cameraView != null && !MrzCameraActivity.this.recognized && MrzCameraActivity.this.cameraView.getCameraSession() != null) {
                MrzCameraActivity.this.cameraView.getCameraSession().setOneShotPreviewCallback(MrzCameraActivity.this);
                AndroidUtilities.runOnUIThread(this, 500);
            }
        }
    }

    /* renamed from: org.telegram.ui.MrzCameraActivity$MrzCameraActivityDelegate */
    public interface MrzCameraActivityDelegate {
        void didFindMrzInfo(Result result);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        destroy(false, null);
        getParentActivity().setRequestedOrientation(-1);
    }

    public View createView(Context context) {
        getParentActivity().setRequestedOrientation(1);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setBackButtonImage(C0541R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new C18491());
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                MrzCameraActivity.this.cameraView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.704f), NUM));
                MrzCameraActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, 0));
                MrzCameraActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.9f), NUM), MeasureSpec.makeMeasureSpec(height, 0));
                setMeasuredDimension(width, height);
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int width = r - l;
                int height = b - t;
                MrzCameraActivity.this.cameraView.layout(0, 0, MrzCameraActivity.this.cameraView.getMeasuredWidth(), MrzCameraActivity.this.cameraView.getMeasuredHeight() + 0);
                MrzCameraActivity.this.recognizedMrzView.setTextSize(0, (float) (MrzCameraActivity.this.cameraView.getMeasuredHeight() / 22));
                MrzCameraActivity.this.recognizedMrzView.setPadding(0, 0, 0, MrzCameraActivity.this.cameraView.getMeasuredHeight() / 15);
                int y = (int) (((float) height) * 0.65f);
                MrzCameraActivity.this.titleTextView.layout(0, y, MrzCameraActivity.this.titleTextView.getMeasuredWidth(), MrzCameraActivity.this.titleTextView.getMeasuredHeight() + y);
                y = (int) (((float) height) * 0.74f);
                int x = (int) (((float) width) * 0.05f);
                MrzCameraActivity.this.descriptionText.layout(x, y, MrzCameraActivity.this.descriptionText.getMeasuredWidth() + x, MrzCameraActivity.this.descriptionText.getMeasuredHeight() + y);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ViewGroup viewGroup = this.fragmentView;
        viewGroup.setOnTouchListener(new C18513());
        this.cameraView = new CameraView(context, false);
        this.cameraView.setDelegate(new C18524());
        viewGroup.addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0f));
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setGravity(1);
        this.titleTextView.setTextSize(1, 24.0f);
        this.titleTextView.setText(LocaleController.getString("PassportScanPassport", C0541R.string.PassportScanPassport));
        viewGroup.addView(this.titleTextView);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        this.descriptionText.setText(LocaleController.getString("PassportScanPassportInfo", C0541R.string.PassportScanPassportInfo));
        viewGroup.addView(this.descriptionText);
        this.recognizedMrzView = new TextView(context);
        this.recognizedMrzView.setTypeface(Typeface.MONOSPACE);
        this.recognizedMrzView.setTextColor(-1);
        this.recognizedMrzView.setGravity(81);
        this.recognizedMrzView.setBackgroundColor(Integer.MIN_VALUE);
        this.recognizedMrzView.setAlpha(0.0f);
        this.cameraView.addView(this.recognizedMrzView);
        this.fragmentView.setKeepScreenOn(true);
        return this.fragmentView;
    }

    public void setDelegate(MrzCameraActivityDelegate mrzCameraActivityDelegate) {
        this.delegate = mrzCameraActivityDelegate;
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        this.cameraView.destroy(async, beforeDestroyRunnable);
        this.cameraView = null;
        this.backgroundHandlerThread.quitSafely();
    }

    public void cancel() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(0));
    }

    public void hideCamera(boolean async) {
        destroy(async, null);
    }

    private void startRecognizing() {
        this.backgroundHandlerThread.start();
        this.handler = new Handler(this.backgroundHandlerThread.getLooper());
        AndroidUtilities.runOnUIThread(new C18535());
    }

    public void onPreviewFrame(final byte[] data, final Camera camera) {
        this.handler.post(new Runnable() {
            public void run() {
                try {
                    Size size = MrzCameraActivity.this.cameraView.getPreviewSize();
                    final Result res = MrzRecognizer.recognize(data, size.getWidth(), size.getHeight(), MrzCameraActivity.this.cameraView.getCameraSession().getDisplayOrientation());
                    if (res != null && !TextUtils.isEmpty(res.firstName) && !TextUtils.isEmpty(res.lastName) && !TextUtils.isEmpty(res.number) && res.birthDay != 0) {
                        if ((res.expiryDay != 0 || res.doesNotExpire) && res.gender != 0) {
                            MrzCameraActivity.this.recognized = true;
                            camera.stopPreview();
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.ui.MrzCameraActivity$6$1$1 */
                                class C18541 implements Runnable {
                                    C18541() {
                                    }

                                    public void run() {
                                        MrzCameraActivity.this.finishFragment();
                                    }
                                }

                                public void run() {
                                    MrzCameraActivity.this.recognizedMrzView.setText(res.rawMRZ);
                                    MrzCameraActivity.this.recognizedMrzView.animate().setDuration(200).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                                    if (MrzCameraActivity.this.delegate != null) {
                                        MrzCameraActivity.this.delegate.didFindMrzInfo(res);
                                    }
                                    AndroidUtilities.runOnUIThread(new C18541(), 1200);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[6];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarWhiteSelector);
        themeDescriptionArr[4] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[5] = new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        return themeDescriptionArr;
    }
}
