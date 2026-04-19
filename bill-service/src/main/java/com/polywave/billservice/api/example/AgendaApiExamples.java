package com.polywave.billservice.api.example;

public final class AgendaApiExamples {

    private AgendaApiExamples() {
    }

    public static final String EXAMPLE_GET_TABS_OK = """
            [
              {
                "code": "HOT_DEBATE",
                "label": "쟁쟁한",
                "description": "찬반이 팽팽한 의안",
                "displayOrder": 1
              },
              {
                "code": "TRENDING",
                "label": "요즘 핫한",
                "description": "최근 급상승 의안",
                "displayOrder": 2
              },
              {
                "code": "RECENT_30D",
                "label": "최근 30일",
                "description": "최근 30일 이내 투표 10건 이상인 의안만, 해당 기간 투표 수 많은 순",
                "displayOrder": 3
              },
              {
                "code": "SAME_AGE",
                "label": "내 또래",
                "description": "최근 7일 이내 동일 연령대(투표 시점) 투표만 집계, 10건 이상 의안만 노출",
                "displayOrder": 4
              }
            ]
            """;

    public static final String EXAMPLE_GET_MAIN_AGENDAS_OK = """
            [
              {
                "categoryCode": "DIGITAL",
                "categoryName": "디지털",
                "categoryIconUrl": "https://storage.googleapis.com/mypoly-assets-dev/bill-categories/DIGITAL.webp",
                "categoryBackgroundColor": "46D9E3",
                "title": "인공지능 산업 진흥법 일부개정법률안",
                "content": "AI 산업 육성 및 안전한 활용을 위한 주요 조항을 담고 있습니다.",
                "registeredDate": "2026-04-16",
                "viewCount": 0,
                "voteCount": 123
              }
            ]
            """;

    public static final String EXAMPLE_GET_AGENDAS_BY_TAB_OK = """
            [
              {
                "billId": 101,
                "officialTitle": "인공지능 산업 진흥법 일부개정법률안",
                "agreeRatio": 0.52,
                "totalVoteCount": 128,
                "hasVoted": true
              },
              {
                "billId": 102,
                "officialTitle": "플랫폼 공정거래법 일부개정법률안",
                "agreeRatio": 0.49,
                "totalVoteCount": 117,
                "hasVoted": false
              }
            ]
            """;
}
