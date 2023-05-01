Feature: resources processing

  @ComponentTest
  Scenario: client makes a GET call to retrieve audio resource by id without defined range
    When the client calls '/api/v1/resources/1111111'
    Then the client receives status code of 200
    And the client receives content as a byte array

#  @E2ETest
#  Scenario: client makes call to GET all events with their visitors
#    When POST resource
#    Then verify response
#    And verify S3
#    And verify db resources
#    And verify db songs
