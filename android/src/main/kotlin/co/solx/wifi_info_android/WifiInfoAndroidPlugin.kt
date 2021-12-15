package co.solx.wifi_info_android

import android.app.Activity
import androidx.annotation.NonNull
import android.content.Context
import android.net.wifi.WifiManager

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


/** WifiInfoAndroidPlugin */
class WifiInfoAndroidPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel: MethodChannel
  private var activity: Activity? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "wifi_info_android")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getWifiInfo") {
      getWifiInfo(result)
    } else {
      result.notImplemented()
    }
  }

  private fun getWifiInfo(result: MethodChannel.Result) {
    try {
      if (activity == null) {
        result.success(null);
        return;
      }

      val wifiManager = activity?.getSystemService(Context.WIFI_SERVICE) as WifiManager
      if (wifiManager == null) {
        result.success(null);
        return;
      }

      val wifiInfo = wifiManager.connectionInfo
      result.success(mutableMapOf(
        "bssid" to wifiInfo.getBSSID(),
        "ssid" to wifiInfo.getSSID(),
        "rssi" to wifiInfo.getRssi(),
        "isConnected" to (wifiInfo.getNetworkId() != -1)
      ))
    } catch(e: Exception) {
      result.error("nowifi", "Got exception when trying to read Wi-Fi state", null)
    }
  }

  override fun onAttachedToActivity(@NonNull binding: ActivityPluginBinding) {
    activity = binding.activity;
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(@NonNull binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }
}
