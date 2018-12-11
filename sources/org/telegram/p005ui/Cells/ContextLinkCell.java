package org.telegram.p005ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LetterDrawable;
import org.telegram.p005ui.Components.RadialProgress2;
import org.telegram.p005ui.PhotoViewer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.MessageMedia;
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
import org.telegram.tgnet.TLRPC.TL_webDocument;

/* renamed from: org.telegram.ui.Cells.ContextLinkCell */
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
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    private boolean buttonPressed;
    private int buttonState;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.m9dp(27.0f);
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable = new LetterDrawable();
    private ImageReceiver linkImageView = new ImageReceiver(this);
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private Object parentObject;
    private RadialProgress2 radialProgress = new RadialProgress2(this);
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.m9dp(7.0f);

    /* renamed from: org.telegram.ui.Cells.ContextLinkCell$ContextLinkCellDelegate */
    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        super(context);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.linkLayout = null;
        this.currentPhotoObject = null;
        this.linkY = AndroidUtilities.m9dp(27.0f);
        if (this.inlineResult == null && this.documentAttach == null) {
            setMeasuredDimension(AndroidUtilities.m9dp(100.0f), AndroidUtilities.m9dp(100.0f));
            return;
        }
        int width;
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxWidth = (viewWidth - AndroidUtilities.m9dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.m9dp(8.0f);
        PhotoSize currentPhotoObjectThumb = null;
        ArrayList<PhotoSize> photoThumbs = null;
        TLObject webFile = null;
        TL_webDocument webDocument = null;
        String urlLocation = null;
        if (this.documentAttach != null) {
            photoThumbs = new ArrayList();
            photoThumbs.add(this.documentAttach.thumb);
        } else if (!(this.inlineResult == null || this.inlineResult.photo == null)) {
            ArrayList<PhotoSize> arrayList = new ArrayList(this.inlineResult.photo.sizes);
        }
        if (!(this.mediaWebpage || this.inlineResult == null)) {
            if (this.inlineResult.title != null) {
                try {
                    this.titleLayout = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(this.inlineResult.title.replace(10, ' '), Theme.chat_contextResult_titleTextPaint.getFontMetricsInt(), AndroidUtilities.m9dp(15.0f), false), Theme.chat_contextResult_titleTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_titleTextPaint.measureText(this.inlineResult.title)), maxWidth), TruncateAt.END), Theme.chat_contextResult_titleTextPaint, maxWidth + AndroidUtilities.m9dp(4.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                this.letterDrawable.setTitle(this.inlineResult.title);
            }
            if (this.inlineResult.description != null) {
                try {
                    this.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(this.inlineResult.description, Theme.chat_contextResult_descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.m9dp(13.0f), false), Theme.chat_contextResult_descriptionTextPaint, maxWidth, maxWidth, 0, 3);
                    if (this.descriptionLayout.getLineCount() > 0) {
                        this.linkY = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.m9dp(1.0f);
                    }
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                }
            }
            if (this.inlineResult.url != null) {
                try {
                    this.linkLayout = new StaticLayout(TextUtils.ellipsize(this.inlineResult.url.replace(10, ' '), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(this.inlineResult.url)), maxWidth), TruncateAt.MIDDLE), Theme.chat_contextResult_descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                } catch (Throwable e22) {
                    FileLog.m13e(e22);
                }
            }
        }
        String ext = null;
        if (this.documentAttach != null) {
            if (MessageObject.isGifDocument(this.documentAttach)) {
                this.currentPhotoObject = this.documentAttach.thumb;
            } else if (MessageObject.isStickerDocument(this.documentAttach)) {
                this.currentPhotoObject = this.documentAttach.thumb;
                ext = "webp";
            } else if (!(this.documentAttachType == 5 || this.documentAttachType == 3)) {
                this.currentPhotoObject = this.documentAttach.thumb;
            }
        } else if (!(this.inlineResult == null || this.inlineResult.photo == null)) {
            this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, AndroidUtilities.getPhotoSize(), true);
            currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(photoThumbs, 80);
            if (currentPhotoObjectThumb == this.currentPhotoObject) {
                currentPhotoObjectThumb = null;
            }
        }
        if (this.inlineResult != null) {
            if ((this.inlineResult.content instanceof TL_webDocument) && this.inlineResult.type != null) {
                if (this.inlineResult.type.startsWith("gif")) {
                    if (this.documentAttachType != 2) {
                        webDocument = (TL_webDocument) this.inlineResult.content;
                        this.documentAttachType = 2;
                    }
                } else if (this.inlineResult.type.equals("photo")) {
                    webDocument = this.inlineResult.thumb instanceof TL_webDocument ? (TL_webDocument) this.inlineResult.thumb : (TL_webDocument) this.inlineResult.content;
                }
            }
            if (webDocument == null && (this.inlineResult.thumb instanceof TL_webDocument)) {
                webDocument = (TL_webDocument) this.inlineResult.thumb;
            }
            if (webDocument == null && this.currentPhotoObject == null && currentPhotoObjectThumb == null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo))) {
                double lat = this.inlineResult.send_message.geo.lat;
                double lon = this.inlineResult.send_message.geo._long;
                if (MessagesController.getInstance(this.currentAccount).mapProvider == 2) {
                    webFile = WebFile.createWithGeoPoint(this.inlineResult.send_message.geo, 72, 72, 15, Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                } else {
                    urlLocation = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, 72, 72, true, 15);
                }
            }
            if (webDocument != null) {
                webFile = WebFile.createWithWebDocument(webDocument);
            }
        }
        int w = 0;
        int h = 0;
        if (this.documentAttach != null) {
            for (int b = 0; b < this.documentAttach.attributes.size(); b++) {
                DocumentAttribute attribute = (DocumentAttribute) this.documentAttach.attributes.get(b);
                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                    w = attribute.var_w;
                    h = attribute.var_h;
                    break;
                }
            }
        }
        if (w == 0 || h == 0) {
            if (this.currentPhotoObject != null) {
                if (currentPhotoObjectThumb != null) {
                    currentPhotoObjectThumb.size = -1;
                }
                w = this.currentPhotoObject.var_w;
                h = this.currentPhotoObject.var_h;
            } else if (this.inlineResult != null) {
                int[] result = MessageObject.getInlineResultWidthAndHeight(this.inlineResult);
                w = result[0];
                h = result[1];
            }
        }
        if (w == 0 || h == 0) {
            h = AndroidUtilities.m9dp(80.0f);
            w = h;
        }
        if (!(this.documentAttach == null && this.currentPhotoObject == null && webFile == null && urlLocation == null)) {
            String currentPhotoFilter;
            String currentPhotoFilterThumb = "52_52_b";
            if (this.mediaWebpage) {
                width = (int) (((float) w) / (((float) h) / ((float) AndroidUtilities.m9dp(80.0f))));
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
                    if (this.currentPhotoObject != null) {
                        fileLocation = this.currentPhotoObject.location;
                    } else {
                        fileLocation = null;
                    }
                    imageReceiver.setImage(tLObject, null, fileLocation, currentPhotoFilter, this.documentAttach.size, ext, this.parentObject, null);
                } else {
                    this.linkImageView.setImage(webFile, urlLocation, null, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, currentPhotoFilter, -1, ext, this.parentObject, 1);
                }
            } else if (this.currentPhotoObject != null) {
                this.linkImageView.setImage(this.currentPhotoObject.location, currentPhotoFilter, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilterThumb, this.currentPhotoObject.size, ext, this.parentObject, 0);
            } else {
                this.linkImageView.setImage(webFile, urlLocation, currentPhotoFilter, null, currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null, currentPhotoFilterThumb, -1, ext, this.parentObject, 1);
            }
            this.drawLinkImageView = true;
        }
        int height;
        int x;
        if (this.mediaWebpage) {
            width = viewWidth;
            height = MeasureSpec.getSize(heightMeasureSpec);
            if (height == 0) {
                height = AndroidUtilities.m9dp(100.0f);
            }
            setMeasuredDimension(width, height);
            x = (width - AndroidUtilities.m9dp(24.0f)) / 2;
            int y = (height - AndroidUtilities.m9dp(24.0f)) / 2;
            this.radialProgress.setProgressRect(x, y, AndroidUtilities.m9dp(24.0f) + x, AndroidUtilities.m9dp(24.0f) + y);
            this.radialProgress.setCircleRadius(AndroidUtilities.m9dp(12.0f));
            this.linkImageView.setImageCoords(0, 0, width, height);
            return;
        }
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
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.needDivider ? 1 : 0) + Math.max(AndroidUtilities.m9dp(68.0f), AndroidUtilities.m9dp(16.0f) + Math.max(AndroidUtilities.m9dp(52.0f), height)));
        int maxPhotoWidth = AndroidUtilities.m9dp(52.0f);
        x = LocaleController.isRTL ? (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.m9dp(8.0f)) - maxPhotoWidth : AndroidUtilities.m9dp(8.0f);
        this.letterDrawable.setBounds(x, AndroidUtilities.m9dp(8.0f), x + maxPhotoWidth, AndroidUtilities.m9dp(60.0f));
        this.linkImageView.setImageCoords(x, AndroidUtilities.m9dp(8.0f), maxPhotoWidth, maxPhotoWidth);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setCircleRadius(AndroidUtilities.m9dp(24.0f));
            this.radialProgress.setProgressRect(AndroidUtilities.m9dp(4.0f) + x, AndroidUtilities.m9dp(12.0f), AndroidUtilities.m9dp(48.0f) + x, AndroidUtilities.m9dp(56.0f));
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
            message.var_id = -Utilities.random.nextInt();
            message.to_id = new TL_peerUser();
            Peer peer = message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            message.from_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            message.message = TtmlNode.ANONYMOUS_REGION_ID;
            message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = message.media;
            messageMedia.flags |= 3;
            message.media.document = new TL_document();
            message.media.document.file_reference = new byte[0];
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                String str;
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.var_id = 0;
                message.media.document.access_hash = 0;
                message.media.document.date = message.date;
                message.media.document.mime_type = "audio/" + ext;
                message.media.document.size = 0;
                message.media.document.thumb = new TL_photoSizeEmpty();
                message.media.document.thumb.type = "s";
                message.media.document.dc_id = 0;
                TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.performer = this.inlineResult.description != null ? this.inlineResult.description : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                fileName.file_name = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getDirectory(4);
                StringBuilder append = new StringBuilder().append(Utilities.MD5(this.inlineResult.content.url)).append(".");
                String str2 = this.inlineResult.content.url;
                if (this.documentAttachType == 5) {
                    str = "mp3";
                } else {
                    str = "ogg";
                }
                message.attachPath = new File(directory, append.append(ImageLoader.getHttpUrlExtension(str2, str)).toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, message, false);
        }
    }

    public void setLink(BotInlineResult contextResult, boolean media, boolean divider, boolean shadow) {
        this.needDivider = divider;
        this.needShadow = shadow;
        this.inlineResult = contextResult;
        this.parentObject = contextResult;
        if (this.inlineResult == null || this.inlineResult.document == null) {
            this.documentAttach = null;
        } else {
            this.documentAttach = this.inlineResult.document;
        }
        this.mediaWebpage = media;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public void setGif(Document document, boolean divider) {
        this.needDivider = divider;
        this.needShadow = false;
        this.inlineResult = null;
        this.parentObject = "gif";
        this.documentAttach = document;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
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
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false, false);
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        int side = AndroidUtilities.m9dp(48.0f);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            boolean area = this.letterDrawable.getBounds().contains(x, y);
            if (event.getAction() == 0) {
                if (area) {
                    this.buttonPressed = true;
                    this.radialProgress.setPressed(this.buttonPressed, false);
                    invalidate();
                    result = true;
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
                this.radialProgress.setPressed(this.buttonPressed, false);
            }
        } else if (!(this.inlineResult == null || this.inlineResult.content == null || TextUtils.isEmpty(this.inlineResult.content.url))) {
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
            if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, false, true);
                invalidate();
            }
        } else if (this.buttonState == 1) {
            if (MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, false, true);
                invalidate();
            }
        } else if (this.buttonState == 2) {
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.inlineResult, 1, 0);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), 1, 1);
            }
            this.buttonState = 4;
            this.radialProgress.setIcon(getIconForCurrentState(), true, false, true);
            invalidate();
        } else if (this.buttonState == 4) {
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(WebFile.createWithWebDocument(this.inlineResult.content));
            }
            this.buttonState = 2;
            this.radialProgress.setIcon(getIconForCurrentState(), false, false, true);
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.m9dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            canvas.translate((float) AndroidUtilities.m9dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            canvas.save();
            canvas.translate((float) AndroidUtilities.m9dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int w;
        int h;
        int x;
        int y;
        if (this.mediaWebpage) {
            if (this.inlineResult != null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue))) {
                w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                x = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - w) / 2);
                y = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - h) / 2);
                canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
                Theme.chat_inlineResultLocation.draw(canvas);
            }
        } else if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
            this.radialProgress.draw(canvas);
        } else if (this.inlineResult != null && this.inlineResult.type.equals("file")) {
            w = Theme.chat_inlineResultFile.getIntrinsicWidth();
            h = Theme.chat_inlineResultFile.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.m9dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.m9dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.m9dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.m9dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultFile.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultFile.draw(canvas);
        } else if (this.inlineResult != null && (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO) || this.inlineResult.type.equals("voice"))) {
            w = Theme.chat_inlineResultAudio.getIntrinsicWidth();
            h = Theme.chat_inlineResultAudio.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.m9dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.m9dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.m9dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.m9dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultAudio.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultAudio.draw(canvas);
        } else if (this.inlineResult == null || !(this.inlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
            this.letterDrawable.draw(canvas);
        } else {
            w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
            h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.m9dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.m9dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.m9dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.m9dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultLocation.draw(canvas);
        }
        if (this.drawLinkImageView) {
            if (this.inlineResult != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(this.inlineResult), false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (!this.scaled || this.scale == 0.8f) {
                    this.scale += ((float) dt) / 400.0f;
                    if (this.scale > 1.0f) {
                        this.scale = 1.0f;
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
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.m9dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.m9dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        if (this.needShadow) {
            Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.m9dp(3.0f));
            Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(canvas);
        }
    }

    private int getIconForCurrentState() {
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setColors(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
            if (this.buttonState == 1) {
                return 1;
            }
            if (this.buttonState == 2) {
                return 2;
            }
            if (this.buttonState == 4) {
                return 3;
            }
            return 0;
        }
        this.radialProgress.setColors(Theme.key_chat_mediaLoaderPhoto, Theme.key_chat_mediaLoaderPhotoSelected, Theme.key_chat_mediaLoaderPhotoIcon, Theme.key_chat_mediaLoaderPhotoIconSelected);
        if (this.buttonState != 1) {
            return 4;
        }
        return 5;
    }

    public void updateButtonState(boolean ifSame, boolean animated) {
        float setProgress = 0.0f;
        String fileName = null;
        File cacheFile = null;
        if (this.documentAttachType == 5 || this.documentAttachType == 3) {
            if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            }
        } else if (this.mediaWebpage) {
            if (this.inlineResult != null) {
                if (this.inlineResult.document instanceof TL_document) {
                    fileName = FileLoader.getAttachFileName(this.inlineResult.document);
                    cacheFile = FileLoader.getPathToAttach(this.inlineResult.document);
                } else if (this.inlineResult.photo instanceof TL_photo) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                    fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                    cacheFile = FileLoader.getPathToAttach(this.currentPhotoObject);
                } else if (this.inlineResult.content instanceof TL_webDocument) {
                    fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, "jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), fileName);
                } else if (this.inlineResult.thumb instanceof TL_webDocument) {
                    fileName = Utilities.MD5(this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, "jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), fileName);
                }
            } else if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            }
        }
        if (!TextUtils.isEmpty(fileName)) {
            if (cacheFile.exists()) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                if (this.documentAttachType == 5 || this.documentAttachType == 3) {
                    boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                    if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                        this.buttonState = 0;
                    } else {
                        this.buttonState = 1;
                    }
                    this.radialProgress.setProgress(1.0f, animated);
                } else {
                    this.buttonState = -1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, ifSame, animated);
                invalidate();
                return;
            }
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            Float progress;
            if (this.documentAttachType == 5 || this.documentAttachType == 3) {
                boolean isLoading;
                if (this.documentAttach != null) {
                    isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
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
                    this.radialProgress.setIcon(getIconForCurrentState(), true, ifSame, animated);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setIcon(getIconForCurrentState(), false, ifSame, animated);
                }
            } else {
                this.buttonState = 1;
                progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    setProgress = progress.floatValue();
                }
                this.radialProgress.setProgress(setProgress, false);
                this.radialProgress.setIcon(getIconForCurrentState(), true, ifSame, animated);
            }
            invalidate();
        }
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, true);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
