import 'dart:convert';

import 'package:energy_drink/domain/models/energy_drink_model/energy_drink_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../../mock_data.dart';

void main() {
  group('Test EnergyDrink', () {
    late EnergyDrink energyDrink;

    setUp(() {
      energyDrink = EnergyDrink(
        'Энергетик 1',
        'Бренд 1',
        Image.asset('assets/search.png'),
        50.00,
        100.00,
        0.75,
        499,
      );
    });

    test('fromJson', () {
      expect(
        EnergyDrink.fromJson(jsonDecode(mockEnergyDrinkString1)),
        equals(energyDrink),
      );
    });

    test('toJson', () {
      final expected = jsonDecode(mockEnergyDrinkString1);
      expected['image'] = energyDrink.image.toString();
      expect(energyDrink.toJson(), equals(expected));
    });
  });
}
