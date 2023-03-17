import 'package:energy_drink/presentation/main_screen/main_screen.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:energy_drink/presentation/settings_screen/settings_screen.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:flutter/material.dart';
// ignore: depend_on_referenced_packages
import 'package:collection/collection.dart';

import 'main.dart' as test_app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('script_5', (tester) async {
    test_app.main();

    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);

    final settingsButton = find.byKey(const Key('settings_button'));
    await tester.tap(settingsButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));
    expect(find.byType(SettingsScreen), findsOneWidget);

    final sortingParameterPrice =
        find.byKey(const Key('sorting_parameter_price'));
    await tester.tap(sortingParameterPrice);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    final sortingDirectionButton =
        find.byKey(const Key('sorting_direction_button'));
    await tester.tap(sortingDirectionButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    final ascendingButton = find.byKey(const Key('ascending_button')).last;
    await tester.tap(ascendingButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    final saveButton = find.byKey(const Key('save_button'));
    await tester.tap(saveButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);
    final energyDrinks = tester.widgetList<Item>(find.byType(Item));
    final energy = find.byType(Item).last;
    await tester.ensureVisible(energy);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));
    expect(
      energyDrinks.isSorted(
        (a, b) => a.energyDrink.key.priceWithDiscount
            .compareTo(b.energyDrink.key.priceWithDiscount),
      ),
      equals(true),
    );
  });
}
