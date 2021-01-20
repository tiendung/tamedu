package dev.tiendung.tamedu.data

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

data class Phap(val title: String, val audioUri: Uri)
data class Quote(val imageUri: Uri, val imageFileName: String, val audioFd: AssetFileDescriptor)

fun getRandomPhap(): Phap {
    val random = (0..PHAP_IDS_TO_TITLES.size).random()
    val (id, title) = PHAP_IDS_TO_TITLES[random]
    val uri = Uri.parse("https://tiendung.github.io/$id")
//    val u = Uri.parse("https://tiendung.github.io/quotes/opus/11.ogg")
    return Phap(title = title,  audioUri = uri)
}

fun getRandomQuote(context: Context): Quote {
    val quoteId = QUOTE_IDS_SORTED_BY_LEN[(0..999).random()]
    val quoteFile = "$QUOTE_DIR/$quoteId"
    return Quote(
            imageFileName = "$quoteFile.png",
            imageUri = Uri.parse("file:///android_asset/$quoteFile.png"),
            audioFd = context.getAssets().openFd("$quoteFile.ogg")
    )
}

const val QUOTE_DIR = "quotes"
fun saveQuoteImageToFile(context: Context, quote: Quote) : File {
    val externalFilesDir = context.getExternalFilesDir(null)
    File(externalFilesDir, QUOTE_DIR).mkdir()
    val file = File(externalFilesDir, quote.imageFileName)
    try {
        val ins = context.getAssets().open(quote.imageFileName)
        val os = FileOutputStream(file)
        val data = ByteArray(ins.available())
        ins.read(data)
        os.write(data)
        ins.close()
        os.close()
    } catch (e: IOException) {
        Log.w("ExternalStorage", "Error writing $file", e)
    }
    return file
}

