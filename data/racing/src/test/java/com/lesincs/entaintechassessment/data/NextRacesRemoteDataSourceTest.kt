package com.lesincs.entaintechassessment.data

import com.lesincs.entaintechassessment.data.model.RaceSummary
import com.lesincs.entaintechassessment.data.util.getMockHttpClient
import com.lesincs.entaintechassessment.data.util.loadResponseFromFile
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NextRacesRemoteDataSourceTest {

    @Test
    fun `should get next race summary list successfully from server`() = runTest {
        val successResponse = loadResponseFromFile("next_races_response.json")
        val mockNedsHttpClient = getMockHttpClient(
            path = "/rest/v1/racing/?method=nextraces&count=10",
            response = successResponse,
            success = true
        )
        val sut = NextRacesRemoteDataSource(mockNedsHttpClient)

        val raceSummaries = sut.getNextRaceSummaries()

        raceSummaries shouldBe listOf(
            RaceSummary(
                raceId = "e91170b7-ae24-46c1-9b8e-f1d001ecd567",
                raceName = "Happy Hire Cromwell Cup",
                raceNumber = 6,
                meetingId = "044319ac-f363-4f5e-8dd3-3683102fb707",
                meetingName = "Cromwell",
                categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
                advertisedStartSeconds = 1733019300,
            ),
            RaceSummary(
                raceId = "fbadd808-430d-4e5b-9734-da07665cc0f6",
                raceName = "Race 6 - 1609M",
                raceNumber = 6,
                meetingId = "e8de4f94-9495-4cfa-ab73-0f720044d2fa",
                meetingName = "Woodbine Mohawk Park",
                categoryId = "161d9be2-e909-4326-8c2c-35ed71fb460b",
                advertisedStartSeconds = 1733019300,
            )
        )
    }

    @Test
    fun `should get null when get next races response from server failed`() = runTest {
        val mockNedsHttpClient = getMockHttpClient(
            path = "/rest/v1/racing/?method=nextraces&count=10",
            success = false
        )
        val sut = NextRacesRemoteDataSource(mockNedsHttpClient)

        val raceSummaries = sut.getNextRaceSummaries()

        raceSummaries shouldBe null
    }
}
