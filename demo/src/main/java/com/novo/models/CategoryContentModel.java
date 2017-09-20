package com.novo.models;

import java.util.List;

/**
 * Created by ayushgarg on 15/09/17.
 */

public class CategoryContentModel {

    private String title;
    private String subTitle;
    private List<CategoryItemModel> itemModels;

    public CategoryContentModel() {
    }

    public CategoryContentModel(String title, String subTitle, List<CategoryItemModel> itemModels) {
        this.title = title;
        this.subTitle = subTitle;
        this.itemModels = itemModels;
    }

    public String getTitle() {
        return title;
    }

    public CategoryContentModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public CategoryContentModel setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public List<CategoryItemModel> getItemModels() {
        return itemModels;
    }

    public CategoryContentModel setItemModels(List<CategoryItemModel> itemModels) {
        this.itemModels = itemModels;
        return this;
    }
}
