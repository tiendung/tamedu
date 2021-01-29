package tamedu.phap

import android.content.Context
import android.os.Environment
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import dev.tiendung.tamedu.helpers.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.Timer
import kotlin.concurrent.schedule

// Init a mediaPlayer to play phap
private var _phapPlayer: MediaPlayer = MediaPlayer()
private var _phapIsLoading: Boolean = false
private var _phapIsPlaying: Boolean = false
private var _currentPhap: Phap? = null
private var _stopPhapClicksCount: Int = 0
private var _isPause = false
private var _autoPlayed = false
private var _isThuGian = false

fun thuGianButtonText(context: Context): String {
    if (_phapIsPlaying) {
        return if (_isPause) "Nghe tiếp" else "Tạm dừng"
    }
    val count = tamedu.count.get(context, THU_GIAN_COUNT_KEY)
    var txt = "Thư giãn"
    if (count != 0) txt = "$txt $count"
    return txt
}

fun isPlaying(): Boolean {
    return _phapIsPlaying
}

fun isThuGian(): Boolean {
    return _isThuGian
}

fun updatePlayPhap(context: Context, thuGianButtonPressed: Boolean = false): String? {

    var txt: String? = null
    if (thuGianButtonPressed && _phapIsPlaying) {
        _isPause = !_isPause
        if (_isPause) {
            _phapPlayer.pause()
        } else {
            _phapPlayer.start()
        }
        return txt
    }

    if (_phapIsPlaying) {
        _stopPhapClicksCount += 1
        when (_stopPhapClicksCount) {
            1 -> {
                txt = "Đang nghe \"${currentTitle()}\". Nhấn \"Dừng nghe\" lần nữa để kết thúc."
                Timer("SettingUp", false).schedule(2500) {
                    if (_phapIsPlaying) {
                        _stopPhapClicksCount = 0
                        context.broadcastUpdateWidget(REFRESH)
                    }
                }
            }
            2 -> {
                finishPhap()
                txt = APP_TITLE
            }
        }
        return txt
    }
    
    if (!_phapIsLoading) { // load new phap or thu gian
        if (thuGianButtonPressed) {
            _isThuGian = true
            tamedu.reminder.newCurrent(context, 15)
            _currentPhap = getRandomThuGian()
        } else {
            _currentPhap = getRandomPhap()
        }
        toast(context, loadAndPlayPhap(context))
    }
    
    txt = "Đang tải \"${currentTitle()}\" ..."
    return txt
}

private fun finishPhap(): String? { 
    _phapPlayer.release() 
    _phapIsPlaying = false
    _phapIsLoading = false
    _autoPlayed = false
    _stopPhapClicksCount = 0
    _isThuGian = false
    return null
}

fun currentTitle(): String { return _currentPhap!!.title }
fun buttonText(): String {
    return when (_phapIsPlaying) {
        true -> if (_stopPhapClicksCount == 0) "Dừng nghe" else "Dừng nghe!"
        false -> when (_phapIsLoading) {
            true -> "Đang tải ..."
            false -> "Nghe pháp"
        }
    }
}

fun checkTimeToPlay(context: Context): String {
    val currentTime = Calendar.getInstance()
    val currH = currentTime[Calendar.HOUR_OF_DAY]
    val currM = currentTime[Calendar.MINUTE]
    // Reset counter
    if (currH >= 1 && currH <= 3) tamedu.count._todayReseted = false
    if (!_autoPlayed && !_phapIsLoading && !_phapIsPlaying &&
        ((currH == 5 && currM > 15) || (currH == 19 && currM > 15)) ) {
        context.broadcastUpdateWidget(NGHE_PHAP)
        _autoPlayed = true
    }
    return "$currH : $currM"
}

private fun loadAndPlayPhap(context: Context): String {
    val phap: Phap = _currentPhap!!
    _phapIsLoading = true
    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setOnPreparedListener { mp ->
            _phapIsLoading = false
            _phapIsPlaying = true
            tamedu.reminder.stopAndMute()
            mp.start()
            context.broadcastUpdateWidget(NGHE_PHAP_BEGIN)
        }

        setOnCompletionListener {
            tamedu.count.inc(context, if (_isThuGian) THU_GIAN_COUNT_KEY else NGHE_PHAP_COUNT_KEY, 1)
            finishPhap()
            context.broadcastUpdateWidget(NGHE_PHAP_FINISH)
        }
    }

    var txt = phap.audioUrl

    if (phap.audioFile.exists()) {
        val fd = FileInputStream(phap.audioFile).fd
        _phapPlayer.setDataSource(fd)
        txt = "${phap.audioFile}"
    } else _phapPlayer.setDataSource(context, Uri.parse(phap.audioUrl))
    _phapPlayer.prepareAsync()

    return txt
}

data class Phap(val title: String, val audioUrl: String, val audioFile: File)

private fun getRandomThuGian(): Phap {
    val (id, title) = THU_GIAN_IDS_TO_TITLES.random()
    return initPhap(id, title)
}

private fun getRandomPhap(): Phap {
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
)