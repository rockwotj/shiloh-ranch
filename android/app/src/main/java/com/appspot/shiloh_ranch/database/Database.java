package com.appspot.shiloh_ranch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.model.Category;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.api.model.Sermon;
import com.google.api.client.json.GenericJson;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rockwotj on 3/8/2015.
 */
public class Database {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "shiloh_ranch.db";

    // Table Names
    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_POST = "posts";
    private static final String TABLE_SERMON = "sermons";
    private static final String TABLE_EVENT = "events";

    // Common Columns
    private static final String KEY_ENTITY = "entity_key";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_TIME_ADDED = "time_added";

    // Category Table - column names
    private static final String KEY_ID = "id";

    // Post Table - column names
    private static final String KEY_CATEGORY = "category";

    // Sermon Table - column names
    private static final String KEY_AUDIO = "audio_link";

    // Event Table - column names
    private static final String KEY_EXCERPT = "excerpt";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TIME = "time";
    private static final String KEY_ATTACHMENT = "attachment";

    // Instance Fields
    private SQLiteDatabase mDatabase;
    private DatabaseHelper mOpenHelper;
    private static Map<Class<? extends GenericJson>, String> mTableMap;

    static {
        mTableMap = new HashMap<>(4);
        mTableMap.put(Event.class, TABLE_EVENT);
        mTableMap.put(Post.class, TABLE_POST);
        mTableMap.put(Sermon.class, TABLE_SERMON);
        mTableMap.put(Category.class, TABLE_CATEGORY);
    }

    // Singleton Instance
    private static Database instance;

    public synchronized static Database getDatabase(Context context) {
        if (instance == null) {
            instance = new Database(context);
            instance.open();
        }
        return instance;
    }

    private Database(Context context) {
        mOpenHelper = new DatabaseHelper(context);
    }

    private void open() {
        mDatabase = mOpenHelper.getWritableDatabase();
    }

    /**
     * You probably don't wanna close it.
     */
    private void close() {
        mDatabase.close();
    }

    /**
     * We will synchronize the DB so that we don't get any problems there.
     * @return the database
     */
    private synchronized SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public void insert(Post post) {
        ContentValues row = getContentValues(post);
        long id = getDatabase().insert(TABLE_POST, null, row);
        if (id == -1) {
            Log.e("SRCC", "INSERT FAILED FOR POST!");
        }
    }


    public void insert(Sermon sermon) {
        ContentValues row = getContentValues(sermon);
        long id = getDatabase().insert(TABLE_SERMON, null, row);
        if (id == -1) {
            Log.e("SRCC", "INSERT FAILED FOR SERMON!");
        }
    }

    public void insert(Event event) {
        ContentValues row = getContentValues(event);
        long id = getDatabase().insert(TABLE_EVENT, null, row);
        if (id == -1) {
            Log.e("SRCC", "INSERT FAILED FOR EVENT!");
        }
    }

    public void insert(Category category) {
        ContentValues row = getContentValues(category);
        long id = getDatabase().insert(TABLE_CATEGORY, null, row);
        if (id == -1) {
            Log.e("SRCC", "INSERT FAILED FOR CATEGORY!");
        }
    }

    public void delete(Class<? extends GenericJson> kind, String entityKey) {
        delete(mTableMap.get(kind), entityKey);
    }

    public void delete(Post post) {
        delete(TABLE_POST, post.getEntityKey());
    }

    public void delete(Sermon sermon) {
        delete(TABLE_SERMON, sermon.getEntityKey());
    }

    public void delete(Event event) {
        delete(TABLE_EVENT, event.getEntityKey());
    }

    public void delete(Category category) {
        delete(TABLE_CATEGORY, category.getEntityKey());
    }

    private void delete(String tableName, String key) {
        getDatabase().delete(tableName, KEY_ENTITY + " = ?", new String[]{key});
    }

    public void update(Post post) {
        ContentValues row = getContentValues(post);
        getDatabase().update(TABLE_POST, row, KEY_ENTITY + " = ?", new String[]{post.getEntityKey()});
    }

    public void update(Sermon sermon) {
        ContentValues row = getContentValues(sermon);
        getDatabase().update(TABLE_SERMON, row, KEY_ENTITY + " = ?", new String[]{sermon.getEntityKey()});
    }

    public void update(Event event) {
        ContentValues row = getContentValues(event);
        getDatabase().update(TABLE_EVENT, row, KEY_ENTITY + " = ?", new String[]{event.getEntityKey()});
    }

    public void update(Category category) {
        ContentValues row = getContentValues(category);
        getDatabase().update(TABLE_CATEGORY, row, KEY_ENTITY + " = ?", new String[]{category.getEntityKey()});
    }

