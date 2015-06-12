/* This file was generated by the ServiceGenerator.
 * The ServiceGenerator is Copyright (c) 2015 Google Inc.
 */

//
//  GTLShilohranchCategory.m
//

// ----------------------------------------------------------------------------
// NOTE: This file is generated from Google APIs Discovery Service.
// Service:
//   shilohranch/v1
// Description:
//   Shiloh Ranch Mobile App API
// Classes:
//   GTLShilohranchCategory (0 custom class methods, 4 custom properties)

#import "GTLShilohranchCategory.h"

// ----------------------------------------------------------------------------
//
//   GTLShilohranchCategory
//

@implementation GTLShilohranchCategory
@dynamic entityKey, identifier, timeAdded, title;

+ (NSDictionary *)propertyToJSONKeyMap {
  NSDictionary *map = @{
    @"identifier" : @"id",
    @"timeAdded" : @"time_added"
  };
  return map;
}

@end