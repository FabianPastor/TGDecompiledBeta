package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AudioPlayerCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

public class AudioPlayerAlert extends BottomSheet implements NotificationCenterDelegate, FileDownloadProgressListener {
    private int TAG;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private AnimatorSet animatorSet;
    private TextView authorTextView;
    private ChatAvatarContainer avatarContainer;
    private View[] buttons = new View[5];
    private TextView durationTextView;
    private float endTranslation;
    private float fullAnimationProgress;
    private int hasNoCover;
    private boolean hasOptions = true;
    private boolean inFullSize;
    private boolean isInFullMode;
    private int lastTime;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ActionBarMenuItem menuItem;
    private Drawable noCoverDrawable;
    private ActionBarMenuItem optionsButton;
    private Paint paint = new Paint(1);
    private float panelEndTranslation;
    private float panelStartTranslation;
    private LaunchActivity parentActivity;
    private BackupImageView placeholderImageView;
    private ImageView playButton;
    private Drawable[] playOrderButtons = new Drawable[2];
    private FrameLayout playerLayout;
    private ArrayList<MessageObject> playlist;
    private LineProgressView progressView;
    private ImageView repeatButton;
    private int scrollOffsetY = Integer.MAX_VALUE;
    private boolean scrollToSong = true;
    private ActionBarMenuItem searchItem;
    private int searchOpenOffset;
    private int searchOpenPosition = -1;
    private boolean searchWas;
    private boolean searching;
    private SeekBarView seekBarView;
    private View shadow;
    private View shadow2;
    private Drawable shadowDrawable;
    private ActionBarMenuItem shuffleButton;
    private float startTranslation;
    private float thumbMaxScale;
    private int thumbMaxX;
    private int thumbMaxY;
    private SimpleTextView timeTextView;
    private TextView titleTextView;
    private int topBeforeSwitch;

