import SwiftUI
import shared

//let videoPlayerController = VideoPlayerControllerFactory().createVideoPlayer()

struct ContentView: View {
    
	var body: some View {
        VStack{
            Text("Hello this is ios app a")
            
            ComposeView().edgesIgnoringSafeArea(.all).statusBarHidden(true)
        }
        
//         HStack{
//             Button(action: {
//                 videoPlayerController.pause()
//
//                     }, label: {
//                         Text("Pause Video")
//                     })
//
//             Button(action: {
//                 videoPlayerController.play()
//                     }, label: {
//                         Text("Play Video")
//                     })
//         }
       
	}
    

}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
