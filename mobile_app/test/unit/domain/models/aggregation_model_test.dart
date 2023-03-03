import 'dart:convert';

import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/energy_drink_model/energy_drink_model.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../../mock_data.dart';

void main() {
  group('Test Aggregation', () {
    late Aggregation aggregation;

    setUpAll(() {
      TestWidgetsFlutterBinding.ensureInitialized();
      aggregation = Aggregation();
    });
    test('init', () {
      expect(aggregation.isNotInit, equals(true));
      aggregation.init(mockShopsNames, mockBrandsNames);
      expect(aggregation.isNotInit, equals(false));
    });

    group('getters', () {
      setUp(() {
        aggregation.init(mockShopsNames, mockBrandsNames);
      });

      test('shops', () {
        expect(aggregation.shops, equals(mockShopsNames));
      });

      test('brands', () {
        expect(aggregation.brands, equals(mockBrandsNames));
      });

      test('sortingParameter', () {
        expect(aggregation.sortingParameter, equals(SortingParameter.discount));
      });

      test('sortingDirection', () {
        expect(
            aggregation.sortingDirection, equals(SortingDirection.descending));
      });

      test('search', () {
        expect(aggregation.search, equals(''));
      });

      test('isNotInit', () {
        expect(aggregation.isNotInit, equals(false));
      });

      test('allShops', () {
        expect(aggregation.allShops, equals(mockShopsNames));
      });

      test('allBrands', () {
        expect(aggregation.allBrands, equals(mockBrandsNames));
      });
    });

    group('setters', () {
      setUp(() {
        aggregation = Aggregation();
      });

      test('shops', () {
        expect(aggregation.shops, equals([]));
        aggregation.shops = mockShopsNames;
        expect(aggregation.shops, equals(mockShopsNames));
      });

      test('brands', () {
        expect(aggregation.brands, equals([]));
        aggregation.brands = mockBrandsNames;
        expect(aggregation.brands, equals(mockBrandsNames));
      });

      test('sortingParameter', () {
        expect(aggregation.sortingParameter, equals(SortingParameter.discount));
        aggregation.sortingParameter = SortingParameter.price;
        expect(aggregation.sortingParameter, equals(SortingParameter.price));
      });

      test('sortingDirection', () {
        expect(
            aggregation.sortingDirection, equals(SortingDirection.descending));
        aggregation.sortingDirection = SortingDirection.ascending;
        expect(
            aggregation.sortingDirection, equals(SortingDirection.ascending));
      });

      test('search', () {
        expect(aggregation.search, equals(''));
        const newValue = 'search';
        aggregation.search = newValue;
        expect(aggregation.search, equals(newValue));
      });
    });

    group('sort', () {
      late EnergyDrink energyDrink1;
      late EnergyDrink energyDrink2;
      late EnergyDrink energyDrink3;

      setUp(() {
        energyDrink1 = EnergyDrink.fromJson(jsonDecode(mockEnergyDrinkString1));
        energyDrink2 = EnergyDrink.fromJson(jsonDecode(mockEnergyDrinkString2));
        energyDrink3 = EnergyDrink.fromJson(jsonDecode(mockEnergyDrinkString3));
        aggregation = Aggregation();
        aggregation.init(mockShopsNames, mockBrandsNames);
      });

      test('discount and descending', () {
        List<EnergyDrink> expected = [energyDrink1, energyDrink2, energyDrink3];
        final result =
            aggregation.sort(getMockShops()).map((e) => e.key).toList();
        expect(result, equals(expected));
      });

      test('discount and ascending', () {
        aggregation.sortingDirection = SortingDirection.ascending;
        List<EnergyDrink> expected = [energyDrink3, energyDrink2, energyDrink1];
        final result =
            aggregation.sort(getMockShops()).map((e) => e.key).toList();
        expect(result, equals(expected));
      });

      test('price and descending', () {
        aggregation.sortingParameter = SortingParameter.price;
        List<EnergyDrink> expected = [energyDrink3, energyDrink2, energyDrink1];
        final result =
            aggregation.sort(getMockShops()).map((e) => e.key).toList();
        expect(result, equals(expected));
      });

      test('price and ascending', () {
        aggregation.sortingParameter = SortingParameter.price;
        aggregation.sortingDirection = SortingDirection.ascending;
        List<EnergyDrink> expected = [energyDrink1, energyDrink2, energyDrink3];
        final result =
            aggregation.sort(getMockShops()).map((e) => e.key).toList();
        expect(result, equals(expected));
      });

      test('with search', () {
        aggregation.search = 'Энергетик 1';
        List<EnergyDrink> expected = [energyDrink1];
        final result =
            aggregation.sort(getMockShops()).map((e) => e.key).toList();
        expect(result, equals(expected));
      });
    });
  });
}
