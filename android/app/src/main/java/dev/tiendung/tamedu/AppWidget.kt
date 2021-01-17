package dev.tiendung.tamedu
//import android.app.AlarmManager

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Implementation of App Widget functionality.
 */
class AppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        _isInitOrAutoUpdate = false

        _speakQuoteToggleClicked = intent.action == "speakQuoteToggle"
        _newQuoteClicked = intent.action == "newQuote"
        _isInitOrAutoUpdate = !(_newQuoteClicked || _speakQuoteToggleClicked)

        if (_speakQuoteToggleClicked) {
            _allowToSpeakQuote = !_allowToSpeakQuote
        }
        
        var updateView = true
        if (intent.action == "saveQuoteImage") {
            updateView = false
            copyQuoteFromAssets(context, _currentQuoteId)
        }

        if (intent.action == "nghePhap") {
            updateView = false
            if (_phapIsPlaying) {
                _phapPlayer.stop()
                Toast.makeText(context, "Đã dừng nghe pháp",
                        Toast.LENGTH_LONG).show()
                _phapIsPlaying = false
            } else {
                playRandomPhap(context)
            }
        }
        
        if (updateView) {
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

private fun getPendingIntentWidget(context: Context, action: String): PendingIntent
{
    // Construct an Intent which is pointing this class.
    val intent = Intent(context, AppWidget::class.java)
    intent.action = action
    // And this time we are sending a broadcast with getBroadcast
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

// Init a mediaPlayer to play quote audio
var _mediaPlayer: MediaPlayer = MediaPlayer()
var  _newQuoteClicked: Boolean = false
var _speakQuoteToggleClicked = false
var _allowToSpeakQuote: Boolean = false
var _isInitOrAutoUpdate: Boolean = true
var _currentQuoteId: Int = 0

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)
   
    // Stop previous audio if playing
    _mediaPlayer.stop()

    // Handle events
    views.setOnClickPendingIntent(R.id.nghe_phap_button,
            getPendingIntentWidget(context, "nghePhap"))

    views.setOnClickPendingIntent(R.id.speak_quote_toggle_button,
            getPendingIntentWidget(context, "speakQuoteToggle"))

    views.setOnClickPendingIntent(R.id.save_quote_button,
            getPendingIntentWidget(context, "saveQuoteImage"))

    views.setOnClickPendingIntent(R.id.appwidget_image,
            getPendingIntentWidget(context, "newQuote"))

    // Show and play random quote
    if (_isInitOrAutoUpdate || _newQuoteClicked) {
        _currentQuoteId = quoteIdsSortedByLen[(0..998).random()] // show only fitable quotes
        showQuoteById(_currentQuoteId, context, views, appWidgetId)
    }

    // Update speak_quote_toggle_button text
    val txt = if (_allowToSpeakQuote) "Dừng đọc" else "Đọc to"
    views.setTextViewText(R.id.speak_quote_toggle_button, txt)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)

    // Play audio after update quote image to views
    if (_isInitOrAutoUpdate)
        playQuoteById(-1, context) // play a bell
    else if ((_newQuoteClicked || _speakQuoteToggleClicked) && _allowToSpeakQuote)
        playQuoteById(_currentQuoteId, context) // play quote
    
    _isInitOrAutoUpdate = true
}

// Storage Permissions
// https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
// https://developer.android.com/training/permissions/requesting.html
private const val REQUEST_EXTERNAL_STORAGE = 1
private val PERMISSIONS_STORAGE = arrayOf<String>(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
)
fun copyQuoteFromAssets(context: Context, quoteId: Int) {
    // Create a path where we will place our private file on external
    // storage.
    
    // val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/quote$quoteId.png")
    File(context.getExternalFilesDir(null), "/quotes").mkdir()
    val file = File(context.getExternalFilesDir(null), "/quotes/quote$quoteId.png")
    Toast.makeText(context, "Lưu lời dạy tại $file",
            Toast.LENGTH_LONG).show()
    try {
        // Very simple code to copy a picture from the application's
        // resource into the external file.  Note that this code does
        // no error checking, and assumes the picture is small (does not
        // try to copy it in chunks).  Note that if external storage is
        // not currently mounted this will silently fail.
        val assetManager = context.getAssets()
        val ins = assetManager.open("quotes/$quoteId.png")
        val os = FileOutputStream(file)
        val data = ByteArray(ins.available())
        ins.read(data)
        os.write(data)
        ins.close()
        os.close()
    } catch (e: IOException) {
        // Unable to create file, likely because external storage is
        // not currently mounted.
        Log.w("ExternalStorage", "Error writing $file", e)
    }
}


