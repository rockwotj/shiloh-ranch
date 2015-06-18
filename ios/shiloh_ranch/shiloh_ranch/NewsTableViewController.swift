//
//  NewsTableViewController.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/14/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class NewsTableViewController: UITableViewController, UIActionSheetDelegate {

    
    let newsCellIdentifier = "News"
    let noNewsCellIdentifier = "Empty"
    
    var categories : [GTLShilohranchCategory] = []
    var allPosts : [GTLShilohranchPost] = []
    var postsInTable : [GTLShilohranchPost] = []
    var selectedCategory : GTLShilohranchCategory?
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        self.view.backgroundColor = UIColor.lightGrayColor()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Filter", style: .Plain, target: self, action: "onFilterPressed")
        getDatabase().getAllCategories() {
            (categories) in
            self.categories = categories
        }
        getDatabase().getAllPosts() {
            (posts) in
            println("Got \(posts.count) posts")
            self.allPosts = posts
            self.postsInTable = posts
            self.selectedCategory = nil
            self.updateView()
        }
        self.refreshControl = UIRefreshControl()
        self.refreshControl?.tintColor = UIColor.whiteColor()
        self.refreshControl?.addTarget(self, action: "refresh", forControlEvents: .ValueChanged)
    }
    
    func refresh() {
        NewsUpdater().sync() {
            self.refreshControl?.endRefreshing()
        }
    }
    
    func onFilterPressed() {
        if NSClassFromString("UIAlertController") != nil {
            let actionSheet = UIAlertController(title: "Select a Category", message: nil, preferredStyle: .ActionSheet)
            let defaultAction = UIAlertAction(title: "Cancel", style: .Cancel, handler: nil)
            actionSheet.addAction(defaultAction)
            for category in categories {
                let action = UIAlertAction(title: category.title, style: .Default) {
                    (_) in
                    self.selectedCategory = category
                    self.postsInTable = self.allPosts.filter() {
                        return self.selectedCategory?.entityKey == $0.category
                    }
                    self.updateView()
                }
                actionSheet.addAction(action)
            }
            self.presentViewController(actionSheet, animated: true, completion: nil)
        } else {
            let actionSheet = UIActionSheet(title: "Select a Category", delegate: self, cancelButtonTitle: "Cancel", destructiveButtonTitle: nil)
            actionSheet.addButtonWithTitle("All")
            for category in categories {
                actionSheet.addButtonWithTitle(category.title)
            }
            actionSheet.showInView(self.view)
        }
    }
    
    func actionSheet(actionSheet: UIActionSheet, clickedButtonAtIndex buttonIndex: Int) {
        if buttonIndex == 0 {
            selectedCategory = nil
            postsInTable = allPosts
        } else {
            selectedCategory = categories[buttonIndex - 1]
            postsInTable = allPosts.filter() {
                return self.selectedCategory?.entityKey == $0.category
            }
        }
        updateView()
    }
    
    func updateView() {
        if selectedCategory != nil {
            self.title = selectedCategory!.title + " News"
        } else {
            self.title = "All News"
        }
        self.navigationController?.title = "News"
        self.tableView.reloadData()
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return postsInTable.count != 0 ? postsInTable.count : 1
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        if postsInTable.count == 0 {
            return tableView.dequeueReusableCellWithIdentifier(noNewsCellIdentifier, forIndexPath: indexPath) as! UITableViewCell
        } else {
            let cell = tableView.dequeueReusableCellWithIdentifier(newsCellIdentifier, forIndexPath: indexPath) as! NewsCell
            let post = postsInTable[indexPath.row]
            cell.setPost(post)
            return cell
        }
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        if postsInTable.count == 0 {
            return 44
        } else {
            let width = tableView.bounds.width - 32
            let maxSize = CGSize(width: width, height: 10000)
            let ctx = NSStringDrawingContext.new()
            let content = postsInTable[indexPath.row].content.dataUsingEncoding(NSUTF8StringEncoding)!
            let options = [NSFontAttributeName: UIFont.systemFontOfSize(16.0),
                NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType,
                NSCharacterEncodingDocumentAttribute: NSNumber(unsignedLong: NSUTF8StringEncoding)]
            let body = NSAttributedString(data: content, options: options, documentAttributes: nil, error: nil)
            let calculationView = UITextView()
            calculationView.attributedText = body
            let sizedString = calculationView.text as NSString
            let size = sizedString.boundingRectWithSize(maxSize, options:.UsesLineFragmentOrigin, attributes: [NSFontAttributeName:calculationView.font], context: ctx).size
            println("Height for cell \(indexPath.row): \(size.height)")
            return size.height + 110
        }
    }


}
