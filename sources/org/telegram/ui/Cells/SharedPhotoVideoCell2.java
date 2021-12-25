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
import android.util.SparseArray;
import android.view.View;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
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

    public SharedPhotoVideoCell2(Context context, SharedResources sharedResources2, int i) {
        super(context);
        this.sharedResources = sharedResources2;
        this.currentAccount = i;
        setChecked(false, false);
        this.imageReceiver.setParentView(this);
    }

    /* JADX WARNING: Removed duplicated region for block: B:87:0x022d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMessageObject(org.telegram.messenger.MessageObject r16, int r17) {
        /*
            r15 = this;
            r0 = r15
            r9 = r16
            r1 = r17
            int r2 = r0.currentParentColumnsCount
            r0.currentParentColumnsCount = r1
            org.telegram.messenger.MessageObject r3 = r0.currentMessageObject
            if (r3 != 0) goto L_0x0010
            if (r9 != 0) goto L_0x0010
            return
        L_0x0010:
            if (r3 == 0) goto L_0x0021
            if (r9 == 0) goto L_0x0021
            int r3 = r3.getId()
            int r4 = r16.getId()
            if (r3 != r4) goto L_0x0021
            if (r2 != r1) goto L_0x0021
            return
        L_0x0021:
            r0.currentMessageObject = r9
            r2 = 0
            r11 = 0
            if (r9 != 0) goto L_0x0033
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.onDetachedFromWindow()
            r0.videoText = r2
            r0.videoInfoLayot = r2
            r0.showVideoLayout = r11
            return
        L_0x0033:
            boolean r3 = r0.attached
            if (r3 == 0) goto L_0x003c
            org.telegram.messenger.ImageReceiver r3 = r0.imageReceiver
            r3.onAttachedToWindow()
        L_0x003c:
            org.telegram.tgnet.TLRPC$Message r3 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r3.restriction_reason
            java.lang.String r3 = org.telegram.messenger.MessagesController.getRestrictionReason(r3)
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            int r4 = r4 / r1
            float r4 = (float) r4
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r5
            int r4 = (int) r4
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r5 = r0.sharedResources
            java.lang.String r4 = r5.getFilterString(r4)
            r5 = 320(0x140, float:4.48E-43)
            r6 = 2
            if (r1 > r6) goto L_0x005e
            int r5 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            goto L_0x005f
        L_0x005e:
            r7 = 3
        L_0x005f:
            r0.videoText = r2
            r0.videoInfoLayot = r2
            r0.showVideoLayout = r11
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            r7 = 1
            if (r3 != 0) goto L_0x006f
        L_0x006c:
            r11 = 1
            goto L_0x022b
        L_0x006f:
            boolean r3 = r16.isVideo()
            r8 = 50
            java.lang.String r10 = "_b"
            if (r3 == 0) goto L_0x0121
            r0.showVideoLayout = r7
            r3 = 9
            if (r1 == r3) goto L_0x0089
            int r1 = r16.getDuration()
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatShortDuration(r1)
            r0.videoText = r1
        L_0x0089:
            org.telegram.messenger.ImageLocation r3 = r9.mediaThumb
            if (r3 == 0) goto L_0x00c6
            android.graphics.drawable.BitmapDrawable r5 = r9.strippedThumb
            if (r5 == 0) goto L_0x00a0
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r6 = 0
            r7 = 0
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r16
            r1.setImage(r2, r3, r4, r5, r6, r7)
            goto L_0x022b
        L_0x00a0:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            org.telegram.messenger.ImageLocation r5 = r9.mediaSmallThumb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            r2.append(r10)
            java.lang.String r6 = r2.toString()
            r7 = 0
            r8 = 0
            r10 = 0
            r12 = 0
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r8
            r8 = r10
            r9 = r16
            r10 = r12
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x022b
        L_0x00c6:
            org.telegram.tgnet.TLRPC$Document r1 = r16.getDocument()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r1.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r1.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r5)
            if (r3 != r5) goto L_0x00d9
            goto L_0x00da
        L_0x00d9:
            r2 = r5
        L_0x00da:
            if (r3 == 0) goto L_0x006c
            android.graphics.drawable.BitmapDrawable r5 = r9.strippedThumb
            if (r5 == 0) goto L_0x00f5
            org.telegram.messenger.ImageReceiver r3 = r0.imageReceiver
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            android.graphics.drawable.BitmapDrawable r5 = r9.strippedThumb
            r6 = 0
            r7 = 0
            r1 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r16
            r1.setImage(r2, r3, r4, r5, r6, r7)
            goto L_0x022b
        L_0x00f5:
            org.telegram.messenger.ImageReceiver r5 = r0.imageReceiver
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r1)
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Document) r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            r1.append(r10)
            java.lang.String r7 = r1.toString()
            r8 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r1 = r5
            r3 = r4
            r4 = r6
            r5 = r7
            r6 = r8
            r7 = r10
            r8 = r12
            r9 = r16
            r10 = r13
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x022b
        L_0x0121:
            org.telegram.tgnet.TLRPC$Message r1 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x006c
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            if (r1 == 0) goto L_0x006c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r9.photoThumbs
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x006c
            boolean r1 = r9.mediaExists
            if (r1 != 0) goto L_0x0176
            boolean r1 = r15.canAutoDownload(r16)
            if (r1 == 0) goto L_0x0140
            goto L_0x0176
        L_0x0140:
            android.graphics.drawable.BitmapDrawable r6 = r9.strippedThumb
            if (r6 == 0) goto L_0x0154
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
            r8 = 0
            r10 = 0
            r9 = r16
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x022b
        L_0x0154:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r8)
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r3 = 0
            r4 = 0
            org.telegram.tgnet.TLObject r5 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r1, r5)
            r6 = 0
            r7 = 0
            r8 = 0
            r10 = 0
            java.lang.String r12 = "b"
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r12
            r9 = r16
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x022b
        L_0x0176:
            org.telegram.messenger.ImageLocation r3 = r9.mediaThumb
            if (r3 == 0) goto L_0x01b3
            android.graphics.drawable.BitmapDrawable r5 = r9.strippedThumb
            if (r5 == 0) goto L_0x018d
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r6 = 0
            r7 = 0
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r16
            r1.setImage(r2, r3, r4, r5, r6, r7)
            goto L_0x022b
        L_0x018d:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            org.telegram.messenger.ImageLocation r5 = r9.mediaSmallThumb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            r2.append(r10)
            java.lang.String r6 = r2.toString()
            r7 = 0
            r8 = 0
            r10 = 0
            r12 = 0
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r8
            r8 = r10
            r9 = r16
            r10 = r12
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x022b
        L_0x01b3:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r5, r11, r1, r11)
            if (r3 != r1) goto L_0x01c2
            goto L_0x01c3
        L_0x01c2:
            r2 = r1
        L_0x01c3:
            android.graphics.drawable.BitmapDrawable r1 = r9.strippedThumb
            if (r1 == 0) goto L_0x01f1
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            org.telegram.tgnet.TLObject r2 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r3, r2)
            r5 = 0
            r8 = 0
            android.graphics.drawable.BitmapDrawable r10 = r9.strippedThumb
            if (r3 == 0) goto L_0x01d9
            int r3 = r3.size
            r12 = r3
            goto L_0x01da
        L_0x01d9:
            r12 = 0
        L_0x01da:
            r13 = 0
            boolean r3 = r16.shouldEncryptPhotoOrVideo()
            if (r3 == 0) goto L_0x01e3
            r14 = 2
            goto L_0x01e4
        L_0x01e3:
            r14 = 1
        L_0x01e4:
            r3 = r4
            r4 = r5
            r5 = r8
            r6 = r10
            r7 = r12
            r8 = r13
            r9 = r16
            r10 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x022b
        L_0x01f1:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            org.telegram.tgnet.TLObject r5 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r3, r5)
            org.telegram.tgnet.TLObject r8 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForObject(r2, r8)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            r2.append(r10)
            java.lang.String r10 = r2.toString()
            if (r3 == 0) goto L_0x0214
            int r2 = r3.size
            r12 = r2
            goto L_0x0215
        L_0x0214:
            r12 = 0
        L_0x0215:
            r13 = 0
            boolean r2 = r16.shouldEncryptPhotoOrVideo()
            if (r2 == 0) goto L_0x021e
            r14 = 2
            goto L_0x021f
        L_0x021e:
            r14 = 1
        L_0x021f:
            r2 = r5
            r3 = r4
            r4 = r8
            r5 = r10
            r6 = r12
            r7 = r13
            r8 = r16
            r9 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x022b:
            if (r11 == 0) goto L_0x023d
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            android.content.Context r2 = r15.getContext()
            r3 = 2131165971(0x7var_, float:1.7946174E38)
            android.graphics.drawable.Drawable r2 = androidx.core.content.ContextCompat.getDrawable(r2, r3)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x023d:
            r15.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedPhotoVideoCell2.setMessageObject(org.telegram.messenger.MessageObject, int):void");
    }

    private boolean canAutoDownload(MessageObject messageObject) {
        if (System.currentTimeMillis() - lastUpdateDownloadSettingsTime > 5000) {
            lastUpdateDownloadSettingsTime = System.currentTimeMillis();
            lastAutoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(messageObject);
        }
        return lastAutoDownload;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0100 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0101  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r27) {
        /*
            r26 = this;
            r0 = r26
            r8 = r27
            super.onDraw(r27)
            float r1 = r0.crossfadeProgress
            r9 = 1091567616(0x41100000, float:9.0)
            r10 = 9
            r11 = 1056964608(0x3var_, float:0.5)
            r12 = 0
            r13 = 1065353216(0x3var_, float:1.0)
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x0049
            float r1 = r0.crossfadeToColumnsCount
            int r2 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0020
            int r2 = r0.currentParentColumnsCount
            if (r2 != r10) goto L_0x0049
        L_0x0020:
            int r1 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r1 != 0) goto L_0x0034
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r1 = (float) r1
            float r2 = r0.crossfadeProgress
            float r1 = r1 * r2
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r13)
            float r3 = r0.crossfadeProgress
            goto L_0x0043
        L_0x0034:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r1 = (float) r1
            float r2 = r0.crossfadeProgress
            float r1 = r1 * r2
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r11)
            float r3 = r0.crossfadeProgress
        L_0x0043:
            float r3 = r13 - r3
            float r2 = r2 * r3
            float r1 = r1 + r2
            goto L_0x0056
        L_0x0049:
            int r1 = r0.currentParentColumnsCount
            if (r1 != r10) goto L_0x0052
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r11)
            goto L_0x0056
        L_0x0052:
            float r1 = org.telegram.messenger.AndroidUtilities.dpf2(r13)
        L_0x0056:
            r14 = r1
            int r1 = r26.getMeasuredWidth()
            float r1 = (float) r1
            r15 = 1073741824(0x40000000, float:2.0)
            float r7 = r14 * r15
            float r1 = r1 - r7
            float r2 = r0.imageScale
            float r1 = r1 * r2
            int r2 = r26.getMeasuredHeight()
            float r2 = (float) r2
            float r2 = r2 - r7
            float r3 = r0.imageScale
            float r2 = r2 * r3
            float r3 = r0.crossfadeProgress
            int r3 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r3 <= 0) goto L_0x0081
            float r3 = r0.crossfadeToColumnsCount
            int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x0081
            int r3 = r0.currentParentColumnsCount
            if (r3 == r10) goto L_0x0081
            float r1 = r1 - r15
            float r2 = r2 - r15
        L_0x0081:
            r6 = r1
            r5 = r2
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x00a3
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            boolean r1 = r1.hasBitmapImage()
            if (r1 == 0) goto L_0x00a3
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            float r1 = r1.getCurrentAlpha()
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x00a3
            float r1 = r0.imageAlpha
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x00a0
            goto L_0x00a3
        L_0x00a0:
            r10 = r5
            r9 = r6
            goto L_0x00fc
        L_0x00a3:
            android.view.ViewParent r1 = r26.getParent()
            if (r1 == 0) goto L_0x00f7
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            android.view.ViewParent r2 = r26.getParent()
            android.view.View r2 = (android.view.View) r2
            int r2 = r2.getMeasuredWidth()
            int r3 = r26.getMeasuredHeight()
            float r4 = r26.getX()
            float r4 = -r4
            r1.setParentSize(r2, r3, r4)
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            r1.updateColors()
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            r1.updateGradient()
            float r1 = r0.crossfadeProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x00df
            float r1 = r0.crossfadeToColumnsCount
            int r1 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r1 == 0) goto L_0x00df
            int r1 = r0.currentParentColumnsCount
            if (r1 == r10) goto L_0x00df
            float r1 = r14 + r13
            r3 = r1
            goto L_0x00e0
        L_0x00df:
            r3 = r14
        L_0x00e0:
            float r4 = r3 + r6
            float r16 = r3 + r5
            org.telegram.ui.Components.FlickerLoadingView r1 = r0.globalGradientView
            android.graphics.Paint r17 = r1.getPaint()
            r1 = r27
            r2 = r3
            r10 = r5
            r5 = r16
            r9 = r6
            r6 = r17
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x00f9
        L_0x00f7:
            r10 = r5
            r9 = r6
        L_0x00f9:
            r26.invalidate()
        L_0x00fc:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 != 0) goto L_0x0101
            return
        L_0x0101:
            float r1 = r0.imageAlpha
            r17 = 1132396544(0x437var_, float:255.0)
            int r2 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x011a
            r2 = 0
            r3 = 0
            float r4 = r7 + r9
            float r5 = r7 + r10
            float r1 = r1 * r17
            int r6 = (int) r1
            r7 = 31
            r1 = r27
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x011d
        L_0x011a:
            r27.save()
        L_0x011d:
            org.telegram.ui.Components.CheckBoxBase r1 = r0.checkBoxBase
            if (r1 == 0) goto L_0x0127
            boolean r1 = r1.isChecked()
            if (r1 != 0) goto L_0x012f
        L_0x0127:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.messenger.MessageObject) r1)
            if (r1 == 0) goto L_0x013e
        L_0x012f:
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r1 = r0.sharedResources
            android.graphics.Paint r6 = r1.backgroundPaint
            r1 = r27
            r2 = r14
            r3 = r14
            r4 = r9
            r5 = r10
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x013e:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x01b0
            float r1 = r0.checkBoxProgress
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 <= 0) goto L_0x0161
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r2 = r0.checkBoxProgress
            float r1 = r1 * r2
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            float r3 = r14 + r1
            float r1 = r1 * r15
            float r6 = r9 - r1
            float r5 = r10 - r1
            r2.setImageCoords(r3, r3, r6, r5)
            goto L_0x017e
        L_0x0161:
            float r1 = r0.crossfadeProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x0178
            float r1 = r0.crossfadeToColumnsCount
            r2 = 1091567616(0x41100000, float:9.0)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0178
            int r1 = r0.currentParentColumnsCount
            r2 = 9
            if (r1 == r2) goto L_0x0178
            float r1 = r14 + r13
            goto L_0x0179
        L_0x0178:
            r1 = r14
        L_0x0179:
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r2.setImageCoords(r1, r1, r9, r10)
        L_0x017e:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            boolean r1 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.messenger.MessageObject) r1)
            if (r1 != 0) goto L_0x01b0
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.draw(r8)
            float r1 = r0.highlightProgress
            int r2 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r2 <= 0) goto L_0x01b0
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r2 = r0.sharedResources
            android.graphics.Paint r2 = r2.highlightPaint
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            float r1 = r1 * r11
            float r1 = r1 * r17
            int r1 = (int) r1
            int r1 = androidx.core.graphics.ColorUtils.setAlphaComponent(r3, r1)
            r2.setColor(r1)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            android.graphics.RectF r1 = r1.getDrawRegion()
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r2 = r0.sharedResources
            android.graphics.Paint r2 = r2.highlightPaint
            r8.drawRect(r1, r2)
        L_0x01b0:
            boolean r1 = r0.showVideoLayout
            if (r1 == 0) goto L_0x02a6
            r27.save()
            float r6 = r14 + r9
            float r5 = r14 + r10
            r8.clipRect(r14, r14, r6, r5)
            int r1 = r0.currentParentColumnsCount
            r2 = 9
            if (r1 == r2) goto L_0x01f7
            android.text.StaticLayout r1 = r0.videoInfoLayot
            if (r1 != 0) goto L_0x01f7
            java.lang.String r1 = r0.videoText
            if (r1 == 0) goto L_0x01f7
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r2 = r0.sharedResources
            android.text.TextPaint r2 = r2.textPaint
            float r1 = r2.measureText(r1)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            android.text.StaticLayout r2 = new android.text.StaticLayout
            java.lang.String r3 = r0.videoText
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r4 = r0.sharedResources
            android.text.TextPaint r4 = r4.textPaint
            android.text.Layout$Alignment r22 = android.text.Layout.Alignment.ALIGN_NORMAL
            r23 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            r25 = 0
            r18 = r2
            r19 = r3
            r20 = r4
            r21 = r1
            r18.<init>(r19, r20, r21, r22, r23, r24, r25)
            r0.videoInfoLayot = r2
        L_0x01f7:
            android.text.StaticLayout r1 = r0.videoInfoLayot
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1099431936(0x41880000, float:17.0)
            r4 = 1082130432(0x40800000, float:4.0)
            if (r1 != 0) goto L_0x0206
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0216
        L_0x0206:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            android.text.StaticLayout r5 = r0.videoInfoLayot
            int r5 = r5.getWidth()
            int r1 = r1 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 + r5
        L_0x0216:
            r5 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r7 = (float) r7
            float r7 = r7 + r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r10 = (float) r10
            float r7 = r7 - r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r10 = (float) r10
            float r7 = r7 - r10
            r8.translate(r6, r7)
            android.graphics.RectF r6 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r1 = (float) r1
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            r6.set(r12, r12, r1, r7)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r1 = (float) r1
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint
            r8.drawRoundRect(r6, r1, r7, r10)
            r27.save()
            android.text.StaticLayout r1 = r0.videoInfoLayot
            if (r1 != 0) goto L_0x0258
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x025c
        L_0x0258:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x025c:
            float r1 = (float) r1
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r5 = r0.sharedResources
            android.graphics.drawable.Drawable r5 = r5.playDrawable
            int r5 = r5.getIntrinsicHeight()
            int r4 = r4 - r5
            float r4 = (float) r4
            float r4 = r4 / r15
            r8.translate(r1, r4)
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r1 = r0.sharedResources
            android.graphics.drawable.Drawable r1 = r1.playDrawable
            float r4 = r0.imageAlpha
            float r4 = r4 * r17
            int r4 = (int) r4
            r1.setAlpha(r4)
            org.telegram.ui.Cells.SharedPhotoVideoCell2$SharedResources r1 = r0.sharedResources
            android.graphics.drawable.Drawable r1 = r1.playDrawable
            r1.draw(r8)
            r27.restore()
            android.text.StaticLayout r1 = r0.videoInfoLayot
            if (r1 == 0) goto L_0x02a3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.text.StaticLayout r3 = r0.videoInfoLayot
            int r3 = r3.getHeight()
            int r2 = r2 - r3
            float r2 = (float) r2
            float r2 = r2 / r15
            r8.translate(r1, r2)
            android.text.StaticLayout r1 = r0.videoInfoLayot
            r1.draw(r8)
        L_0x02a3:
            r27.restore()
        L_0x02a6:
            org.telegram.ui.Components.CheckBoxBase r1 = r0.checkBoxBase
            if (r1 == 0) goto L_0x02cf
            float r1 = r1.getProgress()
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x02cf
            r27.save()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r1 = (float) r1
            float r6 = r9 + r1
            r1 = 1103626240(0x41CLASSNAME, float:25.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r6 = r6 - r1
            r8.translate(r6, r12)
            org.telegram.ui.Components.CheckBoxBase r1 = r0.checkBoxBase
            r1.draw(r8)
            r27.restore()
        L_0x02cf:
            r27.restore()
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

    public void setGradientView(FlickerLoadingView flickerLoadingView) {
        this.globalGradientView = flickerLoadingView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM));
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

    public void setImageAlpha(float f, boolean z) {
        if (this.imageAlpha != f) {
            this.imageAlpha = f;
            if (z) {
                invalidate();
            }
        }
    }

    public void setImageScale(float f, boolean z) {
        if (this.imageScale != f) {
            this.imageScale = f;
            if (z) {
                invalidate();
            }
        }
    }

    public void setCrossfadeView(SharedPhotoVideoCell2 sharedPhotoVideoCell2, float f, int i) {
        this.crossfadeView = sharedPhotoVideoCell2;
        this.crossfadeProgress = f;
        this.crossfadeToColumnsCount = (float) i;
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

    public void setChecked(final boolean z, boolean z2) {
        CheckBoxBase checkBoxBase2 = this.checkBoxBase;
        if ((checkBoxBase2 != null && checkBoxBase2.isChecked()) != z) {
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
            this.checkBoxBase.setChecked(z, z2);
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                this.animator = null;
                valueAnimator.cancel();
            }
            float f = 1.0f;
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = this.checkBoxProgress;
                if (!z) {
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
                    public void onAnimationEnd(Animator animator) {
                        ValueAnimator valueAnimator = SharedPhotoVideoCell2.this.animator;
                        if (valueAnimator != null && valueAnimator.equals(animator)) {
                            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = SharedPhotoVideoCell2.this;
                            sharedPhotoVideoCell2.checkBoxProgress = z ? 1.0f : 0.0f;
                            sharedPhotoVideoCell2.animator = null;
                        }
                    }
                });
                this.animator.start();
            } else {
                if (!z) {
                    f = 0.0f;
                }
                this.checkBoxProgress = f;
            }
            invalidate();
        }
    }

    public void setHighlightProgress(float f) {
        if (this.highlightProgress != f) {
            this.highlightProgress = f;
            invalidate();
        }
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

        public String getFilterString(int i) {
            String str = this.imageFilters.get(i);
            if (str != null) {
                return str;
            }
            String str2 = i + "_" + i + "_isc";
            this.imageFilters.put(i, str2);
            return str2;
        }
    }
}
