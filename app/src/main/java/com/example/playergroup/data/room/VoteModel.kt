package com.example.playergroup.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vote")
data class VoteModel(
    @PrimaryKey(autoGenerate = true) var sequence: Int?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "voteData") var voteData: List<VoteData>, //투표
    @ColumnInfo(name = "clubKey") var clubKey: String?,
    @ColumnInfo(name = "creater") var creater: String?,
    @ColumnInfo(name = "pickCount") var pickCount: Int = 0,
    @ColumnInfo(name = "multipleVote") var multipleVote: Boolean = false,
    @ColumnInfo(name = "review") var review: List<VoteReview> = arrayListOf()
) {

    fun clickRadioBtn(index: Int): VoteModel {
        val data = voteData.mapIndexed { itemIndex, item ->
            if (itemIndex == index) {
                item.copy(checked = item.checked.not())
            } else {
                if (item.checked && multipleVote) {
                    item.copy(checked = false)
                } else {
                    item
                }
            }
        }
        voteData = data
        return this
    }
}

data class VoteData(
    var voteName: String,
    var count: Int,
    var voteUser: ArrayList<String>?,
    var checked: Boolean = false
)

data class VoteReview(
    var name: String = "",
    var review: String = ""
)