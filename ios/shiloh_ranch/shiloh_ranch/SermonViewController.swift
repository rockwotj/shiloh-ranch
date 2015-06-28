//
//  SermonViewController.swift
//  shiloh_ranch
//
//  Created by Tyler Rockwood on 6/1/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//


import UIKit

class SermonViewController: UITableViewController {
    
    let sermonCellIdentifier = "SermonCell"
    
    let regex = NSRegularExpression(pattern: "(.+)\\s*[–—‒-]\\s*(.+)\\s*[–—‒-]\\s*\\((\\d+)/(\\d+)/(\\d+)\\)", options: .CaseInsensitive, error: nil)
    let splitter = NSRegularExpression(pattern: "\\s*[–—‒-]\\s*", options: nil, error: nil)
    let decoder = HTMLDecoder()
    
    var sermons : [GTLShilohranchSermon] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        getDatabase().getAllSermons() {
            (sermons) in
            self.sermons = sermons
            self.tableView.reloadData()
            println("Got \(sermons.count) sermons")
        }
    }
    
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sermons.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(sermonCellIdentifier, forIndexPath: indexPath) as! UITableViewCell
        let post = sermons[indexPath.row]
        var title = decoder.decode(post.title)
        let titleLength = NSMakeRange(0, count(title))
        if regex!.numberOfMatchesInString(title, options: .allZeros, range: titleLength) > 0 {
            let match = regex!.firstMatchInString(title, options: .allZeros, range: titleLength)
            cell.textLabel?.text = (title as NSString).substringWithRange(match!.rangeAtIndex(1))
            var subtitle = (title as NSString).substringWithRange(match!.rangeAtIndex(2))
            subtitle += " - "
            subtitle += (title as NSString).substringWithRange(match!.rangeAtIndex(3)) + "/"
            subtitle += (title as NSString).substringWithRange(match!.rangeAtIndex(4)) + "/"
            subtitle += (title as NSString).substringWithRange(match!.rangeAtIndex(5))
            cell.detailTextLabel?.text = subtitle
        } else {
            cell.textLabel?.text = title
        }
        return cell

    }
    
}