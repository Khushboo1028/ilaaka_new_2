package com.replon.www.replonhomy.Utility;

import android.content.Context;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.replon.www.replonhomy.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class CallUserData {

    private static final String TAG = "CallUserData";
    List<UserDataFirebase> user_arraylist;
    ListenerRegistration getDataListener;

    public String flat_no="",name="",society_name="",username="",unique_id="";
    public Boolean admin,dummy=TRUE;
    public Timestamp date_created;
    public DocumentReference society_id;
    public UserDataFirebase userDataFirebase;


    public CallUserData(final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentFirebaseUser != null) {

            final String user_id = currentFirebaseUser.getUid();

            String user = context.getString(R.string.USER);

            DocumentReference docRef = db.collection(user).document(user_id);

            user_arraylist = new ArrayList<>();

            getDataListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                    if (e!=null){
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                            ? "Local" : "Server";

                    if (snapshot != null && snapshot.exists()) {

                        Log.d(TAG, source + " Our data is here data: " + snapshot.getData());

                        if(snapshot.get("flat_no")!=null) {
                            flat_no=snapshot.getString("flat_no");
                        }
                        if(snapshot.get("name")!=null) {
                            name=snapshot.getString("name");
                        }
                        if(snapshot.get("date_created")!=null) {
                            date_created=snapshot.getTimestamp("date_created");
                        }
                        if(snapshot.get("admin")!=null) {
                            admin=Boolean.parseBoolean(snapshot.get("admin").toString());
                        }
                        if(snapshot.get("society_name")!=null) {

                            society_name=snapshot.getString("society_name");
                            if(society_name.equals(context.getString(R.string.DUMMY_SOC))){
                                dummy=TRUE;
                            }
                            Log.i(TAG,"dummy? "+dummy);
                        }

                        if(snapshot.get("username")!=null) {
                            username=snapshot.getString("username");
                        }
                        if(snapshot.get("society_id")!=null) {
                            society_id=snapshot.getDocumentReference("society_id");
                        }

                        if(snapshot.get("unique_id")!=null) {
                          unique_id=snapshot.getString("unique_id");
                        }

                        userDataFirebase = new UserDataFirebase(
                                flat_no,
                                society_name,
                                date_created,
                                admin,
                                society_name,
                                username,
                                society_id,
                                unique_id
                        );



                    }



                }
            });     // End of docRef.SnapshotListener

        }
    }


}
