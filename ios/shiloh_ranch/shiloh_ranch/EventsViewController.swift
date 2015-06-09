//
//  SecondViewController.swift
//  shiloh_ranch
//
//  Created by Tyler Rockwood on 6/1/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class EventsViewController: UIViewController, JTCalendarDataSource {

    @IBOutlet weak var calendarMenuView: JTCalendarMenuView!
    
    @IBOutlet weak var calendarContentView: JTCalendarContentView!
    
    var calendar : JTCalendar?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        calendar = JTCalendar()
        calendar?.calendarAppearance.isWeekMode = true;
        calendar?.menuMonthsView = calendarMenuView
        calendar?.contentView = calendarContentView
        calendar?.dataSource = self
        calendar?.currentDate = NSDate()
        calendar?.currentDateSelected = NSDate()
        calendar?.reloadData()
    }
    
    override func viewDidLayoutSubviews() {
        self.calendar?.repositionViews()
    }
    
    func calendarDidDateSelected(calendar: JTCalendar!, date: NSDate!) {
        println("\(date)")
    }
    
    func calendarHaveEvent(calendar: JTCalendar!, date: NSDate!) -> Bool {
        return false
    }


}

