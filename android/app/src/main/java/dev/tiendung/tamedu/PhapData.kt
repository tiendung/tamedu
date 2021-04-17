package tamedu.phap.data

import android.os.Environment
import java.io.File
import android.content.Context
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}

fun getPhapsFromJsonFile(context: Context): List<Phap> {
    val listPhapType = object : TypeToken<List<Phap>>() {}.type
    return Gson().fromJson(getJsonDataFromAsset(context, "phaps.json"), listPhapType)
}


data class Phap(val title: String, val audioUrl: String, val audioFile: File)


fun getRandomThuGian(): Phap {
    val (id, title) = THU_GIAN_IDS_TO_TITLES.random()
    return initPhap(id, title)
}

fun getRandomPhap(): Phap {
    val (id, title) = PHAP_IDS_TO_TITLES.random()
    return initPhap(id, title)
}

private fun initPhap(id: String, title: String): Phap {
    return Phap(
            title = title,
            audioUrl = "https://tiendung.github.io/$id",
            audioFile = File(Environment.getExternalStorageDirectory(), "Documents/stp/$id")
    )
}
private val THU_GIAN_IDS_TO_TITLES = arrayOf(
        "phaps/HuongDanRaQuetVaThuGianToanThan.ogg" to "Thư giãn 30 phút",
        "phaps/huongdan_thienNam.ogg" to "Thư giãn 24 phút",
        "phaps/thien_nam_15_phut_01.ogg" to "Thư giãn 15 phút",
        "phaps/thien_nam_10_phut.ogg" to "Thư giãn 10 phút"
)

