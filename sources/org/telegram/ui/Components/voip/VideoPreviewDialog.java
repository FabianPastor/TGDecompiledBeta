package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.RendererCommon;

public abstract class VideoPreviewDialog extends FrameLayout {
    private final ActionBar actionBar;
    private final LinearLayout buttonsLayout;
    FrameLayout container;
    CellFlickerDrawable drawable = new CellFlickerDrawable();
    int flipIconEndFrame;
    private final RLottieImageView flipIconView;
    boolean ignoreLayout = false;
    boolean isDismissed;
    public boolean micEnabled;
    private final RLottieImageView micIconView;
    View negativeButton;
    float outProgress;
    View positiveButton;
    private final TextView subtitle;
    VoIPTextureView textureView;

    /* access modifiers changed from: protected */
    public void onDismiss(boolean z) {
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public VideoPreviewDialog(Context context) {
        super(context);
        Context context2 = context;
        ActionBar actionBar2 = new ActionBar(context2);
        this.actionBar = actionBar2;
        actionBar2.setBackButtonDrawable(new BackDrawable(false));
        actionBar2.setBackgroundColor(0);
        actionBar2.setItemsColor(Theme.getColor("voipgroup_actionBarItems"), false);
        actionBar2.setTitle(LocaleController.getString("CallVideoPreviewTitle", NUM));
        actionBar2.setOccupyStatusBar(false);
        actionBar2.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    VideoPreviewDialog.this.dismiss(false);
                }
                super.onItemClick(i);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        this.container = frameLayout;
        frameLayout.setClipChildren(false);
        addView(this.container, LayoutHelper.createFrame(-1, -1.0f));
        this.container.addView(actionBar2);
        VoIPTextureView voIPTextureView = new VoIPTextureView(context2, false, false);
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.textureView.setRoundCorners((float) AndroidUtilities.dp(8.0f));
        if (VoIPService.getSharedInstance() != null) {
            this.textureView.renderer.setMirror(VoIPService.getSharedInstance().isFrontFaceCamera());
        }
        VoIPTextureView voIPTextureView2 = this.textureView;
        voIPTextureView2.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        voIPTextureView2.clipToTexture = true;
        voIPTextureView2.renderer.setAlpha(0.0f);
        this.textureView.renderer.setRotateTextureWitchScreen(true);
        this.textureView.renderer.setUseCameraRotation(true);
        TextView textView = new TextView(context2);
        this.subtitle = textView;
        textView.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_nameText"), 102));
        textView.setTextSize(1, 15.0f);
        textView.setText(LocaleController.getString("VideoPreviewDesrciption", NUM));
        textView.setGravity(1);
        this.container.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 80, 24.0f, 0.0f, 24.0f, 108.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.buttonsLayout = linearLayout;
        linearLayout.setOrientation(0);
        AnonymousClass2 r10 = new TextView(getContext()) {
            public void setEnabled(boolean z) {
                super.setEnabled(z);
                setAlpha(z ? 1.0f : 0.5f);
            }

            public void setTextColor(int i) {
                super.setTextColor(i);
                setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(i));
            }
        };
        r10.setMinWidth(AndroidUtilities.dp(64.0f));
        r10.setTag(-1);
        r10.setTextSize(1, 14.0f);
        r10.setTextColor(Theme.getColor("voipgroup_nameText"));
        r10.setGravity(17);
        r10.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r10.setText(LocaleController.getString("Cancel", NUM));
        r10.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("voipgroup_listViewBackground"), ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_nameText"), 76)));
        r10.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        this.negativeButton = r10;
        AnonymousClass3 r7 = new TextView(getContext()) {
            Paint gradientPaint = new Paint(1);

            /* access modifiers changed from: protected */
            public void onSizeChanged(int i, int i2, int i3, int i4) {
                super.onSizeChanged(i, i2, i3, i4);
                this.gradientPaint.setShader(new LinearGradient(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, new int[]{Theme.getColor("voipgroup_unmuteButton"), Theme.getColor("voipgroup_unmuteButton2")}, (float[]) null, Shader.TileMode.CLAMP));
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.gradientPaint);
                super.onDraw(canvas);
            }
        };
        r7.setMinWidth(AndroidUtilities.dp(64.0f));
        r7.setTag(-1);
        r7.setTextSize(1, 14.0f);
        r7.setTextColor(Theme.getColor("voipgroup_nameText"));
        r7.setGravity(17);
        r7.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r7.setText(LocaleController.getString("ShareVideo", NUM));
        if (Build.VERSION.SDK_INT >= 23) {
            r7.setForeground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_nameText"), 76)));
        }
        r7.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        this.positiveButton = r7;
        linearLayout.addView(r10, LayoutHelper.createLinear(-1, 48, 1.0f, 0, 4, 0, 4, 0));
        linearLayout.addView(r7, LayoutHelper.createLinear(-1, 48, 1.0f, 0, 4, 0, 4, 0));
        addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        this.container.addView(linearLayout, LayoutHelper.createFrame(-1, -2, 80));
        if (VoIPService.getSharedInstance() != null) {
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
                public void onFrameResolutionChanged(int i, int i2, int i3) {
                }

                public void onFirstFrameRendered() {
                    VideoPreviewDialog.this.textureView.animate().alpha(1.0f).setDuration(250);
                }
            });
            VoIPService.getSharedInstance().setLocalSink(this.textureView.renderer, false);
        }
        r10.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VideoPreviewDialog.this.lambda$new$0$VideoPreviewDialog(view);
            }
        });
        r7.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VideoPreviewDialog.this.lambda$new$1$VideoPreviewDialog(view);
            }
        });
        setAlpha(0.0f);
        setTranslationX((float) AndroidUtilities.dp(32.0f));
        animate().alpha(1.0f).translationX(0.0f).setDuration(150).start();
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.flipIconView = rLottieImageView;
        rLottieImageView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(48.0f), ColorUtils.setAlphaComponent(-16777216, 76)));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
        rLottieImageView.setAnimation(rLottieDrawable);
        rLottieImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        rLottieImageView.setOnClickListener(new View.OnClickListener(rLottieDrawable) {
            public final /* synthetic */ RLottieDrawable f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                VideoPreviewDialog.this.lambda$new$2$VideoPreviewDialog(this.f$1, view);
            }
        });
        addView(rLottieImageView, LayoutHelper.createFrame(48, 48.0f));
        RLottieImageView rLottieImageView2 = new RLottieImageView(context2);
        this.micIconView = rLottieImageView2;
        rLottieImageView2.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f));
        rLottieImageView2.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(48.0f), ColorUtils.setAlphaComponent(-16777216, 76)));
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
        rLottieImageView2.setAnimation(rLottieDrawable2);
        rLottieImageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.micEnabled = true;
        rLottieDrawable2.setCurrentFrame(69);
        rLottieImageView2.setOnClickListener(new View.OnClickListener(rLottieDrawable2) {
            public final /* synthetic */ RLottieDrawable f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                VideoPreviewDialog.this.lambda$new$3$VideoPreviewDialog(this.f$1, view);
            }
        });
        addView(rLottieImageView2, LayoutHelper.createFrame(48, 48.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VideoPreviewDialog(View view) {
        dismiss(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$VideoPreviewDialog(View view) {
        if (!this.isDismissed) {
            dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$VideoPreviewDialog(RLottieDrawable rLottieDrawable, View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().switchCamera();
            if (this.flipIconEndFrame == 18) {
                this.flipIconEndFrame = 39;
                rLottieDrawable.setCustomEndFrame(39);
                rLottieDrawable.start();
                return;
            }
            rLottieDrawable.setCurrentFrame(0, false);
            this.flipIconEndFrame = 18;
            rLottieDrawable.setCustomEndFrame(18);
            rLottieDrawable.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$VideoPreviewDialog(RLottieDrawable rLottieDrawable, View view) {
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

    public void dismiss(final boolean z) {
        if (!this.isDismissed) {
            this.isDismissed = true;
            animate().alpha(0.0f).translationX((float) AndroidUtilities.dp(32.0f)).setDuration(150).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    if (VideoPreviewDialog.this.getParent() != null) {
                        ((ViewGroup) VideoPreviewDialog.this.getParent()).removeView(VideoPreviewDialog.this);
                    }
                    VideoPreviewDialog.this.onDismiss(z);
                }
            });
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        VoIPTextureView voIPTextureView = this.textureView;
        float right = ((float) (this.textureView.getRight() - AndroidUtilities.dp(60.0f))) - voIPTextureView.currentClipHorizontal;
        float bottom = ((float) (voIPTextureView.getBottom() - AndroidUtilities.dp(60.0f))) - this.textureView.currentClipVertical;
        this.flipIconView.setTranslationX(right);
        this.flipIconView.setTranslationY(bottom);
        this.flipIconView.setScaleX(this.textureView.getScaleX());
        this.flipIconView.setScaleY(this.textureView.getScaleY());
        this.flipIconView.setPivotX((((float) getMeasuredWidth()) / 2.0f) - right);
        this.flipIconView.setPivotY((((float) getMeasuredHeight()) / 2.0f) - bottom);
        this.flipIconView.setAlpha(this.textureView.renderer.getAlpha() * (1.0f - this.outProgress));
        float left = ((float) (this.textureView.getLeft() + AndroidUtilities.dp(12.0f))) + this.textureView.currentClipHorizontal;
        this.micIconView.setTranslationX(left);
        this.micIconView.setTranslationY(bottom);
        this.micIconView.setScaleX(this.textureView.getScaleX());
        this.micIconView.setScaleY(this.textureView.getScaleY());
        this.micIconView.setPivotX((((float) getMeasuredWidth()) / 2.0f) - left);
        this.micIconView.setPivotY((((float) getMeasuredHeight()) / 2.0f) - bottom);
        this.micIconView.setAlpha(this.textureView.renderer.getAlpha() * (1.0f - this.outProgress));
        canvas.drawColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_actionBar"), (int) ((1.0f - this.outProgress) * 255.0f)));
        if (this.isDismissed || this.textureView.renderer.getAlpha() != 1.0f) {
            invalidate();
        }
        if (!this.textureView.renderer.isFirstFrameRendered() && this.textureView.renderer.getAlpha() != 1.0f) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.textureView.getLayoutParams();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set((float) marginLayoutParams.leftMargin, (float) marginLayoutParams.topMargin, (float) (getMeasuredWidth() - marginLayoutParams.rightMargin), (float) (getMeasuredHeight() - marginLayoutParams.bottomMargin));
            float f = !GroupCallActivity.isLandscapeMode ? 0.5625f : 1.7777778f;
            if (rectF.width() / rectF.height() > f) {
                float width = (rectF.width() - (rectF.height() * f)) / 2.0f;
                rectF.left += width;
                rectF.right -= width;
            } else {
                float height = (rectF.height() - (rectF.width() * f)) / 2.0f;
                rectF.top += height;
                rectF.bottom -= height;
            }
            this.drawable.setParentWidth(getMeasuredWidth());
            this.drawable.draw(canvas, rectF, (float) AndroidUtilities.dp(8.0f));
            invalidate();
        }
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        boolean z = View.MeasureSpec.getSize(i) > View.MeasureSpec.getSize(i2);
        this.ignoreLayout = true;
        if (z) {
            this.actionBar.setTitle((CharSequence) null);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.textureView.getLayoutParams();
            marginLayoutParams.topMargin = AndroidUtilities.dp(8.0f);
            marginLayoutParams.bottomMargin = AndroidUtilities.dp(76.0f);
            int dp = AndroidUtilities.dp(48.0f);
            marginLayoutParams.leftMargin = dp;
            marginLayoutParams.rightMargin = dp;
            this.negativeButton.setVisibility(0);
            this.subtitle.setVisibility(8);
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.buttonsLayout.getLayoutParams();
            int dp2 = AndroidUtilities.dp(80.0f);
            marginLayoutParams2.leftMargin = dp2;
            marginLayoutParams2.rightMargin = dp2;
            marginLayoutParams2.bottomMargin = AndroidUtilities.dp(16.0f);
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) this.textureView.getLayoutParams();
            this.actionBar.setTitle(LocaleController.getString("CallVideoPreviewTitle", NUM));
            marginLayoutParams3.topMargin = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(8.0f);
            marginLayoutParams3.bottomMargin = AndroidUtilities.dp(168.0f);
            int dp3 = AndroidUtilities.dp(12.0f);
            marginLayoutParams3.leftMargin = dp3;
            marginLayoutParams3.rightMargin = dp3;
            this.negativeButton.setVisibility(8);
            this.subtitle.setVisibility(0);
            ViewGroup.MarginLayoutParams marginLayoutParams4 = (ViewGroup.MarginLayoutParams) this.buttonsLayout.getLayoutParams();
            int dp4 = AndroidUtilities.dp(16.0f);
            marginLayoutParams4.bottomMargin = dp4;
            marginLayoutParams4.leftMargin = dp4;
            marginLayoutParams4.rightMargin = dp4;
        }
        this.ignoreLayout = false;
        super.onMeasure(i, i2);
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
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

    public void update() {
        if (VoIPService.getSharedInstance() != null) {
            this.textureView.renderer.setMirror(VoIPService.getSharedInstance().isFrontFaceCamera());
        }
    }
}
