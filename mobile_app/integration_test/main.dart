import 'dart:async';

import 'package:energy_drink/data/services/shared_pref_service.dart';
import 'package:energy_drink/main.dart';
import 'package:flutter/material.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runZonedGuarded<Future<void>>(
    () async {
      await SharedPrefService.init();
      await SharedPrefService.sharedPref.clear();
      runApp(const MyApp());
    },
    (error, stack) => {},
  );
}
