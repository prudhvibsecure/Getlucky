package com.bsecure.getlucky.store;

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
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.adpters.CategoryListAdapter;
import com.bsecure.getlucky.adpters.KeywordsListAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.models.KeyWords;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryKeysSearch extends AppCompatActivity implements CategoryListAdapter.KeywordsListListener{
    private SearchView searchView;
    private ArrayList<KeyWords> keys_list=new ArrayList<>();;
    private ArrayList<KeyWords> keys_list_edit=new ArrayList<>();
    private CategoryListAdapter adapter;
    private RecyclerView mRecyclerView;
    private ArrayList<String> keywords=new ArrayList<>();
    private ArrayList<String> keywords_ids=new ArrayList<>();
    String my_key="",ids="",m_select_list="";
    private CheckBox checkBox;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store_keys_view);

        checkBox= findViewById(R.id.check_all);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Categories");
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
                        ids=ids+","+keywords_ids.get(i);
                    }
                    my_key=my_key.replaceFirst(",","");
                    ids=ids.replaceFirst(",","");
                    Intent intent = new Intent();
                    intent.putExtra("keys_data", my_key.trim());
                    intent.putExtra("keys_ids", ids.trim());
                   // intent.putExtra("keys_json", new JSONArray(ids));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
        try{

            String category = AppPreferences.getInstance(this).getFromStore("category");
            JSONArray catarry = new JSONArray(category);
            if (catarry.length() > 0) {
                for (int f = 0; f < catarry.length(); f++) {
                    KeyWords keyWords=new KeyWords();
                    JSONObject oob = catarry.getJSONObject(f);
                    keyWords.setKeyword(oob.optString("category_name"));
                    keyWords.setId(oob.optString("category_id"));
                    keys_list.add(keyWords);
                }
                adapter = new CategoryListAdapter(keys_list, this, this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(adapter);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        String cat_data=getIntent().getStringExtra("cat_selected_keys");
        if (cat_data.length()!=0){
            try {
            JSONArray catarry = new JSONArray(cat_data);
            if (catarry.length() > 0) {
                for (int k = 0; k < catarry.length(); k++) {
                    KeyWords keyWords=new KeyWords();
                    JSONObject oob = catarry.getJSONObject(k);
                    keyWords.setId(oob.optString("category_id"));
                    keys_list_edit.add(keyWords);
                  //  m_select_list=m_select_list+","+oob.optString("category_id");
                }
                //m_select_list=m_select_list.replaceFirst(",","");
                adapter.setCat(keys_list_edit);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        checkBox .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    adapter.selectAll();
                }
                else {
                    adapter.unselectall();
                }


            }
        });
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
            keywords_ids.add(matchesList.get(pos).getId());
        }else{
            keywords.remove(matchesList.get(pos).getKeyword());
            keywords_ids.remove(matchesList.get(pos).getId());
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
