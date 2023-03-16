import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/presentation/settings_screen/settings_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class MainAppBar extends StatelessWidget with PreferredSizeWidget {
  const MainAppBar({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var controller = TextEditingController();
    return SafeArea(
      child: Row(
        children: [
          Flexible(
            flex: 10,
            child: Container(
              height: 60,
              decoration: const BoxDecoration(
                border: Border.symmetric(
                  horizontal: BorderSide(
                    color: Colors.black,
                    width: 1.0,
                  ),
                ),
              ),
              child: Row(
                children: [
                  const SizedBox(width: 9),
                  Image.asset(
                    'assets/search.png',
                    height: 40,
                    width: 40,
                  ),
                  const SizedBox(width: 20),
                  Expanded(
                    child: TextFormField(
                      controller: controller,
                      decoration: const InputDecoration(
                          hintText: 'Поиск',
                          border: InputBorder.none,
                          hintStyle: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w500,
                          )),
                      onEditingComplete: () {
                        context.read<Aggregation>().search = controller.text;
                        FocusManager.instance.primaryFocus?.unfocus();
                      },
                    ),
                  ),
                  const SizedBox(width: 10),
                ],
              ),
            ),
          ),
          Flexible(
            flex: 2,
            child: GestureDetector(
              key: const Key('settings_button'),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const SettingsScreen(),
                  ),
                );
              },
              child: Container(
                height: 60,
                decoration: const BoxDecoration(
                  border: Border(
                    left: BorderSide(color: Colors.black, width: 1),
                    top: BorderSide(color: Colors.black, width: 1),
                    bottom: BorderSide(color: Colors.black, width: 1),
                  ),
                ),
                child: Center(
                  child: Image.asset(
                    'assets/settings.png',
                    height: 40,
                    width: 40,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
