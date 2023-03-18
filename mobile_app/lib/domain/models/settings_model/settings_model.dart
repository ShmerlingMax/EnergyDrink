import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:flutter/material.dart';
import 'package:equatable/equatable.dart';

class Settings extends Equatable with ChangeNotifier {
  List<String> _shops = [];
  List<String> _brands = [];
  SortingParameter _sortingParameter = SortingParameter.discount;
  SortingDirection _sortingDirection = SortingDirection.descending;

  List<String> get shops => _shops;
  List<String> get brands => _brands;
  SortingParameter get sortingParameter => _sortingParameter;
  SortingDirection get sortingDirection => _sortingDirection;

  set shops(List<String> value) {
    _shops = value;
    notifyListeners();
  }

  set brands(List<String> value) {
    _brands = value;
    notifyListeners();
  }

  set sortingParameter(SortingParameter value) {
    _sortingParameter = value;
    notifyListeners();
  }

  set sortingDirection(SortingDirection value) {
    _sortingDirection = value;
    notifyListeners();
  }

  void init(List<String> shops, List<String> brands) {
    _shops = shops;
    _brands = brands;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  void reset(
    List<String> shops,
    List<String> brands,
    SortingParameter sortingParameter,
    SortingDirection sortingDirection,
  ) {
    _shops = shops;
    _brands = brands;
    _sortingParameter = sortingParameter;
    _sortingDirection = sortingDirection;
    notifyListeners();
  }

  @override
  List<Object> get props => [
        _shops,
        _brands,
        _sortingParameter,
        _sortingDirection,
      ];
}
