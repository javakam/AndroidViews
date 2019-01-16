package com.work.recycler.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.recycler.group.bean.ItemParam;
import com.work.recycler.group.ours.Article;
import com.work.recycler.group.ours.ArticleAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Title: RecyclerGroupExpandActivity
 * <p>
 * Description:同时支持StickyHeader和Expand的RecyclerView
 * </p>
 *
 * @author Changbao
 * @date 2019/1/15  15:04
 */
public class RecyclerGroupExpandActivity extends BaseActivity {

    private GroupRecyclerView mRecyclerView;
    private ArticleAdapter mAdapter;
    private List<ItemParam> mData = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_groud_expand);

        initData();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new GroupItemDecoration<String, Article,String>());
        mRecyclerView.setAdapter(mAdapter = new ArticleAdapter(this, mData));
        mRecyclerView.notifyDataSetChanged();

    }

    private void initData() {

        LinkedHashMap<String, List<Article>> map = new LinkedHashMap<>();
        map.put("今日推荐", create(0));
        map.put("每周热点", create(1));
        map.put("最高评论", create(2));

        List<String> titles = new ArrayList<>();
        for (ItemParam<String, Article, String> data : mData) {
            mAdapter.resetGroups(map,data.getItemBean().getStickyData());
        }
    }

    private List<Article> create(int p) {
        List<Article> list = new ArrayList<>();
        Article a1, a2;
        if (p == 0) {
            List<String> titles = new ArrayList<>();
            titles.add("今日推荐");

            a1 = create("新西兰克马德克群岛发生5.7级地震 震源深度10千米",
                    "#地震快讯#中震，震源深度10千米。");
            a2 = create("俄罗斯喊冤不当\"背锅侠\" 俄美陷入\"后真相\"旋涡",
                    "“差到令人震惊”，但不怪特朗普韦杰夫近来连遭美国“恶那么重要了。");
            list.add(a1);
            list.add(a2);

            List<String> childData = new ArrayList<>();
            childData.add("aaa");
            childData.add("bbb");
            mData.add(new ItemParam<String, Article, String>(titles, a1, childData));
            mData.add(new ItemParam<String, Article, String>(titles, a2, childData));

        } else if (p == 1) {
            List<String> titles = new ArrayList<>();
            titles.add("每周热点");

            list.add(a1 = create("亚马逊CTO：我们要让人类成为机器人的中心！",
                    "那些相信应用下载会让世界变得更美好的智能手机上未能实现信息的民主化。"));
            list.add(a2 = create("有特斯拉车主想用免费的充电桩挖矿，但这可能行不通",
                    "在社交网络 Facebook 上的一个特斯拉车主群组中，有人开免费获得的电力挖矿了。"));

            List<String> childData = new ArrayList<>();
            childData.add("ccc");
            childData.add("ddd");
            mData.add(new ItemParam<String,Article, String>(titles,a1, childData));
            mData.add(new ItemParam<String,Article, String>(titles,a2, childData));
        } else if (p == 2) {
            List<String> titles = new ArrayList<>();
            titles.add("最高评论");

            list.add(a1 = create("2017年进入尾声，苹果大笔押注的ARkit还好么？",
                    "谷歌推出了AR眼镜、ARCore平台和应用在手机上R效果的深度传感器。"));
            list.add(a2 = create("亚马逊CTO：我们要让人类成为机器人的中心！",
                    "那些相信应用下载会让世界变得更美好的有这些都未能实现信息的民主化。"));

            List<String> childData = new ArrayList<>();
            childData.add("qqq");
            childData.add("wwww");
            mData.add(new ItemParam<String,Article, String>(titles,a1, childData));
            mData.add(new ItemParam<String,Article, String>(titles,a2, childData));
        }
        return list;
    }

    private Article create(String title, String content) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        return article;
    }
}
