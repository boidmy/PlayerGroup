package com.example.playergroup.data

import com.example.playergroup.data.AppUpDateType.*

data class IntroData (
    var appNewVersion: String? = null,
    var appUpdateType: String? = null,
    var appUpdateInfo: String? = null,
) {
    /**
     * appUpDateType - A : 강제 업데이트
     *               - B : 선택 업데이트
     */
    fun getUpDateType(): AppUpDateType =
        if (appUpdateType == "A") FORCED else if (appUpdateType == "B") SELECT else NONE

}

enum class AppUpDateType {
    FORCED,
    SELECT,
    NONE
}
