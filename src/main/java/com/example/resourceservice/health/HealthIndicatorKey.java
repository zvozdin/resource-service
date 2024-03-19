package com.example.resourceservice.health;

public enum HealthIndicatorKey {

	RESOURCE_DB("resource_db"),
	LOCALSTACK_S3("localstack_s3");

	private final String indicatorKey;

	HealthIndicatorKey(String indicatorKey) {
		this.indicatorKey = indicatorKey;
	}

}
