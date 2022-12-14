import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:energy_drink/presentation/main_screen/main_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_) => Aggregation(),
        ),
        ChangeNotifierProvider(
          create: (_) => Settings(),
        ),
      ],
      child: MaterialApp(
        title: 'Энергосы',
        theme: ThemeData(
          scaffoldBackgroundColor: Colors.white,
          backgroundColor: Colors.white,
          appBarTheme: const AppBarTheme(
            color: Colors.white,
            elevation: 0,
          ),
        ),
        home: const MainScreen(),
      ),
    );
  }
}
