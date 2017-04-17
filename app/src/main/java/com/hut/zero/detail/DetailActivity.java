package com.hut.zero.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hut.zero.R;
import com.hut.zero.bean.BeanType;

public class DetailActivity extends AppCompatActivity {
    private DetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        if (savedInstanceState != null) {
            fragment = (DetailFragment) getSupportFragmentManager().getFragment(savedInstanceState,"detailFragment");
        } else {
            fragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

        Intent intent = getIntent();
        DetailPresenter presenter = new DetailPresenter(DetailActivity.this, fragment);
        presenter.setType((BeanType) intent.getSerializableExtra("type"));
        presenter.setId(intent.getIntExtra("id", 0));
        presenter.setTitle(intent.getStringExtra("title"));
        presenter.setCoverUrl(intent.getStringExtra("coverUrl"));
        presenter.logMsg();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "detailFragment", fragment);
        }
    }
}
