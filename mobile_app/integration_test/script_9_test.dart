import 'package:energy_drink/presentation/main_screen/main_screen.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:energy_drink/presentation/selection_screen/selection_screen.dart';
import 'package:energy_drink/presentation/selection_screen/widgets/item.dart';
import 'package:energy_drink/presentation/settings_screen/settings_screen.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:flutter/material.dart';
// ignore: depend_on_referenced_packages
import 'package:collection/collection.dart';

import 'main.dart' as test_app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('script_9', (tester) async {
    test_app.main();

    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));

    expect(find.byType(MainScreen), findsOneWidget);

    final settingsButton = find.byKey(const Key('settings_button'));
    await tester.tap(settingsButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));
    expect(find.byType(SettingsScreen), findsOneWidget);

    final brandsAddButton = find.byKey(const Key('brands_add_button'));
    await tester.ensureVisible(brandsAddButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));
    await tester.tap(brandsAddButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));
    expect(find.byType(SelectionScreen), findsOneWidget);

    final checkFirstBrand = find.byType(SelectionItem).first;
    final brandName = tester.widget<SelectionItem>(checkFirstBrand).text;
    await tester.tap(checkFirstBrand);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    final applyButton = find.byKey(const Key('apply_button'));
    await tester.tap(applyButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(SettingsScreen), findsOneWidget);
    final saveButton = find.byKey(const Key('save_button'));
    await tester.tap(saveButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);
    final energyDrinks = tester.widgetList<Item>(find.byType(Item));
    expect(
      energyDrinks.any((element) => element.energyDrink.key.brand == brandName),
      equals(false),
    );
    expect(
      energyDrinks.isSorted(
        (a, b) =>
            b.energyDrink.key.discount.compareTo(a.energyDrink.key.discount),
      ),
      equals(true),
    );
  });
}
