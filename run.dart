import 'package:hive/hive.dart';

import 'lib/helpers/config.dart';
import 'lib/models/job.dart';
import 'lib/models/habit.dart';

void main() async {
  await Hive.init('db');

  Hive.registerAdapter(JobAdapter());
  Hive.registerAdapter(HabitAdapter());

  var habs = await Hive.openBox<Habit>(HABS_BOX);
  var jobs = await Hive.openBox<Job>(JOBS_BOX);
  var mine = await Hive.openBox(MINE_BOX);

  print('hello');

  var j = Job('Run', 5, 'km');
  print("${j} => key: ${j.key}");

  jobs.put(111, j); // Store this object for the first time
  print('Number of jobs: ${jobs.length}');
  print(j);

  var nj = Job('clean house', 1, "");
  jobs.add(nj);
  print('Number of jobs: ${jobs.length}');
  print(nj);

  DateTime now = new DateTime.now();
  DateTime fromTime = new DateTime(now.year, now.month, now.day);
  DateTime toTime = fromTime.add(new Duration(days: 1));
  print('$fromTime => $toTime');
  print(fromTime.millisecondsSinceEpoch);
  print(fromTime.millisecondsSinceEpoch is int);
}
