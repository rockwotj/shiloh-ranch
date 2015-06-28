//
//  HTMLDecoder.m
//  Shiloh Ranch
//
//  Created by Tyler Rockwood on 6/27/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//

#import "HTMLDecoder.h"

@implementation HTMLDecoder 


-(id)init
{
    self = [super init];
    self.entities = @[
                 @[@"\"", @"&quot;", @"&#034;"],
                 @[@"&", @"&amp;", @"&#038;"],
                 @[@"'", @"&apos;", @"&#039;"],
                 @[@"<", @"&lt;", @"&#060;"],
                 @[@">", @"&gt;", @"&#062;"],
                 @[@" ", @"&nbsp;", @"&#160;"],
                 @[@"¡", @"&iexcl;", @"&#161;"],
                 @[@"¢", @"&cent;", @"&#162;"],
                 @[@"£", @"&pound;", @"&#163;"],
                 @[@"¤", @"&curren;", @"&#164;"],
                 @[@"¥", @"&yen;", @"&#165;"],
                 @[@"¦", @"&brvbar;", @"&#166;"],
                 @[@"§", @"&sect;", @"&#167;"],
                 @[@"¨", @"&uml;", @"&#168;"],
                 @[@"©", @"&copy;", @"&#169;"],
                 @[@"ª", @"&ordf;", @"&#170;"],
                 @[@"«", @"&laquo;", @"&#171;"],
                 @[@"¬", @"&not;", @"&#172;"],
                 @[@"®", @"&reg;", @"&#174;"],
                 @[@"¯", @"&macr;", @"&#175;"],
                 @[@"°", @"&deg;", @"&#176;"],
                 @[@"±", @"&plusmn;", @"&#177;"],
                 @[@"²", @"&sup2;", @"&#178;"],
                 @[@"³", @"&sup3;", @"&#179;"],
                 @[@"´", @"&acute;", @"&#180;"],
                 @[@"µ", @"&micro;", @"&#181;"],
                 @[@"¶", @"&para;", @"&#182;"],
                 @[@"·", @"&middot;", @"&#183;"],
                 @[@"¸", @"&cedil;", @"&#184;"],
                 @[@"¹", @"&sup1;", @"&#185;"],
                 @[@"º", @"&ordm;", @"&#186;"],
                 @[@"»", @"&raquo;", @"&#187;"],
                 @[@"¼", @"&frac14;", @"&#188;"],
                 @[@"½", @"&frac12;", @"&#189;"],
                 @[@"¾", @"&frac34;", @"&#190;"],
                 @[@"¿", @"&iquest;", @"&#191;"],
                 @[@"÷", @"&divide;", @"&#247;"],
                 @[@"ˆ", @"&circ;", @"&#710;"],
                 @[@"˜", @"&tilde;", @"&#732;"],
                 @[@"–", @"&ndash;", @"&#8211;"],
                 @[@"—", @"&mdash;", @"&#8212;"],
                 @[@"‘", @"&lsquo;", @"&#8216;"],
                 @[@"’", @"&rsquo;", @"&#8217;"],
                 @[@"‚", @"&sbquo;", @"&#8218;"],
                 @[@"“", @"&ldquo;", @"&#8220;"],
                 @[@"”", @"&rdquo;", @"&#8221;"],
                 @[@"„", @"&bdquo;", @"&#8222;"],
                 @[@"†", @"&dagger;", @"&#8224;"],
                 @[@"‡", @"&Dagger;", @"&#8225;"],
                 @[@"•", @"&bull;", @"&#8226;"],
                 @[@"…", @"&hellip;", @"&#8230;"],
                 @[@"‰", @"&permil;", @"&#8240;"],
                 @[@"′", @"&prime;", @"&#8242;"],
                 @[@"″", @"&Prime;", @"&#8243;"],
                 @[@"‹", @"&lsaquo;", @"&#8249;"],
                 @[@"›", @"&rsaquo;", @"&#8250;"],
                 @[@"‾", @"&oline;", @"&#8254;"],
                 @[@"⁄", @"&frasl;", @"&#8260;"],
                 @[@"€", @"&euro;", @"&#8364;"],
                 @[@"ℑ", @"&image;", @"&#8465;"],
                 @[@"℘", @"&weierp;", @"&#8472;"],
                 @[@"ℜ", @"&real;", @"&#8476;"],
                 @[@"™", @"&trade;", @"&#8482;"],
                 @[@"ℵ", @"&alefsym;", @"&#8501;"],
                 @[@"←", @"&larr;", @"&#8592;"],
                 @[@"↑", @"&uarr;", @"&#8593;"],
                 @[@"→", @"&rarr;", @"&#8594;"],
                 @[@"↓", @"&darr;", @"&#8595;"],
                 @[@"↔", @"&harr;", @"&#8596;"],
                 @[@"↵", @"&crarr;", @"&#8629;"],
                 @[@"⇐", @"&lArr;", @"&#8656;"],
                 @[@"⇑", @"&UArr;", @"&#8657;"],
                 @[@"⇒", @"&rArr;", @"&#8658;"],
                 @[@"⇓", @"&dArr;", @"&#8659;"],
                 @[@"⇔", @"&hArr;", @"&#8660;"],
                 @[@"−", @"&minus;", @"&#8722;"],
                 @[@"∗", @"&lowast;", @"&#8727;"],
                 @[@"√", @"&radic;", @"&#8730;"],
                 @[@"∝", @"&prop;", @"&#8733;"],
                 @[@"∞", @"&infin;", @"&#8734;"],
                 @[@"∠", @"&ang;", @"&#8736;"],
                 @[@"∧", @"&and;", @"&#8743;"],
                 @[@"∨", @"&or;", @"&#8744;"],
                 @[@"∩", @"&cap;", @"&#8745;"],
                 @[@"∪", @"&cup;", @"&#8746;"],
                 @[@"∫", @"&int;", @"&#8747;"],
                 @[@"∴", @"&there4;", @"&#8756;"],
                 @[@"∼", @"&sim;", @"&#8764;"],
                 @[@"≅", @"&cong;", @"&#8773;"],
                 @[@"≈", @"&asymp;", @"&#8776;"],
                 @[@"≠", @"&ne;", @"&#8800;"],
                 @[@"≡", @"&equiv;", @"&#8801;"],
                 @[@"≤", @"&le;", @"&#8804;"],
                 @[@"≥", @"&ge;", @"&#8805;"],
                 @[@"⊂", @"&sub;", @"&#8834;"],
                 @[@"⊃", @"&sup;", @"&#8835;"],
                 @[@"⊄", @"&nsub;", @"&#8836;"],
                 @[@"⊆", @"&sube;", @"&#8838;"],
                 @[@"⊇", @"&supe;", @"&#8839;"],
                 @[@"⊕", @"&oplus;", @"&#8853;"],
                 @[@"⊗", @"&otimes;", @"&#8855;"],
                 @[@"⊥", @"&perp;", @"&#8869;"],
                 @[@"⋅", @"&sdot;", @"&#8901;"],
                 @[@"⌈", @"&lceil;", @"&#8968;"],
                 @[@"⌉", @"&rceil;", @"&#8969;"],
                 @[@"⌊", @"&lfloor;", @"&#8970;"],
                 @[@"⌋", @"&rfloor;", @"&#8971;"],
                 @[@"⟨", @"&lang;", @"&#9001;"],
                 @[@"⟩", @"&rang;", @"&#9002;"],
                 @[@"◊", @"&loz;", @"&#9674;"],
                 @[@"♠", @"&spades;", @"&#9824;"],
                 @[@"♣", @"&clubs;", @"&#9827;"],
                 @[@"♥", @"&hearts;", @"&#9829;"],
                 @[@"♦", @"&diams;", @"&#9830;"]];
    return self;
}

