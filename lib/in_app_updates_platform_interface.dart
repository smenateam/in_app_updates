import 'package:in_app_updates/models/models.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'in_app_updates_method_channel.dart';

abstract class InAppUpdatesPlatform extends PlatformInterface {
  /// Constructs a InAppUpdatesPlatform.
  InAppUpdatesPlatform() : super(token: _token);

  static final Object _token = Object();

  static InAppUpdatesPlatform _instance = MethodChannelInAppUpdates();

  /// The default instance of [InAppUpdatesPlatform] to use.
  ///
  /// Defaults to [MethodChannelInAppUpdates].
  static InAppUpdatesPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [InAppUpdatesPlatform] when
  /// they register themselves.
  static set instance(InAppUpdatesPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<dynamic> isUpdateAvailable({
    IosAppStoreRequestModel? iosAppStoreRequestModel,
    bool? isHuawei,
  }) {
    throw UnimplementedError('updateAvailable() has not been implemented.');
  }

  Stream<dynamic> getAndroidStream() {
    throw UnimplementedError('getEventChannel() has not been implemented.');
  }

  Future<bool?> completeUpdate() {
    throw UnimplementedError('completeUpdate() has not been implemented.');
  }

  Future<void> updateApp({
    AndroidInAppUpdateRequestConfig? androidInAppUpdateRequestModel,
    bool? mustBtnOne,
  }) {
    throw UnimplementedError('updateApp() has not been implemented.');
  }
}
