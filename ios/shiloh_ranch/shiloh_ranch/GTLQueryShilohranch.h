/* This file was generated by the ServiceGenerator.
 * The ServiceGenerator is Copyright (c) 2015 Google Inc.
 */

//
//  GTLQueryShilohranch.h
//

// ----------------------------------------------------------------------------
// NOTE: This file is generated from Google APIs Discovery Service.
// Service:
//   shilohranch/v1
// Description:
//   Shiloh Ranch Mobile App API
// Classes:
//   GTLQueryShilohranch (10 custom class methods, 6 custom properties)

#if GTL_BUILT_AS_FRAMEWORK
  #import "GTL/GTLQuery.h"
#else
  #import "GTLQuery.h"
#endif

@interface GTLQueryShilohranch : GTLQuery

//
// Parameters valid on all methods.
//

// Selector specifying which fields to include in a partial response.
@property (nonatomic, copy) NSString *fields;

//
// Method-specific parameters; see the comments below for more information.
//
@property (nonatomic, copy) NSString *lastSync;
@property (nonatomic, assign) long long limit;
@property (nonatomic, assign) long long milliseconds;
@property (nonatomic, copy) NSString *pageToken;
@property (nonatomic, assign) long long timeZoneOffset;

#pragma mark -
#pragma mark Service level methods
// These create a GTLQueryShilohranch object.

// Method: shilohranch.categories
//  Optional:
//   lastSync: NSString
//   limit: long long
//   pageToken: NSString
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchCategoryCollection.
+ (instancetype)queryForCategories;

// Method: shilohranch.deletions
//  Optional:
//   lastSync: NSString
//   limit: long long
//   pageToken: NSString
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchDeletionCollection.
+ (instancetype)queryForDeletions;

// Method: shilohranch.events
//  Optional:
//   lastSync: NSString
//   limit: long long
//   pageToken: NSString
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchEventCollection.
+ (instancetype)queryForEvents;

// Method: shilohranch.posts
//  Optional:
//   lastSync: NSString
//   limit: long long
//   pageToken: NSString
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchPostCollection.
+ (instancetype)queryForPosts;

// Method: shilohranch.sermons
//  Optional:
//   lastSync: NSString
//   limit: long long
//   pageToken: NSString
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchSermonCollection.
+ (instancetype)queryForSermons;

#pragma mark -
#pragma mark "update" methods
// These create a GTLQueryShilohranch object.

// Method: shilohranch.update.categories
//  Optional:
//   timeZoneOffset: long long
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchUpdate.
+ (instancetype)queryForUpdateCategoriesWithMilliseconds:(long long)milliseconds;

// Method: shilohranch.update.deletions
//  Optional:
//   timeZoneOffset: long long
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchUpdate.
+ (instancetype)queryForUpdateDeletionsWithMilliseconds:(long long)milliseconds;

// Method: shilohranch.update.events
//  Optional:
//   timeZoneOffset: long long
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchUpdate.
+ (instancetype)queryForUpdateEventsWithMilliseconds:(long long)milliseconds;

// Method: shilohranch.update.posts
//  Optional:
//   timeZoneOffset: long long
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchUpdate.
+ (instancetype)queryForUpdatePostsWithMilliseconds:(long long)milliseconds;

// Method: shilohranch.update.sermons
//  Optional:
//   timeZoneOffset: long long
//  Authorization scope(s):
//   kGTLAuthScopeShilohranchUserinfoEmail
// Fetches a GTLShilohranchUpdate.
+ (instancetype)queryForUpdateSermonsWithMilliseconds:(long long)milliseconds;

@end
