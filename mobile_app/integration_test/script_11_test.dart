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

  testWidgets('script_11', (tester) async {
    test_app.main();

    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));

    expect(find.byType(MainScreen), findsOneWidget);

    final settingsButton = find.byKey(const Key('settings_button'));
    await tester.tap(settingsButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));
    expect(find.byType(SettingsScreen), findsOneWidget);

    final shopsAddButton = find.byKey(const Key('shops_add_button'));
    await tester.tap(shopsAddButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));
    expect(find.byType(SelectionScreen), findsOneWidget);

    final selectAllButton = find.byKey(const Key('select_all_button'));
    await tester.tap(selectAllButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));

    final applyButton = find.byKey(const Key('apply_button'));
    await tester.tap(applyButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));
    expect(find.byType(SelectionScreen), findsOneWidget);

    final checkFirstShop = find.byType(SelectionItem).first;
    final shopName = tester.widget<SelectionItem>(checkFirstShop).text;
    await tester.tap(checkFirstShop);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

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
        final shops =
        await (tester.state(find.byType(MainScreen)) as MainScreenState).shops;
    final expectImage =
        shops.where((element) => element.name == shopName).first.image;
    expect(
      energyDrinks.any((element) => element.energyDrink.value != expectImage),
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
