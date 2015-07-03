//
//  Helpers.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/11/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

func showErrorDialog(error : NSError) {
    let dialog = UIAlertView(title: "Error!", message: error.localizedDescription, delegate: nil, cancelButtonTitle: "OK")
    dialog.show()
}

func showErrorDialog(message : String) {
    let dialog = UIAlertView(title: "Error!", message: message, delegate: nil, cancelButtonTitle: "OK")
    dialog.show()
}

func getApiService() -> GTLServiceShilohranch {
    let app = UIApplication.sharedApplication().delegate as! AppDelegate
    return app.service
}

func getDatabase() -> Database {
    let app = UIApplication.sharedApplication().delegate as! AppDelegate
    return app.database
}

func convertDateToUnixTime(dateString : String) -> Int64 {
    var timestamp = dateString.stringByReplacingOccurrencesOfString("T", withString: " ")
    let dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SS"
    if let date = dateFormatter.dateFromString(timestamp) {
        let lastSync = Int64(date.timeIntervalSince1970 * 1000)
        return lastSync
    } else {
        println("Error converting datetime to unix time: \(timestamp)")
        return -1
    }
}

func parseDate(dateString : String) -> NSDate? {
    var timestamp = dateString.stringByReplacingOccurrencesOfString("T", withString: " ")
    let dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SS"
    if let date = dateFormatter.dateFromString(timestamp) {
        return date
    } else {
        println("Error converting datetime to unix time: \(timestamp)")
        return nil
    }
}

func getTimeFromDate(dateString : String) -> String? {
    var timestamp = dateString.stringByReplacingOccurrencesOfString("T", withString: " ")
    let dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SS"
    if let date = dateFormatter.dateFromString(timestamp) {
        dateFormatter.dateFormat = "hh:mm aa"
        return dateFormatter.stringFromDate(date)
    } else {
        println("Error converting datetime to NSDate: \(timestamp)")
        return nil
    }
}

func prettyFormatDate(dateString : String) -> String? {
    var timestamp = dateString.stringByReplacingOccurrencesOfString("T", withString: " ")
    let dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SS"
    if let date = dateFormatter.dateFromString(timestamp) {
        dateFormatter.dateFormat = "MM/dd/yyyy"
        return dateFormatter.stringFromDate(date)
    } else {
        println("Error converting datetime to NSDate: \(timestamp)")
        return nil
    }
}

func parseDateComponents(dateString : String) -> NSDateComponents? {
    if let date = parseDate(dateString) {
        return dateComponents(date)
    } else {
        return nil
    }
}

func dateComponents(date: NSDate) -> NSDateComponents {
    return NSCalendar.currentCalendar().components(.DayCalendarUnit | .MonthCalendarUnit | .YearCalendarUnit, fromDate: date)
}

func getDayOfWeek(dateString: String) -> String? {
    var timestamp = dateString.stringByReplacingOccurrencesOfString("T", withString: " ")
    let dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SS"
    if let date = dateFormatter.dateFromString(timestamp) {
        return getDayOfWeek(date)
    } else {
        println("Error converting datetime to NSDate: \(timestamp)")
        return nil
    }

}

func getDayOfWeek(date: NSDate) -> String? {
    let dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = "EEEE"
    return dateFormatter.stringFromDate(date)
}

extension NSDate
{
    func isGreaterThanDate(dateToCompare : NSDate) -> Bool
    {
        //Declare Variables
        var isGreater = false
        
        //Compare Values
        if self.compare(dateToCompare) == NSComparisonResult.OrderedDescending
        {
            isGreater = true
        }
        
        //Return Result
        return isGreater
    }
    
    
    func isLessThanDate(dateToCompare : NSDate) -> Bool
    {
        //Declare Variables
        var isLess = false
        
        //Compare Values
        if self.compare(dateToCompare) == NSComparisonResult.OrderedAscending
        {
            isLess = true
        }
        
        //Return Result
        return isLess
    }
    
    
    func addDays(daysToAdd : Int) -> NSDate
    {
        var secondsInDays : NSTimeInterval = Double(daysToAdd) * 60 * 60 * 24
        var dateWithDaysAdded : NSDate = self.dateByAddingTimeInterval(secondsInDays)
        
        //Return Result
        return dateWithDaysAdded
    }
    
    
    func addHours(hoursToAdd : Int) -> NSDate
    {
        var secondsInHours : NSTimeInterval = Double(hoursToAdd) * 60 * 60
        var dateWithHoursAdded : NSDate = self.dateByAddingTimeInterval(secondsInHours)
        
        //Return Result
        return dateWithHoursAdded
    }
}

infix operator ~> {}

/**
Executes the lefthand closure on a background thread and,
upon completion, the righthand closure on the main thread.
*/
func ~> (
    backgroundClosure: () -> (),
    mainClosure:       () -> ())
{
    dispatch_async(queue) {
        backgroundClosure()
        dispatch_async(dispatch_get_main_queue(), mainClosure)
    }
}

