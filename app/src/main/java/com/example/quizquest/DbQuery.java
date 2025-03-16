package com.example.quizquest;

import android.util.ArrayMap;
import android.util.Log;

import com.example.quizquest.Models.CategoryModel;
import com.example.quizquest.Models.ProfileModel;
import com.example.quizquest.Models.QuestionModel;
import com.example.quizquest.Models.RankModel;
import com.example.quizquest.Models.TestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.quizquest.QuestionsActivity.NOT_VISITED;

public class DbQuery {

    public static List<CategoryModel> g_catList = new ArrayList<>();
    public static int selected_cat_index = 0;

    public static List<QuestionModel> g_bookmarkList = new ArrayList<>();
    public static List<String> g_bmIdList = new ArrayList<>();

    public static List<TestModel> g_testList = new ArrayList<>();
    public static int selected_test_index = 0;

    public static List<QuestionModel> g_quesList = new ArrayList<>();

    public static List<RankModel> g_userList = new ArrayList<>();

    public static RankModel  myPerformance = new RankModel("You",0,0, null);

    public static ProfileModel myprofile = new ProfileModel("NA",null,"NA",null,null);

    public static boolean isMeOnTopList = false;
    public static int usersCount = 0;

    public static FirebaseFirestore g_firestore;
    static int tmp = 0;

    public static String liveTestID = null;
    public static Timestamp testTime = null;