    private class ListAdapter extends SelectionAdapter {
        private Context context;
        private ArrayList<MessageObject> searchResult = new ArrayList();
        private Timer searchTimer;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            if (AudioPlayerAlert.this.searchWas) {
                return this.searchResult.size();
            }
            if (AudioPlayerAlert.this.searching) {
                return AudioPlayerAlert.this.playlist.size();
            }
            return AudioPlayerAlert.this.playlist.size() + 1;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return AudioPlayerAlert.this.searchWas || viewHolder.getAdapterPosition() > 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View audioPlayerCell;
            if (i != 0) {
                audioPlayerCell = new AudioPlayerCell(this.context);
            } else {
                audioPlayerCell = new View(this.context);
                audioPlayerCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(178.0f)));
            }
            return new Holder(audioPlayerCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 1) {
                AudioPlayerCell audioPlayerCell = (AudioPlayerCell) viewHolder.itemView;
                if (AudioPlayerAlert.this.searchWas) {
                    audioPlayerCell.setMessageObject((MessageObject) this.searchResult.get(i));
                } else if (AudioPlayerAlert.this.searching) {
                    if (SharedConfig.playOrderReversed) {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(i));
                    } else {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get((AudioPlayerAlert.this.playlist.size() - i) - 1));
                    }
                } else if (i <= 0) {
                } else {
                    if (SharedConfig.playOrderReversed) {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(i - 1));
                    } else {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(AudioPlayerAlert.this.playlist.size() - i));
                    }
                }
            }
        }

        public int getItemViewType(int i) {
            return (AudioPlayerAlert.this.searchWas || AudioPlayerAlert.this.searching || i != 0) ? 1 : 0;
        }

        public void search(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        ListAdapter.this.searchTimer.cancel();
                        ListAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    ListAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$AudioPlayerAlert$ListAdapter$uVY9bQZgLPgCaSv9pJfZ7D2na7c(this, str));
        }

        public /* synthetic */ void lambda$processSearch$1$AudioPlayerAlert$ListAdapter(String str) {
            Utilities.searchQueue.postRunnable(new -$$Lambda$AudioPlayerAlert$ListAdapter$FXPaVUN-2Qhq8rdrH0D2ehP2yJM(this, str, new ArrayList(AudioPlayerAlert.this.playlist)));
        }

        /* JADX WARNING: Removed duplicated region for block: B:62:0x00c0 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00bc A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00bc A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x00c0 A:{SYNTHETIC} */
        public /* synthetic */ void lambda$null$0$AudioPlayerAlert$ListAdapter(java.lang.String r11, java.util.ArrayList r12) {
            /*
            r10 = this;
            r11 = r11.trim();
            r11 = r11.toLowerCase();
            r0 = r11.length();
            if (r0 != 0) goto L_0x0017;
        L_0x000e:
            r11 = new java.util.ArrayList;
            r11.<init>();
            r10.updateSearchResults(r11);
            return;
        L_0x0017:
            r0 = org.telegram.messenger.LocaleController.getInstance();
            r0 = r0.getTranslitString(r11);
            r1 = r11.equals(r0);
            if (r1 != 0) goto L_0x002b;
        L_0x0025:
            r1 = r0.length();
            if (r1 != 0) goto L_0x002c;
        L_0x002b:
            r0 = 0;
        L_0x002c:
            r1 = 0;
            r2 = 1;
            if (r0 == 0) goto L_0x0032;
        L_0x0030:
            r3 = 1;
            goto L_0x0033;
        L_0x0032:
            r3 = 0;
        L_0x0033:
            r3 = r3 + r2;
            r3 = new java.lang.String[r3];
            r3[r1] = r11;
            if (r0 == 0) goto L_0x003c;
        L_0x003a:
            r3[r2] = r0;
        L_0x003c:
            r11 = new java.util.ArrayList;
            r11.<init>();
            r0 = 0;
        L_0x0042:
            r2 = r12.size();
            if (r0 >= r2) goto L_0x00c7;
        L_0x0048:
            r2 = r12.get(r0);
            r2 = (org.telegram.messenger.MessageObject) r2;
            r4 = 0;
        L_0x004f:
            r5 = r3.length;
            if (r4 >= r5) goto L_0x00c3;
        L_0x0052:
            r5 = r3[r4];
            r6 = r2.getDocumentName();
            if (r6 == 0) goto L_0x00c0;
        L_0x005a:
            r7 = r6.length();
            if (r7 != 0) goto L_0x0061;
        L_0x0060:
            goto L_0x00c0;
        L_0x0061:
            r6 = r6.toLowerCase();
            r6 = r6.contains(r5);
            if (r6 == 0) goto L_0x006f;
        L_0x006b:
            r11.add(r2);
            goto L_0x00c3;
        L_0x006f:
            r6 = r2.type;
            if (r6 != 0) goto L_0x007c;
        L_0x0073:
            r6 = r2.messageOwner;
            r6 = r6.media;
            r6 = r6.webpage;
            r6 = r6.document;
            goto L_0x0082;
        L_0x007c:
            r6 = r2.messageOwner;
            r6 = r6.media;
            r6 = r6.document;
        L_0x0082:
            r7 = 0;
        L_0x0083:
            r8 = r6.attributes;
            r8 = r8.size();
            if (r7 >= r8) goto L_0x00b9;
        L_0x008b:
            r8 = r6.attributes;
            r8 = r8.get(r7);
            r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;
            r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
            if (r9 == 0) goto L_0x00b6;
        L_0x0097:
            r6 = r8.performer;
            if (r6 == 0) goto L_0x00a4;
        L_0x009b:
            r6 = r6.toLowerCase();
            r6 = r6.contains(r5);
            goto L_0x00a5;
        L_0x00a4:
            r6 = 0;
        L_0x00a5:
            if (r6 != 0) goto L_0x00b4;
        L_0x00a7:
            r7 = r8.title;
            if (r7 == 0) goto L_0x00b4;
        L_0x00ab:
            r6 = r7.toLowerCase();
            r5 = r6.contains(r5);
            goto L_0x00ba;
        L_0x00b4:
            r5 = r6;
            goto L_0x00ba;
        L_0x00b6:
            r7 = r7 + 1;
            goto L_0x0083;
        L_0x00b9:
            r5 = 0;
        L_0x00ba:
            if (r5 == 0) goto L_0x00c0;
        L_0x00bc:
            r11.add(r2);
            goto L_0x00c3;
        L_0x00c0:
            r4 = r4 + 1;
            goto L_0x004f;
        L_0x00c3:
            r0 = r0 + 1;
            goto L_0x0042;
        L_0x00c7:
            r10.updateSearchResults(r11);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert$ListAdapter.lambda$null$0$AudioPlayerAlert$ListAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$AudioPlayerAlert$ListAdapter$q5JODj0x7FGd36CwY5UAvx5F7pI(this, arrayList));
        }

        public /* synthetic */ void lambda$updateSearchResults$2$AudioPlayerAlert$ListAdapter(ArrayList arrayList) {
            AudioPlayerAlert.this.searchWas = true;
            this.searchResult = arrayList;
            notifyDataSetChanged();
            AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void onFailedDownload(String str, boolean z) {
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public void onSuccessDownload(String str) {
    }

    public AudioPlayerAlert(Context context) {
        Context context2 = context;
        super(context2, true, 0);
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null) {
            this.currentAccount = playingMessageObject.currentAccount;
        } else {
            this.currentAccount = UserConfig.selectedAccount;
        }
        this.parentActivity = (LaunchActivity) context2;
        this.noCoverDrawable = context.getResources().getDrawable(NUM).mutate();
        this.noCoverDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_placeholder"), Mode.MULTIPLY));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.musicDidLoad);
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        String str = "player_background";
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.paint.setColor(Theme.getColor("player_placeholderBackground"));
        this.containerView = new FrameLayout(context2) {
            private boolean ignoreLayout = false;

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || AudioPlayerAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) AudioPlayerAlert.this.scrollOffsetY) || AudioPlayerAlert.this.placeholderImageView.getTranslationX() != 0.0f) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                AudioPlayerAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !AudioPlayerAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int dp;
                i2 = MeasureSpec.getSize(i2);
                int dp2 = (((AndroidUtilities.dp(178.0f) + (AudioPlayerAlert.this.playlist.size() * AndroidUtilities.dp(56.0f))) + AudioPlayerAlert.this.backgroundPaddingTop) + ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.statusBarHeight;
                int makeMeasureSpec = MeasureSpec.makeMeasureSpec(i2, NUM);
                int i3 = 0;
                if (AudioPlayerAlert.this.searching) {
                    dp = AndroidUtilities.dp(178.0f) + ActionBar.getCurrentActionBarHeight();
                    dp2 = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                } else {
                    dp = dp2 < i2 ? i2 - dp2 : dp2 < i2 ? 0 : i2 - ((i2 / 5) * 3);
                    dp2 = ActionBar.getCurrentActionBarHeight() + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                }
                dp += dp2;
                boolean z = true;
                if (AudioPlayerAlert.this.listView.getPaddingTop() != dp) {
                    this.ignoreLayout = true;
                    AudioPlayerAlert.this.listView.setPadding(0, dp, 0, AndroidUtilities.dp(8.0f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(i, makeMeasureSpec);
                AudioPlayerAlert audioPlayerAlert = AudioPlayerAlert.this;
                if (getMeasuredHeight() < i2) {
                    z = false;
                }
                audioPlayerAlert.inFullSize = z;
                i2 -= ActionBar.getCurrentActionBarHeight();
                if (VERSION.SDK_INT >= 21) {
                    i3 = AndroidUtilities.statusBarHeight;
                }
                i2 = (i2 - i3) - AndroidUtilities.dp(120.0f);
                i = Math.max(i2, getMeasuredWidth());
                AudioPlayerAlert.this.thumbMaxX = ((getMeasuredWidth() - i) / 2) - AndroidUtilities.dp(17.0f);
                AudioPlayerAlert.this.thumbMaxY = AndroidUtilities.dp(19.0f);
                AudioPlayerAlert.this.panelEndTranslation = (float) (getMeasuredHeight() - AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
                AudioPlayerAlert audioPlayerAlert2 = AudioPlayerAlert.this;
                audioPlayerAlert2.thumbMaxScale = (((float) i) / ((float) audioPlayerAlert2.placeholderImageView.getMeasuredWidth())) - 1.0f;
                AudioPlayerAlert.this.endTranslation = (float) (ActionBar.getCurrentActionBarHeight() + (AndroidUtilities.statusBarHeight - AndroidUtilities.dp(19.0f)));
                i = (int) Math.ceil((double) (((float) AudioPlayerAlert.this.placeholderImageView.getMeasuredHeight()) * (AudioPlayerAlert.this.thumbMaxScale + 1.0f)));
                if (i > i2) {
                    audioPlayerAlert2 = AudioPlayerAlert.this;
                    audioPlayerAlert2.endTranslation = audioPlayerAlert2.endTranslation - ((float) (i - i2));
                }
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int measuredHeight = AudioPlayerAlert.this.actionBar.getMeasuredHeight();
                AudioPlayerAlert.this.shadow.layout(AudioPlayerAlert.this.shadow.getLeft(), measuredHeight, AudioPlayerAlert.this.shadow.getRight(), AudioPlayerAlert.this.shadow.getMeasuredHeight() + measuredHeight);
                AudioPlayerAlert.this.updateLayout();
                AudioPlayerAlert audioPlayerAlert = AudioPlayerAlert.this;
                audioPlayerAlert.setFullAnimationProgress(audioPlayerAlert.fullAnimationProgress);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                AudioPlayerAlert.this.shadowDrawable.setBounds(0, Math.max(AudioPlayerAlert.this.actionBar.getMeasuredHeight(), AudioPlayerAlert.this.scrollOffsetY) - AudioPlayerAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                AudioPlayerAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        this.actionBar = new ActionBar(context2);
        this.actionBar.setBackgroundColor(Theme.getColor("player_actionBar"));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("player_actionBarItems"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("player_actionBarSelector"), false);
        String str2 = "player_actionBarTitle";
        this.actionBar.setTitleColor(Theme.getColor(str2));
        this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setTitle("1");
        this.actionBar.setSubtitle("1");
        this.actionBar.getTitleTextView().setAlpha(0.0f);
        this.actionBar.getSubtitleTextView().setAlpha(0.0f);
        this.avatarContainer = new ChatAvatarContainer(context2, null, false);
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitleColors(Theme.getColor(str2), Theme.getColor("player_actionBarSubtitle"));
        if (playingMessageObject != null) {
            long dialogId = playingMessageObject.getDialogId();
            int i2 = (int) dialogId;
            int i3 = (int) (dialogId >> 32);
            User user;
            if (i2 == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i3));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                        this.avatarContainer.setUserAvatar(user);
                    }
                }
            } else if (i2 > 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2));
                if (user != null) {
                    this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                    this.avatarContainer.setUserAvatar(user);
                }
            } else {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
                if (chat != null) {
                    this.avatarContainer.setTitle(chat.title);
                    this.avatarContainer.setChatAvatar(chat);
                }
            }
        }
        this.avatarContainer.setSubtitle(LocaleController.getString("AudioTitle", NUM));
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.menuItem = createMenu.addItem(0, NUM);
        this.menuItem.addSubItem(1, NUM, LocaleController.getString("Forward", NUM));
        this.menuItem.addSubItem(2, NUM, LocaleController.getString("ShareFile", NUM));
        this.menuItem.addSubItem(4, NUM, LocaleController.getString("ShowInChat", NUM));
        this.menuItem.setTranslationX((float) AndroidUtilities.dp(48.0f));
        this.menuItem.setAlpha(0.0f);
        this.searchItem = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchCollapse() {
                AudioPlayerAlert.this.avatarContainer.setVisibility(0);
                if (AudioPlayerAlert.this.hasOptions) {
                    AudioPlayerAlert.this.menuItem.setVisibility(4);
                }
                if (AudioPlayerAlert.this.searching) {
                    AudioPlayerAlert.this.searchWas = false;
                    AudioPlayerAlert.this.searching = false;
                    AudioPlayerAlert.this.setAllowNestedScroll(true);
                    AudioPlayerAlert.this.listAdapter.search(null);
                }
            }

            public void onSearchExpand() {
                AudioPlayerAlert audioPlayerAlert = AudioPlayerAlert.this;
                audioPlayerAlert.searchOpenPosition = audioPlayerAlert.layoutManager.findLastVisibleItemPosition();
                View findViewByPosition = AudioPlayerAlert.this.layoutManager.findViewByPosition(AudioPlayerAlert.this.searchOpenPosition);
                AudioPlayerAlert.this.searchOpenOffset = (findViewByPosition == null ? 0 : findViewByPosition.getTop()) - AudioPlayerAlert.this.listView.getPaddingTop();
                AudioPlayerAlert.this.avatarContainer.setVisibility(8);
                if (AudioPlayerAlert.this.hasOptions) {
                    AudioPlayerAlert.this.menuItem.setVisibility(8);
                }
                AudioPlayerAlert.this.searching = true;
                AudioPlayerAlert.this.setAllowNestedScroll(false);
                AudioPlayerAlert.this.listAdapter.notifyDataSetChanged();
            }

            public void onTextChanged(EditText editText) {
                if (editText.length() > 0) {
                    AudioPlayerAlert.this.listAdapter.search(editText.getText().toString());
                    return;
                }
                AudioPlayerAlert.this.searchWas = false;
                AudioPlayerAlert.this.listAdapter.search(null);
            }
        });
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setHint(LocaleController.getString("Search", NUM));
        searchField.setTextColor(Theme.getColor(str2));
        String str3 = "player_time";
        searchField.setHintTextColor(Theme.getColor(str3));
        searchField.setCursorColor(Theme.getColor(str2));
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
            this.actionBar.setActionModeTopColor(Theme.getColor("player_actionBarTop"));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    AudioPlayerAlert.this.dismiss();
                } else {
                    AudioPlayerAlert.this.onSubItemClick(i);
                }
            }
        });
        this.shadow = new View(context2);
        this.shadow.setAlpha(0.0f);
        this.shadow.setBackgroundResource(NUM);
        this.shadow2 = new View(context2);
        this.shadow2.setAlpha(0.0f);
        this.shadow2.setBackgroundResource(NUM);
        this.playerLayout = new FrameLayout(context2);
        this.playerLayout.setBackgroundColor(Theme.getColor(str));
        this.placeholderImageView = new BackupImageView(context2) {
            private RectF rect = new RectF();

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                if (AudioPlayerAlert.this.hasNoCover == 1 || (AudioPlayerAlert.this.hasNoCover == 2 && !(getImageReceiver().hasBitmapImage() && getImageReceiver().getCurrentAlpha() == 1.0f))) {
                    this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.drawRoundRect(this.rect, (float) getRoundRadius(), (float) getRoundRadius(), AudioPlayerAlert.this.paint);
                    int dp = (int) (((float) AndroidUtilities.dp(63.0f)) * Math.max(((AudioPlayerAlert.this.thumbMaxScale / getScaleX()) / 3.0f) / AudioPlayerAlert.this.thumbMaxScale, 1.0f / AudioPlayerAlert.this.thumbMaxScale));
                    float f = (float) (dp / 2);
                    int centerX = (int) (this.rect.centerX() - f);
                    int centerY = (int) (this.rect.centerY() - f);
                    AudioPlayerAlert.this.noCoverDrawable.setBounds(centerX, centerY, centerX + dp, dp + centerY);
                    AudioPlayerAlert.this.noCoverDrawable.draw(canvas);
                }
                if (AudioPlayerAlert.this.hasNoCover != 1) {
                    super.onDraw(canvas);
                }
            }
        };
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        this.placeholderImageView.setPivotX(0.0f);
        this.placeholderImageView.setPivotY(0.0f);
        this.placeholderImageView.setOnClickListener(new -$$Lambda$AudioPlayerAlert$rAc8YAca3eZCLAfei_UTWtnjvbQ(this));
        this.titleTextView = new TextView(context2);
        this.titleTextView.setTextColor(Theme.getColor(str2));
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setSingleLine(true);
        this.playerLayout.addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 18.0f, 60.0f, 0.0f));
        this.authorTextView = new TextView(context2);
        this.authorTextView.setTextColor(Theme.getColor(str3));
        this.authorTextView.setTextSize(1, 14.0f);
        this.authorTextView.setEllipsize(TruncateAt.END);
        this.authorTextView.setSingleLine(true);
        this.playerLayout.addView(this.authorTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 40.0f, 60.0f, 0.0f));
        this.optionsButton = new ActionBarMenuItem(context2, null, 0, Theme.getColor("player_actionBarItems"));
        this.optionsButton.setLongClickEnabled(false);
        this.optionsButton.setIcon(NUM);
        this.optionsButton.setAdditionalOffset(-AndroidUtilities.dp(120.0f));
        this.playerLayout.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 19.0f, 10.0f, 0.0f));
        this.optionsButton.addSubItem(1, NUM, LocaleController.getString("Forward", NUM));
        this.optionsButton.addSubItem(2, NUM, LocaleController.getString("ShareFile", NUM));
        this.optionsButton.addSubItem(4, NUM, LocaleController.getString("ShowInChat", NUM));
        this.optionsButton.setOnClickListener(new -$$Lambda$AudioPlayerAlert$L6a3eFAcdzUb5tmijbnBD0GiP9c(this));
        this.optionsButton.setDelegate(new -$$Lambda$AudioPlayerAlert$2le4yIg27yXBU0W-K7geAc_qlH0(this));
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.seekBarView = new SeekBarView(context2);
        this.seekBarView.setDelegate(-$$Lambda$AudioPlayerAlert$pQ-5LDOKmp9BWnrwt0sO-Br-q0g.INSTANCE);
        this.playerLayout.addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 8.0f, 62.0f, 8.0f, 0.0f));
        this.progressView = new LineProgressView(context2);
        this.progressView.setVisibility(4);
        this.progressView.setBackgroundColor(Theme.getColor("player_progressBackground"));
        this.progressView.setProgressColor(Theme.getColor("player_progress"));
        this.playerLayout.addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, 51, 20.0f, 78.0f, 20.0f, 0.0f));
        this.timeTextView = new SimpleTextView(context2);
        this.timeTextView.setTextSize(12);
        this.timeTextView.setText("0:00");
        this.timeTextView.setTextColor(Theme.getColor(str3));
        this.playerLayout.addView(this.timeTextView, LayoutHelper.createFrame(100, -2.0f, 51, 20.0f, 92.0f, 0.0f, 0.0f));
        this.durationTextView = new TextView(context2);
        this.durationTextView.setTextSize(1, 12.0f);
        this.durationTextView.setTextColor(Theme.getColor(str3));
        this.durationTextView.setGravity(17);
        this.playerLayout.addView(this.durationTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 90.0f, 20.0f, 0.0f));
        AnonymousClass6 anonymousClass6 = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                i3 = ((i3 - i) - AndroidUtilities.dp(248.0f)) / 4;
                for (int i5 = 0; i5 < 5; i5++) {
                    i = AndroidUtilities.dp((float) ((i5 * 48) + 4)) + (i3 * i5);
                    i2 = AndroidUtilities.dp(9.0f);
                    AudioPlayerAlert.this.buttons[i5].layout(i, i2, AudioPlayerAlert.this.buttons[i5].getMeasuredWidth() + i, AudioPlayerAlert.this.buttons[i5].getMeasuredHeight() + i2);
                }
            }
        };
        this.playerLayout.addView(anonymousClass6, LayoutHelper.createFrame(-1, 66.0f, 51, 0.0f, 106.0f, 0.0f, 0.0f));
        View[] viewArr = this.buttons;
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, null, 0, 0);
        this.shuffleButton = actionBarMenuItem;
        viewArr[0] = actionBarMenuItem;
        this.shuffleButton.setLongClickEnabled(false);
        this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
        anonymousClass6.addView(this.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
        this.shuffleButton.setOnClickListener(new -$$Lambda$AudioPlayerAlert$vDsQnotDAAYl8VqVL3Roz4WO8JM(this));
        TextView addSubItem = this.shuffleButton.addSubItem(1, LocaleController.getString("ReverseOrder", NUM));
        addSubItem.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.playOrderButtons[0] = context.getResources().getDrawable(NUM).mutate();
        addSubItem.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        addSubItem.setCompoundDrawablesWithIntrinsicBounds(this.playOrderButtons[0], null, null, null);
        addSubItem = this.shuffleButton.addSubItem(2, LocaleController.getString("Shuffle", NUM));
        addSubItem.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.playOrderButtons[1] = context.getResources().getDrawable(NUM).mutate();
        addSubItem.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        addSubItem.setCompoundDrawablesWithIntrinsicBounds(this.playOrderButtons[1], null, null, null);
        this.shuffleButton.setDelegate(new -$$Lambda$AudioPlayerAlert$HopcJEXECX0ZxGGa4UewdstQ_LI(this));
        viewArr = this.buttons;
        ImageView imageView = new ImageView(context2);
        viewArr[1] = imageView;
        imageView.setScaleType(ScaleType.CENTER);
        String str4 = "player_button";
        String str5 = "player_buttonActive";
        imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, NUM, Theme.getColor(str4), Theme.getColor(str5)));
        anonymousClass6.addView(imageView, LayoutHelper.createFrame(48, 48, 51));
        imageView.setOnClickListener(-$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg.INSTANCE);
        imageView.setContentDescription(LocaleController.getString("AccDescrPrevious", NUM));
        View[] viewArr2 = this.buttons;
        ImageView imageView2 = new ImageView(context2);
        this.playButton = imageView2;
        viewArr2[2] = imageView2;
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, NUM, Theme.getColor(str4), Theme.getColor(str5)));
        anonymousClass6.addView(this.playButton, LayoutHelper.createFrame(48, 48, 51));
        this.playButton.setOnClickListener(-$$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8ac-chYU.INSTANCE);
        View[] viewArr3 = this.buttons;
        imageView2 = new ImageView(context2);
        viewArr3[3] = imageView2;
        imageView2.setScaleType(ScaleType.CENTER);
        imageView2.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, NUM, Theme.getColor(str4), Theme.getColor(str5)));
        anonymousClass6.addView(imageView2, LayoutHelper.createFrame(48, 48, 51));
        imageView2.setOnClickListener(-$$Lambda$AudioPlayerAlert$4X94PKs6A-0UhkXhWsjRM6iEc3I.INSTANCE);
        imageView2.setContentDescription(LocaleController.getString("Next", NUM));
        viewArr3 = this.buttons;
        ImageView imageView3 = new ImageView(context2);
        this.repeatButton = imageView3;
        viewArr3[4] = imageView3;
        this.repeatButton.setScaleType(ScaleType.CENTER);
        this.repeatButton.setPadding(0, 0, AndroidUtilities.dp(8.0f), 0);
        anonymousClass6.addView(this.repeatButton, LayoutHelper.createFrame(50, 48, 51));
        this.repeatButton.setOnClickListener(new -$$Lambda$AudioPlayerAlert$dXFO6JRbR1ZdMBRfhd2XydhikZ0(this));
        this.listView = new RecyclerListView(context2) {
            boolean ignoreLayout;

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:36:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:21:0x0090  */
            /* JADX WARNING: Removed duplicated region for block: B:21:0x0090  */
            /* JADX WARNING: Removed duplicated region for block: B:36:? A:{SYNTHETIC, RETURN} */
            public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                /*
                r10 = this;
                super.onLayout(r11, r12, r13, r14, r15);
                r11 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r11 = r11.searchOpenPosition;
                r0 = -1;
                r1 = 1;
                r2 = 0;
                if (r11 == r0) goto L_0x0043;
            L_0x000e:
                r11 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r11 = r11.actionBar;
                r11 = r11.isSearchFieldVisible();
                if (r11 != 0) goto L_0x0043;
            L_0x001a:
                r10.ignoreLayout = r1;
                r11 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r11 = r11.layoutManager;
                r1 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r1 = r1.searchOpenPosition;
                r3 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r3 = r3.searchOpenOffset;
                r11.scrollToPositionWithOffset(r1, r3);
                r5 = 0;
                r4 = r10;
                r6 = r12;
                r7 = r13;
                r8 = r14;
                r9 = r15;
                super.onLayout(r5, r6, r7, r8, r9);
                r10.ignoreLayout = r2;
                r11 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r11.searchOpenPosition = r0;
                goto L_0x00cb;
            L_0x0043:
                r11 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r11 = r11.scrollToSong;
                if (r11 == 0) goto L_0x00cb;
            L_0x004b:
                r11 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r11.scrollToSong = r2;
                r11 = org.telegram.messenger.MediaController.getInstance();
                r11 = r11.getPlayingMessageObject();
                if (r11 == 0) goto L_0x00cb;
            L_0x005a:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.listView;
                r0 = r0.getChildCount();
                r3 = 0;
            L_0x0065:
                if (r3 >= r0) goto L_0x008d;
            L_0x0067:
                r4 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r4 = r4.listView;
                r4 = r4.getChildAt(r3);
                r5 = r4 instanceof org.telegram.ui.Cells.AudioPlayerCell;
                if (r5 == 0) goto L_0x008a;
            L_0x0075:
                r5 = r4;
                r5 = (org.telegram.ui.Cells.AudioPlayerCell) r5;
                r5 = r5.getMessageObject();
                if (r5 != r11) goto L_0x008a;
            L_0x007e:
                r0 = r4.getBottom();
                r3 = r10.getMeasuredHeight();
                if (r0 > r3) goto L_0x008d;
            L_0x0088:
                r0 = 1;
                goto L_0x008e;
            L_0x008a:
                r3 = r3 + 1;
                goto L_0x0065;
            L_0x008d:
                r0 = 0;
            L_0x008e:
                if (r0 != 0) goto L_0x00cb;
            L_0x0090:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.playlist;
                r11 = r0.indexOf(r11);
                if (r11 < 0) goto L_0x00cb;
            L_0x009c:
                r10.ignoreLayout = r1;
                r0 = org.telegram.messenger.SharedConfig.playOrderReversed;
                if (r0 == 0) goto L_0x00ac;
            L_0x00a2:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.layoutManager;
                r0.scrollToPosition(r11);
                goto L_0x00c0;
            L_0x00ac:
                r0 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r0 = r0.layoutManager;
                r1 = org.telegram.ui.Components.AudioPlayerAlert.this;
                r1 = r1.playlist;
                r1 = r1.size();
                r1 = r1 - r11;
                r0.scrollToPosition(r1);
            L_0x00c0:
                r4 = 0;
                r3 = r10;
                r5 = r12;
                r6 = r13;
                r7 = r14;
                r8 = r15;
                super.onLayout(r4, r5, r6, r7, r8);
                r10.ignoreLayout = r2;
            L_0x00cb:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert$AnonymousClass7.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* Access modifiers changed, original: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return AudioPlayerAlert.this.playerLayout == null || f2 > AudioPlayerAlert.this.playerLayout.getY() + ((float) AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
            }

            public boolean drawChild(Canvas canvas, View view, long j) {
                canvas.save();
                canvas.clipRect(0, (AudioPlayerAlert.this.actionBar != null ? AudioPlayerAlert.this.actionBar.getMeasuredHeight() : 0) + AndroidUtilities.dp(50.0f), getMeasuredWidth(), getMeasuredHeight());
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }
        };
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener(-$$Lambda$AudioPlayerAlert$48MEuBkln-g5kGMLmNUMeP-Rsutc.INSTANCE);
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && AudioPlayerAlert.this.searching && AudioPlayerAlert.this.searchWas) {
                    AndroidUtilities.hideKeyboard(AudioPlayerAlert.this.getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                AudioPlayerAlert.this.updateLayout();
            }
        });
        this.playlist = MediaController.getInstance().getPlaylist();
        this.listAdapter.notifyDataSetChanged();
        this.containerView.addView(this.playerLayout, LayoutHelper.createFrame(-1, 178.0f));
        this.containerView.addView(this.shadow2, LayoutHelper.createFrame(-1, 3.0f));
        this.containerView.addView(this.placeholderImageView, LayoutHelper.createFrame(40, 40.0f, 51, 17.0f, 19.0f, 0.0f, 0.0f));
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f));
        this.containerView.addView(this.actionBar);
        updateTitle(false);
        updateRepeatButton();
        updateShuffleButton();
    }

    public /* synthetic */ void lambda$new$0$AudioPlayerAlert(View view) {
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        String str = "fullAnimationProgress";
        float f = 0.0f;
        Animator[] animatorArr;
        float[] fArr;
        if (this.scrollOffsetY <= this.actionBar.getMeasuredHeight()) {
            animatorSet = this.animatorSet;
            animatorArr = new Animator[1];
            fArr = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, str, fArr);
            animatorSet.playTogether(animatorArr);
        } else {
            animatorSet = this.animatorSet;
            animatorArr = new Animator[4];
            fArr = new float[1];
            fArr[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, str, fArr);
            ActionBar actionBar = this.actionBar;
            fArr = new float[1];
            fArr[0] = this.isInFullMode ? 0.0f : 1.0f;
            String str2 = "alpha";
            animatorArr[1] = ObjectAnimator.ofFloat(actionBar, str2, fArr);
            View view2 = this.shadow;
            float[] fArr2 = new float[1];
            fArr2[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(view2, str2, fArr2);
            view2 = this.shadow2;
            fArr2 = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr2[0] = f;
            animatorArr[3] = ObjectAnimator.ofFloat(view2, str2, fArr2);
            animatorSet.playTogether(animatorArr);
        }
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(250);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(AudioPlayerAlert.this.animatorSet)) {
                    if (AudioPlayerAlert.this.isInFullMode) {
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(0);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(4);
                    } else {
                        AudioPlayerAlert.this.listView.setScrollEnabled(true);
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(4);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(0);
                    }
                    AudioPlayerAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        if (this.hasOptions) {
            this.menuItem.setVisibility(0);
        }
        this.searchItem.setVisibility(0);
        this.isInFullMode ^= 1;
        this.listView.setScrollEnabled(false);
        if (this.isInFullMode) {
            this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(68.0f));
        } else {
            this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
        }
    }

    public /* synthetic */ void lambda$new$1$AudioPlayerAlert(View view) {
        this.optionsButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$3$AudioPlayerAlert(View view) {
        this.shuffleButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$4$AudioPlayerAlert(int i) {
        MediaController.getInstance().toggleShuffleMusic(i);
        updateShuffleButton();
        this.listAdapter.notifyDataSetChanged();
    }

    static /* synthetic */ void lambda$new$6(View view) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    public /* synthetic */ void lambda$new$8$AudioPlayerAlert(View view) {
        SharedConfig.toggleRepeatMode();
        updateRepeatButton();
    }

    static /* synthetic */ void lambda$new$9(View view, int i) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
        }
    }

    @Keep
    public void setFullAnimationProgress(float f) {
        this.fullAnimationProgress = f;
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp((1.0f - this.fullAnimationProgress) * 20.0f));
        f = (this.thumbMaxScale * this.fullAnimationProgress) + 1.0f;
        this.placeholderImageView.setScaleX(f);
        this.placeholderImageView.setScaleY(f);
        this.placeholderImageView.getTranslationY();
        this.placeholderImageView.setTranslationX(((float) this.thumbMaxX) * this.fullAnimationProgress);
        BackupImageView backupImageView = this.placeholderImageView;
        float f2 = this.startTranslation;
        backupImageView.setTranslationY(f2 + ((this.endTranslation - f2) * this.fullAnimationProgress));
        FrameLayout frameLayout = this.playerLayout;
        f2 = this.panelStartTranslation;
        frameLayout.setTranslationY(f2 + ((this.panelEndTranslation - f2) * this.fullAnimationProgress));
        View view = this.shadow2;
        f2 = this.panelStartTranslation;
        view.setTranslationY((f2 + ((this.panelEndTranslation - f2) * this.fullAnimationProgress)) + ((float) this.playerLayout.getMeasuredHeight()));
        this.menuItem.setAlpha(this.fullAnimationProgress);
        this.searchItem.setAlpha(1.0f - this.fullAnimationProgress);
        this.avatarContainer.setAlpha(1.0f - this.fullAnimationProgress);
        this.actionBar.getTitleTextView().setAlpha(this.fullAnimationProgress);
        this.actionBar.getSubtitleTextView().setAlpha(this.fullAnimationProgress);
    }

    @Keep
    public float getFullAnimationProgress() {
        return this.fullAnimationProgress;
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x00a4 */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:29|30|31|32) */
    /* JADX WARNING: Missing block: B:16:0x0066, code skipped:
            if (r7.exists() == false) goto L_0x0068;
     */
    private void onSubItemClick(int r7) {
        /*
        r6 = this;
        r0 = org.telegram.messenger.MediaController.getInstance();
        r0 = r0.getPlayingMessageObject();
        if (r0 == 0) goto L_0x017a;
    L_0x000a:
        r1 = r6.parentActivity;
        if (r1 != 0) goto L_0x0010;
    L_0x000e:
        goto L_0x017a;
    L_0x0010:
        r2 = 1;
        if (r7 != r2) goto L_0x004b;
    L_0x0013:
        r7 = org.telegram.messenger.UserConfig.selectedAccount;
        r3 = r6.currentAccount;
        if (r7 == r3) goto L_0x001c;
    L_0x0019:
        r1.switchToAccount(r3, r2);
    L_0x001c:
        r7 = new android.os.Bundle;
        r7.<init>();
        r1 = "onlySelect";
        r7.putBoolean(r1, r2);
        r1 = 3;
        r2 = "dialogsType";
        r7.putInt(r2, r1);
        r1 = new org.telegram.ui.DialogsActivity;
        r1.<init>(r7);
        r7 = new java.util.ArrayList;
        r7.<init>();
        r7.add(r0);
        r0 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$bZwv44or3-083Y0MYIMNRo1ORcQ;
        r0.<init>(r6, r7);
        r1.setDelegate(r0);
        r7 = r6.parentActivity;
        r7.lambda$runLinkRequest$27$LaunchActivity(r1);
        r6.dismiss();
        goto L_0x017a;
    L_0x004b:
        r3 = 2;
        if (r7 != r3) goto L_0x00ff;
    L_0x004e:
        r7 = r0.messageOwner;	 Catch:{ Exception -> 0x00f9 }
        r7 = r7.attachPath;	 Catch:{ Exception -> 0x00f9 }
        r7 = android.text.TextUtils.isEmpty(r7);	 Catch:{ Exception -> 0x00f9 }
        r1 = 0;
        if (r7 != 0) goto L_0x0068;
    L_0x0059:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x00f9 }
        r3 = r0.messageOwner;	 Catch:{ Exception -> 0x00f9 }
        r3 = r3.attachPath;	 Catch:{ Exception -> 0x00f9 }
        r7.<init>(r3);	 Catch:{ Exception -> 0x00f9 }
        r3 = r7.exists();	 Catch:{ Exception -> 0x00f9 }
        if (r3 != 0) goto L_0x0069;
    L_0x0068:
        r7 = r1;
    L_0x0069:
        if (r7 != 0) goto L_0x0071;
    L_0x006b:
        r7 = r0.messageOwner;	 Catch:{ Exception -> 0x00f9 }
        r7 = org.telegram.messenger.FileLoader.getPathToMessage(r7);	 Catch:{ Exception -> 0x00f9 }
    L_0x0071:
        r3 = r7.exists();	 Catch:{ Exception -> 0x00f9 }
        if (r3 == 0) goto L_0x00c9;
    L_0x0077:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x00f9 }
        r3 = "android.intent.action.SEND";
        r1.<init>(r3);	 Catch:{ Exception -> 0x00f9 }
        if (r0 == 0) goto L_0x0088;
    L_0x0080:
        r0 = r0.getMimeType();	 Catch:{ Exception -> 0x00f9 }
        r1.setType(r0);	 Catch:{ Exception -> 0x00f9 }
        goto L_0x008d;
    L_0x0088:
        r0 = "audio/mp3";
        r1.setType(r0);	 Catch:{ Exception -> 0x00f9 }
    L_0x008d:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00f9 }
        r3 = 24;
        r4 = "android.intent.extra.STREAM";
        if (r0 < r3) goto L_0x00ac;
    L_0x0095:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00a4 }
        r3 = "org.telegram.messenger.beta.provider";
        r0 = androidx.core.content.FileProvider.getUriForFile(r0, r3, r7);	 Catch:{ Exception -> 0x00a4 }
        r1.putExtra(r4, r0);	 Catch:{ Exception -> 0x00a4 }
        r1.setFlags(r2);	 Catch:{ Exception -> 0x00a4 }
        goto L_0x00b3;
    L_0x00a4:
        r7 = android.net.Uri.fromFile(r7);	 Catch:{ Exception -> 0x00f9 }
        r1.putExtra(r4, r7);	 Catch:{ Exception -> 0x00f9 }
        goto L_0x00b3;
    L_0x00ac:
        r7 = android.net.Uri.fromFile(r7);	 Catch:{ Exception -> 0x00f9 }
        r1.putExtra(r4, r7);	 Catch:{ Exception -> 0x00f9 }
    L_0x00b3:
        r7 = r6.parentActivity;	 Catch:{ Exception -> 0x00f9 }
        r0 = "ShareFile";
        r2 = NUM; // 0x7f0d0977 float:1.874703E38 double:1.0531309747E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x00f9 }
        r0 = android.content.Intent.createChooser(r1, r0);	 Catch:{ Exception -> 0x00f9 }
        r1 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r7.startActivityForResult(r0, r1);	 Catch:{ Exception -> 0x00f9 }
        goto L_0x017a;
    L_0x00c9:
        r7 = new org.telegram.ui.ActionBar.AlertDialog$Builder;	 Catch:{ Exception -> 0x00f9 }
        r0 = r6.parentActivity;	 Catch:{ Exception -> 0x00f9 }
        r7.<init>(r0);	 Catch:{ Exception -> 0x00f9 }
        r0 = "AppName";
        r2 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x00f9 }
        r7.setTitle(r0);	 Catch:{ Exception -> 0x00f9 }
        r0 = "OK";
        r2 = NUM; // 0x7f0d06da float:1.8745672E38 double:1.053130644E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x00f9 }
        r7.setPositiveButton(r0, r1);	 Catch:{ Exception -> 0x00f9 }
        r0 = "PleaseDownload";
        r1 = NUM; // 0x7f0d0849 float:1.8746417E38 double:1.0531308255E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);	 Catch:{ Exception -> 0x00f9 }
        r7.setMessage(r0);	 Catch:{ Exception -> 0x00f9 }
        r7.show();	 Catch:{ Exception -> 0x00f9 }
        goto L_0x017a;
    L_0x00f9:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
        goto L_0x017a;
    L_0x00ff:
        r3 = 4;
        if (r7 != r3) goto L_0x017a;
    L_0x0102:
        r7 = org.telegram.messenger.UserConfig.selectedAccount;
        r3 = r6.currentAccount;
        if (r7 == r3) goto L_0x010b;
    L_0x0108:
        r1.switchToAccount(r3, r2);
    L_0x010b:
        r7 = new android.os.Bundle;
        r7.<init>();
        r3 = r0.getDialogId();
        r1 = (int) r3;
        r5 = 32;
        r3 = r3 >> r5;
        r4 = (int) r3;
        if (r1 == 0) goto L_0x0151;
    L_0x011b:
        r3 = "chat_id";
        if (r4 != r2) goto L_0x0123;
    L_0x011f:
        r7.putInt(r3, r1);
        goto L_0x0156;
    L_0x0123:
        if (r1 <= 0) goto L_0x012b;
    L_0x0125:
        r2 = "user_id";
        r7.putInt(r2, r1);
        goto L_0x0156;
    L_0x012b:
        if (r1 >= 0) goto L_0x0156;
    L_0x012d:
        r2 = r6.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r4 = -r1;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = r2.getChat(r4);
        if (r2 == 0) goto L_0x014c;
    L_0x013e:
        r4 = r2.migrated_to;
        if (r4 == 0) goto L_0x014c;
    L_0x0142:
        r4 = "migrated_to";
        r7.putInt(r4, r1);
        r1 = r2.migrated_to;
        r1 = r1.channel_id;
        r1 = -r1;
    L_0x014c:
        r1 = -r1;
        r7.putInt(r3, r1);
        goto L_0x0156;
    L_0x0151:
        r1 = "enc_id";
        r7.putInt(r1, r4);
    L_0x0156:
        r0 = r0.getId();
        r1 = "message_id";
        r7.putInt(r1, r0);
        r0 = r6.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r1 = org.telegram.messenger.NotificationCenter.closeChats;
        r2 = 0;
        r3 = new java.lang.Object[r2];
        r0.postNotificationName(r1, r3);
        r0 = r6.parentActivity;
        r1 = new org.telegram.ui.ChatActivity;
        r1.<init>(r7);
        r0.presentFragment(r1, r2, r2);
        r6.dismiss();
    L_0x017a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.onSubItemClick(int):void");
    }

    public /* synthetic */ void lambda$onSubItemClick$10$AudioPlayerAlert(ArrayList arrayList, DialogsActivity dialogsActivity, ArrayList arrayList2, CharSequence charSequence, boolean z) {
        ArrayList arrayList3 = arrayList;
        ArrayList arrayList4 = arrayList2;
        int i = 0;
        if (arrayList2.size() > 1 || ((Long) arrayList4.get(0)).longValue() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) || charSequence != null) {
            while (i < arrayList2.size()) {
                long j;
                long longValue = ((Long) arrayList4.get(i)).longValue();
                if (charSequence != null) {
                    j = longValue;
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequence.toString(), longValue, null, null, true, null, null, null);
                } else {
                    j = longValue;
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(arrayList, j);
                i++;
                arrayList4 = arrayList2;
            }
            dialogsActivity.finishFragment();
            return;
        }
        long longValue2 = ((Long) arrayList4.get(0)).longValue();
        int i2 = (int) longValue2;
        int i3 = (int) (longValue2 >> 32);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (i2 == 0) {
            bundle.putInt("enc_id", i3);
        } else if (i2 > 0) {
            bundle.putInt("user_id", i2);
        } else if (i2 < 0) {
            bundle.putInt("chat_id", -i2);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        ChatActivity chatActivity = new ChatActivity(bundle);
        if (this.parentActivity.presentFragment(chatActivity, true, false)) {
            chatActivity.showFieldPanelForForward(true, arrayList3);
        } else {
            dialogsActivity.finishFragment();
        }
    }

    private int getCurrentTop() {
        if (this.listView.getChildCount() != 0) {
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
            if (holder != null) {
                int paddingTop = this.listView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
                    i = childAt.getTop();
                }
                return paddingTop - i;
            }
        }
        return -1000;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset) {
            boolean z = i == NotificationCenter.messagePlayingDidReset && ((Boolean) objArr[1]).booleanValue();
            updateTitle(z);
            View childAt;
            AudioPlayerCell audioPlayerCell;
            MessageObject messageObject;
            if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
                i = this.listView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof AudioPlayerCell) {
                        audioPlayerCell = (AudioPlayerCell) childAt;
                        messageObject = audioPlayerCell.getMessageObject();
                        if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                            audioPlayerCell.updateButtonState(false, true);
                        }
                    }
                }
            } else if (i == NotificationCenter.messagePlayingDidStart && ((MessageObject) objArr[0]).eventId == 0) {
                i = this.listView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof AudioPlayerCell) {
                        audioPlayerCell = (AudioPlayerCell) childAt;
                        messageObject = audioPlayerCell.getMessageObject();
                        if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                            audioPlayerCell.updateButtonState(false, true);
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject.isMusic()) {
                updateProgress(playingMessageObject);
            }
        } else if (i == NotificationCenter.musicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            View childAt = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop();
            if (top <= 0 || holder == null || holder.getAdapterPosition() != 0) {
                top = 0;
            }
            if (this.searchWas || this.searching) {
                top = 0;
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.listView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
                this.playerLayout.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.placeholderImageView.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.shadow2.setTranslationY((float) (Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY) + this.playerLayout.getMeasuredHeight()));
                this.containerView.invalidate();
                String str = "alpha";
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if ((!this.inFullSize || this.scrollOffsetY > this.actionBar.getMeasuredHeight()) && !this.searchWas) {
                    if (this.actionBar.getTag() != null) {
                        animatorSet = this.actionBarAnimation;
                        if (animatorSet != null) {
                            animatorSet.cancel();
                        }
                        this.actionBar.setTag(null);
                        this.actionBarAnimation = new AnimatorSet();
                        animatorSet = this.actionBarAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.actionBar, str, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.shadow, str, new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.shadow2, str, new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                        this.actionBarAnimation.setDuration(180);
                        this.actionBarAnimation.start();
                    }
                } else if (this.actionBar.getTag() == null) {
                    animatorSet = this.actionBarAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    this.actionBar.setTag(Integer.valueOf(1));
                    this.actionBarAnimation = new AnimatorSet();
                    animatorSet = this.actionBarAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.actionBar, str, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.shadow, str, new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.shadow2, str, new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                    this.actionBarAnimation.setDuration(180);
                    this.actionBarAnimation.start();
                }
            }
            this.startTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
            this.panelStartTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
        }
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoad);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public void onBackPressed() {
        ActionBar actionBar = this.actionBar;
        if (actionBar == null || !actionBar.isSearchFieldVisible()) {
            super.onBackPressed();
        } else {
            this.actionBar.closeSearchField();
        }
    }

    public void onProgressDownload(String str, float f) {
        this.progressView.setProgress(f, true);
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateShuffleButton() {
        Drawable mutate;
        String str = "player_button";
        String str2 = "player_buttonActive";
        if (SharedConfig.shuffleMusic) {
            mutate = getContext().getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
            this.shuffleButton.setIcon(mutate);
            this.shuffleButton.setContentDescription(LocaleController.getString("Shuffle", NUM));
        } else {
            mutate = getContext().getResources().getDrawable(NUM).mutate();
            if (SharedConfig.playOrderReversed) {
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
            } else {
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            }
            this.shuffleButton.setIcon(mutate);
            this.shuffleButton.setContentDescription(LocaleController.getString("ReverseOrder", NUM));
        }
        this.playOrderButtons[0].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.playOrderReversed ? str2 : str), Mode.MULTIPLY));
        mutate = this.playOrderButtons[1];
        if (SharedConfig.shuffleMusic) {
            str = str2;
        }
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
    }

    private void updateRepeatButton() {
        int i = SharedConfig.repeatMode;
        if (i == 0) {
            this.repeatButton.setImageResource(NUM);
            String str = "player_button";
            this.repeatButton.setTag(str);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOff", NUM));
            return;
        }
        String str2 = "player_buttonActive";
        if (i == 1) {
            this.repeatButton.setImageResource(NUM);
            this.repeatButton.setTag(str2);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatList", NUM));
        } else if (i == 2) {
            this.repeatButton.setImageResource(NUM);
            this.repeatButton.setTag(str2);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOne", NUM));
        }
    }

    private void updateProgress(MessageObject messageObject) {
        SeekBarView seekBarView = this.seekBarView;
        if (seekBarView != null) {
            if (!seekBarView.isDragging()) {
                this.seekBarView.setProgress(messageObject.audioProgress);
                this.seekBarView.setBufferedProgress(messageObject.bufferedProgress);
            }
            int i = this.lastTime;
            int i2 = messageObject.audioProgressSec;
            if (i != i2) {
                this.lastTime = i2;
                this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i2 / 60), Integer.valueOf(messageObject.audioProgressSec % 60)}));
            }
        }
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        String str = messageObject.messageOwner.attachPath;
        File file = null;
        if (str != null && str.length() > 0) {
            File file2 = new File(messageObject.messageOwner.attachPath);
            if (file2.exists()) {
                file = file2;
            }
        }
        if (file == null) {
            file = FileLoader.getPathToMessage(messageObject.messageOwner);
        }
        Object obj = (SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic()) ? 1 : null;
        if (file.exists() || obj != null) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        String fileName = messageObject.getFileName();
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
        Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
        this.progressView.setProgress(fileProgress != null ? fileProgress.floatValue() : 0.0f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void updateTitle(boolean z) {
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if ((playingMessageObject == null && z) || (playingMessageObject != null && !playingMessageObject.isMusic())) {
            dismiss();
        } else if (playingMessageObject != null) {
            if (playingMessageObject.eventId != 0 || playingMessageObject.getId() <= -NUM) {
                this.hasOptions = false;
                this.menuItem.setVisibility(4);
                this.optionsButton.setVisibility(4);
            } else {
                this.hasOptions = true;
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.menuItem.setVisibility(0);
                }
                this.optionsButton.setVisibility(0);
            }
            checkIfMusicDownloaded(playingMessageObject);
            updateProgress(playingMessageObject);
            String str = "player_buttonActive";
            String str2 = "player_button";
            ImageView imageView;
            if (MediaController.getInstance().isMessagePaused()) {
                imageView = this.playButton;
                imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(imageView.getContext(), NUM, Theme.getColor(str2), Theme.getColor(str)));
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                imageView = this.playButton;
                imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(imageView.getContext(), NUM, Theme.getColor(str2), Theme.getColor(str)));
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            String musicTitle = playingMessageObject.getMusicTitle();
            str = playingMessageObject.getMusicAuthor();
            this.titleTextView.setText(musicTitle);
            this.authorTextView.setText(str);
            this.actionBar.setTitle(musicTitle);
            this.actionBar.setSubtitle(str);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(" ");
            stringBuilder.append(musicTitle);
            stringBuilder.toString();
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (audioInfo == null || audioInfo.getCover() == null) {
                musicTitle = playingMessageObject.getArtworkUrl(false);
                if (TextUtils.isEmpty(musicTitle)) {
                    this.placeholderImageView.setImageDrawable(null);
                    this.hasNoCover = 1;
                } else {
                    this.placeholderImageView.setImage(musicTitle, null, null);
                    this.hasNoCover = 2;
                }
                this.placeholderImageView.invalidate();
            } else {
                this.hasNoCover = 0;
                this.placeholderImageView.setImageBitmap(audioInfo.getCover());
            }
            if (this.durationTextView != null) {
                CharSequence format;
                int duration = playingMessageObject.getDuration();
                TextView textView = this.durationTextView;
                if (duration != 0) {
                    format = String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                } else {
                    format = "-:--";
                }
                textView.setText(format);
            }
        }
    }
}
