package com.polywave.billservice.api.example;

public final class AgendaApiExamples {

    private AgendaApiExamples() {
    }

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
}
