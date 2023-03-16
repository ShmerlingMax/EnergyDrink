import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:flutter/widgets.dart';
import 'package:provider/provider.dart';

class SaveButton extends StatelessWidget {
  const SaveButton({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.bottomRight,
      child: GestureDetector(
        key: const Key('save_button'),
        onTap: () {
          final settings = context.read<Settings>();
          context.read<Aggregation>()
            ..shops = settings.shops.toList()
            ..brands = settings.brands.toList()
            ..sortingParameter = settings.sortingParameter
            ..sortingDirection = settings.sortingDirection;
          Navigator.pop(context);
        },
        child: Container(
          width: 160,
          height: 120,
          color: const Color(0xFFB2C2D7),
          child: const Center(
            child: Text(
              'Сохранить',
              style: TextStyle(
                fontSize: 25,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
