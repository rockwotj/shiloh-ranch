//
//  NewsUpdater.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import Foundation


class NewsUpdater : Updater {

    override func getSyncQuery() -> GTLQuery? {
        return GTLQueryShilohranch.queryForUpdatePostsWithMilliseconds(getLastSyncTime())
    }
    
    override func update(pageToken : String? = nil) {
        let service = getApiService()
        let query = GTLQueryShilohranch.queryForPosts() as GTLQueryShilohranch
        query.pageToken = pageToken
        service.executeQuery(query) { (ticket, response, error) -> Void in
            if error != nil {
                showErrorDialog(error)
            } else {
                let postCollection = response as! GTLShilohranchPostCollection
                let posts = postCollection.items() as! [GTLShilohranchPost]
                if pageToken == nil && !posts.isEmpty {
                    let lastSync = convertDateToUnixTime(posts[0].timeAdded)
                    if lastSync > 0 {
                        println("Setting last SyncTime for Posts to be \(lastSync)")
                        self.setLastSyncTime(lastSync)
                    }
                }
                for post in posts {
                    println("Got Post named: \(post.title)")
                    self.save(post)
                }
                if let nextPageToken = postCollection.nextPageToken {
                    self.update(pageToken: nextPageToken)
                }
            }
        }
    }
    
    func save(entity : GTLShilohranchPost) {
        let database = getDatabase()
        database.insert(entity)
    }
    
    override func getModelType() -> String! {
        return "Post"
    }
}