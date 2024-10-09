package qa.guru.kaspresso

import android.Manifest
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.qameta.allure.kotlin.Allure
import org.junit.Rule
import org.junit.Test


class IncrementButtonTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withForcedAllureSupport()
) {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @get:Rule
    val runtimePermission: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    @get:Rule
    val testCaseRule:AllureTestCaseRule  = AllureTestCaseRule.allureTW();

    @Test
    fun multiplyTests() = run {

        testCaseRule.create("113", { res ->
            Allure.label("test", "first");
            step("Increase counter value to 2") {
                println(" TEST 113 ")
            }
        })

        testCaseRule.create("114", { res ->
            Allure.label("test", "second");
            step("Increase counter value to 3") {
                println(" TEST 114 ")
            }
        })

    }

    @Test
    fun incrementButtonShouldIncreaseCounterValue() = run {
        step("Increase counter value to 2") {
            MainScreen {
                repeat(2) {
                    incrementButton.click()
                }
            }
        }
        step("Counter value should be 2") {
            MainScreen {
                counterText.hasText("Count: 1")
            }
        }
    }

}