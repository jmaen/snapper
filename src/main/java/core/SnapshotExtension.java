package core;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class SnapshotExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) throws IllegalAccessException, IOException {
        if(context.getTestClass().isPresent()
                && context.getTestInstance().isPresent()
                && context.getTestMethod().isPresent()) {

            Class<?> testClass = context.getTestClass().get();
            Object testInstance = context.getTestInstance().get();
            Method testMethod = context.getTestMethod().get();

            if(testMethod.isAnnotationPresent(SnapshotTest.class)) {
                List<Field> fields = FieldUtils.getAllFieldsList(testClass)
                        .stream()
                        .filter(field -> field.getType() == WebDriver.class)
                        .collect(Collectors.toList());
                if (fields.size() != 1) {
                    throw new SnapshotException("Test class has to contain exactly one field of type 'WebDriver'");
                }
                WebDriver driver = (WebDriver) FieldUtils.readField(fields.get(0), testInstance, true);

                Snapshot snapshot = Snapshot.of(testMethod);
                snapshot.shouldMatch(SnapshotUtil.takeScreenshot(driver));
            }
        }
    }

}
