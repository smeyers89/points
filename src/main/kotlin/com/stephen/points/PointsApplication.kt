package com.stephen.points

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.net.http.HttpClient
import java.time.Duration

@SpringBootApplication
class PointsApplication{
	@Bean
	fun httpClient(): HttpClient {
		return HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT))
			.build()
	}

	private companion object {
		const val CONNECTION_TIMEOUT: Long = 10
	}
}

fun main(args: Array<String>) {
	runApplication<PointsApplication>(*args)
}
