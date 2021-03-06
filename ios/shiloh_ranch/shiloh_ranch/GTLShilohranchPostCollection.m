/* This file was generated by the ServiceGenerator.
 * The ServiceGenerator is Copyright (c) 2015 Google Inc.
 */

//
//  GTLShilohranchPostCollection.m
//

// ----------------------------------------------------------------------------
// NOTE: This file is generated from Google APIs Discovery Service.
// Service:
//   shilohranch/v1
// Description:
//   Shiloh Ranch Mobile App API
// Classes:
//   GTLShilohranchPostCollection (0 custom class methods, 2 custom properties)

#import "GTLShilohranchPostCollection.h"

#import "GTLShilohranchPost.h"

// ----------------------------------------------------------------------------
//
//   GTLShilohranchPostCollection
//

@implementation GTLShilohranchPostCollection
@dynamic items, nextPageToken;

+ (NSDictionary *)arrayPropertyToClassMap {
  NSDictionary *map = @{
    @"items" : [GTLShilohranchPost class]
  };
  return map;
}

@end
