import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:energy_drink/data/services/shared_pref_service.dart';

void main() {
  group('Test SharedPrefService', () {
    test('init', () async {
      SharedPreferences.setMockInitialValues({});
      await SharedPrefService.init();
      expect(SharedPrefService.sharedPref, isNot(null));
    });

    group('get', () {
      test('key exists', () async {
        const key = 'key';
        const value = 'value';
        SharedPreferences.setMockInitialValues({key: value});
        await SharedPrefService.init();
        expect(SharedPrefService.get(key), equals(value));
      });

      test('key does not exists', () async {
        const key = 'key';
        SharedPreferences.setMockInitialValues({});
        await SharedPrefService.init();
        expect(SharedPrefService.get(key), equals(null));
      });
    });

    group('save', () {
      test('key exists', () async {
        const key = 'key';
        const oldValue = 'oldValue';
        const newValue = 'newValue';
        SharedPreferences.setMockInitialValues({key: oldValue});
        await SharedPrefService.init();
        expect(await SharedPrefService.save(key, newValue), equals(true));
        expect(SharedPrefService.get(key), equals(newValue));
      });

      test('key does not exists', () async {
        const key = 'key';
        const newValue = 'newValue';
        SharedPreferences.setMockInitialValues({});
        await SharedPrefService.init();
        expect(await SharedPrefService.save(key, newValue), equals(true));
        expect(SharedPrefService.get(key), equals(newValue));
      });
    });
  });
}
