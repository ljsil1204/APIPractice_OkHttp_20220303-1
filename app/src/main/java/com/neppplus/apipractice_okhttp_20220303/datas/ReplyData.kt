package com.neppplus.apipractice_okhttp_20220303.datas

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ReplyData(
    var id: Int,
    var content: String,
) {

    var writer = UserData()  // 모든 댓글에는 작성자가 있다. null 가능성이 없다.

    var selectedSide = SideData() // 모든 댓글에는 선택한 진영이 있다. null 가능성 X.

//    작성 일시를 담아둘 변수
//    일/시 데이터를 변경 => 내부의 숫자만 변경. 변수에 새 객체 대입 X. => val로 써도 됨.
    val createdAt = Calendar.getInstance()

    var reReplyCount = 0
    var likeCount = 0
    var hateCount = 0

//    내가 좋아요 / 싫어요를 찍은 댓글인지
    var isMyLike = false
    var isMyHate = false


    //    보조 생성자 추가 연습 : 파라미터 x.
    constructor() :  this( 0, "내용없음" )

//     각 댓글이 자신의 작성일시를, 핸드폰 시간대에 맞게 보정. + 가공된 문구로 내보내기

    fun getFormattedCreatedAt() : String {

//        가공 양식 지정
        val sdf = SimpleDateFormat("M월 d일 a h시 m분")

//        시차 보정에 사용할 Calendar 변수  (원래 있는 createdAt은 놔두고, 별도로 추가)
//        내 폰 시간대 (local) 에 맞게 보정 예정
        val localCal = Calendar.getInstance()

//        작성일시의 일시값을 그대로 복사  (원래값 : 현재 일시)
        localCal.time =  this.createdAt.time

//        localCal의 값을, 내 폰 설정에 맞게 시간을 더하자.
//        ex. 한국 : +9를 더해야함.
//        ex. LA : -8을 더해야함.

//        Calendar 객체의 기능으로, 시간대 정보 추출.
        val localTimeZone = localCal.timeZone

//        시간대가, GMT와의 시차가 얼마나 나는지? (rawOffset - 몇 ms 차이까지)
        val diffHour = localTimeZone.rawOffset / 60 / 60 / 1000   // ms => 시간으로 변경

//        구해진 시차를, 기존 시간에서 시간값으로 더해주기
        localCal.add(Calendar.HOUR, diffHour)


//        현재 시간과의 차이를 구해서, 차이값에 따라 다른 양식으로 가공.
//        ex. 2분 전 작성 =>  "2분 전"
//        ex. 5일 전 작성 => "3월 10일 오전 5시 6분"

        val now = Calendar.getInstance() // 현재시간 표현 변수. (핸드폰 시간대가 적용되어있음)

        val timeAgo = now.timeInMillis - localCal.timeInMillis  // 현재시간 - 작성일시 결과. 몇ms 차이나는가?

//        5초 이내 => "방금 전"
        if (timeAgo < 5 * 1000) {
            return "방금 전"
        }
//        1분 이내 => "?초 전"
        else if (timeAgo < 60 * 1000) {

//            timeAgo : ms단위로 계산됨. => ?초전을 보여주려면, 초단위로 변환.
            val diffSecond = timeAgo / 1000

            return "${diffSecond}초 전"
        }
        else if (timeAgo < 1 * 60 * 60 * 1000) {
//            1시간 이내에 작성된 글?  => ?분 전
            val diffMinute = timeAgo / 1000 / 60
            return "${diffMinute}분 전"
        }
        else if (timeAgo < 24 * 60 * 60 * 1000) {
//            24시간 이내 : ?시간 전

            val diff = timeAgo / 1000 / 60 / 60
            return "${diff}시간 전"

        }
        else if( timeAgo < 10 * 24 * 60 * 60 * 1000 ) {
//            10일 이내 : ?일 전
            val diffDay = timeAgo / 1000 / 60 / 60 / 24
            return "${diffDay}일 전"
        }
        else {
//            10일 이상 : sdf로 가공해서 리턴.
            return  sdf.format(localCal.time)
        }



    }

    companion object {

        fun getReplyDataFromJson( jsonObj : JSONObject ) : ReplyData {

            val replyData = ReplyData()

//            JSON 정보 > 멤버변수 채우기

            replyData.id = jsonObj.getInt("id")
            replyData.content = jsonObj.getString("content")
            replyData.writer =  UserData.getUserDataFromJson(  jsonObj.getJSONObject("user")  )

            replyData.selectedSide = SideData.getSideDataFromJson( jsonObj.getJSONObject("selected_side") )

//            Calendar로 되어있는 작성일시의 시간을, 서버가 알려주는 댓글 작성 일시로 맞춰줘야함.

//            임시1) 2022-01-12 10:55:35 로 변경  (한번에 모두 변경)
//            replyData.createdAt.set( 2022, Calendar.JANUARY, 12, 10, 55, 35 )

//            임시2) 연도만 2021년으로 변경. (항목을 찍어서 변경)
//            replyData.createdAt.set( Calendar.YEAR, 2021 )

//            실제) 서버가 주는 created_at 에 담긴 String을 => parse 해서, Calendar로 변경.
//            createdAt 변수의 일시 값으로 => parse 결과물 사용.

//            서버가 주는 양식을 보고, 그대로 적자.
            val sdf = SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" )

//            created_at으로 내려오는 문구. (서버가 주는 String 임시 저장)
            val createdAtStr = jsonObj.getString("created_at")

//            createdAtStr 변수를 => Date로 변경 (parse) => Calendar의 time에 대입.
            replyData.createdAt.time = sdf.parse( createdAtStr )

            replyData.reReplyCount = jsonObj.getInt("reply_count")
            replyData.likeCount = jsonObj.getInt("like_count")
            replyData.hateCount = jsonObj.getInt("dislike_count")

            replyData.isMyLike = jsonObj.getBoolean("my_like")
            replyData.isMyHate = jsonObj.getBoolean("my_dislike")

            return replyData

       }

    }

}