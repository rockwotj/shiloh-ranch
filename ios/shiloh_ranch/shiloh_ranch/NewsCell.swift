//
//  NewsCell.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/13/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class NewsCell : UICollectionViewCell {

    @IBOutlet weak var cardView: UIView!
    
    @IBOutlet weak var titleView: UILabel!
    
    @IBOutlet weak var bodyView: UITextView!
    
    override func layoutSubviews() {
        super.layoutSubviews()
        // Curved Edges
        cardView.layer.cornerRadius = 5.0
        cardView.layer.masksToBounds = true
        // Border
        cardView.layer.borderColor = UIColor.lightGrayColor().CGColor
        cardView.layer.borderWidth = 1.0
        // Drop Shadow
        cardView.layer.shadowColor = UIColor.blackColor().CGColor
        cardView.layer.shadowOpacity = 0.8
        cardView.layer.shadowRadius = 0.5
        cardView.layer.shadowOffset = CGSizeMake(2, 2)
    }
    
    func setPost(post : GTLShilohranchPost) {
        titleView.text = post.title
        let content = post.content.dataUsingEncoding(NSUTF8StringEncoding)!
        let options = [NSFontAttributeName: UIFont.systemFontOfSize(16.0),
            NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType,
            NSCharacterEncodingDocumentAttribute: NSNumber(unsignedLong: NSUTF8StringEncoding)]
        let body = NSAttributedString(data: content, options: options, documentAttributes: nil, error: nil)
        bodyView.attributedText = body!
    }
    
}