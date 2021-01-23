import 'package:hive/hive.dart';

import 'lib/models/mine.dart';
import 'lib/models/job.dart';
import 'lib/models/habit.dart';

void main() async {
  await Hive.init('db');

  Hive.registerAdapter(JobAdapter());
  Hive.registerAdapter(HabitAdapter());

  Habit.x = await Hive.openBox<Habit>(Habit.BOX_NAME);
  Job.x = await Hive.openBox<Job>(Job.BOX_NAME);
  Mine.x = await Hive.openBox(Mine.BOX_NAME);

  print('Boxed loaded.');

  var j = Job('Run', 5, 'km', new DateTime.now());
  print(j);

  Job.x.add(j);
  print('Number of jobs: ${Job.x.length}');
  print(j);

  var nj = Job('clean house', 1, "", new DateTime.now());
  Job.x.add(nj);
  print('Number of jobs: ${Job.x.length}');
  print(nj);

  print('Today jobs: ${Job.getAllForDate(new DateTime.now())}');
  print('All jobs: ${Job.x.values}');
}
