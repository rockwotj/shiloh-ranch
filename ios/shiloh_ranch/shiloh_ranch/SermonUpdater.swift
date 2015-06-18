//
//  SermonUpdater.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import Foundation

class SermonsUpdater : Updater {
    
    override func getSyncQuery() -> GTLQuery? {
        return GTLQueryShilohranch.queryForUpdateSermonsWithMilliseconds(getLastSyncTime())
    }
    
    override func update(pageToken : String? = nil) {
        let service = getApiService()
        let query = GTLQueryShilohranch.queryForSermons() as GTLQueryShilohranch
        query.pageToken = pageToken
        service.executeQuery(query) { (ticket, response, error) -> Void in
            if error != nil {
                showErrorDialog(error)
            } else {
                let sermonCollection = response as! GTLShilohranchSermonCollection
                let sermons = sermonCollection.items() as! [GTLShilohranchSermon]
                if pageToken == nil && !sermons.isEmpty {
                    let lastSync = convertDateToUnixTime(sermons[0].timeAdded)
                    if lastSync > 0 {
                        println("Setting last SyncTime for Sermons to be \(lastSync)")
                        self.setLastSyncTime(lastSync)
                    }
                }
                for sermon in sermons {
                    println("Got Sermon named: \(sermon.title)")
                    self.save(sermon)
                }
                if let nextPageToken = sermonCollection.nextPageToken {
                    self.update(pageToken: nextPageToken)
                }
            }
        }
    }
    
    func save(entity : GTLShilohranchSermon) {
        let database = getDatabase()
        database.insert(entity)
    }
    
    override func getModelType() -> String! {
        return "Category"
    }
}