package qa.guru.kaspresso;

import static io.qameta.allure.kotlin.Allure.getLifecycle;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.qameta.allure.kotlin.model.Label;
import io.qameta.allure.kotlin.model.Status;
import io.qameta.allure.kotlin.model.StatusDetails;
import io.qameta.allure.kotlin.model.TestResult;
import io.qameta.allure.kotlin.util.ResultsUtils;

public class AllureTestCaseRule extends TestWatcher {

    private static final String ALLURE_ID_LABEL = "AS_ID";

    private static final String IGNORE_TEST_RESULT_MESSAGE = "Not selected in execution";

    private final List<TestResult> children = new ArrayList<>();

    private TestResult parent;

    public static AllureTestCaseRule allureTW() {
        return new AllureTestCaseRule();
    }

    protected void starting(final Description description) {
        getLifecycle().updateTestCase((update) -> {
            this.parent = update;
            return null;
        });
    }

    protected void finished(final Description description) {
        getLifecycle().updateTestCase((update) -> {
            update.setStatus(Status.SKIPPED);
            StatusDetails sd = new StatusDetails();
            sd.setMessage(IGNORE_TEST_RESULT_MESSAGE);
            update.setStatusDetails(sd);
            return null;
        });
    }

    public void create(final String id,
                       final Consumer<TestResult> consumer) {
        final String uuid = UUID.randomUUID().toString();
        createTestResult(uuid, parent).ifPresent(testResult -> {
            getLifecycle().scheduleTestCase(uuid, testResult);
            getLifecycle().startTestCase(uuid);
            getLifecycle().updateTestCase(uuid, (result) -> {
                Label l = new Label();
                l.setValue(id);
                l.setName(ALLURE_ID_LABEL);
                result.getLabels().add(l);
                if (!children.isEmpty()) {
                    result.getSteps().addAll(children.get(children.size()-1).getSteps());
                }
                return null;
            });
            try {
                consumer.accept(testResult);
                getLifecycle().updateTestCase(uuid, (result) -> {
                    result.setStatus(Status.PASSED);
                    return null;
                });
            } catch (Exception e) {
                getLifecycle().updateTestCase(uuid, (result) -> {
                    result.setStatus(ResultsUtils.getStatus(e));
                    result.setStatusDetails(ResultsUtils.getStatusDetails(e));
                    return null;
                });
            } finally {
                getLifecycle().updateTestCase(uuid, (result) -> {
                    result.setStop(System.currentTimeMillis());
                    this.children.add(result);
                    return null;
                });
                getLifecycle().stopTestCase(uuid);
                getLifecycle().writeTestCase(uuid);
            }
        });
    }

    private static Optional<TestResult> createTestResult(final String uuid, final TestResult parent) {
        if (Objects.nonNull(parent)) {
            final TestResult testResult = new TestResult(uuid);
            final List<Label> labels = parent.getLabels().stream()
                    .filter(l -> !l.getName().equals(ALLURE_ID_LABEL))
                    .collect(Collectors.toList());
            testResult.setName(parent.getName());
            testResult.setDescription(parent.getDescription());
            testResult.setFullName(parent.getFullName());
            testResult.getLabels().addAll(labels);
            testResult.getLinks().addAll(parent.getLinks());
            testResult.setDescriptionHtml(parent.getDescriptionHtml());
            testResult.getParameters().addAll(parent.getParameters());
            return Optional.of(testResult);
        }
        return Optional.empty();
    }

}
