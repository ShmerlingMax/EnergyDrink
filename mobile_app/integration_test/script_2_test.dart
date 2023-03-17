import 'package:energy_drink/presentation/main_screen/main_screen.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:flutter/material.dart';

import 'main.dart' as test_app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('script_2', (tester) async {
    test_app.main();

    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);

    final searchField = find.byType(TextFormField);
    final fullName = tester
        .widgetList<Item>(find.byType(Item))
        .first
        .energyDrink
        .key
        .fullName;
    await tester.enterText(searchField, fullName);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    await tester.testTextInput.receiveAction(TextInputAction.done);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    final energyDrinks = tester.widgetList<Item>(find.byType(Item));
    expect(find.byType(Item), findsOneWidget);
    expect(energyDrinks.first.energyDrink.key.fullName, equals(fullName));
  });
}
