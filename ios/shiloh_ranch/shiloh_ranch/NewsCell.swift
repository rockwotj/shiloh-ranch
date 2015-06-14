//
//  NewsCell.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class NewsCell : UITableViewCell {
    
    @IBOutlet weak var cardView: UIView!
    @IBOutlet weak var titleView: UILabel!
    @IBOutlet weak var bodyView: UITextView!
    
    override func layoutSubviews() {
        
    }
    
    func setPost(post : GTLShilohranchPost) {
        titleView.text = post.title
        let body = NSAttributedString(data: post.content.dataUsingEncoding(NSUTF8StringEncoding)!, options: [NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType, NSCharacterEncodingDocumentAttribute: NSNumber(unsignedLong: NSUTF8StringEncoding)], documentAttributes: nil, error: nil)
        bodyView.attributedText = body!
    }
    
}