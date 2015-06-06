//
//  VerticalScrollView.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/5/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class VerticalScrollView: UIScrollView {
//    - (void)setFrame:(CGRect)frame{
//    [super setFrame:frame];
//    [self setNeedsLayout];
//    }
//    
//    - (void)layoutSubviews{
//    [super layoutSubviews];
//    NSArray * subviews=self.subviews;
//    for(UIView * view in subviews){
//    CGRect viewFrame=view.frame;
//    viewFrame.size.width=self.bounds.size.width;
//    view.frame=viewFrame;
//    }
//    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        for view in self.subviews {
            var viewFrame = view.frame
            viewFrame.size.width = self.bounds.width
            view.frame = viewFrame
        }
    }
}