    public static void loadQuestions(final OnQCompleteListener onCompleteListener)
    {

        g_quesList.clear();

        CollectionReference collectionRef = g_firestore.collection("Questions");
        collectionRef.whereEqualTo("CATEGORY",g_catList.get(selected_cat_index).getDocID())
                .whereEqualTo("TEST", g_testList.get(selected_test_index).getTestID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(final QueryDocumentSnapshot doc : queryDocumentSnapshots)
                        {

                            boolean isBookMarked = false;

                            if(g_bmIdList.contains(doc.getId()))
                                isBookMarked = true;

                            g_quesList.add(new QuestionModel(
                                    doc.getId(),
                               doc.getString("QUESTION"),
                               doc.getString("A"),
                               doc.getString("B"),
                               doc.getString("C"),
                               doc.getString("D"),
                               doc.getLong("ANSWER").intValue(),
                               NOT_VISITED,
                                    -1,
                                    isBookMarked
                            ));
                        }


                        if(onCompleteListener != null)
                            onCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });


    }


    public static void loadBmIds(final OnQCompleteListener onCompleteListener)
    {

        g_bmIdList.clear();

        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS");

        bmDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                int count = myPerformance.getBookmarksCount();

                for(int i=1; i <= count; i++)
                {
                    String docID = documentSnapshot.getString("BM" + String.valueOf(i) + "_ID");
                    g_bmIdList.add(docID);
                }

                if (onCompleteListener != null)
                    onCompleteListener.onSuccess();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });

    }


    public static void loadBookmarks(final OnQCompleteListener onCompleteListener)
    {

        g_bookmarkList.clear();

        tmp = 0;

        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS");

        bmDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()) {
                    final int count = myPerformance.getBookmarksCount();
                    Log.d("BM count=", String.valueOf(count));

                    if(count == 0) {
                        if (onCompleteListener != null)
                            onCompleteListener.onSuccess();
                    }


                    CollectionReference reference = g_firestore.collection("Questions");

                    for (int i = 1; i <= count; i++) {
                        String docID = documentSnapshot.getString("BM" + String.valueOf(i) + "_ID");


                        reference.document(docID)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot doc) {

                                if (doc.exists()) {

                                    g_bookmarkList.add(new QuestionModel(
                                            doc.getId(),
                                            doc.getString("QUESTION"),
                                            doc.getString("A"),
                                            doc.getString("B"),
                                            doc.getString("C"),
                                            doc.getString("D"),
                                            doc.getLong("ANSWER").intValue(),
                                            0,
                                            -1,
                                            false
                                    ));

                                }

                                tmp++;

                                if (tmp == count) {


                                    if (onCompleteListener != null)
                                        onCompleteListener.onSuccess();
                                }


                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (onCompleteListener != null)
                                            onCompleteListener.onFailure();
                                    }
                                });


                    }
                }
                else
                {
                    if (onCompleteListener != null)
                        onCompleteListener.onSuccess();

                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });


    }

    public static void getTopUsers(final OnQCompleteListener onCompleteListener)
    {
        g_userList.clear();

        g_firestore.collection("USERS")
                .whereGreaterThan("TOTAL_SCORE",0)
                .orderBy("TOTAL_SCORE", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String myUID = FirebaseAuth.getInstance().getUid();

                        int rank = 1;
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            String photoUrl;
                            if(doc.getString("PHOTO_URL") != null)
                                photoUrl = doc.getString("PHOTO_URL");
                            else
                                photoUrl = null;

                            g_userList.add(new RankModel(
                                doc.getString("NAME"),
                                    rank,
                                    doc.getLong("TOTAL_SCORE").intValue(),
                                    photoUrl
                            ));


                            if(myUID.compareTo(doc.getId()) == 0)
                            {
                                isMeOnTopList = true;
                                myPerformance.setRank(rank);
                            }

                            rank++;

                        }

                        if(onCompleteListener !=  null)
                            onCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(onCompleteListener !=  null)
                            onCompleteListener.onFailure();
                    }
                });

    }

    public static void getUsersCount(final OnQCompleteListener onCompleteListener)
    {
        g_firestore.collection("USERS").document("TOTAL_USERS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        usersCount = documentSnapshot.getLong("COUNT").intValue();

                        if(onCompleteListener != null)
                            onCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });

    }

    public static void loadTestData(final OnQCompleteListener onCompleteListener)
    {

        g_testList.clear();

        CollectionReference collectionRef = g_firestore.collection("QUIZ");

        collectionRef.document(g_catList.get(selected_cat_index).getDocID())
                .collection("TESTS_LIST").document("TESTS_INFO")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();

                            int noOfTests = g_catList.get(selected_cat_index).getNoOfTest();

                            for(int t=1; t <= noOfTests; t++) {
                                g_testList.add(new TestModel(
                                        doc.getString("TEST" + String.valueOf(t) + "_ID"),
                                        0,
                                        doc.getLong("TEST" + String.valueOf(t) + "_TIME").intValue()
                                ));
                            }

                            if(onCompleteListener != null)
                                onCompleteListener.onSuccess();

                        }
                        else
                        {

                            if(onCompleteListener != null)
                                onCompleteListener.onFailure();
                        }

                    }
                });


    }


    public static void loadMyScores(final OnQCompleteListener onCompleteListener)
    {
        CollectionReference collectionRef = g_firestore.collection("USERS");

        collectionRef.document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();

                            for(int i=0; i  < g_testList.size(); i++)
                            {
                                int top = 0;

                                if(doc.get(g_testList.get(i).getTestID()) != null)
                                    top = doc.getLong(g_testList.get(i).getTestID()).intValue();

                                g_testList.get(i).setTopScore(top);
                            }

                           // myRank.setScore(doc.getLong("TOTAL").intValue());

                            if(onCompleteListener != null)
                                onCompleteListener.onSuccess();


                        }
                        else
                        {
                            if(onCompleteListener != null)
                                onCompleteListener.onFailure();
                        }


                    }
                });

    }

    public static void updateUsersCount(final OnQCompleteListener onCompleteListener)
    {
        g_firestore.collection("USERS").document("TOTAL_USERS")
                .update("COUNT", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if(onCompleteListener != null)
                            onCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });

    }

    public static void createUserData(final String name, final String email, final String phone, final String photo_url, final OnQCompleteListener onCompleteListener)
    {
        Map<String,Object> userData = new ArrayMap<>();
        userData.put("EMAIL_ID", email);
        userData.put("NAME", name);
        userData.put("BOOKMARKS", 0);

        if(phone != null)
            userData.put("PHONE", phone);

        if(photo_url != null)
            userData.put("PHOTO_URL",photo_url);

        userData.put("TOTAL_SCORE",0);

        WriteBatch batch = g_firestore.batch();

        //CollectionReference collectionRef = g_firestore.collection("USERS");
        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        batch.set(userDoc, userData);

        DocumentReference userCountDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
        batch.update(userCountDoc, "COUNT", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //set Data
                        myPerformance.setScore(0);
                        myPerformance.setuName(name);

                        myprofile.setName(name);
                        myprofile.setEmailID(email);
                        myprofile.setPhoneNo(phone);
                        myprofile.setPhotoURL(photo_url);
                        // myprofile.setState(state);


                        loadCategories(new OnQCompleteListener() {
                            @Override
                            public void onSuccess() {

                                getUsersCount(new OnQCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        if(onCompleteListener != null)
                                            onCompleteListener.onSuccess();
                                    }

                                    @Override
                                    public void onFailure() {

                                        if(onCompleteListener != null)
                                            onCompleteListener.onFailure();
                                    }
                                });


                            }

                            @Override
                            public void onFailure() {
                                if(onCompleteListener != null)
                                    onCompleteListener.onFailure();
                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });
    }

    public static void saveProfileData(final String name, final String phone, final String state, final OnQCompleteListener onCompleteListener)
    {

        Map<String, Object> profileData = new ArrayMap<>();

        profileData.put("NAME",name);

        if(phone != null)
            profileData.put("PHONE",phone);

        if(state != null)
            profileData.put("STATE",state);

        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .update(profileData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        myprofile.setName(name);

                        if(phone != null)
                            myprofile.setPhoneNo(phone);

                        if(state != null)
                            myprofile.setState(state);


                        onCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        onCompleteListener.onFailure();
                    }
                });


    }


    public static void loadCategories(final OnQCompleteListener onCompleteListener)
    {

        g_catList.clear();
       // Log.d("LOGGG","in loadData");

        CollectionReference collectionRef = g_firestore.collection("QUIZ");

        collectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String,QueryDocumentSnapshot> docList = new ArrayMap<>();

                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots)
                    {
                        docList.put(doc.getId(),doc);
                    }

                    QueryDocumentSnapshot catListDoc = docList.get("Categories");

                    long catCount = catListDoc.getLong("COUNT");

                    for( int i=1; i <= catCount; i++)
                    {

                        String catID = catListDoc.getString("CAT" + String.valueOf(i) + "_ID");

                        QueryDocumentSnapshot catDoc = docList.get(catID);

                        int noOfTests = catDoc.getLong("NO_OF_TESTS").intValue();

                        g_catList.add(new CategoryModel(
                                catID,
                                catDoc.getString("NAME"),
                                noOfTests
                        ));


                    }


                    //Call callback
                        if(onCompleteListener != null)
                            onCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });
    }


    public static void getMyTotalScore(final OnQCompleteListener onCompleteListener)
    {
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        myPerformance.setScore(documentSnapshot.getLong("TOTAL_SCORE").intValue());
                        myPerformance.setuName(documentSnapshot.getString("NAME"));

                        if(documentSnapshot.getLong("BOOKMARKS") != null)
                            myPerformance.setBookmarksCount(documentSnapshot.getLong("BOOKMARKS").intValue());
                        else
                            myPerformance.setBookmarksCount(0);

                        myprofile.setName(documentSnapshot.getString("NAME"));
                        myprofile.setEmailID(documentSnapshot.getString("EMAIL_ID"));

                        if(documentSnapshot.getString("PHONE") != null)
                            myprofile.setPhoneNo(documentSnapshot.getString("PHONE"));

                        if(documentSnapshot.getString("STATE") != null)
                            myprofile.setState(documentSnapshot.getString("STATE"));

                        if( documentSnapshot.getString("PHOTO_URL") != null)
                            myprofile.setPhotoURL(documentSnapshot.getString("PHOTO_URL"));

                        if(onCompleteListener != null)
                            onCompleteListener.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();

                    }
                });
    }


    public static void setLiveTestListener()
    {
        final DocumentReference docRef = g_firestore.collection("LIVE_TEST").document("INFO");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("LOGGGG: ", documentSnapshot.getData().toString());

                    if(documentSnapshot.getBoolean("AVAILABLE"))
                    {
                        liveTestID = documentSnapshot.getString("TEST_ID");
                        testTime = documentSnapshot.getTimestamp("TIME");
                    }
                    else
                    {
                        liveTestID = null;
                    }

                } else {
                    System.out.print("Current data: null");
                }
            }
        });

    }


    public static void loadData(final OnQCompleteListener onCompleteListener)
    {

        setLiveTestListener();

        loadCategories(new OnQCompleteListener() {
            @Override
            public void onSuccess() {

                getUsersCount(new OnQCompleteListener() {
                    @Override
                    public void onSuccess() {

                        getMyTotalScore(new OnQCompleteListener() {
                            @Override
                            public void onSuccess() {
                                loadBmIds(onCompleteListener);
                            }

                            @Override
                            public void onFailure() {
                                if(onCompleteListener != null)
                                    onCompleteListener.onFailure();
                            }
                        });

                    }

                    @Override
                    public void onFailure() {

                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });

            }

            @Override
            public void onFailure() {
                if(onCompleteListener != null)
                    onCompleteListener.onFailure();
            }

            });

    }


    public static void saveResult(final int score ,final OnQCompleteListener onCompleteListener)
    {

        WriteBatch batch = g_firestore.batch();

        //BookMarks
        Map<String, Object> bmData = new ArrayMap<>();

        for(int i=1; i <= g_bmIdList.size(); i++)
        {
            bmData.put("BM" + String.valueOf(i) + "_ID", g_bmIdList.get(i-1));
        }

        final int count = g_bmIdList.size();

        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS");

        batch.set(bmDoc, bmData);


        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid());

        Map<String, Object> userData = new ArrayMap<>();
        userData.put("TOTAL_SCORE", myPerformance.getScore() + score);
        userData.put("BOOKMARKS", count);

        batch.update(userDoc,userData);



        if( score > g_testList.get(selected_test_index).getTopScore() )
        {
            Map<String, Object> data = new ArrayMap<>();
            data.put(g_testList.get(selected_test_index).getTestID(), score);

            DocumentReference scoreDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_SCORES");

            batch.set(scoreDoc, data, SetOptions.merge());
        }


        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if( score > g_testList.get(selected_test_index).getTopScore() )
                    g_testList.get(selected_test_index).setTopScore(score);

                myPerformance.setScore(myPerformance.getScore() + score);

                myPerformance.setBookmarksCount(count);

                if(onCompleteListener != null)
                    onCompleteListener.onSuccess();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(onCompleteListener != null)
                            onCompleteListener.onFailure();
                    }
                });

    }


    public static void clearData()
    {
        g_catList.clear();
        g_testList.clear();
        g_quesList.clear();
        g_userList.clear();
        g_bookmarkList.clear();

        liveTestID = null;
    }

}
