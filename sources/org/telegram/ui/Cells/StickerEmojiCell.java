package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerEmojiCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private float alpha = 1.0f;
    private boolean changingAlpha;
    private int currentAccount = UserConfig.selectedAccount;
    private String currentEmoji;
    private TextView emojiTextView;
    private boolean fromEmojiPanel;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private TLRPC.Document sticker;
    private SendMessagesHelper.ImportingSticker stickerPath;
    private long time;

    public StickerEmojiCell(Context context, boolean isEmojiPanel) {
        super(context);
        this.fromEmojiPanel = isEmojiPanel;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        TextView textView = new TextView(context);
        this.emojiTextView = textView;
        textView.setTextSize(1, 16.0f);
        addView(this.emojiTextView, LayoutHelper.createFrame(28, 28, 85));
        setFocusable(true);
    }

    public TLRPC.Document getSticker() {
        return this.sticker;
    }

    public SendMessagesHelper.ImportingSticker getStickerPath() {
        SendMessagesHelper.ImportingSticker importingSticker = this.stickerPath;
        if (importingSticker == null || !importingSticker.validated) {
            return null;
        }
        return this.stickerPath;
    }

    public String getEmoji() {
        return this.currentEmoji;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public boolean isRecent() {
        return this.recent;
    }

    public void setRecent(boolean value) {
        this.recent = value;
    }

    public void setSticker(TLRPC.Document document, Object parent, boolean showEmoji) {
        setSticker(document, (SendMessagesHelper.ImportingSticker) null, parent, (String) null, showEmoji);
    }

    public void setSticker(SendMessagesHelper.ImportingSticker path) {
        setSticker((TLRPC.Document) null, path, (Object) null, path.emoji, path.emoji != null);
    }

    public MessageObject.SendAnimationData getSendAnimationData() {
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!imageReceiver.hasNotThumb()) {
            return null;
        }
        MessageObject.SendAnimationData data = new MessageObject.SendAnimationData();
        int[] position = new int[2];
        this.imageView.getLocationInWindow(position);
        data.x = imageReceiver.getCenterX() + ((float) position[0]);
        data.y = imageReceiver.getCenterY() + ((float) position[1]);
        data.width = imageReceiver.getImageWidth();
        data.height = imageReceiver.getImageHeight();
        return data;
    }

    public void setSticker(TLRPC.Document document, SendMessagesHelper.ImportingSticker path, Object parent, String emoji, boolean showEmoji) {
        String str;
        String str2;
        TLRPC.Document document2 = document;
        SendMessagesHelper.ImportingSticker importingSticker = path;
        String str3 = emoji;
        this.currentEmoji = str3;
        float f = 1.0f;
        if (importingSticker != null) {
            this.stickerPath = importingSticker;
            if (importingSticker.validated) {
                BackupImageView backupImageView = this.imageView;
                ImageLocation forPath = ImageLocation.getForPath(importingSticker.path);
                SvgHelper.SvgDrawable svgRectThumb = DocumentObject.getSvgRectThumb("dialogBackgroundGray", 1.0f);
                if (importingSticker.animated) {
                    str2 = "tgs";
                } else {
                    str2 = null;
                }
                backupImageView.setImage(forPath, "80_80", (ImageLocation) null, (String) null, svgRectThumb, (Bitmap) null, str2, 0, (Object) null);
            } else {
                BackupImageView backupImageView2 = this.imageView;
                SvgHelper.SvgDrawable svgRectThumb2 = DocumentObject.getSvgRectThumb("dialogBackgroundGray", 1.0f);
                if (importingSticker.animated) {
                    str = "tgs";
                } else {
                    str = null;
                }
                backupImageView2.setImage((ImageLocation) null, (String) null, (ImageLocation) null, (String) null, svgRectThumb2, (Bitmap) null, str, 0, (Object) null);
            }
            if (str3 != null) {
                TextView textView = this.emojiTextView;
                textView.setText(Emoji.replaceEmoji(str3, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
                Object obj = parent;
                return;
            }
            this.emojiTextView.setVisibility(4);
            Object obj2 = parent;
        } else if (document2 != null) {
            this.sticker = document2;
            this.parentObject = parent;
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
            boolean z = this.fromEmojiPanel;
            String str4 = z ? "emptyListPlaceholder" : "windowBackgroundGray";
            if (z) {
                f = 0.2f;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document2, str4, f);
            if (MessageObject.canAutoplayAnimatedSticker(document)) {
                if (svgThumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", (String) null, (Drawable) svgThumb, this.parentObject);
                } else if (thumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", ImageLocation.getForDocument(thumb, document2), (String) null, 0, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "66_66", (String) null, (Drawable) null, this.parentObject);
                }
            } else if (svgThumb != null) {
                if (thumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(thumb, document2), (String) null, "webp", (Drawable) svgThumb, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(document), (String) null, "webp", (Drawable) svgThumb, this.parentObject);
                }
            } else if (thumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(thumb, document2), (String) null, "webp", (Drawable) null, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(document), (String) null, "webp", (Drawable) null, this.parentObject);
            }
            if (str3 != null) {
                TextView textView2 = this.emojiTextView;
                textView2.setText(Emoji.replaceEmoji(str3, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
            } else if (showEmoji) {
                boolean set = false;
                int a = 0;
                while (true) {
                    if (a >= document2.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = document2.attributes.get(a);
                    if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                        a++;
                    } else if (attribute.alt != null && attribute.alt.length() > 0) {
                        this.emojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                        set = true;
                    }
                }
                if (!set) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                }
                this.emojiTextView.setVisibility(0);
            } else {
                this.emojiTextView.setVisibility(4);
            }
        } else {
            Object obj3 = parent;
        }
    }

    public void disable() {
        this.changingAlpha = true;
        this.alpha = 0.5f;
        this.time = 0;
        this.imageView.getImageReceiver().setAlpha(this.alpha);
        this.imageView.invalidate();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean isDisabled() {
        return this.changingAlpha;
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void invalidate() {
        this.emojiTextView.invalidate();
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean z;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && (this.changingAlpha || ((z && this.scale != 0.8f) || (!(z = this.scaled) && this.scale != 1.0f)))) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.changingAlpha) {
                long j = this.time + dt;
                this.time = j;
                if (j > 1050) {
                    this.time = 1050;
                }
                float interpolation = (interpolator.getInterpolation(((float) this.time) / 1050.0f) * 0.5f) + 0.5f;
                this.alpha = interpolation;
                if (interpolation >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha);
            } else {
                if (this.scaled) {
                    float f = this.scale;
                    if (f != 0.8f) {
                        float f2 = f - (((float) dt) / 400.0f);
                        this.scale = f2;
                        if (f2 < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                float f3 = this.scale + (((float) dt) / 400.0f);
                this.scale = f3;
                if (f3 > 1.0f) {
                    this.scale = 1.0f;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return result;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        String descr = LocaleController.getString("AttachSticker", NUM);
        if (this.sticker != null) {
            int a = 0;
            while (true) {
                if (a >= this.sticker.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = this.sticker.attributes.get(a);
                if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                    a++;
                } else if (attribute.alt != null && attribute.alt.length() > 0) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                    descr = attribute.alt + " " + descr;
                }
            }
        }
        info.setContentDescription(descr);
        info.setEnabled(true);
    }
}
