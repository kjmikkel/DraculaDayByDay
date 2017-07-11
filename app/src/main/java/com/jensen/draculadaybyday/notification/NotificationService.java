package com.jensen.draculadaybyday.notification;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

public class NotificationService extends JobService {

    private NotificationService mNotification;
    private NotificationManager mNotificationManager;

    private UpdateAppsAsyncTask updateTask = new UpdateAppsAsyncTask();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        // Notifications
        /*
        //build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_fangs)
                .setContentTitle("Simple notification")
                .setContentText("This is test of simple notification.");

        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // To post your notification to the notification bar
        notificationManager.notify(0 , mBuilder.build());
        */

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return updateTask.stopJob(jobParameters);
    }

    private class UpdateAppsAsyncTask extends AsyncTask<JobParameters, Void, JobParameters[]> {


        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {

            // Do updating and stopping logical here.
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                if (!hasJobBeenStopped(params)) {
                    jobFinished(params, false);
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

