package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.FlickerLoadingView;

public class SharedPhotoVideoCell2 extends View {
    static boolean lastAutoDownload;
    static long lastUpdateDownloadSettingsTime;
    ValueAnimator animator;
    private boolean attached;
    CheckBoxBase checkBoxBase;
    float checkBoxProgress;
    float crossfadeProgress;
    float crossfadeToColumnsCount;
    SharedPhotoVideoCell2 crossfadeView;
    int currentAccount;
    MessageObject currentMessageObject;
    int currentParentColumnsCount;
    FlickerLoadingView globalGradientView;
    float highlightProgress;
    float imageAlpha = 1.0f;
    public ImageReceiver imageReceiver = new ImageReceiver();
    float imageScale = 1.0f;
    SharedResources sharedResources;
    boolean showVideoLayout;
    StaticLayout videoInfoLayot;
    String videoText;

    public SharedPhotoVideoCell2(Context context, SharedResources sharedResources2, int currentAccount2) {
        super(context);
        this.sharedResources = sharedResources2;
        this.currentAccount = currentAccount2;
        setChecked(false, false);
        this.imageReceiver.setParentView(this);
    }

    public void setMessageObject(MessageObject messageObject, int parentColumnsCount) {
        int stride;
        TLRPC.PhotoSize currentPhotoObjectThumb;
        TLRPC.PhotoSize qualityThumb;
        int stride2;
        String imageFilter;
        MessageObject messageObject2 = messageObject;
        int i = parentColumnsCount;
        int oldParentColumsCount = this.currentParentColumnsCount;
        this.currentParentColumnsCount = i;
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 != null || messageObject2 != null) {
            if (messageObject3 == null || messageObject2 == null || messageObject3.getId() != messageObject.getId() || oldParentColumsCount != i) {
                this.currentMessageObject = messageObject2;
                if (messageObject2 == null) {
                    this.imageReceiver.onDetachedFromWindow();
                    this.videoText = null;
                    this.videoInfoLayot = null;
                    this.showVideoLayout = false;
                    return;
                }
                if (this.attached) {
                    this.imageReceiver.onAttachedToWindow();
                }
                String restrictionReason = MessagesController.getRestrictionReason(messageObject2.messageOwner.restriction_reason);
                String imageFilter2 = this.sharedResources.getFilterString((int) (((float) (AndroidUtilities.displaySize.x / i)) / AndroidUtilities.density));
                boolean showImageStub = false;
                if (i <= 2) {
                    stride = AndroidUtilities.getPhotoSize();
                } else if (i == 3) {
                    stride = 320;
                } else if (i == 5) {
                    stride = 320;
                } else {
                    stride = 320;
                }
                this.videoText = null;
                this.videoInfoLayot = null;
                this.showVideoLayout = false;
                if (!TextUtils.isEmpty(restrictionReason)) {
                    showImageStub = true;
                    int i2 = stride;
                    String str = imageFilter2;
                } else if (messageObject.isVideo()) {
                    this.showVideoLayout = true;
                    if (i != 9) {
                        this.videoText = AndroidUtilities.formatShortDuration(messageObject.getDuration());
                    }
                    if (messageObject2.mediaThumb == null) {
                        int stride3 = stride;
                        String imageFilter3 = imageFilter2;
                        TLRPC.Document document = messageObject.getDocument();
                        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                        int stride4 = stride3;
                        TLRPC.PhotoSize qualityThumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, stride4);
                        if (thumb == qualityThumb2) {
                            qualityThumb = null;
                        } else {
                            qualityThumb = qualityThumb2;
                        }
                        if (thumb == null) {
                            stride2 = stride4;
                            TLRPC.PhotoSize photoSize = thumb;
                            imageFilter = imageFilter3;
                            TLRPC.Document document2 = document;
                            showImageStub = true;
                        } else if (messageObject2.strippedThumb != null) {
                            int stride5 = stride4;
                            TLRPC.PhotoSize photoSize2 = qualityThumb;
                            this.imageReceiver.setImage(ImageLocation.getForDocument(qualityThumb, document), imageFilter3, messageObject2.strippedThumb, (String) null, messageObject, 0);
                            stride2 = stride5;
                            imageFilter = imageFilter3;
                        } else {
                            int stride6 = stride4;
                            TLRPC.PhotoSize qualityThumb3 = qualityThumb;
                            ImageReceiver imageReceiver2 = this.imageReceiver;
                            ImageLocation forDocument = ImageLocation.getForDocument(qualityThumb3, document);
                            ImageLocation forDocument2 = ImageLocation.getForDocument(thumb, document);
                            StringBuilder sb = new StringBuilder();
                            String imageFilter4 = imageFilter3;
                            sb.append(imageFilter4);
                            sb.append("_b");
                            imageFilter = imageFilter4;
                            TLRPC.PhotoSize photoSize3 = qualityThumb3;
                            stride2 = stride6;
                            TLRPC.PhotoSize photoSize4 = thumb;
                            TLRPC.Document document3 = document;
                            imageReceiver2.setImage(forDocument, imageFilter4, forDocument2, sb.toString(), (Drawable) null, 0, (String) null, messageObject, 0);
                        }
                        String str2 = imageFilter;
                        int i3 = stride2;
                    } else if (messageObject2.strippedThumb != null) {
                        this.imageReceiver.setImage(messageObject2.mediaThumb, imageFilter2, messageObject2.strippedThumb, (String) null, messageObject, 0);
                        int i4 = stride;
                        String str3 = imageFilter2;
                    } else {
                        int i5 = stride;
                        String str4 = imageFilter2;
                        this.imageReceiver.setImage(messageObject2.mediaThumb, imageFilter2, messageObject2.mediaSmallThumb, imageFilter2 + "_b", (Drawable) null, 0, (String) null, messageObject, 0);
                    }
                } else {
                    int stride7 = stride;
                    String imageFilter5 = imageFilter2;
                    if (!(messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject2.messageOwner.media.photo == null || messageObject2.photoThumbs.isEmpty()) {
                        int i6 = stride7;
                        showImageStub = true;
                    } else if (messageObject2.mediaExists || canAutoDownload(messageObject)) {
                        if (messageObject2.mediaThumb == null) {
                            String imageFilter6 = imageFilter5;
                            TLRPC.PhotoSize currentPhotoObjectThumb2 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 50);
                            int stride8 = stride7;
                            TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, stride8, false, currentPhotoObjectThumb2, false);
                            if (currentPhotoObject == currentPhotoObjectThumb2) {
                                currentPhotoObjectThumb = null;
                            } else {
                                currentPhotoObjectThumb = currentPhotoObjectThumb2;
                            }
                            if (messageObject2.strippedThumb != null) {
                                TLRPC.PhotoSize photoSize5 = currentPhotoObjectThumb;
                                TLRPC.PhotoSize photoSize6 = currentPhotoObject;
                                int i7 = stride8;
                                this.imageReceiver.setImage(ImageLocation.getForObject(currentPhotoObject, messageObject2.photoThumbsObject), imageFilter6, (ImageLocation) null, (String) null, messageObject2.strippedThumb, currentPhotoObject != null ? currentPhotoObject.size : 0, (String) null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                            } else {
                                int i8 = stride8;
                                ImageReceiver imageReceiver3 = this.imageReceiver;
                                TLRPC.PhotoSize currentPhotoObject2 = currentPhotoObject;
                                ImageLocation forObject = ImageLocation.getForObject(currentPhotoObject2, messageObject2.photoThumbsObject);
                                TLRPC.PhotoSize currentPhotoObjectThumb3 = currentPhotoObjectThumb;
                                ImageLocation forObject2 = ImageLocation.getForObject(currentPhotoObjectThumb3, messageObject2.photoThumbsObject);
                                StringBuilder sb2 = new StringBuilder();
                                String imageFilter7 = imageFilter6;
                                sb2.append(imageFilter7);
                                sb2.append("_b");
                                String str5 = imageFilter7;
                                TLRPC.PhotoSize photoSize7 = currentPhotoObjectThumb3;
                                imageReceiver3.setImage(forObject, imageFilter7, forObject2, sb2.toString(), currentPhotoObject2 != null ? currentPhotoObject2.size : 0, (String) null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                            }
                        } else if (messageObject2.strippedThumb != null) {
                            this.imageReceiver.setImage(messageObject2.mediaThumb, imageFilter5, messageObject2.strippedThumb, (String) null, messageObject, 0);
                            String str6 = imageFilter5;
                            int i9 = stride7;
                        } else {
                            ImageReceiver imageReceiver4 = this.imageReceiver;
                            ImageLocation imageLocation = messageObject2.mediaThumb;
                            ImageLocation imageLocation2 = messageObject2.mediaSmallThumb;
                            StringBuilder sb3 = new StringBuilder();
                            String imageFilter8 = imageFilter5;
                            sb3.append(imageFilter8);
                            sb3.append("_b");
                            String str7 = imageFilter8;
                            imageReceiver4.setImage(imageLocation, imageFilter8, imageLocation2, sb3.toString(), (Drawable) null, 0, (String) null, messageObject, 0);
                            int i10 = stride7;
                        }
                    } else if (messageObject2.strippedThumb != null) {
                        this.imageReceiver.setImage((ImageLocation) null, (String) null, (ImageLocation) null, (String) null, messageObject2.strippedThumb, 0, (String) null, messageObject, 0);
                        String str8 = imageFilter5;
                        int i11 = stride7;
                    } else {
                        TLRPC.PhotoSize currentPhotoObjectThumb4 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 50);
                        TLRPC.PhotoSize photoSize8 = currentPhotoObjectThumb4;
                        this.imageReceiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(currentPhotoObjectThumb4, messageObject2.photoThumbsObject), "b", (Drawable) null, 0, (String) null, messageObject, 0);
                        String str9 = imageFilter5;
                        int i12 = stride7;
                    }
                }
                if (showImageStub) {
                    this.imageReceiver.setImageBitmap(ContextCompat.getDrawable(getContext(), NUM));
                }
                invalidate();
            }
        }
    }

    private boolean canAutoDownload(MessageObject messageObject) {
        if (System.currentTimeMillis() - lastUpdateDownloadSettingsTime > 5000) {
            lastUpdateDownloadSettingsTime = System.currentTimeMillis();
            lastAutoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(messageObject);
        }
        return lastAutoDownload;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x010c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x010d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r0 = r25
            r8 = r26
            super.onDraw(r26)
            float r1 = r0.crossfadeProgress
            r9 = 1091567616(0x41100000, float:9.0)
            r10 = 9
            r11 = 1056964608(0x3var_, float:0.5)
            r12 = 0
            r13 = 1065353216(0x3var_, float:1.0)
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x0050
            float r1 = r0.crossfadeToColumnsCount
            int r2 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0020
            int r2 = r0.currentParentColumnsCount
            if (r2 != r10) goto L_0x0050
        L_0x0020:
            int r1 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r1 != 0) goto L_0x003a
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r1 = (float) r1
            float r2 = r0.crossfadeProgress
            float r1 = r1 * r2
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r13)
            float r3 = r0.crossfadeProgress
            float r3 = r13 - r3
            float r2 = r2 * r3
            float r1 = r1 + r2
            r14 = r1
            goto L_0x005e
        L_0x003a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r1 = (float) r1
            float r2 = r0.crossfadeProgress
            float r1 = r1 * r2
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r11)
            float r3 = r0.crossfadeProgress
            float r3 = r13 - r3
            float r2 = r2 * r3
            float r1 = r1 + r2
            r14 = r1
            goto L_0x005e
        L_0x0050:
            int r1 = r0.currentParentColumnsCount
            if (r1 != r10) goto L_0x0059
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r11)
            goto L_0x005d
        L_0x0059:
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r13)
        L_0x005d:
            r14 = r1
        L_0x005e:
            int r1 = r25.getMeasuredWidth()
            float r1 = (float) r1
            r15 = 1073741824(0x40000000, float:2.0)
            float r2 = r14 * r15
            float r1 = r1 - r2
            float r2 = r0.imageScale
            float r1 = r1 * r2
            int r2 = r25.getMeasuredHeight()
            float r2 = (float) r2
            float r3 = r14 * r15
            float r2 = r2 - r3
            float r3 = r0.imageScale
            float r2 = r2 * r3
            float r3 = r0.crossfadeProgress
            int r3 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x008d
            float r3 = r0.crossfadeToColumnsCount
            int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x008d
            int r3 = r0.currentParentColumnsCount
            if (r3 == r10) goto L_0x008d
            float r1 = r1 - r15
            float r2 = r2 - r15
            r7 = r1
            r6 = r2
            goto L_0x008f
        L_0x008d:
            r7 = r1
            r6 = r2
        L_0x008f:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x00ae
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            boolean r1 = r1.hasBitmapImage()
            if (r1 == 0) goto L_0x00ae
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            float r1 = r1.getCurrentAlpha()
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x00ae
            float r1 = r0.imageAlpha
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x00ac
            goto L_0x00ae
        L_0x00ac:
            r10 = r6
            goto L_0x0108
        L_0x00ae:
            android.view.ViewParent r1 = r25.getParent()
            if (r1 == 0) goto L_0x0104
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            android.view.ViewParent r2 = r25.getParent()
            android.view.View r2 = (android.view.View) r2
            int r2 = r2.getMeasuredWidth()
            int r3 = r25.getMeasuredHeight()
            float r4 = r25.getX()
            float r4 = -r4
            r1.setParentSize(r2, r3, r4)
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            r1.updateColors()
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            r1.updateGradient()
            r1 = r14
            float r2 = r0.crossfadeProgress
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x00eb
            float r2 = r0.crossfadeToColumnsCount
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x00eb
            int r2 = r0.currentParentColumnsCount
            if (r2 == r10) goto L_0x00eb
            float r1 = r1 + r13
            r16 = r1
            goto L_0x00ed
        L_0x00eb:
            r16 = r1
        L_0x00ed:
            float r4 = r16 + r7
            float r5 = r16 + r6
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            android.graphics.Paint r17 = r1.getPaint()
            r1 = r26
            r2 = r16
            r3 = r16
            r10 = r6
            r6 = r17
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x0105
        L_0x0104:
            r10 = r6
        L_0x0105:
            r25.invalidate()
        L_0x0108:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 != 0) goto L_0x010d
            return
        L_0x010d:
            float r1 = r0.imageAlpha
            r16 = 1132396544(0x437var_, float:255.0)
            int r2 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x012c
            r2 = 0
            r3 = 0
            float r4 = r14 * r15
            float r4 = r4 + r7
            float r5 = r14 * r15
            float r5 = r5 + r10
            float r1 = r1 * r16
            int r6 = (int) r1
            r17 = 31
            r1 = r26
            r19 = r7
            r7 = r17
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x0131
        L_0x012c:
            r19 = r7
            r26.save()
        L_0x0131:
            org.telegram.ui.Components.CheckBoxBase r1 = r0.checkBoxBase
            if (r1 == 0) goto L_0x013b
            boolean r1 = r1.isChecked()
            if (r1 != 0) goto L_0x0143
        L_0x013b:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.messenger.MessageObject) r1)
            if (r1 == 0) goto L_0x0153
        L_0x0143:
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r1 = r0.sharedResources
            android.graphics.Paint r6 = r1.backgroundPaint
            r1 = r26
            r2 = r14
            r3 = r14
            r4 = r19
            r5 = r10
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x0153:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x01cc
            float r1 = r0.checkBoxProgress
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 <= 0) goto L_0x017c
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r2 = r0.checkBoxProgress
            float r1 = r1 * r2
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            float r3 = r14 + r1
            float r4 = r14 + r1
            float r5 = r1 * r15
            r6 = r19
            float r7 = r6 - r5
            float r5 = r1 * r15
            float r5 = r10 - r5
            r2.setImageCoords(r3, r4, r7, r5)
            goto L_0x0197
        L_0x017c:
            r6 = r19
            r1 = r14
            float r2 = r0.crossfadeProgress
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0192
            float r2 = r0.crossfadeToColumnsCount
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0192
            int r2 = r0.currentParentColumnsCount
            r3 = 9
            if (r2 == r3) goto L_0x0192
            float r1 = r1 + r13
        L_0x0192:
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r2.setImageCoords(r1, r1, r6, r10)
        L_0x0197:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.messenger.MessageObject) r1)
            if (r1 != 0) goto L_0x01ce
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.draw(r8)
            float r1 = r0.highlightProgress
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 <= 0) goto L_0x01ce
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r1 = r0.sharedResources
            android.graphics.Paint r1 = r1.highlightPaint
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            float r3 = r0.highlightProgress
            float r3 = r3 * r11
            float r3 = r3 * r16
            int r3 = (int) r3
            int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r3)
            r1.setColor(r2)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            android.graphics.RectF r1 = r1.getDrawRegion()
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r2 = r0.sharedResources
            android.graphics.Paint r2 = r2.highlightPaint
            r8.drawRect(r1, r2)
            goto L_0x01ce
        L_0x01cc:
            r6 = r19
        L_0x01ce:
            boolean r1 = r0.showVideoLayout
            if (r1 == 0) goto L_0x02c8
            r26.save()
            float r7 = r14 + r6
            float r1 = r14 + r10
            r8.clipRect(r14, r14, r7, r1)
            int r1 = r0.currentParentColumnsCount
            r2 = 9
            if (r1 == r2) goto L_0x0217
            android.text.StaticLayout r1 = r0.videoInfoLayot
            if (r1 != 0) goto L_0x0217
            java.lang.String r1 = r0.videoText
            if (r1 == 0) goto L_0x0217
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r1 = r0.sharedResources
            android.text.TextPaint r1 = r1.textPaint
            java.lang.String r2 = r0.videoText
            float r1 = r1.measureText(r2)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            android.text.StaticLayout r2 = new android.text.StaticLayout
            java.lang.String r3 = r0.videoText
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r4 = r0.sharedResources
            android.text.TextPaint r4 = r4.textPaint
            android.text.Layout$Alignment r21 = android.text.Layout.Alignment.ALIGN_NORMAL
            r22 = 1065353216(0x3var_, float:1.0)
            r23 = 0
            r24 = 0
            r17 = r2
            r18 = r3
            r19 = r4
            r20 = r1
            r17.<init>(r18, r19, r20, r21, r22, r23, r24)
            r0.videoInfoLayot = r2
        L_0x0217:
            android.text.StaticLayout r1 = r0.videoInfoLayot
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1099431936(0x41880000, float:17.0)
            r4 = 1082130432(0x40800000, float:4.0)
            if (r1 != 0) goto L_0x0226
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0236
        L_0x0226:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.text.StaticLayout r5 = r0.videoInfoLayot
            int r5 = r5.getWidth()
            int r1 = r1 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r5
        L_0x0236:
            r5 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r9 = (float) r9
            float r9 = r9 + r10
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r11 = (float) r11
            float r9 = r9 - r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r11 = (float) r11
            float r9 = r9 - r11
            r8.translate(r7, r9)
            android.graphics.RectF r7 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r9 = (float) r1
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r11 = (float) r11
            r7.set(r12, r12, r9, r11)
            android.graphics.RectF r7 = org.telegram.messenger.AndroidUtilities.rectTmp
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r9 = (float) r9
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r11 = (float) r11
            android.graphics.Paint r13 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint
            r8.drawRoundRect(r7, r9, r11, r13)
            r26.save()
            android.text.StaticLayout r7 = r0.videoInfoLayot
            if (r7 != 0) goto L_0x027a
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x027e
        L_0x027a:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x027e:
            float r4 = (float) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r7 = r0.sharedResources
            android.graphics.drawable.Drawable r7 = r7.playDrawable
            int r7 = r7.getIntrinsicHeight()
            int r5 = r5 - r7
            float r5 = (float) r5
            float r5 = r5 / r15
            r8.translate(r4, r5)
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r4 = r0.sharedResources
            android.graphics.drawable.Drawable r4 = r4.playDrawable
            float r5 = r0.imageAlpha
            float r5 = r5 * r16
            int r5 = (int) r5
            r4.setAlpha(r5)
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r4 = r0.sharedResources
            android.graphics.drawable.Drawable r4 = r4.playDrawable
            r4.draw(r8)
            r26.restore()
            android.text.StaticLayout r4 = r0.videoInfoLayot
            if (r4 == 0) goto L_0x02c5
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.text.StaticLayout r4 = r0.videoInfoLayot
            int r4 = r4.getHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            float r3 = r3 / r15
            r8.translate(r2, r3)
            android.text.StaticLayout r2 = r0.videoInfoLayot
            r2.draw(r8)
        L_0x02c5:
            r26.restore()
        L_0x02c8:
            org.telegram.ui.Components.CheckBoxBase r1 = r0.checkBoxBase
            if (r1 == 0) goto L_0x02f1
            float r1 = r1.getProgress()
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x02f1
            r26.save()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r1 = (float) r1
            float r7 = r6 + r1
            r1 = 1103626240(0x41CLASSNAME, float:25.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r7 = r7 - r1
            r8.translate(r7, r12)
            org.telegram.ui.Components.CheckBoxBase r1 = r0.checkBoxBase
            r1.draw(r8)
            r26.restore()
        L_0x02f1:
            r26.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedPhotoVideoCell2.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        CheckBoxBase checkBoxBase2 = this.checkBoxBase;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onAttachedToWindow();
        }
        if (this.currentMessageObject != null) {
            this.imageReceiver.onAttachedToWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        CheckBoxBase checkBoxBase2 = this.checkBoxBase;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onDetachedFromWindow();
        }
        if (this.currentMessageObject != null) {
            this.imageReceiver.onDetachedFromWindow();
        }
    }

    public void setGradientView(FlickerLoadingView globalGradientView2) {
        this.globalGradientView = globalGradientView2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM));
    }

    public int getMessageId() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            return messageObject.getId();
        }
        return 0;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public void setImageAlpha(float alpha, boolean invalidate) {
        if (this.imageAlpha != alpha) {
            this.imageAlpha = alpha;
            if (invalidate) {
                invalidate();
            }
        }
    }

    public void setImageScale(float scale, boolean invalidate) {
        if (this.imageScale != scale) {
            this.imageScale = scale;
            if (invalidate) {
                invalidate();
            }
        }
    }

    public void setCrossfadeView(SharedPhotoVideoCell2 cell, float crossfadeProgress2, int crossfadeToColumnsCount2) {
        this.crossfadeView = cell;
        this.crossfadeProgress = crossfadeProgress2;
        this.crossfadeToColumnsCount = (float) crossfadeToColumnsCount2;
    }

    public void drawCrossafadeImage(Canvas canvas) {
        if (this.crossfadeView != null) {
            canvas.save();
            canvas.translate(getX(), getY());
            this.crossfadeView.setImageScale((((float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f))) * this.imageScale) / ((float) (this.crossfadeView.getMeasuredWidth() - AndroidUtilities.dp(2.0f))), false);
            this.crossfadeView.draw(canvas);
            canvas.restore();
        }
    }

    public View getCrossfadeView() {
        return this.crossfadeView;
    }

    public void setChecked(final boolean checked, boolean animated) {
        CheckBoxBase checkBoxBase2 = this.checkBoxBase;
        if ((checkBoxBase2 != null && checkBoxBase2.isChecked()) != checked) {
            if (this.checkBoxBase == null) {
                CheckBoxBase checkBoxBase3 = new CheckBoxBase(this, 21, (Theme.ResourcesProvider) null);
                this.checkBoxBase = checkBoxBase3;
                checkBoxBase3.setColor((String) null, "sharedMedia_photoPlaceholder", "checkboxCheck");
                this.checkBoxBase.setDrawUnchecked(false);
                this.checkBoxBase.setBackgroundType(1);
                this.checkBoxBase.setBounds(0, 0, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                if (this.attached) {
                    this.checkBoxBase.onAttachedToWindow();
                }
            }
            this.checkBoxBase.setChecked(checked, animated);
            if (this.animator != null) {
                ValueAnimator animatorFinal = this.animator;
                this.animator = null;
                animatorFinal.cancel();
            }
            float f = 1.0f;
            if (animated) {
                float[] fArr = new float[2];
                fArr[0] = this.checkBoxProgress;
                if (!checked) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        SharedPhotoVideoCell2.this.checkBoxProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        SharedPhotoVideoCell2.this.invalidate();
                    }
                });
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SharedPhotoVideoCell2.this.animator != null && SharedPhotoVideoCell2.this.animator.equals(animation)) {
                            SharedPhotoVideoCell2.this.checkBoxProgress = checked ? 1.0f : 0.0f;
                            SharedPhotoVideoCell2.this.animator = null;
                        }
                    }
                });
                this.animator.start();
            } else {
                if (!checked) {
                    f = 0.0f;
                }
                this.checkBoxProgress = f;
            }
            invalidate();
        }
    }

    public void startHighlight() {
    }

    public void setHighlightProgress(float p) {
        if (this.highlightProgress != p) {
            this.highlightProgress = p;
            invalidate();
        }
    }

    public void moveImageToFront() {
        this.imageReceiver.moveImageToFront();
    }

    public static class SharedResources {
        /* access modifiers changed from: private */
        public Paint backgroundPaint = new Paint();
        Paint highlightPaint = new Paint();
        SparseArray<String> imageFilters = new SparseArray<>();
        Drawable playDrawable;
        TextPaint textPaint = new TextPaint(1);

        public SharedResources(Context context) {
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setColor(-1);
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            Drawable drawable = ContextCompat.getDrawable(context, NUM);
            this.playDrawable = drawable;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), this.playDrawable.getIntrinsicHeight());
            this.backgroundPaint.setColor(Theme.getColor("sharedMedia_photoPlaceholder"));
        }

        public String getFilterString(int width) {
            String str = this.imageFilters.get(width);
            if (str != null) {
                return str;
            }
            String str2 = width + "_" + width + "_isc";
            this.imageFilters.put(width, str2);
            return str2;
        }
    }
}
