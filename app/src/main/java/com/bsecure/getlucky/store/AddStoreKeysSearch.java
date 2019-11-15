package com.bsecure.getlucky.store;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.adpters.KeywordsListAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.models.KeyWords;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddStoreKeysSearch extends AppCompatActivity implements KeywordsListAdapter.KeywordsListListener{
    private SearchView searchView;
    private ArrayList<KeyWords> keys_list;
    private KeywordsListAdapter adapter;
    private RecyclerView mRecyclerView;
    private ArrayList<String> keywords=new ArrayList<>();
    String my_key="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store_keys_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Keywords");
        mRecyclerView=findViewById(R.id.mrecycler);
        whiteNotificationBar(mRecyclerView);
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (keywords.isEmpty()){
                    finish();
                }else {
                    for ( int i=0;i<keywords.size();i++){
                         my_key=my_key+","+keywords.get(i);
                    }
                    my_key=my_key.replaceFirst(",","");
                    Intent intent = new Intent();
                    intent.putExtra("keys_data", my_key.trim());
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
        try{
            keys_list=new ArrayList<>();
            String category = AppPreferences.getInstance(this).getFromStore("keywords");
            JSONArray catarry = new JSONArray(category);
            if (catarry.length() > 0) {
                for (int f = 0; f < catarry.length(); f++) {
                    KeyWords keyWords=new KeyWords();
                    JSONObject oob = catarry.getJSONObject(f);
                    keyWords.setKeyword(oob.optString("keyword"));
                    keys_list.add(keyWords);
                }
                adapter = new KeywordsListAdapter(keys_list, this, this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(adapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRowClicked(List<KeyWords> matchesList, int pos, CheckBox checkBox) {

        if (checkBox.isChecked()){
            keywords.add(matchesList.get(pos).getKeyword());
        }else{
            keywords.remove(matchesList.get(pos).getKeyword());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
