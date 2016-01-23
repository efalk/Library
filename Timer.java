
package org.efalk.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Schedule and receive periodic alarms.
 * Don't forget to register this in the manifest
 *	<receiver android:name="Timer" android:enabled="true" />
 *
 * Modify (or subclass) this to be useful.
 */
public class Timer extends BroadcastReceiver {
    static private final String TAG = "Timer";

    /**
     * Modify or override this in a subclass to be useful.
     */
    @Override
    public void onReceive(Context ctx, Intent intent) {
	Toast toast = Toast.makeText(ctx, "Alarm dingding", Toast.LENGTH_LONG);
	Log.d(TAG, "Alarm dingding");
	toast.show();
    }

    /**
     * Schedule a one-time notification.
     * @param ctx     Context
     * @param atTime  Time (in ms) when the notification should occur
     * @param pi      PendingIntent to use; may be null
     * @return the PendingIntent
     * atTime is relative to System.currentTimeMillis()
     * If pi is null, a new PendingIntent will be created.
     */
    public static PendingIntent schedule(Context ctx, long atTime,
    	PendingIntent pi)
    {
	if (pi == null)
	    pi = getIntent(ctx);
	AlarmManager mgr =
	  (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	mgr.set(AlarmManager.RTC_WAKEUP, atTime, pi);

	return pi;
    }

    /**
     * Schedule a one-time notification.
     * @param ctx     Context
     * @param atTime  Time (in ms) when the notification should occur
     * @return PendingIntent used for the notification
     *  The PendingIntent can be re-used
     */
    public static PendingIntent schedule(Context ctx, long atTime)
    {
	return schedule(ctx, atTime, null);
    }

    /**
     * Schedule a repeating notification.
     * @param ctx       Context
     * @param atTime    Time (in ms) when the notification should occur
     * @param interval  Time (in ms) between notifications
     * @param pi        PendingIntent to use; may be null
     * @return the PendingIntent
     * Use the returned PendingIntent to cancel the notification.
     * If pi is null, a new PendingIntent will be created.
     */
    public static void schedule(Context ctx, long atTime, long interval,
    	PendingIntent pi)
    {
	if (pi == null)
	    pi = getIntent(ctx);
	AlarmManager mgr =
	  (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	mgr.setRepeating(AlarmManager.RTC_WAKEUP, atTime, interval, pi);
    }

    /**
     * Cancel a notification.
     * @param ctx       Context -- any context will do
     * @param pi        PendingIntent to cancel
     * Use the returned PendingIntent to cancel the notification.
     * If pi is null, a new PendingIntent will be created.
     */
    public static void cancel(Context ctx, PendingIntent pi) {
	AlarmManager mgr =
	  (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	mgr.cancel(pi);
    }

    /**
     * Utility: return a PendingIntent that will launch this receiver
     */
    public static PendingIntent getIntent(Context ctx) {
	Intent intent = new Intent(ctx, Timer.class);
	return PendingIntent.getBroadcast(ctx, 0, intent, 0);
    }
}
