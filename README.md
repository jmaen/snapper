# Snapper
**Snapper** is a [JUnit](https://junit.org/junit5) extension that makes it easy to write visual regression tests with [Selenium](https://www.selenium.dev).

- **Snapshot handling:** Loads / stores the corresponding snapshot for each test
- **Automatic screenshots:** Takes screenshots automatically after each test
- **Image comparison:** Compares screenshots against the baseline and creates diff images to help identify errors

## Usage
A basic test could be as simple as the following:
```java
@SnapshotTest
public void basicTest() {
  driver.get("https://www.google.com");
}
```
All you have to do is to add a field of type `WebDriver` to any class in your test class hierarchy.
Snapper finds that field and takes a screenshot of the browser window controlled by that driver right after the test method finished.

After that, it will try to find the corresponding snapshot, which by default is located in the `__snapshots__` directory in your test resources, named `{class}_{method}.png`.
Snapper also supports custom snapshot names, simply pass a name of your choice to the `SnapshotTest` annotation.

If the images match, the test passes, otherwise it fails. In case of a failed test, Snapper also creates a diff image of both images
and highlights the differences. By default, these diff images are located in the `build/reports/snapper` directory.

It can also be helpful to only focus on a single web element or ignore specific elements, especially when dealing with dynamic data (e.g. times).
To do so, you have to give the test method a `SnapshotContext` parameter. This parameter can then be used to tell Snapper how to handle the snapshot in detail.

A more advanced test that includes the above features might look like the following:

```java
@SnapshotTest(name="customName")
public void advancedTest(SnapshotContext context) {
  driver.get("https://www.google.com");

  WebElement searchBar = driver.findElement(By.name("q"));
  context.setTarget(searchBar);

  WebElement searchIcon = driver.findElement(By.cssSelector(".QCzoEc > svg:nth-child(1)"));
  context.addIgnoredElement(searchIcon);
}
```
In the above example, Snapper will only take a screenshot of the search bar and ignore the search icon.

## Configuration
The aforementioned defaults can be changed through the `snapper.properties` file or by accessing the `SnapshotConfig` directly from your tests.


| Property | Description | Default |
| ----- | ----- | ----- |
| snapshot-dir | Base directory of all snapshots | src/test/java/\_\_snapshots__ |
| output-dir | Directory for diff images | build/reports/snapper |
| update-mode | Which snapshots to update: all, missing or none | missing |
| tolerance | How far the actual color can deviate before it is considered an error, from [0-1] | 0 |
| diff-mode | How to highlight differences in the diff image: pixel or box | box |
| diff-color | Color used for difference highlighting | #FF0000 |
| box-threshold | Minimum distance between two different boxes (only for diff-mode=box) | 10 |
| box-stroke-width | Stroke width (only for diff-mode=box) | 3 |
