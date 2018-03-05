package pl.net.kotula.externalstoragesample;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.schibsted.spain.barista.interaction.PermissionGranter;
import com.schibsted.spain.barista.rule.BaristaRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String TAG = ExampleInstrumentedTest.class.getSimpleName();
    @Rule
    public BaristaRule<MainActivity> baristaRule = BaristaRule.create(MainActivity.class);;

    @Before
    public void setUp(){
        Context context = InstrumentationRegistry.getContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "Trying to grant permissions");
//            Runtime.getRuntime().exec("pm grant " + context.getPackageName() + " android.permission.WRITE_EXTERNAL_STORAGE");
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + context.getPackageName()
                            + " android.permission.WRITE_EXTERNAL_STORAGE");
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + context.getPackageName()
                            + " android.permission.READ_EXTERNAL_STORAGE");
        }

        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int selfPermissionCheck = ContextCompat.checkSelfPermission(context, permission);
        Log.i(TAG, "Context: " + context.getClass() + ", package: " + context.getPackageName() + ", permission: " + selfPermissionCheck);
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sample/";
        cleanDirectory(new File(folder));
    }


    @Test
    public void resultWhenNoFileFound() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("pl.net.kotula.externalstoragesample", appContext.getPackageName());

        baristaRule.launchActivity();
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.READ_EXTERNAL_STORAGE);

        onView(withId(R.id.textView)).check(matches(withText("If you want to use this - create file " + MainActivity.FILENAME_TO_READ)));
    }

    @Test
    public void resultWhenSimpleFile() throws Exception {
        String content = "simple content";
        createFile(MainActivity.FILENAME_TO_READ, content);
        baristaRule.launchActivity();
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.READ_EXTERNAL_STORAGE);
        onView(withId(R.id.textView)).check(matches(withText(content)));

    }


    protected static void cleanDirectory(File file) {
        if (!file.exists()) return;
        Preconditions.checkArgument(file.isDirectory(), "cleanDirectory is a method for removing directories, not regular files!!");
        for (String name : file.list()) {
            File child = new File(file, name);
            if (child.isDirectory()) {
                cleanDirectory(child);
            }
            assertTrue("Couldn't delete file: " + child.getAbsolutePath(), child.delete());
        }
    }

    protected static void createFile(String path, String content) throws IOException {
        File file = new File(path);
        OutputStreamWriter wr = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        wr.write(content);
        wr.close();
    }

    protected static void createDirectory(String path) throws IOException {
        File file = new File(path);
        assertTrue("Couldn't create file " + path, file.mkdir());
        if (!file.exists())
            throw new IOException("Creation of " + path + " failed");
    }
}
