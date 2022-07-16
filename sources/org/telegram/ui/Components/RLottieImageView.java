package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;

public class RLottieImageView extends ImageView {
    private boolean attachedToWindow;
    private boolean autoRepeat;
    private RLottieDrawable drawable;
    /* access modifiers changed from: private */
    public ImageReceiver imageReceiver;
    private HashMap<String, Integer> layerColors;
    private boolean playing;

    public RLottieImageView(Context context) {
        super(context);
    }

    public void clearLayerColors() {
        this.layerColors.clear();
    }

    public void setLayerColor(String str, int i) {
        if (this.layerColors == null) {
            this.layerColors = new HashMap<>();
        }
        this.layerColors.put(str, Integer.valueOf(i));
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setLayerColor(str, i);
        }
    }

    public void replaceColors(int[] iArr) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.replaceColors(iArr);
        }
    }

    public void setAnimation(int i, int i2, int i3) {
        setAnimation(i, i2, i3, (int[]) null);
    }

    public void setAnimation(int i, int i2, int i3, int[] iArr) {
        setAnimation(new RLottieDrawable(i, "" + i, AndroidUtilities.dp((float) i2), AndroidUtilities.dp((float) i3), false, iArr));
    }

    public void setOnAnimationEndListener(Runnable runnable) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setOnAnimationEndListener(runnable);
        }
    }

    public void setAnimation(RLottieDrawable rLottieDrawable) {
        if (this.drawable != rLottieDrawable) {
            ImageReceiver imageReceiver2 = this.imageReceiver;
            if (imageReceiver2 != null) {
                imageReceiver2.onDetachedFromWindow();
                this.imageReceiver = null;
            }
            this.drawable = rLottieDrawable;
            rLottieDrawable.setMasterParent(this);
            if (this.autoRepeat) {
                this.drawable.setAutoRepeat(1);
            }
            if (this.layerColors != null) {
                this.drawable.beginApplyLayerColors();
                for (Map.Entry next : this.layerColors.entrySet()) {
                    this.drawable.setLayerColor((String) next.getKey(), ((Integer) next.getValue()).intValue());
                }
                this.drawable.commitApplyLayerColors();
            }
            this.drawable.setAllowDecodeSingleFrame(true);
            setImageDrawable(this.drawable);
        }
    }

    public void setAnimation(TLRPC$Document tLRPC$Document, int i, int i2) {
        SvgHelper.SvgDrawable svgDrawable;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        int i3 = i;
        int i4 = i2;
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.onDetachedFromWindow();
            this.imageReceiver = null;
        }
        if (tLRPC$Document2 != null) {
            this.imageReceiver = new ImageReceiver();
            if ("video/webm".equals(tLRPC$Document2.mime_type)) {
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
                this.imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), i3 + "_" + i4 + "_pcache_" + "g", ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), (String) null, (Drawable) null, tLRPC$Document2.size, (String) null, tLRPC$Document, 1);
            } else {
                if (!ImageLoader.getInstance().hasLottieMemCache(tLRPC$Document2.id + "@" + i3 + "_" + i4)) {
                    SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document2.thumbs, "windowBackgroundWhiteGrayIcon", 0.2f);
                    if (svgThumb != null) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                    svgDrawable = svgThumb;
                } else {
                    svgDrawable = null;
                }
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
                this.imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), i3 + "_" + i4, ImageLocation.getForDocument(closestPhotoSizeWithSize2, tLRPC$Document2), (String) null, (ImageLocation) null, (String) null, svgDrawable, 0, (String) null, tLRPC$Document, 1);
            }
            this.imageReceiver.setAspectFit(true);
            this.imageReceiver.setParentView(this);
            this.imageReceiver.setAutoRepeat(1);
            this.imageReceiver.setAllowStartLottieAnimation(true);
            this.imageReceiver.setAllowStartAnimation(true);
            this.imageReceiver.clip = false;
            setImageDrawable(new Drawable() {
                public int getOpacity() {
                    return -2;
                }

                public void draw(Canvas canvas) {
                    Rect rect = AndroidUtilities.rectTmp2;
                    rect.set(getBounds());
                    rect.inset(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f));
                    RLottieImageView.this.imageReceiver.setImageCoords(rect);
                    RLottieImageView.this.imageReceiver.draw(canvas);
                }

                public void setAlpha(int i) {
                    RLottieImageView.this.imageReceiver.setAlpha(((float) i) / 255.0f);
                }

                public void setColorFilter(ColorFilter colorFilter) {
                    RLottieImageView.this.imageReceiver.setColorFilter(colorFilter);
                }
            });
            if (this.attachedToWindow) {
                this.imageReceiver.onAttachedToWindow();
            }
        }
    }

    public void clearAnimationDrawable() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
        }
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.onDetachedFromWindow();
            this.imageReceiver = null;
        }
        this.drawable = null;
        setImageDrawable((Drawable) null);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.onAttachedToWindow();
        }
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setCallback(this);
            if (this.playing) {
                this.drawable.start();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
        }
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.onDetachedFromWindow();
            this.imageReceiver = null;
        }
    }

    public boolean isPlaying() {
        RLottieDrawable rLottieDrawable = this.drawable;
        return rLottieDrawable != null && rLottieDrawable.isRunning();
    }

    public void setAutoRepeat(boolean z) {
        this.autoRepeat = z;
    }

    public void setProgress(float f) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setProgress(f);
        }
    }

    public void setImageResource(int i) {
        super.setImageResource(i);
        this.drawable = null;
    }

    public void playAnimation() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            this.playing = true;
            if (this.attachedToWindow) {
                rLottieDrawable.start();
                ImageReceiver imageReceiver2 = this.imageReceiver;
                if (imageReceiver2 != null) {
                    imageReceiver2.startAnimation();
                }
            }
        }
    }

    public void stopAnimation() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            this.playing = false;
            if (this.attachedToWindow) {
                rLottieDrawable.stop();
                ImageReceiver imageReceiver2 = this.imageReceiver;
                if (imageReceiver2 != null) {
                    imageReceiver2.stopAnimation();
                }
            }
        }
    }

    public RLottieDrawable getAnimatedDrawable() {
        return this.drawable;
    }
}
