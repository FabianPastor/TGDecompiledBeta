package org.telegram.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.BrightnessControlCell;
import org.telegram.ui.Cells.ChatListCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemeCell;
import org.telegram.ui.Cells.ThemeTypeCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    private static final int create_theme = 1;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int backgroundRow;
    private int chatListHeaderRow;
    private int chatListInfoRow;
    private int chatListRow;
    private int contactsReimportRow;
    private int contactsSortRow;
    private int currentType;
    private int customTabsRow;
    private int directShareRow;
    private int emojiRow;
    private int enableAnimationsRow;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(this, null);
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);
    private int newThemeInfoRow;
    private int nightAutomaticRow;
    private int nightDisabledRow;
    private int nightScheduledRow;
    private int nightThemeRow;
    private int nightTypeInfoRow;
    private int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryRow;
    private int scheduleFromRow;
    private int scheduleFromToInfoRow;
    private int scheduleHeaderRow;
    private int scheduleLocationInfoRow;
    private int scheduleLocationRow;
    private int scheduleToRow;
    private int scheduleUpdateLocationRow;
    private int sendByEnterRow;
    private int settings2Row;
    private int settingsRow;
    private int stickersRow;
    private int stickersSection2Row;
    private int textSizeHeaderRow;
    private int textSizeRow;
    private int themeEndRow;
    private int themeHeaderRow;
    private int themeInfoRow;
    private int themeStartRow;
    private boolean updatingLocation;

    private class GpsLocationListener implements LocationListener {
        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }

        private GpsLocationListener() {
        }

        /* synthetic */ GpsLocationListener(ThemeActivity themeActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }
    }

    public interface SizeChooseViewDelegate {
        void onSizeChanged();
    }

    private class TextSizeCell extends FrameLayout {
        private ChatMessageCell[] cells = new ChatMessageCell[2];
        private int endFontSize = 30;
        private int lastWidth;
        private LinearLayout messagesContainer;
        private Drawable shadowDrawable;
        private SeekBarView sizeBar;
        private int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textPaint = new TextPaint(1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            this.shadowDrawable = Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow");
            this.sizeBar = new SeekBarView(context);
            this.sizeBar.setReportChanges(true);
            this.sizeBar.setDelegate(new -$$Lambda$ThemeActivity$TextSizeCell$Ci0_0LdqTC4U6xi9evAA0pUhylM(this));
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 9.0f, 5.0f, 43.0f, 0.0f));
            this.messagesContainer = new LinearLayout(context, ThemeActivity.this) {
                private Drawable backgroundDrawable;

                /* Access modifiers changed, original: protected */
                public void dispatchSetPressed(boolean z) {
                }

                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    return false;
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return false;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return false;
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
                    if (cachedWallpaperNonBlocking != null) {
                        this.backgroundDrawable = cachedWallpaperNonBlocking;
                    }
                    cachedWallpaperNonBlocking = this.backgroundDrawable;
                    if (cachedWallpaperNonBlocking instanceof ColorDrawable) {
                        cachedWallpaperNonBlocking.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        this.backgroundDrawable.draw(canvas);
                    } else if (!(cachedWallpaperNonBlocking instanceof BitmapDrawable)) {
                        super.onDraw(canvas);
                    } else if (((BitmapDrawable) cachedWallpaperNonBlocking).getTileModeX() == TileMode.REPEAT) {
                        canvas.save();
                        float f = 2.0f / AndroidUtilities.density;
                        canvas.scale(f, f);
                        this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                        this.backgroundDrawable.draw(canvas);
                        canvas.restore();
                    } else {
                        int measuredHeight = getMeasuredHeight();
                        float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                        float intrinsicHeight = ((float) measuredHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                        if (measuredWidth < intrinsicHeight) {
                            measuredWidth = intrinsicHeight;
                        }
                        int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * measuredWidth));
                        int ceil2 = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * measuredWidth));
                        int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                        measuredHeight = (measuredHeight - ceil2) / 2;
                        canvas.save();
                        canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                        this.backgroundDrawable.setBounds(measuredWidth2, measuredHeight, ceil + measuredWidth2, ceil2 + measuredHeight);
                        this.backgroundDrawable.draw(canvas);
                        canvas.restore();
                    }
                    TextSizeCell.this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    TextSizeCell.this.shadowDrawable.draw(canvas);
                }
            };
            this.messagesContainer.setOrientation(1);
            this.messagesContainer.setWillNotDraw(false);
            this.messagesContainer.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            addView(this.messagesContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            TL_message tL_message = new TL_message();
            tL_message.message = LocaleController.getString("FontSizePreviewReply", NUM);
            int i = currentTimeMillis + 60;
            tL_message.date = i;
            tL_message.dialog_id = 1;
            tL_message.flags = 259;
            tL_message.from_id = UserConfig.getInstance(ThemeActivity.this.currentAccount).getClientUserId();
            tL_message.id = 1;
            tL_message.media = new TL_messageMediaEmpty();
            tL_message.out = true;
            tL_message.to_id = new TL_peerUser();
            tL_message.to_id.user_id = 0;
            MessageObject messageObject = new MessageObject(ThemeActivity.this.currentAccount, tL_message, true);
            tL_message = new TL_message();
            tL_message.message = LocaleController.getString("FontSizePreviewLine2", NUM);
            tL_message.date = currentTimeMillis + 960;
            tL_message.dialog_id = 1;
            tL_message.flags = 259;
            tL_message.from_id = UserConfig.getInstance(ThemeActivity.this.currentAccount).getClientUserId();
            tL_message.id = 1;
            tL_message.media = new TL_messageMediaEmpty();
            tL_message.out = true;
            tL_message.to_id = new TL_peerUser();
            tL_message.to_id.user_id = 0;
            MessageObject messageObject2 = new MessageObject(ThemeActivity.this.currentAccount, tL_message, true);
            messageObject2.resetLayout();
            messageObject2.eventId = 1;
            tL_message = new TL_message();
            tL_message.message = LocaleController.getString("FontSizePreviewLine1", NUM);
            tL_message.date = i;
            tL_message.dialog_id = 1;
            tL_message.flags = 265;
            tL_message.from_id = 0;
            tL_message.id = 1;
            tL_message.reply_to_msg_id = 5;
            tL_message.media = new TL_messageMediaEmpty();
            tL_message.out = false;
            tL_message.to_id = new TL_peerUser();
            tL_message.to_id.user_id = UserConfig.getInstance(ThemeActivity.this.currentAccount).getClientUserId();
            MessageObject messageObject3 = new MessageObject(ThemeActivity.this.currentAccount, tL_message, true);
            messageObject3.customReplyName = LocaleController.getString("FontSizePreviewName", NUM);
            messageObject3.eventId = 1;
            messageObject3.resetLayout();
            messageObject3.replyMessageObject = messageObject;
            int i2 = 0;
            while (true) {
                ChatMessageCell[] chatMessageCellArr = this.cells;
                if (i2 < chatMessageCellArr.length) {
                    chatMessageCellArr[i2] = new ChatMessageCell(context);
                    this.cells[i2].setDelegate(new ChatMessageCellDelegate(ThemeActivity.this) {
                        public /* synthetic */ boolean canPerformActions() {
                            return -CC.$default$canPerformActions(this);
                        }

                        public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                            -CC.$default$didLongPress(this, chatMessageCell, f, f2);
                        }

                        public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                            -CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                        }

                        public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                            -CC.$default$didPressCancelSendButton(this, chatMessageCell);
                        }

                        public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2) {
                            -CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                        }

                        public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                            -CC.$default$didPressHiddenForward(this, chatMessageCell);
                        }

                        public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                            -CC.$default$didPressImage(this, chatMessageCell, f, f2);
                        }

                        public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                            -CC.$default$didPressInstantButton(this, chatMessageCell, i);
                        }

                        public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                            -CC.$default$didPressOther(this, chatMessageCell, f, f2);
                        }

                        public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                            -CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                        }

                        public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                            -CC.$default$didPressShare(this, chatMessageCell);
                        }

                        public /* synthetic */ void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                            -CC.$default$didPressUrl(this, messageObject, characterStyle, z);
                        }

                        public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, User user, float f, float f2) {
                            -CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                        }

                        public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                            -CC.$default$didPressViaBot(this, chatMessageCell, str);
                        }

                        public /* synthetic */ void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                            -CC.$default$didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                        }

                        public /* synthetic */ boolean isChatAdminCell(int i) {
                            return -CC.$default$isChatAdminCell(this, i);
                        }

                        public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                            -CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                        }

                        public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                            return -CC.$default$needPlayMessage(this, messageObject);
                        }

                        public /* synthetic */ void videoTimerReached() {
                            -CC.$default$videoTimerReached(this);
                        }
                    });
                    chatMessageCellArr = this.cells;
                    chatMessageCellArr[i2].isChat = false;
                    chatMessageCellArr[i2].setFullyDraw(true);
                    this.cells[i2].setMessageObject(i2 == 0 ? messageObject3 : messageObject2, null, false, false);
                    this.messagesContainer.addView(this.cells[i2], LayoutHelper.createLinear(-1, -2));
                    i2++;
                } else {
                    return;
                }
            }
        }

        public /* synthetic */ void lambda$new$0$ThemeActivity$TextSizeCell(float f) {
            int i = this.startFontSize;
            int round = Math.round(((float) i) + (((float) (this.endFontSize - i)) * f));
            if (round != SharedConfig.fontSize) {
                SharedConfig.fontSize = round;
                Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putInt("fons_size", SharedConfig.fontSize);
                edit.commit();
                Theme.chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
                round = 0;
                while (true) {
                    ChatMessageCell[] chatMessageCellArr = this.cells;
                    if (round < chatMessageCellArr.length) {
                        chatMessageCellArr[round].getMessageObject().resetLayout();
                        this.cells[round].requestLayout();
                        round++;
                    } else {
                        return;
                    }
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(SharedConfig.fontSize);
            canvas.drawText(stringBuilder.toString(), (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            i = MeasureSpec.getSize(i);
            if (this.lastWidth != i) {
                SeekBarView seekBarView = this.sizeBar;
                int i3 = SharedConfig.fontSize;
                int i4 = this.startFontSize;
                seekBarView.setProgress(((float) (i3 - i4)) / ((float) (this.endFontSize - i4)));
                this.lastWidth = i;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.messagesContainer.invalidate();
            this.sizeBar.invalidate();
            int i = 0;
            while (true) {
                ChatMessageCell[] chatMessageCellArr = this.cells;
                if (i < chatMessageCellArr.length) {
                    chatMessageCellArr[i].invalidate();
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 1 || itemViewType == 4 || itemViewType == 7;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(View view) {
            ThemeInfo currentThemeInfo = ((ThemeCell) view.getParent()).getCurrentThemeInfo();
            if (ThemeActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                String str = "ShareFile";
                builder.setItems(currentThemeInfo.pathToFile == null ? new CharSequence[]{LocaleController.getString(str, NUM)} : new CharSequence[]{LocaleController.getString(str, NUM), LocaleController.getString("Edit", NUM), LocaleController.getString("Delete", NUM)}, new -$$Lambda$ThemeActivity$ListAdapter$Tpabhu9ZexFtM7ci83cbgEQWG1k(this, currentThemeInfo));
                ThemeActivity.this.showDialog(builder.create());
            }
        }

        /* JADX WARNING: Missing exception handler attribute for start block: B:50:0x00d1 */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x00aa A:{Catch:{ Exception -> 0x00f6 }} */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00a9 A:{RETURN, Catch:{ Exception -> 0x00f6 }} */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0071 A:{SYNTHETIC, Splitter:B:26:0x0071} */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00a9 A:{RETURN, Catch:{ Exception -> 0x00f6 }} */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x00aa A:{Catch:{ Exception -> 0x00f6 }} */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x007c A:{SYNTHETIC, Splitter:B:31:0x007c} */
        /* JADX WARNING: Can't wrap try/catch for region: R(4:48|49|50|51) */
        public /* synthetic */ void lambda$null$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme.ThemeInfo r4, android.content.DialogInterface r5, int r6) {
            /*
            r3 = this;
            r5 = 0;
            r0 = 1;
            if (r6 != 0) goto L_0x00fc;
        L_0x0004:
            r6 = r4.pathToFile;
            if (r6 != 0) goto L_0x0085;
        L_0x0008:
            r6 = r4.assetName;
            if (r6 != 0) goto L_0x0085;
        L_0x000c:
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r6 = org.telegram.ui.ActionBar.Theme.getDefaultColors();
            r6 = r6.entrySet();
            r6 = r6.iterator();
        L_0x001d:
            r1 = r6.hasNext();
            if (r1 == 0) goto L_0x0044;
        L_0x0023:
            r1 = r6.next();
            r1 = (java.util.Map.Entry) r1;
            r2 = r1.getKey();
            r2 = (java.lang.String) r2;
            r4.append(r2);
            r2 = "=";
            r4.append(r2);
            r1 = r1.getValue();
            r4.append(r1);
            r1 = "\n";
            r4.append(r1);
            goto L_0x001d;
        L_0x0044:
            r6 = new java.io.File;
            r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
            r2 = "default_theme.attheme";
            r6.<init>(r1, r2);
            r1 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x006b }
            r1.<init>(r6);	 Catch:{ Exception -> 0x006b }
            r4 = r4.toString();	 Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r4 = org.telegram.messenger.AndroidUtilities.getStringBytes(r4);	 Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r1.write(r4);	 Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r1.close();	 Catch:{ Exception -> 0x0075 }
            goto L_0x0095;
        L_0x0063:
            r4 = move-exception;
            r5 = r1;
            goto L_0x007a;
        L_0x0066:
            r4 = move-exception;
            r5 = r1;
            goto L_0x006c;
        L_0x0069:
            r4 = move-exception;
            goto L_0x007a;
        L_0x006b:
            r4 = move-exception;
        L_0x006c:
            org.telegram.messenger.FileLog.e(r4);	 Catch:{ all -> 0x0069 }
            if (r5 == 0) goto L_0x0095;
        L_0x0071:
            r5.close();	 Catch:{ Exception -> 0x0075 }
            goto L_0x0095;
        L_0x0075:
            r4 = move-exception;
            org.telegram.messenger.FileLog.e(r4);
            goto L_0x0095;
        L_0x007a:
            if (r5 == 0) goto L_0x0084;
        L_0x007c:
            r5.close();	 Catch:{ Exception -> 0x0080 }
            goto L_0x0084;
        L_0x0080:
            r5 = move-exception;
            org.telegram.messenger.FileLog.e(r5);
        L_0x0084:
            throw r4;
        L_0x0085:
            r5 = r4.assetName;
            if (r5 == 0) goto L_0x008e;
        L_0x0089:
            r6 = org.telegram.ui.ActionBar.Theme.getAssetFile(r5);
            goto L_0x0095;
        L_0x008e:
            r6 = new java.io.File;
            r4 = r4.pathToFile;
            r6.<init>(r4);
        L_0x0095:
            r4 = new java.io.File;
            r5 = 4;
            r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
            r1 = r6.getName();
            r4.<init>(r5, r1);
            r5 = org.telegram.messenger.AndroidUtilities.copyFile(r6, r4);	 Catch:{ Exception -> 0x00f6 }
            if (r5 != 0) goto L_0x00aa;
        L_0x00a9:
            return;
        L_0x00aa:
            r5 = new android.content.Intent;	 Catch:{ Exception -> 0x00f6 }
            r6 = "android.intent.action.SEND";
            r5.<init>(r6);	 Catch:{ Exception -> 0x00f6 }
            r6 = "text/xml";
            r5.setType(r6);	 Catch:{ Exception -> 0x00f6 }
            r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00f6 }
            r1 = 24;
            r2 = "android.intent.extra.STREAM";
            if (r6 < r1) goto L_0x00d9;
        L_0x00be:
            r6 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x00d1 }
            r6 = r6.getParentActivity();	 Catch:{ Exception -> 0x00d1 }
            r1 = "org.telegram.messenger.beta.provider";
            r6 = androidx.core.content.FileProvider.getUriForFile(r6, r1, r4);	 Catch:{ Exception -> 0x00d1 }
            r5.putExtra(r2, r6);	 Catch:{ Exception -> 0x00d1 }
            r5.setFlags(r0);	 Catch:{ Exception -> 0x00d1 }
            goto L_0x00e0;
        L_0x00d1:
            r4 = android.net.Uri.fromFile(r4);	 Catch:{ Exception -> 0x00f6 }
            r5.putExtra(r2, r4);	 Catch:{ Exception -> 0x00f6 }
            goto L_0x00e0;
        L_0x00d9:
            r4 = android.net.Uri.fromFile(r4);	 Catch:{ Exception -> 0x00f6 }
            r5.putExtra(r2, r4);	 Catch:{ Exception -> 0x00f6 }
        L_0x00e0:
            r4 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x00f6 }
            r6 = "ShareFile";
            r0 = NUM; // 0x7f0d08f6 float:1.8746768E38 double:1.053130911E-314;
            r6 = org.telegram.messenger.LocaleController.getString(r6, r0);	 Catch:{ Exception -> 0x00f6 }
            r5 = android.content.Intent.createChooser(r5, r6);	 Catch:{ Exception -> 0x00f6 }
            r6 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r4.startActivityForResult(r5, r6);	 Catch:{ Exception -> 0x00f6 }
            goto L_0x0175;
        L_0x00f6:
            r4 = move-exception;
            org.telegram.messenger.FileLog.e(r4);
            goto L_0x0175;
        L_0x00fc:
            if (r6 != r0) goto L_0x0123;
        L_0x00fe:
            r5 = org.telegram.ui.ThemeActivity.this;
            r5 = r5.parentLayout;
            if (r5 == 0) goto L_0x0175;
        L_0x0106:
            org.telegram.ui.ActionBar.Theme.applyTheme(r4);
            r5 = org.telegram.ui.ThemeActivity.this;
            r5 = r5.parentLayout;
            r5.rebuildAllFragmentViews(r0, r0);
            r5 = new org.telegram.ui.Components.ThemeEditorView;
            r5.<init>();
            r6 = org.telegram.ui.ThemeActivity.this;
            r6 = r6.getParentActivity();
            r4 = r4.name;
            r5.show(r6, r4);
            goto L_0x0175;
        L_0x0123:
            r6 = org.telegram.ui.ThemeActivity.this;
            r6 = r6.getParentActivity();
            if (r6 != 0) goto L_0x012c;
        L_0x012b:
            return;
        L_0x012c:
            r6 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
            r0 = org.telegram.ui.ThemeActivity.this;
            r0 = r0.getParentActivity();
            r6.<init>(r0);
            r0 = NUM; // 0x7f0d034a float:1.8743823E38 double:1.0531301935E-314;
            r1 = "DeleteThemeAlert";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r6.setMessage(r0);
            r0 = NUM; // 0x7f0d00e7 float:1.8742583E38 double:1.0531298917E-314;
            r1 = "AppName";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r6.setTitle(r0);
            r0 = NUM; // 0x7f0d032b float:1.874376E38 double:1.053130178E-314;
            r1 = "Delete";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r1 = new org.telegram.ui.-$$Lambda$ThemeActivity$ListAdapter$HjGrFd2877SP2gFmUCLASSNAMEvuRyOmw;
            r1.<init>(r3, r4);
            r6.setPositiveButton(r0, r1);
            r4 = NUM; // 0x7f0d01de float:1.8743084E38 double:1.0531300137E-314;
            r0 = "Cancel";
            r4 = org.telegram.messenger.LocaleController.getString(r0, r4);
            r6.setNegativeButton(r4, r5);
            r4 = org.telegram.ui.ThemeActivity.this;
            r5 = r6.create();
            r4.showDialog(r5);
        L_0x0175:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity$ListAdapter.lambda$null$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        public /* synthetic */ void lambda$null$0$ThemeActivity$ListAdapter(ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            ThemeActivity.this.updateRows();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View themeCell;
            View textInfoPrivacyCell;
            String str = "windowBackgroundGrayShadow";
            String str2 = "windowBackgroundWhite";
            switch (i) {
                case 0:
                    Context context = this.mContext;
                    boolean z = true;
                    if (ThemeActivity.this.currentType != 1) {
                        z = false;
                    }
                    themeCell = new ThemeCell(context, z);
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    if (ThemeActivity.this.currentType == 0) {
                        themeCell.setOnOptionsClick(new -$$Lambda$ThemeActivity$ListAdapter$pjEslbWZHQ4g-Rxni-i-jc6xbJY(this));
                        break;
                    }
                    break;
                case 1:
                    themeCell = new TextSettingsCell(this.mContext);
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    break;
                case 2:
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                    break;
                case 3:
                    textInfoPrivacyCell = new ShadowSectionCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                    break;
                case 4:
                    themeCell = new ThemeTypeCell(this.mContext);
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    break;
                case 5:
                    themeCell = new HeaderCell(this.mContext);
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    break;
                case 6:
                    themeCell = new BrightnessControlCell(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void didChangedValue(float f) {
                            int i = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int i2 = (int) (f * 100.0f);
                            Theme.autoNightBrighnessThreshold = f;
                            if (i != i2) {
                                Holder holder = (Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    break;
                case 7:
                    themeCell = new TextCheckCell(this.mContext);
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    break;
                case 8:
                    themeCell = new TextSizeCell(this.mContext);
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    break;
                default:
                    themeCell = new ChatListCell(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void didSelectChatType(boolean z) {
                            SharedConfig.setUseThreeLinesLayout(z);
                        }
                    };
                    themeCell.setBackgroundColor(Theme.getColor(str2));
                    break;
            }
            themeCell = textInfoPrivacyCell;
            return new Holder(themeCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            boolean z2 = true;
            String string;
            if (itemViewType == 0) {
                i -= ThemeActivity.this.themeStartRow;
                ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(i);
                ThemeCell themeCell = (ThemeCell) viewHolder.itemView;
                if (i != Theme.themes.size() - 1) {
                    z = true;
                }
                themeCell.setTheme(themeInfo, z);
            } else if (itemViewType == 1) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == ThemeActivity.this.nightThemeRow) {
                    String str = "AutoNightTheme";
                    if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("AutoNightThemeOff", NUM), false);
                        return;
                    } else {
                        textSettingsCell.setTextAndValue(LocaleController.getString(str, NUM), Theme.getCurrentNightThemeName(), false);
                        return;
                    }
                }
                String str2 = "%02d:%02d";
                if (i == ThemeActivity.this.scheduleFromRow) {
                    i = Theme.autoNightDayStartTime;
                    i -= (i / 60) * 60;
                    textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format(str2, new Object[]{Integer.valueOf(itemViewType), Integer.valueOf(i)}), true);
                } else if (i == ThemeActivity.this.scheduleToRow) {
                    i = Theme.autoNightDayEndTime;
                    i -= (i / 60) * 60;
                    textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format(str2, new Object[]{Integer.valueOf(itemViewType), Integer.valueOf(i)}), false);
                } else if (i == ThemeActivity.this.scheduleUpdateLocationRow) {
                    textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                } else if (i == ThemeActivity.this.contactsSortRow) {
                    i = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                    if (i == 0) {
                        string = LocaleController.getString("Default", NUM);
                    } else if (i == 1) {
                        string = LocaleController.getString("FirstName", NUM);
                    } else {
                        string = LocaleController.getString("LastName", NUM);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("SortBy", NUM), string, true);
                } else if (i == ThemeActivity.this.backgroundRow) {
                    textSettingsCell.setText(LocaleController.getString("ChatBackground", NUM), true);
                } else if (i == ThemeActivity.this.contactsReimportRow) {
                    textSettingsCell.setText(LocaleController.getString("ImportContacts", NUM), true);
                } else if (i == ThemeActivity.this.stickersRow) {
                    textSettingsCell.setText(LocaleController.getString("StickersAndMasks", NUM), false);
                } else if (i == ThemeActivity.this.emojiRow) {
                    textSettingsCell.setText(LocaleController.getString("Emoji", NUM), true);
                }
            } else if (itemViewType == 2) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == ThemeActivity.this.automaticBrightnessInfoRow) {
                    textInfoPrivacyCell.setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                } else if (i == ThemeActivity.this.scheduleLocationInfoRow) {
                    textInfoPrivacyCell.setText(ThemeActivity.this.getLocationSunString());
                }
            } else if (itemViewType == 4) {
                ThemeTypeCell themeTypeCell = (ThemeTypeCell) viewHolder.itemView;
                if (i == ThemeActivity.this.nightDisabledRow) {
                    string = LocaleController.getString("AutoNightDisabled", NUM);
                    if (Theme.selectedAutoNightType == 0) {
                        z = true;
                    }
                    themeTypeCell.setValue(string, z, true);
                } else if (i == ThemeActivity.this.nightScheduledRow) {
                    string = LocaleController.getString("AutoNightScheduled", NUM);
                    if (Theme.selectedAutoNightType == 1) {
                        z = true;
                    }
                    themeTypeCell.setValue(string, z, true);
                } else if (i == ThemeActivity.this.nightAutomaticRow) {
                    string = LocaleController.getString("AutoNightAutomatic", NUM);
                    if (Theme.selectedAutoNightType != 2) {
                        z2 = false;
                    }
                    themeTypeCell.setValue(string, z2, false);
                }
            } else if (itemViewType == 5) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == ThemeActivity.this.scheduleHeaderRow) {
                    headerCell.setText(LocaleController.getString("AutoNightSchedule", NUM));
                } else if (i == ThemeActivity.this.automaticHeaderRow) {
                    headerCell.setText(LocaleController.getString("AutoNightBrightness", NUM));
                } else if (i == ThemeActivity.this.preferedHeaderRow) {
                    headerCell.setText(LocaleController.getString("AutoNightPreferred", NUM));
                } else if (i == ThemeActivity.this.settingsRow) {
                    headerCell.setText(LocaleController.getString("SETTINGS", NUM));
                } else if (i == ThemeActivity.this.themeHeaderRow) {
                    headerCell.setText(LocaleController.getString("ColorTheme", NUM));
                } else if (i == ThemeActivity.this.textSizeHeaderRow) {
                    headerCell.setText(LocaleController.getString("TextSizeHeader", NUM));
                } else if (i == ThemeActivity.this.chatListHeaderRow) {
                    headerCell.setText(LocaleController.getString("ChatList", NUM));
                }
            } else if (itemViewType == 6) {
                ((BrightnessControlCell) viewHolder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
            } else if (itemViewType == 7) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == ThemeActivity.this.scheduleLocationRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", NUM), Theme.autoNightScheduleByLocation, true);
                } else if (i == ThemeActivity.this.enableAnimationsRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", NUM), MessagesController.getGlobalMainSettings().getBoolean("view_animations", true), true);
                } else if (i == ThemeActivity.this.sendByEnterRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", NUM), MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false), true);
                } else if (i == ThemeActivity.this.saveToGalleryRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", NUM), SharedConfig.saveToGallery, false);
                } else if (i == ThemeActivity.this.raiseToSpeakRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", NUM), SharedConfig.raiseToSpeak, true);
                } else if (i == ThemeActivity.this.customTabsRow) {
                    textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", NUM), LocaleController.getString("ChromeCustomTabsInfo", NUM), SharedConfig.customTabs, false, true);
                } else if (i == ThemeActivity.this.directShareRow) {
                    textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", NUM), LocaleController.getString("DirectShareInfo", NUM), SharedConfig.directShare, false, true);
                }
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 4) {
                ((ThemeTypeCell) viewHolder.itemView).setTypeChecked(viewHolder.getAdapterPosition() == Theme.selectedAutoNightType);
            } else if (itemViewType == 0) {
                ((ThemeCell) viewHolder.itemView).updateCurrentThemeCheck();
            }
            if (itemViewType != 2 && itemViewType != 3) {
                viewHolder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }

        public int getItemViewType(int i) {
            if (i == ThemeActivity.this.nightThemeRow || i == ThemeActivity.this.scheduleFromRow || i == ThemeActivity.this.emojiRow || i == ThemeActivity.this.scheduleToRow || i == ThemeActivity.this.scheduleUpdateLocationRow || i == ThemeActivity.this.backgroundRow || i == ThemeActivity.this.contactsReimportRow || i == ThemeActivity.this.contactsSortRow || i == ThemeActivity.this.stickersRow) {
                return 1;
            }
            if (i == ThemeActivity.this.automaticBrightnessInfoRow || i == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow || i == ThemeActivity.this.scheduleFromToInfoRow || i == ThemeActivity.this.stickersSection2Row || i == ThemeActivity.this.settings2Row || i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.chatListInfoRow) {
                return 3;
            }
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow) {
                return 4;
            }
            if (i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow || i == ThemeActivity.this.preferedHeaderRow || i == ThemeActivity.this.settingsRow || i == ThemeActivity.this.themeHeaderRow || i == ThemeActivity.this.textSizeHeaderRow || i == ThemeActivity.this.chatListHeaderRow) {
                return 5;
            }
            if (i == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (i == ThemeActivity.this.scheduleLocationRow || i == ThemeActivity.this.enableAnimationsRow || i == ThemeActivity.this.sendByEnterRow || i == ThemeActivity.this.saveToGalleryRow || i == ThemeActivity.this.raiseToSpeakRow || i == ThemeActivity.this.customTabsRow || i == ThemeActivity.this.directShareRow) {
                return 7;
            }
            if (i == ThemeActivity.this.textSizeRow) {
                return 8;
            }
            return i == ThemeActivity.this.chatListRow ? 9 : 0;
        }
    }

    static /* synthetic */ void lambda$openThemeCreate$5(DialogInterface dialogInterface, int i) {
    }

    public ThemeActivity(int i) {
        this.currentType = i;
        updateRows();
    }

    private void updateRows() {
        int i;
        int i2 = this.rowCount;
        this.rowCount = 0;
        this.emojiRow = -1;
        this.contactsReimportRow = -1;
        this.contactsSortRow = -1;
        this.scheduleLocationRow = -1;
        this.scheduleUpdateLocationRow = -1;
        this.scheduleLocationInfoRow = -1;
        this.nightDisabledRow = -1;
        this.nightScheduledRow = -1;
        this.nightAutomaticRow = -1;
        this.nightTypeInfoRow = -1;
        this.scheduleHeaderRow = -1;
        this.nightThemeRow = -1;
        this.newThemeInfoRow = -1;
        this.scheduleFromRow = -1;
        this.scheduleToRow = -1;
        this.scheduleFromToInfoRow = -1;
        this.themeStartRow = -1;
        this.themeEndRow = -1;
        this.themeInfoRow = -1;
        this.preferedHeaderRow = -1;
        this.automaticHeaderRow = -1;
        this.automaticBrightnessRow = -1;
        this.automaticBrightnessInfoRow = -1;
        this.textSizeHeaderRow = -1;
        this.themeHeaderRow = -1;
        this.chatListHeaderRow = -1;
        this.chatListRow = -1;
        this.chatListInfoRow = -1;
        this.textSizeRow = -1;
        this.backgroundRow = -1;
        this.settingsRow = -1;
        this.customTabsRow = -1;
        this.directShareRow = -1;
        this.enableAnimationsRow = -1;
        this.raiseToSpeakRow = -1;
        this.sendByEnterRow = -1;
        this.saveToGalleryRow = -1;
        this.settings2Row = -1;
        this.stickersRow = -1;
        this.stickersSection2Row = -1;
        int i3 = 2;
        if (this.currentType == 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.textSizeHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.textSizeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.backgroundRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightThemeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.newThemeInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.themeHeaderRow = i;
            i = this.rowCount;
            this.themeStartRow = i;
            this.rowCount = i + Theme.themes.size();
            i = this.rowCount;
            this.themeEndRow = i;
            this.rowCount = i + 1;
            this.themeInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.chatListHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.chatListRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.chatListInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.settingsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.customTabsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.directShareRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.enableAnimationsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.raiseToSpeakRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendByEnterRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.saveToGalleryRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.settings2Row = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersSection2Row = i;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightDisabledRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightScheduledRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightAutomaticRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightTypeInfoRow = i;
            i = Theme.selectedAutoNightType;
            if (i == 1) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.scheduleHeaderRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.scheduleLocationRow = i;
                if (Theme.autoNightScheduleByLocation) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleUpdateLocationRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleLocationInfoRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleFromRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleToRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleFromToInfoRow = i;
                }
            } else if (i == 2) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.automaticHeaderRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.automaticBrightnessRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.automaticBrightnessInfoRow = i;
            }
            if (Theme.selectedAutoNightType != 0) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.preferedHeaderRow = i;
                i = this.rowCount;
                this.themeStartRow = i;
                this.rowCount = i + Theme.themes.size();
                i = this.rowCount;
                this.themeEndRow = i;
                this.rowCount = i + 1;
                this.themeInfoRow = i;
            }
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            if (this.currentType != 0) {
                int i4 = this.previousUpdatedType;
                if (i4 != -1) {
                    int i5 = this.nightTypeInfoRow + 1;
                    if (i4 != Theme.selectedAutoNightType) {
                        i = 0;
                        while (i < 3) {
                            Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(i);
                            if (holder != null) {
                                ((ThemeTypeCell) holder.itemView).setTypeChecked(i == Theme.selectedAutoNightType);
                            }
                            i++;
                        }
                        int i6 = Theme.selectedAutoNightType;
                        if (i6 == 0) {
                            this.listAdapter.notifyItemRangeRemoved(i5, i2 - i5);
                        } else {
                            i2 = 4;
                            ListAdapter listAdapter2;
                            if (i6 == 1) {
                                i6 = this.previousUpdatedType;
                                if (i6 == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i5, this.rowCount - i5);
                                } else if (i6 == 2) {
                                    this.listAdapter.notifyItemRangeRemoved(i5, 3);
                                    listAdapter2 = this.listAdapter;
                                    if (!Theme.autoNightScheduleByLocation) {
                                        i2 = 5;
                                    }
                                    listAdapter2.notifyItemRangeInserted(i5, i2);
                                }
                            } else if (i6 == 2) {
                                i6 = this.previousUpdatedType;
                                if (i6 == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i5, this.rowCount - i5);
                                } else if (i6 == 1) {
                                    listAdapter2 = this.listAdapter;
                                    if (!Theme.autoNightScheduleByLocation) {
                                        i2 = 5;
                                    }
                                    listAdapter2.notifyItemRangeRemoved(i5, i2);
                                    this.listAdapter.notifyItemRangeInserted(i5, 3);
                                }
                            }
                        }
                    } else {
                        boolean z = this.previousByLocation;
                        boolean z2 = Theme.autoNightScheduleByLocation;
                        if (z != z2) {
                            i5 += 2;
                            listAdapter.notifyItemRangeRemoved(i5, z2 ? 3 : 2);
                            ListAdapter listAdapter3 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i3 = 3;
                            }
                            listAdapter3.notifyItemRangeInserted(i5, i3);
                        }
                    }
                }
            }
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.currentType == 1) {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        stopLocationUpdate();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        Theme.saveAutoNightThemeConfig();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", NUM));
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
            addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            addItem.addSubItem(1, NUM, LocaleController.getString("CreateNewThemeMenu", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ThemeActivity.this.finishFragment();
                } else if (i == 1 && ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NewTheme", NUM));
                    builder.setMessage(LocaleController.getString("CreateNewThemeAlert", NUM));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    builder.setPositiveButton(LocaleController.getString("CreateTheme", NUM), new -$$Lambda$ThemeActivity$1$ZQnhOSOAx8cfjiv91xqtf3q-RU0(this));
                    ThemeActivity.this.showDialog(builder.create());
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ThemeActivity$1(DialogInterface dialogInterface, int i) {
                ThemeActivity.this.openThemeCreate();
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$ThemeActivity$K_P6g0KdeH1Gzx3Q4dvwe8rVcqw(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$4$ThemeActivity(View view, int i) {
        SharedPreferences globalMainSettings;
        String str;
        boolean z;
        Editor edit;
        if (i == this.enableAnimationsRow) {
            globalMainSettings = MessagesController.getGlobalMainSettings();
            str = "view_animations";
            z = globalMainSettings.getBoolean(str, true);
            edit = globalMainSettings.edit();
            edit.putBoolean(str, z ^ 1);
            edit.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(z ^ 1);
            }
        } else {
            int i2 = 0;
            if (i == this.backgroundRow) {
                presentFragment(new WallpapersListActivity(0));
            } else if (i == this.sendByEnterRow) {
                globalMainSettings = MessagesController.getGlobalMainSettings();
                str = "send_by_enter";
                z = globalMainSettings.getBoolean(str, false);
                edit = globalMainSettings.edit();
                edit.putBoolean(str, z ^ 1);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(z ^ 1);
                }
            } else if (i == this.raiseToSpeakRow) {
                SharedConfig.toogleRaiseToSpeak();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
                }
            } else if (i == this.saveToGalleryRow) {
                SharedConfig.toggleSaveToGallery();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
                }
            } else if (i == this.customTabsRow) {
                SharedConfig.toggleCustomTabs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
                }
            } else if (i == this.directShareRow) {
                SharedConfig.toggleDirectShare();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.directShare);
                }
            } else if (i != this.contactsReimportRow) {
                if (i == this.contactsSortRow) {
                    if (getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("SortBy", NUM));
                        builder.setItems(new CharSequence[]{LocaleController.getString("Default", NUM), LocaleController.getString("SortFirstName", NUM), LocaleController.getString("SortLastName", NUM)}, new -$$Lambda$ThemeActivity$LBhNIATz8-Cljc8e68mM8gJjZHI(this, i));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        showDialog(builder.create());
                    }
                } else if (i == this.stickersRow) {
                    presentFragment(new StickersActivity(0));
                } else if (i != this.emojiRow) {
                    int i3 = this.themeStartRow;
                    if (i >= i3 && i < this.themeEndRow) {
                        i -= i3;
                        if (i >= 0 && i < Theme.themes.size()) {
                            ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(i);
                            if (this.currentType != 0) {
                                Theme.setCurrentNightTheme(themeInfo);
                            } else if (themeInfo != Theme.getCurrentTheme()) {
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.valueOf(false));
                            } else {
                                return;
                            }
                            int childCount = this.listView.getChildCount();
                            while (i2 < childCount) {
                                View childAt = this.listView.getChildAt(i2);
                                if (childAt instanceof ThemeCell) {
                                    ((ThemeCell) childAt).updateCurrentThemeCheck();
                                }
                                i2++;
                            }
                        }
                    } else if (i == this.nightThemeRow) {
                        presentFragment(new ThemeActivity(1));
                    } else if (i == this.nightDisabledRow) {
                        Theme.selectedAutoNightType = 0;
                        updateRows();
                        Theme.checkAutoNightThemeConditions();
                    } else if (i == this.nightScheduledRow) {
                        Theme.selectedAutoNightType = 1;
                        if (Theme.autoNightScheduleByLocation) {
                            updateSunTime(null, true);
                        }
                        updateRows();
                        Theme.checkAutoNightThemeConditions();
                    } else if (i == this.nightAutomaticRow) {
                        Theme.selectedAutoNightType = 2;
                        updateRows();
                        Theme.checkAutoNightThemeConditions();
                    } else if (i == this.scheduleLocationRow) {
                        Theme.autoNightScheduleByLocation ^= 1;
                        ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
                        updateRows();
                        if (Theme.autoNightScheduleByLocation) {
                            updateSunTime(null, true);
                        }
                        Theme.checkAutoNightThemeConditions();
                    } else if (i == this.scheduleFromRow || i == this.scheduleToRow) {
                        if (getParentActivity() != null) {
                            int i4;
                            if (i == this.scheduleFromRow) {
                                i3 = Theme.autoNightDayStartTime;
                                i4 = i3 / 60;
                            } else {
                                i3 = Theme.autoNightDayEndTime;
                                i4 = i3 / 60;
                            }
                            showDialog(new TimePickerDialog(getParentActivity(), new -$$Lambda$ThemeActivity$Vm53Z0hPZ6cQlgJQ4_8I1uGYaeQ(this, i, (TextSettingsCell) view), i4, i3 - (i4 * 60), true));
                        }
                    } else if (i == this.scheduleUpdateLocationRow) {
                        updateSunTime(null, true);
                    }
                } else if (getParentActivity() != null) {
                    boolean[] zArr = new boolean[2];
                    Builder builder2 = new Builder(getParentActivity());
                    builder2.setApplyTopPadding(false);
                    builder2.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    int i5 = 0;
                    while (true) {
                        if (i5 >= (VERSION.SDK_INT >= 19 ? 2 : 1)) {
                            break;
                        }
                        String string;
                        if (i5 == 0) {
                            zArr[i5] = SharedConfig.allowBigEmoji;
                            string = LocaleController.getString("EmojiBigSize", NUM);
                        } else if (i5 == 1) {
                            zArr[i5] = SharedConfig.useSystemEmoji;
                            string = LocaleController.getString("EmojiUseDefault", NUM);
                        } else {
                            string = null;
                        }
                        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21);
                        checkBoxCell.setTag(Integer.valueOf(i5));
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                        checkBoxCell.setText(string, "", zArr[i5], true);
                        checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                        checkBoxCell.setOnClickListener(new -$$Lambda$ThemeActivity$n976DkWHqIPAEeJVINRGuhlSY0Q(zArr));
                        i5++;
                    }
                    BottomSheetCell bottomSheetCell = new BottomSheetCell(getParentActivity(), 1);
                    bottomSheetCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    bottomSheetCell.setTextAndIcon(LocaleController.getString("Save", NUM).toUpperCase(), 0);
                    bottomSheetCell.setTextColor(Theme.getColor("dialogTextBlue2"));
                    bottomSheetCell.setOnClickListener(new -$$Lambda$ThemeActivity$ECOH3EyfCYRNnULM041aKWeOCIY(this, zArr, i));
                    linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 50));
                    builder2.setCustomView(linearLayout);
                    showDialog(builder2.create());
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$0$ThemeActivity(int i, DialogInterface dialogInterface, int i2) {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("sortContactsBy", i2);
        edit.commit();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(i);
        }
    }

    static /* synthetic */ void lambda$null$1(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        int intValue = ((Integer) checkBoxCell.getTag()).intValue();
        zArr[intValue] = zArr[intValue] ^ 1;
        checkBoxCell.setChecked(zArr[intValue], true);
    }

    public /* synthetic */ void lambda$null$2$ThemeActivity(boolean[] zArr, int i, View view) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        boolean z = zArr[0];
        SharedConfig.allowBigEmoji = z;
        edit.putBoolean("allowBigEmoji", z);
        boolean z2 = zArr[1];
        SharedConfig.useSystemEmoji = z2;
        edit.putBoolean("useSystemEmoji", z2);
        edit.commit();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(i);
        }
    }

    public /* synthetic */ void lambda$null$3$ThemeActivity(int i, TextSettingsCell textSettingsCell, TimePicker timePicker, int i2, int i3) {
        int i4 = (i2 * 60) + i3;
        String str = "%02d:%02d";
        if (i == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = i4;
            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format(str, new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
            return;
        }
        Theme.autoNightDayEndTime = i4;
        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format(str, new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void openThemeCreate() {
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getParentActivity());
        editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewTheme", NUM));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), -$$Lambda$ThemeActivity$T2DEwxCT8S71lYsgAFvNuKCLASSNAMEQo.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView textView = new TextView(getParentActivity());
        textView.setText(LocaleController.formatString("EnterThemeName", NUM, new Object[0]));
        textView.setTextSize(16.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        String str = "dialogTextBlack";
        textView.setTextColor(Theme.getColor(str));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        editTextBoldCursor.setTextSize(1, 16.0f);
        editTextBoldCursor.setTextColor(Theme.getColor(str));
        editTextBoldCursor.setMaxLines(1);
        editTextBoldCursor.setLines(1);
        editTextBoldCursor.setInputType(16385);
        editTextBoldCursor.setGravity(51);
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
        editTextBoldCursor.setCursorWidth(1.5f);
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        editTextBoldCursor.setOnEditorActionListener(-$$Lambda$ThemeActivity$VWCUOR2j_GKIfMXwZfHRrJ8b5fU.INSTANCE);
        AlertDialog create = builder.create();
        create.setOnShowListener(new -$$Lambda$ThemeActivity$1vEC6O3lueqPvsr0HLElXf1QyPI(editTextBoldCursor));
        showDialog(create);
        create.getButton(-1).setOnClickListener(new -$$Lambda$ThemeActivity$wZ4-Th-MCpzrvar_lsEjlTx06zvs(this, editTextBoldCursor, create));
    }

    static /* synthetic */ void lambda$null$7(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public /* synthetic */ void lambda$openThemeCreate$9$ThemeActivity(EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, View view) {
        if (editTextBoldCursor.length() == 0) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(editTextBoldCursor, 2.0f, 0);
            return;
        }
        ThemeEditorView themeEditorView = new ThemeEditorView();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(editTextBoldCursor.getText().toString());
        stringBuilder.append(".attheme");
        String stringBuilder2 = stringBuilder.toString();
        themeEditorView.show(getParentActivity(), stringBuilder2);
        Theme.saveCurrentTheme(stringBuilder2, true);
        updateRows();
        alertDialog.dismiss();
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        String str = "themehint";
        if (!globalMainSettings.getBoolean(str, false)) {
            globalMainSettings.edit().putBoolean(str, true).commit();
            try {
                Toast.makeText(getParentActivity(), LocaleController.getString("CreateNewThemeHelp", NUM), 1).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void updateSunTime(Location location, boolean z) {
        String str = "location";
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(str);
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                    return;
                }
            }
        }
        String str2 = "gps";
        if (getParentActivity() != null) {
            if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                try {
                    if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService(str)).isProviderEnabled(str2)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("GpsDisabledAlert", NUM));
                        builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new -$$Lambda$ThemeActivity$oEXZvbxqKHkZY6ZgCJTwtSLiYYk(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        showDialog(builder.create());
                        return;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                return;
            }
        }
        try {
            location = locationManager.getLastKnownLocation(str2);
            if (location == null) {
                location = locationManager.getLastKnownLocation("network");
            }
            if (location == null) {
                location = locationManager.getLastKnownLocation("passive");
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (location == null || z) {
            startLocationUpdate();
            if (location == null) {
                return;
            }
        }
        Theme.autoNightLocationLatitude = location.getLatitude();
        Theme.autoNightLocationLongitude = location.getLongitude();
        int[] calculateSunriseSunset = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
        Theme.autoNightSunriseTime = calculateSunriseSunset[0];
        Theme.autoNightSunsetTime = calculateSunriseSunset[1];
        Theme.autoNightCityName = null;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        Theme.autoNightLastSunCheckDay = instance.get(5);
        Utilities.globalQueue.postRunnable(new -$$Lambda$ThemeActivity$TGRjhun5kuM4-l-qFwZrzxfz7ho(this));
        Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null) {
            View view = holder.itemView;
            if (view instanceof TextInfoPrivacyCell) {
                ((TextInfoPrivacyCell) view).setText(getLocationSunString());
            }
        }
        if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    public /* synthetic */ void lambda$updateSunTime$10$ThemeActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
        }
    }

    public /* synthetic */ void lambda$updateSunTime$12$ThemeActivity() {
        String str = null;
        try {
            List fromLocation = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (fromLocation.size() > 0) {
                str = ((Address) fromLocation.get(0)).getLocality();
            }
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeActivity$aYbaWuvP_GFKk6bpoq7IXffT-AE(this, str));
    }

    public /* synthetic */ void lambda$null$11$ThemeActivity(String str) {
        Theme.autoNightCityName = str;
        if (Theme.autoNightCityName == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", new Object[]{Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude)});
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            Holder holder = (Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow);
            if (holder != null) {
                View view = holder.itemView;
                if (view instanceof TextSettingsCell) {
                    ((TextSettingsCell) view).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                }
            }
        }
    }

    private void startLocationUpdate() {
        if (!this.updatingLocation) {
            this.updatingLocation = true;
            LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            try {
                locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    private void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$ThemeActivity$6BTFbEaGAqWQVckpQGbDShbZmy0(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$13$ThemeActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private String getLocationSunString() {
        int i = Theme.autoNightSunriseTime;
        i -= (i / 60) * 60;
        Object[] objArr = new Object[]{Integer.valueOf(r1), Integer.valueOf(i)};
        String format = String.format("%02d:%02d", objArr);
        int i2 = Theme.autoNightSunsetTime;
        i2 -= (i2 / 60) * 60;
        Object[] objArr2 = new Object[]{Integer.valueOf(r6), Integer.valueOf(i2)};
        return LocaleController.formatString("AutoNightUpdateLocationInfo", NUM, String.format("%02d:%02d", objArr2), format);
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[57];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, ThemeCell.class, TextSizeCell.class, ChatListCell.class}, null, null, null, "windowBackgroundWhite");
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r1[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        r1[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        r1[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        r1[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        Class[] clsArr = new Class[]{ThemeCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[12] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{ThemeCell.class};
        strArr = new String[1];
        strArr[0] = "checkImage";
        r1[13] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "featuredStickers_addedIcon");
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"optionsButton"}, null, null, null, "stickers_menu");
        view = this.listView;
        View view2 = view;
        r1[15] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        view2 = view;
        r1[16] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[19] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r1[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        r1[22] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        r1[23] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        view = this.listView;
        view2 = view;
        r1[24] = new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, null, null, null, "profile_actionIcon");
        view = this.listView;
        view2 = view;
        r1[25] = new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, null, null, null, "profile_actionIcon");
        view = this.listView;
        clsArr = new Class[]{BrightnessControlCell.class};
        strArr = new String[1];
        strArr[0] = "seekBarView";
        r1[26] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "player_progressBackground");
        view = this.listView;
        view2 = view;
        r1[27] = new ThemeDescription(view2, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, "player_progress");
        r1[28] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[29] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        view = this.listView;
        int i = ThemeDescription.FLAG_PROGRESSBAR;
        clsArr = new Class[]{TextSizeCell.class};
        strArr = new String[1];
        strArr[0] = "sizeBar";
        r1[30] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "player_progress");
        r1[31] = new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, "player_progressBackground");
        r1[32] = new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackground");
        r1[33] = new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackgroundChecked");
        r1[34] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        r1[35] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        r1[36] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        r1[37] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        r1[38] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        r1[39] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        r1[40] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        r1[41] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        r1[42] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheck");
        r1[43] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        r1[44] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        r1[45] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        r1[46] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        r1[47] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        r1[48] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        r1[49] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        r1[50] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        r1[51] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        r1[52] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        r1[53] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        r1[54] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        r1[55] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        r1[56] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return r1;
    }
}
