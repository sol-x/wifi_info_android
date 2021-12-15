import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:wifi_info_android/wifi_info_android.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Stream<WifiInfo?> checkConnectionStream() async* {
    var firstInfo = await WifiInfoAndroid.getWifiInfo();
    yield firstInfo;
    yield* Stream.periodic(const Duration(seconds: 2), (_) {
      return WifiInfoAndroid.getWifiInfo();
    }).asyncMap((event) => event);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('WiFi example app'),
        ),
        body: Center(
            child: StreamBuilder(
                stream: checkConnectionStream(),
                builder: (context, snapshot) {
                  final wifiInfo =
                      snapshot.connectionState == ConnectionState.waiting
                          ? null
                          : snapshot.data as WifiInfo?;

                  return wifiInfo == null
                      ? const Text('Could not get wifi info')
                      : Text(
                          'ssid: ${wifiInfo.ssid}, bssid: ${wifiInfo.bssid}, isConnected: ${wifiInfo.isConnected}, rssi: ${wifiInfo.rssi}\n');
                })),
      ),
    );
  }
}
