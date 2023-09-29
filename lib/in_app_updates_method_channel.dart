import 'dart:io';

import 'package:flutter/services.dart';
import 'package:in_app_updates/models/models.dart';

import 'in_app_updates_platform_interface.dart';

/// An implementation of [InAppUpdatesPlatform] that uses method channels.
class MethodChannelInAppUpdates extends InAppUpdatesPlatform {
  /// The method channel used to interact with the native platform.
  final _methodChannel = const MethodChannel('in_app_updates');
  final _eventChannel = const EventChannel('in_app_updates_event_channel');

  @override
  Future<dynamic> isUpdateAvailable({
    IosAppStoreRequestModel? iosAppStoreRequestModel,
    bool? isHuawei,
  }) async {
    try {
      Map<String, dynamic> args = (iosAppStoreRequestModel?.toJson() ?? {})..addAll({"isHuawei": isHuawei});
      final result = await _methodChannel.invokeMethod(
        'isUpdateAvailable',
        args,
      );
      if (Platform.isIOS) {
        return IosAppStoreModel.fromJson(result.cast<String, dynamic>());
      }
      if (isHuawei == false) {
        return AndroidInAppUpdateInfoModel.fromJson(result.cast<String, dynamic>());
      }
      return null;
    } catch (e) {
      rethrow;
    }
  }

  @override
  Stream<dynamic> getAndroidStream() => _eventChannel.receiveBroadcastStream();

  @override
  Future<bool?> completeUpdate() async {
    try {
      if (Platform.isAndroid) {
        return await _methodChannel.invokeMethod('completeUpdate');
      }
      return null;
    } catch (e) {
      rethrow;
    }
  }

  @override
  Future<void> updateApp({
    AndroidInAppUpdateRequestConfig? androidInAppUpdateRequestModel,
    bool? mustBtnOne,
  }) async {
    try {
      if (Platform.isAndroid) {
        Map<String, dynamic> args = (androidInAppUpdateRequestModel?.toJson() ?? {})
          ..addAll(
            {'mustBtnOne': mustBtnOne ?? false},
          );
        return await _methodChannel.invokeMethod('updateApp', args);
      } else if (Platform.isIOS) {
        return await _methodChannel.invokeMethod('updateApp');
      }
    } catch (e) {
      rethrow;
    }
  }
}
