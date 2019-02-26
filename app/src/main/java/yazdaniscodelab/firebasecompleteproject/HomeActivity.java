package yazdaniscodelab.firebasecompleteproject;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import yazdaniscodelab.firebasecompleteproject.Model.Data;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    //Firebase..
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //globar variable..

    private String title;
    private String description;
    private String budget;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Firebase Complete Project");
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("AllData").child(uid);


        //Recycler view..

        recyclerView=findViewById(R.id.recyclerId);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //floating button

        fab=findViewById(R.id.fab_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });
    }


    public void addData(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        View myview=inflater.inflate(R.layout.inputlayout,null);
        mydialog.setView(myview);
        final AlertDialog dialog=mydialog.create();

        final EditText mTitle=myview.findViewById(R.id.title);
        final EditText mDescription=myview.findViewById(R.id.description);
        final EditText mBudget=myview.findViewById(R.id.budget);
        Button btnSave=myview.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=mTitle.getText().toString().trim();
                String description=mDescription.getText().toString().trim();
                String budget=mBudget.getText().toString().trim();

                String mDate= DateFormat.getDateInstance().format(new Date());
                String id=mDatabase.push().getKey();
                Data data=new Data(title,description,budget,id,mDate);
                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.dataitem,
                        MyViewHolder.class,
                        mDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setBudget(model.getBudget());
                viewHolder.setDate(model.getDate());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key=getRef(position).getKey();
                        title=model.getTitle();
                        description=model.getDescription();
                        budget=model.getBudget();
                        upDateData();
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setTitle(String title){
            TextView mTitle=mView.findViewById(R.id.title_item);
            mTitle.setText(title);
        }

        public void setDescription(String description){
            TextView mDescription=mView.findViewById(R.id.description_item);
            mDescription.setText(description);
        }
        public void setBudget(String budget){
            TextView mBudget=mView.findViewById(R.id.budget_item);
            mBudget.setText("$"+budget);
        }
        public void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_item);
            mDate.setText(date);
        }


    }

    public void upDateData(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        View myview=inflater.inflate(R.layout.updatelayout,null);
        mydialog.setView(myview);
        final AlertDialog dialog=mydialog.create();

        final EditText mTitle=myview.findViewById(R.id.title_upd);
        final EditText mDescription=myview.findViewById(R.id.description_upd);
        final EditText mBudget=myview.findViewById(R.id.budget_upd);
        Button btnUpdate=myview.findViewById(R.id.btnUpdate_upd);
        Button btnDelete=myview.findViewById(R.id.btnDelete_upd);

        //we need to set our server data inside edit text..

        mTitle.setText(title);
        mTitle.setSelection(title.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        mBudget.setText(budget);
        mBudget.setSelection(budget.length());


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title=mTitle.getText().toString().trim();
                description=mDescription.getText().toString().trim();
                budget=mBudget.getText().toString().trim();

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(title,description,budget,post_key,mDate);
                mDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });


        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
