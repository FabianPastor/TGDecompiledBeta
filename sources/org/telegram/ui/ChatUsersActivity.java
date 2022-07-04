package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.GigagroupConvertAlert;
import org.telegram.ui.Components.IntSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCreateActivity;

public class ChatUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int SELECT_TYPE_ADMIN = 1;
    public static final int SELECT_TYPE_BLOCK = 2;
    public static final int SELECT_TYPE_EXCEPTION = 3;
    public static final int SELECT_TYPE_MEMBERS = 0;
    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_BANNED = 0;
    public static final int TYPE_KICKED = 3;
    public static final int TYPE_USERS = 2;
    private static final int done_button = 1;
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public int addNew2Row;
    /* access modifiers changed from: private */
    public int addNewRow;
    /* access modifiers changed from: private */
    public int addNewSectionRow;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public int blockedEmptyRow;
    /* access modifiers changed from: private */
    public int botEndRow;
    /* access modifiers changed from: private */
    public int botHeaderRow;
    /* access modifiers changed from: private */
    public int botStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> bots = new ArrayList<>();
    private boolean botsEndReached;
    private LongSparseArray<TLObject> botsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int changeInfoRow;
    /* access modifiers changed from: private */
    public long chatId = this.arguments.getLong("chat_id");
    /* access modifiers changed from: private */
    public ArrayList<TLObject> contacts = new ArrayList<>();
    private boolean contactsEndReached;
    /* access modifiers changed from: private */
    public int contactsEndRow;
    /* access modifiers changed from: private */
    public int contactsHeaderRow;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> contactsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatBannedRights defaultBannedRights = new TLRPC.TL_chatBannedRights();
    private int delayResults;
    /* access modifiers changed from: private */
    public ChatUsersActivityDelegate delegate;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public boolean firstLoaded;
    /* access modifiers changed from: private */
    public FlickerLoadingView flickerLoadingView;
    /* access modifiers changed from: private */
    public int gigaConvertRow;
    /* access modifiers changed from: private */
    public int gigaHeaderRow;
    /* access modifiers changed from: private */
    public int gigaInfoRow;
    private LongSparseArray<TLRPC.TL_groupCallParticipant> ignoredUsers;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    private String initialBannedRights;
    private int initialSlowmode;
    /* access modifiers changed from: private */
    public boolean isChannel;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public int loadingHeaderRow;
    /* access modifiers changed from: private */
    public int loadingProgressRow;
    /* access modifiers changed from: private */
    public int loadingUserCellRow;
    /* access modifiers changed from: private */
    public boolean loadingUsers;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    private boolean openTransitionStarted;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> participants = new ArrayList<>();
    /* access modifiers changed from: private */
    public int participantsDivider2Row;
    /* access modifiers changed from: private */
    public int participantsDividerRow;
    /* access modifiers changed from: private */
    public int participantsEndRow;
    /* access modifiers changed from: private */
    public int participantsInfoRow;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> participantsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int participantsStartRow;
    /* access modifiers changed from: private */
    public int permissionsSectionRow;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public View progressBar;
    /* access modifiers changed from: private */
    public int recentActionsRow;
    /* access modifiers changed from: private */
    public int removedUsersRow;
    /* access modifiers changed from: private */
    public int restricted1SectionRow;
    /* access modifiers changed from: private */
    public int rowCount;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public int selectType = this.arguments.getInt("selectType");
    /* access modifiers changed from: private */
    public int selectedSlowmode;
    /* access modifiers changed from: private */
    public int sendMediaRow;
    /* access modifiers changed from: private */
    public int sendMessagesRow;
    /* access modifiers changed from: private */
    public int sendPollsRow;
    /* access modifiers changed from: private */
    public int sendStickersRow;
    /* access modifiers changed from: private */
    public int slowmodeInfoRow;
    /* access modifiers changed from: private */
    public int slowmodeRow;
    /* access modifiers changed from: private */
    public int slowmodeSelectRow;
    /* access modifiers changed from: private */
    public int type = this.arguments.getInt("type");
    private UndoView undoView;

    public interface ChatUsersActivityDelegate {
        void didAddParticipantToList(long j, TLObject tLObject);

        void didChangeOwner(TLRPC.User user);

        void didKickParticipant(long j);

        void didSelectUser(long j);

        /* renamed from: org.telegram.ui.ChatUsersActivity$ChatUsersActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didAddParticipantToList(ChatUsersActivityDelegate _this, long uid, TLObject participant) {
            }

            public static void $default$didChangeOwner(ChatUsersActivityDelegate _this, TLRPC.User user) {
            }

            public static void $default$didSelectUser(ChatUsersActivityDelegate _this, long uid) {
            }

            public static void $default$didKickParticipant(ChatUsersActivityDelegate _this, long userId) {
            }
        }
    }

    private class ChooseView extends View {
        private final SeekBarAccessibilityDelegate accessibilityDelegate;
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private final Paint paint = new Paint(1);
        private int sideSide;
        private ArrayList<Integer> sizes = new ArrayList<>();
        private boolean startMoving;
        private int startMovingItem;
        private float startX;
        /* access modifiers changed from: private */
        public ArrayList<String> strings = new ArrayList<>();
        private final TextPaint textPaint;

        public ChooseView(Context context) {
            super(context);
            String string;
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
            for (int a = 0; a < 7; a++) {
                switch (a) {
                    case 0:
                        string = LocaleController.getString("SlowmodeOff", NUM);
                        break;
                    case 1:
                        string = LocaleController.formatString("SlowmodeSeconds", NUM, 10);
                        break;
                    case 2:
                        string = LocaleController.formatString("SlowmodeSeconds", NUM, 30);
                        break;
                    case 3:
                        string = LocaleController.formatString("SlowmodeMinutes", NUM, 1);
                        break;
                    case 4:
                        string = LocaleController.formatString("SlowmodeMinutes", NUM, 5);
                        break;
                    case 5:
                        string = LocaleController.formatString("SlowmodeMinutes", NUM, 15);
                        break;
                    default:
                        string = LocaleController.formatString("SlowmodeHours", NUM, 1);
                        break;
                }
                this.strings.add(string);
                this.sizes.add(Integer.valueOf((int) Math.ceil((double) this.textPaint.measureText(string))));
            }
            this.accessibilityDelegate = new IntSeekBarAccessibilityDelegate(ChatUsersActivity.this) {
                public int getProgress() {
                    return ChatUsersActivity.this.selectedSlowmode;
                }

                public void setProgress(int progress) {
                    ChooseView.this.setItem(progress);
                }

                public int getMaxValue() {
                    return ChooseView.this.strings.size() - 1;
                }

                /* access modifiers changed from: protected */
                public CharSequence getContentDescription(View host) {
                    if (ChatUsersActivity.this.selectedSlowmode == 0) {
                        return LocaleController.getString("SlowmodeOff", NUM);
                    }
                    return ChatUsersActivity.this.formatSeconds(ChatUsersActivity.this.getSecondsForIndex(ChatUsersActivity.this.selectedSlowmode));
                }
            };
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            this.accessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || this.accessibilityDelegate.performAccessibilityActionInternal(this, action, arguments);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            boolean z = false;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int a = 0;
                while (true) {
                    if (a >= this.strings.size()) {
                        break;
                    }
                    int i = this.sideSide;
                    int i2 = this.lineSize + (this.gapSize * 2);
                    int i3 = this.circleSize;
                    int cx = i + ((i2 + i3) * a) + (i3 / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == ChatUsersActivity.this.selectedSlowmode) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingItem = ChatUsersActivity.this.selectedSlowmode;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= this.strings.size()) {
                            break;
                        }
                        int i4 = this.sideSide;
                        int i5 = this.lineSize;
                        int i6 = this.gapSize;
                        int i7 = this.circleSize;
                        int cx2 = i4 + (((i6 * 2) + i5 + i7) * a2) + (i7 / 2);
                        int diff = (i5 / 2) + (i7 / 2) + i6;
                        if (x <= ((float) (cx2 - diff)) || x >= ((float) (cx2 + diff))) {
                            a2++;
                        } else if (ChatUsersActivity.this.selectedSlowmode != a2) {
                            setItem(a2);
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    int a3 = 0;
                    while (true) {
                        if (a3 >= this.strings.size()) {
                            break;
                        }
                        int i8 = this.sideSide;
                        int i9 = this.lineSize + (this.gapSize * 2);
                        int i10 = this.circleSize;
                        int cx3 = i8 + ((i9 + i10) * a3) + (i10 / 2);
                        if (x <= ((float) (cx3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx3))) {
                            a3++;
                        } else if (ChatUsersActivity.this.selectedSlowmode != a3) {
                            setItem(a3);
                        }
                    }
                } else if (ChatUsersActivity.this.selectedSlowmode != this.startMovingItem) {
                    setItem(ChatUsersActivity.this.selectedSlowmode);
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        /* access modifiers changed from: private */
        public void setItem(int index) {
            if (ChatUsersActivity.this.info != null) {
                int unused = ChatUsersActivity.this.selectedSlowmode = index;
                ChatUsersActivity.this.listViewAdapter.notifyItemChanged(ChatUsersActivity.this.slowmodeInfoRow);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * this.strings.size())) - ((this.gapSize * 2) * (this.strings.size() - 1))) - (this.sideSide * 2)) / (this.strings.size() - 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int a = 0;
            while (a < this.strings.size()) {
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (a <= ChatUsersActivity.this.selectedSlowmode) {
                    this.paint.setColor(Theme.getColor("switchTrackChecked"));
                } else {
                    this.paint.setColor(Theme.getColor("switchTrack"));
                }
                canvas.drawCircle((float) cx, (float) cy, (float) (a == ChatUsersActivity.this.selectedSlowmode ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    int width = this.lineSize;
                    if (a == ChatUsersActivity.this.selectedSlowmode || a == ChatUsersActivity.this.selectedSlowmode + 1) {
                        width -= AndroidUtilities.dp(3.0f);
                    }
                    if (a == ChatUsersActivity.this.selectedSlowmode + 1) {
                        x += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (x + width), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                }
                int size = this.sizes.get(a).intValue();
                String text = this.strings.get(a);
                if (a == 0) {
                    canvas.drawText(text, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (a == this.strings.size() - 1) {
                    canvas.drawText(text, (float) ((getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(text, (float) (cx - (size / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                a++;
            }
        }
    }

    public ChatUsersActivity(Bundle args) {
        super(args);
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (!(chat == null || chat.default_banned_rights == null)) {
            this.defaultBannedRights.view_messages = this.currentChat.default_banned_rights.view_messages;
            this.defaultBannedRights.send_stickers = this.currentChat.default_banned_rights.send_stickers;
            this.defaultBannedRights.send_media = this.currentChat.default_banned_rights.send_media;
            this.defaultBannedRights.embed_links = this.currentChat.default_banned_rights.embed_links;
            this.defaultBannedRights.send_messages = this.currentChat.default_banned_rights.send_messages;
            this.defaultBannedRights.send_games = this.currentChat.default_banned_rights.send_games;
            this.defaultBannedRights.send_inline = this.currentChat.default_banned_rights.send_inline;
            this.defaultBannedRights.send_gifs = this.currentChat.default_banned_rights.send_gifs;
            this.defaultBannedRights.pin_messages = this.currentChat.default_banned_rights.pin_messages;
            this.defaultBannedRights.send_polls = this.currentChat.default_banned_rights.send_polls;
            this.defaultBannedRights.invite_users = this.currentChat.default_banned_rights.invite_users;
            this.defaultBannedRights.change_info = this.currentChat.default_banned_rights.change_info;
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        this.isChannel = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
    }

    private void updateRows() {
        boolean z;
        boolean z2;
        TLRPC.ChatFull chatFull;
        boolean z3;
        TLRPC.ChatFull chatFull2;
        boolean z4;
        TLRPC.ChatFull chatFull3;
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat != null) {
            this.recentActionsRow = -1;
            this.addNewRow = -1;
            this.addNew2Row = -1;
            this.addNewSectionRow = -1;
            this.restricted1SectionRow = -1;
            this.participantsStartRow = -1;
            this.participantsDividerRow = -1;
            this.participantsDivider2Row = -1;
            this.gigaInfoRow = -1;
            this.gigaConvertRow = -1;
            this.gigaHeaderRow = -1;
            this.participantsEndRow = -1;
            this.participantsInfoRow = -1;
            this.blockedEmptyRow = -1;
            this.permissionsSectionRow = -1;
            this.sendMessagesRow = -1;
            this.sendMediaRow = -1;
            this.sendStickersRow = -1;
            this.sendPollsRow = -1;
            this.embedLinksRow = -1;
            this.addUsersRow = -1;
            this.pinMessagesRow = -1;
            this.changeInfoRow = -1;
            this.removedUsersRow = -1;
            this.contactsHeaderRow = -1;
            this.contactsStartRow = -1;
            this.contactsEndRow = -1;
            this.botHeaderRow = -1;
            this.botStartRow = -1;
            this.botEndRow = -1;
            this.membersHeaderRow = -1;
            this.slowmodeRow = -1;
            this.slowmodeSelectRow = -1;
            this.slowmodeInfoRow = -1;
            this.loadingProgressRow = -1;
            this.loadingUserCellRow = -1;
            this.loadingHeaderRow = -1;
            int i = 0;
            this.rowCount = 0;
            int i2 = this.type;
            if (i2 == 3) {
                int i3 = 0 + 1;
                this.rowCount = i3;
                this.permissionsSectionRow = 0;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.sendMessagesRow = i3;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.sendMediaRow = i4;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.sendStickersRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.sendPollsRow = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.embedLinksRow = i7;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.addUsersRow = i8;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.pinMessagesRow = i9;
                this.rowCount = i10 + 1;
                this.changeInfoRow = i10;
                if (ChatObject.isChannel(chat) && this.currentChat.creator && this.currentChat.megagroup && !this.currentChat.gigagroup) {
                    int i11 = this.currentChat.participants_count;
                    TLRPC.ChatFull chatFull4 = this.info;
                    if (chatFull4 != null) {
                        i = chatFull4.participants_count;
                    }
                    if (Math.max(i11, i) >= getMessagesController().maxMegagroupCount - 1000) {
                        int i12 = this.rowCount;
                        int i13 = i12 + 1;
                        this.rowCount = i13;
                        this.participantsDivider2Row = i12;
                        int i14 = i13 + 1;
                        this.rowCount = i14;
                        this.gigaHeaderRow = i13;
                        int i15 = i14 + 1;
                        this.rowCount = i15;
                        this.gigaConvertRow = i14;
                        this.rowCount = i15 + 1;
                        this.gigaInfoRow = i15;
                    }
                }
                if ((!ChatObject.isChannel(this.currentChat) && this.currentChat.creator) || (this.currentChat.megagroup && !this.currentChat.gigagroup && ChatObject.canBlockUsers(this.currentChat))) {
                    if (this.participantsDivider2Row == -1) {
                        int i16 = this.rowCount;
                        this.rowCount = i16 + 1;
                        this.participantsDivider2Row = i16;
                    }
                    int i17 = this.rowCount;
                    int i18 = i17 + 1;
                    this.rowCount = i18;
                    this.slowmodeRow = i17;
                    int i19 = i18 + 1;
                    this.rowCount = i19;
                    this.slowmodeSelectRow = i18;
                    this.rowCount = i19 + 1;
                    this.slowmodeInfoRow = i19;
                }
                if (ChatObject.isChannel(this.currentChat)) {
                    if (this.participantsDivider2Row == -1) {
                        int i20 = this.rowCount;
                        this.rowCount = i20 + 1;
                        this.participantsDivider2Row = i20;
                    }
                    int i21 = this.rowCount;
                    this.rowCount = i21 + 1;
                    this.removedUsersRow = i21;
                }
                if ((this.slowmodeInfoRow == -1 && this.gigaHeaderRow == -1) || this.removedUsersRow != -1) {
                    int i22 = this.rowCount;
                    this.rowCount = i22 + 1;
                    this.participantsDividerRow = i22;
                }
                if (ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
                    int i23 = this.rowCount;
                    this.rowCount = i23 + 1;
                    this.addNewRow = i23;
                }
                if (!this.loadingUsers || (z4 = this.firstLoaded)) {
                    if (!this.participants.isEmpty()) {
                        int i24 = this.rowCount;
                        this.participantsStartRow = i24;
                        int size = i24 + this.participants.size();
                        this.rowCount = size;
                        this.participantsEndRow = size;
                    }
                    if (this.addNewRow != -1 || this.participantsStartRow != -1) {
                        int i25 = this.rowCount;
                        this.rowCount = i25 + 1;
                        this.addNewSectionRow = i25;
                    }
                } else if (!z4 && (chatFull3 = this.info) != null && chatFull3.banned_count > 0) {
                    int i26 = this.rowCount;
                    this.rowCount = i26 + 1;
                    this.loadingUserCellRow = i26;
                }
            } else if (i2 == 0) {
                if (ChatObject.canBlockUsers(chat)) {
                    int i27 = this.rowCount;
                    this.rowCount = i27 + 1;
                    this.addNewRow = i27;
                    if (!this.participants.isEmpty() || (this.loadingUsers && !this.firstLoaded && (chatFull2 = this.info) != null && chatFull2.kicked_count > 0)) {
                        int i28 = this.rowCount;
                        this.rowCount = i28 + 1;
                        this.participantsInfoRow = i28;
                    }
                }
                if (!this.loadingUsers || z3) {
                    if (!this.participants.isEmpty()) {
                        int i29 = this.rowCount;
                        int i30 = i29 + 1;
                        this.rowCount = i30;
                        this.restricted1SectionRow = i29;
                        this.participantsStartRow = i30;
                        int size2 = i30 + this.participants.size();
                        this.rowCount = size2;
                        this.participantsEndRow = size2;
                    }
                    if (this.participantsStartRow == -1) {
                        int i31 = this.rowCount;
                        this.rowCount = i31 + 1;
                        this.blockedEmptyRow = i31;
                    } else if (this.participantsInfoRow == -1) {
                        int i32 = this.rowCount;
                        this.rowCount = i32 + 1;
                        this.participantsInfoRow = i32;
                    } else {
                        int i33 = this.rowCount;
                        this.rowCount = i33 + 1;
                        this.addNewSectionRow = i33;
                    }
                } else if (!(z3 = this.firstLoaded)) {
                    int i34 = this.rowCount;
                    int i35 = i34 + 1;
                    this.rowCount = i35;
                    this.restricted1SectionRow = i34;
                    this.rowCount = i35 + 1;
                    this.loadingUserCellRow = i35;
                }
            } else if (i2 == 1) {
                if (ChatObject.isChannel(chat) && this.currentChat.megagroup && !this.currentChat.gigagroup && ((chatFull = this.info) == null || chatFull.participants_count <= 200 || (!this.isChannel && this.info.can_set_stickers))) {
                    int i36 = this.rowCount;
                    int i37 = i36 + 1;
                    this.rowCount = i37;
                    this.recentActionsRow = i36;
                    this.rowCount = i37 + 1;
                    this.addNewSectionRow = i37;
                }
                if (ChatObject.canAddAdmins(this.currentChat)) {
                    int i38 = this.rowCount;
                    this.rowCount = i38 + 1;
                    this.addNewRow = i38;
                }
                if (!this.loadingUsers || z2) {
                    if (!this.participants.isEmpty()) {
                        int i39 = this.rowCount;
                        this.participantsStartRow = i39;
                        int size3 = i39 + this.participants.size();
                        this.rowCount = size3;
                        this.participantsEndRow = size3;
                    }
                    int i40 = this.rowCount;
                    this.rowCount = i40 + 1;
                    this.participantsInfoRow = i40;
                } else if (!(z2 = this.firstLoaded)) {
                    int i41 = this.rowCount;
                    this.rowCount = i41 + 1;
                    this.loadingUserCellRow = i41;
                }
            } else if (i2 == 2) {
                if (this.selectType == 0 && ChatObject.canAddUsers(chat)) {
                    int i42 = this.rowCount;
                    this.rowCount = i42 + 1;
                    this.addNewRow = i42;
                }
                if (this.selectType == 0 && ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
                    int i43 = this.rowCount;
                    this.rowCount = i43 + 1;
                    this.addNew2Row = i43;
                }
                if (!this.loadingUsers || z) {
                    boolean hasAnyOther = false;
                    if (!this.contacts.isEmpty()) {
                        int i44 = this.rowCount;
                        int i45 = i44 + 1;
                        this.rowCount = i45;
                        this.contactsHeaderRow = i44;
                        this.contactsStartRow = i45;
                        int size4 = i45 + this.contacts.size();
                        this.rowCount = size4;
                        this.contactsEndRow = size4;
                        hasAnyOther = true;
                    }
                    if (!this.bots.isEmpty()) {
                        int i46 = this.rowCount;
                        int i47 = i46 + 1;
                        this.rowCount = i47;
                        this.botHeaderRow = i46;
                        this.botStartRow = i47;
                        int size5 = i47 + this.bots.size();
                        this.rowCount = size5;
                        this.botEndRow = size5;
                        hasAnyOther = true;
                    }
                    if (!this.participants.isEmpty()) {
                        if (hasAnyOther) {
                            int i48 = this.rowCount;
                            this.rowCount = i48 + 1;
                            this.membersHeaderRow = i48;
                        }
                        int i49 = this.rowCount;
                        this.participantsStartRow = i49;
                        int size6 = i49 + this.participants.size();
                        this.rowCount = size6;
                        this.participantsEndRow = size6;
                    }
                    int i50 = this.rowCount;
                    if (i50 != 0) {
                        this.rowCount = i50 + 1;
                        this.participantsInfoRow = i50;
                    }
                } else if (!(z = this.firstLoaded)) {
                    if (this.selectType == 0) {
                        int i51 = this.rowCount;
                        this.rowCount = i51 + 1;
                        this.loadingHeaderRow = i51;
                    }
                    int i52 = this.rowCount;
                    this.rowCount = i52 + 1;
                    this.loadingUserCellRow = i52;
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChatParticipants(0, 200);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        int i;
        this.searching = false;
        this.actionBar.setBackButtonImage(NUM);
        int i2 = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i3 = this.type;
        if (i3 == 3) {
            this.actionBar.setTitle(LocaleController.getString("ChannelPermissions", NUM));
        } else if (i3 == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", NUM));
        } else if (i3 == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", NUM));
        } else if (i3 == 2) {
            int i4 = this.selectType;
            if (i4 == 0) {
                if (this.isChannel) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", NUM));
                }
            } else if (i4 == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", NUM));
            } else if (i4 == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", NUM));
            } else if (i4 == 3) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddException", NUM));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatUsersActivity.this.checkDiscard()) {
                        ChatUsersActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatUsersActivity.this.processDone();
                }
            }
        });
        if (this.selectType != 0 || (i = this.type) == 2 || i == 0 || i == 3) {
            this.searchListViewAdapter = new SearchAdapter(context);
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    boolean unused = ChatUsersActivity.this.searching = true;
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(8);
                    }
                }

                public void onSearchCollapse() {
                    ChatUsersActivity.this.searchListViewAdapter.searchUsers((String) null);
                    boolean unused = ChatUsersActivity.this.searching = false;
                    ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                    ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                    ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    ChatUsersActivity.this.listView.setFastScrollVisible(true);
                    ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(0);
                    }
                }

                public void onTextChanged(EditText editText) {
                    if (ChatUsersActivity.this.searchListViewAdapter != null) {
                        String text = editText.getText().toString();
                        int oldItemsCount = ChatUsersActivity.this.listView.getAdapter() == null ? 0 : ChatUsersActivity.this.listView.getAdapter().getItemCount();
                        ChatUsersActivity.this.searchListViewAdapter.searchUsers(text);
                        if (!(!TextUtils.isEmpty(text) || ChatUsersActivity.this.listView == null || ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.listViewAdapter)) {
                            ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                            ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                            if (oldItemsCount == 0) {
                                ChatUsersActivity.this.showItemsAnimated(0);
                            }
                        }
                        ChatUsersActivity.this.progressBar.setVisibility(8);
                        ChatUsersActivity.this.flickerLoadingView.setVisibility(0);
                    }
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            if (this.type == 0 && !this.firstLoaded) {
                actionBarMenuItemSearchListener.setVisibility(8);
            }
            if (this.type == 3) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("ChannelSearchException", NUM));
            } else {
                this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
            }
            if (!ChatObject.isChannel(this.currentChat) && !this.currentChat.creator) {
                this.searchItem.setVisibility(8);
            }
            if (this.type == 3) {
                this.doneItem = menu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), (CharSequence) LocaleController.getString("Done", NUM));
            }
        }
        this.fragmentView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                canvas.drawColor(Theme.getColor(ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter ? "windowBackgroundWhite" : "windowBackgroundGray"));
                super.dispatchDraw(canvas);
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        FrameLayout progressLayout = new FrameLayout(context);
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        progressLayout.addView(this.flickerLoadingView);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        progressLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        this.flickerLoadingView.setVisibility(8);
        this.progressBar.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, progressLayout, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.title.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.addView(progressLayout, 0);
        AnonymousClass4 r5 = new RecyclerListView(context) {
            public void invalidate() {
                super.invalidate();
                if (ChatUsersActivity.this.fragmentView != null) {
                    ChatUsersActivity.this.fragmentView.invalidate();
                }
            }
        };
        this.listView = r5;
        AnonymousClass5 r8 = new LinearLayoutManager(context, 1, false) {
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (!ChatUsersActivity.this.firstLoaded && ChatUsersActivity.this.type == 0 && ChatUsersActivity.this.participants.size() == 0) {
                    return 0;
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        this.layoutManager = r8;
        r5.setLayoutManager(r8);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator() {
            int animationIndex = -1;

            /* access modifiers changed from: protected */
            public long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return 0;
            }

            /* access modifiers changed from: protected */
            public long getMoveAnimationDelay() {
                return 0;
            }

            public long getMoveDuration() {
                return 220;
            }

            public long getRemoveDuration() {
                return 220;
            }

            public long getAddDuration() {
                return 220;
            }

            /* access modifiers changed from: protected */
            public void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                ChatUsersActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
            }

            public void runPendingAnimations() {
                boolean removalsPending = !this.mPendingRemovals.isEmpty();
                boolean movesPending = !this.mPendingMoves.isEmpty();
                boolean changesPending = !this.mPendingChanges.isEmpty();
                boolean additionsPending = !this.mPendingAdditions.isEmpty();
                if (removalsPending || movesPending || additionsPending || changesPending) {
                    this.animationIndex = ChatUsersActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
                }
                super.runPendingAnimations();
            }
        };
        this.listView.setItemAnimator(itemAnimator);
        itemAnimator.setSupportsChangeAnimations(false);
        this.listView.setAnimateEmptyView(true, 0);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatUsersActivity$$ExternalSyntheticLambda8(this, context));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ChatUsersActivity$$ExternalSyntheticLambda9(this));
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                }
            });
        }
        UndoView undoView2 = new UndoView(context);
        this.undoView = undoView2;
        frameLayout.addView(undoView2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(false, 0);
        if (this.needOpenSearch) {
            this.searchItem.openSearch(false);
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3305lambda$createView$1$orgtelegramuiChatUsersActivity(Context context, View view, int position) {
        boolean canEditAdmin;
        String rank;
        TLRPC.TL_chatAdminRights adminRights;
        TLRPC.TL_chatBannedRights bannedRights;
        TLObject participant;
        long peerId;
        long peerId2;
        int i;
        int i2;
        long peerId3;
        boolean z;
        TLObject participant2;
        int i3 = position;
        boolean listAdapter = this.listView.getAdapter() == this.listViewAdapter;
        int i4 = 3;
        if (!listAdapter) {
            Context context2 = context;
        } else if (i3 == this.addNewRow) {
            int i5 = this.type;
            if (i5 == 0) {
                Context context3 = context;
            } else if (i5 == 3) {
                Context context4 = context;
            } else if (i5 == 1) {
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", this.chatId);
                bundle.putInt("type", 2);
                bundle.putInt("selectType", 1);
                ChatUsersActivity fragment = new ChatUsersActivity(bundle);
                fragment.setDelegate(new ChatUsersActivityDelegate() {
                    public /* synthetic */ void didKickParticipant(long j) {
                        ChatUsersActivityDelegate.CC.$default$didKickParticipant(this, j);
                    }

                    public void didAddParticipantToList(long uid, TLObject participant) {
                        if (participant != null && ChatUsersActivity.this.participantsMap.get(uid) == null) {
                            DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                            ChatUsersActivity.this.participants.add(participant);
                            ChatUsersActivity.this.participantsMap.put(uid, participant);
                            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                            chatUsersActivity.sortAdmins(chatUsersActivity.participants);
                            ChatUsersActivity.this.updateListAnimated(diffCallback);
                        }
                    }

                    public void didChangeOwner(TLRPC.User user) {
                        ChatUsersActivity.this.onOwnerChaged(user);
                    }

                    public void didSelectUser(long uid) {
                        TLRPC.User user = ChatUsersActivity.this.getMessagesController().getUser(Long.valueOf(uid));
                        if (user != null) {
                            AndroidUtilities.runOnUIThread(new ChatUsersActivity$8$$ExternalSyntheticLambda0(this, user), 200);
                        }
                        if (ChatUsersActivity.this.participantsMap.get(uid) == null) {
                            DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                            TLRPC.TL_channelParticipantAdmin chatParticipant = new TLRPC.TL_channelParticipantAdmin();
                            chatParticipant.peer = new TLRPC.TL_peerUser();
                            chatParticipant.peer.user_id = user.id;
                            chatParticipant.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                            chatParticipant.promoted_by = ChatUsersActivity.this.getAccountInstance().getUserConfig().clientUserId;
                            ChatUsersActivity.this.participants.add(chatParticipant);
                            ChatUsersActivity.this.participantsMap.put(user.id, chatParticipant);
                            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                            chatUsersActivity.sortAdmins(chatUsersActivity.participants);
                            ChatUsersActivity.this.updateListAnimated(diffCallback);
                        }
                    }

                    /* renamed from: lambda$didSelectUser$0$org-telegram-ui-ChatUsersActivity$8  reason: not valid java name */
                    public /* synthetic */ void m3315lambda$didSelectUser$0$orgtelegramuiChatUsersActivity$8(TLRPC.User user) {
                        if (BulletinFactory.canShowBulletin(ChatUsersActivity.this)) {
                            BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, user.first_name).show();
                        }
                    }
                });
                fragment.setInfo(this.info);
                presentFragment(fragment);
                Context context5 = context;
                return;
            } else if (i5 == 2) {
                Bundle args = new Bundle();
                args.putBoolean("addToGroup", true);
                args.putLong(this.isChannel ? "channelId" : "chatId", this.currentChat.id);
                GroupCreateActivity fragment2 = new GroupCreateActivity(args);
                fragment2.setInfo(this.info);
                LongSparseArray<TLObject> longSparseArray = this.contactsMap;
                fragment2.setIgnoreUsers((longSparseArray == null || longSparseArray.size() == 0) ? this.participantsMap : this.contactsMap);
                final Context context6 = context;
                fragment2.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new GroupCreateActivity.ContactsAddActivityDelegate() {
                    public void didSelectUsers(ArrayList<TLRPC.User> users, int fwdCount) {
                        int count = users.size();
                        ArrayList userRestrictedPrivacy = new ArrayList();
                        int[] processed = {0};
                        Runnable showUserRestrictedPrivacyAlert = new ChatUsersActivity$9$$ExternalSyntheticLambda0(userRestrictedPrivacy, count, context6);
                        int a = 0;
                        while (a < count) {
                            TLRPC.User user = users.get(a);
                            ArrayList userRestrictedPrivacy2 = userRestrictedPrivacy;
                            TLRPC.User user2 = user;
                            ChatUsersActivity.this.getMessagesController().addUserToChat(ChatUsersActivity.this.chatId, user2, fwdCount, (String) null, ChatUsersActivity.this, false, new ChatUsersActivity$9$$ExternalSyntheticLambda1(this, processed, count, userRestrictedPrivacy2, showUserRestrictedPrivacyAlert, user), new ChatUsersActivity$9$$ExternalSyntheticLambda2(processed, userRestrictedPrivacy, user2, count, showUserRestrictedPrivacyAlert));
                            ChatUsersActivity.this.getMessagesController().putUser(user2, false);
                            a++;
                            userRestrictedPrivacy = userRestrictedPrivacy2;
                        }
                    }

                    static /* synthetic */ void lambda$didSelectUsers$0(ArrayList userRestrictedPrivacy, int count, Context context) {
                        CharSequence description;
                        CharSequence title;
                        if (userRestrictedPrivacy.size() == 1) {
                            if (count > 1) {
                                title = LocaleController.getString("InviteToGroupErrorTitleAUser", NUM);
                            } else {
                                title = LocaleController.getString("InviteToGroupErrorTitleThisUser", NUM);
                            }
                            description = AndroidUtilities.replaceTags(LocaleController.formatString("InviteToGroupErrorMessageSingle", NUM, UserObject.getFirstName((TLRPC.User) userRestrictedPrivacy.get(0))));
                        } else if (userRestrictedPrivacy.size() == 2) {
                            title = LocaleController.getString("InviteToGroupErrorTitleSomeUsers", NUM);
                            description = AndroidUtilities.replaceTags(LocaleController.formatString("InviteToGroupErrorMessageDouble", NUM, UserObject.getFirstName((TLRPC.User) userRestrictedPrivacy.get(0)), UserObject.getFirstName((TLRPC.User) userRestrictedPrivacy.get(1))));
                        } else if (userRestrictedPrivacy.size() == count) {
                            title = LocaleController.getString("InviteToGroupErrorTitleTheseUsers", NUM);
                            description = LocaleController.getString("InviteToGroupErrorMessageMultipleAll", NUM);
                        } else {
                            title = LocaleController.getString("InviteToGroupErrorTitleSomeUsers", NUM);
                            description = LocaleController.getString("InviteToGroupErrorMessageMultipleSome", NUM);
                        }
                        new AlertDialog.Builder(context).setTitle(title).setMessage(description).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
                    }

                    /* renamed from: lambda$didSelectUsers$1$org-telegram-ui-ChatUsersActivity$9  reason: not valid java name */
                    public /* synthetic */ void m3316lambda$didSelectUsers$1$orgtelegramuiChatUsersActivity$9(int[] processed, int count, ArrayList userRestrictedPrivacy, Runnable showUserRestrictedPrivacyAlert, TLRPC.User user) {
                        processed[0] = processed[0] + 1;
                        if (processed[0] >= count && userRestrictedPrivacy.size() > 0) {
                            showUserRestrictedPrivacyAlert.run();
                        }
                        DiffCallback savedState = ChatUsersActivity.this.saveState();
                        ArrayList<TLObject> array = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participants : ChatUsersActivity.this.contacts;
                        LongSparseArray<TLObject> map = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participantsMap : ChatUsersActivity.this.contactsMap;
                        if (map.get(user.id) == null) {
                            if (ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                                TLRPC.TL_channelParticipant channelParticipant1 = new TLRPC.TL_channelParticipant();
                                channelParticipant1.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                channelParticipant1.peer = new TLRPC.TL_peerUser();
                                channelParticipant1.peer.user_id = user.id;
                                channelParticipant1.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                                array.add(0, channelParticipant1);
                                map.put(user.id, channelParticipant1);
                            } else {
                                TLRPC.ChatParticipant participant = new TLRPC.TL_chatParticipant();
                                participant.user_id = user.id;
                                participant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                array.add(0, participant);
                                map.put(user.id, participant);
                            }
                        }
                        if (array == ChatUsersActivity.this.participants) {
                            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                            chatUsersActivity.sortAdmins(chatUsersActivity.participants);
                        }
                        ChatUsersActivity.this.updateListAnimated(savedState);
                    }

                    static /* synthetic */ boolean lambda$didSelectUsers$2(int[] processed, ArrayList userRestrictedPrivacy, TLRPC.User user, int count, Runnable showUserRestrictedPrivacyAlert, TLRPC.TL_error err) {
                        processed[0] = processed[0] + 1;
                        boolean z = err != null && "USER_PRIVACY_RESTRICTED".equals(err.text);
                        boolean privacyRestricted = z;
                        if (z) {
                            userRestrictedPrivacy.add(user);
                        }
                        if (processed[0] >= count && userRestrictedPrivacy.size() > 0) {
                            showUserRestrictedPrivacyAlert.run();
                        }
                        if (!privacyRestricted) {
                            return true;
                        }
                        return false;
                    }

                    public void needAddBot(TLRPC.User user) {
                        ChatUsersActivity.this.openRightsEdit(user.id, (TLObject) null, (TLRPC.TL_chatAdminRights) null, (TLRPC.TL_chatBannedRights) null, "", true, 0, false);
                    }
                });
                presentFragment(fragment2);
                return;
            } else {
                Context context7 = context;
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putLong("chat_id", this.chatId);
            bundle2.putInt("type", 2);
            if (this.type == 0) {
                i4 = 2;
            }
            bundle2.putInt("selectType", i4);
            ChatUsersActivity fragment3 = new ChatUsersActivity(bundle2);
            fragment3.setInfo(this.info);
            fragment3.setDelegate(new ChatUsersActivityDelegate() {
                public /* synthetic */ void didChangeOwner(TLRPC.User user) {
                    ChatUsersActivityDelegate.CC.$default$didChangeOwner(this, user);
                }

                public /* synthetic */ void didSelectUser(long j) {
                    ChatUsersActivityDelegate.CC.$default$didSelectUser(this, j);
                }

                public void didAddParticipantToList(long uid, TLObject participant) {
                    if (ChatUsersActivity.this.participantsMap.get(uid) == null) {
                        DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                        ChatUsersActivity.this.participants.add(participant);
                        ChatUsersActivity.this.participantsMap.put(uid, participant);
                        ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                        chatUsersActivity.sortUsers(chatUsersActivity.participants);
                        ChatUsersActivity.this.updateListAnimated(diffCallback);
                    }
                }

                public void didKickParticipant(long uid) {
                    if (ChatUsersActivity.this.participantsMap.get(uid) == null) {
                        DiffCallback diffCallback = ChatUsersActivity.this.saveState();
                        TLRPC.TL_channelParticipantBanned chatParticipant = new TLRPC.TL_channelParticipantBanned();
                        if (uid > 0) {
                            chatParticipant.peer = new TLRPC.TL_peerUser();
                            chatParticipant.peer.user_id = uid;
                        } else {
                            chatParticipant.peer = new TLRPC.TL_peerChannel();
                            chatParticipant.peer.channel_id = -uid;
                        }
                        chatParticipant.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                        chatParticipant.kicked_by = ChatUsersActivity.this.getAccountInstance().getUserConfig().clientUserId;
                        ChatUsersActivity.this.info.kicked_count++;
                        ChatUsersActivity.this.participants.add(chatParticipant);
                        ChatUsersActivity.this.participantsMap.put(uid, chatParticipant);
                        ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                        chatUsersActivity.sortUsers(chatUsersActivity.participants);
                        ChatUsersActivity.this.updateListAnimated(diffCallback);
                    }
                }
            });
            presentFragment(fragment3);
            return;
        } else {
            Context context8 = context;
            if (i3 == this.recentActionsRow) {
                presentFragment(new ChannelAdminLogActivity(this.currentChat));
                return;
            } else if (i3 == this.removedUsersRow) {
                Bundle args2 = new Bundle();
                args2.putLong("chat_id", this.chatId);
                args2.putInt("type", 0);
                ChatUsersActivity fragment4 = new ChatUsersActivity(args2);
                fragment4.setInfo(this.info);
                presentFragment(fragment4);
                return;
            } else if (i3 == this.gigaConvertRow) {
                showDialog(new GigagroupConvertAlert(getParentActivity(), this) {
                    /* access modifiers changed from: protected */
                    public void onCovert() {
                        ChatUsersActivity.this.getMessagesController().convertToGigaGroup(ChatUsersActivity.this.getParentActivity(), ChatUsersActivity.this.currentChat, ChatUsersActivity.this, new ChatUsersActivity$10$$ExternalSyntheticLambda0(this));
                    }

                    /* renamed from: lambda$onCovert$0$org-telegram-ui-ChatUsersActivity$10  reason: not valid java name */
                    public /* synthetic */ void m3314lambda$onCovert$0$orgtelegramuiChatUsersActivity$10(boolean result) {
                        if (result && ChatUsersActivity.this.parentLayout != null) {
                            BaseFragment editActivity = ChatUsersActivity.this.parentLayout.fragmentsStack.get(ChatUsersActivity.this.parentLayout.fragmentsStack.size() - 2);
                            if (editActivity instanceof ChatEditActivity) {
                                editActivity.removeSelfFromStack();
                                Bundle args = new Bundle();
                                args.putLong("chat_id", ChatUsersActivity.this.chatId);
                                ChatEditActivity fragment = new ChatEditActivity(args);
                                fragment.setInfo(ChatUsersActivity.this.info);
                                ChatUsersActivity.this.parentLayout.addFragmentToStack(fragment, ChatUsersActivity.this.parentLayout.fragmentsStack.size() - 1);
                                ChatUsersActivity.this.finishFragment();
                                fragment.showConvertTooltip();
                                return;
                            }
                            ChatUsersActivity.this.finishFragment();
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onCancel() {
                    }
                });
            } else if (i3 == this.addNew2Row) {
                if (this.info != null) {
                    ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.chatId, 0, 0);
                    TLRPC.ChatFull chatFull = this.info;
                    manageLinksActivity.setInfo(chatFull, chatFull.exported_invite);
                    presentFragment(manageLinksActivity);
                    return;
                }
                return;
            } else if (i3 > this.permissionsSectionRow && i3 <= this.changeInfoRow) {
                TextCheckCell2 checkCell = (TextCheckCell2) view;
                if (checkCell.isEnabled()) {
                    if (!checkCell.hasIcon()) {
                        checkCell.setChecked(!checkCell.isChecked());
                        if (i3 == this.changeInfoRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights = this.defaultBannedRights;
                            tL_chatBannedRights.change_info = !tL_chatBannedRights.change_info;
                            return;
                        } else if (i3 == this.addUsersRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.defaultBannedRights;
                            tL_chatBannedRights2.invite_users = !tL_chatBannedRights2.invite_users;
                            return;
                        } else if (i3 == this.pinMessagesRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights3 = this.defaultBannedRights;
                            tL_chatBannedRights3.pin_messages = !tL_chatBannedRights3.pin_messages;
                            return;
                        } else {
                            boolean disabled = !checkCell.isChecked();
                            if (i3 == this.sendMessagesRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights4 = this.defaultBannedRights;
                                tL_chatBannedRights4.send_messages = !tL_chatBannedRights4.send_messages;
                            } else if (i3 == this.sendMediaRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights5 = this.defaultBannedRights;
                                tL_chatBannedRights5.send_media = !tL_chatBannedRights5.send_media;
                            } else if (i3 == this.sendStickersRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights6 = this.defaultBannedRights;
                                boolean z2 = !tL_chatBannedRights6.send_stickers;
                                tL_chatBannedRights6.send_inline = z2;
                                tL_chatBannedRights6.send_gifs = z2;
                                tL_chatBannedRights6.send_games = z2;
                                tL_chatBannedRights6.send_stickers = z2;
                            } else if (i3 == this.embedLinksRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights7 = this.defaultBannedRights;
                                tL_chatBannedRights7.embed_links = !tL_chatBannedRights7.embed_links;
                            } else if (i3 == this.sendPollsRow) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights8 = this.defaultBannedRights;
                                tL_chatBannedRights8.send_polls = !tL_chatBannedRights8.send_polls;
                            }
                            if (disabled) {
                                if (this.defaultBannedRights.view_messages && !this.defaultBannedRights.send_messages) {
                                    this.defaultBannedRights.send_messages = true;
                                    RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                    if (holder != null) {
                                        ((TextCheckCell2) holder.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_media) {
                                    this.defaultBannedRights.send_media = true;
                                    RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                                    if (holder2 != null) {
                                        ((TextCheckCell2) holder2.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_polls) {
                                    this.defaultBannedRights.send_polls = true;
                                    RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                                    if (holder3 != null) {
                                        ((TextCheckCell2) holder3.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_stickers) {
                                    TLRPC.TL_chatBannedRights tL_chatBannedRights9 = this.defaultBannedRights;
                                    tL_chatBannedRights9.send_inline = true;
                                    tL_chatBannedRights9.send_gifs = true;
                                    tL_chatBannedRights9.send_games = true;
                                    tL_chatBannedRights9.send_stickers = true;
                                    RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                                    if (holder4 != null) {
                                        ((TextCheckCell2) holder4.itemView).setChecked(false);
                                    }
                                }
                                if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.embed_links) {
                                    this.defaultBannedRights.embed_links = true;
                                    RecyclerView.ViewHolder holder5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                                    if (holder5 != null) {
                                        ((TextCheckCell2) holder5.itemView).setChecked(false);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            } else if ((!this.defaultBannedRights.embed_links || !this.defaultBannedRights.send_inline || !this.defaultBannedRights.send_media || !this.defaultBannedRights.send_polls) && this.defaultBannedRights.send_messages) {
                                this.defaultBannedRights.send_messages = false;
                                RecyclerView.ViewHolder holder6 = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder6 != null) {
                                    ((TextCheckCell2) holder6.itemView).setChecked(true);
                                    return;
                                }
                                return;
                            } else {
                                return;
                            }
                        }
                    } else if (TextUtils.isEmpty(this.currentChat.username) || !(i3 == this.pinMessagesRow || i3 == this.changeInfoRow)) {
                        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("EditCantEditPermissions", NUM)).show();
                        return;
                    } else {
                        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("EditCantEditPermissionsPublic", NUM)).show();
                        return;
                    }
                } else {
                    return;
                }
            }
        }
        TLRPC.TL_chatBannedRights bannedRights2 = null;
        TLRPC.TL_chatAdminRights adminRights2 = null;
        String rank2 = "";
        long peerId4 = 0;
        boolean canEditAdmin2 = false;
        if (listAdapter) {
            TLObject participant3 = this.listViewAdapter.getItem(i3);
            if (participant3 instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) participant3;
                peerId4 = MessageObject.getPeerId(channelParticipant.peer);
                bannedRights2 = channelParticipant.banned_rights;
                TLRPC.TL_chatAdminRights adminRights3 = channelParticipant.admin_rights;
                rank2 = channelParticipant.rank;
                canEditAdmin2 = (!(channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant.can_edit;
                if (participant3 instanceof TLRPC.TL_channelParticipantCreator) {
                    TLRPC.TL_chatAdminRights adminRights4 = ((TLRPC.TL_channelParticipantCreator) participant3).admin_rights;
                    if (adminRights4 == null) {
                        TLRPC.TL_chatAdminRights adminRights5 = new TLRPC.TL_chatAdminRights();
                        adminRights5.add_admins = true;
                        adminRights5.pin_messages = true;
                        adminRights5.invite_users = true;
                        adminRights5.ban_users = true;
                        adminRights5.delete_messages = true;
                        adminRights5.edit_messages = true;
                        adminRights5.post_messages = true;
                        adminRights5.change_info = true;
                        if (!this.isChannel) {
                            adminRights5.manage_call = true;
                        }
                        adminRights2 = adminRights5;
                    } else {
                        adminRights2 = adminRights4;
                    }
                } else {
                    adminRights2 = adminRights3;
                }
            } else if (participant3 instanceof TLRPC.ChatParticipant) {
                long peerId5 = ((TLRPC.ChatParticipant) participant3).user_id;
                boolean canEditAdmin3 = this.currentChat.creator;
                if (participant3 instanceof TLRPC.TL_chatParticipantCreator) {
                    adminRights2 = new TLRPC.TL_chatAdminRights();
                    adminRights2.add_admins = true;
                    adminRights2.pin_messages = true;
                    adminRights2.invite_users = true;
                    adminRights2.ban_users = true;
                    adminRights2.delete_messages = true;
                    adminRights2.edit_messages = true;
                    adminRights2.post_messages = true;
                    adminRights2.change_info = true;
                    if (!this.isChannel) {
                        adminRights2.manage_call = true;
                    }
                }
                canEditAdmin = canEditAdmin3;
                bannedRights = null;
                adminRights = adminRights2;
                rank = rank2;
                peerId = peerId5;
                participant = participant3;
            }
            adminRights = adminRights2;
            rank = rank2;
            peerId = peerId4;
            canEditAdmin = canEditAdmin2;
            participant = participant3;
            bannedRights = bannedRights2;
        } else {
            TLObject object = this.searchListViewAdapter.getItem(i3);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                getMessagesController().putUser(user, false);
                long j = user.id;
                peerId4 = j;
                participant2 = getAnyParticipant(j);
            } else if ((object instanceof TLRPC.ChannelParticipant) || (object instanceof TLRPC.ChatParticipant)) {
                participant2 = object;
            } else {
                participant2 = null;
            }
            if (participant2 instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant2 = (TLRPC.ChannelParticipant) participant2;
                long peerId6 = MessageObject.getPeerId(channelParticipant2.peer);
                boolean canEditAdmin4 = (!(channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant2.can_edit;
                TLRPC.TL_chatBannedRights bannedRights3 = channelParticipant2.banned_rights;
                adminRights = channelParticipant2.admin_rights;
                rank = channelParticipant2.rank;
                peerId = peerId6;
                canEditAdmin = canEditAdmin4;
                participant = participant2;
                bannedRights = bannedRights3;
            } else if (participant2 instanceof TLRPC.ChatParticipant) {
                long peerId7 = ((TLRPC.ChatParticipant) participant2).user_id;
                canEditAdmin = this.currentChat.creator;
                bannedRights = null;
                adminRights = null;
                rank = rank2;
                peerId = peerId7;
                participant = participant2;
            } else if (participant2 == null) {
                adminRights = null;
                rank = rank2;
                peerId = peerId4;
                canEditAdmin = true;
                participant = participant2;
                bannedRights = null;
            } else {
                adminRights = null;
                rank = rank2;
                peerId = peerId4;
                canEditAdmin = false;
                participant = participant2;
                bannedRights = null;
            }
        }
        if (peerId != 0) {
            int i6 = this.selectType;
            if (i6 != 0) {
                if (i6 != 3) {
                    i2 = 1;
                    if (i6 != 1) {
                        removeParticipant(peerId);
                        TLObject tLObject = participant;
                        boolean z3 = listAdapter;
                        long j2 = peerId;
                        return;
                    }
                } else {
                    i2 = 1;
                }
                if (i6 == i2 || !canEditAdmin) {
                    peerId3 = peerId;
                    boolean z4 = listAdapter;
                    z = false;
                } else if ((participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(peerId));
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    long peerId8 = peerId;
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, UserObject.getUserName(user2)));
                    AlertDialog.Builder builder2 = builder;
                    boolean z5 = listAdapter;
                    ChatUsersActivity$$ExternalSyntheticLambda13 chatUsersActivity$$ExternalSyntheticLambda13 = r0;
                    TLRPC.User user3 = user2;
                    TLRPC.User user4 = user2;
                    String string = LocaleController.getString("OK", NUM);
                    ChatUsersActivity$$ExternalSyntheticLambda13 chatUsersActivity$$ExternalSyntheticLambda132 = new ChatUsersActivity$$ExternalSyntheticLambda13(this, user3, participant, adminRights, bannedRights, rank, canEditAdmin);
                    builder2.setPositiveButton(string, chatUsersActivity$$ExternalSyntheticLambda13);
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder2.create());
                    TLObject tLObject2 = participant;
                    long j3 = peerId8;
                    return;
                } else {
                    peerId3 = peerId;
                    boolean z6 = listAdapter;
                    z = false;
                }
                int i7 = i6 == 1 ? 0 : 1;
                if (i6 == 1 || i6 == 3) {
                    z = true;
                }
                TLObject tLObject3 = participant;
                openRightsEdit(peerId3, participant, adminRights, bannedRights, rank, canEditAdmin, i7, z);
                long j4 = peerId3;
                return;
            }
            long peerId9 = peerId;
            final TLObject participant4 = participant;
            boolean z7 = listAdapter;
            boolean canEdit = false;
            int i8 = this.type;
            if (i8 == 1) {
                peerId2 = peerId9;
                canEdit = peerId2 != getUserConfig().getClientUserId() && (this.currentChat.creator || canEditAdmin);
            } else {
                peerId2 = peerId9;
                if (i8 == 0 || i8 == 3) {
                    canEdit = ChatObject.canBlockUsers(this.currentChat);
                }
            }
            int i9 = this.type;
            if (i9 != 0 && ((i9 == 1 || !this.isChannel) && (i9 != 2 || this.selectType != 0))) {
                if (bannedRights == null) {
                    TLRPC.TL_chatBannedRights bannedRights4 = new TLRPC.TL_chatBannedRights();
                    i = 1;
                    bannedRights4.view_messages = true;
                    bannedRights4.send_stickers = true;
                    bannedRights4.send_media = true;
                    bannedRights4.embed_links = true;
                    bannedRights4.send_messages = true;
                    bannedRights4.send_games = true;
                    bannedRights4.send_inline = true;
                    bannedRights4.send_gifs = true;
                    bannedRights4.pin_messages = true;
                    bannedRights4.send_polls = true;
                    bannedRights4.invite_users = true;
                    bannedRights4.change_info = true;
                    bannedRights = bannedRights4;
                } else {
                    i = 1;
                }
                ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(peerId2, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, this.type == i ? 0 : 1, canEdit, participant4 == null, (String) null);
                chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                        TLObject tLObject = participant4;
                        if (tLObject instanceof TLRPC.ChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                            channelParticipant.admin_rights = rightsAdmin;
                            channelParticipant.banned_rights = rightsBanned;
                            channelParticipant.rank = rank;
                            ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0, false);
                        }
                    }

                    public void didChangeOwner(TLRPC.User user) {
                        ChatUsersActivity.this.onOwnerChaged(user);
                    }
                });
                presentFragment(chatRightsEditActivity);
            } else if (peerId2 != getUserConfig().getClientUserId()) {
                Bundle args3 = new Bundle();
                if (peerId2 > 0) {
                    args3.putLong("user_id", peerId2);
                } else {
                    args3.putLong("chat_id", -peerId2);
                }
                presentFragment(new ProfileActivity(args3));
            }
        } else {
            boolean z8 = listAdapter;
            long j5 = peerId;
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3304lambda$createView$0$orgtelegramuiChatUsersActivity(TLRPC.User user, TLObject participant, TLRPC.TL_chatAdminRights ar, TLRPC.TL_chatBannedRights br, String rankFinal, boolean canEdit, DialogInterface dialog, int which) {
        openRightsEdit(user.id, participant, ar, br, rankFinal, canEdit, this.selectType == 1 ? 0 : 1, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r0 = r3.listView.getAdapter();
        r2 = r3.listViewAdapter;
     */
    /* renamed from: lambda$createView$2$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ boolean m3306lambda$createView$2$orgtelegramuiChatUsersActivity(android.view.View r4, int r5) {
        /*
            r3 = this;
            android.app.Activity r0 = r3.getParentActivity()
            r1 = 0
            if (r0 == 0) goto L_0x001c
            org.telegram.ui.Components.RecyclerListView r0 = r3.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.ChatUsersActivity$ListAdapter r2 = r3.listViewAdapter
            if (r0 != r2) goto L_0x001c
            org.telegram.tgnet.TLObject r0 = r2.getItem(r5)
            boolean r0 = r3.createMenuForParticipant(r0, r1)
            if (r0 == 0) goto L_0x001c
            r1 = 1
        L_0x001c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.m3306lambda$createView$2$orgtelegramuiChatUsersActivity(android.view.View, int):boolean");
    }

    /* access modifiers changed from: private */
    public void sortAdmins(ArrayList<TLObject> participants2) {
        Collections.sort(participants2, new ChatUsersActivity$$ExternalSyntheticLambda2(this));
    }

    /* renamed from: lambda$sortAdmins$3$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ int m3312lambda$sortAdmins$3$orgtelegramuiChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        if (!(lhs instanceof TLRPC.ChannelParticipant) || !(rhs instanceof TLRPC.ChannelParticipant)) {
            return 0;
        }
        return (int) (MessageObject.getPeerId(((TLRPC.ChannelParticipant) lhs).peer) - MessageObject.getPeerId(((TLRPC.ChannelParticipant) rhs).peer));
    }

    /* access modifiers changed from: private */
    public void showItemsAnimated(int from) {
        if (!this.isPaused && this.openTransitionStarted) {
            if (this.listView.getAdapter() != this.listViewAdapter || !this.firstLoaded) {
                View progressView = null;
                for (int i = 0; i < this.listView.getChildCount(); i++) {
                    View child = this.listView.getChildAt(i);
                    if (child instanceof FlickerLoadingView) {
                        progressView = child;
                    }
                }
                final View finalProgressView = progressView;
                if (progressView != null) {
                    this.listView.removeView(progressView);
                    from--;
                }
                final int finalFrom = from;
                this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        ChatUsersActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int n = ChatUsersActivity.this.listView.getChildCount();
                        AnimatorSet animatorSet = new AnimatorSet();
                        for (int i = 0; i < n; i++) {
                            View child = ChatUsersActivity.this.listView.getChildAt(i);
                            if (child != finalProgressView && ChatUsersActivity.this.listView.getChildAdapterPosition(child) >= finalFrom) {
                                child.setAlpha(0.0f);
                                ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                                a.setStartDelay((long) ((int) ((((float) Math.min(ChatUsersActivity.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) ChatUsersActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                                a.setDuration(200);
                                animatorSet.playTogether(new Animator[]{a});
                            }
                        }
                        View view = finalProgressView;
                        if (view != null && view.getParent() == null) {
                            ChatUsersActivity.this.listView.addView(finalProgressView);
                            final RecyclerView.LayoutManager layoutManager = ChatUsersActivity.this.listView.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.ignoreView(finalProgressView);
                                Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, new float[]{finalProgressView.getAlpha(), 0.0f});
                                animator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        finalProgressView.setAlpha(1.0f);
                                        layoutManager.stopIgnoringView(finalProgressView);
                                        ChatUsersActivity.this.listView.removeView(finalProgressView);
                                    }
                                });
                                animator.start();
                            }
                        }
                        animatorSet.start();
                        return true;
                    }
                });
            }
        }
    }

    public void setIgnoresUsers(LongSparseArray<TLRPC.TL_groupCallParticipant> participants2) {
        this.ignoredUsers = participants2;
    }

    /* access modifiers changed from: private */
    public void onOwnerChaged(TLRPC.User user) {
        TLRPC.User user2;
        ArrayList<TLObject> arrayList;
        LongSparseArray<TLObject> map;
        int a;
        boolean foundAny;
        TLRPC.User user3 = user;
        this.undoView.showWithAction(-this.chatId, this.isChannel ? 9 : 10, (Object) user3);
        boolean foundAny2 = false;
        this.currentChat.creator = false;
        int a2 = 0;
        while (a2 < 3) {
            boolean found = false;
            if (a2 == 0) {
                map = this.contactsMap;
                arrayList = this.contacts;
            } else if (a2 == 1) {
                map = this.botsMap;
                arrayList = this.bots;
            } else {
                map = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject object = map.get(user3.id);
            if (object instanceof TLRPC.ChannelParticipant) {
                TLRPC.TL_channelParticipantCreator creator = new TLRPC.TL_channelParticipantCreator();
                creator.peer = new TLRPC.TL_peerUser();
                creator.peer.user_id = user3.id;
                map.put(user3.id, creator);
                int index = arrayList.indexOf(object);
                if (index >= 0) {
                    arrayList.set(index, creator);
                }
                found = true;
                foundAny2 = true;
            }
            long selfUserId = getUserConfig().getClientUserId();
            TLObject object2 = map.get(selfUserId);
            if (object2 instanceof TLRPC.ChannelParticipant) {
                TLRPC.TL_channelParticipantAdmin admin = new TLRPC.TL_channelParticipantAdmin();
                admin.peer = new TLRPC.TL_peerUser();
                admin.peer.user_id = selfUserId;
                admin.self = true;
                admin.inviter_id = selfUserId;
                admin.promoted_by = selfUserId;
                admin.date = (int) (System.currentTimeMillis() / 1000);
                admin.admin_rights = new TLRPC.TL_chatAdminRights();
                TLRPC.TL_chatAdminRights tL_chatAdminRights = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights2 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights3 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights4 = admin.admin_rights;
                TLRPC.TL_chatAdminRights tL_chatAdminRights5 = admin.admin_rights;
                foundAny = foundAny2;
                TLRPC.TL_chatAdminRights tL_chatAdminRights6 = admin.admin_rights;
                boolean z = found;
                TLRPC.TL_chatAdminRights tL_chatAdminRights7 = admin.admin_rights;
                a = a2;
                admin.admin_rights.add_admins = true;
                tL_chatAdminRights7.pin_messages = true;
                tL_chatAdminRights6.invite_users = true;
                tL_chatAdminRights5.ban_users = true;
                tL_chatAdminRights4.delete_messages = true;
                tL_chatAdminRights3.edit_messages = true;
                tL_chatAdminRights2.post_messages = true;
                tL_chatAdminRights.change_info = true;
                if (!this.isChannel) {
                    admin.admin_rights.manage_call = true;
                }
                map.put(selfUserId, admin);
                int index2 = arrayList.indexOf(object2);
                if (index2 >= 0) {
                    arrayList.set(index2, admin);
                }
                found = true;
            } else {
                foundAny = foundAny2;
                a = a2;
                boolean z2 = found;
            }
            if (found) {
                Collections.sort(arrayList, new ChatUsersActivity$$ExternalSyntheticLambda1(this));
            }
            a2 = a + 1;
            user3 = user;
            foundAny2 = foundAny;
        }
        int i = a2;
        if (!foundAny2) {
            TLRPC.TL_channelParticipantCreator creator2 = new TLRPC.TL_channelParticipantCreator();
            creator2.peer = new TLRPC.TL_peerUser();
            user2 = user;
            creator2.peer.user_id = user2.id;
            this.participantsMap.put(user2.id, creator2);
            this.participants.add(creator2);
            sortAdmins(this.participants);
            updateRows();
        } else {
            user2 = user;
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(user2);
        }
    }

    /* renamed from: lambda$onOwnerChaged$4$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ int m3310lambda$onOwnerChaged$4$orgtelegramuiChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        return 0;
    }

    private void openRightsEdit2(long peerId, int date, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, int type2, boolean removeFragment) {
        TLObject tLObject = participant;
        boolean[] needShowBulletin = new boolean[1];
        final boolean isAdmin = (tLObject instanceof TLRPC.TL_channelParticipantAdmin) || (tLObject instanceof TLRPC.TL_chatParticipantAdmin);
        boolean[] needShowBulletin2 = needShowBulletin;
        final boolean[] zArr = needShowBulletin2;
        final long j = peerId;
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(this, peerId, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, type2, true, false, (String) null) {
            final /* synthetic */ ChatUsersActivity this$0;

            {
                this.this$0 = this$0;
            }

            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                if (!isOpen && backward && zArr[0] && BulletinFactory.canShowBulletin(this.this$0)) {
                    if (j > 0) {
                        TLRPC.User user = getMessagesController().getUser(Long.valueOf(j));
                        if (user != null) {
                            BulletinFactory.createPromoteToAdminBulletin(this.this$0, user.first_name).show();
                            return;
                        }
                        return;
                    }
                    TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-j));
                    if (chat != null) {
                        BulletinFactory.createPromoteToAdminBulletin(this.this$0, chat.title).show();
                    }
                }
            }
        };
        final int i = type2;
        final long j2 = peerId;
        final int i2 = date;
        final boolean[] zArr2 = needShowBulletin2;
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                TLRPC.ChatParticipant newParticipant;
                TLRPC.ChannelParticipant newPart;
                int i = i;
                if (i == 0) {
                    int a = 0;
                    while (true) {
                        if (a >= ChatUsersActivity.this.participants.size()) {
                            break;
                        }
                        TLObject p = (TLObject) ChatUsersActivity.this.participants.get(a);
                        if (p instanceof TLRPC.ChannelParticipant) {
                            if (MessageObject.getPeerId(((TLRPC.ChannelParticipant) p).peer) == j2) {
                                if (rights == 1) {
                                    newPart = new TLRPC.TL_channelParticipantAdmin();
                                } else {
                                    newPart = new TLRPC.TL_channelParticipant();
                                }
                                newPart.admin_rights = rightsAdmin;
                                newPart.banned_rights = rightsBanned;
                                newPart.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                if (j2 > 0) {
                                    newPart.peer = new TLRPC.TL_peerUser();
                                    newPart.peer.user_id = j2;
                                } else {
                                    newPart.peer = new TLRPC.TL_peerChannel();
                                    newPart.peer.channel_id = -j2;
                                }
                                newPart.date = i2;
                                newPart.flags |= 4;
                                newPart.rank = rank;
                                ChatUsersActivity.this.participants.set(a, newPart);
                            }
                        } else if (p instanceof TLRPC.ChatParticipant) {
                            TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) p;
                            if (rights == 1) {
                                newParticipant = new TLRPC.TL_chatParticipantAdmin();
                            } else {
                                newParticipant = new TLRPC.TL_chatParticipant();
                            }
                            newParticipant.user_id = chatParticipant.user_id;
                            newParticipant.date = chatParticipant.date;
                            newParticipant.inviter_id = chatParticipant.inviter_id;
                            int index = ChatUsersActivity.this.info.participants.participants.indexOf(chatParticipant);
                            if (index >= 0) {
                                ChatUsersActivity.this.info.participants.participants.set(index, newParticipant);
                            }
                            ChatUsersActivity.this.loadChatParticipants(0, 200);
                        }
                        a++;
                    }
                    if (rights == 1 && !isAdmin) {
                        zArr2[0] = true;
                    }
                } else if (i == 1 && rights == 0) {
                    ChatUsersActivity.this.removeParticipants(j2);
                }
            }

            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(fragment);
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public void openRightsEdit(long user_id, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, int type2, boolean removeFragment) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user_id, this.chatId, adminRights, this.defaultBannedRights, bannedRights, rank, type2, canEditAdmin, participant == null, (String) null);
        final TLObject tLObject = participant;
        final long j = user_id;
        final boolean z = removeFragment;
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                TLObject tLObject = tLObject;
                if (tLObject instanceof TLRPC.ChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                    channelParticipant.admin_rights = rightsAdmin;
                    channelParticipant.banned_rights = rightsBanned;
                    channelParticipant.rank = rank;
                }
                if (ChatUsersActivity.this.delegate != null && rights == 1) {
                    ChatUsersActivity.this.delegate.didSelectUser(j);
                } else if (ChatUsersActivity.this.delegate != null) {
                    ChatUsersActivity.this.delegate.didAddParticipantToList(j, tLObject);
                }
                if (z) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }

            public void didChangeOwner(TLRPC.User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(fragment, removeFragment);
    }

    private void removeParticipant(long userId) {
        if (ChatObject.isChannel(this.currentChat)) {
            getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(userId)), (TLRPC.ChatFull) null);
            ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
            if (chatUsersActivityDelegate != null) {
                chatUsersActivityDelegate.didKickParticipant(userId);
            }
            finishFragment();
        }
    }

    private TLObject getAnyParticipant(long userId) {
        LongSparseArray<TLObject> map;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
            } else if (a == 1) {
                map = this.botsMap;
            } else {
                map = this.participantsMap;
            }
            TLObject p = map.get(userId);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    private void removeParticipants(TLObject object) {
        if (object instanceof TLRPC.ChatParticipant) {
            removeParticipants(((TLRPC.ChatParticipant) object).user_id);
        } else if (object instanceof TLRPC.ChannelParticipant) {
            removeParticipants(MessageObject.getPeerId(((TLRPC.ChannelParticipant) object).peer));
        }
    }

    /* access modifiers changed from: private */
    public void removeParticipants(long peerId) {
        ArrayList<TLObject> arrayList;
        LongSparseArray<TLObject> map;
        TLRPC.ChatFull chatFull;
        boolean updated = false;
        DiffCallback savedState = saveState();
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
                arrayList = this.contacts;
            } else if (a == 1) {
                map = this.botsMap;
                arrayList = this.bots;
            } else {
                map = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject p = map.get(peerId);
            if (p != null) {
                map.remove(peerId);
                arrayList.remove(p);
                updated = true;
                if (this.type == 0 && (chatFull = this.info) != null) {
                    chatFull.kicked_count--;
                }
            }
        }
        if (updated) {
            updateListAnimated(savedState);
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        if (adapter == searchAdapter) {
            searchAdapter.removeUserId(peerId);
        }
    }

    /* access modifiers changed from: private */
    public void updateParticipantWithRights(TLRPC.ChannelParticipant channelParticipant, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, long user_id, boolean withDelegate) {
        LongSparseArray<TLObject> map;
        ChatUsersActivityDelegate chatUsersActivityDelegate;
        boolean delegateCalled = false;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                map = this.contactsMap;
            } else if (a == 1) {
                map = this.botsMap;
            } else {
                map = this.participantsMap;
            }
            TLObject p = map.get(MessageObject.getPeerId(channelParticipant.peer));
            if (p instanceof TLRPC.ChannelParticipant) {
                channelParticipant = (TLRPC.ChannelParticipant) p;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
                if (withDelegate) {
                    channelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (withDelegate && p != null && !delegateCalled && (chatUsersActivityDelegate = this.delegate) != null) {
                delegateCalled = true;
                chatUsersActivityDelegate.didAddParticipantToList(user_id, p);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean createMenuForParticipant(TLObject participant, boolean resultOnly) {
        int date;
        TLRPC.TL_chatAdminRights adminRights;
        TLRPC.TL_chatBannedRights bannedRights;
        boolean canEdit;
        long peerId;
        String rank;
        int[] icons;
        CharSequence[] items;
        String str;
        String str2;
        int i;
        boolean allowSetAdmin;
        ArrayList<String> items2;
        ArrayList<Integer> actions;
        ArrayList<Integer> icons2;
        boolean hasRemove;
        TLObject tLObject = participant;
        if (tLObject == null) {
            TLObject tLObject2 = tLObject;
        } else if (this.selectType != 0) {
            TLObject tLObject3 = tLObject;
        } else {
            if (tLObject instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                long peerId2 = MessageObject.getPeerId(channelParticipant.peer);
                boolean canEdit2 = channelParticipant.can_edit;
                TLRPC.TL_chatBannedRights bannedRights2 = channelParticipant.banned_rights;
                TLRPC.TL_chatAdminRights adminRights2 = channelParticipant.admin_rights;
                int date2 = channelParticipant.date;
                rank = channelParticipant.rank;
                peerId = peerId2;
                canEdit = canEdit2;
                bannedRights = bannedRights2;
                adminRights = adminRights2;
                date = date2;
            } else if (tLObject instanceof TLRPC.ChatParticipant) {
                TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) tLObject;
                long peerId3 = chatParticipant.user_id;
                int date3 = chatParticipant.date;
                rank = "";
                peerId = peerId3;
                canEdit = ChatObject.canAddAdmins(this.currentChat);
                bannedRights = null;
                adminRights = null;
                date = date3;
            } else {
                rank = null;
                peerId = 0;
                canEdit = false;
                bannedRights = null;
                adminRights = null;
                date = 0;
            }
            if (peerId == 0) {
                TLObject tLObject4 = tLObject;
                long j = peerId;
            } else if (peerId == getUserConfig().getClientUserId()) {
                TLObject tLObject5 = tLObject;
                long j2 = peerId;
            } else {
                int i2 = this.type;
                if (i2 == 2) {
                    TLRPC.User user = getMessagesController().getUser(Long.valueOf(peerId));
                    boolean allowSetAdmin2 = ChatObject.canAddAdmins(this.currentChat) && ((tLObject instanceof TLRPC.TL_channelParticipant) || (tLObject instanceof TLRPC.TL_channelParticipantBanned) || (tLObject instanceof TLRPC.TL_chatParticipant) || canEdit);
                    boolean canEditAdmin = (!(tLObject instanceof TLRPC.TL_channelParticipantAdmin) && !(tLObject instanceof TLRPC.TL_channelParticipantCreator) && !(tLObject instanceof TLRPC.TL_chatParticipantCreator) && !(tLObject instanceof TLRPC.TL_chatParticipantAdmin)) || canEdit;
                    boolean editingAdmin = (tLObject instanceof TLRPC.TL_channelParticipantAdmin) || (tLObject instanceof TLRPC.TL_chatParticipantAdmin);
                    if (this.selectType == 0) {
                        allowSetAdmin = allowSetAdmin2 & (!UserObject.isDeleted(user));
                    } else {
                        allowSetAdmin = allowSetAdmin2;
                    }
                    if (!resultOnly) {
                        items2 = new ArrayList<>();
                        actions = new ArrayList<>();
                        icons2 = new ArrayList<>();
                    } else {
                        items2 = null;
                        actions = null;
                        icons2 = null;
                    }
                    if (allowSetAdmin) {
                        if (resultOnly) {
                            return true;
                        }
                        items2.add(editingAdmin ? LocaleController.getString("EditAdminRights", NUM) : LocaleController.getString("SetAsAdmin", NUM));
                        icons2.add(NUM);
                        actions.add(0);
                    }
                    if (!ChatObject.canBlockUsers(this.currentChat) || !canEditAdmin) {
                        hasRemove = false;
                    } else if (resultOnly) {
                        return true;
                    } else {
                        if (!this.isChannel) {
                            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.gigagroup) {
                                items2.add(LocaleController.getString("ChangePermissions", NUM));
                                icons2.add(NUM);
                                actions.add(1);
                            }
                            items2.add(LocaleController.getString("KickFromGroup", NUM));
                        } else {
                            items2.add(LocaleController.getString("ChannelRemoveUser", NUM));
                        }
                        icons2.add(NUM);
                        actions.add(2);
                        hasRemove = true;
                    }
                    if (actions == null) {
                        ArrayList<Integer> arrayList = icons2;
                        ArrayList<Integer> arrayList2 = actions;
                        ArrayList<String> arrayList3 = items2;
                        long j3 = peerId;
                    } else if (actions.isEmpty()) {
                        ArrayList<Integer> arrayList4 = icons2;
                        ArrayList<Integer> arrayList5 = actions;
                        ArrayList<String> arrayList6 = items2;
                        long j4 = peerId;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        int[] intArray = AndroidUtilities.toIntArray(icons2);
                        long j5 = peerId;
                        long j6 = peerId;
                        CharSequence[] charSequenceArr = (CharSequence[]) items2.toArray(new CharSequence[actions.size()]);
                        ChatUsersActivity$$ExternalSyntheticLambda12 chatUsersActivity$$ExternalSyntheticLambda12 = r0;
                        AlertDialog.Builder builder2 = builder;
                        ArrayList<Integer> arrayList7 = icons2;
                        ArrayList<Integer> arrayList8 = actions;
                        ArrayList<String> items3 = items2;
                        ChatUsersActivity$$ExternalSyntheticLambda12 chatUsersActivity$$ExternalSyntheticLambda122 = new ChatUsersActivity$$ExternalSyntheticLambda12(this, actions, user, j5, canEditAdmin, participant, date, adminRights, bannedRights, rank);
                        builder2.setItems(charSequenceArr, intArray, chatUsersActivity$$ExternalSyntheticLambda12);
                        AlertDialog alertDialog = builder2.create();
                        showDialog(alertDialog);
                        if (hasRemove) {
                            alertDialog.setItemColor(items3.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                        }
                        TLObject tLObject6 = participant;
                        return true;
                    }
                    return false;
                }
                long peerId4 = peerId;
                if (i2 != 3 || !ChatObject.canBlockUsers(this.currentChat)) {
                    if (this.type != 0 || !ChatObject.canBlockUsers(this.currentChat)) {
                        if (this.type != 1 || !ChatObject.canAddAdmins(this.currentChat) || !canEdit) {
                            TLObject tLObject7 = participant;
                            items = null;
                            icons = null;
                        } else if (resultOnly) {
                            return true;
                        } else {
                            if (this.currentChat.creator) {
                                TLObject tLObject8 = participant;
                            } else if ((participant instanceof TLRPC.TL_channelParticipantCreator) || !canEdit) {
                                items = new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", NUM)};
                                icons = new int[]{NUM};
                            }
                            items = new CharSequence[]{LocaleController.getString("EditAdminRights", NUM), LocaleController.getString("ChannelRemoveUserAdmin", NUM)};
                            icons = new int[]{NUM, NUM};
                        }
                    } else if (resultOnly) {
                        return true;
                    } else {
                        CharSequence[] items4 = new CharSequence[2];
                        if (!ChatObject.canAddUsers(this.currentChat) || peerId4 <= 0) {
                            str = null;
                        } else {
                            if (this.isChannel) {
                                i = NUM;
                                str2 = "ChannelAddToChannel";
                            } else {
                                i = NUM;
                                str2 = "ChannelAddToGroup";
                            }
                            str = LocaleController.getString(str2, i);
                        }
                        items4[0] = str;
                        items4[1] = LocaleController.getString("ChannelDeleteFromList", NUM);
                        TLObject tLObject9 = participant;
                        items = items4;
                        icons = new int[]{NUM, NUM};
                    }
                } else if (resultOnly) {
                    return true;
                } else {
                    TLObject tLObject10 = participant;
                    items = new CharSequence[]{LocaleController.getString("ChannelEditPermissions", NUM), LocaleController.getString("ChannelDeleteFromList", NUM)};
                    icons = new int[]{NUM, NUM};
                }
                if (items == null) {
                    return false;
                }
                AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
                ChatUsersActivity$$ExternalSyntheticLambda14 chatUsersActivity$$ExternalSyntheticLambda14 = r0;
                ChatUsersActivity$$ExternalSyntheticLambda14 chatUsersActivity$$ExternalSyntheticLambda142 = new ChatUsersActivity$$ExternalSyntheticLambda14(this, items, peerId4, adminRights, rank, participant, bannedRights);
                builder3.setItems(items, icons, chatUsersActivity$$ExternalSyntheticLambda14);
                AlertDialog alertDialog2 = builder3.create();
                showDialog(alertDialog2);
                if (this.type != 1) {
                    return true;
                }
                alertDialog2.setItemColor(items.length - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                return true;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$createMenuForParticipant$6$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3300x442d0779(ArrayList actions, TLRPC.User user, long peerId, boolean canEditAdmin, TLObject participant, int date, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, DialogInterface dialogInterface, int i) {
        ArrayList arrayList = actions;
        TLRPC.User user2 = user;
        TLObject tLObject = participant;
        int i2 = i;
        if (((Integer) arrayList.get(i2)).intValue() == 2) {
            getMessagesController().deleteParticipantFromChat(this.chatId, user2, (TLRPC.ChatFull) null);
            removeParticipants(peerId);
            if (this.currentChat == null || user2 == null || !BulletinFactory.canShowBulletin(this)) {
                ArrayList arrayList2 = arrayList;
                int i3 = i2;
                ArrayList arrayList3 = arrayList2;
                return;
            }
            BulletinFactory.createRemoveFromChatBulletin(this, user2, this.currentChat.title).show();
            ArrayList arrayList4 = arrayList;
            int i4 = i2;
            ArrayList arrayList5 = arrayList4;
            return;
        }
        long j = peerId;
        if (((Integer) arrayList.get(i2)).intValue() == 1 && canEditAdmin) {
            if ((tLObject instanceof TLRPC.TL_channelParticipantAdmin) || (tLObject instanceof TLRPC.TL_chatParticipantAdmin)) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", NUM));
                builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, UserObject.getUserName(user)));
                ChatUsersActivity$$ExternalSyntheticLambda11 chatUsersActivity$$ExternalSyntheticLambda11 = r0;
                String string = LocaleController.getString("OK", NUM);
                AlertDialog.Builder builder22 = builder2;
                ChatUsersActivity$$ExternalSyntheticLambda11 chatUsersActivity$$ExternalSyntheticLambda112 = new ChatUsersActivity$$ExternalSyntheticLambda11(this, peerId, date, participant, adminRights, bannedRights, rank, canEditAdmin, actions, i);
                builder22.setPositiveButton(string, chatUsersActivity$$ExternalSyntheticLambda11);
                builder22.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder22.create());
                ArrayList arrayList6 = actions;
                int i5 = i;
                return;
            }
        }
        openRightsEdit2(peerId, date, participant, adminRights, bannedRights, rank, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
    }

    /* renamed from: lambda$createMenuForParticipant$5$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3299xdd5447b8(long peerId, int date, TLObject participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean canEditAdmin, ArrayList actions, int i, DialogInterface dialog, int which) {
        openRightsEdit2(peerId, date, participant, adminRights, bannedRights, rank, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
    }

    /* renamed from: lambda$createMenuForParticipant$9$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3303x78b746bc(CharSequence[] items, long peerId, TLRPC.TL_chatAdminRights adminRights, String rank, TLObject participant, TLRPC.TL_chatBannedRights bannedRights, DialogInterface dialogInterface, int i) {
        int i2;
        int i3;
        final TLObject tLObject;
        TLRPC.Chat chat;
        TLRPC.User user;
        long j = peerId;
        final TLObject tLObject2 = participant;
        int i4 = i;
        int i5 = this.type;
        if (i5 == 1) {
            if (i4 != 0) {
                CharSequence[] charSequenceArr = items;
            } else if (items.length == 2) {
                ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(peerId, this.chatId, adminRights, (TLRPC.TL_chatBannedRights) null, (TLRPC.TL_chatBannedRights) null, rank, 0, true, false, (String) null);
                chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                        TLObject tLObject = tLObject2;
                        if (tLObject instanceof TLRPC.ChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                            channelParticipant.admin_rights = rightsAdmin;
                            channelParticipant.banned_rights = rightsBanned;
                            channelParticipant.rank = rank;
                            ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0, false);
                        }
                    }

                    public void didChangeOwner(TLRPC.User user) {
                        ChatUsersActivity.this.onOwnerChaged(user);
                    }
                });
                presentFragment(chatRightsEditActivity);
                long j2 = j;
                int i6 = i4;
                TLObject tLObject3 = tLObject2;
                long j3 = j2;
                return;
            }
            getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Long.valueOf(peerId)), new TLRPC.TL_chatAdminRights(), "", true ^ this.isChannel, this, false, false, (String) null, (Runnable) null);
            removeParticipants(peerId);
            int i7 = i;
            TLObject tLObject4 = tLObject2;
            return;
        }
        TLObject tLObject5 = tLObject2;
        long j4 = j;
        if (i5 == 0) {
            i2 = i;
        } else if (i5 == 3) {
            i2 = i;
        } else if (i == 0) {
            if (j4 > 0) {
                user = getMessagesController().getUser(Long.valueOf(peerId));
                chat = null;
            } else {
                user = null;
                chat = getMessagesController().getChat(Long.valueOf(-j4));
            }
            getMessagesController().deleteParticipantFromChat(this.chatId, user, chat, (TLRPC.ChatFull) null, false, false);
            TLObject tLObject6 = tLObject5;
            return;
        } else {
            TLObject tLObject7 = tLObject5;
            return;
        }
        if (i2 != 0) {
            tLObject = tLObject5;
            i3 = 1;
            if (i2 == 1) {
                TLRPC.TL_channels_editBanned req = new TLRPC.TL_channels_editBanned();
                req.participant = getMessagesController().getInputPeer(j4);
                req.channel = getMessagesController().getInputChannel(this.chatId);
                req.banned_rights = new TLRPC.TL_chatBannedRights();
                getConnectionsManager().sendRequest(req, new ChatUsersActivity$$ExternalSyntheticLambda6(this));
            }
        } else if (i5 == 3) {
            tLObject = tLObject5;
            ChatRightsEditActivity chatRightsEditActivity2 = new ChatRightsEditActivity(peerId, this.chatId, (TLRPC.TL_chatAdminRights) null, this.defaultBannedRights, bannedRights, rank, 1, true, false, (String) null);
            chatRightsEditActivity2.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                    TLObject tLObject = tLObject;
                    if (tLObject instanceof TLRPC.ChannelParticipant) {
                        TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                        channelParticipant.admin_rights = rightsAdmin;
                        channelParticipant.banned_rights = rightsBanned;
                        channelParticipant.rank = rank;
                        ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, rightsAdmin, rightsBanned, 0, false);
                    }
                }

                public void didChangeOwner(TLRPC.User user) {
                    ChatUsersActivity.this.onOwnerChaged(user);
                }
            });
            presentFragment(chatRightsEditActivity2);
            i3 = 1;
        } else {
            tLObject = tLObject5;
            if (i5 != 0) {
                i3 = 1;
            } else if (j4 > 0) {
                i3 = 1;
                getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Long.valueOf(peerId)), 0, (String) null, this, (Runnable) null);
            } else {
                i3 = 1;
            }
        }
        if ((i2 == 0 && this.type == 0) || i2 == i3) {
            removeParticipants(tLObject);
        }
    }

    /* renamed from: lambda$createMenuForParticipant$8$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3302x11de86fb(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            getMessagesController().processUpdates(updates, false);
            if (!updates.chats.isEmpty()) {
                AndroidUtilities.runOnUIThread(new ChatUsersActivity$$ExternalSyntheticLambda18(this, updates), 1000);
            }
        }
    }

    /* renamed from: lambda$createMenuForParticipant$7$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3301xab05CLASSNAMEa(TLRPC.Updates updates) {
        getMessagesController().loadFullChat(updates.chats.get(0).id, 0, true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            boolean hadInfo = false;
            TLRPC.ChatFull chatFull = args[0];
            boolean byChannelUsers = args[2].booleanValue();
            if (chatFull.id != this.chatId) {
                return;
            }
            if (!byChannelUsers || !ChatObject.isChannel(this.currentChat)) {
                if (this.info != null) {
                    hadInfo = true;
                }
                this.info = chatFull;
                if (!hadInfo) {
                    int currentSlowmode = getCurrentSlowmode();
                    this.initialSlowmode = currentSlowmode;
                    this.selectedSlowmode = currentSlowmode;
                }
                AndroidUtilities.runOnUIThread(new ChatUsersActivity$$ExternalSyntheticLambda16(this));
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$10$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3307x636404ba() {
        loadChatParticipants(0, 200);
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    private int getCurrentSlowmode() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 0;
        }
        if (chatFull.slowmode_seconds == 10) {
            return 1;
        }
        if (this.info.slowmode_seconds == 30) {
            return 2;
        }
        if (this.info.slowmode_seconds == 60) {
            return 3;
        }
        if (this.info.slowmode_seconds == 300) {
            return 4;
        }
        if (this.info.slowmode_seconds == 900) {
            return 5;
        }
        if (this.info.slowmode_seconds == 3600) {
            return 6;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public int getSecondsForIndex(int index) {
        if (index == 1) {
            return 10;
        }
        if (index == 2) {
            return 30;
        }
        if (index == 3) {
            return 60;
        }
        if (index == 4) {
            return 300;
        }
        if (index == 5) {
            return 900;
        }
        if (index == 6) {
            return 3600;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public String formatSeconds(int seconds) {
        if (seconds < 60) {
            return LocaleController.formatPluralString("Seconds", seconds, new Object[0]);
        }
        if (seconds < 3600) {
            return LocaleController.formatPluralString("Minutes", seconds / 60, new Object[0]);
        }
        return LocaleController.formatPluralString("Hours", (seconds / 60) / 60, new Object[0]);
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights) && this.initialSlowmode == this.selectedSlowmode) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", NUM));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", NUM));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatUsersActivity$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatUsersActivity$$ExternalSyntheticLambda10(this));
        showDialog(builder.create());
        return false;
    }

    /* renamed from: lambda$checkDiscard$11$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3297lambda$checkDiscard$11$orgtelegramuiChatUsersActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$12$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3298lambda$checkDiscard$12$orgtelegramuiChatUsersActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    /* access modifiers changed from: private */
    public String formatUserPermissions(TLRPC.TL_chatBannedRights rights) {
        if (rights == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (rights.view_messages && this.defaultBannedRights.view_messages != rights.view_messages) {
            builder.append(LocaleController.getString("UserRestrictionsNoRead", NUM));
        }
        if (rights.send_messages && this.defaultBannedRights.send_messages != rights.send_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSend", NUM));
        }
        if (rights.send_media && this.defaultBannedRights.send_media != rights.send_media) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendMedia", NUM));
        }
        if (rights.send_stickers && this.defaultBannedRights.send_stickers != rights.send_stickers) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendStickers", NUM));
        }
        if (rights.send_polls && this.defaultBannedRights.send_polls != rights.send_polls) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendPolls", NUM));
        }
        if (rights.embed_links && this.defaultBannedRights.embed_links != rights.embed_links) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoEmbedLinks", NUM));
        }
        if (rights.invite_users && this.defaultBannedRights.invite_users != rights.invite_users) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoInviteUsers", NUM));
        }
        if (rights.pin_messages && this.defaultBannedRights.pin_messages != rights.pin_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoPinMessages", NUM));
        }
        if (rights.change_info && this.defaultBannedRights.change_info != rights.change_info) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoChangeInfo", NUM));
        }
        if (builder.length() != 0) {
            builder.replace(0, 1, builder.substring(0, 1).toUpperCase());
            builder.append('.');
        }
        return builder.toString();
    }

    /* access modifiers changed from: private */
    public void processDone() {
        TLRPC.ChatFull chatFull;
        if (this.type == 3) {
            if (!this.currentChat.creator || ChatObject.isChannel(this.currentChat) || this.selectedSlowmode == this.initialSlowmode || this.info == null) {
                if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
                    getMessagesController().setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
                    TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
                    if (chat != null) {
                        chat.default_banned_rights = this.defaultBannedRights;
                    }
                }
                int i = this.selectedSlowmode;
                if (!(i == this.initialSlowmode || (chatFull = this.info) == null)) {
                    chatFull.slowmode_seconds = getSecondsForIndex(i);
                    this.info.flags |= 131072;
                    getMessagesController().setChannelSlowMode(this.chatId, this.info.slowmode_seconds);
                }
                finishFragment();
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatUsersActivity$$ExternalSyntheticLambda4(this));
        }
    }

    /* renamed from: lambda$processDone$13$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3311lambda$processDone$13$orgtelegramuiChatUsersActivity(long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            processDone();
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            int currentSlowmode = getCurrentSlowmode();
            this.initialSlowmode = currentSlowmode;
            this.selectedSlowmode = currentSlowmode;
        }
    }

    public boolean needDelayOpenAnimation() {
        return true;
    }

    private int getChannelAdminParticipantType(TLObject participant) {
        if ((participant instanceof TLRPC.TL_channelParticipantCreator) || (participant instanceof TLRPC.TL_channelParticipantSelf)) {
            return 0;
        }
        if ((participant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_channelParticipant)) {
            return 1;
        }
        return 2;
    }

    /* access modifiers changed from: private */
    public void loadChatParticipants(int offset, int count) {
        if (!this.loadingUsers) {
            this.contactsEndReached = false;
            this.botsEndReached = false;
            loadChatParticipants(offset, count, true);
        }
    }

    private ArrayList<TLRPC.TL_channels_getParticipants> loadChatParticipantsRequests(int offset, int count, boolean reset) {
        TLRPC.Chat chat;
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        ArrayList<TLRPC.TL_channels_getParticipants> requests = new ArrayList<>();
        requests.add(req);
        req.channel = getMessagesController().getInputChannel(this.chatId);
        int i = this.type;
        if (i == 0) {
            req.filter = new TLRPC.TL_channelParticipantsKicked();
        } else if (i == 1) {
            req.filter = new TLRPC.TL_channelParticipantsAdmins();
        } else if (i == 2) {
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && chatFull.participants_count <= 200 && (chat = this.currentChat) != null && chat.megagroup) {
                req.filter = new TLRPC.TL_channelParticipantsRecent();
            } else if (this.selectType == 1) {
                if (!this.contactsEndReached) {
                    this.delayResults = 2;
                    req.filter = new TLRPC.TL_channelParticipantsContacts();
                    this.contactsEndReached = true;
                    requests.addAll(loadChatParticipantsRequests(0, 200, false));
                } else {
                    req.filter = new TLRPC.TL_channelParticipantsRecent();
                }
            } else if (!this.contactsEndReached) {
                this.delayResults = 3;
                req.filter = new TLRPC.TL_channelParticipantsContacts();
                this.contactsEndReached = true;
                requests.addAll(loadChatParticipantsRequests(0, 200, false));
            } else if (!this.botsEndReached) {
                req.filter = new TLRPC.TL_channelParticipantsBots();
                this.botsEndReached = true;
                requests.addAll(loadChatParticipantsRequests(0, 200, false));
            } else {
                req.filter = new TLRPC.TL_channelParticipantsRecent();
            }
        } else if (i == 3) {
            req.filter = new TLRPC.TL_channelParticipantsBanned();
        }
        req.filter.q = "";
        req.offset = offset;
        req.limit = count;
        return requests;
    }

    private void loadChatParticipants(int offset, int count, boolean reset) {
        LongSparseArray<TLRPC.TL_groupCallParticipant> longSparseArray;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.bots.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            this.botsMap.clear();
            int i = this.type;
            if (i == 1) {
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull != null) {
                    int size = chatFull.participants.participants.size();
                    for (int a = 0; a < size; a++) {
                        TLRPC.ChatParticipant participant = this.info.participants.participants.get(a);
                        if ((participant instanceof TLRPC.TL_chatParticipantCreator) || (participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                            this.participants.add(participant);
                        }
                        this.participantsMap.put(participant.user_id, participant);
                    }
                }
            } else if (i == 2 && this.info != null) {
                long selfUserId = getUserConfig().clientUserId;
                int size2 = this.info.participants.participants.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    TLRPC.ChatParticipant participant2 = this.info.participants.participants.get(a2);
                    if ((this.selectType == 0 || participant2.user_id != selfUserId) && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(participant2.user_id) < 0)) {
                        if (this.selectType == 1) {
                            if (getContactsController().isContact(participant2.user_id)) {
                                this.contacts.add(participant2);
                                this.contactsMap.put(participant2.user_id, participant2);
                            } else if (!UserObject.isDeleted(getMessagesController().getUser(Long.valueOf(participant2.user_id)))) {
                                this.participants.add(participant2);
                                this.participantsMap.put(participant2.user_id, participant2);
                            }
                        } else if (getContactsController().isContact(participant2.user_id)) {
                            this.contacts.add(participant2);
                            this.contactsMap.put(participant2.user_id, participant2);
                        } else {
                            TLRPC.User user = getMessagesController().getUser(Long.valueOf(participant2.user_id));
                            if (user == null || !user.bot) {
                                this.participants.add(participant2);
                                this.participantsMap.put(participant2.user_id, participant2);
                            } else {
                                this.bots.add(participant2);
                                this.botsMap.put(participant2.user_id, participant2);
                            }
                        }
                    }
                }
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            updateRows();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(true, false);
        }
        ListAdapter listAdapter3 = this.listViewAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
        ArrayList<TLRPC.TL_channels_getParticipants> requests = loadChatParticipantsRequests(offset, count, reset);
        ArrayList<TLRPC.TL_channels_channelParticipants> responses = new ArrayList<>();
        Runnable onRequestsEnd = new ChatUsersActivity$$ExternalSyntheticLambda17(this, requests, responses);
        AtomicInteger responsesReceived = new AtomicInteger(0);
        for (int i2 = 0; i2 < requests.size(); i2++) {
            responses.add((Object) null);
            int index = i2;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(requests.get(index), new ChatUsersActivity$$ExternalSyntheticLambda5(responses, index, responsesReceived, requests, onRequestsEnd)), this.classGuid);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:138:0x0160 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0152  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01a3 A[Catch:{ Exception -> 0x01a9 }] */
    /* renamed from: lambda$loadChatParticipants$14$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3309lambda$loadChatParticipants$14$orgtelegramuiChatUsersActivity(java.util.ArrayList r23, java.util.ArrayList r24) {
        /*
            r22 = this;
            r1 = r22
            r0 = 0
            r2 = 0
        L_0x0004:
            int r3 = r23.size()
            r4 = 2
            r5 = 0
            r6 = 1
            if (r2 >= r3) goto L_0x01ba
            r3 = r23
            java.lang.Object r7 = r3.get(r2)
            org.telegram.tgnet.TLRPC$TL_channels_getParticipants r7 = (org.telegram.tgnet.TLRPC.TL_channels_getParticipants) r7
            r8 = r24
            java.lang.Object r9 = r8.get(r2)
            org.telegram.tgnet.TLRPC$TL_channels_channelParticipants r9 = (org.telegram.tgnet.TLRPC.TL_channels_channelParticipants) r9
            if (r7 == 0) goto L_0x01b4
            if (r9 != 0) goto L_0x0023
            goto L_0x01b6
        L_0x0023:
            int r10 = r1.type
            if (r10 != r6) goto L_0x0030
            org.telegram.messenger.MessagesController r10 = r22.getMessagesController()
            long r11 = r1.chatId
            r10.processLoadedAdminsResponse(r11, r9)
        L_0x0030:
            org.telegram.messenger.MessagesController r10 = r22.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r9.users
            r10.putUsers(r11, r5)
            org.telegram.messenger.MessagesController r10 = r22.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r11 = r9.chats
            r10.putChats(r11, r5)
            org.telegram.messenger.UserConfig r5 = r22.getUserConfig()
            long r10 = r5.getClientUserId()
            int r5 = r1.selectType
            if (r5 == 0) goto L_0x0072
            r5 = 0
        L_0x004f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r12 = r9.participants
            int r12 = r12.size()
            if (r5 >= r12) goto L_0x0072
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r12 = r9.participants
            java.lang.Object r12 = r12.get(r5)
            org.telegram.tgnet.TLRPC$ChannelParticipant r12 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r12
            org.telegram.tgnet.TLRPC$Peer r12 = r12.peer
            long r12 = org.telegram.messenger.MessageObject.getPeerId(r12)
            int r14 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r14 != 0) goto L_0x006f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r12 = r9.participants
            r12.remove(r5)
            goto L_0x0072
        L_0x006f:
            int r5 = r5 + 1
            goto L_0x004f
        L_0x0072:
            int r5 = r1.type
            if (r5 != r4) goto L_0x0096
            int r5 = r1.delayResults
            int r5 = r5 - r6
            r1.delayResults = r5
            org.telegram.tgnet.TLRPC$ChannelParticipantsFilter r5 = r7.filter
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantsContacts
            if (r5 == 0) goto L_0x0086
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r1.contacts
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r12 = r1.contactsMap
            goto L_0x009f
        L_0x0086:
            org.telegram.tgnet.TLRPC$ChannelParticipantsFilter r5 = r7.filter
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantsBots
            if (r5 == 0) goto L_0x0091
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r1.bots
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r12 = r1.botsMap
            goto L_0x009f
        L_0x0091:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r1.participants
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r12 = r1.participantsMap
            goto L_0x009f
        L_0x0096:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r1.participants
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r12 = r1.participantsMap
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r13 = r1.participantsMap
            r13.clear()
        L_0x009f:
            r5.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r13 = r9.participants
            r5.addAll(r13)
            r13 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r14 = r9.participants
            int r14 = r14.size()
        L_0x00ae:
            if (r13 >= r14) goto L_0x00d3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChannelParticipant> r15 = r9.participants
            java.lang.Object r15 = r15.get(r13)
            org.telegram.tgnet.TLRPC$ChannelParticipant r15 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r15
            r16 = r7
            long r6 = r15.user_id
            int r17 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r17 != 0) goto L_0x00c4
            r5.remove(r15)
            goto L_0x00cd
        L_0x00c4:
            org.telegram.tgnet.TLRPC$Peer r6 = r15.peer
            long r6 = org.telegram.messenger.MessageObject.getPeerId(r6)
            r12.put(r6, r15)
        L_0x00cd:
            int r13 = r13 + 1
            r7 = r16
            r6 = 1
            goto L_0x00ae
        L_0x00d3:
            r16 = r7
            int r6 = r5.size()
            int r6 = r6 + r0
            int r0 = r1.type
            if (r0 != r4) goto L_0x0170
            r0 = 0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r7 = r1.participants
            int r7 = r7.size()
        L_0x00e5:
            if (r0 >= r7) goto L_0x016b
            java.util.ArrayList<org.telegram.tgnet.TLObject> r13 = r1.participants
            java.lang.Object r13 = r13.get(r0)
            org.telegram.tgnet.TLObject r13 = (org.telegram.tgnet.TLObject) r13
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant
            if (r14 != 0) goto L_0x0101
            java.util.ArrayList<org.telegram.tgnet.TLObject> r14 = r1.participants
            r14.remove(r0)
            int r0 = r0 + -1
            int r7 = r7 + -1
            r18 = r5
            r19 = r6
            goto L_0x0160
        L_0x0101:
            r14 = r13
            org.telegram.tgnet.TLRPC$ChannelParticipant r14 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r14
            org.telegram.tgnet.TLRPC$Peer r15 = r14.peer
            r18 = r5
            long r4 = org.telegram.messenger.MessageObject.getPeerId(r15)
            r15 = 0
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r3 = r1.contactsMap
            java.lang.Object r3 = r3.get(r4)
            if (r3 != 0) goto L_0x014d
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r3 = r1.botsMap
            java.lang.Object r3 = r3.get(r4)
            if (r3 == 0) goto L_0x0120
            r19 = r6
            goto L_0x014f
        L_0x0120:
            int r3 = r1.selectType
            r19 = r6
            r6 = 1
            if (r3 != r6) goto L_0x0141
            r20 = 0
            int r3 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r3 <= 0) goto L_0x0141
            org.telegram.messenger.MessagesController r3 = r22.getMessagesController()
            java.lang.Long r6 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r6)
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r3)
            if (r3 == 0) goto L_0x0141
            r15 = 1
            goto L_0x0150
        L_0x0141:
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r1.ignoredUsers
            if (r3 == 0) goto L_0x0150
            int r3 = r3.indexOfKey(r4)
            if (r3 < 0) goto L_0x0150
            r15 = 1
            goto L_0x0150
        L_0x014d:
            r19 = r6
        L_0x014f:
            r15 = 1
        L_0x0150:
            if (r15 == 0) goto L_0x0160
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r1.participants
            r3.remove(r0)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r3 = r1.participantsMap
            r3.remove(r4)
            int r0 = r0 + -1
            int r7 = r7 + -1
        L_0x0160:
            r3 = 1
            int r0 = r0 + r3
            r3 = r23
            r5 = r18
            r6 = r19
            r4 = 2
            goto L_0x00e5
        L_0x016b:
            r18 = r5
            r19 = r6
            goto L_0x0174
        L_0x0170:
            r18 = r5
            r19 = r6
        L_0x0174:
            int r0 = r1.type     // Catch:{ Exception -> 0x01ab }
            if (r0 == 0) goto L_0x0182
            r3 = 3
            if (r0 == r3) goto L_0x0182
            r3 = 2
            if (r0 != r3) goto L_0x017f
            goto L_0x0182
        L_0x017f:
            r5 = r18
            goto L_0x019e
        L_0x0182:
            org.telegram.tgnet.TLRPC$Chat r0 = r1.currentChat     // Catch:{ Exception -> 0x01ab }
            if (r0 == 0) goto L_0x019c
            boolean r0 = r0.megagroup     // Catch:{ Exception -> 0x01ab }
            if (r0 == 0) goto L_0x019c
            org.telegram.tgnet.TLRPC$ChatFull r0 = r1.info     // Catch:{ Exception -> 0x01ab }
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelFull     // Catch:{ Exception -> 0x01ab }
            if (r3 == 0) goto L_0x019c
            int r0 = r0.participants_count     // Catch:{ Exception -> 0x01ab }
            r3 = 200(0xc8, float:2.8E-43)
            if (r0 > r3) goto L_0x019c
            r5 = r18
            r1.sortUsers(r5)     // Catch:{ Exception -> 0x01a9 }
            goto L_0x01a8
        L_0x019c:
            r5 = r18
        L_0x019e:
            int r0 = r1.type     // Catch:{ Exception -> 0x01a9 }
            r3 = 1
            if (r0 != r3) goto L_0x01a8
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r1.participants     // Catch:{ Exception -> 0x01a9 }
            r1.sortAdmins(r0)     // Catch:{ Exception -> 0x01a9 }
        L_0x01a8:
            goto L_0x01b1
        L_0x01a9:
            r0 = move-exception
            goto L_0x01ae
        L_0x01ab:
            r0 = move-exception
            r5 = r18
        L_0x01ae:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01b1:
            r0 = r19
            goto L_0x01b6
        L_0x01b4:
            r16 = r7
        L_0x01b6:
            int r2 = r2 + 1
            goto L_0x0004
        L_0x01ba:
            r8 = r24
            int r2 = r1.type
            r3 = 2
            if (r2 != r3) goto L_0x01c5
            int r2 = r1.delayResults
            if (r2 > 0) goto L_0x01ea
        L_0x01c5:
            org.telegram.ui.ChatUsersActivity$ListAdapter r2 = r1.listViewAdapter
            if (r2 == 0) goto L_0x01ce
            int r2 = r2.getItemCount()
            goto L_0x01cf
        L_0x01ce:
            r2 = 0
        L_0x01cf:
            r1.showItemsAnimated(r2)
            r1.loadingUsers = r5
            r2 = 1
            r1.firstLoaded = r2
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r1.searchItem
            if (r2 == 0) goto L_0x01ea
            int r3 = r1.type
            if (r3 != 0) goto L_0x01e6
            r3 = 5
            if (r0 <= r3) goto L_0x01e3
            goto L_0x01e6
        L_0x01e3:
            r3 = 8
            goto L_0x01e7
        L_0x01e6:
            r3 = 0
        L_0x01e7:
            r2.setVisibility(r3)
        L_0x01ea:
            r22.updateRows()
            org.telegram.ui.ChatUsersActivity$ListAdapter r2 = r1.listViewAdapter
            if (r2 == 0) goto L_0x0213
            org.telegram.ui.Components.RecyclerListView r2 = r1.listView
            boolean r3 = r1.openTransitionStarted
            r2.setAnimateEmptyView(r3, r5)
            org.telegram.ui.ChatUsersActivity$ListAdapter r2 = r1.listViewAdapter
            r2.notifyDataSetChanged()
            org.telegram.ui.Components.StickerEmptyView r2 = r1.emptyView
            if (r2 == 0) goto L_0x0213
            org.telegram.ui.ChatUsersActivity$ListAdapter r2 = r1.listViewAdapter
            int r2 = r2.getItemCount()
            if (r2 != 0) goto L_0x0213
            boolean r2 = r1.firstLoaded
            if (r2 == 0) goto L_0x0213
            org.telegram.ui.Components.StickerEmptyView r2 = r1.emptyView
            r3 = 1
            r2.showProgress(r5, r3)
        L_0x0213:
            r22.resumeDelayedFragmentAnimation()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.m3309lambda$loadChatParticipants$14$orgtelegramuiChatUsersActivity(java.util.ArrayList, java.util.ArrayList):void");
    }

    static /* synthetic */ void lambda$loadChatParticipants$15(TLRPC.TL_error error, TLObject response, ArrayList responses, int index, AtomicInteger responsesReceived, ArrayList requests, Runnable onRequestsEnd) {
        if (error == null && (response instanceof TLRPC.TL_channels_channelParticipants)) {
            responses.set(index, (TLRPC.TL_channels_channelParticipants) response);
        }
        responsesReceived.getAndIncrement();
        if (responsesReceived.get() == requests.size()) {
            onRequestsEnd.run();
        }
    }

    /* access modifiers changed from: private */
    public void sortUsers(ArrayList<TLObject> objects) {
        Collections.sort(objects, new ChatUsersActivity$$ExternalSyntheticLambda3(this, getConnectionsManager().getCurrentTime()));
    }

    /* renamed from: lambda$sortUsers$17$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ int m3313lambda$sortUsers$17$orgtelegramuiChatUsersActivity(int currentTime, TLObject lhs, TLObject rhs) {
        TLRPC.ChannelParticipant p1 = (TLRPC.ChannelParticipant) lhs;
        TLRPC.ChannelParticipant p2 = (TLRPC.ChannelParticipant) rhs;
        long peer1 = MessageObject.getPeerId(p1.peer);
        long peer2 = MessageObject.getPeerId(p2.peer);
        int status1 = 0;
        if (peer1 > 0) {
            TLRPC.User user1 = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(p1.peer)));
            if (!(user1 == null || user1.status == null)) {
                status1 = user1.self ? currentTime + 50000 : user1.status.expires;
            }
        } else {
            status1 = -100;
        }
        int status2 = 0;
        if (peer2 > 0) {
            TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(p2.peer)));
            if (!(user2 == null || user2.status == null)) {
                status2 = user2.self ? currentTime + 50000 : user2.status.expires;
            }
        } else {
            status2 = -100;
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.requestLayout();
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public int getSelectType() {
        return this.selectType;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.openTransitionStarted = true;
        }
        if (isOpen && !backward && this.needOpenSearch) {
            this.searchItem.getSearchField().requestFocus();
            AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
            this.searchItem.setVisibility(8);
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private boolean searchInProgress;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private LongSparseArray<TLObject> searchResultMap = new LongSparseArray<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int totalCount = 0;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda4(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ChatUsersActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3318lambda$new$0$orgtelegramuiChatUsersActivity$SearchAdapter(int searchId) {
            if (!this.searchAdapterHelper.isSearchInProgress()) {
                int oldItemCount = getItemCount();
                notifyDataSetChanged();
                if (getItemCount() > oldItemCount) {
                    ChatUsersActivity.this.showItemsAnimated(oldItemCount);
                }
                if (!this.searchInProgress && getItemCount() == 0 && searchId != 0) {
                    ChatUsersActivity.this.emptyView.showProgress(false, true);
                }
            }
        }

        public void searchUsers(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultMap.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, ChatUsersActivity.this.type != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(query)) {
                this.searchInProgress = true;
                ChatUsersActivity.this.emptyView.showProgress(true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1 chatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1 = new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1(this, query);
                this.searchRunnable = chatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1;
                dispatchQueue.postRunnable(chatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1, 300);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void m3322xvar_e43d8(String query) {
            AndroidUtilities.runOnUIThread(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda0(this, query));
        }

        /* renamed from: lambda$processSearch$3$org-telegram-ui-ChatUsersActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3321x148bavar_(String query) {
            ArrayList<TLRPC.TL_contact> contactsCopy = null;
            this.searchRunnable = null;
            ArrayList<TLObject> participantsCopy = (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) ? null : new ArrayList<>(ChatUsersActivity.this.info.participants.participants);
            if (ChatUsersActivity.this.selectType == 1) {
                contactsCopy = new ArrayList<>(ChatUsersActivity.this.getContactsController().contacts);
            }
            Runnable addContacts = null;
            if (participantsCopy == null && contactsCopy == null) {
                this.searchInProgress = false;
                String str = query;
            } else {
                addContacts = new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda2(this, query, participantsCopy, contactsCopy);
            }
            this.searchAdapterHelper.queryServerSearch(query, ChatUsersActivity.this.selectType != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type, 1, addContacts);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:46:0x014d, code lost:
            if (r3.contains(" " + r4) != false) goto L_0x015f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x0273, code lost:
            if (r6.contains(" " + r15) != false) goto L_0x0285;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x0162 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x0289 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x019f A[LOOP:1: B:37:0x010f->B:60:0x019f, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:99:0x02d2 A[LOOP:3: B:77:0x0239->B:99:0x02d2, LOOP_END] */
        /* renamed from: lambda$processSearch$2$org-telegram-ui-ChatUsersActivity$SearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m3320x15021510(java.lang.String r31, java.util.ArrayList r32, java.util.ArrayList r33) {
            /*
                r30 = this;
                r0 = r30
                r1 = r32
                r2 = r33
                java.lang.String r3 = r31.trim()
                java.lang.String r3 = r3.toLowerCase()
                int r4 = r3.length()
                if (r4 != 0) goto L_0x002c
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                androidx.collection.LongSparseArray r5 = new androidx.collection.LongSparseArray
                r5.<init>()
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r0.updateSearchResults(r4, r5, r6, r7)
                return
            L_0x002c:
                org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r4 = r4.getTranslitString(r3)
                boolean r5 = r3.equals(r4)
                if (r5 != 0) goto L_0x0040
                int r5 = r4.length()
                if (r5 != 0) goto L_0x0041
            L_0x0040:
                r4 = 0
            L_0x0041:
                r5 = 0
                r6 = 1
                if (r4 == 0) goto L_0x0047
                r7 = 1
                goto L_0x0048
            L_0x0047:
                r7 = 0
            L_0x0048:
                int r7 = r7 + r6
                java.lang.String[] r7 = new java.lang.String[r7]
                r7[r5] = r3
                if (r4 == 0) goto L_0x0051
                r7[r6] = r4
            L_0x0051:
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                androidx.collection.LongSparseArray r9 = new androidx.collection.LongSparseArray
                r9.<init>()
                java.util.ArrayList r10 = new java.util.ArrayList
                r10.<init>()
                java.util.ArrayList r11 = new java.util.ArrayList
                r11.<init>()
                java.lang.String r13 = "@"
                java.lang.String r14 = " "
                if (r1 == 0) goto L_0x01dc
                r15 = 0
                int r5 = r32.size()
            L_0x0070:
                if (r15 >= r5) goto L_0x01cf
                java.lang.Object r16 = r1.get(r15)
                r12 = r16
                org.telegram.tgnet.TLObject r12 = (org.telegram.tgnet.TLObject) r12
                boolean r6 = r12 instanceof org.telegram.tgnet.TLRPC.ChatParticipant
                if (r6 == 0) goto L_0x0088
                r6 = r12
                org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC.ChatParticipant) r6
                r18 = r3
                r19 = r4
                long r3 = r6.user_id
                goto L_0x0099
            L_0x0088:
                r18 = r3
                r19 = r4
                boolean r3 = r12 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant
                if (r3 == 0) goto L_0x01b4
                r3 = r12
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r3
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer
                long r3 = org.telegram.messenger.MessageObject.getPeerId(r3)
            L_0x0099:
                r20 = 0
                int r6 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
                if (r6 <= 0) goto L_0x00d6
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                java.lang.Long r1 = java.lang.Long.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r1 = r6.getUser(r1)
                r20 = r5
                long r5 = r1.id
                r21 = r9
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.UserConfig r9 = r9.getUserConfig()
                long r22 = r9.getClientUserId()
                int r9 = (r5 > r22 ? 1 : (r5 == r22 ? 0 : -1))
                if (r9 != 0) goto L_0x00c7
                r27 = r7
                r25 = r8
                goto L_0x01bc
            L_0x00c7:
                java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r1)
                java.lang.String r5 = r5.toLowerCase()
                java.lang.String r6 = r1.username
                java.lang.String r9 = r1.first_name
                java.lang.String r1 = r1.last_name
                goto L_0x00f7
            L_0x00d6:
                r20 = r5
                r21 = r9
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
                long r5 = -r3
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r5)
                java.lang.String r5 = r1.title
                java.lang.String r5 = r5.toLowerCase()
                java.lang.String r6 = r1.username
                java.lang.String r9 = r1.title
                r22 = 0
                r1 = r22
            L_0x00f7:
                r22 = r3
                org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r3 = r3.getTranslitString(r5)
                boolean r4 = r5.equals(r3)
                if (r4 == 0) goto L_0x0108
                r3 = 0
            L_0x0108:
                r4 = 0
                r24 = r4
                int r4 = r7.length
                r25 = r8
                r8 = 0
            L_0x010f:
                if (r8 >= r4) goto L_0x01ad
                r26 = r4
                r4 = r7[r8]
                boolean r27 = r5.startsWith(r4)
                if (r27 != 0) goto L_0x015d
                r27 = r7
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r14)
                r7.append(r4)
                java.lang.String r7 = r7.toString()
                boolean r7 = r5.contains(r7)
                if (r7 != 0) goto L_0x015f
                if (r3 == 0) goto L_0x0150
                boolean r7 = r3.startsWith(r4)
                if (r7 != 0) goto L_0x015f
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r14)
                r7.append(r4)
                java.lang.String r7 = r7.toString()
                boolean r7 = r3.contains(r7)
                if (r7 == 0) goto L_0x0150
                goto L_0x015f
            L_0x0150:
                if (r6 == 0) goto L_0x015a
                boolean r7 = r6.startsWith(r4)
                if (r7 == 0) goto L_0x015a
                r7 = 2
                goto L_0x0160
            L_0x015a:
                r7 = r24
                goto L_0x0160
            L_0x015d:
                r27 = r7
            L_0x015f:
                r7 = 1
            L_0x0160:
                if (r7 == 0) goto L_0x019f
                r8 = 1
                if (r7 != r8) goto L_0x0171
                java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r1, r4)
                r10.add(r8)
                r28 = r1
                r29 = r3
                goto L_0x019b
            L_0x0171:
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r13)
                r8.append(r6)
                java.lang.String r8 = r8.toString()
                r28 = r1
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r13)
                r1.append(r4)
                java.lang.String r1 = r1.toString()
                r29 = r3
                r3 = 0
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r8, r3, r1)
                r10.add(r1)
            L_0x019b:
                r11.add(r12)
                goto L_0x01bc
            L_0x019f:
                r28 = r1
                r29 = r3
                int r8 = r8 + 1
                r24 = r7
                r4 = r26
                r7 = r27
                goto L_0x010f
            L_0x01ad:
                r28 = r1
                r29 = r3
                r27 = r7
                goto L_0x01bc
            L_0x01b4:
                r20 = r5
                r27 = r7
                r25 = r8
                r21 = r9
            L_0x01bc:
                int r15 = r15 + 1
                r1 = r32
                r3 = r18
                r4 = r19
                r5 = r20
                r9 = r21
                r8 = r25
                r7 = r27
                r6 = 1
                goto L_0x0070
            L_0x01cf:
                r18 = r3
                r19 = r4
                r20 = r5
                r27 = r7
                r25 = r8
                r21 = r9
                goto L_0x01e6
            L_0x01dc:
                r18 = r3
                r19 = r4
                r27 = r7
                r25 = r8
                r21 = r9
            L_0x01e6:
                if (r2 == 0) goto L_0x02f9
                r1 = 0
            L_0x01e9:
                int r3 = r33.size()
                if (r1 >= r3) goto L_0x02f2
                java.lang.Object r3 = r2.get(r1)
                org.telegram.tgnet.TLRPC$TL_contact r3 = (org.telegram.tgnet.TLRPC.TL_contact) r3
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                long r5 = r3.user_id
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
                long r5 = r4.id
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.UserConfig r7 = r7.getUserConfig()
                long r7 = r7.getClientUserId()
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 != 0) goto L_0x021d
                r3 = r21
                r2 = r25
                r8 = r27
                goto L_0x02e6
            L_0x021d:
                java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r4)
                java.lang.String r5 = r5.toLowerCase()
                org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r6 = r6.getTranslitString(r5)
                boolean r7 = r5.equals(r6)
                if (r7 == 0) goto L_0x0234
                r6 = 0
            L_0x0234:
                r7 = 0
                r8 = r27
                int r9 = r8.length
                r12 = 0
            L_0x0239:
                if (r12 >= r9) goto L_0x02e0
                r15 = r8[r12]
                boolean r20 = r5.startsWith(r15)
                if (r20 != 0) goto L_0x0285
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r2.append(r14)
                r2.append(r15)
                java.lang.String r2 = r2.toString()
                boolean r2 = r5.contains(r2)
                if (r2 != 0) goto L_0x0285
                if (r6 == 0) goto L_0x0276
                boolean r2 = r6.startsWith(r15)
                if (r2 != 0) goto L_0x0285
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r2.append(r14)
                r2.append(r15)
                java.lang.String r2 = r2.toString()
                boolean r2 = r6.contains(r2)
                if (r2 == 0) goto L_0x0276
                goto L_0x0285
            L_0x0276:
                java.lang.String r2 = r4.username
                if (r2 == 0) goto L_0x0287
                java.lang.String r2 = r4.username
                boolean r2 = r2.startsWith(r15)
                if (r2 == 0) goto L_0x0287
                r2 = 2
                r7 = r2
                goto L_0x0287
            L_0x0285:
                r2 = 1
                r7 = r2
            L_0x0287:
                if (r7 == 0) goto L_0x02d2
                r2 = 1
                if (r7 != r2) goto L_0x0299
                java.lang.String r9 = r4.first_name
                java.lang.String r12 = r4.last_name
                java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r12, r15)
                r10.add(r9)
                r2 = 0
                goto L_0x02c1
            L_0x0299:
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                r9.append(r13)
                java.lang.String r12 = r4.username
                r9.append(r12)
                java.lang.String r9 = r9.toString()
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r13)
                r12.append(r15)
                java.lang.String r12 = r12.toString()
                r2 = 0
                java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r2, r12)
                r10.add(r9)
            L_0x02c1:
                r9 = r25
                r9.add(r4)
                r17 = r3
                long r2 = r4.id
                r12 = r21
                r12.put(r2, r4)
                r2 = r9
                r3 = r12
                goto L_0x02e6
            L_0x02d2:
                r17 = r3
                r3 = r21
                r2 = r25
                int r12 = r12 + 1
                r3 = r17
                r2 = r33
                goto L_0x0239
            L_0x02e0:
                r17 = r3
                r3 = r21
                r2 = r25
            L_0x02e6:
                int r1 = r1 + 1
                r25 = r2
                r21 = r3
                r27 = r8
                r2 = r33
                goto L_0x01e9
            L_0x02f2:
                r3 = r21
                r2 = r25
                r8 = r27
                goto L_0x02ff
            L_0x02f9:
                r3 = r21
                r2 = r25
                r8 = r27
            L_0x02ff:
                r0.updateSearchResults(r2, r3, r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.m3320x15021510(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<Object> users, LongSparseArray<TLObject> usersMap, ArrayList<CharSequence> names, ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda3(this, users, usersMap, names, participants));
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-ChatUsersActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3323x4d77140(ArrayList users, LongSparseArray usersMap, ArrayList names, ArrayList participants) {
            if (ChatUsersActivity.this.searching) {
                this.searchInProgress = false;
                this.searchResult = users;
                this.searchResultMap = usersMap;
                this.searchResultNames = names;
                this.searchAdapterHelper.mergeResults(users);
                if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                    ArrayList<TLObject> search = this.searchAdapterHelper.getGroupSearch();
                    search.clear();
                    search.addAll(participants);
                }
                int oldItemCount = getItemCount();
                notifyDataSetChanged();
                if (getItemCount() > oldItemCount) {
                    ChatUsersActivity.this.showItemsAnimated(oldItemCount);
                }
                if (!this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                    ChatUsersActivity.this.emptyView.showProgress(false, true);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.totalCount;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                this.groupStartRow = 0;
                this.totalCount += count + 1;
            } else {
                this.groupStartRow = -1;
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                int i = this.totalCount;
                this.contactsStartRow = i;
                this.totalCount = i + count2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + count3 + 1;
            } else {
                this.globalStartRow = -1;
            }
            if (!(!ChatUsersActivity.this.searching || ChatUsersActivity.this.listView == null || ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter)) {
                ChatUsersActivity.this.listView.setAnimateEmptyView(true, 0);
                ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                ChatUsersActivity.this.listView.setFastScrollVisible(false);
                ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
            }
            super.notifyDataSetChanged();
        }

        public void removeUserId(long userId) {
            this.searchAdapterHelper.removeUserId(userId);
            Object object = this.searchResultMap.get(userId);
            if (object != null) {
                this.searchResult.remove(object);
            }
            notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                if (count + 1 <= i) {
                    i -= count + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return this.searchAdapterHelper.getGroupSearch().get(i - 1);
                }
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                if (count2 + 1 <= i) {
                    i -= count2 + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchResult.get(i - 1);
                }
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 == 0 || count3 + 1 <= i || i == 0) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                    manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    manageChatUserCell.setDelegate(new ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda5(this));
                    view = manageChatUserCell;
                    break;
                default:
                    view = new GraySectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-ChatUsersActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ boolean m3319xcd403d42(ManageChatUserCell cell, boolean click) {
            TLObject object = getItem(((Integer) cell.getTag()).intValue());
            if (!(object instanceof TLRPC.ChannelParticipant)) {
                return false;
            }
            return ChatUsersActivity.this.createMenuForParticipant((TLRPC.ChannelParticipant) object, !click);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v12, resolved type: java.lang.CharSequence} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0214  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                r20 = this;
                r1 = r20
                r2 = r21
                r0 = r22
                int r3 = r21.getItemViewType()
                switch(r3) {
                    case 0: goto L_0x008c;
                    case 1: goto L_0x000f;
                    default: goto L_0x000d;
                }
            L_0x000d:
                goto L_0x023c
            L_0x000f:
                android.view.View r3 = r2.itemView
                org.telegram.ui.Cells.GraySectionCell r3 = (org.telegram.ui.Cells.GraySectionCell) r3
                int r4 = r1.groupStartRow
                if (r0 != r4) goto L_0x0068
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x002d
                r4 = 2131624884(0x7f0e03b4, float:1.887696E38)
                java.lang.String r5 = "ChannelBlockedUsers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x023c
            L_0x002d:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 3
                if (r4 != r5) goto L_0x0044
                r4 = 2131624955(0x7f0e03fb, float:1.8877104E38)
                java.lang.String r5 = "ChannelRestrictedUsers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x023c
            L_0x0044:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x005a
                r4 = 2131624968(0x7f0e0408, float:1.887713E38)
                java.lang.String r5 = "ChannelSubscribers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x023c
            L_0x005a:
                r4 = 2131624917(0x7f0e03d5, float:1.8877027E38)
                java.lang.String r5 = "ChannelMembers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x023c
            L_0x0068:
                int r4 = r1.globalStartRow
                if (r0 != r4) goto L_0x007a
                r4 = 2131626079(0x7f0e085f, float:1.8879384E38)
                java.lang.String r5 = "GlobalSearch"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x023c
            L_0x007a:
                int r4 = r1.contactsStartRow
                if (r0 != r4) goto L_0x023c
                r4 = 2131625242(0x7f0e051a, float:1.8877686E38)
                java.lang.String r5 = "Contacts"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x023c
            L_0x008c:
                org.telegram.tgnet.TLObject r3 = r1.getItem(r0)
                r4 = 0
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.User
                if (r5 == 0) goto L_0x0097
                r5 = r3
                goto L_0x00ec
            L_0x0097:
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant
                if (r5 == 0) goto L_0x00d5
                r5 = r3
                org.telegram.tgnet.TLRPC$ChannelParticipant r5 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r5
                org.telegram.tgnet.TLRPC$Peer r5 = r5.peer
                long r5 = org.telegram.messenger.MessageObject.getPeerId(r5)
                r7 = 0
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r9 < 0) goto L_0x00bf
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                java.lang.Long r8 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)
                if (r7 == 0) goto L_0x00bc
                java.lang.String r4 = r7.username
            L_0x00bc:
                r5 = r7
                goto L_0x00d4
            L_0x00bf:
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                long r8 = -r5
                java.lang.Long r8 = java.lang.Long.valueOf(r8)
                org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r8)
                if (r7 == 0) goto L_0x00d2
                java.lang.String r4 = r7.username
            L_0x00d2:
                r8 = r7
                r5 = r8
            L_0x00d4:
                goto L_0x00ec
            L_0x00d5:
                boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.ChatParticipant
                if (r5 == 0) goto L_0x023b
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                r6 = r3
                org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC.ChatParticipant) r6
                long r6 = r6.user_id
                java.lang.Long r6 = java.lang.Long.valueOf(r6)
                org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            L_0x00ec:
                r6 = 0
                r7 = 0
                org.telegram.ui.Adapters.SearchAdapterHelper r8 = r1.searchAdapterHelper
                java.util.ArrayList r8 = r8.getGroupSearch()
                int r8 = r8.size()
                r9 = 0
                r10 = 0
                if (r8 == 0) goto L_0x010b
                int r11 = r8 + 1
                if (r11 <= r0) goto L_0x0108
                org.telegram.ui.Adapters.SearchAdapterHelper r11 = r1.searchAdapterHelper
                java.lang.String r10 = r11.getLastFoundChannel()
                r9 = 1
                goto L_0x010b
            L_0x0108:
                int r11 = r8 + 1
                int r0 = r0 - r11
            L_0x010b:
                java.lang.String r11 = "@"
                if (r9 != 0) goto L_0x016d
                java.util.ArrayList<java.lang.Object> r12 = r1.searchResult
                int r8 = r12.size()
                if (r8 == 0) goto L_0x0165
                int r12 = r8 + 1
                if (r12 <= r0) goto L_0x015a
                r9 = 1
                java.util.ArrayList<java.lang.CharSequence> r12 = r1.searchResultNames
                int r13 = r0 + -1
                java.lang.Object r12 = r12.get(r13)
                r7 = r12
                java.lang.CharSequence r7 = (java.lang.CharSequence) r7
                if (r7 == 0) goto L_0x0152
                boolean r12 = android.text.TextUtils.isEmpty(r4)
                if (r12 != 0) goto L_0x0152
                java.lang.String r12 = r7.toString()
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                r13.append(r11)
                r13.append(r4)
                java.lang.String r13 = r13.toString()
                boolean r12 = r12.startsWith(r13)
                if (r12 == 0) goto L_0x0152
                r6 = r7
                r7 = 0
                r19 = r6
                r6 = r0
                r0 = r8
                r8 = r7
                r7 = r19
                goto L_0x0174
            L_0x0152:
                r19 = r6
                r6 = r0
                r0 = r8
                r8 = r7
                r7 = r19
                goto L_0x0174
            L_0x015a:
                int r12 = r8 + 1
                int r0 = r0 - r12
                r19 = r6
                r6 = r0
                r0 = r8
                r8 = r7
                r7 = r19
                goto L_0x0174
            L_0x0165:
                r19 = r6
                r6 = r0
                r0 = r8
                r8 = r7
                r7 = r19
                goto L_0x0174
            L_0x016d:
                r19 = r6
                r6 = r0
                r0 = r8
                r8 = r7
                r7 = r19
            L_0x0174:
                java.lang.String r13 = "windowBackgroundWhiteBlueText4"
                r14 = -1
                if (r9 != 0) goto L_0x01fd
                if (r4 == 0) goto L_0x01fd
                org.telegram.ui.Adapters.SearchAdapterHelper r15 = r1.searchAdapterHelper
                java.util.ArrayList r15 = r15.getGlobalSearch()
                int r15 = r15.size()
                if (r15 == 0) goto L_0x01f5
                int r0 = r15 + 1
                if (r0 <= r6) goto L_0x01f0
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                boolean r16 = r0.startsWith(r11)
                if (r16 == 0) goto L_0x019e
                r12 = 1
                java.lang.String r0 = r0.substring(r12)
                r12 = r0
                goto L_0x019f
            L_0x019e:
                r12 = r0
            L_0x019f:
                android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x01e5 }
                r0.<init>()     // Catch:{ Exception -> 0x01e5 }
                r0.append(r11)     // Catch:{ Exception -> 0x01e5 }
                r0.append(r4)     // Catch:{ Exception -> 0x01e5 }
                int r11 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r4, r12)     // Catch:{ Exception -> 0x01e5 }
                r16 = r11
                if (r11 == r14) goto L_0x01de
                int r11 = r12.length()     // Catch:{ Exception -> 0x01e5 }
                if (r16 != 0) goto L_0x01bd
                int r11 = r11 + 1
                r14 = r16
                goto L_0x01c1
            L_0x01bd:
                int r16 = r16 + 1
                r14 = r16
            L_0x01c1:
                android.text.style.ForegroundColorSpan r1 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x01e5 }
                r17 = r3
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r13)     // Catch:{ Exception -> 0x01da }
                r1.<init>(r3)     // Catch:{ Exception -> 0x01da }
                int r3 = r14 + r11
                r18 = r7
                r7 = 33
                r0.setSpan(r1, r14, r3, r7)     // Catch:{ Exception -> 0x01d8 }
                r16 = r14
                goto L_0x01e2
            L_0x01d8:
                r0 = move-exception
                goto L_0x01ea
            L_0x01da:
                r0 = move-exception
                r18 = r7
                goto L_0x01ea
            L_0x01de:
                r17 = r3
                r18 = r7
            L_0x01e2:
                r7 = r0
                r0 = r15
                goto L_0x0203
            L_0x01e5:
                r0 = move-exception
                r17 = r3
                r18 = r7
            L_0x01ea:
                r7 = r4
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r15
                goto L_0x0203
            L_0x01f0:
                r17 = r3
                r18 = r7
                goto L_0x01f9
            L_0x01f5:
                r17 = r3
                r18 = r7
            L_0x01f9:
                r0 = r15
                r7 = r18
                goto L_0x0203
            L_0x01fd:
                r17 = r3
                r18 = r7
                r7 = r18
            L_0x0203:
                if (r10 == 0) goto L_0x022a
                if (r4 == 0) goto L_0x022a
                android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
                r1.<init>(r4)
                r8 = r1
                int r1 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r4, r10)
                r3 = -1
                if (r1 == r3) goto L_0x022a
                r3 = r8
                android.text.SpannableStringBuilder r3 = (android.text.SpannableStringBuilder) r3
                android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r11.<init>(r12)
                int r12 = r10.length()
                int r12 = r12 + r1
                r13 = 33
                r3.setSpan(r11, r1, r12, r13)
            L_0x022a:
                android.view.View r1 = r2.itemView
                org.telegram.ui.Cells.ManageChatUserCell r1 = (org.telegram.ui.Cells.ManageChatUserCell) r1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
                r1.setTag(r3)
                r3 = 0
                r1.setData(r5, r8, r7, r3)
                r0 = r6
                goto L_0x023c
            L_0x023b:
                return
            L_0x023c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) {
                return 1;
            }
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == 7) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            if (viewType == 0) {
                Object object = ((ManageChatUserCell) holder.itemView).getCurrentObject();
                return ChatUsersActivity.this.type == 1 || !(object instanceof TLRPC.User) || !((TLRPC.User) object).self;
            } else if (viewType == 0 || viewType == 2 || viewType == 6) {
                return true;
            } else {
                return false;
            }
        }

        public int getItemCount() {
            return ChatUsersActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: org.telegram.ui.ChatUsersActivity$ChooseView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v21, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v23, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v24, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v25, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v28, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v29, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                r0 = 0
                r1 = 1
                r2 = 6
                java.lang.String r3 = "windowBackgroundWhite"
                switch(r12) {
                    case 0: goto L_0x00fc;
                    case 1: goto L_0x00f4;
                    case 2: goto L_0x00e5;
                    case 3: goto L_0x00dd;
                    case 4: goto L_0x00a6;
                    case 5: goto L_0x0088;
                    case 6: goto L_0x0078;
                    case 7: goto L_0x0068;
                    case 8: goto L_0x005b;
                    case 9: goto L_0x0008;
                    case 10: goto L_0x0046;
                    case 11: goto L_0x001a;
                    default: goto L_0x0008;
                }
            L_0x0008:
                org.telegram.ui.ChatUsersActivity$ChooseView r0 = new org.telegram.ui.ChatUsersActivity$ChooseView
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                android.content.Context r2 = r10.mContext
                r0.<init>(r2)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setBackgroundColor(r1)
                goto L_0x0145
            L_0x001a:
                org.telegram.ui.Components.FlickerLoadingView r4 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r5 = r10.mContext
                r4.<init>(r5)
                r4.setIsSingleCell(r1)
                r4.setViewType(r2)
                r4.showDate(r0)
                r0 = 1084227584(0x40a00000, float:5.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r4.setPaddingLeft(r0)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r4.setBackgroundColor(r0)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -1
                r0.<init>((int) r1, (int) r1)
                r4.setLayoutParams(r0)
                r0 = r4
                goto L_0x0145
            L_0x0046:
                org.telegram.ui.Cells.LoadingCell r0 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r1 = r10.mContext
                r2 = 1109393408(0x42200000, float:40.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r3 = 1123024896(0x42var_, float:120.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r0.<init>(r1, r2, r3)
                goto L_0x0145
            L_0x005b:
                org.telegram.ui.Cells.GraySectionCell r0 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                r1 = 0
                r0.setBackground(r1)
                goto L_0x0145
            L_0x0068:
                org.telegram.ui.Cells.TextCheckCell2 r0 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setBackgroundColor(r1)
                goto L_0x0145
            L_0x0078:
                org.telegram.ui.Cells.TextSettingsCell r0 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setBackgroundColor(r1)
                goto L_0x0145
            L_0x0088:
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r5 = r10.mContext
                r7 = 21
                r8 = 11
                r9 = 0
                java.lang.String r6 = "windowBackgroundWhiteBlueHeader"
                r4 = r0
                r4.<init>(r5, r6, r7, r8, r9)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setBackgroundColor(r1)
                r1 = 43
                r0.setHeight(r1)
                r1 = r0
                goto L_0x0145
            L_0x00a6:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                r1 = r0
                org.telegram.ui.Cells.TextInfoPrivacyCell r1 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r1
                org.telegram.ui.ChatUsersActivity r2 = org.telegram.ui.ChatUsersActivity.this
                boolean r2 = r2.isChannel
                if (r2 == 0) goto L_0x00c3
                r2 = 2131626808(0x7f0e0b38, float:1.8880863E38)
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString((int) r2)
                r1.setText(r2)
                goto L_0x00cd
            L_0x00c3:
                r2 = 2131626809(0x7f0e0b39, float:1.8880865E38)
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString((int) r2)
                r1.setText(r2)
            L_0x00cd:
                android.content.Context r2 = r10.mContext
                r3 = 2131165436(0x7var_fc, float:1.794509E38)
                java.lang.String r4 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
                r1.setBackground(r2)
                goto L_0x0145
            L_0x00dd:
                org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x0145
            L_0x00e5:
                org.telegram.ui.Cells.ManageChatTextCell r0 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setBackgroundColor(r1)
                goto L_0x0145
            L_0x00f4:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x0145
            L_0x00fc:
                org.telegram.ui.Cells.ManageChatUserCell r4 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r5 = r10.mContext
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                int r6 = r6.type
                r7 = 3
                if (r6 == 0) goto L_0x0114
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                int r6 = r6.type
                if (r6 != r7) goto L_0x0112
                goto L_0x0114
            L_0x0112:
                r6 = 6
                goto L_0x0115
            L_0x0114:
                r6 = 7
            L_0x0115:
                org.telegram.ui.ChatUsersActivity r8 = org.telegram.ui.ChatUsersActivity.this
                int r8 = r8.type
                if (r8 == 0) goto L_0x0127
                org.telegram.ui.ChatUsersActivity r8 = org.telegram.ui.ChatUsersActivity.this
                int r8 = r8.type
                if (r8 != r7) goto L_0x0126
                goto L_0x0127
            L_0x0126:
                r2 = 2
            L_0x0127:
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.selectType
                if (r7 != 0) goto L_0x0130
                r0 = 1
            L_0x0130:
                r4.<init>(r5, r6, r2, r0)
                r0 = r4
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setBackgroundColor(r1)
                org.telegram.ui.ChatUsersActivity$ListAdapter$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.ChatUsersActivity$ListAdapter$$ExternalSyntheticLambda0
                r1.<init>(r10)
                r0.setDelegate(r1)
                r1 = r0
            L_0x0145:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-ChatUsersActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m3317xfCLASSNAME(ManageChatUserCell cell, boolean click) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) cell.getTag()).intValue()), !click);
        }

        /* JADX WARNING: Removed duplicated region for block: B:293:0x079a  */
        /* JADX WARNING: Removed duplicated region for block: B:294:0x079c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r28, int r29) {
            /*
                r27 = this;
                r0 = r27
                r1 = r28
                r2 = r29
                int r3 = r28.getItemViewType()
                r4 = 2131165435(0x7var_fb, float:1.7945087E38)
                java.lang.String r5 = ""
                r6 = 2
                r7 = 2131165436(0x7var_fc, float:1.794509E38)
                r8 = 3
                java.lang.String r9 = "windowBackgroundGrayShadow"
                r10 = -1
                r11 = 0
                r12 = 0
                r13 = 1
                switch(r3) {
                    case 0: goto L_0x064c;
                    case 1: goto L_0x052c;
                    case 2: goto L_0x03d4;
                    case 3: goto L_0x0391;
                    case 4: goto L_0x001d;
                    case 5: goto L_0x02f6;
                    case 6: goto L_0x02c4;
                    case 7: goto L_0x00e1;
                    case 8: goto L_0x0046;
                    case 9: goto L_0x001d;
                    case 10: goto L_0x001d;
                    case 11: goto L_0x001f;
                    default: goto L_0x001d;
                }
            L_0x001d:
                goto L_0x0842
            L_0x001f:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Components.FlickerLoadingView r3 = (org.telegram.ui.Components.FlickerLoadingView) r3
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x0041
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                if (r4 != 0) goto L_0x0034
                goto L_0x003c
            L_0x0034:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                int r13 = r4.kicked_count
            L_0x003c:
                r3.setItemsCount(r13)
                goto L_0x0842
            L_0x0041:
                r3.setItemsCount(r13)
                goto L_0x0842
            L_0x0046:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.GraySectionCell r3 = (org.telegram.ui.Cells.GraySectionCell) r3
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.membersHeaderRow
                if (r2 != r4) goto L_0x0084
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
                if (r4 == 0) goto L_0x0076
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = r4.megagroup
                if (r4 != 0) goto L_0x0076
                r4 = 2131624941(0x7f0e03ed, float:1.8877076E38)
                java.lang.String r5 = "ChannelOtherSubscribers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x0076:
                r4 = 2131624939(0x7f0e03eb, float:1.8877072E38)
                java.lang.String r5 = "ChannelOtherMembers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x0084:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.botHeaderRow
                if (r2 != r4) goto L_0x009a
                r4 = 2131624885(0x7f0e03b5, float:1.8876962E38)
                java.lang.String r5 = "ChannelBots"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x009a:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.contactsHeaderRow
                if (r2 != r4) goto L_0x00d4
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
                if (r4 == 0) goto L_0x00c6
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = r4.megagroup
                if (r4 != 0) goto L_0x00c6
                r4 = 2131624893(0x7f0e03bd, float:1.8876979E38)
                java.lang.String r5 = "ChannelContacts"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x00c6:
                r4 = 2131626089(0x7f0e0869, float:1.8879404E38)
                java.lang.String r5 = "GroupContacts"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x00d4:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.loadingHeaderRow
                if (r2 != r4) goto L_0x0842
                r3.setText(r5)
                goto L_0x0842
            L_0x00e1:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.TextCheckCell2 r3 = (org.telegram.ui.Cells.TextCheckCell2) r3
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.changeInfoRow
                if (r2 != r4) goto L_0x0116
                r4 = 2131628819(0x7f0e1313, float:1.8884941E38)
                java.lang.String r5 = "UserRestrictionsChangeInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.change_info
                if (r5 != 0) goto L_0x0110
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r5 = r5.currentChat
                java.lang.String r5 = r5.username
                boolean r5 = android.text.TextUtils.isEmpty(r5)
                if (r5 == 0) goto L_0x0110
                r5 = 1
                goto L_0x0111
            L_0x0110:
                r5 = 0
            L_0x0111:
                r3.setTextAndCheck(r4, r5, r12)
                goto L_0x01fc
            L_0x0116:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.addUsersRow
                if (r2 != r4) goto L_0x0135
                r4 = 2131628824(0x7f0e1318, float:1.8884952E38)
                java.lang.String r5 = "UserRestrictionsInviteUsers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.invite_users
                r5 = r5 ^ r13
                r3.setTextAndCheck(r4, r5, r13)
                goto L_0x01fc
            L_0x0135:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.pinMessagesRow
                if (r2 != r4) goto L_0x0166
                r4 = 2131628834(0x7f0e1322, float:1.8884972E38)
                java.lang.String r5 = "UserRestrictionsPinMessages"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.pin_messages
                if (r5 != 0) goto L_0x0160
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r5 = r5.currentChat
                java.lang.String r5 = r5.username
                boolean r5 = android.text.TextUtils.isEmpty(r5)
                if (r5 == 0) goto L_0x0160
                r5 = 1
                goto L_0x0161
            L_0x0160:
                r5 = 0
            L_0x0161:
                r3.setTextAndCheck(r4, r5, r13)
                goto L_0x01fc
            L_0x0166:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendMessagesRow
                if (r2 != r4) goto L_0x0185
                r4 = 2131628836(0x7f0e1324, float:1.8884976E38)
                java.lang.String r5 = "UserRestrictionsSend"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.send_messages
                r5 = r5 ^ r13
                r3.setTextAndCheck(r4, r5, r13)
                goto L_0x01fc
            L_0x0185:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendMediaRow
                if (r2 != r4) goto L_0x01a3
                r4 = 2131628837(0x7f0e1325, float:1.8884978E38)
                java.lang.String r5 = "UserRestrictionsSendMedia"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.send_media
                r5 = r5 ^ r13
                r3.setTextAndCheck(r4, r5, r13)
                goto L_0x01fc
            L_0x01a3:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendStickersRow
                if (r2 != r4) goto L_0x01c1
                r4 = 2131628839(0x7f0e1327, float:1.8884982E38)
                java.lang.String r5 = "UserRestrictionsSendStickers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.send_stickers
                r5 = r5 ^ r13
                r3.setTextAndCheck(r4, r5, r13)
                goto L_0x01fc
            L_0x01c1:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.embedLinksRow
                if (r2 != r4) goto L_0x01df
                r4 = 2131628823(0x7f0e1317, float:1.888495E38)
                java.lang.String r5 = "UserRestrictionsEmbedLinks"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.embed_links
                r5 = r5 ^ r13
                r3.setTextAndCheck(r4, r5, r13)
                goto L_0x01fc
            L_0x01df:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendPollsRow
                if (r2 != r4) goto L_0x01fc
                r4 = 2131628838(0x7f0e1326, float:1.888498E38)
                java.lang.String r5 = "UserRestrictionsSendPolls"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.defaultBannedRights
                boolean r5 = r5.send_polls
                r5 = r5 ^ r13
                r3.setTextAndCheck(r4, r5, r13)
            L_0x01fc:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendMediaRow
                if (r2 == r4) goto L_0x0232
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendStickersRow
                if (r2 == r4) goto L_0x0232
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.embedLinksRow
                if (r2 == r4) goto L_0x0232
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendPollsRow
                if (r2 != r4) goto L_0x021d
                goto L_0x0232
            L_0x021d:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.sendMessagesRow
                if (r2 != r4) goto L_0x024c
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.view_messages
                r4 = r4 ^ r13
                r3.setEnabled(r4)
                goto L_0x024c
            L_0x0232:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.send_messages
                if (r4 != 0) goto L_0x0248
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.defaultBannedRights
                boolean r4 = r4.view_messages
                if (r4 != 0) goto L_0x0248
                r4 = 1
                goto L_0x0249
            L_0x0248:
                r4 = 0
            L_0x0249:
                r3.setEnabled(r4)
            L_0x024c:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.canBlockUsers(r4)
                if (r4 == 0) goto L_0x02bf
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.addUsersRow
                if (r2 != r4) goto L_0x026c
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r4, r8)
                if (r4 == 0) goto L_0x02b2
            L_0x026c:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.pinMessagesRow
                if (r2 != r4) goto L_0x0280
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r4, r12)
                if (r4 == 0) goto L_0x02b2
            L_0x0280:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.changeInfoRow
                if (r2 != r4) goto L_0x0294
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r4, r13)
                if (r4 == 0) goto L_0x02b2
            L_0x0294:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                java.lang.String r4 = r4.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x02ba
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.pinMessagesRow
                if (r2 == r4) goto L_0x02b2
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.changeInfoRow
                if (r2 != r4) goto L_0x02ba
            L_0x02b2:
                r4 = 2131166033(0x7var_, float:1.79463E38)
                r3.setIcon(r4)
                goto L_0x0842
            L_0x02ba:
                r3.setIcon(r12)
                goto L_0x0842
            L_0x02bf:
                r3.setIcon(r12)
                goto L_0x0842
            L_0x02c4:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.TextSettingsCell r3 = (org.telegram.ui.Cells.TextSettingsCell) r3
                r4 = 2131624882(0x7f0e03b2, float:1.8876956E38)
                java.lang.String r5 = "ChannelBlacklist"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                java.lang.Object[] r5 = new java.lang.Object[r13]
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r6 = r6.info
                if (r6 == 0) goto L_0x02e4
                org.telegram.ui.ChatUsersActivity r6 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r6 = r6.info
                int r6 = r6.kicked_count
                goto L_0x02e5
            L_0x02e4:
                r6 = 0
            L_0x02e5:
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5[r12] = r6
                java.lang.String r6 = "%d"
                java.lang.String r5 = java.lang.String.format(r6, r5)
                r3.setTextAndValue(r4, r5, r12)
                goto L_0x0842
            L_0x02f6:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.HeaderCell r3 = (org.telegram.ui.Cells.HeaderCell) r3
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.restricted1SectionRow
                if (r2 != r4) goto L_0x034f
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x0341
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                if (r4 == 0) goto L_0x031b
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r4 = r4.info
                int r4 = r4.kicked_count
                goto L_0x0325
            L_0x031b:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r4 = r4.participants
                int r4 = r4.size()
            L_0x0325:
                if (r4 == 0) goto L_0x0333
                java.lang.Object[] r5 = new java.lang.Object[r12]
                java.lang.String r6 = "RemovedUser"
                java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r4, r5)
                r3.setText(r5)
                goto L_0x033f
            L_0x0333:
                r5 = 2131624884(0x7f0e03b4, float:1.887696E38)
                java.lang.String r6 = "ChannelBlockedUsers"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r3.setText(r5)
            L_0x033f:
                goto L_0x0842
            L_0x0341:
                r4 = 2131624955(0x7f0e03fb, float:1.8877104E38)
                java.lang.String r5 = "ChannelRestrictedUsers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x034f:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.permissionsSectionRow
                if (r2 != r4) goto L_0x0365
                r4 = 2131624943(0x7f0e03ef, float:1.887708E38)
                java.lang.String r5 = "ChannelPermissionsHeader"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x0365:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.slowmodeRow
                if (r2 != r4) goto L_0x037b
                r4 = 2131628360(0x7f0e1148, float:1.888401E38)
                java.lang.String r5 = "Slowmode"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x037b:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.gigaHeaderRow
                if (r2 != r4) goto L_0x0842
                r4 = 2131624755(0x7f0e0333, float:1.8876699E38)
                java.lang.String r5 = "BroadcastGroup"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x0391:
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewSectionRow
                if (r2 == r3) goto L_0x03c7
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.type
                if (r3 != r8) goto L_0x03ba
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsDividerRow
                if (r2 != r3) goto L_0x03ba
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.addNewRow
                if (r3 != r10) goto L_0x03ba
                org.telegram.ui.ChatUsersActivity r3 = org.telegram.ui.ChatUsersActivity.this
                int r3 = r3.participantsStartRow
                if (r3 != r10) goto L_0x03ba
                goto L_0x03c7
            L_0x03ba:
                android.view.View r3 = r1.itemView
                android.content.Context r5 = r0.mContext
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r5, (int) r4, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                goto L_0x0842
            L_0x03c7:
                android.view.View r3 = r1.itemView
                android.content.Context r4 = r0.mContext
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r4, (int) r7, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                goto L_0x0842
            L_0x03d4:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.ManageChatTextCell r3 = (org.telegram.ui.Cells.ManageChatTextCell) r3
                java.lang.String r4 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r5 = "windowBackgroundWhiteBlackText"
                r3.setColors(r4, r5)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.addNewRow
                java.lang.String r5 = "windowBackgroundWhiteBlueButton"
                java.lang.String r7 = "windowBackgroundWhiteBlueIcon"
                if (r2 != r4) goto L_0x04b4
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                r9 = 2131165690(0x7var_fa, float:1.7945604E38)
                if (r4 != r8) goto L_0x0410
                r3.setColors(r7, r5)
                r4 = 2131624868(0x7f0e03a4, float:1.8876928E38)
                java.lang.String r5 = "ChannelAddException"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.participantsStartRow
                if (r5 == r10) goto L_0x040b
                r12 = 1
            L_0x040b:
                r3.setText(r4, r11, r9, r12)
                goto L_0x0842
            L_0x0410:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x0429
                r4 = 2131624883(0x7f0e03b3, float:1.8876958E38)
                java.lang.String r5 = "ChannelBlockUser"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r5 = 2131165970(0x7var_, float:1.7946172E38)
                r3.setText(r4, r11, r5, r12)
                goto L_0x0842
            L_0x0429:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r13) goto L_0x0456
                r3.setColors(r7, r5)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.loadingUsers
                if (r4 == 0) goto L_0x0444
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.firstLoaded
                if (r4 == 0) goto L_0x0445
            L_0x0444:
                r12 = 1
            L_0x0445:
                r4 = r12
                r5 = 2131624867(0x7f0e03a3, float:1.8876926E38)
                java.lang.String r6 = "ChannelAddAdmin"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r6 = 2131165634(0x7var_c2, float:1.794549E38)
                r3.setText(r5, r11, r6, r4)
                goto L_0x04b2
            L_0x0456:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r6) goto L_0x04b2
                r3.setColors(r7, r5)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.addNew2Row
                if (r4 != r10) goto L_0x048d
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.loadingUsers
                if (r4 == 0) goto L_0x0479
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.firstLoaded
                if (r4 == 0) goto L_0x048e
            L_0x0479:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.membersHeaderRow
                if (r4 != r10) goto L_0x048e
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r4 = r4.participants
                boolean r4 = r4.isEmpty()
                if (r4 != 0) goto L_0x048e
            L_0x048d:
                r12 = 1
            L_0x048e:
                r4 = r12
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                boolean r5 = r5.isChannel
                if (r5 == 0) goto L_0x04a4
                r5 = 2131624287(0x7f0e015f, float:1.887575E38)
                java.lang.String r6 = "AddSubscriber"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r3.setText(r5, r11, r9, r4)
                goto L_0x04b0
            L_0x04a4:
                r5 = 2131624269(0x7f0e014d, float:1.8875713E38)
                java.lang.String r6 = "AddMember"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r3.setText(r5, r11, r9, r4)
            L_0x04b0:
                goto L_0x0842
            L_0x04b2:
                goto L_0x0842
            L_0x04b4:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.recentActionsRow
                if (r2 != r4) goto L_0x04cd
                r4 = 2131625667(0x7f0e06c3, float:1.8878548E38)
                java.lang.String r5 = "EventLog"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r5 = 2131165791(0x7var_f, float:1.794581E38)
                r3.setText(r4, r11, r5, r12)
                goto L_0x0842
            L_0x04cd:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.addNew2Row
                if (r2 != r4) goto L_0x050e
                r3.setColors(r7, r5)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.loadingUsers
                if (r4 == 0) goto L_0x04e8
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.firstLoaded
                if (r4 == 0) goto L_0x04fd
            L_0x04e8:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.membersHeaderRow
                if (r4 != r10) goto L_0x04fd
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                java.util.ArrayList r4 = r4.participants
                boolean r4 = r4.isEmpty()
                if (r4 != 0) goto L_0x04fd
                r12 = 1
            L_0x04fd:
                r4 = r12
                r5 = 2131624906(0x7f0e03ca, float:1.8877005E38)
                java.lang.String r6 = "ChannelInviteViaLink"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r6 = 2131165783(0x7var_, float:1.7945793E38)
                r3.setText(r5, r11, r6, r4)
                goto L_0x052a
            L_0x050e:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.gigaConvertRow
                if (r2 != r4) goto L_0x052a
                r3.setColors(r7, r5)
                r4 = 2131624756(0x7f0e0334, float:1.88767E38)
                java.lang.String r5 = "BroadcastGroupConvert"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r5 = 2131165673(0x7var_e9, float:1.794557E38)
                r3.setText(r4, r11, r5, r12)
                goto L_0x0842
            L_0x052a:
                goto L_0x0842
            L_0x052c:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r3 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r3
                org.telegram.ui.ChatUsersActivity r11 = org.telegram.ui.ChatUsersActivity.this
                int r11 = r11.participantsInfoRow
                if (r2 != r11) goto L_0x05ea
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 == 0) goto L_0x05be
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r8) goto L_0x054a
                goto L_0x05be
            L_0x054a:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r13) goto L_0x058a
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.addNewRow
                if (r4 == r10) goto L_0x057c
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x056f
                r4 = 2131624878(0x7f0e03ae, float:1.8876948E38)
                java.lang.String r5 = "ChannelAdminsInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x057f
            L_0x056f:
                r4 = 2131626580(0x7f0e0a54, float:1.88804E38)
                java.lang.String r5 = "MegaAdminsInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x057f
            L_0x057c:
                r3.setText(r5)
            L_0x057f:
                android.content.Context r4 = r0.mContext
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r4, (int) r7, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                goto L_0x0842
            L_0x058a:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != r6) goto L_0x0842
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x05b0
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.selectType
                if (r4 == 0) goto L_0x05a3
                goto L_0x05b0
            L_0x05a3:
                r4 = 2131624918(0x7f0e03d6, float:1.887703E38)
                java.lang.String r5 = "ChannelMembersInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x05b3
            L_0x05b0:
                r3.setText(r5)
            L_0x05b3:
                android.content.Context r4 = r0.mContext
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r4, (int) r7, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                goto L_0x0842
            L_0x05be:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                boolean r4 = r4.isChannel
                if (r4 == 0) goto L_0x05d3
                r4 = 2131626808(0x7f0e0b38, float:1.8880863E38)
                java.lang.String r5 = "NoBlockedChannel2"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x05df
            L_0x05d3:
                r4 = 2131626809(0x7f0e0b39, float:1.8880865E38)
                java.lang.String r5 = "NoBlockedGroup2"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
            L_0x05df:
                android.content.Context r4 = r0.mContext
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r4, (int) r7, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                goto L_0x0842
            L_0x05ea:
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r5.slowmodeInfoRow
                if (r2 != r5) goto L_0x0634
                android.content.Context r5 = r0.mContext
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r5, (int) r4, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r5 = r4.selectedSlowmode
                int r4 = r4.getSecondsForIndex(r5)
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.info
                if (r5 == 0) goto L_0x0627
                if (r4 != 0) goto L_0x0610
                goto L_0x0627
            L_0x0610:
                r5 = 2131628363(0x7f0e114b, float:1.8884017E38)
                java.lang.Object[] r6 = new java.lang.Object[r13]
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r7 = r7.formatSeconds(r4)
                r6[r12] = r7
                java.lang.String r7 = "SlowmodeInfoSelected"
                java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
                r3.setText(r5)
                goto L_0x064a
            L_0x0627:
                r5 = 2131628362(0x7f0e114a, float:1.8884015E38)
                java.lang.String r6 = "SlowmodeInfoOff"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r3.setText(r5)
                goto L_0x064a
            L_0x0634:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.gigaInfoRow
                if (r2 != r4) goto L_0x064a
                r4 = 2131624757(0x7f0e0335, float:1.8876703E38)
                java.lang.String r5 = "BroadcastGroupConvertInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0842
            L_0x064a:
                goto L_0x0842
            L_0x064c:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.ManageChatUserCell r3 = (org.telegram.ui.Cells.ManageChatUserCell) r3
                java.lang.Integer r4 = java.lang.Integer.valueOf(r29)
                r3.setTag(r4)
                org.telegram.tgnet.TLObject r4 = r0.getItem(r2)
                r5 = 0
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.participantsStartRow
                if (r2 < r7) goto L_0x068d
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.participantsEndRow
                if (r2 >= r7) goto L_0x068d
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.participantsEndRow
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r9 = r9.currentChat
                boolean r9 = org.telegram.messenger.ChatObject.isChannel(r9)
                if (r9 == 0) goto L_0x068a
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r9 = r9.currentChat
                boolean r9 = r9.megagroup
                if (r9 != 0) goto L_0x068a
                r9 = 1
                goto L_0x068b
            L_0x068a:
                r9 = 0
            L_0x068b:
                r5 = r9
                goto L_0x06c4
            L_0x068d:
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.contactsStartRow
                if (r2 < r7) goto L_0x06be
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.contactsEndRow
                if (r2 >= r7) goto L_0x06be
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.contactsEndRow
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r9 = r9.currentChat
                boolean r9 = org.telegram.messenger.ChatObject.isChannel(r9)
                if (r9 == 0) goto L_0x06bb
                org.telegram.ui.ChatUsersActivity r9 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.tgnet.TLRPC$Chat r9 = r9.currentChat
                boolean r9 = r9.megagroup
                if (r9 != 0) goto L_0x06bb
                r9 = 1
                goto L_0x06bc
            L_0x06bb:
                r9 = 0
            L_0x06bc:
                r5 = r9
                goto L_0x06c4
            L_0x06be:
                org.telegram.ui.ChatUsersActivity r7 = org.telegram.ui.ChatUsersActivity.this
                int r7 = r7.botEndRow
            L_0x06c4:
                boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant
                if (r9 == 0) goto L_0x06ed
                r9 = r4
                org.telegram.tgnet.TLRPC$ChannelParticipant r9 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r9
                org.telegram.tgnet.TLRPC$Peer r10 = r9.peer
                long r14 = org.telegram.messenger.MessageObject.getPeerId(r10)
                long r12 = r9.kicked_by
                long r10 = r9.promoted_by
                org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = r9.banned_rights
                int r8 = r9.date
                boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantBanned
                r20 = r1
                boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator
                boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin
                r21 = r10
                r10 = r14
                r13 = r12
                r26 = r20
                r20 = r4
                r4 = r9
                r9 = r26
                goto L_0x070d
            L_0x06ed:
                boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC.ChatParticipant
                if (r1 == 0) goto L_0x0841
                r1 = r4
                org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1
                long r14 = r1.user_id
                int r8 = r1.date
                r12 = 0
                r10 = 0
                r6 = 0
                r9 = 0
                r20 = r4
                boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator
                boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin
                r21 = r10
                r10 = r14
                r13 = r12
                r26 = r4
                r4 = r1
                r1 = r26
            L_0x070d:
                r23 = 0
                int r12 = (r10 > r23 ? 1 : (r10 == r23 ? 0 : -1))
                if (r12 <= 0) goto L_0x0725
                org.telegram.ui.ChatUsersActivity r12 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
                java.lang.Long r15 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r15)
                r23 = r4
                r15 = r5
                goto L_0x0737
            L_0x0725:
                org.telegram.ui.ChatUsersActivity r12 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
                r23 = r4
                r15 = r5
                long r4 = -r10
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$Chat r12 = r12.getChat(r4)
            L_0x0737:
                if (r12 == 0) goto L_0x083c
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 3
                if (r4 != r5) goto L_0x0757
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                java.lang.String r4 = r4.formatUserPermissions(r6)
                int r5 = r7 + -1
                if (r2 == r5) goto L_0x074e
                r5 = 1
                goto L_0x074f
            L_0x074e:
                r5 = 0
            L_0x074f:
                r19 = r6
                r6 = 0
                r3.setData(r12, r6, r4, r5)
                goto L_0x0842
            L_0x0757:
                r19 = r6
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                if (r4 != 0) goto L_0x07a3
                r4 = 0
                if (r9 == 0) goto L_0x0792
                org.telegram.ui.ChatUsersActivity r5 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                java.lang.Long r6 = java.lang.Long.valueOf(r13)
                org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
                if (r5 == 0) goto L_0x078d
                r17 = r4
                r6 = 1
                java.lang.Object[] r4 = new java.lang.Object[r6]
                java.lang.String r24 = org.telegram.messenger.UserObject.getUserName(r5)
                r16 = 0
                r4[r16] = r24
                java.lang.String r6 = "UserRemovedBy"
                r25 = r5
                r5 = 2131628808(0x7f0e1308, float:1.888492E38)
                java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r5, r4)
                goto L_0x0796
            L_0x078d:
                r17 = r4
                r25 = r5
                goto L_0x0794
            L_0x0792:
                r17 = r4
            L_0x0794:
                r4 = r17
            L_0x0796:
                int r5 = r7 + -1
                if (r2 == r5) goto L_0x079c
                r5 = 1
                goto L_0x079d
            L_0x079c:
                r5 = 0
            L_0x079d:
                r6 = 0
                r3.setData(r12, r6, r4, r5)
                goto L_0x0842
            L_0x07a3:
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                int r4 = r4.type
                r5 = 1
                if (r4 != r5) goto L_0x0817
                r5 = 0
                if (r1 == 0) goto L_0x07bd
                r6 = 2131624894(0x7f0e03be, float:1.887698E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r4, r6)
                r25 = r1
                r16 = 0
                goto L_0x080b
            L_0x07bd:
                if (r23 == 0) goto L_0x0803
                org.telegram.ui.ChatUsersActivity r4 = org.telegram.ui.ChatUsersActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                java.lang.Long r6 = java.lang.Long.valueOf(r21)
                org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r6)
                if (r4 == 0) goto L_0x07fc
                r18 = r5
                long r5 = r4.id
                int r24 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
                if (r24 != 0) goto L_0x07e5
                r5 = 2131624876(0x7f0e03ac, float:1.8876944E38)
                java.lang.String r6 = "ChannelAdministrator"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r25 = r1
                r16 = 0
                goto L_0x080b
            L_0x07e5:
                r6 = 1
                java.lang.Object[] r5 = new java.lang.Object[r6]
                java.lang.String r24 = org.telegram.messenger.UserObject.getUserName(r4)
                r16 = 0
                r5[r16] = r24
                java.lang.String r6 = "EditAdminPromotedBy"
                r25 = r1
                r1 = 2131625553(0x7f0e0651, float:1.8878317E38)
                java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r1, r5)
                goto L_0x080b
            L_0x07fc:
                r25 = r1
                r18 = r5
                r16 = 0
                goto L_0x0809
            L_0x0803:
                r25 = r1
                r18 = r5
                r16 = 0
            L_0x0809:
                r5 = r18
            L_0x080b:
                int r1 = r7 + -1
                if (r2 == r1) goto L_0x0811
                r1 = 1
                goto L_0x0812
            L_0x0811:
                r1 = 0
            L_0x0812:
                r4 = 0
                r3.setData(r12, r4, r5, r1)
                goto L_0x083b
            L_0x0817:
                r25 = r1
                r16 = 0
                org.telegram.ui.ChatUsersActivity r1 = org.telegram.ui.ChatUsersActivity.this
                int r1 = r1.type
                r4 = 2
                if (r1 != r4) goto L_0x083b
                if (r15 == 0) goto L_0x082e
                if (r8 == 0) goto L_0x082e
                long r4 = (long) r8
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatJoined(r4)
                goto L_0x082f
            L_0x082e:
                r1 = 0
            L_0x082f:
                int r4 = r7 + -1
                if (r2 == r4) goto L_0x0835
                r4 = 1
                goto L_0x0836
            L_0x0835:
                r4 = 0
            L_0x0836:
                r5 = 0
                r3.setData(r12, r5, r1, r4)
                goto L_0x0842
            L_0x083b:
                goto L_0x0842
            L_0x083c:
                r25 = r1
                r19 = r6
                goto L_0x0842
            L_0x0841:
                return
            L_0x0842:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == ChatUsersActivity.this.addNewRow || position == ChatUsersActivity.this.addNew2Row || position == ChatUsersActivity.this.recentActionsRow || position == ChatUsersActivity.this.gigaConvertRow) {
                return 2;
            }
            if ((position >= ChatUsersActivity.this.participantsStartRow && position < ChatUsersActivity.this.participantsEndRow) || ((position >= ChatUsersActivity.this.botStartRow && position < ChatUsersActivity.this.botEndRow) || (position >= ChatUsersActivity.this.contactsStartRow && position < ChatUsersActivity.this.contactsEndRow))) {
                return 0;
            }
            if (position == ChatUsersActivity.this.addNewSectionRow || position == ChatUsersActivity.this.participantsDividerRow || position == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (position == ChatUsersActivity.this.restricted1SectionRow || position == ChatUsersActivity.this.permissionsSectionRow || position == ChatUsersActivity.this.slowmodeRow || position == ChatUsersActivity.this.gigaHeaderRow) {
                return 5;
            }
            if (position == ChatUsersActivity.this.participantsInfoRow || position == ChatUsersActivity.this.slowmodeInfoRow || position == ChatUsersActivity.this.gigaInfoRow) {
                return 1;
            }
            if (position == ChatUsersActivity.this.blockedEmptyRow) {
                return 4;
            }
            if (position == ChatUsersActivity.this.removedUsersRow) {
                return 6;
            }
            if (position == ChatUsersActivity.this.changeInfoRow || position == ChatUsersActivity.this.addUsersRow || position == ChatUsersActivity.this.pinMessagesRow || position == ChatUsersActivity.this.sendMessagesRow || position == ChatUsersActivity.this.sendMediaRow || position == ChatUsersActivity.this.sendStickersRow || position == ChatUsersActivity.this.embedLinksRow || position == ChatUsersActivity.this.sendPollsRow) {
                return 7;
            }
            if (position == ChatUsersActivity.this.membersHeaderRow || position == ChatUsersActivity.this.contactsHeaderRow || position == ChatUsersActivity.this.botHeaderRow || position == ChatUsersActivity.this.loadingHeaderRow) {
                return 8;
            }
            if (position == ChatUsersActivity.this.slowmodeSelectRow) {
                return 9;
            }
            if (position == ChatUsersActivity.this.loadingProgressRow) {
                return 10;
            }
            if (position == ChatUsersActivity.this.loadingUserCellRow) {
                return 11;
            }
            return 0;
        }

        public TLObject getItem(int position) {
            if (position >= ChatUsersActivity.this.participantsStartRow && position < ChatUsersActivity.this.participantsEndRow) {
                return (TLObject) ChatUsersActivity.this.participants.get(position - ChatUsersActivity.this.participantsStartRow);
            }
            if (position >= ChatUsersActivity.this.contactsStartRow && position < ChatUsersActivity.this.contactsEndRow) {
                return (TLObject) ChatUsersActivity.this.contacts.get(position - ChatUsersActivity.this.contactsStartRow);
            }
            if (position < ChatUsersActivity.this.botStartRow || position >= ChatUsersActivity.this.botEndRow) {
                return null;
            }
            return (TLObject) ChatUsersActivity.this.bots.get(position - ChatUsersActivity.this.botStartRow);
        }
    }

    public DiffCallback saveState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.oldBotStartRow = this.botStartRow;
        diffCallback.oldBotEndRow = this.botEndRow;
        diffCallback.oldBots.clear();
        diffCallback.oldBots.addAll(this.bots);
        diffCallback.oldContactsEndRow = this.contactsEndRow;
        diffCallback.oldContactsStartRow = this.contactsStartRow;
        diffCallback.oldContacts.clear();
        diffCallback.oldContacts.addAll(this.contacts);
        diffCallback.oldParticipantsStartRow = this.participantsStartRow;
        diffCallback.oldParticipantsEndRow = this.participantsEndRow;
        diffCallback.oldParticipants.clear();
        diffCallback.oldParticipants.addAll(this.participants);
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        return diffCallback;
    }

    public void updateListAnimated(DiffCallback savedState) {
        if (this.listViewAdapter == null) {
            updateRows();
            return;
        }
        updateRows();
        savedState.fillPositions(savedState.newPositionToItem);
        DiffUtil.calculateDiff(savedState).dispatchUpdatesTo((RecyclerView.Adapter) this.listViewAdapter);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && this.layoutManager != null && recyclerListView.getChildCount() > 0) {
            View view = null;
            int position = -1;
            int i = 0;
            while (true) {
                if (i >= this.listView.getChildCount()) {
                    break;
                }
                RecyclerListView recyclerListView2 = this.listView;
                position = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i));
                if (position != -1) {
                    view = this.listView.getChildAt(i);
                    break;
                }
                i++;
            }
            if (view != null) {
                this.layoutManager.scrollToPositionWithOffset(position, view.getTop() - this.listView.getPaddingTop());
            }
        }
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldBotEndRow;
        int oldBotStartRow;
        /* access modifiers changed from: private */
        public ArrayList<TLObject> oldBots;
        /* access modifiers changed from: private */
        public ArrayList<TLObject> oldContacts;
        int oldContactsEndRow;
        int oldContactsStartRow;
        /* access modifiers changed from: private */
        public ArrayList<TLObject> oldParticipants;
        int oldParticipantsEndRow;
        int oldParticipantsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldParticipants = new ArrayList<>();
            this.oldBots = new ArrayList<>();
            this.oldContacts = new ArrayList<>();
        }

        public int getOldListSize() {
            return this.oldRowCount;
        }

        public int getNewListSize() {
            return ChatUsersActivity.this.rowCount;
        }

        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldItemPosition >= this.oldBotStartRow && oldItemPosition < this.oldBotEndRow && newItemPosition >= ChatUsersActivity.this.botStartRow && newItemPosition < ChatUsersActivity.this.botEndRow) {
                return this.oldBots.get(oldItemPosition - this.oldBotStartRow).equals(ChatUsersActivity.this.bots.get(newItemPosition - ChatUsersActivity.this.botStartRow));
            }
            if (oldItemPosition >= this.oldContactsStartRow && oldItemPosition < this.oldContactsEndRow && newItemPosition >= ChatUsersActivity.this.contactsStartRow && newItemPosition < ChatUsersActivity.this.contactsEndRow) {
                return this.oldContacts.get(oldItemPosition - this.oldContactsStartRow).equals(ChatUsersActivity.this.contacts.get(newItemPosition - ChatUsersActivity.this.contactsStartRow));
            }
            if (oldItemPosition < this.oldParticipantsStartRow || oldItemPosition >= this.oldParticipantsEndRow || newItemPosition < ChatUsersActivity.this.participantsStartRow || newItemPosition >= ChatUsersActivity.this.participantsEndRow) {
                return this.oldPositionToItem.get(oldItemPosition) == this.newPositionToItem.get(newItemPosition);
            }
            return this.oldParticipants.get(oldItemPosition - this.oldParticipantsStartRow).equals(ChatUsersActivity.this.participants.get(newItemPosition - ChatUsersActivity.this.participantsStartRow));
        }

        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (!areItemsTheSame(oldItemPosition, newItemPosition) || ChatUsersActivity.this.restricted1SectionRow == newItemPosition) {
                return false;
            }
            return true;
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, ChatUsersActivity.this.recentActionsRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, ChatUsersActivity.this.addNewRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, ChatUsersActivity.this.addNew2Row, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, ChatUsersActivity.this.addNewSectionRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, ChatUsersActivity.this.restricted1SectionRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, ChatUsersActivity.this.participantsDividerRow, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, ChatUsersActivity.this.participantsDivider2Row, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, ChatUsersActivity.this.gigaHeaderRow, sparseIntArray);
            int pointer9 = pointer8 + 1;
            put(pointer9, ChatUsersActivity.this.gigaConvertRow, sparseIntArray);
            int pointer10 = pointer9 + 1;
            put(pointer10, ChatUsersActivity.this.gigaInfoRow, sparseIntArray);
            int pointer11 = pointer10 + 1;
            put(pointer11, ChatUsersActivity.this.participantsInfoRow, sparseIntArray);
            int pointer12 = pointer11 + 1;
            put(pointer12, ChatUsersActivity.this.blockedEmptyRow, sparseIntArray);
            int pointer13 = pointer12 + 1;
            put(pointer13, ChatUsersActivity.this.permissionsSectionRow, sparseIntArray);
            int pointer14 = pointer13 + 1;
            put(pointer14, ChatUsersActivity.this.sendMessagesRow, sparseIntArray);
            int pointer15 = pointer14 + 1;
            put(pointer15, ChatUsersActivity.this.sendMediaRow, sparseIntArray);
            int pointer16 = pointer15 + 1;
            put(pointer16, ChatUsersActivity.this.sendStickersRow, sparseIntArray);
            int pointer17 = pointer16 + 1;
            put(pointer17, ChatUsersActivity.this.sendPollsRow, sparseIntArray);
            int pointer18 = pointer17 + 1;
            put(pointer18, ChatUsersActivity.this.embedLinksRow, sparseIntArray);
            int pointer19 = pointer18 + 1;
            put(pointer19, ChatUsersActivity.this.addUsersRow, sparseIntArray);
            int pointer20 = pointer19 + 1;
            put(pointer20, ChatUsersActivity.this.pinMessagesRow, sparseIntArray);
            int pointer21 = pointer20 + 1;
            put(pointer21, ChatUsersActivity.this.changeInfoRow, sparseIntArray);
            int pointer22 = pointer21 + 1;
            put(pointer22, ChatUsersActivity.this.removedUsersRow, sparseIntArray);
            int pointer23 = pointer22 + 1;
            put(pointer23, ChatUsersActivity.this.contactsHeaderRow, sparseIntArray);
            int pointer24 = pointer23 + 1;
            put(pointer24, ChatUsersActivity.this.botHeaderRow, sparseIntArray);
            int pointer25 = pointer24 + 1;
            put(pointer25, ChatUsersActivity.this.membersHeaderRow, sparseIntArray);
            int pointer26 = pointer25 + 1;
            put(pointer26, ChatUsersActivity.this.slowmodeRow, sparseIntArray);
            int pointer27 = pointer26 + 1;
            put(pointer27, ChatUsersActivity.this.slowmodeSelectRow, sparseIntArray);
            int pointer28 = pointer27 + 1;
            put(pointer28, ChatUsersActivity.this.slowmodeInfoRow, sparseIntArray);
            int pointer29 = pointer28 + 1;
            put(pointer29, ChatUsersActivity.this.loadingProgressRow, sparseIntArray);
            int pointer30 = pointer29 + 1;
            put(pointer30, ChatUsersActivity.this.loadingUserCellRow, sparseIntArray);
            put(pointer30 + 1, ChatUsersActivity.this.loadingHeaderRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatUsersActivity$$ExternalSyntheticLambda7(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, ChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2Track"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2TrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"title"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"subtitle"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$18$org-telegram-ui-ChatUsersActivity  reason: not valid java name */
    public /* synthetic */ void m3308lambda$getThemeDescriptions$18$orgtelegramuiChatUsersActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
