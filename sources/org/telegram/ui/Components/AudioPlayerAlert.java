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
import android.support.annotation.Keep;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AudioPlayerCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;
import org.telegram.ui.LaunchActivity;

public class AudioPlayerAlert extends BottomSheet implements FileDownloadProgressListener, NotificationCenterDelegate {
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
    private boolean hasNoCover;
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
    private ArrayList<MessageObject> playlist = new ArrayList();
    private LineProgressView progressView;
    private ImageView repeatButton;
    private int scrollOffsetY = ConnectionsManager.DEFAULT_DATACENTER_ID;
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

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$5 */
    class C10865 implements OnClickListener {

        /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$5$1 */
        class C10851 extends AnimatorListenerAdapter {
            C10851() {
            }

            public void onAnimationEnd(Animator animator) {
                if (animator.equals(AudioPlayerAlert.this.animatorSet) != null) {
                    if (AudioPlayerAlert.this.isInFullMode == null) {
                        AudioPlayerAlert.this.listView.setScrollEnabled(true);
                        if (AudioPlayerAlert.this.hasOptions != null) {
                            AudioPlayerAlert.this.menuItem.setVisibility(4);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(0);
                    } else {
                        if (AudioPlayerAlert.this.hasOptions != null) {
                            AudioPlayerAlert.this.menuItem.setVisibility(0);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(4);
                    }
                    AudioPlayerAlert.this.animatorSet = null;
                }
            }
        }

        C10865() {
        }

        public void onClick(View view) {
            if (AudioPlayerAlert.this.animatorSet != null) {
                AudioPlayerAlert.this.animatorSet.cancel();
                AudioPlayerAlert.this.animatorSet = null;
            }
            AudioPlayerAlert.this.animatorSet = new AnimatorSet();
            float f = 1.0f;
            Animator[] animatorArr;
            AudioPlayerAlert audioPlayerAlert;
            String str;
            float[] fArr;
            if (AudioPlayerAlert.this.scrollOffsetY <= AudioPlayerAlert.this.actionBar.getMeasuredHeight()) {
                view = AudioPlayerAlert.this.animatorSet;
                animatorArr = new Animator[1];
                audioPlayerAlert = AudioPlayerAlert.this;
                str = "fullAnimationProgress";
                fArr = new float[1];
                if (AudioPlayerAlert.this.isInFullMode) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(audioPlayerAlert, str, fArr);
                view.playTogether(animatorArr);
            } else {
                view = AudioPlayerAlert.this.animatorSet;
                animatorArr = new Animator[4];
                audioPlayerAlert = AudioPlayerAlert.this;
                str = "fullAnimationProgress";
                fArr = new float[1];
                fArr[0] = AudioPlayerAlert.this.isInFullMode ? 0.0f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(audioPlayerAlert, str, fArr);
                ActionBar access$1300 = AudioPlayerAlert.this.actionBar;
                str = "alpha";
                fArr = new float[1];
                fArr[0] = AudioPlayerAlert.this.isInFullMode ? 0.0f : 1.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(access$1300, str, fArr);
                View access$1400 = AudioPlayerAlert.this.shadow;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                fArr2[0] = AudioPlayerAlert.this.isInFullMode ? 0.0f : 1.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(access$1400, str2, fArr2);
                access$1400 = AudioPlayerAlert.this.shadow2;
                str2 = "alpha";
                fArr2 = new float[1];
                if (AudioPlayerAlert.this.isInFullMode) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[3] = ObjectAnimator.ofFloat(access$1400, str2, fArr2);
                view.playTogether(animatorArr);
            }
            AudioPlayerAlert.this.animatorSet.setInterpolator(new DecelerateInterpolator());
            AudioPlayerAlert.this.animatorSet.setDuration(250);
            AudioPlayerAlert.this.animatorSet.addListener(new C10851());
            AudioPlayerAlert.this.animatorSet.start();
            if (AudioPlayerAlert.this.hasOptions != null) {
                AudioPlayerAlert.this.menuItem.setVisibility(0);
            }
            AudioPlayerAlert.this.searchItem.setVisibility(0);
            AudioPlayerAlert.this.isInFullMode = AudioPlayerAlert.this.isInFullMode ^ true;
            AudioPlayerAlert.this.listView.setScrollEnabled(false);
            if (AudioPlayerAlert.this.isInFullMode != null) {
                AudioPlayerAlert.this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(68.0f));
            } else {
                AudioPlayerAlert.this.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$6 */
    class C10876 implements OnClickListener {
        C10876() {
        }

        public void onClick(View view) {
            AudioPlayerAlert.this.optionsButton.toggleSubMenu();
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$2 */
    class C20372 extends ActionBarMenuItemSearchListener {
        C20372() {
        }

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
            AudioPlayerAlert.this.searchOpenPosition = AudioPlayerAlert.this.layoutManager.findLastVisibleItemPosition();
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
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$3 */
    class C20383 extends ActionBarMenuOnItemClick {
        C20383() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                AudioPlayerAlert.this.dismiss();
            } else {
                AudioPlayerAlert.this.onSubItemClick(i);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$7 */
    class C20407 implements ActionBarMenuItemDelegate {
        C20407() {
        }

        public void onItemClick(int i) {
            AudioPlayerAlert.this.onSubItemClick(i);
        }
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$8 */
    class C20418 implements SeekBarViewDelegate {
        C20418() {
        }

        public void onSeekBarDrag(float f) {
            MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), f);
        }
    }

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
            return 1 + AudioPlayerAlert.this.playlist.size();
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            if (!AudioPlayerAlert.this.searchWas) {
                if (viewHolder.getAdapterPosition() <= null) {
                    return null;
                }
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new AudioPlayerCell(this.context);
            } else {
                viewGroup = new View(this.context);
                viewGroup.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(178.0f)));
            }
            return new Holder(viewGroup);
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
            if (!AudioPlayerAlert.this.searchWas) {
                if (!AudioPlayerAlert.this.searching) {
                    return i == 0 ? 0 : 1;
                }
            }
            return 1;
        }

        public void search(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
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
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    ListAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    final ArrayList arrayList = new ArrayList(AudioPlayerAlert.this.playlist);
                    Utilities.searchQueue.postRunnable(new Runnable() {
                        public void run() {
                            String toLowerCase = str.trim().toLowerCase();
                            if (toLowerCase.length() == 0) {
                                ListAdapter.this.updateSearchResults(new ArrayList());
                                return;
                            }
                            String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                            if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                                translitString = null;
                            }
                            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                            strArr[0] = toLowerCase;
                            if (translitString != null) {
                                strArr[1] = translitString;
                            }
                            ArrayList arrayList = new ArrayList();
                            for (int i = 0; i < arrayList.size(); i++) {
                                MessageObject messageObject = (MessageObject) arrayList.get(i);
                                for (CharSequence charSequence : strArr) {
                                    String documentName = messageObject.getDocumentName();
                                    if (documentName != null) {
                                        if (documentName.length() != 0) {
                                            if (!documentName.toLowerCase().contains(charSequence)) {
                                                Document document;
                                                boolean contains;
                                                if (messageObject.type == 0) {
                                                    document = messageObject.messageOwner.media.webpage.document;
                                                } else {
                                                    document = messageObject.messageOwner.media.document;
                                                }
                                                int i2 = 0;
                                                while (i2 < document.attributes.size()) {
                                                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                                                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                                                        boolean contains2 = documentAttribute.performer != null ? documentAttribute.performer.toLowerCase().contains(charSequence) : false;
                                                        contains = (contains2 || documentAttribute.title == null) ? contains2 : documentAttribute.title.toLowerCase().contains(charSequence);
                                                        if (contains) {
                                                            arrayList.add(messageObject);
                                                            break;
                                                        }
                                                    } else {
                                                        i2++;
                                                    }
                                                }
                                                contains = false;
                                                if (contains) {
                                                    arrayList.add(messageObject);
                                                    break;
                                                }
                                            } else {
                                                arrayList.add(messageObject);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            ListAdapter.this.updateSearchResults(arrayList);
                        }
                    });
                }
            });
        }

        private void updateSearchResults(final ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    AudioPlayerAlert.this.searchWas = true;
                    ListAdapter.this.searchResult = arrayList;
                    ListAdapter.this.notifyDataSetChanged();
                    AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
                }
            });
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void onFailedDownload(String str) {
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public void onSuccessDownload(String str) {
    }

    public AudioPlayerAlert(Context context) {
        Context context2 = context;
        super(context2, true);
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null) {
            r0.currentAccount = playingMessageObject.currentAccount;
        } else {
            r0.currentAccount = UserConfig.selectedAccount;
        }
        r0.parentActivity = (LaunchActivity) context2;
        r0.noCoverDrawable = context.getResources().getDrawable(C0446R.drawable.nocover).mutate();
        r0.noCoverDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_placeholder), Mode.MULTIPLY));
        r0.TAG = DownloadController.getInstance(r0.currentAccount).generateObserverTag();
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.musicDidLoaded);
        r0.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
        r0.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_background), Mode.MULTIPLY));
        r0.paint.setColor(Theme.getColor(Theme.key_player_placeholderBackground));
        r0.containerView = new FrameLayout(context2) {
            private boolean ignoreLayout = null;

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || AudioPlayerAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) AudioPlayerAlert.this.scrollOffsetY) || AudioPlayerAlert.this.placeholderImageView.getTranslationX() != 0.0f) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                AudioPlayerAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return (AudioPlayerAlert.this.isDismissed() || super.onTouchEvent(motionEvent) == null) ? null : true;
            }

            protected void onMeasure(int i, int i2) {
                int dp;
                i2 = MeasureSpec.getSize(i2);
                int dp2 = (((AndroidUtilities.dp(178.0f) + (AudioPlayerAlert.this.playlist.size() * AndroidUtilities.dp(56.0f))) + AudioPlayerAlert.backgroundPaddingTop) + ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.statusBarHeight;
                int makeMeasureSpec = MeasureSpec.makeMeasureSpec(i2, NUM);
                int i3 = 0;
                if (AudioPlayerAlert.this.searching) {
                    dp = (AndroidUtilities.dp(178.0f) + ActionBar.getCurrentActionBarHeight()) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                } else {
                    dp = dp2 < i2 ? i2 - dp2 : dp2 < i2 ? 0 : i2 - ((i2 / 5) * 3);
                    dp += ActionBar.getCurrentActionBarHeight() + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                }
                boolean z = true;
                if (AudioPlayerAlert.this.listView.getPaddingTop() != dp) {
                    this.ignoreLayout = true;
                    AudioPlayerAlert.this.listView.setPadding(0, dp, 0, AndroidUtilities.dp(8.0f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(i, makeMeasureSpec);
                i = AudioPlayerAlert.this;
                if (getMeasuredHeight() < i2) {
                    z = false;
                }
                i.inFullSize = z;
                i2 -= ActionBar.getCurrentActionBarHeight();
                if (VERSION.SDK_INT >= 21) {
                    i3 = AndroidUtilities.statusBarHeight;
                }
                i2 = (i2 - i3) - AndroidUtilities.dp(NUM);
                i = Math.max(i2, getMeasuredWidth());
                AudioPlayerAlert.this.thumbMaxX = ((getMeasuredWidth() - i) / 2) - AndroidUtilities.dp(17.0f);
                AudioPlayerAlert.this.thumbMaxY = AndroidUtilities.dp(19.0f);
                AudioPlayerAlert.this.panelEndTranslation = (float) (getMeasuredHeight() - AudioPlayerAlert.this.playerLayout.getMeasuredHeight());
                AudioPlayerAlert.this.thumbMaxScale = (((float) i) / ((float) AudioPlayerAlert.this.placeholderImageView.getMeasuredWidth())) - NUM;
                AudioPlayerAlert.this.endTranslation = (float) (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(5.0f));
                i = (int) Math.ceil((double) (((float) AudioPlayerAlert.this.placeholderImageView.getMeasuredHeight()) * (1.0f + AudioPlayerAlert.this.thumbMaxScale)));
                if (i > i2) {
                    AudioPlayerAlert.this.endTranslation = AudioPlayerAlert.this.endTranslation - ((float) (i - i2));
                }
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                z = AudioPlayerAlert.this.actionBar.getMeasuredHeight();
                AudioPlayerAlert.this.shadow.layout(AudioPlayerAlert.this.shadow.getLeft(), z, AudioPlayerAlert.this.shadow.getRight(), AudioPlayerAlert.this.shadow.getMeasuredHeight() + z);
                AudioPlayerAlert.this.updateLayout();
                AudioPlayerAlert.this.setFullAnimationProgress(AudioPlayerAlert.this.fullAnimationProgress);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected void onDraw(Canvas canvas) {
                AudioPlayerAlert.this.shadowDrawable.setBounds(0, Math.max(AudioPlayerAlert.this.actionBar.getMeasuredHeight(), AudioPlayerAlert.this.scrollOffsetY) - AudioPlayerAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                AudioPlayerAlert.this.shadowDrawable.draw(canvas);
            }
        };
        r0.containerView.setWillNotDraw(false);
        r0.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        r0.actionBar = new ActionBar(context2);
        r0.actionBar.setBackgroundColor(Theme.getColor(Theme.key_player_actionBar));
        r0.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        r0.actionBar.setItemsColor(Theme.getColor(Theme.key_player_actionBarItems), false);
        r0.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_player_actionBarSelector), false);
        r0.actionBar.setTitleColor(Theme.getColor(Theme.key_player_actionBarTitle));
        r0.actionBar.setSubtitleColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
        r0.actionBar.setAlpha(0.0f);
        r0.actionBar.setTitle("1");
        r0.actionBar.setSubtitle("1");
        r0.actionBar.getTitleTextView().setAlpha(0.0f);
        r0.actionBar.getSubtitleTextView().setAlpha(0.0f);
        r0.avatarContainer = new ChatAvatarContainer(context2, null, false);
        r0.avatarContainer.setEnabled(false);
        r0.avatarContainer.setTitleColors(Theme.getColor(Theme.key_player_actionBarTitle), Theme.getColor(Theme.key_player_actionBarSubtitle));
        if (playingMessageObject != null) {
            long dialogId = playingMessageObject.getDialogId();
            int i = (int) dialogId;
            int i2 = (int) (dialogId >> 32);
            User user;
            if (i == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance(r0.currentAccount).getEncryptedChat(Integer.valueOf(i2));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        r0.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                        r0.avatarContainer.setUserAvatar(user);
                    }
                }
            } else if (i > 0) {
                user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(i));
                if (user != null) {
                    r0.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                    r0.avatarContainer.setUserAvatar(user);
                }
            } else {
                Chat chat = MessagesController.getInstance(r0.currentAccount).getChat(Integer.valueOf(-i));
                if (chat != null) {
                    r0.avatarContainer.setTitle(chat.title);
                    r0.avatarContainer.setChatAvatar(chat);
                }
            }
        }
        r0.avatarContainer.setSubtitle(LocaleController.getString("AudioTitle", C0446R.string.AudioTitle));
        r0.actionBar.addView(r0.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenu createMenu = r0.actionBar.createMenu();
        r0.menuItem = createMenu.addItem(0, (int) C0446R.drawable.ic_ab_other);
        r0.menuItem.addSubItem(1, LocaleController.getString("Forward", C0446R.string.Forward));
        r0.menuItem.addSubItem(2, LocaleController.getString("ShareFile", C0446R.string.ShareFile));
        r0.menuItem.addSubItem(4, LocaleController.getString("ShowInChat", C0446R.string.ShowInChat));
        r0.menuItem.setTranslationX((float) AndroidUtilities.dp(48.0f));
        r0.menuItem.setAlpha(0.0f);
        r0.searchItem = createMenu.addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C20372());
        EditTextBoldCursor searchField = r0.searchItem.getSearchField();
        searchField.setHint(LocaleController.getString("Search", C0446R.string.Search));
        searchField.setTextColor(Theme.getColor(Theme.key_player_actionBarTitle));
        searchField.setHintTextColor(Theme.getColor(Theme.key_player_time));
        searchField.setCursorColor(Theme.getColor(Theme.key_player_actionBarTitle));
        if (!AndroidUtilities.isTablet()) {
            r0.actionBar.showActionModeTop();
            r0.actionBar.setActionModeTopColor(Theme.getColor(Theme.key_player_actionBarTop));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C20383());
        r0.shadow = new View(context2);
        r0.shadow.setAlpha(0.0f);
        r0.shadow.setBackgroundResource(C0446R.drawable.header_shadow);
        r0.shadow2 = new View(context2);
        r0.shadow2.setAlpha(0.0f);
        r0.shadow2.setBackgroundResource(C0446R.drawable.header_shadow);
        r0.playerLayout = new FrameLayout(context2);
        r0.playerLayout.setBackgroundColor(Theme.getColor(Theme.key_player_background));
        r0.placeholderImageView = new BackupImageView(context2) {
            private RectF rect = new RectF();

            protected void onDraw(Canvas canvas) {
                if (AudioPlayerAlert.this.hasNoCover) {
                    this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    canvas.drawRoundRect(this.rect, (float) getRoundRadius(), (float) getRoundRadius(), AudioPlayerAlert.this.paint);
                    int dp = (int) (((float) AndroidUtilities.dp(63.0f)) * Math.max(((AudioPlayerAlert.this.thumbMaxScale / getScaleX()) / 3.0f) / AudioPlayerAlert.this.thumbMaxScale, 1.0f / AudioPlayerAlert.this.thumbMaxScale));
                    float f = (float) (dp / 2);
                    int centerX = (int) (this.rect.centerX() - f);
                    int centerY = (int) (this.rect.centerY() - f);
                    AudioPlayerAlert.this.noCoverDrawable.setBounds(centerX, centerY, centerX + dp, dp + centerY);
                    AudioPlayerAlert.this.noCoverDrawable.draw(canvas);
                    return;
                }
                super.onDraw(canvas);
            }
        };
        r0.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        r0.placeholderImageView.setPivotX(0.0f);
        r0.placeholderImageView.setPivotY(0.0f);
        r0.placeholderImageView.setOnClickListener(new C10865());
        r0.titleTextView = new TextView(context2);
        r0.titleTextView.setTextColor(Theme.getColor(Theme.key_player_actionBarTitle));
        r0.titleTextView.setTextSize(1, 15.0f);
        r0.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.titleTextView.setEllipsize(TruncateAt.END);
        r0.titleTextView.setSingleLine(true);
        r0.playerLayout.addView(r0.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 18.0f, 60.0f, 0.0f));
        r0.authorTextView = new TextView(context2);
        r0.authorTextView.setTextColor(Theme.getColor(Theme.key_player_time));
        r0.authorTextView.setTextSize(1, 14.0f);
        r0.authorTextView.setEllipsize(TruncateAt.END);
        r0.authorTextView.setSingleLine(true);
        r0.playerLayout.addView(r0.authorTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 72.0f, 40.0f, 60.0f, 0.0f));
        r0.optionsButton = new ActionBarMenuItem(context2, null, 0, Theme.getColor(Theme.key_player_actionBarItems));
        r0.optionsButton.setLongClickEnabled(false);
        r0.optionsButton.setIcon((int) C0446R.drawable.ic_ab_other);
        r0.optionsButton.setAdditionalOffset(-AndroidUtilities.dp(120.0f));
        r0.playerLayout.addView(r0.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 19.0f, 10.0f, 0.0f));
        r0.optionsButton.addSubItem(1, LocaleController.getString("Forward", C0446R.string.Forward));
        r0.optionsButton.addSubItem(2, LocaleController.getString("ShareFile", C0446R.string.ShareFile));
        r0.optionsButton.addSubItem(4, LocaleController.getString("ShowInChat", C0446R.string.ShowInChat));
        r0.optionsButton.setOnClickListener(new C10876());
        r0.optionsButton.setDelegate(new C20407());
        r0.seekBarView = new SeekBarView(context2);
        r0.seekBarView.setDelegate(new C20418());
        r0.playerLayout.addView(r0.seekBarView, LayoutHelper.createFrame(-1, 30.0f, 51, 8.0f, 62.0f, 8.0f, 0.0f));
        r0.progressView = new LineProgressView(context2);
        r0.progressView.setVisibility(4);
        r0.progressView.setBackgroundColor(Theme.getColor(Theme.key_player_progressBackground));
        r0.progressView.setProgressColor(Theme.getColor(Theme.key_player_progress));
        r0.playerLayout.addView(r0.progressView, LayoutHelper.createFrame(-1, 2.0f, 51, 20.0f, 78.0f, 20.0f, 0.0f));
        r0.timeTextView = new SimpleTextView(context2);
        r0.timeTextView.setTextSize(12);
        r0.timeTextView.setTextColor(Theme.getColor(Theme.key_player_time));
        r0.playerLayout.addView(r0.timeTextView, LayoutHelper.createFrame(100, -2.0f, 51, 20.0f, 92.0f, 0.0f, 0.0f));
        r0.durationTextView = new TextView(context2);
        r0.durationTextView.setTextSize(1, 12.0f);
        r0.durationTextView.setTextColor(Theme.getColor(Theme.key_player_time));
        r0.durationTextView.setGravity(17);
        r0.playerLayout.addView(r0.durationTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 90.0f, 20.0f, 0.0f));
        View c10889 = new FrameLayout(context2) {
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                i3 = ((i3 - i) - AndroidUtilities.dp(true)) / 4;
                for (i = 0; i < 5; i++) {
                    i2 = AndroidUtilities.dp((float) ((48 * i) + 4)) + (i3 * i);
                    i4 = AndroidUtilities.dp(NUM);
                    AudioPlayerAlert.this.buttons[i].layout(i2, i4, AudioPlayerAlert.this.buttons[i].getMeasuredWidth() + i2, AudioPlayerAlert.this.buttons[i].getMeasuredHeight() + i4);
                }
            }
        };
        r0.playerLayout.addView(c10889, LayoutHelper.createFrame(-1, 66.0f, 51, 0.0f, 106.0f, 0.0f, 0.0f));
        View[] viewArr = r0.buttons;
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, null, 0, 0);
        r0.shuffleButton = actionBarMenuItem;
        viewArr[0] = actionBarMenuItem;
        r0.shuffleButton.setLongClickEnabled(false);
        r0.shuffleButton.setAdditionalOffset(-AndroidUtilities.dp(10.0f));
        c10889.addView(r0.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
        r0.shuffleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudioPlayerAlert.this.shuffleButton.toggleSubMenu();
            }
        });
        TextView addSubItem = r0.shuffleButton.addSubItem(1, LocaleController.getString("ReverseOrder", C0446R.string.ReverseOrder));
        addSubItem.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r0.playOrderButtons[0] = context.getResources().getDrawable(C0446R.drawable.music_reverse).mutate();
        addSubItem.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        addSubItem.setCompoundDrawablesWithIntrinsicBounds(r0.playOrderButtons[0], null, null, null);
        addSubItem = r0.shuffleButton.addSubItem(2, LocaleController.getString("Shuffle", C0446R.string.Shuffle));
        addSubItem.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r0.playOrderButtons[1] = context.getResources().getDrawable(C0446R.drawable.pl_shuffle).mutate();
        addSubItem.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        addSubItem.setCompoundDrawablesWithIntrinsicBounds(r0.playOrderButtons[1], null, null, null);
        r0.shuffleButton.setDelegate(new ActionBarMenuItemDelegate() {
            public void onItemClick(int i) {
                MediaController.getInstance().toggleShuffleMusic(i);
                AudioPlayerAlert.this.updateShuffleButton();
                AudioPlayerAlert.this.listAdapter.notifyDataSetChanged();
            }
        });
        viewArr = r0.buttons;
        View imageView = new ImageView(context2);
        viewArr[1] = imageView;
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, C0446R.drawable.pl_previous, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        c10889.addView(imageView, LayoutHelper.createFrame(48, 48, 51));
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MediaController.getInstance().playPreviousMessage();
            }
        });
        viewArr = r0.buttons;
        ImageView imageView2 = new ImageView(context2);
        r0.playButton = imageView2;
        viewArr[2] = imageView2;
        r0.playButton.setScaleType(ScaleType.CENTER);
        r0.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, C0446R.drawable.pl_play, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        c10889.addView(r0.playButton, LayoutHelper.createFrame(48, 48, 51));
        r0.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (MediaController.getInstance().isDownloadingCurrentMessage() == null) {
                    if (MediaController.getInstance().isMessagePaused() != null) {
                        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                    } else {
                        MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
                    }
                }
            }
        });
        View[] viewArr2 = r0.buttons;
        imageView = new ImageView(context2);
        viewArr2[3] = imageView;
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(context2, C0446R.drawable.pl_next, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        c10889.addView(imageView, LayoutHelper.createFrame(48, 48, 51));
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MediaController.getInstance().playNextMessage();
            }
        });
        viewArr2 = r0.buttons;
        ImageView imageView3 = new ImageView(context2);
        r0.repeatButton = imageView3;
        viewArr2[4] = imageView3;
        r0.repeatButton.setScaleType(ScaleType.CENTER);
        r0.repeatButton.setPadding(0, 0, AndroidUtilities.dp(8.0f), 0);
        c10889.addView(r0.repeatButton, LayoutHelper.createFrame(50, 48, 51));
        r0.repeatButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SharedConfig.toggleRepeatMode();
                AudioPlayerAlert.this.updateRepeatButton();
            }
        });
        r0.listView = new RecyclerListView(context2) {
            boolean ignoreLayout;

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (!AudioPlayerAlert.this.searchOpenPosition && !AudioPlayerAlert.this.actionBar.isSearchFieldVisible()) {
                    this.ignoreLayout = true;
                    AudioPlayerAlert.this.layoutManager.scrollToPositionWithOffset(AudioPlayerAlert.this.searchOpenPosition, AudioPlayerAlert.this.searchOpenOffset);
                    super.onLayout(false, i, i2, i3, i4);
                    this.ignoreLayout = false;
                    AudioPlayerAlert.this.searchOpenPosition = -1;
                } else if (AudioPlayerAlert.this.scrollToSong) {
                    AudioPlayerAlert.this.scrollToSong = false;
                    z = MediaController.getInstance().getPlayingMessageObject();
                    if (z) {
                        boolean z2;
                        int childCount = AudioPlayerAlert.this.listView.getChildCount();
                        for (int i5 = 0; i5 < childCount; i5++) {
                            View childAt = AudioPlayerAlert.this.listView.getChildAt(i5);
                            if ((childAt instanceof AudioPlayerCell) && ((AudioPlayerCell) childAt).getMessageObject() == z) {
                                if (childAt.getBottom() <= getMeasuredHeight()) {
                                    z2 = true;
                                    if (z2) {
                                        z = AudioPlayerAlert.this.playlist.indexOf(z);
                                        if (z >= false) {
                                            this.ignoreLayout = true;
                                            if (SharedConfig.playOrderReversed) {
                                                AudioPlayerAlert.this.layoutManager.scrollToPosition(z);
                                            } else {
                                                AudioPlayerAlert.this.layoutManager.scrollToPosition(AudioPlayerAlert.this.playlist.size() - z);
                                            }
                                            super.onLayout(false, i, i2, i3, i4);
                                            this.ignoreLayout = false;
                                        }
                                    }
                                }
                                z2 = false;
                                if (z2) {
                                    z = AudioPlayerAlert.this.playlist.indexOf(z);
                                    if (z >= false) {
                                        this.ignoreLayout = true;
                                        if (SharedConfig.playOrderReversed) {
                                            AudioPlayerAlert.this.layoutManager.scrollToPosition(AudioPlayerAlert.this.playlist.size() - z);
                                        } else {
                                            AudioPlayerAlert.this.layoutManager.scrollToPosition(z);
                                        }
                                        super.onLayout(false, i, i2, i3, i4);
                                        this.ignoreLayout = false;
                                    }
                                }
                            }
                        }
                        z2 = false;
                        if (z2) {
                            z = AudioPlayerAlert.this.playlist.indexOf(z);
                            if (z >= false) {
                                this.ignoreLayout = true;
                                if (SharedConfig.playOrderReversed) {
                                    AudioPlayerAlert.this.layoutManager.scrollToPosition(z);
                                } else {
                                    AudioPlayerAlert.this.layoutManager.scrollToPosition(AudioPlayerAlert.this.playlist.size() - z);
                                }
                                super.onLayout(false, i, i2, i3, i4);
                                this.ignoreLayout = false;
                            }
                        }
                    }
                }
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected boolean allowSelectChildAtPosition(float f, float f2) {
                AudioPlayerAlert.this.playerLayout.getY();
                AudioPlayerAlert.this.playerLayout.getMeasuredHeight();
                if (AudioPlayerAlert.this.playerLayout != null) {
                    if (f2 <= AudioPlayerAlert.this.playerLayout.getY() + ((float) AudioPlayerAlert.this.playerLayout.getMeasuredHeight())) {
                        return false;
                    }
                }
                return true;
            }

            public boolean drawChild(Canvas canvas, View view, long j) {
                canvas.save();
                canvas.clipRect(0, (AudioPlayerAlert.this.actionBar != null ? AudioPlayerAlert.this.actionBar.getMeasuredHeight() : 0) + AndroidUtilities.dp(50.0f), getMeasuredWidth(), getMeasuredHeight());
                view = super.drawChild(canvas, view, j);
                canvas.restore();
                return view;
            }
        };
        r0.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        r0.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        r0.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        r0.listView.setHorizontalScrollBarEnabled(false);
        r0.listView.setVerticalScrollBarEnabled(false);
        r0.containerView.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = r0.listView;
        Adapter listAdapter = new ListAdapter(context2);
        r0.listAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        r0.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        r0.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if ((view instanceof AudioPlayerCell) != 0) {
                    ((AudioPlayerCell) view).didPressedButton();
                }
            }
        });
        r0.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && AudioPlayerAlert.this.searching != null && AudioPlayerAlert.this.searchWas != null) {
                    AndroidUtilities.hideKeyboard(AudioPlayerAlert.this.getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                AudioPlayerAlert.this.updateLayout();
            }
        });
        r0.playlist = MediaController.getInstance().getPlaylist();
        r0.listAdapter.notifyDataSetChanged();
        r0.containerView.addView(r0.playerLayout, LayoutHelper.createFrame(-1, 178.0f));
        r0.containerView.addView(r0.shadow2, LayoutHelper.createFrame(-1, 3.0f));
        r0.containerView.addView(r0.placeholderImageView, LayoutHelper.createFrame(40, 40.0f, 51, 17.0f, 19.0f, 0.0f, 0.0f));
        r0.containerView.addView(r0.shadow, LayoutHelper.createFrame(-1, 3.0f));
        r0.containerView.addView(r0.actionBar);
        updateTitle(false);
        updateRepeatButton();
        updateShuffleButton();
    }

    @Keep
    public void setFullAnimationProgress(float f) {
        this.fullAnimationProgress = f;
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp(20.0f * (1.0f - this.fullAnimationProgress)));
        f = (this.thumbMaxScale * this.fullAnimationProgress) + 1.0f;
        this.placeholderImageView.setScaleX(f);
        this.placeholderImageView.setScaleY(f);
        this.placeholderImageView.getTranslationY();
        this.placeholderImageView.setTranslationX(((float) this.thumbMaxX) * this.fullAnimationProgress);
        this.placeholderImageView.setTranslationY(this.startTranslation + ((this.endTranslation - this.startTranslation) * this.fullAnimationProgress));
        this.playerLayout.setTranslationY(this.panelStartTranslation + ((this.panelEndTranslation - this.panelStartTranslation) * this.fullAnimationProgress));
        this.shadow2.setTranslationY((this.panelStartTranslation + ((this.panelEndTranslation - this.panelStartTranslation) * this.fullAnimationProgress)) + ((float) this.playerLayout.getMeasuredHeight()));
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

    private void onSubItemClick(int r21) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r20 = this;
        r1 = r20;
        r2 = r21;
        r3 = org.telegram.messenger.MediaController.getInstance();
        r3 = r3.getPlayingMessageObject();
        if (r3 == 0) goto L_0x02a9;
    L_0x000e:
        r4 = r1.parentActivity;
        if (r4 != 0) goto L_0x0014;
    L_0x0012:
        goto L_0x02a9;
    L_0x0014:
        r4 = 3;
        r5 = 1;
        if (r2 != r5) goto L_0x0053;
    L_0x0018:
        r2 = org.telegram.messenger.UserConfig.selectedAccount;
        r6 = r1.currentAccount;
        if (r2 == r6) goto L_0x0025;
    L_0x001e:
        r2 = r1.parentActivity;
        r6 = r1.currentAccount;
        r2.switchToAccount(r6, r5);
    L_0x0025:
        r2 = new android.os.Bundle;
        r2.<init>();
        r6 = "onlySelect";
        r2.putBoolean(r6, r5);
        r5 = "dialogsType";
        r2.putInt(r5, r4);
        r4 = new org.telegram.ui.DialogsActivity;
        r4.<init>(r2);
        r2 = new java.util.ArrayList;
        r2.<init>();
        r2.add(r3);
        r3 = new org.telegram.ui.Components.AudioPlayerAlert$19;
        r3.<init>(r2);
        r4.setDelegate(r3);
        r2 = r1.parentActivity;
        r2.presentFragment(r4);
        r20.dismiss();
        goto L_0x02a8;
    L_0x0053:
        r6 = 2;
        r7 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;
        r8 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
        r9 = 0;
        if (r2 != r6) goto L_0x010c;
    L_0x005d:
        r2 = r3.messageOwner;	 Catch:{ Exception -> 0x0105 }
        r2 = r2.attachPath;	 Catch:{ Exception -> 0x0105 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x0105 }
        if (r2 != 0) goto L_0x0076;	 Catch:{ Exception -> 0x0105 }
    L_0x0067:
        r2 = new java.io.File;	 Catch:{ Exception -> 0x0105 }
        r4 = r3.messageOwner;	 Catch:{ Exception -> 0x0105 }
        r4 = r4.attachPath;	 Catch:{ Exception -> 0x0105 }
        r2.<init>(r4);	 Catch:{ Exception -> 0x0105 }
        r4 = r2.exists();	 Catch:{ Exception -> 0x0105 }
        if (r4 != 0) goto L_0x0077;	 Catch:{ Exception -> 0x0105 }
    L_0x0076:
        r2 = r9;	 Catch:{ Exception -> 0x0105 }
    L_0x0077:
        if (r2 != 0) goto L_0x007f;	 Catch:{ Exception -> 0x0105 }
    L_0x0079:
        r2 = r3.messageOwner;	 Catch:{ Exception -> 0x0105 }
        r2 = org.telegram.messenger.FileLoader.getPathToMessage(r2);	 Catch:{ Exception -> 0x0105 }
    L_0x007f:
        r4 = r2.exists();	 Catch:{ Exception -> 0x0105 }
        if (r4 == 0) goto L_0x00db;	 Catch:{ Exception -> 0x0105 }
    L_0x0085:
        r4 = new android.content.Intent;	 Catch:{ Exception -> 0x0105 }
        r6 = "android.intent.action.SEND";	 Catch:{ Exception -> 0x0105 }
        r4.<init>(r6);	 Catch:{ Exception -> 0x0105 }
        if (r3 == 0) goto L_0x0096;	 Catch:{ Exception -> 0x0105 }
    L_0x008e:
        r3 = r3.getMimeType();	 Catch:{ Exception -> 0x0105 }
        r4.setType(r3);	 Catch:{ Exception -> 0x0105 }
        goto L_0x009b;	 Catch:{ Exception -> 0x0105 }
    L_0x0096:
        r3 = "audio/mp3";	 Catch:{ Exception -> 0x0105 }
        r4.setType(r3);	 Catch:{ Exception -> 0x0105 }
    L_0x009b:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0105 }
        r6 = 24;
        if (r3 < r6) goto L_0x00bc;
    L_0x00a1:
        r3 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x00b2 }
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00b2 }
        r7 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x00b2 }
        r6 = android.support.v4.content.FileProvider.getUriForFile(r6, r7, r2);	 Catch:{ Exception -> 0x00b2 }
        r4.putExtra(r3, r6);	 Catch:{ Exception -> 0x00b2 }
        r4.setFlags(r5);	 Catch:{ Exception -> 0x00b2 }
        goto L_0x00c5;
    L_0x00b2:
        r3 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x0105 }
        r2 = android.net.Uri.fromFile(r2);	 Catch:{ Exception -> 0x0105 }
        r4.putExtra(r3, r2);	 Catch:{ Exception -> 0x0105 }
        goto L_0x00c5;	 Catch:{ Exception -> 0x0105 }
    L_0x00bc:
        r3 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x0105 }
        r2 = android.net.Uri.fromFile(r2);	 Catch:{ Exception -> 0x0105 }
        r4.putExtra(r3, r2);	 Catch:{ Exception -> 0x0105 }
    L_0x00c5:
        r2 = r1.parentActivity;	 Catch:{ Exception -> 0x0105 }
        r3 = "ShareFile";	 Catch:{ Exception -> 0x0105 }
        r5 = NUM; // 0x7f0c05ef float:1.8612273E38 double:1.053098149E-314;	 Catch:{ Exception -> 0x0105 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);	 Catch:{ Exception -> 0x0105 }
        r3 = android.content.Intent.createChooser(r4, r3);	 Catch:{ Exception -> 0x0105 }
        r4 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;	 Catch:{ Exception -> 0x0105 }
        r2.startActivityForResult(r3, r4);	 Catch:{ Exception -> 0x0105 }
        goto L_0x02a8;	 Catch:{ Exception -> 0x0105 }
    L_0x00db:
        r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;	 Catch:{ Exception -> 0x0105 }
        r3 = r1.parentActivity;	 Catch:{ Exception -> 0x0105 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0105 }
        r3 = "AppName";	 Catch:{ Exception -> 0x0105 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r8);	 Catch:{ Exception -> 0x0105 }
        r2.setTitle(r3);	 Catch:{ Exception -> 0x0105 }
        r3 = "OK";	 Catch:{ Exception -> 0x0105 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r7);	 Catch:{ Exception -> 0x0105 }
        r2.setPositiveButton(r3, r9);	 Catch:{ Exception -> 0x0105 }
        r3 = "PleaseDownload";	 Catch:{ Exception -> 0x0105 }
        r4 = NUM; // 0x7f0c051d float:1.8611847E38 double:1.053098045E-314;	 Catch:{ Exception -> 0x0105 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0105 }
        r2.setMessage(r3);	 Catch:{ Exception -> 0x0105 }
        r2.show();	 Catch:{ Exception -> 0x0105 }
        goto L_0x02a8;
    L_0x0105:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);
        goto L_0x02a8;
    L_0x010c:
        r6 = 0;
        if (r2 != r4) goto L_0x0228;
    L_0x010f:
        r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r4 = r1.parentActivity;
        r2.<init>(r4);
        r4 = "AppName";
        r4 = org.telegram.messenger.LocaleController.getString(r4, r8);
        r2.setTitle(r4);
        r4 = new boolean[r5];
        r10 = r3.getDialogId();
        r8 = (int) r10;
        if (r8 == 0) goto L_0x0209;
    L_0x0128:
        if (r8 <= 0) goto L_0x013a;
    L_0x012a:
        r10 = r1.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r8 = java.lang.Integer.valueOf(r8);
        r8 = r10.getUser(r8);
        r10 = r9;
        goto L_0x014b;
    L_0x013a:
        r10 = r1.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r8 = -r8;
        r8 = java.lang.Integer.valueOf(r8);
        r8 = r10.getChat(r8);
        r10 = r8;
        r8 = r9;
    L_0x014b:
        if (r8 != 0) goto L_0x0153;
    L_0x014d:
        r11 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r11 != 0) goto L_0x0209;
    L_0x0153:
        r11 = r1.currentAccount;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11);
        r11 = r11.getCurrentTime();
        if (r8 == 0) goto L_0x016d;
    L_0x015f:
        r12 = r8.id;
        r13 = r1.currentAccount;
        r13 = org.telegram.messenger.UserConfig.getInstance(r13);
        r13 = r13.getClientUserId();
        if (r12 != r13) goto L_0x016f;
    L_0x016d:
        if (r10 == 0) goto L_0x0209;
    L_0x016f:
        r12 = r3.messageOwner;
        r12 = r12.action;
        if (r12 == 0) goto L_0x017d;
    L_0x0175:
        r12 = r3.messageOwner;
        r12 = r12.action;
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r12 == 0) goto L_0x0209;
    L_0x017d:
        r12 = r3.isOut();
        if (r12 == 0) goto L_0x0209;
    L_0x0183:
        r12 = r3.messageOwner;
        r12 = r12.date;
        r11 = r11 - r12;
        r12 = 172800; // 0x2a300 float:2.42144E-40 double:8.53745E-319;
        if (r11 > r12) goto L_0x0209;
    L_0x018d:
        r11 = new android.widget.FrameLayout;
        r12 = r1.parentActivity;
        r11.<init>(r12);
        r12 = new org.telegram.ui.Cells.CheckBoxCell;
        r13 = r1.parentActivity;
        r12.<init>(r13, r5);
        r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6);
        r12.setBackgroundDrawable(r13);
        if (r10 == 0) goto L_0x01b3;
    L_0x01a4:
        r5 = "DeleteForAll";
        r8 = NUM; // 0x7f0c01f7 float:1.8610212E38 double:1.053097647E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r8);
        r8 = "";
        r12.setText(r5, r8, r6, r6);
        goto L_0x01c9;
    L_0x01b3:
        r10 = "DeleteForUser";
        r13 = NUM; // 0x7f0c01f8 float:1.8610214E38 double:1.0530976475E-314;
        r5 = new java.lang.Object[r5];
        r8 = org.telegram.messenger.UserObject.getFirstName(r8);
        r5[r6] = r8;
        r5 = org.telegram.messenger.LocaleController.formatString(r10, r13, r5);
        r8 = "";
        r12.setText(r5, r8, r6, r6);
    L_0x01c9:
        r5 = org.telegram.messenger.LocaleController.isRTL;
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r10 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        if (r5 == 0) goto L_0x01d6;
    L_0x01d1:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r10);
        goto L_0x01da;
    L_0x01d6:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r8);
    L_0x01da:
        r13 = org.telegram.messenger.LocaleController.isRTL;
        if (r13 == 0) goto L_0x01e3;
    L_0x01de:
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        goto L_0x01e7;
    L_0x01e3:
        r8 = org.telegram.messenger.AndroidUtilities.dp(r10);
    L_0x01e7:
        r12.setPadding(r5, r6, r8, r6);
        r13 = -1;
        r14 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r15 = 51;
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r19 = 0;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19);
        r11.addView(r12, r5);
        r5 = new org.telegram.ui.Components.AudioPlayerAlert$20;
        r5.<init>(r4);
        r12.setOnClickListener(r5);
        r2.setView(r11);
    L_0x0209:
        r5 = "OK";
        r5 = org.telegram.messenger.LocaleController.getString(r5, r7);
        r6 = new org.telegram.ui.Components.AudioPlayerAlert$21;
        r6.<init>(r3, r4);
        r2.setPositiveButton(r5, r6);
        r3 = "Cancel";
        r4 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r2.setNegativeButton(r3, r9);
        r2.show();
        goto L_0x02a8;
    L_0x0228:
        r4 = 4;
        if (r2 != r4) goto L_0x02a8;
    L_0x022b:
        r2 = org.telegram.messenger.UserConfig.selectedAccount;
        r4 = r1.currentAccount;
        if (r2 == r4) goto L_0x0238;
    L_0x0231:
        r2 = r1.parentActivity;
        r4 = r1.currentAccount;
        r2.switchToAccount(r4, r5);
    L_0x0238:
        r2 = new android.os.Bundle;
        r2.<init>();
        r7 = r3.getDialogId();
        r4 = (int) r7;
        r9 = 32;
        r7 = r7 >> r9;
        r7 = (int) r7;
        if (r4 == 0) goto L_0x0280;
    L_0x0248:
        if (r7 != r5) goto L_0x0250;
    L_0x024a:
        r5 = "chat_id";
        r2.putInt(r5, r4);
        goto L_0x0285;
    L_0x0250:
        if (r4 <= 0) goto L_0x0258;
    L_0x0252:
        r5 = "user_id";
        r2.putInt(r5, r4);
        goto L_0x0285;
    L_0x0258:
        if (r4 >= 0) goto L_0x0285;
    L_0x025a:
        r5 = r1.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r7 = -r4;
        r7 = java.lang.Integer.valueOf(r7);
        r5 = r5.getChat(r7);
        if (r5 == 0) goto L_0x0279;
    L_0x026b:
        r7 = r5.migrated_to;
        if (r7 == 0) goto L_0x0279;
    L_0x026f:
        r7 = "migrated_to";
        r2.putInt(r7, r4);
        r4 = r5.migrated_to;
        r4 = r4.channel_id;
        r4 = -r4;
    L_0x0279:
        r5 = "chat_id";
        r4 = -r4;
        r2.putInt(r5, r4);
        goto L_0x0285;
    L_0x0280:
        r4 = "enc_id";
        r2.putInt(r4, r7);
    L_0x0285:
        r4 = "message_id";
        r3 = r3.getId();
        r2.putInt(r4, r3);
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r3);
        r4 = org.telegram.messenger.NotificationCenter.closeChats;
        r5 = new java.lang.Object[r6];
        r3.postNotificationName(r4, r5);
        r3 = r1.parentActivity;
        r4 = new org.telegram.ui.ChatActivity;
        r4.<init>(r2);
        r3.presentFragment(r4, r6, r6);
        r20.dismiss();
    L_0x02a8:
        return;
    L_0x02a9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.onSubItemClick(int):void");
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
        return C0542C.PRIORITY_DOWNLOAD;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AudioPlayerCell audioPlayerCell;
        MessageObject messageObject;
        if (!(i == NotificationCenter.messagePlayingDidStarted || i == NotificationCenter.messagePlayingPlayStateChanged)) {
            if (i != NotificationCenter.messagePlayingDidReset) {
                if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                    i = MediaController.getInstance().getPlayingMessageObject();
                    if (!(i == 0 || i.isMusic() == 0)) {
                        updateProgress(i);
                    }
                } else if (i == NotificationCenter.musicDidLoaded) {
                    this.playlist = MediaController.getInstance().getPlaylist();
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        }
        boolean z = true;
        if (i != NotificationCenter.messagePlayingDidReset || ((Boolean) objArr[1]).booleanValue() == 0) {
            z = false;
        }
        updateTitle(z);
        if (i != NotificationCenter.messagePlayingDidReset) {
            if (i != NotificationCenter.messagePlayingPlayStateChanged) {
                if (i == NotificationCenter.messagePlayingDidStarted && ((MessageObject) objArr[0]).eventId == 0) {
                    i = this.listView.getChildCount();
                    for (i2 = 0; i2 < i; i2++) {
                        objArr = this.listView.getChildAt(i2);
                        if (objArr instanceof AudioPlayerCell) {
                            audioPlayerCell = (AudioPlayerCell) objArr;
                            messageObject = audioPlayerCell.getMessageObject();
                            if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                                audioPlayerCell.updateButtonState(false);
                            }
                        }
                    }
                }
                return;
            }
        }
        i = this.listView.getChildCount();
        for (i2 = 0; i2 < i; i2++) {
            objArr = this.listView.getChildAt(i2);
            if (objArr instanceof AudioPlayerCell) {
                audioPlayerCell = (AudioPlayerCell) objArr;
                messageObject = audioPlayerCell.getMessageObject();
                if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                    audioPlayerCell.updateButtonState(false);
                }
            }
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
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if ((!this.inFullSize || this.scrollOffsetY > this.actionBar.getMeasuredHeight()) && !this.searchWas) {
                    if (this.actionBar.getTag() != null) {
                        if (this.actionBarAnimation != null) {
                            this.actionBarAnimation.cancel();
                        }
                        this.actionBar.setTag(null);
                        this.actionBarAnimation = new AnimatorSet();
                        animatorSet = this.actionBarAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                        this.actionBarAnimation.setDuration(180);
                        this.actionBarAnimation.start();
                    }
                } else if (this.actionBar.getTag() == null) {
                    if (this.actionBarAnimation != null) {
                        this.actionBarAnimation.cancel();
                    }
                    this.actionBar.setTag(Integer.valueOf(1));
                    this.actionBarAnimation = new AnimatorSet();
                    animatorSet = this.actionBarAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{1.0f});
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoaded);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public void onBackPressed() {
        if (this.actionBar == null || !this.actionBar.isSearchFieldVisible()) {
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
        if (SharedConfig.shuffleMusic) {
            mutate = getContext().getResources().getDrawable(C0446R.drawable.pl_shuffle).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
            this.shuffleButton.setIcon(mutate);
        } else {
            mutate = getContext().getResources().getDrawable(C0446R.drawable.music_reverse).mutate();
            if (SharedConfig.playOrderReversed) {
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
            } else {
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_button), Mode.MULTIPLY));
            }
            this.shuffleButton.setIcon(mutate);
        }
        this.playOrderButtons[0].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.playOrderReversed ? Theme.key_player_buttonActive : Theme.key_player_button), Mode.MULTIPLY));
        this.playOrderButtons[1].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.shuffleMusic ? Theme.key_player_buttonActive : Theme.key_player_button), Mode.MULTIPLY));
    }

    private void updateRepeatButton() {
        int i = SharedConfig.repeatMode;
        if (i == 0) {
            this.repeatButton.setImageResource(C0446R.drawable.pl_repeat);
            this.repeatButton.setTag(Theme.key_player_button);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_button), Mode.MULTIPLY));
        } else if (i == 1) {
            this.repeatButton.setImageResource(C0446R.drawable.pl_repeat);
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
        } else if (i == 2) {
            this.repeatButton.setImageResource(C0446R.drawable.pl_repeat1);
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
        }
    }

    private void updateProgress(MessageObject messageObject) {
        if (this.seekBarView != null) {
            if (!this.seekBarView.isDragging()) {
                this.seekBarView.setProgress(messageObject.audioProgress);
                this.seekBarView.setBufferedProgress(messageObject.bufferedProgress);
            }
            if (this.lastTime != messageObject.audioProgressSec) {
                this.lastTime = messageObject.audioProgressSec;
                this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(messageObject.audioProgressSec / 60), Integer.valueOf(messageObject.audioProgressSec % 60)}));
            }
        }
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        File file = null;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
            File file2 = new File(messageObject.messageOwner.attachPath);
            if (file2.exists()) {
                file = file2;
            }
        }
        if (file == null) {
            file = FileLoader.getPathToMessage(messageObject.messageOwner);
        }
        boolean z = SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic();
        if (file.exists() || z) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        messageObject = messageObject.getFileName();
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(messageObject, this);
        messageObject = ImageLoader.getInstance().getFileProgress(messageObject);
        this.progressView.setProgress(messageObject != null ? messageObject.floatValue() : null, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void updateTitle(boolean z) {
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if ((playingMessageObject == null && z) || (playingMessageObject != null && !playingMessageObject.isMusic())) {
            dismiss();
        } else if (playingMessageObject != null) {
            if (playingMessageObject.eventId != 0) {
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
            if (MediaController.getInstance().isMessagePaused()) {
                this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), C0446R.drawable.pl_play, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
            } else {
                this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), C0446R.drawable.pl_pause, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
            }
            z = playingMessageObject.getMusicTitle();
            CharSequence musicAuthor = playingMessageObject.getMusicAuthor();
            this.titleTextView.setText(z);
            this.authorTextView.setText(musicAuthor);
            this.actionBar.setTitle(z);
            this.actionBar.setSubtitle(musicAuthor);
            z = MediaController.getInstance().getAudioInfo();
            if (!z || z.getCover() == null) {
                this.hasNoCover = true;
                this.placeholderImageView.invalidate();
                this.placeholderImageView.setImageDrawable(null);
            } else {
                this.hasNoCover = false;
                this.placeholderImageView.setImageBitmap(z.getCover());
            }
            if (this.durationTextView) {
                z = playingMessageObject.getDuration();
                TextView textView = this.durationTextView;
                if (z) {
                    z = String.format("%d:%02d", new Object[]{Integer.valueOf(z / 60), Integer.valueOf(z % 60)});
                } else {
                    z = "-:--";
                }
                textView.setText(z);
            }
        }
    }
}
