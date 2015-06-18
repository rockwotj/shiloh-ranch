//
//  Database.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/11/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import Foundation

class Database {
    
    // Table Names
    private let TABLE_CATEGORY = "categories";
    private let TABLE_POST = "posts";
    private let TABLE_SERMON = "sermons";
    private let TABLE_EVENT = "events";
    
    // Common Columns
    private let KEY_ENTITY = "entity_key";
    private let KEY_TITLE = "title";
    private let KEY_DATE = "date";
    private let KEY_CONTENT = "content";
    private let KEY_TIME_ADDED = "time_added";
    
    // Category Table - column names
    private let KEY_ID = "id";
    
    // Post Table - column names
    private let KEY_CATEGORY = "category";
    
    // Sermon Table - column names
    private let KEY_AUDIO = "audio_link";
    
    // Event Table - column names
    private let KEY_LOCATION = "location";
    private let KEY_END_TIME = "start_time";
    private let KEY_START_TIME = "end_time";
    private let KEY_REPEAT = "repeat";
    
    private let database : FMDatabaseQueue
    
    init () {
        let filemgr = NSFileManager.defaultManager()
        let databasePath = getDatabasePath()
        if !filemgr.fileExistsAtPath(databasePath) {
            let db = FMDatabase(path: databasePath)
            if db == nil {
                println("Error: \(db.lastErrorMessage())")
            }
            if db.open() {
                let create_table = "CREATE TABLE IF NOT EXISTS "
                var create_categories_table = "\(create_table) \(TABLE_CATEGORY) ("
                create_categories_table += "\(KEY_ENTITY) TEXT PRIMARY KEY, "
                create_categories_table += "\(KEY_ID) INTEGER, "
                create_categories_table += "\(KEY_TITLE) TEXT);"
                var create_posts_table = "\(create_table) \(TABLE_POST) ("
                create_posts_table += "\(KEY_ENTITY) TEXT PRIMARY KEY, "
                create_posts_table += "\(KEY_TITLE) TEXT, "
                create_posts_table += "\(KEY_CATEGORY) TEXT, "
                create_posts_table += "\(KEY_CONTENT) TEXT, "
                create_posts_table += "\(KEY_DATE) TEXT, "
                create_posts_table += "\(KEY_TIME_ADDED) TEXT);"
                var create_sermons_table = "\(create_table) \(TABLE_SERMON) ("
                create_sermons_table += "\(KEY_ENTITY) TEXT PRIMARY KEY, "
                create_sermons_table += "\(KEY_TITLE) TEXT, "
                create_sermons_table += "\(KEY_AUDIO) TEXT, "
                create_sermons_table += "\(KEY_DATE) TEXT, "
                create_sermons_table += "\(KEY_TIME_ADDED) TEXT);"
                var create_events_table = "\(create_table) \(TABLE_EVENT) ("
                create_events_table += "\(KEY_ENTITY) TEXT PRIMARY KEY, "
                create_events_table += "\(KEY_TITLE) TEXT, "
                create_events_table += "\(KEY_REPEAT) TEXT, "
                create_events_table += "\(KEY_CONTENT) TEXT, "
                create_events_table += "\(KEY_START_TIME) TEXT, "
                create_events_table += "\(KEY_LOCATION) TEXT, "
                create_events_table += "\(KEY_END_TIME) TEXT, "
                create_events_table += "\(KEY_TIME_ADDED) TEXT);"
                if !db.executeStatements(create_categories_table) {
                    println("Error [CREATE CATEGORY TABLE]: \(db.lastErrorMessage())")
                }
                if !db.executeStatements(create_posts_table) {
                    println("Error [CREATE POSTS]: \(db.lastErrorMessage())")
                }
                if !db.executeStatements(create_sermons_table) {
                    println("Error [CREATE SERMONS]: \(db.lastErrorMessage())")
                }
                if !db.executeStatements(create_events_table) {
                    println("Error [CREATE EVENTS]: \(db.lastErrorMessage())")
                }
                db.close()
            } else {
                println("Error: \(db.lastErrorMessage())")
            }
        }
        self.database = FMDatabaseQueue(path: databasePath)
    }
    
