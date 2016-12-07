package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress;

public class ContextLinkCell extends View implements FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static TextPaint descriptionTextPaint;
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private static Paint paint;
    private static Drawable shadowDrawable;
    private static TextPaint titleTextPaint;
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private MessageObject currentMessageObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(27.0f);
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private RadialProgress radialProgress;
    private float scale;
    private boolean scaled;
    private long time = 0;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(7.0f);

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        super(context);
        if (titleTextPaint == null) {
            titleTextPaint = new TextPaint(1);
            titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            titleTextPaint.setColor(-14606047);
            descriptionTextPaint = new TextPaint(1);
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        titleTextPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.linkImageView = new ImageReceiver(this);
        this.letterDrawable = new LetterDrawable();
        this.radialProgress = new RadialProgress(this);
        this.TAG = MediaController.getInstance().generateObserverTag();
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.linkLayout = null;
        this.linkY = AndroidUtilities.dp(27.0f);
        if (this.inlineResult == null && this.documentAttach == null) {
            setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
            return;
        }
        int width;
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxWidth = (viewWidth - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        PhotoSize currentPhotoObject = null;
        PhotoSize currentPhotoObjectThumb = null;
        ArrayList<PhotoSize> photoThumbs = null;
        String url = null;
        if (this.documentAttach != null) {
            photoThumbs = new ArrayList();
            photoThumbs.add(this.documentAttach.thumb);
        } else if (!(this.inlineResult == null || this.inlineResult.photo == null)) {
            ArrayList<PhotoSize> arrayList = new ArrayList(this.inlineResult.photo.sizes);
        }
        if (!(this.mediaWebpage || this.inlineResult == null)) {
            if (this.inlineResult.title != null) {
                try {
                    this.titleLayout = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(this.inlineResult.title.replace('\n', ' '), titleTextPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), titleTextPaint, (float) Math.min((int) Math.ceil((double) titleTextPaint.measureText(this.inlineResult.title)), maxWidth), TruncateAt.END), titleTextPaint, maxWidth + AndroidUtilities.dp(4.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                this.letterDrawable.setTitle(this.inlineResult.title);
            }
            if (this.inlineResult.description != null) {
                try {
                    this.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(this.inlineResult.description, descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.dp(13.0f), false), descriptionTextPaint, maxWidth, maxWidth, 0, 3);
                    if (this.descriptionLayout.getLineCount() > 0) {
                        this.linkY = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                }
            }
            if (this.inlineResult.url != null) {
                try {
                    this.linkLayout = new StaticLayout(TextUtils.ellipsize(this.inlineResult.url.replace('\n', ' '), descriptionTextPaint, (float) Math.min((int) Math.ceil((double) descriptionTextPaint.measureText(this.inlineResult.url)), maxWidth), TruncateAt.MIDDLE), descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                } catch (Throwable e22) {
                    FileLog.e("tmessages", e22);
                }
            }
        }
        String ext = null;
        if (this.documentAttach != null) {
            if (MessageObject.isGifDocument(this.documentAttach)) {
                currentPhotoObject = this.documentAttach.thumb;
            } else if (MessageObject.isStickerDocument(this.documentAttach)) {
                currentPhotoObject = this.documentAttach.thumb;
                ext = "webp";
            } else if (!(this.documentAttachType == 5 || this.documentAttachType == 3)) {
                currentPhotoObject = this.documentAttach.thumb;
            }
        } else if (!(this.inlineResult == null || this.inlineResult.photo == null)) {
            currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), true);
            currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
            if (currentPhotoObjectThumb == currentPhotoObject) {
                currentPhotoObjectThumb = null;
            }
        }
        if (this.inlineResult != null) {
            if (!(this.inlineResult.content_url == null || this.inlineResult.type == null)) {
                if (this.inlineResult.type.startsWith("gif")) {
                    if (this.documentAttachType != 2) {
                        url = this.inlineResult.content_url;
                        this.documentAttachType = 2;
                    }
                } else if (this.inlineResult.type.equals("photo")) {
                    url = this.inlineResult.thumb_url;
                    if (url == null) {
                        url = this.inlineResult.content_url;
                    }
                }
            }
            if (url == null && this.inlineResult.thumb_url != null) {
                url = this.inlineResult.thumb_url;
            }
        }
        if (url == null && currentPhotoObject == null && currentPhotoObjectThumb == null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo))) {
            double lat = this.inlineResult.send_message.geo.lat;
            double lon = this.inlineResult.send_message.geo._long;
            url = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density))), Double.valueOf(lat), Double.valueOf(lon)});
        }
        int w = 0;
        int h = 0;
        if (this.documentAttach != null) {
            for (int b = 0; b < this.documentAttach.attributes.size(); b++) {
                DocumentAttribute attribute = (DocumentAttribute) this.documentAttach.attributes.get(b);
                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                    w = attribute.w;
                    h = attribute.h;
                    break;
                }
            }
        }
        if (w == 0 || h == 0) {
            if (currentPhotoObject != null) {
                if (currentPhotoObjectThumb != null) {
                    currentPhotoObjectThumb.size = -1;
                }
                w = currentPhotoObject.w;
                h = currentPhotoObject.h;
            } else if (this.inlineResult != null) {
                w = this.inlineResult.w;
                h = this.inlineResult.h;
            }
        }
        if (w == 0 || h == 0) {
            h = AndroidUtilities.dp(80.0f);
            w = h;
        }
        if (!(this.documentAttach == null && currentPhotoObject == null && url == null)) {
            String currentPhotoFilter;
            String currentPhotoFilterThumb = "52_52_b";
            if (this.mediaWebpage) {
                width = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.dp(80.0f))));
                if (this.documentAttachType == 2) {
                    currentPhotoFilter = String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf((int) (((float) width) / AndroidUtilities.density)), Integer.valueOf(80)});
                    currentPhotoFilterThumb = currentPhotoFilter;
                } else {
                    currentPhotoFilter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) width) / AndroidUtilities.density)), Integer.valueOf(80)});
                    currentPhotoFilterThumb = currentPhotoFilter + "_b";
                }
            } else {
                currentPhotoFilter = "52_52";
            }
            this.linkImageView.setAspectFit(this.documentAttachType == 6);
            if (this.documentAttachType == 2) {
                if (this.documentAttach != null) {
                    FileLocation fileLocation;
                    ImageReceiver imageReceiver = this.linkImageView;
                    TLObject tLObject = this.documentAttach;
                    if (currentPhotoObject != null) {
                        fileLocation = currentPhotoObject.location;
                    } else {
                        fileLocation = null;
                    }
                    imageReceiver.setImage(tLObject, null, fileLocation, currentPhotoFilter, this.documentAttach.size, ext, false);
                } else {
                    this.linkImageView.setImage(null, url, null, null, currentPhotoObject != null ? currentPhotoObject.location : null, currentPhotoFilter, -1, ext, true);
                }
            } else if (currentPhotoObject != null) {
                this.linkImageView.setImage(currentPhotoObject.location, currentPhotoFilter, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilterThumb, currentPhotoObject.size, ext, false);
            } else {
                this.linkImageView.setImage(null, url, currentPhotoFilter, null, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilterThumb, -1, ext, true);
            }
            this.drawLinkImageView = true;
        }
        int height;
        if (this.mediaWebpage) {
            setBackgroundDrawable(null);
            width = viewWidth;
            height = MeasureSpec.getSize(heightMeasureSpec);
            if (height == 0) {
                height = AndroidUtilities.dp(100.0f);
            }
            setMeasuredDimension(width, height);
            int x = (width - AndroidUtilities.dp(24.0f)) / 2;
            int y = (height - AndroidUtilities.dp(24.0f)) / 2;
            this.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
            this.linkImageView.setImageCoords(0, 0, width, height);
            return;
        }
        setBackgroundResource(R.drawable.list_selector);
        height = 0;
        if (!(this.titleLayout == null || this.titleLayout.getLineCount() == 0)) {
            height = 0 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
        }
        if (!(this.descriptionLayout == null || this.descriptionLayout.getLineCount() == 0)) {
            height += this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
        }
        if (this.linkLayout != null && this.linkLayout.getLineCount() > 0) {
            height += this.linkLayout.getLineBottom(this.linkLayout.getLineCount() - 1);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.needDivider ? 1 : 0) + Math.max(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(16.0f) + Math.max(AndroidUtilities.dp(52.0f), height)));
        int maxPhotoWidth = AndroidUtilities.dp(52.0f);
        x = LocaleController.isRTL ? (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - maxPhotoWidth : AndroidUtilities.dp(8.0f);
        this.letterDrawable.setBounds(x, AndroidUtilities.dp(8.0f), x + maxPhotoWidth, AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
        this.linkImageView.setImageCoords(x, AndroidUtilities.dp(8.0f), maxPhotoWidth, maxPhotoWidth);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + x, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(48.0f) + x, AndroidUtilities.dp(56.0f));
        }
    }

    private void setAttachType() {
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        if (this.documentAttach != null) {
            if (MessageObject.isGifDocument(this.documentAttach)) {
                this.documentAttachType = 2;
            } else if (MessageObject.isStickerDocument(this.documentAttach)) {
                this.documentAttachType = 6;
            } else if (MessageObject.isMusicDocument(this.documentAttach)) {
                this.documentAttachType = 5;
            } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
                this.documentAttachType = 3;
            }
        } else if (this.inlineResult != null) {
            if (this.inlineResult.photo != null) {
                this.documentAttachType = 7;
            } else if (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                this.documentAttachType = 5;
            } else if (this.inlineResult.type.equals("voice")) {
                this.documentAttachType = 3;
            }
        }
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            TL_message message = new TL_message();
            message.out = true;
            message.id = -Utilities.random.nextInt();
            message.to_id = new TL_peerUser();
            Peer peer = message.to_id;
            int clientUserId = UserConfig.getClientUserId();
            message.from_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            message.message = "-1";
            message.media = new TL_messageMediaDocument();
            message.media.document = new TL_document();
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = "";
            } else {
                String str;
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.id = 0;
                message.media.document.access_hash = 0;
                message.media.document.date = message.date;
                message.media.document.mime_type = "audio/" + ext;
                message.media.document.size = 0;
                message.media.document.thumb = new TL_photoSizeEmpty();
                message.media.document.thumb.type = "s";
                message.media.document.dc_id = 0;
                TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.duration = this.inlineResult.duration;
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : "";
                attributeAudio.performer = this.inlineResult.description != null ? this.inlineResult.description : "";
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                fileName.file_name = Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getInstance().getDirectory(4);
                StringBuilder append = new StringBuilder().append(Utilities.MD5(this.inlineResult.content_url)).append(".");
                String str2 = this.inlineResult.content_url;
                if (this.documentAttachType == 5) {
                    str = "mp3";
                } else {
                    str = "ogg";
                }
                message.attachPath = new File(directory, append.append(ImageLoader.getHttpUrlExtension(str2, str)).toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(message, null, false);
        }
    }

    public void setLink(BotInlineResult contextResult, boolean media, boolean divider, boolean shadow) {
        this.needDivider = divider;
        this.needShadow = shadow;
        if (this.needShadow && shadowDrawable == null) {
            shadowDrawable = getContext().getResources().getDrawable(R.drawable.header_shadow);
        }
        this.inlineResult = contextResult;
        if (this.inlineResult == null || this.inlineResult.document == null) {
            this.documentAttach = null;
        } else {
            this.documentAttach = this.inlineResult.document;
        }
        this.mediaWebpage = media;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public void setGif(Document document, boolean divider) {
        this.needDivider = divider;
        this.needShadow = false;
        this.inlineResult = null;
        this.documentAttach = document;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public Document getDocument() {
        return this.documentAttach;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        MediaController.getInstance().removeLoadingFileObserver(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false);
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
            getBackground().setHotspot(event.getX(), event.getY());
        }
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        int side = AndroidUtilities.dp(48.0f);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            boolean area = this.letterDrawable.getBounds().contains(x, y);
            if (event.getAction() == 0) {
                if (area) {
                    this.buttonPressed = true;
                    invalidate();
                    result = true;
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                    invalidate();
                } else if (event.getAction() == 2 && !area) {
                    this.buttonPressed = false;
                    invalidate();
                }
                this.radialProgress.swapBackground(getDrawableForCurrentState());
            }
        } else if (!(this.inlineResult == null || this.inlineResult.content_url == null || this.inlineResult.content_url.length() <= 0)) {
            if (event.getAction() == 0) {
                if (this.letterDrawable.getBounds().contains(x, y)) {
                    this.buttonPressed = true;
                    result = true;
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    this.delegate.didPressedImage(this);
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                } else if (event.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                    this.buttonPressed = false;
                }
            }
        }
        if (result) {
            return result;
        }
        return super.onTouchEvent(event);
    }

    private void didPressedButton() {
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return;
        }
        if (this.buttonState == 0) {
            if (MediaController.getInstance().playAudio(this.currentMessageObject)) {
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 1) {
            if (MediaController.getInstance().pauseAudio(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 2) {
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttach != null) {
                FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            } else {
                ImageLoader.getInstance().loadHttpFile(this.inlineResult.content_url, this.documentAttachType == 5 ? "mp3" : "ogg");
            }
            this.buttonState = 4;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.buttonState == 4) {
            if (this.documentAttach != null) {
                FileLoader.getInstance().cancelLoadFile(this.documentAttach);
            } else {
                ImageLoader.getInstance().cancelLoadHttpFile(this.inlineResult.content_url);
            }
            this.buttonState = 2;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            descriptionTextPaint.setColor(-7697782);
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            descriptionTextPaint.setColor(Theme.MSG_LINK_TEXT_COLOR);
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int w;
        int h;
        int x;
        int y;
        if (this.mediaWebpage) {
            if (this.inlineResult != null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue))) {
                w = Theme.inlineLocationDrawable.getIntrinsicWidth();
                h = Theme.inlineLocationDrawable.getIntrinsicHeight();
                x = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - w) / 2);
                y = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - h) / 2);
                canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                Theme.inlineLocationDrawable.setBounds(x, y, x + w, y + h);
                Theme.inlineLocationDrawable.draw(canvas);
            }
        } else if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setProgressColor(this.buttonPressed ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : -1);
            this.radialProgress.draw(canvas);
        } else if (this.inlineResult != null && this.inlineResult.type.equals("file")) {
            w = Theme.inlineDocDrawable.getIntrinsicWidth();
            h = Theme.inlineDocDrawable.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.inlineDocDrawable.setBounds(x, y, x + w, y + h);
            Theme.inlineDocDrawable.draw(canvas);
        } else if (this.inlineResult != null && (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO) || this.inlineResult.type.equals("voice"))) {
            w = Theme.inlineAudioDrawable.getIntrinsicWidth();
            h = Theme.inlineAudioDrawable.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.inlineAudioDrawable.setBounds(x, y, x + w, y + h);
            Theme.inlineAudioDrawable.draw(canvas);
        } else if (this.inlineResult == null || !(this.inlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
            this.letterDrawable.draw(canvas);
        } else {
            w = Theme.inlineLocationDrawable.getIntrinsicWidth();
            h = Theme.inlineLocationDrawable.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.inlineLocationDrawable.setBounds(x, y, x + w, y + h);
            Theme.inlineLocationDrawable.draw(canvas);
        }
        if (this.drawLinkImageView) {
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (!this.scaled || this.scale == 0.8f) {
                    this.scale += ((float) dt) / 400.0f;
                    if (this.scale > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    }
                } else {
                    this.scale -= ((float) dt) / 400.0f;
                    if (this.scale < 0.8f) {
                        this.scale = 0.8f;
                    }
                }
                invalidate();
            }
            canvas.scale(this.scale, this.scale, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && (this.documentAttachType == 7 || this.documentAttachType == 2)) {
            this.radialProgress.setProgressColor(-1);
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), paint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), paint);
            }
        }
        if (this.needShadow && shadowDrawable != null) {
            shadowDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            shadowDrawable.draw(canvas);
        }
    }

    private Drawable getDrawableForCurrentState() {
        int i = 1;
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return this.buttonState == 1 ? Theme.photoStatesDrawables[5][0] : null;
        } else {
            if (this.buttonState == -1) {
                return null;
            }
            this.radialProgress.setAlphaForPrevious(false);
            Drawable[] drawableArr = Theme.fileStatesDrawable[this.buttonState + 5];
            if (!this.buttonPressed) {
                i = 0;
            }
            return drawableArr[i];
        }
    }

    public void updateButtonState(boolean animated) {
        String fileName = null;
        File cacheFile = null;
        if (this.documentAttachType == 5 || this.documentAttachType == 3) {
            if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            } else {
                fileName = this.inlineResult.content_url;
                cacheFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, this.documentAttachType == 5 ? "mp3" : "ogg"));
            }
        } else if (this.mediaWebpage) {
            if (this.inlineResult != null) {
                if (this.inlineResult.document instanceof TL_document) {
                    fileName = FileLoader.getAttachFileName(this.inlineResult.document);
                    cacheFile = FileLoader.getPathToAttach(this.inlineResult.document);
                } else if (this.inlineResult.photo instanceof TL_photo) {
                    PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                    fileName = FileLoader.getAttachFileName(currentPhotoObject);
                    cacheFile = FileLoader.getPathToAttach(currentPhotoObject);
                } else if (this.inlineResult.content_url != null) {
                    fileName = Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, "jpg");
                    cacheFile = new File(FileLoader.getInstance().getDirectory(4), fileName);
                } else if (this.inlineResult.thumb_url != null) {
                    fileName = Utilities.MD5(this.inlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb_url, "jpg");
                    cacheFile = new File(FileLoader.getInstance().getDirectory(4), fileName);
                }
            } else if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            }
        }
        if (TextUtils.isEmpty(fileName)) {
            this.radialProgress.setBackground(null, false, false);
            return;
        }
        if (cacheFile.exists() && cacheFile.length() == 0) {
            cacheFile.delete();
        }
        if (cacheFile.exists()) {
            MediaController.getInstance().removeLoadingFileObserver(this);
            if (this.documentAttachType == 5 || this.documentAttachType == 3) {
                boolean playing = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isAudioPaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
            } else {
                this.buttonState = -1;
            }
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            invalidate();
            return;
        }
        MediaController.getInstance().addLoadingFileObserver(fileName, this);
        Float progress;
        if (this.documentAttachType == 5 || this.documentAttachType == 3) {
            boolean isLoading;
            if (this.documentAttach != null) {
                isLoading = FileLoader.getInstance().isLoadingFile(fileName);
            } else {
                isLoading = ImageLoader.getInstance().isLoadingHttpFile(fileName);
            }
            if (isLoading) {
                this.buttonState = 4;
                progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    this.radialProgress.setProgress(progress.floatValue(), animated);
                } else {
                    this.radialProgress.setProgress(0.0f, animated);
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
            } else {
                this.buttonState = 2;
                this.radialProgress.setProgress(0.0f, animated);
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            }
        } else {
            this.buttonState = 1;
            progress = ImageLoader.getInstance().getFileProgress(fileName);
            this.radialProgress.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
        }
        invalidate();
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String fileName) {
        updateButtonState(false);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
        updateButtonState(true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
