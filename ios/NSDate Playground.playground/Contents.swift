//: Playground - NSDate: a place where people can format time

import UIKit

var date = NSDate()
var dateFormatter = NSDateFormatter()
dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSSSSS"
var dateString = dateFormatter.stringFromDate(date)
println(dateString)
dateString.stringByReplacingOccurrencesOfString(" ", withString: "T")
// Back to NSDate
dateString = "2015-03-08 20:19:12.503690"

dateFormatter.dateFromString(dateString)