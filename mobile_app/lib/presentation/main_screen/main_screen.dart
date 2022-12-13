import 'package:energy_drink/data/api/api.dart';
import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/shop_model/shop_model.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:flutter/material.dart';

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
            builder:
                (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.hasData) {
                final List<String> shopsStr = [];
                for (int i = 0; i < snapshot.data[0].length; i++) {
                  shopsStr.add(snapshot.data[0][i].name);
                }
                final aggragation = Aggregation(
                  shopsStr,
                  snapshot.data[1],
                  SortingParameter.price,
                  SortingDirection.ascending,
                );
                final drinks = aggragation.sort(snapshot.data[0]);
                return GridView.builder(
                  shrinkWrap: true,
                  itemCount: drinks.length,
                  gridDelegate:
                      const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
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
