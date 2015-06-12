/* This file was generated by the ServiceGenerator.
 * The ServiceGenerator is Copyright (c) 2015 Google Inc.
 */

//
//  GTLShilohranchSermonCollection.m
//

// ----------------------------------------------------------------------------
// NOTE: This file is generated from Google APIs Discovery Service.
// Service:
//   shilohranch/v1
// Description:
//   Shiloh Ranch Mobile App API
// Classes:
//   GTLShilohranchSermonCollection (0 custom class methods, 2 custom properties)

#import "GTLShilohranchSermonCollection.h"

#import "GTLShilohranchSermon.h"

// ----------------------------------------------------------------------------
//
//   GTLShilohranchSermonCollection
//

@implementation GTLShilohranchSermonCollection
@dynamic items, nextPageToken;

+ (NSDictionary *)arrayPropertyToClassMap {
  NSDictionary *map = @{
    @"items" : [GTLShilohranchSermon class]
  };
  return map;
}

@end
