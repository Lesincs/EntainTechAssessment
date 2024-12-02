package com.lesincs.entaintechassessment

import com.lesincs.entaintechassessment.nextraces.CountdownSecondsFormatter
import io.kotest.matchers.shouldBe
import org.junit.Test

class CountdownSecondsFormatterTest {

    private val sut: CountdownSecondsFormatter = CountdownSecondsFormatter()

    @Test
    fun `should format countdown seconds properly`() {
        sut.format(20) shouldBe "20s"
        sut.format(60) shouldBe "1m"
        sut.format(123) shouldBe "2m 3s"
        sut.format(-20) shouldBe "-20s"
        sut.format(-60) shouldBe "-1m"
        sut.format(-123) shouldBe "-2m 3s"
    }
}
