import 'package:energy_drink/presentation/main_screen/main_screen.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:energy_drink/presentation/settings_screen/settings_screen.dart';
import 'package:energy_drink/presentation/settings_screen/widgets/shops_brands_selector.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:flutter/material.dart';
// ignore: depend_on_referenced_packages
import 'package:collection/collection.dart';

import 'main.dart' as test_app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('script_7', (tester) async {
    test_app.main();

    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));

    expect(find.byType(MainScreen), findsOneWidget);

    final settingsButton = find.byKey(const Key('settings_button'));
    await tester.pump(const Duration(seconds: 5));
    await tester.pumpAndSettle();
    await tester.tap(settingsButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 5));
    expect(find.byType(SettingsScreen), findsOneWidget);

    final shopsCount =
        find.byKey(const Key('brand_delete_button')).evaluate().length;
    for (var i = 0; i < shopsCount - 1; i++) {
      final widget =
          find.byKey(const Key('brand_delete_button')).evaluate().first.widget;
      await tester.tap(find.byWidget(widget));
      await tester.pumpAndSettle();
      await tester.pump(const Duration(seconds: 1));
    }
    final brandName = tester
        .widgetList<ShopsOrBrandsItem>(find.byType(ShopsOrBrandsItem))
        .where((element) => !element.isShops)
        .first
        .text;
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    final saveButton = find.byKey(const Key('save_button'));
    await tester.tap(saveButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);
    final energyDrinks = tester.widgetList<Item>(find.byType(Item));
    expect(
      energyDrinks.any((element) => element.energyDrink.key.brand != brandName),
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
