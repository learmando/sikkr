package edu.chalmers.sikkr.backend.mms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import static android.provider.Telephony.Mms.Inbox.CONTENT_URI;
import static android.provider.Telephony.Mms.Addr.*;
import static android.provider.Telephony.Mms.Sent.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.chalmers.sikkr.backend.VoiceMessage;

/**
 * Created by Eric on 2014-10-02.
 */
public class MMSInbox {

    private Context context;
    private final List<VoiceMessage> voiceMessages = new ArrayList<VoiceMessage>();
    private final static MMSInbox singleton = new MMSInbox();

    public static void setContext(Context context) {
        singleton.context = context;
    }

    public static MMSInbox getSharedInstance() {
        if (singleton.context != null) {
            return singleton;
        } else {
            throw new UnsupportedOperationException("This singleton requires a context to be set.");
        }
    }

    public void refreshInbox() {
        voiceMessages.clear();
        loadInbox();
    }

    public void loadInbox() {
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS));
            String partID = cursor.getString(cursor.getColumnIndexOrThrow(_ID));

            Cursor curPart = context.getContentResolver().query(Uri.parse ("content://mms/" + partID + "/part"), null, null, null, null);
            curPart.moveToFirst();
            Uri partURI = Uri.parse("content://mms/part/" + curPart.getString(0));

            Calendar timestamp = new GregorianCalendar();
            timestamp.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(DATE)));

            MMS mms = new MMS(timestamp, sender, partURI);
            getInboxContents().add(mms);
        }
    }

    public List<VoiceMessage> getInboxContents() {
        return voiceMessages;
    }
}
