package com.outerspace.baking;

import androidx.core.util.Consumer;

import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.model.RecipeModel;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RecipeModelTest {

    private class TestConsumer<T> implements Consumer<T> {
        private T t;

        public T getList() { return t; }

        @Override
        public void accept(T t) {
            this.t = t;
        }
    };

    @Test
    public void fetchRecipeListTest() {

        TestConsumer<List<Recipe>> consumer = new TestConsumer<>();

        RecipeModel.fetchRecipeList(consumer);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Recipe> recipes = consumer.getList();
        Assert.assertNotNull(recipes);
    }
}