import 'package:shared_preferences/shared_preferences.dart';

class SharedPrefService {
  static late SharedPreferences sharedPref;

  static Future<void> init() async {
    sharedPref = await SharedPreferences.getInstance();
  }

  static String? get(String key) {
    return sharedPref.getString(key);
  }

  static Future<bool> save(String key, String data) async {
    return sharedPref.setString(key, data);
  }
}
