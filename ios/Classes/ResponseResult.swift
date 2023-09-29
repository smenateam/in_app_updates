import Foundation


struct ResponseResult : Codable {
    let resultCount: Int?
    let results: [Result]?
}
struct Result : Codable {
     let advisories: [String]?
     let supportedDevices: [String]?
     let ipadScreenshotUrls: [String]?
     let appletvScreenshotUrls: [String]?
     let artworkUrl60: String?
     let artworkUrl512: String?
     let artworkUrl100: String?
     let artistViewUrl: String?
     let kind: String?
     let releaseNotes: String?
     let minimumOsVersion: String?
     let description: String?
     let genreIds: [String]?
     let primaryGenreName: String?
     let primaryGenreId: Int?
     let sellerName: String?
     let isVppDeviceBasedLicensingEnabled: Bool?
     let releaseDate: String?
     let bundleId: String?
     let trackId: Int?
     let trackName: String?
     let currency: String?
     let currentVersionReleaseDate: String?
     let trackCensoredName: String?
     let languageCodesISO2A: [String]?
     let fileSizeBytes: String?
     let formattedPrice: String?
     let averageUserRatingForCurrentVersion: Float?
     let userRatingCountForCurrentVersion: Int?
     let averageUserRating: Float?
     let trackViewUrl: String?
     let trackContentRating: String?
     let artistId: Int?
     let artistName: String?
     let genres: [String]?
     let price: Float?
     let version: String?
     let wrapperType: String?
     let userRatingCount: Int?
}

class DictionaryEncoder {
    private let encoder = JSONEncoder()

    func encode<T>(_ value: T) throws -> [String: Any] where T : Encodable {
        let data = try encoder.encode(value)
        return try JSONSerialization.jsonObject(with: data, options: .allowFragments) as! [String: Any]
    }
}
