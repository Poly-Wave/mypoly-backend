package com.polywave.billservice.api.example;

public final class CategoryApiExamples {

  private CategoryApiExamples() {
  }

  public static final String EXAMPLE_GET_CATEGORIES_OK = """
      [
        {
          "id": 1,
          "code": "DIGITAL",
          "name": "디지털",
          "displayOrder": 1,
          "iconUrl": "https://storage.googleapis.com/mypoly-assets-dev/bill-categories/DIGITAL.webp"
        },
        {
          "id": 2,
          "code": "SECURITY",
          "name": "보안",
          "displayOrder": 2,
          "iconUrl": "https://storage.googleapis.com/mypoly-assets-dev/bill-categories/SECURITY.webp"
        }
      ]
      """;

  public static final String EXAMPLE_UPDATE_INTERESTS_REQUEST = """
      {
        "categoryIds": [1, 2, 3]
      }
      """;

  public static final String EXAMPLE_UPDATE_INTERESTS_OK = """
      {}
      """;

  public static final String EXAMPLE_BAD_REQUEST_VALIDATION = """
      {
        "code": "VALIDATION_ERROR"
      }
      """;

  public static final String EXAMPLE_INVALID_ONBOARDING_STATUS = """
      {
        "code": "INVALID_ONBOARDING_STATUS"
      }
      """;

  public static final String EXAMPLE_USER_SERVICE_API_FAILED = """
      {
        "code": "USER_SERVICE_API_FAILED"
      }
      """;

  public static final String EXAMPLE_MISSING_JWT_TOKEN = """
      {
        "code": "MISSING_JWT_TOKEN"
      }
      """;
}