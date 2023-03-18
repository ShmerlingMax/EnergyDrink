import 'package:energy_drink/domain/models/selection_model/selection_model.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../../mock_data.dart';

void main() {
  group('Test SelectionModel', () {
    late SelectionModel selection;

    setUpAll(() {
      TestWidgetsFlutterBinding.ensureInitialized();
      selection = SelectionModel();
    });

    test('init', () {
      selection.init(mockShopsNames, mockBrandsNames);
      expect(selection.shops, equals(mockShopsNames));
      expect(selection.brands, equals(mockBrandsNames));
    });

    group('getters', () {
      setUp(() {
        selection.init(mockShopsNames, mockBrandsNames);
      });

      test('shops', () {
        expect(selection.shops, equals(mockShopsNames));
      });

      test('brands', () {
        expect(selection.brands, equals(mockBrandsNames));
      });
    });

    group('setters', () {
      setUp(() {
        selection.init(mockShopsNames, mockBrandsNames);
      });

      test('shops', () {
        const newValue = <String>[];
        selection.shops = newValue;
        expect(selection.shops, equals(newValue));
      });

      test('brands', () {
        const newValue = <String>[];
        selection.brands = newValue;
        expect(selection.brands, equals(newValue));
      });
    });
  });
}
