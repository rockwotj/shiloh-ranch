//
//  HTMLDecoder.h
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/27/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface HTMLDecoder : NSObject

- (NSString *)decode:(NSString *)string;
@property (nonatomic) NSArray *entities;
@end