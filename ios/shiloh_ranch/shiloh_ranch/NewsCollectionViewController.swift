//
//  NewsCollectionViewController.swift
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/21/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

import UIKit

class NewsCollectionViewController : TGLStackedViewController, UICollectionViewDataSource, UICollectionViewDelegate, UIActionSheetDelegate {
 
    let newsCellIdentifier = "News"
    let noNewsCellIdentifier = "Empty"
    
    var categories : [GTLShilohranchCategory] = []
    var allPosts : [GTLShilohranchPost] = []
    var postsInTable : [GTLShilohranchPost] = []
    var selectedCategory : GTLShilohranchCategory?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set to NO to prevent a small number
        // of cards from filling the entire
        // view height evenly and only show
        // their -topReveal amount
        //
        self.stackedLayout.fillHeight = true;
        
        // Set to NO to prevent a small number
        // of cards from being scrollable and
        // bounce
        //
        self.stackedLayout.alwaysBounce = true;
        
        // Set to NO to prevent unexposed
        // items at top and bottom from
        // being selectable
        //
        self.unexposedItemsAreSelectable = true;
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
    }

    func onFilterPressed() {
        if NSClassFromString("UIAlertController") != nil {
            let actionSheet = UIAlertController(title: "Select a Category", message: nil, preferredStyle: .ActionSheet)
            let defaultAction = UIAlertAction(title: "Cancel", style: .Cancel, handler: nil)
            actionSheet.addAction(defaultAction)
            let allAction = UIAlertAction(title: "All", style: .Default) {
                (_) in
                self.selectedCategory = nil
                self.postsInTable = self.allPosts.filter() {
                    (_) in
                    return true
                }
                self.updateView()
            }
            actionSheet.addAction(allAction)
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
        self.collectionView?.reloadData()
    }

    
    override func moveItemAtIndexPath(fromIndexPath: NSIndexPath!, toIndexPath: NSIndexPath!) {
        //TODO
    }
    
    override func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return postsInTable.count
    }
    
    override func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(newsCellIdentifier, forIndexPath: indexPath) as! NewsCell
        let post = postsInTable[indexPath.row]
        cell.setPost(post)
        return cell
    }
    
}