import 'package:dio/dio.dart';
import 'package:energy_drink/data/api/api.dart';
import 'package:energy_drink/data/api/mock_dio_adapter.dart';
import 'package:energy_drink/data/services/shared_pref_service.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../mock_data.dart';

class MockRequestOptions extends Mock implements RequestOptions {}

void main() {
  late final MockDioAdapter mockDioAdapter;

  setUpAll(() {
    mockDioAdapter = MockDioAdapter();
    registerFallbackValue(MockRequestOptions());
  });

  setUp(() {
    Api().test = true;
  });

  group('Test API', () {
    test('client getter', () {
      final baseOptions = BaseOptions(
        baseUrl: ApiConfig.baseUrl,
        connectTimeout: 5000,
        receiveTimeout: 10000,
        sendTimeout: 5000,
      );
      final result = Api().client.options;
      expect(result.baseUrl, equals(baseOptions.baseUrl));
      expect(result.connectTimeout, equals(baseOptions.connectTimeout));
      expect(result.receiveTimeout, equals(baseOptions.receiveTimeout));
      expect(result.sendTimeout, equals(baseOptions.sendTimeout));
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

        expect(await Api().getShops(), equals(await getMockShops()));
      });

      test('no data saved', () async {
        final time = DateTime.now().toString();
        SharedPreferences.setMockInitialValues(
          {
            "timeShops": time,
          },
        );
        await SharedPrefService.init();
        final mockResponse = ResponseBody.fromString(
          mockShopsString,
          200,
          headers: {
            Headers.contentTypeHeader: [Headers.textPlainContentType],
          },
        );
        when(() => mockDioAdapter.fetch(any(), any(), any()))
            .thenAnswer((_) async => mockResponse);

        expect(await Api().getShops(), equals(await getMockShops()));
        expect(SharedPrefService.get('shops'), equals(mockShopsString));
        expect(SharedPrefService.get('timeShops'), isNot(time));
      });

      test('successfully updating data', () async {
        SharedPreferences.setMockInitialValues({});
        await SharedPrefService.init();
        final mockResponse = ResponseBody.fromString(
          mockShopsString,
          200,
          headers: {
            Headers.contentTypeHeader: [Headers.textPlainContentType],
          },
        );
        when(() => mockDioAdapter.fetch(any(), any(), any()))
            .thenAnswer((_) async => mockResponse);

        expect(await Api().getShops(), equals(await getMockShops()));
        expect(SharedPrefService.get('shops'), equals(mockShopsString));
        expect(SharedPrefService.get('timeShops'), isNot(null));
      });

      test('error updating data', () async {
        SharedPreferences.setMockInitialValues({});
        await SharedPrefService.init();
        final mockResponse = ResponseBody.fromString(
          mockShopsString,
          500,
        );
        when(() => mockDioAdapter.fetch(any(), any(), any()))
            .thenAnswer((_) async => mockResponse);

        expect(await Api().getShops(), equals([]));
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

        expect(await Api().getBrands(), equals(await getMockBrands()));
      });

      test('no data saved', () async {
        final time = DateTime.now().toString();
        SharedPreferences.setMockInitialValues(
          {
            "timeBrands": time,
          },
        );
        await SharedPrefService.init();
        final mockResponse = ResponseBody.fromString(
          mockBrandsString,
          200,
          headers: {
            Headers.contentTypeHeader: [Headers.textPlainContentType],
          },
        );
        when(() => mockDioAdapter.fetch(any(), any(), any()))
            .thenAnswer((_) async => mockResponse);

        expect(await Api().getBrands(), equals(await getMockBrands()));
        expect(SharedPrefService.get('brands'), equals(mockBrandsString));
        expect(SharedPrefService.get('timeBrands'), isNot(time));
      });

      test('successfully updating data', () async {
        SharedPreferences.setMockInitialValues({});
        await SharedPrefService.init();
        final mockResponse = ResponseBody.fromString(
          mockBrandsString,
          200,
          headers: {
            Headers.contentTypeHeader: [Headers.textPlainContentType],
          },
        );
        when(() => mockDioAdapter.fetch(any(), any(), any()))
            .thenAnswer((_) async => mockResponse);

        expect(await Api().getBrands(), equals(await getMockBrands()));
        expect(SharedPrefService.get('brands'), equals(mockBrandsString));
        expect(SharedPrefService.get('timeBrands'), isNot(null));
      });

      test('error updating data', () async {
        SharedPreferences.setMockInitialValues({});
        await SharedPrefService.init();
        final mockResponse = ResponseBody.fromString(
          mockBrandsString,
          500,
        );
        when(() => mockDioAdapter.fetch(any(), any(), any()))
            .thenAnswer((_) async => mockResponse);

        expect(await Api().getBrands(), equals([]));
      });
    });
  });
}
