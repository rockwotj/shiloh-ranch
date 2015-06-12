/* This file was generated by the ServiceGenerator.
 * The ServiceGenerator is Copyright (c) 2015 Google Inc.
 */

//
//  GTLServiceShilohranch.m
//

// ----------------------------------------------------------------------------
// NOTE: This file is generated from Google APIs Discovery Service.
// Service:
//   shilohranch/v1
// Description:
//   Shiloh Ranch Mobile App API
// Classes:
//   GTLServiceShilohranch (0 custom class methods, 0 custom properties)

#import "GTLShilohranch.h"

@implementation GTLServiceShilohranch

#if DEBUG
// Method compiled in debug builds just to check that all the needed support
// classes are present at link time.
+ (NSArray *)checkClasses {
  NSArray *classes = @[
    [GTLQueryShilohranch class],
    [GTLShilohranchCategory class],
    [GTLShilohranchCategoryCollection class],
    [GTLShilohranchDeletion class],
    [GTLShilohranchDeletionCollection class],
    [GTLShilohranchEvent class],
    [GTLShilohranchEventCollection class],
    [GTLShilohranchPost class],
    [GTLShilohranchPostCollection class],
    [GTLShilohranchSermon class],
    [GTLShilohranchSermonCollection class],
    [GTLShilohranchUpdate class]
  ];
  return classes;
}
#endif  // DEBUG

- (instancetype)init {
  self = [super init];
  if (self) {
    // Version from discovery.
    self.apiVersion = @"v1";

    // From discovery.  Where to send JSON-RPC.
    // Turn off prettyPrint for this service to save bandwidth (especially on
    // mobile). The fetcher logging will pretty print.
    self.rpcURL = [NSURL URLWithString:@"https://shiloh-ranch.appspot.com/_ah/api/rpc?prettyPrint=false"];
  }
  return self;
}

@end
