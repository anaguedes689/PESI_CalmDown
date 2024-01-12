/*package com.feup.pesi.calmdown.service;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class SyncService extends JobIntentService {

    // Unique job ID for the service
    private static final int JOB_ID = 1000;

    // Entry point for the service
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SyncService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Perform data synchronization here
        syncDataToFirestore();
    }

    private void syncDataToFirestore() {
        // Retrieve data from the local database
        JacketDataDbAdapter jacketDataDbAdapter = new JacketDataDbAdapter(getApplicationContext());
        jacketDataDbAdapter.open();

        // Get recent average data
        List<AverageData> recentAverageData = jacketDataDbAdapter.getRecentAverageData();

        // Sync data to Firestore
        FirestoreSyncHelper.syncToFirestore(recentAverageData);

        jacketDataDbAdapter.close();
    }
}
*/