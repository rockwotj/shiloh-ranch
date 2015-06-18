//
//  DeletionsUpdater.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import Foundation


class DeletionsUpdater : Updater {
    
    override func getSyncQuery() -> GTLQuery? {
        return GTLQueryShilohranch.queryForUpdateDeletionsWithMilliseconds(getLastSyncTime())
    }
    
    override func update(pageToken : String? = nil) {
        let service = getApiService()
        let query = GTLQueryShilohranch.queryForDeletions() as GTLQueryShilohranch
        query.pageToken = pageToken
        service.executeQuery(query) { (ticket, response, error) -> Void in
            if error != nil {
                showErrorDialog(error)
            } else {
                let deletionCollection = response as! GTLShilohranchDeletionCollection
                let deletions = deletionCollection.items() as! [GTLShilohranchDeletion]
                if pageToken == nil && !deletions.isEmpty {
                    let lastSync = convertDateToUnixTime(deletions[0].timeAdded)
                    if lastSync > 0 {
                        println("Setting last SyncTime for Categories to be \(lastSync)")
                        self.setLastSyncTime(lastSync)
                    }
                }
                for deletion in deletions {
                    println("Got Deletion named: \(deletion.kind)")
                    self.delete(deletion)
                }
                if let nextPageToken = deletionCollection.nextPageToken {
                    self.update(pageToken: nextPageToken)
                }
            }
        }
    }
    
    func delete(entity : GTLShilohranchDeletion) {
        let database = getDatabase()
        switch(entity.kind) {
            case "Sermon":
                database.deleteSermonWithKey(entity.deletionKey)
            case "Post":
                database.deletePostWithKey(entity.deletionKey)
            case "Event":
                database.deleteEventWithKey(entity.deletionKey)
            case "Category":
                database.deleteCategoryWithKey(entity.deletionKey)
            default:
                println("Unknown kind: \(entity.kind)")
        }
    }
    
    override func getModelType() -> String! {
        return "Category"
    }
}