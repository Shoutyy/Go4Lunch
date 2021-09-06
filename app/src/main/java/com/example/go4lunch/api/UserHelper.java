package com.example.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.go4lunch.models.User;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class UserHelper {
    /**
     * Static referencement for request CRUD for Users Collection
     */
    private static final String COLLECTION_NAME = "users";
    private static final String placeBook = null;

    /**
     * Collection reference
     *
     * @return
     */
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getUsersBookCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).whereEqualTo("placeId", placeBook);
    }
    
    /**
     * Create
     *
     * @param uid
     * @param username
     * @param urlPicture
     * @return
     */
    public static Task<Void> createUser(String uid, String username, String urlPicture, String placeId, ArrayList<String> like, int currentTime) {
        //Create user object
        User userToCreate = new User(uid, username, urlPicture, placeId, like, currentTime);
        //Add a new user Document in Firestore
        return UserHelper.getUsersCollection()
                .document(uid) //Setting uID for Document
                .set(userToCreate);//Setting object for Document
    }

    /**
     * Get
     *
     * @param uid
     * @return
     */
    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    /**
     * Update
     *
     * @param username
     * @param uid
     * @return
     */
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updatePlaceId(String uid, String placeId, int currentTime) {
        return UserHelper.getUsersCollection().document(uid).update("placeId", placeId, "currentTime", currentTime);
    }

    public static Task<Void> updateLike(String uid, String placeId) {
        return UserHelper.getUsersCollection().document(uid).update("like", FieldValue.arrayUnion(placeId));
    }

    /**
     * Delete
     *
     * @param uid
     * @return
     */
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

    public static Task<Void> deletePlaceId(String uid) {
        return UserHelper.getUsersCollection().document(uid).update("placeId", null);
    }

    public static Task<Void> deleteLike(String uid, String placeId) {
        return UserHelper.getUsersCollection().document(uid).update("like", FieldValue.arrayRemove(placeId));
    }
}
