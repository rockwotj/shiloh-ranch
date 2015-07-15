//
//  SecondViewController.swift
//  shiloh_ranch
//
//  Created by Tyler Rockwood on 6/1/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class EventsViewController: UIViewController, JTCalendarDataSource, UITableViewDataSource, UITableViewDelegate {

    let eventCellIdentifier = "Event"
    let noEventCellIdentifier = "Empty"
    let ShowDetailSegueIdentifier = "ShowEventDetail"
    
    @IBOutlet weak var calendarMenuView: JTCalendarMenuView!
    @IBOutlet weak var calendarContentView: JTCalendarContentView!
    @IBOutlet weak var scheduleView: UITableView!
    
    var calendar : JTCalendar?
    var events : [GTLShilohranchEvent] = []
    var selectedEvents : [GTLShilohranchEvent] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        EventsUpdater().sync()
        calendar = JTCalendar()
        let today = NSDate()
        calendar?.currentDate = today
        calendar?.currentDateSelected = NSDate()
        calendar?.calendarAppearance.isWeekMode = true;
        calendar?.menuMonthsView = calendarMenuView
        calendar?.contentView = calendarContentView
        calendar?.dataSource = self
        scheduleView.dataSource = self
        scheduleView.delegate = self
        getDatabase().getAllEvents() {
            events in
            self.events = events.sorted() {
                let date1 = parseDate($0.startTime)!
                let date2 = parseDate($1.startTime)!
                return date1.isLessThanDate(date2)
            }
            self.filterEventsByDate(today) ~> {self.updateView(); self.calendar?.reloadData()};
        }
    }
    
    override func viewDidLayoutSubviews() {
        self.calendar?.repositionViews()
    }
    
    func updateView() {
        scheduleView.reloadData()
    }
    
    func filterEventsByDate(date: NSDate) -> () -> () {
        return {
            let selectedDate = dateComponents(date)
            self.selectedEvents = self.events.filter() {
                event in
                let eventDate = parseDate(event.startTime)!
                let eventDateComponents = dateComponents(eventDate)
                let isOnDate = self.isOnSameDay(selectedDate, day2: eventDateComponents)
                return isOnDate || (event.repeat.hasPrefix("WEEKLY")) && self.isWeeklyRepeat(date, date2: eventDate, repeatString: event.repeat)
            }
        }
    }
    
    func calendarDidDateSelected(calendar: JTCalendar!, date: NSDate!) {
        filterEventsByDate(date) ~> updateView
    }
    
    func isOnSameDay(day1 : NSDateComponents, day2 : NSDateComponents) -> Bool {
        var isOnSameDay = day1.year == day2.year
        isOnSameDay = isOnSameDay && day1.day == day2.day
        isOnSameDay = isOnSameDay && day1.month == day2.month
        return isOnSameDay
    }
    
    func isWeeklyRepeat(currentDate : NSDate, date2 : NSDate, repeatString : String) -> Bool {
        if let range = repeatString.rangeOfString(":") {
            var until = repeatString.substringFromIndex(range.endIndex)
            until = until.stringByReplacingOccurrencesOfString("Z", withString: "")
            var timestamp = until.stringByReplacingOccurrencesOfString("T", withString: " ")
            let dateFormatter = NSDateFormatter()
            dateFormatter.dateFormat = "yyyyMMdd HHmmss"
            if let untilDate = dateFormatter.dateFromString(timestamp) {
                let eventOver = untilDate.isLessThanDate(currentDate)
                if eventOver {
                    return false
                }
            }
        }
        return getDayOfWeek(currentDate) == getDayOfWeek(date2)
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == ShowDetailSegueIdentifier {
            let detailController = segue.destinationViewController as! EventDetailViewController
            let index = scheduleView.indexPathForSelectedRow()!.row
            detailController.event = selectedEvents[index]
        }
    }
    
    func calendarHaveEvent(calendar: JTCalendar!, date: NSDate!) -> Bool {
        let selectedDate = dateComponents(date)
        for event in events {
            let eventDate = parseDateComponents(event.startTime)
            if isOnSameDay(selectedDate, day2: eventDate!) {
                return true
            } else if event.repeat.hasPrefix("WEEKLY") && self.isWeeklyRepeat(date, date2: parseDate(event.startTime)!, repeatString: event.repeat) {
                return true
            }
        }
        return false
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        if selectedEvents.count == 0 {
            return tableView.dequeueReusableCellWithIdentifier(noEventCellIdentifier, forIndexPath: indexPath) as! UITableViewCell
        } else {
            let cell = tableView.dequeueReusableCellWithIdentifier(eventCellIdentifier, forIndexPath: indexPath) as! UITableViewCell
            let event = selectedEvents[indexPath.row]
            cell.textLabel?.text = event.title
            return cell
        }
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return selectedEvents.count != 0 ? selectedEvents.count : 1
    }

}

