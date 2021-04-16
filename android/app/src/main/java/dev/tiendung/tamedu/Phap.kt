package tamedu.phap

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import dev.tiendung.tamedu.helpers.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

// Init a mediaPlayer to play phap
private var _phapPlayer: MediaPlayer = MediaPlayer()
private var _phapIsLoading: Boolean = false
private var _phapIsPlaying: Boolean = false
private var _currentPhap: Phap? = null
private var _autoPlayed = false
private var _isThuGian = false
private var _currentPhapPosition: Int = 0
private var _phapPlayerTimer: Timer = Timer()

fun getCurrentPhapPosition():String {
    var s = _currentPhapPosition / 1000
    val m = s / 60
    s %= 60
    return "%02d:%02d".format(m, s.absoluteValue)
}

fun docButtonText(): String {
    return if (_phapIsPlaying) "Dừng" else tamedu.reminder.toggleText()
}

fun thuGianButtonText(context: Context): String {
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

fun startPlayThuGian(context: Context): String? {
    if (!_phapIsLoading) { // load new phap or thu gian
        finishPhap()
        _isThuGian = true
        tamedu.reminder.newCurrent(context, 15)
        _currentPhap = getRandomThuGian()
        toast(context, loadAndPlayPhap(context))
    }
    return "Đang tải \"${currentTitle()}\" ..."
}

fun startPlayPhap(context: Context): String? {
    if (!_phapIsLoading) { // load new phap or thu gian
        finishPhap()
        _currentPhap = getRandomPhap()
        toast(context, loadAndPlayPhap(context))
    }
    return "Đang tải \"${currentTitle()}\" ..."
}

fun finishPhap(): String? {
    _phapPlayerTimer.cancel()
    _phapPlayer.release()
    _phapIsPlaying = false
    _phapIsLoading = false
    _autoPlayed = false
    _currentPhapPosition = 0
    _isThuGian = false
    return null
}

fun currentTitle(): String { return _currentPhap!!.title }

fun checkTimeToPlay(context: Context): String {
    val currentTime = Calendar.getInstance()
    val currH = currentTime[Calendar.HOUR_OF_DAY]
    val currM = currentTime[Calendar.MINUTE]
    // Reset counter
    if (currH >= 1 && currH <= 3) tamedu.count._todayReseted = false
    // Auto play Phap
    if (!_autoPlayed && !_phapIsLoading && !_phapIsPlaying && (
                    (currH == 11 && currM > 15) || (currH == 12 && currM > 15) ||
                    (currH == 22 && currM > 15) || (currH == 23 && currM > 15) ||
                    (currH ==  0 && currM > 15) || (currH ==  1 && currM > 15) ||
                    (currH ==  2 && currM > 15) || (currH ==  3 && currM > 15) ||
                    (currH ==  4 && currM > 15) || (currH ==  5 && currM >  5)
    )) {
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

        // Delay 0.6 second
        setOnPreparedListener { mp ->
            Timer("SettingUpNghePhap", false).schedule(600) {
                _phapIsLoading = false
                _phapIsPlaying = true
                tamedu.reminder.stopAndMute()
                if (!_isThuGian) {
                    var x = mp.getDuration() * (0.5+0.3*Random().nextDouble())
                    mp.seekTo(x.roundToLong(), MediaPlayer.SEEK_NEXT_SYNC)
                }
                mp.start()
                context.broadcastUpdateWidget(NGHE_PHAP_BEGIN)
            }
            // Every second, check progress
            _phapPlayerTimer = Timer("CheckNghePhapProgress", false)
            _phapPlayerTimer.schedule(2200, 1000) {
                _currentPhapPosition = mp.getDuration() - mp.getCurrentPosition()
                context.broadcastUpdateWidget(NGHE_PHAP_PROGRESS)
            }
        }

        setOnCompletionListener {
            val k = if (_isThuGian) THU_GIAN_COUNT_KEY else NGHE_PHAP_COUNT_KEY
            tamedu.count.inc(context, k, 1)
            finishPhap()
            context.broadcastUpdateWidget(NGHE_PHAP_FINISH)
        }
    }

    var txt = phap.audioUrl

    if (phap.audioFile.exists()) {
        val fd = FileInputStream(phap.audioFile).fd
        _phapPlayer.setDataSource(fd)
        txt = "${phap.audioFile}".replace("/storage/emulated/0/","")
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