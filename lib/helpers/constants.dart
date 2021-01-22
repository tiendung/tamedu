import 'package:flutter/material.dart';

class Constants extends InheritedWidget {
// Works with hot-reload. Easily testable and mockable
// https://stackoverflow.com/questions/54069239/whats-the-best-practice-to-keep-all-the-constants-in-flutter
  static Constants of(BuildContext context) =>
      context.dependOnInheritedWidgetOfExactType<Constants>();

  const Constants({Widget child, Key key}) : super(key: key, child: child);

  final String homepageTitle = 'Vượt qua dễ duôi';

  @override
  bool updateShouldNotify(Constants oldWidget) => false;
}