private val PHAP_IDS_TO_TITLES = arrayOf(
"phaps/an-uong-dung.ogg" to "Ăn uống đúng",
"phaps/Tu-Tap-Khong-Phai-Chi-La-Thien.ogg" to "Tu tập ko phải chỉ là thiền",
"phaps/Tinh-Tan-fix.ogg" to "Tinh tấn",
"phaps/Vo-Mong.ogg" to "Vỡ mộng",
"phaps/Tran-Trong.ogg" to "Trân trọng",
"phaps/suy-nghi-tich-cuc.ogg" to "Suy nghĩ tích cực",
"phaps/cai-nay-chi-ton-tai-trong-suy-nghi.ogg" to "Cái này chỉ tồn tại trong suy nghĩ",
"phaps/20200704.ogg" to "04-07-2020",
"phaps/2020-05-31.ogg" to "31-05-2020",
"phaps/rungthiennuisuong.ogg" to "Nói chuyện 02-05-2020",
"phaps/20160501NhanDuyen.ogg" to "Nhân Duyên 01-05-2016",
"phaps/20160502XaLy.ogg" to "Xả Ly 02-05-2016",
"phaps/20161105TaoPhuoc.ogg" to "Tạo Phước 05-11-2016",
"phaps/CuSyTu.ogg" to "Cư Sỹ Tu",
"phaps/thuyetphap_CacMucDoCoY.ogg" to "Các mức độ cố ý",
"phaps/thuyetphap-ODoiMoiLaMatTran.ogg" to "Ở đời mới là mặt trận",
"phaps/thuyetphap-chilacamgiac.ogg" to "Chỉ là một cảm giác",
"phaps/chanhNiemSauSac.ogg" to "Chánh niệm sâu sắc",
"phaps/hai-giai-doan-chanh-niem.ogg" to "Hai giai đoạn chánh niệm",
"phaps/thuyetPhap_goiYDinhHuong.ogg" to "Gợi ý định hướng",
"phaps/thuyetphap_giaTriGiaTang.ogg" to "Giá trị gia tăng",
"phaps/thuyetphap_songTichCuc.ogg" to "Sống tích cực",
"phaps/thuyetphap_hayBietThuDong.ogg" to "Hay biết thụ động",
"phaps/thuyetphap_tapTrungVaChanhNiem.ogg" to "Tập trung và chánh niệm",
"phaps/cacchudetutaptrongcuocsong_dinhHinhTuongLai.ogg" to "Định hình tương lai",
"phaps/thuyetPhap_cuocSongLaDeSuDung.ogg" to "Cuộc sống là để sử dụng",
"phaps/tutaptrongCS_BuonChan.ogg" to "Buồn chán",
"phaps/tutaptrongCS_luaChon.ogg" to "Lựa chọn",
"phaps/tutaptrongCS_nguoiDocHai.ogg" to "Người độc hại",
"phaps/tutaptrongCS_tamNhinCuocDoi.ogg" to "Tầm nhìn cuộc đời",
"phaps/tutaptrongCS_camXuc.ogg" to "Cảm xúc",
"phaps/tutaptrongCS_nhungHieuBietSaiLamVeThien.ogg" to "Những hiểu biết sai về thiền",
"phaps/tutaptrongCS_tuPhanBien.ogg" to "Tự phản biện",
"phaps/tutaptrongCS_damSongKhacNguoi.ogg" to "Dám sống khác người",
"phaps/tutaptrongCS_ViecQuanTrong.ogg" to "Việc quan trọng",
"phaps/tutaptrongCS_TheNaoLaQuyetDinhDung.ogg" to "Thế nào là quyết định đúng",
"phaps/tutaptrongCS_CungKinhPhap.ogg" to "Cung kính Pháp",
"phaps/tutaptrongCS_ChuYVaoSuKhacBiet.ogg" to "Chú ý vào sự khác biệt",
"phaps/tutaptrongCS_PhatTrienTinhGiacVaTue.ogg" to "Phát triển tỉnh giác và tuệ",
"phaps/tutaptrongCS_VanTuTu.ogg" to "Văn - Tư - Tu",
"phaps/tutaptrongCS_TimHungThuThucHanh.ogg" to "Tìm hứng thú thực hành",
"phaps/tutaptrongCS_DauTuChoTuongLai.ogg" to "Đầu tư cho tương lai",
"phaps/tutaptrongCS_ThaLongDeCamNhanSauSacHon.ogg" to "Thả lỏng để cảm nhận sâu sắc hơn",
"phaps/tutaptrongCS_XungDangCuocSongLamNguoi.ogg" to "Xứng đáng cuộc sống làm người",
"phaps/tutaptrongCS_CuHuychChoCuocDoi.ogg" to "Cú huých cho cuộc đời",
"phaps/tutaptrongCS_ChuY.ogg" to "Chú ý",
"phaps/tutaptrongCS_CongNghiep.ogg" to "Cộng nghiệp",
"phaps/tutaptrongCS_CachTangCuongDucTin.ogg" to "Cách tăng trưởng đức tin",
"phaps/tutaptrongCS_LyTuongVaThucTe.ogg" to "Lý tưởng và thực tế",
"phaps/tutaptrongCS_NguoiChanhNiemLamGi.ogg" to "Người chánh niệm làm gì",
"phaps/tutaptrongCS_NoiChuyenVeHanhPhuc.ogg" to "Nói chuyện về hạnh phúc",
"phaps/tutaptrongCS_SuGiaCuaSuThat.ogg" to "Sứ giả của sự thật",
"phaps/tutaptrongCS_ThuGianVaSatNaDinh.ogg" to "Thư giãn và sát na định",
"phaps/tutaptrongCS_SuDungChanhNiem.ogg" to "Sử dụng chánh niệm",
"phaps/tutaptrongCS_ThongTinThua.ogg" to "Thông tin thừa",
"phaps/tutaptrongCS_NguoiChanThat.ogg" to "Người chân thật",
"phaps/tutaptrongCS_NguoiThuongLuu.ogg" to "Người thượng lưu",
"phaps/tutaptrongCS_XuLyKhungHoang.ogg" to "Xử lý khủng hoảng",
"phaps/tutaptrongCS_DayCon.ogg" to "Dạy con",
"phaps/tutaptrongCS_ChuyenNghiep.ogg" to "Chuyên nghiệp",
"phaps/tutaptrongCS_ChetKhongHoiTiec.ogg" to "Chết không hối tiếc",
"phaps/tutaptrongCS_HinhAnhTuTao.ogg" to "Hình ảnh tự tạo",
"phaps/tutaptrongCS_NhungChuongNgaiCuaNguoiNuTrongTuTap.ogg" to "Chướng ngại của người nữ trong tu tập",
"phaps/tutaptrongCS_bonLoaiPhapHanh.ogg" to "Bốn loại pháp hành",
"phaps/tutaptrongCS_HopDongQuanHe.ogg" to "Hợp đồng quan hệ",
"phaps/tutaptrongCS_ThayToi.ogg" to "Thầy tôi",
"phaps/tutaptrongCS_CacChiPhanGiacNgo.ogg" to "Các chi phần giác ngộ",
"phaps/tutaptrongCS_BatMan.ogg" to "Bất mãn",
"phaps/tutaptrongCS_BalamatKhamNhan.ogg" to "Ba la mật Kham nhẫn",
"phaps/tutaptrongCS_HauDau.ogg" to "Hậu đậu",
"phaps/tutaptrongCS_BalamatQuyetDinh.ogg" to "Ba la mật Quyết định",
"phaps/tutaptrongCS_BalamatChanThat.ogg" to "Ba la mật Chân thật",
"phaps/tutaptrongCS_HuongDanThucHanhThienTuNiemXu.ogg" to "Hướng dẫn thực hành Thiền Tứ Niệm Xứ",
"phaps/tutaptrongCS_DiTimChanLy.ogg" to "Đi tìm chân lý",
"phaps/tutaptrongCS_7GiaiDoanThanhTinh.ogg" to "7 giai đoạn thanh tịnh",
"phaps/tutaptrongCS_HaiThaiCuc.ogg" to "Hai thái cực",
"phaps/tutaptrongCS_TuKyLuat.ogg" to "Tự kỷ luật",
"phaps/tutaptrongCS_thucHanhTuanTuTuNiemXu.ogg" to "Thực hành tuần tự Tứ niệm Xứ",
"phaps/tutaptrongCS_nhungCaiPhaiTuQuangVaoThan.ogg" to "Những cái Phải tự quàng vào thân",
"phaps/tutaptrongCS_DeDuoi.ogg" to "Dễ duôi",
"phaps/tutaptrongCS_LaoDongGiupChanhNiemRaSao.ogg" to "Lao động giúp chánh niệm ra sao",
"phaps/tutaptrongCS_benhCuaNguoiTu.ogg" to "Bệnh của người tu",
"phaps/tutaptrongCS_NoiChanhNiemCoLoiIchGi.ogg" to "Nói chánh niệm có lợi ích gì",
"phaps/tutaptrongCS_ThoiPhapLeDangYTaiMyanmar.ogg" to "Thời pháp lễ dâng y tại Myanmar",
"phaps/tutaptrongCS_GhiNhanThuanTuyVaTriTueTrucGiac.ogg" to "Ghi nhận thuần tuý và trí tuệ trực giác",
"phaps/tutaptrongCS_thucHanhTrongCuocSong.ogg" to "Thực hành trong cuộc sống",
"phaps/tutaptrongCS_dichDenCuaDuongTu.ogg" to "Đích đến của đường tu",
"phaps/tutaptrongCS_NguoiTriVaKeNgu.ogg" to "Người trí và kẻ ngu",
"phaps/tutaptrongCS_ChapNhanChinhMinh.ogg" to "Chấp nhận chính mình",
"phaps/tutaptrongCS_TamDonGian.ogg" to "Tâm đơn giản",
"phaps/tutaptrongCS_TueGiac.ogg" to "Tuệ giác",
"phaps/tutaptrongCS_YeuThuongChinhMinh.ogg" to "Yêu thương chính mình",
"phaps/tutaptrongCS_YNghiaCuocDoi.ogg" to "Ý nghĩa cuộc đời",
"phaps/tutaptrongCS_TuTrongCuocSong.ogg" to "Tu trong cuộc sống",
"phaps/tutaptrongCS_TuDeTimLaiChinhMinh.ogg" to "Tu để tìm lại chính mình",
"phaps/tutaptrongCS_TietKiemNangLuong.ogg" to "Tiết kiệm năng lượng",
"phaps/tutaptrongCS_ThienAc.ogg" to "Thiện ác",
"phaps/tutaptrongCS_TamDanhGia.ogg" to "Tâm đánh giá",
"phaps/tutaptrongCS_NgaMan.ogg" to "Ngã mạn",
"phaps/Trinh-Phap-1-1-2015-re-edit-add-hoi-huong.ogg" to "Trình pháp",
"phaps/tutaptrongCS_DayChuyenSanXuatPhienNao.ogg" to "Dây chuyền sản xuất phiền não",
"phaps/tutaptrongCS_Balamat.ogg" to "Ba la mật",
"phaps/tutaptrongCS_TinhThuong.ogg" to "Tình thương",
"phaps/tutaptrongCS_CoDon.ogg" to "Cô đơn",
"phaps/tutaptrongCS_AiLamMinhKho.ogg" to "Ai làm mình khổ",
"phaps/KinhHanhPhuc10.ogg" to "Kinh Hạnh Phúc 10",
"phaps/KinhHanhPhuc9.ogg" to "Kinh Hạnh Phúc 9",
"phaps/KinhHanhPhuc8.ogg" to "Kinh Hạnh Phúc 8",
"phaps/KinhHanhPhuc7.ogg" to "Kinh Hạnh Phúc 7",
"phaps/KinhHanhPhuc6.ogg" to "Kinh Hạnh Phúc 6",
"phaps/KinhHanhPhuc5.ogg" to "Kinh Hạnh Phúc 5",
"phaps/KinhHanhPhuc4.ogg" to "Kinh Hạnh Phúc 4",
"phaps/KinhHanhPhuc3.ogg" to "Kinh Hạnh Phúc 3",
"phaps/KinhHanhPhuc2.ogg" to "Kinh Hạnh Phúc 2",
"phaps/KinhHanhPhuc1.ogg" to "Kinh Hạnh Phúc 1",
"phaps/buocdautapthien_thucHanhDonGian.ogg" to "Thực hành đơn giản ",
"phaps/buocdautapthien_CongThucThien.ogg" to "Công thức thiền",
"phaps/buocdautapthien_MayChiaSeVePhapHanh.ogg" to "Mấy chia sẻ về Pháp Hành",
"phaps/buocdautapthien_ChanhKienVaNghiep.ogg" to "Chánh kiến và nghiệp",
"phaps/buocdautapthien_TieuChuanThienTot.ogg" to "Tiêu chuẩn Thiền tốt",
"phaps/buocdautapthien_ChanhNiemVaDinhHuongCuocSong.ogg" to "Chánh niệm và định hướng cuộc sống",
"phaps/buocdautapthien_HanhThienViLoiIchCuaChinhMinh.ogg" to "Hành thiền vì lợi ích của chính mình",
"phaps/buocdautapthien_GhiNhanThuanTuy.ogg" to "Ghi nhận thuần túy",
"phaps/buocdautapthien_ConDuongTuTap.ogg" to "Con đường tu tập",
"phaps/buocdautapthien_MucDichTuTap.ogg" to "Mục đích tu tập",
"phaps/buocdautapthien_TuChoMinhHayChoNguoi.ogg" to "Tu cho mình hay tu cho người",
"phaps/buocdautapthien_suHieuBietDunDan.ogg" to "Sự hiểu biết đúng đắn",
"phaps/buocdautapthien_SachVoLamHaiMinhTheNao.ogg" to "Sách vở làm hại mình thế nào?",
"phaps/buocdautapthien_khongTronLanPhapHanh.ogg" to "Không trộn lẫn Pháp hành",
"phaps/buocdautapthien_ThaiDoDung.ogg" to "Thái độ đúng",
"phaps/buocdautapthien_ChanhNiemHoiTho.ogg" to "Chánh niệm hơi thở",
"phaps/buocdautapthien_CacTuTheThien.ogg" to "Các tư thế thiền",
"phaps/buocdautapthien_TaiSaoPhaiThuGian.ogg" to "Tại sao phải thư giãn",
"phaps/buocdautapthien_raSoatCamNhanThaLong.ogg" to "Rà soát - Cảm nhận - Thả lỏng",
"phaps/buocdautapthien_ThoaiMaiThuGianBietMinh.ogg" to "Thoải mái – Thư giãn – Biết mình",
"phaps/buocdautapthien_ThienLaGi.ogg" to "Thiền là gì?",
"phaps/buocdautapthien_ChanhNiemLaGi.ogg" to "Chánh niệm là gì?",
"phaps/thuyetphap_013SongThuanPhap.ogg" to "Sống thuận Pháp",
"phaps/thuyetphap_DucTin.ogg" to "Đức tin",
"phaps/thuyetphap_NhungLoiDayThucSuCuaDucPhat.ogg" to "Những lời dạy thực sự của đức Phật",
"phaps/thuyetphap_DaoPhatDichThucDayNhungGi.ogg" to "Đạo Phật đích thực dạy những gì?",
"phaps/thuyetphap_leNghiPhatTuCanBiet.ogg" to "Lễ nghi Phật Tử cần biết",
"phaps/thuyetphap_LichSuPhatTrienDaoPhat.ogg" to "Lịch sử phát triển Đạo Phật",
"phaps/thuyetphap_denVoiDao.ogg" to "Đến với Đạo",
"phaps/tutaptrongCS_DoiDienVoiChinhMinh.ogg" to "Đối diện chính mình"
)

