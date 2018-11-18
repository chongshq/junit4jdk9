package org.junit.rules;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.common.After;
import org.junit.common.Rule;
import org.junit.common.Test;

public class TempFolderRuleTest {
    private static File[] createdFiles = new File[20];

    public static class HasTempFolder {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingTempFolder() throws IOException {
            createdFiles[0] = folder.newFile("myfile.txt");
            Assert.assertTrue(createdFiles[0].exists());
        }

        @Test
        public void testTempFolderLocation() throws IOException {
            File folderRoot = folder.getRoot();
            String tmpRoot = System.getProperty("java.io.tmpdir");
            Assert.assertTrue(folderRoot.toString().startsWith(tmpRoot));
        }
    }

    @Test
    public void tempFolderIsDeleted() {
        Assert.assertThat(PrintableResult.testResult(HasTempFolder.class), ResultMatchers.isSuccessful());
        Assert.assertFalse(createdFiles[0].exists());
    }

    public static class CreatesSubFolder {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingTempFolderStringReflection() throws Exception {
            String subfolder = "subfolder";
            String filename = "a.txt";
            // force usage of folder.newFolder(String),
            // check is available and works, to avoid a potential NoSuchMethodError with non-recompiled code.
            Method method = folder.getClass().getMethod("newFolder", new Class<?>[]{String.class});
            createdFiles[0] = (File) method.invoke(folder, subfolder);
            new File(createdFiles[0], filename).createNewFile();

            File expectedFile = new File(folder.getRoot(), join(subfolder, filename));

            Assert.assertTrue(expectedFile.exists());
        }

        @Test
        public void testUsingTempFolderString() throws IOException {
            String subfolder = "subfolder";
            String filename = "a.txt";
            // this uses newFolder(String), ensure that a single String works
            createdFiles[0] = folder.newFolder(subfolder);
            new File(createdFiles[0], filename).createNewFile();

            File expectedFile = new File(folder.getRoot(), join(subfolder, filename));

            Assert.assertTrue(expectedFile.exists());
        }

        @Test
        public void testUsingTempTreeFolders() throws IOException {
            String subfolder = "subfolder";
            String anotherfolder = "anotherfolder";
            String filename = "a.txt";

            createdFiles[0] = folder.newFolder(subfolder, anotherfolder);
            new File(createdFiles[0], filename).createNewFile();

            File expectedFile = new File(folder.getRoot(), join(subfolder, anotherfolder, filename));

            Assert.assertTrue(expectedFile.exists());
        }

        private String join(String... folderNames) {
            StringBuilder path = new StringBuilder();
            for (String folderName : folderNames) {
                path.append(File.separator).append(folderName);
            }
            return path.toString();
        }
    }

    @Test
    public void subFolderIsDeleted() {
        Assert.assertThat(PrintableResult.testResult(CreatesSubFolder.class), ResultMatchers.isSuccessful());
        Assert.assertFalse(createdFiles[0].exists());
    }

    public static class CreatesRandomSubFolders {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingRandomTempFolders() throws IOException {
            for (int i = 0; i < 20; i++) {
                File newFolder = folder.newFolder();
                Assert.assertThat(Arrays.asList(createdFiles), not(hasItem(newFolder)));
                createdFiles[i] = newFolder;
                new File(newFolder, "a.txt").createNewFile();
                Assert.assertTrue(newFolder.exists());
            }
        }
    }

    @Test
    public void randomSubFoldersAreDeleted() {
        Assert.assertThat(PrintableResult.testResult(CreatesRandomSubFolders.class), ResultMatchers.isSuccessful());
        for (File f : createdFiles) {
            Assert.assertFalse(f.exists());
        }
    }

    public static class CreatesRandomFiles {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testUsingRandomTempFiles() throws IOException {
            for (int i = 0; i < 20; i++) {
                File newFile = folder.newFile();
                Assert.assertThat(Arrays.asList(createdFiles), not(hasItem(newFile)));
                createdFiles[i] = newFile;
                Assert.assertTrue(newFile.exists());
            }
        }
    }

    @Test
    public void randomFilesAreDeleted() {
        Assert.assertThat(PrintableResult.testResult(CreatesRandomFiles.class), ResultMatchers.isSuccessful());
        for (File f : createdFiles) {
            Assert.assertFalse(f.exists());
        }
    }

    @Test
    public void recursiveDeleteFolderWithOneElement() throws IOException {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File file = folder.newFile("a");
        folder.delete();
        Assert.assertFalse(file.exists());
        Assert.assertFalse(folder.getRoot().exists());
    }

    @Test
    public void recursiveDeleteFolderWithOneRandomElement() throws IOException {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File file = folder.newFile();
        folder.delete();
        Assert.assertFalse(file.exists());
        Assert.assertFalse(folder.getRoot().exists());
    }

    @Test
    public void recursiveDeleteFolderWithZeroElements() throws IOException {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        folder.delete();
        Assert.assertFalse(folder.getRoot().exists());
    }

    public static class NameClashes {
        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void fileWithFileClash() throws IOException {
            folder.newFile("something.txt");
            folder.newFile("something.txt");
        }

        @Test
        public void fileWithFolderTest() throws IOException {
            folder.newFolder("dummy");
            folder.newFile("dummy");
        }
    }

    @Test
    public void nameClashesResultInTestFailures() {
        Assert.assertThat(PrintableResult.testResult(NameClashes.class), ResultMatchers.failureCountIs(2));
    }

    private static final String GET_ROOT_DUMMY = "dummy-getRoot";

    private static final String NEW_FILE_DUMMY = "dummy-newFile";

    private static final String NEW_FOLDER_DUMMY = "dummy-newFolder";

    public static class IncorrectUsage {
        public TemporaryFolder folder = new TemporaryFolder();

        @Test
        public void testGetRoot() throws IOException {
            new File(folder.getRoot(), GET_ROOT_DUMMY).createNewFile();
        }

        @Test
        public void testNewFile() throws IOException {
            folder.newFile(NEW_FILE_DUMMY);
        }

        @Test
        public void testNewFolder() throws IOException {
            folder.newFolder(NEW_FOLDER_DUMMY);
        }
    }

    @Test
    public void incorrectUsageWithoutApplyingTheRuleShouldNotPolluteTheCurrentWorkingDirectory() {
        Assert.assertThat(PrintableResult.testResult(IncorrectUsage.class), ResultMatchers.failureCountIs(3));
        Assert.assertFalse("getRoot should have failed early", new File(GET_ROOT_DUMMY).exists());
        Assert.assertFalse("newFile should have failed early", new File(NEW_FILE_DUMMY).exists());
        Assert.assertFalse("newFolder should have failed early", new File(NEW_FOLDER_DUMMY).exists());
    }

    @After
    public void cleanCurrentWorkingDirectory() {
        new File(GET_ROOT_DUMMY).delete();
        new File(NEW_FILE_DUMMY).delete();
        new File(NEW_FOLDER_DUMMY).delete();
    }
}
