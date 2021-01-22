import 'package:hive/hive.dart';

import 'lib/helpers/config.dart';
import 'lib/models/job.dart';
import 'lib/models/habit.dart';

void main() async {
  await Hive.init('db');

  Hive.registerAdapter(JobAdapter());
  Hive.registerAdapter(HabitAdapter());

  Job.x = await Hive.openBox<Job>(Job.BOX_NAME);
  var mine = await Hive.openBox(MINE_BOX);

  print('hello');

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
