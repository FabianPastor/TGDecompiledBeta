package org.telegram.messenger;

import java.util.HashMap;

public class EmojiData {
    public static final String[][] data;
    public static final char[] dataChars = new char[]{'☮', '✝', '☪', '☸', '✡', '☯', '☦', '⛎', '♈', '♉', '♊', '♋', '♌', '♍', '♎', '♏', '♐', '♑', '♒', '♓', '⚛', '☢', '☣', '✴', '㊙', '㊗', '⛔', '❌', '⭕', '♨', '❗', '❕', '❓', '❔', '‼', '⁉', '⚜', '〽', '⚠', '♻', '❇', '✳', '❎', '✅', '➿', 'Ⓜ', '♿', '▶', '⏸', '⏯', '⏹', '⏺', '⏭', '⏮', '⏩', '⏪', '◀', '⏫', '⏬', '➡', '⬅', '⬆', '⬇', '↗', '↘', '↙', '↖', '↕', '↔', '↪', '↩', '⤴', '⤵', 'ℹ', '〰', '➰', '✔', '➕', '➖', '➗', '✖', '©', '®', '™', '☑', '⚪', '⚫', '▪', '▫', '⬛', '⬜', '◼', '◻', '◾', '◽', '♠', '♣', '♥', '♦', '☺', '☹', '✊', '✌', '✋', '☝', '✍', '⛑', '❤', '❣', '☕', '⚽', '⚾', '⛳', '⛷', '⛸', '⛹', '⌚', '⌨', '☎', '⏱', '⏲', '⏰', '⏳', '⌛', '⚖', '⚒', '⛏', '⚙', '⛓', '⚔', '☠', '⚰', '⚱', '⚗', '⛱', '✉', '✂', '✒', '✏', '✈', '⛵', '⛴', '⚓', '⛽', '⛲', '⛰', '⛺', '⛪', '⛩', '☘', '⭐', '✨', '☄', '☀', '⛅', '☁', '⛈', '⚡', '❄', '☃', '⛄', '☂', '☔'};
    public static final HashMap<Character, Boolean> dataCharsMap = new HashMap(dataChars.length);
    public static final String[][] dataColored;
    public static final String[] emojiColored = new String[]{"👐", "🙌", "👏", "🙏", "👍", "👎", "👊", "✊", "🤛", "🤜", "🤞", "✌", "🤘", "👌", "👈", "👉", "👆", "👇", "☝", "✋", "🤚", "🖐", "🖖", "👋", "🤙", "💪", "🖕", "✍", "🤳", "💅", "👂", "👃", "👶", "👦", "👧", "👨", "👩", "👱‍♀", "👱", "👴", "👵", "👲", "👳‍♀", "👳", "👮‍♀", "👮", "👷‍♀", "👷", "💂‍♀", "💂", "🕵‍♀", "🕵", "👩‍⚕", "👨‍⚕", "👩‍🌾", "👨‍🌾", "👩‍🍳", "👨‍🍳", "👩‍🎓", "👨‍🎓", "👩‍🎤", "👨‍🎤", "👩‍🏫", "👨‍🏫", "👩‍🏭", "👨‍🏭", "👩‍💻", "👨‍💻", "👩‍💼", "👨‍💼", "👩‍🔧", "👨‍🔧", "👩‍🔬", "👨‍🔬", "👩‍🎨", "👨‍🎨", "👩‍🚒", "👨‍🚒", "👩‍✈", "👨‍✈", "👩‍🚀", "👨‍🚀", "👩‍⚖", "👨‍⚖", "🤶", "🎅", "👸", "🤴", "👰", "🤵", "👼", "🤰", "🙇‍♀", "🙇", "💁", "💁‍♂", "🙅", "🙅‍♂", "🙆", "🙆‍♂", "🙋", "🙋‍♂", "🤦‍♀", "🤦‍♂", "🤷‍♀", "🤷‍♂", "🙎", "🙎‍♂", "🙍", "🙍‍♂", "💇", "💇‍♂", "💆", "💆‍♂", "🕴", "💃", "🕺", "🚶‍♀", "🚶", "🏃‍♀", "🏃", "🏋‍♀", "🏋", "🤸‍♀", "🤸‍♂", "⛹‍♀", "⛹", "🤾‍♀", "🤾‍♂", "🏌‍♀", "🏌", "🏄‍♀", "🏄", "🏊‍♀", "🏊", "🤽‍♀", "🤽‍♂", "🏄", "🏊‍♀", "🏊", "🤽‍♀", "🤽‍♂", "🚣‍♀", "🚣", "🏇", "🚴‍♀", "🚴", "🚵‍♀", "🚵", "🤹‍♀", "🤹‍♂", "🛀"};
    public static final HashMap<String, Boolean> emojiColoredMap = new HashMap(emojiColored.length);
    public static final char[] emojiToFE0F = new char[]{'⭐', '☀', '⛅', '☁', '⚡', '❄', '⛄', '☔', '✈', '⛵', '⚓', '⛽', '⛲', '⛺', '⛪', '☕', '⚽', '⚾', '⛳', '⌚', '☎', '⌛', '✉', '✂', '✒', '✏', '♈', '♉', '♊', '♋', '♌', '♍', '♎', '♏', '♐', '♑', '♒', '♓', '✴', '㊙', '㊗', '⛔', '⭕', '♨', '❗', '‼', '⁉', '〽', '⚠', '♻', '❇', '✳', 'Ⓜ', '♿', '▶', '◀', '➡', '⬅', '⬆', '⬇', '↗', '↘', '↙', '↖', '↕', '↔', '↪', '↩', '⤴', '⤵', 'ℹ', '✔', '✖', '☑', '⚪', '⚫', '▪', '▫', '⬛', '⬜', '◼', '◻', '◾', '◽', '♠', '♣', '♥', '♦', '☺', '☹', '✌', '☝', '❤'};
    public static final HashMap<Character, Boolean> emojiToFE0FMap = new HashMap(emojiToFE0F.length);

