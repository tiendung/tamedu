package tamedu.phap

import android.content.Context
import android.net.Uri
import android.media.AudioAttributes
import android.media.MediaPlayer
import java.util.Calendar

import dev.tiendung.tamedu.helpers.*

// Init a mediaPlayer to play phap
private var _phapPlayer: MediaPlayer = MediaPlayer()
private var _phapIsLoading: Boolean = false
private var _phapIsPlaying: Boolean = false
private var _currentPhap: Phap? = null
private var _stopPhapClicksCount: Int = 0
private var _autoPlayed = false

fun updatePlayPhap(context: Context): String? {
    if (_phapIsPlaying) {
        _stopPhapClicksCount += 1
        when (_stopPhapClicksCount) {
            1 -> return "Đang nghe \"${currentTitle()}\"'. Nhấn \"Dừng nghe\" lần nữa để kết thúc."
            2 -> return finishPhap()
        }
    }
    if (!_phapIsLoading) {
        _currentPhap = getRandomPhap()
        loadAndPlayPhap(context)
        return "Đang tải '${currentTitle()}' ..."
    }
    return null
}

private fun finishPhap(): String? { 
    _phapPlayer.release() 
    _phapIsPlaying = false
    _phapIsLoading = false
    _autoPlayed = false
    _stopPhapClicksCount = 0
    return null
}

fun currentTitle(): String { return _currentPhap!!.title }
fun buttonText(): String {
    return when (_phapIsPlaying) {
        true  -> if (_stopPhapClicksCount == 0) "Dừng nghe" else "Dừng nghe ($_stopPhapClicksCount)"
        false -> when (_phapIsLoading) {
            true  -> "Đang tải ..."
            false -> "Nghe pháp"
        }
    }
}

fun checkToPlayInEarlyMorning(context: Context): String {
    val currentTime = Calendar.getInstance()
    val currH = currentTime[Calendar.HOUR_OF_DAY]
    val currM = currentTime[Calendar.MINUTE]
    if (!_autoPlayed && !_phapIsLoading && !_phapIsPlaying &&
        ((currH == 5 && currM > 15) || (currH == 19 && currM > 15)) ) {
        context.broadcastUpdateWidget(NGHE_PHAP)
        _autoPlayed = true
    }
    return "$currH : $currM"
}

private fun loadAndPlayPhap(context: Context) {
    val phap: Phap = _currentPhap!!
    _phapIsLoading = true
    _phapPlayer = MediaPlayer().apply {
        setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )
        setDataSource(context, phap.audioUri)
        setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            _phapIsLoading = false
            _phapIsPlaying = true
            mp.start()
            context.broadcastUpdateWidget(PLAY_PHAP_BEGIN)
        })
        setOnCompletionListener(MediaPlayer.OnCompletionListener { mp ->
            finishPhap()
            context.broadcastUpdateWidget(FINISH_PHAP)
        })
        prepareAsync()
    }
}

data class Phap(val title: String, val audioUri: Uri)

private fun getRandomPhap(): Phap {
    val (id, title) = PHAP_IDS_TO_TITLES.random()
    val uri = Uri.parse("https://tiendung.github.io/$id")
//    val uri = Uri.parse("https://tiendung.github.io/quotes/opus/11.ogg")
    return Phap(title = title,  audioUri = uri)
}

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
    "phaps/tutaptrongCS_DayCon.ogg" to "Dạy con",
    "phaps/tutaptrongCS_ChetKhongHoiTiec.ogg" to "Chết không hối tiếc",
    "phaps/tutaptrongCS_DeDuoi.ogg" to "Dễ duôi",
    "phaps/tutaptrongCS_LaoDongGiupChanhNiemRaSao.ogg" to "Lao động giúp chánh niệm ra sao",
    "phaps/tutaptrongCS_TamDonGian.ogg" to "Tâm đơn giản",
    "phaps/tutaptrongCS_TietKiemNangLuong.ogg" to "Tiết kiệm năng lượng"
)