Feature: resources processing e2e

  @E2ETest
  Scenario: client makes call to GET all events with their visitors
    When a client post file by '/api/v1/resources' endpoint to store it into S3
    Then a client receives 200 as status code
    And a client receives resourceId to track it
    And a file has been stored into S3 with the same key as the resourceId from the response
    And a file tracking info - id and name have been stored into resource db
    And a file metadata has been stored into song db
