package com.jensen.draculadaybyday.notification;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class Schedule {

    private final JobScheduler mJobScheduler;
    private Schedule mSchedule;

    private int JOB_ID = 1;

    private Schedule(Context context) {
        mJobScheduler =  (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public Schedule getInstance(Context context) {
        if (mSchedule == null) {
            mSchedule = new Schedule(context);
        }

        return mSchedule;
    }

    public void makeNotification(long timeToFutureJob) {
        if (mJobScheduler != null) {
            /*
            ComponentName notificationName = new ComponentName(context, NotificationService.class);
            JobInfo jInfo = new JobInfo.Builder(JOB_ID, notificationName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .setOverrideDeadline(timeToFutureJob)
                    .setPersisted(true)
                    .build();

            mJobScheduler.schedule(jInfo);
            */
        }
    }
}