    static {
        r1 = new String[5][];
        r1[0] = new String[]{"😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "☺", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "😘", "😗", "😙", "😚", "😋", "😜", "😝", "😛", "🤑", "🤗", "🤓", "😎", "🤡", "🤠", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹", "😣", "😖", "😫", "😩", "😤", "😠", "😡", "😶", "😐", "😑", "😯", "😦", "😧", "😮", "😲", "😵", "😳", "😱", "😨", "😰", "😢", "😥", "🤤", "😭", "😓", "😪", "😴", "🙄", "🤔", "🤥", "😬", "🤐", "🤢", "🤧", "😷", "🤒", "🤕", "😈", "👿", "👹", "👺", "💩", "👻", "💀", "☠", "👽", "👾", "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "👐", "🙌", "👏", "🙏", "🤝", "👍", "👎", "👊", "✊", "🤛", "🤜", "🤞", "✌", "🤘", "👌", "👈", "👉", "👆", "👇", "☝", "✋", "🤚", "🖐", "🖖", "👋", "🤙", "💪", "🖕", "✍", "🤳", "💅", "💍", "💄", "💋", "👄", "👅", "👂", "👃", "👣", "👁", "👀", "🗣", "👤", "👥", "👶", "👦", "👧", "👨", "👩", "👱‍♀", "👱", "👴", "👵", "👲", "👳‍♀", "👳", "👮‍♀", "👮", "👷‍♀", "👷", "💂‍♀", "💂", "🕵‍♀", "🕵", "👩‍⚕", "👨‍⚕", "👩‍🌾", "👨‍🌾", "👩‍🍳", "👨‍🍳", "👩‍🎓", "👨‍🎓", "👩‍🎤", "👨‍🎤", "👩‍🏫", "👨‍🏫", "👩‍🏭", "👨‍🏭", "👩‍💻", "👨‍💻", "👩‍💼", "👨‍💼", "👩‍🔧", "👨‍🔧", "👩‍🔬", "👨‍🔬", "👩‍🎨", "👨‍🎨", "👩‍🚒", "👨‍🚒", "👩‍✈", "👨‍✈", "👩‍🚀", "👨‍🚀", "👩‍⚖", "👨‍⚖", "🤶", "🎅", "👸", "🤴", "👰", "🤵", "👼", "🤰", "🙇‍♀", "🙇", "💁", "💁‍♂", "🙅", "🙅‍♂", "🙆", "🙆‍♂", "🙋", "🙋‍♂", "🤦‍♀", "🤦‍♂", "🤷‍♀", "🤷‍♂", "🙎", "🙎‍♂", "🙍", "🙍‍♂", "💇", "💇‍♂", "💆", "💆‍♂", "🕴", "💃", "🕺", "👯", "👯‍♂", "🚶‍♀", "🚶", "🏃‍♀", "🏃", "👫", "👭", "👬", "💑", "👩‍❤‍👩", "👨‍❤‍👨", "💏", "👩‍❤‍💋‍👩", "👨‍❤‍💋‍👨", "👪", "👨‍👩‍👧", "👨‍👩‍👧‍👦", "👨‍👩‍👦‍👦", "👨‍👩‍👧‍👧", "👩‍👩‍👦", "👩‍👩‍👧", "👩‍👩‍👧‍👦", "👩‍👩‍👦‍👦", "👩‍👩‍👧‍👧", "👨‍👨‍👦", "👨‍👨‍👧", "👨‍👨‍👧‍👦", "👨‍👨‍👦‍👦", "👨‍👨‍👧‍👧", "👩‍👦", "👩‍👧", "👩‍👧‍👦", "👩‍👦‍👦", "👩‍👧‍👧", "👨‍👦", "👨‍👧", "👨‍👧‍👦", "👨‍👦‍👦", "👨‍👧‍👧", "👚", "👕", "👖", "👔", "👗", "👙", "👘", "👠", "👡", "👢", "👞", "👟", "👒", "🎩", "🎓", "👑", "⛑", "🎒", "👝", "👛", "👜", "💼", "👓", "🕶", "🌂", "☂", "❤", "💛", "💚", "💙", "💜", "🖤", "💔", "❣", "💕", "💞", "💓", "💗", "💖", "💘", "💝"};
        r1[1] = null;
        r1[2] = new String[]{"🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🍍", "🥝", "🥑", "🍅", "🍆", "🥒", "🥕", "🌽", "🌶", "🥔", "🍠", "🌰", "🥜", "🍯", "🥐", "🍞", "🥖", "🧀", "🥚", "🍳", "🥓", "🥞", "🍤", "🍗", "🍖", "🍕", "🌭", "🍔", "🍟", "🥙", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍥", "🍣", "🍱", "🍛", "🍙", "🍚", "🍘", "🍢", "🍡", "🍧", "🍨", "🍦", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🍩", "🍪", "🥛", "🍼", "☕", "🍵", "🍶", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹", "🍾", "🥄", "🍴", "🍽", "⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🏉", "🎱", "🏓", "🏸", "🥅", "🏒", "🏑", "🏏", "⛳", "🏹", "🎣", "🥊", "🥋", "⛸", "🎿", "⛷", "🏂", "🏋‍♀", "🏋", "🤺", "🤼‍♀", "🤼‍♂", "🤸‍♀", "🤸‍♂", "⛹‍♀", "⛹", "🤾‍♀", "🤾‍♂", "🏌‍♀", "🏌", "🏄‍♀", "🏄", "🏊‍♀", "🏊", "🤽‍♀", "🤽‍♂", "🚣‍♀", "🚣", "🏇", "🚴‍♀", "🚴", "🚵‍♀", "🚵", "🎽", "🏅", "🎖", "🥇", "🥈", "🥉", "🏆", "🏵", "🎗", "🎫", "🎟", "🎪", "🤹‍♀", "🤹‍♂", "🎭", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹", "🥁", "🎷", "🎺", "🎸", "🎻", "🎲", "🎯", "🎳", "🎮", "🎰"};
        r1[3] = null;
        r1[4] = new String[]{"💟", "☮", "✝", "☪", "🕉", "☸", "✡", "🔯", "🕎", "☯", "☦", "🛐", "⛎", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓", "🆔", "⚛", "🉑", "☢", "☣", "📴", "📳", "🈶", "🈚", "🈸", "🈺", "🈷", "✴", "🆚", "💮", "🉐", "㊙", "㊗", "🈴", "🈵", "🈹", "🈲", "🅰", "🅱", "🆎", "🆑", "🅾", "🆘", "❌", "⭕", "🛑", "⛔", "📛", "🚫", "💯", "💢", "♨", "🚷", "🚯", "🚳", "🚱", "🔞", "📵", "🚭", "❗", "❕", "❓", "❔", "‼", "⁉", "🔅", "🔆", "〽", "⚠", "🚸", "🔱", "⚜", "🔰", "♻", "✅", "🈯", "💹", "❇", "✳", "❎", "🌐", "💠", "Ⓜ", "🌀", "💤", "🏧", "🚾", "♿", "🅿", "🈳", "🈂", "🛂", "🛃", "🛄", "🛅", "🚹", "🚺", "🚼", "🚻", "🚮", "🎦", "📶", "🈁", "🔣", "ℹ", "🔤", "🔡", "🔠", "🆖", "🆗", "🆙", "🆒", "🆕", "🆓", "0⃣", "1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "🔟", "🔢", "#⃣", "*⃣", "▶", "⏸", "⏯", "⏹", "⏺", "⏭", "⏮", "⏩", "⏪", "⏫", "⏬", "◀", "🔼", "🔽", "➡", "⬅", "⬆", "⬇", "↗", "↘", "↙", "↖", "↕", "↔", "↪", "↩", "⤴", "⤵", "🔀", "🔁", "🔂", "🔄", "🔃", "🎵", "🎶", "➕", "➖", "➗", "✖", "💲", "💱", "™", "©", "®", "〰", "➰", "➿", "🔚", "🔙", "🔛", "🔝", "🔜", "✔", "☑", "🔘", "⚪", "⚫", "🔴", "🔵", "🔺", "🔻", "🔸", "🔹", "🔶", "🔷", "🔳", "🔲", "▪", "▫", "◾", "◽", "◼", "◻", "⬛", "⬜", "🔈", "🔇", "🔉", "🔊", "🔔", "🔕", "📣", "📢", "👁‍🗨", "💬", "💭", "🗯", "♠", "♣", "♥", "♦", "🃏", "🎴", "🀄", "🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚", "🕛", "🕜", "🕝", "🕞", "🕟", "🕠", "🕡", "🕢", "🕣", "🕤", "🕥", "🕦", "🕧", "⌚", "📱", "📲", "💻", "⌨", "🖥", "🖨", "🖱", "🖲", "🕹", "🗜", "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹", "🎥", "📽", "🎞", "📞", "☎", "📟", "📠", "📺", "📻", "🎙", "🎚", "🎛", "⏱", "⏲", "⏰", "🕰", "⌛", "⏳", "📡", "🔋", "🔌", "💡", "🔦", "🕯", "🗑", "🛢", "💸", "💵", "💴", "💶", "💷", "💰", "💳", "💎", "⚖", "🔧", "🔨", "⚒", "🛠", "⛏", "🔩", "⚙", "⛓", "🔫", "💣", "🔪", "🗡", "⚔", "🛡", "🚬", "⚰", "⚱", "🏺", "🔮", "📿", "💈", "⚗", "🔭", "🔬", "🕳", "💊", "💉", "🌡", "🚽", "🚰", "🚿", "🛁", "🛀", "🛎", "🔑", "🗝", "🚪", "🛋", "🛏", "🛌", "🖼", "🛍", "🛒", "🎁", "🎈", "🎏", "🎀", "🎊", "🎉", "🎎", "🏮", "🎐", "✉", "📩", "📨", "📧", "💌", "📥", "📤", "📦", "🏷", "📪", "📫", "📬", "📭", "📮", "📯", "📜", "📃", "📄", "📑", "📊", "📈", "📉", "🗒", "🗓", "📆", "📅", "📇", "🗃", "🗳", "🗄", "📋", "📁", "📂", "🗂", "🗞", "📰", "📓", "📔", "📒", "📕", "📗", "📘", "📙", "📚", "📖", "🔖", "🔗", "📎", "🖇", "📐", "📏", "📌", "📍", "✂", "🖊", "🖋", "✒", "🖌", "🖍", "📝", "✏", "🔍", "🔎", "🔏", "🔐", "🔒", "🔓"};
        dataColored = r1;
        r1 = new String[5][];
        r1[0] = new String[]{"😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "☺", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "😘", "😗", "😙", "😚", "😋", "😜", "😝", "😛", "🤑", "🤗", "🤓", "😎", "🤡", "🤠", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹", "😣", "😖", "😫", "😩", "😤", "😠", "😡", "😶", "😐", "😑", "😯", "😦", "😧", "😮", "😲", "😵", "😳", "😱", "😨", "😰", "😢", "😥", "🤤", "😭", "😓", "😪", "😴", "🙄", "🤔", "🤥", "😬", "🤐", "🤢", "🤧", "😷", "🤒", "🤕", "😈", "👿", "👹", "👺", "💩", "👻", "💀", "☠", "👽", "👾", "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "👐", "👐🏻", "👐🏼", "👐🏽", "👐🏾", "👐🏿", "🙌", "🙌🏻", "🙌🏼", "🙌🏽", "🙌🏾", "🙌🏿", "👏", "👏🏻", "👏🏼", "👏🏽", "👏🏾", "👏🏿", "🙏", "🙏🏻", "🙏🏼", "🙏🏽", "🙏🏾", "🙏🏿", "🤝", "👍", "👍🏻", "👍🏼", "👍🏽", "👍🏾", "👍🏿", "👎", "👎🏻", "👎🏼", "👎🏽", "👎🏾", "👎🏿", "👊", "👊🏻", "👊🏼", "👊🏽", "👊🏾", "👊🏿", "✊", "✊🏻", "✊🏼", "✊🏽", "✊🏾", "✊🏿", "🤛", "🤛🏻", "🤛🏼", "🤛🏽", "🤛🏾", "🤛🏿", "🤜", "🤜🏻", "🤜🏼", "🤜🏽", "🤜🏾", "🤜🏿", "🤞", "🤞🏻", "🤞🏼", "🤞🏽", "🤞🏾", "🤞🏿", "✌", "✌🏻", "✌🏼", "✌🏽", "✌🏾", "✌🏿", "🤘", "🤘🏻", "🤘🏼", "🤘🏽", "🤘🏾", "🤘🏿", "👌", "👌🏻", "👌🏼", "👌🏽", "👌🏾", "👌🏿", "👈", "👈🏻", "👈🏼", "👈🏽", "👈🏾", "👈🏿", "👉", "👉🏻", "👉🏼", "👉🏽", "👉🏾", "👉🏿", "👆", "👆🏻", "👆🏼", "👆🏽", "👆🏾", "👆🏿", "👇", "👇🏻", "👇🏼", "👇🏽", "👇🏾", "👇🏿", "☝", "☝🏻", "☝🏼", "☝🏽", "☝🏾", "☝🏿", "✋", "✋🏻", "✋🏼", "✋🏽", "✋🏾", "✋🏿", "🤚", "🤚🏻", "🤚🏼", "🤚🏽", "🤚🏾", "🤚🏿", "🖐", "🖐🏻", "🖐🏼", "🖐🏽", "🖐🏾", "🖐🏿", "🖖", "🖖🏻", "🖖🏼", "🖖🏽", "🖖🏾", "🖖🏿", "👋", "👋🏻", "👋🏼", "👋🏽", "👋🏾", "👋🏿", "🤙", "🤙🏻", "🤙🏼", "🤙🏽", "🤙🏾", "🤙🏿", "💪", "💪🏻", "💪🏼", "💪🏽", "💪🏾", "💪🏿", "🖕", "🖕🏻", "🖕🏼", "🖕🏽", "🖕🏾", "🖕🏿", "✍", "✍🏻", "✍🏼", "✍🏽", "✍🏾", "✍🏿", "🤳", "🤳🏻", "🤳🏼", "🤳🏽", "🤳🏾", "🤳🏿", "💅", "💅🏻", "💅🏼", "💅🏽", "💅🏾", "💅🏿", "💍", "💄", "💋", "👄", "👅", "👂", "👂🏻", "👂🏼", "👂🏽", "👂🏾", "👂🏿", "👃", "👃🏻", "👃🏼", "👃🏽", "👃🏾", "👃🏿", "👣", "👁", "👀", "🗣", "👤", "👥", "👶", "👶🏻", "👶🏼", "👶🏽", "👶🏾", "👶🏿", "👦", "👦🏻", "👦🏼", "👦🏽", "👦🏾", "👦🏿", "👧", "👧🏻", "👧🏼", "👧🏽", "👧🏾", "👧🏿", "👨", "👨🏻", "👨🏼", "👨🏽", "👨🏾", "👨🏿", "👩", "👩🏻", "👩🏼", "👩🏽", "👩🏾", "👩🏿", "👱‍♀", "👱🏻‍♀", "👱🏼‍♀", "👱🏽‍♀", "👱🏾‍♀", "👱🏿‍♀", "👱", "👱🏻", "👱🏼", "👱🏽", "👱🏾", "👱🏿", "👴", "👴🏻", "👴🏼", "👴🏽", "👴🏾", "👴🏿", "👵", "👵🏻", "👵🏼", "👵🏽", "👵🏾", "👵🏿", "👲", "👲🏻", "👲🏼", "👲🏽", "👲🏾", "👲🏿", "👳‍♀", "👳🏻‍♀", "👳🏼‍♀", "👳🏽‍♀", "👳🏾‍♀", "👳🏿‍♀", "👳", "👳🏻", "👳🏼", "👳🏽", "👳🏾", "👳🏿", "👮‍♀", "👮🏻‍♀", "👮🏼‍♀", "👮🏽‍♀", "👮🏾‍♀", "👮🏿‍♀", "👮", "👮🏻", "👮🏼", "👮🏽", "👮🏾", "👮🏿", "👷‍♀", "👷🏻‍♀", "👷🏼‍♀", "👷🏽‍♀", "👷🏾‍♀", "👷🏿‍♀", "👷", "👷🏻", "👷🏼", "👷🏽", "👷🏾", "👷🏿", "💂‍♀", "💂🏻‍♀", "💂🏼‍♀", "💂🏽‍♀", "💂🏾‍♀", "💂🏿‍♀", "💂", "💂🏻", "💂🏼", "💂🏽", "💂🏾", "💂🏿", "🕵‍♀", "🕵🏻‍♀", "🕵🏼‍♀", "🕵🏽‍♀", "🕵🏾‍♀", "🕵🏿‍♀", "🕵", "🕵🏻", "🕵🏼", "🕵🏽", "🕵🏾", "🕵🏿", "👩‍⚕", "👩🏻‍⚕", "👩🏼‍⚕", "👩🏽‍⚕", "👩🏾‍⚕", "👩🏿‍⚕", "👨‍⚕", "👨🏻‍⚕", "👨🏼‍⚕", "👨🏽‍⚕", "👨🏾‍⚕", "👨🏿‍⚕", "👩‍🌾", "👩🏻‍🌾", "👩🏼‍🌾", "👩🏽‍🌾", "👩🏾‍🌾", "👩🏿‍🌾", "👨‍🌾", "👨🏻‍🌾", "👨🏼‍🌾", "👨🏽‍🌾", "👨🏾‍🌾", "👨🏿‍🌾", "👩‍🍳", "👩🏻‍🍳", "👩🏼‍🍳", "👩🏽‍🍳", "👩🏾‍🍳", "👩🏿‍🍳", "👨‍🍳", "👨🏻‍🍳", "👨🏼‍🍳", "👨🏽‍🍳", "👨🏾‍🍳", "👨🏿‍🍳", "👩‍🎓", "👩🏻‍🎓", "👩🏼‍🎓", "👩🏽‍🎓", "👩🏾‍🎓", "👩🏿‍🎓", "👨‍🎓", "👨🏻‍🎓", "👨🏼‍🎓", "👨🏽‍🎓", "👨🏾‍🎓", "👨🏿‍🎓", "👩‍🎤", "👩🏻‍🎤", "👩🏼‍🎤", "👩🏽‍🎤", "👩🏾‍🎤", "👩🏿‍🎤", "👨‍🎤", "👨🏻‍🎤", "👨🏼‍🎤", "👨🏽‍🎤", "👨🏾‍🎤", "👨🏿‍🎤", "👩‍🏫", "👩🏻‍🏫", "👩🏼‍🏫", "👩🏽‍🏫", "👩🏾‍🏫", "👩🏿‍🏫", "👨‍🏫", "👨🏻‍🏫", "👨🏼‍🏫", "👨🏽‍🏫", "👨🏾‍🏫", "👨🏿‍🏫", "👩‍🏭", "👩🏻‍🏭", "👩🏼‍🏭", "👩🏽‍🏭", "👩🏾‍🏭", "👩🏿‍🏭", "👨‍🏭", "👨🏻‍🏭", "👨🏼‍🏭", "👨🏽‍🏭", "👨🏾‍🏭", "👨🏿‍🏭", "👩‍💻", "👩🏻‍💻", "👩🏼‍💻", "👩🏽‍💻", "👩🏾‍💻", "👩🏿‍💻", "👨‍💻", "👨🏻‍💻", "👨🏼‍💻", "👨🏽‍💻", "👨🏾‍💻", "👨🏿‍💻", "👩‍💼", "👩🏻‍💼", "👩🏼‍💼", "👩🏽‍💼", "👩🏾‍💼", "👩🏿‍💼", "👨‍💼", "👨🏻‍💼", "👨🏼‍💼", "👨🏽‍💼", "👨🏾‍💼", "👨🏿‍💼", "👩‍🔧", "👩🏻‍🔧", "👩🏼‍🔧", "👩🏽‍🔧", "👩🏾‍🔧", "👩🏿‍🔧", "👨‍🔧", "👨🏻‍🔧", "👨🏼‍🔧", "👨🏽‍🔧", "👨🏾‍🔧", "👨🏿‍🔧", "👩‍🔬", "👩🏻‍🔬", "👩🏼‍🔬", "👩🏽‍🔬", "👩🏾‍🔬", "👩🏿‍🔬", "👨‍🔬", "👨🏻‍🔬", "👨🏼‍🔬", "👨🏽‍🔬", "👨🏾‍🔬", "👨🏿‍🔬", "👩‍🎨", "👩🏻‍🎨", "👩🏼‍🎨", "👩🏽‍🎨", "👩🏾‍🎨", "👩🏿‍🎨", "👨‍🎨", "👨🏻‍🎨", "👨🏼‍🎨", "👨🏽‍🎨", "👨🏾‍🎨", "👨🏿‍🎨", "👩‍🚒", "👩🏻‍🚒", "👩🏼‍🚒", "👩🏽‍🚒", "👩🏾‍🚒", "👩🏿‍🚒", "👨‍🚒", "👨🏻‍🚒", "👨🏼‍🚒", "👨🏽‍🚒", "👨🏾‍🚒", "👨🏿‍🚒", "👩‍✈", "👩🏻‍✈", "👩🏼‍✈", "👩🏽‍✈", "👩🏾‍✈", "👩🏿‍✈", "👨‍✈", "👨🏻‍✈", "👨🏼‍✈", "👨🏽‍✈", "👨🏾‍✈", "👨🏿‍✈", "👩‍🚀", "👩🏻‍🚀", "👩🏼‍🚀", "👩🏽‍🚀", "👩🏾‍🚀", "👩🏿‍🚀", "👨‍🚀", "👨🏻‍🚀", "👨🏼‍🚀", "👨🏽‍🚀", "👨🏾‍🚀", "👨🏿‍🚀", "👩‍⚖", "👩🏻‍⚖", "👩🏼‍⚖", "👩🏽‍⚖", "👩🏾‍⚖", "👩🏿‍⚖", "👨‍⚖", "👨🏻‍⚖", "👨🏼‍⚖", "👨🏽‍⚖", "👨🏾‍⚖", "👨🏿‍⚖", "🤶", "🤶🏻", "🤶🏼", "🤶🏽", "🤶🏾", "🤶🏿", "🎅", "🎅🏻", "🎅🏼", "🎅🏽", "🎅🏾", "🎅🏿", "👸", "👸🏻", "👸🏼", "👸🏽", "👸🏾", "👸🏿", "🤴", "🤴🏻", "🤴🏼", "🤴🏽", "🤴🏾", "🤴🏿", "👰", "👰🏻", "👰🏼", "👰🏽", "👰🏾", "👰🏿", "🤵", "🤵🏻", "🤵🏼", "🤵🏽", "🤵🏾", "🤵🏿", "👼", "👼🏻", "👼🏼", "👼🏽", "👼🏾", "👼🏿", "🤰", "🤰🏻", "🤰🏼", "🤰🏽", "🤰🏾", "🤰🏿", "🙇‍♀", "🙇🏻‍♀", "🙇🏼‍♀", "🙇🏽‍♀", "🙇🏾‍♀", "🙇🏿‍♀", "🙇", "🙇🏻", "🙇🏼", "🙇🏽", "🙇🏾", "🙇🏿", "💁", "💁🏻", "💁🏼", "💁🏽", "💁🏾", "💁🏿", "💁‍♂", "💁🏻‍♂", "💁🏼‍♂", "💁🏽‍♂", "💁🏾‍♂", "💁🏿‍♂", "🙅", "🙅🏻", "🙅🏼", "🙅🏽", "🙅🏾", "🙅🏿", "🙅‍♂", "🙅🏻‍♂", "🙅🏼‍♂", "🙅🏽‍♂", "🙅🏾‍♂", "🙅🏿‍♂", "🙆", "🙆🏻", "🙆🏼", "🙆🏽", "🙆🏾", "🙆🏿", "🙆‍♂", "🙆🏻‍♂", "🙆🏼‍♂", "🙆🏽‍♂", "🙆🏾‍♂", "🙆🏿‍♂", "🙋", "🙋🏻", "🙋🏼", "🙋🏽", "🙋🏾", "🙋🏿", "🙋‍♂", "🙋🏻‍♂", "🙋🏼‍♂", "🙋🏽‍♂", "🙋🏾‍♂", "🙋🏿‍♂", "🤦‍♀", "🤦🏻‍♀", "🤦🏼‍♀", "🤦🏽‍♀", "🤦🏾‍♀", "🤦🏿‍♀", "🤦‍♂", "🤦🏻‍♂", "🤦🏼‍♂", "🤦🏽‍♂", "🤦🏾‍♂", "🤦🏿‍♂", "🤷‍♀", "🤷🏻‍♀", "🤷🏼‍♀", "🤷🏽‍♀", "🤷🏾‍♀", "🤷🏿‍♀", "🤷‍♂", "🤷🏻‍♂", "🤷🏼‍♂", "🤷🏽‍♂", "🤷🏾‍♂", "🤷🏿‍♂", "🙎", "🙎🏻", "🙎🏼", "🙎🏽", "🙎🏾", "🙎🏿", "🙎‍♂", "🙎🏻‍♂", "🙎🏼‍♂", "🙎🏽‍♂", "🙎🏾‍♂", "🙎🏿‍♂", "🙍", "🙍🏻", "🙍🏼", "🙍🏽", "🙍🏾", "🙍🏿", "🙍‍♂", "🙍🏻‍♂", "🙍🏼‍♂", "🙍🏽‍♂", "🙍🏾‍♂", "🙍🏿‍♂", "💇", "💇🏻", "💇🏼", "💇🏽", "💇🏾", "💇🏿", "💇‍♂", "💇🏻‍♂", "💇🏼‍♂", "💇🏽‍♂", "💇🏾‍♂", "💇🏿‍♂", "💆", "💆🏻", "💆🏼", "💆🏽", "💆🏾", "💆🏿", "💆‍♂", "💆🏻‍♂", "💆🏼‍♂", "💆🏽‍♂", "💆🏾‍♂", "💆🏿‍♂", "🕴", "🕴🏻", "🕴🏼", "🕴🏽", "🕴🏾", "🕴🏿", "💃", "💃🏻", "💃🏼", "💃🏽", "💃🏾", "💃🏿", "🕺", "🕺🏻", "🕺🏼", "🕺🏽", "🕺🏾", "🕺🏿", "👯", "👯‍♂", "🚶‍♀", "🚶🏻‍♀", "🚶🏼‍♀", "🚶🏽‍♀", "🚶🏾‍♀", "🚶🏿‍♀", "🚶", "🚶🏻", "🚶🏼", "🚶🏽", "🚶🏾", "🚶🏿", "🏃‍♀", "🏃🏻‍♀", "🏃🏼‍♀", "🏃🏽‍♀", "🏃🏾‍♀", "🏃🏿‍♀", "🏃", "🏃🏻", "🏃🏼", "🏃🏽", "🏃🏾", "🏃🏿", "👫", "👭", "👬", "💑", "👩‍❤‍👩", "👨‍❤‍👨", "💏", "👩‍❤‍💋‍👩", "👨‍❤‍💋‍👨", "👪", "👨‍👩‍👧", "👨‍👩‍👧‍👦", "👨‍👩‍👦‍👦", "👨‍👩‍👧‍👧", "👩‍👩‍👦", "👩‍👩‍👧", "👩‍👩‍👧‍👦", "👩‍👩‍👦‍👦", "👩‍👩‍👧‍👧", "👨‍👨‍👦", "👨‍👨‍👧", "👨‍👨‍👧‍👦", "👨‍👨‍👦‍👦", "👨‍👨‍👧‍👧", "👩‍👦", "👩‍👧", "👩‍👧‍👦", "👩‍👦‍👦", "👩‍👧‍👧", "👨‍👦", "👨‍👧", "👨‍👧‍👦", "👨‍👦‍👦", "👨‍👧‍👧", "👚", "👕", "👖", "👔", "👗", "👙", "👘", "👠", "👡", "👢", "👞", "👟", "👒", "🎩", "🎓", "👑", "⛑", "🎒", "👝", "👛", "👜", "💼", "👓", "🕶", "🌂", "☂", "❤", "💛", "💚", "💙", "💜", "🖤", "💔", "❣", "💕", "💞", "💓", "💗", "💖", "💘", "💝"};
        r1[1] = new String[]{"🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐚", "🐞", "🐜", "🕷", "🕸", "🐢", "🐍", "🦎", "🦂", "🦀", "🦑", "🐙", "🦐", "🐠", "🐟", "🐡", "🐬", "🦈", "🐳", "🐋", "🐊", "🐆", "🐅", "🐃", "🐂", "🐄", "🦌", "🐪", "🐫", "🐘", "🦏", "🦍", "🐎", "🐖", "🐐", "🐏", "🐑", "🐕", "🐩", "🐈", "🐓", "🦃", "🕊", "🐇", "🐁", "🐀", "🐿", "🐾", "🐉", "🐲", "🌵", "🎄", "🌲", "🌳", "🌴", "🌱", "🌿", "☘", "🍀", "🎍", "🎋", "🍃", "🍂", "🍁", "🍄", "🌾", "💐", "🌷", "🌹", "🥀", "🌻", "🌼", "🌸", "🌺", "🌎", "🌍", "🌏", "🌕", "🌖", "🌗", "🌘", "🌑", "🌒", "🌓", "🌔", "🌚", "🌝", "🌞", "🌛", "🌜", "🌙", "💫", "⭐", "🌟", "✨", "⚡", "🔥", "💥", "☄", "☀", "🌤", "⛅", "🌥", "🌦", "🌈", "☁", "🌧", "⛈", "🌩", "🌨", "☃", "⛄", "❄", "🌬", "💨", "🌪", "🌫", "🌊", "💧", "💦", "☔"};
        r1[2] = new String[]{"🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🍍", "🥝", "🥑", "🍅", "🍆", "🥒", "🥕", "🌽", "🌶", "🥔", "🍠", "🌰", "🥜", "🍯", "🥐", "🍞", "🥖", "🧀", "🥚", "🍳", "🥓", "🥞", "🍤", "🍗", "🍖", "🍕", "🌭", "🍔", "🍟", "🥙", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍥", "🍣", "🍱", "🍛", "🍙", "🍚", "🍘", "🍢", "🍡", "🍧", "🍨", "🍦", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🍩", "🍪", "🥛", "🍼", "☕", "🍵", "🍶", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹", "🍾", "🥄", "🍴", "🍽", "⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🏉", "🎱", "🏓", "🏸", "🥅", "🏒", "🏑", "🏏", "⛳", "🏹", "🎣", "🥊", "🥋", "⛸", "🎿", "⛷", "🏂", "🏋‍♀", "🏋🏻‍♀", "🏋🏼‍♀", "🏋🏽‍♀", "🏋🏾‍♀", "🏋🏿‍♀", "🏋", "🏋🏻", "🏋🏼", "🏋🏽", "🏋🏾", "🏋🏿", "🤺", "🤼‍♀", "🤼‍♂", "🤸‍♀", "🤸🏻‍♀", "🤸🏼‍♀", "🤸🏽‍♀", "🤸🏾‍♀", "🤸🏿‍♀", "🤸‍♂", "🤸🏻‍♂", "🤸🏼‍♂", "🤸🏽‍♂", "🤸🏾‍♂", "🤸🏿‍♂", "⛹‍♀", "⛹🏻‍♀", "⛹🏼‍♀", "⛹🏽‍♀", "⛹🏾‍♀", "⛹🏿‍♀", "⛹", "⛹🏻", "⛹🏼", "⛹🏽", "⛹🏾", "⛹🏿", "🤾‍♀", "🤾🏻‍♀", "🤾🏼‍♀", "🤾🏽‍♀", "🤾🏾‍♀", "🤾🏿‍♀", "🤾‍♂", "🤾🏻‍♂", "🤾🏼‍♂", "🤾🏽‍♂", "🤾🏾‍♂", "🤾🏿‍♂", "🏌‍♀", "🏌🏻‍♀", "🏌🏼‍♀", "🏌🏽‍♀", "🏌🏾‍♀", "🏌🏿‍♀", "🏌", "🏌🏻", "🏌🏼", "🏌🏽", "🏌🏾", "🏌🏿", "🏄‍♀", "🏄🏻‍♀", "🏄🏼‍♀", "🏄🏽‍♀", "🏄🏾‍♀", "🏄🏿‍♀", "🏄", "🏄🏻", "🏄🏼", "🏄🏽", "🏄🏾", "🏄🏿", "🏊‍♀", "🏊🏻‍♀", "🏊🏼‍♀", "🏊🏽‍♀", "🏊🏾‍♀", "🏊🏿‍♀", "🏊", "🏊🏻", "🏊🏼", "🏊🏽", "🏊🏾", "🏊🏿", "🤽‍♀", "🤽🏻‍♀", "🤽🏼‍♀", "🤽🏽‍♀", "🤽🏾‍♀", "🤽🏿‍♀", "🤽‍♂", "🤽🏻‍♂", "🤽🏼‍♂", "🤽🏽‍♂", "🤽🏾‍♂", "🤽🏿‍♂", "🚣‍♀", "🚣🏻‍♀", "🚣🏼‍♀", "🚣🏽‍♀", "🚣🏾‍♀", "🚣🏿‍♀", "🚣", "🚣🏻", "🚣🏼", "🚣🏽", "🚣🏾", "🚣🏿", "🏇", "🏇🏻", "🏇🏼", "🏇🏽", "🏇🏾", "🏇🏿", "🚴‍♀", "🚴🏻‍♀", "🚴🏼‍♀", "🚴🏽‍♀", "🚴🏾‍♀", "🚴🏿‍♀", "🚴", "🚴🏻", "🚴🏼", "🚴🏽", "🚴🏾", "🚴🏿", "🚵‍♀", "🚵🏻‍♀", "🚵🏼‍♀", "🚵🏽‍♀", "🚵🏾‍♀", "🚵🏿‍♀", "🚵", "🚵🏻", "🚵🏼", "🚵🏽", "🚵🏾", "🚵🏿", "🎽", "🏅", "🎖", "🥇", "🥈", "🥉", "🏆", "🏵", "🎗", "🎫", "🎟", "🎪", "🤹‍♀", "🤹🏻‍♀", "🤹🏼‍♀", "🤹🏽‍♀", "🤹🏾‍♀", "🤹🏿‍♀", "🤹‍♂", "🤹🏻‍♂", "🤹🏼‍♂", "🤹🏽‍♂", "🤹🏾‍♂", "🤹🏿‍♂", "🎭", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹", "🥁", "🎷", "🎺", "🎸", "🎻", "🎲", "🎯", "🎳", "🎮", "🎰"};
        r1[3] = new String[]{"🚗", "🚕", "🚙", "🚌", "🚎", "🏎", "🚓", "🚑", "🚒", "🚐", "🚚", "🚛", "🚜", "🛴", "🚲", "🛵", "🏍", "🚨", "🚔", "🚍", "🚘", "🚖", "🚡", "🚠", "🚟", "🚃", "🚋", "🚞", "🚝", "🚄", "🚅", "🚈", "🚂", "🚆", "🚇", "🚊", "🚉", "🚁", "🛩", "✈", "🛫", "🛬", "🚀", "🛰", "💺", "🛶", "⛵", "🛥", "🚤", "🛳", "⛴", "🚢", "⚓", "🚧", "⛽", "🚏", "🚦", "🚥", "🗺", "🗿", "🗽", "⛲", "🗼", "🏰", "🏯", "🏟", "🎡", "🎢", "🎠", "⛱", "🏖", "🏝", "⛰", "🏔", "🗻", "🌋", "🏜", "🏕", "⛺", "🛤", "🛣", "🏗", "🏭", "🏠", "🏡", "🏘", "🏚", "🏢", "🏬", "🏣", "🏤", "🏥", "🏦", "🏨", "🏪", "🏫", "🏩", "💒", "🏛", "⛪", "🕌", "🕍", "🕋", "⛩", "🗾", "🎑", "🏞", "🌅", "🌄", "🌠", "🎇", "🎆", "🌇", "🌆", "🏙", "🌃", "🌌", "🌉", "🌁", "🏳", "🏴", "🏁", "🚩", "🏳‍🌈", "🇦🇺", "🇦🇹", "🇦🇿", "🇦🇽", "🇦🇱", "🇩🇿", "🇦🇸", "🇦🇮", "🇦🇴", "🇦🇩", "🇦🇶", "🇦🇬", "🇦🇷", "🇦🇲", "🇦🇼", "🇦🇫", "🇧🇸", "🇧🇩", "🇧🇧", "🇧🇭", "🇧🇾", "🇧🇿", "🇧🇪", "🇧🇯", "🇧🇲", "🇧🇬", "🇧🇴", "🇧🇶", "🇧🇦", "🇧🇼", "🇧🇷", "🇮🇴", "🇧🇳", "🇧🇫", "🇧🇮", "🇧🇹", "🇻🇺", "🇻🇦", "🇬🇧", "🇭🇺", "🇻🇪", "🇻🇬", "🇻🇮", "🇹🇱", "🇻🇳", "🇬🇦", "🇭🇹", "🇬🇾", "🇬🇲", "🇬🇭", "🇬🇵", "🇬🇹", "🇬🇳", "🇬🇼", "🇩🇪", "🇬🇬", "🇬🇮", "🇭🇳", "🇭🇰", "🇬🇩", "🇬🇱", "🇬🇷", "🇬🇪", "🇬🇺", "🇩🇰", "🇯🇪", "🇩🇯", "🇩🇲", "🇩🇴", "🇪🇺", "🇪🇬", "🇿🇲", "🇪🇭", "🇿🇼", "🇮🇱", "🇮🇳", "🇮🇩", "🇯🇴", "🇮🇶", "🇮🇷", "🇮🇪", "🇮🇸", "🇪🇸", "🇮🇹", "🇾🇪", "🇨🇻", "🇰🇿", "🇰🇾", "🇰🇭", "🇨🇲", "🇨🇦", "🇮🇨", "🇶🇦", "🇰🇪", "🇨🇾", "🇰🇬", "🇰🇮", "🇨🇳", "🇰🇵", "🇨🇨", "🇨🇴", "🇰🇲", "🇨🇬", "🇨🇩", "🇽🇰", "🇨🇷", "🇨🇮", "🇨🇺", "🇰🇼", "🇨🇼", "🇱🇦", "🇱🇻", "🇱🇸", "🇱🇷", "🇱🇧", "🇱🇾", "🇱🇹", "🇱🇮", "🇱🇺", "🇲🇺", "🇲🇷", "🇲🇬", "🇾🇹", "🇲🇴", "🇲🇰", "🇲🇼", "🇲🇾", "🇲🇱", "🇲🇻", "🇲🇹", "🇲🇦", "🇲🇶", "🇲🇭", "🇲🇽", "🇫🇲", "🇲🇿", "🇲🇩", "🇲🇨", "🇲🇳", "🇲🇸", "🇲🇲", "🇳🇦", "🇳🇷", "🇳🇵", "🇳🇪", "🇳🇬", "🇳🇱", "🇳🇮", "🇳🇺", "🇳🇿", "🇳🇨", "🇳🇴", "🇮🇲", "🇳🇫", "🇨🇽", "🇸🇭", "🇨🇰", "🇹🇨", "🇦🇪", "🇴🇲", "🇵🇳", "🇵🇰", "🇵🇼", "🇵🇸", "🇵🇦", "🇵🇬", "🇵🇾", "🇵🇪", "🇵🇱", "🇵🇹", "🇵🇷", "🇰🇷", "🇷🇪", "🇷🇺", "🇷🇼", "🇷🇴", "🇸🇻", "🇼🇸", "🇸🇲", "🇸🇹", "🇸🇦", "🇸🇿", "🇲🇵", "🇸🇨", "🇧🇱", "🇵🇲", "🇸🇳", "🇻🇨", "🇰🇳", "🇱🇨", "🇷🇸", "🇸🇬", "🇸🇽", "🇸🇾", "🇸🇰", "🇸🇮", "🇺🇸", "🇸🇧", "🇸🇴", "🇸🇩", "🇸🇷", "🇸🇱", "🇹🇯", "🇹🇭", "🇹🇼", "🇹🇿", "🇹🇬", "🇹🇰", "🇹🇴", "🇹🇹", "🇹🇻", "🇹🇳", "🇹🇲", "🇹🇷", "🇺🇬", "🇺🇿", "🇺🇦", "🇼🇫", "🇺🇾", "🇫🇴", "🇫🇯", "🇵🇭", "🇫🇮", "🇫🇰", "🇫🇷", "🇬🇫", "🇵🇫", "🇹🇫", "🇭🇷", "🇨🇫", "🇹🇩", "🇲🇪", "🇨🇿", "🇨🇱", "🇨🇭", "🇸🇪", "🇱🇰", "🇪🇨", "🇬🇶", "🇪🇷", "🇪🇪", "🇪🇹", "🇿🇦", "🇬🇸", "🇸🇸", "🇯🇲", "🇯🇵", "🎌"};
        r1[4] = new String[]{"💟", "☮", "✝", "☪", "🕉", "☸", "✡", "🔯", "🕎", "☯", "☦", "🛐", "⛎", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓", "🆔", "⚛", "🉑", "☢", "☣", "📴", "📳", "🈶", "🈚", "🈸", "🈺", "🈷", "✴", "🆚", "💮", "🉐", "㊙", "㊗", "🈴", "🈵", "🈹", "🈲", "🅰", "🅱", "🆎", "🆑", "🅾", "🆘", "❌", "⭕", "🛑", "⛔", "📛", "🚫", "💯", "💢", "♨", "🚷", "🚯", "🚳", "🚱", "🔞", "📵", "🚭", "❗", "❕", "❓", "❔", "‼", "⁉", "🔅", "🔆", "〽", "⚠", "🚸", "🔱", "⚜", "🔰", "♻", "✅", "🈯", "💹", "❇", "✳", "❎", "🌐", "💠", "Ⓜ", "🌀", "💤", "🏧", "🚾", "♿", "🅿", "🈳", "🈂", "🛂", "🛃", "🛄", "🛅", "🚹", "🚺", "🚼", "🚻", "🚮", "🎦", "📶", "🈁", "🔣", "ℹ", "🔤", "🔡", "🔠", "🆖", "🆗", "🆙", "🆒", "🆕", "🆓", "0⃣", "1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "🔟", "🔢", "#⃣", "*⃣", "▶", "⏸", "⏯", "⏹", "⏺", "⏭", "⏮", "⏩", "⏪", "⏫", "⏬", "◀", "🔼", "🔽", "➡", "⬅", "⬆", "⬇", "↗", "↘", "↙", "↖", "↕", "↔", "↪", "↩", "⤴", "⤵", "🔀", "🔁", "🔂", "🔄", "🔃", "🎵", "🎶", "➕", "➖", "➗", "✖", "💲", "💱", "™", "©", "®", "〰", "➰", "➿", "🔚", "🔙", "🔛", "🔝", "🔜", "✔", "☑", "🔘", "⚪", "⚫", "🔴", "🔵", "🔺", "🔻", "🔸", "🔹", "🔶", "🔷", "🔳", "🔲", "▪", "▫", "◾", "◽", "◼", "◻", "⬛", "⬜", "🔈", "🔇", "🔉", "🔊", "🔔", "🔕", "📣", "📢", "👁‍🗨", "💬", "💭", "🗯", "♠", "♣", "♥", "♦", "🃏", "🎴", "🀄", "🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚", "🕛", "🕜", "🕝", "🕞", "🕟", "🕠", "🕡", "🕢", "🕣", "🕤", "🕥", "🕦", "🕧", "⌚", "📱", "📲", "💻", "⌨", "🖥", "🖨", "🖱", "🖲", "🕹", "🗜", "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹", "🎥", "📽", "🎞", "📞", "☎", "📟", "📠", "📺", "📻", "🎙", "🎚", "🎛", "⏱", "⏲", "⏰", "🕰", "⌛", "⏳", "📡", "🔋", "🔌", "💡", "🔦", "🕯", "🗑", "🛢", "💸", "💵", "💴", "💶", "💷", "💰", "💳", "💎", "⚖", "🔧", "🔨", "⚒", "🛠", "⛏", "🔩", "⚙", "⛓", "🔫", "💣", "🔪", "🗡", "⚔", "🛡", "🚬", "⚰", "⚱", "🏺", "🔮", "📿", "💈", "⚗", "🔭", "🔬", "🕳", "💊", "💉", "🌡", "🚽", "🚰", "🚿", "🛁", "🛀", "🛀🏻", "🛀🏼", "🛀🏽", "🛀🏾", "🛀🏿", "🛎", "🔑", "🗝", "🚪", "🛋", "🛏", "🛌", "🖼", "🛍", "🛒", "🎁", "🎈", "🎏", "🎀", "🎊", "🎉", "🎎", "🏮", "🎐", "✉", "📩", "📨", "📧", "💌", "📥", "📤", "📦", "🏷", "📪", "📫", "📬", "📭", "📮", "📯", "📜", "📃", "📄", "📑", "📊", "📈", "📉", "🗒", "🗓", "📆", "📅", "📇", "🗃", "🗳", "🗄", "📋", "📁", "📂", "🗂", "🗞", "📰", "📓", "📔", "📒", "📕", "📗", "📘", "📙", "📚", "📖", "🔖", "🔗", "📎", "🖇", "📐", "📏", "📌", "📍", "✂", "🖊", "🖋", "✒", "🖌", "🖍", "📝", "✏", "🔍", "🔎", "🔏", "🔐", "🔒", "🔓"};
        data = r1;
        for (char valueOf : emojiToFE0F) {
            emojiToFE0FMap.put(Character.valueOf(valueOf), Boolean.valueOf(true));
        }
        for (char valueOf2 : dataChars) {
            dataCharsMap.put(Character.valueOf(valueOf2), Boolean.valueOf(true));
        }
        for (Object put : emojiColored) {
            emojiColoredMap.put(put, Boolean.valueOf(true));
        }
        dataColored[1] = data[1];
        dataColored[3] = data[3];
    }
}