fun showQuoteById(quoteId: Int, context: Context, views: RemoteViews, appWidgetId: Int) {
    val appWidgetTarget = AppWidgetTarget(context, R.id.appwidget_image, views, appWidgetId)
    Glide.with(context)
            .asBitmap()
            .load(Uri.parse("file:///android_asset/quotes/$quoteId.png"))
            .override(1200)
            .into(appWidgetTarget)
}

fun playQuoteById(quoteId: Int, context: Context) {
    // https://stackoverflow.com/questions/5747060/how-do-you-play-android-inputstream-on-mediaplayer
    val fileName = if (quoteId == -1) "bell.ogg" else "quotes/$quoteId.ogg"
    val assetManager = context.getAssets()
    val fd = assetManager.openFd(fileName) // file descriptor

    _mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength())
        prepare()
        start()
    }
}

var _phapPlayer : MediaPlayer = MediaPlayer()
var _phapIsPlaying : Boolean = false

fun playRandomPhap(context: Context) {
    _phapIsPlaying = true
    val randomIndex = (0..phapIds2Titles.size).random()
    val phapId2Title = phapIds2Titles.entries.elementAt(randomIndex)

    val audioUrl =  "https://tiendung.github.io/${phapId2Title.key}"
    Toast.makeText(context, "Chuẩn bị nghe '${phapId2Title.value}'.\n Ấn 'NGHE PHÁP' lần nữa để dừng",
            Toast.LENGTH_LONG).show()

    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(context, Uri.parse(audioUrl))
        setOnPreparedListener(OnPreparedListener { mp -> mp.start() })
        prepareAsync()
    }
}
val phapIds2Titles: HashMap<String, String> = hashMapOf(
"phaps/Tu-Tap-Khong-Phai-Chi-La-Thien.ogg" to "Tu tập ko phải chỉ là thiền",
"phaps/Vo-Mong.ogg" to "Vỡ mộng",
"phaps/Tinh-Tan-fix.ogg" to "Tinh tấn",
"phaps/Tran-Trong.ogg" to "Trân trọng",
"phaps/Duyen.ogg" to "Nhân, Duyên và kết quả",
"phaps/suy-nghi-tich-cuc.ogg" to "Suy nghĩ tích cực",
"phaps/cai-nay-chi-ton-tai-trong-suy-nghi.ogg" to "Cái này chỉ tồn tại trong suy nghĩ",
"phaps/thuyetphap-ODoiMoiLaMatTran.ogg" to "Ở đời mới là mặt trận",
"phaps/thuyetphap-chilacamgiac.ogg" to "Chỉ là một cảm giác",
"phaps/thuyetPhap_goiYDinhHuong.ogg" to "Gợi ý định hướng",
"phaps/thuyetphap_giaTriGiaTang.ogg" to "Giá trị gia tăng",
"phaps/thuyetphap_songTichCuc.ogg" to "Sống tích cực",
"phaps/thuyetPhap_cuocSongLaDeSuDung.ogg" to "Cuộc sống là để sử dụng",
"phaps/cacchudetutaptrongcuocsong_dinhHinhTuongLai.ogg" to "Định hình tương lai",
"phaps/tutaptrongCS_BuonChan.ogg" to "Buồn chán",
"phaps/tutaptrongCS_luaChon.ogg" to "Lựa chọn",
"phaps/tutaptrongCS_tamNhinCuocDoi.ogg" to "Tầm nhìn cuộc đời",
"phaps/tutaptrongCS_camXuc.ogg" to "Cảm xúc",
"phaps/tutaptrongCS_ViecQuanTrong.ogg" to "Việc quan trọng",
"phaps/tutaptrongCS_PhatTrienTinhGiacVaTue.ogg" to "Phát triển tỉnh giác và tuệ",
"phaps/tutaptrongCS_TimHungThuThucHanh.ogg" to "Tìm hứng thú thực hành",
"phaps/tutaptrongCS_DauTuChoTuongLai.ogg" to "Đầu tư cho tương lai",
"phaps/tutaptrongCS_XungDangCuocSongLamNguoi.ogg" to "Xứng đáng cuộc sống làm người",
"phaps/tutaptrongCS_NguoiChanhNiemLamGi.ogg" to "Người chánh niệm làm gì",
"phaps/tutaptrongCS_NoiChuyenVeHanhPhuc.ogg" to "Nói chuyện về hạnh phúc",
"phaps/tutaptrongCS_SuDungChanhNiem.ogg" to "Sử dụng chánh niệm",
"phaps/tutaptrongCS_NguoiThuongLuu.ogg" to "Người thượng lưu",
"phaps/tutaptrongCS_DayCon.ogg" to "Dạy con",
"phaps/tutaptrongCS_ChetKhongHoiTiec.ogg" to "Chết không hối tiếc",
"phaps/tutaptrongCS_DeDuoi.ogg" to "Dễ duôi",
"phaps/tutaptrongCS_LaoDongGiupChanhNiemRaSao.ogg" to "Lao động giúp chánh niệm ra sao",
"phaps/tutaptrongCS_TamDonGian.ogg" to "Tâm đơn giản",
"phaps/tutaptrongCS_TietKiemNangLuong.ogg" to "Tiết kiệm năng lượng"
)

