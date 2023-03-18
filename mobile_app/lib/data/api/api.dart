import 'dart:convert';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:energy_drink/data/services/shared_pref_service.dart';
import 'package:energy_drink/domain/models/shop_model/shop_model.dart';

import 'mock_dio_adapter.dart';

class ApiConfig {
  static var baseUrl = "http://51.250.0.53:8080";
  static var brands = "/brands";
  static var shops = "/shops";
}

class Api {
  var test = false;

  static final Api _singleton = Api._();

  Api._();

  factory Api([bool? test]) {
    return _singleton;
  }

  get _baseOptions => BaseOptions(
        baseUrl: ApiConfig.baseUrl,
        connectTimeout: 5000,
        receiveTimeout: 10000,
        sendTimeout: 5000,
      );

  Dio get client {
    var dio = Dio(_baseOptions);

    if (test) {
      dio.httpClientAdapter = MockDioAdapter();
    }
    return dio;
  }

  Future<List<Shop>> getShops() async {
    final time = SharedPrefService.get('timeShops');
    dynamic json = [];
    if (time != null &&
        DateTime.now().difference(DateTime.parse(time)).inDays < 1) {
      final res = SharedPrefService.get('shops');
      if (res != null) {
        json = jsonDecode(res);
      } else {
        try {
          final response = await client.get(ApiConfig.shops);
          SharedPrefService.save('shops', response.data);
          SharedPrefService.save('timeShops', DateTime.now().toString());
          json = jsonDecode(response.data);
        } catch (e) {}
      }
    } else {
      try {
        final response = await client.get(ApiConfig.shops);
        SharedPrefService.save('shops', response.data);
        SharedPrefService.save('timeShops', DateTime.now().toString());
        json = jsonDecode(response.data);
      } catch (e) {
        final res = SharedPrefService.get('shops');
        if (res != null) {
          json = jsonDecode(res);
        }
      }
    }
    if (json.isEmpty) {
      return [];
    }
    final shops = (json['shops'] as List<dynamic>)
        .map((e) => Shop.fromJson(e as Map<String, dynamic>))
        .toList();
    return shops;
  }

  Future<List<String>> getBrands() async {
    final time = SharedPrefService.get('timeBrands');
    dynamic json = [];
    if (time != null &&
        DateTime.now().difference(DateTime.parse(time)).inDays < 1) {
      final res = SharedPrefService.get('brands');
      if (res != null) {
        json = jsonDecode(res);
      } else {
        try {
          final response = await client.get(ApiConfig.brands);
          SharedPrefService.save('brands', response.data);
          SharedPrefService.save('timeBrands', DateTime.now().toString());
          json = jsonDecode(response.data);
        } catch (e) {}
      }
    } else {
      try {
        final response = await client.get(ApiConfig.brands);
        SharedPrefService.save('brands', response.data);
        SharedPrefService.save('timeBrands', DateTime.now().toString());
        json = jsonDecode(response.data);
      } catch (e) {
        final res = SharedPrefService.get('brands');
        if (res != null) {
          json = jsonDecode(res);
        }
      }
    }
    if (json.isEmpty) {
      return [];
    }
    final brands =
        (json['brands'] as List<dynamic>).map((e) => e as String).toList();
    return brands;
  }
}
