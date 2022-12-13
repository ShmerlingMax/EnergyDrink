import 'dart:io';
import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:energy_drink/domain/models/shop_model/shop_model.dart';
import 'package:flutter/services.dart' show rootBundle;

class ApiConfig {
  static const baseUrl = "";
  static const brands = "/brands";
  static const shops = "/shops";
}

class Api {
  static final Api _singleton = Api._();

  Api._();

  factory Api() {
    return _singleton;
  }

  final _baseOptions = BaseOptions(
    baseUrl: ApiConfig.baseUrl,
    connectTimeout: 5000,
    receiveTimeout: 10000,
    sendTimeout: 5000,
  );

  Dio get client {
    final dio = Dio(_baseOptions);
    return dio;
  }

  Future<List<Shop>> getShops() async {
    //TODO заменить
    final json = jsonDecode(await rootBundle.loadString('assets/shops.json'));
    final shops = (json['shops'] as List<dynamic>)
        .map((e) => Shop.fromJson(e as Map<String, dynamic>))
        .toList();
    return shops;
  }

  Future<List<String>> getBrands() async {
    //TODO заменить
    final json = jsonDecode(await rootBundle.loadString('assets/brands.json'));
    final brands = (json['brands'] as List<dynamic>).map((e) => e as String).toList();

    return brands;
  }
}
