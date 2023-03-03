import 'dart:convert';

import 'package:energy_drink/domain/models/energy_drink_model/energy_drink_model.dart';
import 'package:energy_drink/domain/models/shop_model/shop_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../../mock_data.dart';

void main() {
  group('Test Shop', () {
    late Shop shop;

    setUp(() {
      shop = Shop(
        'Магазин 1',
        Image.asset('assets/search.png'),
        [
          EnergyDrink.fromJson(
            jsonDecode(mockEnergyDrinkString1),
          ),
        ],
      );
    });

    test('fromJson', () {
      expect(
        Shop.fromJson(jsonDecode(mockShopString1)),
        equals(shop),
      );
    });

    test('toJson', () {
      final expected = jsonDecode(mockShopString1);
      expected['image'] = shop.image.toString();
      expected['energyDrinks'] = shop.energyDrinks;
      expect(shop.toJson(), equals(expected));
    });
  });
}
