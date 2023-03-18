import 'package:mocktail/mocktail.dart';
import 'package:dio/dio.dart';

class MockDioAdapter extends Mock implements HttpClientAdapter {
  static final MockDioAdapter _dioAdapterMock = MockDioAdapter._internal();
  MockDioAdapter._internal();

  factory MockDioAdapter() {
    return _dioAdapterMock;
  }
}
