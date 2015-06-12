//
//  NewsViewController.swift
//  shiloh_ranch
//
//  Created by Tyler Rockwood on 6/1/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//


import UIKit


class NewsViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let query = GTLQueryShilohranch.queryForCategories() as GTLQueryShilohranch
        let app = UIApplication.sharedApplication().delegate as! AppDelegate
        let service = app.service
        service.executeQuery(query) { (ticket, response, error) -> Void in
            if error != nil {
                showErrorDialog(error)
            } else {
                let categoryCollection = response as! GTLShilohranchCategoryCollection
                let categories = categoryCollection.items() as! [GTLShilohranchCategory]
                for category in categories {
                    println("Category named: \(category.title)")
                }
            }
        }
    }
    
    
}