    func getAllCategories(callback : ([GTLShilohranchCategory]) -> Void)   {
        database.inDatabase { (db) in
            var categories : [GTLShilohranchCategory] = []
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_CATEGORY)", withArgumentsInArray: nil) {
                while rs.next() {
                    let category = GTLShilohranchCategory()
                    category.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    category.title = rs.stringForColumn(self.KEY_TITLE)
                    categories.append(category)
                }
            } else {
                println("Selecting categories failed: \(db.lastErrorMessage())")
            }
            categories.sort { $0.title < $1.title }
            callback(categories)
        }
    }
    
    func getCategory(entityKey : String, callback : (GTLShilohranchCategory?) -> Void)   {
        database.inDatabase { (db) in
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_CATEGORY) WHERE \(self.KEY_ENTITY)=?", entityKey) {
                if rs.next() {
                    let category = GTLShilohranchCategory()
                    category.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    category.title = rs.stringForColumn(self.KEY_TITLE)
                    category.timeAdded = rs.stringForColumn(self.KEY_TIME_ADDED)
                    callback(category)
                    return
                }
            } else {
                println("Selecting category failed: \(db.lastErrorMessage())")
            }
            callback(nil)
        }
    }
    
    func getAllPosts(callback : ([GTLShilohranchPost]) -> Void) {
        database.inDatabase { (db) in
            var posts : [GTLShilohranchPost] = []
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_POST)", withArgumentsInArray: nil) {
                while rs.next() {
                    let post = GTLShilohranchPost()
                    post.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    post.title = rs.stringForColumn(self.KEY_TITLE)
                    post.category = rs.stringForColumn(self.KEY_CATEGORY)
                    post.content = rs.stringForColumn(self.KEY_CONTENT)
                    post.date = rs.stringForColumn(self.KEY_DATE)
                    post.timeAdded = rs.stringForColumn(self.KEY_TIME_ADDED)
                    posts.append(post)
                }
            } else {
                println("Selecting posts failed: \(db.lastErrorMessage())")
            }
            callback(posts)
        }
    }
    
    func getPost(entityKey : String, callback : (GTLShilohranchPost?) -> Void)  {
        database.inDatabase { (db) in
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_POST) WHERE \(self.KEY_ENTITY)=?", entityKey) {
                if rs.next() {
                    let post = GTLShilohranchPost()
                    post.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    post.title = rs.stringForColumn(self.KEY_TITLE)
                    post.category = rs.stringForColumn(self.KEY_CATEGORY)
                    post.content = rs.stringForColumn(self.KEY_CONTENT)
                    post.date = rs.stringForColumn(self.KEY_DATE)
                    post.timeAdded = rs.stringForColumn(self.KEY_TIME_ADDED)
                    callback(post)
                    return
                }
            } else {
                println("Selecting post failed: \(db.lastErrorMessage())")
            }
            callback(nil)
        }
    }
    
    func getAllEvents(callback : ([GTLShilohranchEvent]) -> Void) {
        database.inDatabase { (db) in
            var events : [GTLShilohranchEvent] = []
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_EVENT)") {
                while rs.next() {
                    let event = GTLShilohranchEvent()
                    event.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    event.title = rs.stringForColumn(self.KEY_TITLE)
                    event.repeat = rs.stringForColumn(self.KEY_REPEAT)
                    event.content = rs.stringForColumn(self.KEY_CONTENT)
                    event.startTime = rs.stringForColumn(self.KEY_START_TIME)
                    event.location = rs.stringForColumn(self.KEY_LOCATION)
                    event.endTime = rs.stringForColumn(self.KEY_END_TIME)
                    event.timeAdded = rs.stringForColumn(self.KEY_TIME_ADDED)
                    events.append(event)
                }
            } else {
                println("Selecting events failed: \(db.lastErrorMessage())")
            }
            callback(events)
        }
    }
    
    func getEvent(entityKey : String, callback : (GTLShilohranchEvent?) -> Void) {
        database.inDatabase { (db) in
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_EVENT) WHERE \(self.KEY_ENTITY)=?", entityKey) {
                if rs.next() {
                    let event = GTLShilohranchEvent()
                    event.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    event.title = rs.stringForColumn(self.KEY_TITLE)
                    event.repeat = rs.stringForColumn(self.KEY_REPEAT)
                    event.content = rs.stringForColumn(self.KEY_CONTENT)
                    event.startTime = rs.stringForColumn(self.KEY_START_TIME)
                    event.location = rs.stringForColumn(self.KEY_LOCATION)
                    event.endTime = rs.stringForColumn(self.KEY_END_TIME)
                    event.timeAdded = rs.stringForColumn(self.KEY_TIME_ADDED)
                    callback(event)
                    return
                }
            } else {
                println("Selecting event failed: \(db.lastErrorMessage())")
            }
            callback(nil)
        }
    }
    
    func getAllSermons(callback : ([GTLShilohranchSermon]) -> Void) {
        database.inDatabase { (db) in
            var sermons : [GTLShilohranchSermon] = []
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_SERMON)", withArgumentsInArray: nil) {
                while rs.next() {
                    let sermon = GTLShilohranchSermon()
                    sermon.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    sermon.title = rs.stringForColumn(self.KEY_TITLE)
                    sermon.audioLink = rs.stringForColumn(self.KEY_AUDIO)
                    sermon.date = rs.stringForColumn(self.KEY_DATE)
                    sermon.timeAdded = rs.stringForColumn(self.KEY_TIME_ADDED)
                    sermons.append(sermon)
                }
            } else {
                println("Selecting sermons failed: \(db.lastErrorMessage())")
            }
            callback(sermons)
        }
    }
    
    func getSermon(entityKey : String, callback : ([GTLShilohranchSermon]) -> Void) {
        database.inDatabase { (db) in
            var sermons : [GTLShilohranchSermon] = []
            if let rs = db.executeQuery("SELECT * FROM \(self.TABLE_SERMON) WHERE \(self.KEY_ENTITY)=?", entityKey) {
                while rs.next() {
                    let sermon = GTLShilohranchSermon()
                    sermon.entityKey = rs.stringForColumn(self.KEY_ENTITY)
                    sermon.title = rs.stringForColumn(self.KEY_TITLE)
                    sermon.audioLink = rs.stringForColumn(self.KEY_AUDIO)
                    sermon.date = rs.stringForColumn(self.KEY_DATE)
                    sermon.timeAdded = rs.stringForColumn(self.KEY_TIME_ADDED)
                    sermons.append(sermon)
                }
            } else {
                println("Selecting sermons failed: \(db.lastErrorMessage())")
            }
            callback(sermons)
        }
    }
    
    func insert(category : GTLShilohranchCategory) {
        database.inDatabase { (db) in
            db.executeUpdate("DELETE FROM \(self.TABLE_CATEGORY) WHERE \(self.KEY_ENTITY)=?", category.entityKey)
            let ok = db.executeUpdate("INSERT INTO \(self.TABLE_CATEGORY) (\(self.KEY_ENTITY), \(self.KEY_TITLE)) VALUES (?, ?)",
                category.entityKey, category.title)
            if !ok {
                println("Inserting Category Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func insert(post : GTLShilohranchPost) {
        let sqlStatement = "INSERT INTO \(TABLE_POST) " +
        "(\(KEY_ENTITY), \(KEY_TITLE), \(KEY_CATEGORY), \(KEY_CONTENT), \(KEY_DATE), \(KEY_TIME_ADDED)) VALUES (?, ?, ?, ?, ?, ?)"
        database.inDatabase { (db) in
            db.executeUpdate("DELETE FROM \(self.TABLE_POST) WHERE \(self.KEY_ENTITY)=?", post.entityKey)
            let ok = db.executeUpdate(sqlStatement, post.entityKey, post.title, post.category, post.content, post.date, post.timeAdded)
            if !ok {
                println("Inserting Post Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func insert(sermon : GTLShilohranchSermon) {
        let sqlStatement = "INSERT INTO \(TABLE_SERMON) (\(KEY_ENTITY), \(KEY_TITLE), \(KEY_AUDIO), \(KEY_DATE), \(KEY_TIME_ADDED)) VALUES (?, ?, ?, ?, ?)"
        database.inDatabase { (db) in
            db.executeUpdate("DELETE FROM \(self.TABLE_SERMON) WHERE \(self.KEY_ENTITY)=?", sermon.entityKey)
            let ok = db.executeUpdate(sqlStatement, sermon.entityKey, sermon.title, sermon.audioLink, sermon.date, sermon.timeAdded)
            if !ok {
                println("Inserting Sermon Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func insert(event : GTLShilohranchEvent) {
        let sqlStatement = "INSERT INTO \(TABLE_EVENT) " +
        "(\(KEY_ENTITY), \(KEY_TITLE), \(KEY_REPEAT), \(KEY_CONTENT), \(KEY_START_TIME), \(KEY_LOCATION), \(KEY_END_TIME), \(KEY_TIME_ADDED)) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        database.inDatabase { (db) in
            db.executeUpdate("DELETE FROM \(self.TABLE_EVENT) WHERE \(self.KEY_ENTITY)=?", event.entityKey)
            let ok = db.executeUpdate(sqlStatement, event.entityKey, event.title, event.repeat != nil ? event.repeat : "", event.content, event.startTime, event.location, event.endTime, event.timeAdded)
            if !ok {
                println("Inserting Event Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func update(category : GTLShilohranchCategory) {
        let sqlStatement = "UPDATE \(TABLE_CATEGORY) " +
            "SET \(KEY_TITLE)=? " +
            "WHERE \(KEY_ENTITY)=?;"
        database.inDatabase { (db) in
            let ok = db.executeUpdate(sqlStatement, category.title, category.entityKey)
            if !ok {
                println("Updating Category Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func update(post : GTLShilohranchPost) {
        let sqlStatement = "UPDATE \(TABLE_POST) " +
            "SET \(KEY_TITLE)=?, \(KEY_CATEGORY)=?, \(KEY_CONTENT)=?, \(KEY_DATE)=?, \(KEY_TIME_ADDED)=? " +
            "WHERE \(KEY_ENTITY)=?;"
        database.inDatabase { (db) in
            let ok = db.executeUpdate(sqlStatement, post.title, post.category, post.content, post.date, post.timeAdded, post.entityKey)
            if !ok {
                println("Updating Post Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func update(sermon : GTLShilohranchSermon) {
        let sqlStatement = "UPDATE \(TABLE_POST) " +
            "SET \(KEY_TITLE)=?, \(KEY_AUDIO)=?, \(KEY_DATE)=?, \(KEY_TIME_ADDED)=? " +
            "WHERE \(KEY_ENTITY)=?;"
        database.inDatabase { (db) in
            let ok = db.executeUpdate(sqlStatement, sermon.title, sermon.audioLink, sermon.date, sermon.timeAdded, sermon.entityKey)
            if !ok {
                println("Updating Sermon Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func update(event : GTLShilohranchEvent) {
        let sqlStatement = "UPDATE \(TABLE_POST) " +
            "SET  \(KEY_TITLE)=?, \(KEY_REPEAT)=?, \(KEY_CONTENT)=?, \(KEY_START_TIME)=?, \(KEY_LOCATION)=?, \(KEY_END_TIME)=?, \(KEY_TIME_ADDED)=? " +
            "WHERE \(KEY_ENTITY)=?;"
        database.inDatabase { (db) in
            let ok = db.executeUpdate(sqlStatement, event.title, event.repeat != nil ? event.repeat : "", event.content, event.startTime, event.location, event.endTime, event.timeAdded, event.entityKey)
            if !ok {
                println("Updating Event Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func delete(category : GTLShilohranchCategory) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_CATEGORY) WHERE \(self.KEY_ENTITY)=?", category.entityKey)
            if !ok {
                println("Deleting Category Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func delete(post : GTLShilohranchPost) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_POST) WHERE \(self.KEY_ENTITY)=?", post.entityKey)
            if !ok {
                println("Deleting Post Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func delete(sermon : GTLShilohranchSermon) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_SERMON) WHERE \(self.KEY_ENTITY)=?", sermon.entityKey)
            if !ok {
                println("Deleting Sermon Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func delete(event : GTLShilohranchEvent) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_EVENT) WHERE \(self.KEY_ENTITY)=?", event.entityKey)
            if !ok {
                println("Deleting Event Failed: \(db.lastErrorMessage())")
            }
        }
    }

    func deleteCategoryWithKey(entityKey : String) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_CATEGORY) WHERE \(self.KEY_ENTITY)=?", entityKey)
            if !ok {
                println("Deleting Category Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func deletePostWithKey(entityKey : String) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_POST) WHERE \(self.KEY_ENTITY)=?", entityKey)
            if !ok {
                println("Deleting Post Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func deleteSermonWithKey(entityKey : String) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_SERMON) WHERE \(self.KEY_ENTITY)=?", entityKey)
            if !ok {
                println("Deleting Sermon Failed: \(db.lastErrorMessage())")
            }
        }
    }
    
    func deleteEventWithKey(entityKey : String) {
        database.inDatabase { (db) in
            let ok = db.executeUpdate("DELETE FROM \(self.TABLE_EVENT) WHERE \(self.KEY_ENTITY)=?", entityKey)
            if !ok {
                println("Deleting Event Failed: \(db.lastErrorMessage())")
            }
        }
    }
}

private func getDatabasePath() -> String {
    let dirPaths = NSSearchPathForDirectoriesInDomains(.DocumentDirectory, .UserDomainMask, true)
    
    let docsDir = dirPaths[0] as! String
    
    var databasePath = docsDir.stringByAppendingPathComponent("shiloh_ranch.db")
    return databasePath as String
}