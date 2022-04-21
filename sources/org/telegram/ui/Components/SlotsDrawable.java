package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.text.TextUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatMessageCell;

public class SlotsDrawable extends RLottieDrawable {
    private ReelValue center;
    private int[] frameCounts = new int[5];
    private int[] frameNums = new int[5];
    private ReelValue left;
    private long[] nativePtrs = new long[5];
    private boolean playWinAnimation;
    private ReelValue right;
    private int[] secondFrameCounts = new int[3];
    private int[] secondFrameNums = new int[3];
    private long[] secondNativePtrs = new long[3];

    enum ReelValue {
        bar,
        berries,
        lemon,
        seven,
        sevenWin
    }

    public SlotsDrawable(String diceEmoji, int w, int h) {
        super(diceEmoji, w, h);
        this.loadFrameRunnable = new SlotsDrawable$$ExternalSyntheticLambda3(this);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4398lambda$new$0$orgtelegramuiComponentsSlotsDrawable() {
        int i;
        int result;
        if (!this.isRecycled) {
            if (this.nativePtr == 0 || (this.isDice == 2 && this.secondNativePtr == 0)) {
                if (this.frameWaitSync != null) {
                    this.frameWaitSync.countDown();
                }
                uiHandler.post(this.uiRunnableNoFrame);
                return;
            }
            if (this.backgroundBitmap == null) {
                try {
                    this.backgroundBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (this.backgroundBitmap != null) {
                try {
                    if (this.isDice == 1) {
                        result = -1;
                        int a = 0;
                        while (true) {
                            long[] jArr = this.nativePtrs;
                            if (a >= jArr.length) {
                                break;
                            }
                            result = getFrame(jArr[a], this.frameNums[a], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), a == 0);
                            if (a != 0) {
                                int[] iArr = this.frameNums;
                                if (iArr[a] + 1 < this.frameCounts[a]) {
                                    iArr[a] = iArr[a] + 1;
                                } else if (a != 4) {
                                    iArr[a] = 0;
                                    this.nextFrameIsLast = false;
                                    if (this.secondNativePtr != 0) {
                                        this.isDice = 2;
                                    }
                                }
                            }
                            a++;
                        }
                        i = -1;
                    } else {
                        if (this.setLastFrame != 0) {
                            int a2 = 0;
                            while (true) {
                                int[] iArr2 = this.secondFrameNums;
                                if (a2 >= iArr2.length) {
                                    break;
                                }
                                iArr2[a2] = this.secondFrameCounts[a2] - 1;
                                a2++;
                            }
                        }
                        if (this.playWinAnimation != 0) {
                            int[] iArr3 = this.frameNums;
                            if (iArr3[0] + 1 < this.frameCounts[0]) {
                                iArr3[0] = iArr3[0] + 1;
                            } else {
                                iArr3[0] = -1;
                            }
                        }
                        getFrame(this.nativePtrs[0], Math.max(this.frameNums[0], 0), this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), true);
                        int a3 = 0;
                        while (true) {
                            long[] jArr2 = this.secondNativePtrs;
                            if (a3 >= jArr2.length) {
                                break;
                            }
                            long j = jArr2[a3];
                            int[] iArr4 = this.secondFrameNums;
                            getFrame(j, iArr4[a3] >= 0 ? iArr4[a3] : this.secondFrameCounts[a3] - 1, this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                            if (!this.nextFrameIsLast) {
                                int[] iArr5 = this.secondFrameNums;
                                if (iArr5[a3] + 1 < this.secondFrameCounts[a3]) {
                                    iArr5[a3] = iArr5[a3] + 1;
                                } else {
                                    iArr5[a3] = -1;
                                }
                            }
                            a3++;
                        }
                        result = getFrame(this.nativePtrs[4], this.frameNums[4], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                        int[] iArr6 = this.frameNums;
                        if (iArr6[4] + 1 < this.frameCounts[4]) {
                            iArr6[4] = iArr6[4] + 1;
                        }
                        int[] iArr7 = this.secondFrameNums;
                        if (iArr7[0] == -1 && iArr7[1] == -1 && iArr7[2] == -1) {
                            this.nextFrameIsLast = true;
                            this.autoRepeatPlayCount++;
                        }
                        ReelValue reelValue = this.left;
                        ReelValue reelValue2 = this.right;
                        if (reelValue != reelValue2 || reelValue2 != this.center) {
                            i = -1;
                            this.frameNums[0] = -1;
                        } else if (this.secondFrameNums[0] == this.secondFrameCounts[0] - 100) {
                            this.playWinAnimation = true;
                            if (reelValue == ReelValue.sevenWin) {
                                Runnable runnable = (Runnable) this.onFinishCallback.get();
                                if (runnable != null) {
                                    AndroidUtilities.runOnUIThread(runnable);
                                }
                                i = -1;
                            } else {
                                i = -1;
                            }
                        } else {
                            i = -1;
                        }
                    }
                    if (result == i) {
                        uiHandler.post(this.uiRunnableNoFrame);
                        if (this.frameWaitSync != null) {
                            this.frameWaitSync.countDown();
                            return;
                        }
                        return;
                    }
                    this.nextRenderingBitmap = this.backgroundBitmap;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            uiHandler.post(this.uiRunnable);
            if (this.frameWaitSync != null) {
                this.frameWaitSync.countDown();
            }
        }
    }

    private ReelValue reelValue(int rawValue) {
        switch (rawValue) {
            case 0:
                return ReelValue.bar;
            case 1:
                return ReelValue.berries;
            case 2:
                return ReelValue.lemon;
            default:
                return ReelValue.seven;
        }
    }

    private void init(int rawValue) {
        int rawValue2 = rawValue - 1;
        ReelValue leftReelValue = reelValue(rawValue2 & 3);
        ReelValue centerReelValue = reelValue((rawValue2 >> 2) & 3);
        ReelValue rightReelValue = reelValue(rawValue2 >> 4);
        if (leftReelValue == ReelValue.seven && centerReelValue == ReelValue.seven && rightReelValue == ReelValue.seven) {
            leftReelValue = ReelValue.sevenWin;
            centerReelValue = ReelValue.sevenWin;
            rightReelValue = ReelValue.sevenWin;
        }
        this.left = leftReelValue;
        this.center = centerReelValue;
        this.right = rightReelValue;
    }

    private boolean is777() {
        return this.left == ReelValue.sevenWin && this.center == ReelValue.sevenWin && this.right == ReelValue.sevenWin;
    }

    public boolean setBaseDice(ChatMessageCell messageCell, TLRPC.TL_messages_stickerSet stickerSet) {
        if (this.nativePtr != 0 || this.loadingInBackground) {
            return true;
        }
        this.loadingInBackground = true;
        MessageObject currentMessageObject = messageCell.getMessageObject();
        Utilities.globalQueue.postRunnable(new SlotsDrawable$$ExternalSyntheticLambda9(this, stickerSet, messageCell.getMessageObject().currentAccount, currentMessageObject, messageCell));
        return true;
    }

    /* renamed from: lambda$setBaseDice$5$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4402lambda$setBaseDice$5$orgtelegramuiComponentsSlotsDrawable(TLRPC.TL_messages_stickerSet stickerSet, int account, MessageObject currentMessageObject, ChatMessageCell messageCell) {
        int num;
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda4(this));
            return;
        }
        boolean loading = false;
        int a = 0;
        while (true) {
            long[] jArr = this.nativePtrs;
            if (a >= jArr.length) {
                break;
            }
            if (jArr[a] != 0) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSet;
            } else {
                if (a == 0) {
                    num = 1;
                } else if (a == 1) {
                    num = 8;
                } else if (a == 2) {
                    num = 14;
                } else if (a == 3) {
                    num = 20;
                } else {
                    num = 2;
                }
                TLRPC.Document document = (TLRPC.Document) stickerSet.documents.get(num);
                String json = readRes(FileLoader.getPathToAttach(document, true), 0);
                if (TextUtils.isEmpty(json)) {
                    loading = true;
                    AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda0(document, account, currentMessageObject, messageCell, stickerSet));
                } else {
                    this.nativePtrs[a] = createWithJson(json, "dice", this.metaData, (int[]) null);
                    this.frameCounts[a] = this.metaData[0];
                }
            }
            a++;
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = stickerSet;
        if (loading) {
            AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda5(this));
        } else {
            AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda8(this, account, messageCell));
        }
    }

    /* renamed from: lambda$setBaseDice$1$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4399lambda$setBaseDice$1$orgtelegramuiComponentsSlotsDrawable() {
        this.loadingInBackground = false;
        if (!this.secondLoadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    static /* synthetic */ void lambda$setBaseDice$2(TLRPC.Document document, int account, MessageObject currentMessageObject, ChatMessageCell messageCell, TLRPC.TL_messages_stickerSet stickerSet) {
        DownloadController.getInstance(account).addLoadingFileObserver(FileLoader.getAttachFileName(document), currentMessageObject, messageCell);
        FileLoader.getInstance(account).loadFile(document, stickerSet, 1, 1);
    }

    /* renamed from: lambda$setBaseDice$3$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4400lambda$setBaseDice$3$orgtelegramuiComponentsSlotsDrawable() {
        this.loadingInBackground = false;
    }

    /* renamed from: lambda$setBaseDice$4$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4401lambda$setBaseDice$4$orgtelegramuiComponentsSlotsDrawable(int account, ChatMessageCell messageCell) {
        this.loadingInBackground = false;
        if (this.secondLoadingInBackground || !this.destroyAfterLoading) {
            this.nativePtr = this.nativePtrs[0];
            DownloadController.getInstance(account).removeLoadingFileObserver(messageCell);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            scheduleNextGetFrame();
            invalidateInternal();
            return;
        }
        recycle();
    }

    public boolean setDiceNumber(ChatMessageCell messageCell, int number, TLRPC.TL_messages_stickerSet stickerSet, boolean instant) {
        if (this.secondNativePtr != 0) {
            int i = number;
        } else if (this.secondLoadingInBackground) {
            int i2 = number;
        } else {
            init(number);
            MessageObject currentMessageObject = messageCell.getMessageObject();
            int account = messageCell.getMessageObject().currentAccount;
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new SlotsDrawable$$ExternalSyntheticLambda10(this, stickerSet, account, currentMessageObject, messageCell, instant));
            return true;
        }
        return true;
    }

    /* renamed from: lambda$setDiceNumber$10$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4403lambda$setDiceNumber$10$orgtelegramuiComponentsSlotsDrawable(TLRPC.TL_messages_stickerSet stickerSet, int account, MessageObject currentMessageObject, ChatMessageCell messageCell, boolean instant) {
        int num;
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda6(this));
            return;
        }
        boolean loading = false;
        int a = 0;
        while (true) {
            long[] jArr = this.secondNativePtrs;
            if (a >= jArr.length + 2) {
                break;
            }
            if (a <= 2) {
                if (jArr[a] != 0) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSet;
                    a++;
                } else if (a == 0) {
                    if (this.left == ReelValue.bar) {
                        num = 5;
                    } else if (this.left == ReelValue.berries) {
                        num = 6;
                    } else if (this.left == ReelValue.lemon) {
                        num = 7;
                    } else if (this.left == ReelValue.seven) {
                        num = 4;
                    } else {
                        num = 3;
                    }
                } else if (a == 1) {
                    if (this.center == ReelValue.bar) {
                        num = 11;
                    } else if (this.center == ReelValue.berries) {
                        num = 12;
                    } else if (this.center == ReelValue.lemon) {
                        num = 13;
                    } else if (this.center == ReelValue.seven) {
                        num = 10;
                    } else {
                        num = 9;
                    }
                } else if (this.right == ReelValue.bar) {
                    num = 17;
                } else if (this.right == ReelValue.berries) {
                    num = 18;
                } else if (this.right == ReelValue.lemon) {
                    num = 19;
                } else if (this.right == ReelValue.seven) {
                    num = 16;
                } else {
                    num = 15;
                }
            } else if (this.nativePtrs[a] != 0) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = stickerSet;
                a++;
            } else if (a == 3) {
                num = 1;
            } else {
                num = 2;
            }
            TLRPC.Document document = (TLRPC.Document) stickerSet.documents.get(num);
            String json = readRes(FileLoader.getPathToAttach(document, true), 0);
            if (TextUtils.isEmpty(json)) {
                loading = true;
                AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda2(document, account, currentMessageObject, messageCell, stickerSet));
            } else if (a <= 2) {
                this.secondNativePtrs[a] = createWithJson(json, "dice", this.metaData, (int[]) null);
                this.secondFrameCounts[a] = this.metaData[0];
            } else {
                char c = 4;
                this.nativePtrs[a == 3 ? (char) 0 : 4] = createWithJson(json, "dice", this.metaData, (int[]) null);
                int[] iArr = this.frameCounts;
                if (a == 3) {
                    c = 0;
                }
                iArr[c] = this.metaData[0];
            }
            a++;
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet3 = stickerSet;
        if (loading) {
            AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda7(this));
        } else {
            AndroidUtilities.runOnUIThread(new SlotsDrawable$$ExternalSyntheticLambda1(this, instant, account, messageCell));
        }
    }

    /* renamed from: lambda$setDiceNumber$6$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4404lambda$setDiceNumber$6$orgtelegramuiComponentsSlotsDrawable() {
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    static /* synthetic */ void lambda$setDiceNumber$7(TLRPC.Document document, int account, MessageObject currentMessageObject, ChatMessageCell messageCell, TLRPC.TL_messages_stickerSet stickerSet) {
        DownloadController.getInstance(account).addLoadingFileObserver(FileLoader.getAttachFileName(document), currentMessageObject, messageCell);
        FileLoader.getInstance(account).loadFile(document, stickerSet, 1, 1);
    }

    /* renamed from: lambda$setDiceNumber$8$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4405lambda$setDiceNumber$8$orgtelegramuiComponentsSlotsDrawable() {
        this.secondLoadingInBackground = false;
    }

    /* renamed from: lambda$setDiceNumber$9$org-telegram-ui-Components-SlotsDrawable  reason: not valid java name */
    public /* synthetic */ void m4406lambda$setDiceNumber$9$orgtelegramuiComponentsSlotsDrawable(boolean instant, int account, ChatMessageCell messageCell) {
        if (instant && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
            this.isDice = 2;
            this.setLastFrame = true;
        }
        this.secondLoadingInBackground = false;
        if (this.loadingInBackground || !this.destroyAfterLoading) {
            this.secondNativePtr = this.secondNativePtrs[0];
            DownloadController.getInstance(account).removeLoadingFileObserver(messageCell);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            scheduleNextGetFrame();
            invalidateInternal();
            return;
        }
        recycle();
    }

    public void recycle() {
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadingInBackground || this.secondLoadingInBackground) {
            this.destroyAfterLoading = true;
        } else if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            int a = 0;
            while (true) {
                long[] jArr = this.nativePtrs;
                if (a >= jArr.length) {
                    break;
                }
                if (jArr[a] != 0) {
                    if (jArr[a] == this.nativePtr) {
                        this.nativePtr = 0;
                    }
                    destroy(this.nativePtrs[a]);
                    this.nativePtrs[a] = 0;
                }
                a++;
            }
            int a2 = 0;
            while (true) {
                long[] jArr2 = this.secondNativePtrs;
                if (a2 < jArr2.length) {
                    if (jArr2[a2] != 0) {
                        if (jArr2[a2] == this.secondNativePtr) {
                            this.secondNativePtr = 0;
                        }
                        destroy(this.secondNativePtrs[a2]);
                        this.secondNativePtrs[a2] = 0;
                    }
                    a2++;
                } else {
                    recycleResources();
                    return;
                }
            }
        } else {
            this.destroyWhenDone = true;
        }
    }

    /* access modifiers changed from: protected */
    public void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
                int a = 0;
                while (true) {
                    long[] jArr = this.nativePtrs;
                    if (a >= jArr.length) {
                        break;
                    }
                    if (jArr[a] != 0) {
                        destroy(jArr[a]);
                        this.nativePtrs[a] = 0;
                    }
                    a++;
                }
                int a2 = 0;
                while (true) {
                    long[] jArr2 = this.secondNativePtrs;
                    if (a2 >= jArr2.length) {
                        break;
                    }
                    if (jArr2[a2] != 0) {
                        destroy(jArr2[a2]);
                        this.secondNativePtrs[a2] = 0;
                    }
                    a2++;
                }
            }
        }
        if (this.nativePtr == 0 && this.secondNativePtr == 0) {
            recycleResources();
            return;
        }
        this.waitingForNextTask = true;
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }
}
