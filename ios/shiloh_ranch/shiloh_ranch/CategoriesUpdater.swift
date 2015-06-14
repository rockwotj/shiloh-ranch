//
//  CategoriesUpdater.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import Foundation

class CategoriesUpdater : Updater {
    
    override func getSyncQuery() -> GTLQuery? {
        return GTLQueryShilohranch.queryForUpdateCategoriesWithMilliseconds(getLastSyncTime())
    }
    
    override func update(pageToken : String? = nil) {
        let service = getApiService()
        let query = GTLQueryShilohranch.queryForCategories() as GTLQueryShilohranch
        query.pageToken = pageToken
        service.executeQuery(query) { (ticket, response, error) -> Void in
            if error != nil {
                showErrorDialog(error)
            } else {
                let categoryCollection = response as! GTLShilohranchCategoryCollection
                let categories = categoryCollection.items() as! [GTLShilohranchCategory]
                if pageToken == nil && !categories.isEmpty {
                    let lastSync = convertDateToUnixTime(categories[0].timeAdded)
                    if lastSync > 0 {
                        println("Setting last SyncTime for Categories to be \(lastSync)")
                        self.setLastSyncTime(lastSync)
                    }
                }
                for category in categories {
                    println("Got Category named: \(category.title)")
                    self.save(category)
                }
                if let nextPageToken = categoryCollection.nextPageToken {
                    self.update(pageToken: nextPageToken)
                }
            }
        }
    }
    
    func save(entity : GTLShilohranchCategory) {
        let database = getDatabase()
        database.insert(entity)
    }
    
    override func getModelType() -> String! {
        return "Category"
    }
}