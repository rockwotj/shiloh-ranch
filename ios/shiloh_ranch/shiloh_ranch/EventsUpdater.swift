//
//  EventsUpdater.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import Foundation

class EventsUpdater : Updater {
    
    override func getSyncQuery() -> GTLQuery? {
        return GTLQueryShilohranch.queryForUpdateEventsWithMilliseconds(getLastSyncTime())
    }
    
    override func update(pageToken : String? = nil) {
        let service = getApiService()
        let query = GTLQueryShilohranch.queryForEvents() as GTLQueryShilohranch
        query.pageToken = pageToken
        service.executeQuery(query) { (ticket, response, error) -> Void in
            if error != nil {
                showErrorDialog(error)
            } else {
                let eventCollection = response as! GTLShilohranchEventCollection
                let events = eventCollection.items() as! [GTLShilohranchEvent]
                if pageToken == nil && !events.isEmpty {
                    let lastSync = convertDateToUnixTime(events[0].timeAdded)
                    if lastSync > 0 {
                        println("Setting last SyncTime for Events to be \(lastSync)")
                        self.setLastSyncTime(lastSync)
                    }
                }
                for event in events {
                    println("Got Event named: \(event.title)")
                    self.save(event)
                }
                if let nextPageToken = eventCollection.nextPageToken {
                    self.update(pageToken: nextPageToken)
                }
            }
        }
    }
    
    func save(entity : GTLShilohranchEvent) {
        let database = getDatabase()
        database.insert(entity)
    }
    
    override func getModelType() -> String! {
        return "Event"
    }
}