const val BELL_FILE_NAME = "bell.ogg"
val PHAP_IDS_TO_TITLES = arrayOf(
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
val QUOTE_IDS_SORTED_BY_LEN = intArrayOf(
        612,19,394,868,257,701,656,870,588,1184,653,1044,1627,267,269,1148,927,1037,1202,74,568,668,851,1297,82,
        1474,1558,123,549,950,1460,250,1295,1340,145,587,591,936,427,1173,1411,1658,1662,595,1038,499,546,711,913,1255,
        1289,847,1107,1249,1320,1009,1342,298,203,294,354,659,717,696,1068,1310,251,321,128,593,1417,31,61,960,1021,
        1365,287,678,838,1063,1485,422,775,842,1056,40,73,217,314,673,1099,1155,1171,1064,1111,1457,1648,739,1552,1615,
        607,403,1264,1336,1668,253,699,1447,917,1049,1122,522,1054,1356,434,722,807,311,392,886,1332,316,373,516,1600,
        928,945,985,1052,1276,1639,1643,88,1669,9,26,103,156,551,620,811,1026,1035,1085,1129,1398,1492,1550,69,214,
        344,400,586,647,947,1123,1161,1298,1435,1454,1486,163,355,366,412,579,640,796,1027,152,220,343,378,465,616,
        689,813,1311,1530,29,42,102,694,758,1112,1322,1346,1409,1459,1497,1518,94,142,200,208,375,472,602,638,1132,
        1278,1449,1624,86,120,271,368,410,493,534,555,880,896,972,982,1095,1191,1235,1348,1636,154,297,393,399,519,
        539,544,971,1046,1065,1133,1147,1176,1288,1597,1599,1609,1638,1649,169,369,374,545,635,654,860,890,959,1282,1442,
        1605,1619,1650,332,426,475,718,907,994,1116,1152,1477,1543,524,686,752,760,930,931,1094,1136,1258,1266,1271,1334,
        1407,1499,1536,50,78,126,234,709,996,1076,1092,1143,1233,1324,1330,1375,1581,1585,105,140,265,326,357,585,631,
        762,942,998,1001,1018,1022,1110,1213,1431,1448,1488,1490,1523,130,176,243,359,414,476,502,569,688,943,1084,1410,
        1429,1440,1446,75,138,165,191,279,288,566,954,1113,1220,1362,1620,230,396,420,496,1053,15,96,97,192,340,
        456,486,572,855,989,1039,1240,1328,1515,1554,1626,435,479,764,1109,1145,1150,147,157,307,431,609,624,892,895,
        1077,1098,1137,1262,1308,1586,33,187,1010,1380,110,197,296,449,552,769,902,992,1108,1179,1616,1423,1621,1075,1277,
        1285,1371,1384,1590,168,398,1019,1470,1617,1667,62,852,1190,1316,21,70,116,322,462,560,1397,1502,1549,293,782,
        825,1134,1175,1666,25,184,302,338,385,407,746,1478,159,256,770,976,1000,1033,1114,1359,1430,1623,213,241,411,
        621,761,1244,1489,41,43,85,164,351,454,661,719,894,916,956,1370,1437,1544,46,77,258,337,881,940,1468,
        1640,162,377,728,944,80,222,225,418,459,500,710,923,1400,1672,10,170,613,71,146,179,193,221,905,973,
        1082,1141,1210,286,712,731,1209,1260,1344,1575,1664,12,89,471,702,1121,1294,1419,1,160,180,280,364,384,387,
        415,1246,1281,1548,118,245,548,874,1421,487,597,1483,1513,1534,139,183,283,432,787,876,1017,1149,1450,488,726,
        771,857,1024,1040,1413,1517,360,967,1070,186,589,681,1221,1464,1659,63,744,1087,1250,229,335,457,962,1050,1174,
        1203,1252,362,658,663,734,812,1180,1466,1604,1644,129,578,646,750,910,1089,1211,1467,1578,1630,655,871,908,997,
        1182,1516,386,889,901,1016,1309,249,453,520,523,596,599,707,891,903,1216,1296,1313,1463,182,216,361,409,636,
        674,979,1496,1611,1641,571,810,999,1042,1317,1357,1376,1602,11,667,748,1045,1128,1217,1318,1451,1452,65,416,680,
        763,909,1168,1170,1354,1498,1565,58,215,223,491,679,755,815,898,1048,1622,1670,144,231,1014,1058,1438,446,503,
        517,724,1169,1253,23,121,190,469,1096,1500,149,437,723,766,801,983,1504,781,835,1006,1369,60,445,765,768,
        882,1061,1139,1232,1665,0,199,299,358,436,800,879,1555,380,526,1032,1242,18,90,194,235,329,483,866,1181,
        1426,467,826,924,1358,1465,92,425,525,535,664,691,853,22,112,155,484,975,1004,1142,1520,124,174,282,328,
        336,611,244,268,442,869,906,1368,1512,1566,1589,104,389,1071,1350,1625,67,76,981,1115,1347,1633,1647,122,136,
        143,395,458,683,799,822,932,1144,1373,1532,66,788,827,862,1028,1395,141,439,590,56,201,367,601,754,974,
        986,1062,1126,1327,47,618,1069,1337,1573,341,556,648,725,1041,1274,1556,278,438,644,666,1228,1453,83,255,363,
        1067,1312,1265,1351,13,125,204,345,660,756,1222,1268,1510,1514,1574,35,331,574,1408,1540,1582,1588,441,1036,1200,
        1267,1608,536,563,1307,1353,1386,1422,346,645,676,805,1257,1594,1596,1657,198,304,430,531,639,684,1055,1183,1197,
        1495,1642,87,91,1101,1159,1186,1443,1509,1592,212,634,1043,1178,1654,178,497,854,1391,1618,17,652,977,1119,36,
        1333,1404,598,721,798,816,897,1388,1557,185,440,641,965,1248,1345,1427,1570,24,562,789,840,1002,1299,1392,1541,
        306,859,1153,1194,1204,1215,1239,1272,1580,333,682,844,873,1475,1572,1651,177,492,669,780,926,970,1157,1291,1436,
        68,290,349,406,413,507,774,1193,1577,95,266,561,603,626,759,1660,166,514,741,829,1361,1374,473,1091,1160,1323
)
