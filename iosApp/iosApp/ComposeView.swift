import Foundation
import shared
import SwiftUI

struct ComposeView: UIViewControllerRepresentable {
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
    }
    
    func makeUIViewController(context: Context) -> some UIViewController {
        let videoItem: VideoItem?=VideoItem(videoUrl:"https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8", title: Optional.none, videoDescription: Optional.none, licenseUrl: Optional.none, listOfClosedCaptions:Optional.none)
        
        let listOfVideoItems = [
            VideoItem(videoUrl:"https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8", title: Optional.none, videoDescription: Optional.none, licenseUrl: Optional.none, listOfClosedCaptions:Optional.none),
            VideoItem(videoUrl:"https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8", title: Optional.none, videoDescription: Optional.none, licenseUrl: Optional.none, listOfClosedCaptions:Optional.none),
            VideoItem(videoUrl:"https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", title: Optional.none, videoDescription: Optional.none, licenseUrl: Optional.none, listOfClosedCaptions:Optional.none),
            VideoItem(videoUrl:"https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8", title: Optional.none, videoDescription: Optional.none, licenseUrl: Optional.none, listOfClosedCaptions:Optional.none),
            VideoItem(videoUrl:"https://cdn.bitmovin.com/content/assets/art-of-motion_drm/mpds/11331.mpd", title: Optional.none, videoDescription: Optional.none, licenseUrl: "https://cwip-shaka-proxy.appspot.com/no_auth", listOfClosedCaptions:Optional.none),
            
        ]
      return  MainViewControllerKt.VideoViewController(videoItem:Optional.none , listOfVideoUrls: listOfVideoItems)
    }
}