- (NSString *)decode:(NSString *)string {
    NSMutableString *returnString = [[NSMutableString alloc] initWithCapacity:string.length];
    NSMutableString *htmlCode = [[NSMutableString alloc] initWithString:@""];
    for (int i = 0; i < string.length; i++) {
        unichar c = [string characterAtIndex:i];
        NSUInteger currentLength = [htmlCode length];
        if (c == '&') {
            [returnString appendFormat:@"%@", htmlCode];
            htmlCode = [[NSMutableString alloc] initWithFormat:@"%c", c];
        } else if (c == ';' && currentLength > 0) {
            [htmlCode appendFormat:@"%c", c];
            for (int j = 0; j < self.entities.count; j++) {
                NSArray *entity = [self.entities objectAtIndex:j];
                NSString *htmlCharName = [entity objectAtIndex:1];
                NSString *htmlCharCode = [entity objectAtIndex:2];
                if ([htmlCharName isEqualToString:htmlCode]) {
                    htmlCode = [entity objectAtIndex:0];
                    break;
                } else if ([htmlCharCode isEqualToString:htmlCode]) {
                    htmlCode = [entity objectAtIndex:0];
                    break;
                }
            }
            [returnString appendString:htmlCode];
            htmlCode = [[NSMutableString alloc] initWithString:@""];
        } else if (currentLength > 0) {
            [htmlCode appendFormat:@"%c", c];
        } else {
            [returnString appendFormat:@"%c", c];
        }
    }
    return returnString;
}
@end
