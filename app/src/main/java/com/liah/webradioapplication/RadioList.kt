package com.liah.webradioapplication

import com.liah.webradioapplication.api.Radio

object RadioList {
    val radioList = arrayOf(
            Radio(
                    "KBS 제1라디오", "FM 97.3㎒", "KBS",
                    "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=21#refresh",
                    null,
                    "http://cfpwwwapi.kbs.co.kr/api/v1/landing/live/channel_code/21"
            ),
            Radio(
                    "KBS 제2라디오", "FM 106.1㎒", "KBS",
                    "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=22#refresh",
                    null,
                    "http://cfpwwwapi.kbs.co.kr/api/v1/landing/live/channel_code/22"
            ),
            Radio(
                    "KBS 1FM", "FM 93.1㎒", "KBS",
                    "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=24#refresh",
                    null,
                    "http://cfpwwwapi.kbs.co.kr/api/v1/landing/live/channel_code/24"
            ),
            Radio(
                    "KBS 2FM", "FM 89.1㎒", "KBS",
                    "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=25#refresh",
                    null,
                    "http://cfpwwwapi.kbs.co.kr/api/v1/landing/live/channel_code/25"
            ),
            Radio(
                    "MBC 라디오",
                    "FM 95.9㎒",
                    "MBC",
                    "http://mini.imbc.com/webapp_v3/mini.html?channel=sfm",
                    null,
                    "http://miniplay.imbc.com/WebHLS.ashx?channel=sfm&protocol=M3U8"
            ),
            Radio(
                    "MBC FM4U",
                    "FM 91.9㎒",
                    "MBC",
                    "http://mini.imbc.com/webapp_v3/mini.html?channel=mfm",
                    null,
                    "http://miniplay.imbc.com/WebHLS.ashx?channel=mfm&protocol=M3U8"
            ),
            Radio(
                    "SBS 러브FM", "FM 103.5㎒", "SBS",
                    "http://play.sbs.co.kr/onair/pc/index.html?id=S08",
                    null,
                    "http://apis.sbs.co.kr/play-api/1.0/onair/channel/S08?protocol=hls"
            ),
            Radio(
                    "SBS 파워FM", "FM 107.7㎒", "SBS",
                    "http://play.sbs.co.kr/onair/pc/index.html?id=S07",
                    null,
                    "http://apis.sbs.co.kr/play-api/1.0/onair/channel/S07?protocol=hls"
            ),
            Radio(
                    "CBS 음악FM",
                    "FM 93.9㎒",
                    "CBS",
                    "http://www.cbs.co.kr/radio/frame/AodJwPlayer.asp#refresh",
                    "http://aac.cbs.co.kr/cbs939/_definst_/cbs939.stream/playlist.m3u8",
                    null
            ),
            Radio(
                    "CBS 표준FM",
                    "FM 98.1㎒",
                    "CBS",
                    "http://www.cbs.co.kr/radio/frame/AodJwPlayer.asp",
                    "http://aac.cbs.co.kr/cbs981/_definst_/cbs981.stream/playlist.m3u8",
                    null
            ),
            Radio(
                    "TBS 교통방송",
                    "FM 95.1㎒",
                    "TBS",
                    "http://tbs.seoul.kr/player/live.do?channelCode=CH_A",
                    "http://58.234.158.60:1935/fmlive/myStream/playlist.m3u8",
                    null
            ),
//            Radio(
//                    "TBS eFM",
//                    "FM 101.3㎒",
//                    "TBS",
//                    "http://tbs.seoul.kr/player/live.do?channelCode=CH_E",
//                    "http://58.234.158.60:1935/efmlive/myStream/playlist.m3u8",
//                    null
//            ),
            Radio(
                    "EBS FM 교육방송",
                    "FM 104.5㎒",
                    "EBS",
                    "http://www.ebs.co.kr/radio/home?mainTop",
                    "http://ebsonair.ebs.co.kr/fmradiofamilypc/familypc1m/playlist.m3u8",
                    null
            )

    )
}