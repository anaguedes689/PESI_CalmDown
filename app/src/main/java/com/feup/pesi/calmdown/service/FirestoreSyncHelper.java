/*package com.feup.pesi.calmdown.service;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestoreSyncHelper {

    public static void syncBpmToFirestore() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Convert timestamp to Firestore Timestamp
        Timestamp firestoreTimestamp = new Timestamp(new Date(dateTime));


        // Create a map of data to be synced to Firestore
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("averageBpm", averageBpm);
        dataMap.put("timestamp", firestoreTimestamp);

        // Replace "bpm_data_collection" with your Firestore collection name
        db.collection("bpm_data_collection")
                .add(dataMap)
                .addOnSuccessListener(documentReference -> {
                    // Handle success
                    Log.d("FirestoreSync", "BPM data synced successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("FirestoreSync", "Error syncing BPM data to Firestore", e);
                });
    }
}

*/

