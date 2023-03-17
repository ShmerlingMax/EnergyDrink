import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:energy_drink/presentation/main_screen/main_screen.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:energy_drink/presentation/settings_screen/settings_screen.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:flutter/material.dart';
// ignore: depend_on_referenced_packages
import 'package:collection/collection.dart';
import 'package:provider/provider.dart';

import 'main.dart' as test_app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('script_10', (tester) async {
    test_app.main();

    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);

    final settingsButton = find.byKey(const Key('settings_button'));
    await tester.tap(settingsButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));
    expect(find.byType(SettingsScreen), findsOneWidget);

    final cancelButton = find.byKey(const Key('cancel_button'));
    await tester.tap(cancelButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    final BuildContext context = tester.element(find.byType(SettingsScreen));
    final settings = context.read<Settings>();
    expect(settings.sortingParameter, equals(SortingParameter.discount));
    expect(settings.sortingDirection, equals(SortingDirection.descending));

    final saveButton = find.byKey(const Key('save_button'));
    await tester.tap(saveButton);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);
    final energyDrinks = tester.widgetList<Item>(find.byType(Item));
    await tester.pump(const Duration(seconds: 3));
    expect(
      energyDrinks.isSorted(
        (a, b) =>
            b.energyDrink.key.discount.compareTo(a.energyDrink.key.discount),
      ),
      equals(true),
    );
  });
}
