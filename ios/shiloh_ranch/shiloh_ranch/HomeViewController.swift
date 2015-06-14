//
//  FirstViewController.swift
//  shiloh_ranch
//
//  Created by Tyler Rockwood on 6/1/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class HomeViewController: UIViewController {

    @IBOutlet weak var innerView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Donate", style: UIBarButtonItemStyle.Plain, target: self, action: "onDonatePressed")
        let width = self.view.bounds.width
        self.setViewWidth(innerView, width: width)
    }

    func onDonatePressed() {
        UIApplication.sharedApplication().openURL(NSURL(string: "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XURAEFVZV2CAJ")!)
    }

}

