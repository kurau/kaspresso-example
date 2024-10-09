package qa.guru.kaspresso;

import static io.qameta.allure.kotlin.Allure.getLifecycle;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.function.Consumer;

import io.qameta.allure.kotlin.model.TestResult;

public class TestWatcherExampleRule extends TestWatcher {

    public static TestWatcherExampleRule tw() {
        return new TestWatcherExampleRule();
    }

    protected void starting(final Description description) {
        getLifecycle().updateTestCase((update) -> {
            System.out.println(" STARTING ");
            return null;
        });
    }

    protected void finished(final Description description) {
        getLifecycle().updateTestCase((update) -> {
            System.out.println(" FINISHED ");
            return null;
        });
    }

    public void create(final String id,
                       final Consumer<TestResult> consumer) {
        System.out.println(" CREATE ");
    }

}
