import Flutter
import UIKit

public class InAppUpdatesPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "in_app_updates", binaryMessenger: registrar.messenger())
    let instance = InAppUpdatesPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }
  var id: String? ;
  var country: String?;
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "isUpdateAvailable":
        let arguments = call.arguments as? Dictionary<String,Any?>
        if(arguments==nil){
            result(FlutterError(code: "NULLABLE_EXCEPTION",message: "You should pass IosAppStoreRequestModel" ,details: nil))
            return
        }
        id = (arguments!["id"] as! String)
        country = (arguments!["country"] as! String)
        
        let url = URL(string: "https://itunes.apple.com/lookup?id=\(String(describing: id!))&country=\(String(describing: country!))")!
        
    let task = URLSession.shared.dataTask(with: url) { data, response, error in
        if let data = data {
            do {
                 let decoder = JSONDecoder()
                 let responseResult = try decoder.decode(ResponseResult.self, from: data)
                 let params = try DictionaryEncoder().encode(responseResult)
                 result(params)
            } catch {
                result(FlutterError(code: "PARSE_ERROR",message: "Can not parse new data" ,details: nil))
            }
            } else if let error = error {
                result(FlutterError(code: "NETWORK_EXCEPTION",message: error.localizedDescription ,details: nil))
            }
        }
    task.resume()
    case "updateApp":
        if(id==nil || country==nil){
            result(FlutterError(code: "NULLABLE_EXCEPTION",message: "id==nil or country==nil use isUpdateAvailable first" ,details: nil))
            return
        }
        if let url = URL(string: "itms-apps://itunes.apple.com/app/id\(String(describing: id!))") {
            UIApplication.shared.open(url)
            result(nil)
        }
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