/* Seleted Phaps
private val PHAP_IDS_TO_TITLES = arrayOf(
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
"phaps/tutaptrongCS_ChetKhongHoiTiec.ogg" to "Chết không hối tiếc",
"phaps/tutaptrongCS_DeDuoi.ogg" to "Dễ duôi",
"phaps/tutaptrongCS_tuPhanBien.ogg" to "Tự phản biện",
"phaps/tutaptrongCS_TheNaoLaQuyetDinhDung.ogg" to "Thế nào là quyết định đúng",
"phaps/tutaptrongCS_ChuYVaoSuKhacBiet.ogg" to "Chú ý vào sự khác biệt",
"phaps/tutaptrongCS_ThaLongDeCamNhanSauSacHon.ogg" to "Thả lỏng để cảm nhận sâu sắc hơn",
"phaps/tutaptrongCS_LyTuongVaThucTe.ogg" to "Lý tưởng và thực tế",
"phaps/tutaptrongCS_ThongTinThua.ogg" to "Thông tin thừa",
"phaps/tutaptrongCS_DayCon.ogg" to "Dạy con",
"phaps/tutaptrongCS_SuGiaCuaSuThat.ogg" to "Sứ giả của sự thật",
"phaps/tutaptrongCS_NguoiChanThat.ogg" to "Người chân thật",
"phaps/tutaptrongCS_LaoDongGiupChanhNiemRaSao.ogg" to "Lao động giúp chánh niệm ra sao",
"phaps/tutaptrongCS_TietKiemNangLuong.ogg" to "Tiết kiệm năng lượng",
"phaps/buocdautapthien_TaiSaoPhaiThuGian.ogg" to "Tại sao phải thư giãn",
"phaps/buocdautapthien_ThaiDoDung.ogg" to "Thái độ đúng",
"phaps/buocdautapthien_CongThucThien.ogg" to "Công thức thiền",
"phaps/buocdautapthien_thucHanhDonGian.ogg" to "Thực hành đơn giản ",
"phaps/buocdautapthien_raSoatCamNhanThaLong.ogg" to "Rà soát - Cảm nhận - Thả lỏng",
"phaps/buocdautapthien_ThoaiMaiThuGianBietMinh.ogg" to "Thoải mái – Thư giãn – Biết mình",
"phaps/thuyetphap_013SongThuanPhap.ogg" to "Sống thuận Pháp"
) /* */