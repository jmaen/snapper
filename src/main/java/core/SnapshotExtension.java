package core;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SnapshotExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) throws IllegalAccessException {
        if(context.getTestClass().isPresent()
                && context.getTestInstance().isPresent()
                && context.getTestMethod().isPresent()) {

            Class<?> testClass = context.getTestClass().get();
            Method testMethod = context.getTestMethod().get();
            SnapshotName nameAnnotation = testMethod.getDeclaredAnnotation(SnapshotName.class);
            String name;
            if(nameAnnotation != null) {
                name = nameAnnotation.value();
            } else {
                name = testClass.getName() + "_" + testMethod.getName();
            }

            Field field = FieldUtils.getField(testClass, "snapshot", true);
            if(!Snapshot.class.isAssignableFrom(field.getType())) {
                throw new IllegalStateException("Snapshot field has the wrong type");
            }
            field.set(context.getTestInstance().get(), new Snapshot(name));
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws IllegalAccessException, IOException {
        if(context.getTestClass().isPresent()
                && context.getTestInstance().isPresent()) {

            Object testInstance = context.getTestInstance().get();

            Field snapshotField = FieldUtils.getField(context.getTestClass().get(), "snapshot", true);
            if(!Snapshot.class.isAssignableFrom(snapshotField.getType())) {
                throw new IllegalStateException("Snapshot field has the wrong type");
            }
            Snapshot snapshot = (Snapshot) snapshotField.get(testInstance);

            Field driverField = FieldUtils.getField(context.getTestClass().get(), "driver", true);
            if(WebDriver.class != driverField.getType()) {
                throw new IllegalStateException("Driver field has the wrong type");
            }
            WebDriver driver = (WebDriver) driverField.get(testInstance);

            snapshot.shouldMatch(SnapshotUtil.takeScreenshot(driver));
        }
    }

}