val quoteIdsSortedByLen = intArrayOf(612, 19, 394, 868, 257, 701, 656, 870, 588, 1184, 653, 1044, 1627, 267, 269, 1148, 927, 1037, 1202, 74, 568, 668, 851, 1297, 82, 1474, 1558, 123, 549, 950, 1460, 250, 1295, 1340, 145, 587, 591, 936, 427, 1173, 1411, 1658, 1662, 595, 1038, 499, 546, 711, 913, 1255, 1289, 847, 1107, 1249, 1320, 1009, 1342, 298, 203, 294, 354, 659, 717, 696, 1068, 1310, 251, 321, 128, 593, 1417, 31, 61, 960, 1021, 1365, 287, 678, 838, 1063, 1485, 422, 775, 842, 1056, 40, 73, 217, 314, 673, 1099, 1155, 1171, 1064, 1111, 1457, 1648, 739, 1552, 1615, 607, 403, 1264, 1336, 1668, 253, 699, 1447, 917, 1049, 1122, 522, 1054, 1356, 434, 722, 807, 311, 392, 886, 1332, 316, 373, 516, 1600, 928, 945, 985, 1052, 1276, 1639, 1643, 88, 1669, 9, 26, 103, 156, 551, 620, 811, 1026, 1035, 1085, 1129, 1398, 1492, 1550, 69, 214, 344, 400, 586, 647, 947, 1123, 1161, 1298, 1435, 1454, 1486, 163, 355, 366, 412, 579, 640, 796, 1027, 152, 220, 343, 378, 465, 616, 689, 813, 1311, 1530, 29, 42, 102, 694, 758, 1112, 1322, 1346, 1409, 1459, 1497, 1518, 94, 142, 200, 208, 375, 472, 602, 638, 1132, 1278, 1449, 1624, 86, 120, 271, 368, 410, 493, 534, 555, 880, 896, 972, 982, 1095, 1191, 1235, 1348, 1636, 154, 297, 393, 399, 519, 539, 544, 971, 1046, 1065, 1133, 1147, 1176, 1288, 1597, 1599, 1609, 1638, 1649, 169, 369, 374, 545, 635, 654, 860, 890, 959, 1282, 1442, 1605, 1619, 1650, 332, 426, 475, 718, 907, 994, 1116, 1152, 1477, 1543, 524, 686, 752, 760, 930, 931, 1094, 1136, 1258, 1266, 1271, 1334, 1407, 1499, 1536, 50, 78, 126, 234, 709, 996, 1076, 1092, 1143, 1233, 1324, 1330, 1375, 1581, 1585, 105, 140, 265, 326, 357, 585, 631, 762, 942, 998, 1001, 1018, 1022, 1110, 1213, 1431, 1448, 1488, 1490, 1523, 130, 176, 243, 359, 414, 476, 502, 569, 688, 943, 1084, 1410, 1429, 1440, 1446, 75, 138, 165, 191, 279, 288, 566, 954, 1113, 1220, 1362, 1620, 230, 396, 420, 496, 1053, 15, 96, 97, 192, 340, 456, 486, 572, 855, 989, 1039, 1240, 1328, 1515, 1554, 1626, 435, 479, 764, 1109, 1145, 1150, 147, 157, 307, 431, 609, 624, 892, 895, 1077, 1098, 1137, 1262, 1308, 1586, 33, 187, 1010, 1380, 110, 197, 296, 449, 552, 769, 902, 992, 1108, 1179, 1616, 1423, 1621, 1075, 1277, 1285, 1371, 1384, 1590, 168, 398, 1019, 1470, 1617, 1667, 62, 852, 1190, 1316, 21, 70, 116, 322, 462, 560, 1397, 1502, 1549, 293, 782, 825, 1134, 1175, 1666, 25, 184, 302, 338, 385, 407, 746, 1478, 159, 256, 770, 976, 1000, 1033, 1114, 1359, 1430, 1623, 213, 241, 411, 621, 761, 1244, 1489, 41, 43, 85, 164, 351, 454, 661, 719, 894, 916, 956, 1370, 1437, 1544, 46, 77, 258, 337, 881, 940, 1468, 1640, 162, 377, 728, 944, 80, 222, 225, 418, 459, 500, 710, 923, 1400, 1672, 10, 170, 613, 71, 146, 179, 193, 221, 905, 973, 1082, 1141, 1210, 286, 712, 731, 1209, 1260, 1344, 1575, 1664, 12, 89, 471, 702, 1121, 1294, 1419, 1, 160, 180, 280, 364, 384, 387, 415, 1246, 1281, 1548, 118, 245, 548, 874, 1421, 487, 597, 1483, 1513, 1534, 139, 183, 283, 432, 787, 876, 1017, 1149, 1450, 488, 726, 771, 857, 1024, 1040, 1413, 1517, 360, 967, 1070, 186, 589, 681, 1221, 1464, 1659, 63, 744, 1087, 1250, 229, 335, 457, 962, 1050, 1174, 1203, 1252, 362, 658, 663, 734, 812, 1180, 1466, 1604, 1644, 129, 578, 646, 750, 910, 1089, 1211, 1467, 1578, 1630, 655, 871, 908, 997, 1182, 1516, 386, 889, 901, 1016, 1309, 249, 453, 520, 523, 596, 599, 707, 891, 903, 1216, 1296, 1313, 1463, 182, 216, 361, 409, 636, 674, 979, 1496, 1611, 1641, 571, 810, 999, 1042, 1317, 1357, 1376, 1602, 11, 667, 748, 1045, 1128, 1217, 1318, 1451, 1452, 65, 416, 680, 763, 909, 1168, 1170, 1354, 1498, 1565, 58, 215, 223, 491, 679, 755, 815, 898, 1048, 1622, 1670, 144, 231, 1014, 1058, 1438, 446, 503, 517, 724, 1169, 1253, 23, 121, 190, 469, 1096, 1500, 149, 437, 723, 766, 801, 983, 1504, 781, 835, 1006, 1369, 60, 445, 765, 768, 882, 1061, 1139, 1232, 1665, 0, 199, 299, 358, 436, 800, 879, 1555, 380, 526, 1032, 1242, 18, 90, 194, 235, 329, 483, 866, 1181, 1426, 467, 826, 924, 1358, 1465, 92, 425, 525, 535, 664, 691, 853, 22, 112, 155, 484, 975, 1004, 1142, 1520, 124, 174, 282, 328, 336, 611, 244, 268, 442, 869, 906, 1368, 1512, 1566, 1589, 104, 389, 1071, 1350, 1625, 67, 76, 981, 1115, 1347, 1633, 1647, 122, 136, 143, 395, 458, 683, 799, 822, 932, 1144, 1373, 1532, 66, 788, 827, 862, 1028, 1395, 141, 439, 590, 56, 201, 367, 601, 754, 974, 986, 1062, 1126, 1327, 47, 618, 1069, 1337, 1573, 341, 556, 648, 725, 1041, 1274, 1556, 278, 438, 644, 666, 1228, 1453, 83, 255, 363, 1067, 1312, 1265, 1351, 13, 125, 204, 345, 660, 756, 1222, 1268, 1510, 1514, 1574, 35, 331, 574, 1408, 1540, 1582, 1588, 441, 1036, 1200, 1267, 1608, 536, 563, 1307, 1353, 1386, 1422, 346, 645, 676, 805, 1257, 1594, 1596, 1657, 198, 304, 430, 531, 639, 684, 1055, 1183, 1197, 1495, 1642, 87, 91, 1101, 1159, 1186, 1443, 1509, 1592, 212, 634, 1043, 1178, 1654, 178, 497, 854, 1391, 1618, 17, 652, 977, 1119, 36, 1333, 1404, 598, 721, 798, 816, 897, 1388, 1557, 185, 440, 641, 965, 1248, 1345, 1427, 1570, 24, 562, 789, 840, 1002, 1299, 1392, 1541, 306, 859, 1153, 1194, 1204, 1215, 1239, 1272, 1580, 333, 682, 844, 873, 1475, 1572, 1651, 177, 492, 669, 780, 926, 970, 1157, 1291, 1436, 68, 290, 349, 406, 413, 507, 774, 1193, 1577, 95, 266, 561, 603, 626, 759, 1660, 166, 514, 741, 829, 1361, 1374, 473, 1091, 1160, 1323, 1455, 1487, 1629, 111, 911, 1106, 1130, 1339, 1579, 405, 498, 915, 1118, 1405, 236, 274, 315, 444, 1445, 132, 1047, 1231, 1472, 1537, 93, 511, 513, 558, 795, 1131, 1230, 1283, 57, 837, 933, 1163, 1607, 113, 202, 417, 779, 817, 920, 1012, 1352, 1425, 1569, 1587, 3, 227, 372, 987, 1167, 1214, 1360, 1367, 1432, 677, 1005, 1286, 1382, 1390, 226, 727, 740, 749, 100, 153, 382, 443, 466, 559, 776, 1482, 1571, 1584, 161, 550, 1165, 1476, 1503, 107, 228, 247, 292, 803, 955, 1023, 1072, 1598, 1008, 1030, 1335, 27, 1205, 1501, 1521, 109, 151, 218, 504, 521, 922, 1226, 1377, 1479, 594, 732, 797, 1097, 1302, 1401, 175, 356, 402, 753, 848, 81, 424, 703, 904, 939, 1013, 419, 429, 819, 1319, 949, 1066, 1088, 1164, 1583, 1603, 16, 44, 733, 1166, 1256, 1259, 1315, 38, 489, 650, 1081, 1507, 1637, 347, 509, 529, 565, 778, 1187, 1198, 232, 339, 408, 671, 964, 1293, 1420, 690, 743, 841, 948, 1241, 1251, 1290, 1303, 1439, 1522, 327, 704, 1441, 188, 564, 714, 793, 808, 845, 875, 1140, 1199, 1610, 614, 605, 745, 1646, 28, 117, 352, 452, 713, 818, 1156, 1379, 1671, 158, 270, 747, 963, 240, 1083, 1243, 1563, 195, 651, 830, 1280, 606, 610, 831, 878, 1051, 1403, 39, 48, 196, 246, 295, 1529, 248, 289, 833, 1060, 1079, 1378, 273, 1078, 397, 450, 1158, 324, 581, 961, 1542, 576, 1301, 1389, 1553, 150, 207, 461, 515, 1387, 1562, 1635, 1663, 330, 334, 537, 577, 633, 786, 1363, 1606, 32, 114, 300, 885, 969, 1456, 893, 101, 219, 404, 527, 1343, 1415, 1614, 1628, 14, 623, 742, 953, 1270, 1505, 49, 468, 934, 941, 1124, 137, 464, 662, 675, 832, 951, 1593, 45, 381, 804, 1185, 1645, 463, 736, 791, 823, 1546, 52, 388, 1223, 1461, 1653, 984, 376, 617, 737, 1196, 1261, 1287, 7, 260, 730, 912, 1402, 30, 53, 455, 685, 687, 1172, 1539, 1595, 242, 1491, 239, 370, 592, 887, 1300, 657, 693, 715, 1146, 1162, 1673, 301, 383, 1011, 1414, 308, 505, 672, 1506, 1519, 237, 542, 1273, 1349, 1526, 557, 856, 1029, 262, 305, 839, 1292, 1444, 1561, 697, 820, 1086, 1207, 259, 1103, 1481, 84, 883, 1034, 1135, 1533, 134, 582, 622, 921, 1480, 1547, 115, 627, 695, 1304, 937, 1074, 1406, 131, 481, 135, 751, 423, 1254, 233, 323, 448, 530, 700, 1195, 570, 792, 263, 318, 171, 353, 460, 716, 738, 865, 1238, 1338, 277, 470, 1057, 106, 506, 821, 918, 1279, 51, 809, 1355, 1458, 575, 642, 720, 824, 946, 1177, 1306, 1412, 211, 533, 554, 968, 1188, 1192, 401, 649, 692, 783, 1189, 1632, 275, 79, 276, 608, 512, 254, 858, 863, 1511, 478, 518, 706, 966, 1341, 1383, 342, 567, 1364, 1631, 55, 252, 806, 914, 1003, 1073, 1120, 1531, 625, 784, 814, 993, 1208, 309, 371, 1385, 167, 272, 325, 583, 802, 1424, 1568, 209, 665, 864, 1025, 1508, 172, 310, 501, 628, 1020, 1399, 98, 313, 729, 938, 978, 1201, 1225, 1263, 584, 670, 861, 1125, 319, 1206, 532, 790, 1418, 264, 482, 1212, 773, 1428, 127, 285, 1219, 34, 528, 785, 900, 991, 1551, 958, 1326, 64, 540, 872, 1275, 37, 261, 1493, 1434, 1612, 284, 495, 600, 1284, 391, 1652, 5, 72, 988, 20, 1090, 1535, 735, 1366, 1567, 573, 1484, 428, 777, 794, 929, 281, 379, 1093, 637, 925, 1394, 1527, 206, 238, 510, 1236, 1528, 1218, 1564, 1591, 1613, 1325, 1151, 1234, 850, 59, 541, 836, 1007, 1473, 317, 538, 619, 849, 133, 303, 1154, 1229, 630, 899, 1245, 1396, 8, 867, 480, 543, 1127, 888, 957, 919, 181, 390, 884, 508, 846, 1269, 877, 1655, 1494, 1634, 1138, 1331, 224, 698, 1237, 451, 1059, 547, 477, 705, 1031, 1321, 935, 433, 1538, 210, 1416, 4, 205, 1102, 1656, 54, 1469, 1525, 148, 474, 2, 1104, 350, 485, 365, 952, 1224, 291, 447, 553, 843, 995, 6, 1080, 1393, 312, 980, 708, 828, 421, 1471, 1545, 643, 1105, 604, 1100, 173, 834, 1601, 119, 494, 580, 632, 629, 1576, 1381, 990, 757, 767, 1305, 108, 615, 1462, 1227, 189, 348, 1559, 1524, 1015, 1661, 320, 1560, 1329, 1247, 772, 1433, 490, 1372, 1314)