package com.github.nabin0.kmmvideoplayer.controller.test


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.flow.MutableStateFlow
import platform.AVFoundation.AVContentKeyRecipientProtocol
import platform.AVFoundation.AVContentKeyRequest
import platform.AVFoundation.AVContentKeyRequestProtocolVersionsKey
import platform.AVFoundation.AVContentKeyRequestRetryReason
import platform.AVFoundation.AVContentKeyResponse
import platform.AVFoundation.AVContentKeySession
import platform.AVFoundation.AVContentKeySession.Companion.contentKeySessionWithKeySystem
import platform.AVFoundation.AVContentKeySessionDelegateProtocol
import platform.AVFoundation.AVContentKeySystemAuthorizationToken
import platform.AVFoundation.AVContentKeySystemFairPlayStreaming
import platform.AVFoundation.AVPersistableContentKeyRequest
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.addContentKeyRecipient
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.setRate
import platform.AVKit.AVPlayerViewController
import platform.AVKit.AVPlayerViewControllerDelegateProtocol
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.dataUsingEncoding
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.setHTTPBody
import platform.Foundation.setHTTPMethod
import platform.Foundation.setValue
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_queue_global_t

actual class CounterData {
    actual val count: MutableStateFlow<Int> = MutableStateFlow(0)
    actual fun updateCounter() {
        count.value = count.value + 1
    }

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun player() {
        val url =
            "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"

        val controller = TestController()

        UIKitView(
            factory = {
                // Create a UIView to hold the AVPlayerLayer
                val playerContainer = UIView()
                playerContainer.addSubview(controller.view)
                // Return the playerContainer as the root UIView
                playerContainer
            },
            onResize = { view: UIView, rect: CValue<CGRect> ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                view.layer.setFrame(rect)
                //playerLayer.setFrame(rect)
                controller.view.layer.frame = rect
                CATransaction.commit()
            },
            update = { view ->
                controller.player!!.play()
            },
            modifier = Modifier.fillMaxSize()
        )

        return

        /*

//        val url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//        val plr = AVPlayer(NSURL(string = url))
//        val ctrl = AVPlayerViewController()
//        ctrl.player = plr
//        plr.play()
        val player = remember { AVPlayer(uRL = NSURL.URLWithString(url)!!) }
        //val playerLayer = remember { AVPlayerLayer() }
        val avPlayerViewController = remember { AVPlayerViewController() }
//        val avPlayerViewController = remember { CustomControllew()}
        avPlayerViewController.player = player
        avPlayerViewController.showsPlaybackControls = true
        //playerLayer.player = player
        // Use a UIKitView to integrate with your existing UIKit views
        UIKitView(
            factory = {
                // Create a UIView to hold the AVPlayerLayer
                val playerContainer = UIView()
                playerContainer.addSubview(avPlayerViewController.view)
                // Return the playerContainer as the root UIView
                playerContainer
            },
            onResize = { view: UIView, rect: CValue<CGRect> ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                view.layer.setFrame(rect)
                //playerLayer.setFrame(rect)
                avPlayerViewController.view.layer.frame = rect
                CATransaction.commit()
            },
            update = { view ->
                player.play()
                avPlayerViewController.player!!.play()
            },
            modifier = Modifier.fillMaxSize()
        )
        println("--------------step 1")

        try {
            val apt = PlayerDTest(player, avPlayerViewController)
            apt.callthis()

        } catch (e: Exception) {
            e.printStackTrace()
            println("----------error ${e.message}  $e")
        }

    }

         */

    }


    class TestController : AVPlayerViewController, AVContentKeySessionDelegateProtocol {
        val fpsCertificateUrl: String =
            "https://v-msnprod.monumentalsportsnetwork.com/certs/fairplay.cer"
        val licenseToken: String =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2ZXJzaW9uIjoxLCJjb21fa2V5X2lkIjoiZWI5ZDU3NjMtNTc5OS00ZDA0LWI4NzgtYjAyOTAwYmFjNDUyIiwibWVzc2FnZSI6eyJ0eXBlIjoiZW50aXRsZW1lbnRfbWVzc2FnZSIsInZlcnNpb24iOjIsImxpY2Vuc2UiOnsiZHVyYXRpb24iOjkwMH0sImNvbnRlbnRfa2V5c19zb3VyY2UiOnsiaW5saW5lIjpbeyJpZCI6IjUyMGZmYWIwLTA4Y2ItNDhlMS04NDlhLTVmYjliYjg4YjA2NCIsInVzYWdlX3BvbGljeSI6IlBvbGljeSBBIn1dfSwiY29udGVudF9rZXlfdXNhZ2VfcG9saWNpZXMiOlt7Im5hbWUiOiJQb2xpY3kgQSIsInBsYXlyZWFkeSI6eyJtaW5fZGV2aWNlX3NlY3VyaXR5X2xldmVsIjoyMDAwLCJwbGF5X2VuYWJsZXJzIjpbIjc4NjYyN0Q4LUMyQTYtNDRCRS04Rjg4LTA4QUUyNTVCMDFBNyJdfSwid2lkZXZpbmUiOnsiZGV2aWNlX3NlY3VyaXR5X2xldmVsIjoiU1dfU0VDVVJFX0NSWVBUTyJ9fV19fQ.QfBjXZuL3EijeNhqJf88YyePaSow3TsK2S0TnjaNfTw"
        val licenseServiceUrl: String =
            "https://37eb8fe7.drm-fairplay-licensing.axprod.net/AcquireLicense"
        val videoUrl: String =
            "https://v-msnprod.monumentalsportsnetwork.com/Renditions/20231127/1701124798032_download_msn_app_preview/hls/fairplay/1701124798032_download_msn_app_preview.m3u8"
        var fpsCertificate: NSData? = null
        var contentKeySession: AVContentKeySession? = null
        val urlSession = NSURLSession.sharedSession

        val url =
            "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"

        @OptIn(BetaInteropApi::class)
        @OverrideInit
        constructor() : super(nibName = null, bundle = null)


        override fun viewDidLoad() {
            super.viewDidLoad()
            prepareAndPlay()
        }

        private fun prepareAndPlay() {
            val player = AVPlayer(uRL = NSURL.URLWithString(url)!!)
            this.player = player
            this.showsPlaybackControls = true
        }

        override fun contentKeySession(
            session: AVContentKeySession,
            didProvideContentKeyRequest: AVContentKeyRequest
        ) {
            println("-----------  hello this is controller")
        }

    }


    class PlayerDTest(val player: AVPlayer, val avplayerController: AVPlayerViewController) :
        NSObject(),
        AVContentKeySessionDelegateProtocol {

        // Certificate Url
        val fpsCertificateUrl: String =
            "https://v-msnprod.monumentalsportsnetwork.com/certs/fairplay.cer"

        // License Token
        val licenseToken: String =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2ZXJzaW9uIjoxLCJjb21fa2V5X2lkIjoiZWI5ZDU3NjMtNTc5OS00ZDA0LWI4NzgtYjAyOTAwYmFjNDUyIiwibWVzc2FnZSI6eyJ0eXBlIjoiZW50aXRsZW1lbnRfbWVzc2FnZSIsInZlcnNpb24iOjIsImxpY2Vuc2UiOnsiZHVyYXRpb24iOjkwMH0sImNvbnRlbnRfa2V5c19zb3VyY2UiOnsiaW5saW5lIjpbeyJpZCI6IjUyMGZmYWIwLTA4Y2ItNDhlMS04NDlhLTVmYjliYjg4YjA2NCIsInVzYWdlX3BvbGljeSI6IlBvbGljeSBBIn1dfSwiY29udGVudF9rZXlfdXNhZ2VfcG9saWNpZXMiOlt7Im5hbWUiOiJQb2xpY3kgQSIsInBsYXlyZWFkeSI6eyJtaW5fZGV2aWNlX3NlY3VyaXR5X2xldmVsIjoyMDAwLCJwbGF5X2VuYWJsZXJzIjpbIjc4NjYyN0Q4LUMyQTYtNDRCRS04Rjg4LTA4QUUyNTVCMDFBNyJdfSwid2lkZXZpbmUiOnsiZGV2aWNlX3NlY3VyaXR5X2xldmVsIjoiU1dfU0VDVVJFX0NSWVBUTyJ9fV19fQ.QfBjXZuL3EijeNhqJf88YyePaSow3TsK2S0TnjaNfTw"

        // License Service Url
        val licenseServiceUrl: String =
            "https://37eb8fe7.drm-fairplay-licensing.axprod.net/AcquireLicense"

        // Video url
        val videoUrl: String =
            "https://v-msnprod.monumentalsportsnetwork.com/Renditions/20231127/1701124798032_download_msn_app_preview/hls/fairplay/1701124798032_download_msn_app_preview.m3u8"

        // Certificate data
        var fpsCertificate: NSData? = null

        // Content Key session
        var contentKeySession: AVContentKeySession? = null

        // URLSession
        val urlSession = NSURLSession.sharedSession

        init {
            println("-----------inside the mehtod init")
            player.setRate(2f)
        }


        fun callthis() {
            contentKeySession = contentKeySessionWithKeySystem(AVContentKeySystemFairPlayStreaming)
//        contentKeySession = contentKeySessionWithKeySystem(AVContentKeySystemAuthorizationToken)
            contentKeySession!!.setDelegate(this@PlayerDTest, dispatch_queue_global_t())
            // contentKeySession!!.setDelegate(self, queue: DispatchQueue(label: "\(Bundle.main.bundleIdentifier!).ContentKeyDelegateQueue"))
            val assetUrl = NSURL(fileURLWithPath = videoUrl)
            val asset: AVURLAsset = AVURLAsset(uRL = assetUrl, null)
            contentKeySession!!.addContentKeyRecipient(asset as AVContentKeyRecipientProtocol)
            val playerItem = AVPlayerItem(asset = asset)
            player.replaceCurrentItemWithPlayerItem(playerItem)
            player.play()
            println("---------------inside callthis")

        }


        override fun contentKeySession(
            session: AVContentKeySession,
            contentKeyRequest: AVContentKeyRequest,
            didFailWithError: NSError
        ) {
            println("---------------inside callthis")

        }

        override fun contentKeySession(
            session: AVContentKeySession,
            didProvidePersistableContentKeyRequest: AVPersistableContentKeyRequest
        ) {
            println("---------------inside callthis")
        }


        override fun contentKeySession(
            session: AVContentKeySession,
            didUpdatePersistableContentKey: NSData,
            forContentKeyIdentifier: Any
        ) {
            println("---------------inside callthis")

        }

        override fun contentKeySessionContentProtectionSessionIdentifierDidChange(session: AVContentKeySession) {
            println("---------------inside callthis")

        }

        override fun contentKeySessionDidGenerateExpiredSessionReport(session: AVContentKeySession) {
            println("---------------inside callthis")
        }

        override fun contentKeySession(
            session: AVContentKeySession,
            didProvideContentKeyRequest: AVContentKeyRequest
        ) {
            println("---------------this opme os ca;;ed hjerejlajgiosdfjg")

            val contentKeyIdentifierString = didProvideContentKeyRequest.identifier as? String
            println("--------contentidentifietr $contentKeyIdentifierString")


            val contentIdentifier = contentKeyIdentifierString?.replace("skd://", "")

            println("---------contentidentifietr $contentIdentifier")

            val contentIdentifierData = contentIdentifier?.encodeToByteArray()

            val getCkcAndMakeContentAvailable: (spcData: NSData?, error: NSError?) -> Unit =
                { spcData, error ->
                    if (error != null) {
                        println("---------ERROR: Failed to prepare SPC: $error")
                        didProvideContentKeyRequest.processContentKeyResponseError(error)
                    }

                    if (error == null) {

                        if (spcData != null) {
//                    /*
//                     Send SPC to License Service and obtain CKC.
//                    */
                            println("----------spc data get here $spcData")
                            val url = NSURL(string = licenseServiceUrl)
                            val ksmRequest = NSMutableURLRequest(uRL = url)
                            ksmRequest.setHTTPMethod("POST")
                            ksmRequest.setValue(
                                value = licenseToken,
                                forHTTPHeaderField = "X-AxDRM-Message"
                            )
                            ksmRequest.setHTTPBody(spcData)

                            val dataTask =
                                urlSession.dataTaskWithRequest(ksmRequest) { data, response, error ->
                                    // Handle the completion of the data task here
                                    if (error != null) {
                                        println("ERROR: Error getting CKC: ${error.localizedDescription}")
                                    } else if (data != null) {
                                        /*
                                         AVContentKeyResponse is used to represent the data returned from the license service when requesting a key for
                                         decrypting content.
                                         */
                                        val keyResponse =
                                            AVContentKeyResponse.contentKeyResponseWithFairPlayStreamingKeyResponseData(
                                                data
                                            )

                                        /*
                                         Provide the content key response to make protected content available for processing.
                                        */
                                        didProvideContentKeyRequest.processContentKeyResponse(
                                            keyResponse
                                        )
                                    }
                                }

                            dataTask.resume()
                        }

                    }
                }


            try {
                val applicationCertificate = requestApplicationCertificate()

                if (applicationCertificate != null) {
                    print("application certificate is not null.")
                    didProvideContentKeyRequest.makeStreamingContentKeyRequestDataForApp(
                        contentIdentifier = contentIdentifierData?.toNsData(),
                        options = mapOf(AVContentKeyRequestProtocolVersionsKey to listOf(1)),
                        appIdentifier = applicationCertificate,
                        completionHandler = { nsData, nsError ->
                            getCkcAndMakeContentAvailable(nsData, nsError)
                        }
                    )
                }
            } catch (e: Exception) {
                // didProvideContentKeyRequest.processContentKeyResponseError()
                e.printStackTrace()
            }

            println("------------1")
        }

        private fun requestApplicationCertificate(): NSData? {
            println("------------certificate")

            var applicationCertificate: NSData? = null
            try {
                applicationCertificate =
                    NSData.dataWithContentsOfURL(url = NSURL.fileURLWithPath(path = fpsCertificateUrl))
            } catch (e: Exception) {
                print("Error loading FairPlay application certificate: ($e)")
            }

            return applicationCertificate
        }

        @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
        fun ByteArray.toNsData(): NSData? = memScoped {
            val string = NSString.create(string = this@toNsData.decodeToString())
            return string.dataUsingEncoding(encoding = NSUTF8StringEncoding)
        }

    }


    actual class CounterFactory {
        actual fun getCounterData(): CounterData {
            return CounterData()
        }

    }
