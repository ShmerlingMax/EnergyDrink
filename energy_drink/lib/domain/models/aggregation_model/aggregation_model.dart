import 'package:flutter/material.dart';

import '../energy_drink_model/energy_drink_model.dart';
import '../shop_model/shop_model.dart';

enum SortingParameter {
  price,
  discount,
}

enum SortingDirection {
  ascending,
  descending,
}

class Aggregation with ChangeNotifier {
  final List<String> _shops;
  final List<String> _brands;
  SortingParameter _sortingParameter;
  SortingDirection _sortingDirection;
  String _search = '';
  List<String> _currentShops;
  List<String> _currentBrands;

  Aggregation(
    this._shops,
    this._brands,
  )   : _sortingParameter = SortingParameter.discount,
        _sortingDirection = SortingDirection.descending,
        _currentShops = _shops,
        _currentBrands = _brands;

  List<String> get shops => _currentShops;
  List<String> get brands => _currentBrands;
  SortingParameter get sortingParameter => _sortingParameter;
  SortingDirection get sortingDirection => _sortingDirection;
  String get search => _search;

  set shops(List<String> value) {
    _currentShops = value;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  set brands(List<String> value) {
    _currentBrands = value;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  set sortingParameter(SortingParameter value) {
    _sortingParameter = value;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  set sortingDirection(SortingDirection value) {
    _sortingDirection = value;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  set search(String value) {
    _search = value;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  List<MapEntry<EnergyDrink, Image>> sort(List<Shop> shops) {
    List<MapEntry<EnergyDrink, Image>> drinks = [];
    for (int i = 0; i < shops.length; i++) {
      if (!_currentShops.contains(shops[i].name)) continue;
      for (int j = 0; j < shops[i].energyDrinks.length; j++) {
        if (_currentBrands.contains(shops[i].energyDrinks[j].brand) &&
            shops[i]
                .energyDrinks[j]
                .fullName
                .toLowerCase()
                .contains(search.toLowerCase())) {
          drinks.add(MapEntry(shops[i].energyDrinks[j], shops[i].image));
        }
      }
    }
    if (_sortingParameter == SortingParameter.price) {
      if (_sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink1, drink2) => drink2.key.priceWithDiscount
              .compareTo(drink1.key.priceWithDiscount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) => drink2.key.priceWithDiscount
              .compareTo(drink1.key.priceWithDiscount),
        );
      }
    } else {
      if (_sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink1, drink2) =>
              drink2.key.discount.compareTo(drink1.key.discount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) =>
              drink2.key.discount.compareTo(drink1.key.discount),
        );
      }
    }
    return drinks;
  }

  void reset() {
    _currentBrands = _brands;
    _currentShops = _shops;
    _sortingParameter = SortingParameter.discount;
    _sortingDirection = SortingDirection.descending;
    notifyListeners();
  }
}
