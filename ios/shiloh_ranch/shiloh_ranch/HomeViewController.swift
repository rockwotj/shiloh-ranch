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
        // Set Width
        let width = self.view.bounds.width
        let viewsDictionary = ["innerView":innerView,]
        let metricsDictionary = ["viewWidth":width]
        let constraint:Array = NSLayoutConstraint.constraintsWithVisualFormat("H:[innerView(viewWidth)]", options: NSLayoutFormatOptions(0), metrics: metricsDictionary, views: viewsDictionary)
        innerView.addConstraints(constraint)
        
    }
    

    func onDonatePressed() {
        UIApplication.sharedApplication().openURL(NSURL(string: "http://tylerrockwood.com")!)
    }

}

