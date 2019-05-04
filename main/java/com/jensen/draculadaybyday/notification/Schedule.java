package com.jensen.draculadaybyday.notification;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class Schedule {

    private final JobScheduler mJobScheduler;
    private static Schedule mSchedule;
    private static Context mContext;

    private int JOB_ID = 1;

    private Schedule(Context context) {
        mJobScheduler =  (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

    }

    public static Schedule getInstance(Context context) {
        if (mSchedule == null) {
            mSchedule = new Schedule(context);
        }
        mContext = context;

        return mSchedule;
    }

    public void makeNotification(long timeToFutureJob, int futureNotifications) {
        if (mJobScheduler != null) {
            try {
                ComponentName notificationName = new ComponentName(mContext, NotificationService.class);
                JobInfo jInfo = new JobInfo.Builder(JOB_ID, notificationName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .setOverrideDeadline(timeToFutureJob)
                        .setPersisted(true)
                        .build();

                mJobScheduler.schedule(jInfo);
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());
            }
        }
    }
}
