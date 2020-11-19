package com.dellhoak.userroles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class admin_dashboard extends AppCompatActivity {
    Button logoutbtn;
    TextView fullname_toolbar, email_toolbar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    Toolbar toolbar;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    List<String> titles;
    Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        toolbar = (Toolbar) findViewById(R.id.custom_toolbarid);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerviewadmin);


        fullname_toolbar = findViewById(R.id.fullname_toolbar);
        email_toolbar = findViewById(R.id.email_toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();

        email_toolbar.setText(user.getEmail());

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(user.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String fullname = documentSnapshot.getString("Fullname");
                fullname_toolbar.setText(fullname);

            }
        });

        Query query = firebaseFirestore.collection("recycler_titles").orderBy("Title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<model> options = new FirestoreRecyclerOptions.Builder<model>()
                .setQuery(query, model.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<model, modeViewHolder>(options) {
            @NonNull
            @Override
            public modeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recyclerview,parent,false);
                return new modeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull modeViewHolder modeViewHolder, int i, @NonNull model model) {
                modeViewHolder.title.setText(model.getTitle());
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firestoreRecyclerAdapter);
//
//        titles = new ArrayList<>();
//
//        titles.add("LOGOUT");
//        titles.add("1");
//        titles.add("2");
//        titles.add("3");
//        titles.add("4");
//        titles.add("5");

//        adapter = new Adapter(this, titles);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1,GridLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setAdapter(adapter);



        logoutbtn = findViewById(R.id.logoutbtnadmin);

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    private class modeViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        public modeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textviewcardholder);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();

    }

    @Override
    public void onBackPressed() {
        new iOSDialogBuilder(admin_dashboard.this)
                .setTitle("Are you sure you want to EXIT")
                .setSubtitle("Click ok To Exit")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("ok",new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        finish();

                    }
                })
                .setNegativeListener("Dismiss", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }

                })
                .build().show();
    }
}