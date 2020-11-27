package expo.modules.developmentclient.modules

import android.content.Intent
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import expo.modules.developmentclient.DevelopmentClientController.Companion.instance
import kotlinx.coroutines.runBlocking

private const val ON_NEW_DEEP_LINK_EVENT = "expo.modules.developmentclient.onnewdeeplinkevnet"

class DevelopmentClientModule(reactContext: ReactApplicationContext?) : ReactContextBaseJavaModule(reactContext) {
  override fun initialize() {
    super.initialize()
    instance.pendingIntentRegistry.subscribe(this::onNewPendingIntent)
  }

  override fun invalidate() {
    super.invalidate()
    instance.pendingIntentRegistry.unsubscribe(this::onNewPendingIntent)
  }

  override fun getName(): String {
    return "EXDevelopmentClient"
  }

  @ReactMethod
  fun loadApp(url: String, promise: Promise) {
    runBlocking {
      try {
        instance.loadApp(url)
      } catch (e: Exception) {
        promise.reject(e)
      }
      promise.resolve(null)
    }
  }

  @ReactMethod
  fun getPendingDeepLink(promise: Promise) {
    promise.resolve(instance.pendingIntentRegistry.intent?.data?.toString())
  }

  private fun onNewPendingIntent(intent: Intent) {
    intent.data?.toString()?.let {
      reactApplicationContext
        .getJSModule(RCTDeviceEventEmitter::class.java)
        .emit(ON_NEW_DEEP_LINK_EVENT, it)
    }
  }
}
