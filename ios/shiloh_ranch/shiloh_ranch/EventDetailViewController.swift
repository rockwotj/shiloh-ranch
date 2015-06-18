//
//  EventDetailViewController.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/17/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class EventDetailViewController: UIViewController {
    
    var event : GTLShilohranchEvent?
    @IBOutlet weak var contentView: UITextView!
    
    override func viewDidLoad() {
        self.automaticallyAdjustsScrollViewInsets = false
        self.title = event?.title
        var text = event!.content.stringByReplacingOccurrencesOfString("\\,", withString: ",")
        text = text.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceCharacterSet())
        let startTime = getTimeFromDate(event!.startTime)
        let endTime = getTimeFromDate(event!.endTime)
        text += "<p>Event at \(event!.location!)<br>Goes from \(startTime!) to \(endTime!)</p>"
        let content = text.dataUsingEncoding(NSUTF8StringEncoding)!
        let options = [NSFontAttributeName: UIFont.systemFontOfSize(16.0),
            NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType,
            NSCharacterEncodingDocumentAttribute: NSNumber(unsignedLong: NSUTF8StringEncoding)]
        let body = NSAttributedString(data: content, options: options, documentAttributes: nil, error: nil)
        contentView.attributedText = body
    }
}
