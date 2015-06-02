//
//  TabViewController.swift
//  shiloh_ranch
//
//  Created by Tyler Rockwood on 6/1/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class TabViewController : UITabBarController {
    
    override func tabBar(tabBar: UITabBar, didSelectItem item: UITabBarItem!) {
        if (item.tag == 1) {
            UIApplication.sharedApplication().openURL(NSURL(string: "http://tylerrockwood.com")!)
        }
    }
    
}
