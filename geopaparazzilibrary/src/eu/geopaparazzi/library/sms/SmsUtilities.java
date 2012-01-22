/*
 * Geopaparazzi - Digital field mapping on Android based devices
 * Copyright (C) 2010  HydroloGIS (www.hydrologis.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.geopaparazzi.library.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;
import eu.geopaparazzi.library.R;
import eu.geopaparazzi.library.util.LibraryConstants;
import eu.geopaparazzi.library.util.Utilities;
import eu.geopaparazzi.library.util.debug.Debug;
import eu.geopaparazzi.library.util.debug.Logger;

/**
 * Utilities for sms handling.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class SmsUtilities {

    /**
     * Create a text containing the OSM link to the current position.
     * 
     * @param context the {@link Context} to use.
     * @param messageText the text to add before the url.
     * @return the position url.
     */
    public static String createPositionText( final Context context, String messageText ) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        float gpsLon = preferences.getFloat(LibraryConstants.PREFS_KEY_LON, -9999);
        float gpsLat = preferences.getFloat(LibraryConstants.PREFS_KEY_LAT, -9999);
        StringBuilder sB = new StringBuilder();
        if (gpsLon != -9999) {
            String latString = String.valueOf(gpsLat).replaceAll(",", ".");
            String lonString = String.valueOf(gpsLon).replaceAll(",", ".");
            // http://www.openstreetmap.org/?lat=46.068941&lon=11.169849&zoom=18&layers=M&mlat=42.95647&mlon=12.70393
            // http://maps.google.com/maps?q=46.068941,11.169849&z=16

            if (messageText != null)
                sB.append(messageText).append(":");
            sB.append("http://www.openstreetmap.org/?lat=");
            sB.append(latString);
            sB.append("&lon=");
            sB.append(lonString);
            sB.append("&zoom=14");
            sB.append("&layers=M&mlat=");
            sB.append(latString);
            sB.append("&mlon=");
            sB.append(lonString);

            String msg = sB.toString();
            if (sB.toString().length() > 160) {
                // if longer than 160 chars it will not work
                sB = new StringBuilder(msg);
            }
        } else {
            sB.append(context.getString(R.string.last_position_unknown));
        }

        return sB.toString();
    }

    /**
     * Send an SMS.
     * 
     * @param context the {@link Context} to use.
     * @param number the number to which to send the SMS.
     * @param msg the SMS body text.
     */
    public static void sendSMS( Context context, String number, String msg ) {
        SmsManager mng = SmsManager.getDefault();
        PendingIntent dummyEvent = PendingIntent.getBroadcast(context, 0, new Intent("com.devx.SMSExample.IGNORE_ME"), 0);
        try {
            if (msg.length() > 160) {
                msg = msg.substring(0, 160);
                if (Debug.D)
                    Logger.i("SMSUTILITIES", "Trimming msg to: " + msg);
            }
            mng.sendTextMessage(number, null, msg, dummyEvent, dummyEvent);
            Utilities.toast(context, R.string.message_sent, Toast.LENGTH_LONG);
        } catch (Exception e) {
            Logger.e(context, e.getLocalizedMessage(), e);
            Utilities.messageDialog(context, "An error occurred while sending the SMS.", null);
        }

    }

}
