import 'package:energy_drink/data/api/api.dart';
import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'widgets/app_bar.dart';

class MainScreen extends StatelessWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final shops = Api().getShops();
    final brands = Api().getBrands();
    return GestureDetector(
      onTap: () => FocusManager.instance.primaryFocus?.unfocus(),
      child: Scaffold(
        appBar: const MainAppBar(),
        body: SafeArea(
          child: FutureBuilder(
            future: Future.wait([shops, brands]),
            builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.hasData) {
                final List<String> shopsStr = [];
                for (int i = 0; i < snapshot.data[0].length; i++) {
                  shopsStr.add(snapshot.data[0][i].name);
                }
                context.read<Aggregation>().shops = shopsStr;
                context.read<Aggregation>().brands = snapshot.data[1];
                final drinks =
                    context.watch<Aggregation>().sort(snapshot.data[0]);
                final itemWidth = MediaQuery.of(context).size.width / 3;
                final itemHeight = MediaQuery.of(context).size.height / 4.5;
                if (drinks.isEmpty) {
                  return const Expanded(
                    child: Center(
                      child: Text('По вашему запросу ничего не найдено'),
                    ),
                  );
                }
                return GridView.builder(
                  shrinkWrap: true,
                  itemCount: drinks.length,
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    childAspectRatio: itemWidth / itemHeight,
                  ),
                  itemBuilder: (context, index) => Item(
                    drinks[index],
                    index,
                  ),
                );
              } else {
                return const Expanded(
                  child: Center(
                    child: CircularProgressIndicator(),
                  ),
                );
              }
            },
          ),
        ),
      ),
    );
  }
}
