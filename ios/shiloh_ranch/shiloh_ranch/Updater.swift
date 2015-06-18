//
//  Updater.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import Foundation

class Updater {
    
    func sync(callback : (() -> Void)? = nil) {
        let service = getApiService()
        let query = getSyncQuery()!
        service.executeQuery(query, completionHandler: { (ticket, response, error) -> Void in
            if error != nil {
                showErrorDialog(error)
            } else {
                let sync = response as! GTLShilohranchUpdate
                println("\(self.getModelType()) has needsUpdate of \(sync.needsUpdate) with lastSync being \(self.getLastSyncTime())")
                if (sync.needsUpdate != 0) {
                    self.update()
                    if callback != nil {
                        callback!()
                    }
                }
            }
        })
    }
    
    func update(pageToken : String? = nil) {
        
    }
    
    func getSyncQuery () -> GTLQuery? {
        return nil
    }
    
    func getModelType() -> String! {
        return ""
    }
    
    func getLastSyncTime() -> Int64 {
        if let lastSync = NSUserDefaults.standardUserDefaults().valueForKey(getModelType()) as? NSNumber {
            return lastSync.longLongValue
        } else {
            return 0 
        }
    }
    
    func setLastSyncTime(time : Int64) {
        NSUserDefaults.standardUserDefaults().setValue(NSNumber(longLong: time), forKey: getModelType())
    }
    
}