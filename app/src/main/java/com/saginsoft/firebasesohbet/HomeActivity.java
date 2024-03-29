package com.saginsoft.firebasesohbet;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    ArrayList<ListItem> list = new ArrayList<>();
    Adapter adapter;
    ListView listView;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Yeni Sohbet");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        listView = findViewById(R.id.listView);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        databaseReference.child(Child.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserInfo info = snapshot.getValue(UserInfo.class);
                    if (!info.getUid().equals(firebaseAuth.getUid())) {
                        list.add(new ListItem(info.getUid(), info.getName(), info.getPhotoUrl()));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, TalkActivity.class);
                intent.putExtra("aliciUid", list.get(position).getUid());
                intent.putExtra("aliciName", list.get(position).getName());
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottomBtn);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navBtnInbox) {
                    startActivity(new Intent(HomeActivity.this, InboxActivity.class));
                } else if (menuItem.getItemId() == R.id.navBtnProfil) {
                    startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    class Adapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.control_row_item_profil, null);
            }

            TextView textName = view.findViewById(R.id.textName);
            CircleImageView circleImageView = view.findViewById(R.id.circleImageView);

            textName.setText(list.get(position).getName());
            Helper.imageLoad(HomeActivity.this, list.get(position).getProfilUrl(), circleImageView);

            return view;
        }
    }

    class ListItem {
        String uid;
        String name;
        String profilUrl;

        public ListItem(String uid, String name, String profilUrl) {
            this.uid = uid;
            this.name = name;
            this.profilUrl = profilUrl;
        }

        public String getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public String getProfilUrl() {
            return profilUrl;
        }
    }
}
