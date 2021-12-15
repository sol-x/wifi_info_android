import 'dart:async';

import 'package:flutter/services.dart';

class WifiInfo {
  final bool isConnected;
  final int rssi;
  final String ssid;
  final String bssid;

  WifiInfo(
      {required this.isConnected,
      required this.rssi,
      required this.ssid,
      required this.bssid});
}

class WifiInfoAndroid {
  static const MethodChannel _channel = MethodChannel('wifi_info_android');

  static Future<WifiInfo?> getWifiInfo() async {
    final Map<dynamic, dynamic>? result =
        await _channel.invokeMapMethod("getWifiInfo");
    if (result == null) {
      return null;
    }
    return WifiInfo(
      isConnected: result['isConnected'],
      rssi: result['rssi'],
      ssid: result['ssid'],
      bssid: result['bssid'],
    );
  }
}
