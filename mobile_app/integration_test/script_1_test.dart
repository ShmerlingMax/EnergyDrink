import 'package:energy_drink/presentation/main_screen/main_screen.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
// ignore: depend_on_referenced_packages
import 'package:collection/collection.dart';

import 'main.dart' as test_app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('script_1', (tester) async {
    test_app.main();

    await tester.pumpAndSettle();
    await tester.pump(const Duration(seconds: 3));

    expect(find.byType(MainScreen), findsOneWidget);

    final energyDrinks = tester.widgetList<Item>(find.byType(Item));
    expect(
      energyDrinks.isSorted(
        (a, b) =>
            b.energyDrink.key.discount.compareTo(a.energyDrink.key.discount),
      ),
      equals(true),
    );
  });
}
