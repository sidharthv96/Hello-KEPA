package com.sidharth.hellokpa.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Category {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<CategoryItem> ITEMS = new ArrayList<CategoryItem>();

    public static final String[] cats = {"Higher Officers of Kerala Police","Officers KEPA","SCPOs/CPOs KEPA","Ministerial Staff","CFs and PTS"};
    /**
     * A map of sample (dummy) items, by ID.
     */


    static {
        // Add some sample items.
        for (int i = 1; i <= cats.length; i++) {
            addItem(createCategoryItem(i));
        }
    }

    private static void addItem(CategoryItem item) {
        ITEMS.add(item);
    }

    private static CategoryItem createCategoryItem(int position) {
        return new CategoryItem(position, cats[position-1], makeDetails(position));
    }

    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
//        }
//        return builder.toString();
        return ".";
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class CategoryItem {
        public final int id;
        public final String content;
        public final String details;

        public CategoryItem(int id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
