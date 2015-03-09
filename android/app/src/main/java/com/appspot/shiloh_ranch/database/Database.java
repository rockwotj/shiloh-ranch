package com.appspot.shiloh_ranch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.appspot.shiloh_ranch.api.model.Category;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.api.model.Sermon;

import java.util.ArrayList;
import java.util.List;

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

    // Instance Field
    private SQLiteDatabase mDatabase;
    private DatabaseHelper mOpenHelper;

    public Database(Context context) {
        mOpenHelper = new DatabaseHelper(context);
    }

    public void open() {
        mDatabase = mOpenHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public void insertPost(Post post) {

    }

    public void insertSermon(Sermon sermon) {

    }

    public void insertEvent(Event event) {

    }

    public void insetCategory(Category category) {

    }

    public Post getPost(String entityKey) {

    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        Cursor cursor = mDatabase.query(TABLE_POST, null, null, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        posts.clear();
        do {
            posts.add(getPostFromCursor(cursor));
        } while (cursor.moveToNext());
        // TODO: Sort results by Date
        return posts;
    }

    public List<Post> getAllPosts(String categoryKey) {

    }

    private ContentValues getContentValues(Post post) {
        ContentValues row = new ContentValues();
        row.put(KEY_ENTITY, post.getEntityKey());
        row.put(KEY_TITLE, post.getTitle());
        row.put(KEY_CATEGORY, post.getCategory());
        row.put(KEY_CONTENT, post.getContent());
        row.put(KEY_DATE, post.getDate());
        return row;
    }

    private ContentValues getContentValues(Sermon sermon) {
        ContentValues row = new ContentValues();
        row.put(KEY_ENTITY, sermon.getEntityKey());
        row.put(KEY_TITLE, sermon.getTitle());
        row.put(KEY_AUDIO, sermon.getAudioLink());
        row.put(KEY_DATE, sermon.getDate());
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
        return post;
    }

    private Sermon getSermonFromCursor(Cursor cursor) {
        Sermon sermon = new Sermon();
        sermon.setEntityKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENTITY)));
        sermon.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        sermon.setAudioLink(cursor.getString(cursor.getColumnIndexOrThrow(KEY_AUDIO)));
        sermon.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
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
            sb.append(");");
            CREATE_TABLE_POST = sb.toString();
            // SERMON
            sb = new StringBuilder();
            sb.append("CREATE TABLE " + TABLE_SERMON + "(");
            sb.append(KEY_ENTITY + " text primary key, ");
            sb.append(KEY_TITLE + " text, ");
            sb.append(KEY_AUDIO + " text, ");
            sb.append(KEY_DATE + " text");
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
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SERMON);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            // create new tables
            onCreate(sqLiteDatabase);
        }
    }


}
