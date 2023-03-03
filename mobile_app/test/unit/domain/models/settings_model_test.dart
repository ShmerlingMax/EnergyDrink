import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../../mock_data.dart';

void main() {
  group('Test Settings', () {
    late Settings settings;
    setUpAll(() {
      TestWidgetsFlutterBinding.ensureInitialized();
      settings = Settings();
    });
    test('init', () {
      settings.init(mockShopsNames, mockBrandsNames);
      expect(settings.shops, equals(mockShopsNames));
      expect(settings.brands, equals(mockBrandsNames));
      expect(settings.sortingParameter, equals(SortingParameter.discount));
      expect(settings.sortingDirection, equals(SortingDirection.descending));
    });

    test('reset', () {
      final expected = Settings()
        ..init([], [])
        ..sortingParameter = SortingParameter.price
        ..sortingDirection = SortingDirection.ascending;
      settings.init(mockShopsNames, mockBrandsNames);
      settings.reset(
        [],
        [],
        SortingParameter.price,
        SortingDirection.ascending,
      );
      expect(settings, equals(expected));
    });

    group('getters', () {
      setUp(() {
        settings = Settings();
        settings.init(mockShopsNames, mockBrandsNames);
      });

      test('shops', () {
        expect(settings.shops, equals(mockShopsNames));
      });

      test('brands', () {
        expect(settings.brands, equals(mockBrandsNames));
      });

      test('sortingParameter', () {
        expect(settings.sortingParameter, equals(SortingParameter.discount));
      });

      test('sortingDirection', () {
        expect(settings.sortingDirection, equals(SortingDirection.descending));
      });
    });

    group('setters', () {
      setUp(() {
        settings = Settings();
      });

      test('shops', () {
        settings.shops = mockShopsNames;
        expect(settings.shops, equals(mockShopsNames));
      });

      test('brands', () {
        settings.brands = mockBrandsNames;
        expect(settings.brands, equals(mockBrandsNames));
      });

      test('sortingParameter', () {
        settings.sortingParameter = SortingParameter.price;
        expect(settings.sortingParameter, equals(SortingParameter.price));
      });

      test('sortingDirection', () {
        settings.sortingDirection = SortingDirection.ascending;
        expect(settings.sortingDirection, equals(SortingDirection.ascending));
      });
    });
  });
}
