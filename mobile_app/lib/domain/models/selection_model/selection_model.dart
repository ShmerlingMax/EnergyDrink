import 'package:flutter/material.dart';

class SelectionModel with ChangeNotifier {
  List<String> _shops = [];
  List<String> _brands = [];

  List<String> get shops => _shops;
  List<String> get brands => _brands;

  set shops(List<String> value) {
    _shops = value;
    notifyListeners();
  }

  set brands(List<String> value) {
    _brands = value;
    notifyListeners();
  }

  void init(List<String> shops, List<String> brands) {
    _shops = shops;
    _brands = brands;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }
}