    /**
     * Gets a single Post from the database
     *
     * @param entityKey the Post's Key
     * @return the Post matching the Key
     */
    public Post getPost(String entityKey) {
        String whereClause = KEY_CATEGORY + "=" + "'" + entityKey + "'";
        Cursor cursor = getDatabase().query(TABLE_POST, null, whereClause, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        return getPostFromCursor(cursor);
    }

    /**
     * Gets all of the posts in the database, sorted by timeAdded
     *
     * @return All Posts, sorted by timeAdded
     */
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        Cursor cursor = getDatabase().query(TABLE_POST, null, null, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return new ArrayList<>();
        }
        do {
            posts.add(getPostFromCursor(cursor));
        } while (cursor.moveToNext());
        Collections.sort(posts, DateTimeUtils.getModelDateComparator());
        return posts;
    }

    /**
     * Gets all of the posts under a category in the database, sorted by timeAdded
     *
     * @param categoryKey the entityKey of the category
     * @return All Posts, sorted by timeAdded
     */
    public List<Post> getAllPosts(String categoryKey) {
        List<Post> posts = new ArrayList<>();
        String whereClause = (categoryKey == null) ? null : KEY_CATEGORY + "=" + "'" + categoryKey + "'";
        Cursor cursor = getDatabase().query(TABLE_POST, null, whereClause, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return new ArrayList<>();
        }
        do {
            posts.add(getPostFromCursor(cursor));
        } while (cursor.moveToNext());
        Collections.sort(posts, DateTimeUtils.getModelDateComparator());
        return posts;
    }


