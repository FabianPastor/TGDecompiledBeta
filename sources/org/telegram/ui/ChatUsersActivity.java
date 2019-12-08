package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChannelParticipantsFilter;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsBots;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsContacts;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.UndoView;

public class ChatUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_BANNED = 0;
    public static final int TYPE_KICKED = 3;
    public static final int TYPE_USERS = 2;
    private static final int done_button = 1;
    private static final int search_button = 0;
    private int addNew2Row;
    private int addNewRow;
    private int addNewSectionRow;
    private int addUsersRow;
    private int blockedEmptyRow;
    private int botEndRow;
    private int botHeaderRow;
    private int botStartRow;
    private ArrayList<TLObject> bots = new ArrayList();
    private boolean botsEndReached;
    private SparseArray<TLObject> botsMap = new SparseArray();
    private int changeInfoRow;
    private int chatId = this.arguments.getInt("chat_id");
    private ArrayList<TLObject> contacts = new ArrayList();
    private boolean contactsEndReached;
    private int contactsEndRow;
    private int contactsHeaderRow;
    private SparseArray<TLObject> contactsMap = new SparseArray();
    private int contactsStartRow;
    private Chat currentChat = getMessagesController().getChat(Integer.valueOf(this.chatId));
    private TL_chatBannedRights defaultBannedRights = new TL_chatBannedRights();
    private int delayResults;
    private ChatUsersActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private int embedLinksRow;
    private EmptyTextProgressView emptyView;
    private boolean firstLoaded;
    private ChatFull info;
    private String initialBannedRights;
    private int initialSlowmode;
    private boolean isChannel;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingUsers;
    private int membersHeaderRow;
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    private ArrayList<TLObject> participants = new ArrayList();
    private int participantsDivider2Row;
    private int participantsDividerRow;
    private int participantsEndRow;
    private int participantsInfoRow;
    private SparseArray<TLObject> participantsMap = new SparseArray();
    private int participantsStartRow;
    private int permissionsSectionRow;
    private int pinMessagesRow;
    private int recentActionsRow;
    private int removedUsersRow;
    private int restricted1SectionRow;
    private int rowCount;
    private ActionBarMenuItem searchItem;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private int selectType = this.arguments.getInt("selectType");
    private int selectedSlowmode;
    private int sendMediaRow;
    private int sendMessagesRow;
    private int sendPollsRow;
    private int sendStickersRow;
    private int slowmodeInfoRow;
    private int slowmodeRow;
    private int slowmodeSelectRow;
    private int type = this.arguments.getInt("type");
    private UndoView undoView;

    public interface ChatUsersActivityDelegate {
        void didAddParticipantToList(int i, TLObject tLObject);

        void didChangeOwner(User user);
    }

    private class ChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private Paint paint;
        private int sideSide;
        private ArrayList<Integer> sizes = new ArrayList();
        private boolean startMoving;
        private int startMovingItem;
        private float startX;
        private ArrayList<String> strings = new ArrayList();
        private TextPaint textPaint;

        public ChooseView(Context context) {
            super(context);
            Integer valueOf = Integer.valueOf(1);
            this.paint = new Paint(1);
            this.textPaint = new TextPaint(1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            for (int i = 0; i < 7; i++) {
                Object formatString;
                if (i != 0) {
                    String str = "SlowmodeSeconds";
                    if (i == 1) {
                        formatString = LocaleController.formatString(str, NUM, Integer.valueOf(10));
                    } else if (i != 2) {
                        String str2 = "SlowmodeMinutes";
                        if (i == 3) {
                            formatString = LocaleController.formatString(str2, NUM, valueOf);
                        } else if (i == 4) {
                            formatString = LocaleController.formatString(str2, NUM, Integer.valueOf(5));
                        } else if (i != 5) {
                            formatString = LocaleController.formatString("SlowmodeHours", NUM, valueOf);
                        } else {
                            formatString = LocaleController.formatString(str2, NUM, Integer.valueOf(15));
                        }
                    } else {
                        formatString = LocaleController.formatString(str, NUM, Integer.valueOf(30));
                    }
                } else {
                    formatString = LocaleController.getString("SlowmodeOff", NUM);
                }
                this.strings.add(formatString);
                this.sizes.add(Integer.valueOf((int) Math.ceil((double) this.textPaint.measureText(formatString))));
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            int i = 0;
            int i2;
            int i3;
            int i4;
            int i5;
            if (motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                i2 = 0;
                while (i2 < this.strings.size()) {
                    i3 = this.sideSide;
                    i4 = this.lineSize + (this.gapSize * 2);
                    i5 = this.circleSize;
                    i3 = (i3 + ((i4 + i5) * i2)) + (i5 / 2);
                    if (x <= ((float) (i3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i3 + AndroidUtilities.dp(15.0f)))) {
                        i2++;
                    } else {
                        boolean z;
                        if (i2 == ChatUsersActivity.this.selectedSlowmode) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingItem = ChatUsersActivity.this.selectedSlowmode;
                    }
                }
            } else if (motionEvent.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    while (i < this.strings.size()) {
                        i2 = this.sideSide;
                        i3 = this.lineSize;
                        int i6 = this.gapSize;
                        i4 = (i6 * 2) + i3;
                        i5 = this.circleSize;
                        i2 = (i2 + ((i4 + i5) * i)) + (i5 / 2);
                        i3 = ((i3 / 2) + (i5 / 2)) + i6;
                        if (x <= ((float) (i2 - i3)) || x >= ((float) (i2 + i3))) {
                            i++;
                        } else if (ChatUsersActivity.this.selectedSlowmode != i) {
                            setItem(i);
                        }
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (!this.moving) {
                    i2 = 0;
                    while (i2 < this.strings.size()) {
                        i3 = this.sideSide;
                        i4 = this.lineSize + (this.gapSize * 2);
                        i5 = this.circleSize;
                        i3 = (i3 + ((i4 + i5) * i2)) + (i5 / 2);
                        if (x <= ((float) (i3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i3 + AndroidUtilities.dp(15.0f)))) {
                            i2++;
                        } else if (ChatUsersActivity.this.selectedSlowmode != i2) {
                            setItem(i2);
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

        private void setItem(int i) {
            if (ChatUsersActivity.this.info != null) {
                ChatUsersActivity.this.selectedSlowmode = i;
                ChatUsersActivity.this.info.slowmode_seconds = ChatUsersActivity.this.getSecondsForIndex(i);
                ChatFull access$100 = ChatUsersActivity.this.info;
                access$100.flags |= 131072;
                for (i = 0; i < 3; i++) {
                    ViewHolder findViewHolderForAdapterPosition = ChatUsersActivity.this.listView.findViewHolderForAdapterPosition(ChatUsersActivity.this.slowmodeInfoRow);
                    if (findViewHolderForAdapterPosition != null) {
                        ChatUsersActivity.this.listViewAdapter.onBindViewHolder(findViewHolderForAdapterPosition, ChatUsersActivity.this.slowmodeInfoRow);
                    }
                }
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
            MeasureSpec.getSize(i);
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * this.strings.size())) - ((this.gapSize * 2) * (this.strings.size() - 1))) - (this.sideSide * 2)) / (this.strings.size() - 1);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int i = 0;
            while (i < this.strings.size()) {
                int i2 = this.sideSide;
                int i3 = this.lineSize + (this.gapSize * 2);
                int i4 = this.circleSize;
                i2 = (i2 + ((i3 + i4) * i)) + (i4 / 2);
                if (i <= ChatUsersActivity.this.selectedSlowmode) {
                    this.paint.setColor(Theme.getColor("switchTrackChecked"));
                } else {
                    this.paint.setColor(Theme.getColor("switchTrack"));
                }
                canvas.drawCircle((float) i2, (float) measuredHeight, (float) (i == ChatUsersActivity.this.selectedSlowmode ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (i != 0) {
                    i3 = (i2 - (this.circleSize / 2)) - this.gapSize;
                    i4 = this.lineSize;
                    i3 -= i4;
                    if (i == ChatUsersActivity.this.selectedSlowmode || i == ChatUsersActivity.this.selectedSlowmode + 1) {
                        i4 -= AndroidUtilities.dp(3.0f);
                    }
                    if (i == ChatUsersActivity.this.selectedSlowmode + 1) {
                        i3 += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) i3, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (i3 + i4), (float) (AndroidUtilities.dp(1.0f) + measuredHeight), this.paint);
                }
                i3 = ((Integer) this.sizes.get(i)).intValue();
                String str = (String) this.strings.get(i);
                if (i == 0) {
                    canvas.drawText(str, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (i == this.strings.size() - 1) {
                    canvas.drawText(str, (float) ((getMeasuredWidth() - i3) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(str, (float) (i2 - (i3 / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                i++;
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 7) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            boolean z = false;
            if (itemViewType == 0) {
                TLObject currentObject = ((ManageChatUserCell) viewHolder.itemView).getCurrentObject();
                return ((currentObject instanceof User) && ((User) currentObject).self) ? false : true;
            } else {
                if (itemViewType == 0 || itemViewType == 2 || itemViewType == 6) {
                    z = true;
                }
                return z;
            }
        }

        public int getItemCount() {
            if (!ChatUsersActivity.this.loadingUsers || ChatUsersActivity.this.firstLoaded) {
                return ChatUsersActivity.this.rowCount;
            }
            return 0;
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), z ^ 1);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View manageChatUserCell;
            View textInfoPrivacyCell;
            String str = "windowBackgroundWhite";
            boolean z = true;
            switch (i) {
                case 0:
                    Context context = this.mContext;
                    int i2 = 6;
                    int i3 = (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) ? 7 : 6;
                    if (!(ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3)) {
                        i2 = 2;
                    }
                    if (ChatUsersActivity.this.selectType != 0) {
                        z = false;
                    }
                    manageChatUserCell = new ManageChatUserCell(context, i3, i2, z);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(str));
                    manageChatUserCell.setDelegate(new -$$Lambda$ChatUsersActivity$ListAdapter$eixJJWW-1mDLHNoI-EEjSfsGLFc(this));
                    break;
                case 1:
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    manageChatUserCell = new ManageChatTextCell(this.mContext);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 3:
                    textInfoPrivacyCell = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    textInfoPrivacyCell = new FrameLayout(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2) - AndroidUtilities.dp(56.0f), NUM));
                        }
                    };
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    LinearLayout linearLayout = new LinearLayout(this.mContext);
                    linearLayout.setOrientation(1);
                    textInfoPrivacyCell.addView(linearLayout, LayoutHelper.createFrame(-2, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                    ImageView imageView = new ImageView(this.mContext);
                    imageView.setImageResource(NUM);
                    imageView.setScaleType(ScaleType.CENTER);
                    String str2 = "emptyListPlaceholder";
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 1));
                    TextView textView = new TextView(this.mContext);
                    textView.setText(LocaleController.getString("NoBlockedUsers", NUM));
                    textView.setTextColor(Theme.getColor(str2));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(1);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    textView = new TextView(this.mContext);
                    if (ChatUsersActivity.this.isChannel) {
                        textView.setText(LocaleController.getString("NoBlockedChannel2", NUM));
                    } else {
                        textView.setText(LocaleController.getString("NoBlockedGroup2", NUM));
                    }
                    textView.setTextColor(Theme.getColor(str2));
                    textView.setTextSize(1, 15.0f);
                    textView.setGravity(1);
                    linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    textInfoPrivacyCell.setLayoutParams(new LayoutParams(-1, -1));
                    break;
                case 5:
                    View headerCell = new HeaderCell(this.mContext, false, 21, 11, false);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    headerCell.setHeight(43);
                    break;
                case 6:
                    manageChatUserCell = new TextSettingsCell(this.mContext);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 7:
                    manageChatUserCell = new TextCheckCell2(this.mContext);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 8:
                    textInfoPrivacyCell = new GraySectionCell(this.mContext);
                    break;
                default:
                    manageChatUserCell = new ChooseView(this.mContext);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(str));
                    break;
            }
            manageChatUserCell = textInfoPrivacyCell;
            return new Holder(manageChatUserCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            String str = "windowBackgroundGrayShadow";
            boolean z = false;
            boolean z2;
            String access$3700;
            String str2;
            String string;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    int access$3200;
                    int i3;
                    int i4;
                    int i5;
                    TL_chatBannedRights tL_chatBannedRights;
                    boolean z3;
                    boolean z4;
                    ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder2.itemView;
                    manageChatUserCell.setTag(Integer.valueOf(i));
                    TLObject item = getItem(i2);
                    if (i2 >= ChatUsersActivity.this.participantsStartRow && i2 < ChatUsersActivity.this.participantsEndRow) {
                        access$3200 = ChatUsersActivity.this.participantsEndRow;
                    } else if (i2 < ChatUsersActivity.this.contactsStartRow || i2 >= ChatUsersActivity.this.contactsEndRow) {
                        access$3200 = ChatUsersActivity.this.botEndRow;
                    } else {
                        access$3200 = ChatUsersActivity.this.contactsEndRow;
                    }
                    if (item instanceof ChannelParticipant) {
                        ChannelParticipant channelParticipant = (ChannelParticipant) item;
                        i3 = channelParticipant.user_id;
                        i4 = channelParticipant.kicked_by;
                        i5 = channelParticipant.promoted_by;
                        tL_chatBannedRights = channelParticipant.banned_rights;
                        z3 = channelParticipant instanceof TL_channelParticipantBanned;
                        z4 = channelParticipant instanceof TL_channelParticipantCreator;
                        z2 = channelParticipant instanceof TL_channelParticipantAdmin;
                    } else {
                        ChatParticipant chatParticipant = (ChatParticipant) item;
                        i3 = chatParticipant.user_id;
                        z4 = chatParticipant instanceof TL_chatParticipantCreator;
                        z2 = chatParticipant instanceof TL_chatParticipantAdmin;
                        tL_chatBannedRights = null;
                        i4 = 0;
                        i5 = 0;
                        z3 = false;
                    }
                    User user = ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i3));
                    if (user == null) {
                        return;
                    }
                    CharSequence formatString;
                    if (ChatUsersActivity.this.type == 3) {
                        access$3700 = ChatUsersActivity.this.formatUserPermissions(tL_chatBannedRights);
                        if (i2 != access$3200 - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, null, access$3700, z);
                        return;
                    } else if (ChatUsersActivity.this.type == 0) {
                        formatString = (!z3 || ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i4)) == null) ? null : LocaleController.formatString("UserRemovedBy", NUM, ContactsController.formatName(ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i4)).first_name, ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i4)).last_name));
                        if (i2 != access$3200 - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, null, formatString, z);
                        return;
                    } else if (ChatUsersActivity.this.type == 1) {
                        if (z4) {
                            formatString = LocaleController.getString("ChannelCreator", NUM);
                        } else {
                            if (z2) {
                                User user2 = ChatUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i5));
                                if (user2 != null) {
                                    formatString = user2.id == user.id ? LocaleController.getString("ChannelAdministrator", NUM) : LocaleController.formatString("EditAdminPromotedBy", NUM, ContactsController.formatName(user2.first_name, user2.last_name));
                                }
                            }
                            formatString = null;
                        }
                        if (i2 != access$3200 - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, null, formatString, z);
                        return;
                    } else if (ChatUsersActivity.this.type == 2) {
                        if (i2 != access$3200 - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, null, null, z);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                    if (i2 == ChatUsersActivity.this.participantsInfoRow) {
                        if (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) {
                            if (ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat)) {
                                if (ChatUsersActivity.this.isChannel) {
                                    textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedChannel2", NUM));
                                } else {
                                    textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedGroup2", NUM));
                                }
                            } else if (ChatUsersActivity.this.isChannel) {
                                textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedChannel2", NUM));
                            } else {
                                textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedGroup2", NUM));
                            }
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                            return;
                        } else if (ChatUsersActivity.this.type == 1) {
                            if (ChatUsersActivity.this.addNewRow != -1) {
                                if (ChatUsersActivity.this.isChannel) {
                                    textInfoPrivacyCell.setText(LocaleController.getString("ChannelAdminsInfo", NUM));
                                } else {
                                    textInfoPrivacyCell.setText(LocaleController.getString("MegaAdminsInfo", NUM));
                                }
                                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                                return;
                            }
                            textInfoPrivacyCell.setText("");
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                            return;
                        } else if (ChatUsersActivity.this.type == 2) {
                            if (ChatUsersActivity.this.isChannel && ChatUsersActivity.this.selectType == 0) {
                                textInfoPrivacyCell.setText(LocaleController.getString("ChannelMembersInfo", NUM));
                            } else {
                                textInfoPrivacyCell.setText("");
                            }
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                            return;
                        } else {
                            return;
                        }
                    } else if (i2 == ChatUsersActivity.this.slowmodeInfoRow) {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        if (ChatUsersActivity.this.info == null || ChatUsersActivity.this.info.slowmode_seconds == 0) {
                            textInfoPrivacyCell.setText(LocaleController.getString("SlowmodeInfoOff", NUM));
                            return;
                        }
                        str2 = "SlowmodeInfoSelected";
                        Object[] objArr;
                        if (ChatUsersActivity.this.info.slowmode_seconds < 60) {
                            objArr = new Object[1];
                            objArr[0] = LocaleController.formatPluralString("Seconds", ChatUsersActivity.this.info.slowmode_seconds);
                            textInfoPrivacyCell.setText(LocaleController.formatString(str2, NUM, objArr));
                            return;
                        } else if (ChatUsersActivity.this.info.slowmode_seconds < 3600) {
                            objArr = new Object[1];
                            objArr[0] = LocaleController.formatPluralString("Minutes", ChatUsersActivity.this.info.slowmode_seconds / 60);
                            textInfoPrivacyCell.setText(LocaleController.formatString(str2, NUM, objArr));
                            return;
                        } else {
                            objArr = new Object[1];
                            objArr[0] = LocaleController.formatPluralString("Hours", (ChatUsersActivity.this.info.slowmode_seconds / 60) / 60);
                            textInfoPrivacyCell.setText(LocaleController.formatString(str2, NUM, objArr));
                            return;
                        }
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder2.itemView;
                    manageChatTextCell.setColors("windowBackgroundWhiteGrayIcon", "windowBackgroundWhiteBlackText");
                    if (i2 == ChatUsersActivity.this.addNewRow) {
                        str2 = "windowBackgroundWhiteBlueButton";
                        String str3 = "windowBackgroundWhiteBlueIcon";
                        if (ChatUsersActivity.this.type == 3) {
                            manageChatTextCell.setColors(str3, str2);
                            string = LocaleController.getString("ChannelAddException", NUM);
                            if (ChatUsersActivity.this.participantsStartRow != -1) {
                                z = true;
                            }
                            manageChatTextCell.setText(string, null, NUM, z);
                            return;
                        } else if (ChatUsersActivity.this.type == 0) {
                            manageChatTextCell.setText(LocaleController.getString("ChannelBlockUser", NUM), null, NUM, false);
                            return;
                        } else if (ChatUsersActivity.this.type == 1) {
                            manageChatTextCell.setColors(str3, str2);
                            manageChatTextCell.setText(LocaleController.getString("ChannelAddAdmin", NUM), null, NUM, true);
                            return;
                        } else if (ChatUsersActivity.this.type == 2) {
                            manageChatTextCell.setColors(str3, str2);
                            if (ChatUsersActivity.this.isChannel) {
                                string = LocaleController.getString("AddSubscriber", NUM);
                                if (ChatUsersActivity.this.membersHeaderRow == -1 && !ChatUsersActivity.this.participants.isEmpty()) {
                                    z = true;
                                }
                                manageChatTextCell.setText(string, null, NUM, z);
                                return;
                            }
                            string = LocaleController.getString("AddMember", NUM);
                            if (ChatUsersActivity.this.membersHeaderRow == -1 && !ChatUsersActivity.this.participants.isEmpty()) {
                                z = true;
                            }
                            manageChatTextCell.setText(string, null, NUM, z);
                            return;
                        } else {
                            return;
                        }
                    } else if (i2 == ChatUsersActivity.this.recentActionsRow) {
                        manageChatTextCell.setText(LocaleController.getString("EventLog", NUM), null, NUM, false);
                        return;
                    } else if (i2 == ChatUsersActivity.this.addNew2Row) {
                        manageChatTextCell.setText(LocaleController.getString("ChannelInviteViaLink", NUM), null, NUM, true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (i2 == ChatUsersActivity.this.addNewSectionRow || (ChatUsersActivity.this.type == 3 && i2 == ChatUsersActivity.this.participantsDividerRow && ChatUsersActivity.this.addNewRow == -1 && ChatUsersActivity.this.participantsStartRow == -1)) {
                        viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        return;
                    } else {
                        viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        return;
                    }
                case 5:
                    HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
                    if (i2 == ChatUsersActivity.this.restricted1SectionRow) {
                        if (ChatUsersActivity.this.type == 0) {
                            i2 = ChatUsersActivity.this.info != null ? ChatUsersActivity.this.info.kicked_count : ChatUsersActivity.this.participants.size();
                            if (i2 != 0) {
                                headerCell.setText(LocaleController.formatPluralString("RemovedUser", i2));
                                return;
                            } else {
                                headerCell.setText(LocaleController.getString("ChannelBlockedUsers", NUM));
                                return;
                            }
                        }
                        headerCell.setText(LocaleController.getString("ChannelRestrictedUsers", NUM));
                        return;
                    } else if (i2 == ChatUsersActivity.this.permissionsSectionRow) {
                        headerCell.setText(LocaleController.getString("ChannelPermissionsHeader", NUM));
                        return;
                    } else if (i2 == ChatUsersActivity.this.slowmodeRow) {
                        headerCell.setText(LocaleController.getString("Slowmode", NUM));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    string = LocaleController.getString("ChannelBlacklist", NUM);
                    Object[] objArr2 = new Object[1];
                    objArr2[0] = Integer.valueOf(ChatUsersActivity.this.info != null ? ChatUsersActivity.this.info.kicked_count : 0);
                    textSettingsCell.setTextAndValue(string, String.format("%d", objArr2), false);
                    return;
                case 7:
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) viewHolder2.itemView;
                    boolean z5;
                    if (i2 == ChatUsersActivity.this.changeInfoRow) {
                        access$3700 = LocaleController.getString("UserRestrictionsChangeInfo", NUM);
                        z5 = !ChatUsersActivity.this.defaultBannedRights.change_info && TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username);
                        textCheckCell2.setTextAndCheck(access$3700, z5, false);
                    } else if (i2 == ChatUsersActivity.this.addUsersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsInviteUsers", NUM), ChatUsersActivity.this.defaultBannedRights.invite_users ^ 1, true);
                    } else if (i2 == ChatUsersActivity.this.pinMessagesRow) {
                        access$3700 = LocaleController.getString("UserRestrictionsPinMessages", NUM);
                        z5 = !ChatUsersActivity.this.defaultBannedRights.pin_messages && TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username);
                        textCheckCell2.setTextAndCheck(access$3700, z5, true);
                    } else if (i2 == ChatUsersActivity.this.sendMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", NUM), ChatUsersActivity.this.defaultBannedRights.send_messages ^ 1, true);
                    } else if (i2 == ChatUsersActivity.this.sendMediaRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", NUM), ChatUsersActivity.this.defaultBannedRights.send_media ^ 1, true);
                    } else if (i2 == ChatUsersActivity.this.sendStickersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", NUM), ChatUsersActivity.this.defaultBannedRights.send_stickers ^ 1, true);
                    } else if (i2 == ChatUsersActivity.this.embedLinksRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", NUM), ChatUsersActivity.this.defaultBannedRights.embed_links ^ 1, true);
                    } else if (i2 == ChatUsersActivity.this.sendPollsRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendPolls", NUM), ChatUsersActivity.this.defaultBannedRights.send_polls ^ 1, true);
                    }
                    if (i2 == ChatUsersActivity.this.sendMediaRow || i2 == ChatUsersActivity.this.sendStickersRow || i2 == ChatUsersActivity.this.embedLinksRow || i2 == ChatUsersActivity.this.sendPollsRow) {
                        z2 = (ChatUsersActivity.this.defaultBannedRights.send_messages || ChatUsersActivity.this.defaultBannedRights.view_messages) ? false : true;
                        textCheckCell2.setEnabled(z2);
                    } else if (i2 == ChatUsersActivity.this.sendMessagesRow) {
                        textCheckCell2.setEnabled(ChatUsersActivity.this.defaultBannedRights.view_messages ^ 1);
                    }
                    if (!ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat)) {
                        textCheckCell2.setIcon(0);
                        return;
                    } else if ((i2 != ChatUsersActivity.this.addUsersRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 3)) && ((i2 != ChatUsersActivity.this.pinMessagesRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 0)) && ((i2 != ChatUsersActivity.this.changeInfoRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 1)) && (TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username) || !(i2 == ChatUsersActivity.this.pinMessagesRow || i2 == ChatUsersActivity.this.changeInfoRow))))) {
                        textCheckCell2.setIcon(0);
                        return;
                    } else {
                        textCheckCell2.setIcon(NUM);
                        return;
                    }
                case 8:
                    GraySectionCell graySectionCell = (GraySectionCell) viewHolder2.itemView;
                    if (i2 == ChatUsersActivity.this.membersHeaderRow) {
                        if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.currentChat.megagroup) {
                            graySectionCell.setText(LocaleController.getString("ChannelOtherMembers", NUM));
                            return;
                        } else {
                            graySectionCell.setText(LocaleController.getString("ChannelOtherSubscribers", NUM));
                            return;
                        }
                    } else if (i2 == ChatUsersActivity.this.botHeaderRow) {
                        graySectionCell.setText(LocaleController.getString("ChannelBots", NUM));
                        return;
                    } else if (i2 != ChatUsersActivity.this.contactsHeaderRow) {
                        return;
                    } else {
                        if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.currentChat.megagroup) {
                            graySectionCell.setText(LocaleController.getString("GroupContacts", NUM));
                            return;
                        } else {
                            graySectionCell.setText(LocaleController.getString("ChannelContacts", NUM));
                            return;
                        }
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == ChatUsersActivity.this.addNewRow || i == ChatUsersActivity.this.addNew2Row || i == ChatUsersActivity.this.recentActionsRow) {
                return 2;
            }
            if ((i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) || ((i >= ChatUsersActivity.this.botStartRow && i < ChatUsersActivity.this.botEndRow) || (i >= ChatUsersActivity.this.contactsStartRow && i < ChatUsersActivity.this.contactsEndRow))) {
                return 0;
            }
            if (i == ChatUsersActivity.this.addNewSectionRow || i == ChatUsersActivity.this.participantsDividerRow || i == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (i == ChatUsersActivity.this.restricted1SectionRow || i == ChatUsersActivity.this.permissionsSectionRow || i == ChatUsersActivity.this.slowmodeRow) {
                return 5;
            }
            if (i == ChatUsersActivity.this.participantsInfoRow || i == ChatUsersActivity.this.slowmodeInfoRow) {
                return 1;
            }
            if (i == ChatUsersActivity.this.blockedEmptyRow) {
                return 4;
            }
            if (i == ChatUsersActivity.this.removedUsersRow) {
                return 6;
            }
            if (i == ChatUsersActivity.this.changeInfoRow || i == ChatUsersActivity.this.addUsersRow || i == ChatUsersActivity.this.pinMessagesRow || i == ChatUsersActivity.this.sendMessagesRow || i == ChatUsersActivity.this.sendMediaRow || i == ChatUsersActivity.this.sendStickersRow || i == ChatUsersActivity.this.embedLinksRow || i == ChatUsersActivity.this.sendPollsRow) {
                return 7;
            }
            if (i == ChatUsersActivity.this.membersHeaderRow || i == ChatUsersActivity.this.contactsHeaderRow || i == ChatUsersActivity.this.botHeaderRow) {
                return 8;
            }
            if (i == ChatUsersActivity.this.slowmodeSelectRow) {
                return 9;
            }
            return 0;
        }

        public TLObject getItem(int i) {
            if (i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) {
                return (TLObject) ChatUsersActivity.this.participants.get(i - ChatUsersActivity.this.participantsStartRow);
            }
            if (i < ChatUsersActivity.this.contactsStartRow || i >= ChatUsersActivity.this.contactsEndRow) {
                return (i < ChatUsersActivity.this.botStartRow || i >= ChatUsersActivity.this.botEndRow) ? null : (TLObject) ChatUsersActivity.this.bots.get(i - ChatUsersActivity.this.botStartRow);
            } else {
                return (TLObject) ChatUsersActivity.this.contacts.get(i - ChatUsersActivity.this.contactsStartRow);
            }
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<TLObject> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Runnable searchRunnable;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(ChatUsersActivity.this) {
                public /* synthetic */ SparseArray<User> getExcludeUsers() {
                    return -CC.$default$getExcludeUsers(this);
                }

                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }

                public void onDataSetChanged() {
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults(null);
                this.searchAdapterHelper.queryServerSearch(null, ChatUsersActivity.this.type != 0, false, true, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$ChatUsersActivity$SearchAdapter$6g0h_djjISAKh4b_dwWIpTZOwOI -__lambda_chatusersactivity_searchadapter_6g0h_djjisakh4b_dwwiptzowoi = new -$$Lambda$ChatUsersActivity$SearchAdapter$6g0h_djjISAKh4b_dwWIpTZOwOI(this, str);
            this.searchRunnable = -__lambda_chatusersactivity_searchadapter_6g0h_djjisakh4b_dwwiptzowoi;
            dispatchQueue.postRunnable(-__lambda_chatusersactivity_searchadapter_6g0h_djjisakh4b_dwwiptzowoi, 300);
        }

        public /* synthetic */ void lambda$searchDialogs$0$ChatUsersActivity$SearchAdapter(String str) {
            processSearch(str);
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChatUsersActivity$SearchAdapter$zcGPzg6AlSiEVmqjCWms3OvMW4s(this, str));
        }

        public /* synthetic */ void lambda$processSearch$2$ChatUsersActivity$SearchAdapter(String str) {
            ArrayList arrayList = null;
            this.searchRunnable = null;
            ArrayList arrayList2 = (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) ? null : new ArrayList(ChatUsersActivity.this.info.participants.participants);
            if (ChatUsersActivity.this.selectType == 1) {
                arrayList = new ArrayList(ChatUsersActivity.this.getContactsController().contacts);
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatUsersActivity.this.selectType != 0, false, true, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, false, ChatUsersActivity.this.type);
            if (arrayList2 != null || arrayList != null) {
                Utilities.searchQueue.postRunnable(new -$$Lambda$ChatUsersActivity$SearchAdapter$nCoaqRgr8A9Exi9qEjyQaRMerr0(this, str, arrayList2, arrayList));
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:51:0x013b A:{LOOP_END, LOOP:1: B:27:0x00ad->B:51:0x013b} */
        /* JADX WARNING: Removed duplicated region for block: B:94:0x0100 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:86:0x0226 A:{LOOP_END, LOOP:3: B:64:0x019e->B:86:0x0226} */
        /* JADX WARNING: Removed duplicated region for block: B:98:0x01ea A:{SYNTHETIC} */
        /* JADX WARNING: Missing block: B:36:0x00e9, code skipped:
            if (r15.contains(r6.toString()) != false) goto L_0x00fd;
     */
        /* JADX WARNING: Missing block: B:73:0x01d8, code skipped:
            if (r7.contains(r2.toString()) != false) goto L_0x01e7;
     */
        public /* synthetic */ void lambda$null$1$ChatUsersActivity$SearchAdapter(java.lang.String r20, java.util.ArrayList r21, java.util.ArrayList r22) {
            /*
            r19 = this;
            r0 = r19;
            r1 = r21;
            r2 = r22;
            r3 = r20.trim();
            r3 = r3.toLowerCase();
            r4 = r3.length();
            if (r4 != 0) goto L_0x0027;
        L_0x0014:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r3 = new java.util.ArrayList;
            r3.<init>();
            r0.updateSearchResults(r1, r2, r3);
            return;
        L_0x0027:
            r4 = org.telegram.messenger.LocaleController.getInstance();
            r4 = r4.getTranslitString(r3);
            r5 = r3.equals(r4);
            if (r5 != 0) goto L_0x003b;
        L_0x0035:
            r5 = r4.length();
            if (r5 != 0) goto L_0x003c;
        L_0x003b:
            r4 = 0;
        L_0x003c:
            r5 = 0;
            r7 = 1;
            if (r4 == 0) goto L_0x0042;
        L_0x0040:
            r8 = 1;
            goto L_0x0043;
        L_0x0042:
            r8 = 0;
        L_0x0043:
            r8 = r8 + r7;
            r8 = new java.lang.String[r8];
            r8[r5] = r3;
            if (r4 == 0) goto L_0x004c;
        L_0x004a:
            r8[r7] = r4;
        L_0x004c:
            r3 = new java.util.ArrayList;
            r3.<init>();
            r4 = new java.util.ArrayList;
            r4.<init>();
            r9 = new java.util.ArrayList;
            r9.<init>();
            r11 = "@";
            r12 = " ";
            if (r1 == 0) goto L_0x014f;
        L_0x0061:
            r13 = 0;
        L_0x0062:
            r14 = r21.size();
            if (r13 >= r14) goto L_0x014f;
        L_0x0068:
            r14 = r1.get(r13);
            r14 = (org.telegram.tgnet.TLRPC.ChatParticipant) r14;
            r15 = org.telegram.ui.ChatUsersActivity.this;
            r15 = r15.getMessagesController();
            r5 = r14.user_id;
            r5 = java.lang.Integer.valueOf(r5);
            r5 = r15.getUser(r5);
            r15 = r5.id;
            r10 = org.telegram.ui.ChatUsersActivity.this;
            r10 = r10.getUserConfig();
            r10 = r10.getClientUserId();
            if (r15 != r10) goto L_0x008e;
        L_0x008c:
            goto L_0x0147;
        L_0x008e:
            r10 = r5.first_name;
            r15 = r5.last_name;
            r10 = org.telegram.messenger.ContactsController.formatName(r10, r15);
            r10 = r10.toLowerCase();
            r15 = org.telegram.messenger.LocaleController.getInstance();
            r15 = r15.getTranslitString(r10);
            r16 = r10.equals(r15);
            if (r16 == 0) goto L_0x00a9;
        L_0x00a8:
            r15 = 0;
        L_0x00a9:
            r6 = r8.length;
            r7 = 0;
            r17 = 0;
        L_0x00ad:
            if (r7 >= r6) goto L_0x0147;
        L_0x00af:
            r1 = r8[r7];
            r18 = r10.startsWith(r1);
            if (r18 != 0) goto L_0x00fb;
        L_0x00b7:
            r18 = r6;
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r12);
            r6.append(r1);
            r6 = r6.toString();
            r6 = r10.contains(r6);
            if (r6 != 0) goto L_0x00fd;
        L_0x00ce:
            if (r15 == 0) goto L_0x00ec;
        L_0x00d0:
            r6 = r15.startsWith(r1);
            if (r6 != 0) goto L_0x00fd;
        L_0x00d6:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r12);
            r6.append(r1);
            r6 = r6.toString();
            r6 = r15.contains(r6);
            if (r6 == 0) goto L_0x00ec;
        L_0x00eb:
            goto L_0x00fd;
        L_0x00ec:
            r6 = r5.username;
            if (r6 == 0) goto L_0x00f8;
        L_0x00f0:
            r6 = r6.startsWith(r1);
            if (r6 == 0) goto L_0x00f8;
        L_0x00f6:
            r6 = 2;
            goto L_0x00fe;
        L_0x00f8:
            r6 = r17;
            goto L_0x00fe;
        L_0x00fb:
            r18 = r6;
        L_0x00fd:
            r6 = 1;
        L_0x00fe:
            if (r6 == 0) goto L_0x013b;
        L_0x0100:
            r10 = 1;
            if (r6 != r10) goto L_0x010f;
        L_0x0103:
            r6 = r5.first_name;
            r5 = r5.last_name;
            r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r5, r1);
            r4.add(r1);
            goto L_0x0137;
        L_0x010f:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r11);
            r5 = r5.username;
            r6.append(r5);
            r5 = r6.toString();
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r11);
            r6.append(r1);
            r1 = r6.toString();
            r6 = 0;
            r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r6, r1);
            r4.add(r1);
        L_0x0137:
            r9.add(r14);
            goto L_0x0147;
        L_0x013b:
            r17 = r10;
            r7 = r7 + 1;
            r1 = r21;
            r17 = r6;
            r6 = r18;
            goto L_0x00ad;
        L_0x0147:
            r13 = r13 + 1;
            r1 = r21;
            r5 = 0;
            r7 = 1;
            goto L_0x0062;
        L_0x014f:
            if (r2 == 0) goto L_0x0234;
        L_0x0151:
            r1 = 0;
        L_0x0152:
            r5 = r22.size();
            if (r1 >= r5) goto L_0x0234;
        L_0x0158:
            r5 = r2.get(r1);
            r5 = (org.telegram.tgnet.TLRPC.TL_contact) r5;
            r6 = org.telegram.ui.ChatUsersActivity.this;
            r6 = r6.getMessagesController();
            r5 = r5.user_id;
            r5 = java.lang.Integer.valueOf(r5);
            r5 = r6.getUser(r5);
            r6 = r5.id;
            r7 = org.telegram.ui.ChatUsersActivity.this;
            r7 = r7.getUserConfig();
            r7 = r7.getClientUserId();
            if (r6 != r7) goto L_0x0180;
        L_0x017c:
            r2 = 1;
            r15 = 0;
            goto L_0x022e;
        L_0x0180:
            r6 = r5.first_name;
            r7 = r5.last_name;
            r6 = org.telegram.messenger.ContactsController.formatName(r6, r7);
            r6 = r6.toLowerCase();
            r7 = org.telegram.messenger.LocaleController.getInstance();
            r7 = r7.getTranslitString(r6);
            r10 = r6.equals(r7);
            if (r10 == 0) goto L_0x019b;
        L_0x019a:
            r7 = 0;
        L_0x019b:
            r10 = r8.length;
            r13 = 0;
            r14 = 0;
        L_0x019e:
            if (r13 >= r10) goto L_0x017c;
        L_0x01a0:
            r15 = r8[r13];
            r17 = r6.startsWith(r15);
            if (r17 != 0) goto L_0x01e7;
        L_0x01a8:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r2.append(r12);
            r2.append(r15);
            r2 = r2.toString();
            r2 = r6.contains(r2);
            if (r2 != 0) goto L_0x01e7;
        L_0x01bd:
            if (r7 == 0) goto L_0x01db;
        L_0x01bf:
            r2 = r7.startsWith(r15);
            if (r2 != 0) goto L_0x01e7;
        L_0x01c5:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r2.append(r12);
            r2.append(r15);
            r2 = r2.toString();
            r2 = r7.contains(r2);
            if (r2 == 0) goto L_0x01db;
        L_0x01da:
            goto L_0x01e7;
        L_0x01db:
            r2 = r5.username;
            if (r2 == 0) goto L_0x01e8;
        L_0x01df:
            r2 = r2.startsWith(r15);
            if (r2 == 0) goto L_0x01e8;
        L_0x01e5:
            r14 = 2;
            goto L_0x01e8;
        L_0x01e7:
            r14 = 1;
        L_0x01e8:
            if (r14 == 0) goto L_0x0226;
        L_0x01ea:
            r2 = 1;
            if (r14 != r2) goto L_0x01fa;
        L_0x01ed:
            r6 = r5.first_name;
            r7 = r5.last_name;
            r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r7, r15);
            r4.add(r6);
            r15 = 0;
            goto L_0x0222;
        L_0x01fa:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r11);
            r7 = r5.username;
            r6.append(r7);
            r6 = r6.toString();
            r7 = new java.lang.StringBuilder;
            r7.<init>();
            r7.append(r11);
            r7.append(r15);
            r7 = r7.toString();
            r15 = 0;
            r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r15, r7);
            r4.add(r6);
        L_0x0222:
            r3.add(r5);
            goto L_0x022e;
        L_0x0226:
            r2 = 1;
            r15 = 0;
            r13 = r13 + 1;
            r2 = r22;
            goto L_0x019e;
        L_0x022e:
            r1 = r1 + 1;
            r2 = r22;
            goto L_0x0152;
        L_0x0234:
            r0.updateSearchResults(r3, r4, r9);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity$SearchAdapter.lambda$null$1$ChatUsersActivity$SearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<TLObject> arrayList3) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChatUsersActivity$SearchAdapter$Sw9CFmRc9E_mExIImd0E0Zq2CWY(this, arrayList, arrayList2, arrayList3));
        }

        public /* synthetic */ void lambda$updateSearchResults$3$ChatUsersActivity$SearchAdapter(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(this.searchResult);
            if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                arrayList = this.searchAdapterHelper.getGroupSearch();
                arrayList.clear();
                arrayList.addAll(arrayList3);
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            int size3 = this.searchAdapterHelper.getGroupSearch().size();
            int i = 0;
            if (size != 0) {
                i = 0 + (size + 1);
            }
            if (size2 != 0) {
                i += size2 + 1;
            }
            return size3 != 0 ? i + (size3 + 1) : i;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                this.groupStartRow = 0;
                this.totalCount += size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchResult.size();
            if (size2 != 0) {
                size = this.totalCount;
                this.contactsStartRow = size;
                this.totalCount = size + (size2 + 1);
            } else {
                this.contactsStartRow = -1;
            }
            size2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size2 != 0) {
                size = this.totalCount;
                this.globalStartRow = size;
                this.totalCount = size + (size2 + 1);
            } else {
                this.globalStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        /* JADX WARNING: Missing block: B:26:0x0061, code skipped:
            return null;
     */
        public org.telegram.tgnet.TLObject getItem(int r3) {
            /*
            r2 = this;
            r0 = r2.searchAdapterHelper;
            r0 = r0.getGroupSearch();
            r0 = r0.size();
            r1 = 0;
            if (r0 == 0) goto L_0x0024;
        L_0x000d:
            r0 = r0 + 1;
            if (r0 <= r3) goto L_0x0023;
        L_0x0011:
            if (r3 != 0) goto L_0x0014;
        L_0x0013:
            return r1;
        L_0x0014:
            r0 = r2.searchAdapterHelper;
            r0 = r0.getGroupSearch();
            r3 = r3 + -1;
            r3 = r0.get(r3);
            r3 = (org.telegram.tgnet.TLObject) r3;
            return r3;
        L_0x0023:
            r3 = r3 - r0;
        L_0x0024:
            r0 = r2.searchResult;
            r0 = r0.size();
            if (r0 == 0) goto L_0x003f;
        L_0x002c:
            r0 = r0 + 1;
            if (r0 <= r3) goto L_0x003e;
        L_0x0030:
            if (r3 != 0) goto L_0x0033;
        L_0x0032:
            return r1;
        L_0x0033:
            r0 = r2.searchResult;
            r3 = r3 + -1;
            r3 = r0.get(r3);
            r3 = (org.telegram.tgnet.TLObject) r3;
            return r3;
        L_0x003e:
            r3 = r3 - r0;
        L_0x003f:
            r0 = r2.searchAdapterHelper;
            r0 = r0.getGlobalSearch();
            r0 = r0.size();
            if (r0 == 0) goto L_0x0061;
        L_0x004b:
            r0 = r0 + 1;
            if (r0 <= r3) goto L_0x0061;
        L_0x004f:
            if (r3 != 0) goto L_0x0052;
        L_0x0051:
            return r1;
        L_0x0052:
            r0 = r2.searchAdapterHelper;
            r0 = r0.getGlobalSearch();
            r3 = r3 + -1;
            r3 = r0.get(r3);
            r3 = (org.telegram.tgnet.TLObject) r3;
            return r3;
        L_0x0061:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity$SearchAdapter.getItem(int):org.telegram.tgnet.TLObject");
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$4$ChatUsersActivity$SearchAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
            if (!(getItem(((Integer) manageChatUserCell.getTag()).intValue()) instanceof ChannelParticipant)) {
                return false;
            }
            return ChatUsersActivity.this.createMenuForParticipant((ChannelParticipant) getItem(((Integer) manageChatUserCell.getTag()).intValue()), z ^ 1);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View graySectionCell;
            if (i != 0) {
                graySectionCell = new GraySectionCell(this.mContext);
            } else {
                graySectionCell = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                graySectionCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                graySectionCell.setDelegate(new -$$Lambda$ChatUsersActivity$SearchAdapter$fSIulZwDi8NAXDLh6m3-GQxlD8U(this));
            }
            return new Holder(graySectionCell);
        }

        /* JADX WARNING: Removed duplicated region for block: B:76:0x017e  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x0147  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x015c A:{Catch:{ Exception -> 0x0175 }} */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x017e  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x0147  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x015c A:{Catch:{ Exception -> 0x0175 }} */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x017e  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00e5  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x0147  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x015c A:{Catch:{ Exception -> 0x0175 }} */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x017e  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
            r13 = this;
            r0 = r14.getItemViewType();
            r1 = 1;
            if (r0 == 0) goto L_0x0088;
        L_0x0007:
            if (r0 == r1) goto L_0x000b;
        L_0x0009:
            goto L_0x01ac;
        L_0x000b:
            r14 = r14.itemView;
            r14 = (org.telegram.ui.Cells.GraySectionCell) r14;
            r0 = r13.groupStartRow;
            if (r15 != r0) goto L_0x0064;
        L_0x0013:
            r15 = org.telegram.ui.ChatUsersActivity.this;
            r15 = r15.type;
            if (r15 != 0) goto L_0x0029;
        L_0x001b:
            r15 = NUM; // 0x7f0e0227 float:1.8876155E38 double:1.053162429E-314;
            r0 = "ChannelBlockedUsers";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x01ac;
        L_0x0029:
            r15 = org.telegram.ui.ChatUsersActivity.this;
            r15 = r15.type;
            r0 = 3;
            if (r15 != r0) goto L_0x0040;
        L_0x0032:
            r15 = NUM; // 0x7f0e0265 float:1.887628E38 double:1.0531624595E-314;
            r0 = "ChannelRestrictedUsers";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x01ac;
        L_0x0040:
            r15 = org.telegram.ui.ChatUsersActivity.this;
            r15 = r15.isChannel;
            if (r15 == 0) goto L_0x0056;
        L_0x0048:
            r15 = NUM; // 0x7f0e026d float:1.8876297E38 double:1.0531624634E-314;
            r0 = "ChannelSubscribers";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x01ac;
        L_0x0056:
            r15 = NUM; // 0x7f0e0243 float:1.8876212E38 double:1.0531624427E-314;
            r0 = "ChannelMembers";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x01ac;
        L_0x0064:
            r0 = r13.globalStartRow;
            if (r15 != r0) goto L_0x0076;
        L_0x0068:
            r15 = NUM; // 0x7f0e04ea float:1.887759E38 double:1.053162778E-314;
            r0 = "GlobalSearch";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x01ac;
        L_0x0076:
            r0 = r13.contactsStartRow;
            if (r15 != r0) goto L_0x01ac;
        L_0x007a:
            r15 = NUM; // 0x7f0e0305 float:1.8876605E38 double:1.0531625385E-314;
            r0 = "Contacts";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x01ac;
        L_0x0088:
            r0 = r13.getItem(r15);
            r2 = r0 instanceof org.telegram.tgnet.TLRPC.User;
            if (r2 == 0) goto L_0x0093;
        L_0x0090:
            r0 = (org.telegram.tgnet.TLRPC.User) r0;
            goto L_0x00c0;
        L_0x0093:
            r2 = r0 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant;
            if (r2 == 0) goto L_0x00aa;
        L_0x0097:
            r2 = org.telegram.ui.ChatUsersActivity.this;
            r2 = r2.getMessagesController();
            r0 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r0;
            r0 = r0.user_id;
            r0 = java.lang.Integer.valueOf(r0);
            r0 = r2.getUser(r0);
            goto L_0x00c0;
        L_0x00aa:
            r2 = r0 instanceof org.telegram.tgnet.TLRPC.ChatParticipant;
            if (r2 == 0) goto L_0x01ac;
        L_0x00ae:
            r2 = org.telegram.ui.ChatUsersActivity.this;
            r2 = r2.getMessagesController();
            r0 = (org.telegram.tgnet.TLRPC.ChatParticipant) r0;
            r0 = r0.user_id;
            r0 = java.lang.Integer.valueOf(r0);
            r0 = r2.getUser(r0);
        L_0x00c0:
            r2 = r0.username;
            r3 = r13.searchAdapterHelper;
            r3 = r3.getGroupSearch();
            r3 = r3.size();
            r4 = 0;
            r5 = 0;
            if (r3 == 0) goto L_0x00de;
        L_0x00d0:
            r3 = r3 + r1;
            if (r3 <= r15) goto L_0x00dd;
        L_0x00d3:
            r3 = r13.searchAdapterHelper;
            r3 = r3.getLastFoundChannel();
            r6 = r3;
            r3 = r15;
            r15 = 1;
            goto L_0x00e1;
        L_0x00dd:
            r15 = r15 - r3;
        L_0x00de:
            r3 = r15;
            r6 = r5;
            r15 = 0;
        L_0x00e1:
            r7 = "@";
            if (r15 != 0) goto L_0x0122;
        L_0x00e5:
            r8 = r13.searchResult;
            r8 = r8.size();
            if (r8 == 0) goto L_0x0122;
        L_0x00ed:
            r8 = r8 + r1;
            if (r8 <= r3) goto L_0x0121;
        L_0x00f0:
            r15 = r13.searchResultNames;
            r8 = r3 + -1;
            r15 = r15.get(r8);
            r15 = (java.lang.CharSequence) r15;
            if (r15 == 0) goto L_0x011e;
        L_0x00fc:
            r8 = android.text.TextUtils.isEmpty(r2);
            if (r8 != 0) goto L_0x011e;
        L_0x0102:
            r8 = r15.toString();
            r9 = new java.lang.StringBuilder;
            r9.<init>();
            r9.append(r7);
            r9.append(r2);
            r9 = r9.toString();
            r8 = r8.startsWith(r9);
            if (r8 == 0) goto L_0x011e;
        L_0x011b:
            r8 = r5;
            r5 = r15;
            goto L_0x011f;
        L_0x011e:
            r8 = r15;
        L_0x011f:
            r15 = 1;
            goto L_0x0123;
        L_0x0121:
            r3 = r3 - r8;
        L_0x0122:
            r8 = r5;
        L_0x0123:
            r9 = 33;
            r10 = "windowBackgroundWhiteBlueText4";
            r11 = -1;
            if (r15 != 0) goto L_0x017b;
        L_0x012a:
            if (r2 == 0) goto L_0x017b;
        L_0x012c:
            r15 = r13.searchAdapterHelper;
            r15 = r15.getGlobalSearch();
            r15 = r15.size();
            if (r15 == 0) goto L_0x017b;
        L_0x0138:
            r15 = r15 + r1;
            if (r15 <= r3) goto L_0x017b;
        L_0x013b:
            r15 = r13.searchAdapterHelper;
            r15 = r15.getLastFoundUsername();
            r5 = r15.startsWith(r7);
            if (r5 == 0) goto L_0x014b;
        L_0x0147:
            r15 = r15.substring(r1);
        L_0x014b:
            r1 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x0175 }
            r1.<init>();	 Catch:{ Exception -> 0x0175 }
            r1.append(r7);	 Catch:{ Exception -> 0x0175 }
            r1.append(r2);	 Catch:{ Exception -> 0x0175 }
            r5 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r15);	 Catch:{ Exception -> 0x0175 }
            if (r5 == r11) goto L_0x017c;
        L_0x015c:
            r15 = r15.length();	 Catch:{ Exception -> 0x0175 }
            if (r5 != 0) goto L_0x0165;
        L_0x0162:
            r15 = r15 + 1;
            goto L_0x0167;
        L_0x0165:
            r5 = r5 + 1;
        L_0x0167:
            r7 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x0175 }
            r12 = org.telegram.ui.ActionBar.Theme.getColor(r10);	 Catch:{ Exception -> 0x0175 }
            r7.<init>(r12);	 Catch:{ Exception -> 0x0175 }
            r15 = r15 + r5;
            r1.setSpan(r7, r5, r15, r9);	 Catch:{ Exception -> 0x0175 }
            goto L_0x017c;
        L_0x0175:
            r15 = move-exception;
            org.telegram.messenger.FileLog.e(r15);
            r1 = r2;
            goto L_0x017c;
        L_0x017b:
            r1 = r5;
        L_0x017c:
            if (r6 == 0) goto L_0x019e;
        L_0x017e:
            r15 = org.telegram.messenger.UserObject.getUserName(r0);
            r8 = new android.text.SpannableStringBuilder;
            r8.<init>(r15);
            r15 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r15, r6);
            if (r15 == r11) goto L_0x019e;
        L_0x018d:
            r2 = new android.text.style.ForegroundColorSpan;
            r5 = org.telegram.ui.ActionBar.Theme.getColor(r10);
            r2.<init>(r5);
            r5 = r6.length();
            r5 = r5 + r15;
            r8.setSpan(r2, r15, r5, r9);
        L_0x019e:
            r14 = r14.itemView;
            r14 = (org.telegram.ui.Cells.ManageChatUserCell) r14;
            r15 = java.lang.Integer.valueOf(r3);
            r14.setTag(r15);
            r14.setData(r0, r8, r1, r4);
        L_0x01ac:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity$SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            return (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) ? 1 : 0;
        }
    }

    private int getSecondsForIndex(int i) {
        return i == 1 ? 10 : i == 2 ? 30 : i == 3 ? 60 : i == 4 ? 300 : i == 5 ? 900 : i == 6 ? 3600 : 0;
    }

    public ChatUsersActivity(Bundle bundle) {
        super(bundle);
        Chat chat = this.currentChat;
        if (chat != null) {
            TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
            if (tL_chatBannedRights != null) {
                TL_chatBannedRights tL_chatBannedRights2 = this.defaultBannedRights;
                tL_chatBannedRights2.view_messages = tL_chatBannedRights.view_messages;
                tL_chatBannedRights2.send_stickers = tL_chatBannedRights.send_stickers;
                tL_chatBannedRights2.send_media = tL_chatBannedRights.send_media;
                tL_chatBannedRights2.embed_links = tL_chatBannedRights.embed_links;
                tL_chatBannedRights2.send_messages = tL_chatBannedRights.send_messages;
                tL_chatBannedRights2.send_games = tL_chatBannedRights.send_games;
                tL_chatBannedRights2.send_inline = tL_chatBannedRights.send_inline;
                tL_chatBannedRights2.send_gifs = tL_chatBannedRights.send_gifs;
                tL_chatBannedRights2.pin_messages = tL_chatBannedRights.pin_messages;
                tL_chatBannedRights2.send_polls = tL_chatBannedRights.send_polls;
                tL_chatBannedRights2.invite_users = tL_chatBannedRights.invite_users;
                tL_chatBannedRights2.change_info = tL_chatBannedRights.change_info;
            }
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        boolean z = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
        this.isChannel = z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0118  */
    /* JADX WARNING: Missing block: B:12:0x00ba, code skipped:
            if (org.telegram.messenger.ChatObject.canBlockUsers(r0) != false) goto L_0x00bc;
     */
    private void updateRows() {
        /*
        r5 = this;
        r0 = r5.getMessagesController();
        r1 = r5.chatId;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        r5.currentChat = r0;
        r0 = r5.currentChat;
        if (r0 != 0) goto L_0x0015;
    L_0x0014:
        return;
    L_0x0015:
        r1 = -1;
        r5.recentActionsRow = r1;
        r5.addNewRow = r1;
        r5.addNew2Row = r1;
        r5.addNewSectionRow = r1;
        r5.restricted1SectionRow = r1;
        r5.participantsStartRow = r1;
        r5.participantsDividerRow = r1;
        r5.participantsDivider2Row = r1;
        r5.participantsEndRow = r1;
        r5.participantsInfoRow = r1;
        r5.blockedEmptyRow = r1;
        r5.permissionsSectionRow = r1;
        r5.sendMessagesRow = r1;
        r5.sendMediaRow = r1;
        r5.sendStickersRow = r1;
        r5.sendPollsRow = r1;
        r5.embedLinksRow = r1;
        r5.addUsersRow = r1;
        r5.pinMessagesRow = r1;
        r5.changeInfoRow = r1;
        r5.removedUsersRow = r1;
        r5.contactsHeaderRow = r1;
        r5.contactsStartRow = r1;
        r5.contactsEndRow = r1;
        r5.botHeaderRow = r1;
        r5.botStartRow = r1;
        r5.botEndRow = r1;
        r5.membersHeaderRow = r1;
        r5.slowmodeRow = r1;
        r5.slowmodeSelectRow = r1;
        r5.slowmodeInfoRow = r1;
        r2 = 0;
        r5.rowCount = r2;
        r3 = r5.type;
        r4 = 3;
        if (r3 != r4) goto L_0x013b;
    L_0x005c:
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.permissionsSectionRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.sendMessagesRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.sendMediaRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.sendStickersRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.sendPollsRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.embedLinksRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.addUsersRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.pinMessagesRow = r2;
        r2 = r5.rowCount;
        r3 = r2 + 1;
        r5.rowCount = r3;
        r5.changeInfoRow = r2;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 != 0) goto L_0x00b0;
    L_0x00aa:
        r0 = r5.currentChat;
        r0 = r0.creator;
        if (r0 != 0) goto L_0x00bc;
    L_0x00b0:
        r0 = r5.currentChat;
        r2 = r0.megagroup;
        if (r2 == 0) goto L_0x00dc;
    L_0x00b6:
        r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0);
        if (r0 == 0) goto L_0x00dc;
    L_0x00bc:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.participantsDivider2Row = r0;
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.slowmodeRow = r0;
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.slowmodeSelectRow = r0;
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.slowmodeInfoRow = r0;
    L_0x00dc:
        r0 = r5.currentChat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x00f8;
    L_0x00e4:
        r0 = r5.participantsDivider2Row;
        if (r0 != r1) goto L_0x00f0;
    L_0x00e8:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.participantsDivider2Row = r0;
    L_0x00f0:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.removedUsersRow = r0;
    L_0x00f8:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.participantsDividerRow = r0;
        r0 = r5.currentChat;
        r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0);
        if (r0 == 0) goto L_0x0110;
    L_0x0108:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.addNewRow = r0;
    L_0x0110:
        r0 = r5.participants;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0129;
    L_0x0118:
        r0 = r5.rowCount;
        r5.participantsStartRow = r0;
        r2 = r5.participants;
        r2 = r2.size();
        r0 = r0 + r2;
        r5.rowCount = r0;
        r0 = r5.rowCount;
        r5.participantsEndRow = r0;
    L_0x0129:
        r0 = r5.addNewRow;
        if (r0 != r1) goto L_0x0131;
    L_0x012d:
        r0 = r5.participantsStartRow;
        if (r0 == r1) goto L_0x0285;
    L_0x0131:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.addNewSectionRow = r0;
        goto L_0x0285;
    L_0x013b:
        if (r3 != 0) goto L_0x01a2;
    L_0x013d:
        r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0);
        if (r0 == 0) goto L_0x015b;
    L_0x0143:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.addNewRow = r0;
        r0 = r5.participants;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x015b;
    L_0x0153:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.participantsInfoRow = r0;
    L_0x015b:
        r0 = r5.participants;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x017c;
    L_0x0163:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.restricted1SectionRow = r0;
        r0 = r5.rowCount;
        r5.participantsStartRow = r0;
        r2 = r5.participants;
        r2 = r2.size();
        r0 = r0 + r2;
        r5.rowCount = r0;
        r0 = r5.rowCount;
        r5.participantsEndRow = r0;
    L_0x017c:
        r0 = r5.participantsStartRow;
        if (r0 == r1) goto L_0x0198;
    L_0x0180:
        r0 = r5.participantsInfoRow;
        if (r0 != r1) goto L_0x018e;
    L_0x0184:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.participantsInfoRow = r0;
        goto L_0x0285;
    L_0x018e:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.addNewSectionRow = r0;
        goto L_0x0285;
    L_0x0198:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.blockedEmptyRow = r0;
        goto L_0x0285;
    L_0x01a2:
        r1 = 1;
        if (r3 != r1) goto L_0x01fe;
    L_0x01a5:
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x01cb;
    L_0x01ab:
        r0 = r5.currentChat;
        r0 = r0.megagroup;
        if (r0 == 0) goto L_0x01cb;
    L_0x01b1:
        r0 = r5.info;
        if (r0 == 0) goto L_0x01bb;
    L_0x01b5:
        r0 = r0.participants_count;
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r0 > r1) goto L_0x01cb;
    L_0x01bb:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.recentActionsRow = r0;
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.addNewSectionRow = r0;
    L_0x01cb:
        r0 = r5.currentChat;
        r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0);
        if (r0 == 0) goto L_0x01db;
    L_0x01d3:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.addNewRow = r0;
    L_0x01db:
        r0 = r5.participants;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x01f4;
    L_0x01e3:
        r0 = r5.rowCount;
        r5.participantsStartRow = r0;
        r1 = r5.participants;
        r1 = r1.size();
        r0 = r0 + r1;
        r5.rowCount = r0;
        r0 = r5.rowCount;
        r5.participantsEndRow = r0;
    L_0x01f4:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.participantsInfoRow = r0;
        goto L_0x0285;
    L_0x01fe:
        r4 = 2;
        if (r3 != r4) goto L_0x0285;
    L_0x0201:
        r3 = r5.selectType;
        if (r3 != 0) goto L_0x0213;
    L_0x0205:
        r0 = org.telegram.messenger.ChatObject.canAddUsers(r0);
        if (r0 == 0) goto L_0x0213;
    L_0x020b:
        r0 = r5.rowCount;
        r3 = r0 + 1;
        r5.rowCount = r3;
        r5.addNewRow = r0;
    L_0x0213:
        r0 = r5.contacts;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0235;
    L_0x021b:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.contactsHeaderRow = r0;
        r0 = r5.rowCount;
        r5.contactsStartRow = r0;
        r2 = r5.contacts;
        r2 = r2.size();
        r0 = r0 + r2;
        r5.rowCount = r0;
        r0 = r5.rowCount;
        r5.contactsEndRow = r0;
        r2 = 1;
    L_0x0235:
        r0 = r5.bots;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0257;
    L_0x023d:
        r0 = r5.rowCount;
        r2 = r0 + 1;
        r5.rowCount = r2;
        r5.botHeaderRow = r0;
        r0 = r5.rowCount;
        r5.botStartRow = r0;
        r2 = r5.bots;
        r2 = r2.size();
        r0 = r0 + r2;
        r5.rowCount = r0;
        r0 = r5.rowCount;
        r5.botEndRow = r0;
        goto L_0x0258;
    L_0x0257:
        r1 = r2;
    L_0x0258:
        r0 = r5.participants;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x027b;
    L_0x0260:
        if (r1 == 0) goto L_0x026a;
    L_0x0262:
        r0 = r5.rowCount;
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.membersHeaderRow = r0;
    L_0x026a:
        r0 = r5.rowCount;
        r5.participantsStartRow = r0;
        r1 = r5.participants;
        r1 = r1.size();
        r0 = r0 + r1;
        r5.rowCount = r0;
        r0 = r5.rowCount;
        r5.participantsEndRow = r0;
    L_0x027b:
        r0 = r5.rowCount;
        if (r0 == 0) goto L_0x0285;
    L_0x027f:
        r1 = r0 + 1;
        r5.rowCount = r1;
        r5.participantsInfoRow = r0;
    L_0x0285:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.updateRows():void");
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

    /* JADX WARNING: Missing block: B:25:0x00b9, code skipped:
            if (r1 != 3) goto L_0x011b;
     */
    public android.view.View createView(android.content.Context r10) {
        /*
        r9 = this;
        r0 = 0;
        r9.searching = r0;
        r9.searchWas = r0;
        r1 = r9.actionBar;
        r2 = NUM; // 0x7var_e8 float:1.7945048E38 double:1.0529356177E-314;
        r1.setBackButtonImage(r2);
        r1 = r9.actionBar;
        r2 = 1;
        r1.setAllowOverlayTitle(r2);
        r1 = r9.type;
        r3 = 2;
        r4 = 3;
        if (r1 != r4) goto L_0x0029;
    L_0x0019:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e025a float:1.8876259E38 double:1.053162454E-314;
        r6 = "ChannelPermissions";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
        goto L_0x00a5;
    L_0x0029:
        if (r1 != 0) goto L_0x003a;
    L_0x002b:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e0225 float:1.8876151E38 double:1.053162428E-314;
        r6 = "ChannelBlacklist";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
        goto L_0x00a5;
    L_0x003a:
        if (r1 != r2) goto L_0x004b;
    L_0x003c:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e0220 float:1.887614E38 double:1.0531624254E-314;
        r6 = "ChannelAdministrators";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
        goto L_0x00a5;
    L_0x004b:
        if (r1 != r3) goto L_0x00a5;
    L_0x004d:
        r1 = r9.selectType;
        if (r1 != 0) goto L_0x0073;
    L_0x0051:
        r1 = r9.isChannel;
        if (r1 == 0) goto L_0x0064;
    L_0x0055:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e026d float:1.8876297E38 double:1.0531624634E-314;
        r6 = "ChannelSubscribers";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
        goto L_0x00a5;
    L_0x0064:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e0243 float:1.8876212E38 double:1.0531624427E-314;
        r6 = "ChannelMembers";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
        goto L_0x00a5;
    L_0x0073:
        if (r1 != r2) goto L_0x0084;
    L_0x0075:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e0216 float:1.887612E38 double:1.0531624205E-314;
        r6 = "ChannelAddAdmin";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
        goto L_0x00a5;
    L_0x0084:
        if (r1 != r3) goto L_0x0095;
    L_0x0086:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e0226 float:1.8876153E38 double:1.0531624284E-314;
        r6 = "ChannelBlockUser";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
        goto L_0x00a5;
    L_0x0095:
        if (r1 != r4) goto L_0x00a5;
    L_0x0097:
        r1 = r9.actionBar;
        r5 = NUM; // 0x7f0e0217 float:1.8876123E38 double:1.053162421E-314;
        r6 = "ChannelAddException";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r1.setTitle(r5);
    L_0x00a5:
        r1 = r9.actionBar;
        r5 = new org.telegram.ui.ChatUsersActivity$1;
        r5.<init>();
        r1.setActionBarMenuOnItemClick(r5);
        r1 = r9.selectType;
        if (r1 != 0) goto L_0x00bb;
    L_0x00b3:
        r1 = r9.type;
        if (r1 == r3) goto L_0x00bb;
    L_0x00b7:
        if (r1 == 0) goto L_0x00bb;
    L_0x00b9:
        if (r1 != r4) goto L_0x011b;
    L_0x00bb:
        r1 = new org.telegram.ui.ChatUsersActivity$SearchAdapter;
        r1.<init>(r10);
        r9.searchListViewAdapter = r1;
        r1 = r9.actionBar;
        r1 = r1.createMenu();
        r5 = NUM; // 0x7var_f2 float:1.7945069E38 double:1.0529356226E-314;
        r5 = r1.addItem(r0, r5);
        r5 = r5.setIsSearchField(r2);
        r6 = new org.telegram.ui.ChatUsersActivity$2;
        r6.<init>();
        r5 = r5.setActionBarMenuItemSearchListener(r6);
        r9.searchItem = r5;
        r5 = r9.type;
        if (r5 != r4) goto L_0x00f1;
    L_0x00e2:
        r5 = r9.searchItem;
        r6 = NUM; // 0x7f0e0266 float:1.8876283E38 double:1.05316246E-314;
        r7 = "ChannelSearchException";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r5.setSearchFieldHint(r6);
        goto L_0x00ff;
    L_0x00f1:
        r5 = r9.searchItem;
        r6 = NUM; // 0x7f0e094f float:1.887987E38 double:1.053163334E-314;
        r7 = "Search";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r5.setSearchFieldHint(r6);
    L_0x00ff:
        r5 = r9.type;
        if (r5 != r4) goto L_0x011b;
    L_0x0103:
        r5 = NUM; // 0x7var_ float:1.794511E38 double:1.0529356325E-314;
        r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x7f0e03a9 float:1.8876938E38 double:1.0531626196E-314;
        r8 = "Done";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r1 = r1.addItemWithWidth(r2, r5, r6, r7);
        r9.doneItem = r1;
    L_0x011b:
        r1 = new android.widget.FrameLayout;
        r1.<init>(r10);
        r9.fragmentView = r1;
        r1 = r9.fragmentView;
        r5 = "windowBackgroundGray";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r1.setBackgroundColor(r5);
        r1 = r9.fragmentView;
        r1 = (android.widget.FrameLayout) r1;
        r5 = new org.telegram.ui.Components.EmptyTextProgressView;
        r5.<init>(r10);
        r9.emptyView = r5;
        r5 = r9.type;
        if (r5 == 0) goto L_0x0140;
    L_0x013c:
        if (r5 == r3) goto L_0x0140;
    L_0x013e:
        if (r5 != r4) goto L_0x014e;
    L_0x0140:
        r4 = r9.emptyView;
        r5 = NUM; // 0x7f0e066d float:1.8878374E38 double:1.0531629694E-314;
        r6 = "NoResult";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r4.setText(r5);
    L_0x014e:
        r4 = r9.emptyView;
        r4.setShowAtCenter(r2);
        r4 = r9.emptyView;
        r5 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r6 = -1;
        r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5);
        r1.addView(r4, r7);
        r4 = new org.telegram.ui.Components.RecyclerListView;
        r4.<init>(r10);
        r9.listView = r4;
        r4 = r9.listView;
        r7 = r9.emptyView;
        r4.setEmptyView(r7);
        r4 = r9.listView;
        r7 = new androidx.recyclerview.widget.LinearLayoutManager;
        r7.<init>(r10, r2, r0);
        r4.setLayoutManager(r7);
        r0 = r9.listView;
        r4 = new org.telegram.ui.ChatUsersActivity$ListAdapter;
        r4.<init>(r10);
        r9.listViewAdapter = r4;
        r0.setAdapter(r4);
        r0 = r9.listView;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x018a;
    L_0x0189:
        goto L_0x018b;
    L_0x018a:
        r2 = 2;
    L_0x018b:
        r0.setVerticalScrollbarPosition(r2);
        r0 = r9.listView;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5);
        r1.addView(r0, r2);
        r0 = r9.listView;
        r2 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$NZfH3lLhYK75IchC8EUeaUmZVB4;
        r2.<init>(r9);
        r0.setOnItemClickListener(r2);
        r0 = r9.listView;
        r2 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$k6-qzY2kz4GbcNGf2mKY2QD1vEo;
        r2.<init>(r9);
        r0.setOnItemLongClickListener(r2);
        r0 = r9.searchItem;
        if (r0 == 0) goto L_0x01b9;
    L_0x01af:
        r0 = r9.listView;
        r2 = new org.telegram.ui.ChatUsersActivity$6;
        r2.<init>();
        r0.setOnScrollListener(r2);
    L_0x01b9:
        r0 = new org.telegram.ui.Components.UndoView;
        r0.<init>(r10);
        r9.undoView = r0;
        r10 = r9.undoView;
        r2 = -1;
        r3 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r4 = 83;
        r5 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = 0;
        r7 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8);
        r1.addView(r10, r0);
        r10 = r9.loadingUsers;
        if (r10 == 0) goto L_0x01df;
    L_0x01d9:
        r10 = r9.emptyView;
        r10.showProgress();
        goto L_0x01e4;
    L_0x01df:
        r10 = r9.emptyView;
        r10.showTextView();
    L_0x01e4:
        r9.updateRows();
        r10 = r9.fragmentView;
        return r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createView(android.content.Context):android.view.View");
    }

    /* JADX WARNING: Removed duplicated region for block: B:261:0x04cb  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04ca A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0397  */
    public /* synthetic */ void lambda$createView$1$ChatUsersActivity(android.view.View r22, int r23) {
        /*
        r21 = this;
        r9 = r21;
        r0 = r23;
        r1 = r9.listView;
        r1 = r1.getAdapter();
        r2 = r9.listViewAdapter;
        r3 = 0;
        r4 = 1;
        if (r1 != r2) goto L_0x0012;
    L_0x0010:
        r1 = 1;
        goto L_0x0013;
    L_0x0012:
        r1 = 0;
    L_0x0013:
        r2 = 3;
        r5 = 2;
        if (r1 == 0) goto L_0x029c;
    L_0x0017:
        r6 = r9.addNewRow;
        r7 = "type";
        r8 = "chat_id";
        if (r0 != r6) goto L_0x00b7;
    L_0x001f:
        r0 = r9.type;
        r1 = "selectType";
        if (r0 == 0) goto L_0x0094;
    L_0x0025:
        if (r0 != r2) goto L_0x0028;
    L_0x0027:
        goto L_0x0094;
    L_0x0028:
        if (r0 != r4) goto L_0x0050;
    L_0x002a:
        r0 = new android.os.Bundle;
        r0.<init>();
        r2 = r9.chatId;
        r0.putInt(r8, r2);
        r0.putInt(r7, r5);
        r0.putInt(r1, r4);
        r1 = new org.telegram.ui.ChatUsersActivity;
        r1.<init>(r0);
        r0 = new org.telegram.ui.ChatUsersActivity$3;
        r0.<init>();
        r1.setDelegate(r0);
        r0 = r9.info;
        r1.setInfo(r0);
        r9.presentFragment(r1);
        goto L_0x00b6;
    L_0x0050:
        if (r0 != r5) goto L_0x00b6;
    L_0x0052:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "addToGroup";
        r0.putBoolean(r1, r4);
        r1 = r9.isChannel;
        if (r1 == 0) goto L_0x0063;
    L_0x0060:
        r1 = "channelId";
        goto L_0x0065;
    L_0x0063:
        r1 = "chatId";
    L_0x0065:
        r2 = r9.currentChat;
        r2 = r2.id;
        r0.putInt(r1, r2);
        r1 = new org.telegram.ui.GroupCreateActivity;
        r1.<init>(r0);
        r0 = r9.info;
        r1.setInfo(r0);
        r0 = r9.contactsMap;
        if (r0 == 0) goto L_0x0083;
    L_0x007a:
        r0 = r0.size();
        if (r0 == 0) goto L_0x0083;
    L_0x0080:
        r0 = r9.contactsMap;
        goto L_0x0085;
    L_0x0083:
        r0 = r9.participantsMap;
    L_0x0085:
        r1.setIgnoreUsers(r0);
        r0 = new org.telegram.ui.ChatUsersActivity$4;
        r0.<init>();
        r1.setDelegate(r0);
        r9.presentFragment(r1);
        goto L_0x00b6;
    L_0x0094:
        r0 = new android.os.Bundle;
        r0.<init>();
        r3 = r9.chatId;
        r0.putInt(r8, r3);
        r0.putInt(r7, r5);
        r3 = r9.type;
        if (r3 != 0) goto L_0x00a6;
    L_0x00a5:
        r2 = 2;
    L_0x00a6:
        r0.putInt(r1, r2);
        r1 = new org.telegram.ui.ChatUsersActivity;
        r1.<init>(r0);
        r0 = r9.info;
        r1.setInfo(r0);
        r9.presentFragment(r1);
    L_0x00b6:
        return;
    L_0x00b7:
        r6 = r9.recentActionsRow;
        if (r0 != r6) goto L_0x00c6;
    L_0x00bb:
        r0 = new org.telegram.ui.ChannelAdminLogActivity;
        r1 = r9.currentChat;
        r0.<init>(r1);
        r9.presentFragment(r0);
        return;
    L_0x00c6:
        r6 = r9.removedUsersRow;
        if (r0 != r6) goto L_0x00e5;
    L_0x00ca:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = r9.chatId;
        r0.putInt(r8, r1);
        r0.putInt(r7, r3);
        r1 = new org.telegram.ui.ChatUsersActivity;
        r1.<init>(r0);
        r0 = r9.info;
        r1.setInfo(r0);
        r9.presentFragment(r1);
        return;
    L_0x00e5:
        r6 = r9.addNew2Row;
        if (r0 != r6) goto L_0x00f4;
    L_0x00e9:
        r0 = new org.telegram.ui.GroupInviteActivity;
        r1 = r9.chatId;
        r0.<init>(r1);
        r9.presentFragment(r0);
        return;
    L_0x00f4:
        r6 = r9.permissionsSectionRow;
        if (r0 <= r6) goto L_0x029c;
    L_0x00f8:
        r6 = r9.changeInfoRow;
        if (r0 > r6) goto L_0x029c;
    L_0x00fc:
        r1 = r22;
        r1 = (org.telegram.ui.Cells.TextCheckCell2) r1;
        r2 = r1.isEnabled();
        if (r2 != 0) goto L_0x0107;
    L_0x0106:
        return;
    L_0x0107:
        r2 = r1.hasIcon();
        if (r2 == 0) goto L_0x0149;
    L_0x010d:
        r1 = r9.currentChat;
        r1 = r1.username;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0134;
    L_0x0117:
        r1 = r9.pinMessagesRow;
        if (r0 == r1) goto L_0x011f;
    L_0x011b:
        r1 = r9.changeInfoRow;
        if (r0 != r1) goto L_0x0134;
    L_0x011f:
        r0 = r21.getParentActivity();
        r1 = NUM; // 0x7f0e03cc float:1.887701E38 double:1.053162637E-314;
        r2 = "EditCantEditPermissionsPublic";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0 = android.widget.Toast.makeText(r0, r1, r3);
        r0.show();
        goto L_0x0148;
    L_0x0134:
        r0 = r21.getParentActivity();
        r1 = NUM; // 0x7f0e03cb float:1.8877007E38 double:1.0531626364E-314;
        r2 = "EditCantEditPermissions";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0 = android.widget.Toast.makeText(r0, r1, r3);
        r0.show();
    L_0x0148:
        return;
    L_0x0149:
        r2 = r1.isChecked();
        r2 = r2 ^ r4;
        r1.setChecked(r2);
        r2 = r9.changeInfoRow;
        if (r0 != r2) goto L_0x015e;
    L_0x0155:
        r0 = r9.defaultBannedRights;
        r1 = r0.change_info;
        r1 = r1 ^ r4;
        r0.change_info = r1;
        goto L_0x029b;
    L_0x015e:
        r2 = r9.addUsersRow;
        if (r0 != r2) goto L_0x016b;
    L_0x0162:
        r0 = r9.defaultBannedRights;
        r1 = r0.invite_users;
        r1 = r1 ^ r4;
        r0.invite_users = r1;
        goto L_0x029b;
    L_0x016b:
        r2 = r9.pinMessagesRow;
        if (r0 != r2) goto L_0x0178;
    L_0x016f:
        r0 = r9.defaultBannedRights;
        r1 = r0.pin_messages;
        r1 = r1 ^ r4;
        r0.pin_messages = r1;
        goto L_0x029b;
    L_0x0178:
        r1 = r1.isChecked();
        r1 = r1 ^ r4;
        r2 = r9.sendMessagesRow;
        if (r0 != r2) goto L_0x0189;
    L_0x0181:
        r0 = r9.defaultBannedRights;
        r2 = r0.send_messages;
        r2 = r2 ^ r4;
        r0.send_messages = r2;
        goto L_0x01be;
    L_0x0189:
        r2 = r9.sendMediaRow;
        if (r0 != r2) goto L_0x0195;
    L_0x018d:
        r0 = r9.defaultBannedRights;
        r2 = r0.send_media;
        r2 = r2 ^ r4;
        r0.send_media = r2;
        goto L_0x01be;
    L_0x0195:
        r2 = r9.sendStickersRow;
        if (r0 != r2) goto L_0x01a7;
    L_0x0199:
        r0 = r9.defaultBannedRights;
        r2 = r0.send_stickers;
        r2 = r2 ^ r4;
        r0.send_inline = r2;
        r0.send_gifs = r2;
        r0.send_games = r2;
        r0.send_stickers = r2;
        goto L_0x01be;
    L_0x01a7:
        r2 = r9.embedLinksRow;
        if (r0 != r2) goto L_0x01b3;
    L_0x01ab:
        r0 = r9.defaultBannedRights;
        r2 = r0.embed_links;
        r2 = r2 ^ r4;
        r0.embed_links = r2;
        goto L_0x01be;
    L_0x01b3:
        r2 = r9.sendPollsRow;
        if (r0 != r2) goto L_0x01be;
    L_0x01b7:
        r0 = r9.defaultBannedRights;
        r2 = r0.send_polls;
        r2 = r2 ^ r4;
        r0.send_polls = r2;
    L_0x01be:
        if (r1 == 0) goto L_0x0270;
    L_0x01c0:
        r0 = r9.defaultBannedRights;
        r1 = r0.view_messages;
        if (r1 == 0) goto L_0x01dd;
    L_0x01c6:
        r1 = r0.send_messages;
        if (r1 != 0) goto L_0x01dd;
    L_0x01ca:
        r0.send_messages = r4;
        r0 = r9.listView;
        r1 = r9.sendMessagesRow;
        r0 = r0.findViewHolderForAdapterPosition(r1);
        if (r0 == 0) goto L_0x01dd;
    L_0x01d6:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.TextCheckCell2) r0;
        r0.setChecked(r3);
    L_0x01dd:
        r0 = r9.defaultBannedRights;
        r1 = r0.view_messages;
        if (r1 != 0) goto L_0x01e7;
    L_0x01e3:
        r0 = r0.send_messages;
        if (r0 == 0) goto L_0x0200;
    L_0x01e7:
        r0 = r9.defaultBannedRights;
        r1 = r0.send_media;
        if (r1 != 0) goto L_0x0200;
    L_0x01ed:
        r0.send_media = r4;
        r0 = r9.listView;
        r1 = r9.sendMediaRow;
        r0 = r0.findViewHolderForAdapterPosition(r1);
        if (r0 == 0) goto L_0x0200;
    L_0x01f9:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.TextCheckCell2) r0;
        r0.setChecked(r3);
    L_0x0200:
        r0 = r9.defaultBannedRights;
        r1 = r0.view_messages;
        if (r1 != 0) goto L_0x020a;
    L_0x0206:
        r0 = r0.send_messages;
        if (r0 == 0) goto L_0x0223;
    L_0x020a:
        r0 = r9.defaultBannedRights;
        r1 = r0.send_polls;
        if (r1 != 0) goto L_0x0223;
    L_0x0210:
        r0.send_polls = r4;
        r0 = r9.listView;
        r1 = r9.sendPollsRow;
        r0 = r0.findViewHolderForAdapterPosition(r1);
        if (r0 == 0) goto L_0x0223;
    L_0x021c:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.TextCheckCell2) r0;
        r0.setChecked(r3);
    L_0x0223:
        r0 = r9.defaultBannedRights;
        r1 = r0.view_messages;
        if (r1 != 0) goto L_0x022d;
    L_0x0229:
        r0 = r0.send_messages;
        if (r0 == 0) goto L_0x024c;
    L_0x022d:
        r0 = r9.defaultBannedRights;
        r1 = r0.send_stickers;
        if (r1 != 0) goto L_0x024c;
    L_0x0233:
        r0.send_inline = r4;
        r0.send_gifs = r4;
        r0.send_games = r4;
        r0.send_stickers = r4;
        r0 = r9.listView;
        r1 = r9.sendStickersRow;
        r0 = r0.findViewHolderForAdapterPosition(r1);
        if (r0 == 0) goto L_0x024c;
    L_0x0245:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.TextCheckCell2) r0;
        r0.setChecked(r3);
    L_0x024c:
        r0 = r9.defaultBannedRights;
        r1 = r0.view_messages;
        if (r1 != 0) goto L_0x0256;
    L_0x0252:
        r0 = r0.send_messages;
        if (r0 == 0) goto L_0x029b;
    L_0x0256:
        r0 = r9.defaultBannedRights;
        r1 = r0.embed_links;
        if (r1 != 0) goto L_0x029b;
    L_0x025c:
        r0.embed_links = r4;
        r0 = r9.listView;
        r1 = r9.embedLinksRow;
        r0 = r0.findViewHolderForAdapterPosition(r1);
        if (r0 == 0) goto L_0x029b;
    L_0x0268:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.TextCheckCell2) r0;
        r0.setChecked(r3);
        goto L_0x029b;
    L_0x0270:
        r0 = r9.defaultBannedRights;
        r1 = r0.embed_links;
        if (r1 == 0) goto L_0x0282;
    L_0x0276:
        r1 = r0.send_inline;
        if (r1 == 0) goto L_0x0282;
    L_0x027a:
        r1 = r0.send_media;
        if (r1 == 0) goto L_0x0282;
    L_0x027e:
        r0 = r0.send_polls;
        if (r0 != 0) goto L_0x029b;
    L_0x0282:
        r0 = r9.defaultBannedRights;
        r1 = r0.send_messages;
        if (r1 == 0) goto L_0x029b;
    L_0x0288:
        r0.send_messages = r3;
        r0 = r9.listView;
        r1 = r9.sendMessagesRow;
        r0 = r0.findViewHolderForAdapterPosition(r1);
        if (r0 == 0) goto L_0x029b;
    L_0x0294:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.TextCheckCell2) r0;
        r0.setChecked(r4);
    L_0x029b:
        return;
    L_0x029c:
        r8 = 0;
        r6 = "";
        if (r1 == 0) goto L_0x0320;
    L_0x02a1:
        r1 = r9.listViewAdapter;
        r0 = r1.getItem(r0);
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant;
        if (r1 == 0) goto L_0x02e7;
    L_0x02ab:
        r1 = r0;
        r1 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r1;
        r6 = r1.user_id;
        r7 = r1.banned_rights;
        r10 = r1.admin_rights;
        r11 = r1.rank;
        r12 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
        if (r12 != 0) goto L_0x02be;
    L_0x02ba:
        r12 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r12 == 0) goto L_0x02c2;
    L_0x02be:
        r1 = r1.can_edit;
        if (r1 == 0) goto L_0x02c4;
    L_0x02c2:
        r1 = 1;
        goto L_0x02c5;
    L_0x02c4:
        r1 = 0;
    L_0x02c5:
        r12 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r12 == 0) goto L_0x02de;
    L_0x02c9:
        r10 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r10.<init>();
        r10.add_admins = r4;
        r10.pin_messages = r4;
        r10.invite_users = r4;
        r10.ban_users = r4;
        r10.delete_messages = r4;
        r10.edit_messages = r4;
        r10.post_messages = r4;
        r10.change_info = r4;
    L_0x02de:
        r13 = r10;
        r16 = r11;
        r11 = r6;
        r10 = r7;
        r6 = r0;
        r7 = r1;
        goto L_0x0395;
    L_0x02e7:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.ChatParticipant;
        if (r1 == 0) goto L_0x0317;
    L_0x02eb:
        r1 = r0;
        r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1;
        r1 = r1.user_id;
        r7 = r9.currentChat;
        r7 = r7.creator;
        r10 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
        if (r10 == 0) goto L_0x030e;
    L_0x02f8:
        r10 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r10.<init>();
        r10.add_admins = r4;
        r10.pin_messages = r4;
        r10.invite_users = r4;
        r10.ban_users = r4;
        r10.delete_messages = r4;
        r10.edit_messages = r4;
        r10.post_messages = r4;
        r10.change_info = r4;
        goto L_0x030f;
    L_0x030e:
        r10 = r8;
    L_0x030f:
        r11 = r1;
        r16 = r6;
        r13 = r10;
        r6 = r0;
        r10 = r8;
        goto L_0x0395;
    L_0x0317:
        r16 = r6;
        r10 = r8;
        r13 = r10;
        r7 = 0;
        r11 = 0;
    L_0x031d:
        r6 = r0;
        goto L_0x0395;
    L_0x0320:
        r1 = r9.searchListViewAdapter;
        r0 = r1.getItem(r0);
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.User;
        if (r1 == 0) goto L_0x033f;
    L_0x032a:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r1 = r21.getMessagesController();
        r1.putUser(r0, r3);
        r0 = r0.id;
        r1 = r9.getAnyParticipant(r0);
        r20 = r1;
        r1 = r0;
        r0 = r20;
        goto L_0x034a;
    L_0x033f:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant;
        if (r1 != 0) goto L_0x0349;
    L_0x0343:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.ChatParticipant;
        if (r1 == 0) goto L_0x0348;
    L_0x0347:
        goto L_0x0349;
    L_0x0348:
        r0 = r8;
    L_0x0349:
        r1 = 0;
    L_0x034a:
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant;
        if (r7 == 0) goto L_0x0372;
    L_0x034e:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r1 == 0) goto L_0x0353;
    L_0x0352:
        return;
    L_0x0353:
        r1 = r0;
        r1 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r1;
        r6 = r1.user_id;
        r7 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
        if (r7 != 0) goto L_0x0360;
    L_0x035c:
        r7 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r7 == 0) goto L_0x0364;
    L_0x0360:
        r7 = r1.can_edit;
        if (r7 == 0) goto L_0x0366;
    L_0x0364:
        r7 = 1;
        goto L_0x0367;
    L_0x0366:
        r7 = 0;
    L_0x0367:
        r10 = r1.banned_rights;
        r11 = r1.admin_rights;
        r1 = r1.rank;
        r16 = r1;
        r13 = r11;
        r11 = r6;
        goto L_0x031d;
    L_0x0372:
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.ChatParticipant;
        if (r7 == 0) goto L_0x038a;
    L_0x0376:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
        if (r1 == 0) goto L_0x037b;
    L_0x037a:
        return;
    L_0x037b:
        r1 = r0;
        r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1;
        r1 = r1.user_id;
        r7 = r9.currentChat;
        r7 = r7.creator;
        r11 = r1;
        r16 = r6;
        r10 = r8;
        r13 = r10;
        goto L_0x031d;
    L_0x038a:
        r11 = r1;
        r16 = r6;
        r10 = r8;
        r13 = r10;
        if (r0 != 0) goto L_0x0393;
    L_0x0391:
        r7 = 1;
        goto L_0x031d;
    L_0x0393:
        r7 = 0;
        goto L_0x031d;
    L_0x0395:
        if (r11 == 0) goto L_0x04dd;
    L_0x0397:
        r0 = r9.selectType;
        if (r0 == 0) goto L_0x043b;
    L_0x039b:
        if (r0 == r2) goto L_0x03a5;
    L_0x039d:
        if (r0 != r4) goto L_0x03a0;
    L_0x039f:
        goto L_0x03a5;
    L_0x03a0:
        r9.removeUser(r11);
        goto L_0x04dd;
    L_0x03a5:
        r0 = r9.selectType;
        if (r0 == r4) goto L_0x041a;
    L_0x03a9:
        if (r7 == 0) goto L_0x041a;
    L_0x03ab:
        r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
        if (r0 != 0) goto L_0x03b3;
    L_0x03af:
        r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
        if (r0 == 0) goto L_0x041a;
    L_0x03b3:
        r0 = r21.getMessagesController();
        r1 = java.lang.Integer.valueOf(r11);
        r2 = r0.getUser(r1);
        r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0 = r21.getParentActivity();
        r11.<init>(r0);
        r0 = NUM; // 0x7f0e00f1 float:1.8875526E38 double:1.0531622757E-314;
        r1 = "AppName";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r11.setTitle(r0);
        r0 = NUM; // 0x7f0e00cc float:1.8875451E38 double:1.0531622574E-314;
        r1 = new java.lang.Object[r4];
        r4 = r2.first_name;
        r5 = r2.last_name;
        r4 = org.telegram.messenger.ContactsController.formatName(r4, r5);
        r1[r3] = r4;
        r3 = "AdminWillBeRemoved";
        r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1);
        r11.setMessage(r0);
        r0 = NUM; // 0x7f0e070e float:1.88787E38 double:1.053163049E-314;
        r1 = "OK";
        r12 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r14 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$QBAiFB7vQBVLCLASSNAMElkUtoQa9r6rI;
        r0 = r14;
        r1 = r21;
        r3 = r6;
        r4 = r13;
        r5 = r10;
        r6 = r16;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        r11.setPositiveButton(r12, r14);
        r0 = NUM; // 0x7f0e01fa float:1.8876064E38 double:1.0531624066E-314;
        r1 = "Cancel";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r11.setNegativeButton(r0, r8);
        r0 = r11.create();
        r9.showDialog(r0);
        goto L_0x04dd;
    L_0x041a:
        r0 = r9.selectType;
        if (r0 != r4) goto L_0x0420;
    L_0x041e:
        r8 = 0;
        goto L_0x0421;
    L_0x0420:
        r8 = 1;
    L_0x0421:
        r0 = r9.selectType;
        if (r0 == r4) goto L_0x042a;
    L_0x0425:
        if (r0 != r2) goto L_0x0428;
    L_0x0427:
        goto L_0x042a;
    L_0x0428:
        r12 = 0;
        goto L_0x042b;
    L_0x042a:
        r12 = 1;
    L_0x042b:
        r0 = r21;
        r1 = r11;
        r2 = r6;
        r3 = r13;
        r4 = r10;
        r5 = r16;
        r6 = r7;
        r7 = r8;
        r8 = r12;
        r0.openRightsEdit(r1, r2, r3, r4, r5, r6, r7, r8);
        goto L_0x04dd;
    L_0x043b:
        r0 = r9.type;
        if (r0 != r4) goto L_0x0455;
    L_0x043f:
        r0 = r21.getUserConfig();
        r0 = r0.getClientUserId();
        if (r11 == r0) goto L_0x0453;
    L_0x0449:
        r0 = r9.currentChat;
        r0 = r0.creator;
        if (r0 != 0) goto L_0x0451;
    L_0x044f:
        if (r7 == 0) goto L_0x0453;
    L_0x0451:
        r0 = 1;
        goto L_0x0463;
    L_0x0453:
        r0 = 0;
        goto L_0x0463;
    L_0x0455:
        if (r0 == 0) goto L_0x045d;
    L_0x0457:
        if (r0 != r2) goto L_0x045a;
    L_0x0459:
        goto L_0x045d;
    L_0x045a:
        r18 = 0;
        goto L_0x0465;
    L_0x045d:
        r0 = r9.currentChat;
        r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0);
    L_0x0463:
        r18 = r0;
    L_0x0465:
        r0 = r9.type;
        if (r0 == 0) goto L_0x04c0;
    L_0x0469:
        if (r0 == r4) goto L_0x046f;
    L_0x046b:
        r0 = r9.isChannel;
        if (r0 != 0) goto L_0x04c0;
    L_0x046f:
        r0 = r9.type;
        if (r0 != r5) goto L_0x0478;
    L_0x0473:
        r0 = r9.selectType;
        if (r0 != 0) goto L_0x0478;
    L_0x0477:
        goto L_0x04c0;
    L_0x0478:
        if (r10 != 0) goto L_0x0499;
    L_0x047a:
        r0 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r0.<init>();
        r0.view_messages = r4;
        r0.send_stickers = r4;
        r0.send_media = r4;
        r0.embed_links = r4;
        r0.send_messages = r4;
        r0.send_games = r4;
        r0.send_inline = r4;
        r0.send_gifs = r4;
        r0.pin_messages = r4;
        r0.send_polls = r4;
        r0.invite_users = r4;
        r0.change_info = r4;
        r15 = r0;
        goto L_0x049a;
    L_0x0499:
        r15 = r10;
    L_0x049a:
        r0 = new org.telegram.ui.ChatRightsEditActivity;
        r12 = r9.chatId;
        r14 = r9.defaultBannedRights;
        r1 = r9.type;
        if (r1 != r4) goto L_0x04a7;
    L_0x04a4:
        r17 = 0;
        goto L_0x04a9;
    L_0x04a7:
        r17 = 1;
    L_0x04a9:
        if (r6 != 0) goto L_0x04ae;
    L_0x04ab:
        r19 = 1;
        goto L_0x04b0;
    L_0x04ae:
        r19 = 0;
    L_0x04b0:
        r10 = r0;
        r10.<init>(r11, r12, r13, r14, r15, r16, r17, r18, r19);
        r1 = new org.telegram.ui.ChatUsersActivity$5;
        r1.<init>(r6);
        r0.setDelegate(r1);
        r9.presentFragment(r0);
        goto L_0x04dd;
    L_0x04c0:
        r0 = r21.getUserConfig();
        r0 = r0.getClientUserId();
        if (r11 != r0) goto L_0x04cb;
    L_0x04ca:
        return;
    L_0x04cb:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "user_id";
        r0.putInt(r1, r11);
        r1 = new org.telegram.ui.ProfileActivity;
        r1.<init>(r0);
        r9.presentFragment(r1);
    L_0x04dd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$1$ChatUsersActivity(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$null$0$ChatUsersActivity(User user, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str, boolean z, DialogInterface dialogInterface, int i) {
        openRightsEdit(user.id, tLObject, tL_chatAdminRights, tL_chatBannedRights, str, z, this.selectType == 1 ? 0 : 1, false);
    }

    public /* synthetic */ boolean lambda$createView$2$ChatUsersActivity(View view, int i) {
        if (getParentActivity() == null) {
            return false;
        }
        Adapter adapter = this.listView.getAdapter();
        Adapter adapter2 = this.listViewAdapter;
        return adapter == adapter2 && createMenuForParticipant(adapter2.getItem(i), false);
    }

    private void onOwnerChaged(User user) {
        this.undoView.showWithAction((long) (-this.chatId), this.isChannel ? 9 : 10, (Object) user);
        this.currentChat.creator = false;
        int i = 0;
        Object obj = null;
        while (i < 3) {
            SparseArray sparseArray;
            ArrayList arrayList;
            int i2;
            Object obj2;
            if (i == 0) {
                sparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                sparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                sparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = (TLObject) sparseArray.get(user.id);
            if (tLObject instanceof ChannelParticipant) {
                TL_channelParticipantCreator tL_channelParticipantCreator = new TL_channelParticipantCreator();
                i2 = user.id;
                tL_channelParticipantCreator.user_id = i2;
                sparseArray.put(i2, tL_channelParticipantCreator);
                int indexOf = arrayList.indexOf(tLObject);
                if (indexOf >= 0) {
                    arrayList.set(indexOf, tL_channelParticipantCreator);
                }
                obj = 1;
                obj2 = 1;
            } else {
                obj2 = obj;
                obj = null;
            }
            i2 = getUserConfig().getClientUserId();
            TLObject tLObject2 = (TLObject) sparseArray.get(i2);
            if (tLObject2 instanceof ChannelParticipant) {
                TL_channelParticipantAdmin tL_channelParticipantAdmin = new TL_channelParticipantAdmin();
                tL_channelParticipantAdmin.user_id = i2;
                tL_channelParticipantAdmin.self = true;
                tL_channelParticipantAdmin.inviter_id = i2;
                tL_channelParticipantAdmin.promoted_by = i2;
                tL_channelParticipantAdmin.date = (int) (System.currentTimeMillis() / 1000);
                tL_channelParticipantAdmin.admin_rights = new TL_chatAdminRights();
                TL_chatAdminRights tL_chatAdminRights = tL_channelParticipantAdmin.admin_rights;
                tL_chatAdminRights.add_admins = true;
                tL_chatAdminRights.pin_messages = true;
                tL_chatAdminRights.invite_users = true;
                tL_chatAdminRights.ban_users = true;
                tL_chatAdminRights.delete_messages = true;
                tL_chatAdminRights.edit_messages = true;
                tL_chatAdminRights.post_messages = true;
                tL_chatAdminRights.change_info = true;
                sparseArray.put(i2, tL_channelParticipantAdmin);
                int indexOf2 = arrayList.indexOf(tLObject2);
                if (indexOf2 >= 0) {
                    arrayList.set(indexOf2, tL_channelParticipantAdmin);
                }
                obj = 1;
            }
            if (obj != null) {
                Collections.sort(arrayList, new -$$Lambda$ChatUsersActivity$UQlsFnrMUxNXwuVmGiizDyM6Qyc(this));
            }
            i++;
            obj = obj2;
        }
        if (obj == null) {
            TL_channelParticipantCreator tL_channelParticipantCreator2 = new TL_channelParticipantCreator();
            int i3 = user.id;
            tL_channelParticipantCreator2.user_id = i3;
            this.participantsMap.put(i3, tL_channelParticipantCreator2);
            this.participants.add(tL_channelParticipantCreator2);
            Collections.sort(this.participants, new -$$Lambda$ChatUsersActivity$ajQEaBYtsO8tKPwQAoVMLub4eKs(this));
            updateRows();
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(user);
        }
    }

    public /* synthetic */ int lambda$onOwnerChaged$3$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    public /* synthetic */ int lambda$onOwnerChaged$4$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    private void openRightsEdit2(final int i, final int i2, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str, boolean z, int i3, boolean z2) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, tL_chatAdminRights, this.defaultBannedRights, tL_chatBannedRights, str, i3, true, false);
        int i4 = i2;
        final int i5 = i3;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str) {
                int i2 = i5;
                if (i2 == 0) {
                    for (int i3 = 0; i3 < ChatUsersActivity.this.participants.size(); i3++) {
                        TLObject tLObject = (TLObject) ChatUsersActivity.this.participants.get(i3);
                        if (tLObject instanceof ChannelParticipant) {
                            if (((ChannelParticipant) tLObject).user_id == i) {
                                ChannelParticipant tL_channelParticipantAdmin;
                                if (i == 1) {
                                    tL_channelParticipantAdmin = new TL_channelParticipantAdmin();
                                } else {
                                    tL_channelParticipantAdmin = new TL_channelParticipant();
                                }
                                tL_channelParticipantAdmin.admin_rights = tL_chatAdminRights;
                                tL_channelParticipantAdmin.banned_rights = tL_chatBannedRights;
                                tL_channelParticipantAdmin.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                                tL_channelParticipantAdmin.user_id = i;
                                tL_channelParticipantAdmin.date = i2;
                                tL_channelParticipantAdmin.flags |= 4;
                                tL_channelParticipantAdmin.rank = str;
                                ChatUsersActivity.this.participants.set(i3, tL_channelParticipantAdmin);
                                return;
                            }
                        } else if (tLObject instanceof ChatParticipant) {
                            ChatParticipant tL_chatParticipantAdmin;
                            ChatParticipant chatParticipant = (ChatParticipant) tLObject;
                            if (i == 1) {
                                tL_chatParticipantAdmin = new TL_chatParticipantAdmin();
                            } else {
                                tL_chatParticipantAdmin = new TL_chatParticipant();
                            }
                            tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                            tL_chatParticipantAdmin.date = chatParticipant.date;
                            tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                            int indexOf = ChatUsersActivity.this.info.participants.participants.indexOf(chatParticipant);
                            if (indexOf >= 0) {
                                ChatUsersActivity.this.info.participants.participants.set(indexOf, tL_chatParticipantAdmin);
                            }
                            ChatUsersActivity.this.loadChatParticipants(0, 200);
                        }
                    }
                } else if (i2 == 1 && i == 0) {
                    ChatUsersActivity.this.removeParticipants(i);
                }
            }

            public void didChangeOwner(User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    private void openRightsEdit(int i, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str, boolean z, int i2, boolean z2) {
        final TLObject tLObject2 = tLObject;
        final boolean z3 = z2;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, tL_chatAdminRights, this.defaultBannedRights, tL_chatBannedRights, str, i2, z, tLObject2 == null);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str) {
                TLObject tLObject = tLObject2;
                if (tLObject instanceof ChannelParticipant) {
                    ChannelParticipant channelParticipant = (ChannelParticipant) tLObject;
                    channelParticipant.admin_rights = tL_chatAdminRights;
                    channelParticipant.banned_rights = tL_chatBannedRights;
                    channelParticipant.rank = str;
                }
                if (z3) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }

            public void didChangeOwner(User user) {
                ChatUsersActivity.this.onOwnerChaged(user);
            }
        });
        presentFragment(chatRightsEditActivity, z3);
    }

    private void removeUser(int i) {
        if (ChatObject.isChannel(this.currentChat)) {
            getMessagesController().deleteUserFromChat(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), null);
            finishFragment();
        }
    }

    private TLObject getAnyParticipant(int i) {
        for (int i2 = 0; i2 < 3; i2++) {
            SparseArray sparseArray;
            if (i2 == 0) {
                sparseArray = this.contactsMap;
            } else if (i2 == 1) {
                sparseArray = this.botsMap;
            } else {
                sparseArray = this.participantsMap;
            }
            TLObject tLObject = (TLObject) sparseArray.get(i);
            if (tLObject != null) {
                return tLObject;
            }
        }
        return null;
    }

    private void removeParticipants(TLObject tLObject) {
        if (tLObject instanceof ChatParticipant) {
            removeParticipants(((ChatParticipant) tLObject).user_id);
        } else if (tLObject instanceof ChannelParticipant) {
            removeParticipants(((ChannelParticipant) tLObject).user_id);
        }
    }

    private void removeParticipants(int i) {
        Object obj = null;
        for (int i2 = 0; i2 < 3; i2++) {
            SparseArray sparseArray;
            ArrayList arrayList;
            if (i2 == 0) {
                sparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i2 == 1) {
                sparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                sparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = (TLObject) sparseArray.get(i);
            if (tLObject != null) {
                sparseArray.remove(i);
                arrayList.remove(tLObject);
                obj = 1;
            }
        }
        if (obj != null) {
            updateRows();
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    private void updateParticipantWithRights(ChannelParticipant channelParticipant, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, int i, boolean z) {
        Object obj = null;
        for (int i2 = 0; i2 < 3; i2++) {
            SparseArray sparseArray;
            if (i2 == 0) {
                sparseArray = this.contactsMap;
            } else if (i2 == 1) {
                sparseArray = this.botsMap;
            } else {
                sparseArray = this.participantsMap;
            }
            TLObject tLObject = (TLObject) sparseArray.get(channelParticipant.user_id);
            if (tLObject instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) tLObject;
                channelParticipant.admin_rights = tL_chatAdminRights;
                channelParticipant.banned_rights = tL_chatBannedRights;
                if (z) {
                    channelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (z && tLObject != null && obj == null) {
                ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
                if (chatUsersActivityDelegate != null) {
                    chatUsersActivityDelegate.didAddParticipantToList(i, tLObject);
                    obj = 1;
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:121:0x02ae  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02ad A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02ad A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02ae  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02ae  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02ad A:{RETURN} */
    private boolean createMenuForParticipant(org.telegram.tgnet.TLObject r23, boolean r24) {
        /*
        r22 = this;
        r11 = r22;
        r6 = r23;
        if (r6 == 0) goto L_0x02e5;
    L_0x0006:
        r1 = r11.selectType;
        if (r1 == 0) goto L_0x000c;
    L_0x000a:
        goto L_0x02e5;
    L_0x000c:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant;
        if (r1 == 0) goto L_0x0029;
    L_0x0010:
        r1 = r6;
        r1 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r1;
        r3 = r1.user_id;
        r4 = r1.can_edit;
        r5 = r1.banned_rights;
        r7 = r1.admin_rights;
        r8 = r1.date;
        r1 = r1.rank;
        r10 = r1;
        r1 = r4;
        r9 = r5;
        r4 = r3;
        r21 = r8;
        r8 = r7;
        r7 = r21;
        goto L_0x0049;
    L_0x0029:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.ChatParticipant;
        if (r1 == 0) goto L_0x0043;
    L_0x002d:
        r1 = r6;
        r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1;
        r3 = r1.user_id;
        r1 = r1.date;
        r4 = r11.currentChat;
        r4 = org.telegram.messenger.ChatObject.canAddAdmins(r4);
        r5 = "";
        r7 = r1;
        r1 = r4;
        r10 = r5;
        r8 = 0;
        r9 = 0;
        r4 = r3;
        goto L_0x0049;
    L_0x0043:
        r1 = 0;
        r4 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
    L_0x0049:
        if (r4 == 0) goto L_0x02e3;
    L_0x004b:
        r3 = r22.getUserConfig();
        r3 = r3.getClientUserId();
        if (r4 != r3) goto L_0x0057;
    L_0x0055:
        goto L_0x02e3;
    L_0x0057:
        r3 = r11.type;
        r12 = "EditAdminRights";
        r13 = "dialogRedIcon";
        r14 = "dialogTextRed2";
        r2 = 2;
        r15 = 1;
        if (r3 != r2) goto L_0x01ce;
    L_0x0063:
        r3 = r22.getMessagesController();
        r2 = java.lang.Integer.valueOf(r4);
        r3 = r3.getUser(r2);
        r2 = r11.currentChat;
        r2 = org.telegram.messenger.ChatObject.canAddAdmins(r2);
        if (r2 == 0) goto L_0x0087;
    L_0x0077:
        r2 = r6 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipant;
        if (r2 != 0) goto L_0x0085;
    L_0x007b:
        r2 = r6 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantBanned;
        if (r2 != 0) goto L_0x0085;
    L_0x007f:
        r2 = r6 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipant;
        if (r2 != 0) goto L_0x0085;
    L_0x0083:
        if (r1 == 0) goto L_0x0087;
    L_0x0085:
        r2 = 1;
        goto L_0x0088;
    L_0x0087:
        r2 = 0;
    L_0x0088:
        r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
        if (r0 != 0) goto L_0x0098;
    L_0x008c:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r5 != 0) goto L_0x0098;
    L_0x0090:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
        if (r5 != 0) goto L_0x0098;
    L_0x0094:
        r5 = r6 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
        if (r5 == 0) goto L_0x009a;
    L_0x0098:
        if (r1 == 0) goto L_0x009c;
    L_0x009a:
        r5 = 1;
        goto L_0x009d;
    L_0x009c:
        r5 = 0;
    L_0x009d:
        if (r0 != 0) goto L_0x00a6;
    L_0x009f:
        r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
        if (r0 == 0) goto L_0x00a4;
    L_0x00a3:
        goto L_0x00a6;
    L_0x00a4:
        r0 = 0;
        goto L_0x00a7;
    L_0x00a6:
        r0 = 1;
    L_0x00a7:
        if (r24 != 0) goto L_0x00bd;
    L_0x00a9:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r16 = new java.util.ArrayList;
        r16.<init>();
        r18 = new java.util.ArrayList;
        r18.<init>();
        r20 = r16;
        r19 = r18;
        goto L_0x00c2;
    L_0x00bd:
        r1 = 0;
        r19 = 0;
        r20 = 0;
    L_0x00c2:
        if (r2 == 0) goto L_0x00f4;
    L_0x00c4:
        if (r24 == 0) goto L_0x00c7;
    L_0x00c6:
        return r15;
    L_0x00c7:
        if (r0 == 0) goto L_0x00d1;
    L_0x00c9:
        r0 = NUM; // 0x7f0e03bf float:1.8876983E38 double:1.0531626304E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r12, r0);
        goto L_0x00da;
    L_0x00d1:
        r0 = NUM; // 0x7f0e09b0 float:1.8880068E38 double:1.053163382E-314;
        r2 = "SetAsAdmin";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
    L_0x00da:
        r1.add(r0);
        r0 = NUM; // 0x7var_e float:1.7944704E38 double:1.0529355337E-314;
        r0 = java.lang.Integer.valueOf(r0);
        r2 = r19;
        r2.add(r0);
        r0 = 0;
        r12 = java.lang.Integer.valueOf(r0);
        r0 = r20;
        r0.add(r12);
        goto L_0x00f8;
    L_0x00f4:
        r2 = r19;
        r0 = r20;
    L_0x00f8:
        r12 = r11.currentChat;
        r12 = org.telegram.messenger.ChatObject.canBlockUsers(r12);
        if (r12 == 0) goto L_0x0171;
    L_0x0100:
        if (r5 == 0) goto L_0x0171;
    L_0x0102:
        if (r24 == 0) goto L_0x0105;
    L_0x0104:
        return r15;
    L_0x0105:
        r12 = r11.isChannel;
        if (r12 != 0) goto L_0x014e;
    L_0x0109:
        r12 = r11.currentChat;
        r12 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r12 == 0) goto L_0x012f;
    L_0x0111:
        r12 = NUM; // 0x7f0e020e float:1.8876104E38 double:1.0531624165E-314;
        r15 = "ChangePermissions";
        r12 = org.telegram.messenger.LocaleController.getString(r15, r12);
        r1.add(r12);
        r12 = NUM; // 0x7var_ float:1.7944708E38 double:1.0529355346E-314;
        r12 = java.lang.Integer.valueOf(r12);
        r2.add(r12);
        r12 = 1;
        r15 = java.lang.Integer.valueOf(r12);
        r0.add(r15);
    L_0x012f:
        r12 = NUM; // 0x7f0e0567 float:1.8877843E38 double:1.05316284E-314;
        r15 = "KickFromGroup";
        r12 = org.telegram.messenger.LocaleController.getString(r15, r12);
        r1.add(r12);
        r12 = NUM; // 0x7var_ float:1.794471E38 double:1.052935535E-314;
        r12 = java.lang.Integer.valueOf(r12);
        r2.add(r12);
        r15 = 2;
        r12 = java.lang.Integer.valueOf(r15);
        r0.add(r12);
        goto L_0x016f;
    L_0x014e:
        r12 = NUM; // 0x7var_ float:1.794471E38 double:1.052935535E-314;
        r15 = NUM; // 0x7f0e0263 float:1.8876277E38 double:1.0531624585E-314;
        r12 = "ChannelRemoveUser";
        r12 = org.telegram.messenger.LocaleController.getString(r12, r15);
        r1.add(r12);
        r12 = NUM; // 0x7var_ float:1.794471E38 double:1.052935535E-314;
        r12 = java.lang.Integer.valueOf(r12);
        r2.add(r12);
        r12 = 2;
        r12 = java.lang.Integer.valueOf(r12);
        r0.add(r12);
    L_0x016f:
        r12 = 1;
        goto L_0x0172;
    L_0x0171:
        r12 = 0;
    L_0x0172:
        if (r0 == 0) goto L_0x01cc;
    L_0x0174:
        r15 = r0.isEmpty();
        if (r15 == 0) goto L_0x017b;
    L_0x017a:
        goto L_0x01cc;
    L_0x017b:
        r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r6 = r22.getParentActivity();
        r15.<init>(r6);
        r6 = r0.size();
        r6 = new java.lang.CharSequence[r6];
        r6 = r1.toArray(r6);
        r6 = (java.lang.CharSequence[]) r6;
        r2 = org.telegram.messenger.AndroidUtilities.toIntArray(r2);
        r19 = r13;
        r13 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$YZqfKsthwpHL5NIaJ1wlFL2xzKY;
        r16 = r0;
        r0 = r13;
        r17 = r1;
        r1 = r22;
        r20 = r14;
        r14 = r2;
        r2 = r16;
        r24 = r12;
        r12 = r6;
        r6 = r23;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
        r15.setItems(r12, r14, r13);
        r0 = r15.create();
        r11.showDialog(r0);
        if (r24 == 0) goto L_0x01c9;
    L_0x01b8:
        r1 = r17.size();
        r2 = 1;
        r1 = r1 - r2;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r20);
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r19);
        r0.setItemColor(r1, r2, r3);
    L_0x01c9:
        r2 = 1;
        goto L_0x02e2;
    L_0x01cc:
        r0 = 0;
        return r0;
    L_0x01ce:
        r19 = r13;
        r20 = r14;
        r0 = 3;
        r2 = NUM; // 0x7f0e0232 float:1.8876177E38 double:1.0531624343E-314;
        r5 = "ChannelDeleteFromList";
        if (r3 != r0) goto L_0x0208;
    L_0x01da:
        r0 = r11.currentChat;
        r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0);
        if (r0 == 0) goto L_0x0208;
    L_0x01e2:
        if (r24 == 0) goto L_0x01e6;
    L_0x01e4:
        r0 = 1;
        return r0;
    L_0x01e6:
        r0 = 1;
        r1 = 2;
        r3 = new java.lang.CharSequence[r1];
        r6 = NUM; // 0x7f0e0238 float:1.887619E38 double:1.0531624373E-314;
        r7 = "ChannelEditPermissions";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r7 = 0;
        r3[r7] = r6;
        r2 = org.telegram.messenger.LocaleController.getString(r5, r2);
        r3[r0] = r2;
        r2 = new int[r1];
        r2 = {NUM, NUM};
        r6 = r23;
        r13 = r2;
        r12 = r3;
    L_0x0205:
        r5 = 0;
        goto L_0x02ab;
    L_0x0208:
        r0 = r11.type;
        if (r0 != 0) goto L_0x024d;
    L_0x020c:
        r0 = r11.currentChat;
        r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0);
        if (r0 == 0) goto L_0x024d;
    L_0x0214:
        if (r24 == 0) goto L_0x0218;
    L_0x0216:
        r0 = 1;
        return r0;
    L_0x0218:
        r0 = 2;
        r1 = new java.lang.CharSequence[r0];
        r0 = r11.currentChat;
        r0 = org.telegram.messenger.ChatObject.canAddUsers(r0);
        if (r0 == 0) goto L_0x0237;
    L_0x0223:
        r0 = r11.isChannel;
        if (r0 == 0) goto L_0x022d;
    L_0x0227:
        r0 = NUM; // 0x7f0e021a float:1.8876129E38 double:1.0531624224E-314;
        r3 = "ChannelAddToChannel";
        goto L_0x0232;
    L_0x022d:
        r0 = NUM; // 0x7f0e021b float:1.887613E38 double:1.053162423E-314;
        r3 = "ChannelAddToGroup";
    L_0x0232:
        r0 = org.telegram.messenger.LocaleController.getString(r3, r0);
        goto L_0x0238;
    L_0x0237:
        r0 = 0;
    L_0x0238:
        r3 = 0;
        r1[r3] = r0;
        r0 = org.telegram.messenger.LocaleController.getString(r5, r2);
        r2 = 1;
        r1[r2] = r0;
        r0 = 2;
        r0 = new int[r0];
        r0 = {NUM, NUM};
        r6 = r23;
        r13 = r0;
        r12 = r1;
        goto L_0x0205;
    L_0x024d:
        r2 = 1;
        r0 = r11.type;
        if (r0 != r2) goto L_0x02a6;
    L_0x0252:
        r0 = r11.currentChat;
        r0 = org.telegram.messenger.ChatObject.canAddAdmins(r0);
        if (r0 == 0) goto L_0x02a6;
    L_0x025a:
        if (r1 == 0) goto L_0x02a6;
    L_0x025c:
        if (r24 == 0) goto L_0x025f;
    L_0x025e:
        return r2;
    L_0x025f:
        r0 = r11.currentChat;
        r0 = r0.creator;
        r2 = NUM; // 0x7f0e0264 float:1.8876279E38 double:1.053162459E-314;
        r3 = "ChannelRemoveUserAdmin";
        r6 = r23;
        if (r0 != 0) goto L_0x028a;
    L_0x026c:
        r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r0 != 0) goto L_0x0276;
    L_0x0270:
        if (r1 == 0) goto L_0x0276;
    L_0x0272:
        r0 = 1;
        r1 = 2;
        r5 = 0;
        goto L_0x028d;
    L_0x0276:
        r0 = 1;
        r1 = new java.lang.CharSequence[r0];
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r5 = 0;
        r1[r5] = r2;
        r2 = new int[r0];
        r3 = NUM; // 0x7var_ float:1.794471E38 double:1.052935535E-314;
        r2[r5] = r3;
        r12 = r1;
        r13 = r2;
        goto L_0x02ab;
    L_0x028a:
        r0 = 1;
        r5 = 0;
        r1 = 2;
    L_0x028d:
        r7 = new java.lang.CharSequence[r1];
        r13 = NUM; // 0x7f0e03bf float:1.8876983E38 double:1.0531626304E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r12, r13);
        r7[r5] = r12;
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r7[r0] = r2;
        r2 = new int[r1];
        r2 = {NUM, NUM};
        r13 = r2;
        r12 = r7;
        goto L_0x02ab;
    L_0x02a6:
        r6 = r23;
        r5 = 0;
        r12 = 0;
        r13 = 0;
    L_0x02ab:
        if (r12 != 0) goto L_0x02ae;
    L_0x02ad:
        return r5;
    L_0x02ae:
        r14 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0 = r22.getParentActivity();
        r14.<init>(r0);
        r15 = new org.telegram.ui.-$$Lambda$ChatUsersActivity$Zq3W07Usva2ue5xRNvv7Md_sKfM;
        r0 = r15;
        r1 = r22;
        r2 = r12;
        r3 = r4;
        r4 = r8;
        r5 = r10;
        r6 = r23;
        r7 = r9;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        r14.setItems(r12, r13, r15);
        r0 = r14.create();
        r11.showDialog(r0);
        r1 = r11.type;
        r2 = 1;
        if (r1 != r2) goto L_0x02e2;
    L_0x02d5:
        r1 = r12.length;
        r1 = r1 - r2;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r20);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r19);
        r0.setItemColor(r1, r3, r4);
    L_0x02e2:
        return r2;
    L_0x02e3:
        r0 = 0;
        return r0;
    L_0x02e5:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createMenuForParticipant(org.telegram.tgnet.TLObject, boolean):boolean");
    }

    public /* synthetic */ void lambda$createMenuForParticipant$6$ChatUsersActivity(ArrayList arrayList, User user, int i, boolean z, TLObject tLObject, int i2, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str, DialogInterface dialogInterface, int i3) {
        ArrayList arrayList2 = arrayList;
        User user2 = user;
        TLObject tLObject2 = tLObject;
        int i4 = i3;
        if (((Integer) arrayList2.get(i4)).intValue() == 2) {
            getMessagesController().deleteUserFromChat(this.chatId, user2, null);
            removeParticipants(i);
            if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
                return;
            }
            return;
        }
        int i5 = i;
        if (((Integer) arrayList2.get(i4)).intValue() == 1 && z && ((tLObject2 instanceof TL_channelParticipantAdmin) || (tLObject2 instanceof TL_chatParticipantAdmin))) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, ContactsController.formatName(user2.first_name, user2.last_name)));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ChatUsersActivity$Eu-obL29OUnjUXgxBF5NZEiw6cM(this, i, i2, tLObject, tL_chatAdminRights, tL_chatBannedRights, str, z, arrayList, i3));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
            return;
        }
        openRightsEdit2(i, i2, tLObject, tL_chatAdminRights, tL_chatBannedRights, str, z, ((Integer) arrayList2.get(i4)).intValue(), false);
    }

    public /* synthetic */ void lambda$null$5$ChatUsersActivity(int i, int i2, TLObject tLObject, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i3, DialogInterface dialogInterface, int i4) {
        openRightsEdit2(i, i2, tLObject, tL_chatAdminRights, tL_chatBannedRights, str, z, ((Integer) arrayList.get(i3)).intValue(), false);
    }

    public /* synthetic */ void lambda$createMenuForParticipant$9$ChatUsersActivity(CharSequence[] charSequenceArr, int i, TL_chatAdminRights tL_chatAdminRights, String str, TLObject tLObject, TL_chatBannedRights tL_chatBannedRights, DialogInterface dialogInterface, int i2) {
        int i3 = i;
        final TLObject tLObject2 = tLObject;
        int i4 = i2;
        int i5 = this.type;
        ChatRightsEditActivity chatRightsEditActivity;
        if (i5 == 1) {
            if (i4 == 0 && charSequenceArr.length == 2) {
                chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, tL_chatAdminRights, null, null, str, 0, true, false);
                chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
                    public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str) {
                        TLObject tLObject = tLObject2;
                        if (tLObject instanceof ChannelParticipant) {
                            ChannelParticipant channelParticipant = (ChannelParticipant) tLObject;
                            channelParticipant.admin_rights = tL_chatAdminRights;
                            channelParticipant.banned_rights = tL_chatBannedRights;
                            channelParticipant.rank = str;
                            ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, tL_chatAdminRights, tL_chatBannedRights, 0, false);
                        }
                    }

                    public void didChangeOwner(User user) {
                        ChatUsersActivity.this.onOwnerChaged(user);
                    }
                });
                presentFragment(chatRightsEditActivity);
                return;
            }
            getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), new TL_chatAdminRights(), "", 1 ^ this.isChannel, this, false);
            removeParticipants(i3);
        } else if (i5 == 0 || i5 == 3) {
            int i6;
            TLObject tLObject3;
            int i7;
            if (i4 == 0) {
                i5 = this.type;
                if (i5 == 3) {
                    chatRightsEditActivity = new ChatRightsEditActivity(i, this.chatId, null, this.defaultBannedRights, tL_chatBannedRights, str, 1, true, false);
                    chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
                        public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str) {
                            TLObject tLObject = tLObject2;
                            if (tLObject instanceof ChannelParticipant) {
                                ChannelParticipant channelParticipant = (ChannelParticipant) tLObject;
                                channelParticipant.admin_rights = tL_chatAdminRights;
                                channelParticipant.banned_rights = tL_chatBannedRights;
                                channelParticipant.rank = str;
                                ChatUsersActivity.this.updateParticipantWithRights(channelParticipant, tL_chatAdminRights, tL_chatBannedRights, 0, false);
                            }
                        }

                        public void didChangeOwner(User user) {
                            ChatUsersActivity.this.onOwnerChaged(user);
                        }
                    });
                    presentFragment(chatRightsEditActivity);
                } else if (i5 == 0) {
                    i7 = 1;
                    i6 = i4;
                    tLObject3 = tLObject2;
                    getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), null, 0, null, this, null);
                }
                i6 = i4;
                tLObject3 = tLObject2;
                i7 = 1;
            } else {
                i6 = i4;
                tLObject3 = tLObject2;
                i7 = 1;
                if (i6 == 1) {
                    TL_channels_editBanned tL_channels_editBanned = new TL_channels_editBanned();
                    tL_channels_editBanned.user_id = getMessagesController().getInputUser(i3);
                    tL_channels_editBanned.channel = getMessagesController().getInputChannel(this.chatId);
                    tL_channels_editBanned.banned_rights = new TL_chatBannedRights();
                    getConnectionsManager().sendRequest(tL_channels_editBanned, new -$$Lambda$ChatUsersActivity$mRT9FqMMakDCsFrq7p_gF6dp2MQ(this));
                    if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                        this.actionBar.closeSearchField();
                    }
                }
            }
            if ((i6 == 0 && this.type == 0) || i6 == i7) {
                removeParticipants(tLObject3);
            }
        } else if (i4 == 0) {
            getMessagesController().deleteUserFromChat(this.chatId, getMessagesController().getUser(Integer.valueOf(i)), null);
        }
    }

    public /* synthetic */ void lambda$null$8$ChatUsersActivity(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            Updates updates = (Updates) tLObject;
            getMessagesController().processUpdates(updates, false);
            if (!updates.chats.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ChatUsersActivity$lWuf0Sb3mmUn0FDHcm3jEVQEJ3I(this, updates), 1000);
            }
        }
    }

    public /* synthetic */ void lambda$null$7$ChatUsersActivity(Updates updates) {
        getMessagesController().loadFullChat(((Chat) updates.chats.get(0)).id, 0, true);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            i = 0;
            ChatFull chatFull = (ChatFull) objArr[0];
            boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
            if (chatFull.id != this.chatId) {
                return;
            }
            if (!booleanValue || !ChatObject.isChannel(this.currentChat)) {
                if (this.info != null) {
                    i = 1;
                }
                this.info = chatFull;
                if (i == 0) {
                    i = getCurrentSlowmode();
                    this.initialSlowmode = i;
                    this.selectedSlowmode = i;
                }
                AndroidUtilities.runOnUIThread(new -$$Lambda$ChatUsersActivity$BJJ9Ae4L5rNg4Wh3TY98-WthCtw(this));
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$10$ChatUsersActivity() {
        loadChatParticipants(0, 200);
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    private int getCurrentSlowmode() {
        ChatFull chatFull = this.info;
        if (chatFull != null) {
            int i = chatFull.slowmode_seconds;
            if (i == 10) {
                return 1;
            }
            if (i == 30) {
                return 2;
            }
            if (i == 60) {
                return 3;
            }
            if (i == 300) {
                return 4;
            }
            if (i == 900) {
                return 5;
            }
            if (i == 3600) {
                return 6;
            }
        }
        return 0;
    }

    private boolean checkDiscard() {
        if (ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights) && this.initialSlowmode == this.selectedSlowmode) {
            return true;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", NUM));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", NUM));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new -$$Lambda$ChatUsersActivity$zW-AZ1mNJIqq7Z5DBDTuLLFRhYg(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new -$$Lambda$ChatUsersActivity$41UydPei1hMcnIv9HWa0EFE9AXg(this));
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$11$ChatUsersActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$12$ChatUsersActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    private String formatUserPermissions(TL_chatBannedRights tL_chatBannedRights) {
        if (tL_chatBannedRights == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean z = tL_chatBannedRights.view_messages;
        if (z && this.defaultBannedRights.view_messages != z) {
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoRead", NUM));
        }
        z = tL_chatBannedRights.send_messages;
        String str = ", ";
        if (z && this.defaultBannedRights.send_messages != z) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoSend", NUM));
        }
        z = tL_chatBannedRights.send_media;
        if (z && this.defaultBannedRights.send_media != z) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoSendMedia", NUM));
        }
        z = tL_chatBannedRights.send_stickers;
        if (z && this.defaultBannedRights.send_stickers != z) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoSendStickers", NUM));
        }
        z = tL_chatBannedRights.send_polls;
        if (z && this.defaultBannedRights.send_polls != z) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoSendPolls", NUM));
        }
        z = tL_chatBannedRights.embed_links;
        if (z && this.defaultBannedRights.embed_links != z) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoEmbedLinks", NUM));
        }
        z = tL_chatBannedRights.invite_users;
        if (z && this.defaultBannedRights.invite_users != z) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoInviteUsers", NUM));
        }
        z = tL_chatBannedRights.pin_messages;
        if (z && this.defaultBannedRights.pin_messages != z) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoPinMessages", NUM));
        }
        boolean z2 = tL_chatBannedRights.change_info;
        if (z2 && this.defaultBannedRights.change_info != z2) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(str);
            }
            stringBuilder.append(LocaleController.getString("UserRestrictionsNoChangeInfo", NUM));
        }
        if (stringBuilder.length() != 0) {
            stringBuilder.replace(0, 1, stringBuilder.substring(0, 1).toUpperCase());
            stringBuilder.append('.');
        }
        return stringBuilder.toString();
    }

    private void processDone() {
        if (this.type == 3) {
            if (ChatObject.isChannel(this.currentChat) || this.selectedSlowmode == this.initialSlowmode || this.info == null) {
                if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
                    getMessagesController().setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
                    Chat chat = getMessagesController().getChat(Integer.valueOf(this.chatId));
                    if (chat != null) {
                        chat.default_banned_rights = this.defaultBannedRights;
                    }
                }
                if (!(this.selectedSlowmode == this.initialSlowmode || this.info == null)) {
                    getMessagesController().setChannelSlowMode(this.chatId, this.info.slowmode_seconds);
                }
                finishFragment();
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new -$$Lambda$ChatUsersActivity$sApfWcl-xWiGbnMY6rDDX--X0ic(this));
        }
    }

    public /* synthetic */ void lambda$processDone$13$ChatUsersActivity(int i) {
        this.chatId = i;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        processDone();
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (this.info != null) {
            int currentSlowmode = getCurrentSlowmode();
            this.initialSlowmode = currentSlowmode;
            this.selectedSlowmode = currentSlowmode;
        }
    }

    private int getChannelAdminParticipantType(TLObject tLObject) {
        if ((tLObject instanceof TL_channelParticipantCreator) || (tLObject instanceof TL_channelParticipantSelf)) {
            return 0;
        }
        return ((tLObject instanceof TL_channelParticipantAdmin) || (tLObject instanceof TL_channelParticipant)) ? 1 : 2;
    }

    private void loadChatParticipants(int i, int i2) {
        if (!this.loadingUsers) {
            this.contactsEndReached = false;
            this.botsEndReached = false;
            loadChatParticipants(i, i2, true);
        }
    }

    private void loadChatParticipants(int i, int i2, boolean z) {
        int i3 = 0;
        if (ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (!(emptyTextProgressView == null || this.firstLoaded)) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
            tL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
            int i4 = this.type;
            if (i4 == 0) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsKicked();
            } else if (i4 == 1) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
            } else if (i4 == 2) {
                ChatFull chatFull = this.info;
                if (chatFull != null && chatFull.participants_count <= 200) {
                    Chat chat = this.currentChat;
                    if (chat != null && chat.megagroup) {
                        tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
                    }
                }
                if (this.selectType == 1) {
                    if (this.contactsEndReached) {
                        tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
                    } else {
                        this.delayResults = 2;
                        tL_channels_getParticipants.filter = new TL_channelParticipantsContacts();
                        this.contactsEndReached = true;
                        loadChatParticipants(0, 200, false);
                    }
                } else if (!this.contactsEndReached) {
                    this.delayResults = 3;
                    tL_channels_getParticipants.filter = new TL_channelParticipantsContacts();
                    this.contactsEndReached = true;
                    loadChatParticipants(0, 200, false);
                } else if (this.botsEndReached) {
                    tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
                } else {
                    tL_channels_getParticipants.filter = new TL_channelParticipantsBots();
                    this.botsEndReached = true;
                    loadChatParticipants(0, 200, false);
                }
            } else if (i4 == 3) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsBanned();
            }
            tL_channels_getParticipants.filter.q = "";
            tL_channels_getParticipants.offset = i;
            tL_channels_getParticipants.limit = i2;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_channels_getParticipants, new -$$Lambda$ChatUsersActivity$EXNxki8FMf8JC4fIJSBE168hCOQ(this, tL_channels_getParticipants)), this.classGuid);
            return;
        }
        this.loadingUsers = false;
        this.participants.clear();
        this.bots.clear();
        this.contacts.clear();
        this.participantsMap.clear();
        this.contactsMap.clear();
        this.botsMap.clear();
        i = this.type;
        if (i == 1) {
            ChatFull chatFull2 = this.info;
            if (chatFull2 != null) {
                i = chatFull2.participants.participants.size();
                while (i3 < i) {
                    ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(i3);
                    if ((chatParticipant instanceof TL_chatParticipantCreator) || (chatParticipant instanceof TL_chatParticipantAdmin)) {
                        this.participants.add(chatParticipant);
                    }
                    this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                    i3++;
                }
            }
        } else if (i == 2 && this.info != null) {
            i = getUserConfig().clientUserId;
            i2 = this.info.participants.participants.size();
            while (i3 < i2) {
                ChatParticipant chatParticipant2 = (ChatParticipant) this.info.participants.participants.get(i3);
                if (this.selectType == 0 || chatParticipant2.user_id != i) {
                    if (this.selectType == 1) {
                        if (getContactsController().isContact(chatParticipant2.user_id)) {
                            this.contacts.add(chatParticipant2);
                            this.contactsMap.put(chatParticipant2.user_id, chatParticipant2);
                        } else {
                            this.participants.add(chatParticipant2);
                            this.participantsMap.put(chatParticipant2.user_id, chatParticipant2);
                        }
                    } else if (getContactsController().isContact(chatParticipant2.user_id)) {
                        this.contacts.add(chatParticipant2);
                        this.contactsMap.put(chatParticipant2.user_id, chatParticipant2);
                    } else {
                        User user = getMessagesController().getUser(Integer.valueOf(chatParticipant2.user_id));
                        if (user == null || !user.bot) {
                            this.participants.add(chatParticipant2);
                            this.participantsMap.put(chatParticipant2.user_id, chatParticipant2);
                        } else {
                            this.bots.add(chatParticipant2);
                            this.botsMap.put(chatParticipant2.user_id, chatParticipant2);
                        }
                    }
                }
                i3++;
            }
        }
        ListAdapter listAdapter2 = this.listViewAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        updateRows();
        listAdapter2 = this.listViewAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$loadChatParticipants$17$ChatUsersActivity(TL_channels_getParticipants tL_channels_getParticipants, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatUsersActivity$U2a642fHVniMc_2qJcp8WAu1rnM(this, tL_error, tLObject, tL_channels_getParticipants));
    }

    public /* synthetic */ void lambda$null$16$ChatUsersActivity(TL_error tL_error, TLObject tLObject, TL_channels_getParticipants tL_channels_getParticipants) {
        if (tL_error == null) {
            int i;
            ArrayList arrayList;
            SparseArray sparseArray;
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            if (this.type == 1) {
                getMessagesController().processLoadedAdminsResponse(this.chatId, tL_channels_channelParticipants);
            }
            getMessagesController().putUsers(tL_channels_channelParticipants.users, false);
            int clientUserId = getUserConfig().getClientUserId();
            if (this.selectType != 0) {
                for (i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                    if (((ChannelParticipant) tL_channels_channelParticipants.participants.get(i)).user_id == clientUserId) {
                        tL_channels_channelParticipants.participants.remove(i);
                        break;
                    }
                }
            }
            EmptyTextProgressView emptyTextProgressView;
            if (this.type == 2) {
                this.delayResults--;
                ChannelParticipantsFilter channelParticipantsFilter = tL_channels_getParticipants.filter;
                if (channelParticipantsFilter instanceof TL_channelParticipantsContacts) {
                    arrayList = this.contacts;
                    sparseArray = this.contactsMap;
                } else if (channelParticipantsFilter instanceof TL_channelParticipantsBots) {
                    arrayList = this.bots;
                    sparseArray = this.botsMap;
                } else {
                    arrayList = this.participants;
                    sparseArray = this.participantsMap;
                }
                if (this.delayResults <= 0) {
                    emptyTextProgressView = this.emptyView;
                    if (emptyTextProgressView != null) {
                        emptyTextProgressView.showTextView();
                    }
                }
            } else {
                arrayList = this.participants;
                sparseArray = this.participantsMap;
                sparseArray.clear();
                emptyTextProgressView = this.emptyView;
                if (emptyTextProgressView != null) {
                    emptyTextProgressView.showTextView();
                }
            }
            arrayList.clear();
            arrayList.addAll(tL_channels_channelParticipants.participants);
            i = tL_channels_channelParticipants.participants.size();
            for (int i2 = 0; i2 < i; i2++) {
                ChannelParticipant channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i2);
                sparseArray.put(channelParticipant.user_id, channelParticipant);
            }
            if (this.type == 2) {
                int size = this.participants.size();
                int i3 = 0;
                while (i3 < size) {
                    ChannelParticipant channelParticipant2 = (ChannelParticipant) this.participants.get(i3);
                    if (this.contactsMap.get(channelParticipant2.user_id) != null || this.botsMap.get(channelParticipant2.user_id) != null) {
                        this.participants.remove(i3);
                        this.participantsMap.remove(channelParticipant2.user_id);
                        i3--;
                        size--;
                    }
                    i3++;
                }
            }
            try {
                if ((this.type == 0 || this.type == 3 || this.type == 2) && this.currentChat != null && this.currentChat.megagroup && (this.info instanceof TL_channelFull) && this.info.participants_count <= 200) {
                    Collections.sort(arrayList, new -$$Lambda$ChatUsersActivity$eeaKQwJaQznozuSvaqx-50tG3So(this, getConnectionsManager().getCurrentTime()));
                } else if (this.type == 1) {
                    Collections.sort(this.participants, new -$$Lambda$ChatUsersActivity$R5aX77kVYmn6CvNOtsy5LfR8gxQ(this));
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (this.type != 2 || this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
        }
        updateRows();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0049 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0054 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x005f A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0068 A:{SKIP} */
    public /* synthetic */ int lambda$null$14$ChatUsersActivity(int r4, org.telegram.tgnet.TLObject r5, org.telegram.tgnet.TLObject r6) {
        /*
        r3 = this;
        r5 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r5;
        r6 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r6;
        r0 = r3.getMessagesController();
        r5 = r5.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r5 = r0.getUser(r5);
        r0 = r3.getMessagesController();
        r6 = r6.user_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r0.getUser(r6);
        r0 = 50000; // 0xCLASSNAME float:7.0065E-41 double:2.47033E-319;
        r1 = 0;
        if (r5 == 0) goto L_0x0034;
    L_0x0026:
        r2 = r5.status;
        if (r2 == 0) goto L_0x0034;
    L_0x002a:
        r5 = r5.self;
        if (r5 == 0) goto L_0x0031;
    L_0x002e:
        r5 = r4 + r0;
        goto L_0x0035;
    L_0x0031:
        r5 = r2.expires;
        goto L_0x0035;
    L_0x0034:
        r5 = 0;
    L_0x0035:
        if (r6 == 0) goto L_0x0044;
    L_0x0037:
        r2 = r6.status;
        if (r2 == 0) goto L_0x0044;
    L_0x003b:
        r6 = r6.self;
        if (r6 == 0) goto L_0x0041;
    L_0x003f:
        r4 = r4 + r0;
        goto L_0x0045;
    L_0x0041:
        r4 = r2.expires;
        goto L_0x0045;
    L_0x0044:
        r4 = 0;
    L_0x0045:
        r6 = -1;
        r0 = 1;
        if (r5 <= 0) goto L_0x0052;
    L_0x0049:
        if (r4 <= 0) goto L_0x0052;
    L_0x004b:
        if (r5 <= r4) goto L_0x004e;
    L_0x004d:
        return r0;
    L_0x004e:
        if (r5 >= r4) goto L_0x0051;
    L_0x0050:
        return r6;
    L_0x0051:
        return r1;
    L_0x0052:
        if (r5 >= 0) goto L_0x005d;
    L_0x0054:
        if (r4 >= 0) goto L_0x005d;
    L_0x0056:
        if (r5 <= r4) goto L_0x0059;
    L_0x0058:
        return r0;
    L_0x0059:
        if (r5 >= r4) goto L_0x005c;
    L_0x005b:
        return r6;
    L_0x005c:
        return r1;
    L_0x005d:
        if (r5 >= 0) goto L_0x0061;
    L_0x005f:
        if (r4 > 0) goto L_0x0065;
    L_0x0061:
        if (r5 != 0) goto L_0x0066;
    L_0x0063:
        if (r4 == 0) goto L_0x0066;
    L_0x0065:
        return r6;
    L_0x0066:
        if (r4 >= 0) goto L_0x006a;
    L_0x0068:
        if (r5 > 0) goto L_0x006e;
    L_0x006a:
        if (r4 != 0) goto L_0x006f;
    L_0x006c:
        if (r5 == 0) goto L_0x006f;
    L_0x006e:
        return r0;
    L_0x006f:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$null$14$ChatUsersActivity(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLObject):int");
    }

    public /* synthetic */ int lambda$null$15$ChatUsersActivity(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.needOpenSearch) {
            this.searchItem.openSearch(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ChatUsersActivity$_DQU8FkLhj_sFqftd480SdstddQ -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq = new -$$Lambda$ChatUsersActivity$_DQU8FkLhj_sFqftd480SdstddQ(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[43];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, ChooseView.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[9] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[11] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[14] = new ThemeDescription(view2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[16] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell2.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[19] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switch2Track");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, "switch2TrackChecked");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq;
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$ChatUsersActivity$_DQU8FkLhj_sFqftd480SdstddQ -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2 = -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq;
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2, "avatar_backgroundRed");
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2, "avatar_backgroundOrange");
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2, "avatar_backgroundViolet");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2, "avatar_backgroundGreen");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2, "avatar_backgroundCyan");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2, "avatar_backgroundBlue");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatusersactivity__dqu8fklhj_sfqftd480sdstddq2, "avatar_backgroundPink");
        themeDescriptionArr[32] = new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "undo_background");
        themeDescriptionArr[33] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, "undo_cancelColor");
        themeDescriptionArr[34] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, "undo_cancelColor");
        themeDescriptionArr[35] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, "undo_infoColor");
        themeDescriptionArr[36] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, "undo_infoColor");
        themeDescriptionArr[37] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, "undo_infoColor");
        view = this.undoView;
        view2 = view;
        themeDescriptionArr[38] = new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, null, null, null, "undo_infoColor");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[39] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{ManageChatTextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        themeDescriptionArr[40] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[41] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[42] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$18$ChatUsersActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
            }
        }
    }
}