/**
Executes the lefthand closure on a background thread and,
upon completion, the righthand closure on the main thread.
Passes the background closure's output to the main closure.
*/
func ~> <R> (
    backgroundClosure: () -> R,
    mainClosure:       (result: R) -> ())
{
    dispatch_async(queue) {
        let result = backgroundClosure()
        dispatch_async(dispatch_get_main_queue(), {
            mainClosure(result: result)
        })
    }
}

/** Serial dispatch queue used by the ~> operator. */
private let queue = dispatch_queue_create("serial-worker", DISPATCH_QUEUE_SERIAL)

extension UIViewController {
    
    func setViewWidth(view : UIView, width : CGFloat) {
        let viewsDictionary = ["view":view]
        let metricsDictionary = ["viewWidth":width - 16]
        let constraint:Array = NSLayoutConstraint.constraintsWithVisualFormat("H:[view(viewWidth)]",
            options: NSLayoutFormatOptions(0), metrics: metricsDictionary, views: viewsDictionary)
        view.addConstraints(constraint)
    }
    
}
//
//  FMDB Extensions
//

//  This extension inspired by http://stackoverflow.com/a/24187932/1271826

extension FMDatabase {
    
    /// Private generic function used for the variadic renditions of the FMDatabaseAdditions methods
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The NSArray of the arguments to be bound to the ? placeholders in the SQL.
    /// :param: completionHandler The closure to be used to call the appropriate FMDatabase method to return the desired value.
    ///
    /// :returns: This returns the T value if value is found. Returns nil if column is NULL or upon error.
    
    private func valueForQuery<T>(sql: String, values: [AnyObject]?, completionHandler:(FMResultSet)->(T!)) -> T! {
        var result: T!
        
        if let rs = executeQuery(sql, withArgumentsInArray: values) {
            if rs.next() {
                let obj: AnyObject! = rs.objectForColumnIndex(0)
                if !(obj is NSNull) {
                    result = completionHandler(rs)
                }
            }
            rs.close()
        }
        
        return result
    }
    
    /// This is a rendition of stringForQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns string value if value is found. Returns nil if column is NULL or upon error.
    
    func stringForQuery(sql: String, _ values: AnyObject...) -> String! {
        return valueForQuery(sql, values: values) { $0.stringForColumnIndex(0) }
    }
    
    /// This is a rendition of intForQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns integer value if value is found. Returns nil if column is NULL or upon error.
    
    func intForQuery(sql: String, _ values: AnyObject...) -> Int32! {
        return valueForQuery(sql, values: values) { $0.intForColumnIndex(0) }
    }
    
    /// This is a rendition of longForQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns long value if value is found. Returns nil if column is NULL or upon error.
    
    func longForQuery(sql: String, _ values: AnyObject...) -> Int! {
        return valueForQuery(sql, values: values) { $0.longForColumnIndex(0) }
    }
    
    /// This is a rendition of boolForQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns Bool value if value is found. Returns nil if column is NULL or upon error.
    
    func boolForQuery(sql: String, _ values: AnyObject...) -> Bool! {
        return valueForQuery(sql, values: values) { $0.boolForColumnIndex(0) }
    }
    
    /// This is a rendition of doubleForQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns Double value if value is found. Returns nil if column is NULL or upon error.
    
    func doubleForQuery(sql: String, _ values: AnyObject...) -> Double! {
        return valueForQuery(sql, values: values) { $0.doubleForColumnIndex(0) }
    }
    
    /// This is a rendition of dateForQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns NSDate value if value is found. Returns nil if column is NULL or upon error.
    
    func dateForQuery(sql: String, _ values: AnyObject...) -> NSDate! {
        return valueForQuery(sql, values: values) { $0.dateForColumnIndex(0) }
    }
    
    /// This is a rendition of dataForQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns NSData value if value is found. Returns nil if column is NULL or upon error.
    
    func dataForQuery(sql: String, _ values: AnyObject...) -> NSData! {
        return valueForQuery(sql, values: values) { $0.dataForColumnIndex(0) }
    }
    
    /// This is a rendition of executeQuery that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns FMResultSet if successful. Returns nil upon error.
    
    func executeQuery(sql:String, _ values: AnyObject...) -> FMResultSet? {
        return executeQuery(sql, withArgumentsInArray: values as [AnyObject]);
    }
    
    /// This is a rendition of executeUpdate that handles Swift variadic parameters
    /// for the values to be bound to the ? placeholders in the SQL.
    ///
    /// :param: sql The SQL statement to be used.
    /// :param: values The values to be bound to the ? placeholders
    ///
    /// :returns: This returns true if successful. Returns false upon error.
    
    func executeUpdate(sql:String, _ values: AnyObject...) -> Bool {
        return executeUpdate(sql, withArgumentsInArray: values as [AnyObject]);
    }
}