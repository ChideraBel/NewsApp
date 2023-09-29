package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.newsapp.databinding.ActivityMainBinding;
import com.example.newsapp.databinding.DrawerItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String[] sourceItems;
    private List<Source> sourceList = new ArrayList<>();
    private List<String> menuItems = new ArrayList<>();
    private Menu menu;
    private Hashtable<String, Integer> itemColors = new Hashtable<>();
    private ArrayList<Article> articles = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewsDownloaderVolley.downloadNews(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        articleAdapter = new ArticleAdapter(this, this.articles);
        binding.viewPager2.setAdapter(articleAdapter);
        binding.viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager2.setVisibility(View.INVISIBLE);

        binding.leftDrawer.setOnItemClickListener(
                (parent, view, position, id) -> chooseItem(position));

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                binding.drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    public void updateDrawer(List<Source> sources){
        sourceItems = new String[sources.size()];
        for (int i = 0; i < sourceItems.length; i++)
            sourceItems[i] = sources.get(i).getName();

        binding.leftDrawer.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_item, sourceItems){
            DrawerItemBinding drawerItemBinding;
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
                int textColor = itemColors.get(sources.get(position).getCategory());
                View view = super.getView(position, convertView, parent);
                drawerItemBinding = DrawerItemBinding.bind(view);
                drawerItemBinding.text1.setTextColor(textColor);
                return view;
            }
        });
        this.setTitle("News App ("+sources.size()+")");
    }


    public void updateMenu(List<String> categories, List<Source> sources, List<Integer> categoryColors){
        int menuCount = 1;

        sourceList.clear();
        menuItems.clear();

        sortCat(categories);
        menu.clear();
        menu.add("all");

        for(String category : menuItems){

            menu.add(category);
            MenuItem item = menu.getItem(menuCount);

            itemColors.put(category, categoryColors.get(menuCount-1));
            SpannableString s = new SpannableString(category);
            s.setSpan(new ForegroundColorSpan(categoryColors.get(menuCount-1)), 0, s.length(), 0);
            item.setTitle(s);

            menuCount++;
        }

        sourceList.addAll(sources);

        updateDrawer(sourceList);
    }

    private void chooseItem(int pos) {
        String sourceName = sourceItems[pos];
        String sourceID = sourceList.get(pos).getId();

        NewsDownloaderVolley.downloadArticle(this, sourceID);
        this.setTitle(sourceName);
        binding.drawerLayout.closeDrawer(binding.constraintLayout);
        binding.viewPager2.setVisibility(View.INVISIBLE);
    }

    public void updateNewsArticles(List<Article> articleObjs) {
        int prevSize = articles.size();
        if(articleObjs.size() > 0)
            binding.viewPager2.setVisibility(View.VISIBLE);
        else
            binding.viewPager2.setVisibility(View.INVISIBLE);

        articles.clear();
        if(articleObjs.size() >= 10) {
            for (int i = 0; i < 10; i++) {
                articles.add(articleObjs.get(i));
            }
        }else {
            for (int i = 0; i < articleObjs.size(); i++) {
                articles.add(articleObjs.get(i));
            }
        }
        articleAdapter.notifyItemRangeRemoved(0, prevSize);
        articleAdapter.notifyItemRangeChanged(0, articles.size());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Check drawer first!
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d("TAG", "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }else{
            binding.viewPager2.setVisibility(View.INVISIBLE);
        }

        itemSelection(item.getTitle().toString());
        return super.onOptionsItemSelected(item);
    }

    public void itemSelection(String item){
        List<Source> categorySource = new ArrayList<>();
        if(item.equals("all")){
            categorySource.addAll(sourceList);
        }else{
            for(Source source: sourceList){
                if(source.getCategory().equals(item)){
                    categorySource.add(source);
                }
            }
        }
        updateDrawer(categorySource);
    }

    public void sortCat(List<String> categories){
        Collections.sort(categories, new Comparator<String>()
        {
            @Override
            public int compare(String text1, String text2)
            {
                return text1.compareToIgnoreCase(text2);
            }
        });
        menuItems.addAll(categories);
    }
    public void openLink(View view){
        String websiteAddress = this.articles.get(binding.viewPager2.getCurrentItem()).getUrl();
        if(!websiteAddress.equals("") || view.getVisibility() == View.VISIBLE){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(websiteAddress)));
        }
    }
}