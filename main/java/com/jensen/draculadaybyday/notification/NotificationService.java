package com.jensen.draculadaybyday.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.widget.RemoteViews;

import com.jensen.draculadaybyday.R;

public class NotificationService extends JobService {

    private NotificationService mNotification;
    private NotificationManager mNotificationManager;

    private final UpdateAppsAsyncTask updateTask = new UpdateAppsAsyncTask();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        int size = 5;
        // Notifications
        RemoteViews[] views = RemoteViews.CREATOR.newArray(size);

        Context currentContext = getApplicationContext();

        PersistableBundle bundle = jobParameters.getExtras();

        //build notification
        Notification.Builder mBuilder = new Notification.Builder(this, NotificationUtils.DRACULA_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fangs)
                .setContentTitle("Simple notification")
                .setSubText("How about that")
                .setContentText("This is test of simple notification.");
        /*
        for(int i = 0; i < size; i++) {
            LinearLayout layout = new LinearLayout(currentContext);
       //     layout.addView(new EditText()
            mBuilder.setContent(views[i]);
        }
        */
        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // To post your notification to the notification bar
        notificationManager.notify(0 , mBuilder.build());

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return updateTask.stopJob(jobParameters);
    }

    private static class UpdateAppsAsyncTask extends AsyncTask<JobParameters, Void, JobParameters[]> {


        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {

            // Do updating and stopping logical here.
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                if (!hasJobBeenStopped(params)) {
//                    jobFinished(params, false);
                }
            }
        }

        private boolean hasJobBeenStopped(JobParameters params) {
            // Logic for checking stop.
            return false;
        }

        public boolean stopJob(JobParameters params) {
            // Logic for stopping a job. return true if job should be rescheduled.
            return false;
        }

    }
}

