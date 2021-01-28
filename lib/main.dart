import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

import 'permission_widget.dart';
import 'helpers/constants.dart';

// import 'package:hive/hive.dart';
// import 'package:hive_flutter/hive_flutter.dart';
// import 'helpers/util.dart';
// import 'models/job.dart';
// import 'models/habit.dart';
// import 'models/mine.dart';

void main() async {
  // await Hive.initFlutter();
  // await Hive.init('db');

  // Hive.registerAdapter(JobAdapter());
  // Hive.registerAdapter(HabitAdapter());

  // Habit.x = await Hive.openBox<Habit>(Habit.BOX_NAME);
  // Job.x = await Hive.openBox<Job>(Job.BOX_NAME);
  // Mine.x = await Hive.openBox(Mine.BOX_NAME);
  runApp(Constants(
    child: MyApp(),
  ));
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SuTamPhap.com in Practice',
      theme: ThemeData(
        // This is the theme of your application.
        primarySwatch: Colors.blueGrey,
        // This makes the visual density adapt to the platform that you run
        // the app on. For desktop platforms, the controls will be smaller and
        // closer togethesr (more dense) than on mobile platforms.
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      // home: MyHomePage(title: 'Vượt qua dễ duôi'),
      home: MyHomePage(title: Constants.of(context).homepageTitle),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
                Permission.storage,
                Permission.ignoreBatteryOptimizations,
                Permission.mediaLibrary
              ].map((permission) => PermissionWidget(permission)).toList()
        ),
      ),
      drawer: Drawer(
        // Add a ListView to the drawer. This ensures the user can scroll
        // through the options in the drawer if there isn't enough vertical
        // space to fit everything.
        child: ListView(
          // Important: Remove any padding from the ListView.
          padding: EdgeInsets.zero,
          children: <Widget>[
            DrawerHeader(
              child: Text('Hướng dẫn thực hành'),
              decoration: BoxDecoration(
                color: Colors.black12,
              ),
            ),
            ListTile(
              title: Text('Thư giãn (30 phút)'),
              onTap: () {
                // Update the state of the app
                // ...
                // Then close the drawer
                Navigator.pop(context);
              },
            ),
            ListTile(
              title: Text('Thiền tâm từ (15 phút)'),
              onTap: () {
                // Update the state of the app
                // ...
                // Then close the drawer
                Navigator.pop(context);
              },
            ),
            ListTile(
              title: Text('Niệm chết (20 phút)'),
              onTap: () {
                // Update the state of the app
                // ...
                // Then close the drawer
                Navigator.pop(context);
              },
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ),
    );
  }
}