    /**
     * Gets a single Category from the database
     *
     * @param entityKey the Category's Key
     * @return the Category matching the Key
     */
    public Category getCategory(String entityKey) {
        String whereClause = KEY_CATEGORY + "=" + "'" + entityKey + "'";
        Cursor cursor = getDatabase().query(TABLE_CATEGORY, null, whereClause, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        return getCategoryFromCursor(cursor);
    }

    /**
     * Gets all of the categories in the database, sorted alphabetically
     *
     * @return All categories, sorted alphabetically
     */
    public List<Category> getAllCategories() {
        final List<Category> categories = new ArrayList<>();
        Cursor cursor = getDatabase().query(TABLE_CATEGORY, null, null, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return new ArrayList<>();
        }
        do {
            categories.add(getCategoryFromCursor(cursor));
        } while (cursor.moveToNext());
        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category category, Category category2) {
                return category.getTitle().compareTo(category2.getTitle());
            }
        });
        return categories;
    }

    /**
     * Gets a single event from the database
     *
     * @param entityKey the event's Key
     * @return the event matching the Key
     */
    public Event getEvent(String entityKey) {
        String whereClause = KEY_CATEGORY + "=" + "'" + entityKey + "'";
        Cursor cursor = getDatabase().query(TABLE_EVENT, null, whereClause, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        return getEventFromCursor(cursor);
    }

    /**
     * Gets all of the events in the database, sorted by timeAdded
     *
     * @return All events, sorted by timeAdded
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        Cursor cursor = getDatabase().query(TABLE_EVENT, null, null, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return new ArrayList<>();
        }
        do {
            events.add(getEventFromCursor(cursor));
        } while (cursor.moveToNext());
        Collections.sort(events, DateTimeUtils.getModelDateComparator());
        return events;
    }

    /**
     * Gets a single sermon from the database
     *
     * @param entityKey the sermon's Key
     * @return the sermon matching the Key
     */
    public Sermon getSermon(String entityKey) {
        String whereClause = KEY_CATEGORY + "=" + "'" + entityKey + "'";
        Cursor cursor = getDatabase().query(TABLE_SERMON, null, whereClause, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        return getSermonFromCursor(cursor);
    }

    /**
     * Gets all of the Sermons in the database, sorted by timeAdded
     *
     * @return All Sermons, sorted by timeAdded
     */
    public List<Sermon> getAllSermons() {
        List<Sermon> sermons = new ArrayList<>();
        Cursor cursor = getDatabase().query(TABLE_SERMON, null, null, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return new ArrayList<>();
        }
        do {
            sermons.add(getSermonFromCursor(cursor));
        } while (cursor.moveToNext());
        Collections.sort(sermons, DateTimeUtils.getModelDateComparator());
        return sermons;
    }


    // Private Helper Methods
    private ContentValues getContentValues(Post post) {
        ContentValues row = new ContentValues();
        row.put(KEY_ENTITY, post.getEntityKey());
        row.put(KEY_TITLE, post.getTitle());
        row.put(KEY_CATEGORY, post.getCategory());
        row.put(KEY_CONTENT, post.getContent());
        row.put(KEY_DATE, post.getDate());
        row.put(KEY_TIME_ADDED, post.getTimeAdded());
        return row;
    }

    private ContentValues getContentValues(Sermon sermon) {
        ContentValues row = new ContentValues();
        row.put(KEY_ENTITY, sermon.getEntityKey());
        row.put(KEY_TITLE, sermon.getTitle());
        row.put(KEY_AUDIO, sermon.getAudioLink());
        row.put(KEY_DATE, sermon.getDate());
        row.put(KEY_TIME_ADDED, sermon.getTimeAdded());
        return row;
    }

    private ContentValues getContentValues(Event event) {
        ContentValues row = new ContentValues();
        row.put(KEY_ENTITY, event.getEntityKey());
        row.put(KEY_TITLE, event.getTitle());
        row.put(KEY_EXCERPT, event.getExcerpt());
        row.put(KEY_CONTENT, event.getContent());
        row.put(KEY_ATTACHMENT, event.getAttachment());
        row.put(KEY_DATE, event.getDatePublished());
        row.put(KEY_LOCATION, event.getLocation());
        row.put(KEY_TIME, event.getTime());
        row.put(KEY_TIME_ADDED, event.getTimeAdded());
        return row;
    }

    private ContentValues getContentValues(Category category) {
        ContentValues row = new ContentValues();
        row.put(KEY_ENTITY, category.getEntityKey());
        row.put(KEY_ID, category.getId());
        row.put(KEY_TITLE, category.getTitle());
        return row;
    }

    private Post getPostFromCursor(Cursor cursor) {
        Post post = new Post();
        post.setEntityKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENTITY)));
        post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        post.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));
        post.setContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTENT)));
        post.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
        post.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME_ADDED)));
        return post;
    }

    private Sermon getSermonFromCursor(Cursor cursor) {
        Sermon sermon = new Sermon();
        sermon.setEntityKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENTITY)));
        sermon.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        sermon.setAudioLink(cursor.getString(cursor.getColumnIndexOrThrow(KEY_AUDIO)));
        sermon.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
        sermon.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME_ADDED)));
        return sermon;
    }

    private Event getEventFromCursor(Cursor cursor) {
        Event event = new Event();
        event.setEntityKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENTITY)));
        event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        event.setExcerpt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXCERPT)));
        event.setContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTENT)));
        event.setAttachment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ATTACHMENT)));
        event.setDatePublished(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
        event.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)));
        event.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)));
        event.setTimeAdded(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME_ADDED)));
        return event;
    }

    private Category getCategoryFromCursor(Cursor cursor) {
        Category category = new Category();
        category.setEntityKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENTITY)));
        category.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        category.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        return category;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        // Table Create Statements
        private static final String CREATE_TABLE_CATEGORY;
        private static final String CREATE_TABLE_POST;
        private static final String CREATE_TABLE_SERMON;
        private static final String CREATE_TABLE_EVENT;

        static {
            // Category
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE " + TABLE_CATEGORY + "(");
            sb.append(KEY_ENTITY + " text primary key, ");
            sb.append(KEY_ID + " Integer, ");
            sb.append(KEY_TITLE + " text");
            sb.append(");");
            CREATE_TABLE_CATEGORY = sb.toString();
            // POST
            sb = new StringBuilder();
            sb.append("CREATE TABLE " + TABLE_POST + "(");
            sb.append(KEY_ENTITY + " text primary key, ");
            sb.append(KEY_TITLE + " text, ");
            sb.append(KEY_CATEGORY + " text, ");
            sb.append(KEY_CONTENT + " text, ");
            sb.append(KEY_DATE + " text");
            sb.append(KEY_TIME_ADDED + " text");
            sb.append(");");
            CREATE_TABLE_POST = sb.toString();
            // SERMON
            sb = new StringBuilder();
            sb.append("CREATE TABLE " + TABLE_SERMON + "(");
            sb.append(KEY_ENTITY + " text primary key, ");
            sb.append(KEY_TITLE + " text, ");
            sb.append(KEY_AUDIO + " text, ");
            sb.append(KEY_DATE + " text");
            sb.append(KEY_TIME_ADDED + " text");
            sb.append(");");
            CREATE_TABLE_SERMON = sb.toString();
            // EVENT
            sb = new StringBuilder();
            sb.append("CREATE TABLE " + TABLE_EVENT + "(");
            sb.append(KEY_ENTITY + " text primary key, ");
            sb.append(KEY_TITLE + " text, ");
            sb.append(KEY_EXCERPT + " text, ");
            sb.append(KEY_CONTENT + " text, ");
            sb.append(KEY_ATTACHMENT + " text, ");
            sb.append(KEY_DATE + " text, ");
            sb.append(KEY_LOCATION + " text, ");
            sb.append(KEY_TIME + " text");
            sb.append(KEY_TIME_ADDED + " text");
            sb.append(");");
            CREATE_TABLE_EVENT = sb.toString();

        }

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORY);
            sqLiteDatabase.execSQL(CREATE_TABLE_EVENT);
            sqLiteDatabase.execSQL(CREATE_TABLE_SERMON);
            sqLiteDatabase.execSQL(CREATE_TABLE_POST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            // on upgrade drop older tables
            // Probably should make a more elegant transfer of data later
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SERMON);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            // create new tables
            onCreate(sqLiteDatabase);
        }
    }


}
