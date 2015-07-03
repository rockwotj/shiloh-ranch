//
//  SermonViewController.swift
//  shiloh_ranch
//
//  Created by Tyler Rockwood on 6/1/15.
//  Copyright (c) 2015 Shiloh Ranch Cowboy Church. All rights reserved.
//


import UIKit
import AVFoundation
import MediaPlayer

class SermonViewController: UITableViewController, STKAudioPlayerDelegate {
    
    let sermonCellIdentifier = "SermonCell"
    let streamingCellIdentifier = "NowPlayingCell"
    
    let regex = NSRegularExpression(pattern: "(.+)\\s*[–—‒-]\\s*(.+)\\s*[–—‒-]\\s*\\((\\d+)/(\\d+)/(\\d+)\\)", options: .CaseInsensitive, error: nil)
    let decoder = HTMLDecoder()
    
    var sermons : [GTLShilohranchSermon] = []
    var currentSermon : GTLShilohranchSermon?
    var audioPlayer = STKAudioPlayer()
    var playPauseCellButton : RSPlayPauseButton?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Sermons"
        getDatabase().getAllSermons() {
            (sermons) in
            self.sermons = sermons
            self.tableView.reloadData()
        }
        audioPlayer.delegate = self
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        var selectedSermon = sermons[indexPath.row]
        if selectedSermon.entityKey != currentSermon?.entityKey {
            if audioPlayer.state == STKAudioPlayerStatePaused || audioPlayer.state == STKAudioPlayerStatePlaying {
                audioPlayer.pause()
            } else if audioPlayer.state == STKAudioPlayerStateError {
                audioPlayer = STKAudioPlayer()
            }
            audioPlayer.play(selectedSermon.audioLink)
            println("Start");

            if audioPlayer.state == STKAudioPlayerStateError {
                println("Error!")
            }
            currentSermon = selectedSermon
            setAsNowPlaying(selectedSermon)
            self.tableView.reloadData()
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Stop", style: .Plain, target: self, action: "stopPlayback")
            initSession()
        }
    }
    
    func stopPlayback() {
        currentSermon = nil
        audioPlayer.stop()
        resetNowPlaying()
        deinitSession()
        self.navigationItem.rightBarButtonItem = nil
        self.tableView.reloadData()
    }
    
    func togglePlayPause() {
        if audioPlayer.state == STKAudioPlayerStatePlaying {
            pauseSermon()
        } else if audioPlayer.state == STKAudioPlayerStatePaused {
            playSermon()
        } else if audioPlayer.state == STKAudioPlayerStateError {
            println("Error!")
        } else {
            println("Unknown State! \(audioPlayer.state.value)")
        }
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sermons.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell : UITableViewCell! = nil
        let sermon = sermons[indexPath.row]
        if sermon.entityKey == currentSermon?.entityKey {
            cell = tableView.dequeueReusableCellWithIdentifier(streamingCellIdentifier, forIndexPath: indexPath) as! UITableViewCell
            for subview in cell.contentView.subviews {
                if let playPauseButton = subview as? RSPlayPauseButton {
                    playPauseButton.addTarget(self, action: "togglePlayPause", forControlEvents: .TouchUpInside)
                    playPauseCellButton?.setPaused(false, animated: false)
                    let constraint = NSLayoutConstraint(item: playPauseButton, attribute: NSLayoutAttribute.Height, relatedBy: NSLayoutRelation.Equal, toItem: playPauseButton, attribute: NSLayoutAttribute.Width, multiplier: 1.0, constant: 0.0)
                    playPauseButton.addConstraint(constraint)
                    playPauseCellButton = playPauseButton
                    break
                }
            }
        } else {
            cell = tableView.dequeueReusableCellWithIdentifier(sermonCellIdentifier, forIndexPath: indexPath) as! UITableViewCell
        }
        let elements = extractTitleElements(sermon)
        cell.textLabel?.text = elements.title
        if elements.date != nil && elements.pastor != nil {
            cell.detailTextLabel?.text = elements.pastor! + " - " + elements.date!
        } else {
            cell.detailTextLabel?.text = ""
        }
        return cell

    }
    
    func setAsNowPlaying(sermon: GTLShilohranchSermon) {
        if NSClassFromString("MPNowPlayingInfoCenter") != nil {
            let elements = extractTitleElements(sermon)
            var nowPlaying = [MPMediaItemPropertyTitle: elements.title] as [String : AnyObject]
            nowPlaying[MPNowPlayingInfoPropertyPlaybackRate] = 1.0
            if elements.pastor != nil {
                nowPlaying[MPMediaItemPropertyArtist] = elements.pastor!
            }
            MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = nowPlaying
            MPRemoteCommandCenter.sharedCommandCenter().playCommand.enabled = false
            MPRemoteCommandCenter.sharedCommandCenter().pauseCommand.enabled = true
            MPRemoteCommandCenter.sharedCommandCenter().pauseCommand.addTarget(self, action: "pauseSermon")

        }
    }
    
    func setAsNowPaused(sermon: GTLShilohranchSermon) {
        if NSClassFromString("MPNowPlayingInfoCenter") != nil {
            let elements = extractTitleElements(sermon)
            var nowPlaying = [MPMediaItemPropertyTitle: elements.title] as [String : AnyObject]
            nowPlaying[MPNowPlayingInfoPropertyPlaybackRate] = 0.0
            if elements.pastor != nil {
                nowPlaying[MPMediaItemPropertyArtist] = elements.pastor!
            }
            MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = nowPlaying
            MPRemoteCommandCenter.sharedCommandCenter().playCommand.enabled = true
            MPRemoteCommandCenter.sharedCommandCenter().pauseCommand.enabled = false
            MPRemoteCommandCenter.sharedCommandCenter().playCommand.addTarget(self, action: "playSermon")
        }
    }
    
    func resetNowPlaying() {
        if NSClassFromString("MPNowPlayingInfoCenter") != nil {
            MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = nil
        }
    }
    
    func extractTitleElements(sermon : GTLShilohranchSermon) -> (title: String, pastor: String?, date: String?) {
        var title = decoder.decode(sermon.title)
        let titleLength = NSMakeRange(0, count(title))
        if regex!.numberOfMatchesInString(title, options: .allZeros, range: titleLength) > 0 {
            let match = regex!.firstMatchInString(title, options: .allZeros, range: titleLength)
            var sermonTitle = (title as NSString).substringWithRange(match!.rangeAtIndex(1))
            var pastor = (title as NSString).substringWithRange(match!.rangeAtIndex(2))
            var date = (title as NSString).substringWithRange(match!.rangeAtIndex(3)) + "/"
            date += (title as NSString).substringWithRange(match!.rangeAtIndex(4)) + "/"
            date += (title as NSString).substringWithRange(match!.rangeAtIndex(5))
            return (sermonTitle, pastor, date)
        } else {
            return (title, nil, nil)
        }
    }
    

    // MARK: Background Audio
    override func canBecomeFirstResponder() -> Bool {
        return true
    }

    func audioSessionInterrupted(notification:NSNotification) {
        println("interruption received: \(notification)")
        pauseSermon()
    }
    
    //response to remote control events
    
    override func remoteControlReceivedWithEvent(receivedEvent:UIEvent)  {
        if (receivedEvent.type == .RemoteControl) {
            switch receivedEvent.subtype {
            case .RemoteControlTogglePlayPause:
                togglePlayPause()
            case .RemoteControlPlay:
                playSermon()
            case .RemoteControlPause:
                pauseSermon()
            default:
                println("received sub type \(receivedEvent.subtype) Ignoring")
            }
        } else {
            println("Ack")
        }
    }
    
    func playSermon() {
        audioPlayer.resume()
        if currentSermon != nil {
            setAsNowPlaying(currentSermon!)
        }
        playPauseCellButton?.setPaused(false, animated: true)
    }
    
    func pauseSermon() {
        audioPlayer.pause()
        if currentSermon != nil {
            setAsNowPaused(currentSermon!)
        } else {
            println("Err?")
        }
        playPauseCellButton?.setPaused(true, animated: true)
    }
    
    func initSession() {
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "audioSessionInterrupted:", name: AVAudioSessionInterruptionNotification, object: AVAudioSession.sharedInstance())
        var error:NSError?
        
        AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback, error: &error)
        
        if let nonNilError = error {
            println("an error occurred when audio session category.\n \(error)")
        }
        
        var activationError:NSError?
        let success = AVAudioSession.sharedInstance().setActive(true, error: &activationError)
        if !success {
            if let nonNilActivationError = activationError {
                println("an error occurred when audio session category.\n \(nonNilActivationError)")
            } else {
                println("audio session could not be activated")
            }
        }
        becomeFirstResponder()
        //MPRemoteCommandCenter.sharedCommandCenter().togglePlayPauseCommand.addTarget(self, action: "togglePlayPause")
    }
    
    func deinitSession() {
        AVAudioSession.sharedInstance().setActive(false, error: nil)
        AVAudioSession.sharedInstance().setCategory(nil, error: nil)
        
    }
    
    func audioPlayer(audioPlayer: STKAudioPlayer!, unexpectedError errorCode: STKAudioPlayerErrorCode) {
        showErrorDialog("There was an error streaming this sermon")
        stopPlayback()
    }
    
    func audioPlayer(audioPlayer: STKAudioPlayer!, didFinishPlayingQueueItemId queueItemId: NSObject!, withReason stopReason: STKAudioPlayerStopReason, andProgress progress: Double, andDuration duration: Double) {
        if stopReason.value != 0 && stopReason.value != 2 {
            println("STOP REASON: \(stopReason.value)")
            stopPlayback()
        }
    }
    
    func audioPlayer(audioPlayer: STKAudioPlayer!, didFinishBufferingSourceWithQueueItemId queueItemId: NSObject!) {
        println("Finished Buffering!")
    }
    
    func audioPlayer(audioPlayer: STKAudioPlayer!, stateChanged state: STKAudioPlayerState, previousState: STKAudioPlayerState) {
        println("State Changed!")
    }
    
    func audioPlayer(audioPlayer: STKAudioPlayer!, logInfo line: String!) {
        
    }
    
    func audioPlayer(audioPlayer: STKAudioPlayer!, didStartPlayingQueueItemId queueItemId: NSObject!) {
        println("Playing now!")
    }
    
    func audioPlayer(audioPlayer: STKAudioPlayer!, didCancelQueuedItems queuedItems: [AnyObject]!) {
        //stopPlayback()
        println("Canceled Queued Items?")
    }
}

// MARK: Misc.

func ==(state1 : STKAudioPlayerState, state2 : STKAudioPlayerState) -> Bool {
    return state1.value == state2.value
}

func !=(state1 : STKAudioPlayerStopReason, state2 : STKAudioPlayerStopReason) -> Bool {
    return state1.value != state2.value
}