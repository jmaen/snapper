package core;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class SnapshotExtension extends TypeBasedParameterResolver<SnapshotContext>
        implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private SnapshotContext snapshotContext;

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        snapshotContext = new SnapshotContext();
    }

    @Override
    public SnapshotContext resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return snapshotContext;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws IllegalAccessException, IOException {
        if(context.getTestClass().isPresent()
                && context.getTestInstance().isPresent()
                && context.getTestMethod().isPresent()) {

            Class<?> testClass = context.getTestClass().get();
            Object testInstance = context.getTestInstance().get();
            Method testMethod = context.getTestMethod().get();

            if(testMethod.isAnnotationPresent(SnapshotTest.class)) {
                if(!snapshotContext.hasTarget()) {
                    List<Field> fields = FieldUtils.getAllFieldsList(testClass)
                            .stream()
                            .filter(field -> field.getType() == WebDriver.class)
                            .collect(Collectors.toList());

                    if (fields.size() != 1) {
                        throw new SnapshotException("Test class has to contain exactly one field of type 'WebDriver'");
                    }

                    WebDriver driver = (WebDriver) FieldUtils.readField(fields.get(0), testInstance, true);
                    snapshotContext.setTarget(driver);
                }

                Snapshot snapshot = Snapshot.of(testMethod);
                snapshot.shouldMatch(snapshotContext.takeScreenshot());
            }
        }
    }

}
