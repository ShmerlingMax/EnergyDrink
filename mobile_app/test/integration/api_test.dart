import 'package:energy_drink/data/api/api.dart';
import 'package:energy_drink/data/services/shared_pref_service.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../mock_data.dart';

void main() {
  final baseUrl = ApiConfig.baseUrl;

  setUp(() {
    ApiConfig.baseUrl = baseUrl;
  });

  group('getShops', () {
    test('use saved data', () async {
      SharedPreferences.setMockInitialValues(
        {
          "timeShops": DateTime.now().toString(),
          "shops": mockShopsString,
        },
      );
      await SharedPrefService.init();

      expect(await Api().getShops(), equals(getMockShops()));
    });

    test('use server data', () async {
      SharedPreferences.setMockInitialValues({});
      await SharedPrefService.init();

      expect((await Api().getShops()).isNotEmpty, equals(true));
      expect(SharedPrefService.get('shops'), isNot(null));
      expect(SharedPrefService.get('timeShops'), isNot(null));
    });

    test('server error and no saved data', () async {
      SharedPreferences.setMockInitialValues({});
      await SharedPrefService.init();

      ApiConfig.baseUrl = 'error';

      expect((await Api().getShops()).isEmpty, equals(true));
    });

    test('server error and have saved data', () async {
      final time = DateTime.now().toString();
      SharedPreferences.setMockInitialValues(
        {
          "timeShops": time,
          "shops": mockShopsString,
        },
      );
      await SharedPrefService.init();

      ApiConfig.baseUrl = 'error';

      expect(await Api().getShops(), equals(getMockShops()));
    });
  });

  group('getBrands', () {
    test('use saved data', () async {
      SharedPreferences.setMockInitialValues(
        {
          "timeBrands": DateTime.now().toString(),
          "brands": mockBrandsString,
        },
      );
      await SharedPrefService.init();

      expect(await Api().getBrands(), equals(getMockBrands()));
    });

    test('use server data', () async {
      SharedPreferences.setMockInitialValues({});
      await SharedPrefService.init();

      expect((await Api().getBrands()).isNotEmpty, equals(true));
      expect(SharedPrefService.get('brands'), isNot(null));
      expect(SharedPrefService.get('timeBrands'), isNot(null));
    });

    test('server error and no saved data', () async {
      SharedPreferences.setMockInitialValues({});
      await SharedPrefService.init();

      ApiConfig.baseUrl = 'error';

      expect((await Api().getBrands()).isEmpty, equals(true));
    });

    test('server error and have saved data', () async {
      final time = DateTime.now().toString();
      SharedPreferences.setMockInitialValues(
        {
          "timeBrands": time,
          "brands": mockBrandsString,
        },
      );
      await SharedPrefService.init();

      ApiConfig.baseUrl = 'error';

      expect(await Api().getBrands(), equals(getMockBrands()));
    });
  });
}
